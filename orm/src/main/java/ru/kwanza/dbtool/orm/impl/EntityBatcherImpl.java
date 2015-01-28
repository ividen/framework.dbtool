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

import ru.kwanza.dbtool.core.UpdateException;
import ru.kwanza.dbtool.orm.api.EntityUpdateException;
import ru.kwanza.dbtool.orm.api.IEntityBatcher;
import ru.kwanza.dbtool.orm.api.IEntityManager;

import java.util.*;

/**
 * @author Kiryl Karatsetski
 */
public class EntityBatcherImpl implements IEntityBatcher {

    private IEntityManager entityManager;

    private Map<Class, Collection> createObjectStore = new HashMap<Class, Collection>();
    private Map<Class, Collection> updateObjectStore = new HashMap<Class, Collection>();
    private Map<Class, Collection> deleteObjectStore = new HashMap<Class, Collection>();
    private Map<Class, Collection> deleteKeyStore = new HashMap<Class, Collection>();

    public EntityBatcherImpl(IEntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public void create(Object object) {
        create(object.getClass(), Arrays.asList(object));
    }

    @SuppressWarnings("unchecked")
    public <T> void create(Class<T> entityClass, Collection objects) {
        Collection objectCollection = createObjectStore.get(entityClass);
        if (objectCollection == null) {
            objectCollection = new LinkedList();
            createObjectStore.put(entityClass, objectCollection);
        }
        objectCollection.addAll(objects);
    }

    public void update(Object object) {
        update(object.getClass(), Arrays.asList(object));
    }

    @SuppressWarnings("unchecked")
    public <T> void update(Class<T> entityClass, Collection objects) {
        Collection objectCollection = updateObjectStore.get(entityClass);
        if (objectCollection == null) {
            objectCollection = new LinkedList();
            updateObjectStore.put(entityClass, objectCollection);
        }
        objectCollection.addAll(objects);
    }

    public void delete(Object object) {
        delete(object.getClass(), Arrays.asList(object));
    }

    @SuppressWarnings("unchecked")
    public <T> void delete(Class<T> entityClass, Collection objects) {
        Collection objectCollection = deleteObjectStore.get(entityClass);
        if (objectCollection == null) {
            objectCollection = new LinkedList();
            deleteObjectStore.put(entityClass, objectCollection);
        }
        objectCollection.addAll(objects);
    }

    public void deleteByKey(Class entityClass, Object key) {
        deleteByKeys(entityClass, Arrays.asList(key));
    }

    @SuppressWarnings("unchecked")
    public void deleteByKeys(Class entityClass, Collection keys) {
        Collection objectCollection = deleteKeyStore.get(entityClass);
        if (objectCollection == null) {
            objectCollection = new LinkedList();
            deleteKeyStore.put(entityClass, objectCollection);
        }
        objectCollection.addAll(keys);
    }

    @SuppressWarnings("unchecked")
    public void flush() throws EntityUpdateException {

        final Map<Class, UpdateException> createExceptionMap = new HashMap<Class, UpdateException>();
        final Map<Class, UpdateException> updateExceptionMap = new HashMap<Class, UpdateException>();
        final Map<Class, UpdateException> deleteByObjectExceptionMap = new HashMap<Class, UpdateException>();
        final Map<Class, UpdateException> deleteByKeyExceptionMap = new HashMap<Class, UpdateException>();

        for (Map.Entry<Class, Collection> e : createObjectStore.entrySet()) {
            try {
                entityManager.create(e.getKey(), e.getValue());
            } catch (UpdateException ex) {
                createExceptionMap.put(e.getKey(), ex);
            }
        }

        for (Map.Entry<Class, Collection> e : updateObjectStore.entrySet()) {
            try {
                entityManager.update(e.getKey(), e.getValue());
            } catch (UpdateException ex) {
                updateExceptionMap.put(e.getKey(), ex);
            }
        }

        for (Map.Entry<Class, Collection> e : deleteObjectStore.entrySet()) {
            try {
                entityManager.delete(e.getKey(), e.getValue());
            } catch (UpdateException ex) {
                deleteByObjectExceptionMap.put(e.getKey(), ex);
            }
        }

        for (Map.Entry<Class, Collection> e : deleteKeyStore.entrySet()) {
            try {
                entityManager.deleteByKeys(e.getKey(), e.getValue());
            } catch (UpdateException ex) {
                deleteByKeyExceptionMap.put(e.getKey(), ex);
            }
        }

        createObjectStore.clear();
        updateObjectStore.clear();
        deleteObjectStore.clear();
        deleteKeyStore.clear();

        if (!createExceptionMap.isEmpty() || !updateExceptionMap.isEmpty() || !deleteByObjectExceptionMap.isEmpty()
                || !deleteByKeyExceptionMap.isEmpty()) {
            throw new EntityUpdateException(createExceptionMap, updateExceptionMap, deleteByObjectExceptionMap, deleteByKeyExceptionMap);
        }
    }
}
