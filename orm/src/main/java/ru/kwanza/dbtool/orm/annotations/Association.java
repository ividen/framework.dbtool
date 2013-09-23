package ru.kwanza.dbtool.orm.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * @author Guzanov Alexander
 */
@Retention(RUNTIME)
@Target({FIELD, METHOD})
public @interface Association {

    String property();

    String relationProperty();

    Class relationClass() default Object.class;

    String condition() default "";
}
