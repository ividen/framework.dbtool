package ru.kwanza.dbtool.orm.api;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author Alexander Guzanov
 */
public final class Join {
    private final Type type;
    private final String propertyName;
    private final List<Join> subJoins;

    public enum Type {
        INNER,
        LEFT,
        FETCH
    }

    Join(Type type, String propertyName, Join[] subJoins) {
        this.propertyName = propertyName;
        this.type = type;
        this.subJoins = subJoins==null? Collections.<Join>emptyList(): Arrays.asList(subJoins);
    }


    Join(Type type, String propertyName, List<Join> subJoins) {
        this.propertyName = propertyName;
        this.type = type;
        this.subJoins = subJoins;
    }

    public Type getType() {
        return type;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public List<Join> getSubJoins() {
        return subJoins;
    }

    public static Join left(String property, Join... subJoins) {
        return new Join(Type.LEFT, property, subJoins);
    }

    public static Join inner(String property, Join... subJoins) {
        return new Join(Type.INNER, property, subJoins);
    }

    public static Join fetch(String property, Join... subJoins) {
        return new Join(Type.FETCH, property, subJoins);
    }

    public static Join left(String property, List<Join> subJoins) {
        return new Join(Type.LEFT, property, subJoins);
    }

    public static Join inner(String property, List<Join> subJoins) {
        return new Join(Type.INNER, property, subJoins);
    }

    public static Join fetch(String property, List<Join> subJoins) {
        return new Join(Type.FETCH, property, subJoins);
    }
}
