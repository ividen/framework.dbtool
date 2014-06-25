package ru.kwanza.dbtool.orm.impl.lockoperation.db.mssql;

import ru.kwanza.dbtool.orm.impl.EntityManagerImpl;
import ru.kwanza.dbtool.orm.impl.lockoperation.AbstractLockOperation;
import ru.kwanza.dbtool.orm.impl.querybuilder.QueryEntityInfo;

/**
 * @author Alexander Guzanov
 */
public class MSSQLSkipLockOperation<T> extends AbstractLockOperation<T> {
    public MSSQLSkipLockOperation(EntityManagerImpl em, Class<T> entityClass) {
        super(em, entityClass);
    }

    @Override
    protected String createSQL() {
        return "SELECT " + entityType.getIdField().getColumn() + " FROM " +
                QueryEntityInfo.getTable(entityType) + " WITH(UPDLOCK,READPAST) WHERE " + entityType.getIdField().getColumn() + " IN (?)";
    }
}