package com.jidesoft.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;


public class ActiveCachedArrayList<E> extends ArrayList<E> {
    private HashMap<Object, Integer> _indexCache;
    private boolean lazyCache = false;

    public ActiveCachedArrayList() {
        this(false);
    }

    public ActiveCachedArrayList(boolean inStatus) {
        this.lazyCache = inStatus;
        this.recache();
    }

    public ActiveCachedArrayList(Collection<? extends E> c) {
        super(c);
        this.recache();
    }

    public ActiveCachedArrayList(int initialCapacity) {
        super(initialCapacity);
        this.recache();
    }

    @Override
    public int indexOf(Object elem) {
        if (_indexCache == null) {
            _indexCache = new HashMap();
        }
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

    @Override
    public boolean add(E o) {
        boolean returnB = super.add(o);
        if (lazyCache)
            this.invalidateCache();
        else
            this.addCache(o);
        return returnB;
    }

    @Override
    public void add(int index, E element) {
        super.add(index, element);
        if (lazyCache)
            this.invalidateCache();
        else
            this.recache();
    }

    @Override
    public E remove(int index) {
        E returnE = super.remove(index);
        if (lazyCache) this.invalidateCache();
        else this.recache();
        return returnE;
    }

    @Override
    public boolean remove(Object o) {
        boolean returnB = super.remove(o);
        if (lazyCache) this.invalidateCache();
        else this.recache();
        return returnB;
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        boolean returnB = super.addAll(c);
        if (lazyCache) this.invalidateCache();
        else this.recache();
        return returnB;
    }

    @Override
    public boolean addAll(int index, Collection<? extends E> c) {
        boolean returnB = super.addAll(index, c);
        if (lazyCache) this.invalidateCache();
        else this.recache();
        return returnB;
    }

    @Override
    public E set(int index, E element) {
        E returnE = super.set(index, element);
        if (lazyCache) this.invalidateCache();
        else this.recache();
        return returnE;
    }

    @Override
    public void removeRange(int frontIndex, int toIndex) {
        super.removeRange(frontIndex, toIndex);
        if (lazyCache) this.invalidateCache();
        else this.recache();
    }

    public void invalidateCache() {
        _indexCache = null;
    }

    public void recache() {
        _indexCache = new HashMap();
        Integer i = 0;
        for (Object elem : this) {
            _indexCache.put(elem, i);
            i++;
        }
    }

    // if value was appended to the end of the cache
    // add to _indexCache if it is first occurence
    // if _indexCache already has a value, that means there is an earlier occurence
    // since ArrayList.indexOf() returns the first occurence, it should return the earlier one
    public void addCache(E o) {
        if (_indexCache.get(o) != null)
            _indexCache.put(o, _indexCache.size());
    }

    public void setLazy(boolean inStatus) {
        this.lazyCache = inStatus;
        if (inStatus == false) this.recache();
    }
}
