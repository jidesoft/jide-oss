/*
 * @(#) DateConverter.java
 *
 * Copyright 2002 - 2003 JIDE Software. All rights reserved.
 */
package com.jidesoft.converter;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Converter which converts Date to String and converts it back.
 */
public class MonthConverter implements ObjectConverter {

    /**
     * Default ConverterContext for MonthConverter.
     */
    public static ConverterContext CONTEXT_MONTH = new ConverterContext("Calendar.Month");

    private DateFormat _conciseFormat = new SimpleDateFormat("MMyy");
    private DateFormat _shortFormat = new SimpleDateFormat("MM/yy");
    private DateFormat _mediumFormat = new SimpleDateFormat("MM, yyyy");
    private DateFormat _longFormat = new SimpleDateFormat("MMMMM, yyyy");

    private DateFormat _defaultFormat = _shortFormat;

    /**
     * Creates a new CalendarConverter.
     */
    public MonthConverter() {
    }

    public String toString(Object object, ConverterContext context) {
        if (object == null || !(object instanceof Calendar)) {
            return "";
        }
        else {
            return _defaultFormat.format(((Calendar) object).getTime());
        }
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
                Date time = _shortFormat.parse(string);
                calendar.setTime(time);
            }
            catch (ParseException e2) {
                try {
                    Date time = _mediumFormat.parse(string);
                    calendar.setTime(time);
                }
                catch (ParseException e3) {
                    try {
                        Date time = _longFormat.parse(string);
                        calendar.setTime(time);
                    }
                    catch (ParseException e4) {
                        try {
                            Date time = _conciseFormat.parse(string);
                            calendar.setTime(time);
                        }
                        catch (ParseException e5) {
                            return null;  // nothing works just return null so that old value will be kept.
                        }
                    }
                }
            }
        }
        return calendar;
    }

    public boolean supportFromString(String string, ConverterContext context) {
        return true;
    }

    /**
     * Gets DefaultFormat to format an calendar.
     *
     * @return DefaultFormat
     */
    public DateFormat getDefaultFormat() {
        return _defaultFormat;
    }

    /**
     * Sets DefaultFormat to format an calendar.
     *
     * @param defaultFormat the default format to format a calendar.
     */
    public void setDefaultFormat(DateFormat defaultFormat) {
        _defaultFormat = defaultFormat;
    }
}
