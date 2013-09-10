package ru.kwanza.dbtool.orm.impl.querybuilder;

import ru.kwanza.dbtool.orm.api.Join;
import ru.kwanza.dbtool.orm.impl.mapping.FetchMapping;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Alexander Guzanov
 */
class JoinRelation {
    private String alias;
    private Join.Type type;
    private int fieldsStartIndex;
    private FetchMapping fetchMapping;
    private Map<String, JoinRelation> childs;

    JoinRelation(Join.Type type, String alias, FetchMapping fetchMapping) {
        this.type = type;
        this.alias = alias;
        this.fetchMapping = fetchMapping;
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

    int getFieldsStartIndex() {
        return fieldsStartIndex;
    }

    void setFieldsStartIndex(int fieldsStartIndex) {
        this.fieldsStartIndex = fieldsStartIndex;
    }

    public void addChild(String name, JoinRelation relation) {
        if (childs == null) {
            childs = new HashMap<String, JoinRelation>();
        }

        childs.put(name, relation);
    }

    public JoinRelation getChild(String propertyName) {
        return childs == null ? null : childs.get(propertyName);
    }

    Map<String, JoinRelation> getAllChilds() {
        return childs;
    }
}
