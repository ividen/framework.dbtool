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

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Дополнительные группировка для связанных сущностей.
 * <p/>
 * Аннотация используется вместе с {@link ru.kwanza.dbtool.orm.annotations.OneToMany}, {@link ru.kwanza.dbtool.orm.annotations.ManyToOne}
 * {@link ru.kwanza.dbtool.orm.annotations.Association} если нужно чтобы результат представлял собой ассоциативный массив, где ключом является некое поле
 * <p/>
 * Формат строки условия:
 * <pre>
 *   Группировка =   GROUP_FIELD,GROUP_FIELD | GROUP_FIELD
 *   GROUP_FIELD =  (RELATED_ENTITY.FIELD) | FIELD
 *   RELATED_ENTITY = RELATION_FIELD| (RELATION_FIELD.RELATED_ENTITY)
 *   FIELD - поле сущности
 *   RELATION_FIELD - поле сущности описывающее связь
 * </pre>
 * <p/>
 * Пример:
 * <pre>{@code  @Entity(name="EntityA", table="entity_A")
 *  public class EntityA{
 *    @literal @IdField("id")
 *     private Long id;
 *    @literal @OneToMany(relationProperty="entityAId", relationClass=EntityB.class)
 *    @literal @GroupBy("entityB.entityC.code, id", type=GroupByType.MAP)
 *     private Map<String,Map<id,EntityB>> entitiesB
 *     .....
 *  }
 *
 * @literal @Entity(name="EntityB", table="entity_B")
 * public class EntityB{
 * .....
 * @literal @Field("entity_a_id")
 * private Long entityAId;
 * .....
 * }
 * }</pre>
 *
 * При этом при фетчинге таких связей связанные сущности будут объединятся с помощью INNER JOIN в "фетч" запросе.
 *
 *
 * @author Alexander Guzanov
 * @see ru.kwanza.dbtool.orm.annotations.Entity
 * @see ru.kwanza.dbtool.orm.annotations.OneToMany
 * @see ru.kwanza.dbtool.orm.annotations.ManyToOne
 * @see ru.kwanza.dbtool.orm.annotations.Association
 */
@Retention(RUNTIME)
@Target({FIELD, METHOD})
public @interface GroupBy {
    /**
     * Поле по которому осуществялется группировка.
     * <p/>
     * Может  описывать поля связанных суностей.
     * Формат строки условия:
     * <pre>
     *   Группировка =   GROUP_FIELD,GROUP_FIELD | GROUP_FIELD
     *   GROUP_FIELD =  (RELATED_ENTITY.FIELD) | FIELD
     *   RELATED_ENTITY = RELATION_FIELD| (RELATION_FIELD.RELATED_ENTITY)
     *   FIELD - поле сущности
     *   RELATION_FIELD - поле сущности описывающее связь
     * </pre>

     * @see ru.kwanza.dbtool.orm.annotations.GroupBy
     */
    String value();

    /**
     * Тип группировки
     * @see ru.kwanza.dbtool.orm.annotations.GroupBy
     * @see ru.kwanza.dbtool.orm.annotations.GroupByType
     */
    GroupByType type() default GroupByType.MAP;
}
