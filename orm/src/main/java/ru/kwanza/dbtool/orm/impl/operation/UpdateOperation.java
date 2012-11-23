package ru.kwanza.dbtool.orm.impl.operation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.RowMapper;
import ru.kwanza.dbtool.core.*;
import ru.kwanza.dbtool.core.util.FieldValueExtractor;
import ru.kwanza.dbtool.core.util.UpdateUtil;
import ru.kwanza.dbtool.orm.impl.mapping.EntityField;
import ru.kwanza.dbtool.orm.impl.mapping.FieldMapping;
import ru.kwanza.dbtool.orm.impl.mapping.IEntityMappingRegistry;
import ru.kwanza.toolbox.fieldhelper.FieldHelper;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collection;

/**
 * @author Kiryl Karatsetski
 */
public class UpdateOperation extends Operation implements IUpdateOperation {

    private static final Logger log = LoggerFactory.getLogger(UpdateOperation.class);

    private Collection<FieldMapping> fieldMappings;

    private FieldMapping idFieldMapping;
    private EntityField idEntityField;

    private FieldMapping versionFieldMapping;
    private EntityField versionEntityField;

    private boolean versionSupport;

    private String updateQuery;
    private String checkQuery;

    private UpdateSetter updateSetter = new UpdateOperationSetter();
    private UpdateSetterWithVersion<Object, Long> updateOperationSetter = new UpdateOperationSetter();
    private RowMapper<KeyValue<Comparable, Long>> keyVersionRowMapper = new KeyVersionRowMapper();

    private FieldHelper.Field<Object, Comparable> keyField = new KeyField();
    private FieldHelper.VersionField<Object, Long> versionField = new VersionField();

    private VersionGenerator versionGenerator;

    public UpdateOperation(IEntityMappingRegistry entityMappingRegistry, DBTool dbTool, Class entityClass,
                           VersionGenerator versionGenerator) {
        super(entityMappingRegistry, dbTool, entityClass);
        this.versionGenerator = versionGenerator;
    }

    protected void initOperation() {
        this.fieldMappings = entityMappingRegistry.getFieldMappings(entityClass);

        final Collection<FieldMapping> idFieldMappings = entityMappingRegistry.getIdFields(entityClass);

        if (idFieldMappings == null || idFieldMappings.isEmpty()) {
            throw new RuntimeException("IdFieldMapping for entity class" + entityClass + " not found");
        }

        this.idFieldMapping = idFieldMappings.iterator().next();
        this.idEntityField = idFieldMapping != null ? idFieldMapping.getEntityFiled() : null;

        this.versionFieldMapping = entityMappingRegistry.getVersionField(entityClass);
        this.versionEntityField = versionFieldMapping != null ? versionFieldMapping.getEntityFiled() : null;

        this.versionSupport = versionEntityField != null;

        final String tableName = entityMappingRegistry.getTableName(entityClass);
        final Collection<String> columnNames = entityMappingRegistry.getColumnNames(entityClass);
        final String idColumnName = idFieldMapping != null ? idFieldMapping.getColumnName() : null;
        final String versionColumnName = versionFieldMapping != null ? versionFieldMapping.getColumnName() : null;

        this.updateQuery = buildUpdateQuery(tableName, columnNames, idColumnName, versionColumnName);
        this.checkQuery = buildCheckQuery(tableName, idColumnName, versionColumnName);

        if (log.isTraceEnabled()) {
            log.trace("Build UpdateOperation update query for EntityClass {}: {}", entityClass, updateQuery);
            log.trace("Build UpdateOperation check for EntityClass {}: {}", entityClass, checkQuery);
        }
    }

