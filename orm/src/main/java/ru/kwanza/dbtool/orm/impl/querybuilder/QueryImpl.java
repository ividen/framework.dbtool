package ru.kwanza.dbtool.orm.impl.querybuilder;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.SqlParameterValue;
import ru.kwanza.dbtool.core.DBTool;
import ru.kwanza.dbtool.core.KeyValue;
import ru.kwanza.dbtool.core.SqlCollectionParameterValue;
import ru.kwanza.dbtool.core.util.FieldValueExtractor;
import ru.kwanza.dbtool.core.util.SelectUtil;
import ru.kwanza.dbtool.orm.api.IQuery;
import ru.kwanza.dbtool.orm.api.ListProducer;
import ru.kwanza.dbtool.orm.impl.mapping.EntityField;
import ru.kwanza.dbtool.orm.impl.mapping.FieldMapping;
import ru.kwanza.dbtool.orm.impl.mapping.IEntityMappingRegistry;

import java.lang.reflect.Constructor;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * @author Alexander Guzanov
 */
public class QueryImpl<T> implements IQuery<T> {
    private final DBTool dbTool;
    private final String sql;
    private final List<Integer> paramTypes;
    private final Object[] params;
    private final Integer maxSize;
    private final Integer offset;
    private final IEntityMappingRegistry registry;
    private final Class<T> entityClass;
    private final Constructor<T> contructor;
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
        try {
            this.contructor = entityClass.getConstructor();
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
        this.contructor.setAccessible(true);
        this.paramsCount = paramTypes.size();

        if (dbTool.getDbType() == DBTool.DBType.ORACLE && this.maxSize != null) {
            this.params = new Object[this.paramsCount + 1];
            this.params[this.paramsCount] = this.maxSize;
        } else {
            this.params = new Object[this.paramsCount];
        }
    }

    public String getSql() {
        return sql;
    }

    public int getParamsCount() {
        return paramsCount;
    }

    public T select() {
        final Object[] result = new Object[1];

        SelectUtil.batchSelect(dbTool.getJdbcTemplate(), sql, new SingleObjectObjectExtractor<T>(),
                new SelectUtil.Container<Collection<T>>() {
                    public void add(Collection<T> objects) {
                        if (objects != null && !objects.isEmpty()) {
                            result[0] = objects.iterator().next();
                        }
                    }
                }, params, getResultSetType()
        );

        return (T) result[0];
    }

    public List<T> selectList() {
        final LinkedList<T> result = new LinkedList<T>();
        selectList(result);
        return result;
    }

    public void selectList(final List<T> result) {
        SelectUtil.batchSelect(dbTool.getJdbcTemplate(), sql, new ObjectExtractor<T>(), new SelectUtil.Container<Collection<T>>() {
            public void add(Collection<T> objects) {
                if (objects != null) {
                    result.addAll(objects);
                }
            }
        }, params, getResultSetType());

    }

    public <F> void selectMapList(String propertyName, final Map<F, List<T>> result, final ListProducer<T> listProducer) {
        FieldMapping fieldMapping = registry.getFieldMappingByPropertyName(entityClass, propertyName);
        if (fieldMapping == null) {
            throw new IllegalArgumentException("Unknown field name!");
        }

        SelectUtil.batchSelect(dbTool.getJdbcTemplate(), sql, new MapExtractor(fieldMapping),
                new SelectUtil.Container<Collection<KeyValue<F, T>>>() {
                    public void add(Collection<KeyValue<F, T>> objects) {
                        for (KeyValue<F, T> kv : objects) {
                            List<T> vs = result.get(kv.getKey());
                            if (vs == null) {
                                vs = listProducer.create();
                                result.put(kv.getKey(), vs);
                            }
                            vs.add(kv.getValue());
                        }
                    }
                }, params, getResultSetType());
    }

    public <F> void selectMap(String propertyName, final Map<F, T> result) {
        FieldMapping fieldMapping = registry.getFieldMappingByPropertyName(entityClass, propertyName);
        if (fieldMapping == null) {
            throw new IllegalArgumentException("Unknown field name!");
        }

        SelectUtil.batchSelect(dbTool.getJdbcTemplate(), sql, new MapExtractor(fieldMapping),
                new SelectUtil.Container<Collection<KeyValue<F, T>>>() {
                    public void add(Collection<KeyValue<F, T>> objects) {
                        for (KeyValue<F, T> kv : objects) {
                            result.put(kv.getKey(), kv.getValue());
                        }
                    }
                }, params, getResultSetType());

    }

    public Map<Object, T> selectMap(String propertyName) {
        final Map<Object, T> result = new LinkedHashMap<Object, T>();
        selectMap(propertyName, result);
        return result;
    }

    public Map<Object, List<T>> selectMapList(String propertyName) {
        final Map<Object, List<T>> result = new LinkedHashMap<Object, List<T>>();
        selectMapList(propertyName, result, ListProducer.LINKED_LIST);
        return result;
    }


    public IQuery<T> setParameter(int index, Object value) {
        if (index <= 0 || index > getParamsCount()) {
            throw new IllegalArgumentException("Index of parameter is wrong");
        }


        int type = paramTypes.get(index - 1);
        this.params[index - 1] = type == Integer.MAX_VALUE ? value : ((value instanceof Collection) ?
                new SqlCollectionParameterValue(type, (Collection) value) : new SqlParameterValue(type, value));

        return this;
    }


    @Override
    public String toString() {
        return "QueryImpl{" +
                "query='" + sql + '\'' +
                '}';
    }

    private int getResultSetType() {
        return this.offset == null ? ResultSet.TYPE_FORWARD_ONLY : ResultSet.TYPE_SCROLL_INSENSITIVE;
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
                    rs.absolute(QueryImpl.this.offset);
                } else {
                    return null;
                }
            }
            LinkedList<TYPE> objects = new LinkedList<TYPE>();
            Collection<FieldMapping> fieldMapping = registry.getFieldMappings(entityClass);

            while (rs.next()) {
                T obj;
                try {
                    obj = contructor.newInstance();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                readAndFill(rs, fieldMapping, obj);
                objects.add(getValue(obj));
            }

            return objects;
        }

        public abstract TYPE getValue(Object e);

        private void readAndFill(ResultSet rs, Collection<FieldMapping> fields, T obj) throws SQLException {
            for (FieldMapping idf : fields) {
                Object value = FieldValueExtractor.getValue(rs, idf.getColumn(), idf.getEntityFiled().getType());
                idf.getEntityFiled().setValue(obj, value);
            }
        }
    }

    private class SingleObjectObjectExtractor<T> extends ObjectExtractor<T> {
        private boolean getted = false;

        @Override
        public T getValue(Object e) {
            if (getted) {
                throw new IncorrectResultSizeDataAccessException(1);
            }
            getted = true;
            return super.getValue(e);
        }
    }
}
