package ru.kwanza.dbtool.orm.entity;

import ru.kwanza.dbtool.orm.annotations.Entity;
import ru.kwanza.dbtool.orm.annotations.Field;

/**
 * @author Alexander Guzanov
 */
@Entity(name = "TestEntityWithBlob", table = "test_entity")
public class TestEntityWithBlob extends TestEntity1 {

    @Field(column = "entity_body")
    private byte[] body;

    public byte[] getBody() {
        return body;
    }
}
