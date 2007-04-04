/*
 * @(#)CurrencyConverter.java 5/9/2006
 *
 * Copyright 2002 - 2006 JIDE Software Inc. All rights reserved.
 */

package com.jidesoft.converter;

import java.text.NumberFormat;
import java.text.ParseException;

/**
 * Converter which converts currency to String and converts it back.
 */
public class CurrencyConverter extends NumberConverter {
    public static ConverterContext CONTEXT = new ConverterContext("Currency");

    public CurrencyConverter() {
        this(NumberFormat.getCurrencyInstance());
    }

    public CurrencyConverter(NumberFormat format) {
        super(format);
    }

    public Object fromString(String string, ConverterContext context) {
        try {
            return getNumberFormat().parse(string);
        }
        catch (ParseException e) {
            return null;
        }
    }

    public boolean supportFromString(String string, ConverterContext context) {
        return true;
    }
}
