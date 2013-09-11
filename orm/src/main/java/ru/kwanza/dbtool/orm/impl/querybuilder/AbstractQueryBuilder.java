package ru.kwanza.dbtool.orm.impl.querybuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.kwanza.dbtool.core.DBTool;
import ru.kwanza.dbtool.orm.api.Condition;
import ru.kwanza.dbtool.orm.api.IQuery;
import ru.kwanza.dbtool.orm.api.IQueryBuilder;
import ru.kwanza.dbtool.orm.api.Join;
import ru.kwanza.dbtool.orm.impl.RelationPathScanner;
import ru.kwanza.dbtool.orm.impl.mapping.FetchMapping;
import ru.kwanza.dbtool.orm.impl.mapping.FieldMapping;
import ru.kwanza.dbtool.orm.impl.mapping.IEntityMappingRegistry;

import java.util.*;

/**
 * @author Alexander Guzanov
 */
public abstract class AbstractQueryBuilder<T> implements IQueryBuilder<T> {
    protected static final Logger logger = LoggerFactory.getLogger(AbstractQuery.class);
    protected IEntityMappingRegistry registry;
    protected DBTool dbTool;
    protected Class entityClass;
    protected Condition condition;
    protected List<OrderBy> orderBy = null;
    protected boolean usePaging = false;
    //todo aguzanov эта ссылка не должна передавать в Query
    protected Map<String, List<Integer>> namedParams = new HashMap<String, List<Integer>>();
    //todo aguzanov эта ссылка не должна передавать в Query
    protected JoinRelation rootRelations;
    private int aliasCounter = 0;

    public AbstractQueryBuilder(DBTool dbTool, IEntityMappingRegistry registry, Class entityClass) {
        this.registry = registry;
        this.entityClass = entityClass;
        this.dbTool = dbTool;

        if (!registry.isRegisteredEntityClass(entityClass)) {
            throw new RuntimeException("Not registered entity class: " + entityClass);
        }
    }

    public final IQuery<T> create() {
        StringBuilder sql;
        List<Integer> paramsTypes = new ArrayList<Integer>();

        namedParams.clear();
        String from = createFrom();
        String fieldsString = createFields(rootRelations);
        String where = createWhere(this.condition, paramsTypes);
        String orderBy = createOrderBy();

        sql = createSQLString(fieldsString, from, where, orderBy);

        String sqlString = sql.toString();
        logger.debug("Creating query {}", sqlString);

        return createQuery(createConfig(paramsTypes,rootRelations, sqlString));
    }

