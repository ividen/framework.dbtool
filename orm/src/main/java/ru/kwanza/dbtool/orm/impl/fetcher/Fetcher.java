package ru.kwanza.dbtool.orm.impl.fetcher;

import ru.kwanza.dbtool.orm.annotations.GroupByType;
import ru.kwanza.dbtool.orm.api.*;
import ru.kwanza.dbtool.orm.impl.fetcher.proxy.ProxyCallback;
import ru.kwanza.dbtool.orm.impl.fetcher.proxy.ProxyEntry;
import ru.kwanza.dbtool.orm.impl.fetcher.proxy.ProxyFactory;
import ru.kwanza.dbtool.orm.impl.mapping.FieldMapping;
import ru.kwanza.dbtool.orm.impl.mapping.IEntityMappingRegistry;
import ru.kwanza.dbtool.orm.impl.mapping.RelationMapping;
import ru.kwanza.toolbox.SpringSerializable;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author Alexander Guzanov
 */
public class Fetcher extends SpringSerializable {
    @Resource(name = "dbtool.IEntityMappingRegistry")
    private IEntityMappingRegistry registry;
    @Resource(name = "dbtool.IEntityManager")
    private IEntityManager em;
    @Resource(name = "dbtool.ProxyFactory")
    private ProxyFactory factory;

    @Resource(name = "dbtool.NonEntityMapping")
    private NonEntityMapping nonEntityMapping;
    // this cache contains all paths, with is used to get relational fields
    private ConcurrentMap<PathKey, PathValue> pathCache = new ConcurrentHashMap<PathKey, PathValue>();
    // this cache contains all relations for all entities and queries used to read this relations
    private ConcurrentMap<RelationKey, RelationValue> relationCache = new ConcurrentHashMap<RelationKey, RelationValue>();

    public IEntityMappingRegistry getRegistry() {
        return registry;
    }

    public IEntityManager getEntityManager() {
        return em;
    }

    public ProxyFactory getProxyFactory() {
        return factory;
    }

    public <T> void fetch(Class<T> entityClass, Collection<T> items, String relationPath) {
        if (items == null || items.isEmpty()) {
            return;
        }
        PathValue value = getPathValue(entityClass, relationPath);

        fillItems(items, value);
    }

    public <T> void fetch(T object, String relationPath) {
        fetch((Class<T>) object.getClass(), Collections.singleton(object), relationPath);
    }

