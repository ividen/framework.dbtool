package ru.kwanza.dbtool.orm.impl.fetcher;

import ru.kwanza.dbtool.orm.annotations.Association;
import ru.kwanza.dbtool.orm.annotations.ManyToOne;
import ru.kwanza.dbtool.orm.annotations.OneToMany;
import ru.kwanza.dbtool.orm.api.Condition;
import ru.kwanza.dbtool.orm.api.IEntityManager;
import ru.kwanza.dbtool.orm.api.IQueryBuilder;
import ru.kwanza.dbtool.orm.api.ListProducer;
import ru.kwanza.dbtool.orm.impl.mapping.*;

import java.lang.reflect.AnnotatedElement;
import java.sql.Types;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author Alexander Guzanov
 */
public class FetcherImpl {
    private IEntityMappingRegistry registry;
    private IEntityManager em;
    // this cache contains all paths, with is used to get relational fields
    private ConcurrentMap<PathKey, PathValue> pathCache = new ConcurrentHashMap<PathKey, PathValue>();
    // this cache contains all relations for all entities and queries used to read this relations
    private ConcurrentMap<RelationKey, RelationValue> relationCache = new ConcurrentHashMap<RelationKey, RelationValue>();
    // this cache contains fetch config for non-entity classes
    private ConcurrentMap<Class, Map<String, FetchMapping>> noEntityRelationCache =
            new ConcurrentHashMap<Class, Map<String, FetchMapping>>();

    public FetcherImpl(IEntityMappingRegistry registry, IEntityManager em) {
        this.registry = registry;
        this.em = em;
    }

    public <T> void fetch(Class<T> entityClass, Collection<T> items, String relationPath) {
        if (items == null || items.isEmpty()) {
            return;
        }
        PathKey key = new PathKey(entityClass, relationPath);
        PathValue value = pathCache.get(key);

        if (value == null) {
            value = constructPathValue(relationPath, key);
        }

        fillItems(items, value);
    }

    public <T> void fetch(T object, String relationPath) {
        fetch((Class<T>) object.getClass(), Collections.singleton(object), relationPath);
    }

