package ru.kwanza.dbtool.orm.impl.mapping;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.kwanza.dbtool.orm.annotations.*;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Member;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static ru.kwanza.dbtool.orm.impl.mapping.EntityMappingHelper.*;

/**
 * @author Kiryl Karatsetski
 */
public class EntityMappingRegistryImpl implements IEntityMappingRegistry {

    private static final Logger log = LoggerFactory.getLogger(EntityMappingRegistryImpl.class);

    private Lock registerLock = new ReentrantLock();

    private Map<Class, String> tableNameByEntityClass = new HashMap<Class, String>();
    private Map<String, String> tableNameByEntityName = new HashMap<String, String>();

    private Map<String, Class> entityClassByEntityName = new HashMap<String, Class>();
    private Map<Class, String> entityNameByEntityClass = new HashMap<Class, String>();

    private Map<Class, Collection<String>> columnNamesByEntityClass = new HashMap<Class, Collection<String>>();
    private Map<String, Collection<String>> columnNamesByEntityName = new HashMap<String, Collection<String>>();

    private Map<Class, Collection<FieldMapping>> fieldMappingsByEntityClass = new HashMap<Class, Collection<FieldMapping>>();
    private Map<String, Collection<FieldMapping>> fieldMappingsByEntityName = new HashMap<String, Collection<FieldMapping>>();

    private Map<Class, Collection<FieldMapping>> idFieldMappingsByEntityClass = new HashMap<Class, Collection<FieldMapping>>();
    private Map<String, Collection<FieldMapping>> idFieldMappingsByEntityName = new HashMap<String, Collection<FieldMapping>>();

    private Map<Class, FieldMapping> versionFieldMappingByEntityClass = new HashMap<Class, FieldMapping>();
    private Map<String, FieldMapping> versionFieldMappingByEntityName = new HashMap<String, FieldMapping>();

    private Map<Class, Collection<FetchMapping>> fetchMappingByEntityClass = new HashMap<Class, Collection<FetchMapping>>();
    private Map<String, Collection<FetchMapping>> fetchMappingByEntityName = new HashMap<String, Collection<FetchMapping>>();

    private Map<Class, Map<String, FieldMapping>> fieldMappingByPropertyNameEntityClass = new HashMap<Class, Map<String, FieldMapping>>();
    private Map<String, Map<String, FieldMapping>> fieldMappingByPropertyNameEntityName = new HashMap<String, Map<String, FieldMapping>>();

    private Map<Class, Map<String, FetchMapping>> fetchMappingByPropertyNameEntityClass = new HashMap<Class, Map<String, FetchMapping>>();
    private Map<String, Map<String, FetchMapping>> fetchMappingByPropertyNameEntityName = new HashMap<String, Map<String, FetchMapping>>();

    public void registerEntityClass(Class entityClass) {
        registerLock.lock();
        try {
            processRegisterEntityClass(entityClass, entityClass);
        } finally {
            registerLock.unlock();
        }
    }

    public void processRegisterEntityClass(Class entityClass, Class targetClass) {

        if (Object.class.equals(targetClass)) {
            return;
        }

        if (entityClass == targetClass) {
            registerEntity(entityClass);
        }

        processRegisterEntityClass(entityClass, targetClass.getSuperclass());

        final java.lang.reflect.Field[] declaredFields = targetClass.getDeclaredFields();
        final java.lang.reflect.Method[] methods = targetClass.getDeclaredMethods();

        processFields(entityClass, declaredFields);
        processFields(entityClass, methods);

        processFetches(entityClass, declaredFields);
        processFetches(entityClass, methods);
    }

    public boolean isRegisteredEntityClass(Class entityClass) {
        return entityNameByEntityClass.containsKey(entityClass);
    }

    public boolean isRegisteredEntityName(String entityName) {
        return entityClassByEntityName.containsKey(entityName);
    }

