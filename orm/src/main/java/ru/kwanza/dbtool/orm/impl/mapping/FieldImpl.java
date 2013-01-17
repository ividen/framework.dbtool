package ru.kwanza.dbtool.orm.impl.mapping;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;

/**
 * @author Kiryl Karatsetski
 */
public class FieldImpl extends EntityField {

    private static final Logger log = LoggerFactory.getLogger(FieldImpl.class);

    private Field field;
    private String name;

    public static EntityField create(String name,Field field) {
        field.setAccessible(true);
        return new FieldImpl(name,field);
    }

    FieldImpl(String name,Field field) {
        this.field = field;
        this.name = name;
    }

    @Override
    public Object getValue(Object object) {
        try {
            return field.get(object);
        } catch (Exception e) {
            log.error("Error while getting value for object " + object, e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void setValue(Object object, Object value) {
        try {
            field.set(object, value);
        } catch (Exception e) {
            log.error("Error while setting value " + value + " for object " + object, e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public Class getType() {
        return field.getType();
    }

    @Override
    public String getName() {
        return name;
    }
}
