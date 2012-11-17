package ru.kwanza.dbtool.orm.impl;

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

        for (Class entityClass : createObjectStore.keySet()) {
            try {
                entityManager.create(entityClass, createObjectStore.get(entityClass));
            } catch (UpdateException e) {
                createExceptionMap.put(entityClass, e);
            }
        }

        for (Class entityClass : updateObjectStore.keySet()) {
            try {
                entityManager.update(entityClass, updateObjectStore.get(entityClass));
            } catch (UpdateException e) {
                updateExceptionMap.put(entityClass, e);
            }
        }

        for (Class entityClass : deleteObjectStore.keySet()) {
            try {
                entityManager.delete(entityClass, deleteObjectStore.get(entityClass));
            } catch (UpdateException e) {
                deleteByObjectExceptionMap.put(entityClass, e);
            }
        }

        for (Class entityClass : deleteObjectStore.keySet()) {
            try {
                entityManager.deleteByKey(entityClass, deleteObjectStore.get(entityClass));
            } catch (UpdateException e) {
                deleteByKeyExceptionMap.put(entityClass, e);
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
