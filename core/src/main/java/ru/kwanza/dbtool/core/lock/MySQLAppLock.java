package ru.kwanza.dbtool.core.lock;

import ru.kwanza.dbtool.core.DBTool;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Alexander Guzanov
 */
 class MySQLAppLock extends AppLock {
    public static final String LOCK_SQL = "SELECT GET_LOCK(?,?)";
    public static final String RELEASE_LOCK_SQL = "SELECT RELEASE_LOCK(?)";

    private static long MAX_TIMEOUT = 15 * 60 * 1000;

    protected MySQLAppLock(DBTool dbTool, String lockName, boolean reentrant) throws SQLException {
        super(dbTool, lockName,reentrant);
    }

    @Override
    public void doLock(Connection connection) throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            ps = connection.prepareStatement(LOCK_SQL);
            ps.setString(1, getLockName());
            ps.setInt(2, 100);

            long ts = System.currentTimeMillis();
            while (true) {
                if (rs != null) {
                    rs.close();
                }
                rs = ps.executeQuery();
                rs.next();
                int result = rs.getInt(1);
                if (result == 1) {
                    break;
                }

                if (System.currentTimeMillis() - ts > MAX_TIMEOUT) {
                    throw new IllegalStateException("Max timeout period expired!");
                }
            }
        } finally {
            dbTool.closeResources(rs, ps);
        }
    }

    @Override
    protected void doUnLock(Connection connection) {
        PreparedStatement ps = null;
        try {
            ps = connection.prepareStatement(RELEASE_LOCK_SQL);
            ps.setString(1, getLockName());
            ps.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            dbTool.closeResources(ps);
        }
        super.close();
    }
}
