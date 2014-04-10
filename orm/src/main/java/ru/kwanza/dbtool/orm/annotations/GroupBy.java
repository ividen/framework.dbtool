package ru.kwanza.dbtool.orm.annotations;

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
 * @author Alexander Guzanov
 */
@Retention(RUNTIME)
@Target({FIELD, METHOD})
public @interface GroupBy {
    /**
     * Поле по которому осуществялется группировка.
     * <p/>
     * Может  описывать поля связанных суностей
     */
    String value();

    /**
     * Тип группировки
     *
     * @see ru.kwanza.dbtool.orm.annotations.GroupByType
     */
    GroupByType type() default GroupByType.MAP;
}
