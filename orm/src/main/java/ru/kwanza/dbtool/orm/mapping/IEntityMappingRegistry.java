package ru.kwanza.dbtool.orm.mapping;

import java.util.Collection;

/**
 * @author Kiryl Karatsetski
 */
public interface IEntityMappingRegistry {

    String getTableName(Class entityClass);

    String getTableName(String entityName);

    Class getEntityClass(String entityName);

    Collection<String> getColumnNames(Class entityClass);

    Collection<String> getColumnNames(String entityName);

    Collection<FieldMapping> getFieldMapping(Class entityClass);

    Collection<FieldMapping> getFieldMapping(String entityName);

    Collection<FieldMapping> getIDFields(Class entityClass);

    Collection<FieldMapping> getIDFields(String entityName);

    FieldMapping getVersionField(Class entityClass);

    FieldMapping getVersionField(String entityName);

    Collection<FetchMapping> getFetchMapping(Class entityClass);

    Collection<FetchMapping> getFetchMapping(String entityName);
}
