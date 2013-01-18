package ru.kwanza.dbtool.core.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.*;
import ru.kwanza.dbtool.core.DBTool;
import ru.kwanza.dbtool.core.KeyValue;

import java.sql.ResultSet;
import java.util.*;

/**
 * @author Guzanov Alexander
 */
public class SelectUtil {
    private static final Logger logger = LoggerFactory.getLogger(DBTool.class);

    public static interface Container<U> {
        public void add(U object);
    }

    public static <T> List<T> selectList(JdbcOperations template, String selectSQL, RowMapper<T> rowMapper, Object... params) {

        final ArrayList<T> result = new ArrayList<T>();
        batchSelect(template, selectSQL, new RowMapperResultSetExtractor<T>(rowMapper), new Container<Collection<T>>() {

            public void add(Collection<T> object) {
                result.addAll(object);
            }
        }, params);
        return result;
    }

    public static <T> List<T> selectList(JdbcOperations template, String selectSQL, Class<T> cls, Object... params) {
        return selectList(template, selectSQL, new SingleColumnRowMapper<T>(cls), params);
    }

    public static <T> Set<T> selectSet(JdbcOperations template, String selectSQL, RowMapper<T> rowMapper, Object... inValues) {
        final HashSet<T> result = new HashSet<T>();
        batchSelect(template, selectSQL, new RowMapperResultSetExtractor<T>(rowMapper), new Container<Collection<T>>() {

            public void add(Collection<T> object) {
                result.addAll(object);
            }
        }, inValues);

        return result;
    }

    public static <T> Set<T> selectSet(JdbcOperations template, String selectSQL, Class<T> cls, Object... params) {
        return selectSet(template, selectSQL, new SingleColumnRowMapper<T>(cls), params);
    }

    public static <K, V> Map<K, List<V>> selectMapList(JdbcOperations template, String selectSQL, RowMapper<KeyValue<K, V>> rowMapper,
                                                       Object... params) {
        final Map<K, List<V>> result = new HashMap<K, List<V>>();
        batchSelect(template, selectSQL, new RowMapperResultSetExtractor<KeyValue<K, V>>(rowMapper),
                new Container<Collection<KeyValue<K, V>>>() {
                    public void add(Collection<KeyValue<K, V>> objects) {
                        for (KeyValue<K, V> kv : objects) {
                            List<V> vs = result.get(kv.getKey());
                            if (vs == null) {
                                vs = new ArrayList<V>();
                                result.put(kv.getKey(), vs);
                            }
                            vs.add(kv.getValue());
                        }
                    }
                }, params);
        return result;
    }

    public static <K0, K, V> Map<K0, Map<K, V>> selectMapOfMaps(JdbcOperations template, String selectSQL,
                                                                RowMapper<KeyValue<K0, KeyValue<K, V>>> rowMapper, Object... params) {
        final Map<K0, Map<K, V>> result = new HashMap<K0, Map<K, V>>();
        batchSelect(template, selectSQL, new RowMapperResultSetExtractor<KeyValue<K0, KeyValue<K, V>>>(rowMapper),
                new Container<Collection<KeyValue<K0, KeyValue<K, V>>>>() {
                    public void add(Collection<KeyValue<K0, KeyValue<K, V>>> objects) {
                        for (KeyValue<K0, KeyValue<K, V>> kv0 : objects) {

                            Map<K, V> kv = result.get(kv0.getKey());
                            if (kv == null) {
                                kv = new HashMap<K, V>();
                                result.put(kv0.getKey(), kv);
                            }
                            kv.put(kv0.getValue().getKey(), kv0.getValue().getValue());
                        }
                    }
                }, params);
        return result;
    }

    public static <K, V> Map<K, V> selectMap(JdbcOperations template, String selectSQL, RowMapper<KeyValue<K, V>> rowMapper,
                                             Object... params) {
        final Map<K, V> result = new HashMap<K, V>();
        batchSelect(template, selectSQL, new RowMapperResultSetExtractor<KeyValue<K, V>>(rowMapper),
                new Container<Collection<KeyValue<K, V>>>() {
                    public void add(Collection<KeyValue<K, V>> objects) {
                        for (KeyValue<K, V> kv : objects) {
                            if (result.containsKey(kv.getKey())) {
                                throw new RuntimeException("Duplicate values detected! One key - one value!");
                            }
                            result.put(kv.getKey(), kv.getValue());
                        }
                    }
                }, params);
        return result;
    }

    private static String traceParams(Object[] inValues) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < inValues.length; i++) {
            Object value = inValues[i];
            builder.append('\n').append(i).append("=");
            if (value instanceof Collection) {
                builder.append('[');
                for (Object vi : (Collection) value) {
                    builder.append(vi).append(',');
                }

                builder.append(']');
            } else if (value instanceof SqlParameterValue) {
                builder.append(((SqlParameterValue) value).getValue());
            } else {
                builder.append(value);
            }

        }

        return builder.toString();
    }

    public static void batchSelect(JdbcOperations template, String selectSQL, ResultSetExtractor extractor, Container result,
                                   Object[] inValues) {

        batchSelect(template, selectSQL, extractor, result, inValues, ResultSet.TYPE_FORWARD_ONLY);
    }

    public static void batchSelect(JdbcOperations template, String selectSQL, ResultSetExtractor extractor, Container result,
                                   Object[] inValues, int resultSetType) {

        if (logger.isDebugEnabled()) {
            if (logger.isTraceEnabled()) {
                logger.trace("Executing query \n{} \nwith params:{} ", selectSQL, traceParams(inValues));
            } else {
                logger.debug("Executing query \n{}", selectSQL);
            }
        }

        int lastListParam = -1;
        int[] index = new int[inValues.length];
        Object[] mappingParams = new Object[inValues.length];
        boolean mustContinue = false;
        do {
            if (lastListParam == -1) {
                for (int j = 0; j < inValues.length; j++) {
                    Object p = inValues[j];
                    if (p instanceof Collection) {

                        if (!(p instanceof List)) {
                            p = inValues[j] = new ArrayList((Collection) p);
                        }
                        if (lastListParam == -1) {
                            lastListParam = j;
                        }
                        mustContinue |= mapListParam(index, mappingParams, j, p);
                    } else {
                        mappingParams[j] = p;
                        index[j] = -1;
                    }
                }
            } else {
                int j = lastListParam;
                mustContinue = mapListParam(index, mappingParams, j, inValues[j]);
            }

            SelectStatementCreator selectStatement = new SelectStatementCreator(selectSQL, mappingParams, resultSetType);
            Object res = template.query(selectStatement, extractor);
            if (res != null) {
                result.add(res);
            }
            if (!mustContinue) {
                mustContinue = false;
                for (int j = lastListParam + 1; j < index.length; j++) {
                    if (index[j] >= 0 && index[j] < ((List) inValues[j]).size()) {
                        mustContinue = true;
                        lastListParam = j;
                        break;
                    }
                }
            }
        } while (mustContinue);
    }

    private static boolean mapListParam(int[] index, Object[] mappingParams, int j, Object p) {
        boolean mustContinue = false;
        List list = (List) p;
        int nextIndex = index[j] + QuestionsHelper.MAX_SELECT_IN;
        if (nextIndex >= list.size()) {
            nextIndex = list.size();
        }
        mappingParams[j] = !list.isEmpty() ? list.subList(index[j], nextIndex) : null;
        index[j] = nextIndex;
        if (nextIndex < list.size()) {
            mustContinue = true;
        }
        return mustContinue;
    }
}
