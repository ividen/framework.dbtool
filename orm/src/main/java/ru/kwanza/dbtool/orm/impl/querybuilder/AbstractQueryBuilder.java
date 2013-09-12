package ru.kwanza.dbtool.orm.impl.querybuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.kwanza.dbtool.core.DBTool;
import ru.kwanza.dbtool.orm.api.*;
import ru.kwanza.dbtool.orm.impl.RelationPathScanner;
import ru.kwanza.dbtool.orm.impl.mapping.FieldMapping;
import ru.kwanza.dbtool.orm.impl.mapping.IEntityMappingRegistry;

import java.util.ArrayList;
import java.util.Collection;
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

    public final IQuery<T> create() {
        StringBuilder sql;
        List<Integer> paramsTypes = new ArrayList<Integer>();

        ParamsHolder holder = new ParamsHolder();
        String where = whereFragmentHelper.createWhere(this.condition, paramsTypes, holder);
        String orderBy = createOrderBy();
        String from = createFrom();
        String fieldsString = createFields(relationFactory.getRoot());

        sql = createSQLString(fieldsString, from, where, orderBy);

        String sqlString = sql.toString();
        logger.debug("Creating query {}", sqlString);

        return createQuery(createConfig(paramsTypes, relationFactory.getRoot(), sqlString, holder));
    }

    private String createFrom() {
        final StringBuilder fromPart = new StringBuilder(registry.getTableName(entityClass));

        final JoinRelation rootRelations = relationFactory.getRoot();
        if (rootRelations != null) {
            processJoinRelation(fromPart, rootRelations);
        }
        return fromPart.toString();
    }

    private void processJoinRelation(StringBuilder fromPart, JoinRelation rootRelations) {
        if (rootRelations.getAllChilds() != null) {
            for (JoinRelation joinRelation : rootRelations.getAllChilds().values()) {
                final Class relationClass = joinRelation.getFetchMapping().getRelationClass();
                fromPart.append(joinRelation.getType() == Join.Type.LEFT ? "\n\tLEFT JOIN " : "\n\tINNER JOIN ")
                        .append(registry.getTableName(relationClass)).append(' ').append(joinRelation.getAlias()).append(" ON ")
                        .append(rootRelations.getAlias() == null ? registry.getTableName(this.entityClass) : rootRelations.getAlias())
                        .append('.').append(joinRelation.getFetchMapping().getPropertyMapping().getColumn()).append('=')
                        .append(joinRelation.getAlias()).append('.')
                        .append(joinRelation.getFetchMapping().getRelationPropertyMapping().getColumn());

                processJoinRelation(fromPart, joinRelation);
            }
        }
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
        JoinRelation joinRelation = root.getChild(joinClause.getPropertyName());

        if (joinRelation == null) {
            joinRelation = relationFactory.createJoinRelation(root, joinClause.getType(), joinClause.getPropertyName());
        }

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

    protected String createOrderBy() {
        StringBuilder orderBy = new StringBuilder();
        if (this.orderBy != null && this.orderBy.size() > 0) {
            for (OrderBy ob : this.orderBy) {
                orderBy.append(columnFactory.findColumn(ob.getPropertyName()).getFullColumnName()).append(' ').append(ob.getType())
                        .append(", ");
            }

            orderBy.deleteCharAt(orderBy.length() - 2);
        }

        return orderBy.toString();
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

    protected String createFields(JoinRelation root) {
        StringBuilder result = new StringBuilder();
        String alias = root == null || root.getAllChilds() == null ? null : registry.getTableName(entityClass);
        processFields(alias, root, result);
        result.deleteCharAt(result.length() - 1);
        return result.toString();
    }

    private void processFields(String alias, JoinRelation root, StringBuilder result) {
        Collection<FieldMapping> fields = root == null || root.getFetchMapping() == null
                ? registry.getFieldMappings(this.entityClass)
                : registry.getFieldMappings(root.getFetchMapping().getRelationClass());
        if (fields != null) {
            for (FieldMapping fm : fields) {
                result.append("\n\t");

                if (alias != null) {
                    result.append(alias).append('.').append(fm.getColumn()).append(' ').append(alias).append('_').append(fm.getColumn())
                            .append(",");
                } else {
                    result.append(fm.getColumn()).append(",");
                }
            }

        }
        if (root != null && root.getAllChilds() != null) {
            for (JoinRelation joinRelation : root.getAllChilds().values()) {
                processFields(joinRelation.getAlias(), joinRelation, result);
            }
        }
    }

    public IQueryBuilder<T> where(Condition condition) {
        if (this.condition != null) {
            throw new IllegalStateException("Condition statement is set already in WHERE clause!");
        }
        this.condition = condition;

        return this;
    }

    public IQueryBuilder<T> orderBy(String orderByClause) {
        final List<OrderBy> parse = OrderBy.parse(orderByClause);
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
