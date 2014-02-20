package ru.kwanza.dbtool.orm.impl;

import ru.kwanza.dbtool.orm.api.LockResult;

import java.util.Collection;

/**
 * @author Alexander Guzanov
 */
public abstract class LockOperation<T> {
    public abstract LockResult<T> lock(Collection<T> items);
}
