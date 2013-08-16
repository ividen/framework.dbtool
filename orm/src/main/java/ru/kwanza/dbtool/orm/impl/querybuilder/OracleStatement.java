package ru.kwanza.dbtool.orm.impl.querybuilder;

/**
 * @author Alexander Guzanov
 */
public class OracleStatement<T> extends StatementImpl<T> {
    public OracleStatement(QueryConfig<T> config) {
        super(config);
    }

    @Override
    protected Object[] createParamsArray(QueryConfig<T> config, int paramsCount) {
        Object[] params;
        if (config.isUsePaging()) {
            params = new Object[paramsCount + 1];
        } else {
            params = new Object[paramsCount];
        }

        return params;
    }

    @Override
    protected void installPagingParams(Object[] params, int maxSize, int offset) {
        params[params.length - 1] = (long) maxSize + (long) offset;
    }
}
