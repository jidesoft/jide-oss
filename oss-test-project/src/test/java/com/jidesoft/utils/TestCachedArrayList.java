package com.jidesoft.utils;

import junit.framework.TestCase;

import java.util.ArrayList;

public class TestCachedArrayList extends TestCase {
    CachedArrayList cachedList;
    ActiveCachedArrayList activeCachedList;
    ArrayList list;
    public static final int SIZE = 1000;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        long before, after;

        list = new ArrayList();
        before = System.nanoTime();
        for (int i = 0; i < SIZE; i++) {
            list.add(0, String.valueOf(i));
        }
        after = System.nanoTime();
        System.out.println("Creating ArrayList of length " + SIZE
                + " took " + String.valueOf((after - before) / SIZE) + "ms");

        cachedList = new CachedArrayList();
//        cachedList.setLazyCaching(true);
        before = System.nanoTime();
        for (int i = 0; i < SIZE; i++) {
            cachedList.add(0, String.valueOf(i));
        }
        after = System.nanoTime();
        System.out.println("Creating CachedArrayList of length " + SIZE
                + " took " + String.valueOf((after - before) / SIZE) + "ms");

        activeCachedList = new ActiveCachedArrayList();
//        activeCachedList.setLazy(true);
        before = System.nanoTime();
        for (int i = 0; i < SIZE; i++) {
            activeCachedList.add(0, String.valueOf(i));
        }
        after = System.nanoTime();
        System.out.println("Creating ActiveCachedArrayList of length " + SIZE
                + " took " + String.valueOf((after - before) / SIZE) + "ms");

        System.out.println();
    }

    public void testIndexOf() {
        assertEquals(cachedList.indexOf(cachedList.get(SIZE - 1)), list.indexOf(list.get(SIZE - 1)));
        assertEquals(cachedList.indexOf(cachedList.get(1)), list.indexOf(list.get(1)));
        cachedList.invalidateCache();
    }

    public void testIndexOfPerformance() {
        long before, after;

        //

        before = System.nanoTime();
        for (int i = 0; i < SIZE; i++) {
            list.indexOf("" + i);
        }
        after = System.nanoTime();
        System.out.println("1st Normal: indexof of length " + SIZE
                + " took " + String.valueOf((after - before) / 1000000) + "ms");

        before = System.nanoTime();
//        cachedList.cacheAll();
        for (int i = 0; i < SIZE; i++) {
            cachedList.indexOf("" + i);
        }
        after = System.nanoTime();
        System.out.println("1st Cached: indexof of length " + SIZE
                + " took " + String.valueOf((after - before) / 1000000) + "ms");

        before = System.nanoTime();
//        activeCachedList.recache();
        for (int i = 0; i < SIZE; i++) {
            activeCachedList.indexOf("" + i);
        }
        after = System.nanoTime();
        System.out.println("1st Active: indexof of length " + SIZE
                + " took " + String.valueOf((after - before) / 1000000) + "ms");

        //
        System.out.println();


        before = System.nanoTime();
        for (int i = 0; i < SIZE; i++) {
            list.indexOf("" + i);
        }
        after = System.nanoTime();
        System.out.println("2nd Normal: indexOf ArrayList of length " + SIZE
                + " took " + String.valueOf((after - before) / 1000000) + "ms");

        before = System.nanoTime();
        for (int i = 0; i < SIZE; i++) {
            cachedList.indexOf("" + i);
        }
        after = System.nanoTime();
        System.out.println("2nd Cached: indexOf ArrayList of length " + SIZE
                + " took " + String.valueOf((after - before) / 1000000) + "ms");
        cachedList.invalidateCache();

        before = System.nanoTime();
        for (int i = 0; i < SIZE; i++) {
            activeCachedList.indexOf("" + i);
        }
        after = System.nanoTime();
        System.out.println("2nd Active: indexOf ArrayList of length " + SIZE
                + " took " + String.valueOf((after - before) / 1000000) + "ms");

        System.out.println();
    }

    public void testAddRemove() {
        CachedArrayList<String> list = new CachedArrayList();

        list.setLazyCaching(false);
        list.add("1");
        list.add("2");
        list.add("3");
        assertEquals(3, list.size());
        list.add("3");
        assertEquals(4, list.size());
        assertEquals(2, list.indexOf("3"));

        list.add(1, "3");
        assertEquals(5, list.size());
        assertEquals(1, list.indexOf("3"));

        list.clear();
        assertEquals(0, list.size());

        list.setLazyCaching(true);
        list.add("1");
        list.add("2");
        list.add("3");
        assertEquals(3, list.size());
        list.add("3");
        assertEquals(4, list.size());
        assertEquals(2, list.indexOf("3"));

        list.add(1, "3");
        assertEquals(5, list.size());
        assertEquals(1, list.indexOf("3"));

        list.clear();
        assertEquals(0, list.size());
    }
}
