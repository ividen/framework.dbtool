package ru.kwanza.dbtool.orm.impl.lockoperation;

import ru.kwanza.dbtool.core.DBTool;
import ru.kwanza.dbtool.orm.api.LockType;
import ru.kwanza.dbtool.orm.impl.EntityManagerImpl;
import ru.kwanza.dbtool.orm.impl.lockoperation.db.oracle.OracleSkipLockOperation;
import ru.kwanza.dbtool.orm.impl.lockoperation.db.oracle.OracleWaiteLockOperation;
import ru.kwanza.dbtool.orm.impl.lockoperation.db.oracle.OracleNoWaitLockOperation;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author Alexander Guzanov
 */
public class LockFactory {

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
                if (type == LockType.PESSIMISTIC_WAIT) {
                    result = new OracleWaiteLockOperation<T>(em, entityClass);
                } else if (type == LockType.PESSIMISTIC_NOWAIT) {
                    result = new OracleNoWaitLockOperation(em, entityClass);
                } else if (type == LockType.PESSIMISTIC_SKIP_LOCKED) {
                    result = new OracleSkipLockOperation(em, entityClass);
                }
            } else {
                throw new UnsupportedOperationException("Lock operation is not supported for database " + em.getDbTool().getDbType());
            }

            cache.putIfAbsent(key, result);
        }

        return result;
    }
}
