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
import ru.kwanza.dbtool.orm.api.Join;
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

    private Map<Class, String> tableNames = new HashMap<Class, String>();
    private Map<String, Class> entityClassByEntityName = new HashMap<String, Class>();
    private Map<Class, String> entityNameByEntityClass = new HashMap<Class, String>();

    private Map<Class, Collection<String>> columnNames = new HashMap<Class, Collection<String>>();
    private Map<Class, Collection<FieldMapping>> idFieldMappings = new HashMap<Class, Collection<FieldMapping>>();
    private Map<Class, FieldMapping> versionFieldMappings = new HashMap<Class, FieldMapping>();
    private Map<Class, Map<String, FieldMapping>> fieldMappings = new LinkedHashMap<Class, Map<String, FieldMapping>>();
    private Map<Class, Map<String, RelationMapping>> relationMappings = new LinkedHashMap<Class, Map<String, RelationMapping>>();

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
        final Map<String, FieldMapping> mapping = fieldMappings.get(entityClass);
        final FieldMapping propertyMapping = mapping != null
                ? mapping.get(association.property())
                : new FieldMapping(name, null, Types.BIGINT, false, FieldHelper.constructProperty(entityClass, association.property()));
        final Property fetchField = FieldHelper.constructProperty(entityClass, name);
        final Class relationClass = association.relationClass() != Object.class ? association.relationClass() : fetchField.getType();
        if (relationClass == Object.class) {
            throw new RuntimeException(
                    "Relation @OneToMany in  " + entityClass.getName() + "." + name + " must have relativeClass() specified!");
        }
        final If condition = association.condition().isEmpty() ? null : parseCondition(association);
        final Property[] groupBy = association.groupBy().isEmpty() ? null : parseGroupBy(relationClass, association);

        if (!entityNameByEntityClass.containsKey(relationClass)) {
            this.registerEntityClass(relationClass);
        }

        FieldMapping relationPropertyMapping = getPropertyFieldMapping(relationClass, association.relationProperty());
        if (relationPropertyMapping == null) {
            throw new RuntimeException(
                    "Not found relational property mapping " + relationClass.getName() + "." + association.relationProperty()
                            + " for @Association " + entityClass.getName() + "." + name + "!");
        }

        return new RelationMapping(name, relationClass, propertyMapping, relationPropertyMapping, fetchField, condition, groupBy,
                association.groupByType(), getJoinsForGroupBy(relationClass, groupBy));
    }

    public RelationMapping parseOneToMany(final Class entityClass, final AnnotatedElement annotatedElement) {
        final OneToMany oneToMany = annotatedElement.getAnnotation(OneToMany.class);
        final String name = getPropertyName(annotatedElement);
        final FieldMapping propertyMapping = idFieldMappings.get(entityClass).iterator().next();
        final Property fetchField = FieldHelper.constructProperty(entityClass, name);
        final Class relationClass = oneToMany.relationClass() != Object.class ? oneToMany.relationClass() : fetchField.getType();
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

        return new RelationMapping(name, relationClass, propertyMapping, relationPropertyMapping, fetchField);
    }

    public RelationMapping parseManyToOne(final Class entityClass, final AnnotatedElement annotatedElement) {
        final ManyToOne manyToOne = annotatedElement.getAnnotation(ManyToOne.class);
        final String name = getPropertyName(annotatedElement);
        final Map<String, FieldMapping> mapping = fieldMappings.get(entityClass);
        final FieldMapping propertyMapping = mapping != null
                ? mapping.get(manyToOne.property())
                : new FieldMapping(name, null, Types.BIGINT, false, FieldHelper.constructProperty(entityClass, manyToOne.property()));
        if (propertyMapping == null) {
            throw new RuntimeException(
                    "Not found property " + manyToOne.property() + "for @ManyToOne " + entityClass.getName() + "." + getPropertyName(
                            annotatedElement) + "!");
        }

        final Property fetchField = FieldHelper.constructProperty(entityClass, name);
        final Class relationClass = fetchField.getType();
        if (!entityNameByEntityClass.containsKey(relationClass)) {
            registerEntityClass(relationClass);
        }
        final FieldMapping relationPropertyMapping = idFieldMappings.get(relationClass).iterator().next();
        return new RelationMapping(name, relationClass, propertyMapping, relationPropertyMapping, fetchField);
    }

    private Join[] getJoinsForGroupBy(Class relationClass, Property[] groupBy) {
        if (groupBy == null || groupBy.length == 0) {
            return null;
        }
        final Collection<Join> joins = new ArrayList<Join>();
        if (groupBy != null) {
            for (Property p : groupBy) {
                final Join[] j = parseGroupBy(relationClass, p.getName());
                if (j != null) {
                    joins.add(j[0]);
                }
            }
        }

        if (!joins.isEmpty()) {
            return joins.toArray(new Join[]{});
        }

        return null;
    }

    private Join[] parseGroupBy(Class relationClass, String name) {
        if (!entityNameByEntityClass.containsKey(relationClass)) {
            registerEntityClass(relationClass);
        }
        final int index = name.indexOf('.');
        if (index > 0) {
            final String propertyName = name.substring(0, index);
            final RelationMapping relationMapping = getRelationMapping(relationClass, propertyName);
            if (relationMapping == null) {
                throw new IllegalArgumentException("Can't process groupBy for relation " + propertyName + " in " + relationClass.getName());
            }

            return new Join[]{Join.inner(propertyName, parseGroupBy(relationMapping.getRelationClass(), name.substring(index + 1)))};
        } else {
            final RelationMapping relationMapping = getRelationMapping(relationClass, name);
            if (relationMapping != null) {
                return new Join[]{Join.inner(name, parseGroupBy(relationMapping.getRelationClass(), name.substring(index + 1)))};
            }
            return null;
        }
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
        final Map<String, FieldMapping> fieldMappingByPropertyName = fieldMappings.get(entityClass);
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
            tableNames.put(entityClass, tableName);
            logRegisterEntity(entityClass, entityName, tableName);
        } else {
            throw new RuntimeException("Duplicate entity class " + entityClass + " or entity name class " + entityName);
        }
    }

    private void registerColumnName(Class entityClass, String columnName) {
        Collection<String> columnNames = this.columnNames.get(entityClass);
        if (columnNames == null) {
            columnNames = new LinkedHashSet<String>();
            this.columnNames.put(entityClass, columnNames);
        }

        if (columnNames.contains(columnName)) {
            throw new RuntimeException("Duplicate column name '" + columnName + "' in class " + entityClass);
        }

        columnNames.add(columnName);

        logRegisterColumn(entityClass, columnName);
    }

    private void addFieldMapping(Class entityClass, FieldMapping fieldMapping) {
        registerColumnName(entityClass, fieldMapping.getColumn());
        addFieldMappingByPropertyName(entityClass, fieldMapping);

        logRegisterFieldMapping(entityClass, fieldMapping);
    }

    private void addIdFieldMapping(Class entityClass, FieldMapping fieldMapping) {
        Collection<FieldMapping> fieldMappings = idFieldMappings.get(entityClass);
        if (fieldMappings == null) {
            fieldMappings = new LinkedHashSet<FieldMapping>();
            idFieldMappings.put(entityClass, fieldMappings);
        }
        fieldMappings.add(fieldMapping);

        addFieldMapping(entityClass, fieldMapping);
    }

    private void addVersionFieldMapping(Class entityClass, FieldMapping fieldMapping) {
        versionFieldMappings.put(entityClass, fieldMapping);

        addFieldMapping(entityClass, fieldMapping);
    }

    private void addFetchMapping(Class entityClass, RelationMapping relationMapping) {
        addFetchMappingByPropertyName(entityClass, relationMapping);

        logRegisterFetchMapping(entityClass, relationMapping);
    }

    private void addFieldMappingByPropertyName(Class entityClass, FieldMapping fieldMapping) {
        Map<String, FieldMapping> fieldMappingByPropertyName = fieldMappings.get(entityClass);
        if (fieldMappingByPropertyName == null) {
            fieldMappingByPropertyName = new LinkedHashMap<String, FieldMapping>();
            fieldMappings.put(entityClass, fieldMappingByPropertyName);
        }

        final String propertyName = fieldMapping.getName();

        if (fieldMappingByPropertyName.containsKey(propertyName)) {
            throw new RuntimeException("Duplicate property name '" + propertyName + "' in class " + entityClass);
        }

        fieldMappingByPropertyName.put(propertyName, fieldMapping);
    }

    private void addFetchMappingByPropertyName(Class entityClass, RelationMapping relationMapping) {
        Map<String, RelationMapping> fetchMappingByPropertyName = relationMappings.get(entityClass);
        if (fetchMappingByPropertyName == null) {
            fetchMappingByPropertyName = new LinkedHashMap<String, RelationMapping>();
            relationMappings.put(entityClass, fetchMappingByPropertyName);
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

        return fields.toArray(new Property[]{});
    }

    public String getTableName(Class entityClass) {
        return tableNames.get(entityClass);
    }

    public String getTableName(String entityName) {
        return tableNames.get(getEntityClass(entityName));
    }

    public Class getEntityClass(String entityName) {
        return entityClassByEntityName.get(entityName);
    }

    public String getEntityName(Class entityClass) {
        return entityNameByEntityClass.get(entityClass);
    }

    public Collection<String> getColumnNames(Class entityClass) {
        return columnNames.get(entityClass);
    }

    public Collection<String> getColumnNames(String entityName) {
        return columnNames.get(getEntityClass(entityName));
    }

    public Collection<FieldMapping> getFieldMappings(Class entityClass) {
        final Map<String, FieldMapping> result = fieldMappings.get(entityClass);
        return result != null ? result.values() : null;
    }

    public Collection<FieldMapping> getFieldMappings(String entityName) {
        return getFieldMappings(getEntityClass(entityName));
    }

    public Collection<FieldMapping> getIdFields(Class entityClass) {
        return idFieldMappings.get(entityClass);
    }

    public Collection<FieldMapping> getIdFields(String entityName) {
        return getIdFields(getEntityClass(entityName));
    }

    public FieldMapping getVersionField(Class entityClass) {
        return versionFieldMappings.get(entityClass);
    }

    public FieldMapping getVersionField(String entityName) {
        return getVersionField(getEntityClass(entityName));
    }

    public Collection<RelationMapping> getRelationMappings(Class entityClass) {
        final Map<String, RelationMapping> result = relationMappings.get(entityClass);
        return result != null ? result.values() : null;
    }

    public Collection<RelationMapping> getRelationMappings(String entityName) {
        return getRelationMappings(getEntityClass(entityName));
    }

    public FieldMapping getFieldMapping(Class entityClass, String propertyName) {
        final Map<String, FieldMapping> fieldMappingByPropertyName = fieldMappings.get(entityClass);
        return fieldMappingByPropertyName != null ? fieldMappingByPropertyName.get(propertyName) : null;
    }

    public FieldMapping getFieldMapping(String entityName, String propertyName) {
        return getFieldMapping(getEntityClass(entityName), propertyName);
    }

    public RelationMapping getRelationMapping(Class entityClass, String fieldName) {
        final Map<String, RelationMapping> fieldMappingByPropertyName = relationMappings.get(entityClass);
        return fieldMappingByPropertyName != null ? fieldMappingByPropertyName.get(fieldName) : null;
    }

    public RelationMapping getRelationMapping(String entityName, String fieldName) {
        return getRelationMapping(getEntityClass(entityName), fieldName);
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
