package ru.kwanza.dbtool.orm.impl.mapping.entities;

import ru.kwanza.dbtool.orm.annotations.Entity;
import ru.kwanza.dbtool.orm.annotations.Field;

/**
 * @author Kiryl Karatsetski
 */
@Entity(name = "Agent", tableName = "agent")
public class Agent extends AbstractEntity {

    @Field(columnName = "name")
    private String name;

    public Agent(Long id, String pcid, String name) {
        super(id, pcid);
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
