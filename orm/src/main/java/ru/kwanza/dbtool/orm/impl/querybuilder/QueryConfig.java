package ru.kwanza.dbtool.orm.impl.querybuilder;

import ru.kwanza.dbtool.core.DBTool;
import ru.kwanza.dbtool.orm.impl.mapping.IEntityMappingRegistry;

import java.lang.reflect.Constructor;
import java.util.List;
import java.util.Map;

/**
 * @author Alexander Guzanov
 */
public class QueryConfig<T> {
    private final DBTool dbTool;
    private final String sql;
    private final List<Integer> paramTypes;
    private final IEntityMappingRegistry registry;
    private final Class<T> entityClass;
    private final JoinRelation rootRelation;
    private ParamsHolder holder;
    private int paramsCount;


    QueryConfig(DBTool dbTool, IEntityMappingRegistry registry, Class<T> entityClass, String sql, JoinRelation rootRelations,
                List<Integer> paramTypes, ParamsHolder holder) {
        this.dbTool = dbTool;
        this.sql = sql;
        this.paramTypes = paramTypes;
        this.registry = registry;
        this.entityClass = entityClass;
        this.holder = holder;
        this.rootRelation = rootRelations;
        this.paramsCount = paramTypes.size();
    }

    public JoinRelation getRootRelation() {
        return rootRelation;
    }

    public DBTool getDbTool() {
        return dbTool;
    }

    public String getSql() {
        return sql;
    }

    public List<Integer> getParamTypes() {
        return paramTypes;
    }

    public IEntityMappingRegistry getRegistry() {
        return registry;
    }

    public Class<T> getEntityClass() {
        return entityClass;
    }

    public int getParamsCount() {
        return paramsCount;
    }

     ParamsHolder getHolder() {
        return holder;
    }
}
