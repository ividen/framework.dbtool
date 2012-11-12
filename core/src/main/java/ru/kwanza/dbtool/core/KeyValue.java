package ru.kwanza.dbtool.core;

import java.util.Map;

/**
 * @author Guzanov Alexander
 */
public class KeyValue<K, V> implements Map.Entry<K, V> {
    private K key;
    private V value;

    public KeyValue(K key, V value) {
        this.key = key;
        this.value = value;
    }

    public K getKey() {
        return key;
    }

    public V getValue() {
        return value;
    }

    public V setValue(V value) {
        V result = this.value;
        this.value = value;
        return result;
    }
}
