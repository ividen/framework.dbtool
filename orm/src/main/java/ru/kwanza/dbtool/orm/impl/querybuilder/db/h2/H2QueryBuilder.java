package ru.kwanza.dbtool.orm.impl.querybuilder.db.h2;

import ru.kwanza.dbtool.orm.api.IQuery;
import ru.kwanza.dbtool.orm.impl.EntityManagerImpl;
import ru.kwanza.dbtool.orm.impl.querybuilder.AbstractQueryBuilder;
import ru.kwanza.dbtool.orm.impl.querybuilder.QueryConfig;
import ru.kwanza.dbtool.orm.impl.querybuilder.db.mysql.MySQLQuery;

/**
 * @author Alexander Guzanov
 */
public class H2QueryBuilder<T> extends AbstractQueryBuilder<T> {

    public H2QueryBuilder(EntityManagerImpl em, Class entityClass) {
        super( em, entityClass);
    }

    protected IQuery<T> createQuery(QueryConfig config) {
        return new H2Query<T>(config);
    }
}
