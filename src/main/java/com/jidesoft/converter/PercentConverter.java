/*
 * @(#)PercentConverter.java 4/10/2007
 *
 * Copyright 2002 - 2007 JIDE Software Inc. All rights reserved.
 */

package com.jidesoft.converter;

import java.text.NumberFormat;
import java.util.Locale;

/**
 * Converter which converts percentage to String and converts it back.
 */
public class PercentConverter extends NumberFormatConverter {
    public static ConverterContext CONTEXT = new ConverterContext("Percent");

    public PercentConverter() {
        this(NumberFormat.getPercentInstance(Locale.getDefault()));
    }

    public PercentConverter(NumberFormat format) {
        super(format);
    }

    @Override
    public Object fromString(String string, ConverterContext context) {
        Object o = super.fromString(string, context);
        if (string != null && !string.trim().endsWith("%") && o instanceof Number && ((Number) o).doubleValue() > 1) {
            o = ((Number) o).doubleValue() / 100;
        }
        return o;
    }
}
