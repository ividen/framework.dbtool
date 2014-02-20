package ru.kwanza.dbtool.orm.impl.querybuilder.db.oracle;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.RowMapper;
import ru.kwanza.dbtool.core.FieldGetter;
import ru.kwanza.dbtool.orm.api.LockResult;
import ru.kwanza.dbtool.orm.api.internal.IEntityType;
import ru.kwanza.dbtool.orm.impl.EntityManagerImpl;
import ru.kwanza.dbtool.orm.impl.LockOperation;
import ru.kwanza.dbtool.orm.impl.querybuilder.EntityInfo;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;

/**
 * @author Alexander Guzanov
 */
public class OracleLockOperation<T> extends LockOperation {
    private final EntityManagerImpl em;
    private final IEntityType<T> entityType;
    private final String sql;

    public OracleLockOperation(EntityManagerImpl em, Class<T> entityClass) {
        this.em = em;
        this.entityType = em.getRegistry().getEntityType(entityClass);
        this.sql = createSQL();
    }

    public LockResult<T> lock(Collection<T> items) {

        Set<Object> lockedIds;
        try {
            lockedIds = em.getDbTool().selectSet(sql, new RowMapper<Object>() {
                public Object mapRow(ResultSet resultSet, int i) throws SQLException {
                    return FieldGetter.getValue(resultSet, entityType.getIdField().getColumn(), entityType.getIdField().getProperty().getType());
                }
            }, items);
        } catch (DataAccessException e) {
            e.printStackTrace();
            return new LockResult<T>(Collections.<T>emptyList(), items);
        }

        Collection<T> locked = new ArrayList<T>();
        Collection<T> unlocked = new ArrayList<T>();

        for (T item : items) {
            if (lockedIds.contains(entityType.getIdField().getProperty().value(item))) {
                locked.add(item);
            } else {
                unlocked.add(item);
            }
        }

        return new LockResult<T>(locked, unlocked);
    }

    protected String createSQL() {
        return "SELECT " + entityType.getIdField().getColumn() + " FROM " +
                EntityInfo.getTableName(entityType) + " WHERE " + entityType.getIdField().getColumn() + "IN (?) UPDATE";
    }

}
