package ru.kwanza.dbtool.orm.api;

/**
 * @author Alexander Guzanov
 */
public interface IQueryBuilder<T> {

    IQuery<T> create();

    IQuery<T> createNative(String sql);

    IQueryBuilder<T> setMaxSize(Integer maxSize);

    IQueryBuilder<T> setOffset(Integer offset);

    IQueryBuilder<T> join(Join joinClause);

    IQueryBuilder<T> join(String string);

    IQueryBuilder<T> where(Condition condition);

    IQueryBuilder<T> orderBy(OrderBy... orderBy);
}
