package ru.kwanza.dbtool.orm.impl.fetcher;

import ru.kwanza.dbtool.orm.annotations.Entity;
import ru.kwanza.dbtool.orm.annotations.Field;
import ru.kwanza.dbtool.orm.annotations.IdField;
import ru.kwanza.dbtool.orm.annotations.VersionField;

/**
 * @author Alexander Guzanov
 */

@Entity(name = "TestEntityG", tableName = "test_entity_g")
public class TestEntityG {
    @IdField(columnName = "id")
    private Long id;
    @Field(columnName = "title")
    private String title;
    @VersionField(columnName = "version")
    private Long version;

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public Long getVersion() {
        return version;
    }
}