package ru.kwanza.dbtool.orm.impl;

import ru.kwanza.dbtool.core.DBTool;
import ru.kwanza.dbtool.orm.Filter;
import ru.kwanza.dbtool.orm.IQuery;
import ru.kwanza.dbtool.orm.mapping.IEntityMappingRegistry;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Alexander Guzanov
 */
public class QueryImpl<T> implements IQuery<T> {
    private DBTool dbTool;
    private String select;
    private Object where;
    private String orderBy;
    private String query;
    private Integer offset;
    private Integer maxSize;
    private Class entityClass;
    private IEntityMappingRegistry registry;
    private ArrayList<Object> params;


    public QueryImpl(DBTool dbTool, IEntityMappingRegistry registry, Class<T> entityClass,
                     String select, String where, String orderBy,
                     Integer maxSize, Integer offset) {
        this.dbTool = dbTool;
        this.registry = registry;
        this.entityClass = entityClass;
        this.select = select;
        this.where = where;
        this.orderBy = orderBy;
        this.maxSize = maxSize;
        this.offset = offset;
        this.query = createQueryStatement();
    }

    private String createQueryStatement() {
        if (maxSize != null) {
            if (dbTool.getDbType() == DBTool.DBType.ORACLE) {
                return "SELECT * FROM (" + createSimpleQueryStatement() + ") WHERE rownum < ?";
            } else {
                return "SELECT TOP ? * FROM (" + select + "FROM " + registry.getTableName(entityClass) + getWhere() +
                        getOrderBy() + ") WHERE rownum < ?";
            }

        } else {
            return createSimpleQueryStatement();
        }
    }

    private String createSimpleQueryStatement() {
        return select + "FROM " + registry.getTableName(entityClass) + getWhere() +
                getOrderBy();
    }

    private String getOrderBy() {
        return (orderBy != null ? (" " + orderBy) : "");
    }

    private String getWhere() {
        return (where != null ? (" " + where) : "");
    }

    public T select() {
        return null;
    }

    public T selectWithFilter(Filter... filters) {
        return null;
    }

    public List<T> selectList() {

    }

    public Object[] getParams() {

    }

    public List<T> selectListWithFilter(Filter... filters) {
        return null;
    }

    public IQuery<T> setParameter(int index, Object value) {
        if (params == null) {
            params = new ArrayList<Object>();
            params.add(index, value);
        }
        return null;
    }

    @Override
    public String toString() {
        return "QueryImpl{" +
                "query='" + query + '\'' +
                '}';
    }
}
