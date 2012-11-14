package ru.kwanza.dbtool.orm.impl.fetcher;

import ru.kwanza.dbtool.orm.Condition;
import ru.kwanza.dbtool.orm.IEntityManager;
import ru.kwanza.dbtool.orm.IFetcher;
import ru.kwanza.dbtool.orm.IQueryBuilder;
import ru.kwanza.dbtool.orm.mapping.FetchMapping;
import ru.kwanza.dbtool.orm.mapping.FieldMapping;
import ru.kwanza.dbtool.orm.mapping.IEntityMappingRegistry;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
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

    public <T> void fetch(Class<T> entityClass, Collection<T> items, String relationPath) {
        PathKey key = new PathKey(entityClass, relationPath);
        PathValue value = pathCache.get(key);

        if (value != null) {
            value = constructPathValue(relationPath, key);
        }

        fillItems(items, value);
    }


    private <T> void fillItems(Collection<T> items, PathValue value) {
        Map<RelationKey, Map> results = new HashMap<RelationKey, Map>(value.getRelationKeys().size());

        for (RelationKey relationKey : value.getRelationKeys()) {
            RelationValue relationValue = relationCache.get(relationKey);
            Map<Object, Object> map = relationValue.getFetchQuery()
                    .setParameter(1, relationValue.getRelationIds(items))
                    .selectMap(relationValue.getIDGroupingField());
            results.put(relationKey, map);
        }

        for (T object : items) {
            for (Map.Entry<RelationKey, Map> entry : results.entrySet()) {
                RelationKey key = entry.getKey();
                Map realtion = entry.getValue();
                RelationValue relationValue = relationCache.get(key);
                FetchMapping fetchMapping = relationValue.getFetchMapping();
                Object relationIDValue = fetchMapping.getField().getValue(object);
                if (relationIDValue != null) {
                    Object relationObjValue = realtion.get(relationIDValue);
                    if (relationObjValue != null) {
                        fetchMapping.getFetchField().setValue(object, relationObjValue);
                    }
                }
            }
        }
    }

    private PathValue constructPathValue(String relationPath, PathKey key) {
        PathValue value;
        Map<String, Object> scan = new RelationPathScanner(relationPath).scan();
        value = new PathValue();
        constructPath(key.getEntityClass(), value, scan);
        PathValue pathValue = pathCache.putIfAbsent(key, value);
        if (pathValue != null) {
            value = pathValue;
        }
        return value;
    }

    private void constructPath(Class entityClass, PathValue pathValue, Map<String, Object> scan) {
        List<RelationKey> relationKeys = pathValue.getRelationKeys();
        for (Map.Entry<String, Object> e : scan.entrySet()) {
            String propertyName = e.getKey();

            FetchMapping fm = registry.getFetchMappingByPropertyName(entityClass, propertyName);
            if (fm == null) {
                throw new IllegalArgumentException("Wrong relation name! Fetch field mapping not found!");
            }

            relationKeys.add(constructRelation(entityClass, propertyName, fm));

            if (e.getValue() != null) {
                Map<String, Object> subScan = (Map<String, Object>) e.getValue();
                PathValue nextValue = new PathValue();
                pathValue.setNext(nextValue);
                constructPath(fm.getFetchField().getType(), nextValue, subScan);
            }
        }
    }

    private RelationKey constructRelation(Class entityClass, String propertyName, FetchMapping fm) {
        RelationKey relationKey = new RelationKey(entityClass, propertyName);
        RelationValue relationValue = relationCache.get(relationKey);
        if (relationValue == null) {
            FieldMapping id = registry.getIDFields(fm.getFetchField().getType()).iterator().next();
            //todo aguzanov fetch relation by one of ids column, just for a while
            IQueryBuilder queryBuilder = em.queryBuilder(fm.getFetchField().getType())
                    .where(Condition.in(id.getFieldName()));

            relationValue = new RelationValue(id, fm, queryBuilder.create());
            relationCache.putIfAbsent(relationKey, relationValue);
        }

        return relationKey;
    }
}
