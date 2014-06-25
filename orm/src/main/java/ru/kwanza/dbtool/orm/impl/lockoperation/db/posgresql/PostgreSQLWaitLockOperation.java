package ru.kwanza.dbtool.orm.impl.lockoperation.db.posgresql;

import ru.kwanza.dbtool.orm.impl.EntityManagerImpl;
import ru.kwanza.dbtool.orm.impl.lockoperation.AbstractLockOperation;
import ru.kwanza.dbtool.orm.impl.querybuilder.QueryEntityInfo;

/**
 * @author Alexander Guzanov
 */
public class PostgreSQLWaitLockOperation<T> extends AbstractLockOperation<T> {

    public PostgreSQLWaitLockOperation(EntityManagerImpl em, Class<T> entityClass) {
        super(em, entityClass);
    }

    @Override
    protected String createSQL() {
        return "SELECT " + entityType.getIdField().getColumn() + " FROM " +
                QueryEntityInfo.getTable(entityType) + " WHERE " + entityType.getIdField().getColumn()
                + " IN (?) ORDER BY " + entityType.getIdField().getColumn() + " FOR UPDATE";
    }
}
