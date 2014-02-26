package ru.kwanza.dbtool.orm.impl.lockoperation.db.mssql;

import ru.kwanza.dbtool.orm.impl.EntityManagerImpl;
import ru.kwanza.dbtool.orm.impl.lockoperation.AbstractLockOperation;
import ru.kwanza.dbtool.orm.impl.querybuilder.EntityInfo;

/**
 * @author Alexander Guzanov
 */
public class MSSQLSkipLockOperation<T> extends AbstractLockOperation<T> {
    public MSSQLSkipLockOperation(Class<T> entityClass, EntityManagerImpl em) {
        super(entityClass, em);
    }

    @Override
    protected String createSQL() {
        return "SELECT " + entityType.getIdField().getColumn() + " FROM " +
                EntityInfo.getTableName(entityType) + " WITH(UPDLOCK,READPAST) WHERE " + entityType.getIdField().getColumn() + "IN (?)";
    }
}