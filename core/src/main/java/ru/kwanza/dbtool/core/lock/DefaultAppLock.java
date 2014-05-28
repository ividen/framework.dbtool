package ru.kwanza.dbtool.core.lock;

import ru.kwanza.dbtool.core.DBTool;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.locks.ReentrantLock;

class DefaultAppLock extends AppLock {

    public static final String DO_LOCK_SQL = "update dbmutex set id = ?, ts=? where id = ?";
    public static final String INSERT_LOCK = "insert into dbmutex(id) values(?)";
    public static final String FIND_LOCK = "select id from dbmutex where id = ?";

    DefaultAppLock(DBTool dbTool, String lockName, ReentrantLock lock, boolean reentrant) throws SQLException {
        super(dbTool, lockName, lock, reentrant);
    }

    @Override
    public void doLock(Connection connection) throws SQLException {
        if (!isLockExists(connection)) {
            allocateUnique(connection);
        }else{
            updateLock(connection);
        }
    }

    private void updateLock(Connection connection) throws SQLException {
        PreparedStatement st = null;
        try {
            st = connection.prepareStatement(DO_LOCK_SQL);
            st.setString(1, getLockName());
            st.setLong(2, System.currentTimeMillis());
            st.setString(3, getLockName());
            if (1 != st.executeUpdate()) {
                throw new IllegalStateException("Lock '" + getLockName() + "' not found");
            }
        } finally {
            dbTool.closeResources(st);
        }
    }

    @Override
    protected void doUnLock(Connection connection) {
    }

    private void allocateUnique(Connection connection) throws SQLException {
        PreparedStatement st = null;
        ResultSet rs = null;
        try {
            st = connection.prepareStatement(INSERT_LOCK);
            st.setString(1, getLockName());
            try {
                st.executeUpdate();
            } catch (SQLException e) {
              throw e;
            }
        } finally {
            dbTool.closeResources(rs, st);
        }
    }


    private boolean isLockExists(Connection connection) throws SQLException {
        PreparedStatement st = null;
        ResultSet rs = null;
        try {
            st = connection.prepareStatement(FIND_LOCK);
            st.setString(1, getLockName());

            rs = st.executeQuery();
            if (!rs.next()) {
                return false;
            }

        } finally {
            dbTool.closeResources(rs, st);
        }

        return true;
    }
}
