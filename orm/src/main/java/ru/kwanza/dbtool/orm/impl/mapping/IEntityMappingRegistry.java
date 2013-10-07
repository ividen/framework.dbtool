package ru.kwanza.dbtool.orm.impl.mapping;

import java.util.Collection;

/**
 * @author Kiryl Karatsetski
 */
public interface IEntityMappingRegistry {

    void registerEntityClass(Class entityClass);

    boolean isRegisteredEntityClass(Class entityClass);

    boolean isRegisteredEntityName(String entityName);

    void validateEntityMapping();

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

    Collection<RelationMapping> getFetchMapping(Class entityClass);

    Collection<RelationMapping> getFetchMapping(String entityName);

    FieldMapping getFieldMappingByPropertyName(Class entityClass, String propertyName);

    FieldMapping getFieldMappingByPropertyName(String entityName, String propertyName);

    RelationMapping getFetchMappingByPropertyName(Class entityClass, String propertyName);

    RelationMapping getFetchMappingByPropertyName(String entityName, String propertyName);
}
