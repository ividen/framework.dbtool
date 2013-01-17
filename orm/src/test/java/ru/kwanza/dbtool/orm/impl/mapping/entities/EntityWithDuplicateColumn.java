package ru.kwanza.dbtool.orm.impl.mapping.entities;

import ru.kwanza.dbtool.orm.annotations.Entity;
import ru.kwanza.dbtool.orm.annotations.Field;

/**
 * @author Kiryl Karatsetski
 */
@Entity(name = "EntityWithDuplicateColumn", table = "entity_with_duplicate_column")
public class EntityWithDuplicateColumn {

    @Field(column = "field")
    private int field1;

    @Field(column = "field")
    private int field2;
}

