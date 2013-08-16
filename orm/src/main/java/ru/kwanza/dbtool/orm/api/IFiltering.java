package ru.kwanza.dbtool.orm.api;

import ru.kwanza.dbtool.orm.impl.querybuilder.OrderBy;

/**
 * @author Alexander Guzanov
 */
public interface IFiltering<T> extends ISelectOperationProvider<T> {

    IFiltering<T> paging(Integer offset, Integer maxSize);

    IFiltering<T> filter(boolean use, Condition condition, Object ...params);

    IFiltering<T> filter(Condition condition, Object ...params);

    IFiltering<T> orderBy(String orderByClause);
}
