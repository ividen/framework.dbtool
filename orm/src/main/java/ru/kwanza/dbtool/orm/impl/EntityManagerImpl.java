package ru.kwanza.dbtool.orm.impl;

import ru.kwanza.dbtool.core.DBTool;
import ru.kwanza.dbtool.core.UpdateException;
import ru.kwanza.dbtool.orm.api.*;
import ru.kwanza.dbtool.orm.impl.fetcher.FetcherImpl;
import ru.kwanza.dbtool.orm.impl.filtering.FilteringImpl;
import ru.kwanza.dbtool.orm.impl.mapping.IEntityMappingRegistry;
import ru.kwanza.dbtool.orm.impl.querybuilder.QueryBuilderImpl;

import java.util.Collection;

/**
 * @author Kiryl Karatsetski
 */
public class EntityManagerImpl implements IEntityManager {

    private IEntityMappingRegistry mappingRegistry;
    private IFetcher fetcher;

    private DBTool dbTool;

    public <T> T create(T obj) throws UpdateException {
        return obj;
    }

    public <T> Collection<T> create(Class<T> clazz, Collection<T> obj) throws UpdateException {
        return obj;
    }

    public <T> T update(T obj) throws UpdateException {
        return null;
    }

    public <T> Collection<T> update(Class<T> clazz, Collection<T> obj) throws UpdateException {
        return obj;
    }

    public <T> T delete(T obj) throws UpdateException {
        return obj;
    }

    public <T> Collection<T> delete(Class<T> clazz, Collection<T> obj) throws UpdateException {
        return obj;
    }

    public void deleteById(Class cls, Object key) throws UpdateException {
    }

    public <T> T readById(Class<T> cls, Object key) {
        return null;
    }

    public <T> IQueryBuilder<T> queryBuilder(Class<T> clazz) {
        return new QueryBuilderImpl<T>(dbTool, mappingRegistry, clazz);
    }

    public <T> IFiltering filtering(Class<T> clazz) {
        return new FilteringImpl(this, clazz);
    }

    public void init() {
        this.fetcher = new FetcherImpl(mappingRegistry, this);
    }

    public IEntityBatcher newBatcher() {
        return null;
    }

    public IFetcher getFetcher() {
        return fetcher;
    }

    public void setMappingRegistry(IEntityMappingRegistry mappingRegistry) {
        this.mappingRegistry = mappingRegistry;
    }

    public void setDbTool(DBTool dbTool) {
        this.dbTool = dbTool;
    }


}
