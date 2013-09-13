package ru.kwanza.dbtool.orm.impl.querybuilder.db.mssql;

import ru.kwanza.dbtool.orm.impl.querybuilder.QueryConfig;
import ru.kwanza.dbtool.orm.impl.querybuilder.StatementImpl;

/**
 * @author Alexander Guzanov
 */
public class MSSQLStatement<T> extends StatementImpl<T> {

    private long top;

    public MSSQLStatement(QueryConfig config) {
        super(config);
    }

    @Override
    protected String prepareSql(String sql) {
        if (isUsePaging()) {
            sql = "SELECT TOP "+ (getOffset()+getMaxSize()) + " " + sql.substring("SELECT".length());
        }

        return sql;
    }

}
