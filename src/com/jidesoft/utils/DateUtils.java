/*
 * @(#)DateUtils.java 8/22/2008
 *
 * Copyright 2002 - 2008 JIDE Software Inc. All rights reserved.
 */

package com.jidesoft.utils;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * <code>DateUtils</code> contains many useful methods related to Date and Calendar.
 */
public class DateUtils {

    private static final long DAY_IN_MS = 24 * 60 * 60 * 1000;

    /**
     * Checks if the calendar object is same date as today.
     *
     * @param cal the calendar object
     * @return true if the calendar object is the same date as today.
     */
    public static boolean isToday(Calendar cal) {
        Calendar today = Calendar.getInstance();
        return today.get(Calendar.YEAR) == cal.get(Calendar.YEAR) && today.get(Calendar.DAY_OF_YEAR) == cal.get(Calendar.DAY_OF_YEAR);
    }

    /**
     * Checks if the calendar object is same week as today.
     *
     * @param cal the calendar object
     * @return true if the calendar object is the same week as today.
     */
    public static boolean isThisWeek(Calendar cal) {
        Calendar today = Calendar.getInstance();
        return today.get(Calendar.YEAR) == cal.get(Calendar.YEAR) && today.get(Calendar.WEEK_OF_YEAR) == cal.get(Calendar.WEEK_OF_YEAR);
    }

    /**
     * Checks if the calendar object is same month as today.
     *
     * @param cal the calendar object
     * @return true if the calendar object is the same month as today.
     */
    public static boolean isThisMonth(Calendar cal) {
        Calendar today = Calendar.getInstance();
        return today.get(Calendar.YEAR) == cal.get(Calendar.YEAR) && today.get(Calendar.MONTH) == cal.get(Calendar.MONTH);
    }

    /**
     * Checks if the calendar object is same quarter as today.
     *
     * @param cal the calendar object
     * @return true if the calendar object is the same quarter as today.
     */
    public static boolean isThisQuarter(Calendar cal) {
        Calendar today = Calendar.getInstance();
        return today.get(Calendar.YEAR) == cal.get(Calendar.YEAR) && today.get(Calendar.MONTH) / 3 == cal.get(Calendar.MONTH) / 3;
    }

    /**
     * Checks if the calendar object is same year as today.
     *
     * @param cal the calendar object
     * @return true if the calendar object is the same year as today.
     */
    public static boolean isThisYear(Calendar cal) {
        Calendar today = Calendar.getInstance();
        return today.get(Calendar.YEAR) == cal.get(Calendar.YEAR);

    }

    /**
     * Checks if the calendar object is same date as yesterday.
     *
     * @param cal the calendar object
     * @return true if the calendar object is the same date as yesterday.
     */
    public static boolean isYesterday(Calendar cal) {
        Calendar yesterday = adjustDate(Calendar.getInstance(), -1);
        return yesterday.get(Calendar.YEAR) == cal.get(Calendar.YEAR) && yesterday.get(Calendar.DAY_OF_YEAR) == cal.get(Calendar.DAY_OF_YEAR);
    }

    /**
     * Checks if the calendar object is last week.
     *
     * @param cal the calendar object
     * @return true if the calendar object is last week.
     */
    public static boolean isLastWeek(Calendar cal) {
        Calendar lastWeek = adjustDate(Calendar.getInstance(), -7);
        return lastWeek.get(Calendar.YEAR) == cal.get(Calendar.YEAR) && lastWeek.get(Calendar.WEEK_OF_YEAR) == cal.get(Calendar.WEEK_OF_YEAR);
    }

    /**
     * Checks if the calendar object is last month.
     *
     * @param cal the calendar object
     * @return true if the calendar object is last month.
     */
    public static boolean isLastMonth(Calendar cal) {
        Calendar today = Calendar.getInstance();
        int thisMonth = today.get(Calendar.MONTH);
        if (thisMonth > 1) {
            return today.get(Calendar.YEAR) == cal.get(Calendar.YEAR) && thisMonth - 1 == cal.get(Calendar.MONTH);
        }
        else {
            return today.get(Calendar.YEAR) - 1 == cal.get(Calendar.YEAR) && today.getActualMaximum(Calendar.MONTH) == cal.get(Calendar.MONTH);
        }
    }

