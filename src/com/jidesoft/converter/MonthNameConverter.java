/*
 * @(#)MonthNameConverter.java 5/8/2006
 *
 * Copyright 2002 - 2006 JIDE Software Inc. All rights reserved.
 */

package com.jidesoft.converter;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Converter which converts int to month string and converts it back.
 */
public class MonthNameConverter implements ObjectConverter {

    /**
     * Default ConverterContext for MonthConverter.
     */
    public static ConverterContext CONTEXT = new ConverterContext("MonthName");

    /**
     * 0 -> "1", 1 -> "2", ..., 11 -> "12"
     */
    public static final DateFormat CONCISE_FORMAT = new SimpleDateFormat("M");

    /**
     * 0 -> "01", 1 -> "02", ..., 11 -> "12"
     */
    public static final DateFormat SHORT_FORMAT = new SimpleDateFormat("MM");

    /**
     * 0 -> "Jan", 1 -> "Feb", ..., 11 -> "Dec"
     */
    public static final DateFormat MEDIUM_FORMAT = new SimpleDateFormat("MMM");

    /**
     * 0 -> "January", 1 -> "February", ..., 11 -> "December"
     */
    public static final DateFormat LONG_FORMAT = new SimpleDateFormat("MMMMM");

    private DateFormat _defaultFormat = MEDIUM_FORMAT;

    /**
     * Creates a new CalendarConverter.
     */
    public MonthNameConverter() {
    }

    public String toString(Object object, ConverterContext context) {
        if (object == null || !(object instanceof Integer)) {
            return "";
        }
        else {
            return _defaultFormat.format((getCalendarByMonth((Integer) object).getTime()));
        }
    }

    static final Calendar CAL = Calendar.getInstance();

    static {
        CAL.set(Calendar.DAY_OF_MONTH, 1);
    }

    protected Calendar getCalendarByMonth(int month) {
        CAL.set(Calendar.MONTH, month);
        return CAL;
    }

    public boolean supportToString(Object object, ConverterContext context) {
        return true;
    }

    public Object fromString(String string, ConverterContext context) {
        Calendar calendar = Calendar.getInstance();
        try {
            Date time = _defaultFormat.parse(string);
            calendar.setTime(time);
        }
        catch (ParseException e1) { // if current formatter doesn't work try those default ones.
            try {
                Date time = SHORT_FORMAT.parse(string);
                calendar.setTime(time);
            }
            catch (ParseException e2) {
                try {
                    Date time = MEDIUM_FORMAT.parse(string);
                    calendar.setTime(time);
                }
                catch (ParseException e3) {
                    try {
                        Date time = LONG_FORMAT.parse(string);
                        calendar.setTime(time);
                    }
                    catch (ParseException e4) {
                        try {
                            Date time = CONCISE_FORMAT.parse(string);
                            calendar.setTime(time);
                        }
                        catch (ParseException e5) {
                            return null;  // nothing works just return null so that old value will be kept.
                        }
                    }
                }
            }
        }
        return calendar.get(Calendar.MONTH);
    }

    public boolean supportFromString(String string, ConverterContext context) {
        return true;
    }

    /**
     * Gets default format to format a month.
     *
     * @return DefaultFormat
     */
    public DateFormat getDefaultFormat() {
        return _defaultFormat;
    }

    /**
     * Sets default format to format a month. Default is {@link #MEDIUM_FORMAT}.
     *
     * @param defaultFormat the default format to format the month.
     */
    public void setDefaultFormat(DateFormat defaultFormat) {
        _defaultFormat = defaultFormat;
    }

    public static void main(String[] args) {
        MonthNameConverter converter = new MonthNameConverter();
        converter.setDefaultFormat(MonthNameConverter.LONG_FORMAT);
        for (int i = 0; i < 12; i++) {
            String str = converter.toString(new Integer(i), null);
            System.out.println(str);
            System.out.println(converter.fromString(str, null));
        }
    }
}
