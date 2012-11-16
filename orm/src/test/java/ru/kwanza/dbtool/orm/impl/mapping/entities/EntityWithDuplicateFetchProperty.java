package ru.kwanza.dbtool.orm.impl.mapping.entities;

import ru.kwanza.dbtool.orm.annotations.Entity;
import ru.kwanza.dbtool.orm.annotations.Fetch;

/**
 * @author Kiryl Karatsetski
 */
@Entity(name = "EntityWithDuplicateFieldProperty", tableName = "entity_with_duplicate_field_property")
public class EntityWithDuplicateFetchProperty extends PaymentTrx {

    @Fetch(propertyName = "subAgentId")
    private Agent agent;
}
