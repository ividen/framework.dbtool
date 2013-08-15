package ru.kwanza.dbtool.orm.api;

/**
 * @author Alexander Guzanov
 */
public interface IQuery<T>{

    IStatement<T> prepare();

    IStatement<T> prepare(int offset, int maxSize);
}
