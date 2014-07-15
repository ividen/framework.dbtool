package ru.kwanza.dbtool.orm.impl.querybuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.kwanza.dbtool.orm.api.*;
import ru.kwanza.dbtool.orm.api.internal.IEntityMappingRegistry;
import ru.kwanza.dbtool.orm.impl.EntityManagerImpl;
import ru.kwanza.dbtool.orm.impl.fetcher.FetchInfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author Alexander Guzanov
 */
public abstract class AbstractQueryBuilder<T> implements IQueryBuilder<T> {
    private static final Logger logger = LoggerFactory.getLogger(AbstractQuery.class);
    private EntityManagerImpl em;
    private Class entityClass;
    private If condition;
    private List<OrderBy> orderBy = null;

    private QueryMappingFactory queryMappingFactory;
    private ColumnFactory columnFactory;

    private WhereFragmentHelper whereFragmentHelper;
    private FieldFragmentHelper fieldFragmentHelper;
    private FromFragmentHelper fromFragmentHelper;
    private OrderByFragmentHelper orderByFragmentHelper;
    private boolean lazy = false;

    public AbstractQueryBuilder(EntityManagerImpl em, Class entityClass) {
        if (!em.getRegistry().isRegisteredEntityClass(entityClass)) {
            throw new RuntimeException("Not registered entity class: " + entityClass);
        }
        this.em = em;
        this.entityClass = entityClass;
        this.queryMappingFactory = new QueryMappingFactory(this);
        this.columnFactory = new ColumnFactory(this);
        this.whereFragmentHelper = new WhereFragmentHelper(this);
        this.fieldFragmentHelper = new FieldFragmentHelper(this);
        this.fromFragmentHelper = new FromFragmentHelper(this);
        this.orderByFragmentHelper = new OrderByFragmentHelper(this);
    }

    QueryMappingFactory getQueryMappingFactory() {
        return queryMappingFactory;
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

    EntityManagerImpl getEm() {
        return em;
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
        final String from = fromFragmentHelper.createFromFragment(joinParams);
        final String fieldsString = fieldFragmentHelper.createFieldsFragment();
        final StringBuilder sql = createSQLString(fieldsString, from, where, orderBy);

        final String sqlString = sql.toString();
        logger.debug("Creating query {}", sqlString);

        return createQuery(createConfig(queryMappingFactory.getRoot(), sqlString, joinParams.join(whereParams), createFetchInfo(), false, lazy));
    }

    private List<FetchInfo> createFetchInfo() {
        List<FetchInfo> result = new ArrayList<FetchInfo>();

        createFetchList(queryMappingFactory.getRoot(), result, lazy);


        return result;
    }

    private void createFetchList(QueryMapping queryMapping, List<FetchInfo> result, boolean lazy) {
        if (queryMapping.hasFetches()) {
            for (Map.Entry<String, Join> entry : queryMapping.getFetches().entrySet()) {
                result.addAll(em.getFetcher()
                        .getFetchInfo(queryMapping.getEntityType().getEntityClass(), Collections.singletonList(entry.getValue())));
            }
        }

        if (queryMapping.hasJoins()) {
            for (QueryMapping info : queryMapping.getJoins().values()) {
                createFetchList(info, result, lazy);
            }

        }
    }

    public IQueryBuilder<T> join(String string) {
        for (Join join : JoinHelper.parse(string)) {
            processJoin(queryMappingFactory.getRoot(), join);
        }

        return this;
    }

    public IQueryBuilder<T> join(Join join) {
        processJoin(queryMappingFactory.getRoot(), join);

        return this;
    }

    public IQueryBuilder<T> lazy() {
        this.lazy = true;

        return this;
    }

    private void processJoin(QueryMapping root, Join join) {
        queryMappingFactory.registerInfo(root, join);
    }

    protected abstract IQuery<T> createQuery(QueryConfig config);

    public IQuery<T> createNative(String sql) {
        Parameters holder = new Parameters();
        String preparedSql = SQLParser.prepareSQL(sql, holder);
        return createQuery(createConfig(queryMappingFactory.getRoot(), preparedSql, holder, Collections.<FetchInfo>emptyList(), true, lazy));
    }

    private QueryConfig<T> createConfig(QueryMapping rootRelations, String sqlString, Parameters holder,
                                        List<FetchInfo> fetchInfo, boolean isNative, boolean lazy) {
        return new QueryConfig<T>(em, entityClass, sqlString, rootRelations, holder, fetchInfo,
                isNative ? QueryColumnExtractor.BY_ALIAS : QueryColumnExtractor.BY_INDEX, lazy);
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
