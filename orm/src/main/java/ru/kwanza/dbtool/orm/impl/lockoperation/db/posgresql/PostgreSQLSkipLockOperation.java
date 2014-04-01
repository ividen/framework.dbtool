package ru.kwanza.dbtool.orm.impl.lockoperation.db.posgresql;

import ru.kwanza.dbtool.orm.impl.EntityManagerImpl;
import ru.kwanza.dbtool.orm.impl.lockoperation.AbstractLockOperation;
import ru.kwanza.dbtool.orm.impl.querybuilder.EntityInfo;
import ru.kwanza.toolbox.fieldhelper.FieldHelper;

import java.util.Collection;

/**
 * @author Alexander Guzanov
 */
public class PostgreSQLSkipLockOperation<T> extends AbstractLockOperation<T> {
    private int tableId;

    public PostgreSQLSkipLockOperation(EntityManagerImpl em, Class<T> entityClass) {
        super(em, entityClass);
        tableId = EntityInfo.getTableName(entityType).hashCode();
    }

    @Override
    protected String createSQL() {
        return "SELECT " + entityType.getIdField().getColumn() + " FROM " +
                EntityInfo.getTableName(entityType) + " WHERE " + entityType.getIdField().getColumn()
                + " IN (?) and pg_try_advisory_xact_lock(CAST(((CAST(? AS numeric)*31 + CAST("
                + entityType.getIdField().getColumn() + " AS numeric))%9223372036854775807) as bigint)) ORDER BY "
                + entityType.getIdField().getColumn();
    }

    @Override
    protected Object[] getParams(Collection<T> items) {
        return new Object[]{FieldHelper.getFieldCollection(items, entityType.getIdField().getProperty()),tableId};
    }
}
