package ru.kwanza.dbtool.orm.impl.lockoperation.db.h2;

import ru.kwanza.dbtool.orm.api.LockResult;
import ru.kwanza.dbtool.orm.impl.EntityManagerImpl;
import ru.kwanza.dbtool.orm.impl.querybuilder.QueryEntityInfo;

import java.util.Collection;

/**
 * @author Alexander Guzanov
 */
public class H2WaitLockOperation<T> extends H2LockOperation<T> {

    public static final int TIMEOUT = 1 * 60 * 1000;

    public H2WaitLockOperation(EntityManagerImpl em, Class<T> entityClass) {
        super(em, entityClass);
    }

    @Override
    public LockResult<T> lock(Collection<T> items) {
        int lockTimeout = getLockTimeout();
        setLockTimeout(TIMEOUT);
        try {
            return super.lock(items);
        } finally {
            setLockTimeout(lockTimeout);
        }
    }

    @Override
    protected String createSQL() {
        return "SELECT " + entityType.getIdField().getColumn() + " FROM " +
                QueryEntityInfo.getTable(entityType) + " WHERE " + entityType.getIdField().getColumn() + " IN (?) FOR UPDATE";
    }
}
