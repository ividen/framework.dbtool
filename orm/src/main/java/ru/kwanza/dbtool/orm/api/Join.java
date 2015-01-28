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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Описание способа выбора связанных сущностей.
 * <p/>
 * Три способа , которые влияют на построение запроса
 * <ul>
 * <li>{@link Join#inner} - при выборе используется пересечение INNER JOIN</li>
 * <li>{@link Join#left} - при выборе используется пересечение LEFT JOIN</li>
 * <li>{@link Join#fetch} -  связанные сущности выбираются  с помощью дополнительного запроса</li>
 * </ul>
 *
 * @author Alexander Guzanov
 * @see ru.kwanza.dbtool.orm.api.IQueryBuilder#join
 * @see <a href="http://en.wikipedia.org/wiki/Join_(SQL)">SQL JOIN</a>
 */
public final class Join {
    private final Type type;
    private final String propertyName;
    private final List<Join> subJoins;

    /**
     * Тип способа выбора объектов
     */
    public enum Type {
        /**
         * при выборе используется пересечение INNER JOIN
         *
         * @see Join#inner
         * @see <a href="http://en.wikipedia.org/wiki/Join_(SQL)">SQL JOIN</a>
         */
        INNER,
        /**
         * при выборе используется пересечение LEFT JOIN
         *
         * @see Join#left
         * @see <a href="http://en.wikipedia.org/wiki/Join_(SQL)">SQL JOIN</a>
         */
        LEFT,
        /**
         * связанные сущности выбираются  с помощью дополнительного запроса
         *
         * @see Join#fetch
         * @see ru.kwanza.dbtool.orm.api.IEntityManager#fetch
         */
        FETCH
    }

    Join(Type type, String propertyName, Join[] subJoins) {
        this.propertyName = propertyName;
        this.type = type;
        this.subJoins = subJoins == null ? Collections.<Join>emptyList() : Arrays.asList(subJoins);
    }


    Join(Type type, String propertyName, List<Join> subJoins) {
        this.propertyName = propertyName;
        this.type = type;
        this.subJoins = subJoins;
    }

    /**
     * Тип выбора связанной сущности
     *
     * @see ru.kwanza.dbtool.orm.api.Join.Type
     */
    public Type getType() {
        return type;
    }

    /**
     * Имя свойства, которое описывает связь
     *
     * @return
     */
    public String getPropertyName() {
        return propertyName;
    }

    /**
     * Связи, которые нужно дполнительно выбрирать для связи {@link #getPropertyName()}
     * @return
     */
    public List<Join> getSubJoins() {
        return subJoins;
    }

    /**
     * Описание пересечения типа LEFT JOIN
     *
     * @param property имя поля описывающее связь
     * @param subJoins дополнительные связи, которые нужно выбирать
     * @see Join
     */
    public static Join left(String property, Join... subJoins) {
        return new Join(Type.LEFT, property, subJoins);
    }

    /**
     * Описание пересечения типа INNER JOIN
     *
     * @param property имя поля описывающее связь
     * @param subJoins дополнительные связи, которые нужно выбирать
     * @see Join
     */
    public static Join inner(String property, Join... subJoins) {
        return new Join(Type.INNER, property, subJoins);
    }

    /**
     * Описание связи, которые нужно "зафетчить" дополнительным запросом
     *
     * @param property имя поля описывающее связь
     * @param subJoins дополнительные связи, которые нужно выбирать
     * @see Join
     */
    public static Join fetch(String property, Join... subJoins) {
        return new Join(Type.FETCH, property, subJoins);
    }

    /**
     * Описание пересечения типа LEFT JOIN
     *
     * @param property имя поля описывающее связь
     * @param subJoins дополнительные связи, которые нужно выбирать
     * @see Join
     */
    public static Join left(String property, List<Join> subJoins) {
        return new Join(Type.LEFT, property, subJoins);
    }

    /**
     * Описание пересечения типа INNER JOIN
     *
     * @param property имя поля описывающее связь
     * @param subJoins дополнительные связи, которые нужно выбирать
     * @see Join
     */
    public static Join inner(String property, List<Join> subJoins) {
        return new Join(Type.INNER, property, subJoins);
    }

    /**
     * Описание связи, которые нужно "зафетчить" дополнительным запросом
     *
     * @param property имя поля описывающее связь
     * @param subJoins дополнительные связи, которые нужно выбирать
     * @see Join
     */
    public static Join fetch(String property, List<Join> subJoins) {
        return new Join(Type.FETCH, property, subJoins);
    }
}
