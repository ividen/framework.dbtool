package ru.kwanza.dbtool.orm.api;

/**
 * @author Alexander Guzanov
 */
public interface IStatement<T> extends ISelectOperationProvider<T> {

    IStatement<T> setParameter(int index, Object value);

    IStatement<T> setParameter(String name, Object value);
}
