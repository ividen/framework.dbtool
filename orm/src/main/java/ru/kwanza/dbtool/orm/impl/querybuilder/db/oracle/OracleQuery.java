package ru.kwanza.dbtool.orm.impl.querybuilder.db.oracle;

import ru.kwanza.dbtool.orm.api.IStatement;
import ru.kwanza.dbtool.orm.impl.querybuilder.AbstractQuery;
import ru.kwanza.dbtool.orm.impl.querybuilder.QueryConfig;

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
