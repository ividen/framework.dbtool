package ru.kwanza.dbtool.orm.api;

/**
 * @author Alexander Guzanov
 */
public class Condition {

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

    private String propertyName;
    private String sql;
    private String paramName;
    private Type type;
    private Condition[] childs;

    private Condition(String propertyName, Type type, Condition[] childs, String paramName) {
        this.propertyName = propertyName;
        this.paramName = paramName;
        this.type = type;
        this.childs = childs;
    }

    private Condition(String sql) {
        this.sql = sql;
        this.type = Type.NATIVE;
    }

    private Condition(String propertyName, Type type, String paramName) {
        this(propertyName, type, null, paramName);
    }

    public String getPropertyName() {
        return propertyName;
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

    public Condition[] getChilds() {
        return childs;
    }

    private Condition(Type type, Condition[] childs) {
        this(null, type, childs, null);
    }

    public static Condition isEqual(String property) {
        return isEqual(property, null);
    }

    public static Condition isEqual(String property, String paramName) {
        return new Condition(property, Type.IS_EQUAL, paramName);
    }

    public static Condition notEqual(String property) {
        return notEqual(property, null);
    }

    public static Condition notEqual(String property, String paramName) {
        return new Condition(property, Type.NOT_EQUAL, paramName);
    }

    public static Condition isGreater(String property) {
        return isGreater(property, null);
    }

    public static Condition isGreater(String property, String paramName) {
        return new Condition(property, Type.IS_GREATER, paramName);
    }

    public static Condition isLess(String property) {
        return isLess(property, null);
    }

    public static Condition isLess(String property, String paramName) {
        return new Condition(property, Type.IS_LESS, paramName);
    }

    public static Condition isGreaterOrEqual(String property) {
        return isGreaterOrEqual(property, null);
    }

    public static Condition isGreaterOrEqual(String property, String paramName) {
        return new Condition(property, Type.IS_GREATER_OR_EQUAL, paramName);
    }

    public static Condition isLessOrEqual(String property) {
        return isLessOrEqual(property, null);
    }

    public static Condition isLessOrEqual(String property, String paramName) {
        return new Condition(property, Type.IS_LESS_OR_EQUAL, paramName);
    }

    public static Condition isNull(String property) {
        return new Condition(property, Type.IS_NULL, null);
    }

    public static Condition isNotNull(String property) {
        return new Condition(property, Type.IS_NOT_NULL, null);
    }

    public static Condition in(String property) {
        return in(property, null);
    }

    public static Condition in(String property, String paramName) {
        return new Condition(property, Type.IN, paramName);
    }

    public static Condition like(String property) {
        return like(property, null);
    }

    public static Condition like(String property, String propertyName) {
        return new Condition(property, Type.LIKE, propertyName);
    }

    public static Condition between(String property) {
        return new Condition(property, Type.BETWEEN, null);
    }

    public static Condition and(Condition... conditions) {
        return new Condition(Type.AND, conditions);
    }

    public static Condition or(Condition... conditions) {
        return new Condition(Type.OR, conditions);
    }

    public static Condition not(Condition conditions) {
        return new Condition(Type.NOT, new Condition[]{conditions});
    }

    public static Condition createNative(String sql) {
        return new Condition(sql);
    }

}

