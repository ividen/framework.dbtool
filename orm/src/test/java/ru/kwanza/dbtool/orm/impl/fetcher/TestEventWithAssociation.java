package ru.kwanza.dbtool.orm.impl.fetcher;

import ru.kwanza.dbtool.orm.annotations.Association;

import java.io.Serializable;
import java.util.Collection;

/**
 * @author Alexander Guzanov
 */
public class TestEventWithAssociation   implements Serializable {
    private Long entityAID;

    @Association(property = "entityAID", relationProperty = "entityAID",relationClass = TestEntity.class)
    private Collection<TestEntity> entities;

    public TestEventWithAssociation(Long entityAID) {
        this.entityAID = entityAID;
    }

    public Long getEntityAID() {
        return entityAID;
    }

    public Collection<TestEntity> getEntities() {
        return entities;
    }
}


