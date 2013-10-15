package ru.kwanza.dbtool.orm.impl.operation;

import org.springframework.jdbc.core.JdbcTemplate;
import ru.kwanza.dbtool.core.DBTool;
import ru.kwanza.dbtool.orm.api.internal.IEntityMappingRegistry;

/**
 * @author Kiryl Karatsetski
 */
public abstract class Operation {

    protected IEntityMappingRegistry entityMappingRegistry;

    protected DBTool dbTool;

    protected Class entityClass;

    protected Operation(IEntityMappingRegistry entityMappingRegistry, DBTool dbTool, Class entityClass) {
        if (!entityMappingRegistry.isRegisteredEntityClass(entityClass)) {
            throw new RuntimeException("Not registered entity class: " + entityClass);
        }
        this.entityMappingRegistry = entityMappingRegistry;
        this.dbTool = dbTool;
        this.entityClass = entityClass;
        initOperation();
    }

    protected abstract void initOperation();

    protected JdbcTemplate getJdbcTemplate() {
        return dbTool.getJdbcTemplate();
    }
}
