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

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Аннотация для абстрактной сущности, которая позволяет строить компонентную иерархию.
 * </p>
 * Каждый наследник этой сущности будет располагать своей отдельной таблицой и общим набором полей.
 * При построении запросов к сущностям, помеченным данной аннотации строится UNION запрос, объединяющий таблицы сущностей наследников.
 * <p/>
 * Пример:
 * <pre>{@code @AbstractEntity(name="PaymentTrx")
 * public class PaymentTrx{
 *    @literal @IdField("id")
 *     private Long id;
 *    @literal @Field("started_at")
 *     private Date startedAt;
 *    @literal @Field("finished_at")
 *     private Date finishedAt;
 *    @literal @Field("result_code")
 *     private Integer resultCode;
 *    @literal @Field("amount")
 *     private Long amount;
 *              ...
 * }
 *
 * @author Alexander Guzanov
 * @Entity(name="OnlinePaymentTrx", table="online_payment_trx")
 * public class OnlinePaymentTrx extends PaymentTrx {
 * ...
 * }
 * @Entity(name="OfflinePaymentTrx", table="offline_payment_trx")
 * public class OfflinePaymentTrx extends PaymentTrx {
 * ...
 * }
 *
 * ....
 *
 * IQuery<PaymentTrx> q = em.queryBuilder(PaymentTrx.class).where(If.isEquals("resultCode",If.valueOf(0l))).create();
 * // внутри списка находятся элементы конкретных типов OnlinePaymentTrx или OfflinePaymentTrx
 * List<PaymentTrx> result = q.prepare().selectList()
 * }</pre>
 * <p/>
 * Таким образом будет выполнен запрос:
 * <pre>{@code SELECT * FROM (SELECT ... FROM online_payment_trx UNION ALL SELECT ... FROM offline_payment_trx) T WHERE T.result_code=0
 * }
 * </pre>
 * <p/>
 * Абстрактные сущности могут использоваться для указания отношений, можно  фетчить и джоинить эти сущности.
 * @see ru.kwanza.dbtool.orm.api.IQueryBuilder
 * @see ru.kwanza.dbtool.orm.api.IQuery
 * @see ru.kwanza.dbtool.orm.api.IStatement
 * @see ru.kwanza.dbtool.orm.annotations.Entity
 */
@Retention(RUNTIME)
@Target(TYPE)
public @interface AbstractEntity {
    /**
     * Имя сущности
     * @see ru.kwanza.dbtool.orm.annotations.AbstractEntity
     */
    String name() default "";
}
