package ru.kwanza.dbtool.orm.impl.fetcher.proxy;

import ru.kwanza.dbtool.orm.impl.fetcher.Fetcher;

import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.Collection;

/**
 * @author Alexander Guzanov
 */
public class ProxyHolder implements Serializable {
    private final Collection holders;
    private final Class holderClass;
    private final Class relationClass;
    private final String relationProperty;
    private final Fetcher fetcher;
    private final Object data;
    private final Class clazz;

    public ProxyHolder(Class clazz, IProxy proxy, ProxyCallback callback) throws Exception {
        try {
            this.clazz = clazz;
            this.data = Proxy.getDelegate(proxy);
            this.fetcher = callback.getFetcher();
            this.holders = callback.getHolders();
            this.holderClass = callback.getHolderClass();
            this.relationClass = callback.getRelationClass();
            this.relationProperty = callback.getRelationProperty();
        } catch (Exception e) {
            throw e;
        }
    }

    public Object readResolve() throws ObjectStreamException {
        try {
            final ProxyFactory factory = fetcher.getProxyFactory();
            final Object proxy =
                    factory.newInstance(clazz, new ProxyCallback(fetcher, holderClass, holders, relationClass, relationProperty));
            Proxy.setDelegate(proxy, data);
            return proxy;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
