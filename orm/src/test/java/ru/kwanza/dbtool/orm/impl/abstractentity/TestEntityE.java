package ru.kwanza.dbtool.orm.impl.abstractentity;

import ru.kwanza.dbtool.orm.annotations.Entity;
import ru.kwanza.dbtool.orm.annotations.Field;

/**
 * @author Alexander Guzanov
 */
@Entity(name = "TestEntityE",table = "test_entity_e")
public class TestEntityE extends TestEntityD{
    @Field(column = "entity_gid")
    private Long entityGID;

    public Long getEntityGID() {
        return entityGID;
    }
}
