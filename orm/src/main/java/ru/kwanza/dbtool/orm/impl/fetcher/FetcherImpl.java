package ru.kwanza.dbtool.orm.impl.fetcher;

import ru.kwanza.dbtool.orm.IFetcher;
import ru.kwanza.dbtool.orm.mapping.IEntityMappingRegistry;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Alexander Guzanov
 */
public class FetcherImpl implements IFetcher {
    private IEntityMappingRegistry registry;
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
        for (Map.Entry<String, Object> e:scan.entrySet()){
            String propertyName = e.getKey();
//            registry.getF(key.getEntityClass(), propertyName)
        }
    }
}
