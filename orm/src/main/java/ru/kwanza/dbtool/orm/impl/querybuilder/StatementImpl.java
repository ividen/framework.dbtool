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

import static ru.kwanza.dbtool.core.DBTool.DBType.MYSQL;
import static ru.kwanza.dbtool.core.DBTool.DBType.POSTGRESQL;

/**
 * @author Alexander Guzanov
 */
public abstract class StatementImpl<T> implements IStatement<T> {
    private final QueryConfig<T> config;
    private final Object[] params;
    private Integer maxSize;
    private Integer offset;

    public StatementImpl(QueryConfig<T> config) {
        this.config = config;
        int paramsCount = config.getParamsCount();
        params = createParamsArray(config, paramsCount);
    }

    protected abstract Object[] createParamsArray(QueryConfig<T> config, int paramsCount);

    public T select() {
        final Object[] result = new Object[1];

        SelectUtil.batchSelect(config.getDbTool().getJdbcTemplate(), prepareSql(), new SingleObjectObjectExtractor<T>(),
                new SelectUtil.Container<Collection<T>>() {
                    public void add(Collection<T> objects) {
                        if (objects != null && !objects.isEmpty()) {
                            result[0] = objects.iterator().next();
                        }
                    }
                }, params, getResultSetType());

        return (T) result[0];
    }

    public IStatement<T> paging(int offset, int maxSize) {
        if (!config.isUsePaging()) {
            throw new IllegalStateException("Query don't build with paging");
        }
        this.maxSize = maxSize;
        this.offset = offset;

        installPagingParams(params, maxSize, offset);

        return this;
    }

    protected abstract void installPagingParams(Object[] params, int maxSize, int offset);

    public QueryConfig<T> getConfig() {
        return config;
    }

    protected String prepareSql() {
        return config.getSql();
    }

    public List<T> selectList() {
        final LinkedList<T> result = new LinkedList<T>();
        selectList(result);
        return result;
    }

    public void selectList(final List<T> result) {
        SelectUtil.batchSelect(config.getDbTool().getJdbcTemplate(), prepareSql(), new ObjectExtractor<T>(),
                new SelectUtil.Container<Collection<T>>() {
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

        SelectUtil.batchSelect(config.getDbTool().getJdbcTemplate(), prepareSql(), new MapExtractor(fieldMapping),
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

        SelectUtil.batchSelect(config.getDbTool().getJdbcTemplate(), prepareSql(), new MapExtractor(fieldMapping),
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
        this.params[index - 1] = type == Integer.MAX_VALUE
                ? value
                : ((value instanceof Collection)
                        ? new SqlCollectionParameterValue(type, (Collection) value)
                        : new SqlParameterValue(type, value));

        return this;
    }

    public IStatement<T> setParameter(String name, Object value) {
        List<Integer> indexes = config.getHolder().get(name);
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
                "query='" + prepareSql() + '\'' +
                '}';
    }

    private int getResultSetType() {
        return !config.isUsePaging() ? ResultSet.TYPE_FORWARD_ONLY : ResultSet.TYPE_SCROLL_INSENSITIVE;
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

            Integer offset = StatementImpl.this.offset;
            DBTool.DBType dbType = config.getDbTool().getDbType();
            if ((MYSQL != dbType && POSTGRESQL != dbType && offset != null && offset > 0) || (MYSQL == dbType && offset != null
                    && offset > 0 && StatementImpl.this.maxSize == null)) {
                if (rs.next()) {
                    rs.absolute(offset);
                } else {
                    return null;
                }
            }
            ArrayList<TYPE> objects = new ArrayList<TYPE>();

            while (rs.next()) {
                T result;
                try {
                    result = config.getEntityClass().newInstance();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }

                readAndFill(rs, config.getEntityClass(), config.getRootRelation(), result);
                readRelation(result, config.getRootRelation(), rs);

                objects.add(getValue(result));
            }

            return objects;
        }

        public abstract TYPE getValue(Object e);

        private void readRelation(Object parentObj, JoinRelation relation, ResultSet rs) throws SQLException {
            if (relation == null) {
                return;
            }

            Object obj;
            if (!relation.isRoot()) {
                final Class relationClass = relation.getFetchMapping().getRelationClass();
                if (!hasIdValue(relation, rs, relationClass)) {
                    return;
                }
                try {
                    obj = relationClass.newInstance();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }

                readAndFill(rs, relationClass, relation, obj);
            } else {
                obj = parentObj;
            }

            if (relation.getAllChilds() != null) {
                for (JoinRelation joinRelation : relation.getAllChilds().values()) {
                    readRelation(obj, joinRelation, rs);
                }
            }

            if (relation.getFetchMapping() != null) {
                relation.getFetchMapping().getFetchField().setValue(parentObj, obj);
            }
        }

        private boolean hasIdValue(JoinRelation relation, ResultSet rs, Class relationClass) throws SQLException {
            FieldMapping idField = config.getRegistry().getIdFields(relationClass).iterator().next();
            if (FieldValueExtractor.getValue(rs, relation.getAlias() + "_" + idField.getColumn(), idField.getEntityFiled().getType())
                    == null) {
                return false;
            }
            return true;
        }

        private void readAndFill(ResultSet rs, Class entityClass, JoinRelation relation, Object obj) throws SQLException {
            String alias = relation.getAlias();
            for (FieldMapping idf : config.getRegistry().getFieldMappings(entityClass)) {
                Object value = FieldValueExtractor.getValue(rs, alias + idf.getColumn(), idf.getEntityFiled().getType());
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
