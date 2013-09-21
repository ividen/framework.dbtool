package ru.kwanza.dbtool.orm.impl.querybuilder;

import ru.kwanza.dbtool.core.DBTool;
import ru.kwanza.dbtool.orm.impl.mapping.IEntityMappingRegistry;

/**
 * @author Alexander Guzanov
 */
public class QueryConfig<T> {
    private final DBTool dbTool;
    private final String sql;
    private final IEntityMappingRegistry registry;
    private final Class<T> entityClass;
    private final JoinRelation rootRelation;
    private final ParamsHolder holder;

    QueryConfig(DBTool dbTool, IEntityMappingRegistry registry,
                Class<T> entityClass, String sql, JoinRelation rootRelations, ParamsHolder holder) {
        this.dbTool = dbTool;
        this.sql = sql;
        this.registry = registry;
        this.entityClass = entityClass;
        this.holder = holder.complete();
        this.rootRelation = rootRelations;
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


    public IEntityMappingRegistry getRegistry() {
        return registry;
    }

    public Class<T> getEntityClass() {
        return entityClass;
    }

    public ParamsHolder getParamsHolder() {
        return holder;
    }
}
