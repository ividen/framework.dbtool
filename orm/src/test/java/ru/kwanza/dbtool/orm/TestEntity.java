package ru.kwanza.dbtool.orm;

import ru.kwanza.dbtool.orm.mapping.Entity;
import ru.kwanza.dbtool.orm.mapping.Field;
import ru.kwanza.dbtool.orm.mapping.VersionField;

import java.sql.Types;

/**
 * @author Alexander Guzanov
 */
@Entity(name = "TestEntity", tableName = "test_entity")
public class TestEntity {

    @Field(columnName = "id")
    private Long id;

    @Field
    private String name;

    @Field(columnName = "desc", type = Types.NVARCHAR)
    private String description;

    @VersionField
    private Long version = 0l;

    private Long counter;

    @Field(columnName = "agent_id")
    private Long agentId;

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
