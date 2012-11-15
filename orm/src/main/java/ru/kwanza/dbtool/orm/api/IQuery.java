package ru.kwanza.dbtool.orm.api;

import java.util.List;
import java.util.Map;

/**
 * @author Alexander Guzanov
 */
public interface IQuery<T> {

    T select();

    List<T> selectList();

    IQuery<T> setParameter(int index, Object value);

    <F> Map<F, List<T>> selectMapList(String field);

    <F> Map<F, T> selectMap(String field);

}
