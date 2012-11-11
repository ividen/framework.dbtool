package ru.kwanza.dbtool.orm;

import ru.kwanza.dbtool.UpdateException;

import java.util.Collection;
import java.util.List;

/**
 * @author Alexander Guzanov
 */
public interface IEntityManager{

    public void create(Object obj) throws UpdateException;

    public <T> void create(Class<T> clazz,Collection obj) throws UpdateException;

    public void update(Object obj) throws UpdateException;

    public <T> void update(Class<T> clazz,Collection obj) throws UpdateException;

    public void delete(Object obj) throws UpdateException;

    public <T> void delete(Class<T> clazz,Collection obj) throws UpdateException;

    public void deleteById(Class cls,Object key) throws UpdateException;

    public <T> T readById(Class<T> cls,Object key);

    public <T> IQueryBuilder<T> queryBuilder(Class<T> clazz);


    public IEntityBatcher newBatcher();
}
