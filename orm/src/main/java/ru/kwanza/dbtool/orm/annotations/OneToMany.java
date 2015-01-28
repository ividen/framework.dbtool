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
 * Описание отношения между сущностями вида <b>один-ко-многим</b>
 *
 * Как правило тим поля для данного отношения представляет собой коллекцию.
 * <p/>
 * Например:
 * <pre>{@code @Entity(name="EntityA", table="entity_A")
 *  public class EntityA{
 *    @literal @IdField("id")
 *     private Long id;
 *   @literal @OneToMany(relationProperty="entityAId", relationClass=EntityB.class)
 *     private Collection<EntityB> entitiesB
 *     .....
 *  }
 *
 * @author Guzanov Alexander
 * @literal @Entity(name="EntityB", table="entity_B")
 * public class EntityB{
 * .....
 * @literal @Field("entity_a_id")
 * private Long entityAId;
 * .....
 * }
 * }</pre>
 *
 * Стоит заметить, что поля связи являются немутабельными: т.е для установки связей нужно создавать сущность или указывать поле ссылки.
 * Пример:
 * <pre>{@code @Entity(name="EntityA", table="entity_A")
 * EntityB entityB = ...;
 * EntityA entityA = ...;
 *
 * entityA.getEntitiesB().add(entityB);// don't work
 * entityB.setEntityAId(entityA.getId()); // work
 * }
 * </pre>
 *
 * @see ru.kwanza.dbtool.orm.annotations.Association
 * @see ru.kwanza.dbtool.orm.annotations.ManyToOne
 * @see ru.kwanza.dbtool.orm.annotations.Condition
 * @see ru.kwanza.dbtool.orm.annotations.GroupBy
 * @author Guzanov Alexander
 */
@Retention(RUNTIME)
@Target({FIELD, METHOD})
public @interface OneToMany {

    /**
     * Имя свойства класса, которое используется для выборки  связанной сущности. </br>
     * Сущность с которой устанавливается связь определяются по типу свойства, к которому указана эта аннотация,</br>
     * или свойство {@link #relationClass()}, если результатом выборки является коллекция
     * @see ru.kwanza.dbtool.orm.annotations.OneToMany
     */
    String relationProperty();

    /**
     * Тип связанной сущности.
     *
     * Значения атрибута указывается, если поле связи представляет собой коллекцию
     * @see ru.kwanza.dbtool.orm.annotations.OneToMany
     */
    Class relationClass() default Object.class;
}

