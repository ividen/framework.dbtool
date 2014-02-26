package ru.kwanza.dbtool.orm.impl.lockoperation.db.oracle;

import ru.kwanza.dbtool.orm.impl.EntityManagerImpl;
import ru.kwanza.dbtool.orm.impl.lockoperation.AbstractLockOperation;
import ru.kwanza.dbtool.orm.impl.querybuilder.EntityInfo;

/**
 * @author Alexander Guzanov
 */
public class OracleWaiteLockOperation<T> extends AbstractLockOperation<T> {

    public OracleWaiteLockOperation(EntityManagerImpl em, Class<T> entityClass) {
        super(entityClass, em);
    }

    @Override
    protected String createSQL() {
        return "SELECT " + entityType.getIdField().getColumn() + " FROM " +
                EntityInfo.getTableName(entityType) + " WHERE " + entityType.getIdField().getColumn() + "IN (?) UPDATE";
    }
}
