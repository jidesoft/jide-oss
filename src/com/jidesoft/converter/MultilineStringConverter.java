/*
 * @(#)MultilineStringConverter.java 10/24/2008
 *
 * Copyright 2002 - 2008 JIDE Software Inc. All rights reserved.
 *
 */
package com.jidesoft.converter;

/**
 * Converter which converts a String with new lines to String and convert the new lines to "\n" so that it can be
 * displayed in the UI.
 */
public class MultilineStringConverter implements ObjectConverter {
    public static final ConverterContext CONTEXT = new ConverterContext("MultilineString");

    public String toString(Object object, ConverterContext context) {
        if (object instanceof String) {
            return ((String) object).replaceAll("\n", "\\\\n").replaceAll("\r", "\\\\r");
        }
        else if (object == null) {
            return "";
        }
        else {
            return "" + object;
        }
    }

    public boolean supportToString(Object object, ConverterContext context) {
        return true;
    }

    public Object fromString(String string, ConverterContext context) {
        if (string != null) {
            return string.replaceAll("\\\\n", "\n").replaceAll("\\\\r", "\r");
        }
        else {
            return null;
        }
    }

    public boolean supportFromString(String string, ConverterContext context) {
        return true;
    }
}