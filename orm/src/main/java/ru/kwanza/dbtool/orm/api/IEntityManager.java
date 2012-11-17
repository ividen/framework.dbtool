package ru.kwanza.dbtool.orm.api;

import ru.kwanza.dbtool.core.UpdateException;

import java.util.Collection;

/**
 * @author Alexander Guzanov
 */
public interface IEntityManager {

    void create(Object object) throws UpdateException;

    <T> void create(Class<T> entityClass, Collection objects) throws UpdateException;

    void update(Object object) throws UpdateException;

    <T> void update(Class<T> entityClass, Collection objects) throws UpdateException;

    void delete(Object object) throws UpdateException;

    <T> void delete(Class<T> entityClass, Collection objects) throws UpdateException;

    void deleteByKey(Class entityClass, Object key) throws UpdateException;

    void deleteByKey(Class entityClass, Collection keys) throws UpdateException;

    <T> T readByKey(Class<T> entityClass, Object key);

    <T> IQueryBuilder<T> queryBuilder(Class<T> entityClass);

    <T> IFiltering<T> filtering(Class<T> entityClass);

    IEntityBatcher createEntityBatcher();

    IFetcher getFetcher();
}
