/*
 * @(#)DateComparator.java 7/15/2008
 *
 * Copyright 2002 - 2008 JIDE Software Inc. All rights reserved.
 */

package com.jidesoft.comparator;

import java.util.Comparator;
import java.util.Date;

/**
 * Comparator for Date type. This is a singleton class. Call getInstance() to get the comparator.
 */
public class DateComparator implements Comparator {
    private static DateComparator singleton = null;

    /**
     * Constructor.
     * <p/>
     * Has protected access to prevent other clients creating instances of the class ... it is stateless so we need only
     * one instance.
     */
    protected DateComparator() {
    }

    /**
     * Returns <tt>DateComparator</tt> singleton.
     *
     * @return an instance of DateComparator.
     */
    public static DateComparator getInstance() {
        if (singleton == null)
            singleton = new DateComparator();
        return singleton;
    }

    /**
     * Compares two <tt>Date</tt>s.
     *
     * @param o1 the first object to be compared
     * @param o2 the second object to be compared
     * @return 0 if a and b are equal, -1 if a is before b, 1 if a is after b.
     */
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

        if (o1 instanceof Date) {
            if (o2 instanceof Date) {
                Date l = (Date) o1;
                Date r = (Date) o2;

                if (l.before(r))
                    return -1;
                else if (l.equals(r))
                    return 0;
                else
                    return 1;
            }
            else {
// o2 wasn't comparable
                throw new ClassCastException(
                        "The first argument of this method was not a Date but " +
                                o2.getClass().getName()
                );
            }
        }
        else if (o2 instanceof Date) {
// o1 wasn't comparable
            throw new ClassCastException(
                    "The second argument of this method was not a Date but " +
                            o1.getClass().getName()
            );
        }
        else {
// neither were comparable
            throw new ClassCastException(
                    "Both arguments of this method were not Dates. They are " +
                            o1.getClass().getName() + " and " + o2.getClass().getName()
            );
        }
    }
}