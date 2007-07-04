/*
 * @(#)CachedArrayList.java 10/5/2005
 *
 * Copyright 2002 - 2005 JIDE Software Inc. All rights reserved.
 */
package com.jidesoft.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

/**
 * This is a fast access ArrayList that sacrifices memory for speed. It will
 * reduce the speed of indexOf method from O(n) to O(1). However it will at least double
 * the memory used by ArrayList. So use it approriately.
 */
public class CachedArrayList<E> extends ArrayList<E> {
    private HashMap _indexCache;

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
        if (_indexCache == null) {
            _indexCache = new HashMap();
        }
        Object o = _indexCache.get(elem);
        if (o != null && o instanceof Integer) {
            return (Integer) o;
        }
        else {
            int i = super.indexOf(elem);
            _indexCache.put(elem, i);
            return i;
        }
    }

    @Override
    public boolean add(E o) {
        invalidateCache();
        return super.add(o);
    }

    @Override
    public void add(int index, E element) {
        invalidateCache();
        super.add(index, element);
    }

    @Override
    public E remove(int index) {
        invalidateCache();
        return super.remove(index);
    }

    @Override
    public boolean remove(Object o) {
        invalidateCache();
        return super.remove(o);
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        invalidateCache();
        return super.addAll(c);
    }

    @Override
    public boolean addAll(int index, Collection<? extends E> c) {
        invalidateCache();
        return super.addAll(index, c);
    }

    @Override
    public E set(int index, E element) {
        return super.set(index, element);
    }

    public void invalidateCache() {
        _indexCache = null;
    }
}
