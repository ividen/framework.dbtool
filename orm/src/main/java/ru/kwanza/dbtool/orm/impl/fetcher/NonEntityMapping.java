package ru.kwanza.dbtool.orm.impl.fetcher;

import ru.kwanza.dbtool.orm.annotations.Association;
import ru.kwanza.dbtool.orm.annotations.ManyToOne;
import ru.kwanza.dbtool.orm.annotations.OneToMany;
import ru.kwanza.dbtool.orm.impl.mapping.EntityMappingHelper;
import ru.kwanza.dbtool.orm.impl.mapping.EntityMappingRegistry;
import ru.kwanza.dbtool.orm.impl.mapping.RelationMapping;

import javax.annotation.Resource;
import java.lang.reflect.AnnotatedElement;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author Alexander Guzanov
 */
class NonEntityMapping {
    @Resource(name = "dbtool.IEntityMappingRegistry")
    private EntityMappingRegistry registry;

    private ConcurrentMap<Class, Map<String, RelationMapping>> cache = new ConcurrentHashMap<Class, Map<String, RelationMapping>>();

    public Map<String, RelationMapping> get(Class entityClass) {
        Map<String, RelationMapping> mappings = cache.get(entityClass);
        if (mappings == null) {
            mappings = new HashMap<String, RelationMapping>();
            parseClass(entityClass, mappings);
            if (null != cache.putIfAbsent(entityClass, mappings)) {
                mappings = cache.get(entityClass);
            }
        }

        return mappings;
    }

    private void parseClass(Class aClass, Map<String, RelationMapping> mappings) {
        if (aClass == Object.class) {
            return;
        }

        processElements(aClass, mappings, aClass.getDeclaredFields());
        processElements(aClass, mappings, aClass.getDeclaredMethods());

        parseClass(aClass.getSuperclass(), mappings);
    }

    private void processElements(Class aClass, Map<String, RelationMapping> mappings, AnnotatedElement[] elements) {
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

    private void processAssociation(Class aClass, Map<String, RelationMapping> mappings, AnnotatedElement annotatedElement) {
        final RelationMapping relationMapping = registry.parseAssociation(aClass, annotatedElement);
        mappings.put(relationMapping.getName(), relationMapping);
    }

    private void processManyToOne(Class aClass, Map<String, RelationMapping> mappings, AnnotatedElement annotatedElement) {
        final RelationMapping relationMapping = registry.parseManyToOne(aClass, annotatedElement);
        mappings.put(relationMapping.getName(), relationMapping);
    }
}
