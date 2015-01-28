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
import ru.kwanza.dbtool.orm.api.internal.IRelationMapping;

/**
 * @author Alexander Guzanov
 */
class QueryMappingFactory {
    private final AbstractQueryBuilder builder;
    private final QueryMapping root;
    private int aliasCounter;

    QueryMappingFactory(AbstractQueryBuilder builder) {
        this.builder = builder;
        this.aliasCounter = 1;
        this.root = new QueryMapping.QueryRootMapping(builder.getRegistry().getEntityType(builder.getEntityClass()));
    }

    QueryMapping getRoot() {
        return root;
    }

    QueryMapping registerInfo(QueryMapping root, Join join) {
        QueryMapping queryMapping = root.getJoin(join.getPropertyName());

        if (queryMapping == null) {
            queryMapping = doRegister(root, join);
        }

        if (queryMapping != null) {
            for (Join subJoin : queryMapping.getJoin().getSubJoins()) {
                registerInfo(queryMapping, subJoin);
            }
        }

        return queryMapping;
    }

    private QueryMapping doRegister(QueryMapping root, Join join) {
        if (join.getType() == Join.Type.FETCH) {
            root.addFetch(join);
            return null;
        } else {
            Class entityClass = root.isRoot() ? builder.getEntityClass() : root.getRelationMapping().getRelationClass();
            final IEntityType entityType = builder.getRegistry().getEntityType(entityClass);
            final IRelationMapping relationMapping = entityType.getRelation(join.getPropertyName());

            if (relationMapping == null) {
                throw new IllegalArgumentException(
                        "Wrong relation name for " + entityClass.getName() + " : " + join.getPropertyName() + " !");
            }

            QueryMapping queryMapping = new QueryMapping(join,
                    builder.getRegistry().getEntityType(relationMapping.getRelationClass()), "t" + aliasCounter++, relationMapping);
            root.addJoin(join.getPropertyName(), queryMapping);
            return queryMapping;
        }
    }
}
