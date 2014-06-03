package ru.kwanza.dbtool.orm.impl.abstractentity;

import ru.kwanza.dbtool.orm.annotations.AbstractEntity;
import ru.kwanza.dbtool.orm.annotations.Field;

/**
 * @author Alexander Guzanov
 */

@AbstractEntity()
public class TestEntityD extends AbstractTestEntity {
    @Field( "title")
    private String title;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
