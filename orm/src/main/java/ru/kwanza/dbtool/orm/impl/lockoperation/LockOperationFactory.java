package ru.kwanza.dbtool.orm.impl.lockoperation;

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

import ru.kwanza.dbtool.core.DBTool;
import ru.kwanza.dbtool.core.VersionGenerator;
import ru.kwanza.dbtool.orm.api.LockType;
import ru.kwanza.dbtool.orm.impl.EntityManagerImpl;
import ru.kwanza.dbtool.orm.impl.lockoperation.db.h2.H2NoWaitLockOperation;
import ru.kwanza.dbtool.orm.impl.lockoperation.db.h2.H2SkipLockOperation;
import ru.kwanza.dbtool.orm.impl.lockoperation.db.h2.H2WaitLockOperation;
import ru.kwanza.dbtool.orm.impl.lockoperation.db.mssql.MSSQLNoWaitLockOperation;
import ru.kwanza.dbtool.orm.impl.lockoperation.db.mssql.MSSQLSkipLockOperation;
import ru.kwanza.dbtool.orm.impl.lockoperation.db.mssql.MSSQLWaitLockOperation;
import ru.kwanza.dbtool.orm.impl.lockoperation.db.mysql.MySQLNoWaitLockOperation;
import ru.kwanza.dbtool.orm.impl.lockoperation.db.mysql.MySQLSkipLockOperation;
import ru.kwanza.dbtool.orm.impl.lockoperation.db.mysql.MySQLWaitLockOperation;
import ru.kwanza.dbtool.orm.impl.lockoperation.db.oracle.OracleNoWaitLockOperation;
import ru.kwanza.dbtool.orm.impl.lockoperation.db.oracle.OracleSkipLockOperation;
import ru.kwanza.dbtool.orm.impl.lockoperation.db.oracle.OracleWaitLockOperation;
import ru.kwanza.dbtool.orm.impl.lockoperation.db.posgresql.PostgreSQLNoWaitLockOperation;
import ru.kwanza.dbtool.orm.impl.lockoperation.db.posgresql.PostgreSQLSkipLockOperation;
import ru.kwanza.dbtool.orm.impl.lockoperation.db.posgresql.PostgreSQLWaitLockOperation;

import javax.annotation.Resource;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static ru.kwanza.dbtool.core.DBTool.DBType.*;
import static ru.kwanza.dbtool.orm.api.LockType.*;

/**
 * @author Alexander Guzanov
 */
public class LockOperationFactory {

    @Resource(name = "dbtool.IEntityManager")
    private EntityManagerImpl em;
    @Resource(name = "dbtool.DBTool")
    private DBTool dbTool;
    @Resource(name = "dbtool.VersionGenerator")
    private VersionGenerator versionGenerator;

    private ConcurrentMap<EntryKey, ILockOperation> cache = new ConcurrentHashMap<EntryKey, ILockOperation>();

    private static class EntryKey {
        private Class entityClass;
        private LockType type;

        private EntryKey(Class entityClass, LockType type) {
            this.entityClass = entityClass;
            this.type = type;
        }

        public Class getEntityClass() {
            return entityClass;
        }

        public LockType getType() {
            return type;
        }
    }

    public <T> ILockOperation<T> createOperation(LockType type, Class<T> entityClass) {
        EntryKey key = new EntryKey(entityClass, type);
        ILockOperation result = cache.get(key);
        if (result == null) {

            if (type == INC_VERSION) {
                result = new IncVersionLockOperation(em, versionGenerator, entityClass);
            } else if (dbTool.getDbType() == ORACLE) {
                if (type == WAIT) {
                    result = new OracleWaitLockOperation<T>(em, entityClass);
                } else if (type == NOWAIT) {
                    result = new OracleNoWaitLockOperation(em, entityClass);
                } else if (type == SKIP_LOCKED) {
                    result = new OracleSkipLockOperation<T>(em, entityClass);
                }
            } else if (dbTool.getDbType() == MSSQL) {
                if (type == WAIT) {
                    result = new MSSQLWaitLockOperation<T>(em, entityClass);
                } else if (type == NOWAIT) {
                    result = new MSSQLNoWaitLockOperation<T>(em, entityClass);
                } else if (type == SKIP_LOCKED) {
                    result = new MSSQLSkipLockOperation<T>(em, entityClass);
                }
            } else if (dbTool.getDbType() == MYSQL) {
                if (type == WAIT) {
                    result = new MySQLWaitLockOperation<T>(em, entityClass);
                } else if (type == NOWAIT) {
                    result = new MySQLNoWaitLockOperation<T>(em, entityClass);
                } else if (type == SKIP_LOCKED) {
                    result = new MySQLSkipLockOperation<T>(em, entityClass);
                }
            } else if (dbTool.getDbType() == POSTGRESQL) {
                if (type == WAIT) {
                    result = new PostgreSQLWaitLockOperation<T>(em, entityClass);
                } else if (type == NOWAIT) {
                    result = new PostgreSQLNoWaitLockOperation<T>(em, entityClass);
                } else if (type == SKIP_LOCKED) {
                    result = new PostgreSQLSkipLockOperation<T>(em, entityClass);
                }
            } else if (dbTool.getDbType() == H2) {
                if (type == WAIT) {
                    result = new H2WaitLockOperation<T>(em, entityClass);
                } else if (type == NOWAIT) {
                    result = new H2NoWaitLockOperation<T>(em, entityClass);
                } else if (type == SKIP_LOCKED) {
                    result = new H2SkipLockOperation<T>(em, entityClass);
                }
            } else {
                throw new UnsupportedOperationException("Lock operation is not supported for database " + dbTool.getDbType());
            }

            cache.putIfAbsent(key, result);
        }

        return result;
    }
}