    public void validateEntityMapping() {
        final Collection<FetchMapping> fetchMappings = new LinkedHashSet<FetchMapping>();
        for (Collection<FetchMapping> fetchMappingCollection : fetchMappingByEntityClass.values()) {
            fetchMappings.addAll(fetchMappingCollection);
        }
        for (FetchMapping fetchMapping : fetchMappings) {
            final Class fetchFieldClass = fetchMapping.getFetchField().getType();
            if (!entityNameByEntityClass.containsKey(fetchFieldClass)) {
                throw new RuntimeException("Unknown class relation in mapping registry: " + fetchFieldClass);
            }
        }
    }

    private void processFields(Class entityClass, AnnotatedElement[] annotatedElements) {
        for (AnnotatedElement annotatedElement : annotatedElements) {

            if (isTransient(annotatedElement)) {
                continue;
            }

            if (annotatedElement.isAnnotationPresent(Field.class)) {
                final FieldMapping fieldMapping = createFieldMapping(annotatedElement, annotatedElement.getAnnotation(Field.class));
                addFieldMapping(entityClass, fieldMapping);
            } else if (annotatedElement.isAnnotationPresent(IdField.class)) {
                final FieldMapping fieldMapping = createFieldMapping(annotatedElement, annotatedElement.getAnnotation(IdField.class));
                addIdFieldMapping(entityClass, fieldMapping);
            } else if (annotatedElement.isAnnotationPresent(VersionField.class)) {
                final FieldMapping fieldMapping = createFieldMapping(annotatedElement, annotatedElement.getAnnotation(VersionField.class));
                addVersionFieldMapping(entityClass, fieldMapping);
            }
        }
    }

    private void processFetches(Class entityClass, AnnotatedElement[] annotatedElements) {
        for (AnnotatedElement annotatedElement : annotatedElements) {

            if (isTransient(annotatedElement)) {
                continue;
            }

            if (annotatedElement.isAnnotationPresent(ManyToOne.class)) {
                final ManyToOne manyToOne = annotatedElement.getAnnotation(ManyToOne.class);
                final EntityField propertyField = getFetchPropertyField(entityClass, manyToOne.property());
                final FetchMapping fetchMapping = createFetchMapping(annotatedElement, propertyField);
                addFetchMapping(entityClass, fetchMapping);
            }
        }
    }

    private boolean isTransient(AnnotatedElement annotatedElement) {
        return annotatedElement instanceof Member && Modifier.isTransient(((Member) annotatedElement).getModifiers());
    }

    private EntityField getFetchPropertyField(Class entityClass, String fetchedPropertyName) {
        final Map<String, FieldMapping> fieldMappingByPropertyName = fieldMappingByPropertyNameEntityClass.get(entityClass);
        final FieldMapping fieldMapping = fieldMappingByPropertyName != null ? fieldMappingByPropertyName.get(fetchedPropertyName) : null;
        return fieldMapping != null ? fieldMapping.getEntityFiled() : null;
    }

    @SuppressWarnings("unchecked")
    private void registerEntity(Class entityClass) {
        if (!entityClass.isAnnotationPresent(Entity.class)) {
            throw new RuntimeException("Entity class must be annotated by @Entity: " + entityClass);
        }
        final Entity entity = (Entity) entityClass.getAnnotation(Entity.class);
        final String entityName = getEntityNameFromAnnotation(entity, entityClass);
        final String tableName = getEntityTableName(entity, entityClass);
        if (!entityNameByEntityClass.containsKey(entityClass) && !entityClassByEntityName.containsKey(entityName)) {
            entityNameByEntityClass.put(entityClass, entityName);
            entityClassByEntityName.put(entityName, entityClass);
            tableNameByEntityClass.put(entityClass, tableName);
            tableNameByEntityName.put(getEntityName(entityClass), tableName);

            logRegisterEntity(entityClass, entityName, tableName);

        } else {
            throw new RuntimeException("Duplicate entity class " + entityClass + " or entity name class " + entityName);
        }
    }

    private void registerColumnName(Class entityClass, String columnName) {
        Collection<String> columnNames = columnNamesByEntityClass.get(entityClass);
        if (columnNames == null) {
            columnNames = new LinkedHashSet<String>();
            columnNamesByEntityClass.put(entityClass, columnNames);
            columnNamesByEntityName.put(getEntityName(entityClass), columnNames);
        }

        if (columnNames.contains(columnName)) {
            throw new RuntimeException("Duplicate column name '" + columnName + "' in class " + entityClass);
        }

        columnNames.add(columnName);

        logRegisterColumn(entityClass, columnName);
    }

