package ru.kwanza.dbtool.orm;

import ru.kwanza.dbtool.orm.mapping.Entity;
import ru.kwanza.dbtool.orm.mapping.Field;

/**
 * @author Alexander Guzanov
 */
@Entity(name="Agent", tableName = "agent")
public class Agent {

    @Field
    private Long id;
    @Field
    private String name;
    @Field
    private String address;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