    public <T> void fetchLazy(Class<T> entityClass, Collection<T> items) {
        ProxyCallback.enterSafe();
        try {
            final Collection<RelationMapping> relationMappings = getFetchMappings(entityClass);
            if (relationMappings != null) {
                for (RelationMapping relationMapping : relationMappings) {
                    ProxyCallback batch =
                            new ProxyCallback(this, entityClass, items, relationMapping.getProperty().getType(), relationMapping.getName());
                    for (T item : items) {
                        if (relationMapping.getProperty().value(item) == null) {
                            relationMapping.getProperty().set(item, factory.newInstance(relationMapping.getProperty().getType(), batch));
                        }
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            ProxyCallback.exitSafe();
        }
    }

    public <T> void fetchLazy(T object) {
        fetchLazy((Class<T>) object.getClass(), Collections.singleton(object));
    }

    public <T> void doLazyFetch(Class<T> entityClass, Collection<T> items, String relationPath) {
        ProxyCallback.enterSafe();
        try {
            final RelationValue relationValue = getRelationValue(entityClass, relationPath);
            final Map<Object, Object> map = queryRelation(items, relationValue);
            ArrayList relationItems = new ArrayList();
            for (T item : items) {
                final Object dest = relationValue.getRelationMapping().getProperty().value(item);
                final Object key = relationValue.getRelationMapping().getKeyProperty().value(item);
                if (key != null) {
                    Object src = map.get(key);
                    if (src != null) {
                        if (relationValue.getRelationMapping().isCollection()) {
                            relationItems.addAll((Collection) src);
                        } else {
                            relationItems.add(src);
                        }
                        final ProxyEntry proxy = factory.get(relationValue.getRelationMapping().getProperty().getType());
                        if (relationValue.getRelationMapping().getGroupBy() != null) {
                            src = split(relationValue.getRelationMapping(), src);
                        }

                        proxy.setDelegate(dest, src);
                    }
                }
            }
            fetchLazy(relationValue.getRelationMapping().getRelationClass(), relationItems);
        } finally {
            ProxyCallback.exitSafe();
        }
    }

    private <T> PathValue getPathValue(Class<T> entityClass, String relationPath) {
        PathKey key = new PathKey(entityClass, relationPath);
        PathValue value = pathCache.get(key);

        if (value == null) {
            value = constructPathValue(relationPath, key);
        }
        return value;
    }

    public <T> void fillItems(Collection<T> items, PathValue value) {
        Map<RelationKey, Map> results = new HashMap<RelationKey, Map>(value.getRelationKeys().size());

        for (Map.Entry<RelationKey, PathValue> entry : value.getRelationKeys().entrySet()) {
            RelationKey relationKey = entry.getKey();
            RelationValue relationValue = relationCache.get(relationKey);
            Map<Object, Object> map = queryRelation(items, relationValue);
            if (!map.isEmpty()) {
                results.put(relationKey, map);
                if (entry.getValue() != null) {
                    fillItems(map.values(), entry.getValue());
                }
            }
        }

        for (T object : items) {
            for (Map.Entry<RelationKey, Map> entry : results.entrySet()) {
                RelationKey key = entry.getKey();
                Map realtion = entry.getValue();
                RelationValue relationValue = relationCache.get(key);
                RelationMapping relationMapping = relationValue.getRelationMapping();
                //todo aguzanov isCollection
                if (object instanceof Collection) {
                    Collection c = (Collection) object;
                    for (Object o : c) {
                        Object relationIDValue = relationMapping.getKeyProperty().value(o);
                        if (relationIDValue != null) {
                            Object relationObjValue = realtion.get(relationIDValue);
                            if (relationObjValue != null) {
                                if (relationMapping.getGroupBy() != null) {
                                    relationObjValue = split(relationMapping, relationObjValue);
                                }
                                relationMapping.getProperty().set(o, relationObjValue);
                            }
                        }
                    }
                } else {
                    Object relationIDValue = relationMapping.getKeyProperty().value(object);
                    if (relationIDValue != null) {
                        Object relationObjValue = realtion.get(relationIDValue);
                        if (relationObjValue != null) {
                            if (relationMapping.getGroupBy() != null) {
                                relationObjValue = split(relationMapping, relationObjValue);
                            }
                            relationMapping.getProperty().set(object, relationObjValue);
                        }
                    }
                }
            }
        }
    }

    private Object split(RelationMapping relationMapping, Object relationObjValue) {
        relationObjValue = relationMapping.getGroupByType() == GroupByType.ONE_TO_MANY ? relationMapping.getGroupBy()
                .oneToMany((Collection) relationObjValue) : relationMapping.getGroupBy().oneToOne((Collection) relationObjValue);
        return relationObjValue;
    }

    public <T> Map queryRelation(Collection<T> items, RelationValue relationValue) {
        final RelationMapping fm = relationValue.getRelationMapping();
        final Class type = fm.getProperty().getType();
        final Set relationIds = relationValue.getRelationIds(items);
        if (relationIds.isEmpty()) {
            return Collections.emptyMap();
        }

        if (fm.isCollection()) {
            Map<Object, List<T>> result = new HashMap<Object, List<T>>();
            ListProducer<T> producer;
            producer = gettListProducer(type);
            relationValue.getFetchQuery().prepare().setParameter(1, relationIds)
                    .selectMapList(relationValue.getIDGroupingField(), result, producer);
            return result;
        } else {
            Map<Object, T> result = new HashMap<Object, T>();
            relationValue.getFetchQuery().prepare().setParameter(1, relationIds).selectMap(relationValue.getIDGroupingField(), result);

            return result;
        }
    }

    private <T> ListProducer<T> gettListProducer(Class type) {
        ListProducer<T> producer;
        if (Collection.class == type || ArrayList.class == type || List.class == type || Map.class.isAssignableFrom(type)) {
            producer = ListProducer.ARRAY_LIST;
        } else if (LinkedList.class == type) {
            producer = ListProducer.LINKED_LIST;
        } else {
            producer = ListProducer.create(type);
        }
        return producer;
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

            RelationMapping fm;

            fm = getFetchMapping(entityClass, propertyName);

            RelationKey relationKey = getRelationKey(entityClass, propertyName, fm);

            if (e.getValue() != null) {
                Map<String, Object> subScan = (Map<String, Object>) e.getValue();
                PathValue nextValue = new PathValue();
                constructPath(fm.getRelationClass(), nextValue, subScan);
                relationKeys.put(relationKey, nextValue);
            } else {
                relationKeys.put(relationKey, null);
            }
        }
    }

    private RelationMapping getFetchMapping(Class entityClass, String propertyName) {
        RelationMapping fm;
        if (registry.isRegisteredEntityClass(entityClass)) {
            fm = registry.getRelationMapping(entityClass, propertyName);
            if (fm == null) {
                throw new IllegalArgumentException("Wrong relation name! ManyToOne/OneToMany/Association field mapping not found!");
            }
        } else {
            fm = nonEntityMapping.get(entityClass).get(propertyName);
            if (fm == null) {
                throw new IllegalArgumentException("Wrong relation name! ManyToOne/Association field mapping not found!");
            }
        }
        return fm;
    }

    private Collection<RelationMapping> getFetchMappings(Class entityClass) {
        Collection<RelationMapping> result;
        if (registry.isRegisteredEntityClass(entityClass)) {
            result = registry.getRelationMappings(entityClass);
        } else {
            result = nonEntityMapping.get(entityClass).values();
        }
        return result;
    }

    private RelationValue getRelationValue(Class entityClass, String propertyName) {
        RelationKey relationKey = new RelationKey(entityClass, propertyName);
        RelationValue relationValue = relationCache.get(relationKey);
        if (relationValue == null) {
            RelationMapping fm = getFetchMapping(entityClass, propertyName);
            relationValue = createRealtionValue(relationKey, fm);
        }

        return relationValue;
    }

    private RelationKey getRelationKey(Class entityClass, String propertyName, RelationMapping fm) {
        RelationKey relationKey = new RelationKey(entityClass, propertyName);
        RelationValue relationValue = relationCache.get(relationKey);
        if (relationValue == null) {
            createRealtionValue(relationKey, fm);
        }

        return relationKey;
    }

    private RelationValue createRealtionValue(RelationKey relationKey, RelationMapping fm) {
        RelationValue relationValue;
        FieldMapping relation = fm.getRelationKeyMapping();
        If condition = If.in(relation.getName());
        if (fm.getCondition() != null) {
            condition = If.and(condition, fm.getCondition());
        }
        IQueryBuilder queryBuilder = em.queryBuilder(fm.getRelationClass()).where(condition);
        if (fm.getJoins() != null) {
            for (Join join : fm.getJoins()) {
                queryBuilder.join(join);
            }
        }

        relationValue = new RelationValue(relation, fm, queryBuilder.create());
        relationCache.putIfAbsent(relationKey, relationValue);
        return relationValue;
    }
}
