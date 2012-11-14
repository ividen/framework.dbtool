package ru.kwanza.dbtool.orm.impl.fetcher;

import java.util.LinkedList;
import java.util.List;

/**
* @author Alexander Guzanov
*/
class PathValue {
    private List<RelationKey> relationKeys = new LinkedList<RelationKey>();
    private PathValue next;

    PathValue() {
    }

    public List<RelationKey> getRelationKeys() {
        return relationKeys;
    }

    public PathValue getNext() {
        return next;
    }

    public void setNext(PathValue next) {
        this.next = next;
    }
}
