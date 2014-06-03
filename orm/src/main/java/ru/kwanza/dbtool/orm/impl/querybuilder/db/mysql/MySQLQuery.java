package ru.kwanza.dbtool.orm.impl.querybuilder.db.mysql;

import ru.kwanza.dbtool.orm.api.IStatement;
import ru.kwanza.dbtool.orm.impl.querybuilder.AbstractQuery;
import ru.kwanza.dbtool.orm.impl.querybuilder.QueryConfig;

/**
 * @author Alexander Guzanov
 */
public class MySQLQuery<T> extends AbstractQuery<T> {
    public MySQLQuery(QueryConfig<T> config) {
        super(config);
    }

    @Override
    public IStatement<T> prepare() {
        return new MySQLStatement<T>(config);
    }

}
