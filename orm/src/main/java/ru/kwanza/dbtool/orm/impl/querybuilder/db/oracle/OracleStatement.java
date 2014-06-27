package ru.kwanza.dbtool.orm.impl.querybuilder.db.oracle;

import ru.kwanza.dbtool.orm.impl.querybuilder.QueryConfig;
import ru.kwanza.dbtool.orm.impl.querybuilder.StatementImpl;

import java.util.Arrays;

/**
 * @author Alexander Guzanov
 */
public class OracleStatement<T> extends StatementImpl<T> {
    public OracleStatement(QueryConfig<T> config) {
        super(config);
    }

    @Override
    protected String prepareSql(String sql) {
        if (isUsePaging()) {
            sql = "SELECT  * FROM (" + sql + ") WHERE rownum <= ?";
        }
        return sql;
    }

    @Override
    protected boolean isSupportAbsoluteOffset() {
        return true;
    }

    @Override
    protected Object[] prepareParams(Object[] params) {
        if (!isUsePaging()) {
            return params;
        }

        final Object[] result = Arrays.copyOf(params, params.length + 1);
        result[result.length - 1] = getMaxSize() + getOffset();

        return result;
    }
}
