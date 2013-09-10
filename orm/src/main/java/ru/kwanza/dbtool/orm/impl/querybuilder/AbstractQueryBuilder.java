package ru.kwanza.dbtool.orm.impl.querybuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.kwanza.dbtool.core.DBTool;
import ru.kwanza.dbtool.orm.api.*;
import ru.kwanza.dbtool.orm.impl.RelationPathScanner;
import ru.kwanza.dbtool.orm.impl.mapping.IEntityMappingRegistry;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Alexander Guzanov
 */
public abstract class AbstractQueryBuilder<T> implements IQueryBuilder<T> {
    private static final Logger logger = LoggerFactory.getLogger(AbstractQuery.class);
    private IEntityMappingRegistry registry;
    private DBTool dbTool;
    private Class entityClass;
    private Condition condition;
    private List<OrderBy> orderBy = null;
    private boolean usePaging = false;

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

    protected boolean isUsePaging() {
        return usePaging;
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

    public List<OrderBy> getOrderBy() {
        return orderBy;
    }

    public final IQuery<T> create() {
        StringBuilder sql;
        List<Integer> paramsTypes = new ArrayList<Integer>();

        ParamsHolder holder = new ParamsHolder();
        String where = whereFragmentHelper.createWhereFragment(this.condition, paramsTypes, holder);
        String orderBy = orderByFragmentHelper.createOrderByFragment();
        String from = fromFragmentHelper.createFromFragment();
        String fieldsString = fieldFragmentHelper.createFieldsFragment();

        sql = createSQLString(fieldsString, from, where, orderBy);

        String sqlString = sql.toString();
        logger.debug("Creating query {}", sqlString);

        return createQuery(createConfig(paramsTypes, relationFactory.getRoot(), sqlString, holder));
    }



    public IQueryBuilder<T> join(String string) {
        final Map<String, Object> scan = new RelationPathScanner(string).scan();

        for (Join join : processScanRelations(scan)) {
            processJoin(relationFactory.getRoot(), join);
        }

        return this;
    }

    private ArrayList<Join> processScanRelations(Map<String, Object> scan) {
        final ArrayList<Join> result = new ArrayList<Join>();

        for (Map.Entry<String, Object> entry : scan.entrySet()) {
            if (entry.getValue() instanceof Map) {
                if (entry.getKey().startsWith("#")) {
                    result.add(Join.left(entry.getKey().substring(1).trim(),
                            processScanRelations((Map<String, Object>) entry.getValue()).toArray(new Join[]{})));
                } else {
                    result.add(
                            Join.inner(entry.getKey(), processScanRelations((Map<String, Object>) entry.getValue()).toArray(new Join[]{})));
                }
            } else {
                if (entry.getKey().startsWith("#")) {
                    result.add(Join.left(entry.getKey().substring(1).trim()));
                } else {
                    result.add(Join.inner(entry.getKey()));
                }
            }
        }

        return result;
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

    public IQueryBuilder<T> usePaging(boolean userPaging) {
        this.usePaging = userPaging;

        return this;
    }

    protected abstract IQuery<T> createQuery(QueryConfig config);

    public IQuery<T> createNative(String sql) {
        ParamsHolder holder = new ParamsHolder();
        List<Integer> paramTypes = new ArrayList<Integer>();
        String preparedSql = SQLParser.prepareSQL(sql, paramTypes, holder);
        return createQuery(createConfig(paramTypes, relationFactory.getRoot(), preparedSql, holder));
    }

    private QueryConfig<T> createConfig(List<Integer> paramsTypes, JoinRelation rootRelations, String sqlString, ParamsHolder holder) {
        return new QueryConfig<T>(dbTool, registry, entityClass, sqlString, rootRelations, usePaging, paramsTypes, holder);
    }


    protected StringBuilder createSQLString(String fieldsString, String from, String where, String orderBy) {
        StringBuilder sql;
        sql = new StringBuilder("SELECT ").append(fieldsString).append("\nFROM ").append(from);
        if (where.length() > 0) {
            sql.append("\nWHERE ").append(where);
        }

        if (orderBy.length() > 0) {
            sql.append("\nORDER BY ").append(orderBy);
        }
        return sql;
    }

    public IQueryBuilder<T> where(Condition condition) {
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
        this.orderBy.add(orderBy);
        return this;
    }

    private void checkOrderBy() {
        if (orderBy == null) {
            orderBy = new ArrayList<OrderBy>();
        }
    }

}
