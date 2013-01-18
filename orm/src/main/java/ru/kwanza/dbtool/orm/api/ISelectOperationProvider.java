package ru.kwanza.dbtool.orm.api;

import java.util.List;
import java.util.Map;

/**
 * @author Alexander Guzanov
 */
public interface ISelectOperationProvider<T> {
    T select();

    List<T> selectList();

    <F> Map<F, List<T>> selectMapList(String propertyName);

    <F> Map<F, T> selectMap(String propertyName);

    void selectList(List<T> result);

    <F> void selectMapList(String propertyName, Map<F, List<T>> result, ListProducer<T> listProducer);

    <F> void selectMap(String propertyName, Map<F, T> result);

}
