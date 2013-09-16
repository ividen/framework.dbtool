package ru.kwanza.dbtool.orm.impl.fetcher;

import ru.kwanza.dbtool.orm.annotations.ManyToOne;

/**
 * @author Alexander Guzanov
 */
public class TestEvent {
    private Long entityId;

    public TestEvent(Long entityId) {
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
