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
import java.util.Collection;

/**
 * @author Kiryl Karatsetski
 */
public class CreateOperation extends Operation {

    private Collection<FieldMapping> fieldMappings;

    private FieldMapping versionFieldMapping;

    private String createQuery;

    private UpdateSetter updateSetter = new CreateOperationSetter();

    public CreateOperation(IEntityMappingRegistry registry, JdbcTemplate jdbcTemplate, Class entityClass) {
        super(registry, jdbcTemplate, entityClass);
    }

    @Override
    protected void initOperation() {
        this.fieldMappings = entityMappingRegistry.getFieldMappings(entityClass);
        this.versionFieldMapping = entityMappingRegistry.getVersionField(entityClass);

        final String tableName = entityMappingRegistry.getTableName(entityClass);
        final Collection<String> columnNames = entityMappingRegistry.getColumnNames(entityClass);

        this.createQuery = buildQuery(tableName, columnNames);
    }

    @SuppressWarnings("unchecked")
    public void execute(Collection objects) throws UpdateException {
        UpdateUtil.batchUpdate(jdbcTemplate, createQuery, objects, updateSetter);
    }

    private String buildQuery(String tableName, Collection<String> columnNames) {
        final StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("insert into ");
        stringBuilder.append(tableName).append(" (");
        for (String columnName : columnNames) {
            stringBuilder.append(columnName).append(",");
        }
        stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        stringBuilder.append(") values (");
        for (int i = 0; i < columnNames.size(); i++) {
            stringBuilder.append("?,");
        }
        stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        stringBuilder.append(')');
        return stringBuilder.toString();
    }

    private class CreateOperationSetter implements UpdateSetter {
        public boolean setValues(PreparedStatement pst, Object object) throws SQLException {
            try {
                int index = 0;
                for (FieldMapping fieldMapping : fieldMappings) {
                    final EntityField entityFiled = fieldMapping.getEntityFiled();
                    if (fieldMapping.getColumnName().equals(versionFieldMapping.getColumnName())) {
                        entityFiled.setValue(object, 1L);
                    }
                    FieldSetter.setValue(pst, ++index, entityFiled.getValue(object));
                }
            } catch (SQLException e) {
                throw e;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            return true;
        }
    }
}
