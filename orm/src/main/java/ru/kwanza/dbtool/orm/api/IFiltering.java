package ru.kwanza.dbtool.orm.api;

/**
 * @author Alexander Guzanov
 */
public interface IFiltering<T> extends ISelectOperationProvider<T> {

    IFiltering<T> paging(Integer offset, Integer maxSize);

    IFiltering<T> join(String joinClause);

    IFiltering<T> join(Join join);

    IFiltering<T> join(boolean use, String joinClause);

    IFiltering<T> join(boolean use, Join join);

    IFiltering<T> filter(boolean use, If condition, Object... params);

    IFiltering<T> filter(If condition, Object... params);

    IFiltering<T> orderBy(String orderByClause);

    IFiltering<T> orderBy(OrderBy orderBy);

    IFiltering<T> orderBy(boolean use, String orderByClause);

    IFiltering<T> orderBy(boolean use, OrderBy orderBy);
}
