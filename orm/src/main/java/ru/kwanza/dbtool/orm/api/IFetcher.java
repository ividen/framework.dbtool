package ru.kwanza.dbtool.orm.api;

import java.util.Collection;

/**
 * @author Alexander Guzanov
 */
public interface IFetcher {

    public <T> void fetch(Class<T> entityClass, Collection<T> items, String relationPath);

    public <T> void fetch(T object, String relationPath);

}
