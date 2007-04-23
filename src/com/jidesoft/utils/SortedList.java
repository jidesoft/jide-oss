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
public class SortedList implements List {
    private Comparator comparator;
    private List delegate;

    public SortedList(List delegate, Comparator comparator) {
        this.delegate = delegate;
        this.comparator = comparator;
    }

    public void add(int index, Object element) {
        // no indexed insertion supported
        add(element);
    }

    public boolean add(Object o) {
        int size = delegate.size();
        for (int i = 0; i < size; i++) {
            if (comparator.compare(o, delegate.get(i)) > 0) {
                delegate.add(i, o);
                return true;
            }
        }
        delegate.add(o);
        return true;
    }

    public boolean addAll(Collection c) {
        return delegate.addAll(c);
    }

    public boolean addAll(int index, Collection c) {
        return delegate.addAll(index, c);
    }

    public void clear() {
        delegate.clear();
    }

    public boolean contains(Object o) {
        return delegate.contains(o);
    }

    public boolean containsAll(Collection c) {
        return delegate.containsAll(c);
    }

    public boolean equals(Object o) {
        return delegate.equals(o);
    }

    public Object get(int index) {
        return delegate.get(index);
    }

    public int hashCode() {
        return delegate.hashCode();
    }

    public int indexOf(Object o) {
        return delegate.indexOf(o);
    }

    public boolean isEmpty() {
        return delegate.isEmpty();
    }

    public Iterator iterator() {
        return delegate.iterator();
    }

    public int lastIndexOf(Object o) {
        return delegate.lastIndexOf(o);
    }

    public ListIterator listIterator() {
        return delegate.listIterator();
    }

    public ListIterator listIterator(int index) {
        return delegate.listIterator(index);
    }

    public Object remove(int index) {
        return delegate.remove(index);
    }

    public boolean remove(Object o) {
        return delegate.remove(o);
    }

    public boolean removeAll(Collection c) {
        return delegate.removeAll(c);
    }

    public boolean retainAll(Collection c) {
        return delegate.retainAll(c);
    }

    public Object set(int index, Object element) {
        return delegate.set(index, element);
    }

    public int size() {
        return delegate.size();
    }

    public List subList(int fromIndex, int toIndex) {
        return delegate.subList(fromIndex, toIndex);
    }

    public Object[] toArray() {
        return delegate.toArray();
    }

    public Object[] toArray(Object[] a) {
        return delegate.toArray(a);
    }

    public static void main(String[] args) {
        List sortedList = new SortedList(new ArrayList(), new Comparator() {
            public int compare(Object o1, Object o2) {
                return ((String) o2).compareTo((String) o1);
            }
        });
        sortedList.add("test");
        sortedList.add("aaa");
        sortedList.add("ddd");
        sortedList.add("ccc");
        for (int i = 0; i < sortedList.size(); i++) {
            String s = (String) sortedList.get(i);
            System.out.println(s);
        }
    }
}
