package ru.kwanza.dbtool.orm.impl.fetcher.proxy;

import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import ru.kwanza.dbtool.orm.impl.fetcher.Fetcher;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author Alexander Guzanov
 */
public class ProxyCallback implements MethodInterceptor,Serializable {

    private static final ThreadLocal<Boolean> safeMode = new ThreadLocal<Boolean>() {
        @Override
        protected Boolean initialValue() {
            return false;
        }
    };

    private Fetcher fetcher;
    private volatile boolean loaded = false;
    private ReentrantLock lock = new ReentrantLock();

    private Class relationClass;
    private Class holderClass;
    private Collection holders;
    private String relationProperty;

    public ProxyCallback(Fetcher fetcher, Class holderClass, Collection holders, Class relationClass, String relationProperty) {
        this.fetcher = fetcher;
        this.holderClass = holderClass;
        this.holders = holders;
        this.relationClass = relationClass;
        this.relationProperty = relationProperty;
    }

    public Class getRelationClass() {
        return relationClass;
    }

    public Class getHolderClass() {
        return holderClass;
    }

    public Collection getHolders() {
        return holders;
    }

    public String getRelationProperty() {
        return relationProperty;
    }

    public Fetcher getFetcher() {
        return fetcher;
    }

    public void load() {
        if (!loaded) {
            final ReentrantLock mainLock = lock;
            mainLock.lock();
            try {
                if (!loaded) {
                    doLoad();
                }
            } finally {
                freeAll();
                mainLock.unlock();

            }
        }
    }

    public static void enterSafe() {
        safeMode.set(true);
    }

    public static void exitSafe() {
        safeMode.set(false);
    }

    private void freeAll() {
        this.loaded = true;
        this.lock = null;
        this.holderClass = null;
        this.holders = null;
        this.relationProperty = null;
        this.fetcher = null;

    }

    private void doLoad() {
        fetcher.doLazyFetch(holderClass, holders, relationProperty);
    }

    public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
        if (!safeMode.get()) {
            load();

            final Object delegate = Proxy.getDelegate(obj);
            if (delegate != null) {
                return proxy.invoke(delegate, args);
            } else {
                return proxy.invokeSuper(obj, args);
            }
        } else {
            return proxy.invokeSuper(obj, args);
        }
    }
}
