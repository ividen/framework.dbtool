package ru.kwanza.dbtool.orm;

import ru.kwanza.dbtool.orm.mapping.Entity;
import ru.kwanza.dbtool.orm.mapping.Field;

/**
 * @author Alexander Guzanov
 */
@Entity
public class TestEntityWithBlob extends TestEntity {

    @Field(columnName = "entity_body")
    private byte[] body;

    public byte[] getBody() {
        return body;
    }
}
