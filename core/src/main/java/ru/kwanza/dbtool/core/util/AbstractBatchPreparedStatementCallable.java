package ru.kwanza.dbtool.core.util;

import org.postgresql.jdbc2.AbstractJdbc2Statement;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.jdbc.core.PreparedStatementCreator;
import ru.kwanza.dbtool.core.DBTool;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * @author Michael Yeskov
 */
public abstract class AbstractBatchPreparedStatementCallable implements PreparedStatementCallback, PreparedStatementCreator {

    protected final String sql;
    DBTool.DBType dbType;

    public AbstractBatchPreparedStatementCallable(String sql, DBTool.DBType dbType) {
        this.sql = sql;
        this.dbType = dbType;
    }

    public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
        PreparedStatement ps = con.prepareStatement(sql);
        if (DBTool.DBType.POSTGRESQL.equals(dbType)){
            (ps.unwrap(AbstractJdbc2Statement.class)).setContinueTrxOnErrors(true);
        }
        return ps;
    }
}
