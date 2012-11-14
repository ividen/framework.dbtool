package ru.kwanza.dbtool.orm.impl.fetcher;

import ru.kwanza.dbtool.orm.Condition;
import ru.kwanza.dbtool.orm.IEntityManager;
import ru.kwanza.dbtool.orm.IFetcher;
import ru.kwanza.dbtool.orm.IQueryBuilder;
import ru.kwanza.dbtool.orm.mapping.FetchMapping;
import ru.kwanza.dbtool.orm.mapping.FieldMapping;
import ru.kwanza.dbtool.orm.mapping.IEntityMappingRegistry;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Alexander Guzanov
 */
public class FetcherImpl implements IFetcher {
    private IEntityMappingRegistry registry;
    private IEntityManager em;
    // this cache contains all paths, with is used to get relational fields
    private ConcurrentHashMap<PathKey, PathValue> pathCache = new ConcurrentHashMap<PathKey, PathValue>();
    // this cache contains all relations for all entities and queries used to read this relations
    private ConcurrentHashMap<RelationKey, RelationValue> relationCache = new ConcurrentHashMap<RelationKey, RelationValue>();

    private static final String MAIN_CLASS_WITH_INNER_REGEXP = "([a-zA-Z0-9])+";

    public <T> void fetch(Class<T> entityClass, Collection<T> items, String relationPath) {
        PathKey key = new PathKey(entityClass, relationPath);
        PathValue value = pathCache.get(key);

        if (value != null) {
            Map<String, Object> scan = new RelationPathScanner(relationPath).scan();
            createElements(key, scan);
        } else {

        }
    }

    private void createElements(PathKey key, Map<String, Object> scan) {
        for (Map.Entry<String, Object> e : scan.entrySet()) {
            String propertyName = e.getKey();
            FetchMapping fm = registry.getFetchMappingByPropertyName(key.getEntityClass(), propertyName);
            if (fm == null) {
                throw new IllegalArgumentException("Wrong relation name! Fetch field mapping not found!");
            }

            FieldMapping id = registry.getIDFields(fm.getFetchField().getType()).iterator().next();
            //todo aguzanov relation by one ids column, just for a while
            IQueryBuilder queryBuilder= em.queryBuilder(fm.getFetchField().getType())
                    .where(Condition.isEqual(id.getFieldName()));

            relationCache.putIfAbsent(new RelationKey(key.getEntityClass(),propertyName),
                    new RelationValue(id,fm,queryBuilder.create()));
        }
    }
}
