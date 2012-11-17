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

    void deleteById(Class cls, Object key) throws UpdateException;

    <T> T readById(Class<T> cls, Object key);

    <T> IQueryBuilder<T> queryBuilder(Class<T> clazz);

    <T> IFiltering<T> filtering(Class<T> clazz);

    IEntityBatcher newBatcher();

    IFetcher getFetcher();
}
