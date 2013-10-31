package ru.kwanza.dbtool.orm.impl.querybuilder.db.postgresql;

import ru.kwanza.dbtool.core.DBTool;
import ru.kwanza.dbtool.orm.api.IQuery;
import ru.kwanza.dbtool.orm.api.internal.IEntityMappingRegistry;
import ru.kwanza.dbtool.orm.impl.EntityManagerImpl;
import ru.kwanza.dbtool.orm.impl.querybuilder.AbstractQueryBuilder;
import ru.kwanza.dbtool.orm.impl.querybuilder.QueryConfig;

/**
 * @author Michael Yeskov
 */
public class PostgreSQLQueryBuilder<T> extends AbstractQueryBuilder<T> {

    public PostgreSQLQueryBuilder(EntityManagerImpl em, Class entityClass) {
        super(em, entityClass);
    }

    @Override
    protected IQuery<T> createQuery(QueryConfig config) {
        return new PostgreSQLQuery<T>(config);
    }
}
