package ru.kwanza.dbtool.lock;

import ru.kwanza.dbtool.DBTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * ���������� �� ������ ����������.
 * <p/>
 * ��������� ���������������� ���������� �������� ���������� �� ���� �������������
 * ��������� ���������� ���� MS SQL ������� � ������ 2005 � Oracle.
 * <p/>
 * ��� ��������� ���� ����������� ������ ���������� ������ � ����������� �������,
 * ������ ������ ������� ���������� ��������� ������.
 * <p/>
 * ���������� ����������� � ���������� � ������������� ���� ��� ������� ����������.
 * ���������� �� �������� � ������ autocommit.
 * <p/>
 * ������� �������������:
 * <ol>
 * <li>��������������� ������. ���� ������ �� ��� ��������������, �� ��� ����� �������
 * ������������� ��� ��������� ����������� �� ������.</li>
 * <B>��������! ��� �������������� ������� ����������� ������ ����������.</B>
 * <li>������������� ������</li>
 * <B>���� ����� ����������� ������� �� ������ ���������� ���� MS SQL, ��� �����
 * ������ ��� ����������</B>
 * <li>��������� ������ ��� ������� ����������. ��� ���� ���������� ����� �����</li>
 * </ol>
 * ������:
 * <code><pre>
 * DBTool db = new DBTool();
 * db.acquireLock("test-lock");
 * System.out.println("Locked!");
 * db.close();
 * </pre></code>
 *
 * @author Vasily Varabyou
 */
public abstract class AppLock {
    public static final Logger logger = LoggerFactory.getLogger(AppLock.class);

    private DBTool dbTool;
    private String lockName;
    protected Connection conn;

    private static Map<String, AppLock> locks = new ConcurrentHashMap<String, AppLock>();


    protected AppLock(DBTool dbTool, String lockName) throws SQLException {
        this.dbTool = dbTool;
        this.lockName = lockName;
    }

    public String getLockName() {
        return lockName;
    }

    /**
     * ��������� ������� ������� ������� � ������������ ������.
     */
    public abstract void lock();

    public void close() {
        try {
            if (conn.isClosed()) throw new RuntimeException("Connection is closed");
            conn.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void lockAndClose(){
        try {
            lock();
        } finally {
            if (null != conn) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    logger.error("Can't close connection", e);
                }
            }
        }
    }

    protected void checkNewConnection() throws SQLException {
        conn = dbTool.getDataSource().getConnection();
        if (conn.getAutoCommit()) {
            conn.setAutoCommit(false);
        }
    }

    /**
     * ����������� ������ ��� ��������� ����������.
     * <p/>
     * ��� ������� ��������� ������ ����������, ������� ����������� � ���������
     * ���� ��� ��������.
     *
     * @param dbTool
     * @param lockName  ��� �������
     * @param dbType    ��� ���� ������
     * @param dbVersion ������ ���� ������
     * @return ������ ��� ������������� ���������� �� ������ ������
     * @throws SQLException
     */
    public static synchronized AppLock defineLock(DBTool dbTool, String lockName, DBTool.DBType dbType,
                                                  int dbVersion) throws SQLException {
        AppLock lock = locks.get(lockName);
        if (null == lock) {
            if ((dbType.equals(DBTool.DBType.MSSQL)) && (dbVersion > 8)) {
                lock = new MSSQLAppLock(dbTool, lockName);
            } else if ((dbType.equals(DBTool.DBType.ORACLE)) && (dbVersion > 8)) {
                lock = new OracleAppLock(dbTool, lockName);
            } else {
                lock = new DefaultAppLock(dbTool, lockName);
            }
            locks.put(lockName, lock);
        }
        return lock;
    }
}
