package ru.kwanza.dbtool.orm.impl.lockoperation.db.oracle;

import ru.kwanza.dbtool.orm.impl.EntityManagerImpl;
import ru.kwanza.dbtool.orm.impl.lockoperation.AbstractLockOperation;
import ru.kwanza.dbtool.orm.impl.querybuilder.EntityInfo;

/**
 * @author Alexander Guzanov
 */
public class OracleWaitLockOperation<T> extends AbstractLockOperation<T> {

    public OracleWaitLockOperation(EntityManagerImpl em, Class<T> entityClass) {
        super(em, entityClass);
    }

    @Override
    protected String createSQL() {
        return "SELECT " + entityType.getIdField().getColumn() + " FROM " +
                EntityInfo.getTable(entityType) + " WHERE " + entityType.getIdField().getColumn()
                + " IN (?) ORDER BY " + entityType.getIdField().getColumn() + " FOR UPDATE";
    }
}
