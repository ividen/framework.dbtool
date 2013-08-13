package ru.kwanza.dbtool.orm.api;

/**
 * @author Alexander Guzanov
 */
public final class Join {
    private final Type type;
    private final String propertyName;
    private final Join[] subJoins;

    public enum Type {
        INNER,
        LEFT,
        RIGHT,
        OUTER
    }

    Join(Type type, String propertyName, Join[] subJoins) {
        this.propertyName = propertyName;
        this.type = type;
        this.subJoins = subJoins;
    }

    public static Join left(String property, Join... subJoins) {
        return new Join(Type.LEFT, property, subJoins);
    }

    public static Join right(String property, Join... subJoins) {
        return new Join(Type.RIGHT, property, subJoins);
    }

    public static Join inner(String property, Join... subJoins) {
        return new Join(Type.INNER, property, subJoins);
    }

    public static Join outer(String property, Join... subJoins) {
        return new Join(Type.OUTER, property, subJoins);
    }

}
