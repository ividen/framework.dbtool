package ru.kwanza.dbtool.orm.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Описывает поле версию. <br>
 * Поле должно быть типа {@link java.lang.Long}
 *
 * @author Alexander Guzanov
 */
@Retention(RUNTIME)
@Target({FIELD, METHOD})
public @interface VersionField {


    /**
     * название колонки в базе данных
     * @see ru.kwanza.dbtool.orm.annotations.IdField
     */
    String value();
}
