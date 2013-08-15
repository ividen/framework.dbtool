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

    protected StringBuilder createSQLString(String conditions, String orderBy, String fieldsString) {
        StringBuilder sql;
        if (usePaging) {
            sql = new StringBuilder("SELECT  * FROM (")
                    .append(createDefaultSQLString(fieldsString, conditions, orderBy))
                    .append(") WHERE rownum <= ?");
        } else {
            sql = createDefaultSQLString(fieldsString, conditions, orderBy);
        }
        return sql;
    }


}
