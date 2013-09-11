package ru.kwanza.dbtool.orm.impl.querybuilder;

import ru.kwanza.dbtool.core.DBTool;
import ru.kwanza.dbtool.orm.api.IQuery;
import ru.kwanza.dbtool.orm.impl.mapping.IEntityMappingRegistry;

/**
 * @author Alexander Guzanov
 */
public class OracleQueryBuilder<T> extends AbstractQueryBuilder<T> {

    public OracleQueryBuilder(DBTool dbTool, IEntityMappingRegistry registry, Class entityClass) {
        super(dbTool, registry, entityClass);
    }

    protected IQuery<T> createQuery(QueryConfig config) {
        return new OracleQuery<T>(config);
    }

    protected StringBuilder createSQLString(String fieldsString, String from, String where, String orderBy) {
        StringBuilder sql = super.createSQLString(fieldsString,from , where, orderBy );
        if (usePaging) {
            sql = new StringBuilder("SELECT  * FROM (")
                    .append(sql)
                    .append(") WHERE rownum <= ?");
        }
        return sql;
    }


}
