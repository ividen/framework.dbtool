package ru.kwanza.dbtool.orm.impl.mapping.entities;

import ru.kwanza.dbtool.orm.annotations.Field;
import ru.kwanza.dbtool.orm.annotations.IdField;
import ru.kwanza.dbtool.orm.annotations.VersionField;

/**
 * @author Kiryl Karatsetski
 */
public abstract class AbstractEntity {

    @IdField(columnName = "id")
    private Long id;

    @Field(columnName = "pcid")
    private String pcid;

    @VersionField(columnName = "version")
    private Long version;

    protected AbstractEntity() {
    }

    protected AbstractEntity(Long id, String pcid) {
        this.id = id;
        this.pcid = pcid;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPcid() {
        return pcid;
    }

    public void setPcid(String pcid) {
        this.pcid = pcid;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }
}
