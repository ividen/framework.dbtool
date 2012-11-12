package ru.kwanza.dbtool.orm.annotations;

/**
 * @author Alexander Guzanov
 */
public @interface VersionField {

    String columnName() default "";
}
