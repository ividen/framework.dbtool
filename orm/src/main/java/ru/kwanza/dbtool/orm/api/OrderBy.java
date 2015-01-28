package ru.kwanza.dbtool.orm.api;

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

/**
 * Описание сортировки.
 * <p/>
 * Конструировании  и использовании объекта в {@link IQueryBuilder} и {@lin IFiltering}
 * нужно учитывать, что сортировку можно указать по полю связанной сущности (любой вложенности).
 * В этом случае эти сущности будет добавляться в запрос
 * в виде <b>INNER JOIN</b> пересечение(тип пересечения можно поменять на <B>LEFT JOIN</B> используя {@link ru.kwanza.dbtool.orm.api.IQueryBuilder#join}
 * и {@link ru.kwanza.dbtool.orm.api.IFiltering#join } соотвественно
 *
 * @author Alexander Guzanov
 * @see ru.kwanza.dbtool.orm.api.IFiltering#orderBy(boolean, OrderBy)
 * @see ru.kwanza.dbtool.orm.api.IQueryBuilder#orderBy(OrderBy)
 */
public class OrderBy {
    public static final String ASC = "ASC";
    public static final String DESC = "DESC";
    private String propertyName;
    private String type;

    private OrderBy(String propertyName, String type) {
        this.propertyName = propertyName;
        this.type = type;
    }

    /**
     * Имя свойсва, по которому выполняется сортировка
     */
    public String getPropertyName() {
        return propertyName;
    }

    /**
     * Тип сортировки
     * <ul>
     * <li><B>ASC</B> - по возрастанию </li>
     * <li><B>DESC</B> - по убыванию </li>
     * </ul>
     */
    public String getType() {
        return type;
    }


    /**
     * Сортировка по возрастанию
     *
     * @param propertyName имя поля
     * @return
     */
    public static OrderBy ASC(String propertyName) {
        return new OrderBy(propertyName, ASC);
    }

    /**
     * Сортировка по убыванию
     *
     * @param propertyName имя свойства
     * @return
     */
    public static OrderBy DESC(String propertyName) {
        return new OrderBy(propertyName, DESC);
    }


}
