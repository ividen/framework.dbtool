package ru.kwanza.dbtool.orm.impl.fetcher;

import ru.kwanza.dbtool.orm.annotations.Entity;
import ru.kwanza.dbtool.orm.annotations.Field;
import ru.kwanza.dbtool.orm.annotations.IdField;
import ru.kwanza.dbtool.orm.annotations.VersionField;

import java.io.Serializable;

/**
 * @author Alexander Guzanov
 */

@Entity(name="TestEntityF", table = "test_entity_f")
public class TestEntityF   implements Serializable {
    @IdField( "id")
    private Long id;
    @Field( "title")
    private String title;
    @VersionField( "version")
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
