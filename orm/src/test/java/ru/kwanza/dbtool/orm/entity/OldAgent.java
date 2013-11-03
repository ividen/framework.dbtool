package ru.kwanza.dbtool.orm.entity;

import ru.kwanza.dbtool.orm.annotations.Entity;
import ru.kwanza.dbtool.orm.annotations.Field;
import ru.kwanza.dbtool.orm.annotations.IdField;

/**
 * @author Alexander Guzanov
 */
@Entity(name = "OldAgent", table = "agent")
public class OldAgent {

    @IdField("id1")
    private Long id1;

    @Field("name1")
    private String name1;

    @Field("address")
    private String address;

//    public Long getId() {
//        return id;
//    }
//
//    public void setId(Long id) {
//        this.id = id;
//    }

    public String getName1() {
        return name1;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
