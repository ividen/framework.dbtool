package ru.kwanza.dbtool.orm.impl.mapping;

import java.util.Collection;

/**
 * @author Kiryl Karatsetski
 */
public interface IEntityMappingRegistry {

    void registerEntityClass(Class entityClass);

    String getTableName(Class entityClass);

    String getTableName(String entityName);

    Class getEntityClass(String entityName);

    Collection<String> getColumnNames(Class entityClass);

    Collection<String> getColumnNames(String entityName);

    Collection<FieldMapping> getFieldMapping(Class entityClass);

    Collection<FieldMapping> getFieldMapping(String entityName);

    Collection<FieldMapping> getIdFields(Class entityClass);

    Collection<FieldMapping> getIdFields(String entityName);

    FieldMapping getVersionField(Class entityClass);

    FieldMapping getVersionField(String entityName);

    Collection<FetchMapping> getFetchMapping(Class entityClass);

    Collection<FetchMapping> getFetchMapping(String entityName);

    FieldMapping getFieldMappingByPropertyName(Class entityClass, String propertyName);

    FieldMapping getFieldMappingByPropertyName(String entityName, String propertyName);

    FetchMapping getFetchMappingByPropertyName(Class entityClass, String propertyName);

    FetchMapping getFetchMappingByPropertyName(String entityName, String propertyName);
}
