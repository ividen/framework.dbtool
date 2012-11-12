package ru.kwanza.dbtool.orm.mapping;

import java.util.Collection;

/**
 * @author Kiryl Karatsetski
 */
public interface IEntityMappingRegistry {

    String getCreateSql(Class entityClass);

    String getUpdateSql(Class entityClass);

    String getDeleteSql(Class entityClass);

    String getTableName(Class entityClass);

    String getTableName(String entityName);

    Class getEntityClass(String entityName);

    Collection<String> getColumnNames(Class entityClass);

    Collection<String> getColumnNames(String entityName);

    Collection<FieldMapping> getFieldMapping(Class entityClass);

    Collection<FieldMapping> getFieldMapping(String entityName);
}
