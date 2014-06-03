package ru.kwanza.dbtool.orm.impl.mapping;

import ru.kwanza.dbtool.orm.annotations.*;
import ru.kwanza.toolbox.fieldhelper.FieldHelper;
import ru.kwanza.toolbox.fieldhelper.Property;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.sql.Types;

/**
 * @author Kiryl Karatsetski
 */
public class EntityMappingHelper {

    public static FieldMapping createFieldMapping(Class entityClass, AnnotatedElement annotatedElement, Annotation annotation) {
        final String columnName;
        final int type;

        if (annotation instanceof Field) {
            final Field fieldAnnotation = (Field) annotation;
            columnName = getColumnName(fieldAnnotation, annotatedElement);
            type = fieldAnnotation.type();
        } else if (annotation instanceof IdField) {
            final IdField idFieldAnnotation = (IdField) annotation;
            columnName = getColumnName(idFieldAnnotation, annotatedElement);
            type = idFieldAnnotation.type();
        } else if (annotation instanceof VersionField) {
            final VersionField versionFieldAnnotation = (VersionField) annotation;
            columnName = getColumnName(versionFieldAnnotation, annotatedElement);
            type = Types.BIGINT;
        } else {
            throw new RuntimeException("Unknown annotation: " + annotation);
        }

        final String propertyName = getPropertyName(annotatedElement);

        final Property property = FieldHelper.constructProperty(entityClass, propertyName);

        return new FieldMapping(propertyName, columnName, type, property);
    }

    public static String getEntityTableName(Entity entity, Class entityClass) {
        final String tableName = entity.table();
        if (tableName.trim().isEmpty()) {
            return entityClass.getSimpleName().toLowerCase();
        }
        return tableName;
    }


    public static String getEntitySql(Entity entity, Class entityClass) {
        final String sql = entity.sql();
        if (sql.trim().isEmpty()) {
            return null;
        }
        return sql;
    }

    public static String getEntityNameFromAnnotation(Entity entity, Class entityClass) {
        final String entityName = entity.name();
        if (entityName.trim().isEmpty()) {
            return entityClass.getSimpleName();
        }
        return entityName;
    }

    public static String getEntityNameFromAnnotation(AbstractEntity entity, Class entityClass) {
        final String entityName = entity.name();
        if (entityName.trim().isEmpty()) {
            return entityClass.getSimpleName();
        }
        return entityName;
    }

    private static String getColumnName(Field fieldAnnotation, AnnotatedElement annotatedElement) {
        final String columnName = fieldAnnotation.value();
        if (columnName.trim().isEmpty()) {
            return getPropertyName(annotatedElement);
        }
        return fieldAnnotation.value();
    }

    private static String getColumnName(IdField idFieldAnnotation, AnnotatedElement annotatedElement) {
        final String columnName = idFieldAnnotation.value();
        if (columnName.trim().isEmpty()) {
            return getPropertyName(annotatedElement);
        }
        return idFieldAnnotation.value();
    }

    private static String getColumnName(VersionField versionFieldAnnotation, AnnotatedElement annotatedElement) {
        final String columnName = versionFieldAnnotation.value();
        if (columnName.trim().isEmpty()) {
            return getPropertyName(annotatedElement);
        }
        return versionFieldAnnotation.value();
    }

    public static AnnotatedElement findField(Class clazz, String name) {
        return findField0(clazz, name,
                new StringBuilder("get").append(Character.toUpperCase(name.charAt(0))).append(name.substring(1)).toString());
    }

    private static AnnotatedElement findField0(Class clazz, String name, String methodName) {
        if (clazz == Object.class) {
            return null;
        }

        try {
            return clazz.getDeclaredField(name);
        } catch (NoSuchFieldException e) {
            try {
                return clazz.getDeclaredMethod(methodName);
            } catch (NoSuchMethodException e1) {
                return findField(clazz.getSuperclass(), name);
            }
        }
    }

    public static String getPropertyName(AnnotatedElement annotatedElement) {
        if (annotatedElement instanceof java.lang.reflect.Field) {
            return getFieldPropertyName((java.lang.reflect.Field) annotatedElement);
        } else if (annotatedElement instanceof java.lang.reflect.Method) {
            return getMethodPropertyName((java.lang.reflect.Method) annotatedElement);
        } else {
            throw new RuntimeException("Unknown AnnotatedElement: " + annotatedElement);
        }
    }

    private static String getFieldPropertyName(java.lang.reflect.Field field) {
        return field.getName();
    }

    private static String getMethodPropertyName(java.lang.reflect.Method method) {
        final String methodName = method.getName();
        if (methodName.startsWith("get") && methodName.length() > 3) {
            return uncapitalize(methodName.substring(3, methodName.length()));
        } else {
            throw new RuntimeException("Incorrect getter name: " + methodName);
        }
    }

    private static String uncapitalize(String str) {
        return changeFirstCharacterCase(str, false);
    }

    private static String changeFirstCharacterCase(String stringValue, boolean capitalize) {
        final StringBuilder stringBuilder = new StringBuilder(stringValue.length());
        if (capitalize) {
            stringBuilder.append(Character.toUpperCase(stringValue.charAt(0)));
        } else {
            stringBuilder.append(Character.toLowerCase(stringValue.charAt(0)));
        }
        stringBuilder.append(stringValue.substring(1));
        return stringBuilder.toString();
    }
}
