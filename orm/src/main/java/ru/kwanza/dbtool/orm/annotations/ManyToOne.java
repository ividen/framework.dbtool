package ru.kwanza.dbtool.orm.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Описывает взаимосвязь между двумя сущностями типа <b>многие к одному</b>
 * <p/>
 * Пример:
 * <pre>{@code @Entity(name="EntityA", table="entity_A")
 *  public class EntityA{
 *    @literal @IdField("id")
 *     private Long id;
 *          .....
 *  }
 *
 * @author Guzanov Alexander
 * @author Alexander Guzanov
 * @literal @Entity(name="EntityB", table="entity_B")
 * public class EntityB{
 *      .....
 * @literal @Field("entity_a_id")
 * private Long entityAId;
 * @literal @ManyToOne(property="entityAId")
 * private EntityA entityA
 *      .....
 * }
 * }</pre>
 * Стоит заметить, что поля связи являются немутабельными: т.е для установки связей нужно заполнять соотвествующее поля ссылки.
 * Пример:
 * <pre>{@code @Entity(name="EntityA", table="entity_A")
 * EntityB entityB = ...;
 * EntityA entityA = ...;
 * entityB.setEntityAId(entityA.getCode());// work
 * entityB.setEntityA(entityA); // do't work
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
 *  @literal @ManyToOne(property="agentId", relationClass=Agent.class)
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
 *
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
