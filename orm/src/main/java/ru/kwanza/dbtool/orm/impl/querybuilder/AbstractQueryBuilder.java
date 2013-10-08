package ru.kwanza.dbtool.orm.impl.querybuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.kwanza.dbtool.core.DBTool;
import ru.kwanza.dbtool.orm.api.*;
import ru.kwanza.dbtool.orm.impl.mapping.IEntityMappingRegistry;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Alexander Guzanov
 */
public abstract class AbstractQueryBuilder<T> implements IQueryBuilder<T> {
    private static final Logger logger = LoggerFactory.getLogger(AbstractQuery.class);
    private IEntityMappingRegistry registry;
    private DBTool dbTool;
    private Class entityClass;
    private If condition;
    private List<OrderBy> orderBy = null;

    private JoinRelationFactory relationFactory;
    private ColumnFactory columnFactory;

    private WhereFragmentHelper whereFragmentHelper;
    private FieldFragmentHelper fieldFragmentHelper;
    private FromFragmentHelper fromFragmentHelper;
    private OrderByFragmentHelper orderByFragmentHelper;

    public AbstractQueryBuilder(DBTool dbTool, IEntityMappingRegistry registry, Class entityClass) {
        if (!registry.isRegisteredEntityClass(entityClass)) {
            throw new RuntimeException("Not registered entity class: " + entityClass);
        }
        this.registry = registry;
        this.entityClass = entityClass;
        this.dbTool = dbTool;
        this.relationFactory = new JoinRelationFactory(this);
        this.columnFactory = new ColumnFactory(this);
        this.whereFragmentHelper = new WhereFragmentHelper(this);
        this.fieldFragmentHelper = new FieldFragmentHelper(this);
        this.fromFragmentHelper = new FromFragmentHelper(this);
        this.orderByFragmentHelper = new OrderByFragmentHelper(this);
    }

    JoinRelationFactory getRelationFactory() {
        return relationFactory;
    }

    ColumnFactory getColumnFactory() {
        return columnFactory;
    }

    Class getEntityClass() {
        return entityClass;
    }

    IEntityMappingRegistry getRegistry() {
        return registry;
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

        String where = whereFragmentHelper.createWhereFragment(this.condition, whereParams);
        String orderBy = orderByFragmentHelper.createOrderByFragment();
        String from = fromFragmentHelper.createFromFragment(joinParams);
        String fieldsString = fieldFragmentHelper.createFieldsFragment();

        StringBuilder sql = createSQLString(fieldsString, from, where, orderBy);

        String sqlString = sql.toString();
        logger.debug("Creating query {}", sqlString);

        return createQuery(createConfig(relationFactory.getRoot(), sqlString, joinParams.join(whereParams)));
    }

    public IQueryBuilder<T> join(String string) {
        for (Join join : JoinClauseHelper.parse(string)) {
            processJoin(relationFactory.getRoot(), join);
        }

        return this;
    }

    public IQueryBuilder<T> join(Join joinClause) {
        processJoin(relationFactory.getRoot(), joinClause);

        return this;
    }

    private void processJoin(JoinRelation root, Join joinClause) {
        JoinRelation joinRelation = relationFactory.registerRelation(root, joinClause.getType(), joinClause.getPropertyName());

        if (joinClause != null) {
            for (Join join : joinClause.getSubJoins()) {
                processJoin(joinRelation, join);
            }
        }
    }

    protected abstract IQuery<T> createQuery(QueryConfig config);

    public IQuery<T> createNative(String sql) {
        Parameters holder = new Parameters();
        String preparedSql = SQLParser.prepareSQL(sql, holder);
        return createQuery(createConfig(relationFactory.getRoot(), preparedSql, holder));
    }

    private QueryConfig<T> createConfig(JoinRelation rootRelations, String sqlString, Parameters holder) {
        return new QueryConfig<T>(dbTool, registry, entityClass, sqlString, rootRelations, holder);
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
