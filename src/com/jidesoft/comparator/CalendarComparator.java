package com.jidesoft.comparator;

import java.util.Calendar;
import java.util.Comparator;

/**
 * Comparator for Calendar type. This is a singleton class. Call getInstance() to
 * get the comparator.
 */
public class CalendarComparator implements Comparator {
    private static CalendarComparator singleton = null;

    /**
     * Constructor.
     * <p/>
     * Has protected access to prevent other clients creating instances of the
     * class ... it is stateless so we need only one instance.
     */
    protected CalendarComparator() {
    }

    /**
     * Returns <tt>CalendarComparator</tt> singleton.
     *
     * @return an instance of CalendarComparator.
     */
    public static CalendarComparator getInstance() {
        if (singleton == null)
            singleton = new CalendarComparator();
        return singleton;
    }

    /**
     * Compares two <tt>Calendar</tt>s.
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

        if (o1 instanceof Calendar) {
            if (o2 instanceof Calendar) {
                Calendar l = (Calendar) o1;
                Calendar r = (Calendar) o2;

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
                        "The first argument of this method was not a Calendar but " +
                                o2.getClass().getName()
                );
            }
        }
        else if (o2 instanceof Calendar) {
// o1 wasn't comparable
            throw new ClassCastException(
                    "The second argument of this method was not a Calendar but " +
                            o1.getClass().getName()
            );
        }
        else {
// neither were comparable
            throw new ClassCastException(
                    "Both arguments of this method were not Calendars. They are " +
                            o1.getClass().getName() + " and " + o2.getClass().getName()
            );
        }
    }
}
