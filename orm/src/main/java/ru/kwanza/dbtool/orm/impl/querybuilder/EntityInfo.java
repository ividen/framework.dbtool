package ru.kwanza.dbtool.orm.impl.querybuilder;

import ru.kwanza.dbtool.orm.api.IQuery;
import ru.kwanza.dbtool.orm.api.Join;
import ru.kwanza.dbtool.orm.api.internal.IEntityType;
import ru.kwanza.dbtool.orm.api.internal.IRelationMapping;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Alexander Guzanov
 */
class EntityInfo {
    private String alias;
    private Join join;
    private IEntityType entityType;
    private IRelationMapping relationMapping;
    private Map<String, EntityInfo> childs;
    private IQuery fetchQuery;

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

    void setJoin(Join join) {
        this.join = join;
    }

    String getAlias() {
        return alias;
    }

    IRelationMapping getRelationMapping() {
        return relationMapping;
    }

    void addChild(String name, EntityInfo relation) {
        if (childs == null) {
            childs = new HashMap<String, EntityInfo>();
        }

        childs.put(name, relation);
    }

    EntityInfo getChild(String propertyName) {
        return childs == null ? null : childs.get(propertyName);
    }

    Map<String, EntityInfo> getAllChilds() {
        return childs;
    }

    boolean hasChilds() {
        return childs != null && !childs.isEmpty();
    }

    public void setJoinType(Join join) {
        this.join = join;
    }

    public void setFetchQuery(IQuery fetchQuery) {
        this.fetchQuery = fetchQuery;
    }

    public IQuery getFetchQuery() {
        return fetchQuery;
    }
}
