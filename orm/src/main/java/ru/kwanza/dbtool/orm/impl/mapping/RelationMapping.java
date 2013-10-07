package ru.kwanza.dbtool.orm.impl.mapping;

import ru.kwanza.dbtool.orm.api.If;
import ru.kwanza.toolbox.fieldhelper.Property;

import java.util.Collection;
import java.util.Map;

/**
 * @author Kiryl Karatsetski
 */
public class RelationMapping {
    private final String name;
    private final Class relationClass;
    private final FieldMapping keyMapping;
    private final FieldMapping relationKeyMapping;
    private final Property property;
    private final If condition;
    private final Property[] groupBy;

    public RelationMapping(String name, Class relationClass, FieldMapping keyMapping, FieldMapping relationKeyMapping,
                           Property property, If condition, Property[] groupBy) {
        this.name = name;
        this.relationClass = relationClass;
        this.keyMapping = keyMapping;
        this.relationKeyMapping = relationKeyMapping;
        this.property = property;
        this.condition = condition;
        this.groupBy = groupBy;

    }

    public FieldMapping getKeyMapping() {
        return keyMapping;
    }

    public Class getRelationClass() {
        return relationClass;
    }

    public FieldMapping getRelationKeyMapping() {
        return relationKeyMapping;
    }

    public String getRelationKeyMappingName() {
        return relationKeyMapping != null ? relationKeyMapping.getName() : null;
    }


    public Property getKeyProperty() {
        return keyMapping != null ? keyMapping.getProperty() : null;
    }

    public Property getProperty() {
        return property;
    }

    public String getKeyMappingName() {
        return keyMapping != null ? keyMapping.getName() : null;
    }

    public String getName() {
        return name;
    }

    public If getCondition() {
        return condition;
    }

    public Property[] getGroupBy() {
        return groupBy;
    }

    public boolean isCollection() {
        return Collection.class.isAssignableFrom(getProperty().getType()) || Map.class.isAssignableFrom(getProperty().getType());
    }

    @Override
    public String toString() {
        final StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("RelationMapping{");
        stringBuilder.append("name='").append(getName()).append('\'');
        stringBuilder.append("property='").append(getKeyMappingName()).append('\'');
        stringBuilder.append("relationProperty='").append(getRelationKeyMappingName()).append('\'');
        stringBuilder.append('}');
        return stringBuilder.toString();
    }
}
