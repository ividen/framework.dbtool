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
import ru.kwanza.dbtool.orm.api.internal.IEntityMappingRegistry;
import ru.kwanza.dbtool.orm.api.internal.IFieldMapping;
import ru.kwanza.dbtool.orm.api.internal.IRelationMapping;
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

    private Map<String, AbstractEntityType> entityTypeByEntityName = new HashMap<String, AbstractEntityType>();
    private Map<Class, AbstractEntityType> entityTypeByEntityClass = new HashMap<Class, AbstractEntityType>();

    private ExpressionParser conditionParser = new SpelExpressionParser();

    public ru.kwanza.dbtool.orm.api.internal.IEntityType registerEntityClass(Class entityClass) {
        registerLock.lock();
        try {
            AbstractEntityType result = entityTypeByEntityClass.get(entityClass);
            if (result == null) {
                result = registerEntity(entityClass);

                processMembers(result, entityClass);

                return result;
            } else {
                return result;
            }
        } finally {
            registerLock.unlock();
        }
    }

    private void processMembers(AbstractEntityType entityType, Class targetClass) {
        if (Object.class.equals(targetClass)) {
            return;
        }

        processMembers(entityType, targetClass.getSuperclass());

        final java.lang.reflect.Field[] declaredFields = targetClass.getDeclaredFields();
        final java.lang.reflect.Method[] methods = targetClass.getDeclaredMethods();


        processFields(entityType, declaredFields);
        processFields(entityType, methods);

        processFetches(entityType, declaredFields);
        processFetches(entityType, methods);

    }

    public boolean isRegisteredEntityClass(Class entityClass) {
        return entityTypeByEntityClass.containsKey(entityClass);
    }

    public boolean isRegisteredEntityName(String entityName) {
        return entityTypeByEntityName.containsKey(entityName);
    }

    public AbstractEntityType getEntityType(String name) {
        final AbstractEntityType result = entityTypeByEntityName.get(name);
        if (result == null) {
            throw new IllegalStateException("Can't find entity with name " + name);
        }
        return result;
    }

    public AbstractEntityType getEntityType(Class entityClass) {
        final AbstractEntityType result = entityTypeByEntityClass.get(entityClass);
        if (result == null) {
            throw new IllegalStateException("Can't find entity with entityClass " + entityClass);
        }
        return result;
    }

    private void processFields(AbstractEntityType entityType, AnnotatedElement[] annotatedElements) {
        for (AnnotatedElement annotatedElement : annotatedElements) {

            if (isTransient(annotatedElement)) {
                continue;
            }

            tryCreateFieldMapping(entityType, annotatedElement);
        }
    }

    private void processFetches(AbstractEntityType entityType, AnnotatedElement[] annotatedElements) {
        for (AnnotatedElement annotatedElement : annotatedElements) {

            if (isTransient(annotatedElement)) {
                continue;
            }

            if (annotatedElement.isAnnotationPresent(ManyToOne.class)) {
                processManyToOne(entityType, annotatedElement);
            } else if (annotatedElement.isAnnotationPresent(OneToMany.class)) {
                processOneToMany(entityType, annotatedElement);
            } else if (annotatedElement.isAnnotationPresent(Association.class)) {
                processAssociation(entityType, annotatedElement);
            }
        }
    }

    private void processAssociation(final AbstractEntityType entityType, AnnotatedElement annotatedElement) {
        addFetchMapping(entityType, parseAssociation(entityType, annotatedElement));
    }

    private void processOneToMany(final AbstractEntityType entityType, final AnnotatedElement annotatedElement) {
        addFetchMapping(entityType, parseOneToMany(entityType, annotatedElement));
    }

    private void processManyToOne(final AbstractEntityType entityType, final AnnotatedElement annotatedElement) {
        addFetchMapping(entityType, parseManyToOne(entityType, annotatedElement));
    }

    public IRelationMapping parseAssociation(final Class entityClass, final AnnotatedElement element) {
        return parseAssociation(null, entityClass, element);
    }

    private IRelationMapping parseAssociation(final AbstractEntityType entityType, final AnnotatedElement element) {
        return parseAssociation(entityType, entityType.getEntityClass(), element);
    }

    private IRelationMapping parseAssociation(final AbstractEntityType entityType, final Class entityClass, final AnnotatedElement element) {
        final Association association = element.getAnnotation(Association.class);
        final String name = getPropertyName(element);


        final IFieldMapping propertyMapping = entityType != null
                ? entityType.getField(association.property())
                : new FieldMapping(name, null, Types.BIGINT, FieldHelper.constructProperty(entityClass, association.property()));
        final Property fetchField = FieldHelper.constructProperty(entityClass, name);
        final Class relationClass = association.relationClass() != Object.class ? association.relationClass() : fetchField.getType();
        if (relationClass == Object.class) {
            throw new RuntimeException(
                    "Relation @OneToMany in  " + entityClass.getName() + "." + name + " must have relativeClass() specified!");
        }

        final Condition ifConfig = element.getAnnotation(Condition.class);
        final If condition = ifConfig == null ? null : parseCondition(ifConfig);

        final GroupBy groupByConfig = element.getAnnotation(GroupBy.class);
        final Property[] groupBy = groupByConfig == null ? null : parseGroupBy(relationClass, groupByConfig);
        final GroupByType groupByType = groupByConfig == null ? null : groupByConfig.type();

        if (!entityTypeByEntityClass.containsKey(relationClass)) {
            this.registerEntityClass(relationClass);
        }

        IFieldMapping relationPropertyMapping = getPropertyFieldMapping(relationClass, association.relationProperty());
        if (relationPropertyMapping == null) {
            throw new RuntimeException(
                    "Not found relational property mapping " + relationClass.getName() + "." + association.relationProperty()
                            + " for @Association " + entityClass.getName() + "." + name + "!");
        }

        return new RelationMapping(name, relationClass, propertyMapping, relationPropertyMapping, fetchField, condition, groupBy,
                groupByType, getJoinsForGroupBy(relationClass, groupBy));
    }

    public IRelationMapping parseOneToMany(final AbstractEntityType entityType, final AnnotatedElement element) {
        final OneToMany oneToMany = element.getAnnotation(OneToMany.class);
        final String name = getPropertyName(element);
        final IFieldMapping propertyMapping = entityType.getIdField();
        final Property fetchField = FieldHelper.constructProperty(entityType.getEntityClass(), name);
        final Class relationClass = oneToMany.relationClass() != Object.class ? oneToMany.relationClass() : fetchField.getType();
        if (relationClass == Object.class) {
            throw new RuntimeException(
                    "Relation @OneToMany in  " + entityType.getEntityClass().getName() + "." + name + " must have relativeClass() specified!");
        }

        if (!entityTypeByEntityClass.containsKey(relationClass)) {
            registerEntityClass(relationClass);
        }

        final Condition ifConfig = element.getAnnotation(Condition.class);
        final If condition = ifConfig == null ? null : parseCondition(ifConfig);

        final GroupBy groupByConfig = element.getAnnotation(GroupBy.class);
        final Property[] groupBy = groupByConfig == null ? null : parseGroupBy(relationClass, groupByConfig);
        final GroupByType groupByType = groupByConfig == null ? null : groupByConfig.type();

        if (!entityTypeByEntityClass.containsKey(relationClass)) {
            this.registerEntityClass(relationClass);
        }

        IFieldMapping relationPropertyMapping = getPropertyFieldMapping(relationClass, oneToMany.relationProperty());
        if (relationPropertyMapping == null) {
            throw new RuntimeException(
                    "Not found relational property mapping " + relationClass.getName() + "." + oneToMany.relationProperty()
                            + " for @OneToMany " + entityType.getEntityClass().getName() + "." + name + "!");
        }

        return new RelationMapping(name, relationClass, propertyMapping, relationPropertyMapping, fetchField, condition, groupBy,
                groupByType, getJoinsForGroupBy(relationClass, groupBy));

    }

    public IRelationMapping parseManyToOne(final AbstractEntityType entityType, final AnnotatedElement element) {
        return parseManyToOne(entityType, entityType.getEntityClass(), element);
    }

    public IRelationMapping parseManyToOne(final Class entityClass, final AnnotatedElement element) {
        return parseManyToOne(null, entityClass, element);
    }

    private IRelationMapping parseManyToOne(final AbstractEntityType entityType, final Class entityClass, final AnnotatedElement element) {
        final ManyToOne manyToOne = element.getAnnotation(ManyToOne.class);
        final String name = getPropertyName(element);

        final IFieldMapping propertyMapping = entityType != null
                ? entityType.getField(manyToOne.property())
                : new FieldMapping(name, null, Types.BIGINT, FieldHelper.constructProperty(entityClass, manyToOne.property()));
        if (propertyMapping == null) {
            throw new RuntimeException(
                    "Not found property " + manyToOne.property() + "for @ManyToOne " + entityClass.getName() + "." + getPropertyName(
                            element) + "!");
        }

        final Property fetchField = FieldHelper.constructProperty(entityClass, name);
        final Class relationClass = fetchField.getType();
        if (!entityTypeByEntityClass.containsKey(relationClass)) {
            registerEntityClass(relationClass);
        }

        final Condition ifConfig = element.getAnnotation(Condition.class);
        final If condition = ifConfig == null ? null : parseCondition(ifConfig);

        final GroupBy groupByConfig = element.getAnnotation(GroupBy.class);
        final Property[] groupBy = groupByConfig == null ? null : parseGroupBy(relationClass, groupByConfig);
        final GroupByType groupByType = groupByConfig == null ? null : groupByConfig.type();

        final IFieldMapping relationPropertyMapping = getEntityType(relationClass).getIdField();
        return new RelationMapping(name, relationClass, propertyMapping, relationPropertyMapping, fetchField, condition, groupBy,
                groupByType, getJoinsForGroupBy(relationClass, groupBy));
    }

    private List<Join> getJoinsForGroupBy(Class relationClass, Property[] groupBy) {
        if (groupBy == null || groupBy.length == 0) {
            return Collections.emptyList();
        }
        final List<Join> joins = new ArrayList<Join>();
        if (groupBy != null) {
            for (Property p : groupBy) {
                joins.addAll(parseGroupBy(relationClass, p.getName()));
            }
        }

        return joins;
    }

    private List<Join> parseGroupBy(Class relationClass, String name) {
        if (!entityTypeByEntityClass.containsKey(relationClass)) {
            registerEntityClass(relationClass);
        }
        final int index = name.indexOf('.');
        if (index > 0) {
            final String propertyName = name.substring(0, index);
            final IRelationMapping relationMapping = getEntityType(relationClass).getRelation(propertyName);
            if (relationMapping == null) {
                throw new IllegalArgumentException("Can't process groupBy for relation " + propertyName + " in " + relationClass.getName());
            }

            return Collections
                    .singletonList(Join.inner(propertyName, parseGroupBy(relationMapping.getRelationClass(), name.substring(index + 1))));
        } else {
            final IRelationMapping relationMapping = getEntityType(relationClass).getRelation(name);
            if (relationMapping != null) {
                return Collections
                        .singletonList(Join.inner(name, parseGroupBy(relationMapping.getRelationClass(), name.substring(index + 1))));
            }
            return Collections.emptyList();
        }
    }

    private If parseCondition(Condition ifConfig) {
        final Expression expression = conditionParser.parseExpression(ifConfig.value());
        EvaluationContext ctx = new StandardEvaluationContext(If.class);
        return (If) expression.getValue(ctx, (Object) If.class);
    }

    private boolean isTransient(AnnotatedElement annotatedElement) {
        return annotatedElement instanceof Member && Modifier.isTransient(((Member) annotatedElement).getModifiers());
    }

    private IFieldMapping getPropertyFieldMapping(Class entityClass, String fetchedPropertyName) {
        final AbstractEntityType entityType = entityTypeByEntityClass.get(entityClass);
        return entityType != null ? entityType.getField(fetchedPropertyName) : null;
    }

    @SuppressWarnings("unchecked")
    private AbstractEntityType registerEntity(Class entityClass) {
        if (entityClass.isAnnotationPresent(Entity.class)) {
            final Entity entity = (Entity) entityClass.getAnnotation(Entity.class);
            final String entityName = getEntityNameFromAnnotation(entity, entityClass);
            final String tableName = getEntityTableName(entity, entityClass);
            if (!entityTypeByEntityClass.containsKey(entityClass) && !entityTypeByEntityName.containsKey(entityName)) {
                SimpleEntityType entityType = new SimpleEntityType(entityClass, entityName, tableName, getEntitySql(entity));
                entityTypeByEntityClass.put(entityClass, entityType);
                entityTypeByEntityName.put(entityName, entityType);
                logRegisterEntity(entityClass, entityName, tableName);
                tryUpdateUnionEntityType(entityClass, entityType);
                return entityType;
            } else {
                throw new RuntimeException("Duplicate entity class " + entityClass + " or entity name class " + entityName);
            }
        } else if (entityClass.isAnnotationPresent(AbstractEntity.class)) {
            final AbstractEntity entity = (AbstractEntity) entityClass.getAnnotation(AbstractEntity.class);
            final String entityName = getEntityNameFromAnnotation(entity, entityClass);
            if (!entityTypeByEntityClass.containsKey(entityClass) && !entityTypeByEntityName.containsKey(entityName)) {
                UnionEntityType entityType = new UnionEntityType(entityName, entityClass);
                entityTypeByEntityClass.put(entityClass, entityType);
                entityTypeByEntityName.put(entityName, entityType);
                logRegisterEntity(entityClass, entityName, null);
                tryUpdateUnionEntityType(entityClass, entityType);
                return entityType;
            } else {
                throw new RuntimeException("Duplicate entity class " + entityClass + " or entity name class " + entityName);
            }
        } else {
            throw new RuntimeException("Entity class must be annotated by @Entity: " + entityClass);
        }
    }

    private void tryUpdateUnionEntityType(Class entityClass, AbstractEntityType entityType) {
        final Class abstractEntityClass = findAbstractEntityClass(entityClass.getSuperclass());
        if (abstractEntityClass != null) {
            if (!isRegisteredEntityClass(abstractEntityClass)) {
                registerEntityClass(abstractEntityClass);
            }

            ((UnionEntityType) getEntityType(abstractEntityClass)).addEntity(entityType);
        }
    }

    private Class findAbstractEntityClass(Class entityClass) {
        if (entityClass.isAnnotationPresent(AbstractEntity.class)) {
            return entityClass;
        } else if (entityClass == Object.class) {
            return null;
        } else {
            return findAbstractEntityClass(entityClass.getSuperclass());
        }
    }

    private void addFetchMapping(AbstractEntityType entityType, IRelationMapping relationMapping) {
        if (entityType.getRelation(relationMapping.getName()) != null) {
            throw new RuntimeException("Duplicate property name '" + relationMapping.getName() + "' in class " + entityType.getEntityClass());
        }

        entityType.addRelation(relationMapping);

        logRegisterFetchMapping(entityType.getEntityClass(), relationMapping);
    }

    private Property[] parseGroupBy(Class entityClass, GroupBy groupByConfig) {
        StringTokenizer st = new StringTokenizer(groupByConfig.value(), ",");
        ArrayList<Property> fields = new ArrayList<Property>();
        while (st.hasMoreTokens()) {
            String field = st.nextToken().trim();
            fields.add(FieldHelper.constructProperty(entityClass, field));
        }

        return fields.toArray(new Property[]{});
    }

    static void logRegisterEntity(Class entityClass, String entityName, String tableName) {
        log.trace("Register entity '{}' with '{}' name and '{}' table name", new Object[]{entityClass, entityName, tableName});
    }

    static void logRegisterColumn(Class entityClass, String columnName) {
        log.trace("{}: Register column '{}'", new Object[]{entityClass, columnName});
    }

    static void logRegisterFieldMapping(Class entityClass, FieldMapping fieldMapping) {
        log.trace("{}: Register Field Mapping {}", new Object[]{entityClass, fieldMapping});
    }

    static void logRegisterFetchMapping(Class entityClass, IRelationMapping relationMapping) {
        log.trace("{}: Register ManyToOne Mapping {}", new Object[]{entityClass, relationMapping});
    }
}
