/*
 * @(#)CachedArrayList.java 10/5/2005
 *
 * Copyright 2002 - 2005 JIDE Software Inc. All rights reserved.
 */
package com.jidesoft.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Set;

/**
 * This is a fast access ArrayList that sacrifices memory for speed. It will
 * reduce the speed of indexOf method from O(n) to O(1). However it will at least double
 * the memory used by ArrayList. So use it approriately.
 */
public class CachedArrayList<E> extends ArrayList<E> {
    private HashMap<Object, Integer> _indexCache;

    public CachedArrayList() {
    }

    public CachedArrayList(Collection<? extends E> c) {
        super(c);
    }

    public CachedArrayList(int initialCapacity) {
        super(initialCapacity);
    }

    @Override
    public int indexOf(Object elem) {
        initializeCache();
        Integer o = _indexCache.get(elem);
        if (o != null) {
            return o;
        }
        else {
            int i = super.indexOf(elem);
            _indexCache.put(elem, i);
            return i;
        }
    }

    /**
     * Adjusts the cache so that all values that are greater than index will increase by the value specified by the increase parameter.
     *
     * @param index    the index. All values above this index will be changed.
     * @param increase a positive number to increase or a negative number to decrease.
     */
    protected void adjustCache(int index, int increase) {
        if (_indexCache != null) {
            Set<Object> keys = _indexCache.keySet();
            for (Object key : keys) {
                int value = _indexCache.get(key);
                if (value > index) {
                    _indexCache.put(key, value + increase);
                }
            }
        }
    }

    @Override
    public boolean add(E o) {
        boolean added = super.add(o);
        if (added) {
            initializeCache();
            _indexCache.put(o, size() - 1);
        }
        return added;
    }

    @Override
    public void add(int index, E element) {
        super.add(index, element);
        adjustCache(index, 1);
        initializeCache();
        _indexCache.put(element, index);
    }

    private void initializeCache() {
        if (_indexCache == null) {
            _indexCache = new HashMap();
        }
    }

    @Override
    public E remove(int index) {
        E element = super.remove(index);
        if (element != null) {
            adjustCache(index, -1);
        }
        return element;
    }

    @Override
    public boolean remove(Object o) {
        int oldIndex = indexOf(o);
        boolean removed = super.remove(o);
        if (removed) {
            adjustCache(oldIndex, -1);
        }
        return removed;
    }

    @Override
    public boolean addAll(int index, Collection<? extends E> c) {
        boolean added = super.addAll(index, c);
        initializeCache();
        adjustCache(index, c.size());
        for (E e : c) {
            _indexCache.put(e, index++);
        }
        return added;
    }

    @Override
    public E set(int index, E element) {
        initializeCache();
        Object old = _indexCache.get(get(index));
        E e = super.set(index, element);
        if (old != null) {
            _indexCache.remove(old);
        }
        _indexCache.put(e, index);
        return e;
    }

    /**
     * Invalidated the whole cache.
     */
    public void invalidateCache() {
        _indexCache = null;
    }
}
