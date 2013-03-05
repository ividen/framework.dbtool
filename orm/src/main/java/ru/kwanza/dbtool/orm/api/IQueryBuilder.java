package ru.kwanza.dbtool.orm.api;

/**
 * @author Alexander Guzanov
 */
public interface IQueryBuilder<T> {

    IQuery<T> create();

    IQuery<T> createNative(String sql);

    IQueryBuilder<T> setMaxSize(int maxSize);

    IQueryBuilder<T> setOffset(int offset);

    IQueryBuilder<T> where(Condition condition);

    IQueryBuilder<T> orderBy(OrderBy... orderBy);
}
