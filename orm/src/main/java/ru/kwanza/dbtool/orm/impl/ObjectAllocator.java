package ru.kwanza.dbtool.orm.impl;

/*
 * #%L
 * dbtool-orm
 * %%
 * Copyright (C) 2015 Kwanza
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

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
