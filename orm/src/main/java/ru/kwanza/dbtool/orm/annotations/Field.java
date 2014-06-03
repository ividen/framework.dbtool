package ru.kwanza.dbtool.orm.annotations;

import org.springframework.jdbc.core.SqlTypeValue;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Мэппинг поля на колонку таблицы
 *
 * @author Alexander Guzanov
 */
@Retention(RUNTIME)
@Target({FIELD, METHOD})
public @interface Field {

    /**
     * имя колонки в таблице
     * @see Field
     */
    String value();

    /**
     * Тип колонки в таблице. Это должна быть одна из констант в  of java.sql.Types
     * @see Field
     */
    int type() default SqlTypeValue.TYPE_UNKNOWN;
}
