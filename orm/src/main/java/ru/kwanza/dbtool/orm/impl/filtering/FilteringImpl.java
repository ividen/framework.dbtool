package ru.kwanza.dbtool.orm.impl.filtering;

import ru.kwanza.dbtool.orm.api.*;

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
    private Filter[] filters = null;
    private OrderBy[] orders = null;

    public FilteringImpl(IEntityManager em, Class<T> entityClass) {
        this.em = em;
        this.entityClass = entityClass;
    }

    public IFiltering setOffset(int offset) {
        this.offset = offset;
        return this;
    }

    public IFiltering setMaxSize(int maxSize) {
        this.maxSize = maxSize;
        return this;
    }

    public IFiltering filter(Filter... filters) {
        this.filters = filters;
        return this;
    }

    public IFiltering orderBy(OrderBy... orderBy) {
        orders = orderBy;
        return this;
    }

    public T select() {
        return createQuery().select();
    }

    public List<T> selectList() {
        return createQuery().selectList();
    }

    public <F> Map selectMapList(String propertyName) {
        return createQuery().selectMapList(propertyName);
    }

    public <F> Map selectMap(String propertyName) {
        return createQuery().selectMap(propertyName);
    }

    public void selectList(List<T> result) {
        createQuery().selectList(result);
    }

    public <F> void selectMapList(String propertyName, Map<F, List<T>> result) {
        createQuery().selectMapList(propertyName,result);
    }

    public <F> void selectMap(String propertyName, Map<F, T> result) {
        createQuery().selectMap(propertyName,result);
    }

    protected IQuery<T> createQuery() {
        IQueryBuilder<T> queryBuilder = em.queryBuilder(entityClass);
        if (maxSize != null) {
            queryBuilder.setMaxSize(maxSize);
        }

        if (offset != null) {
            queryBuilder.setOffset(offset);
        }

        LinkedList params = new LinkedList();
        LinkedList<Condition> conditions = new LinkedList<Condition>();
        if (filters != null) {
            for (Filter f : filters) {
                if (f != null && f.isUse()) {
                    if (f.isHasParams()) {
                        for (Object p : f.getValue()) {
                            params.add(p);
                        }
                    }
                    conditions.add(f.getCondition());
                }
            }
        }

        if (!conditions.isEmpty()) {
            Condition[] cns = new Condition[conditions.size()];
            queryBuilder.where(Condition.and(conditions.toArray(cns)));
        }

        if (orders != null) {
            queryBuilder.orderBy(orders);
        }

        IQuery<T> query = queryBuilder.create();
        for (int i = 0; i < params.size(); i++) {
            query.setParameter(i + 1, params.get(i));
        }
        return query;
    }
}
