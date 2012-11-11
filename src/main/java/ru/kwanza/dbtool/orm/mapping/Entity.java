package ru.kwanza.dbtool.orm.mapping;

/**
 * @author Alexander Guzanov
 */
public @interface Entity {

    String name() default "";

    String tableName() default "";
}
