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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.kwanza.dbtool.core.DBTool;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Класс поторый позволяет устанавливать блокировку
 */
public abstract class AppLock {
    public static final Logger logger = LoggerFactory.getLogger(AppLock.class);

    protected DBTool dbTool;
    private ReentrantLock lock;
    private String lockName;
    private boolean reentrant;


    protected AppLock(DBTool dbTool, String lockName, ReentrantLock cachedLock, boolean reentrant) throws SQLException {
        this.dbTool = dbTool;
        this.lockName = lockName;
        this.reentrant = reentrant;
        this.lock = cachedLock;
    }

    /**
     * Имя блокировки
     */
    public String getLockName() {
        return lockName;
    }

    /**
     * Установить блокировку
     */
    public final void lock() {
        if (reEnterLock()) {
            return;
        }

        Connection connection = null;
        try {
            connection = checkNewConnection();
            doLock(connection);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            dbTool.closeResources(connection);
        }
    }

    protected abstract void doLock(Connection connection) throws SQLException;

    protected abstract void doUnLock(Connection connection);

    /**
     * Закрыть работу с блокировкой.
     * <p/>
     * Этот метод обязательно нужно вызывать в секции <b>finally</b>,
     * хотя он не снимает блокировки, а только освобождает ресурсы.
     */
    public final void close() {
        if (exitReEnterLock()) {
            return;
        }

        Connection connection = null;
        try {
            connection = checkNewConnection();
            doUnLock(connection);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            dbTool.closeResources(connection);
        }
    }

    private boolean exitReEnterLock() {
        lock.unlock();

        if (reentrant && lock.isHeldByCurrentThread()) {
            return true;
        }
        return false;
    }

    private boolean reEnterLock() {

        try {
            if (reentrant && lock.isHeldByCurrentThread()) {
                return true;
            }
            return false;
        } finally {
            lock.lock();
        }
    }

    private Connection checkNewConnection() throws SQLException {
        Connection connection = dbTool.getJDBCConnection();
        if (connection.getAutoCommit()) {
            connection.setAutoCommit(false);
        }

        return connection;
    }

    public static AppLock defineLock(DBTool dbTool, String lockName, DBTool.DBType dbType, ReentrantLock lock, boolean reentrant) throws SQLException {
        if (dbType.equals(DBTool.DBType.MSSQL)) {
            return new MSSQLAppLock(dbTool, lockName, lock, reentrant);
        } else if (dbType.equals(DBTool.DBType.ORACLE)) {
            return new DefaultAppLock(dbTool, lockName, lock, reentrant);
        } else if (dbType.equals(DBTool.DBType.MYSQL)) {
            return new DefaultAppLock(dbTool, lockName, lock, reentrant);
        } else {
            return new DefaultAppLock(dbTool, lockName, lock, reentrant);
        }

    }
}
