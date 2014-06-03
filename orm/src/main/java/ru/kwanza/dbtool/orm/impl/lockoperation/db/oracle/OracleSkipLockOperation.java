package ru.kwanza.dbtool.orm.impl.lockoperation.db.oracle;

import ru.kwanza.dbtool.orm.impl.EntityManagerImpl;

/**
 * @author Alexander Guzanov
 */
public class OracleSkipLockOperation<T> extends OracleWaitLockOperation<T> {
    public OracleSkipLockOperation(EntityManagerImpl em, Class<T> entityClass) {
        super(em, entityClass);
    }

    @Override
    protected String createSQL() {
        return super.createSQL() + " SKIP LOCKED";
    }
}
