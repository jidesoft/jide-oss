/*
 * @(#) IntegerConverter.java
 *
 * Copyright 2002 - 2003 JIDE Software. All rights reserved.
 */
package com.jidesoft.converter;

import com.jidesoft.utils.SystemInfo;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Currency;
import java.util.Locale;

/**
 * Converter which converts Number to String and converts it back. You can pass in a NumberFormat as UserObject of
 * ConverterContext if you want to control the format of the number such as maximum decimal point etc.
 */
abstract public class NumberConverter implements ObjectConverter {
    private NumberFormat _numberFormat;
    private boolean _avoidNegativeZero;

    public static final ConverterContext CONTEXT_FRACTION_NUMBER = new ConverterContext("Fraction Number");

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
        if (_numberFormat != null) {
            _numberFormat.setGroupingUsed(isGroupingUsed()); // apply the grouingUsed flag.
        }
    }

    private String trimNegativeSign(String text) {
        if (text.indexOf('-') != 0) {
            return text;
        }
        for (char ch = '1'; ch <= '9'; ch++) {
            if (text.indexOf(ch) >= 0) {
                return text;
            }
        }
        for (char ch = 'a'; ch <= 'f'; ch++) {
            if (text.indexOf(ch) >= 0) {
                return text;
            }
        }
        for (char ch = 'A'; ch <= 'F'; ch++) {
            if (text.indexOf(ch) >= 0) {
                return text;
            }
        }
        if (text.indexOf('0') < 0) {
            return text;
        }
        return text.substring(1);
    }

    public String toString(Object object, ConverterContext context) {
        // format on userObject has a higher priority.
        try {
            if (context == null || context.getUserObject() == null || !(context.getUserObject() instanceof NumberFormat)) {
                if (object instanceof Number && ((Number) object).doubleValue() == Double.NaN) {
                    return "";
                }
                else {
                    String text = getNumberFormat().format(object);
                    if (isAvoidNegativeZero()) {
                        text = trimNegativeSign(text);
                    }
                    return text;
                }
            }
            else {
                NumberFormat format = (NumberFormat) context.getUserObject();
                String text = format.format(object);
                if (isAvoidNegativeZero()) {
                    text = trimNegativeSign(text);
                }
                return text;
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
     * Gets the NumberFormat. Of setNumberFormat is never called, it will return DecimalFormat.getInstance().
     *
     * @return the NumberFormat.
     */
    protected NumberFormat getNumberFormat() {
        if (_numberFormat == null) {
            _numberFormat = DecimalFormat.getInstance();
            _numberFormat.setGroupingUsed(isGroupingUsed());
        }
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
     * Parse the string as number. It will try using getNumberFormat first then try getDefaultNumberFormat which is the
     * US locale number format.
     *
     * @param string the string
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
     * Sets if the grouping will be used for the NumberFormat. We will call NumberFormat#setGroupingUsed when we create
     * the NumberFormat. Default is true. If you want to call this method, please make sure calling it before
     * ObjectConverterManager is used.
     *
     * @param groupingUsed true or false.
     */
    public static void setGroupingUsed(boolean groupingUsed) {
        _groupingUsed = groupingUsed;
    }

    /**
     * Set the fraction digits of this converter.
     *
     * @param minDigits minimum fraction digits
     * @param maxDigits maximum fraction digits
     */
    public void setFractionDigits(int minDigits, int maxDigits) {
        NumberFormat numberFormat = getNumberFormat();
        numberFormat.setMinimumFractionDigits(minDigits);
        numberFormat.setMaximumFractionDigits(maxDigits);
    }

    /**
     * Set the currency of this converter.
     *
     * @param currency currency
     */
    public void setCurrency(Currency currency) {
        NumberFormat numberFormat = getNumberFormat();
        numberFormat.setCurrency(currency);
    }

    /**
     * Set the integer digits of this converter.
     *
     * @param minDigits minimum integer digits
     * @param maxDigits maximum integer digits
     */
    public void setIntegerDigits(int minDigits, int maxDigits) {
        NumberFormat numberFormat = getNumberFormat();
        numberFormat.setMinimumIntegerDigits(minDigits);
        numberFormat.setMaximumIntegerDigits(maxDigits);
    }

    /**
     * Set the rounding mode of this converter.
     *
     * @param mode rounding mode
     */
    public void setRoundingMode(RoundingMode mode) {
        if (SystemInfo.isJdk6Above()) {
            NumberFormat numberFormat = getNumberFormat();
            numberFormat.setRoundingMode(mode);
        }
    }

    /**
     * Gets the flag indicating if negative zero should be avoided.
     *
     * @return true if negative zero should be avoided. Otherwise false.
     * @see #setAvoidNegativeZero(boolean)
     * @since 3.5.1
     */
    public boolean isAvoidNegativeZero() {
        return _avoidNegativeZero;
    }

    /**
     * Sets the flag indicating if negative zero should be avoided.
     * <p/>
     * By default, the value is false to keep backward compatibility. If you don't like the string like "-0.00", please
     * set this flag to true to remove the negative sign.
     *
     * @param avoidNegativeZero the flag
     * @since 3.5.1
     */
    public void setAvoidNegativeZero(boolean avoidNegativeZero) {
        _avoidNegativeZero = avoidNegativeZero;
    }
}
