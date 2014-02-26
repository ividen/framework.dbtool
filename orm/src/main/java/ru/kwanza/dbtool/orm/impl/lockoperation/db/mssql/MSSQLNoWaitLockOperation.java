package ru.kwanza.dbtool.orm.impl.lockoperation.db.mssql;

import ru.kwanza.dbtool.orm.impl.EntityManagerImpl;
import ru.kwanza.dbtool.orm.impl.lockoperation.AbstractLockOperation;
import ru.kwanza.dbtool.orm.impl.querybuilder.EntityInfo;

/**
 * @author Alexander Guzanov
 */
public class MSSQLNoWaitLockOperation<T> extends AbstractLockOperation<T> {
    public MSSQLNoWaitLockOperation(Class<T> entityClass, EntityManagerImpl em) {
        super(entityClass, em);
    }

    @Override
    protected String createSQL() {
        return "SELECT " + entityType.getIdField().getColumn() + " FROM " +
                EntityInfo.getTableName(entityType) + " WITH(UPDLOCK,NOWAIT) WHERE " + entityType.getIdField().getColumn() + "IN (?)";
    }
}
