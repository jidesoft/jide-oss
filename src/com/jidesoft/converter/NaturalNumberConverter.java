/*
 * @(#)NaturalNumberConverter.java 3/9/2005
 *
 * Copyright 2002 - 2005 JIDE Software Inc. All rights reserved.
 */
package com.jidesoft.converter;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;


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
        try {
            int value = getNumberFormat().parse(string).intValue();
            if (value < 0) {
                return 0;
            }
            else {
                return Integer.parseInt(string);
            }
        }
        catch (ParseException e) {
            return null;
        }
    }

    public boolean supportFromString(String string, ConverterContext context) {
        return true;
    }
}
