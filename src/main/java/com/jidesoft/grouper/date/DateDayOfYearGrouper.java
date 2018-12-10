package com.jidesoft.grouper.date;

import com.jidesoft.grouper.GroupResources;
import com.jidesoft.grouper.GrouperContext;

import java.util.Calendar;
import java.util.Locale;

public class DateDayOfYearGrouper extends DateGrouper {
    public static GrouperContext CONTEXT = new GrouperContext("DateDayOfYear");

    public Object getValue(Object value) {
        return getCalendarField(value, Calendar.DAY_OF_YEAR);
    }

    public String getName() {
        return GroupResources.getResourceBundle(Locale.getDefault()).getString("Date.dayOfYear");
    }

//    public static void main(String[] args) {
//        ObjectGrouper grouper = new DateDayOfYearGrouper();
//        Calendar calendar = Calendar.getInstance();
//        for (int i = 0; i < 200; i++) {
//            System.out.println(grouper.getValue(calendar));
//            calendar.roll(Calendar.DAY_OF_YEAR, 1);
//        }
//    }
}
