package ru.kwanza.dbtool.orm.api;

/**
 * @author Alexander Guzanov
 */
public interface IQuery<T> extends ISelectOperationProvider<T>{
    IQuery<T> setParameter(int index, Object value);
}
