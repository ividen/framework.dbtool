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

import java.util.List;
import java.util.Map;

/**
 * Базовый интервейс для выделяния общих метдов для выборки данных
 *
 * @author Alexander Guzanov
 * @see ru.kwanza.dbtool.orm.api.IStatement
 * @see ru.kwanza.dbtool.orm.api.IFiltering
 */
public interface ISelectOperationProvider<T> {
    /**
     * Выбрать один объект
     */
    T select();

    /**
     * выбрать список объектов
     */
    List<T> selectList();

    /**
     * Выбрать список объектов и сгруппировать их по значению неуникального свойства
     *
     * @param propertyName имя свойства объектов
     * @param <F>          - тип поля
     * @return карта списков объектов. Ключ  - значения поля, значения - список объектов.
     * Предполагается, что в выбранном списке может быть несколько объектов с одинаковым значения соотвествующего поля
     * @see #selectMap
     */
    <F> Map<F, List<T>> selectMapList(String propertyName);

    /**
     * Выбрать список объектов и сгруппировать их по значению уникального свойства
     *
     * @param propertyName имя свойства объектов
     * @param <F>          - тип поля
     * @return карта списков объектов. Ключ  - значения поля, значения - Объект.
     * Предполагается, что в выбранном списке может быть только один объект с одинаковым значения соотвествующего поля
     */
    <F> Map<F, T> selectMap(String propertyName);

    /**
     * Выбрать объекты и положить их в список.
     * <p/>
     * По сравнение с  методом {@link #selectList()}, данный позволяет выбрать тип списка, в который выбираются объекты
     *
     * @param result список, в который кладутся выбранные объекты
     */
    void selectList(List<T> result);

    /**
     * Выбрать список объектов и сгруппировать их по значению неуникального свойства.
     * <p/>
     * По сравнение с  методом {@link #selectMapList(String)} , данный позволяет выбрать тип ассоциативного массива и тип списка, в который выбираются объекты
     * Предполагается, что в выбранном списке может быть несколько объектов с одинаковым значения соотвествующего поля
     *
     * @param propertyName имя свойства по которому нужно группировать
     * @param result       результирующий ассоциативный массив
     * @param listProducer контруктор список
     * @param <F>          имя поля
     */
    <F> void selectMapList(String propertyName, Map<F, List<T>> result, ListProducer<T> listProducer);

    /**
     * Выбрать список объектов и сгруппировать их по значению уникального свойства
     * <p/>
     * По сравнение с  методом {@link #selectMap(String)}  , данный позволяет выбрать тип ассоциативного массива, в который выбираются объекты.
     * Предполагается, что в выбранном списке может быть только один объект с одинаковым значения соотвествующего поля.
     *
     * @param propertyName имя свойства по которому нужно группировать
     * @param result       результирующий ассоциативный массив
     * @param <F>          имя поля
     */
    <F> void selectMap(String propertyName, Map<F, T> result);
}
