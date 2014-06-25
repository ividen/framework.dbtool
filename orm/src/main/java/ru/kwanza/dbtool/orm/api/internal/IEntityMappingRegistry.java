package ru.kwanza.dbtool.orm.api.internal;

/**
 * Реестр ORM-сущностей
 *
 * Доступен из spring контекста по имени <b>dbtool.IEntityMappingRegistry</b>
 *
 * @author Kiryl Karatsetski
 */
public interface IEntityMappingRegistry {

    /**
     * Зарегистрировать новый класс.
     *
     * После регистрации метаинформация о классе доступна по {@link #getEntityType(Class)}
     *
     * @param entityClass класс регистрируемой сущности
     */
    IEntityType registerEntityClass(Class entityClass);

    /**
     * Проверить, рарегистрирован ли класс.
     *
     * @param entityClass имя класса
     */
    boolean isRegisteredEntityClass(Class entityClass);

    /**
     * Проверить, зарегистрирована ли сущность с данным именем
     *
     * @param entityName имя сущности, которое указывается в {@link ru.kwanza.dbtool.orm.annotations.Entity#name()}
     *
     */
    boolean isRegisteredEntityName(String entityName);

    /**
     * Получить метоописани сущности с данным именем
     * @param name имя сущности, которое указывется в {@link ru.kwanza.dbtool.orm.annotations.Entity#name()}
     */
    IEntityType getEntityType(String name);

    /**
     * Получение метаинформации по класса
     *
     * @param entityClass имя класса
     */
    IEntityType getEntityType(Class entityClass);
}
