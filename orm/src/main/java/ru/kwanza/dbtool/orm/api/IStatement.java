package ru.kwanza.dbtool.orm.api;

/**
 * @author Alexander Guzanov
 */
public interface IStatement<T> extends ISelectOperationProvider<T> {

    IStatement<T>  paging(int offset, int maxSize);

    IStatement<T> setParameter(int index, Object value);

    IStatement<T> setParameter(String name, Object value);
}
