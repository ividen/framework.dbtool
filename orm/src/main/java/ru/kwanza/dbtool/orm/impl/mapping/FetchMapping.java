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
}
