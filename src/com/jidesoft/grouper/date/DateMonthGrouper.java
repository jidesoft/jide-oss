package com.jidesoft.grouper.date;

import com.jidesoft.converter.ConverterContext;
import com.jidesoft.converter.MonthNameConverter;
import com.jidesoft.grouper.GroupResources;
import com.jidesoft.grouper.GrouperContext;

import java.util.Calendar;
import java.util.Locale;

public class DateMonthGrouper extends DateGrouper {
    public static GrouperContext CONTEXT = new GrouperContext("DateMonth");

    private static Object[] _groups = null;

    public static Object[] getAvailableGroups() {
        if (_groups == null) {
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.MONTH, 0);
            _groups = new Object[cal.getMaximum(Calendar.MONTH) + 1];
            for (int i = 0; i < _groups.length; i++) {
                _groups[i] = getCalendarField(cal, Calendar.MONTH);
                cal.roll(Calendar.MONTH, 1);
            }
        }
        return _groups;
    }

    public Object getValue(Object value) {
        Object field = getCalendarField(value, Calendar.MONTH);
        if (field instanceof Integer && (Integer) field >= 0 && (Integer) field < getAvailableGroups().length) {
            return getAvailableGroups()[((Integer) field)];
        }
        else {
            return null;
        }
    }


    public String getName() {
        return GroupResources.getResourceBundle(Locale.getDefault()).getString("Date.month");
    }

    @Override
    public ConverterContext getConverterContext() {
        return MonthNameConverter.CONTEXT;
    }

//    public static void main(String[] args) {
//        ObjectGrouper grouper = new DateMonthGrouper();
//        Calendar calendar = Calendar.getInstance();
//        for (int i = 0; i < 40; i++) {
//            System.out.println(grouper.getValue(calendar));
//            calendar.roll(Calendar.MONTH, 1);
//        }
//    }
}
