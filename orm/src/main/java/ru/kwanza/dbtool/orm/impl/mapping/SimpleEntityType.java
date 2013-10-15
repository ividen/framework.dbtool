package ru.kwanza.dbtool.orm.impl.mapping;

/**
 * @author Alexander Guzanov
 */
public class SimpleEntityType extends AbstractEntityType {

    public SimpleEntityType(Class entityClass,String entityName, String tableName, String sql) {
        setName(entityName);
        setEntityClass(entityClass);
        setTableName(tableName);
        setSql(sql);
    }

    public void validate() {
    }

    public boolean isAbstract() {
        return false;
    }
}