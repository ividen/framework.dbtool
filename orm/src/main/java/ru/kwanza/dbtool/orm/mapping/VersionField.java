package ru.kwanza.dbtool.orm.mapping;

/**
 * @author Alexander Guzanov
 */
public @interface VersionField {

    String columnName() default "";
}