    private <T> void fillItems(Collection<T> items, PathValue value) {
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
                FetchMapping fetchMapping = relationValue.getFetchMapping();
                if (object instanceof Collection) {
                    Collection c = (Collection) object;
                    for (Object o : c) {
                        Object relationIDValue = fetchMapping.getPropertyField().getValue(o);
                        if (relationIDValue != null) {
                            Object relationObjValue = realtion.get(relationIDValue);
                            if (relationObjValue != null) {
                                fetchMapping.getFetchField().setValue(o, relationObjValue);
                            }
                        }
                    }
                } else {
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
    }

    private <T> Map queryRelation(Collection<T> items, RelationValue relationValue) {
        final FetchMapping fm = relationValue.getFetchMapping();
        final Class type = fm.getFetchField().getType();
        if (Collection.class.isAssignableFrom(type)) {
            Map<Object, List<T>> result = new HashMap<Object, List<T>>();
            ListProducer<T> producer;
            if (Collection.class == type || LinkedList.class == type || List.class == type) {
                producer = ListProducer.LINKED_LIST;
            } else if (ArrayList.class == type) {
                producer = ListProducer.ARRAY_LIST;
            } else {
                producer = new ListProducer<T>() {
                    @Override
                    public List<T> create() {
                        try {
                            return (List<T>) type.newInstance();
                        } catch (Exception e) {
                            throw new RuntimeException("Can't instantiate list for relation " + fm.toString());
                        }
                    }
                };
            }
            relationValue.getFetchQuery().prepare().setParameter(1, relationValue.getRelationIds(items))
                    .selectMapList(relationValue.getIDGroupingField(), result, producer);
            return result;
        } else {
            Map<Object, T> result = new HashMap<Object, T>();
            relationValue.getFetchQuery().prepare().setParameter(1, relationValue.getRelationIds(items))
                    .selectMap(relationValue.getIDGroupingField(), result);
            return result;
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

            FetchMapping fm;

            if (registry.isRegisteredEntityClass(entityClass)) {
                fm = registry.getFetchMappingByPropertyName(entityClass, propertyName);
                if (fm == null) {
                    throw new IllegalArgumentException("Wrong relation name! ManyToOne/OneToMany/Association field mapping not found!");
                }
            } else {
                fm = getNonEntityMappings(entityClass).get(propertyName);
                if (fm == null) {
                    throw new IllegalArgumentException("Wrong relation name! ManyToOne/Association field mapping not found!");
                }
            }

            RelationKey relationKey = constructRelation(entityClass, propertyName, fm);

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

    private RelationKey constructRelation(Class entityClass, String propertyName, FetchMapping fm) {
        RelationKey relationKey = new RelationKey(entityClass, propertyName);
        RelationValue relationValue = relationCache.get(relationKey);
        if (relationValue == null) {
            FieldMapping relation = fm.getRelationPropertyMapping();
            IQueryBuilder queryBuilder = em.queryBuilder(fm.getRelationClass()).where(Condition.in(relation.getName()));

            relationValue = new RelationValue(relation, fm, queryBuilder.create());
            relationCache.putIfAbsent(relationKey, relationValue);
        }

        return relationKey;
    }

    private Map<String, FetchMapping> getNonEntityMappings(Class entityClass) {
        Map<String, FetchMapping> mappings = noEntityRelationCache.get(entityClass);
        if (mappings == null) {
            mappings = new HashMap<String, FetchMapping>();
            parseClass(entityClass, mappings);
            if (null != noEntityRelationCache.putIfAbsent(entityClass, mappings)) {
                mappings = noEntityRelationCache.get(entityClass);
            }
        }

        return mappings;
    }

    private void parseClass(Class aClass, Map<String, FetchMapping> mappings) {
        if (aClass == Object.class) {
            return;
        }

        processElements(aClass, mappings, aClass.getDeclaredFields());
        processElements(aClass, mappings, aClass.getDeclaredMethods());

        parseClass(aClass.getSuperclass(), mappings);
    }

    private void processElements(Class aClass, Map<String, FetchMapping> mappings, AnnotatedElement[] elements) {
        for (AnnotatedElement field : elements) {
            if (field.isAnnotationPresent(ManyToOne.class)) {
                processManyToOne(aClass, mappings, field);
            } else if (field.isAnnotationPresent(Association.class)) {
                processAssociation(aClass, mappings, field);
            } else if (field.isAnnotationPresent(OneToMany.class)) {
                throw new IllegalStateException("@OneToMany in " + aClass.getName() + "." + EntityMappingHelper.getPropertyName(field)
                        + " is not supported for  non-entity relation fetching!");
            }
        }
    }

    private void processAssociation(Class aClass, Map<String, FetchMapping> mappings, AnnotatedElement field) {
        final String propertyName = EntityMappingHelper.getPropertyName(field);
        final EntityField fetchField = EntityMappingHelper.createEntityField(field);

        final Association association = field.getAnnotation(Association.class);
        final String property = association.property();
        final String relationProperty = association.relationProperty();
        final Class relationClass =
                Collection.class.isAssignableFrom(fetchField.getType()) ? association.relationClass() : fetchField.getType();
        final AnnotatedElement propertyField = EntityMappingHelper.findField(aClass, property);

        mappings.put(propertyName, new FetchMapping(propertyName, relationClass,
                new FieldMapping(property, null, Types.BIGINT, false, EntityMappingHelper.createEntityField(propertyField)),
                registry.getFieldMappingByPropertyName(relationClass, relationProperty), fetchField));
    }

    private void processManyToOne(Class aClass, Map<String, FetchMapping> mappings, AnnotatedElement field) {
        final String propertyName = EntityMappingHelper.getPropertyName(field);
        final EntityField fetchField = EntityMappingHelper.createEntityField(field);

        final ManyToOne manyToOne = field.getAnnotation(ManyToOne.class);
        final String property = manyToOne.property();
        final AnnotatedElement propertyField = EntityMappingHelper.findField(aClass, property);
        final Class relationClass = fetchField.getType();

        mappings.put(propertyName, new FetchMapping(propertyName, relationClass,
                new FieldMapping(property, null, Types.BIGINT, false, EntityMappingHelper.createEntityField(propertyField)),
                registry.getIdFields(relationClass).iterator().next(), fetchField));
    }
}
