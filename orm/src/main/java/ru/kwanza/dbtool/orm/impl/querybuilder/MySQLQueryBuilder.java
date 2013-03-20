package ru.kwanza.dbtool.orm.impl.querybuilder;

import ru.kwanza.dbtool.core.DBTool;
import ru.kwanza.dbtool.orm.api.IQuery;
import ru.kwanza.dbtool.orm.impl.mapping.IEntityMappingRegistry;

import java.util.List;

/**
 * @author Alexander Guzanov
 */
public class MySQLQueryBuilder<T> extends AbstractQueryBuilder<T> {

    public MySQLQueryBuilder(DBTool dbTool, IEntityMappingRegistry registry, Class entityClass) {
        super(dbTool, registry, entityClass);
    }

    protected IQuery<T> createQuery(List<Integer> paramsTypes, String sqlString) {
        return new MySQLQuery<T>(new QueryConfig<T>(dbTool, registry, entityClass,
                sqlString, maxSize, offset, paramsTypes, namedParams));
    }

    protected StringBuilder createSQLString(String conditions, String orderBy, String fieldsString) {
        StringBuilder sql;
        if (maxSize != null) {
            sql = new StringBuilder("SELECT ")
                    .append(fieldsString)
                    .append("FROM ")
                    .append(registry.getTableName(entityClass));
            if (conditions.length() > 0) {
                sql.append(" WHERE ").append(conditions);
            }

            if (orderBy.length() > 0) {
                sql.append(" ORDER BY ").append(orderBy);
            }
            sql.append("LIMIT");

            if (offset != null) {
                sql.append(" ?");

            } else {
                sql.append(" 0");
            }

            sql.append(",?");
        }else {
            sql = createDefaultSQLString(fieldsString, conditions, orderBy);
        }
        return sql;
    }


}
