package ru.kwanza.dbtool.orm.impl.lockoperation;

/*
 * #%L
 * dbtool-orm
 * %%
 * Copyright (C) 2015 Kwanza
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

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
            }, getParams(items));
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

    protected Object[] getParams(Collection<T> items) {
        return new Object[]{FieldHelper.getFieldCollection(items, entityType.getIdField().getProperty())};
    }

    protected abstract String createSQL();
}
