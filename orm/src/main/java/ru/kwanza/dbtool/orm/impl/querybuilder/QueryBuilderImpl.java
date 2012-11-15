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

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

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

    public QueryBuilderImpl(DBTool dbTool, IEntityMappingRegistry registry, Class entityClass) {
        this.dbTool = dbTool;
        this.registry = registry;
        this.entityClass = entityClass;
    }

    public IQuery<T> create() {
        StringBuilder sql;
        StringBuilder selectFields = new StringBuilder("");
        StringBuilder orderBy = new StringBuilder();
        StringBuilder where = new StringBuilder();
        addFields(selectFields, registry.getFieldMapping(entityClass));
        selectFields.deleteCharAt(selectFields.length() - 2);

        List<Integer> paramsTypes = new LinkedList<Integer>();
        createConditionString(this.condition, paramsTypes, where);


        if (this.orderBy != null && this.orderBy.length > 0) {
            orderBy.append(" ORDER BY ");
            for (OrderBy ob : this.orderBy) {
                FieldMapping fieldMapping = registry.getFieldMappingByPropertyName(entityClass, ob.getPropertyName());
                if(fieldMapping==null){
                    throw new IllegalArgumentException("Unknown field!");
                }
                orderBy.append(fieldMapping.getColumnName())
                        .append(' ')
                        .append(ob.getType())
                        .append(", ");
            }

            orderBy.deleteCharAt(orderBy.length() - 2);
        }

        if (maxSize != null) {
            if (dbTool.getDbType() == DBTool.DBType.MSSQL) {
                sql = new StringBuilder("SELECT TOP ").append(maxSize).append(' ')
                        .append(selectFields)
                        .append("FROM ")
                        .append(registry.getTableName(entityClass));
                if (where.length() > 0) {
                    sql.append(" WHERE ").append(where);
                }

                if (orderBy.length() > 0) {
                    sql.append(orderBy);
                }

            } else {
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

                sql.append(") WHERE rownum < ?");
            }
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
        return new QueryImpl<T>(dbTool, registry, entityClass, sqlString, maxSize, offset, paramsTypes);
    }

    private void createConditionString(Condition condition, List<Integer> paramsTypes, StringBuilder where) {
        if (condition == null) {
            return;
        }

        Condition[] childs = condition.getChilds();
        Condition.Type type = condition.getType();
        if (childs != null && childs.length > 0) {
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
            FieldMapping fieldMapping =
                    registry.getFieldMappingByPropertyName(entityClass, condition.getPropertyName());
            if(fieldMapping==null){
                     throw new IllegalArgumentException("Unknown field!");
            }
            where.append(fieldMapping.getColumnName());
            if (type == Condition.Type.IS_EQUAL) {
                paramsTypes.add(fieldMapping.getType());
                where.append(" = ?");
            } else if (type == Condition.Type.NOT_EQUAL) {
                paramsTypes.add(fieldMapping.getType());
                where.append(" <> ?");
            } else if (type == Condition.Type.IS_NOT_NULL) {
                where.append(" IS NOT NULL");
            } else if (type == Condition.Type.IS_NULL) {
                where.append(" IS NULL");
            } else if (type == Condition.Type.IS_GREATER) {
                paramsTypes.add(fieldMapping.getType());
                where.append(" > ?");
            } else if (type == Condition.Type.IS_GREATER_OR_EQUAL) {
                paramsTypes.add(fieldMapping.getType());
                where.append(" >= ?");
            } else if (type == Condition.Type.IS_LESS) {
                paramsTypes.add(fieldMapping.getType());
                where.append(" < ?");
            } else if (type == Condition.Type.IS_LESS_OR_EQUAL) {
                paramsTypes.add(fieldMapping.getType());
                where.append(" <= ?");
            } else if (type == Condition.Type.IN) {
                paramsTypes.add(fieldMapping.getType());
                where.append(" IN (?)");
            } else if (type == Condition.Type.LIKE) {
                paramsTypes.add(fieldMapping.getType());
                where.append(" LIKE ?");
            } else if (type == Condition.Type.BETWEEN) {
                paramsTypes.add(fieldMapping.getType());
                paramsTypes.add(fieldMapping.getType());
                where.append(" BETWEEN ? AND ?");
            } else {
                throw new IllegalArgumentException("Unknown condition type!");
            }
        }
    }

    private void addFields(StringBuilder select, Collection<FieldMapping> fields) {
        if (fields != null) {
            for (FieldMapping fm : fields) {
                select.append(fm.getColumnName()).append(", ");
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
