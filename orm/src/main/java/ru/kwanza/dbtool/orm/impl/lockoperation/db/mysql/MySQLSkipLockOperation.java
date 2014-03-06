package ru.kwanza.dbtool.orm.impl.lockoperation.db.mysql;

import ru.kwanza.dbtool.orm.api.LockResult;
import ru.kwanza.dbtool.orm.impl.EntityManagerImpl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 * @author Alexander Guzanov
 */
public class MySQLSkipLockOperation<T> extends MySQLLockOperation<T> {
    public MySQLSkipLockOperation(EntityManagerImpl em, Class<T> entityClass) {
        super(em, entityClass);
    }

    @Override
    public LockResult<T> lock(Collection<T> items) {
        final int lockTimeout = getLockTimeout();
        final Collection<T> locked = new ArrayList<T>();
        final Collection<T> unlocked = new ArrayList<T>();

        try {
            setLockTimeout(1);
            for (T i : items) {
                LockResult<T> result = super.lock(Collections.singletonList(i));
                locked.addAll(result.getLocked());
                unlocked.addAll(result.getUnlocked());
            }
        } finally {
            setLockTimeout(lockTimeout);
        }

        return new LockResult<T>(locked, unlocked);
    }
}

