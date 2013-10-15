package ru.kwanza.dbtool.orm.impl.operation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.kwanza.dbtool.core.DBTool;
import ru.kwanza.dbtool.core.FieldSetter;
import ru.kwanza.dbtool.core.UpdateException;
import ru.kwanza.dbtool.core.UpdateSetter;
import ru.kwanza.dbtool.core.util.UpdateUtil;
import ru.kwanza.dbtool.orm.api.internal.IEntityMappingRegistry;
import ru.kwanza.dbtool.orm.api.internal.IEntityType;
import ru.kwanza.dbtool.orm.api.internal.IFieldMapping;
import ru.kwanza.toolbox.fieldhelper.Property;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collection;

/**
 * @author Kiryl Karatsetski
 */
public class CreateOperation extends Operation implements ICreateOperation {

    private static final Logger log = LoggerFactory.getLogger(CreateOperation.class);

    private Collection<IFieldMapping> fieldMappings;

    private IFieldMapping versionFieldMapping;

    private String createQuery;

    private UpdateSetter updateSetter = new CreateOperationSetter();

    public CreateOperation(IEntityMappingRegistry registry, DBTool dbTool, Class entityClass) {
        super(registry, dbTool, entityClass);
    }

    @Override
    protected void initOperation() {
        final IEntityType entityType = entityMappingRegistry.getEntityType(entityClass);
        this.fieldMappings = entityType.getFields();
        this.versionFieldMapping = entityType.getVersionField();

        final String tableName = entityType.getTableName();
        this.createQuery = buildQuery(tableName, fieldMappings);

        if (log.isTraceEnabled()) {
            log.trace("Build CreateOperation query for EntityClass {}: {}", entityClass, createQuery);
        }
    }

    public void executeCreate(Object object) throws UpdateException {
        executeCreate(Arrays.asList(object));
    }

    @SuppressWarnings("unchecked")
    public void executeCreate(Collection objects) throws UpdateException {
        UpdateUtil.batchUpdate(getJdbcTemplate(), createQuery, objects, updateSetter, dbTool.getDbType());
    }

    private String buildQuery(String tableName, Collection<IFieldMapping> fields) {
        final StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("insert into ");
        stringBuilder.append(tableName).append(" (");
        for (IFieldMapping field : fields) {
            stringBuilder.append(field.getColumn()).append(",");
        }
        stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        stringBuilder.append(") values (");
        for (int i = 0; i < fields.size(); i++) {
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
                for (IFieldMapping fieldMapping : fieldMappings) {
                    final Property entityFiled = fieldMapping.getProperty();
                    if (versionFieldMapping != null && fieldMapping.getColumn().equals(versionFieldMapping.getColumn())) {
                        entityFiled.set(object, 1L);
                    }
                    FieldSetter.setValue(pst, ++index, entityFiled.getType(), entityFiled.value(object));
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
