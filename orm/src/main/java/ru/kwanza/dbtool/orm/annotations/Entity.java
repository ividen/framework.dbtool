package ru.kwanza.dbtool.orm.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Описывает взаимосвязь между java-классом и таблицой в базе данных
 * <p/>
 * Для сущностей помеченных такой аннотацией осуществляется парсинг её полей,
 * определяются поля помеченные {@link ru.kwanza.dbtool.orm.annotations.Field},{@link ru.kwanza.dbtool.orm.annotations.IdField},
 * {@link ru.kwanza.dbtool.orm.annotations.VersionField}.
 * <p/>
 * Ограничения:
 * <ol>
 * <li>У сущности должна быть одно и только одно поле {@link ru.kwanza.dbtool.orm.annotations.IdField}</li>
 * <li>У сущности может быть только одно поле {@link ru.kwanza.dbtool.orm.annotations.VersionField}</li>
 * <li>Поле {@link ru.kwanza.dbtool.orm.annotations.VersionField} должно быть типа {@link java.lang.Long}</li>
 * </ol>
 * <p/>
 * При обработке полей  "наследуются" поля с аннотациями из родительской сущности
 * <p/>
 * Пример:
 * <pre>{@code public abstract class AbstractEntity{
 *  @literal @IdField("id")
 *    private Long id;
 *  @literal @VersionField("version")
 *    private Long version;
 *  @literal @Field("title")
 *    private String title;
 * }
 *  @literal @Entity(name="EntityA", table="entity_A"
 *    public class EntityA extends AbstractEntity{
 *  @literal @Field("int_field")
 *    private Integer intField;
 *  }
 *
 * @author Alexander Guzanov
 * @literal @Entity(name="EntityB", table="entity_A"
 * public class EntityB extends AbstractEntity{
 * @literal @Field("long_field")
 * private Long longField;
 * }
 * }
 * </pre>
 * <p/>
 * В данном примере в для сущностей типа <i>EntityA</i> и <i>EntityB</i>  в таблицах <i>entity_A</i> и <i>entity_B</i> будут поля <i>id</i>,<i>version</i>,<i>title</i>.
 * <p/>
 * При этом нужно учитывать, что <i>AbstractEntity</i> не будет ORM-сущностью(см. {@link ru.kwanza.dbtool.orm.annotations.AbstractEntity})
 * <p/>
 * В некоторых случаях может возникнуть потребность замэпить сущность не на всю таблицу, а не некоторую выборку.
 * В этом случае используется {@link #sql()}, и эти сущности естественно являются readOnly - для них не работают CUD операции
 * <p/>
 * Пример:
 * <pre>{@code @Entity(name="SuccessTrx, sql="SELECT * FROM payment_trx WHERE result_code=1",table="_success_trx_alias")
 * public class SuccessTrx{
 * ...
 * }}</pre>
 * @see ru.kwanza.dbtool.orm.annotations.IdField
 * @see ru.kwanza.dbtool.orm.annotations.VersionField
 * @see ru.kwanza.dbtool.orm.annotations.Field
 */
@Retention(RUNTIME)
@Target(TYPE)
public @interface Entity {

    /**
     * Имя сущности
     * <p/>
     * По умочанию в качестве имени сущности будет выбран {@link Class#getSimpleName()}
     *
     * @see ru.kwanza.dbtool.orm.annotations.Entity
     */
    String name() default "";

    /**
     * Маппинг на сущности на native sql
     * Если указывается такой мэпинг {@link #table()} должен содержать алиас такой сущности - любой уникальной в рамках ORM
     * модели приложения имя.
     *
     * @see ru.kwanza.dbtool.orm.annotations.Entity
     */
    String sql() default "";

    /**
     * Название таблицы на которую осуществляется мэппинг
     *
     * @see ru.kwanza.dbtool.orm.annotations.Entity
     */
    String table();
}
