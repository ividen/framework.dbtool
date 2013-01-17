package ru.kwanza.dbtool.orm.impl.fetcher;

import ru.kwanza.dbtool.orm.annotations.*;

/**
 * @author Alexander Guzanov
 */
@Entity(name = "TestEntityC", table = "test_entity_c")
public class TestEntityC {
    @IdField(column = "id")
    private Long id;
    @Field(column = "title")
    private String title;
    @VersionField(column = "version")
    private Long version;


    @Field(column = "entity_eid")
    private Long entityEID;
    @Field(column = "entity_fid")
    private Long entityFID;


    @ManyToOne(property = "entityEID")
    private TestEntityE entityE;
    @ManyToOne(property = "entityFID")
    private TestEntityF entityF;

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public Long getVersion() {
        return version;
    }

    public Long getEntityEID() {
        return entityEID;
    }

    public Long getEntityFID() {
        return entityFID;
    }

    public TestEntityE getEntityE() {
        return entityE;
    }

    public TestEntityF getEntityF() {
        return entityF;
    }
}
