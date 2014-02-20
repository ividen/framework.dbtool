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

    private Map<String, IFieldMapping> fields = new LinkedHashMap<String, IFieldMapping>();
    private Map<String, IFieldMapping> fieldsByColumnName = new LinkedHashMap<String, IFieldMapping>();
    private Map<String, IRelationMapping> relations = new LinkedHashMap<String, IRelationMapping>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFromClause() {
        if (getSql() == null) return getTableName();
        return "(" + getSql() + ")";
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

    public Collection<IFieldMapping> getFields() {
        return Collections.unmodifiableCollection(fields.values());
    }

    public IRelationMapping getRelation(String name) {
        return relations.get(name);
    }

    public Collection<IRelationMapping> getRelations() {
        return Collections.unmodifiableCollection(relations.values());
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public void setEntityClass(Class entityClass) {
        this.entityClass = entityClass;
    }

    public void setVersionField(IFieldMapping versionField) {
        this.versionField = versionField;
    }

    public void setIdField(IFieldMapping idField) {
        this.idField = idField;
    }

    public void addField(IFieldMapping field) {
        if (fields.get(field.getName()) != null) {
            throw new RuntimeException("Duplicate property name '" + field.getName() + "' in class " + entityClass);
        }

        fields.put(field.getName(), field);
        if (fieldsByColumnName.containsKey(field.getColumn())) {
            throw new RuntimeException("Duplication column " + field.getColumn() + " mapping in " + entityClass.getName());
        }

        fieldsByColumnName.put(field.getColumn(), field);
    }

    public void addRelation(IRelationMapping relation) {
        relations.put(relation.getName(), relation);
    }


}
