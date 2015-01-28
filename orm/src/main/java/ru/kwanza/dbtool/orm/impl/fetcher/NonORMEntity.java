package ru.kwanza.dbtool.orm.impl.fetcher;

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

import ru.kwanza.dbtool.orm.api.internal.IEntityType;
import ru.kwanza.dbtool.orm.api.internal.IFieldMapping;
import ru.kwanza.dbtool.orm.api.internal.IRelationMapping;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Alexander Guzanov
 */
public class NonORMEntity implements IEntityType {
    private Class entityClass;
    private Map<String, IRelationMapping> relations = new HashMap<String, IRelationMapping>();

    public NonORMEntity(Class entityClass, Map<String, IRelationMapping> relations) {
        this.entityClass = entityClass;
        this.relations = relations;
    }

    public String getName() {
        return entityClass.getName();
    }

    public String getTableName() {
        throw new UnsupportedOperationException();
    }

    public String getSql() {
        throw new UnsupportedOperationException();
    }

    public Class getEntityClass() {
        return entityClass;
    }

    public boolean isAbstract() {
        return false;
    }

    public IFieldMapping getIdField() {
        throw new UnsupportedOperationException();
    }

    public IFieldMapping getVersionField() {
        throw new UnsupportedOperationException();
    }

    public IFieldMapping getField(String name) {
        throw new UnsupportedOperationException();
    }

    public Collection<IFieldMapping> getFields() {
        throw new UnsupportedOperationException();
    }

    public IRelationMapping getRelation(String name) {
        return relations.get(name);
    }

    public Collection<IRelationMapping> getRelations() {
        return Collections.unmodifiableCollection(relations.values());
    }
}
