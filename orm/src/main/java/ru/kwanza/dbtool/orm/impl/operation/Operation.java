package ru.kwanza.dbtool.orm.impl.operation;

import org.springframework.jdbc.core.JdbcTemplate;
import ru.kwanza.dbtool.core.UpdateException;
import ru.kwanza.dbtool.orm.impl.mapping.IEntityMappingRegistry;

import java.util.Arrays;
import java.util.Collection;

/**
 * @author Kiryl Karatsetski
 */
public abstract class Operation {

    protected IEntityMappingRegistry entityMappingRegistry;
    protected JdbcTemplate jdbcTemplate;

    protected Class entityClass;

    protected Operation(IEntityMappingRegistry entityMappingRegistry, JdbcTemplate jdbcTemplate, Class entityClass) {
        if (!entityMappingRegistry.isRegisteredEntityClass(entityClass)) {
            throw new RuntimeException("Not registered entity class: " + entityClass);
        }
        this.entityMappingRegistry = entityMappingRegistry;
        this.jdbcTemplate = jdbcTemplate;
        this.entityClass = entityClass;
        initOperation();
    }

    protected abstract void initOperation();

    public abstract void execute(Collection objects) throws UpdateException;

    public void execute(Object object) throws UpdateException {
        execute(Arrays.asList(object));
    }
}
