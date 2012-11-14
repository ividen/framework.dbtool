package ru.kwanza.dbtool.orm.mapping;

/**
 * @author Kiryl Karatsetski
 */
public class FetchMapping {

    private EntityField field;
    private EntityField fetchField;

    public FetchMapping(EntityField mappedField, EntityField fetchField) {
        this.field = mappedField;
        this.fetchField = fetchField;
    }

    public EntityField getField() {
        return field;
    }

    public EntityField getFetchField() {
        return fetchField;
    }
}
