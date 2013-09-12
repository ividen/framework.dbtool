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
    private final Constructor<T> contructor;
    private final boolean usePaging;
    private final JoinRelation rootRelation;
    private ParamsHolder holder;
    private int paramsCount;


    QueryConfig(DBTool dbTool, IEntityMappingRegistry registry, Class<T> entityClass, String sql, JoinRelation rootRelations, boolean usePaging,
                List<Integer> paramTypes, ParamsHolder holder) {
        this.dbTool = dbTool;
        this.sql = sql;
        this.usePaging = usePaging;
        this.paramTypes = paramTypes;
        this.registry = registry;
        this.entityClass = entityClass;
        this.holder = holder;
        this.rootRelation = rootRelations;
        try {
            this.contructor = entityClass.getDeclaredConstructor();
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
        this.contructor.setAccessible(true);
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

    boolean isUsePaging() {
        return usePaging;
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
