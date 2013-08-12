package ru.kwanza.dbtool.core.lock;

import ru.kwanza.dbtool.core.DBTool;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

class MSSQLAppLock extends AppLock {

    MSSQLAppLock(DBTool dbTool, String lockName, boolean reentrant) throws SQLException {
        super(dbTool, lockName, reentrant);
    }

    @Override
    public void doLock(Connection connection) throws SQLException {
        CallableStatement st = null;
        try {
            // 1. в JDBC нет явного способа начать транзакцию
            // 2. функция sp_getapplock требует, чтобы ее выполняли в транзакции
            // 3. сама она транзакцию не начинает
            // поэтому такой финт ушами - если транзакции еще нет, начать:
            PreparedStatement st1 = connection.prepareStatement("select 1 from dbmutex where 0=1");
            st1.execute();
            st1.close();
            st = connection.prepareCall("{? = call sp_getapplock(?, ?, ?)}");
            st.registerOutParameter(1, java.sql.Types.INTEGER);
            st.setString(2, getLockName());
            st.setString(3, "Exclusive");
            st.setString(4, "Transaction");
            st.execute();
            int rc = st.getInt(1);
            if ((0 != rc) && (1 != rc)) {
                throw new RuntimeException("Unable to acquire app lock, sp_getapplock returns " + rc + ": " + st.getWarnings());
            }
        } finally {
            dbTool.closeResources(st);
        }
    }

    @Override
    protected void doUnLock(Connection connection) {
    }
}
