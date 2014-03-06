package ru.kwanza.dbtool.orm.impl.lockoperation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.RowMapper;
import ru.kwanza.dbtool.core.FieldGetter;
import ru.kwanza.dbtool.orm.api.LockResult;
import ru.kwanza.dbtool.orm.api.internal.IEntityType;
import ru.kwanza.dbtool.orm.impl.EntityManagerImpl;
import ru.kwanza.toolbox.fieldhelper.FieldHelper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;

/**
 * @author Alexander Guzanov
 */
public abstract class AbstractLockOperation<T> implements ILockOperation<T> {
    protected final EntityManagerImpl em;
    protected final IEntityType<T> entityType;
    protected final String sql;
    private static Logger logger = LoggerFactory.getLogger(AbstractLockOperation.class);

    public AbstractLockOperation(EntityManagerImpl em, Class<T> entityClass) {
        this.entityType = em.getRegistry().getEntityType(entityClass);
        this.em = em;
        this.sql = createSQL();
    }

    public LockResult<T> lock(Collection<T> items) {

        Set<Object> lockedIds;
        try {
            lockedIds = em.getDbTool().selectSet(sql, new RowMapper<Object>() {
                public Object mapRow(ResultSet resultSet, int i) throws SQLException {
                    return FieldGetter.getValue(resultSet, entityType.getIdField().getColumn(), entityType.getIdField().getProperty().getType());
                }
            }, FieldHelper.getFieldCollection(items, entityType.getIdField().getProperty()));
        } catch (DataAccessException e) {
            logger.warn("Can't set lock: sql=" + sql, e);
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

    protected abstract String createSQL();
}
