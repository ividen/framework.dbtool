package ru.kwanza.dbtool.orm.api;

/**
 * @author Alexander Guzanov
 */
public interface IFiltering<T> extends ISelectOperationProvider<T> {

    IFiltering<T> setOffset(Integer offset);

    IFiltering<T> setMaxSize(Integer maxSize);

    IFiltering<T> filter(Filter ... filters);

    IFiltering<T> orderBy(OrderBy... orderBy);
}
