/*
 * @(#)DateDayOfWeekInMonthGrouper.java 5/19/2006
 *
 * Copyright 2002 - 2006 JIDE Software Inc. All rights reserved.
 */

package com.jidesoft.grouper.date;

import com.jidesoft.grouper.GroupResources;
import com.jidesoft.grouper.GrouperContext;

import java.util.Calendar;
import java.util.Locale;

public class DateDayOfWeekInMonthGrouper extends DateGrouper {
    public static GrouperContext CONTEXT = new GrouperContext("DateDayOfWeekInMonth");

    private static Object[] _groups = null;

    public static Object[] getAvailableGroups() {
        if (_groups == null) {
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.DAY_OF_WEEK_IN_MONTH, 0);
            _groups = new Object[cal.getMaximum(Calendar.DAY_OF_WEEK_IN_MONTH)];
            for (int i = 0; i < _groups.length; i++) {
                _groups[i] = getCalendarField(cal, Calendar.DAY_OF_WEEK_IN_MONTH);
                cal.roll(Calendar.DAY_OF_WEEK_IN_MONTH, 1);
            }
        }
        return _groups;
    }

    public Object getValue(Object value) {
        Object field = getCalendarField(value, Calendar.DAY_OF_WEEK_IN_MONTH);
        if (field instanceof Integer && (Integer) field >= 0 && (Integer) field < getAvailableGroups().length) {
            return getAvailableGroups()[((Integer) field)];
        }
        else {
            return null;
        }
    }

    public String getName() {
        return GroupResources.getResourceBundle(Locale.getDefault()).getString("Date.dayOfWeekInMonth");
    }

//    public static void main(String[] args) {
//        ObjectGrouper grouper = new DateDayOfWeekInMonthGrouper();
//        Calendar calendar = Calendar.getInstance();
//        for (int i = 0; i < 40; i++) {
//            System.out.println(grouper.getValue(calendar));
//            calendar.roll(Calendar.DAY_OF_WEEK_IN_MONTH, 1);
//        }
//    }
}
