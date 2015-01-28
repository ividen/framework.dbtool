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

import ru.kwanza.dbtool.orm.annotations.Association;
import ru.kwanza.dbtool.orm.annotations.GroupBy;
import ru.kwanza.dbtool.orm.annotations.GroupByType;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author Alexander Guzanov
 */
public class EventWithAssociation implements Serializable {
    private Long entityAID;

    @Association(property = "entityAID", relationProperty = "entityAID", relationClass = TestEntity.class)
    private Collection<TestEntity> entities;

    @Association(property = "entityAID", relationProperty = "entityAID", relationClass = TestEntity.class)
    @GroupBy(value = "entityA", type = GroupByType.MAP_OF_LIST)
    private Map<TestEntityA, List<TestEntity>> entitiesByEntityA;

    @Association(property = "entityAID", relationProperty = "entityAID", relationClass = TestEntity.class)
    @GroupBy(value = "entityA, entityC.entityE.id", type = GroupByType.MAP_OF_LIST)
    private Map<TestEntityA, Map<Long, List<TestEntity>>> entitiesByACEId;

    @Association(property = "entityAID", relationProperty = "entityAID", relationClass = TestEntity.class)
    @GroupBy("id")
    private Map<Long, TestEntity> entitiesById;

    public EventWithAssociation(Long entityAID) {
        this.entityAID = entityAID;
    }

    public Long getEntityAID() {
        return entityAID;
    }

    public Collection<TestEntity> getEntities() {
        return entities;
    }

    public Map<TestEntityA, List<TestEntity>> getEntitiesByEntityA() {
        return entitiesByEntityA;
    }

    public Map<TestEntityA, Map<Long, List<TestEntity>>> getEntitiesByACEId() {
        return entitiesByACEId;
    }

    public Map<Long, TestEntity> getEntitiesById() {
        return entitiesById;
    }
}


