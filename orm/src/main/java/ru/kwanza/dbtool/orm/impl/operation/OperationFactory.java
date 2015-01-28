package ru.kwanza.dbtool.orm.impl.operation;

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

import ru.kwanza.dbtool.core.DBTool;
import ru.kwanza.dbtool.core.VersionGenerator;
import ru.kwanza.dbtool.orm.api.internal.IEntityMappingRegistry;
import ru.kwanza.dbtool.orm.impl.EntityManagerImpl;

import javax.annotation.Resource;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Kiryl Karatsetski
 */
public final class OperationFactory {

    private Map<Class, ICreateOperation> createOperationCache = new ConcurrentHashMap<Class, ICreateOperation>();
    private Map<Class, IReadOperation> readOperationCache = new ConcurrentHashMap<Class, IReadOperation>();
    private Map<Class, IUpdateOperation> updateOperationCache = new ConcurrentHashMap<Class, IUpdateOperation>();
    private Map<Class, IDeleteOperation> deleteOperationCache = new ConcurrentHashMap<Class, IDeleteOperation>();

    @Resource(name = "dbtool.IEntityManager")
    private EntityManagerImpl em;

    public ICreateOperation getCreateOperation(Class entityClass) {
        if (!createOperationCache.containsKey(entityClass)) {
            createOperationCache.put(entityClass, new CreateOperation(em, entityClass));
        }
        return createOperationCache.get(entityClass);
    }

    public IReadOperation getReadOperation(Class entityClass) {
        if (!readOperationCache.containsKey(entityClass)) {
            readOperationCache.put(entityClass, new ReadOperation(em, entityClass));
        }
        return readOperationCache.get(entityClass);
    }

    public IUpdateOperation getUpdateOperation(Class entityClass, VersionGenerator versionGenerator) {
        if (!updateOperationCache.containsKey(entityClass)) {
            updateOperationCache.put(entityClass, new UpdateOperation(em, entityClass, versionGenerator));
        }
        return updateOperationCache.get(entityClass);
    }

    public IDeleteOperation getDeleteOperation(Class entityClass) {
        if (!deleteOperationCache.containsKey(entityClass)) {
            deleteOperationCache.put(entityClass, new DeleteOperation(em, entityClass));
        }
        return deleteOperationCache.get(entityClass);
    }
}
