package ru.kwanza.dbtool.core.lock;

import ru.kwanza.dbtool.core.DBTool;

import java.sql.CallableStatement;
import java.sql.SQLException;

class OracleAppLock extends AppLock {

    private String lockHandle;

    OracleAppLock(DBTool dbTool, String lockName) throws SQLException {
        super(dbTool, lockName);
    }

    @Override
    public void doLock() {
        CallableStatement st = null;
        try {
            lockHandle = allocateUnique();
            st = conn.prepareCall(
                    // lockhandle, x_mode, timeut sec, release on commit
                    "{? = call dbms_lock.request(?, 6, 32767, TRUE)}");
            st.registerOutParameter(1, java.sql.Types.INTEGER);
            st.setString(2, lockHandle);
            st.execute();
            int rc = st.getInt(1);
            if (rc != 0) {
                throw new RuntimeException("Unable to acquire app lock, dbms_lock.request() returns " + rc + ": " + st.getWarnings());
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            if (null != st) {
                try {
                    st.close();
                } catch (SQLException e) {
                    logger.error("Can't close PreparedStatement", e);
                }
            }
        }
    }

    private String allocateUnique() throws SQLException {
        String lockHandle = null;
        CallableStatement st = null;
        try {
            st = conn.prepareCall("{call dbms_lock.allocate_unique(?, ?)}");
            st.setString(1, getLockName());
            st.registerOutParameter(2, java.sql.Types.VARCHAR);
            st.execute();
            lockHandle = st.getString(2);
        } finally {
            if (null != st) {
                st.close();
            }
            conn.close();
        }
        return lockHandle;
    }
}
