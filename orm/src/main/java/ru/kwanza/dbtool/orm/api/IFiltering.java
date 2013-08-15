package ru.kwanza.dbtool.orm.api;

/**
 * @author Alexander Guzanov
 */
public interface IFiltering<T> extends ISelectOperationProvider<T> {

    IFiltering<T> paging(Integer offset, Integer maxSize);

    IFiltering<T> filter(Filter... filters);

    IFiltering<T> orderBy(OrderBy... orderBy);
}
