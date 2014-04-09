package ru.kwanza.dbtool.orm.api;

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
