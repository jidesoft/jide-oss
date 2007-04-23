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

/**
 * Converter which converts Date to String and converts it back.
 */
public class DateConverter implements ObjectConverter {

    public static final ConverterContext DATETIME_CONTEXT = new ConverterContext("DateTime");
    public static final ConverterContext TIME_CONTEXT = new ConverterContext("Time");
    public static final ConverterContext DATE_CONTEXT = new ConverterContext("Date");

    private DateFormat _shortFormat = SimpleDateFormat.getDateInstance(DateFormat.SHORT);
    private DateFormat _mediumFormat = SimpleDateFormat.getDateInstance(DateFormat.MEDIUM);
    private DateFormat _longFormat = SimpleDateFormat.getDateInstance(DateFormat.LONG);

    private DateFormat _defaultFormat = SimpleDateFormat.getDateInstance(DateFormat.DEFAULT);

    private DateFormat _shortDatetimeFormat = SimpleDateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);
    private DateFormat _mediumDatetimeFormat = SimpleDateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM);
    private DateFormat _longDatetimeFormat = SimpleDateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG);

    private DateFormat _defaultDatetimeFormat = SimpleDateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.DEFAULT);

    private DateFormat _shortTimeFormat = SimpleDateFormat.getTimeInstance(DateFormat.SHORT);
    private DateFormat _mediumTimeFormat = SimpleDateFormat.getTimeInstance(DateFormat.MEDIUM);
    private DateFormat _longTimeFormat = SimpleDateFormat.getTimeInstance(DateFormat.LONG);

    private DateFormat _defaultTimeFormat = SimpleDateFormat.getTimeInstance(DateFormat.DEFAULT);


    /**
     * Creates a DateConverter.
     */
    public DateConverter() {
    }

    public String toString(Object object, ConverterContext context) {
        if (object == null) {
            return "";
        }
        else {
            if (object instanceof Calendar) {
                object = ((Calendar) object).getTime();
            }

            if (DATETIME_CONTEXT.equals(context)) {
                return _defaultDatetimeFormat.format(object);
            }
            else if (TIME_CONTEXT.equals(context)) {
                return _defaultTimeFormat.format(object);
            }
            else {
                return _defaultFormat.format(object);
            }
        }
    }

    public boolean supportToString(Object object, ConverterContext context) {
        return true;
    }

    /**
     * Converts from a String to a Date.
     *
     * @param string  the string to be converted.
     * @param context the context. It could be DATETIME_CONTEXT, DATE_CONTEXT or TIME_CONTEXT.
     * @return the Date. If the string is null or empty, null will be returned. If the string cannot be parsed as a date, the string itself will be returned.
     */
    public Object fromString(String string, ConverterContext context) {
        if (string == null || string.trim().length() == 0) {
            return null;
        }

        try {
            if (DATETIME_CONTEXT.equals(context)) {
                return _defaultDatetimeFormat.parse(string);
            }
            else if (TIME_CONTEXT.equals(context)) {
                return _defaultTimeFormat.parse(string);
            }
            else {
                return _defaultFormat.parse(string);
            }
        }
        catch (ParseException e1) { // if current formatter doesn't work try those default ones.
            if (DATETIME_CONTEXT.equals(context)) {
                try {
                    return _shortDatetimeFormat.parse(string);
                }
                catch (ParseException e2) {
                    try {
                        return _mediumDatetimeFormat.parse(string);
                    }
                    catch (ParseException e3) {
                        try {
                            return _longDatetimeFormat.parse(string);
                        }
                        catch (ParseException e4) {
                            return string;  // nothing works just return null so that old value will be kept.
                        }
                    }
                }
            }
            else if (TIME_CONTEXT.equals(context)) {
                try {
                    return _shortTimeFormat.parse(string);
                }
                catch (ParseException e2) {
                    try {
                        return _mediumTimeFormat.parse(string);
                    }
                    catch (ParseException e3) {
                        try {
                            return _longTimeFormat.parse(string);
                        }
                        catch (ParseException e4) {
                            return string;  // nothing works just return null so that old value will be kept.
                        }
                    }
                }
            }
            else {
                try {
                    return _shortFormat.parse(string);
                }
                catch (ParseException e2) {
                    try {
                        return _mediumFormat.parse(string);
                    }
                    catch (ParseException e3) {
                        try {
                            return _longFormat.parse(string);
                        }
                        catch (ParseException e4) {
                            return string;  // nothing works just return null so that old value will be kept.
                        }
                    }
                }
            }
        }
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
     * @param defaultFormat
     */
    public void setDefaultFormat(DateFormat defaultFormat) {
        _defaultFormat = defaultFormat;
    }

    /**
     * Gets DefaultTimeFormat to format an calendar. This is used
     * only when context is {@link #TIME_CONTEXT}.
     *
     * @return DefaultTimeFormat
     */
    public DateFormat getDefaultTimeFormat() {
        return _defaultTimeFormat;
    }

    /**
     * Sets DefaultTimeFormat to format an calendar. This is used
     * only when context is {@link #TIME_CONTEXT}.
     *
     * @param defaultTimeFormat
     */
    public void setDefaultTimeFormat(DateFormat defaultTimeFormat) {
        _defaultTimeFormat = defaultTimeFormat;
    }

    /**
     * Gets DefaultDatetimeFormat to format an calendar. This is used
     * only when context is {@link #DATETIME_CONTEXT}.
     *
     * @return DefaultDatetimeFormat
     */
    public DateFormat getDefaultDatetimeFormat() {
        return _defaultDatetimeFormat;
    }

    /**
     * Sets DefaultDatetimeFormat to format an calendar. This is used
     * only when context is {@link #DATETIME_CONTEXT}.
     *
     * @param defaultDatetimeFormat
     */
    public void setDefaultDatetimeFormat(DateFormat defaultDatetimeFormat) {
        _defaultDatetimeFormat = defaultDatetimeFormat;
    }
}
