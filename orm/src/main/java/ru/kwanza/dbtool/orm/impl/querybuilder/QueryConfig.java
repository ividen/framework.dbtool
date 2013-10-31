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
    private final EntityInfo rootRelation;
    private final ParamsHolder holder;

    private List<EntityInfo> fetchEntities;

    QueryConfig(EntityManagerImpl em, Class<T> entityClass, String sql, EntityInfo rootRelations, Parameters parameters,
                List<EntityInfo> fetchEntities) {
        this.sql = sql;
        this.em = em;
        this.entityClass = entityClass;
        this.rootRelation = rootRelations;
        this.holder = parameters.createHolder();
        this.fetchEntities = fetchEntities;
    }

    public EntityInfo getRoot() {
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

    public List<EntityInfo> getFetchEntities() {
        return fetchEntities;
    }
}
