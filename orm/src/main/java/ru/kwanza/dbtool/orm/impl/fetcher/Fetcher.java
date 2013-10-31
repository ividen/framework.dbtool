package ru.kwanza.dbtool.orm.impl.fetcher;

import ru.kwanza.dbtool.orm.annotations.GroupByType;
import ru.kwanza.dbtool.orm.api.*;
import ru.kwanza.dbtool.orm.api.internal.IEntityMappingRegistry;
import ru.kwanza.dbtool.orm.api.internal.IFieldMapping;
import ru.kwanza.dbtool.orm.api.internal.IRelationMapping;
import ru.kwanza.dbtool.orm.impl.fetcher.proxy.Proxy;
import ru.kwanza.dbtool.orm.impl.fetcher.proxy.ProxyCallback;
import ru.kwanza.dbtool.orm.impl.fetcher.proxy.ProxyFactory;
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
    private ConcurrentMap<RelationKey, FetchInfo> relationCache = new ConcurrentHashMap<RelationKey, FetchInfo>();

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
            final Collection<IRelationMapping> relationMappings = getRelationMappings(entityClass);
            if (relationMappings != null) {
                for (IRelationMapping relationMapping : relationMappings) {
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
            final FetchInfo fetchInfo = getRelationValue(entityClass, relationPath);
            final Map<Object, Object> map = queryRelation(items, fetchInfo);
            ArrayList relationItems = new ArrayList();
            for (T item : items) {
                final Object dest = fetchInfo.getRelationMapping().getProperty().value(item);
                final Object key = fetchInfo.getRelationMapping().getKeyProperty().value(item);
                if (key != null) {
                    Object src = map.get(key);
                    if (src != null) {
                        if (fetchInfo.getRelationMapping().isCollection()) {
                            relationItems.addAll((Collection) src);
                        } else {
                            relationItems.add(src);
                        }
                        final Proxy proxy = factory.get(fetchInfo.getRelationMapping().getProperty().getType());
                        if (fetchInfo.getRelationMapping().getGroupBy() != null) {
                            src = split(fetchInfo.getRelationMapping(), src);
                        }

                        proxy.setDelegate(dest, src);
                    }
                }
            }
            fetchLazy(fetchInfo.getRelationMapping().getRelationClass(), relationItems);
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

    public <T> void fetch(Collection<T> items, List<FetchInfo> relations) {
        Map<FetchInfo, Map> results = new HashMap<FetchInfo, Map>(relations.size());

        for (FetchInfo relation : relations) {
            final Map map = queryRelation(items, relation);
            if (!map.isEmpty()) {
                results.put(relation, map);
            }

            for (T object : items) {
                for (Map.Entry<FetchInfo, Map> entry : results.entrySet()) {
                    FetchInfo key = entry.getKey();
                    Map realtion = entry.getValue();
                    IRelationMapping relationMapping = key.getRelationMapping();
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
    }

    private <T> void fillItems(Collection<T> items, PathValue value) {
        Map<RelationKey, Map> results = new HashMap<RelationKey, Map>(value.getRelationKeys().size());

        for (Map.Entry<RelationKey, PathValue> entry : value.getRelationKeys().entrySet()) {
            RelationKey relationKey = entry.getKey();
            FetchInfo fetchInfo = relationCache.get(relationKey);
            Map<Object, Object> map = queryRelation(items, fetchInfo);
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
                FetchInfo fetchInfo = relationCache.get(key);
                IRelationMapping relationMapping = fetchInfo.getRelationMapping();
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

    private Object split(IRelationMapping relationMapping, Object relationObjValue) {
        relationObjValue = relationMapping.getGroupByType() == GroupByType.ONE_TO_MANY ? relationMapping.getGroupBy()
                .oneToMany((Collection) relationObjValue) : relationMapping.getGroupBy().oneToOne((Collection) relationObjValue);
        return relationObjValue;
    }

    public <T> Map queryRelation(Collection<T> items, FetchInfo fetchInfo) {
        final IRelationMapping fm = fetchInfo.getRelationMapping();
        final Class type = fm.getProperty().getType();
        final Set relationIds = fetchInfo.getRelationIds(items);
        if (relationIds.isEmpty()) {
            return Collections.emptyMap();
        }

        if (fm.isCollection()) {
            Map<Object, List<T>> result = new HashMap<Object, List<T>>();
            ListProducer<T> producer;
            producer = gettListProducer(type);
            fetchInfo.getFetchQuery().prepare().setParameter(1, relationIds)
                    .selectMapList(fetchInfo.getIDGroupingField(), result, producer);
            return result;
        } else {
            Map<Object, T> result = new HashMap<Object, T>();
            fetchInfo.getFetchQuery().prepare().setParameter(1, relationIds).selectMap(fetchInfo.getIDGroupingField(), result);

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

            IRelationMapping fm;

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

    private IRelationMapping getFetchMapping(Class entityClass, String propertyName) {
        IRelationMapping relationMapping;
        if (registry.isRegisteredEntityClass(entityClass)) {
            relationMapping = registry.getEntityType(entityClass).getRelation(propertyName);
            if (relationMapping == null) {
                throw new IllegalArgumentException("Wrong relation name! ManyToOne/OneToMany/Association field mapping not found!");
            }
        } else {
            relationMapping = nonEntityMapping.get(entityClass).get(propertyName);
            if (relationMapping == null) {
                throw new IllegalArgumentException("Wrong relation name! ManyToOne/Association field mapping not found!");
            }
        }
        return relationMapping;
    }

    private Collection<IRelationMapping> getRelationMappings(Class entityClass) {
        Collection<IRelationMapping> result;
        if (registry.isRegisteredEntityClass(entityClass)) {
            result = registry.getEntityType(entityClass).getRelations();
        } else {
            result = nonEntityMapping.get(entityClass).values();
        }
        return result;
    }

    private FetchInfo getRelationValue(Class entityClass, String propertyName) {
        RelationKey relationKey = new RelationKey(entityClass, propertyName);
        FetchInfo fetchInfo = relationCache.get(relationKey);
        if (fetchInfo == null) {
            IRelationMapping fm = getFetchMapping(entityClass, propertyName);
            fetchInfo = createRelationValue(relationKey, fm);
        }

        return fetchInfo;
    }

    private RelationKey getRelationKey(Class entityClass, String propertyName, IRelationMapping fm) {
        RelationKey relationKey = new RelationKey(entityClass, propertyName);
        FetchInfo fetchInfo = relationCache.get(relationKey);
        if (fetchInfo == null) {
            createRelationValue(relationKey, fm);
        }

        return relationKey;
    }

    private FetchInfo createRelationValue(RelationKey relationKey, IRelationMapping relationMapping) {
        FetchInfo fetchInfo;
        IFieldMapping relation = relationMapping.getRelationKeyMapping();
        If condition = If.in(relation.getName());
        if (relationMapping.getCondition() != null) {
            condition = If.and(condition, relationMapping.getCondition());
        }
        IQueryBuilder queryBuilder = em.queryBuilder(relationMapping.getRelationClass()).where(condition);
        if (relationMapping.getJoins() != null) {
            for (Join join : relationMapping.getJoins()) {
                queryBuilder.join(join);
            }
        }

        fetchInfo = new FetchInfo(relation, relationMapping, queryBuilder.create());
        relationCache.putIfAbsent(relationKey, fetchInfo);
        return fetchInfo;
    }
}
