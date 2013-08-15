package ru.kwanza.dbtool.orm.impl.querybuilder;

import ru.kwanza.dbtool.core.DBTool;
import ru.kwanza.dbtool.orm.api.IQuery;
import ru.kwanza.dbtool.orm.impl.mapping.IEntityMappingRegistry;

/**
 * @author Alexander Guzanov
 */
public class MSSQLQueryBuilder<T> extends AbstractQueryBuilder<T> {

    public static final String $_TOP = "$TOP$";

    public MSSQLQueryBuilder(DBTool dbTool, IEntityMappingRegistry registry, Class entityClass) {
        super(dbTool, registry, entityClass);
    }


    protected IQuery<T> createQuery(QueryConfig config) {
        return new MSSQLQuery<T>(config);
    }

    protected StringBuilder createSQLString(String conditions, String orderBy, String fieldsString) {
        StringBuilder sql;
        if (usePaging) {
            sql = new StringBuilder("SELECT TOP ")
                    .append($_TOP).append(' ')
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

     static String replaceTop(String sql, Long top) {
         return sql.replace($_TOP, String.valueOf(top));
    }
}
