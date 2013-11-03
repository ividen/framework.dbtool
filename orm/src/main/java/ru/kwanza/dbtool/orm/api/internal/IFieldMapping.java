package ru.kwanza.dbtool.orm.api.internal;

import ru.kwanza.toolbox.fieldhelper.Property;

/**
 * @author Alexander Guzanov
 */
public interface IFieldMapping {

    public String getColumn();

    public int getType();

    public String getName();

    public Property getProperty();
}
