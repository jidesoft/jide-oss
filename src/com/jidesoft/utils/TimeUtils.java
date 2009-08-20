/*
 * @(#)TimeUtils.java 8/19/2009
 *
 * Copyright 2002 - 2009 JIDE Software Inc. All rights reserved.
 */
package com.jidesoft.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import static java.util.Calendar.*;
import java.util.Date;
import java.util.logging.Logger;

public class TimeUtils {
    private static final Logger logger = Logger.getLogger(TimeUtils.class.getName());

    /**
     * Makes it easier to create Date objects from strings without having to remember the symbols required for a
     * SimpleDateFormat. <p>The format used is "dd-MMM-yyyy HH:mm:ss", so an acceptable date is for example 05-Dec-1999
     * 20:04:15</p>
     *
     * @param timeString
     * @return a <code>Date</code> object corresponding to the date/time in the supplied string.
     *
     * @throws ParseException
     */
    public static Date createTime(String timeString) throws ParseException {
        DateFormat format = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");
        return format.parse(timeString);
    }

    /**
     * @param c1
     * @param c2
     * @return the int field from the Calendar class at which the dates differ. This will be one of YEAR, MONTH,
     *         WEEK_OF_YEAR, DAY_OF_YEAR, HOUR_OF_DAY, MINUTE, SECOND, MILLISECOND, or null if there are no differences.
     */
    public static Integer mostSignificantDifference(Calendar c1, Calendar c2) {
        if (!isSameYear(c1, c2)) {
            logger.info("YEAR");
            return YEAR;
        }
        else if (!isSameMonth(c1, c2)) {
            logger.info("Month");
            return MONTH;
        }
        else if (!isSameWeek(c1, c2)) {
            logger.info("WEEK_OF_YEAR");
            return WEEK_OF_YEAR;
        }
        else if (!isSameDay(c1, c2)) {
            logger.info("DAY_OF_MONTH");
            return DAY_OF_MONTH;
        }
        else if (!isSameHour(c1, c2)) {
            logger.info("HOUR_OF_DAY");
            return HOUR_OF_DAY;
        }
        else if (!isSameMinute(c1, c2)) {
            logger.info("MINUTE");
            return MINUTE;
        }
        else if (!isSameSecond(c1, c2)) {
            logger.info("SECOND");
            return SECOND;
        }
        else if (!isSameMillis(c1, c2)) {
            logger.info("MILLISECOND");
            return MILLISECOND;
        }
        else {
            return null;
        }
    }

    public static boolean isSameYear(Calendar c1, Calendar c2) {
        return isSameInField(YEAR, c1, c2);
    }

    public static boolean isSameMonth(Calendar c1, Calendar c2) {
        return isSameInField(MONTH, c1, c2);
    }

    public static boolean isSameWeek(Calendar c1, Calendar c2) {
        return isSameInField(WEEK_OF_YEAR, c1, c2);
    }

    public static boolean isSameDay(Calendar c1, Calendar c2) {
        return isSameInField(DAY_OF_MONTH, c1, c2);
    }

    public static boolean isSameHour(Calendar c1, Calendar c2) {
        return isSameInField(HOUR_OF_DAY, c1, c2);
    }

    public static boolean isSameMinute(Calendar c1, Calendar c2) {
        return isSameInField(MINUTE, c1, c2);
    }

    public static boolean isSameSecond(Calendar c1, Calendar c2) {
        return isSameInField(SECOND, c1, c2);
    }

    public static boolean isSameMillis(Calendar c1, Calendar c2) {
        return isSameInField(MILLISECOND, c1, c2);
    }

    public static boolean isSameInField(int field, Calendar c1, Calendar c2) {
        int field1 = c1.get(field);
        int field2 = c2.get(field);
        return field1 == field2;
    }

    public static double yearsDiff(Calendar c1, Calendar c2) {
        return weeksDiff(c1, c2) / 52.0;
    }

    public static double weeksDiff(Calendar c1, Calendar c2) {
        return daysDiff(c1, c2) / 7.0;
    }

