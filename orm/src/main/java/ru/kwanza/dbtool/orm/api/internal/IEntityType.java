package ru.kwanza.dbtool.orm.api.internal;

import java.util.Collection;

/**
 * Метаинмормация о мэппинга в сущности
 *
 * @author Alexander Guzanov
 */
public interface IEntityType<T> {
    /**
     * Имя сущности
     * <p/>
     *
     * @see ru.kwanza.dbtool.orm.annotations.Entity#name()
     */
    String getName();

    /**
     * Имя таблицы
     * <p/>
     * см. {@link ru.kwanza.dbtool.orm.annotations.Entity#table()}
     */
    String getTableName();

    /**
     * Строка sql запроса, если был соответствующий мэппинг
     * <p/>
     *
     * @see ru.kwanza.dbtool.orm.annotations.Entity#sql()
     */
    String getSql();

    /**
     * Класс сущности
     */
    Class<T> getEntityClass();

    /**
     * Является ли сущность абстрактной
     * <p/>
     * Будет возвращать <i>true</i> если сущность была помечена {@link ru.kwanza.dbtool.orm.annotations.AbstractEntity}
     */
    boolean isAbstract();

    /**
     * Информация по ключевому полю
     *
     * @see ru.kwanza.dbtool.orm.api.internal.IFieldMapping
     */
    IFieldMapping getIdField();

    /**
     * Информация по полю версии
     *
     * @see ru.kwanza.dbtool.orm.api.internal.IFieldMapping
     */
    IFieldMapping getVersionField();

    /**
     * Получение информации по имени поля
     *
     * @param name имя поля
     * @see ru.kwanza.dbtool.orm.api.internal.IFieldMapping
     */
    IFieldMapping getField(String name);

    /**
     * Список полей, для которых описан мэпинг
     */
    Collection<IFieldMapping> getFields();

    /**
     * Получение поля, описывающего связь между сущностями
     *
     * @param name имя поля связи
     * @see ru.kwanza.dbtool.orm.api.internal.IRelationMapping
     */
    IRelationMapping getRelation(String name);

    /**
     * Список полей, описывающих связь
     *
     * @see ru.kwanza.dbtool.orm.api.internal.IRelationMapping
     */
    Collection<IRelationMapping> getRelations();
}
