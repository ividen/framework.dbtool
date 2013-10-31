package ru.kwanza.dbtool.orm.impl.operation;

import ru.kwanza.dbtool.core.DBTool;
import ru.kwanza.dbtool.orm.api.IQuery;
import ru.kwanza.dbtool.orm.api.If;
import ru.kwanza.dbtool.orm.api.internal.IEntityMappingRegistry;
import ru.kwanza.dbtool.orm.api.internal.IFieldMapping;
import ru.kwanza.dbtool.orm.impl.EntityManagerImpl;
import ru.kwanza.dbtool.orm.impl.querybuilder.QueryBuilderFactory;

import java.util.Collection;
import java.util.Map;

/**
 * @author Kiryl Karatsetski
 */
public class ReadOperation extends Operation implements IReadOperation {

    private IQuery queryForObject;

    private IQuery queryForCollection;

    public ReadOperation(EntityManagerImpl em, Class entityClass) {
        super(em,  entityClass);
    }

    @Override
    protected void initOperation() {
        final IFieldMapping idFieldMapping = em.getRegistry().getEntityType(entityClass).getIdField();
        if (idFieldMapping==null) {
            throw new RuntimeException("IdFieldMapping for entity class" + entityClass + " not found");
        }

        final String propertyName = idFieldMapping.getName();

        this.queryForObject =
                QueryBuilderFactory.createBuilder(em, entityClass).where(If.isEqual(propertyName)).create();
        this.queryForCollection =
                QueryBuilderFactory.createBuilder(em, entityClass).where(If.in(propertyName)).create();
    }

    public Object selectByKey(Object key) {
        return queryForObject.prepare().setParameter(1, key).select();
    }

    public Collection selectByKeys(Object keys) {
        return queryForCollection.prepare().setParameter(1, keys).selectList();
    }

    public Map selectMapByKeys(Object keys, String propertyName) {
        return queryForCollection.prepare().setParameter(1, keys).selectMap(propertyName);
    }

    public Map selectMapListByKeys(Object keys, String propertyName) {
        return queryForCollection.prepare().setParameter(1, keys).selectMapList(propertyName);
    }
}
