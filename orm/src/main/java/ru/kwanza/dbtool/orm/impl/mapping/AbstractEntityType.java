package ru.kwanza.dbtool.orm.impl.mapping;

import ru.kwanza.dbtool.orm.api.internal.IEntityType;
import ru.kwanza.dbtool.orm.api.internal.IFieldMapping;
import ru.kwanza.dbtool.orm.api.internal.IRelationMapping;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Alexander Guzanov
 */
public abstract class AbstractEntityType implements IEntityType {
    private String tableName;
    private String sql;
    private Class entityClass;
    private String name;
    private IFieldMapping versionField;
    private IFieldMapping idField;

    private Map<String, AbstractFieldMapping> fields = new LinkedHashMap<String, AbstractFieldMapping>();
    private Map<String, AbstractFieldMapping> fieldsByColumnName = new LinkedHashMap<String, AbstractFieldMapping>();
    private Map<String, IRelationMapping> relations = new LinkedHashMap<String, IRelationMapping>();

    protected AbstractEntityType() {
    }

    protected AbstractEntityType(Class entityClass, String entityName, String tableName, String sql) {
        this.entityClass = entityClass;
        this.name = "".equals(entityName) ? entityClass.getSimpleName() : entityName;
        this.tableName = tableName;
        setSql(sql);
    }

    protected abstract void validate();

    public String getName() {
        return name;
    }

    public String getTableName() {
        return tableName;
    }

    public String getSql() {
        return sql;
    }

    public Class getEntityClass() {
        return entityClass;
    }

    public IFieldMapping getIdField() {
        return idField;
    }

    public IFieldMapping getVersionField() {
        return versionField;
    }

    public IFieldMapping getField(String name) {
        return fields.get(name);
    }

    public Collection<AbstractFieldMapping> getFields() {
        return Collections.unmodifiableCollection(fields.values());
    }

    protected int getFieldsCount(){
        return fields.size();
    }

    public IRelationMapping getRelation(String name) {
        return relations.get(name);
    }

    public Collection<IRelationMapping> getRelations() {
        return Collections.unmodifiableCollection(relations.values());
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public void setVersionField(IFieldMapping versionField) {
        this.versionField = versionField;
    }

    public void setIdField(IFieldMapping idField) {
        this.idField = idField;
    }

    public void addField(AbstractFieldMapping field) {
        if (fields.get(field.getName()) != null) {
            throw new RuntimeException("Duplicate property name '" + field.getName() + "' in class " + entityClass);
        }

        fields.put(field.getName(), field);
        if (fieldsByColumnName.containsKey(field.getColumn())) {
            throw new RuntimeException("Duplication column " + field.getColumn() + " mapping in " + entityClass.getName());
        }

        fieldsByColumnName.put(field.getColumn(), field);
        field.setId(getFieldsCount());
    }

    public void addRelation(IRelationMapping relation) {
        relations.put(relation.getName(), relation);
    }


}
