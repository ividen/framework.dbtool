package ru.kwanza.dbtool.orm.impl.lockoperation;

import ru.kwanza.dbtool.core.DBTool;
import ru.kwanza.dbtool.orm.api.LockType;
import ru.kwanza.dbtool.orm.impl.EntityManagerImpl;
import ru.kwanza.dbtool.orm.impl.lockoperation.db.mssql.MSSQLNoWaitLockOperation;
import ru.kwanza.dbtool.orm.impl.lockoperation.db.mssql.MSSQLSkipLockOperation;
import ru.kwanza.dbtool.orm.impl.lockoperation.db.mssql.MSSQLWaitLockOperation;
import ru.kwanza.dbtool.orm.impl.lockoperation.db.mysql.MySQLNoWaitLockOperation;
import ru.kwanza.dbtool.orm.impl.lockoperation.db.mysql.MySQLSkipLockOperation;
import ru.kwanza.dbtool.orm.impl.lockoperation.db.mysql.MySQLWaitLockOperation;
import ru.kwanza.dbtool.orm.impl.lockoperation.db.oracle.OracleNoWaitLockOperation;
import ru.kwanza.dbtool.orm.impl.lockoperation.db.oracle.OracleSkipLockOperation;
import ru.kwanza.dbtool.orm.impl.lockoperation.db.oracle.OracleWaiteLockOperation;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author Alexander Guzanov
 */
public class LockOperationFactory {

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

    public <T> ILockOperation<T> createOperation(EntityManagerImpl em, LockType type, Class<T> entityClass) {
        EntryKey key = new EntryKey(entityClass, type);
        ILockOperation result = cache.get(key);
        if (result == null) {
            if (em.getDbTool().getDbType() == DBTool.DBType.ORACLE) {
                if (type == LockType.WAIT) {
                    result = new OracleWaiteLockOperation<T>(em, entityClass);
                } else if (type == LockType.NOWAIT) {
                    result = new OracleNoWaitLockOperation(em, entityClass);
                } else if (type == LockType.SKIP_LOCKED) {
                    result = new OracleSkipLockOperation<T>(em, entityClass);
                }
            } else if (em.getDbTool().getDbType() == DBTool.DBType.MSSQL) {
                if (type == LockType.WAIT) {
                    result = new MSSQLWaitLockOperation<T>(em, entityClass);
                } else if (type == LockType.NOWAIT) {
                    result = new MSSQLNoWaitLockOperation<T>(em, entityClass);
                } else if (type == LockType.SKIP_LOCKED) {
                    result = new MSSQLSkipLockOperation<T>(em, entityClass);
                }
            } else if (em.getDbTool().getDbType() == DBTool.DBType.MYSQL) {
                if (type == LockType.WAIT) {
                    result = new MySQLWaitLockOperation<T>(em, entityClass);
                } else if (type == LockType.NOWAIT) {
                    result = new MySQLNoWaitLockOperation<T>(em, entityClass);
                } else if (type == LockType.SKIP_LOCKED) {
                    result = new MySQLSkipLockOperation<T>(em, entityClass);
                }
            } else {
                throw new UnsupportedOperationException("Lock operation is not supported for database " + em.getDbTool().getDbType());
            }

            cache.putIfAbsent(key, result);
        }

        return result;
    }
}
