/*
 * @(#)FastComparableComparator.java 2/27/2007
 *
 * Copyright 2002 - 2007 JIDE Software Inc. All rights reserved.
 */

package com.jidesoft.comparator;

import java.io.Serializable;
import java.util.Comparator;

/**
 * A Comparator that compares Comparable objects. Throws ClassCastExceptions if the objects are not Comparable, or if
 * they are null. Different from {@link ComparableComparator}, it will not throw exception when the two compareTo
 * methods don't provide an inverse result of each other as per the Comparable javadoc. We do so mainly to reduce one
 * call to compareTo. <br> If both objects are null, they will be treated as equal. If one is null and the other is not,
 * the null value will be treated as smaller then non-null value.
 *
 * @author bayard@generationjava.com
 * @author JIDE Software
 */
public class FastComparableComparator implements Comparator, Serializable {

    private static final FastComparableComparator instance =
            new FastComparableComparator();

    /**
     * Return a shared instance of a ComparableComparator.  Developers are encouraged to use the comparator returned
     * from this method instead of constructing a new instance to reduce allocation and GC overhead when multiple
     * comparable comparators may be used in the same VM.
     *
     * @return an instance of ComparableComparator.
     */
    public static FastComparableComparator getInstance() {
        return instance;
    }

    /**
     * Constructs a FastComparableComparator.
     */
    public FastComparableComparator() {
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

        if (o1 instanceof Comparable) {
            if (o2 instanceof Comparable) {
                return ((Comparable) o1).compareTo(o2);
            }
            else {
                // o2 wasn't comparable
                throw new ClassCastException("The second argument of this method was not a Comparable: " + o2.getClass().getName());
            }
        }
        else if (o2 instanceof Comparable) {
            // o1 wasn't comparable
            throw new ClassCastException("The first argument of this method was not a Comparable: " + o1.getClass().getName());
        }
        else {
            // neither were comparable
            throw new ClassCastException("Both arguments of this method were not Comparables: " + o1.getClass().getName() + " and " + o2.getClass().getName());
        }
    }
}
