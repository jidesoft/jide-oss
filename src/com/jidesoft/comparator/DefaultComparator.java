package com.jidesoft.comparator;

import java.util.Comparator;

/**
 * Badly named, this class compares objects by first converting them to <tt>String</tt>s using the
 * <tt>toString</tt> method.
 */
public class DefaultComparator implements Comparator {
    private static DefaultComparator singleton = null;

    /**
     * Constructor.
     * <p/>
     * Has protected access to prevent other clients creating instances of the class ... it is
     * stateless so we need only one instance.
     */
    protected DefaultComparator() {
    }

    /**
     * Returns <tt>ObjectComparator</tt> singleton.
     *
     * @return an instance of DefaultComparator.
     */
    public static DefaultComparator getInstance() {
        if (singleton == null)
            singleton = new DefaultComparator();
        return singleton;
    }

    /**
     * Compares two Objects <b>using the <tt>toString()</tt> method</b> as the value of each object
     * to compare.
     *
     * @param o1 the first object to be compared
     * @param o2 the second object to be compared
     *
     * @return 0 if a and b are equal, less than 0 if a < b, grater than 0 if a > b.
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

        final String s1 = o1.toString();
        final String s2 = o2.toString();
        return s1.compareTo(s2);
    }
}
