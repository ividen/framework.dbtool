package ru.kwanza.dbtool.orm.impl.fetcher;

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

    public FetchInfo(IEntityManager em, IRelationMapping relationMapping, List<Join> subJoins) {
        this.relationMapping = relationMapping;

        IFieldMapping relation = relationMapping.getRelationKeyMapping();
        If condition = If.in(relation.getName());
        if (relationMapping.getCondition() != null) {
            condition = If.and(condition, relationMapping.getCondition());
        }
        IQueryBuilder queryBuilder = em.queryBuilder(relationMapping.getRelationClass()).where(condition);

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
