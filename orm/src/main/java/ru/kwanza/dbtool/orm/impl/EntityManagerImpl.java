package ru.kwanza.dbtool.orm.impl;

import ru.kwanza.dbtool.core.DBTool;
import ru.kwanza.dbtool.core.UpdateException;
import ru.kwanza.dbtool.core.VersionGenerator;
import ru.kwanza.dbtool.orm.api.*;
import ru.kwanza.dbtool.orm.impl.fetcher.FetcherImpl;
import ru.kwanza.dbtool.orm.impl.filtering.FilteringImpl;
import ru.kwanza.dbtool.orm.impl.mapping.IEntityMappingRegistry;
import ru.kwanza.dbtool.orm.impl.operation.OperationFactory;
import ru.kwanza.dbtool.orm.impl.querybuilder.QueryBuilderImpl;

import java.util.Collection;

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
        this.operationFactory = new OperationFactory(mappingRegistry, dbTool.getJdbcTemplate());
        this.fetcher = new FetcherImpl(mappingRegistry, this);
    }

    public void create(Object object) throws UpdateException {
        final Class entityClass = object.getClass();
        operationFactory.getCreateOperation(entityClass).execute(object);
    }

    public <T> void create(Class<T> entityClass, Collection objects) throws UpdateException {
        operationFactory.getCreateOperation(entityClass).execute(objects);
    }

    public void update(Object object) throws UpdateException {
        final Class entityClass = object.getClass();
        operationFactory.getUpdateOperation(entityClass, versionGenerator).execute(object);
    }

    public <T> void update(Class<T> entityClass, Collection objects) throws UpdateException {
        operationFactory.getUpdateOperation(entityClass, versionGenerator).execute(objects);
    }

    public void delete(Object object) throws UpdateException {
        final Class entityClass = object.getClass();
        operationFactory.getDeleteOperation(entityClass).execute(object);
    }

    public <T> void delete(Class<T> entityClass, Collection objects) throws UpdateException {
        operationFactory.getDeleteOperation(entityClass).execute(objects);
    }

    public void deleteByKey(Class entityClass, Object key) throws UpdateException {
        operationFactory.getDeleteOperation(entityClass).executeByKey(key);
    }

    public void deleteByKey(Class entityClass, Collection keys) throws UpdateException {
        operationFactory.getDeleteOperation(entityClass).executeByKeys(keys);
    }

    public <T> T readByKey(Class<T> entityClass, Object key) {
        //TODO kkaratsetski: Кеширование билдера
        //TODO Under construction
        final IQuery<T> query = new QueryBuilderImpl<T>(dbTool, mappingRegistry, entityClass).where(Condition.isEqual("id")).create();
        return query.select();
    }

    public <T> IQueryBuilder<T> queryBuilder(Class<T> entityClass) {
        return new QueryBuilderImpl<T>(dbTool, mappingRegistry, entityClass);
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
