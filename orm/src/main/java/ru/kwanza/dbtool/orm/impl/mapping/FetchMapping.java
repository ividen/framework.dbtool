package ru.kwanza.dbtool.orm.impl.mapping;

/**
 * @author Kiryl Karatsetski
 */
public class FetchMapping {

    private String propertyName;

    private EntityField propertyField;
    private EntityField fetchField;

    public FetchMapping(String propertyName, EntityField propertyField, EntityField fetchField) {
        this.propertyName = propertyName;
        this.propertyField = propertyField;
        this.fetchField = fetchField;
    }

    public EntityField getPropertyField() {
        return propertyField;
    }

    public EntityField getFetchField() {
        return fetchField;
    }

    public String getPropertyName() {
        return propertyName;
    }

    @Override
    public String toString() {
        final StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("FetchMapping{");
        stringBuilder.append("propertyName='").append(propertyName).append('\'');
        stringBuilder.append(", propertyField=").append(propertyField != null ? propertyField.getClass() : null);
        stringBuilder.append(", fetchField=").append(fetchField != null ? fetchField.getClass() : null);
        stringBuilder.append('}');
        return stringBuilder.toString();
    }
}
