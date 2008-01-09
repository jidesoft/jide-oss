/*
 * @(#)ShortConverter.java 3/9/2005
 *
 * Copyright 2002 - 2005 JIDE Software Inc. All rights reserved.
 */
package com.jidesoft.converter;

import java.text.DecimalFormat;
import java.text.NumberFormat;


/**
 * Converter which converts Short to String and converts it back.
 */
public class ShortConverter extends NumberConverter {
    public ShortConverter() {
        this(DecimalFormat.getIntegerInstance());
    }

    public ShortConverter(NumberFormat format) {
        super(format);
    }

    public Object fromString(String string, ConverterContext context) {
        Number number = parseNumber(string);
        return number != null ? number.shortValue() : null;
    }

    public boolean supportFromString(String string, ConverterContext context) {
        return true;
    }
}
