package com.jidesoft.grouper.date;

import com.jidesoft.grouper.GroupResources;
import com.jidesoft.grouper.GrouperContext;

import java.util.Calendar;
import java.util.Locale;

public class DateDayOfWeekGrouper extends DateGrouper {

    public static GrouperContext CONTEXT = new GrouperContext("DateDayOfWeek");

    private static Object[] _groups = null;

    public static Object[] getAvailableGroups() {
        if (_groups == null) {
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.DAY_OF_WEEK, 0);
            _groups = new Object[cal.getMaximum(Calendar.DAY_OF_WEEK)];
            for (int i = 0; i < _groups.length; i++) {
                _groups[i] = i + 1;
                cal.roll(Calendar.DAY_OF_WEEK, 1);
            }
        }
        return _groups;
    }

    public Object getValue(final Object value) {
        Object field = getCalendarField(value, Calendar.DAY_OF_WEEK);
        if (field instanceof Integer) {
            int intValue = (Integer) field;
            if (intValue >= 1 && intValue < (getAvailableGroups().length + 1)) {
                return getAvailableGroups()[intValue - 1];
            }
        }
        return null;
    }

    public String getName() {
        return GroupResources.getResourceBundle(Locale.getDefault()).getString("Date.dayOfWeek");
    }

//    public static void main(String[] args) {
//        ObjectGrouper grouper = new DateDayOfWeekGrouper();
//        System.out.println(grouper.getValue(Calendar.getInstance()));
//    }
}
