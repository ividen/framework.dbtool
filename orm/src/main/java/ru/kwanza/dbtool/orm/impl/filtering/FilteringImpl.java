package ru.kwanza.dbtool.orm.impl.filtering;

import ru.kwanza.dbtool.orm.api.*;

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
    private ArrayList<Filter> filters = new ArrayList<Filter>();
    private StringBuilder orderByClause;

    public FilteringImpl(IEntityManager em, Class<T> entityClass) {
        this.em = em;
        this.entityClass = entityClass;
    }

    public IFiltering<T> paging(Integer offset, Integer maxSize) {
        this.maxSize = maxSize;
        this.offset = offset;

        return this;
    }

    public IFiltering<T> filter(boolean use, Condition condition, Object... params) {
        if (use) {
            filters.add(new Filter(condition, params));
        }

        return this;
    }

    public IFiltering<T> filter(Condition condition, Object... params) {
        return filter(true, condition, params);
    }

    public IFiltering<T> orderBy(String orderByClause) {
        if (this.orderByClause == null) {
            this.orderByClause = new StringBuilder(orderByClause);
        } else {
            this.orderByClause.append(',').append(orderByClause);
        }
        return this;
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
        if (usePaging) {
            queryBuilder.usePaging(true);
        }

        LinkedList params = new LinkedList();
        LinkedList<Condition> conditions = new LinkedList<Condition>();
        if (filters != null) {
            for (Filter f : filters) {
                if (f.isHasParams()) {
                    for (Object p : f.getValue()) {
                        params.add(p);
                    }
                }
                conditions.add(f.getCondition());

            }
        }

        if (!conditions.isEmpty()) {
            Condition[] cns = new Condition[conditions.size()];
            queryBuilder.where(Condition.and(conditions.toArray(cns)));
        }

        if (orderByClause != null) {
            queryBuilder.orderBy(orderByClause.toString());
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
