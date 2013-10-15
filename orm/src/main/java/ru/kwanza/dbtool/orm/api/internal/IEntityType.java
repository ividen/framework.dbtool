package ru.kwanza.dbtool.orm.api.internal;

import java.util.Collection;

/**
 * @author Alexander Guzanov
 */
public interface IEntityType<T> {

    String getName();

    String getTableName();

    String getSql();

    void validate();

    Class<T> getEntityClass();

    boolean isAbstract();

    IFieldMapping getIdField();

    IFieldMapping getVersionField();

    IFieldMapping getField(String name);

    Collection<IFieldMapping> getFields();

    IRelationMapping getRelation(String name);

    Collection<IRelationMapping> getRelations();
}
