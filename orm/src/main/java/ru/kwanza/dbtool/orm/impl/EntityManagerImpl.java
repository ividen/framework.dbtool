package ru.kwanza.dbtool.orm.impl;

import ru.kwanza.dbtool.core.DBTool;
import ru.kwanza.dbtool.core.UpdateException;
import ru.kwanza.dbtool.core.VersionGenerator;
import ru.kwanza.dbtool.orm.api.*;
import ru.kwanza.dbtool.orm.impl.fetcher.FetcherImpl;
import ru.kwanza.dbtool.orm.impl.filtering.FilteringImpl;
import ru.kwanza.dbtool.orm.impl.mapping.FieldMapping;
import ru.kwanza.dbtool.orm.impl.mapping.IEntityMappingRegistry;
import ru.kwanza.dbtool.orm.impl.operation.OperationFactory;
import ru.kwanza.dbtool.orm.impl.querybuilder.QueryBuilderFactory;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author Kiryl Karatsetski
 */
public class EntityManagerImpl implements IEntityManager {

    private DBTool dbTool;

    private VersionGenerator versionGenerator;

    private IEntityMappingRegistry mappingRegistry;

    private OperationFactory operationFactory;

    private IFetcher fetcher;

    public void init() {
        this.operationFactory = new OperationFactory(mappingRegistry, dbTool);
        this.fetcher = new FetcherImpl(mappingRegistry, this);
    }

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
        Collection<FieldMapping> idFieldMappings = mappingRegistry.getIdFields(entityClass);
        if (idFieldMappings == null || idFieldMappings.isEmpty()) {
            throw new RuntimeException("IdFieldMapping for entity class" + entityClass + " not found");
        }

        String idField = idFieldMappings.iterator().next().getName();

        return (Map<F, T>) operationFactory.getReadOperation(entityClass).selectMapByKeys(keys, idField);
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

    public IFetcher getFetcher() {
        return fetcher;
    }

    public void setDbTool(DBTool dbTool) {
        this.dbTool = dbTool;
    }

    public void setVersionGenerator(VersionGenerator versionGenerator) {
        this.versionGenerator = versionGenerator;
    }

    public void setMappingRegistry(IEntityMappingRegistry mappingRegistry) {
        this.mappingRegistry = mappingRegistry;
    }
}
