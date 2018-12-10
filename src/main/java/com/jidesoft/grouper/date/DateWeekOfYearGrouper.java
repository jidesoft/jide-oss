/*
 * @(#)DateWeekOfYearGrouper.java 5/19/2006
 *
 * Copyright 2002 - 2006 JIDE Software Inc. All rights reserved.
 */

package com.jidesoft.grouper.date;

import com.jidesoft.grouper.GroupResources;
import com.jidesoft.grouper.GrouperContext;

import java.util.Calendar;
import java.util.Locale;

public class DateWeekOfYearGrouper extends DateGrouper {
    public static GrouperContext CONTEXT = new GrouperContext("DateWeekOfYear");

    public Object getValue(Object value) {
        return getCalendarField(value, Calendar.WEEK_OF_YEAR);
    }

    public String getName() {
        return GroupResources.getResourceBundle(Locale.getDefault()).getString("Date.weekOfYear");
    }
//    public static void main(String[] args) {
//        ObjectGrouper grouper = new DateWeekOfYearGrouper();
//        Calendar calendar = Calendar.getInstance();
//        for (int i = 0; i < 40; i++) {
//            System.out.println(grouper.getValue(calendar));
//            calendar.roll(Calendar.WEEK_OF_YEAR, 1);
//        }
//    }
}
