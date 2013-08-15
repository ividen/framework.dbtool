package ru.kwanza.dbtool.orm.api;

/**
 * @author Alexander Guzanov
 */
public interface IQueryBuilder<T> {

    IQuery<T> create();

    IQuery<T> createNative(String sql);

    IQueryBuilder<T> usePaging(boolean userPaging);

    IQueryBuilder<T> where(Condition condition);

    IQueryBuilder<T> orderBy(OrderBy... orderBy);
}
