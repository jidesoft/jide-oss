/*
 * @(#)CachedArrayList.java 10/5/2005
 *
 * Copyright 2002 - 2005 JIDE Software Inc. All rights reserved.
 */
package com.jidesoft.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.Map;

/**
 * This is a fast access ArrayList that sacrifices memory for speed. It will reduce the speed of indexOf method from
 * O(n) to O(1). However it will at least double the memory used by ArrayList. So use it appropriately. <p><strong>Just
 * like ArrayList, this implementation is not synchronized.</strong> If you want a thread safe implementation, you can
 * use {@link com.jidesoft.utils.CachedVector}.
 */
public class CachedArrayList<E> extends ArrayList<E> {
    private static final long serialVersionUID = 3835017332487313880L;
    private Map<Object, IntegerWrapper> _indexCache;
    private boolean _lazyCaching = false;
    private boolean _isDirty = false;

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
        if (_indexCache == null || _isDirty) {
            cacheAll();
        }
        IntegerWrapper o = _indexCache.get(elem);
        if (o != null) {
            return o.integer;
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
     * @deprecated no longer being invoked since 3.4.0
     */
    @Deprecated
    protected synchronized void adjustCache(int index, int increase) {
        if (_indexCache != null) {
            Collection<IntegerWrapper> values = _indexCache.values();
            for (IntegerWrapper value : values) {
                if (value.integer >= index) {
                    value.integer += increase;
                }
            }
        }
    }

    protected Map<Object, IntegerWrapper> createCache() {
        return new IdentityHashMap<Object, IntegerWrapper>();
    }


    /**
     * Caches the index of the element.
     *
     * @param o     the element
     * @param index the index.
     */
    public void cacheIt(Object o, int index) {
        if (_indexCache != null) {
            IntegerWrapper old = _indexCache.put(o, new IntegerWrapper(index));
            if (old != null && old.integer < index) {
                _indexCache.put(o, old);
            }

            markDirtyIfNecessary(index);
            if (!_isDirty && !isLazyCaching()) {
                for (int i = size() - 1; i > index; i--) {
                    IntegerWrapper oldI = _indexCache.put(get(i), new IntegerWrapper(i));
                    if (oldI != null && oldI.integer < index) {
                        _indexCache.put(get(i), oldI);
                    }
                }
            }
        }
    }

    /**
     * Marks the entire cache dirty if necessary depends on where the index is.
     * <p/>
     * By default, the cache is marked dirty when the index is smaller than half of the size.
     *
     * @param index the index that is changing.
     * @since 3.4.0
     */
    protected void markDirtyIfNecessary(int index) {
        if (index < size() / 2) {
            _isDirty = true;
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
        // In any cases, can't invoke add(int, E). Otherwise, StackOverflowError might be thrown.
        boolean added = super.add(o);
        if (!isLazyCaching() && _indexCache != null && added) {
            cacheIt(o, size() - 1);
        }
        return added;
    }

    @Override
    public void add(int index, E element) {
        // improve performance for most scenarios in HeaderTableModel
        if (index == size()) {
            add(element); // need make sure add(E) won't invoke this method. Otherwise, it will cause endless loop.
            return;
        }
        super.add(index, element);
        if (!isLazyCaching()) {
            cacheIt(element, index);
        }
        else if (_indexCache != null) {
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
            uncacheAll();
        }
        return element;
    }

    @Override
    public boolean remove(Object o) {
        boolean removed = super.remove(o);
        if (removed) {
            uncacheAll();
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
        // In any cases, can't invoke addAll(int, Collection<? extends E>). Otherwise, StackOverflowError might be thrown.
        int index = size();
        boolean added = super.addAll(c);
        if (added && _indexCache != null) {
            for (E e : c) {
                cacheIt(e, index++);
            }
        }
        return added;
    }

    @Override
    public boolean addAll(int index, Collection<? extends E> c) {
        // improve performance for most scenarios in HeaderTableModel
        if (index == size()) {
            return this.addAll(c); // need make sure addAll(Collection<? extends E>) won't invoke this method. Otherwise, it will cause endless loop.
        }
        boolean added = super.addAll(index, c);
        if (added) {
            uncacheAll();
        }
        return added;
    }

    @Override
    public E set(int index, E element) {
        if (!isLazyCaching()) {
            uncacheAll();
            return super.set(index, element);
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
//        Integer i = 0;
//        for (Object elem : this) {
//            if (_indexCache.get(elem) == null) {
//                _indexCache.put(elem, i);
//            }
//            i++;
//        }
//        for (int i = 0; i < size(); i++) {
//            _indexCache.put(get(i), i);
//        }
        for (int i = size() - 1; i >= 0; i--) {
            _indexCache.put(get(i), new IntegerWrapper(i));
        }
        _isDirty = false;
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
        }
    }

    public static class IntegerWrapper {
        int integer;

        private IntegerWrapper(int integer) {
            this.integer = integer;
        }
    }
}
