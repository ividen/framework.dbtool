package ru.kwanza.dbtool.orm.impl.operation;

import org.springframework.jdbc.core.JdbcTemplate;
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
public class DeleteOperation extends Operation {

    private EntityField idEntityFiled;

    private String deleteQuery;

    private UpdateSetter updateSetterByObject = new UpdateSetterByObject();
    private UpdateSetter updateSetterByKey = new UpdateSetterByKey();

    public DeleteOperation(IEntityMappingRegistry registry, JdbcTemplate jdbcTemplate, Class entityClass) {
        super(registry, jdbcTemplate, entityClass);
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
        final String idColumnName = idFieldMapping.getColumnName();

        this.deleteQuery = buildQuery(tableName, idColumnName);
    }

    private String buildQuery(String tableName, String idColumnName) {
        final StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("delete from ");
        stringBuilder.append(tableName);
        stringBuilder.append(" where ");
        stringBuilder.append(idColumnName).append(" in (?)");
        return stringBuilder.toString();
    }

    @SuppressWarnings("unchecked")
    public void execute(Collection objects) throws UpdateException {
        UpdateUtil.batchUpdate(jdbcTemplate, deleteQuery, objects, updateSetterByObject);
    }

    @SuppressWarnings("unchecked")
    public void executeByKeys(Collection keys) throws UpdateException {
        UpdateUtil.batchUpdate(jdbcTemplate, deleteQuery, keys, updateSetterByKey);
    }

    public void executeByKey(Object key) throws UpdateException {
        executeByKeys(Arrays.asList(key));
    }

    private class UpdateSetterByObject implements UpdateSetter {
        public boolean setValues(PreparedStatement pst, Object object) throws SQLException {
            try {
                FieldSetter.setValue(pst, 1, idEntityFiled.getValue(object));
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
            FieldSetter.setValue(pst, 1, object);
            return true;
        }
    }
}
