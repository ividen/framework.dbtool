package ru.kwanza.dbtool.orm;

import ru.kwanza.dbtool.UpdateException;

import java.util.Collection;

/**
 * @author Alexander Guzanov
 */
public interface IEntityBatcher {
    public void create(Object obj);

    public <T> void create(Class<T> clazz, Collection obj);

    public void update(Object obj) throws UpdateException;

    public <T> void update(Class<T> clazz, Collection obj);

    public void delete(Object obj) throws UpdateException;

    public <T> void delete(Class<T> clazz, Collection obj);

    public void deleteById(Class cls, Object key);

    public void flush() throws EntityUpdateException;
}
