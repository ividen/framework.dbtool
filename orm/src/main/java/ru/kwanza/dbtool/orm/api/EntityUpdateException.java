package ru.kwanza.dbtool.orm.api;

import ru.kwanza.dbtool.core.UpdateException;

import java.util.List;
import java.util.Map;

/**
 * @author Alexander Guzanov
 */
public class EntityUpdateException extends Exception {

    private final Map<Class, UpdateException> createExceptionMap;
    private final Map<Class, UpdateException> updateExceptionMap;
    private final Map<Class, UpdateException> deleteByObjectExceptionMap;
    private final Map<Class, UpdateException> deleteByKeyExceptionMap;

    public EntityUpdateException(Map<Class, UpdateException> createExceptionMap, Map<Class, UpdateException> updateExceptionMap,
                                 Map<Class, UpdateException> deleteByObjectExceptionMap,
                                 Map<Class, UpdateException> deleteByKeyExceptionMap) {
        this.createExceptionMap = createExceptionMap;
        this.updateExceptionMap = updateExceptionMap;
        this.deleteByObjectExceptionMap = deleteByObjectExceptionMap;
        this.deleteByKeyExceptionMap = deleteByKeyExceptionMap;
    }

    public <T> List<T> getCreateConstrained(Class entityClass) {
        return createExceptionMap.get(entityClass).getConstrainted();
    }

    public <T> List<T> getCreateOptimistic(Class entityClass) {
        return createExceptionMap.get(entityClass).getOptimistic();
    }

    public long getCreateUpdateCount(Class entityClass) {
        return createExceptionMap.get(entityClass).getUpdateCount();
    }

    public <T> List<T> getUpdateConstrained(Class entityClass) {
        return updateExceptionMap.get(entityClass).getConstrainted();
    }

    public <T> List<T> getUpdateOptimistic(Class entityClass) {
        return updateExceptionMap.get(entityClass).getOptimistic();
    }

    public long getUpdateUpdateCount(Class entityClass) {
        return updateExceptionMap.get(entityClass).getUpdateCount();
    }

    public <T> List<T> getDeleteByObjectConstrained(Class entityClass) {
        return deleteByObjectExceptionMap.get(entityClass).getConstrainted();
    }

    public <T> List<T> getDeleteByObjectOptimistic(Class entityClass) {
        return deleteByObjectExceptionMap.get(entityClass).getOptimistic();
    }

    public long getDeleteByObjectUpdateCount(Class entityClass) {
        return deleteByObjectExceptionMap.get(entityClass).getUpdateCount();
    }

    public <T> List<T> getDeleteByKeyConstrained(Class entityClass) {
        return deleteByKeyExceptionMap.get(entityClass).getConstrainted();
    }

    public <T> List<T> getDeleteByKeyOptimistic(Class entityClass) {
        return deleteByKeyExceptionMap.get(entityClass).getOptimistic();
    }

    public long getDeleteByKeyUpdateCount(Class entityClass) {
        return deleteByKeyExceptionMap.get(entityClass).getUpdateCount();
    }
}
