package com.jidesoft.grouper.date;

import com.jidesoft.grouper.GroupResources;
import com.jidesoft.grouper.GrouperContext;

import java.util.Calendar;
import java.util.Locale;

public class DateDayOfMonthGrouper extends DateGrouper {
    public static GrouperContext CONTEXT = new GrouperContext("DateDayOfMonth");

    private static Object[] _groups = null;

    public static Object[] getAvailableGroups() {
        if (_groups == null) {
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.MONTH, 0);
            cal.set(Calendar.DAY_OF_MONTH, 1);
            _groups = new Object[cal.getMaximum(Calendar.DAY_OF_MONTH)];
            for (int i = 0; i < _groups.length; i++) {
                _groups[i] = getCalendarField(cal, Calendar.DAY_OF_MONTH);
                cal.roll(Calendar.DAY_OF_MONTH, 1);
            }
        }
        return _groups;
    }

    public Object getValue(Object value) {
        Object field = getCalendarField(value, Calendar.DAY_OF_MONTH);
        if (field instanceof Integer && (Integer) field >= 1 && (Integer) field <= getAvailableGroups().length) {
            return getAvailableGroups()[(Integer) field - 1];
        }
        else {
            return null;
        }
    }

    public String getName() {
        return GroupResources.getResourceBundle(Locale.getDefault()).getString("Date.dayOfMonth");
    }

//    public static void main(String[] args) {
//        ObjectGrouper grouper = new DateDayOfMonthGrouper();
//        Calendar calendar = Calendar.getInstance();
//        for (int i = 0; i < 40; i++) {
//            System.out.println(grouper.getValue(calendar));
//            calendar.roll(Calendar.DATE, 1);
//        }
//    }
}
