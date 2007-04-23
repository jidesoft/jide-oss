/*
 * @(#)CurrencyConverter.java 5/9/2006
 *
 * Copyright 2002 - 2006 JIDE Software Inc. All rights reserved.
 */

package com.jidesoft.converter;

import java.text.NumberFormat;

/**
 * Converter which converts currency to String and converts it back.
 */
public class CurrencyConverter extends NumberFormatConverter {
    public static ConverterContext CONTEXT = new ConverterContext("Currency");

    public CurrencyConverter() {
        this(NumberFormat.getCurrencyInstance());
    }

    public CurrencyConverter(NumberFormat format) {
        super(format);
    }
}
