package ru.kwanza.dbtool.orm.impl.querybuilder;

import ru.kwanza.dbtool.orm.api.Join;
import ru.kwanza.dbtool.orm.api.internal.IRelationMapping;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Alexander Guzanov
 */
class JoinRelation {
    private String alias;
    private Join.Type type;
    private IRelationMapping relationMapping;
    private Map<String, JoinRelation> childs;

    JoinRelation(Join.Type type, String alias, IRelationMapping relationMapping) {
        this.type = type;
        this.alias = alias;
        this.relationMapping = relationMapping;

    }

    JoinRelation(String alias) {
        this.alias = alias;
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

    IRelationMapping getRelationMapping() {
        return relationMapping;
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

    boolean hasChilds() {
        return childs != null && !childs.isEmpty();
    }

}
