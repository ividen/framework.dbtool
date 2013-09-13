package ru.kwanza.dbtool.orm.impl.querybuilder.db.mysql;

import ru.kwanza.dbtool.core.DBTool;
import ru.kwanza.dbtool.orm.api.IQuery;
import ru.kwanza.dbtool.orm.impl.mapping.IEntityMappingRegistry;
import ru.kwanza.dbtool.orm.impl.querybuilder.AbstractQueryBuilder;
import ru.kwanza.dbtool.orm.impl.querybuilder.QueryConfig;

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
}
