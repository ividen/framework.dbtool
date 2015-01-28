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

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Фабрика списков
 *
 * @author Alexander Guzanov
 * @see ru.kwanza.dbtool.orm.api.ISelectOperationProvider#selectMapList(String, java.util.Map, ListProducer)
 * @see <a href="http://en.wikipedia.org/wiki/Abstract_factory_pattern">Шаблон "Абстрактная фабрика"</a>
 */
public abstract class ListProducer<T> {
    /**
     * Фабрика списка {@link java.util.ArrayList}
     */
    public static final ListProducer ARRAY_LIST = new ListProducer() {
        @Override
        public List create() {
            return new ArrayList();
        }
    };

    /**
     * Фабрика списка {@link java.util.ArrayList}
     */
    public static final ListProducer LINKED_LIST = new ListProducer() {
        @Override
        public List create() {
            return new LinkedList();
        }
    };

    public abstract List<T> create();

    /**
     * Фабрика списка определенного класса
     *
     * @param type тип списка
     */
    public static <T> ListProducer<T> create(final Class<? extends List> type) {
        return new ListProducer<T>() {
            @Override
            public List<T> create() {
                try {
                    return (List<T>) type.newInstance();
                } catch (Exception e) {
                    throw new RuntimeException("Can't instantiate list " + type.getName());
                }
            }
        };
    }
}
