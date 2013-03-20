package ru.kwanza.dbtool.orm.impl.querybuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.kwanza.dbtool.core.DBTool;
import ru.kwanza.dbtool.orm.api.Condition;
import ru.kwanza.dbtool.orm.api.IQuery;
import ru.kwanza.dbtool.orm.api.IQueryBuilder;
import ru.kwanza.dbtool.orm.api.OrderBy;
import ru.kwanza.dbtool.orm.impl.mapping.FieldMapping;
import ru.kwanza.dbtool.orm.impl.mapping.IEntityMappingRegistry;

import java.util.*;

/**
 * @author Alexander Guzanov
 */
public class QueryBuilderImpl<T> implements IQueryBuilder<T> {
    private static final Logger logger = LoggerFactory.getLogger(QueryImpl.class);

    private IEntityMappingRegistry registry;
    private DBTool dbTool;
    private Class entityClass;
    private Condition condition;
    private OrderBy[] orderBy;
    private Integer maxSize;
    private Integer offset;
    private Map<String, List<Integer>> namedParams = new HashMap<String, List<Integer>>();


    public QueryBuilderImpl(DBTool dbTool, IEntityMappingRegistry registry, Class entityClass) {
        this.dbTool = dbTool;
        this.registry = registry;
        this.entityClass = entityClass;

        if (!registry.isRegisteredEntityClass(entityClass)) {
            throw new RuntimeException("Not registered entity class: " + entityClass);
        }
    }

    public IQuery<T> create() {
        StringBuilder sql;
        StringBuilder selectFields = new StringBuilder("");
        StringBuilder orderBy = new StringBuilder();
        StringBuilder where = new StringBuilder();
        addFields(selectFields, registry.getFieldMappings(entityClass));
        selectFields.deleteCharAt(selectFields.length() - 2);

        List<Integer> paramsTypes = new LinkedList<Integer>();
        namedParams.clear();
        createConditionString(this.condition, paramsTypes, where);


        if (this.orderBy != null && this.orderBy.length > 0) {
            orderBy.append(" ORDER BY ");
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

        if (dbTool.getDbType() == DBTool.DBType.MSSQL && maxSize != null) {
            long size = (offset == null ? 0 : offset) + maxSize;
            sql = new StringBuilder("SELECT TOP ")
                    .append(size).append(' ')
                    .append(selectFields)
                    .append("FROM ")
                    .append(registry.getTableName(entityClass));
            if (where.length() > 0) {
                sql.append(" WHERE ").append(where);
            }

            if (orderBy.length() > 0) {
                sql.append(orderBy);
            }

        } else if (dbTool.getDbType() == DBTool.DBType.ORACLE && maxSize != null) {
            sql = new StringBuilder("SELECT  * FROM (SELECT ")
                    .append(selectFields)
                    .append("FROM ")
                    .append(registry.getTableName(entityClass));

            if (where.length() > 0) {
                sql.append(" WHERE ").append(where);
            }

            if (orderBy.length() > 0) {
                sql.append(orderBy);
            }


            sql.append(") WHERE ");

            if (maxSize != null) {
                sql.append("rownum <=?");
            }
        } else if (dbTool.getDbType() == DBTool.DBType.MYSQL && maxSize != null) {
            sql = new StringBuilder("SELECT ")
                    .append(selectFields)
                    .append("FROM ")
                    .append(registry.getTableName(entityClass));
            if (where.length() > 0) {
                sql.append(" WHERE ").append(where);
            }

            if (orderBy.length() > 0) {
                sql.append(orderBy);
            }
            sql.append("LIMIT");

            if (offset != null) {
                sql.append(" ?");

            } else {
                sql.append(" 0");
            }

            sql.append(",?");
        } else {
            sql = new StringBuilder("SELECT ")
                    .append(selectFields)
                    .append("FROM ")
                    .append(registry.getTableName(entityClass));
            if (where.length() > 0) {
                sql.append(" WHERE ").append(where);
            }

            if (orderBy.length() > 0) {
                sql.append(orderBy);
            }
        }

        String sqlString = sql.toString();
        logger.debug("Creating query {}", sqlString);
        return new QueryImpl<T>(new QueryConfig<T>(dbTool, registry, entityClass,
                sqlString, maxSize, offset, paramsTypes, namedParams));
    }

    public IQuery<T> createNative(String sql) {
        namedParams.clear();
        LinkedList<Integer> paramTypes = new LinkedList<Integer>();
        String preparedSql = parseSql(sql, paramTypes);
        return new QueryImpl<T>(new QueryConfig<T>(dbTool, registry, entityClass, preparedSql,
                null, null, paramTypes, namedParams));
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
        return sqlBuilder.toString();
    }


    private boolean isDelimiter(char c) {
        return c == '+' || c == '-' || c == ' ' || c == ')' || c == '(' || c == '\n' || c == '\t' || c == ',';
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

    private void addFields(StringBuilder select, Collection<FieldMapping> fields) {
        if (fields != null) {
            for (FieldMapping fm : fields) {
                select.append(fm.getColumn()).append(", ");
            }
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
