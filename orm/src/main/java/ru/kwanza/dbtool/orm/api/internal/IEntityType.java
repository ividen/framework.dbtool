package ru.kwanza.dbtool.orm.api.internal;

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

import java.util.Collection;

/**
 * Метаинмормация о мэппинга в сущности
 *
 * @author Alexander Guzanov
 */
public interface IEntityType<T> {
    /**
     * Имя сущности
     * <p/>
     *
     * @see ru.kwanza.dbtool.orm.annotations.Entity#name()
     */
    String getName();

    /**
     * Имя таблицы
     * <p/>
     * см. {@link ru.kwanza.dbtool.orm.annotations.Entity#table()}
     */
    String getTableName();

    /**
     * Строка sql запроса, если был соответствующий мэппинг
     * <p/>
     *
     * @see ru.kwanza.dbtool.orm.annotations.Entity#sql()
     */
    String getSql();

    /**
     * Класс сущности
     */
    Class<T> getEntityClass();

    /**
     * Является ли сущность абстрактной
     * <p/>
     * Будет возвращать <i>true</i> если сущность была помечена {@link ru.kwanza.dbtool.orm.annotations.AbstractEntity}
     */
    boolean isAbstract();

    /**
     * Информация по ключевому полю
     *
     * @see ru.kwanza.dbtool.orm.api.internal.IFieldMapping
     */
    IFieldMapping getIdField();

    /**
     * Информация по полю версии
     *
     * @see ru.kwanza.dbtool.orm.api.internal.IFieldMapping
     */
    IFieldMapping getVersionField();

    /**
     * Получение информации по имени поля
     *
     * @param name имя поля
     * @see ru.kwanza.dbtool.orm.api.internal.IFieldMapping
     */
    IFieldMapping getField(String name);

    /**
     * Список полей, для которых описан мэпинг
     */
    Collection<? extends IFieldMapping> getFields();

    /**
     * Получение поля, описывающего связь между сущностями
     *
     * @param name имя поля связи
     * @see ru.kwanza.dbtool.orm.api.internal.IRelationMapping
     */
    IRelationMapping getRelation(String name);

    /**
     * Список полей, описывающих связь
     *
     * @see ru.kwanza.dbtool.orm.api.internal.IRelationMapping
     */
    Collection<IRelationMapping> getRelations();
}
