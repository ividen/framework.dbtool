package ru.kwanza.dbtool.orm.impl.querybuilder.db.oracle;

import ru.kwanza.dbtool.core.DBTool;
import ru.kwanza.dbtool.orm.api.IQuery;
import ru.kwanza.dbtool.orm.api.internal.IEntityMappingRegistry;
import ru.kwanza.dbtool.orm.impl.EntityManagerImpl;
import ru.kwanza.dbtool.orm.impl.querybuilder.AbstractQueryBuilder;
import ru.kwanza.dbtool.orm.impl.querybuilder.QueryConfig;

/**
 * @author Alexander Guzanov
 */
public class OracleQueryBuilder<T> extends AbstractQueryBuilder<T> {

    public OracleQueryBuilder(EntityManagerImpl em, Class entityClass) {
        super(em, entityClass);
    }

    protected IQuery<T> createQuery(QueryConfig config) {
        return new OracleQuery<T>(config);
    }

}
