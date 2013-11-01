package ru.kwanza.dbtool.orm.impl.fetcher;

import ru.kwanza.dbtool.orm.api.internal.IEntityType;
import ru.kwanza.dbtool.orm.api.internal.IFieldMapping;
import ru.kwanza.dbtool.orm.api.internal.IRelationMapping;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Alexander Guzanov
 */
public class NonORMEntity implements IEntityType {
    private Class entityClass;
    private Map<String, IRelationMapping> relations = new HashMap<String, IRelationMapping>();

    public NonORMEntity(Class entityClass, Map<String, IRelationMapping> relations) {
        this.entityClass = entityClass;
        this.relations = relations;
    }

    public String getName() {
        return entityClass.getName();
    }

    public String getTableName() {
        throw new UnsupportedOperationException();
    }

    public String getSql() {
        throw new UnsupportedOperationException();
    }

    public Class getEntityClass() {
        return entityClass;
    }

    public boolean isAbstract() {
        return false;
    }

    public IFieldMapping getIdField() {
        throw new UnsupportedOperationException();
    }

    public IFieldMapping getVersionField() {
        throw new UnsupportedOperationException();
    }

    public IFieldMapping getField(String name) {
        throw new UnsupportedOperationException();
    }

    public Collection<IFieldMapping> getFields() {
        throw new UnsupportedOperationException();
    }

    public IRelationMapping getRelation(String name) {
        return relations.get(name);
    }

    public Collection<IRelationMapping> getRelations() {
        return Collections.unmodifiableCollection(relations.values());
    }
}
