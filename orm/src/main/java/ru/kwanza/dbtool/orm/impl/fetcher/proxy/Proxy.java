package ru.kwanza.dbtool.orm.impl.fetcher.proxy;

import net.sf.cglib.proxy.Factory;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.transform.impl.FieldProvider;
import ru.kwanza.dbtool.orm.impl.ObjectAllocator;

import java.lang.reflect.InvocationTargetException;

/**
 * @author Alexander Guzanov
 */
public class Proxy<T> {
    public static final String DELEGATE = "delegate";
    private static final String CGLIB_$_CONSTRUCTED = "CGLIB$CONSTRUCTED";
    private static final String DELEGATE_FIELD = "$cglib_prop_" + DELEGATE;
    private Class<T> theClass;

    Proxy(Class<T> theClass) {

        this.theClass = theClass;
    }

    T newInstance(ProxyCallback loader) throws InvocationTargetException, IllegalAccessException {
        T result = ObjectAllocator.newInstance(theClass);
        ((Factory) result).setCallbacks(new MethodInterceptor[]{loader});
        ((FieldProvider) result).setField(CGLIB_$_CONSTRUCTED, true);

        return result;
    }

    public static boolean isProxy(Object object) {
        return (object instanceof IProxy) && (object instanceof FieldProvider);
    }

    public static <T> T getDelegate(Object object) {
        return (T) ((FieldProvider) object).getField(DELEGATE_FIELD);
    }

    public static <T> void setDelegate(T object, T result) {
        ((FieldProvider) object).setField(DELEGATE_FIELD, result);
    }

}
