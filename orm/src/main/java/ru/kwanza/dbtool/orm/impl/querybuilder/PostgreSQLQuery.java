package ru.kwanza.dbtool.orm.impl.querybuilder;

import ru.kwanza.dbtool.orm.api.IStatement;

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
