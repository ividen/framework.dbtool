package ru.kwanza.dbtool.orm.impl.abstractentity;

import ru.kwanza.dbtool.orm.annotations.AbstractEntity;
import ru.kwanza.dbtool.orm.annotations.IdField;
import ru.kwanza.dbtool.orm.annotations.VersionField;

/**
 * @author Alexander Guzanov
 */
@AbstractEntity(name = "AbstractTestEntity")
public class AbstractTestEntity {
    @IdField( "id")
    private Long id;
    @VersionField( "version")
    private Long version;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }
}
