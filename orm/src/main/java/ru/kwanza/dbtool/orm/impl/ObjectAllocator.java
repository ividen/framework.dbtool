package ru.kwanza.dbtool.orm.impl;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author Alexander Guzanov
 */
public class ObjectAllocator {
    public static ObjectAllocator instance;

    static {
        try {
            instance = new ObjectAllocator();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Object unsafe;
    private Method allocateInstance;

    ObjectAllocator() throws NoSuchFieldException, IllegalAccessException, ClassNotFoundException, NoSuchMethodException {
        Class<?> unsafeClass = Class.forName("sun.misc.Unsafe");
        Field f = unsafeClass.getDeclaredField("theUnsafe");
        f.setAccessible(true);
        unsafe = f.get(null);
        allocateInstance = unsafeClass.getMethod("allocateInstance", Class.class);
    }

    public static <T> T newInstance(Class<T> clazz) throws InvocationTargetException, IllegalAccessException {
        return instance.newInstance0(clazz);
    }

    private <T> T newInstance0(Class<T> clazz) throws InvocationTargetException, IllegalAccessException {
        return (T) allocateInstance.invoke(unsafe, clazz);
    }
}
