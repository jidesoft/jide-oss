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
public class CachedArrayList extends ArrayList {
    private HashMap _indexCache;

    public CachedArrayList() {
    }

    public CachedArrayList(Collection c) {
        super(c);
    }

    public CachedArrayList(int initialCapacity) {
        super(initialCapacity);
    }

    public int indexOf(Object elem) {
        if (_indexCache == null) {
            _indexCache = new HashMap();
        }
        Object o = _indexCache.get(elem);
        if (o != null && o instanceof Integer) {
            return ((Integer) o).intValue();
        }
        else {
            int i = super.indexOf(elem);
            _indexCache.put(elem, new Integer(i));
            return i;
        }
    }

    public boolean add(Object o) {
        invalidateCache();
        return super.add(o);
    }

    public void add(int index, Object element) {
        invalidateCache();
        super.add(index, element);
    }

    public Object remove(int index) {
        invalidateCache();
        return super.remove(index);
    }

    public boolean remove(Object o) {
        invalidateCache();
        return super.remove(o);
    }

    public boolean addAll(Collection c) {
        invalidateCache();
        return super.addAll(c);
    }

    public boolean addAll(int index, Collection c) {
        invalidateCache();
        return super.addAll(index, c);
    }

    public Object set(int index, Object element) {
        return super.set(index, element);
    }

    public void invalidateCache() {
        _indexCache = null;
    }
}
