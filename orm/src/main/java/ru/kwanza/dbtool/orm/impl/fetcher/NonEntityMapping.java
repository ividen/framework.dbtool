package ru.kwanza.dbtool.orm.impl.fetcher;

import ru.kwanza.dbtool.orm.annotations.Association;
import ru.kwanza.dbtool.orm.annotations.ManyToOne;
import ru.kwanza.dbtool.orm.annotations.OneToMany;
import ru.kwanza.dbtool.orm.api.internal.IEntityMappingRegistry;
import ru.kwanza.dbtool.orm.api.internal.IEntityType;
import ru.kwanza.dbtool.orm.api.internal.IRelationMapping;
import ru.kwanza.dbtool.orm.impl.mapping.EntityMappingHelper;
import ru.kwanza.dbtool.orm.impl.mapping.EntityMappingRegistry;

import javax.annotation.Resource;
import java.lang.reflect.AnnotatedElement;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author Alexander Guzanov
 */
class NonEntityMapping implements IEntityMappingRegistry {
    @Resource(name = "dbtool.IEntityMappingRegistry")
    private EntityMappingRegistry registry;

    private ConcurrentMap<Class, IEntityType> cache = new ConcurrentHashMap<Class, IEntityType>();

    private void parseClass(Class aClass, Map<String, IRelationMapping> mappings) {
        if (aClass == Object.class) {
            return;
        }

        processElements(aClass, mappings, aClass.getDeclaredFields());
        processElements(aClass, mappings, aClass.getDeclaredMethods());

        parseClass(aClass.getSuperclass(), mappings);
    }

    private void processElements(Class aClass, Map<String, IRelationMapping> mappings, AnnotatedElement[] elements) {
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

    private void processAssociation(Class aClass, Map<String, IRelationMapping> mappings, AnnotatedElement annotatedElement) {
        final IRelationMapping relationMapping = registry.parseAssociation(aClass, annotatedElement);
        mappings.put(relationMapping.getName(), relationMapping);
    }

    private void processManyToOne(Class aClass, Map<String, IRelationMapping> mappings, AnnotatedElement annotatedElement) {
        final IRelationMapping relationMapping = registry.parseManyToOne(aClass, annotatedElement);
        mappings.put(relationMapping.getName(), relationMapping);
    }

    public void registerEntityClass(Class entityClass) {
        IEntityType type = cache.get(entityClass);
        if (type == null) {
            Map<String, IRelationMapping> relations = new HashMap<String, IRelationMapping>();
            parseClass(entityClass, relations);
            type = new NonORMEntity(entityClass, relations);
            if (null != cache.putIfAbsent(entityClass, type)) {
                type = cache.get(entityClass);
            }
        }

    }

    public boolean isRegisteredEntityClass(Class entityClass) {
        return cache.containsKey(entityClass);
    }

    public boolean isRegisteredEntityName(String entityName) {
        throw new UnsupportedOperationException();
    }

    public IEntityType getEntityType(String name) {
        throw new UnsupportedOperationException();
    }

    public IEntityType getEntityType(Class name) {
        return cache.get(name);
    }
}
