package ru.kwanza.dbtool.orm.impl.querybuilder.db.mssql;

import ru.kwanza.dbtool.core.DBTool;
import ru.kwanza.dbtool.orm.api.IQuery;
import ru.kwanza.dbtool.orm.api.internal.IEntityMappingRegistry;
import ru.kwanza.dbtool.orm.impl.EntityManagerImpl;
import ru.kwanza.dbtool.orm.impl.querybuilder.AbstractQueryBuilder;
import ru.kwanza.dbtool.orm.impl.querybuilder.QueryConfig;

/**
 * @author Alexander Guzanov
 */
public class MSSQLQueryBuilder<T> extends AbstractQueryBuilder<T> {

    public MSSQLQueryBuilder(EntityManagerImpl em, Class entityClass) {
        super(em, entityClass);
    }

    protected IQuery<T> createQuery(QueryConfig config) {
        return new MSSQLQuery<T>(config);
    }
}
