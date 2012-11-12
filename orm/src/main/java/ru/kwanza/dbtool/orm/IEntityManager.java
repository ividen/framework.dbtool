package ru.kwanza.dbtool.orm;

import ru.kwanza.dbtool.core.UpdateException;

import java.util.Collection;

/**
 * @author Alexander Guzanov
 */
public interface IEntityManager {

    void create(Object obj) throws UpdateException;

    <T> void create(Class<T> clazz, Collection obj) throws UpdateException;

    void update(Object obj) throws UpdateException;

    <T> void update(Class<T> clazz, Collection obj) throws UpdateException;

    void delete(Object obj) throws UpdateException;

    <T> void delete(Class<T> clazz, Collection obj) throws UpdateException;

    void deleteById(Class cls, Object key) throws UpdateException;

    <T> T readById(Class<T> cls, Object key);

    <T> IQueryBuilder<T> queryBuilder(Class<T> clazz);

    IEntityBatcher newBatcher();
}
