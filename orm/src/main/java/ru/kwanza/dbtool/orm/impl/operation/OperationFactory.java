package ru.kwanza.dbtool.orm.impl.operation;

import ru.kwanza.dbtool.core.DBTool;
import ru.kwanza.dbtool.core.VersionGenerator;
import ru.kwanza.dbtool.orm.impl.mapping.IEntityMappingRegistry;

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

    @Resource(name = "dbtool.IEntityMappingRegistry")
    private IEntityMappingRegistry entityMappingRegistry;
    @Resource(name = "dbtool.DBTool")
    private DBTool dbTool;

    public ICreateOperation getCreateOperation(Class entityClass) {
        if (!createOperationCache.containsKey(entityClass)) {
            createOperationCache.put(entityClass, new CreateOperation(entityMappingRegistry, dbTool, entityClass));
        }
        return createOperationCache.get(entityClass);
    }

    public IReadOperation getReadOperation(Class entityClass) {
        if (!readOperationCache.containsKey(entityClass)) {
            readOperationCache.put(entityClass, new ReadOperation(entityMappingRegistry, dbTool, entityClass));
        }
        return readOperationCache.get(entityClass);
    }

    public IUpdateOperation getUpdateOperation(Class entityClass, VersionGenerator versionGenerator) {
        if (!updateOperationCache.containsKey(entityClass)) {
            updateOperationCache.put(entityClass, new UpdateOperation(entityMappingRegistry, dbTool, entityClass, versionGenerator));
        }
        return updateOperationCache.get(entityClass);
    }

    public IDeleteOperation getDeleteOperation(Class entityClass) {
        if (!deleteOperationCache.containsKey(entityClass)) {
            deleteOperationCache.put(entityClass, new DeleteOperation(entityMappingRegistry, dbTool, entityClass));
        }
        return deleteOperationCache.get(entityClass);
    }
}
