package ru.kwanza.dbtool.orm.mapping.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.kwanza.dbtool.orm.mapping.EntityField;

import java.lang.reflect.Method;

/**
 * @author Kiryl Karatsetski
 */
public class MethodImpl extends EntityField {

    private static final Logger log = LoggerFactory.getLogger(MethodImpl.class);

    private Method getMethod;

    private Method setMethod;

    public static EntityField create(Method getMethod, Method setMethod) {
        return new MethodImpl(getMethod, setMethod);
    }

    MethodImpl(Method getMethod, Method setMethod) {
        this.getMethod = getMethod;
        this.setMethod = setMethod;
    }

    @Override
    public Object getValue(Object object) {
        try {
            return getMethod.invoke(object);
        } catch (Exception e) {
            //TODO KK: throw RuntimeException
            log.error("Error while getting value for object " + object, e);
            return null;
        }
    }

    @Override
    public void setValue(Object object, Object value) {
        try {
            setMethod.invoke(object, value);
        } catch (Exception e) {
            //TODO KK: throw RuntimeException
            log.error("Error while setting value " + value + " for object " + object, e);
        }
    }

    @Override
    public Class getType() {
        return null;
    }
}
