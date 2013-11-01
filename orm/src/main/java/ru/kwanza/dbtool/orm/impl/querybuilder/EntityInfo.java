package ru.kwanza.dbtool.orm.impl.querybuilder;

import ru.kwanza.dbtool.orm.api.Join;
import ru.kwanza.dbtool.orm.api.internal.IEntityType;
import ru.kwanza.dbtool.orm.api.internal.IRelationMapping;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Alexander Guzanov
 */
public class EntityInfo {
    private String alias;
    private Join join;
    private IEntityType entityType;
    private IRelationMapping relationMapping;
    private Map<String, EntityInfo> joins;
    private Map<String, Join> fetches;

    EntityInfo(Join join, IEntityType entityType, String alias, IRelationMapping relationMapping) {
        this.join = join;
        this.alias = alias;
        this.entityType = entityType;
        this.relationMapping = relationMapping;

    }

    EntityInfo(IEntityType entityType) {
        this.entityType = entityType;
        this.alias = entityType.getTableName();
    }

    IEntityType getEntityType() {
        return entityType;
    }

    boolean isRoot() {
        return join == null;
    }

    Join.Type getJoinType() {
        return join.getType();
    }

    Join getJoin() {
        return join;
    }

    String getAlias() {
        return alias;
    }

    IRelationMapping getRelationMapping() {
        return relationMapping;
    }

    void addJoin(String name, EntityInfo relation) {
        if (joins == null) {
            joins = new HashMap<String, EntityInfo>();
        }

        joins.put(name, relation);
    }

    void addFetch(Join join) {
        if (fetches == null) {
            fetches = new HashMap<String, Join>();
        }

        fetches.put(join.getPropertyName(), join);
    }

    EntityInfo getJoin(String propertyName) {
        return hasJoins() ? joins.get(propertyName) : null;
    }

    Map<String, EntityInfo> getJoins() {
        return joins;
    }

    Map<String, Join> getFetches() {
        return fetches;
    }

    Join getFetch(String propertyName) {
        return hasJoins() ? fetches.get(propertyName) : null;
    }

    boolean hasJoins() {
        return joins != null && !joins.isEmpty();
    }

    boolean hasFetches() {
        return fetches != null && !fetches.isEmpty();
    }

}
