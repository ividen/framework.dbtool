package ru.kwanza.dbtool.orm.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Описывает взаимосвязь между java-классом и таблицой в базе данных
 *
 * @author Alexander Guzanov
 */
@Retention(RUNTIME)
@Target(TYPE)
public @interface Entity {

    String name();

    String tableName();
}
