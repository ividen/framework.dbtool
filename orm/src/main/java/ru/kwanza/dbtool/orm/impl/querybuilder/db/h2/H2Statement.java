package ru.kwanza.dbtool.orm.impl.querybuilder.db.h2;

import ru.kwanza.dbtool.orm.impl.querybuilder.QueryConfig;
import ru.kwanza.dbtool.orm.impl.querybuilder.StatementImpl;

import java.util.Arrays;

/**
 * @author Alexander Guzanov
 */
public class H2Statement<T> extends StatementImpl<T> {
    public H2Statement(QueryConfig<T> config) {
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
        result[result.length - 2] = getMaxSize() ;
        result[result.length - 1] = getOffset();

        return result;
    }
}
