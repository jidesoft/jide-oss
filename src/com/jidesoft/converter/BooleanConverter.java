/*
 * @(#) BooleanConverter.java
 *
 * Copyright 2002 - 2003 JIDE Software. All rights reserved.
 */
package com.jidesoft.converter;

import java.util.Locale;

/**
 * Converter which converts Boolean to String and converts it back.
 */
public class BooleanConverter implements ObjectConverter {

    public BooleanConverter() {
    }

    public String toString(Object object, ConverterContext context) {
        if (Boolean.FALSE.equals(object)) {
            return getFalse();
        }
        else if (Boolean.TRUE.equals(object)) {
            return getTrue();
        }
        else {
            return "";
        }
    }

    public boolean supportToString(Object object, ConverterContext context) {
        return true;
    }

    public Object fromString(String string, ConverterContext context) {
        if (string.equalsIgnoreCase(getTrue())) {
            return Boolean.TRUE;
        }
        else
        if (string.equalsIgnoreCase("true")) { // in case the application runs under different locale, we still consider "true" is true.
            return Boolean.TRUE;
        }
        else if (string.equalsIgnoreCase(getFalse())) {
            return Boolean.FALSE;
        }
        else
        if (string.equalsIgnoreCase("false")) { // in case the application runs under different locale, we still consider "false" is false.
            return Boolean.FALSE;
        }
        else {
            return null;
        }
    }

    public boolean supportFromString(String string, ConverterContext context) {
        return true;
    }

    private String getTrue() {
        String s = Resource.getResourceBundle(Locale.getDefault()).getString("Boolean.true");
        return s != null ? s.trim() : s;
    }

    private String getFalse() {
        String s = Resource.getResourceBundle(Locale.getDefault()).getString("Boolean.false");
        return s != null ? s.trim() : s;
    }

}
