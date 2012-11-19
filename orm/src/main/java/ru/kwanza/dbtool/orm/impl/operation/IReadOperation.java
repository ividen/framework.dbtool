package ru.kwanza.dbtool.orm.impl.operation;

import java.util.Collection;

/**
 * @author Kiryl Karatsetski
 */
public interface IReadOperation {

    Object selectByKey(Object key);

    Collection selectByKeys(Object keys);
}
