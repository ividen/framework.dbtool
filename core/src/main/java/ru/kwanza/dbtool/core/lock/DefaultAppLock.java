package ru.kwanza.dbtool.core.lock;

import ru.kwanza.dbtool.core.DBTool;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

class DefaultAppLock extends AppLock {

    DefaultAppLock(DBTool dbTool, String lockName) throws SQLException {
        super(dbTool, lockName);
        allocateUnique();
    }

    @Override
    public void lock() {
        PreparedStatement st = null;
        Connection conn = null;
        try {
            conn = checkNewConnection();
            st = conn.prepareStatement("update dbmutex set id = id where id = ?");
            st.setString(1, getLockName());
            if (1 != st.executeUpdate()) {
                throw new IllegalStateException("Lock '" + getLockName() + "' not found");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            dbTool.closeResources(st, conn);
        }
    }

    private void allocateUnique() throws SQLException {
        PreparedStatement st = null;
        ResultSet rs = null;
        Connection conn = null;
        try {
            conn = checkNewConnection();
            st = conn.prepareStatement("insert into dbmutex(id) values(?)");
            st.setString(1, getLockName());
            try {
                st.executeUpdate();
            } catch (SQLException e) {
                st = conn.prepareStatement("select id from dbmutex where id = ?");
                st.setString(1, getLockName());
                rs = st.executeQuery();
                if (!rs.next()) {
                    throw e;
                }
            }
        } finally {
            dbTool.closeResources(rs, st, conn);
        }
    }
}
