package ru.kwanza.dbtool.orm.impl.mapping;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import ru.kwanza.dbtool.orm.annotations.*;
import ru.kwanza.dbtool.orm.api.If;
import ru.kwanza.toolbox.fieldhelper.FieldHelper;
import ru.kwanza.toolbox.fieldhelper.Property;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Member;
import java.lang.reflect.Modifier;
import java.sql.Types;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static ru.kwanza.dbtool.orm.impl.mapping.EntityMappingHelper.*;

/**
 * @author Kiryl Karatsetski
 */
public class EntityMappingRegistry implements IEntityMappingRegistry {

    private static final Logger log = LoggerFactory.getLogger(EntityMappingRegistry.class);

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

    private Map<Class, Collection<RelationMapping>> fetchMappingByEntityClass = new HashMap<Class, Collection<RelationMapping>>();
    private Map<String, Collection<RelationMapping>> fetchMappingByEntityName = new HashMap<String, Collection<RelationMapping>>();

    private Map<Class, Map<String, FieldMapping>> fieldMappingByPropertyNameEntityClass = new HashMap<Class, Map<String, FieldMapping>>();
    private Map<String, Map<String, FieldMapping>> fieldMappingByPropertyNameEntityName = new HashMap<String, Map<String, FieldMapping>>();

    private Map<Class, Map<String, RelationMapping>> fetchMappingByPropertyNameEntityClass = new HashMap<Class, Map<String, RelationMapping>>();
    private Map<String, Map<String, RelationMapping>> fetchMappingByPropertyNameEntityName = new HashMap<String, Map<String, RelationMapping>>();

    private ExpressionParser conditionParser = new SpelExpressionParser();

    public void registerEntityClass(Class entityClass) {
        registerLock.lock();
        try {
            if (!entityNameByEntityClass.containsKey(entityClass)) {
                processRegisterEntityClass(entityClass, entityClass);
            }
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
        final Collection<RelationMapping> relationMappings = new LinkedHashSet<RelationMapping>();
        for (Collection<RelationMapping> relationMappingCollection : fetchMappingByEntityClass.values()) {
            relationMappings.addAll(relationMappingCollection);
        }
        for (RelationMapping relationMapping : relationMappings) {
            final Class fetchFieldClass = relationMapping.getProperty().getType();
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
                final FieldMapping fieldMapping =
                        createFieldMapping(entityClass, annotatedElement, annotatedElement.getAnnotation(Field.class));
                addFieldMapping(entityClass, fieldMapping);
            } else if (annotatedElement.isAnnotationPresent(IdField.class)) {
                final FieldMapping fieldMapping =
                        createFieldMapping(entityClass, annotatedElement, annotatedElement.getAnnotation(IdField.class));
                addIdFieldMapping(entityClass, fieldMapping);
            } else if (annotatedElement.isAnnotationPresent(VersionField.class)) {
                final FieldMapping fieldMapping =
                        createFieldMapping(entityClass, annotatedElement, annotatedElement.getAnnotation(VersionField.class));
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
                processManyToOne(entityClass, annotatedElement);
            } else if (annotatedElement.isAnnotationPresent(OneToMany.class)) {
                processOneToMany(entityClass, annotatedElement);
            } else if (annotatedElement.isAnnotationPresent(Association.class)) {
                processAssociation(entityClass, annotatedElement);
            }
        }
    }

    private void processAssociation(final Class entityClass, AnnotatedElement annotatedElement) {
        addFetchMapping(entityClass, parseAssociation(entityClass, annotatedElement));
    }

    private void processOneToMany(final Class entityClass, final AnnotatedElement annotatedElement) {
        addFetchMapping(entityClass, parseOneToMany(entityClass, annotatedElement));
    }

    private void processManyToOne(final Class entityClass, final AnnotatedElement annotatedElement) {
        addFetchMapping(entityClass, parseManyToOne(entityClass, annotatedElement));
    }

