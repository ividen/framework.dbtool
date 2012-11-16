package ru.kwanza.dbtool.orm.impl.mapping.entities;

import ru.kwanza.dbtool.orm.annotations.Entity;
import ru.kwanza.dbtool.orm.annotations.Field;

/**
 * @author Kiryl Karatsetski
 */
@Entity(name = "EntityWithDuplicateColumn", tableName = "entity_with_duplicate_column")
public class EntityWithDuplicateColumn {

    @Field(columnName = "field")
    private int field1;

    @Field(columnName = "field")
    private int field2;
}

