package ru.kwanza.dbtool.orm.annotations;

/*
 * #%L
 * dbtool-orm
 * %%
 * Copyright (C) 2015 Kwanza
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

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
