package ru.kwanza.dbtool.orm.impl.mapping;

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

    Collection<FieldMapping> getFieldMappings(Class entityClass);

    Collection<FieldMapping> getFieldMappings(String entityName);

    Collection<FieldMapping> getIdFields(Class entityClass);

    Collection<FieldMapping> getIdFields(String entityName);

    FieldMapping getVersionField(Class entityClass);

    FieldMapping getVersionField(String entityName);

    Collection<RelationMapping> getRelationMappings(Class entityClass);

    Collection<RelationMapping> getRelationMappings(String entityName);

    FieldMapping getFieldMapping(Class entityClass, String propertyName);

    FieldMapping getFieldMapping(String entityName, String propertyName);

    RelationMapping getRelationMapping(Class entityClass, String propertyName);

    RelationMapping getRelationMapping(String entityName, String propertyName);
}
