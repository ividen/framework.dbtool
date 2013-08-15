package ru.kwanza.dbtool.orm.impl.querybuilder;

import ru.kwanza.dbtool.core.DBTool;
import ru.kwanza.dbtool.orm.impl.mapping.IEntityMappingRegistry;

import java.lang.reflect.Constructor;
import java.util.List;
import java.util.Map;

/**
 * @author Alexander Guzanov
 */
class QueryConfig<T> {
    private final DBTool dbTool;
    private final String sql;
    private final List<Integer> paramTypes;
    private final IEntityMappingRegistry registry;
    private final Class<T> entityClass;
    private final Constructor<T> contructor;
    private final boolean usePaging;
    private Map<String, List<Integer>> namedParams;
    private int paramsCount;


    QueryConfig(DBTool dbTool, IEntityMappingRegistry registry,
                Class<T> entityClass, String sql,boolean usePaging,
                List<Integer> paramTypes, Map<String, List<Integer>> namedParams) {
        this.dbTool = dbTool;
        this.sql = sql;
        this.usePaging = usePaging;
        this.paramTypes = paramTypes;
        this.registry = registry;
        this.entityClass = entityClass;
        this.namedParams = namedParams;
        try {
            this.contructor = entityClass.getDeclaredConstructor();
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
        this.contructor.setAccessible(true);
        this.paramsCount = paramTypes.size();
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

    public Constructor<T> getContructor() {
        return contructor;
    }

    public int getParamsCount() {
        return paramsCount;
    }

    public Map<String, List<Integer>> getNamedParams() {
        return namedParams;
    }
}
