package ru.kwanza.dbtool.orm.impl.fetcher;

import ru.kwanza.dbtool.orm.annotations.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Alexander Guzanov
 */
@Entity(name = "TestEntityE", table = "test_entity_e")
public class TestEntityE   implements Serializable {
    @IdField( "id")
    private Long id;
    @Field( "title")
    private String title;
    @VersionField("version")
    private Long version;

    @Field( "entity_gid")
    private Long entityGID;

    @ManyToOne(property = "entityGID")
    private TestEntityG entityG;

    @OneToMany(relationProperty = "entityEID", relationClass = TestEntityC.class)
    private List<TestEntityC> entitiesC;

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

    public List<TestEntityC> getEntitiesC() {
        return entitiesC;
    }
}
