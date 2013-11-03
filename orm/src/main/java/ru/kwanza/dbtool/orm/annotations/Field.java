package ru.kwanza.dbtool.orm.annotations;

import org.springframework.jdbc.core.SqlTypeValue;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Мэппинг "обычного" поля на колонку таблицы
 *
 * @author Alexander Guzanov
 */
@Retention(RUNTIME)
@Target({FIELD, METHOD})
public @interface Field {

    /**
     * имя колонки в таблице
     */
    String value();

    /**
     * Тип колонки в таблице. Это должна быть одна из констант в  of java.sql.Types
     *
     */
    int type() default SqlTypeValue.TYPE_UNKNOWN;
}
