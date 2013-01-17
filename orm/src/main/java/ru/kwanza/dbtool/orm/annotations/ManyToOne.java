package ru.kwanza.dbtool.orm.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Описывает взаимосвязь между двумя сущностями
 *
 * @author Alexander Guzanov
 */
@Retention(RUNTIME)
@Target({FIELD, METHOD})
public @interface ManyToOne {

    /**
     * Имя свойства класса, которое используется для выборки  связанной сущности. <br>
     * Сущность с которой устанавливается связь определяются по типу свойства, к которому указана эта аннотация.
     */
    String property();
}
