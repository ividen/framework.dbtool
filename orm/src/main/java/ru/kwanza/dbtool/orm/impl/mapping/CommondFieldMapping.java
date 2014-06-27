package ru.kwanza.dbtool.orm.impl.mapping;

import ru.kwanza.dbtool.orm.api.internal.IFieldMapping;
import ru.kwanza.toolbox.fieldhelper.Property;

/**
 * @author Alexander Guzanov
 */
public class CommondFieldMapping  extends AbstractFieldMapping{
    private IFieldMapping original;

    public CommondFieldMapping(IFieldMapping original) {
        this.original = original;
    }

    public String getColumn() {
        return original.getColumn();
    }

    public int getType() {
        return original.getType();
    }

    public String getName() {
        return original.getName();
    }

    public Property getProperty() {
        return original.getProperty();
    }
}
