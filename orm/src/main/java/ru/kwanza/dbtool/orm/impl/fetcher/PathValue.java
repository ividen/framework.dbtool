package ru.kwanza.dbtool.orm.impl.fetcher;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
* @author Alexander Guzanov
*/
class PathValue {
    private Map<RelationKey,PathValue> relationKeys = new LinkedHashMap<RelationKey,PathValue>();

    PathValue() {
    }

    public Map<RelationKey, PathValue> getRelationKeys() {
        return relationKeys;
    }
}
