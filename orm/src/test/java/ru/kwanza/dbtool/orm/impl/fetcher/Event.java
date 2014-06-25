package ru.kwanza.dbtool.orm.impl.fetcher;

import ru.kwanza.dbtool.orm.annotations.ManyToOne;

import java.io.Serializable;

/**
 * @author Alexander Guzanov
 */
public class Event implements Serializable {
    private Long entityId;

    public Event(Long entityId) {
        this.entityId = entityId;
    }

    @ManyToOne(property = "entityId")
    private TestEntity testEntity;

    public Long getEntityId() {
        return entityId;
    }

    public TestEntity getTestEntity() {
        return testEntity;
    }
}
