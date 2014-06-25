package ru.kwanza.dbtool.orm.impl.lockoperation.db.posgresql;

import ru.kwanza.dbtool.orm.impl.EntityManagerImpl;
import ru.kwanza.dbtool.orm.impl.lockoperation.AbstractLockOperation;
import ru.kwanza.dbtool.orm.impl.querybuilder.QueryEntityInfo;

/**
 * @author Alexander Guzanov
 */
public class PostgreSQLNoWaitLockOperation<T> extends AbstractLockOperation<T> {
    public PostgreSQLNoWaitLockOperation(EntityManagerImpl em, Class<T> entityClass) {
        super(em, entityClass);
    }

    protected String createSQL() {
        return "SELECT " + entityType.getIdField().getColumn() + " FROM " +
                QueryEntityInfo.getTable(entityType) + " WHERE " + entityType.getIdField().getColumn()
                + " IN (?) ORDER BY " + entityType.getIdField().getColumn() + " FOR UPDATE NOWAIT";
    }
}
