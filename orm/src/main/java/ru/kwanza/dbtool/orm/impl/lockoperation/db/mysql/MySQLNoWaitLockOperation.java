package ru.kwanza.dbtool.orm.impl.lockoperation.db.mysql;

import ru.kwanza.dbtool.orm.api.LockResult;
import ru.kwanza.dbtool.orm.impl.EntityManagerImpl;

import java.util.Collection;

/**
 * @author Alexander Guzanov
 */
public class MySQLNoWaitLockOperation<T> extends MySQLWaitLockOperation<T> {
    public MySQLNoWaitLockOperation(EntityManagerImpl em, Class<T> entityClass) {
        super(em, entityClass);
    }

    @Override
    public LockResult<T> lock(Collection<T> items) {
        int lockTimeout = getLockTimeout();
        setLockTimeout(1);
        try {
            return super.lock(items);
        } finally {
            setLockTimeout(lockTimeout);
        }
    }


}