    private void addFieldMapping(Class entityClass, FieldMapping fieldMapping) {
        Collection<FieldMapping> fieldMappings = fieldMappingsByEntityClass.get(entityClass);
        if (fieldMappings == null) {
            fieldMappings = new LinkedHashSet<FieldMapping>();
            fieldMappingsByEntityClass.put(entityClass, fieldMappings);
            fieldMappingsByEntityName.put(getEntityName(entityClass), fieldMappings);
        }

        registerColumnName(entityClass, fieldMapping.getColumnName());
        addFieldMappingByPropertyName(entityClass, fieldMapping);

        fieldMappings.add(fieldMapping);

        logRegisterFieldMapping(entityClass, fieldMapping);
    }

    private void addIdFieldMapping(Class entityClass, FieldMapping fieldMapping) {
        Collection<FieldMapping> fieldMappings = idFieldMappingsByEntityClass.get(entityClass);
        if (fieldMappings == null) {
            fieldMappings = new LinkedHashSet<FieldMapping>();
            idFieldMappingsByEntityClass.put(entityClass, fieldMappings);
            idFieldMappingsByEntityName.put(getEntityName(entityClass), fieldMappings);
        }
        fieldMappings.add(fieldMapping);

        addFieldMapping(entityClass, fieldMapping);
    }

    private void addVersionFieldMapping(Class entityClass, FieldMapping fieldMapping) {
        versionFieldMappingByEntityClass.put(entityClass, fieldMapping);
        versionFieldMappingByEntityName.put(getEntityName(entityClass), fieldMapping);

        addFieldMapping(entityClass, fieldMapping);
    }

    private void addFetchMapping(Class entityClass, FetchMapping fetchMapping) {
        Collection<FetchMapping> fetchMappings = fetchMappingByEntityClass.get(entityClass);
        if (fetchMappings == null) {
            fetchMappings = new LinkedHashSet<FetchMapping>();
            fetchMappingByEntityClass.put(entityClass, fetchMappings);
            fetchMappingByEntityName.put(getEntityName(entityClass), fetchMappings);
        }

        addFetchMappingByPropertyName(entityClass, fetchMapping);

        fetchMappings.add(fetchMapping);

        logRegisterFetchMapping(entityClass, fetchMapping);
    }

    private void addFieldMappingByPropertyName(Class entityClass, FieldMapping fieldMapping) {
        Map<String, FieldMapping> fieldMappingByPropertyName = fieldMappingByPropertyNameEntityClass.get(entityClass);
        if (fieldMappingByPropertyName == null) {
            fieldMappingByPropertyName = new LinkedHashMap<String, FieldMapping>();
            fieldMappingByPropertyNameEntityClass.put(entityClass, fieldMappingByPropertyName);
            fieldMappingByPropertyNameEntityName.put(getEntityName(entityClass), fieldMappingByPropertyName);
        }

        final String propertyName = fieldMapping.getPropertyName();

        if (fieldMappingByPropertyName.containsKey(propertyName)) {
            throw new RuntimeException("Duplicate property name '" + propertyName + "' in class " + entityClass);
        }

        fieldMappingByPropertyName.put(propertyName, fieldMapping);
    }

    private void addFetchMappingByPropertyName(Class entityClass, FetchMapping fetchMapping) {
        Map<String, FetchMapping> fetchMappingByPropertyName = fetchMappingByPropertyNameEntityClass.get(entityClass);
        if (fetchMappingByPropertyName == null) {
            fetchMappingByPropertyName = new LinkedHashMap<String, FetchMapping>();
            fetchMappingByPropertyNameEntityClass.put(entityClass, fetchMappingByPropertyName);
            fetchMappingByPropertyNameEntityName.put(getEntityName(entityClass), fetchMappingByPropertyName);
        }

        final String propertyName = fetchMapping.getPropertyName();

        if (fetchMappingByPropertyName.containsKey(propertyName)) {
            throw new RuntimeException("Duplicate property name '" + propertyName + "' in class " + entityClass);
        }

        fetchMappingByPropertyName.put(propertyName, fetchMapping);
    }

