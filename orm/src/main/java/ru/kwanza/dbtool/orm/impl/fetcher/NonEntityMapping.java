package ru.kwanza.dbtool.orm.impl.fetcher;

import ru.kwanza.dbtool.orm.annotations.Association;
import ru.kwanza.dbtool.orm.annotations.ManyToOne;
import ru.kwanza.dbtool.orm.annotations.OneToMany;
import ru.kwanza.dbtool.orm.impl.mapping.*;

import javax.annotation.Resource;
import java.lang.reflect.AnnotatedElement;
import java.sql.Types;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author Alexander Guzanov
 */
class NonEntityMapping {
    @Resource(name="dbtool.IEntityMappingRegistry")
    private IEntityMappingRegistry registry;

    private ConcurrentMap<Class, Map<String, FetchMapping>> cache =
            new ConcurrentHashMap<Class, Map<String, FetchMapping>>();


    public Map<String, FetchMapping> get(Class entityClass) {
        Map<String, FetchMapping> mappings = cache.get(entityClass);
        if (mappings == null) {
            mappings = new HashMap<String, FetchMapping>();
            parseClass(entityClass, mappings);
            if (null != cache.putIfAbsent(entityClass, mappings)) {
                mappings = cache.get(entityClass);
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
