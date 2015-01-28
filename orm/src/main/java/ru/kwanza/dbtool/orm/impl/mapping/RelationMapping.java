package ru.kwanza.dbtool.orm.impl.mapping;

/*
 * #%L
 * dbtool-orm
 * %%
 * Copyright (C) 2015 Kwanza
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

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