    public String getTableName(Class entityClass) {
        return tableNameByEntityClass.get(entityClass);
    }

    public String getTableName(String entityName) {
        return tableNameByEntityName.get(entityName);
    }

    public Class getEntityClass(String entityName) {
        return entityClassByEntityName.get(entityName);
    }

    public String getEntityName(Class entityClass) {
        return entityNameByEntityClass.get(entityClass);
    }

    public Collection<String> getColumnNames(Class entityClass) {
        return columnNamesByEntityClass.get(entityClass);
    }

    public Collection<String> getColumnNames(String entityName) {
        return columnNamesByEntityName.get(entityName);
    }

    public Collection<FieldMapping> getFieldMappings(Class entityClass) {
        return fieldMappingsByEntityClass.get(entityClass);
    }

    public Collection<FieldMapping> getFieldMappings(String entityName) {
        return fieldMappingsByEntityName.get(entityName);
    }

    public Collection<FieldMapping> getIdFields(Class entityClass) {
        return idFieldMappingsByEntityClass.get(entityClass);
    }

    public Collection<FieldMapping> getIdFields(String entityName) {
        return idFieldMappingsByEntityName.get(entityName);
    }

    public FieldMapping getVersionField(Class entityClass) {
        return versionFieldMappingByEntityClass.get(entityClass);
    }

    public FieldMapping getVersionField(String entityName) {
        return versionFieldMappingByEntityName.get(entityName);
    }

    public Collection<FetchMapping> getFetchMapping(Class entityClass) {
        return fetchMappingByEntityClass.get(entityClass);
    }

    public Collection<FetchMapping> getFetchMapping(String entityName) {
        return fetchMappingByEntityName.get(entityName);
    }

    public FieldMapping getFieldMappingByPropertyName(Class entityClass, String propertyName) {
        final Map<String, FieldMapping> fieldMappingByPropertyName = fieldMappingByPropertyNameEntityClass.get(entityClass);
        return fieldMappingByPropertyName != null ? fieldMappingByPropertyName.get(propertyName) : null;
    }

    public FieldMapping getFieldMappingByPropertyName(String entityName, String propertyName) {
        final Map<String, FieldMapping> fieldMappingByPropertyName = fieldMappingByPropertyNameEntityName.get(entityName);
        return fieldMappingByPropertyName != null ? fieldMappingByPropertyName.get(propertyName) : null;
    }

    public FetchMapping getFetchMappingByPropertyName(Class entityClass, String fieldName) {
        final Map<String, FetchMapping> fieldMappingByPropertyName = fetchMappingByPropertyNameEntityClass.get(entityClass);
        return fieldMappingByPropertyName != null ? fieldMappingByPropertyName.get(fieldName) : null;
    }

    public FetchMapping getFetchMappingByPropertyName(String entityName, String fieldName) {
        final Map<String, FetchMapping> fieldMappingByPropertyName = fetchMappingByPropertyNameEntityName.get(entityName);
        return fieldMappingByPropertyName != null ? fieldMappingByPropertyName.get(fieldName) : null;
    }

    private static void logRegisterEntity(Class entityClass, String entityName, String tableName) {
        log.trace("Register entity '{}' with '{}' name and '{}' table name", new Object[]{entityClass, entityName, tableName});
    }

    private static void logRegisterColumn(Class entityClass, String columnName) {
        log.trace("{}: Register column '{}'", new Object[]{entityClass, columnName});
    }

    private static void logRegisterFieldMapping(Class entityClass, FieldMapping fieldMapping) {
        log.trace("{}: Register Field Mapping {}", new Object[]{entityClass, fieldMapping});
    }

    private static void logRegisterFetchMapping(Class entityClass, FetchMapping fetchMapping) {
        log.trace("{}: Register ManyToOne Mapping {}", new Object[]{entityClass, fetchMapping});
    }
}
