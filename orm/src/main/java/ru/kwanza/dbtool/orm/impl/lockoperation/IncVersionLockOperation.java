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

import org.springframework.jdbc.core.RowMapper;
import ru.kwanza.dbtool.core.*;
import ru.kwanza.dbtool.core.util.FieldValueExtractor;
import ru.kwanza.dbtool.core.util.UpdateUtil;
import ru.kwanza.dbtool.orm.api.LockResult;
import ru.kwanza.dbtool.orm.api.internal.IEntityType;
import ru.kwanza.dbtool.orm.impl.EntityManagerImpl;
import ru.kwanza.toolbox.fieldhelper.FieldHelper;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 * @author Alexander Guzanov
 */
public class IncVersionLockOperation<T> implements ILockOperation<T> {
    private final EntityManagerImpl em;
    private final IEntityType<T> entityType;
    private final String updateQuery;
    private final String checkQuery;
    private final KeyVersionRowMapper keyVersionRowMapper = new KeyVersionRowMapper();
    private final UpdateOperationSetter updateOperationSetter = new UpdateOperationSetter();
    private final VersionGenerator versionGenerator;
    private VersionField versionField;

    public IncVersionLockOperation(EntityManagerImpl em, VersionGenerator versionGenerator, Class<T> entityClass) {
        this.em = em;
        this.entityType = em.getRegistry().getEntityType(entityClass);
        this.updateQuery = buildUpdateQuery();
        this.checkQuery = buildCheckQuery();
        this.versionGenerator = versionGenerator;
        this.versionField = new VersionField();
    }

    public LockResult<T> lock(Collection<T> items) {
        try {
            UpdateUtil
                    .batchUpdate(em.getDbTool().getJdbcTemplate(), updateQuery, items,
                            updateOperationSetter, checkQuery, keyVersionRowMapper, entityType.getIdField().getProperty(),
                            versionField, em.getDbTool().getDbType());
        } catch (UpdateException e) {
            ArrayList<T> unlocked = new ArrayList<T>(e.getConstrainted().size() + e.getOptimistic().size());

            unlocked.addAll(e.<T>getConstrainted());
            unlocked.addAll(e.<T>getOptimistic());

            return new LockResult<T>(e.<T>getUpdated(), unlocked);
        }

        return new LockResult<T>(items, Collections.<T>emptyList());
    }

    private String buildUpdateQuery() {
        return "update " + entityType.getTableName() + " set " + entityType.getVersionField().getColumn() +
                "=? " + "where " + entityType.getIdField().getColumn() + "=?" + " and " +
                entityType.getVersionField().getColumn() + "=?";
    }

    private String buildCheckQuery() {
        return "select " + entityType.getIdField().getColumn() + "," +
                entityType.getVersionField().getColumn() + " from " + entityType.getTableName() +
                " where " + entityType.getIdField().getColumn() + " in (?)";
    }


    private class KeyVersionRowMapper implements RowMapper<KeyValue<Comparable, Long>> {
        public KeyValue<Comparable, Long> mapRow(ResultSet rs, int rowNum) throws SQLException {
            final Object key = FieldValueExtractor.getValue(rs, entityType.getIdField().getColumn(),
                    entityType.getIdField().getProperty().getType());
            final Long version = (Long) FieldValueExtractor.getValue(rs, entityType.getVersionField().getColumn(),
                    entityType.getVersionField().getProperty().getType());
            return new KeyValue<Comparable, Long>((Comparable) key, version);
        }
    }


    private class UpdateOperationSetter implements UpdateSetterWithVersion<T, Long> {
        public boolean setValues(PreparedStatement pst, T object, Long newVersion, Long oldVersion) throws SQLException {
            try {
                FieldSetter.setLong(pst, 1, newVersion);
                FieldSetter.setValue(pst, 2, entityType.getIdField().getProperty().getType(),
                        entityType.getIdField().getProperty().value(object));
                FieldSetter.setLong(pst, 3, oldVersion);
            } catch (SQLException e) {
                throw e;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            return true;
        }
    }

    private class VersionField implements FieldHelper.VersionField<T, Long> {
        public Long value(Object object) {
            return (Long) entityType.getVersionField().getProperty().value(object);
        }

        public Long generateNewValue(Object object) {
            return versionGenerator.generate(entityType.getEntityClass().getName(),
                    (Long) entityType.getVersionField().getProperty().value(object));
        }

        public void setValue(Object object, Long value) {
            entityType.getVersionField().getProperty().set(object, value);
        }
    }

}
