package ru.kwanza.dbtool.orm.impl.mapping.entities;

import ru.kwanza.dbtool.orm.annotations.Entity;
import ru.kwanza.dbtool.orm.annotations.Field;

/**
 * @author Kiryl Karatsetski
 */
@Entity(name = "Agent", table = "agent")
public class Agent extends AbstractEntity {

    @Field( "name")
    private String name;

    public Agent() {
    }

    public Agent(Long id, String pcid, String name) {
        super(id, pcid);
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
