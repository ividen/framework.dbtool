package ru.kwanza.dbtool.orm.impl.mapping;

/**
 * @author Alexander Guzanov
 */
public class SimpleEntityType extends AbstractEntityType {
    public SimpleEntityType(Class entityClass, String entityName, String tableName, String sql) {
        super(entityClass, entityName, tableName, sql);
    }

    @Override
    protected void validate() {
    }

    public boolean isAbstract() {
        return false;
    }
}