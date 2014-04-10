package ru.kwanza.dbtool.orm.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Описание общей связи между двумя сущностями.
 * <p/>
 * Основное отличие от {@link ru.kwanza.dbtool.orm.annotations.OneToMany} и {@link ru.kwanza.dbtool.orm.annotations.ManyToOne} - сущности можно связать не только с использованием первчиных ключей
 * Соотвествнно ассоциация может применяться для описание двух видов отношения:
 * <ul>
 * <li>один-ко-многим - в этом случае связанное поле представляет собой коллекцию</li>
 * <li>многие-к-одному - в этом случае связанное поле представляет собой одиночный объект</li>
 * </ul>
 * <p/>
 * Например:
 * <pre>{@code @Entity(name="EntityA", table="entity_A")
 *  public class EntityA{
 *       .....
 *    @literal @Field("code"
 *     private String code;
 *   @literal @Association(property="code", relationProperty="entityACode", relationClass=EntityB.class)
 *     private Collection<EntityB> entitiesB
 *     .....
 *  }
 *
 * @literal @Entity(name="EntityB", table="entity_B")
 * public class EntityB{
 * .....
 * @literal @Field("entity_a_Code")
 * private String entityACode;
 * @literal @Association(property="entityACode", relationProperty="code")
 * private EntityA entityA
 * .....
 * }
 * }</pre>
 * <p/>
 * Стоит заметить, что поля связи являются немутабельными: т.е для установки связей, нужно либо создавать сущность, либо заполнять соотвествующее поля ссылки.
 * Пример:
 * <pre>{@code @Entity(name="EntityA", table="entity_A")
 * EntityB entityB = ...;
 * EntityA entityA = ...;
 * entityB.setEntityACode(entityA.getCode());// work
 * entityB.setEntityA(entityA); // don't work
 * }</pre>
 *
 * Данная аннотация может использоваться не только внутри классов, помеченных {@link ru.kwanza.dbtool.orm.annotations.Entity} и {@link ru.kwanza.dbtool.orm.annotations.AbstractEntity},
 * но и для обычных plain java object. В этом случае для таких классов можно фетчить эти зависимости с помощью {@link ru.kwanza.dbtool.orm.api.IEntityManager#fetch}
 * или {@link ru.kwanza.dbtool.orm.api.IEntityManager#fetchLazy(Class, java.util.Collection) }
 *
 * Пример:
 * <pre>{@code public class PaymentEvent{
 *   private String agentPCID;
 *
 *  @literal @Association(property="agentId", relationProperty="id", relationClass=Agent.class)
 *
 *   private Agent> agent
 *
 *     public Long getAgentId(){
 *         return new PCID(agentPCID).getInternalId();
 *     }
 *
 * }
 *  ...
 *
 *  Collection<PaymentEvent> events = ...;
 *  em.fetchLazy(PaymentEvent.clas, events);
 *
 *  for(PaymentEvent e: events){
 *      e.getAgent();// PROFIT!!!
 *  }
 *
 * }
 * </pre>
 *
 * @see ru.kwanza.dbtool.orm.annotations.OneToMany
 * @see ru.kwanza.dbtool.orm.annotations.ManyToOne
 * @see ru.kwanza.dbtool.orm.annotations.Condition
 * @see ru.kwanza.dbtool.orm.annotations.GroupBy
 */
@Retention(RUNTIME)
@Target({FIELD, METHOD})
public @interface Association {

    /**
     * Имя поля связи в сущности *
     * @see ru.kwanza.dbtool.orm.annotations.Association
     */
    String property();

    /**
     * Имя поля связи в связанной сущности *
     * @see ru.kwanza.dbtool.orm.annotations.Association
     */
    String relationProperty();

    /**
     * Тип связанной сущности
     * <p/>
     * Это поле нужно заполнять в том случае, если тип представляет собой коллекцию.
     * <p/>
     * Например:
     * <pre>{@code @Entity(name="EntityA", table="entity_A")
     *  public class EntityA{
     *       .....
     *    @literal @Field("code"
     *     private String code;
     *   @literal @Association(property="code", relationProperty="entityACode", relationClass=EntityB.class)
     *     private Collection<EntityB> entittiesB
     *     .....
     *  }
     *
     * @literal @Entity(name="EntityB", table="entity_B")
     * public class EntityB{
     * .....
     * @literal @Field("entity_a_Code")
     * private String entityACode;
     * @literal @Association(property="entityACode", relationProperty="code")
     * private EntityA entityA
     * <p/>
     * .....
     * }
     * }</pre>
      * @see ru.kwanza.dbtool.orm.annotations.Association
     */
    Class relationClass() default Object.class;
}
