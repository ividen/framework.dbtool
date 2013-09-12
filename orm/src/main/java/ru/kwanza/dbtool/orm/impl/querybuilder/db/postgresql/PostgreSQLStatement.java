package ru.kwanza.dbtool.orm.impl.querybuilder.db.postgresql;

import ru.kwanza.dbtool.orm.impl.querybuilder.QueryConfig;
import ru.kwanza.dbtool.orm.impl.querybuilder.StatementImpl;

/**
 * @author Michael Yeskov
 */
public class PostgreSQLStatement<T> extends StatementImpl<T> {
    public PostgreSQLStatement(QueryConfig<T> config) {
        super(config);
    }

    @Override
    protected Object[] createParamsArray(QueryConfig<T> config, int paramsCount) {
        int size = paramsCount + (config.isUsePaging() ? 2 : 0);
        Object[] params = new Object[size];
        return params;
    }

    @Override
    protected void installPagingParams(Object[] params, int maxSize, int offset) {
        params[params.length - 2] = maxSize;
        params[params.length - 1] = offset;
    }
}
