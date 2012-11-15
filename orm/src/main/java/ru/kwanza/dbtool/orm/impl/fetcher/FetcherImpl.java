package ru.kwanza.dbtool.orm.impl.fetcher;

import ru.kwanza.dbtool.orm.api.Condition;
import ru.kwanza.dbtool.orm.api.IEntityManager;
import ru.kwanza.dbtool.orm.api.IFetcher;
import ru.kwanza.dbtool.orm.api.IQueryBuilder;
import ru.kwanza.dbtool.orm.impl.mapping.FetchMapping;
import ru.kwanza.dbtool.orm.impl.mapping.FieldMapping;
import ru.kwanza.dbtool.orm.impl.mapping.IEntityMappingRegistry;

import java.util.Collection;
import java.util.HashMap;
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

    public FetcherImpl(IEntityMappingRegistry registry, IEntityManager em) {
        this.registry = registry;
        this.em = em;
    }

    public <T> void fetch(Class<T> entityClass, Collection<T> items, String relationPath) {
        if (items == null) {
            return;
        }
        PathKey key = new PathKey(entityClass, relationPath);
        PathValue value = pathCache.get(key);

        if (value == null) {
            value = constructPathValue(relationPath, key);
        }

        fillItems(items, value);
    }


    private <T> void fillItems(Collection<T> items, PathValue value) {
        Map<RelationKey, Map> results = new HashMap<RelationKey, Map>(value.getRelationKeys().size());

        for (Map.Entry<RelationKey, PathValue> entry : value.getRelationKeys().entrySet()) {
            RelationKey relationKey = entry.getKey();
            RelationValue relationValue = relationCache.get(relationKey);
            Map<Object, Object> map = relationValue.getFetchQuery()
                    .setParameter(1, relationValue.getRelationIds(items))
                    .selectMap(relationValue.getIDGroupingField());
            results.put(relationKey, map);
            if (entry.getValue() != null) {
                fillItems(map.values(), entry.getValue());
            }
        }

        for (T object : items) {
            for (Map.Entry<RelationKey, Map> entry : results.entrySet()) {
                RelationKey key = entry.getKey();
                Map realtion = entry.getValue();
                RelationValue relationValue = relationCache.get(key);
                FetchMapping fetchMapping = relationValue.getFetchMapping();
                Object relationIDValue = fetchMapping.getPropertyField().getValue(object);
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
        Map<RelationKey, PathValue> relationKeys = pathValue.getRelationKeys();
        for (Map.Entry<String, Object> e : scan.entrySet()) {
            String propertyName = e.getKey();

            FetchMapping fm = registry.getFetchMappingByPropertyName(entityClass, propertyName);
            if (fm == null) {
                throw new IllegalArgumentException("Wrong relation name! Fetch field mapping not found!");
            }

            RelationKey relationKey = constructRelation(entityClass, propertyName, fm);

            if (e.getValue() != null) {
                Map<String, Object> subScan = (Map<String, Object>) e.getValue();
                PathValue nextValue = new PathValue();
                constructPath(fm.getFetchField().getType(), nextValue, subScan);
                relationKeys.put(relationKey, nextValue);
            } else {
                relationKeys.put(relationKey, null);
            }
        }
    }

    private RelationKey constructRelation(Class entityClass, String propertyName, FetchMapping fm) {
        RelationKey relationKey = new RelationKey(entityClass, propertyName);
        RelationValue relationValue = relationCache.get(relationKey);
        if (relationValue == null) {
            try{
            FieldMapping id = registry.getIdFields(fm.getFetchField().getType()).iterator().next();
            //todo aguzanov fetch relation by one of ids column, just for a while
            IQueryBuilder queryBuilder = em.queryBuilder(fm.getFetchField().getType())
                    .where(Condition.in(id.getPropertyName()));

            relationValue = new RelationValue(id, fm, queryBuilder.create());
            relationCache.putIfAbsent(relationKey, relationValue);
            }catch (NullPointerException e){
                e.printStackTrace();
            }
        }

        return relationKey;
    }
}
