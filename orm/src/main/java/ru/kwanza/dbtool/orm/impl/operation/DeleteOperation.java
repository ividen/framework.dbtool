package ru.kwanza.dbtool.orm.impl.operation;

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
import ru.kwanza.dbtool.core.DBTool;
import ru.kwanza.dbtool.core.FieldSetter;
import ru.kwanza.dbtool.core.UpdateException;
import ru.kwanza.dbtool.core.UpdateSetter;
import ru.kwanza.dbtool.core.util.UpdateUtil;
import ru.kwanza.dbtool.orm.api.internal.IEntityMappingRegistry;
import ru.kwanza.dbtool.orm.api.internal.IFieldMapping;
import ru.kwanza.dbtool.orm.impl.EntityManagerImpl;
import ru.kwanza.toolbox.fieldhelper.Property;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collection;

/**
 * @author Kiryl Karatsetski
 */
public class DeleteOperation extends Operation implements IDeleteOperation {

    private static final Logger log = LoggerFactory.getLogger(UpdateOperation.class);

    private Property idEntityFiled;

    private String deleteQuery;

    private UpdateSetter updateSetterByObject = new UpdateSetterByObject();
    private UpdateSetter updateSetterByKey = new UpdateSetterByKey();

    public DeleteOperation(EntityManagerImpl em, Class entityClass) {
        super(em, entityClass);
    }

    @Override
    protected void initOperation() {
        final IFieldMapping idField = em.getRegistry().getEntityType(entityClass).getIdField();

        if (idField == null ) {
            throw new RuntimeException("IdFieldMapping for entity class" + entityClass + " not found");
        }

        this.idEntityFiled = idField.getProperty();

        final String tableName = em.getRegistry().getEntityType(entityClass).getTableName();
        final String idColumnName = idField.getColumn();

        this.deleteQuery = buildQuery(tableName, idColumnName);

        if (log.isTraceEnabled()) {
            log.trace("Build DeleteOperation query for EntityClass {}: {}", entityClass, deleteQuery);
        }
    }

    private String buildQuery(String tableName, String idColumnName) {
        final StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("DELETE FROM ");
        stringBuilder.append(tableName);
        stringBuilder.append(" WHERE ");
        stringBuilder.append(idColumnName).append(" IN (?)");
        return stringBuilder.toString();
    }

    public void executeDelete(Object object) throws UpdateException {
        executeDelete(Arrays.asList(object));
    }

    @SuppressWarnings("unchecked")
    public void executeDelete(Collection objects) throws UpdateException {
        UpdateUtil.batchUpdate(getJdbcTemplate(), deleteQuery, objects, updateSetterByObject, em.getDbTool().getDbType());
    }

    public void executeDeleteByKey(Object key) throws UpdateException {
        executeDeleteByKeys(Arrays.asList(key));
    }

    @SuppressWarnings("unchecked")
    public void executeDeleteByKeys(Collection keys) throws UpdateException {
        UpdateUtil.batchUpdate(getJdbcTemplate(), deleteQuery, keys, updateSetterByKey, em.getDbTool().getDbType());
    }

    private class UpdateSetterByObject implements UpdateSetter {
        public boolean setValues(PreparedStatement pst, Object object) throws SQLException {
            try {
                FieldSetter.setValue(pst, 1, idEntityFiled.getType(), idEntityFiled.value(object));
            } catch (SQLException e) {
                throw e;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            return true;

        }
    }

    private class UpdateSetterByKey implements UpdateSetter {
        public boolean setValues(PreparedStatement pst, Object object) throws SQLException {
            FieldSetter.setValue(pst, 1, idEntityFiled.getType(), object);
            return true;
        }
    }
}
