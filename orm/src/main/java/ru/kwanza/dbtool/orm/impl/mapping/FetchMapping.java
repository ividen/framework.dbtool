package ru.kwanza.dbtool.orm.impl.mapping;

import ru.kwanza.dbtool.orm.api.If;

import java.util.concurrent.locks.Condition;

/**
 * @author Kiryl Karatsetski
 */
public class FetchMapping {
    private final String name;
    private final Class relationClass;
    private final FieldMapping propertyFieldMapping;
    private final FieldMapping relationFieldMapping;
    private final EntityField fetchField;
    private final If condition;

    public FetchMapping(String name, Class relationClass, FieldMapping propertyFieldMapping, FieldMapping relationFieldMapping,
                        EntityField fetchField, If condition) {
        this.name = name;
        this.relationClass = relationClass;
        this.propertyFieldMapping = propertyFieldMapping;
        this.relationFieldMapping = relationFieldMapping;
        this.fetchField = fetchField;
        this.condition = condition;
    }

    public FetchMapping(String name, Class relationClass, FieldMapping propertyFieldMapping, FieldMapping relationFieldMapping,
                        EntityField fetchField) {
        this(name, relationClass, propertyFieldMapping, relationFieldMapping, fetchField, null);
    }

    public FieldMapping getPropertyMapping() {
        return propertyFieldMapping;
    }

    public Class getRelationClass() {
        return relationClass;
    }

    public FieldMapping getRelationPropertyMapping() {
        return relationFieldMapping;
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

    public String getName() {
        return name;
    }

    public If getCondition() {
        return condition;
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
