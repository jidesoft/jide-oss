/*
 * @(#)NaturalNumberConverter.java 3/9/2005
 *
 * Copyright 2002 - 2005 JIDE Software Inc. All rights reserved.
 */
package com.jidesoft.converter;

import java.text.DecimalFormat;
import java.text.NumberFormat;


/**
 * Converter which converts Integer to String and converts it back.
 */
public class NaturalNumberConverter extends NumberConverter {
    /**
     * Default ConverterContext for NaturalNumberConverter.
     */
    public static ConverterContext CONTEXT = new ConverterContext("Natural Nunber");

    public NaturalNumberConverter() {
        this(DecimalFormat.getIntegerInstance());
    }

    public NaturalNumberConverter(NumberFormat format) {
        super(format);
    }

    public Object fromString(String string, ConverterContext context) {
        Number number = parseNumber(string);
        return number != null ? (number.intValue() < 0 ? 0 : number.intValue()) : null;
    }

    public boolean supportFromString(String string, ConverterContext context) {
        return true;
    }
}
