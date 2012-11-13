package ru.kwanza.dbtool.core;

import org.springframework.jdbc.core.SqlParameterValue;

import java.util.*;

/**
 * @author Alexander Guzanov
 */
public class SqlCollectionParameterValue<T> extends SqlParameterValue implements List<T> {

    public SqlCollectionParameterValue(int sqlType, Collection<T> value) {
        super(sqlType, new ArrayList<T>(value));
    }

    public List<T> getCollectionValue() {
        return (List) super.getValue();
    }

    public int size() {
        return getCollectionValue().size();
    }

    public boolean isEmpty() {
        return getCollectionValue().isEmpty();
    }

    public boolean contains(Object o) {
        return getCollectionValue().contains(o);
    }

    public Iterator iterator() {
        return getCollectionValue().iterator();
    }

    public Object[] toArray() {
        return getCollectionValue().toArray();
    }

    public <T> T[] toArray(T[] a) {
        return getCollectionValue().toArray(a);
    }

    public boolean add(T o) {
        return getCollectionValue().add(o);
    }

    public boolean remove(Object o) {
        return getCollectionValue().remove(o);
    }

    public boolean containsAll(Collection<?> c) {
        return getCollectionValue().containsAll(c);
    }

    public boolean addAll(int index, Collection<? extends T> c) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public boolean addAll(Collection c) {
        return getCollectionValue().addAll(c);
    }

    public boolean removeAll(Collection<?> c) {
        return getCollectionValue().removeAll(c);
    }

    public boolean retainAll(Collection<?> c) {
        return getCollectionValue().retainAll(c);
    }

    public void clear() {
        getCollectionValue().clear();
    }

    public T get(int index) {
        return getCollectionValue().get(index);
    }

    public T set(int index, T element) {
        return getCollectionValue().set(index, element);
    }

    public void add(int index, T element) {
        getCollectionValue().add(index, element);
    }

    public T remove(int index) {
        return getCollectionValue().remove(index);
    }

    public int indexOf(Object o) {
        return getCollectionValue().indexOf(o);
    }

    public int lastIndexOf(Object o) {
        return getCollectionValue().lastIndexOf(o);
    }

    public ListIterator<T> listIterator() {
        return getCollectionValue().listIterator();
    }

    public ListIterator<T> listIterator(int index) {
        return getCollectionValue().listIterator();
    }

    public List<T> subList(int fromIndex, int toIndex) {
        return getCollectionValue().subList(fromIndex, toIndex);
    }
}