    private String createFrom() {
        final StringBuilder fromPart = new StringBuilder(registry.getTableName(entityClass));

        final JoinRelation rootRelations = this.rootRelations;
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
                        .append(registry.getTableName(relationClass));

                if (joinRelation.getAlias() != null) {
                    fromPart.append(' ').append(joinRelation.getAlias());
                }
                fromPart.append(" ON ")
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

        checkJoins();
        for (Join join : processScanRelations(scan)) {
            processJoin(rootRelations, join);
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
        checkJoins();

        processJoin(rootRelations, joinClause);

        return this;
    }

    private void processJoin(JoinRelation root, Join joinClause) {
        JoinRelation joinRelation = root.getChild(joinClause.getPropertyName());
        Class entityClass = root.getFetchMapping() == null ? this.entityClass : root.getFetchMapping().getRelationClass();

        if (joinRelation == null) {
            aliasCounter++;
            final FetchMapping fetchMapping = registry.getFetchMappingByPropertyName(entityClass, joinClause.getPropertyName());
            if (fetchMapping == null) {
                throw new IllegalArgumentException(
                        "Wrong relation name for " + entityClass.getName() + " : " + joinClause.getPropertyName() + " !");
            }
            joinRelation = new JoinRelation(joinClause.getType(), "t_" + aliasCounter, fetchMapping);
            root.addChild(joinClause.getPropertyName(), joinRelation);
        }

        if (joinClause != null) {
            for (Join join : joinClause.getSubJoins()) {
                processJoin(joinRelation, join);
            }
        }
    }

    private void checkJoins() {
        if (rootRelations == null) {
            rootRelations = new JoinRelation(null, null, null);
        }
    }

    public IQueryBuilder<T> usePaging(boolean userPaging) {
        this.usePaging = userPaging;

        return this;
    }

    protected abstract IQuery<T> createQuery(QueryConfig config);

    public IQuery<T> createNative(String sql) {
        namedParams.clear();
        LinkedList<Integer> paramTypes = new LinkedList<Integer>();
        String preparedSql = parseSql(sql, paramTypes);
        return createQuery(createConfig(paramTypes, rootRelations, preparedSql));
    }

    private QueryConfig<T> createConfig(List<Integer> paramsTypes, JoinRelation rootRelations, String sqlString) {
        return new QueryConfig<T>(dbTool, registry, entityClass, sqlString,rootRelations, usePaging, paramsTypes, namedParams);
    }

    private String parseSql(String sql, List<Integer> paramTypes) {
        StringBuilder sqlBuilder = new StringBuilder();
        StringBuilder paramBuilder = null;
        char[] chars = sql.toCharArray();
        boolean variableMatch = false;
        for (int i = 0; i < chars.length; i++) {
            char c = chars[i];
            if (c == '?') {
                paramTypes.add(Integer.MAX_VALUE);
                paramBuilder = new StringBuilder();
                variableMatch = false;
                sqlBuilder.append('?');
            } else if (c == ':') {
                variableMatch = true;
                sqlBuilder.append('?');
                paramTypes.add(Integer.MAX_VALUE);
                paramBuilder = new StringBuilder();
            } else if (variableMatch) {
                if (isDelimiter(c)) {
                    String paramName = paramBuilder.toString();
                    List<Integer> indexes = namedParams.get(paramName);
                    if (indexes == null) {
                        indexes = new LinkedList<Integer>();
                        namedParams.put(paramName, indexes);
                    }
                    indexes.add(paramTypes.size());
                    variableMatch = false;

                    sqlBuilder.append(c);
                } else {
                    paramBuilder.append(c);
                }
            } else {
                sqlBuilder.append(c);
            }
        }

        if (variableMatch) {
            String paramName = paramBuilder.toString();
            List<Integer> indexes = namedParams.get(paramName);
            if (indexes == null) {
                indexes = new LinkedList<Integer>();
                namedParams.put(paramName, indexes);
            }
            indexes.add(paramTypes.size());
        }

        return sqlBuilder.toString();
    }

    private boolean isDelimiter(char c) {
        return c == '+' || c == '-' || c == ' ' || c == ')' || c == '(' || c == '\n' || c == '\t' || c == ',';
    }

    protected String createWhere(Condition condition, List<Integer> paramsTypes) {
        StringBuilder result = new StringBuilder();

        createConditionString(condition, paramsTypes, result);

        return result.toString();
    }

    private void createConditionString(Condition condition, List<Integer> paramsTypes, StringBuilder where) {
        if (condition == null) {
            return;
        }

        Condition[] childs = condition.getChilds();
        Condition.Type type = condition.getType();
        if (childs != null && childs.length > 0) {
            if (type != Condition.Type.NOT) {
                where.append('(');
                createConditionString(childs[0], paramsTypes, where);
                where.append(')');

                for (int i = 1; i < childs.length; i++) {
                    Condition c = childs[i];
                    where.append(' ').append(type.name()).append(" (");
                    createConditionString(c, paramsTypes, where);
                    where.append(')');
                }
            } else {
                where.append("NOT (");
                createConditionString(childs[0], paramsTypes, where);
                where.append(')');
            }
        } else if (type == Condition.Type.NATIVE) {
            where.append(parseSql(condition.getSql(), paramsTypes));
        } else {
            FieldMapping fieldMapping = registry.getFieldMappingByPropertyName(entityClass, condition.getPropertyName());
            if (fieldMapping == null) {
                throw new IllegalArgumentException("Unknown field!");
            }
            where.append(fieldMapping.getColumn());
            if (type == Condition.Type.IS_EQUAL) {
                addParam(condition, paramsTypes, fieldMapping.getType());
                where.append(" = ?");
            } else if (type == Condition.Type.NOT_EQUAL) {
                addParam(condition, paramsTypes, fieldMapping.getType());
                where.append(" <> ?");
            } else if (type == Condition.Type.IS_NOT_NULL) {
                where.append(" IS NOT NULL");
            } else if (type == Condition.Type.IS_NULL) {
                where.append(" IS NULL");
            } else if (type == Condition.Type.IS_GREATER) {
                addParam(condition, paramsTypes, fieldMapping.getType());
                where.append(" > ?");
            } else if (type == Condition.Type.IS_GREATER_OR_EQUAL) {
                addParam(condition, paramsTypes, fieldMapping.getType());
                where.append(" >= ?");
            } else if (type == Condition.Type.IS_LESS) {
                addParam(condition, paramsTypes, fieldMapping.getType());
                where.append(" < ?");
            } else if (type == Condition.Type.IS_LESS_OR_EQUAL) {
                addParam(condition, paramsTypes, fieldMapping.getType());
                where.append(" <= ?");
            } else if (type == Condition.Type.IN) {
                addParam(condition, paramsTypes, fieldMapping.getType());
                where.append(" IN (?)");
            } else if (type == Condition.Type.LIKE) {
                addParam(condition, paramsTypes, fieldMapping.getType());
                where.append(" LIKE ?");
            } else if (type == Condition.Type.BETWEEN) {
                addParam(condition, paramsTypes, fieldMapping.getType());
                addParam(condition, paramsTypes, fieldMapping.getType());
                where.append(" BETWEEN ? AND ?");
            } else {
                throw new IllegalArgumentException("Unknown condition type!");
            }
        }
    }

    protected String createOrderBy() {
        StringBuilder orderBy = new StringBuilder();
        if (this.orderBy != null && this.orderBy.size() > 0) {
            for (OrderBy ob : this.orderBy) {
                FieldMapping fieldMapping = registry.getFieldMappingByPropertyName(entityClass, ob.getPropertyName());
                if (fieldMapping == null) {
                    throw new IllegalArgumentException("Unknown field!");
                }
                orderBy.append(fieldMapping.getColumn()).append(' ').append(ob.getType()).append(", ");
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

    private void addParam(Condition condition, List<Integer> paramsTypes, int type) {
        paramsTypes.add(type);
        String paramName = condition.getParamName();
        if (paramName != null) {
            List<Integer> indexes = namedParams.get(paramName);
            if (indexes == null) {
                indexes = new ArrayList<Integer>();
                namedParams.put(paramName, indexes);
            }
            indexes.add(paramsTypes.size());
        }
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
                    result.append(alias).append('.');
                }

                result.append(fm.getColumn()).append(",");
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
