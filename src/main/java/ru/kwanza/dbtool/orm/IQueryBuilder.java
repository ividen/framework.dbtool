package ru.kwanza.dbtool.orm;

/**
 * @author Alexander Guzanov
 */
public interface IQueryBuilder<T> {

    public IQuery<T> create();

    public IQueryBuilder<T> setMaxSize(int maxSize);

    public IQueryBuilder<T> setOffset(int maxSize);

    public IQueryBuilder<T> where(Condition condition);

    public IQueryBuilder<T> orderBy(OrderBy ... orderBy);

}
