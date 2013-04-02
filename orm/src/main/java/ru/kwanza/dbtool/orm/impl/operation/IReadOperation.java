package ru.kwanza.dbtool.orm.impl.operation;

import java.util.Collection;
import java.util.Map;

/**
 * @author Kiryl Karatsetski
 */
public interface IReadOperation {

    Object selectByKey(Object key);

    Collection selectByKeys(Object keys);

    Map selectMapByKeys(Object keys, String propertyName);

    Map selectMapListByKeys(Object keys, String propertyName);
}
