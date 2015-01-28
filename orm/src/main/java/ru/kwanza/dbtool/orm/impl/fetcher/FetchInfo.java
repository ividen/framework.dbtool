package ru.kwanza.dbtool.orm.impl.fetcher;

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

import ru.kwanza.dbtool.orm.api.*;
import ru.kwanza.dbtool.orm.api.internal.IFieldMapping;
import ru.kwanza.dbtool.orm.api.internal.IRelationMapping;
import ru.kwanza.dbtool.orm.impl.fetcher.proxy.IProxy;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Alexander Guzanov
 */
public class FetchInfo {
    private IRelationMapping relationMapping;
    private IQuery fetchQuery;

    public FetchInfo(IEntityManager em, IRelationMapping relationMapping, List<Join> subJoins,boolean lazy) {
        this.relationMapping = relationMapping;

        IFieldMapping relation = relationMapping.getRelationKeyMapping();
        If condition = If.in(relation.getName());
        if (relationMapping.getCondition() != null) {
            condition = If.and(condition, relationMapping.getCondition());
        }
        IQueryBuilder queryBuilder = em.queryBuilder(relationMapping.getRelationClass()).where(condition);
        if(lazy){
            queryBuilder = queryBuilder.lazy();
        }

        for (Join join : relationMapping.getJoins()) {
            queryBuilder.join(join);
        }

        for (Join join : subJoins) {
            queryBuilder.join(join);
        }

        this.fetchQuery = queryBuilder.create();
    }

    public IRelationMapping getRelationMapping() {
        return relationMapping;
    }

    public IQuery getFetchQuery() {
        return fetchQuery;
    }

    public void setFetchQuery(IQuery fetchQuery) {
        this.fetchQuery = fetchQuery;
    }

    public Set getRelationIds(Collection objs) {
        HashSet result = new HashSet();
        iterate(objs, result);
        return result;
    }

    private void iterate(Collection objs, HashSet result) {
        for (Object o : objs) {
            if (o instanceof Collection) {
                iterate((Collection) o, result);
            } else {
                if (isWaitingForLoad(o)) {
                    result.add(this.relationMapping.getKeyProperty().value(o));
                }
            }
        }
    }

    private boolean isWaitingForLoad(Object o) {
        final Object value = relationMapping.getProperty().value(o);
        return value == null || value instanceof IProxy;
    }

    public String getIDGroupingField() {
        return relationMapping.getRelationKeyMapping().getName();
    }
}
