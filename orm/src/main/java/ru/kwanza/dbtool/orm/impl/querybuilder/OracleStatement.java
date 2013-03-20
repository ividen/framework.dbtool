package ru.kwanza.dbtool.orm.impl.querybuilder;

/**
 * @author Alexander Guzanov
 */
public class OracleStatement<T> extends StatementImpl<T> {
    public OracleStatement(QueryConfig<T> config) {
        super(config);
    }

    @Override
    protected Object[] createParamsArray(QueryConfig<T> config, int paramsCount, Integer maxSize, Integer offset) {
        Object[] params;
        if (maxSize != null) {
            params = new Object[paramsCount + 1];
            params[paramsCount] = maxSize + (offset == null ? 0 : offset);

        } else {
            params = new Object[paramsCount];
        }

        return params;
    }
}
