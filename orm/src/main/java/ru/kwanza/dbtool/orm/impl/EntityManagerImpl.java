package ru.kwanza.dbtool.orm.impl;

import ru.kwanza.dbtool.core.DBTool;
import ru.kwanza.dbtool.core.UpdateException;
import ru.kwanza.dbtool.core.VersionGenerator;
import ru.kwanza.dbtool.orm.api.IEntityBatcher;
import ru.kwanza.dbtool.orm.api.IEntityManager;
import ru.kwanza.dbtool.orm.api.IFiltering;
import ru.kwanza.dbtool.orm.api.IQueryBuilder;
import ru.kwanza.dbtool.orm.api.internal.IFieldMapping;
import ru.kwanza.dbtool.orm.impl.fetcher.Fetcher;
import ru.kwanza.dbtool.orm.impl.filtering.FilteringImpl;
import ru.kwanza.dbtool.orm.api.internal.IEntityMappingRegistry;
import ru.kwanza.dbtool.orm.impl.operation.OperationFactory;
import ru.kwanza.dbtool.orm.impl.querybuilder.QueryBuilderFactory;
import ru.kwanza.toolbox.SpringSerializable;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author Kiryl Karatsetski
 */
public class EntityManagerImpl extends SpringSerializable implements IEntityManager {

    @Resource(name = "dbtool.DBTool")
    private DBTool dbTool;

    @Resource(name = "dbtool.VersionGenerator")
    private VersionGenerator versionGenerator;

    @Resource(name = "dbtool.IEntityMappingRegistry")
    private IEntityMappingRegistry mappingRegistry;

    @Resource(name = "dbtool.OperationFactory")
    private OperationFactory operationFactory;

    @Resource(name = "dbtool.Fetcher")
    private Fetcher fetcher;

    public <T> T create(T object) throws UpdateException {
        final Class entityClass = object.getClass();
        operationFactory.getCreateOperation(entityClass).executeCreate(object);
        return object;
    }

    public <T> Collection<T> create(Class<T> entityClass, Collection<T> objects) throws UpdateException {
        operationFactory.getCreateOperation(entityClass).executeCreate(objects);
        return objects;
    }

    public <T> T update(T object) throws UpdateException {
        final Class entityClass = object.getClass();
        operationFactory.getUpdateOperation(entityClass, versionGenerator).executeUpdate(object);
        return object;
    }

    public <T> Collection<T> update(Class<T> entityClass, Collection<T> objects) throws UpdateException {
        operationFactory.getUpdateOperation(entityClass, versionGenerator).executeUpdate(objects);
        return objects;
    }

    public <T> T delete(T object) throws UpdateException {
        final Class entityClass = object.getClass();
        operationFactory.getDeleteOperation(entityClass).executeDelete(object);
        return object;
    }

    public <T> Collection<T> delete(Class<T> entityClass, Collection<T> objects) throws UpdateException {
        operationFactory.getDeleteOperation(entityClass).executeDelete(objects);
        return objects;
    }

    public void deleteByKey(Class entityClass, Object key) throws UpdateException {
        operationFactory.getDeleteOperation(entityClass).executeDeleteByKey(key);
    }

    public void deleteByKeys(Class entityClass, Collection keys) throws UpdateException {
        operationFactory.getDeleteOperation(entityClass).executeDeleteByKeys(keys);
    }

    @SuppressWarnings("unchecked")
    public <T> T readByKey(Class<T> entityClass, Object key) {
        return (T) operationFactory.getReadOperation(entityClass).selectByKey(key);
    }

    @SuppressWarnings("unchecked")
    public <T> Collection<T> readByKeys(Class<T> entityClass, Collection keys) {
        return (Collection<T>) operationFactory.getReadOperation(entityClass).selectByKeys(keys);
    }

    @SuppressWarnings("unchecked")
    public <F, T> Map<F, T> readMapByKeys(Class<T> entityClass, Collection keys, String propertyName) {
        return (Map<F, T>) operationFactory.getReadOperation(entityClass).selectMapByKeys(keys, propertyName);
    }

    @SuppressWarnings("unchecked")
    public <F, T> Map<F, T> readMapByKeys(Class<T> entityClass, Collection keys) {
        final IFieldMapping idFieldMapping = mappingRegistry.getEntityType(entityClass).getIdField();
        if (idFieldMapping == null) {
            throw new RuntimeException("IdFieldMapping for entity class" + entityClass + " not found");
        }

        return (Map<F, T>) operationFactory.getReadOperation(entityClass).selectMapByKeys(keys, idFieldMapping.getName());
    }

    @SuppressWarnings("unchecked")
    public <F, T> Map<F, List<T>> readMapListByKeys(Class<T> entityClass, Collection keys, String propertyName) {
        return (Map<F, List<T>>) operationFactory.getReadOperation(entityClass).selectMapListByKeys(keys, propertyName);
    }

    public <T> IQueryBuilder<T> queryBuilder(Class<T> entityClass) {
        return QueryBuilderFactory.createBuilder(dbTool, mappingRegistry, entityClass);
    }

    public <T> IFiltering<T> filtering(Class<T> entityClass) {
        return new FilteringImpl<T>(this, entityClass);
    }

    public IEntityBatcher createEntityBatcher() {
        return new EntityBatcherImpl(this);
    }

    public <T> void fetch(Class<T> entityClass, Collection<T> items, String relationPath) {
        fetcher.fetch(entityClass, items, relationPath);
    }

    public <T> void fetch(T object, String relationPath) {
        fetcher.fetch(object, relationPath);
    }

    public <T> void fetchLazy(Class<T> entityClass, Collection<T> items) {
        fetcher.fetchLazy(entityClass, items);
    }

    public <T> void fetchLazy(T object) {
        fetcher.fetchLazy(object);
    }

}
