package ru.kwanza.dbtool.orm.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * @author Alexander Guzanov
 */

@Retention(RUNTIME)
@Target(TYPE)
public @interface AbstractEntity {
    String name();
}
