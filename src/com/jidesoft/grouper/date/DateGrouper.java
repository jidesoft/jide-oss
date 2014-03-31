package com.jidesoft.grouper.date;

import com.jidesoft.grouper.AbstractObjectGrouper;

import java.util.Calendar;
import java.util.Date;

/**
 * An abstract Grouper which can take data type such as Date, Calendar or Long and provide {@link
 * #getCalendarFieldAsInt(Object, int)} and {@link #getCalendarField(Object, int)} methods to access the field of the
 * Calendar.
 */
abstract public class DateGrouper extends AbstractObjectGrouper {
    protected static Calendar INSTANCE = Calendar.getInstance();

    /**
     * To avoid creating too many instance of Calendar and improve the performance, {@link #getCalendarField(Object,
     * int)} and {@link #getCalendarFieldAsInt(Object, int)} will use a cached instance of Calendar if the value passed
     * in is Date or Long. By default, the cached Calendar instance was created from Calendar.getInstance(). This method
     * will give you this instance and allow you to modify it. For example, setting a different time-zone. Since this
     * instance is static, there is only one instance for the whole application. So just so you know, if you modify it,
     * it will affect all the usages.
     *
     * @return the Calendar instance used by the two getCalendarField methods.
     */
    public static Calendar getCalendarInstance() {
        return INSTANCE;
    }

    /**
     * Gets the field value from the value.
     *
     * @param value a Date, Long or Calendar. If the value is a Date or a Long, we will use a cached Calendar instance
     *              to get the field value. This cached Calendar instance can be retrieved using {@link
     *              #getCalendarInstance()} in case you want to customize it.
     * @param field the field as defined in Calendar such as Calender.YEAR, Calendar.DAY_OF_MONTH.
     * @return the field value. Null if the value that was passed in is null.
     * @throws java.lang.IllegalArgumentException if the value is not a Date, a Long or a Calendar.
     */
    public synchronized static Object getCalendarField(Object value, int field) {
        if (value instanceof Date) {
            INSTANCE.setTime(((Date) value));
            return INSTANCE.get(field);
        }
        else if (value instanceof Long) {
            INSTANCE.setTime(new Date((Long) value));
            return INSTANCE.get(field);
        }
        else if (value instanceof Calendar) {
            return ((Calendar) value).get(field);
        }
        else if (value == null) {
            return null;
        }
        else {
            throw new IllegalArgumentException("Type incompatible");
        }
    }

    /**
     * Gets the field value from the value.
     *
     * @param value a Date, Long or Calendar. If the value is a Date or a Long, we will use a cached Calendar instance
     *              to get the field value. This cached Calendar instance can be retrieved using {@link
     *              #getCalendarInstance()} in case you want to customize it.
     * @param field the field as defined in Calendar such as Calender.YEAR, Calendar.DAY_OF_MONTH.
     * @return the field value. -1 if the value that was passed in is null.
     * @throws java.lang.IllegalArgumentException if the value is not a Date, a Long or a Calendar.
     */
    public synchronized static int getCalendarFieldAsInt(Object value, int field) {
        if (value instanceof Date) {
            INSTANCE.setTime(((Date) value));
            return INSTANCE.get(field);
        }
        else if (value instanceof Long) {
            INSTANCE.setTime(new Date((Long) value));
            return INSTANCE.get(field);
        }
        else if (value instanceof Calendar) {
            return ((Calendar) value).get(field);
        }
        else if (value == null) {
            return -1;
        }
        else {
            throw new IllegalArgumentException("Type incompatible");
        }
    }

    public Class<?> getType() {
        return int.class;
    }
}
