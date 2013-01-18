package ru.kwanza.dbtool.orm.impl.fetcher;

import ru.kwanza.dbtool.orm.annotations.*;

import java.util.Collection;

/**
 * @author Alexander Guzanov
 */
@Entity(name="TestEntityA", table = "test_entity_a")
public class TestEntityA {
    @IdField(column = "id")
    private Long id;
    @Field(column = "title")
    private String title;
    @VersionField(column = "version")
    private Long version;
    @OneToMany(relationClass = TestEntity.class,relationProperty = "entityAID")
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

    public Collection<TestEntity> getTestEntities() {
        return testEntities;
    }
}
