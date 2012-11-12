package ru.kwanza.dbtool.core.lock;

import ru.kwanza.dbtool.core.DBTool;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.SQLException;

class MSSQLAppLock extends AppLock {

    MSSQLAppLock(DBTool dbTool, String lockName) throws SQLException {
        super(dbTool, lockName);
    }

    @Override
    public void lock() {
        CallableStatement st = null;
        try {
            checkNewConnection();
            // 1. в JDBC нет явного способа начать транзакцию
            // 2. функция sp_getapplock требует, чтобы ее выполняли в транзакции
            // 3. сама она транзакцию не начинает
            // поэтому такой финт ушами - если транзакции еще нет, начать:
            PreparedStatement st1 = conn.prepareStatement("select 1 from dbmutex where 0=1");
            st1.execute();
            st1.close();
            st = conn.prepareCall("{? = call sp_getapplock(?, ?, ?)}");
            st.registerOutParameter(1, java.sql.Types.INTEGER);
            st.setString(2, getLockName());
            st.setString(3, "Exclusive");
            st.setString(4, "Transaction");
            st.execute();
            int rc = st.getInt(1);
            if ((0 != rc) && (1 != rc)) {
                throw new RuntimeException("Unable to acquire app lock, sp_getapplock returns " + rc + ": " + st.getWarnings());
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
}
