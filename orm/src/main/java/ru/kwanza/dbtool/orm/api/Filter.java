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
 * объект описывающий фильтр для использования в {@link ru.kwanza.dbtool.orm.api.IFiltering}
 *
 * @author Alexander Guzanov
 */
public class Filter {
    private If condition;
    private boolean use;
    private Object[] params;

    /**
     * Конструктор
     *
     * @param use       использовать ли предикат при построении запроса
     * @param condition sql-предикат
     * @param params    список параметров для предиката
     * @see ru.kwanza.dbtool.orm.api.IFiltering
     * @see ru.kwanza.dbtool.orm.api.If
     */
    public Filter(boolean use, If condition, Object... params) {
        this.use = use;
        this.condition = condition;
        this.params = params;
    }

    /**
     * sql предикат для фильтра
     *
     * @see ru.kwanza.dbtool.orm.api.IFiltering
     */
    public If getCondition() {
        return condition;
    }

    /**
     * использовать ли фильтр при построении запроса
     */
    public boolean isUse() {
        return use;
    }

    /**
     * Параметры для sql-предиката
     */
    public Object[] getParams() {
        return params;
    }
}
