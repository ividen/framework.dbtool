package ru.kwanza.dbtool.orm.impl.querybuilder.db.mysql;

import ru.kwanza.dbtool.orm.impl.querybuilder.QueryConfig;
import ru.kwanza.dbtool.orm.impl.querybuilder.StatementImpl;

import java.util.Arrays;

/**
 * @author Alexander Guzanov
 */
public class MySQLStatement<T> extends StatementImpl<T> {
    public MySQLStatement(QueryConfig<T> config) {
        super(config);
    }

    @Override
    protected String prepareSql(String sql) {
        if (isUsePaging()) {
            sql += " LIMIT ?,?";
        }
        return sql;
    }

    @Override
    protected Object[] prepareParams(Object[] params) {
        if (!isUsePaging()) {
            return params;
        }

        final Object[] result = Arrays.copyOf(params, params.length + 2);
        result[result.length - 2] = getOffset();
        result[result.length - 1] = getMaxSize();

        return result;

    }
}
