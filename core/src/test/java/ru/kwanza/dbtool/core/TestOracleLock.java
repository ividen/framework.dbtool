package ru.kwanza.dbtool.core;

import java.sql.CallableStatement;
import java.sql.SQLException;

/**
 * @author Ivan Baluk
 */
public class TestOracleLock extends AbstractTestLock {
    @Override
    protected String getContextFileName() {
        return "oracle_config_select_util.xml";
    }

    @Override
    protected void lock(String name) throws SQLException {
        CallableStatement st = null;
        try {
            st = conn.prepareCall("{? = call dbms_lock.request(?, 6, 32767, TRUE)}");
            st.registerOutParameter(1, java.sql.Types.INTEGER);
            st.setString(2, allocateUnique(name));
            st.execute();
            int rc = st.getInt(1);
            if (rc != 0) {
                throw new RuntimeException("Unable to acquire app lock, dbms_lock.request() returns " + rc);
            }
        } finally {
            if (st != null) {
                st.close();
            }
        }
    }

    private String allocateUnique(String name) throws SQLException {
        CallableStatement st = null;
        try {
            st = conn.prepareCall("{call dbms_lock.allocate_unique(?, ?)}");
            st.setString(1, name);
            st.registerOutParameter(2, java.sql.Types.VARCHAR);
            st.execute();
            return st.getString(2);
        } finally {
            if (null != st) {
                st.close();
            }
        }
    }

    public void testDefineException() throws Exception {
        String lockName = "";
        for (int i = 0; i < 150; i++) {
            lockName = lockName + 'A';
        }
        boolean throwCheck = false;
        try {
            dbTool.getLock(lockName);
        } catch (RuntimeException e) {
            throwCheck = true;
            assertTrue(e.getMessage().contains("ORA-12899"));
        }
        assertTrue(throwCheck);
    }
}
