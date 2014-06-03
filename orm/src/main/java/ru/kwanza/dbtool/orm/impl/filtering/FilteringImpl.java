package ru.kwanza.dbtool.orm.impl.filtering;

import ru.kwanza.dbtool.orm.api.*;
import ru.kwanza.dbtool.orm.impl.querybuilder.JoinHelper;
import ru.kwanza.dbtool.orm.impl.querybuilder.OrderByFragmentHelper;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author Alexander Guzanov
 */
public class FilteringImpl<T> implements IFiltering<T> {

    private IEntityManager em;
    private Class<T> entityClass;
    private Integer offset;
    private Integer maxSize;
    private List<Filter> filters = null;
    private List<Join> joins = null;
    private List<OrderBy> orderBys = null;

    public FilteringImpl(IEntityManager em, Class<T> entityClass) {
        this.em = em;
        this.entityClass = entityClass;
    }

    public IFiltering<T> paging(Integer offset, Integer maxSize) {
        this.maxSize = maxSize;
        this.offset = offset;

        return this;
    }

    public IFiltering<T> join(String join) {
        getJoins().addAll(JoinHelper.parse(join));
        return this;
    }

    public IFiltering<T> join(Join join) {
        getJoins().add(join);
        return this;
    }

    public IFiltering<T> join(boolean use, String join) {
        if (!use) {
            return this;
        }

        return join(join);
    }

    public IFiltering<T> join(boolean use, Join join) {
        if (!use) {
            return this;
        }

        return join(join);
    }

    public IFiltering<T> filter(boolean use, If condition, Object... params) {
        if (use) {
            getFilters().add(new Filter(true, condition, params));
        }

        return this;
    }

    private List<Filter> getFilters() {
        if (filters == null) {
            filters = new ArrayList<Filter>();
        }
        return filters;
    }

    private List<Join> getJoins() {
        if (joins == null) {
            joins = new ArrayList<Join>();
        }
        return joins;
    }

    private List<OrderBy> getOrderBys() {
        if (orderBys == null) {
            orderBys = new ArrayList<OrderBy>();
        }
        return orderBys;
    }

    public IFiltering<T> filter(If condition, Object... params) {
        return filter(true, condition, params);
    }

    public IFiltering<T> filter(Filter... filters) {
        for (Filter filter : filters) {
            if (filter.isUse()) {
                getFilters().add(filter);
            }
        }

        return this;
    }

    public IFiltering<T> orderBy(String orderByClause) {
        getOrderBys().addAll(OrderByFragmentHelper.parse(orderByClause));
        return this;
    }

    public IFiltering<T> orderBy(OrderBy orderBy) {
        getOrderBys().add(orderBy);
        return this;
    }

    public IFiltering<T> orderBy(boolean use, String orderByClause) {
        if (!use) {
            return this;
        }

        return orderBy(orderByClause);
    }

    public IFiltering<T> orderBy(boolean use, OrderBy orderBy) {
        if (!use) {
            return this;
        }

        return orderBy(orderBy);
    }

    public T select() {
        return createStatement().select();
    }

    public List<T> selectList() {
        return createStatement().selectList();
    }

    public <F> Map selectMapList(String propertyName) {
        return createStatement().selectMapList(propertyName);
    }

    public <F> Map selectMap(String propertyName) {
        return createStatement().selectMap(propertyName);
    }

    public void selectList(List<T> result) {
        createStatement().selectList(result);
    }

    public <F> void selectMapList(String propertyName, Map<F, List<T>> result, ListProducer<T> listProducer) {
        createStatement().selectMapList(propertyName, result, listProducer);
    }

    public <F> void selectMap(String propertyName, Map<F, T> result) {
        createStatement().selectMap(propertyName, result);
    }

    private IStatement<T> createStatement() {
        IQueryBuilder<T> queryBuilder = em.queryBuilder(entityClass);
        final boolean usePaging = maxSize != null && offset != null;

        LinkedList params = new LinkedList();
        LinkedList<If> conditions = new LinkedList<If>();
        if (filters != null) {
            for (Filter f : filters) {
                if (f.getParams() != null) {
                    for (Object p : f.getParams()) {
                        params.add(p);
                    }
                }
                conditions.add(f.getCondition());

            }
        }

        if (!conditions.isEmpty()) {
            If[] cns = new If[conditions.size()];
            queryBuilder.where(If.and(conditions.toArray(cns)));
        }

        if (joins != null) {
            for (Join join : joins) {
                queryBuilder.join(join);
            }
        }

        if (orderBys != null) {
            for (OrderBy orderBy : orderBys) {
                queryBuilder.orderBy(orderBy);
            }
        }

        IStatement<T> statement = queryBuilder.create().prepare();
        if (usePaging) {
            statement.paging(offset, maxSize);
        }
        for (int i = 0; i < params.size(); i++) {
            statement.setParameter(i + 1, params.get(i));
        }
        return statement;
    }

}
