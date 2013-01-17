package ru.kwanza.dbtool.orm.impl.mapping;

/**
 * @author Kiryl Karatsetski
 */
public class FetchMapping {
    private FieldMapping propertyFieldMapping;
    private FieldMapping relationFieldMapping;
    private EntityField fetchField;


    FetchMapping(FieldMapping propertyFieldMapping,
                 FieldMapping relationFieldMapping,
                 EntityField fetchField) {
        this.propertyFieldMapping = propertyFieldMapping;
        this.relationFieldMapping = relationFieldMapping;
        this.fetchField = fetchField;
    }

    public String getRelationPropertyName() {
        return relationFieldMapping.getPropertyName();
    }

    public EntityField getRelationEntityField() {
        return relationFieldMapping.getEntityFiled();
    }

    public EntityField getPropertyField() {
        return propertyFieldMapping.getEntityFiled();
    }

    public EntityField getFetchField() {
        return fetchField;
    }

    public String getPropertyName() {
        return propertyFieldMapping.getPropertyName();
    }

    @Override
    public String toString() {
        final StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("FetchMapping{");
        stringBuilder.append("property='").append(getPropertyName()).append('\'');
        stringBuilder.append("relationProperty='").append(getRelationEntityField()).append('\'');
        stringBuilder.append('}');
        return stringBuilder.toString();
    }
}
