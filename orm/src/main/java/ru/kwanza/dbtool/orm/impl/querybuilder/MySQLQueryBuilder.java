package ru.kwanza.dbtool.orm.impl.querybuilder;

import ru.kwanza.dbtool.core.DBTool;
import ru.kwanza.dbtool.orm.api.IQuery;
import ru.kwanza.dbtool.orm.impl.mapping.IEntityMappingRegistry;

/**
 * @author Alexander Guzanov
 */
public class MySQLQueryBuilder<T> extends AbstractQueryBuilder<T> {

    public MySQLQueryBuilder(DBTool dbTool, IEntityMappingRegistry registry, Class entityClass) {
        super(dbTool, registry, entityClass);
    }

    protected IQuery<T> createQuery(QueryConfig config) {
        return new MySQLQuery<T>(config);
    }

    protected StringBuilder createSQLString(String fieldsString, String from, String where, String orderBy) {
        StringBuilder sql = createSQLString(fieldsString, from, where, orderBy); ;
        if (isUsePaging()) {
            sql.append("LIMIT ?,?");
        } else {

        }
        return sql;
    }

}
