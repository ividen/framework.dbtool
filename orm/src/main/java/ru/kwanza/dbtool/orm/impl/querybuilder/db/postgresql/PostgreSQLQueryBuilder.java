package ru.kwanza.dbtool.orm.impl.querybuilder.db.postgresql;

import ru.kwanza.dbtool.core.DBTool;
import ru.kwanza.dbtool.orm.api.IQuery;
import ru.kwanza.dbtool.orm.impl.mapping.IEntityMappingRegistry;
import ru.kwanza.dbtool.orm.impl.querybuilder.AbstractQueryBuilder;
import ru.kwanza.dbtool.orm.impl.querybuilder.QueryConfig;

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
}
