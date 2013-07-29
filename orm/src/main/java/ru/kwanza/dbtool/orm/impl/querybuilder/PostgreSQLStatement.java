package ru.kwanza.dbtool.orm.impl.querybuilder;

/**
 * @author Michael Yeskov
 */
public class PostgreSQLStatement<T> extends StatementImpl<T> {
    public PostgreSQLStatement(QueryConfig<T> config) {
        super(config);
    }

    @Override
    protected Object[] createParamsArray(QueryConfig<T> config, int paramsCount, Integer maxSize, Integer offset) {
        int size = paramsCount + (maxSize != null ? 1 : 0) + (offset != null ? 1 : 0);
        Object[] params = new Object[size];

        if (maxSize != null && offset != null) {
            params[size - 2] = maxSize;
            params[size - 1] = offset;
        } else if (maxSize != null){
            params[size - 1] = maxSize;
        } else if (offset != null) {
            params[size - 1] = offset;
        }

        return params;
    }
}
