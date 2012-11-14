package ru.kwanza.dbtool.orm.impl.fetcher;

import ru.kwanza.dbtool.orm.annotations.*;

/**
 * @author Alexander Guzanov
 */
@Entity(name = "TestEntityE", tableName = "test_entity_e")
public class TestEntityE {
    @IdField(columnName = "id")
    private Long id;
    @Field(columnName = "title")
    private String title;
    @VersionField(columnName = "version")
    private Long version;

    @Field(columnName = "entity_gid")
    private Long entityGID;

    @Fetch(fieldName = "entityGID")
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
