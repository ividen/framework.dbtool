package ru.kwanza.dbtool.orm.impl.mapping.entities;

import ru.kwanza.dbtool.orm.annotations.Entity;
import ru.kwanza.dbtool.orm.annotations.ManyToOne;

/**
 * @author Kiryl Karatsetski
 */
@Entity(name = "EntityWithDuplicateFetchProperty", table = "entity_with_duplicate_fetch_property")
public class EntityWithDuplicateFetchProperty extends PaymentTrx {

    @ManyToOne(property = "subAgentId")
    private Agent agent;
}
