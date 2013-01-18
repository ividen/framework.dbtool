package ru.kwanza.dbtool.orm.impl.mapping;

/**
 * @author Kiryl Karatsetski
 */
public class FetchMapping {
    private String name;
    private Class relationClass;
    private FieldMapping propertyFieldMapping;
    private FieldMapping relationFieldMapping;
    private EntityField fetchField;

    FetchMapping(String name,
                 Class relationClass,
                 FieldMapping propertyFieldMapping,
                 FieldMapping relationFieldMapping,
                 EntityField fetchField) {
        this.name = name;
        this.relationClass = relationClass;
        this.propertyFieldMapping = propertyFieldMapping;
        this.relationFieldMapping = relationFieldMapping;
        this.fetchField = fetchField;
    }

    public String getRelationPropertyName() {
        return relationFieldMapping != null ? relationFieldMapping.getName() : null;
    }

    public EntityField getRelationEntityField() {
        return relationFieldMapping != null ? relationFieldMapping.getEntityFiled() : null;
    }

    public EntityField getPropertyField() {
        return propertyFieldMapping != null ? propertyFieldMapping.getEntityFiled() : null;
    }

    public EntityField getFetchField() {
        return fetchField;
    }

    public String getPropertyName() {
        return propertyFieldMapping != null ? propertyFieldMapping.getName() : null;
    }
    
    public String getName(){
        return name;
    }

    @Override
    public String toString() {
        final StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("FetchMapping{");
        stringBuilder.append("name='").append(getName()).append('\'');
        stringBuilder.append("property='").append(getPropertyName()).append('\'');
        stringBuilder.append("relationProperty='").append(getRelationPropertyName()).append('\'');
        stringBuilder.append('}');
        return stringBuilder.toString();
    }
}
