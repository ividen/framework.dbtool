package ru.kwanza.dbtool.orm.impl.querybuilder.db.postgresql;

import ru.kwanza.dbtool.orm.api.IStatement;
import ru.kwanza.dbtool.orm.impl.querybuilder.AbstractQuery;
import ru.kwanza.dbtool.orm.impl.querybuilder.QueryConfig;

/**
 * @author Michael Yeskov
 */
public class PostgreSQLQuery<T> extends AbstractQuery<T> {
    public PostgreSQLQuery(QueryConfig<T> config) {
        super(config);
    }

    @Override
    public IStatement<T> prepare() {
        return new PostgreSQLStatement<T>(config);
    }

}
