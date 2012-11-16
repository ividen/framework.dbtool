package ru.kwanza.dbtool.orm.api;

import java.util.List;
import java.util.Map;

/**
 * @author Alexander Guzanov
 */
public interface ISelectOperationProfiler<T> {
    T select();

    List<T> selectList();

    <F> Map<F, List<T>> selectMapList(String field);

    <F> Map<F, T> selectMap(String field);

}
