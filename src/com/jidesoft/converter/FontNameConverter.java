/*
 * @(#) FontNameConverter.java
 *
 * Copyright 2002 - 2003 JIDE Software. All rights reserved.
 */
package com.jidesoft.converter;

import java.awt.*;

/**
 * Converter which converts Font Name String to String and converts it back. It's almost the same as StringConverter
 * except if user types in a string which cannot find in font list on your computer, it will return null in fromString
 * and return "" in toString.
 */
public class FontNameConverter implements ObjectConverter {
    /**
     * ConverterContext for a font name.
     */
    public static ConverterContext CONTEXT = new ConverterContext("FontName");

    public String toString(Object object, ConverterContext context) {
        if (object == null) {
            return "";
        }
        else {
            String[] fontNames = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
            for (String fontName : fontNames) { // check font if it is available
                if (fontName.equals(object)) {
                    return fontName;
                }
            }
            return "";
        }
    }

    public boolean supportToString(Object object, ConverterContext context) {
        return true;
    }

    public Object fromString(String string, ConverterContext context) {
        if (string.length() == 0) {
            return null;
        }
        else {
            String[] fontNames = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
            for (String fontName : fontNames) { // check font if it is available
                if (fontName.equals(string)) {
                    return string;
                }
            }
            return null;
        }
    }

    public boolean supportFromString(String string, ConverterContext context) {
        return true;
    }
}
