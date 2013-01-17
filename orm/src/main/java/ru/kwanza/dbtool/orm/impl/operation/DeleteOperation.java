package ru.kwanza.dbtool.orm.impl.operation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.kwanza.dbtool.core.DBTool;
import ru.kwanza.dbtool.core.FieldSetter;
import ru.kwanza.dbtool.core.UpdateException;
import ru.kwanza.dbtool.core.UpdateSetter;
import ru.kwanza.dbtool.core.util.UpdateUtil;
import ru.kwanza.dbtool.orm.impl.mapping.EntityField;
import ru.kwanza.dbtool.orm.impl.mapping.FieldMapping;
import ru.kwanza.dbtool.orm.impl.mapping.IEntityMappingRegistry;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collection;

/**
 * @author Kiryl Karatsetski
 */
public class DeleteOperation extends Operation implements IDeleteOperation {

    private static final Logger log = LoggerFactory.getLogger(UpdateOperation.class);

    private EntityField idEntityFiled;

    private String deleteQuery;

    private UpdateSetter updateSetterByObject = new UpdateSetterByObject();
    private UpdateSetter updateSetterByKey = new UpdateSetterByKey();

    public DeleteOperation(IEntityMappingRegistry registry, DBTool dbTool, Class entityClass) {
        super(registry, dbTool, entityClass);
    }

    @Override
    protected void initOperation() {
        final Collection<FieldMapping> idFieldMappings = entityMappingRegistry.getIdFields(entityClass);

        if (idFieldMappings == null || idFieldMappings.isEmpty()) {
            throw new RuntimeException("IdFieldMapping for entity class" + entityClass + " not found");
        }

        final FieldMapping idFieldMapping = idFieldMappings.iterator().next();
        this.idEntityFiled = idFieldMapping.getEntityFiled();

        final String tableName = entityMappingRegistry.getTableName(entityClass);
        final String idColumnName = idFieldMapping.getColumn();

        this.deleteQuery = buildQuery(tableName, idColumnName);

        if (log.isTraceEnabled()) {
            log.trace("Build DeleteOperation query for EntityClass {}: {}", entityClass, deleteQuery);
        }
    }

    private String buildQuery(String tableName, String idColumnName) {
        final StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("delete from ");
        stringBuilder.append(tableName);
        stringBuilder.append(" where ");
        stringBuilder.append(idColumnName).append(" in (?)");
        return stringBuilder.toString();
    }

    public void executeDelete(Object object) throws UpdateException {
        executeDelete(Arrays.asList(object));
    }

    @SuppressWarnings("unchecked")
    public void executeDelete(Collection objects) throws UpdateException {
        UpdateUtil.batchUpdate(getJdbcTemplate(), deleteQuery, objects, updateSetterByObject);
    }

    public void executeDeleteByKey(Object key) throws UpdateException {
        executeDeleteByKeys(Arrays.asList(key));
    }

    @SuppressWarnings("unchecked")
    public void executeDeleteByKeys(Collection keys) throws UpdateException {
        UpdateUtil.batchUpdate(getJdbcTemplate(), deleteQuery, keys, updateSetterByKey);
    }

    private class UpdateSetterByObject implements UpdateSetter {
        public boolean setValues(PreparedStatement pst, Object object) throws SQLException {
            try {
                FieldSetter.setValue(pst, 1, idEntityFiled.getType(), idEntityFiled.getValue(object));
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
