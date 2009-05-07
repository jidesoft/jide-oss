/*
 * @(#)CharSequenceComparator.java 7/15/2008
 *
 * Copyright 2002 - 2008 JIDE Software Inc. All rights reserved.
 *
 */

package com.jidesoft.comparator;

import java.io.Serializable;
import java.util.Comparator;

/**
 * A Comparator that compares CharSequence objects (including String and StringBuffer as both extend CharSequence.
 * Throws ClassCastExceptions if the objects are not CharSequence, or if they are null. If both objects are null, they
 * will be treated as equal. If one is null and the other is not, the null value will be treated as smaller then
 * non-null value.
 */
public class CharSequenceComparator implements Comparator, Serializable {
    private boolean _caseSensitive;
    public static final ComparatorContext CONTEXT = new ComparatorContext("IgnoreLocale");
    public static final ComparatorContext CONTEXT_IGNORE_CASE = new ComparatorContext("IgnoreLocale_Ignorecase");

    /**
     * Constructs a CharSequenceComparator.
     */
    public CharSequenceComparator() {
        this(true);
    }

    public CharSequenceComparator(boolean caseSensitive) {
        _caseSensitive = caseSensitive;
    }

    /**
     * Checks if the case is sensitive when comparing.
     *
     * @return true if the comparator is case sensitive.
     */
    public boolean isCaseSensitive() {
        return _caseSensitive;
    }

    /**
     * Sets the case sensitive flag. By default, it's true meaning the comparator is case sensitive.
     *
     * @param caseSensitive true or false.
     */
    public void setCaseSensitive(boolean caseSensitive) {
        _caseSensitive = caseSensitive;
    }

    public int compare(Object o1, Object o2) {
        if (o1 == null && o2 == null) {
            return 0;
        }
        else if (o1 == null) {
            return -1;
        }
        else if (o2 == null) {
            return 1;
        }

        if (o1 instanceof CharSequence) {
            if (o2 instanceof CharSequence) {
                CharSequence s1 = (CharSequence) o1;
                CharSequence s2 = (CharSequence) o2;
                return isCaseSensitive() ? compareCase(s1, s2) : compareIgnoreCase(s1, s2);
            }
            else {
                // o2 wasn't comparable
                throw new ClassCastException("The second argument of this method was not a CharSequence: " + o2.getClass().getName());
            }
        }
        else if (o2 instanceof Comparable) {
            // o1 wasn't comparable
            throw new ClassCastException("The first argument of this method was not a CharSequence: " + o1.getClass().getName());
        }
        else {
            // neither were comparable
            throw new ClassCastException("Both arguments of this method were not CharSequences: " + o1.getClass().getName() + " and " + o2.getClass().getName());
        }
    }

    private int compareCase(CharSequence s1, CharSequence s2) {
        int len1 = s1.length();
        int len2 = s2.length();
        int n = Math.min(len1, len2);

        int k = 0;
        while (k < n) {
            char c1 = s1.charAt(k);
            char c2 = s2.charAt(k);
            if (c1 != c2) {
                return c1 - c2;
            }
            k++;
        }
        return len1 - len2;
    }

    private int compareIgnoreCase(CharSequence s1, CharSequence s2) {
        int n1 = s1.length(), n2 = s2.length();
        for (int i1 = 0, i2 = 0; i1 < n1 && i2 < n2; i1++, i2++) {
            char c1 = s1.charAt(i1);
            char c2 = s2.charAt(i2);
            if (c1 != c2) {
                c1 = Character.toUpperCase(c1);
                c2 = Character.toUpperCase(c2);
                if (c1 != c2) {
                    c1 = Character.toLowerCase(c1);
                    c2 = Character.toLowerCase(c2);
                    if (c1 != c2) {
                        return c1 - c2;
                    }
                }
            }
        }
        return n1 - n2;
    }

//    /**
//     * * sort a set of string from stdin
//     */
//    public static void main(String[] args) {
//        try {
//            Vector<String> v = new Vector<String>();
//            for (int i = 0; i < 1; i++) {
//                v.add("a1");
//                v.add("A2");
//                v.add("a2");
//                v.add("a10");
//                v.add("a1c1");
//                v.add("a1c2");
//                v.add("a1c10");
//                v.add("a1c20");
//                v.add("a100");
//                v.add("b3");
//                v.add("c3");
//                v.add("cc3");
//                v.add("cc2");
//                v.add("1.doc");
//                v.add("2.doc");
//                v.add("3.doc");
//                v.add("4.doc");
//                v.add("5.doc");
//                v.add("6.doc");
//                v.add("7.doc");
//                v.add("8.doc");
//                v.add("9.doc");
//                v.add("10.doc");
//                v.add("11.doc");
//                v.add("12.doc");
//                v.add("13.doc");
//                v.add("14.doc");
//                v.add("15.doc");
//                v.add("16.doc");
//                v.add("17.doc");
//                v.add("18.doc");
//                v.add("19.doc");
//                v.add("20.doc");
//                v.add("100.doc");
//                v.add("101.doc");
//                v.add("102.doc");
//            }
//            long start = System.nanoTime();
//            Collections.sort(v, new CharSequenceComparator());
//            long end = System.nanoTime();
//            System.out.println(end - start);
//            for (String s : v) System.out.println(s);
//        }
//        catch (Throwable error) {
//            error.printStackTrace();
//            System.exit(-1);
//        }
//    }

}