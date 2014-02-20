package ru.kwanza.dbtool.orm.impl;

import ru.kwanza.dbtool.core.DBTool;
import ru.kwanza.dbtool.orm.api.LockType;
import ru.kwanza.dbtool.orm.impl.querybuilder.db.oracle.OracleLockOperation;
import ru.kwanza.dbtool.orm.impl.querybuilder.db.oracle.OracleNoWaitLockOperation;
import ru.kwanza.dbtool.orm.impl.querybuilder.db.oracle.OracleSkipLockedLockOperation;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author Alexander Guzanov
 */
public class LockFactory {

    private ConcurrentMap<EntryKey, LockOperation> cache = new ConcurrentHashMap<EntryKey, LockOperation>();

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

    public <T> LockOperation<T> createOperation(EntityManagerImpl em, LockType type, Class<T> entityClass) {
        EntryKey key = new EntryKey(entityClass, type);
        LockOperation result = cache.get(key);
        if (result == null) {
            if (em.getDbTool().getDbType() == DBTool.DBType.ORACLE) {
                if (type == LockType.PESSIMISTIC_WAIT) {
                    result = new OracleLockOperation<T>(em, entityClass);
                } else if (type == LockType.PESSIMISTIC_NOWAIT) {
                    result = new OracleNoWaitLockOperation(em, entityClass);
                } else if (type == LockType.PESSIMISTIC_SKIP_LOCKED) {
                    result = new OracleSkipLockedLockOperation(em, entityClass);
                }
            } else {
                throw new UnsupportedOperationException("Lock operation is not supported for database " + em.getDbTool().getDbType());
            }

            cache.putIfAbsent(key, result);
        }

        return result;
    }
}
