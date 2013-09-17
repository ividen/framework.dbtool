package ru.kwanza.dbtool.orm.impl.fetcher.proxy;

import net.sf.cglib.core.ClassGenerator;
import net.sf.cglib.core.DefaultGeneratorStrategy;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.transform.ClassTransformer;
import net.sf.cglib.transform.ClassTransformerChain;
import net.sf.cglib.transform.TransformingClassGenerator;
import net.sf.cglib.transform.impl.AddPropertyTransformer;
import net.sf.cglib.transform.impl.FieldProviderTransformer;
import org.objectweb.asm.Type;
import ru.kwanza.toolbox.SpringSerializable;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author Alexander Guzanov
 */
public class ProxyFactory extends SpringSerializable{
    private ConcurrentMap<Class, ProxyEntry> generatedClasses = new ConcurrentHashMap<Class, ProxyEntry>();

    public <T> T newInstance(final Class<T> clazz, ProxyCallback callback)
            throws InvocationTargetException, IllegalAccessException, InstantiationException, NoSuchFieldException, NoSuchMethodException {

        ProxyEntry<T> result = get(clazz);

        return result.newInstance(callback);
    }

    public <T> ProxyEntry<T> get(final Class<T> clazz) {
        ProxyEntry<T> result = generatedClasses.get(clazz);

        if (result == null) {
            Enhancer enhancer = new Enhancer();
            enhancer.setStrategy(new DefaultGeneratorStrategy() {
                @Override
                protected ClassGenerator transform(ClassGenerator cg) throws Exception {
                    return new TransformingClassGenerator(cg, new ClassTransformerChain(
                            new ClassTransformer[]{new AddPropertyTransformer(new String[]{ProxyEntry.DELEGATE}, new Type[]{Type.getType(clazz)}),
                                    new FieldProviderTransformer()
                            }));
                }
            });
            enhancer.setInterceptDuringConstruction(false);
            enhancer.setCallbackType(ProxyCallback.class);

            if (!clazz.isInterface()) {
                enhancer.setSuperclass(clazz);
                enhancer.setInterfaces(new Class[]{IProxy.class});
            } else {
                enhancer.setInterfaces(new Class[]{IProxy.class,Serializable.class, clazz});
            }

            result = new ProxyEntry(enhancer.createClass());
            if (null != generatedClasses.putIfAbsent(clazz, result)) {
                result = generatedClasses.get(result);
            }

        }

        return result;
    }

}
