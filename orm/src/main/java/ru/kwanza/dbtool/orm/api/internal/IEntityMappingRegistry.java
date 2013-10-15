package ru.kwanza.dbtool.orm.api.internal;

import java.util.Collection;

/**
 * @author Kiryl Karatsetski
 */
public interface IEntityMappingRegistry {

    void registerEntityClass(Class entityClass);

    boolean isRegisteredEntityClass(Class entityClass);

    boolean isRegisteredEntityName(String entityName);

    String getTableName(Class entityClass);

    String getTableName(String entityName);

    String getEntityName(Class entityClass);

    Class getEntityClass(String entityName);

    Collection<String> getColumnNames(Class entityClass);

    Collection<String> getColumnNames(String entityName);

    Collection<IFieldMapping> getFieldMappings(Class entityClass);

    Collection<IFieldMapping> getFieldMappings(String entityName);

    Collection<IFieldMapping> getIdFields(Class entityClass);

    Collection<IFieldMapping> getIdFields(String entityName);

    IFieldMapping getVersionField(Class entityClass);

    IFieldMapping getVersionField(String entityName);

    Collection<IRelationMapping> getRelationMappings(Class entityClass);

    Collection<IRelationMapping> getRelationMappings(String entityName);

    IFieldMapping getFieldMapping(Class entityClass, String propertyName);

    IFieldMapping getFieldMapping(String entityName, String propertyName);

    IRelationMapping getRelationMapping(Class entityClass, String propertyName);

    IRelationMapping getRelationMapping(String entityName, String propertyName);
}
