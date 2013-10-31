package ru.kwanza.dbtool.orm.impl.mapping;

import ru.kwanza.dbtool.orm.annotations.GroupByType;
import ru.kwanza.dbtool.orm.api.If;
import ru.kwanza.dbtool.orm.api.Join;
import ru.kwanza.dbtool.orm.api.internal.IFieldMapping;
import ru.kwanza.dbtool.orm.api.internal.IRelationMapping;
import ru.kwanza.toolbox.fieldhelper.Property;
import ru.kwanza.toolbox.splitter.Splitter;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author Kiryl Karatsetski
 */
class RelationMapping implements IRelationMapping {
    private final String name;
    private final Class relationClass;
    private final IFieldMapping keyMapping;
    private final IFieldMapping relationKeyMapping;
    private final Property property;
    private final If condition;
    private final Splitter groupBy;
    private final GroupByType groupByType;
    private List<Join> joins = null;

    public RelationMapping(String name, Class relationClass, IFieldMapping keyMapping, IFieldMapping relationKeyMapping,
                           Property property) {
        this(name, relationClass, keyMapping, relationKeyMapping, property, null, null, null, null);
    }

    public RelationMapping(String name, Class relationClass, IFieldMapping keyMapping, IFieldMapping relationKeyMapping, Property property,
                           If condition, Property[] groupBy, GroupByType groupByType,  List<Join> joins) {
        this.name = name;
        this.relationClass = relationClass;
        this.keyMapping = keyMapping;
        this.relationKeyMapping = relationKeyMapping;
        this.property = property;
        this.condition = condition;
        this.groupBy = groupBy == null ? null : new Splitter(groupBy);
        this.groupByType = groupByType;
        this.joins = joins==null? Collections.<Join>emptyList():joins;
    }

    public List<Join> getJoins() {
        return joins;
    }

    public IFieldMapping getKeyMapping() {
        return keyMapping;
    }

    public Class getRelationClass() {
        return relationClass;
    }

    public IFieldMapping getRelationKeyMapping() {
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
