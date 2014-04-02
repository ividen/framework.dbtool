package ru.kwanza.dbtool.orm.api;

/**
 * Построитель запросов
 *
 * @author Alexander Guzanov
 */
public interface IQueryBuilder<T> {

    IQuery<T> create();

    IQuery<T> createNative(String sql);

    IQueryBuilder<T> join(Join join);

    IQueryBuilder<T> lazy();

    IQueryBuilder<T> join(String string);

    IQueryBuilder<T> where(If condition);

    IQueryBuilder<T> orderBy(String orderByClause);

    IQueryBuilder<T> orderBy(OrderBy orderBy);
}
