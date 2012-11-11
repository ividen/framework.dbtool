package ru.kwanza.dbtool.orm;

import java.util.List;

/**
 * @author Alexander Guzanov
 */
public interface IQuery<T> {

    public T select();

    public T selectWithFilter(Filter ... filters);

    public List<T> selectList();

    public List<T> selectListWithFilter(Filter ... filters);

    public IQuery<T> setParameter(int index, Object value);
}
