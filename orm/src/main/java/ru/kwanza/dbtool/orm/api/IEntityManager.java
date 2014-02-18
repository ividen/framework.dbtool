package ru.kwanza.dbtool.orm.api;

import ru.kwanza.dbtool.core.UpdateException;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author Alexander Guzanov
 */
public interface IEntityManager {

    <T> T create(T object) throws UpdateException;

    <T> Collection<T> create(Class<T> entityClass, Collection<T> objects) throws UpdateException;

    <T> T update(T object) throws UpdateException;

    <T> Collection<T> update(Class<T> entityClass, Collection<T> objects) throws UpdateException;

    <T> T delete(T object) throws UpdateException;

    <T> Collection<T> delete(Class<T> entityClass, Collection<T> objects) throws UpdateException;

    void deleteByKey(Class entityClass, Object key) throws UpdateException;

    void deleteByKeys(Class entityClass, Collection keys) throws UpdateException;

    <T> T readByKey(Class<T> entityClass, Object key);

    <T> Collection<T> readByKeys(Class<T> entityClass, Collection keys);

    <F, T> Map<F, T> readMapByKeys(Class<T> entityClass, Collection keys, String propertyName);

    <F, T> Map<F, T> readMapByKeys(Class<T> entityClass, Collection keys);

    <F, T> Map<F, List<T>> readMapListByKeys(Class<T> entityClass, Collection keys, String propertyName);

    <T> IQueryBuilder<T> queryBuilder(Class<T> entityClass);

    <T> IFiltering<T> filtering(Class<T> entityClass);

    IEntityBatcher createEntityBatcher();

    <T> void fetch(Class<T> entityClass, Collection<T> items, String relationPath);

    <T> void fetch(T object, String relationPath);

    <T> void fetchLazy(Class<T> entityClass, Collection<T> items);

    <T> void fetchLazy(T object);

    boolean isProxy(Object object);

    <T> T unwrapProxy(T object);

    boolean isNull(Object object);

    <T> LockResult<T> lockOpt(LockType type, Collection<T> items);
}