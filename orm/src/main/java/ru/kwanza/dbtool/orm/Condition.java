package ru.kwanza.dbtool.orm;

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
        OR
    }

    private String propertyName;
    private Type type;
    private Condition[] childs;

    private Condition(String propertyName, Type type, Condition[] childs) {
        this.propertyName = propertyName;
        this.type = type;
        this.childs = childs;
    }

    private Condition(String propertyName, Type type) {
        this(propertyName, type, null);
    }

    public String getPropertyName() {
        return propertyName;
    }

    public Type getType() {
        return type;
    }

    public Condition[] getChilds() {
        return childs;
    }

    private Condition(Type type, Condition[] childs) {
        this(null, type, childs);
    }

    public static Condition isEqual(String property) {
        return new Condition(property, Type.IS_EQUAL);
    }

    public static Condition notEqual(String property) {
        return new Condition(property, Type.NOT_EQUAL);
    }

    public static Condition isGreater(String property) {
        return new Condition(property, Type.IS_GREATER);
    }

    public static Condition isLess(String property) {
        return new Condition(property, Type.IS_LESS);
    }

    public static Condition isGreaterOrEqual(String property) {
        return new Condition(property, Type.IS_GREATER_OR_EQUAL);
    }

    public static Condition isLessOrEqual(String property) {
        return new Condition(property, Type.IS_LESS_OR_EQUAL);
    }

    public static Condition isNull(String property) {
        return new Condition(property, Type.IS_NULL);
    }

    public static Condition isNotNull(String property) {
        return new Condition(property, Type.IS_NOT_NULL);
    }

    public static Condition in(String property) {
        return new Condition(property, Type.IN);
    }

    public static Condition like(String property) {
        return new Condition(property, Type.LIKE);

    }

    public static Condition between(String property) {
        return new Condition(property, Type.BETWEEN);
    }

    public static Condition and(Condition... conditions) {
        return new Condition(Type.AND,conditions);
    }

    public static Condition or(Condition... conditions) {
        return new Condition(Type.OR,conditions);
    }

}

