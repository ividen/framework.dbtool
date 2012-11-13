package ru.kwanza.dbtool.orm.impl;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.SqlParameterValue;
import ru.kwanza.dbtool.core.DBTool;
import ru.kwanza.dbtool.core.SqlCollectionParameterValue;
import ru.kwanza.dbtool.core.util.FieldValueExtractor;
import ru.kwanza.dbtool.core.util.SelectUtil;
import ru.kwanza.dbtool.orm.Filter;
import ru.kwanza.dbtool.orm.IQuery;
import ru.kwanza.dbtool.orm.mapping.FieldMapping;
import ru.kwanza.dbtool.orm.mapping.IEntityMappingRegistry;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

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


    public QueryImpl(DBTool dbTool, IEntityMappingRegistry registry, Class<T> entityClass, String sql, Integer maxSize, Integer offset,
                     List<Integer> paramTypes) {
        this.dbTool = dbTool;
        this.sql = sql;
        this.maxSize = maxSize;
        this.offset = offset;
        this.paramTypes = paramTypes;
        this.registry = registry;
        this.entityClass = entityClass;

        int paramsCount = paramTypes.size();
        if (maxSize != null) {
            paramsCount++;
        }

        this.params = new Object[paramsCount];
        if (maxSize != null) {
            if (dbTool.getDbType() == DBTool.DBType.ORACLE) {
                this.params[paramsCount] = this.maxSize;
            } else {
                this.params[0] = this.maxSize;
            }
        }
    }

    public T select() {
        return null;
    }

    public T selectWithFilter(Filter... filters) {
        return null;
    }

    public List<T> selectList() {
        final LinkedList<T> result = new LinkedList<T>();
        SelectUtil.batchSelect(dbTool.getJdbcTemplate(), sql, new Extractor<T>(), new SelectUtil.Container<Collection<T>>() {
            public void add(Collection<T> objects) {
                if (objects != null) {
                    result.addAll(objects);
                }
            }
        }, params);

        return result;
    }

    public List<T> selectListWithFilter(Filter... filters) {
        return null;
    }

    public IQuery<T> setParameter(int index, Object value) {
        if (index <= 0 || index > params.length - 1) {
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

    private class Extractor<T> implements ResultSetExtractor {
        public Collection<T> extractData(ResultSet rs) throws SQLException, DataAccessException {
            if (QueryImpl.this.offset != null && QueryImpl.this.offset > 1) {
                if (rs.next()) {
                    rs.absolute(QueryImpl.this.offset - 1);
                } else {
                    return null;
                }
            }
            LinkedList<T> objects = new LinkedList<T>();

            Collection<FieldMapping> idFields = registry.getIDFields(entityClass);
            Collection<FieldMapping> fieldMapping = registry.getFieldMapping(entityClass);
            Collection<FieldMapping> versionField = Collections.singletonList(registry.getVersionField(entityClass));

            while (rs.next()) {
                T obj;
                try {
                    obj = (T) entityClass.newInstance();
                } catch (Exception e) {
                    throw new RuntimeException();
                }

                readAndFill(rs, idFields, obj);
                readAndFill(rs, fieldMapping, obj);
                readAndFill(rs, versionField, obj);

                objects.add(obj);
            }

            return objects;
        }

        private void readAndFill(ResultSet rs, Collection<FieldMapping> fields, T obj) throws SQLException {
            for (FieldMapping idf : fields) {
                Object value = FieldValueExtractor.getValue(rs, idf.getColumnName(), idf.getEntityFiled().getType());
                idf.getEntityFiled().setValue(obj, value);
            }
        }
    }
}
