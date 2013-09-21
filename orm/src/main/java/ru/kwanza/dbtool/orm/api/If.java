package ru.kwanza.dbtool.orm.api;

import java.util.Collection;

/**
 * @author Alexander Guzanov
 */
public class If {
    public enum Type {
        IS_EQUAL,
        NOT_EQUAL,
        IS_GREATER,
        IS_LESS,
        IS_GREATER_OR_EQUAL,
        IS_LESS_OR_EQUAL,
        IS_NULL,
        IS_NOT_NULL,
        BETWEEN,
        IN,
        LIKE,
        AND,
        OR,
        NOT,
        NATIVE;
    }

    private final String propertyName;
    private final String sql;
    private final String paramName;
    private final Type type;
    private final If[] childs;
    private final Object value;

    public static class Value<T> {
        private T obj;

        private Value(T obj) {
            this.obj = obj;
        }
    }

    private If(String propertyName, String sql, Type type, If[] childs, String paramName, Object value) {
        this.sql = sql;
        this.propertyName = propertyName;
        this.paramName = paramName;
        this.type = type;
        this.childs = childs;
        this.value = value;
    }

    public static <T> Value<T> valueOf(T value) {
        return new Value(value);
    }

    public String getPropertyName() {
        return propertyName;
    }

    public Object getValue() {
        return value;
    }

    public String getSql() {
        return sql;
    }

    public String getParamName() {
        return paramName;
    }

    public Type getType() {
        return type;
    }

    public If[] getChilds() {
        return childs;
    }

    private If(Type type, If[] childs) {
        this(null, null, type, childs, null, null);
    }

    public static If isEqual(String property) {
        return new If(property, null, Type.IS_EQUAL, null, null, null);
    }

    public static If isEqual(String property, String paramName) {
        return new If(property, null, Type.IS_EQUAL, null, paramName, null);
    }

    public static If isEqual(String property, Value value) {
        return new If(property, null, Type.IS_EQUAL, null, null, value.obj);
    }

    public static If notEqual(String property) {
        return new If(property, null, Type.NOT_EQUAL, null, null, null);
    }

    public static If notEqual(String property, String paramName) {
        return new If(property, null, Type.NOT_EQUAL, null, paramName, null);
    }

    public static If notEqual(String property, Value value) {
        return new If(property, null, Type.NOT_EQUAL, null, null, value.obj);
    }

    public static If isGreater(String property) {
        return new If(property, null, Type.IS_GREATER, null, null, null);
    }

    public static If isGreater(String property, String paramName) {
        return new If(property, null, Type.IS_GREATER, null, paramName, null);
    }

    public static If isGreater(String property, Value value) {
        return new If(property, null, Type.IS_GREATER, null, null, value.obj);
    }

    public static If isLess(String property) {
        return new If(property, null, Type.IS_LESS, null, null, null);
    }

    public static If isLess(String property, String paramName) {
        return new If(property, null, Type.IS_LESS, null, paramName, null);
    }

    public static If isLess(String property, Value value) {
        return new If(property, null, Type.IS_LESS, null, null, value.obj);
    }

    public static If isGreaterOrEqual(String property) {
        return new If(property, null, Type.IS_GREATER_OR_EQUAL, null, null, null);
    }

    public static If isGreaterOrEqual(String property, String paramName) {
        return new If(property, null, Type.IS_GREATER_OR_EQUAL, null, paramName, null);
    }

    public static If isGreaterOrEqual(String property, Value value) {
        return new If(property, null, Type.IS_GREATER_OR_EQUAL, null, null, value.obj);
    }

    public static If isLessOrEqual(String property) {
        return new If(property, null, Type.IS_LESS_OR_EQUAL, null, null, null);
    }

    public static If isLessOrEqual(String property, String paramName) {
        return new If(property, null, Type.IS_LESS_OR_EQUAL, null, paramName, null);
    }

    public static If isLessOrEqual(String property, Value value) {
        return new If(property, null, Type.IS_LESS_OR_EQUAL, null, null, value.obj);
    }

    public static If isNull(String property) {
        return new If(property, null, Type.IS_NULL, null, null, null);
    }

    public static If isNotNull(String property) {
        return new If(property, null, Type.IS_NOT_NULL, null, null, null);
    }

    public static If in(String property) {
        return new If(property, null, Type.IN, null, null, null);
    }

    public static If in(String property, String paramName) {
        return new If(property, null, Type.IN, null, paramName, null);
    }

    public static If in(String property, Value<? extends Collection> value) {
        return new If(property, null, Type.IN, null, null, value.obj);
    }

    public static If like(String property) {
        return new If(property, null, Type.LIKE, null, null, null);
    }

    public static If like(String property, String paramName) {
        return new If(property, null, Type.LIKE, null, paramName, null);
    }

    public static If like(String property, Value value) {
        return new If(property, null, Type.LIKE, null, null, value.obj);
    }

    public static If between(String property) {
        return new If(property, null, Type.BETWEEN, null, null, null);
    }

    public static If and(If... conditions) {
        return new If(Type.AND, conditions);
    }

    public static If or(If... conditions) {
        return new If(Type.OR, conditions);
    }

    public static If not(If conditions) {
        return new If(Type.NOT, new If[]{conditions});
    }

    public static If createNative(String sql) {
        return new If(null, sql, Type.NATIVE, null, null, null);
    }

}

