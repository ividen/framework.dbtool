package ru.kwanza.dbtool.orm.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Дополнительные условия для связанных сущностей.
 * <p/>
 * Аннотация используется вместе с {@link ru.kwanza.dbtool.orm.annotations.OneToMany}, {@link ru.kwanza.dbtool.orm.annotations.ManyToOne}
 * {@link ru.kwanza.dbtool.orm.annotations.Association} если для связи нужно указать дополнительное условие.
 * <p/>
 * Формат строки условия - <a href="http://docs.spring.io/spring/docs/3.0.x/reference/expressions.html">SpEl</a>, в котором контекстом является
 * класс {@link ru.kwanza.dbtool.orm.api.If}.
 * <p/>
 * <p/>
 * Пример:
 * <pre>
 * {@code @Entity(name = "TestEntity", table = "test_entity")
 *  public class TestEntity {
 *
 *  @literal @IdField( "id")
 *   private Long id;
 *  @literal @Association(property = "entityAID", relationProperty = "id" )
 *  @literal @Condition("and(isNotNull('amount'),isGreater('amount',valueOf(-1)))")
 *   private EntityA entityAWithPositiveAmount;
 *
 * }
 * }</pre>
 *
 * Условия в отношениях могут быть и более сложным -  содержать условия по полям связанных сущности.
 * <pre>{@code
 *
 *@literal  @Entity(name = "TestEntity", table = "test_entity")
 *  public class TestEntity  implements Serializable{
 *  @literal @IdField( "id")
 *   private Long id;
 *  @literal @Condition("and(isNotNull('entityE.amount'),isGreater('entityE.amount',valueOf(-1)))")
 *   private TestEntityC entityCWhereEntityEAmountIsPositive;
 *  }
 *
 *  ...
 *
 *  IQuery<TestEntity> q = em.queryBuilder(TestEntity.class).join("!entityCWhereEntityEAmountIsPositive").create();
 *  List<TestEntity> result = q.prepare().selectList();
 * }</pre>
 *
 * В приведенном примере будет выполнен следующий сложный запрос:
 *
 * <pre>
 *    SELECT * FROM test_entity
 *         INNER JOIN (SELECT * FROM entity_C
 *                            INNER JOIN entity_E ON
 *                                entity_C.id=entityE.entity_C_id AND entity_E.amount Is Not Null AND entity_e.amount>-1))
 * </pre>
 *
 * @see ru.kwanza.dbtool.orm.annotations.Association
 * @see ru.kwanza.dbtool.orm.annotations.OneToMany
 * @see ru.kwanza.dbtool.orm.annotations.ManyToOne
 * @see ru.kwanza.dbtool.orm.api.IQueryBuilder#join(String)
 */
@Retention(RUNTIME)
@Target({FIELD, METHOD})
public @interface Condition {
    /**
     * Формат строки условия - <a href="http://docs.spring.io/spring/docs/3.0.x/reference/expressions.html">SpEl</a>, в котором контектом является
     * класс {@link ru.kwanza.dbtool.orm.api.If}.
     *
     * @see ru.kwanza.dbtool.orm.annotations.Condition
     */
    String value();
}
