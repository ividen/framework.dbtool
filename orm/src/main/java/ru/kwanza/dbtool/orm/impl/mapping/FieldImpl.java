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


    public static EntityField create(Field field) {
        field.setAccessible(true);
        return new FieldImpl(field);
    }

    FieldImpl(Field field) {
        this.field = field;
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
}
