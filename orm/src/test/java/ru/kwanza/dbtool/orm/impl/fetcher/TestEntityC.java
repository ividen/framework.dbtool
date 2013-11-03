package ru.kwanza.dbtool.orm.impl.fetcher;

import ru.kwanza.dbtool.orm.annotations.*;

import java.io.Serializable;
import java.util.Collection;

/**
 * @author Alexander Guzanov
 */
@Entity(name = "TestEntityC", table = "test_entity_c")
public class TestEntityC implements Serializable {
    @IdField("id")
    private Long id;
    @Field("title")
    private String title;
    @VersionField("version")
    private Long version;

    @Field("entity_eid")
    private Long entityEID;
    @Field("entity_fid")
    private Long entityFID;

    @ManyToOne(property = "entityEID")
    private TestEntityE entityE;
    @ManyToOne(property = "entityFID")
    private TestEntityF entityF;

    @OneToMany(relationClass = TestEntity.class, relationProperty = "entityCID")
    private Collection<TestEntity> testEntities;

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

    public Collection<TestEntity> getTestEntities() {
        return testEntities;
    }
}
