package ru.kwanza.dbtool.orm.impl.querybuilder;

import ru.kwanza.dbtool.orm.api.Join;
import ru.kwanza.dbtool.orm.impl.mapping.FetchMapping;
import ru.kwanza.dbtool.orm.impl.mapping.IEntityMappingRegistry;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Alexander Guzanov
 */
class JoinRelation {
    private String alias;
    private int aliasIndex;
    private Join.Type type;
    private FetchMapping fetchMapping;
    private Map<String, JoinRelation> childs;

    JoinRelation(Join.Type type, JoinRelation root, FetchMapping fetchMapping) {
        this.type = type;
        this.aliasIndex = 1;
        this.alias = (root.isRoot() ? "t" : root.getAlias()) + root.aliasIndex++;
        this.fetchMapping = fetchMapping;

    }

    JoinRelation(String alias) {
        this.alias = alias;
        this.aliasIndex = 1;
    }

    static JoinRelation createJoinRelation(IEntityMappingRegistry registry, JoinRelation root, Join.Type type, Class entityClass,
                                           String propertyName) {
        JoinRelation joinRelation;

        final FetchMapping fetchMapping = registry.getFetchMappingByPropertyName(entityClass, propertyName);
        if (fetchMapping == null) {
            throw new IllegalArgumentException("Wrong relation name for " + entityClass.getName() + " : " + propertyName + " !");
        }

        joinRelation = new JoinRelation(type, root, fetchMapping);
        root.addChild(propertyName, joinRelation);
        return joinRelation;
    }

    boolean isRoot() {
        return type == null;
    }

    Join.Type getType() {
        return type;
    }

    String getAlias() {
        return alias;
    }

    FetchMapping getFetchMapping() {
        return fetchMapping;
    }

    void addChild(String name, JoinRelation relation) {
        if (childs == null) {
            childs = new HashMap<String, JoinRelation>();
        }

        childs.put(name, relation);
    }

    JoinRelation getChild(String propertyName) {
        return childs == null ? null : childs.get(propertyName);
    }

    Map<String, JoinRelation> getAllChilds() {
        return childs;
    }
}
