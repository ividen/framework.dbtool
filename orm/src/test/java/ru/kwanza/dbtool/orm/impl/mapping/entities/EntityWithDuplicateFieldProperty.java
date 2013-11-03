package ru.kwanza.dbtool.orm.impl.mapping.entities;

import ru.kwanza.dbtool.orm.annotations.Entity;
import ru.kwanza.dbtool.orm.annotations.IdField;

/**
 * @author Kiryl Karatsetski
 */
@Entity(name = "EntityWithDuplicateFieldProperty", table = "entity_with_duplicate_field_property")
public class EntityWithDuplicateFieldProperty extends AbstractEntity {

    @IdField( "id2")
    private Long id;
}
