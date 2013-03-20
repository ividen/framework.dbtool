package ru.kwanza.dbtool.orm.impl.querybuilder;

import ru.kwanza.dbtool.orm.api.IStatement;

/**
 * @author Alexander Guzanov
 */
public class OracleQuery<T> extends AbstractQuery<T> {
    public OracleQuery(QueryConfig<T> config) {
        super(config);
    }

    @Override
    public IStatement<T> prepare() {
        return new OracleStatement<T>(config);
    }
}
