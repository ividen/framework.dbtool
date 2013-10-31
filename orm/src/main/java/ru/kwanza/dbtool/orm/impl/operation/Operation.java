package ru.kwanza.dbtool.orm.impl.operation;

import org.springframework.jdbc.core.JdbcTemplate;
import ru.kwanza.dbtool.core.DBTool;
import ru.kwanza.dbtool.orm.api.internal.IEntityMappingRegistry;
import ru.kwanza.dbtool.orm.impl.EntityManagerImpl;

/**
 * @author Kiryl Karatsetski
 */
public abstract class Operation {

    protected EntityManagerImpl em;
    protected Class entityClass;

    protected Operation(EntityManagerImpl em, Class entityClass) {
        if (!em.getRegistry().isRegisteredEntityClass(entityClass)) {
            throw new RuntimeException("Not registered entity class: " + entityClass);
        }
        this.em = em;
        this.entityClass = entityClass;
        initOperation();
    }

    protected abstract void initOperation();

    protected JdbcTemplate getJdbcTemplate() {
        return em.getDbTool().getJdbcTemplate();
    }
}
