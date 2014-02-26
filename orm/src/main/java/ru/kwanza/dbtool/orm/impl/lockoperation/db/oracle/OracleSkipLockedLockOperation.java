package ru.kwanza.dbtool.orm.impl.lockoperation.db.oracle;

import ru.kwanza.dbtool.orm.impl.EntityManagerImpl;

/**
 * @author Alexander Guzanov
 */
public class OracleSkipLockedLockOperation<T> extends OracleLockOperation<T> {
    public OracleSkipLockedLockOperation(EntityManagerImpl em, Class<T> entityClass) {
        super(em, entityClass);
    }

    @Override
    protected String createSQL() {
        return super.createSQL() + " WITH SKIPED LOCKED";
    }
}
