package ru.kwanza.dbtool.orm.api;

/**
 * @author Alexander Guzanov
 */
public interface IFiltering<T> extends ISelectOperationProfiler<T> {

    IFiltering<T> setOffset(int offset);

    IFiltering<T> setMaxSize(int maxSize);

    IFiltering<T> filter(Filter ... filters);

    IFiltering<T> orderBy(OrderBy... orderBy);
}
