package ru.kwanza.dbtool.orm.impl.mapping;

import ru.kwanza.dbtool.orm.annotations.GroupByType;
import ru.kwanza.dbtool.orm.api.If;
import ru.kwanza.dbtool.orm.api.Join;
import ru.kwanza.toolbox.fieldhelper.Property;
import ru.kwanza.toolbox.splitter.Splitter;

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
    private final Splitter groupBy;
    private final GroupByType groupByType;
    private Join[] joins = null;

    public RelationMapping(String name, Class relationClass, FieldMapping keyMapping, FieldMapping relationKeyMapping, Property property) {
        this(name, relationClass, keyMapping, relationKeyMapping, property, null, null, null, null);
    }

    public RelationMapping(String name, Class relationClass, FieldMapping keyMapping, FieldMapping relationKeyMapping, Property property,
                           If condition, Property[] groupBy, GroupByType groupByType, Join[] joins) {
        this.name = name;
        this.relationClass = relationClass;
        this.keyMapping = keyMapping;
        this.relationKeyMapping = relationKeyMapping;
        this.property = property;
        this.condition = condition;
        this.groupBy = groupBy == null ? null : new Splitter(groupBy);
        this.groupByType = groupByType;
        this.joins = joins;
    }

    public Join[] getJoins() {
        return joins;
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

    public Splitter getGroupBy() {
        return groupBy;
    }

    public GroupByType getGroupByType() {
        return groupByType;
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
