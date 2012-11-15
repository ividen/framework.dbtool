package ru.kwanza.dbtool.orm.entity;

import ru.kwanza.dbtool.orm.annotations.*;

import java.sql.Types;

/**
 * @author Alexander Guzanov
 */
@Entity(name = "TestEntity1", tableName = "test_entity")
public class TestEntity1 extends Agent {

    @IdField(columnName = "id")
    private Long id;

    @Field(columnName = "name")
    private String name;

    @Field(columnName = "desc", type = Types.NVARCHAR)
    private String description;

    @VersionField(columnName = "version")
    private Long version = 0l;

    private Long counter;

    @Fetch(propertyName = "agentId")
    private Agent agent;

    public Agent getAgent() {
        return agent;
    }

    public void setAgent(Agent agent) {
        this.agent = agent;
    }

    @Field(columnName = "counter")
    public Long getCounter() {
        return counter;
    }

    @Field(columnName = "agent_id")
    private Long agentId;

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
