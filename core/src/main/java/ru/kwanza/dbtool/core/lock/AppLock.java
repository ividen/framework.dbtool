package ru.kwanza.dbtool.core.lock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.kwanza.dbtool.core.DBTool;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Класс поторый позволяет устанавливать блокировку
 */
public abstract class AppLock extends ReentrantLock {
    public static final Logger logger = LoggerFactory.getLogger(AppLock.class);

    protected DBTool dbTool;
    private String lockName;
    private boolean reentrant;

    //todo aguzanov подумать над исспользованием easygrid для эффективности хранение
    private static final ConcurrentMap<String, AppLock> locks = new ConcurrentHashMap<String, AppLock>();

    protected AppLock(DBTool dbTool, String lockName, boolean reentrant) throws SQLException {
        this.dbTool = dbTool;
        this.lockName = lockName;
        this.reentrant = reentrant;
    }

    /**
     * Имя блокировки
     */
    public String getLockName() {
        return lockName;
    }

    /**
     * Установить блокировку
     */
    public final void lock() {
        if (reEnterLock()) {
            return;
        }

        Connection connection = null;
        try {
            connection = checkNewConnection();
            doLock(connection);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            dbTool.closeResources(connection);
        }
    }

    protected abstract void doLock(Connection connection) throws SQLException;

    protected abstract void doUnLock(Connection connection);

    /**
     * Закрыть работу с блокировкой.
     *
     * Этот метод обязательно нужно вызывать в секции <b>finally</b>,
     * хотя он не снимает блокировки, а только освобождает ресурсы.
     *
     */
    public final void close() {
        if (exitReEnterLock()) {
            return;
        }

        Connection connection = null;
        try {
            connection = checkNewConnection();
            doUnLock(connection);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            dbTool.closeResources(connection);
        }
    }

    private boolean exitReEnterLock() {
        super.unlock();

        if (reentrant && isHeldByCurrentThread()) {
            return true;
        }
        return false;
    }

    private boolean reEnterLock() {

        try {
            if (reentrant && isHeldByCurrentThread()) {
                return true;
            }
            return false;
        } finally {
            super.lock();
        }
    }

    private Connection checkNewConnection() throws SQLException {
        Connection connection = dbTool.getJDBCConnection();
        if (connection.getAutoCommit()) {
            connection.setAutoCommit(false);
        }

        return connection;
    }

    public static AppLock defineLock(DBTool dbTool, String lockName, DBTool.DBType dbType, boolean reentrant) throws SQLException {
        AppLock appLock = locks.get(lockName);
        if (appLock == null) {
            if (dbType.equals(DBTool.DBType.MSSQL)) {
                appLock = new MSSQLAppLock(dbTool, lockName, reentrant);
            } else if (dbType.equals(DBTool.DBType.ORACLE)) {
                appLock = new OracleAppLock(dbTool, lockName, reentrant);
            } else if (dbType.equals(DBTool.DBType.MYSQL)) {
                appLock = new MySQLAppLock(dbTool, lockName, reentrant);
            } else {
                appLock = new DefaultAppLock(dbTool, lockName, reentrant);
            }

            if (null != locks.putIfAbsent(lockName, appLock)) {
                appLock = locks.get(lockName);
            }
        }

        return appLock;
    }
}
