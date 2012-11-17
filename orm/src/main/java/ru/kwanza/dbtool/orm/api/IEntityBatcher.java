package ru.kwanza.dbtool.orm.api;

import java.util.Collection;

/**
 * @author Alexander Guzanov
 */
public interface IEntityBatcher {

    void create(Object object);

    <T> void create(Class<T> entityClass, Collection objects);

    void update(Object object);

    <T> void update(Class<T> entityClass, Collection objects);

    void delete(Object object);

    <T> void delete(Class<T> entityClass, Collection objects);

    void deleteByKey(Class entityClass, Object key);

    void deleteByKeys(Class entityClass, Collection keys);

    void flush() throws EntityUpdateException;
}
