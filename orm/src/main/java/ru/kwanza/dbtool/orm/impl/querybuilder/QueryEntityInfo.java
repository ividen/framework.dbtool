package ru.kwanza.dbtool.orm.impl.querybuilder;

import ru.kwanza.dbtool.orm.api.Join;
import ru.kwanza.dbtool.orm.api.internal.IEntityType;
import ru.kwanza.dbtool.orm.api.internal.IFieldMapping;
import ru.kwanza.dbtool.orm.api.internal.IRelationMapping;
import ru.kwanza.dbtool.orm.impl.mapping.AbstractEntityType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Alexander Guzanov
 */
public class QueryEntityInfo {
    private String alias;
    private Join join;
    private IEntityType entityType;
    private IRelationMapping relationMapping;
    private Map<String, QueryEntityInfo> joins;
    private Map<String, Join> fetches;

    QueryEntityInfo(Join join, IEntityType entityType, String alias, IRelationMapping relationMapping) {
        this.join = join;
        this.alias = alias;
        this.entityType = entityType;
        this.relationMapping = relationMapping;
    }

    IEntityType getEntityType() {
        return entityType;
    }

    boolean isRoot() {
        return false;
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

    void addJoin(String name, QueryEntityInfo relation) {
        if (joins == null) {
            joins = new HashMap<String, QueryEntityInfo>();
        }

        joins.put(name, relation);
    }

    void addFetch(Join join) {
        if (fetches == null) {
            fetches = new HashMap<String, Join>();
        }

        fetches.put(join.getPropertyName(), join);
    }

    QueryEntityInfo getJoin(String propertyName) {
        return hasJoins() ? joins.get(propertyName) : null;
    }

    Map<String, QueryEntityInfo> getJoins() {
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

    public static String getTable(IEntityType entityType) {
        return entityType.getSql() == null ? entityType.getTableName() : "(" + entityType.getSql() + ") ";
    }

    public String getTableWithAlias() {
        String result = null;
        if (entityType.getSql() == null) {
            result = entityType.getTableName();
            String s = getAlias();
            if (s != null) {
                result += " " + s;
            }
        } else {
            result = "(" + entityType.getSql() + ")";

            String s = getAlias();
            if (s != null) {
                result += " " + s;
            } else {
                result += " " + entityType.getTableName();
            }
        }

        return result;
    }

    public String getColumnAlias(IFieldMapping fieldMapping) {
        return getAlias() + "_" + fieldMapping.getId();
    }

    public String getColumnWithAlias(IFieldMapping fieldMapping) {
        return getColumnName(fieldMapping) + " " + getColumnAlias(fieldMapping);
    }

    public String getColumnName(IFieldMapping fm) {
        return getAlias() + "." + fm.getColumn();
    }

    public static class RootQueryEntityInfo extends QueryEntityInfo {

        public static final String ROOT_ALIAS = "t0";

        RootQueryEntityInfo(IEntityType entityType) {
            super(null, entityType, null, null);
        }

        @Override
        boolean isRoot() {
            return true;
        }

        @Override
        public String getColumnAlias(IFieldMapping fieldMapping) {
            return hasJoins() ? (ROOT_ALIAS + "_" + fieldMapping.getId()) : fieldMapping.getColumn();
        }

        public String getColumnWithAlias(IFieldMapping fieldMapping) {
            return hasJoins() ? (getColumnName(fieldMapping) + " " + ROOT_ALIAS + "_" + fieldMapping.getId()) : fieldMapping.getColumn();
        }

        @Override
        public String getColumnName(IFieldMapping fm) {
            return getEntityType().getTableName() + "." + fm.getColumn();
        }

        @Override
        String getAlias() {
            return null;

        }
    }


}
