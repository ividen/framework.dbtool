package ru.kwanza.dbtool.orm.impl.lockoperation.db.posgresql;

import ru.kwanza.dbtool.orm.impl.EntityManagerImpl;
import ru.kwanza.dbtool.orm.impl.lockoperation.AbstractLockOperation;
import ru.kwanza.dbtool.orm.impl.querybuilder.EntityInfo;

/**
 * @author Alexander Guzanov
 */
public class PostgreSQLSkipLockOperation<T> extends AbstractLockOperation<T> {
    public PostgreSQLSkipLockOperation(EntityManagerImpl em, Class<T> entityClass) {
        super(em, entityClass);
    }

    @Override
    protected String createSQL() {
        return "SELECT " + entityType.getIdField().getColumn() + " FROM " +
                EntityInfo.getTableName(entityType) + " WHERE " + entityType.getIdField().getColumn()
                + " IN (?) and pg_try_advisory_xact_lock(" + entityType.getIdField().getColumn() + ") ORDER BY " + entityType.getIdField().getColumn() + " FOR UPDATE";
    }
}
