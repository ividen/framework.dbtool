package ru.kwanza.dbtool.orm.annotations;

/**
 * @author Alexander Guzanov
 */
public @interface Entity {

    String name() default "";

    String tableName() default "";
}
