package ru.kwanza.dbtool.orm.impl.lockoperation.db.h2;

import org.h2.engine.Session;
import org.h2.jdbc.JdbcConnection;
import ru.kwanza.dbtool.orm.impl.EntityManagerImpl;
import ru.kwanza.dbtool.orm.impl.lockoperation.AbstractLockOperation;
import ru.kwanza.dbtool.orm.impl.querybuilder.QueryEntityInfo;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author Alexander Guzanov
 */
public class H2LockOperation<T> extends AbstractLockOperation<T> {
    public H2LockOperation(EntityManagerImpl em, Class<T> entityClass) {
        super(em, entityClass);
    }

    @Override
    protected String createSQL() {
        return "SELECT " + entityType.getIdField().getColumn() + " FROM " +
                QueryEntityInfo.getTable(entityType) + " WHERE " + entityType.getIdField().getColumn() + " IN (?) FOR UPDATE";
    }

    protected void setLockTimeout(int timeout) {
        em.getDbTool().getJdbcTemplate().execute("SET LOCK_TIMEOUT " + timeout);
    }

    protected int getLockTimeout() {
        Connection connection = em.getDbTool().getJDBCConnection();
        try {
            JdbcConnection c = null;
            c = connection.unwrap(JdbcConnection.class);
            return ((Session) c.getSession()).getLockTimeout();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

}
