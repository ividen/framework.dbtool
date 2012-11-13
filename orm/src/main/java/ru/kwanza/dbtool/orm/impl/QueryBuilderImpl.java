package ru.kwanza.dbtool.orm.impl;

import ru.kwanza.dbtool.core.DBTool;
import ru.kwanza.dbtool.orm.Condition;
import ru.kwanza.dbtool.orm.IQuery;
import ru.kwanza.dbtool.orm.IQueryBuilder;
import ru.kwanza.dbtool.orm.OrderBy;
import ru.kwanza.dbtool.orm.mapping.FieldMapping;
import ru.kwanza.dbtool.orm.mapping.IEntityMappingRegistry;

import java.util.Collection;
import java.util.Collections;

/**
 * @author Alexander Guzanov
 */
public class QueryBuilderImpl<T> implements IQueryBuilder<T> {
    private IEntityMappingRegistry registry;
    private DBTool dbTool;
    private Class entityClass;
    private Condition condition;
    private OrderBy[] orderBy;
    private Integer maxSize;
    private Integer offset;

    public QueryBuilderImpl(IEntityMappingRegistry registry, Class entityClass) {
        this.registry = registry;
        this.entityClass = entityClass;
    }

    public IQuery<T> create() {
        String where = condition.toSQLString(registry, entityClass);
        StringBuilder select = new StringBuilder("SELECT ");

        StringBuilder orderBy = new StringBuilder();
        addFields(select, registry.getIDFields(entityClass));
        addFields(select, registry.getFieldMapping(entityClass));
        addFields(select, Collections.singleton(registry.getVersionField(entityClass)));

        select.deleteCharAt(select.length() - 1);
        if (this.orderBy != null && this.orderBy.length > 0) {
            orderBy.append("ORDER BY ");
            for (OrderBy ob : this.orderBy) {
                orderBy.append(registry.getFieldByPropertyName(entityClass, ob.getPropertyName()).getColumnName()).append(' ').append(ob.getType()).append(',');
            }

            orderBy.deleteCharAt(select.length() - 1);
        }

        return new QueryImpl<T>(dbTool, select.toString(), maxSize, offset, where.toString(), orderBy.toString());
    }

    private void addFields(StringBuilder select, Collection<FieldMapping> fields) {
        for (FieldMapping fm : fields) {
            select.append(fm.getColumnName()).append(" ,");
        }
    }

    public IQueryBuilder<T> setMaxSize(int maxSize) {
        this.maxSize = maxSize;
        return this;
    }

    public IQueryBuilder<T> setOffset(int offset) {
        this.offset = offset;
        return this;
    }

    public IQueryBuilder<T> where(Condition condition) {
        if (this.condition != null) {
            throw new IllegalStateException("Condition statement is set already in WHERE clause!");
        }
        this.condition = condition;
        return this;
    }

    public IQueryBuilder<T> orderBy(OrderBy... orderBy) {
        if (this.orderBy != null) {
            throw new IllegalStateException("Order statement is set already in ORDER BY clause!");
        }
        this.orderBy = orderBy;
        return this;
    }
}
