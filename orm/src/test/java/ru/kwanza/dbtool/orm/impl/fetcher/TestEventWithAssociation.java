package ru.kwanza.dbtool.orm.impl.fetcher;

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
public class TestEventWithAssociation implements Serializable {
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

    public TestEventWithAssociation(Long entityAID) {
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


