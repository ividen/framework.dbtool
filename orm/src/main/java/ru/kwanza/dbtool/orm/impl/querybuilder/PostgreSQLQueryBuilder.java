package ru.kwanza.dbtool.orm.impl.querybuilder;

import ru.kwanza.dbtool.core.DBTool;
import ru.kwanza.dbtool.orm.api.IQuery;
import ru.kwanza.dbtool.orm.impl.mapping.IEntityMappingRegistry;

import java.util.List;

/**
 * @author Michael Yeskov
 */
public class PostgreSQLQueryBuilder<T> extends AbstractQueryBuilder<T> {

    public PostgreSQLQueryBuilder(DBTool dbTool, IEntityMappingRegistry registry, Class entityClass) {
        super(dbTool, registry, entityClass);
    }

    @Override
    protected IQuery<T> createQuery(QueryConfig config) {
        return new PostgreSQLQuery<T>(config);
    }

    protected StringBuilder createSQLString(String conditions, String orderBy, String fieldsString) {
        StringBuilder sql = createDefaultSQLString(fieldsString, conditions, orderBy);

        if (usePaging) {
            sql.append(" LIMIT ?");
            sql.append(" OFFSET ?");
        }

        return sql;
    }


}
