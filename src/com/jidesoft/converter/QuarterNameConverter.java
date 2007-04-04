/*
 * @(#)QuarterNameConverter.java 5/8/2006
 *
 * Copyright 2002 - 2006 JIDE Software Inc. All rights reserved.
 */

package com.jidesoft.converter;

import java.text.MessageFormat;
import java.text.ParseException;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Converter which converts quarter to String and converts it back.
 */
public class QuarterNameConverter implements ObjectConverter {

    /**
     * Default ConverterContext for MonthConverter.
     */
    public static ConverterContext CONTEXT = new ConverterContext("QuarterName");
    private static String _quarterNamePattern;

    /**
     * Creates a new CalendarConverter.
     */
    public QuarterNameConverter() {
    }

    public String toString(Object object, ConverterContext context) {
        if (object == null || !(object instanceof Integer)) {
            return "";
        }
        else {
            return MessageFormat.format(getQuarterNamePattern(), new Object[]{new Integer((((Integer) object).intValue() + 1))});
        }
    }

    public boolean supportToString(Object object, ConverterContext context) {
        return true;
    }

    public Object fromString(String string, ConverterContext context) {
        String quarterNamePattern = getQuarterNamePattern();
        try {
            Object[] values = new MessageFormat(quarterNamePattern).parse(string);
            if (values.length > 0) {
                return new Integer(Integer.parseInt("" + values[0]));
            }
        }
        catch (ParseException e) {

        }
        return new Integer(0);
    }

    public boolean supportFromString(String string, ConverterContext context) {
        return true;
    }

//    public static void main(String[] args) {
//        ObjectConverter converter = new QuarterNameConverter();
//        for (int i = 0; i < 4; i++) {
//            String str = converter.toString(new Integer(i), null);
//            System.out.println(str);
//            System.out.println(converter.fromString(str, null));
//        }
//    }

    /**
     * Gets the quarter name pattern when converting from an int to a String. For example, if the int is 0, it will
     * converted to "Qtr 1" if the quarter name pattern is "Qtr {0}".
     *
     * @return the prefix.
     */
    public String getQuarterNamePattern() {
        if (_quarterNamePattern == null) {
            return getResourceString("Quarter.quarter");
        }
        return _quarterNamePattern;
    }

    /**
     * Sets the quarter name pattern. For example, if the int is 0, it will
     * converted to "Qtr 1" if the pattern is "Qtr {0}".
     *
     * @param quarterName
     */
    public void setQuarterNamePattern(String quarterName) {
        _quarterNamePattern = quarterName;
    }

    protected String getResourceString(String key) {
        final ResourceBundle resourceBundle = Resource.getResourceBundle(Locale.getDefault());
        return resourceBundle.getString(key);
    }

}
