package ru.kwanza.dbtool.core.lock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.kwanza.dbtool.core.DBTool;

import java.sql.Connection;
import java.sql.SQLException;

public abstract class AppLock {
    public static final Logger logger = LoggerFactory.getLogger(AppLock.class);

    protected DBTool dbTool;
    private String lockName;
    protected Connection conn;

    protected AppLock(DBTool dbTool, String lockName) throws SQLException {
        this.dbTool = dbTool;
        this.lockName = lockName;
    }

    public String getLockName() {
        return lockName;
    }

    public final void lock() {
        try {
            checkNewConnection();
            doLock();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public abstract void doLock() throws SQLException;

    public void close() {
        try {
            if (conn.isClosed()) {
                throw new RuntimeException("Connection is closed");
            }
            conn.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void checkNewConnection() throws SQLException {
        conn = dbTool.getJDBCConnection();
        if (conn.getAutoCommit()) {
            conn.setAutoCommit(false);
        }
    }

    public static AppLock defineLock(DBTool dbTool, String lockName, DBTool.DBType dbType) throws SQLException {
        if (dbType.equals(DBTool.DBType.MSSQL)) {
            return new MSSQLAppLock(dbTool, lockName);
        } else if (dbType.equals(DBTool.DBType.ORACLE)) {
            return new OracleAppLock(dbTool, lockName);
        } else if (dbType.equals(DBTool.DBType.MYSQL)) {
            return new MySQLAppLock(dbTool, lockName);
        } else {
            return new DefaultAppLock(dbTool, lockName);
        }
    }
}
