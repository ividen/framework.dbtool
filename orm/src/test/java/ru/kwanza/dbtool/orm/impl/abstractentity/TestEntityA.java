package ru.kwanza.dbtool.orm.impl.abstractentity;

import ru.kwanza.dbtool.orm.annotations.Entity;
import ru.kwanza.dbtool.orm.annotations.Field;

/**
 * @author Alexander Guzanov
 */
@Entity(name="TestEntityA", table = "test_entity_a")
public class TestEntityA extends AbstractTestEntity {
    @Field( "title")
    private String title;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
