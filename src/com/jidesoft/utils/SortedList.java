/*
 * @(#)SortedList.java 1/10/2007
 *
 * Copyright 2002 - 2007 JIDE Software Inc. All rights reserved.
 */

package com.jidesoft.utils;

import java.util.*;

/**
 * @author Patrick Gotthardt
 */
public class SortedList<E> implements List<E> {
    private Comparator<E> comparator;
    private List<E> delegate;

    public SortedList(List<E> delegate, Comparator comparator) {
        this.delegate = delegate;
        this.comparator = comparator;
    }

    public void add(int index, E element) {
        // no indexed insertion supported
        add(element);
    }

    public boolean add(E o) {
        int size = delegate.size();
        for (int i = 0; i < size; i++) {
            if (comparator.compare(delegate.get(i), o) > 0) {
                delegate.add(i, o);
                return true;
            }
        }
        delegate.add(o);
        return true;
    }

    public boolean addAll(Collection<? extends E> c) {
        return delegate.addAll(c);
    }

    public boolean addAll(int index, Collection<? extends E> c) {
        return delegate.addAll(index, c);
    }

    public void clear() {
        delegate.clear();
    }

    public boolean contains(Object o) {
        return delegate.contains(o);
    }

    public boolean containsAll(Collection<?> c) {
        return delegate.containsAll(c);
    }

    @Override
    public boolean equals(Object o) {
        return delegate.equals(o);
    }

    public E get(int index) {
        return delegate.get(index);
    }

    @Override
    public int hashCode() {
        return delegate.hashCode();
    }

    public int indexOf(Object o) {
        return delegate.indexOf(o);
    }

    public boolean isEmpty() {
        return delegate.isEmpty();
    }

    public Iterator<E> iterator() {
        return delegate.iterator();
    }

    public int lastIndexOf(Object o) {
        return delegate.lastIndexOf(o);
    }

    public ListIterator<E> listIterator() {
        return delegate.listIterator();
    }

    public ListIterator<E> listIterator(int index) {
        return delegate.listIterator(index);
    }

    public E remove(int index) {
        return delegate.remove(index);
    }

    public boolean remove(Object o) {
        return delegate.remove(o);
    }

    public boolean removeAll(Collection<?> c) {
        return delegate.removeAll(c);
    }

    public boolean retainAll(Collection<?> c) {
        return delegate.retainAll(c);
    }

    public E set(int index, E element) {
        return delegate.set(index, element);
    }

    public int size() {
        return delegate.size();
    }

    public List<E> subList(int fromIndex, int toIndex) {
        return delegate.subList(fromIndex, toIndex);
    }

    public Object[] toArray() {
        return delegate.toArray();
    }

    public Object[] toArray(Object[] a) {
        return delegate.toArray(a);
    }

    public static void main(String[] args) {
        List<String> sortedList = new SortedList(new ArrayList(), new Comparator<String>() {
            public int compare(String o1, String o2) {
                return o1.compareTo(o2);
            }
        });
        sortedList.add("test");
        sortedList.add("aaa");
        sortedList.add("ddd");
        sortedList.add("ccc");
        for (String s : sortedList) {
            System.out.println(s);
        }
    }
}
