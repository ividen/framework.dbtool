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

import ru.kwanza.dbtool.orm.impl.EntityManagerImpl;
import ru.kwanza.dbtool.orm.impl.fetcher.FetchInfo;

import java.util.List;

/**
 * @author Alexander Guzanov
 */
public class QueryConfig<T> {
    private final String sql;
    private final EntityManagerImpl em;
    private final Class<T> entityClass;
    private final QueryMapping rootRelation;
    private final ParamsHolder holder;
    private final boolean lazy;
    private final QueryColumnExtractor extractor;

    private List<FetchInfo> fetchEntities;

    QueryConfig(EntityManagerImpl em, Class<T> entityClass, String sql, QueryMapping rootRelations, Parameters parameters,
                List<FetchInfo> fetchEntities, QueryColumnExtractor extractor, boolean lazy) {
        this.sql = sql;
        this.em = em;
        this.entityClass = entityClass;
        this.rootRelation = rootRelations;
        this.holder = parameters.createHolder();
        this.fetchEntities = fetchEntities;
        this.lazy = lazy;
        this.extractor = extractor;
    }

    public QueryMapping getRoot() {
        return rootRelation;
    }

    public String getSql() {
        return sql;
    }

    public EntityManagerImpl getEntityManager() {
        return em;
    }

    public Class<T> getEntityClass() {
        return entityClass;
    }

    public ParamsHolder getParamsHolder() {
        return holder;
    }

    public List<FetchInfo> getFetchInfo() {
        return fetchEntities;
    }

    public boolean isLazy() {
        return lazy;
    }

    public QueryColumnExtractor getExtractor() {
        return extractor;
    }
}
