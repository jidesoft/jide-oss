/*
 * @(#)YearConverter.java 5/8/2006
 *
 * Copyright 2002 - 2006 JIDE Software Inc. All rights reserved.
 */

package com.jidesoft.converter;

import java.text.NumberFormat;

/**
 * Converter which converts year to int and converts it back. It is no difference from a number converter except it
 * doesn't use grouping when formatting.
 */
public class YearNameConverter extends NumberConverter {

    /**
     * Default ConverterContext for MonthConverter.
     */
    public static ConverterContext CONTEXT = new ConverterContext("YearName");

    /**
     * Creates a new CalendarConverter.
     */
    public YearNameConverter() {
        setNumberFormat(getDefaultNumberFormat());
    }

    @Override
    protected NumberFormat getDefaultNumberFormat() {
        NumberFormat format = super.getDefaultNumberFormat();
        format.setGroupingUsed(false);
        format.setMaximumFractionDigits(0);
        format.setMinimumFractionDigits(0);
        return format;
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
