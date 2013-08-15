package ru.kwanza.dbtool.orm.impl.querybuilder;

/**
 * @author Michael Yeskov
 */
public class PostgreSQLStatement<T> extends StatementImpl<T> {
    public PostgreSQLStatement(QueryConfig<T> config, Integer offset, Integer maxSize) {
        super(config, offset, maxSize);
    }

    @Override
    protected Object[] createParamsArray(QueryConfig<T> config, int paramsCount, Integer maxSize, Integer offset) {
        int size = paramsCount + (config.isUsePaging() ? 2 : 0);
        Object[] params = new Object[size];

        if (config.isUsePaging()) {
            params[size - 2] = maxSize;
            params[size - 1] = offset;
        }

        return params;
    }
}
