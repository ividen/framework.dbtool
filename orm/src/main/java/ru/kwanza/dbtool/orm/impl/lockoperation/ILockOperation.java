package ru.kwanza.dbtool.orm.impl.lockoperation;

import ru.kwanza.dbtool.orm.api.LockResult;

import java.util.Collection;

/**
 * @author Alexander Guzanov
 */
public interface ILockOperation<T> {
    LockResult<T> lock(Collection<T> items);
}
