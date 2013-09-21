package ru.kwanza.dbtool.orm.api;

/**
 * @author Alexander Guzanov
 */
public interface IQuery<T> {

    IStatement<T> prepare();
}
