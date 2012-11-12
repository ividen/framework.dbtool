package ru.kwanza.dbtool.orm;

/**
 * @author Alexander Guzanov
 */
public interface IQueryBuilder<T> {

    IQuery<T> create();

    IQueryBuilder<T> setMaxSize(int maxSize);

    IQueryBuilder<T> setOffset(int maxSize);

    IQueryBuilder<T> where(Condition condition);

    IQueryBuilder<T> orderBy(OrderBy... orderBy);
}
