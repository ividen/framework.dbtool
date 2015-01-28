package ru.kwanza.dbtool.orm.impl.lockoperation.db.h2;

/*
 * #%L
 * dbtool-orm
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

import org.h2.engine.Session;
import org.h2.jdbc.JdbcConnection;
import ru.kwanza.dbtool.orm.impl.EntityManagerImpl;
import ru.kwanza.dbtool.orm.impl.lockoperation.AbstractLockOperation;
import ru.kwanza.dbtool.orm.impl.querybuilder.QueryMapping;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author Alexander Guzanov
 */
public class H2LockOperation<T> extends AbstractLockOperation<T> {
    public H2LockOperation(EntityManagerImpl em, Class<T> entityClass) {
        super(em, entityClass);
    }

    @Override
    protected String createSQL() {
        return "SELECT " + entityType.getIdField().getColumn() + " FROM " +
                QueryMapping.getTable(entityType) + " WHERE " + entityType.getIdField().getColumn() + " IN (?) FOR UPDATE";
    }

    protected void setLockTimeout(int timeout) {
        em.getDbTool().getJdbcTemplate().execute("SET LOCK_TIMEOUT " + timeout);
    }

    protected int getLockTimeout() {
        Connection connection = em.getDbTool().getJDBCConnection();
        try {
            JdbcConnection c = null;
            c = connection.unwrap(JdbcConnection.class);
            return ((Session) c.getSession()).getLockTimeout();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

}
