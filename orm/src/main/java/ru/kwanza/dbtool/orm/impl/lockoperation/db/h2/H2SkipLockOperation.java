package ru.kwanza.dbtool.orm.impl.lockoperation.db.h2;

import ru.kwanza.dbtool.orm.api.LockResult;
import ru.kwanza.dbtool.orm.impl.EntityManagerImpl;
import ru.kwanza.dbtool.orm.impl.lockoperation.db.mysql.MySQLLockOperation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author Alexander Guzanov
 */
public class H2SkipLockOperation<T> extends H2LockOperation<T> {
    public H2SkipLockOperation(EntityManagerImpl em, Class<T> entityClass) {
        super(em, entityClass);
    }

    @Override
    public LockResult<T> lock(Collection<T> items) {
        final int lockTimeout = getLockTimeout();
        final Collection<T> locked = new ArrayList<T>();
        final Collection<T> unlocked = new ArrayList<T>();
        setLockTimeout(1);
        try {
            doLock(new ArrayList<T>(items), locked, unlocked);
        } finally {
            setLockTimeout(lockTimeout);
        }

        return new LockResult<T>(locked, unlocked);
    }


    private void doLock(List<T> items, Collection<T> locked, Collection<T> unlocked) {
        LockResult<T> result = super.lock(items);

        if (!result.getUnlocked().isEmpty()) {
            if (result.getUnlocked().size() == 1) {
                unlocked.addAll(result.getUnlocked());
            } else {
                doLock(items.subList(0, items.size() / 2), locked, unlocked);
                doLock(items.subList(items.size() / 2, items.size()), locked, unlocked);
            }
        } else {
            locked.addAll(result.getLocked());
        }
    }
}

