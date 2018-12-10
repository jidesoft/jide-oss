/*
 * @(#)DateQuarterGrouper.java 5/19/2006
 *
 * Copyright 2002 - 2006 JIDE Software Inc. All rights reserved.
 */

package com.jidesoft.grouper.date;

import com.jidesoft.converter.ConverterContext;
import com.jidesoft.converter.QuarterNameConverter;
import com.jidesoft.grouper.GroupResources;
import com.jidesoft.grouper.GrouperContext;

import java.util.Calendar;
import java.util.Locale;

public class DateQuarterGrouper extends DateGrouper {
    public static GrouperContext CONTEXT = new GrouperContext("DateQuarter");

    private static Object[] _groups = null;

    public static Object[] getAvailableGroups() {
        if (_groups == null) {
            _groups = new Object[4];
            for (int i = 0; i < _groups.length; i++) {
                _groups[i] = i;
            }
        }
        return _groups;
    }

    public Object getValue(Object value) {
        Object dateField = getCalendarField(value, Calendar.MONTH);
        if (dateField instanceof Integer) {
            return getAvailableGroups()[(Integer) dateField / 3];
        }
        else {
            return null;
        }
    }

    public String getName() {
        return GroupResources.getResourceBundle(Locale.getDefault()).getString("Date.quarter");
    }

    @Override
    public ConverterContext getConverterContext() {
        return QuarterNameConverter.CONTEXT;
    }

//    public static void main(String[] args) {
//        ObjectGrouper grouper = new DateQuarterGrouper();
//        Calendar calendar = Calendar.getInstance();
//        for (int i = 0; i < 40; i++) {
//            System.out.println(grouper.getValue(calendar));
//            calendar.roll(Calendar.MONTH, 1);
//        }
//    }
}
