package ru.kwanza.dbtool.orm.api;

import ru.kwanza.dbtool.core.UpdateException;

import java.util.Collection;

/**
 * @author Alexander Guzanov
 */
public interface IEntityManager {

    <T> T create(T obj) throws UpdateException;

    <T>  Collection<T> create(Class<T> clazz, Collection<T> obj) throws UpdateException;

    <T> T update(T obj) throws UpdateException;

    <T> Collection<T> update(Class<T> clazz, Collection<T> obj) throws UpdateException;

    <T> T delete(T obj) throws UpdateException;

    <T> Collection<T> delete(Class<T> clazz, Collection<T> obj) throws UpdateException;

    void deleteByKey(Class entityClass, Object key) throws UpdateException;

    void deleteByKey(Class entityClass, Collection keys) throws UpdateException;

    <T> T readByKey(Class<T> entityClass, Object key);

    <T> IQueryBuilder<T> queryBuilder(Class<T> entityClass);

    <T> IFiltering<T> filtering(Class<T> entityClass);

    IEntityBatcher createEntityBatcher();

    IFetcher getFetcher();
}
