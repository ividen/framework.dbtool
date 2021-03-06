package ru.kwanza.dbtool.orm.impl.fetcher.proxy;

/*
 * #%L
 * dbtool-orm
 * %%
 * Copyright (C) 2015 Kwanza
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

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
public class ProxyFactory extends SpringSerializable {
    private ConcurrentMap<Class, Proxy> generatedClasses = new ConcurrentHashMap<Class, Proxy>();

    public <T> T newInstance(final Class<T> clazz, ProxyCallback callback)
            throws InvocationTargetException, IllegalAccessException, InstantiationException, NoSuchFieldException, NoSuchMethodException {

        Proxy<T> result = get(clazz);

        return result.newInstance(callback);
    }

    public <T> Proxy<T> get(final Class<T> clazz) {
        Proxy<T> result = generatedClasses.get(clazz);

        if (result == null) {
            Enhancer enhancer = new Enhancer();
            enhancer.setStrategy(new DefaultGeneratorStrategy() {
                @Override
                protected ClassGenerator transform(ClassGenerator cg) throws Exception {
                    return new TransformingClassGenerator(cg, new ClassTransformerChain(new ClassTransformer[]{
                            new AddPropertyTransformer(new String[]{Proxy.DELEGATE}, new Type[]{Type.getType(clazz)}),
                            new FieldProviderTransformer()}));
                }
            });
            enhancer.setInterceptDuringConstruction(false);
            enhancer.setCallbackType(ProxyCallback.class);

            if (!clazz.isInterface()) {
                enhancer.setSuperclass(clazz);
                enhancer.setInterfaces(new Class[]{IProxy.class});
            } else {
                enhancer.setInterfaces(new Class[]{IProxy.class, Serializable.class, clazz});
            }

            result = new Proxy(enhancer.createClass());
            if (null != generatedClasses.putIfAbsent(clazz, result)) {
                result = generatedClasses.get(result);
            }

        }

        return result;
    }

}
