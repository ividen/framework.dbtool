package ru.kwanza.dbtool.orm.api;

import java.util.List;
import java.util.Map;

/**
 * @author Alexander Guzanov
 */
public interface IQuery<T> extends ISelectOperationProvider<T>{
    IQuery<T> setParameter(int index, Object value);
}
