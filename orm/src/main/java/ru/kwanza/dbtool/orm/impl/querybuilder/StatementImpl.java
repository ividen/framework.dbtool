package ru.kwanza.dbtool.orm.impl.querybuilder;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.SqlParameterValue;
import org.springframework.jdbc.core.SqlTypeValue;
import ru.kwanza.dbtool.core.DBTool;
import ru.kwanza.dbtool.core.KeyValue;
import ru.kwanza.dbtool.core.SqlCollectionParameterValue;
import ru.kwanza.dbtool.core.util.FieldValueExtractor;
import ru.kwanza.dbtool.core.util.SelectUtil;
import ru.kwanza.dbtool.orm.api.IStatement;
import ru.kwanza.dbtool.orm.api.ListProducer;
import ru.kwanza.dbtool.orm.api.internal.IEntityType;
import ru.kwanza.dbtool.orm.api.internal.IFieldMapping;
import ru.kwanza.dbtool.orm.impl.ObjectAllocator;
import ru.kwanza.dbtool.orm.impl.mapping.UnionEntityType;
import ru.kwanza.toolbox.fieldhelper.FieldHelper;
import ru.kwanza.toolbox.fieldhelper.Property;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import static ru.kwanza.dbtool.core.DBTool.DBType.H2;
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
        this.params = config.getParamsHolder().createParamsArray();
    }

    public T select() {
        final Object[] result = new Object[1];

        SelectUtil.batchSelect(config.getEntityManager().getDbTool().getJdbcTemplate(), prepareSql(config.getSql()),
                new SingleObjectObjectExtractor<T>(), new SelectUtil.Container<Collection<T>>() {
            public void add(Collection<T> objects) {
                if (objects != null && !objects.isEmpty()) {
                    result[0] = objects.iterator().next();
                }
            }
        }, getParamValues(), getResultSetType());

        return (T) result[0];
    }

    private Object[] getParamValues() {
        return prepareParams(config.getParamsHolder().fullParamsArray(params));
    }

    public IStatement<T> paging(int offset, int maxSize) {
        this.maxSize = maxSize;
        this.offset = offset;

        return this;
    }

    protected boolean isUsePaging() {
        return maxSize != null && offset != null;
    }

    protected Integer getMaxSize() {
        return maxSize;
    }

    protected Integer getOffset() {
        return offset;
    }

    public QueryConfig<T> getConfig() {
        return config;
    }

    protected String prepareSql(String sql) {
        return sql;
    }

    protected Object[] prepareParams(Object[] params) {
        return params;
    }

    public List<T> selectList() {
        final ArrayList<T> result = new ArrayList<T>();
        selectList(result);
        return result;
    }

    public void selectList(final List<T> result) {
        SelectUtil
                .batchSelect(config.getEntityManager().getDbTool().getJdbcTemplate(), prepareSql(config.getSql()), new ObjectExtractor<T>(),
                        new SelectUtil.Container<Collection<T>>() {
                            public void add(Collection<T> objects) {
                                if (objects != null) {
                                    result.addAll(objects);
                                }
                            }
                        }, getParamValues(), getResultSetType());
        fetchLazyIfNeed(result);
    }

    private void fetchLazyIfNeed(Collection<T> result) {
        if (config.isLazy()) {
            config.getEntityManager().fetchLazy(config.getEntityClass(), result);
        }
    }

    public <F> void selectMapList(String propertyName, final Map<F, List<T>> result, final ListProducer<T> listProducer) {
        IFieldMapping fieldMapping = config.getEntityManager().getRegistry().getEntityType(config.getEntityClass()).getField(propertyName);
        if (fieldMapping == null) {
            throw new IllegalArgumentException("Unknown field name!");
        }
        final List<T> containerForLazy = config.isLazy() ? new ArrayList<T>() : null;

        SelectUtil.batchSelect(config.getEntityManager().getDbTool().getJdbcTemplate(), prepareSql(config.getSql()),
                new MapExtractor(fieldMapping), new SelectUtil.Container<Collection<KeyValue<F, T>>>() {
            public void add(Collection<KeyValue<F, T>> objects) {
                for (KeyValue<F, T> kv : objects) {
                    List<T> vs = result.get(kv.getKey());
                    if (vs == null) {
                        vs = listProducer.create();
                        result.put(kv.getKey(), vs);
                    }
                    vs.add(kv.getValue());
                    if (config.isLazy()) {
                        containerForLazy.add(kv.getValue());
                    }
                }
            }
        }, getParamValues(), getResultSetType());

        fetchLazyIfNeed(containerForLazy);
    }

    public <F> void selectMap(String propertyName, final Map<F, T> result) {
        IFieldMapping fieldMapping = config.getEntityManager().getRegistry().getEntityType(config.getEntityClass()).getField(propertyName);
        if (fieldMapping == null) {
            throw new IllegalArgumentException("Unknown field name!");
        }

        SelectUtil.batchSelect(config.getEntityManager().getDbTool().getJdbcTemplate(), prepareSql(config.getSql()),
                new MapExtractor(fieldMapping), new SelectUtil.Container<Collection<KeyValue<F, T>>>() {
            public void add(Collection<KeyValue<F, T>> objects) {
                for (KeyValue<F, T> kv : objects) {
                    result.put(kv.getKey(), kv.getValue());
                }
            }
        }, getParamValues(), getResultSetType());

        fetchLazyIfNeed(result.values());
    }

    public Map<Object, T> selectMap(String propertyName) {
        final Map<Object, T> result = new LinkedHashMap<Object, T>();
        selectMap(propertyName, result);
        return result;
    }

    public Map<Object, List<T>> selectMapList(String propertyName) {
        final Map<Object, List<T>> result = new LinkedHashMap<Object, List<T>>();
        selectMapList(propertyName, result, ListProducer.ARRAY_LIST);
        return result;
    }

    public IStatement<T> setParameter(int index, Object value) {
        if (index <= 0 || index > config.getParamsHolder().getCount()) {
            throw new IllegalArgumentException("Index of parameter is wrong");
        }

        int type = config.getParamsHolder().getParamType(index - 1);
        this.params[index - 1] = type == SqlTypeValue.TYPE_UNKNOWN
                ? value
                : ((value instanceof Collection)
                ? new SqlCollectionParameterValue(type, (Collection) value)
                : new SqlParameterValue(type, value));

        return this;
    }

    public IStatement<T> setParameter(String name, Object value) {
        List<Integer> indexes = config.getParamsHolder().getIndexes(name);
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
                "query='" + prepareSql(config.getSql()) + '\'' +
                '}';
    }

    private int getResultSetType() {
        return !isUsePaging() ? ResultSet.TYPE_FORWARD_ONLY : ResultSet.TYPE_SCROLL_INSENSITIVE;
    }

    private class ObjectExtractor<T> extends BaseExtractor<T> {
        @Override
        protected Collection getFetchableObjects(ArrayList<T> objects) {
            return objects;
        }

        @Override
        public T getValue(Object e) {
            return (T) e;
        }
    }

    private class MapExtractor extends BaseExtractor<KeyValue<Object, T>> {
        private Property field;

        private MapExtractor(IFieldMapping mapping) {
            this.field = mapping.getProperty();
        }

        @Override
        protected Collection getFetchableObjects(ArrayList<KeyValue<Object, T>> objects) {
            return FieldHelper.getFieldCollection(objects, new FieldHelper.Field<KeyValue<Object, T>, T>() {
                public Object value(KeyValue o) {
                    return o.getValue();
                }
            });
        }

        @Override
        public KeyValue<Object, T> getValue(Object e) {
            return new KeyValue<Object, T>(field.value(e), (T) e);
        }
    }

    private abstract class BaseExtractor<TYPE> implements ResultSetExtractor {

        public Collection<TYPE> extractData(ResultSet rs) throws SQLException, DataAccessException {

            Integer offset = StatementImpl.this.offset;
            DBTool.DBType dbType = config.getEntityManager().getDbTool().getDbType();
            if ((MYSQL != dbType && POSTGRESQL != dbType && H2 != dbType && offset != null && offset > 0) || (MYSQL == dbType && offset != null
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
                final IEntityType entityType = getEntityType(config.getRoot(), rs, config.getEntityClass());
                result = (T) createObject(entityType);
                readAndFill(rs, entityType.getFields(), config.getRoot(), result);
                readEntities(result, config.getRoot(), rs);

                objects.add(getValue(result));
            }

            if (!config.getFetchInfo().isEmpty()) {
                config.getEntityManager().getFetcher().fetch(getFetchableObjects(objects), config.getFetchInfo());
            }

            return objects;
        }

        protected abstract Collection getFetchableObjects(ArrayList<TYPE> objects);

        public abstract TYPE getValue(Object e);

        private void readEntities(Object parentObj, EntityInfo entityInfo, ResultSet rs) throws SQLException {
            Object obj;
            if (!entityInfo.isRoot()) {
                final Class relationClass = entityInfo.getRelationMapping().getRelationClass();
                if (!hasIdValue(entityInfo, rs, relationClass)) {
                    return;
                }

                IEntityType entityType = getEntityType(entityInfo, rs, relationClass);

                obj = createObject(entityType);

                readAndFill(rs, entityType.getFields(), entityInfo, obj);
            } else {
                obj = parentObj;
            }

            if (entityInfo.getJoins() != null) {
                for (EntityInfo subEntityInfo : entityInfo.getJoins().values()) {
                    readEntities(obj, subEntityInfo, rs);
                }
            }

            if (entityInfo.getRelationMapping() != null) {
                entityInfo.getRelationMapping().getProperty().set(parentObj, obj);
            }
        }

        private Object createObject(IEntityType entityType) {
            Object obj;
            try {
                obj = ObjectAllocator.newInstance(entityType.getEntityClass());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            return obj;
        }

        private IEntityType getEntityType(EntityInfo entityInfo, ResultSet rs, Class entityClass) throws SQLException {
            IEntityType entityType = config.getEntityManager().getRegistry().getEntityType(entityClass);
            if (entityType instanceof UnionEntityType) {

                final UnionEntityType unionEntityType = (UnionEntityType) entityType;
                entityType = unionEntityType.getEntity(rs.getInt(Column.getFullColumnName(entityInfo, UnionEntityType.getClazzField())));
            }
            return entityType;
        }

        private boolean hasIdValue(EntityInfo entityInfo, ResultSet rs, Class entityClass) throws SQLException {
            IFieldMapping idField = config.getEntityManager().getRegistry().getEntityType(entityClass).getIdField();
            if (FieldValueExtractor.getValue(rs, Column.getFullColumnName(entityInfo, idField), idField.getProperty().getType()) == null) {
                return false;
            }
            return true;
        }

        private void readAndFill(ResultSet rs, Collection<IFieldMapping> fields, EntityInfo entityInfo, Object obj) throws SQLException {
            for (IFieldMapping idf : fields) {
                Object value = FieldValueExtractor.getValue(rs, Column.getFullColumnName(entityInfo, idf), idf.getProperty().getType());
                idf.getProperty().set(obj, value);
            }

        }
    }

    private class SingleObjectObjectExtractor<T> extends ObjectExtractor<T> {
        private boolean getted = false;

        @Override
        protected Collection getFetchableObjects(ArrayList<T> objects) {
            return objects;
        }

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
