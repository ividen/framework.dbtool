package ru.kwanza.dbtool.orm.impl.fetcher;

import ru.kwanza.dbtool.orm.annotations.*;

/**
 * @author Alexander Guzanov
 */
@Entity(name = "TestEntityC", tableName = "test_entity_c")
public class TestEntityC {
    @IdField(columnName = "id")
    private Long id;
    @Field(columnName = "title")
    private String title;
    @VersionField(columnName = "version")
    private Long version;


    @Field(columnName = "entity_eid")
    private Long entityAID;
    @Field(columnName = "entity_fid")
    private Long entityBID;


    @Fetch(fieldName = "entityEID")
    private TestEntityE entityE;
    @Fetch(fieldName = "entityFID")
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

    public Long getEntityAID() {
        return entityAID;
    }

    public Long getEntityBID() {
        return entityBID;
    }

    public TestEntityE getEntityE() {
        return entityE;
    }

    public TestEntityF getEntityF() {
        return entityF;
    }
}
