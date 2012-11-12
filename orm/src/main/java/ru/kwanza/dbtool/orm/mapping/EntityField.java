package ru.kwanza.dbtool.orm.mapping;

/**
 * @author Kiryl Karatsetski
 */
public abstract class EntityField {

    public abstract Object getValue(Object object);

    public abstract void setValue(Object object, Object value);
}
