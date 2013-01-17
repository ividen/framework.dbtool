package ru.kwanza.dbtool.orm.entity;

import ru.kwanza.dbtool.orm.annotations.*;

import java.sql.Types;

/**
 * @author Alexander Guzanov
 */
@Entity(name = "TestEntity1", table = "test_entity")
public class TestEntity1 extends OldAgent {

    @IdField(column = "id")
    private Long id;

    @Field(column = "name")
    private String name;

    @Field(column = "desc", type = Types.NVARCHAR)
    private String description;

    @VersionField(column = "version")
    private Long version = 0l;

    private Long counter;

    @ManyToOne(property = "agentId")
    private OldAgent oldAgent;

    public OldAgent getOldAgent() {
        return oldAgent;
    }

    public void setOldAgent(OldAgent oldAgent) {
        this.oldAgent = oldAgent;
    }

    @Field(column = "counter")
    public Long getCounter() {
        return counter;
    }

    @Field(column = "agent_id")
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
