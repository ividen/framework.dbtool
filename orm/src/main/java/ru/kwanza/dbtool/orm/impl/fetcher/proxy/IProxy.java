package ru.kwanza.dbtool.orm.impl.fetcher.proxy;

import java.io.ObjectStreamException;

/**
 * @author Alexander Guzanov
 */
public interface IProxy {

    public Object writeReplace() throws ObjectStreamException;
}
