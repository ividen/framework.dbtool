package ru.kwanza.dbtool.orm.impl.querybuilder;

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
