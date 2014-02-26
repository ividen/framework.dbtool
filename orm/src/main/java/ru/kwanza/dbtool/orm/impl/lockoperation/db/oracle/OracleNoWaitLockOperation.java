package ru.kwanza.dbtool.orm.impl.lockoperation.db.oracle;

import ru.kwanza.dbtool.orm.impl.EntityManagerImpl;

/**
 * @author Alexander Guzanov
 */
public class OracleNoWaitLockOperation<T> extends OracleWaiteLockOperation<T> {
    public OracleNoWaitLockOperation(EntityManagerImpl em, Class entityClass) {
        super(em, entityClass);
    }

    @Override
    protected String createSQL() {
        return super.createSQL() + " WITH NOWAIT";
    }
}
