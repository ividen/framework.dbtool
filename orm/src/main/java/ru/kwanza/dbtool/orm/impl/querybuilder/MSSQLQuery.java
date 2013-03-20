package ru.kwanza.dbtool.orm.impl.querybuilder;

import ru.kwanza.dbtool.orm.api.IStatement;

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