    private String buildUpdateQuery(String tableName, Collection<String> columnNames, String idColumnName, String versionColumnName) {
        final StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("update ");
        stringBuilder.append(tableName);
        stringBuilder.append(" set ");
        for (String columnName : columnNames) {
            stringBuilder.append(columnName).append("=?, ");
        }
        stringBuilder.deleteCharAt(stringBuilder.length() - 2);
        stringBuilder.append("where ");
        stringBuilder.append(idColumnName).append("=?");
        if (versionColumnName != null) {
            stringBuilder.append(" and ");
            stringBuilder.append(versionColumnName).append("=?");
        }
        return stringBuilder.toString();
    }

    private String buildCheckQuery(String tableName, String idColumnName, String versionColumnName) {
        final StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("select ");
        stringBuilder.append(idColumnName).append(",").append(versionColumnName);
        stringBuilder.append(" from ");
        stringBuilder.append(tableName);
        stringBuilder.append(" where ");
        stringBuilder.append(idColumnName).append(" in (?)");
        return stringBuilder.toString();
    }

    public void executeUpdate(Object object) throws UpdateException {
        executeUpdate(Arrays.asList(object));
    }

    @SuppressWarnings("unchecked")
    public void executeUpdate(Collection objects) throws UpdateException {
        if (versionSupport) {
            UpdateUtil.batchUpdate(getJdbcTemplate(), updateQuery, objects, updateOperationSetter, checkQuery, keyVersionRowMapper, keyField,
                    versionField);
        } else {
            UpdateUtil.batchUpdate(getJdbcTemplate(), updateQuery, objects, updateSetter);
        }
    }

    private class UpdateOperationSetter implements UpdateSetter, UpdateSetterWithVersion<Object, Long> {

        public boolean setValues(PreparedStatement pst, Object object) throws SQLException {
            try {
                int index = 0;
                for (FieldMapping fieldMapping : fieldMappings) {
                    final EntityField entityFiled = fieldMapping.getEntityFiled();
                    FieldSetter.setValue(pst, ++index, entityFiled.getType(), entityFiled.getValue(object));
                }
                FieldSetter.setValue(pst, ++index, idEntityField.getType(), idEntityField.getValue(object));
            } catch (SQLException e) {
                throw e;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            return true;
        }

        public boolean setValues(PreparedStatement pst, Object object, Long newVersion, Long oldVersion) throws SQLException {
            try {
                int index = 0;
                for (FieldMapping fieldMapping : fieldMappings) {
                    final EntityField entityFiled = fieldMapping.getEntityFiled();
                    if (fieldMapping.getColumnName().equals(versionFieldMapping.getColumnName())) {
                        FieldSetter.setLong(pst, ++index, newVersion);
                    } else {
                        FieldSetter.setValue(pst, ++index, entityFiled.getType(), entityFiled.getValue(object));
                    }
                }
                FieldSetter.setValue(pst, ++index, idEntityField.getType(), idEntityField.getValue(object));
                FieldSetter.setLong(pst, ++index, oldVersion);
            } catch (SQLException e) {
                throw e;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            return true;
        }
    }

    private class KeyVersionRowMapper implements RowMapper<KeyValue<Comparable, Long>> {
        public KeyValue<Comparable, Long> mapRow(ResultSet rs, int rowNum) throws SQLException {
            final Object key = FieldValueExtractor.getValue(rs, idFieldMapping.getColumnName(), idEntityField.getType());
            final Long version = (Long) FieldValueExtractor.getValue(rs, versionFieldMapping.getColumnName(), versionEntityField.getType());
            return new KeyValue<Comparable, Long>((Comparable) key, version);
        }
    }

    private class KeyField implements FieldHelper.Field<Object, Comparable> {
        public Comparable value(Object object) {
            return (Comparable) idEntityField.getValue(object);
        }
    }

    private class VersionField implements FieldHelper.VersionField<Object, Long> {
        public Long value(Object object) {
            return (Long) versionEntityField.getValue(object);
        }

        public Long generateNewValue(Object object) {
            return versionGenerator.generate(entityClass.getName(), (Long) versionEntityField.getValue(object));
        }

        public void setValue(Object object, Long value) {
            versionEntityField.setValue(object, value);
        }
    }
}