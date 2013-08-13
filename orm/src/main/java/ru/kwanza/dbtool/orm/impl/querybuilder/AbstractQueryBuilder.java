package ru.kwanza.dbtool.orm.impl.querybuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.kwanza.dbtool.core.DBTool;
import ru.kwanza.dbtool.orm.api.*;
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
    protected OrderBy[] orderBy;
    protected Integer maxSize;
    protected Integer offset;
    protected Map<String, List<Integer>> namedParams = new HashMap<String, List<Integer>>();
    protected ArrayList<Join> joins = null;


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
        List<Integer> paramsTypes = new LinkedList<Integer>();

        namedParams.clear();
        String conditions = getConditionStringAndTypes(this.condition, paramsTypes);
        String orderBy = createOrderBy();
        String fieldsString = getFieldsString(registry.getFieldMappings(entityClass));

        sql = createSQLString(conditions, orderBy, fieldsString);

        String sqlString = sql.toString();
        logger.debug("Creating query {}", sqlString);
        return createQuery(paramsTypes, sqlString);
    }

    public IQueryBuilder<T> join(String string) {

        return this;
    }

    public IQueryBuilder<T> join(Join joinClause) {
        joins.add(joinClause);

        return this;
    }

    protected abstract IQuery<T> createQuery(List<Integer> paramsTypes, String sqlString);

    protected abstract StringBuilder createSQLString(String conditions, String orderBy, String fieldsString);

    public IQuery<T> createNative(String sql) {
        namedParams.clear();
        LinkedList<Integer> paramTypes = new LinkedList<Integer>();
        String preparedSql = parseSql(sql, paramTypes);
        return createQuery(paramTypes, preparedSql);
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

    protected String getConditionStringAndTypes(Condition condition, List<Integer> paramsTypes) {
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
            FieldMapping fieldMapping =
                    registry.getFieldMappingByPropertyName(entityClass, condition.getPropertyName());
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
        if (this.orderBy != null && this.orderBy.length > 0) {
            for (OrderBy ob : this.orderBy) {
                FieldMapping fieldMapping = registry.getFieldMappingByPropertyName(entityClass, ob.getPropertyName());
                if (fieldMapping == null) {
                    throw new IllegalArgumentException("Unknown field!");
                }
                orderBy.append(fieldMapping.getColumn())
                        .append(' ')
                        .append(ob.getType())
                        .append(", ");
            }

            orderBy.deleteCharAt(orderBy.length() - 2);
        }

        return orderBy.toString();
    }

    protected StringBuilder createDefaultSQLString(String fieldsString, String conditions, String orderBy) {
        StringBuilder sql;
        sql = new StringBuilder("SELECT ")
                .append(fieldsString)
                .append("FROM ")
                .append(registry.getTableName(entityClass));
        if (conditions.length() > 0) {
            sql.append(" WHERE ").append(conditions);
        }

        if (orderBy.length() > 0) {
            sql.append(" ORDER BY ").append(orderBy);
        }
        return sql;
    }

    private void addParam(Condition condition, List<Integer> paramsTypes, int type) {
        paramsTypes.add(type);
        String paramName = condition.getParamName();
        if (paramName != null) {
            List<Integer> indexes = namedParams.get(paramName);
            if (indexes == null) {
                indexes = new LinkedList<Integer>();
                namedParams.put(paramName, indexes);
            }
            indexes.add(paramsTypes.size());
        }
    }

    protected String getFieldsString(Collection<FieldMapping> fields) {
        StringBuilder result = new StringBuilder();
        if (fields != null) {
            for (FieldMapping fm : fields) {
                result.append(fm.getColumn()).append(", ");
            }
        }
        result.deleteCharAt(result.length() - 2);
        return result.toString();
    }

    public IQueryBuilder<T> setMaxSize(Integer maxSize) {
        this.maxSize = maxSize;
        return this;
    }

    public IQueryBuilder<T> setOffset(Integer offset) {
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
