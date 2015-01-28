package ru.kwanza.dbtool.core.lock;

/*
 * #%L
 * dbtool-core
 * %%
 * Copyright (C) 2015 Kwanza
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import ru.kwanza.dbtool.core.DBTool;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.concurrent.locks.ReentrantLock;

class MSSQLAppLock extends AppLock {

    MSSQLAppLock(DBTool dbTool, String lockName, ReentrantLock lock, boolean reentrant) throws SQLException {
        super(dbTool, lockName, lock, reentrant);
    }

    @Override
    public void doLock(Connection connection) throws SQLException {
        CallableStatement st = null;
        Connection conn = null;
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
