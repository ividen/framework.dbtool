package ru.kwanza.dbtool.orm.impl.mapping;

/**
 * @author Kiryl Karatsetski
 */
public class FieldMapping {

    private String propertyName;

    private String columnName;

    public int type;

    private boolean autoGenerated;

    private EntityField entityField;

    public FieldMapping(String propertyName, String columnName, int type, boolean autoGenerated, EntityField entityField) {
        this.propertyName = propertyName;
        this.columnName = columnName;
        this.type = type;
        this.autoGenerated = autoGenerated;
        this.entityField = entityField;
    }

    public String getColumnName() {
        return columnName;
    }

    public int getType() {
        return type;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public boolean isAutoGenerated() {
        return autoGenerated;
    }

    public EntityField getEntityFiled() {
        return this.entityField;
    }

    @Override
    public String toString() {
        final StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("FieldMapping{");
        stringBuilder.append("propertyName='").append(propertyName).append('\'');
        stringBuilder.append(", columnName='").append(columnName).append('\'');
        stringBuilder.append(", type=").append(type);
        stringBuilder.append(", autoGenerated=").append(autoGenerated);
        stringBuilder.append(", entityField=").append(entityField != null ? entityField.getClass() : null);
        stringBuilder.append('}');
        return stringBuilder.toString();
    }
}
