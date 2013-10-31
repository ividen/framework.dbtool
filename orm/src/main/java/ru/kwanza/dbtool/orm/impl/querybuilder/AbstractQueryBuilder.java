package ru.kwanza.dbtool.orm.impl.querybuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.kwanza.dbtool.orm.api.*;
import ru.kwanza.dbtool.orm.api.internal.IEntityMappingRegistry;
import ru.kwanza.dbtool.orm.api.internal.IFieldMapping;
import ru.kwanza.dbtool.orm.api.internal.IRelationMapping;
import ru.kwanza.dbtool.orm.impl.EntityManagerImpl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Alexander Guzanov
 */
public abstract class AbstractQueryBuilder<T> implements IQueryBuilder<T> {
    private static final Logger logger = LoggerFactory.getLogger(AbstractQuery.class);
    private EntityManagerImpl em;
    private Class entityClass;
    private If condition;
    private List<OrderBy> orderBy = null;

    private EntityInfoFactory entityInfoFactory;
    private ColumnFactory columnFactory;

    private WhereFragmentHelper whereFragmentHelper;
    private FieldFragmentHelper fieldFragmentHelper;
    private FromFragmentHelper fromFragmentHelper;
    private OrderByFragmentHelper orderByFragmentHelper;

    public AbstractQueryBuilder(EntityManagerImpl em, Class entityClass) {
        if (!em.getRegistry().isRegisteredEntityClass(entityClass)) {
            throw new RuntimeException("Not registered entity class: " + entityClass);
        }
        this.em = em;
        this.entityClass = entityClass;
        this.entityInfoFactory = new EntityInfoFactory(this);
        this.columnFactory = new ColumnFactory(this);
        this.whereFragmentHelper = new WhereFragmentHelper(this);
        this.fieldFragmentHelper = new FieldFragmentHelper(this);
        this.fromFragmentHelper = new FromFragmentHelper(this);
        this.orderByFragmentHelper = new OrderByFragmentHelper(this);
    }

    EntityInfoFactory getEntityInfoFactory() {
        return entityInfoFactory;
    }

    ColumnFactory getColumnFactory() {
        return columnFactory;
    }

    Class getEntityClass() {
        return entityClass;
    }

    IEntityMappingRegistry getRegistry() {
        return em.getRegistry();
    }

    WhereFragmentHelper getWhereFragmentHelper() {
        return whereFragmentHelper;
    }

    FieldFragmentHelper getFieldFragmentHelper() {
        return fieldFragmentHelper;
    }

    FromFragmentHelper getFromFragmentHelper() {
        return fromFragmentHelper;
    }

    OrderByFragmentHelper getOrderByFragmentHelper() {
        return orderByFragmentHelper;
    }

    public List<OrderBy> getOrderBy() {
        return orderBy;
    }

    public final IQuery<T> create() {
        Parameters whereParams = new Parameters();
        Parameters joinParams = new Parameters();

        final String where = whereFragmentHelper.createWhereFragment(this.condition, whereParams);
        final String orderBy = orderByFragmentHelper.createOrderByFragment();
        final FromFragmentHelper.Result result = fromFragmentHelper.createFromFragment(joinParams);
        final String from = result.sqlPart;
        final String fieldsString = fieldFragmentHelper.createFieldsFragment();
        final StringBuilder sql = createSQLString(fieldsString, from, where, orderBy);

        final String sqlString = sql.toString();
        logger.debug("Creating query {}", sqlString);

        for (EntityInfo fetchEntity : result.fetchEntities) {
            final IRelationMapping relationMapping = fetchEntity.getRelationMapping();
            IFieldMapping relation = relationMapping.getRelationKeyMapping();
            If condition = If.in(relation.getName());
            if (relationMapping.getCondition() != null) {
                condition = If.and(condition, relationMapping.getCondition());
            }
            IQueryBuilder queryBuilder = em.queryBuilder(relationMapping.getRelationClass()).where(condition);
            for (Join join : relationMapping.getJoins()) {
                queryBuilder.join(join);
            }

            for (Join join : fetchEntity.getJoin().getSubJoins()) {
                queryBuilder.join(join);
            }
            fetchEntity.setFetchQuery(queryBuilder.create());
        }

        return createQuery(createConfig(entityInfoFactory.getRoot(), sqlString, joinParams.join(whereParams),result.fetchEntities));
    }

    public IQueryBuilder<T> join(String string) {
        for (Join join : JoinHelper.parse(string)) {
            processJoin(entityInfoFactory.getRoot(), join);
        }

        return this;
    }

    public IQueryBuilder<T> join(Join join) {
        processJoin(entityInfoFactory.getRoot(), join);

        return this;
    }

    private void processJoin(EntityInfo root, Join join) {
        entityInfoFactory.registerInfo(root, join);
    }

    protected abstract IQuery<T> createQuery(QueryConfig config);

    public IQuery<T> createNative(String sql) {
        Parameters holder = new Parameters();
        String preparedSql = SQLParser.prepareSQL(sql, holder);
        return createQuery(createConfig(entityInfoFactory.getRoot(), preparedSql, holder, Collections.<EntityInfo>emptyList()));
    }

    private QueryConfig<T> createConfig(EntityInfo rootRelations, String sqlString, Parameters holder, List<EntityInfo> fetchEntities) {
        return new QueryConfig<T>(em, entityClass, sqlString, rootRelations, holder, fetchEntities);
    }

    protected StringBuilder createSQLString(String fieldsString, String from, String where, String orderBy) {
        StringBuilder sql;
        sql = new StringBuilder("SELECT ").append(fieldsString).append(" FROM ").append(from);
        if (where.length() > 0) {
            sql.append(" WHERE ").append(where);
        }

        if (orderBy.length() > 0) {
            sql.append(" ORDER BY ").append(orderBy);
        }
        return sql;
    }

    public IQueryBuilder<T> where(If condition) {
        if (this.condition != null) {
            throw new IllegalStateException("Condition statement is set already in WHERE clause!");
        }
        this.condition = condition;

        return this;
    }

    public IQueryBuilder<T> orderBy(String orderByClause) {
        final List<OrderBy> parse = OrderByFragmentHelper.parse(orderByClause);
        checkOrderBy();
        orderBy.addAll(parse);

        return this;
    }

    public IQueryBuilder<T> orderBy(OrderBy orderBy) {
        checkOrderBy();
        this.orderBy.add(orderBy);
        return this;
    }

    private void checkOrderBy() {
        if (orderBy == null) {
            orderBy = new ArrayList<OrderBy>();
        }
    }

}
