package ru.kwanza.dbtool.orm.impl.fetcher;

import ru.kwanza.dbtool.orm.annotations.GroupByType;
import ru.kwanza.dbtool.orm.api.Join;
import ru.kwanza.dbtool.orm.api.ListProducer;
import ru.kwanza.dbtool.orm.api.internal.IEntityType;
import ru.kwanza.dbtool.orm.api.internal.IRelationMapping;
import ru.kwanza.dbtool.orm.impl.EntityManagerImpl;
import ru.kwanza.dbtool.orm.impl.fetcher.proxy.Proxy;
import ru.kwanza.dbtool.orm.impl.fetcher.proxy.ProxyCallback;
import ru.kwanza.dbtool.orm.impl.fetcher.proxy.ProxyFactory;
import ru.kwanza.dbtool.orm.impl.querybuilder.JoinHelper;
import ru.kwanza.toolbox.SpringSerializable;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author Alexander Guzanov
 */
public class Fetcher extends SpringSerializable {
    @Resource(name = "dbtool.IEntityManager")
    private EntityManagerImpl em;
    @Resource(name = "dbtool.ProxyFactory")
    private ProxyFactory factory;

    @Resource(name = "dbtool.NonEntityMapping")
    private NonEntityMapping nonEntityMapping;

    private ConcurrentMap<FetchEntry, FetchKey> entries = new ConcurrentHashMap<FetchEntry, FetchKey>();
    private ConcurrentMap<FetchKey, List<FetchInfo>> fetchInfo = new ConcurrentHashMap<FetchKey, List<FetchInfo>>();


    public ProxyFactory getProxyFactory() {
        return factory;
    }

    public <T> void fetch(Class<T> entityClass, Collection<T> items, String relationPath) {
        if (items == null || items.isEmpty()) {
            return;
        }

        fetch(items, getFetchInfo(entityClass, relationPath));
    }

    public List<FetchInfo> getFetchInfo(Class entityClass, String relationPath) {
        return getFetchInfo(getKey(entityClass, relationPath));
    }

    public List<FetchInfo> getFetchInfo(Class entityClass, List<Join> joins) {
        return getFetchInfo(new FetchKey(entityClass, joins));
    }

    private List<FetchInfo> getFetchInfo(FetchKey key) {
        List<FetchInfo> result = fetchInfo.get(key);

        if (result == null) {
            result = new ArrayList<FetchInfo>();

            final IEntityType entityType = getEntityType(key.getEntityClass());

            for (Join join : key.getJoins()) {
                final IRelationMapping relation = entityType.getRelation(join.getPropertyName());
                if (relation == null) {
                    throw new IllegalArgumentException("Relation " + join.getPropertyName() + " not found  in " + key.getEntityClass());
                }

                result.add(new FetchInfo(em, relation, join.getSubJoins()));
            }
            if (null != fetchInfo.putIfAbsent(key, result)) {
                result = fetchInfo.get(key);
            }
        }

        return result;
    }


    private FetchKey getKey(Class entityClass, String relationPath) {
        final FetchEntry entry = new FetchEntry(entityClass, relationPath);
        FetchKey result = entries.get(entry);
        if (result == null) {
            result = new FetchKey(entityClass, JoinHelper.parse(relationPath));
            if (null != entries.putIfAbsent(entry, result)) {
                result = entries.get(entry);
            }
        }

        return result;
    }


    private IEntityType getEntityType(Class entityClass) {
        IEntityType entityType;
        if (em.getRegistry().isRegisteredEntityClass(entityClass)) {
            entityType = em.getRegistry().getEntityType(entityClass);
        } else {
            if (!nonEntityMapping.isRegisteredEntityClass(entityClass)) {
                nonEntityMapping.registerEntityClass(entityClass);
            }

            entityType = nonEntityMapping.getEntityType(entityClass);
        }
        return entityType;
    }

    public <T> void fetch(T object, String relationPath) {
        fetch((Class<T>) object.getClass(), Collections.singleton(object), relationPath);
    }

    public <T> void fetchLazy(Class<T> entityClass, Collection<T> items) {
        ProxyCallback.enterSafe();
        try {
            final Collection<IRelationMapping> relationMappings = getEntityType(entityClass).getRelations();
            if (relationMappings != null) {
                for (IRelationMapping relationMapping : relationMappings) {
                    ProxyCallback batch =
                            new ProxyCallback(this, entityClass, items, relationMapping.getProperty().getType(), relationMapping.getName());
                    for (T item : items) {
                        if (relationMapping.getProperty().value(item) == null && relationMapping.getKeyMapping().getProperty().value(item) != null) {
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

    public <T> void doLazyFetch(Class<T> entityClass, Collection<T> items, String propertyName) {
        ProxyCallback.enterSafe();
        try {
            final FetchInfo fetchInfo = getFetchInfo(entityClass, propertyName).get(0);
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

    private Object split(IRelationMapping relationMapping, Object relationObjValue) {
        relationObjValue = relationMapping.getGroupByType() == GroupByType.ONE_TO_MANY ? relationMapping.getGroupBy()
                .oneToMany((Collection) relationObjValue) : relationMapping.getGroupBy().oneToOne((Collection) relationObjValue);
        return relationObjValue;
    }

    public <T> Map queryRelation(Collection<T> items, FetchInfo fetchInfo) {
        final IRelationMapping relation = fetchInfo.getRelationMapping();
        final Class type = relation.getProperty().getType();
        final Set relationIds = fetchInfo.getRelationIds(items);
        if (relationIds.isEmpty()) {
            return Collections.emptyMap();
        }

        if (relation.isCollection()) {
            Map<Object, List<T>> result = new HashMap<Object, List<T>>();
            ListProducer<T> producer;
            producer = getListProducer(type);
            fetchInfo.getFetchQuery().prepare().setParameter(1, relationIds)
                    .selectMapList(fetchInfo.getIDGroupingField(), result, producer);
            return result;
        } else {
            Map<Object, T> result = new HashMap<Object, T>();
            fetchInfo.getFetchQuery().prepare().setParameter(1, relationIds).selectMap(fetchInfo.getIDGroupingField(), result);

            return result;
        }
    }

    private <T> ListProducer<T> getListProducer(Class type) {
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

}
