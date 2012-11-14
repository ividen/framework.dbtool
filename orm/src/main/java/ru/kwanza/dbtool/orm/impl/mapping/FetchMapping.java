package ru.kwanza.dbtool.orm.impl.mapping;

/**
 * @author Kiryl Karatsetski
 */
public class FetchMapping {

    private EntityField propertyField;
    private EntityField fetchField;

    public FetchMapping(EntityField mappedField, EntityField fetchField) {
        this.propertyField = mappedField;
        this.fetchField = fetchField;
    }

    public EntityField getPropertyField() {
        return propertyField;
    }

    public EntityField getFetchField() {
        return fetchField;
    }
}
