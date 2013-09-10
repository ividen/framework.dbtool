package ru.kwanza.dbtool.orm.impl.querybuilder.db.mssql;

import ru.kwanza.dbtool.orm.api.IStatement;
import ru.kwanza.dbtool.orm.impl.querybuilder.AbstractQuery;
import ru.kwanza.dbtool.orm.impl.querybuilder.QueryConfig;

/**
 * @author Alexander Guzanov
 */
public class MSSQLQuery<T> extends AbstractQuery<T> {
    public MSSQLQuery(QueryConfig<T> config) {
        super(config);
    }

    @Override
    public IStatement<T> prepare() {
        return new MSSQLStatement<T>(config);
    }

}
