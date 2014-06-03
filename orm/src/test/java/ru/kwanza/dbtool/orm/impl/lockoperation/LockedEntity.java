package ru.kwanza.dbtool.orm.impl.lockoperation;

import ru.kwanza.dbtool.orm.annotations.Entity;
import ru.kwanza.dbtool.orm.annotations.Field;
import ru.kwanza.dbtool.orm.annotations.IdField;
import ru.kwanza.dbtool.orm.annotations.VersionField;

/**
 * @author Alexander Guzanov
 */
@Entity(table="locked_entity")
public class LockedEntity {
    @IdField("id")
    private Long id;
    @Field("name")
    private String name;
    @VersionField("version")
    private Long version;

    public LockedEntity(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Long getVersion() {
        return version;
    }
}
