/*
 * @(#)CachedArrayList.java 10/5/2005
 *
 * Copyright 2002 - 2005 JIDE Software Inc. All rights reserved.
 */
package com.jidesoft.utils;

import java.util.*;

/**
 * This is a fast access ArrayList that sacrifices memory for speed. It will reduce the speed of indexOf method from
 * O(n) to O(1). However it will at least double the memory used by ArrayList. So use it appropriately. <p><strong>Just
 * like ArrayList, this implementation is not synchronized.</strong> If you want a thread safe implementation, you can
 * use {@link com.jidesoft.utils.CachedVector}.
 */
public class CachedArrayList<E> extends ArrayList<E> {
    private Map<Object, Integer> _indexCache;
    private boolean _lazyCaching = false;

    public CachedArrayList() {
    }

    public CachedArrayList(Collection<? extends E> c) {
        super(c);
        if (!isLazyCaching()) {
            cacheAll();
        }
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
        else if (isLazyCaching()) {
            int i = super.indexOf(elem);
            if (i == -1) {
                uncacheIt(elem);
            }
            else {
                cacheIt(elem, i);
            }
            return i;
        }
        else {
            return -1;
        }
    }

    /**
     * Adjusts the cache so that all values that are greater than index will increase by the value specified by the
     * increase parameter.
     *
     * @param index    the index. All values above this index will be changed.
     * @param increase a positive number to increase or a negative number to decrease.
     */
    protected synchronized void adjustCache(int index, int increase) {
        if (_indexCache != null) {
            Map<Object, Integer> newCache = createCache();
            Set<Object> keys = _indexCache.keySet();
            for (Object key : keys) {
                int value = _indexCache.get(key);
                if (value >= index) {
                    newCache.put(key, value + increase);
                }
                else {
                    newCache.put(key, value);
                }
            }
            _indexCache = newCache;
        }
    }

    protected Map<Object, Integer> createCache() {
        return new IdentityHashMap<Object, Integer>();
    }


    /**
     * Caches the index of the element.
     *
     * @param o     the element
     * @param index the index.
     */
    public void cacheIt(Object o, int index) {
        if (_indexCache != null && (_indexCache.get(o) == null || index < _indexCache.get(o))) {
            _indexCache.put(o, index);
        }
    }

    /**
     * Uncaches the index of the element.
     *
     * @param o the element
     */
    public void uncacheIt(Object o) {
        if (_indexCache != null) {
            _indexCache.remove(o);
        }
    }

    @Override
    public boolean add(E o) {
        boolean added = super.add(o);
        if (!isLazyCaching() && added) {
            initializeCache();
            cacheIt(o, size() - 1);
        }
        return added;
    }

    @Override
    public void add(int index, E element) {
        super.add(index, element);
        if (!isLazyCaching()) {
            initializeCache();
            adjustCache(index, 1);
            cacheIt(element, index);
        }
        else if (_indexCache != null) {
            adjustCache(index, 1);
            cacheIt(element, index);
        }
    }

    private void initializeCache() {
        if (_indexCache == null) {
            _indexCache = createCache();
        }
    }

    @Override
    public E remove(int index) {
        E element = super.remove(index);
        if (element != null) {
            uncacheIt(element);
            adjustCache(index, -1);
        }
        return element;
    }

    @Override
    public boolean remove(Object o) {
        int oldIndex = indexOf(o);
        boolean removed = super.remove(o);
        if (removed) {
            uncacheIt(o);
            adjustCache(oldIndex, -1);
        }
        return removed;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        uncacheAll();
        return super.removeAll(c);
    }


    @Override
    public void clear() {
        uncacheAll();
        super.clear();
    }


    @Override
    public boolean addAll(Collection<? extends E> c) {
        boolean added = super.addAll(c);
        if (added) {
            cacheAll();
        }
        return added;
    }

    @Override
    public boolean addAll(int index, Collection<? extends E> c) {
        boolean added = super.addAll(index, c);
        initializeCache();
        adjustCache(index, c.size());
        for (E e : c) {
            cacheIt(e, index++);
        }
        return added;
    }

    @Override
    public E set(int index, E element) {
        if (!isLazyCaching()) {
            initializeCache();
            E e = super.set(index, element);
            uncacheIt(e);
            cacheIt(element, index);
            return e;
        }
        else {
            return super.set(index, element);
        }
    }

    /**
     * Invalidated the whole cache.
     */
    public void invalidateCache() {
        uncacheAll();
    }

    /**
     * Uncache the whole cache. It is the same as {@link #invalidateCache()}.
     */
    public void uncacheAll() {
        if (_indexCache != null) {
            _indexCache.clear();
            _indexCache = null;
        }
    }

    /**
     * Cache all the element index.
     */
    public void cacheAll() {
        _indexCache = createCache();
        Integer i = 0;
        for (Object elem : this) {
            if (_indexCache.get(elem) == null) {
                _indexCache.put(elem, i);
            }
            i++;
        }
//        for (int i = 0; i < size(); i++) {
//            _indexCache.put(get(i), i);
//        }
//        for (int i = size() - 1; i >= 0; i--) {
//            _indexCache.put(get(i), i);
//        }
    }

    public boolean isLazyCaching() {
        return _lazyCaching;
    }

    public void setLazyCaching(boolean lazyCaching) {
        _lazyCaching = lazyCaching;
    }

    @Override
    protected void removeRange(int fromIndex, int toIndex) {
        if (fromIndex == toIndex) {
            remove(fromIndex);
        }
        else {
            super.removeRange(fromIndex, toIndex);
            uncacheAll();
            if (!isLazyCaching()) {
                cacheAll();
            }
        }
    }
}
