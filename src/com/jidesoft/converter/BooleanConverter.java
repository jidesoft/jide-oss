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

    BooleanConverter() {
    }

    public String toString(Object object, ConverterContext context) {
        if (Boolean.FALSE.equals(object)) {
            return Resource.getResourceBundle(Locale.getDefault()).getString("Boolean.false");
        }
        else if (Boolean.TRUE.equals(object)) {
            return Resource.getResourceBundle(Locale.getDefault()).getString("Boolean.true");
        }
        else {
            return "";
        }
    }

    public boolean supportToString(Object object, ConverterContext context) {
        return true;
    }

    public Object fromString(String string, ConverterContext context) {
        if (string.equalsIgnoreCase(Resource.getResourceBundle(Locale.getDefault()).getString("Boolean.true"))) {
            return Boolean.TRUE;
        }
        else
        if (string.equalsIgnoreCase("true")) { // in case the application runs under different locale, we still condier "true" is true.
            return Boolean.TRUE;
        }
        else if (string.equalsIgnoreCase(Resource.getResourceBundle(Locale.getDefault()).getString("Boolean.false"))) {
            return Boolean.FALSE;
        }
        else
        if (string.equalsIgnoreCase("false")) { // in case the application runs under different locale, we still condier "false" is false.
            return Boolean.FALSE;
        }
        else {
            return null;
        }
    }

    public boolean supportFromString(String string, ConverterContext context) {
        return true;
    }
}
