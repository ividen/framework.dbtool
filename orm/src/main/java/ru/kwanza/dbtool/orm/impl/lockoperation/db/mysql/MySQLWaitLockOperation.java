package ru.kwanza.dbtool.orm.impl.lockoperation.db.mysql;

import ru.kwanza.dbtool.orm.impl.EntityManagerImpl;
import ru.kwanza.dbtool.orm.impl.querybuilder.QueryMapping;

/**
 * @author Alexander Guzanov
 */
public class MySQLWaitLockOperation<T> extends MySQLLockOperation<T> {
    public MySQLWaitLockOperation(EntityManagerImpl em, Class<T> entityClass) {
        super(em, entityClass);
    }

    @Override
    protected String createSQL() {
        return "SELECT " + entityType.getIdField().getColumn() + " FROM " +
                QueryMapping.getTable(entityType) + " WHERE " + entityType.getIdField().getColumn() + " IN (?) FOR UPDATE";
    }
}
