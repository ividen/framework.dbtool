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
import ru.kwanza.dbtool.orm.api.IStatement;
import ru.kwanza.dbtool.orm.api.ListProducer;
import ru.kwanza.dbtool.orm.impl.mapping.EntityField;
import ru.kwanza.dbtool.orm.impl.mapping.FieldMapping;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * @author Alexander Guzanov
 */
public abstract class StatementImpl<T> implements IStatement<T> {
    private final QueryConfig<T> config;
    private final Object[] params;

    public StatementImpl(QueryConfig<T> config) {
        this.config = config;

        int paramsCount = config.getParamsCount();
        Integer maxSize = config.getMaxSize();
        Integer offset = config.getOffset();
        params = createParamsArray(config, paramsCount, maxSize, offset);
    }

    protected abstract Object[] createParamsArray(QueryConfig<T> config, int paramsCount, Integer maxSize, Integer offset) ;


    public T select() {
        final Object[] result = new Object[1];

        SelectUtil.batchSelect(config.getDbTool().getJdbcTemplate(), config.getSql(),
                new SingleObjectObjectExtractor<T>(),
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
        SelectUtil.batchSelect(config.getDbTool().getJdbcTemplate(), config.getSql(),
                new ObjectExtractor<T>(), new SelectUtil.Container<Collection<T>>() {
            public void add(Collection<T> objects) {
                if (objects != null) {
                    result.addAll(objects);
                }
            }
        }, params, getResultSetType());

    }

    public <F> void selectMapList(String propertyName, final Map<F, List<T>> result, final ListProducer<T> listProducer) {
        FieldMapping fieldMapping = config.getRegistry().getFieldMappingByPropertyName(config.getEntityClass(), propertyName);
        if (fieldMapping == null) {
            throw new IllegalArgumentException("Unknown field name!");
        }

        SelectUtil.batchSelect(config.getDbTool().getJdbcTemplate(), config.getSql(), new MapExtractor(fieldMapping),
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
        FieldMapping fieldMapping = config.getRegistry().getFieldMappingByPropertyName(config.getEntityClass(), propertyName);
        if (fieldMapping == null) {
            throw new IllegalArgumentException("Unknown field name!");
        }

        SelectUtil.batchSelect(config.getDbTool().getJdbcTemplate(), config.getSql(), new MapExtractor(fieldMapping),
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


    public IStatement<T> setParameter(int index, Object value) {
        if (index <= 0 || index > config.getParamsCount()) {
            throw new IllegalArgumentException("Index of parameter is wrong");
        }


        int type = config.getParamTypes().get(index - 1);
        this.params[index - 1] = type == Integer.MAX_VALUE ? value : ((value instanceof Collection) ?
                new SqlCollectionParameterValue(type, (Collection) value) : new SqlParameterValue(type, value));

        return this;
    }

    public IStatement<T> setParameter(String name, Object value) {
        List<Integer> indexes = config.getNamedParams().get(name);
        if (indexes == null) {
            throw new IllegalStateException("Query doesn't constain named param!");
        }

        for (Integer index : indexes) {
            setParameter(index, value);
        }

        return this;
    }

    @Override
    public String toString() {
        return "Statement{" +
                "query='" + config.getSql() + '\'' +
                '}';
    }

    private int getResultSetType() {
        return config.getOffset() == null ? ResultSet.TYPE_FORWARD_ONLY : ResultSet.TYPE_SCROLL_INSENSITIVE;
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
            Integer offset = config.getOffset();
            if ((config.getDbTool().getDbType() != DBTool.DBType.MYSQL && offset != null && offset > 1) ||
                    config.getDbTool().getDbType()== DBTool.DBType.MYSQL && offset!=null && config.getMaxSize()==null) {
                if (rs.next()) {
                    rs.absolute(offset);
                } else {
                    return null;
                }
            }
            LinkedList<TYPE> objects = new LinkedList<TYPE>();
            Collection<FieldMapping> fieldMapping = config.getRegistry().getFieldMappings(config.getEntityClass());

            while (rs.next()) {
                T obj;
                try {
                    obj = config.getContructor().newInstance();
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
