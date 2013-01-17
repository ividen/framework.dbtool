package ru.kwanza.dbtool.orm.impl.fetcher;

import ru.kwanza.dbtool.orm.annotations.*;

/**
 * @author Alexander Guzanov
 */
@Entity(name = "TestEntityE", table = "test_entity_e")
public class TestEntityE {
    @IdField(column = "id")
    private Long id;
    @Field(column = "title")
    private String title;
    @VersionField(column = "version")
    private Long version;

    @Field(column = "entity_gid")
    private Long entityGID;

    @ManyToOne(property = "entityGID")
    private TestEntityG entityG;

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public Long getVersion() {
        return version;
    }

    public Long getEntityGID() {
        return entityGID;
    }

    public TestEntityG getEntityG() {
        return entityG;
    }
}
