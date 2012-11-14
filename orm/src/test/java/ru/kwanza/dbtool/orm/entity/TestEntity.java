package ru.kwanza.dbtool.orm.entity;

import ru.kwanza.dbtool.orm.annotations.*;

import java.sql.Types;

/**
 * @author Alexander Guzanov
 */
@Entity(name = "TestEntity", tableName = "test_entity")
public class TestEntity {

    @IdField(columnName = "id")
    private Long id;

    @Field(columnName = "name")
    private String name;

    @Field(columnName = "desc", type = Types.NVARCHAR)
    private String description;

    @VersionField(columnName = "version")
    private Long version = 0l;

    private Long counter;

    @Field(columnName = "agent_id")
    private Long agentId;

    @Fetch(propertyName = "agentId")
    private Agent agent;

    public Agent getAgent() {
        return agent;
    }

    @Field(columnName = "counter")
    public Long getCounter() {
        return counter;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setCounter(Long counter) {
        this.counter = counter;
    }
}
