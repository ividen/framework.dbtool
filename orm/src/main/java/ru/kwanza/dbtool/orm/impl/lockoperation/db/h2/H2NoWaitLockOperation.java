package ru.kwanza.dbtool.orm.impl.lockoperation.db.h2;

import ru.kwanza.dbtool.orm.api.LockResult;
import ru.kwanza.dbtool.orm.impl.EntityManagerImpl;
import ru.kwanza.dbtool.orm.impl.lockoperation.db.mysql.MySQLWaitLockOperation;

import java.util.Collection;

/**
 * @author Alexander Guzanov
 */
public class H2NoWaitLockOperation<T> extends H2LockOperation<T> {
    public H2NoWaitLockOperation(EntityManagerImpl em, Class<T> entityClass) {
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
