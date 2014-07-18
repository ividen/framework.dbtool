package ru.kwanza.dbtool.orm.api;

import ru.kwanza.dbtool.core.UpdateException;

import java.util.List;
import java.util.Map;

/**
 * Ошибка возникающая в {@link IEntityBatcher#flush()}
 * <p/>
 * Содержит ссылки на объекты, над которыми не удалось выполнить операции.
 *
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

    /**
     * Список объекто определенного типа, котрые не удалось создать в базе данных
     *
     * @param entityClass тип сущности
     */
    public <T> List<T> getCreateConstrained(Class entityClass) {
        return createExceptionMap.get(entityClass).getConstrainted();
    }

    /**
     * Список успешно созданных объектов указанного типа
     *
     * @param entityClass
     * @param <T>
     * @return
     */
    public <T> List<T> getCreateSuccessed(Class entityClass) {
        return createExceptionMap.get(entityClass).getUpdated();
    }

    /**
     * Количество созданных объектов определенного типа
     *
     * @param entityClass
     */
    public long getCreateUpdateCount(Class entityClass) {
        return createExceptionMap.get(entityClass).getUpdated().size();
    }

    /**
     * Список объектов определенного типа, которые не удалось обновить в базе данных по причине constraints violation
     *
     * @param entityClass тип сущности
     */
    public <T> List<T> getUpdateConstrained(Class entityClass) {
        return updateExceptionMap.get(entityClass).getConstrainted();
    }

    /**
     * Список объектов определенного типа, которые  удалось обновить в базе данных по причине constraints violation
     *
     * @param entityClass тип сущности
     */
    public <T> List<T> getUpdateSuccessed(Class entityClass) {
        return updateExceptionMap.get(entityClass).getConstrainted();
    }

    /**
     * Список объектов определенного типа, которые не удалось обновить в базе данных по причине optimistic violation
     *
     * @param entityClass тип сущности
     */
    public <T> List<T> getUpdateOptimistic(Class entityClass) {
        return updateExceptionMap.get(entityClass).getOptimistic();
    }

    /**
     * Количество измененных объектов опреденного типа
     *
     * @param entityClass тип сущности
     */
    public long getUpdateUpdateCount(Class entityClass) {
        return updateExceptionMap.get(entityClass).getUpdated().size();
    }

    /**
     * Список объектов, которые не удалось удалить по причине optimistic violation
     *
     * @param entityClass тип сущности
     */
    public <T> List<T> getDeleteByObjectOptimistic(Class entityClass) {
        return deleteByObjectExceptionMap.get(entityClass).getOptimistic();
    }

    /**
     * Список объектов, которые не удалось удалить по причине optimistic violation
     *
     * @param entityClass тип сущности
     */
    public <T> List<T> getDeleteSuccessed(Class entityClass) {
        return deleteByObjectExceptionMap.get(entityClass).getUpdated();
    }

    /**
     * Количество объектов опреденного типа, которые были удалены
     *
     * @param entityClass
     * @return
     */
    public long getDeleteByObjectUpdateCount(Class entityClass) {
        return deleteByObjectExceptionMap.get(entityClass).getUpdated().size();
    }


    public <T> List<T> getDeleteByKeyOptimistic(Class entityClass) {
        return deleteByKeyExceptionMap.get(entityClass).getOptimistic();
    }

    public long getDeleteByKeyUpdateCount(Class entityClass) {
        return deleteByKeyExceptionMap.get(entityClass).getUpdated().size();
    }
}