    public static double daysDiff(Calendar c1, Calendar c2) {
        return hoursDiff(c1, c2) / 24.0;
    }

    public static double hoursDiff(Calendar c1, Calendar c2) {
        return minutesDiff(c1, c2) / 60.0;
    }

    public static double minutesDiff(Calendar c1, Calendar c2) {
        return secondsDiff(c1, c2) / 60.0;
    }

    public static double secondsDiff(Calendar c1, Calendar c2) {
        return millisDiff(c1, c2) / 1000.0;
    }

    public static long millisDiff(Calendar c1, Calendar c2) {
        long time1 = c1.getTimeInMillis();
        long time2 = c2.getTimeInMillis();
        return Math.abs(time1 - time2);
    }

    // Return the minimum of the range, rounded down
    public static Calendar min(Calendar c1, Calendar c2) {
        Calendar result = Calendar.getInstance();
        result.setTimeInMillis(c2.getTimeInMillis());
        if (yearsDiff(c1, c2) > 1.0) {
            result.set(WEEK_OF_YEAR, 0);
            result.set(MONTH, 0);
            result.set(DAY_OF_MONTH, 0);
            result.set(HOUR_OF_DAY, 0);
            result.set(MINUTE, 0);
            result.set(SECOND, 0);
            result.set(MILLISECOND, 0);
        }
        else if (weeksDiff(c1, c2) > 1.0) {
            result.set(HOUR_OF_DAY, 0);
            result.set(MINUTE, 0);
            result.set(SECOND, 0);
            result.set(MILLISECOND, 0);
        }
        else if (daysDiff(c1, c2) > 1.0) {
            result.set(HOUR_OF_DAY, 0);
            result.set(MINUTE, 0);
            result.set(SECOND, 0);
            result.set(MILLISECOND, 0);
            return result;
        }
        else if (hoursDiff(c1, c2) > 1.0) {
            result.set(MINUTE, 0);
            result.set(SECOND, 0);
            result.set(MILLISECOND, 0);
        }
        else if (minutesDiff(c1, c2) > 1.0) {
            result.set(SECOND, 0);
            result.set(MILLISECOND, 0);
        }
        else if (secondsDiff(c1, c2) > 1.0) {
            result.set(MILLISECOND, 0);
            result.add(SECOND, 1);
        }
        return result;
    }

    // Return the maximum of the range, rounded up
    public static Calendar max(Calendar c1, Calendar c2) {
        Calendar result = Calendar.getInstance();
        result.setTimeInMillis(c2.getTimeInMillis());
        if (yearsDiff(c1, c2) > 1.0) {
            result.set(WEEK_OF_YEAR, 0);
            result.set(MONTH, 0);
            result.set(DAY_OF_MONTH, 0);
            result.set(HOUR_OF_DAY, 0);
            result.set(MINUTE, 0);
            result.set(SECOND, 0);
            result.set(MILLISECOND, 0);
            result.add(YEAR, 1);
        }
        else if (weeksDiff(c1, c2) > 1.0) {
            result.set(HOUR_OF_DAY, 0);
            result.set(MINUTE, 0);
            result.set(SECOND, 0);
            result.set(MILLISECOND, 0);
            result.add(DAY_OF_MONTH, 1);
        }
        else if (daysDiff(c1, c2) > 1.0) {
            result.set(HOUR_OF_DAY, 0);
            result.set(MINUTE, 0);
            result.set(SECOND, 0);
            result.set(MILLISECOND, 0);
            result.add(DAY_OF_MONTH, 1);
        }
        else if (hoursDiff(c1, c2) > 1.0) {
            result.set(MINUTE, 0);
            result.set(SECOND, 0);
            result.set(MILLISECOND, 0);
            result.add(HOUR_OF_DAY, 1);
        }
        else if (minutesDiff(c1, c2) > 1.0) {
            result.set(SECOND, 0);
            result.set(MILLISECOND, 0);
            result.add(MINUTE, 1);
        }
        else if (secondsDiff(c1, c2) > 1.0) {
            result.set(MILLISECOND, 0);
            result.add(SECOND, 1);
        }
        return result;
	}
}
