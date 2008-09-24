/*
 * @(#)YearConverter.java 5/8/2006
 *
 * Copyright 2002 - 2006 JIDE Software Inc. All rights reserved.
 */

package com.jidesoft.converter;

/**
 * Converter which converts year to int and converts it back. It is no difference from a number converter except it
 * doesn't use grouping when formatting.
 */
public class YearNameConverter implements ObjectConverter {

    /**
     * Default ConverterContext for MonthConverter.
     */
    public static ConverterContext CONTEXT = new ConverterContext("YearName");

    /**
     * Creates a new CalendarConverter.
     */
    public YearNameConverter() {
    }

    public String toString(Object object, ConverterContext context) {
        if (object == null || !(object instanceof Number)) {
            return "";
        }
        else {
            return object.toString();
        }
    }

    public boolean supportToString(Object object, ConverterContext context) {
        return true;
    }

    public Object fromString(String string, ConverterContext context) {
        try {
            return Integer.parseInt(string);
        }
        catch (NumberFormatException e) {
            return string;
        }
    }

    public boolean supportFromString(String string, ConverterContext context) {
        return true;
    }
}
