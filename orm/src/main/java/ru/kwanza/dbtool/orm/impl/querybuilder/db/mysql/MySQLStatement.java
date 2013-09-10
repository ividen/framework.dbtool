package ru.kwanza.dbtool.orm.impl.querybuilder.db.mysql;

import ru.kwanza.dbtool.orm.impl.querybuilder.QueryConfig;
import ru.kwanza.dbtool.orm.impl.querybuilder.StatementImpl;

/**
 * @author Alexander Guzanov
 */
public class MySQLStatement<T> extends StatementImpl<T> {
    public MySQLStatement(QueryConfig<T> config) {
        super(config);
    }

    @Override
    protected Object[] createParamsArray(QueryConfig<T> config, int paramsCount) {
        Object[] result;
        if (config.isUsePaging()) {
            result = new Object[paramsCount + 2];
        } else {
            result = new Object[paramsCount];
        }
        return result;
    }

    @Override
    protected void installPagingParams(Object[] params, int maxSize, int offset) {
        params[params.length - 2] = offset;
        params[params.length - 1] = maxSize;
    }
}
