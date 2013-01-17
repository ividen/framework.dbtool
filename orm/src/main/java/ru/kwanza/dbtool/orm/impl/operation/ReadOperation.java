package ru.kwanza.dbtool.orm.impl.operation;

import ru.kwanza.dbtool.core.DBTool;
import ru.kwanza.dbtool.orm.api.Condition;
import ru.kwanza.dbtool.orm.api.IQueryBuilder;
import ru.kwanza.dbtool.orm.impl.mapping.FieldMapping;
import ru.kwanza.dbtool.orm.impl.mapping.IEntityMappingRegistry;
import ru.kwanza.dbtool.orm.impl.querybuilder.QueryBuilderImpl;

import java.util.Collection;

/**
 * @author Kiryl Karatsetski
 */
public class ReadOperation extends Operation implements IReadOperation {

    private IQueryBuilder queryBuilderForObject;

    private IQueryBuilder queryBuilderForList;

    public ReadOperation(IEntityMappingRegistry entityMappingRegistry, DBTool dbTool, Class entityClass) {
        super(entityMappingRegistry, dbTool, entityClass);
    }

    @Override
    protected void initOperation() {
        final Collection<FieldMapping> idFieldMappings = entityMappingRegistry.getIdFields(entityClass);
        if (idFieldMappings == null || idFieldMappings.isEmpty()) {
            throw new RuntimeException("IdFieldMapping for entity class" + entityClass + " not found");
        }

        final FieldMapping idFieldMapping = idFieldMappings.iterator().next();
        final String propertyName = idFieldMapping.getName();

        this.queryBuilderForObject = new QueryBuilderImpl(dbTool, entityMappingRegistry, entityClass).where(Condition.isEqual(propertyName));
        this.queryBuilderForList = new QueryBuilderImpl(dbTool, entityMappingRegistry, entityClass).where(Condition.in(propertyName));
    }

    public Object selectByKey(Object key) {
        return queryBuilderForObject.create().setParameter(1, key).select();
    }

    public Collection selectByKeys(Object keys) {
        return queryBuilderForList.create().setParameter(1, keys).selectList();
    }
}
