package ru.kwanza.dbtool.orm.api.internal;

import ru.kwanza.toolbox.fieldhelper.Property;

/**
 * Информация о мэпинге поля
 *
 * @author Alexander Guzanov
 *
 * @see ru.kwanza.dbtool.orm.api.internal.IEntityType
 * @see ru.kwanza.dbtool.orm.api.internal.IEntityMappingRegistry
 */
public interface IFieldMapping {

    public String getColumn();

    public int getType();

    public String getName();

    public Property getProperty();
}
