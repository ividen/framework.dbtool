package ru.kwanza.dbtool.orm.impl.fetcher;

import java.util.List;

/**
* @author Alexander Guzanov
*/
class PathValue {
    private List<RelationKey> relationKeys;
    private PathValue next;

    PathValue(List<RelationKey> relationKeys, PathValue next) {
        this.relationKeys = relationKeys;
        this.next = next;
    }

    public List<RelationKey> getRelationKeys() {
        return relationKeys;
    }

    public PathValue getNext() {
        return next;
    }
}