    public RelationMapping parseAssociation(final Class entityClass, final AnnotatedElement element) {
        final Association association = element.getAnnotation(Association.class);
        final String name = getPropertyName(element);
        final Map<String, FieldMapping> mapping = fieldMappingByPropertyNameEntityClass.get(entityClass);
        final FieldMapping propertyMapping = mapping != null
                ? mapping.get(association.property())
                : new FieldMapping(name, null, Types.BIGINT, false, FieldHelper.constructProperty(entityClass, association.property()));
        final Property fetchField = FieldHelper.constructProperty(entityClass, name);
        final Class relationClass =
                Collection.class.isAssignableFrom(fetchField.getType()) ? association.relationClass() : fetchField.getType();
        if (relationClass == Object.class) {
            throw new RuntimeException(
                    "Relation @OneToMany in  " + entityClass.getName() + "." + name + " must have relativeClass() specified!");
        }
        final If condition = association.condition().isEmpty() ? null : parseCondition(association);
        final Property[] groupBy = association.groupBy().isEmpty() ? null : parseGroupBy(entityClass, association);

        if (!entityNameByEntityClass.containsKey(relationClass)) {
            this.registerEntityClass(relationClass);
        }

        FieldMapping relationPropertyMapping = getPropertyFieldMapping(relationClass, association.relationProperty());
        if (relationPropertyMapping == null) {
            throw new RuntimeException(
                    "Not found relational property mapping " + relationClass.getName() + "." + association.relationProperty()
                            + " for @Association " + entityClass.getName() + "." + name + "!");
        }

        return new RelationMapping(name, relationClass, propertyMapping, relationPropertyMapping, fetchField, condition, groupBy);
    }

    public RelationMapping parseOneToMany(final Class entityClass, final AnnotatedElement annotatedElement) {
        final OneToMany oneToMany = annotatedElement.getAnnotation(OneToMany.class);
        final String name = getPropertyName(annotatedElement);
        final FieldMapping propertyMapping = idFieldMappingsByEntityClass.get(entityClass).iterator().next();
        final Property fetchField = FieldHelper.constructProperty(entityClass, name);
        final Class relationClass =
                Collection.class.isAssignableFrom(fetchField.getType()) ? oneToMany.relationClass() : fetchField.getType();
        if (relationClass == Object.class) {
            throw new RuntimeException(
                    "Relation @OneToMany in  " + entityClass.getName() + "." + name + " must have relativeClass() specified!");
        }

        if (!entityNameByEntityClass.containsKey(relationClass)) {
            registerEntityClass(relationClass);
        }
        FieldMapping relationPropertyMapping = getPropertyFieldMapping(relationClass, oneToMany.relationProperty());
        if (relationPropertyMapping == null) {
            throw new RuntimeException(
                    "Not found relational property mapping " + relationClass.getName() + "." + oneToMany.relationProperty()
                            + " for @OneToMany " + entityClass.getName() + "." + name + "!");
        }

        return new RelationMapping(name, relationClass, propertyMapping, relationPropertyMapping, fetchField, null, null);
    }

    public RelationMapping parseManyToOne(final Class entityClass, final AnnotatedElement annotatedElement) {
        final ManyToOne manyToOne = annotatedElement.getAnnotation(ManyToOne.class);
        final String name = getPropertyName(annotatedElement);
        final Map<String, FieldMapping> mapping = fieldMappingByPropertyNameEntityClass.get(entityClass);
        final FieldMapping propertyMapping = mapping != null
                ? mapping.get(manyToOne.property())
                : new FieldMapping(name, null, Types.BIGINT, false, FieldHelper.constructProperty(entityClass, manyToOne.property()));
        if (propertyMapping == null) {
            throw new RuntimeException(
                    "Not found property " + manyToOne.property() + "for @ManyToOne " + entityClass.getName() + "." + getPropertyName(
                            annotatedElement) + "!");
        }

        final Property fetchField = FieldHelper.constructProperty(entityClass,name);
        final Class relationClass = fetchField.getType();
        if (!entityNameByEntityClass.containsKey(relationClass)) {
            registerEntityClass(relationClass);
        }
        final FieldMapping relationPropertyMapping = idFieldMappingsByEntityClass.get(relationClass).iterator().next();
        return new RelationMapping(name, relationClass, propertyMapping, relationPropertyMapping, fetchField, null, null);
    }

    private If parseCondition(Association association) {
        final Expression expression = conditionParser.parseExpression(association.condition());
        EvaluationContext ctx = new StandardEvaluationContext(If.class);
        return (If) expression.getValue(ctx, (Object) If.class);
    }

    private boolean isTransient(AnnotatedElement annotatedElement) {
        return annotatedElement instanceof Member && Modifier.isTransient(((Member) annotatedElement).getModifiers());
    }

