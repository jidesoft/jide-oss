package com.jidesoft.comparator;

import java.util.Comparator;

/**
 * Comparator for boolean type. This is a singleton class. Call getInstance() to
 * get the comparator.
 */
public class BooleanComparator implements Comparator {
    private static BooleanComparator singleton = null;

    /**
     * Constructor.
     * <p/>
     * Has protected access to prevent other clients creating instances of the
     * class ... it is stateless so we need only one instance.
     */
    protected BooleanComparator() {
    }

    /**
     * Returns <tt>BooleanComparator</tt> singleton.
     *
     * @return an instance of BooleanComparator.
     */
    public static BooleanComparator getInstance() {
        if (singleton == null)
            singleton = new BooleanComparator();
        return singleton;
    }

    /**
     * Compares two <tt>Boolean</tt>s. False is treated as being less than True.
     *
     * @param o1 the first object to be compared
     * @param o2 the second object to be compared
     * @return 0 if a and b are equal, -1 if a is less than b, 1 if a is more than b
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

        if (o1 instanceof Boolean) {
            if (o2 instanceof Boolean) {
                final boolean b1 = (Boolean) o1;
                final boolean b2 = (Boolean) o2;

                if (b1 == b2)
                    return 0;
                else if (b1) // Define false < true
                    return 1;
                else
                    return -1;
            }
            else {
                // o2 wasn't comparable
                throw new ClassCastException(
                        "The first argument of this method was not a Boolean but " +
                                o2.getClass().getName()
                );
            }
        }
        else if (o2 instanceof Boolean) {
            // o1 wasn't comparable
            throw new ClassCastException(
                    "The second argument of this method was not a Boolean but " +
                            o1.getClass().getName()
            );
        }
        else {
            // neither were comparable
            throw new ClassCastException(
                    "Both arguments of this method were not Booleans. They are " +
                            o1.getClass().getName() + " and " + o2.getClass().getName()
            );
        }
    }
}
