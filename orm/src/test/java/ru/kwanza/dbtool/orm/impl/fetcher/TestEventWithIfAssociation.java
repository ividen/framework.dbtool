package ru.kwanza.dbtool.orm.impl.fetcher;

import ru.kwanza.dbtool.orm.annotations.Association;
import ru.kwanza.dbtool.orm.annotations.Condition;
import ru.kwanza.dbtool.orm.annotations.ManyToOne;

/**
 * @author Alexander Guzanov
 */
public class TestEventWithIfAssociation {
    private final Long id;

    @ManyToOne(property = "id")
    @Condition("and(isNotNull('version'),isGreater('intField',valueOf(0)),like('entityA.title',valueOf('test_entity_a%')))")
    public TestEntity entity;

    public TestEventWithIfAssociation(Long id) {
        this.id = id;
    }

    public TestEntity getEntity() {
        return entity;
    }
}
