package ru.kwanza.dbtool.orm.api.internal;

/**
 * @author Kiryl Karatsetski
 */
public interface IEntityMappingRegistry {

    void registerEntityClass(Class entityClass);

    boolean isRegisteredEntityClass(Class entityClass);

    boolean isRegisteredEntityName(String entityName);

    IEntityType getEntityType(String name);

    IEntityType getEntityType(Class name);
}
