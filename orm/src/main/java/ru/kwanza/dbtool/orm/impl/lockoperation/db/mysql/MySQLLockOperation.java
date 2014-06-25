package ru.kwanza.dbtool.orm.impl.lockoperation.db.mysql;

import ru.kwanza.dbtool.orm.impl.EntityManagerImpl;
import ru.kwanza.dbtool.orm.impl.lockoperation.AbstractLockOperation;
import ru.kwanza.dbtool.orm.impl.querybuilder.QueryEntityInfo;

/**
 * @author Alexander Guzanov
 */
public class MySQLLockOperation<T> extends AbstractLockOperation<T> {
    public MySQLLockOperation(EntityManagerImpl em, Class<T> entityClass) {
        super(em, entityClass);
    }

    @Override
    protected String createSQL() {
        return "SELECT " + entityType.getIdField().getColumn() + " FROM " +
                QueryEntityInfo.getTable(entityType) + " WHERE " + entityType.getIdField().getColumn() + " IN (?) FOR UPDATE";
    }

    protected void setLockTimeout(int timeout) {
        em.getDbTool().getJdbcTemplate().execute("set innodb_lock_wait_timeout=" + timeout);
    }

    protected int getLockTimeout() {
        return em.getDbTool().getJdbcTemplate().queryForInt("select @@innodb_lock_wait_timeout");
    }

}
