package ru.kwanza.dbtool.orm.impl.mapping;

import ru.kwanza.dbtool.orm.api.internal.IFieldMapping;

/**
 * @author Alexander Guzanov
 */
public abstract class AbstractFieldMapping implements IFieldMapping {
    private Integer id;

    public Integer getId() {
        return id;
    }

    void setId(int id) {
        if (this.id != null) {
            throw new IllegalStateException("Field  name=" + this.getName()
                    + ", column=" + this.getName() + " already belongs to other entity!");
        }
        this.id = id;
    }
}
