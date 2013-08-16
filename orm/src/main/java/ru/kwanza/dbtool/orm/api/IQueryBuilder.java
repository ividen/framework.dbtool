package ru.kwanza.dbtool.orm.api;

import ru.kwanza.dbtool.orm.impl.querybuilder.OrderBy;

/**
 * @author Alexander Guzanov
 */
public interface IQueryBuilder<T> {

    IQuery<T> create();

    IQuery<T> createNative(String sql);

    IQueryBuilder<T> usePaging(boolean userPaging);

    IQueryBuilder<T> join(Join joinClause);

    IQueryBuilder<T> join(String string);

    IQueryBuilder<T> where(Condition condition);

    IQueryBuilder<T> orderBy(String orderByClause);
}
