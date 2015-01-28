package ru.kwanza.dbtool.orm.impl.querybuilder;

/*
 * #%L
 * dbtool-orm
 * %%
 * Copyright (C) 2015 Kwanza
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import ru.kwanza.dbtool.orm.api.Join;
import ru.kwanza.dbtool.orm.api.internal.IEntityType;
import ru.kwanza.dbtool.orm.api.internal.IFieldMapping;
import ru.kwanza.dbtool.orm.api.internal.IRelationMapping;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Alexander Guzanov
 */
public class QueryMapping {
    private String alias;
    private Join join;
    private IEntityType entityType;
    private IRelationMapping relationMapping;
    private Map<String, QueryMapping> joins;
    private Map<String, Join> fetches;
    private int fieldStartIndex;

    QueryMapping(Join join, IEntityType entityType, String alias, IRelationMapping relationMapping) {
        this.join = join;
        this.alias = alias;
        this.entityType = entityType;
        this.relationMapping = relationMapping;
    }

    public int getFieldStartIndex() {
        return fieldStartIndex;
    }

    void setFieldStartIndex(int fieldStartIndex) {
        this.fieldStartIndex = fieldStartIndex;
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

    void addJoin(String name, QueryMapping relation) {
        if (joins == null) {
            joins = new HashMap<String, QueryMapping>();
        }

        joins.put(name, relation);
    }

    void addFetch(Join join) {
        if (fetches == null) {
            fetches = new HashMap<String, Join>();
        }

        fetches.put(join.getPropertyName(), join);
    }

    QueryMapping getJoin(String propertyName) {
        return hasJoins() ? joins.get(propertyName) : null;
    }

    Map<String, QueryMapping> getJoins() {
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
        return getAlias() + "_" + fieldMapping.getOrderNum();
    }

    public int getColumnIndex(IFieldMapping fieldMapping) {
        return fieldStartIndex + fieldMapping.getOrderNum();
    }

    public String getColumnWithAlias(IFieldMapping fieldMapping) {
        return getColumnName(fieldMapping) + " " + getColumnAlias(fieldMapping);
    }

    public String getColumnName(IFieldMapping fm) {
        return getAlias() + "." + fm.getColumn();
    }

    public static class QueryRootMapping extends QueryMapping {

        public static final String ROOT_ALIAS = "t0";

        QueryRootMapping(IEntityType entityType) {
            super(null, entityType, null, null);
        }

        @Override
        boolean isRoot() {
            return true;
        }

        @Override
        public String getColumnAlias(IFieldMapping fieldMapping) {
            return hasJoins() ? (ROOT_ALIAS + "_" + fieldMapping.getOrderNum()) : fieldMapping.getColumn();
        }

        public String getColumnWithAlias(IFieldMapping fieldMapping) {
            return hasJoins() ? (getColumnName(fieldMapping) + " " + ROOT_ALIAS + "_" + fieldMapping.getOrderNum()) : fieldMapping.getColumn();
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