    /**
     * Checks if the calendar object is last quarter.
     *
     * @param cal the calendar object
     * @return true if the calendar object is last quarter.
     */
    public static boolean isLastQuarter(Calendar cal) {
        Calendar today = Calendar.getInstance();
        int thisQuarter = today.get(Calendar.MONTH) / 3;
        if (thisQuarter > 1) {
            return today.get(Calendar.YEAR) == cal.get(Calendar.YEAR) && thisQuarter - 1 == cal.get(Calendar.MONTH) / 3;
        }
        else {
            return today.get(Calendar.YEAR) - 1 == cal.get(Calendar.YEAR) && today.getActualMaximum(Calendar.MONTH) / 3 == cal.get(Calendar.MONTH) / 3;
        }
    }

    /**
     * Checks if the calendar object is last year.
     *
     * @param cal the calendar object
     * @return true if the calendar object is last year.
     */
    public static boolean isLastYear(Calendar cal) {
        Calendar today = Calendar.getInstance();
        return today.get(Calendar.YEAR) - 1 == cal.get(Calendar.YEAR);

    }

    /**
     * Checks if the calendar object is same date as tomorrow.
     *
     * @param cal the calendar object
     * @return true if the calendar object is the same date as tomorrow.
     */
    public static boolean isTomorrow(Calendar cal) {
        Calendar tomorrow = adjustDate(Calendar.getInstance(), 1);
        return tomorrow.get(Calendar.YEAR) == cal.get(Calendar.YEAR) && tomorrow.get(Calendar.DAY_OF_YEAR) == cal.get(Calendar.DAY_OF_YEAR);
    }

    /**
     * Checks if the calendar object is next week.
     *
     * @param cal the calendar object
     * @return true if the calendar object is next week.
     */
    public static boolean isNextWeek(Calendar cal) {
        Calendar nextWeek = adjustDate(Calendar.getInstance(), 7);
        return nextWeek.get(Calendar.YEAR) == cal.get(Calendar.YEAR) && nextWeek.get(Calendar.WEEK_OF_YEAR) == cal.get(Calendar.WEEK_OF_YEAR);
    }

    /**
     * Checks if the calendar object is next month.
     *
     * @param cal the calendar object
     * @return true if the calendar object is next month.
     */
    public static boolean isNextMonth(Calendar cal) {
        Calendar today = Calendar.getInstance();
        int thisMonth = today.get(Calendar.MONTH);
        if (thisMonth < today.getActualMaximum(Calendar.MONTH)) {
            return today.get(Calendar.YEAR) == cal.get(Calendar.YEAR) && thisMonth + 1 == cal.get(Calendar.MONTH);
        }
        else {
            return today.get(Calendar.YEAR) + 1 == cal.get(Calendar.YEAR) && today.getMinimum(Calendar.MONTH) == cal.get(Calendar.MONTH);
        }
    }

    /**
     * Checks if the calendar object is next quarter.
     *
     * @param cal the calendar object
     * @return true if the calendar object is next quarter.
     */
    public static boolean isNextQuarter(Calendar cal) {
        Calendar today = Calendar.getInstance();
        int thisQuarter = today.get(Calendar.MONTH) / 3;
        if (thisQuarter < today.getActualMaximum(Calendar.MONTH) / 3) {
            return today.get(Calendar.YEAR) == cal.get(Calendar.YEAR) && thisQuarter + 1 == cal.get(Calendar.MONTH) / 3;
        }
        else {
            return today.get(Calendar.YEAR) + 1 == cal.get(Calendar.YEAR) && today.getActualMinimum(Calendar.MONTH) / 3 == cal.get(Calendar.MONTH) / 3;
        }
    }

    /**
     * Checks if the calendar object is next year.
     *
     * @param cal the calendar object
     * @return true if the calendar object is next year.
     */
    public static boolean isNextYear(Calendar cal) {
        Calendar today = Calendar.getInstance();
        return today.get(Calendar.YEAR) + 1 == cal.get(Calendar.YEAR);

    }

    /**
     * Checks if the calendar object is in the specified month, regardless of the year.
     *
     * @param cal   the calendar object.
     * @param month the month, starting from 0 for January. You can use the value defined in Calendar such as
     *              Calendar.JANUARY, Calendar.FEBRUARY, etc.
     * @return true if the calendar object is the specified month.
     */
    public static boolean isAtMonth(Calendar cal, int month) {
        return cal.get(Calendar.MONTH) == month;
    }

    /**
     * Checks if the calendar object is in the specified quarter, regardless of the year.
     *
     * @param cal     the calendar object.
     * @param quarter the quarter, starting from 1 for the first quarter (including January, February, and March).
     * @return true if the calendar object is the specified quarter.
     */
    public static boolean isAtQuarter(Calendar cal, int quarter) {
        return cal.get(Calendar.MONTH) / 3 + 1 == quarter;
    }

