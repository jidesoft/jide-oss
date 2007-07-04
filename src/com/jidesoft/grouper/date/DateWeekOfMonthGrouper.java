package com.jidesoft.grouper.date;

import com.jidesoft.grouper.GroupResources;
import com.jidesoft.grouper.GrouperContext;

import java.util.Calendar;
import java.util.Locale;

public class DateWeekOfMonthGrouper extends DateGrouper {
    public static GrouperContext CONTEXT = new GrouperContext("DateWeekOfMonth");


    private static Object[] _groups = null;

    public static Object[] getAvailableGroups() {
        if (_groups == null) {
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.DATE, 0);
            _groups = new Object[cal.getMaximum(Calendar.WEEK_OF_MONTH)];
            for (int i = 0; i < _groups.length; i++) {
                _groups[i] = getCalendarField(cal, Calendar.WEEK_OF_MONTH);
                cal.roll(Calendar.WEEK_OF_MONTH, 1);
            }
        }
        return _groups;
    }

    public Object getValue(Object value) {
        Object field = getCalendarField(value, Calendar.WEEK_OF_MONTH);
        if (field instanceof Integer && (Integer) field >= 0 && (Integer) field < getAvailableGroups().length) {
            return getAvailableGroups()[((Integer) field)];
        }
        else {
            return null;
        }
    }

    public String getName() {
        return GroupResources.getResourceBundle(Locale.getDefault()).getString("Date.weekOfMonth");
    }

//    public static void main(String[] args) {
//        ObjectGrouper grouper = new DateWeekOfMonthGrouper();
//        Calendar calendar = Calendar.getInstance();
//        for (int i = 0; i < 40; i++) {
//            System.out.println(grouper.getValue(calendar));
//            calendar.roll(Calendar.DAY_OF_YEAR, 7);
//        }
//    }
}
