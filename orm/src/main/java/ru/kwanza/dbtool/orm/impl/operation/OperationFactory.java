package ru.kwanza.dbtool.orm.impl.operation;

import org.springframework.jdbc.core.JdbcTemplate;
import ru.kwanza.dbtool.core.VersionGenerator;
import ru.kwanza.dbtool.orm.impl.mapping.IEntityMappingRegistry;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Kiryl Karatsetski
 */
public final class OperationFactory {

    private Map<Class, Operation> createOperationCache = new ConcurrentHashMap<Class, Operation>();
    private Map<Class, Operation> updateOperationCache = new ConcurrentHashMap<Class, Operation>();
    private Map<Class, Operation> deleteOperationCache = new ConcurrentHashMap<Class, Operation>();

    private IEntityMappingRegistry entityMappingRegistry;

    private JdbcTemplate jdbcTemplate;

    public OperationFactory(IEntityMappingRegistry entityMappingRegistry, JdbcTemplate jdbcTemplate) {
        this.entityMappingRegistry = entityMappingRegistry;
        this.jdbcTemplate = jdbcTemplate;
    }

    public Operation getCreateOperation(Class entityClass) {
        if (!createOperationCache.containsKey(entityClass)) {
            createOperationCache.put(entityClass, new CreateOperation(entityMappingRegistry, jdbcTemplate, entityClass));
        }
        return createOperationCache.get(entityClass);
    }

    public Operation getUpdateOperation(Class entityClass, VersionGenerator versionGenerator) {
        if (!updateOperationCache.containsKey(entityClass)) {
            updateOperationCache.put(entityClass, new UpdateOperation(entityMappingRegistry, jdbcTemplate, entityClass, versionGenerator));
        }
        return updateOperationCache.get(entityClass);
    }

    public DeleteOperation getDeleteOperation(Class entityClass) {
        if (!deleteOperationCache.containsKey(entityClass)) {
            deleteOperationCache.put(entityClass, new DeleteOperation(entityMappingRegistry, jdbcTemplate, entityClass));
        }
        return (DeleteOperation) deleteOperationCache.get(entityClass);
    }
}
