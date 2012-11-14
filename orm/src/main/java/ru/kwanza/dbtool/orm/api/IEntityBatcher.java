package ru.kwanza.dbtool.orm.api;

import ru.kwanza.dbtool.core.UpdateException;

import java.util.Collection;

/**
 * @author Alexander Guzanov
 */
public interface IEntityBatcher {

    void create(Object obj);

    <T> void create(Class<T> clazz, Collection obj);

    void update(Object obj) throws UpdateException;

    <T> void update(Class<T> clazz, Collection obj);

    void delete(Object obj) throws UpdateException;

    <T> void delete(Class<T> clazz, Collection obj);

    void deleteById(Class cls, Object key);

    void flush() throws EntityUpdateException;
}
