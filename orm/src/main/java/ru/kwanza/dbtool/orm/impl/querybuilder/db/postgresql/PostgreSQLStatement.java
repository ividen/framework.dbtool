package ru.kwanza.dbtool.orm.impl.querybuilder.db.postgresql;

import ru.kwanza.dbtool.orm.impl.querybuilder.QueryConfig;
import ru.kwanza.dbtool.orm.impl.querybuilder.StatementImpl;

import java.util.Arrays;

/**
 * @author Michael Yeskov
 */
public class PostgreSQLStatement<T> extends StatementImpl<T> {
    public PostgreSQLStatement(QueryConfig<T> config) {
        super(config);
    }

    @Override
    protected String prepareSql(String sql) {
        if (isUsePaging()) {
            sql += " LIMIT ? OFFSET ?";
        }

        return sql;
    }

    @Override
    protected Object[] prepareParams(Object[] params) {
        if (!isUsePaging()) {
            return params;
        }

        final Object[] result = Arrays.copyOf(params, params.length + 2);
        params[params.length - 2] = getMaxSize();
        params[params.length - 1] = getOffset();

        return result;
    }
}
