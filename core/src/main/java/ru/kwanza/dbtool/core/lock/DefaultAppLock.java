package ru.kwanza.dbtool.core.lock;

import ru.kwanza.dbtool.core.DBTool;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

class DefaultAppLock extends AppLock {

    public static final String DO_LOCK_SQL = "update dbmutex set id = id where id = ?";
    public static final String INSERT_LOCK = "insert into dbmutex(id) values(?)";
    public static final String UPDATE_LOCK = "select id from dbmutex where id = ?";

    DefaultAppLock(DBTool dbTool, String lockName, boolean reentrant) throws SQLException {
        super(dbTool, lockName, reentrant);
    }

    @Override
    public void doLock(Connection connection) throws SQLException {
        allocateUnique(connection);
        PreparedStatement st = null;
        try {
            st = connection.prepareStatement(DO_LOCK_SQL);
            st.setString(1, getLockName());
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
                st.close();
                st = connection.prepareStatement(UPDATE_LOCK);
                st.setString(1, getLockName());

                rs = st.executeQuery();
                if (!rs.next()) {
                    throw e;
                }
            }
        } finally {
            dbTool.closeResources(rs, st);
        }
    }
}
