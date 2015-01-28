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
        } else {
            updateLock(connection);
        }
    }

    private void updateLock(Connection connection) throws SQLException {
        PreparedStatement st = null;
        Connection conn = null;
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
        Connection conn = null;
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
