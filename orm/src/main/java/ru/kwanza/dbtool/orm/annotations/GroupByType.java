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

/**
 * Тип крупировки
 * <ul>
 * <li><B>MAP</B> - групировка по уникальному полю. Результатом группировки является карта <i>Map&lt;?,?&gt;</i></li>
 * <li><B>MAP_OF_LIST</B> - групировка по неуникальному полю. Результатом группировки является карта <i>Map&lt;?,List&lt;?&gt;&gt;</i></li>
 * </ul>
 *
 * @author Alexander Guzanov
 * @see ru.kwanza.dbtool.orm.annotations.GroupBy
 */
public enum GroupByType {
    /**
     * групировка по уникальному полю. Результатом группировки является карта <i>Map&lt;?,?&gt;</i></li>
     */
    MAP,
    /**
     * групировка по неуникальному полю. Результатом группировки является карта <i>Map&lt;?,List&lt;?&gt;&gt;</i></li>
     */
    MAP_OF_LIST;
}
