package ru.kwanza.dbtool.orm.impl.mapping.entities;

import ru.kwanza.dbtool.orm.annotations.Field;
import ru.kwanza.dbtool.orm.annotations.IdField;

/**
 * @author Kiryl Karatsetski
 */
public abstract class AbstractEntity {

    @IdField(columnName = "id")
    private Long id;

    @Field(columnName = "pcid")
    private String pcid;
}
