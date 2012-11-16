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
    private Long entityEID;
    @Field(columnName = "entity_fid")
    private Long entityFID;


    @Fetch(propertyName = "entityEID")
    private TestEntityE entityE;
    @Fetch(propertyName = "entityFID")
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
