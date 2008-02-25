/*
 * @(#) IntegerConverter.java
 *
 * Copyright 2002 - 2003 JIDE Software. All rights reserved.
 */
package com.jidesoft.converter;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

/**
 * Converter which converts Number to String and converts it back. You can pass in a NumberFormat as
 * UserObject of ConverterContext if you want to control the format of the number such as maximum
 * decimal point etc.
 */
abstract public class NumberConverter implements ObjectConverter {
    private NumberFormat _numberFormat;

    /**
     * Creates a number converter with no NumberFormat.
     */
    public NumberConverter() {
    }

    /**
     * Creates the number converter with a specified NumberFormat.
     *
     * @param format the number format.
     */
    public NumberConverter(NumberFormat format) {
        _numberFormat = format;
    }

    public String toString(Object object, ConverterContext context) {
        // format on userOjbect has a higher priority.
        try {
            if (context == null || context.getUserObject() == null || !(context.getUserObject() instanceof NumberFormat)) {
                return getNumberFormat().format(object);
            }
            else {
                NumberFormat format = (NumberFormat) context.getUserObject();
                return format.format(object);
            }
        }
        catch (IllegalArgumentException e) {
            return "";
        }
    }

    public boolean supportToString(Object object, ConverterContext context) {
        return true;
    }

    public void setNumberFormat(NumberFormat numberFormat) {
        _numberFormat = numberFormat;
    }

    /**
     * Gets the NumberFormat. Of setNumberFormat is never called, it will return
     * DecimalFormat.getInstance().
     *
     * @return the NumberFormat.
     */
    protected NumberFormat getNumberFormat() {
        if (_numberFormat == null) {
            _numberFormat = DecimalFormat.getInstance();
        }
        _numberFormat.setGroupingUsed(isGroupingUsed());
        return _numberFormat;
    }

    /**
     * Gets the NumberFormat for Locale.US.
     *
     * @return the NumberFormat for Locale.US.
     */
    protected NumberFormat getDefaultNumberFormat() {
        NumberFormat format = DecimalFormat.getInstance(Locale.US);
        format.setGroupingUsed(isGroupingUsed());
        return format;
    }

    /**
     * Parse the string as number. It will try using getNumberFormat first then try
     * getDefaultNumberFormat which is the US locale number format.
     *
     * @param string the string
     *
     * @return the Number. Null if the string is not a number.
     */
    protected Number parseNumber(String string) {
        Number number;
        try {
            number = getNumberFormat().parse(string);
        }
        catch (ParseException e) {
            try {
                number = getDefaultNumberFormat().parse(string);
            }
            catch (ParseException e1) {
                number = null;
            }
        }
        return number;
    }

    private static boolean _groupingUsed = true;

    /**
     * Gets flag if the grouping is used for the format.
     *
     * @return if the grouping is used.
     */
    public static boolean isGroupingUsed() {
        return _groupingUsed;
    }

    /**
     * Sets if the grouping will be used for the NumberFormat. We will call
     * NumberFormat#setGroupingUsed when we create the NumberFormat. Default is true. If you want to
     * call this method, please make sure calling it before ObjectConverterManager is used.
     *
     * @param groupingUsed true or false.
     */
    public static void setGroupingUsed(boolean groupingUsed) {
        _groupingUsed = groupingUsed;
    }
}
