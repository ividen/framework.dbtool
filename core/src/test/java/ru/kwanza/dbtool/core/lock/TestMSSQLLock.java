package ru.kwanza.dbtool.core.lock;

import ru.kwanza.dbtool.core.lock.AbstractTestLock;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * @author Ivan Baluk
 */
public class TestMSSQLLock extends AbstractTestLock {
    @Override
    protected String getContextFileName() {
        return "mssql-config.xml";
    }

    @Override
    protected void lock(String name) throws SQLException {
        CallableStatement st = null;
        try {
            PreparedStatement st1 = conn.prepareStatement("select 1 from dbmutex where 0=1");
            st1.execute();
            st1.close();
            st = conn.prepareCall("{? = call sp_getapplock(?, ?, ?)}");
            st.registerOutParameter(1, java.sql.Types.INTEGER);
            st.setString(2, name);
            st.setString(3, "Exclusive");
            st.setString(4, "Transaction");
            st.execute();
            int rc = st.getInt(1);
            if ((0 != rc) && (1 != rc)) {
                throw new RuntimeException("Unable to acquire app lock, sp_getapplock returns " + rc);
            }
        } finally {
            if (null != st) {
                st.close();
            }
        }
    }
}
