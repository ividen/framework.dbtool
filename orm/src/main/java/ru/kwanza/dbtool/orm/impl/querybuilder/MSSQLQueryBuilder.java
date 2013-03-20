package ru.kwanza.dbtool.orm.impl.querybuilder;

import ru.kwanza.dbtool.core.DBTool;
import ru.kwanza.dbtool.orm.api.IQuery;
import ru.kwanza.dbtool.orm.impl.mapping.IEntityMappingRegistry;

import java.util.List;

/**
 * @author Alexander Guzanov
 */
public class MSSQLQueryBuilder<T> extends AbstractQueryBuilder<T> {

    public MSSQLQueryBuilder(DBTool dbTool, IEntityMappingRegistry registry, Class entityClass) {
        super(dbTool, registry, entityClass);
    }


    protected IQuery<T> createQuery(List<Integer> paramsTypes, String sqlString) {
        return new MSSQLQuery<T>(new QueryConfig<T>(dbTool, registry, entityClass,
                sqlString, maxSize, offset, paramsTypes, namedParams));
    }

    protected StringBuilder createSQLString(String conditions, String orderBy, String fieldsString) {
        StringBuilder sql;
        if (maxSize != null) {
            long size = (offset == null ? 0 : offset) + maxSize;
            sql = new StringBuilder("SELECT TOP ")
                    .append(size).append(' ')
                    .append(fieldsString)
                    .append("FROM ")
                    .append(registry.getTableName(entityClass));
            if (conditions.length() > 0) {
                sql.append(" WHERE ").append(conditions);
            }

            if (orderBy.length() > 0) {
                sql.append(" ORDER BY ").append(orderBy);
            }

        } else {
            sql = createDefaultSQLString(fieldsString, conditions, orderBy);
        }
        return sql;
    }


}
