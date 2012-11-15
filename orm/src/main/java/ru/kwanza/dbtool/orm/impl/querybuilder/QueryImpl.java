package ru.kwanza.dbtool.orm.impl.querybuilder;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.SqlParameterValue;
import ru.kwanza.dbtool.core.DBTool;
import ru.kwanza.dbtool.core.KeyValue;
import ru.kwanza.dbtool.core.SqlCollectionParameterValue;
import ru.kwanza.dbtool.core.util.FieldValueExtractor;
import ru.kwanza.dbtool.core.util.SelectUtil;
import ru.kwanza.dbtool.orm.api.Filter;
import ru.kwanza.dbtool.orm.api.IQuery;
import ru.kwanza.dbtool.orm.impl.mapping.EntityField;
import ru.kwanza.dbtool.orm.impl.mapping.FieldMapping;
import ru.kwanza.dbtool.orm.impl.mapping.IEntityMappingRegistry;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * @author Alexander Guzanov
 */
public class QueryImpl<T> implements IQuery<T> {
    private DBTool dbTool;
    private String sql;
    private List<Integer> paramTypes;
    private Object[] params;
    private Integer maxSize;
    private Integer offset;
    private IEntityMappingRegistry registry;
    private Class<T> entityClass;
    private int paramsCount;


    public QueryImpl(DBTool dbTool, IEntityMappingRegistry registry, Class<T> entityClass, String sql, Integer maxSize, Integer offset,
                     List<Integer> paramTypes) {
        this.dbTool = dbTool;
        this.sql = sql;
        this.maxSize = maxSize;
        this.offset = offset;
        this.paramTypes = paramTypes;
        this.registry = registry;
        this.entityClass = entityClass;

        int pc = this.paramsCount = paramTypes.size();
        if (this.maxSize != null) {
            pc++;
        }

        this.params = new Object[pc];
        if (maxSize != null) {
            if (dbTool.getDbType() == DBTool.DBType.ORACLE) {
                this.params[this.paramsCount] = this.maxSize;
            } else {
                this.params[0] = this.maxSize;
            }
        }
    }

    public String getSql() {
        return sql;
    }

    public int getParamsCount() {
        return paramsCount;
    }

    public T select() {
        return null;
    }

    public T selectWithFilter(Filter... filters) {
        return null;
    }

    public List<T> selectList() {
        final LinkedList<T> result = new LinkedList<T>();
        SelectUtil.batchSelect(dbTool.getJdbcTemplate(), sql, new ObjectExtractor<T>(), new SelectUtil.Container<Collection<T>>() {
            public void add(Collection<T> objects) {
                if (objects != null) {
                    result.addAll(objects);
                }
            }
        }, params);

        return result;
    }

    public Map<Object, T> selectMap(String field) {
        final Map<Object, T> result = new HashMap<Object, T>();
        FieldMapping fieldMapping = registry.getFieldMappingByPropertyName(entityClass, field);
        if (fieldMapping == null) {
            throw new IllegalArgumentException("Unknown field name!");
        }

        SelectUtil.batchSelect(dbTool.getJdbcTemplate(), sql, new MapExtractor(fieldMapping),
                new SelectUtil.Container<Collection<KeyValue<Object, T>>>() {
                    public void add(Collection<KeyValue<Object, T>> objects) {
                        for (KeyValue<Object, T> kv : objects) {
                            result.put(kv.getKey(), kv.getValue());
                        }
                    }
                }, params);

        return result;
    }

    public Map<Object, List<T>> selectMapList(String field) {
        final Map<Object, List<T>> result = new HashMap<Object, List<T>>();
        FieldMapping fieldMapping = registry.getFieldMappingByPropertyName(entityClass, field);
        if (fieldMapping == null) {
            throw new IllegalArgumentException("Unknown field name!");
        }

        SelectUtil.batchSelect(dbTool.getJdbcTemplate(), sql, new MapExtractor(fieldMapping),
                new SelectUtil.Container<Collection<KeyValue<Object, T>>>() {
                    public void add(Collection<KeyValue<Object, T>> objects) {
                        for (KeyValue<Object, T> kv : objects) {
                            List<T> vs = result.get(kv.getKey());
                            if (vs == null) {
                                vs = new ArrayList<T>();
                                result.put(kv.getKey(), vs);
                            }
                            vs.add(kv.getValue());
                        }
                    }
                }, params);
        return result;
    }


    public List<T> selectListWithFilter(Filter... filters) {
        return null;
    }

    public IQuery<T> setParameter(int index, Object value) {
        if (index <= 0 || index > getParamsCount()) {
            throw new IllegalArgumentException("Index of parameter is wrong");
        }

        int i;
        if (maxSize != null) {
            if (dbTool.getDbType() == DBTool.DBType.ORACLE) {
                i = index - 1;
            } else {
                i = index;
            }
        } else {
            i = index - 1;
        }

        int type = paramTypes.get(index - 1);
        this.params[i] = type == Integer.MAX_VALUE ? value : ((value instanceof Collection) ?
                new SqlCollectionParameterValue(type, (Collection) value) : new SqlParameterValue(type, value));

        return this;
    }


    @Override
    public String toString() {
        return "QueryImpl{" +
                "query='" + sql + '\'' +
                '}';
    }


    private class ObjectExtractor<T> extends BaseExtractor<T> {
        @Override
        public T getValue(Object e) {
            return (T) e;
        }
    }

    private class MapExtractor extends BaseExtractor<KeyValue<Object, T>> {
        private EntityField field;

        private MapExtractor(FieldMapping mapping) {
            this.field = mapping.getEntityFiled();
        }

        @Override
        public KeyValue<Object, T> getValue(Object e) {
            return new KeyValue<Object, T>(field.getValue(e), (T) e);
        }
    }


    private abstract class BaseExtractor<TYPE> implements ResultSetExtractor {
        public Collection<TYPE> extractData(ResultSet rs) throws SQLException, DataAccessException {
            if (QueryImpl.this.offset != null && QueryImpl.this.offset > 1) {
                if (rs.next()) {
                    rs.absolute(QueryImpl.this.offset - 1);
                } else {
                    return null;
                }
            }
            LinkedList<TYPE> objects = new LinkedList<TYPE>();

            Collection<FieldMapping> fieldMapping = registry.getFieldMapping(entityClass);

            while (rs.next()) {
                T obj;
                try {
                    obj = (T) entityClass.newInstance();
                } catch (Exception e) {
                    throw new RuntimeException();
                }

                readAndFill(rs, fieldMapping, obj);

                objects.add(getValue(obj));
            }

            return objects;
        }

        public abstract TYPE getValue(Object e);

        private void readAndFill(ResultSet rs, Collection<FieldMapping> fields, T obj) throws SQLException {
            for (FieldMapping idf : fields) {
                Object value = FieldValueExtractor.getValue(rs, idf.getColumnName(), idf.getEntityFiled().getType());
                idf.getEntityFiled().setValue(obj, value);
            }
        }
    }
}
