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

    public IFiltering setOffset(Integer offset) {
        this.offset = offset;
        return this;
    }

    public IFiltering setMaxSize(Integer maxSize) {
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

    public <F> void selectMapList(String propertyName, Map<F, List<T>> result,ListProducer<T> listProducer) {
        createStatement().selectMapList(propertyName, result, listProducer);
    }

    public <F> void selectMap(String propertyName, Map<F, T> result) {
        createStatement().selectMap(propertyName, result);
    }

    private IStatement<T> createStatement() {
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

        IStatement<T> statement = queryBuilder.create().prepare();
        for (int i = 0; i < params.size(); i++) {
            statement.setParameter(i + 1, params.get(i));
        }
        return statement;
    }

}