    /**
     * Adjusts the Calendar to several days before or after the current date.
     *
     * @param calendar        the Calendar object to be adjusted.
     * @param differenceInDay the difference in days. It accepts both position and negative number.
     * @return the calendar after the adjustment. It should always be the same instance as the calendar parameter.
     */
    public static Calendar adjustDate(Calendar calendar, int differenceInDay) {
        calendar.setTimeInMillis(calendar.getTimeInMillis() + DAY_IN_MS * differenceInDay);
        return calendar;
    }

    /**
     * Returns the minimum Date in the Date list.
     *
     * @param dates the list of Date to calculate the minimum.
     * @return the minimum date in the Date list.
     */
    public static Date min(List<Date> dates) {
        long min = Long.MAX_VALUE;
        Date minDate = null;
        for (Date value : dates) {
            long v = value.getTime();
            if (v < min) {
                min = v;
                minDate = value;
            }
        }
        return minDate;
    }

    /**
     * Returns the maximum Date in the Date list.
     *
     * @param dates the list of Date to calculate the maximum.
     * @return the maximum date in the Date list.
     */
    public static Date max(List<Date> dates) {
        long max = Long.MIN_VALUE;
        Date maxDate = null;
        for (Date value : dates) {
            long v = value.getTime();
            if (v > max) {
                max = v;
                maxDate = value;
            }
        }
        return maxDate;
    }

    /**
     * Returns the minimum Calendar in the Calendar list.
     *
     * @param calendars the list of Calendar to calculate the minimum.
     * @return the minimum calendar in the Calendar list.
     */
    public static Calendar min(List<Calendar> calendars) {
        long min = Long.MAX_VALUE;
        Calendar minCalendar = null;
        for (Calendar value : calendars) {
            long v = value.getTimeInMillis();
            if (v < min) {
                min = v;
                minCalendar = value;
            }
        }
        return minCalendar;
    }

    /**
     * Returns the maximum Calendar in the Calendar list.
     *
     * @param calendars the list of Calendar to calculate the maximum.
     * @return the maximum calendar in the Calendar list.
     */
    public static Calendar max(List<Calendar> calendars) {
        long max = Long.MIN_VALUE;
        Calendar maxCalendar = null;
        for (Calendar value : calendars) {
            long v = value.getTimeInMillis();
            if (v > max) {
                max = v;
                maxCalendar = value;
            }
        }
        return maxCalendar;
    }

//    public static void main(String[] args) {
//        Calendar cal = Calendar.getInstance();
//        for (int i = 0; i < 400; i++) {
//            System.out.println(ObjectConverterManager.toString(cal));
//            System.out.printf("isToday: %b, isThisWeek: %b, isThisMonth: %b, isThisQuarter: %b, isThisYear: %b\n", isToday(cal), isThisWeek(cal), isThisMonth(cal), isThisQuarter(cal), isThisYear(cal));
//            System.out.printf("isYesterday: %b, isLastWeek: %b, isLastMonth: %b, isLastQuarter: %b, isLastYear: %b\n", isYesterday(cal), isLastWeek(cal), isLastMonth(cal), isLastQuarter(cal), isLastYear(cal));
//            System.out.printf("isTomorrow: %b, isNextWeek: %b, isNextMonth: %b, isNextQuarter: %b, isNextYear: %b\n", isTomorrow(cal), isNextWeek(cal), isNextMonth(cal), isNextQuarter(cal), isNextYear(cal));
//            adjustDate(cal, -1);
//        }
//        cal = Calendar.getInstance();
//        for (int i = 0; i < 400; i++) {
//            System.out.println(ObjectConverterManager.toString(cal));
//            System.out.printf("isToday: %b, isThisWeek: %b, isThisMonth: %b, isThisQuarter: %b, isThisYear: %b\n", isToday(cal), isThisWeek(cal), isThisMonth(cal), isThisQuarter(cal), isThisYear(cal));
//            System.out.printf("isYesterday: %b, isLastWeek: %b, isLastMonth: %b, isLastQuarter: %b, isLastYear: %b\n", isYesterday(cal), isLastWeek(cal), isLastMonth(cal), isLastQuarter(cal), isLastYear(cal));
//            System.out.printf("isTomorrow: %b, isNextWeek: %b, isNextMonth: %b, isNextQuarter: %b, isNextYear: %b\n", isTomorrow(cal), isNextWeek(cal), isNextMonth(cal), isNextQuarter(cal), isNextYear(cal));
//            adjustDate(cal, 1);
//        }
//    }
}
