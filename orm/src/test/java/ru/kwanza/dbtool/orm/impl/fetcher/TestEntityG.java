package ru.kwanza.dbtool.orm.impl.fetcher;

import ru.kwanza.dbtool.orm.annotations.*;

import java.io.Serializable;
import java.util.Collection;

/**
 * @author Alexander Guzanov
 */

@Entity(name = "TestEntityG", table = "test_entity_g")
public class TestEntityG   implements Serializable {
    @IdField( "id")
    private Long id;
    @Field( "title")
    private String title;
    @VersionField("version")
    private Long version;

    @OneToMany(relationClass = TestEntityE.class,relationProperty = "entityGID")
    private Collection<TestEntityE> entitiesE;

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public Long getVersion() {
        return version;
    }

    public Collection<TestEntityE> getEntitiesE() {
        return entitiesE;
    }
}
