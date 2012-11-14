package ru.kwanza.dbtool.orm;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author Alexander Guzanov
 */
public interface IQuery<T> {

    T select();

    T selectWithFilter(Filter... filters);

    List<T> selectList();

    List<T> selectListWithFilter(Filter... filters);

    IQuery<T> setParameter(int index, Object value);

    Map<Object, List<T>> selectMapList(String field);

    Map<Object, T> selectMap(String field);

}
