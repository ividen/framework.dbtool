package ru.kwanza.dbtool.orm.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.sql.Types;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Описывает  ключевое поле (идентификатор) для сущности
 *
 * @author Alexander Guzanov
 */
@Retention(RUNTIME)
@Target({FIELD, METHOD})
public @interface IdField {

    /**
     * название колонки в базе данных
     * @see ru.kwanza.dbtool.orm.annotations.IdField
     */
    String value();

    /**
     * Тип колонки. Это должна быть константа из  of java.sql.Types
     * @see ru.kwanza.dbtool.orm.annotations.IdField
     */
    int type() default Types.BIGINT;
}
