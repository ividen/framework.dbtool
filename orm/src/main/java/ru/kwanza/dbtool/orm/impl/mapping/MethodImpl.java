package ru.kwanza.dbtool.orm.impl.mapping;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;

/**
 * @author Kiryl Karatsetski
 */
public class MethodImpl extends EntityField {

    private static final Logger log = LoggerFactory.getLogger(MethodImpl.class);

    private Method getter;
    private Method setter;
    private String name;

    public static EntityField create(String name,Method getMethod, Method setMethod) {
        return new MethodImpl(name,getMethod, setMethod);
    }

    MethodImpl(String name, Method getter, Method setter) {
        this.getter = getter;
        this.setter = setter;
        this.name = name;
    }

    @Override
    public Object getValue(Object object) {
        try {
            return getter.invoke(object);
        } catch (Exception e) {
            log.error("Error while getting value for object " + object, e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void setValue(Object object, Object value) {
        try {
            setter.invoke(object, value);
        } catch (Exception e) {
            log.error("Error while setting value " + value + " for object " + object, e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public Class getType() {
        return getter.getReturnType();
    }

    @Override
    public String getName() {
        return name;
    }
}
