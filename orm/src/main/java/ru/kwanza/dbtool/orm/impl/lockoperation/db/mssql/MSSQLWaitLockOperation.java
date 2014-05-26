package ru.kwanza.dbtool.orm.impl.lockoperation.db.mssql;

import ru.kwanza.dbtool.orm.impl.EntityManagerImpl;
import ru.kwanza.dbtool.orm.impl.lockoperation.AbstractLockOperation;
import ru.kwanza.dbtool.orm.impl.querybuilder.EntityInfo;

/**
 * @author Alexander Guzanov
 */
public class MSSQLWaitLockOperation<T> extends AbstractLockOperation<T> {
    public MSSQLWaitLockOperation(EntityManagerImpl em, Class<T> entityClass) {
        super(em, entityClass);
    }

    @Override
    protected String createSQL() {
        return "SELECT " + entityType.getIdField().getColumn() + " FROM " +
                EntityInfo.getTable(entityType) + " WITH(UPDLOCK,ROWLOCK) WHERE " + entityType.getIdField().getColumn() + " IN (?)";
    }
}
