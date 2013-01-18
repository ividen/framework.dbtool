package ru.kwanza.dbtool.orm.api;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Alexander Guzanov
 */
public abstract class ListProducer<T> {
    public static final ListProducer ARRAY_LIST = new ListProducer() {
        @Override
        public List create() {
            return new ArrayList();
        }
    };

    public static final ListProducer LINKED_LIST = new ListProducer() {
        @Override
        public List create() {
            return new LinkedList();
        }
    };

    public abstract List<T> create();
}
