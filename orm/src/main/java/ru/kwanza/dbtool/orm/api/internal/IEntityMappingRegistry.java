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

/**
 * Реестр ORM-сущностей
 *
 * Доступен из spring контекста по имени <b>dbtool.IEntityMappingRegistry</b>
 *
 * @author Kiryl Karatsetski
 */
public interface IEntityMappingRegistry {

    /**
     * Зарегистрировать новый класс.
     *
     * После регистрации метаинформация о классе доступна по {@link #getEntityType(Class)}
     *
     * @param entityClass класс регистрируемой сущности
     */
    IEntityType registerEntityClass(Class entityClass);

    /**
     * Проверить, рарегистрирован ли класс.
     *
     * @param entityClass имя класса
     */
    boolean isRegisteredEntityClass(Class entityClass);

    /**
     * Проверить, зарегистрирована ли сущность с данным именем
     *
     * @param entityName имя сущности, которое указывается в {@link ru.kwanza.dbtool.orm.annotations.Entity#name()}
     *
     */
    boolean isRegisteredEntityName(String entityName);

    /**
     * Получить метоописани сущности с данным именем
     * @param name имя сущности, которое указывется в {@link ru.kwanza.dbtool.orm.annotations.Entity#name()}
     */
    IEntityType getEntityType(String name);

    /**
     * Получение метаинформации по класса
     *
     * @param entityClass имя класса
     */
    IEntityType getEntityType(Class entityClass);
}