    private FieldMapping getPropertyFieldMapping(Class entityClass, String fetchedPropertyName) {
        final Map<String, FieldMapping> fieldMappingByPropertyName = fieldMappingByPropertyNameEntityClass.get(entityClass);
        return fieldMappingByPropertyName != null ? fieldMappingByPropertyName.get(fetchedPropertyName) : null;
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

        registerColumnName(entityClass, fieldMapping.getColumn());
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

    private void addFetchMapping(Class entityClass, RelationMapping relationMapping) {
        Collection<RelationMapping> relationMappings = fetchMappingByEntityClass.get(entityClass);
        if (relationMappings == null) {
            relationMappings = new LinkedHashSet<RelationMapping>();
            fetchMappingByEntityClass.put(entityClass, relationMappings);
            fetchMappingByEntityName.put(getEntityName(entityClass), relationMappings);
        }

        addFetchMappingByPropertyName(entityClass, relationMapping);

        relationMappings.add(relationMapping);

        logRegisterFetchMapping(entityClass, relationMapping);
    }

    private void addFieldMappingByPropertyName(Class entityClass, FieldMapping fieldMapping) {
        Map<String, FieldMapping> fieldMappingByPropertyName = fieldMappingByPropertyNameEntityClass.get(entityClass);
        if (fieldMappingByPropertyName == null) {
            fieldMappingByPropertyName = new LinkedHashMap<String, FieldMapping>();
            fieldMappingByPropertyNameEntityClass.put(entityClass, fieldMappingByPropertyName);
            fieldMappingByPropertyNameEntityName.put(getEntityName(entityClass), fieldMappingByPropertyName);
        }

        final String propertyName = fieldMapping.getName();

        if (fieldMappingByPropertyName.containsKey(propertyName)) {
            throw new RuntimeException("Duplicate property name '" + propertyName + "' in class " + entityClass);
        }

        fieldMappingByPropertyName.put(propertyName, fieldMapping);
    }

    private void addFetchMappingByPropertyName(Class entityClass, RelationMapping relationMapping) {
        Map<String, RelationMapping> fetchMappingByPropertyName = fetchMappingByPropertyNameEntityClass.get(entityClass);
        if (fetchMappingByPropertyName == null) {
            fetchMappingByPropertyName = new LinkedHashMap<String, RelationMapping>();
            fetchMappingByPropertyNameEntityClass.put(entityClass, fetchMappingByPropertyName);
            fetchMappingByPropertyNameEntityName.put(getEntityName(entityClass), fetchMappingByPropertyName);
        }

        final String propertyName = relationMapping.getName();

        if (fetchMappingByPropertyName.containsKey(propertyName)) {
            throw new RuntimeException("Duplicate property name '" + propertyName + "' in class " + entityClass);
        }

        fetchMappingByPropertyName.put(propertyName, relationMapping);
    }

    private Property[] parseGroupBy(Class entityClass, Association association) {
        StringTokenizer st = new StringTokenizer(association.groupBy(), ",");
        ArrayList<Property> fields = new ArrayList<Property>();
        while (st.hasMoreTokens()) {
            String field = st.nextToken();
            fields.add(FieldHelper.constructProperty(entityClass, field));
        }

        return (Property[]) fields.toArray();
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

    public Collection<RelationMapping> getFetchMapping(Class entityClass) {
        return fetchMappingByEntityClass.get(entityClass);
    }

    public Collection<RelationMapping> getFetchMapping(String entityName) {
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

    public RelationMapping getFetchMappingByPropertyName(Class entityClass, String fieldName) {
        final Map<String, RelationMapping> fieldMappingByPropertyName = fetchMappingByPropertyNameEntityClass.get(entityClass);
        return fieldMappingByPropertyName != null ? fieldMappingByPropertyName.get(fieldName) : null;
    }

    public RelationMapping getFetchMappingByPropertyName(String entityName, String fieldName) {
        final Map<String, RelationMapping> fieldMappingByPropertyName = fetchMappingByPropertyNameEntityName.get(entityName);
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

    private static void logRegisterFetchMapping(Class entityClass, RelationMapping relationMapping) {
        log.trace("{}: Register ManyToOne Mapping {}", new Object[]{entityClass, relationMapping});
    }
}
