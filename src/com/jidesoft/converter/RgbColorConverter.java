/*
 * @(#) RgbColorConverter.java
 *
 * Copyright 2002 - 2003 JIDE Software. All rights reserved.
 */
package com.jidesoft.converter;

import java.awt.*;
import java.util.StringTokenizer;

/**
 * If alpha value is not included, converts Color to/from "XXX, XXX, XXX" format. For example "0, 0, 0" is Color(0, 0,
 * 0) and "255, 0, 255" is Color(255, 0, 255).
 * <p/>
 * If alpha value is included, converts Color to/from "XXX, XXX, XXX, XXX" format. For example "0, 0, 0, 255" is
 * Color(0, 0, 0, 255) and "255, 0, 255, 100" is Color(255, 0, 255, 100).
 */
public class RgbColorConverter extends ColorConverter {

    private boolean _alphaIncluded = false;

    /**
     * Creates a RgbColorConverter. This is the default constructor and will not include alpha value.
     */
    public RgbColorConverter() {
    }

    /**
     * Creates a RgbColorConverter. With this constructor, you can create a converter with alpha value included.
     *
     * @param alphaIncluded the flag if alpha value will be included in this converter
     */
    public RgbColorConverter(boolean alphaIncluded) {
        _alphaIncluded = alphaIncluded;
    }

    /**
     * Get the flag if this converter should consider alpha value.
     * <p/>
     * If you use default constructor, the default value of this flag is false.
     * <p/>
     *
     * @return true if this converter should consider alpha value.
     *
     * @see RgbColorConverter
     */
    public boolean isAlphaIncluded() {
        return _alphaIncluded;
    }

    /**
     * Set the flag if this converter should consider alpha value.
     * <p/>
     *
     * @param alphaIncluded the flag if this converter should consider alpha value.
     * @see #isAlphaIncluded()
     */
    public void setAlphaIncluded(boolean alphaIncluded) {
        _alphaIncluded = alphaIncluded;
    }

    public String toString(Object object, ConverterContext context) {
        if (object instanceof Color) {
            Color color = (Color) object;
            StringBuffer colorText = new StringBuffer();
            colorText.append(color.getRed()).append(", ");
            colorText.append(color.getGreen()).append(", ");
            colorText.append(color.getBlue());
            if (isAlphaIncluded()) {
                colorText.append(", ").append(color.getAlpha());
            }
            return new String(colorText);
        }
        else {
            return "";
        }
    }

    public boolean supportToString(Object object, ConverterContext context) {
        return true;
    }

    public boolean supportFromString(String string, ConverterContext context) {
        return true;
    }

    public Object fromString(String string, ConverterContext context) {
        if (string == null || string.trim().length() == 0) {
            return null;
        }
        StringTokenizer token = new StringTokenizer(string, ",; ");
        int r = 0, g = 0, b = 0, a = 255;
        if (token.hasMoreTokens()) {
            String s = token.nextToken();
            try {
                r = Integer.parseInt(s, 10) % 256;
            }
            catch (NumberFormatException e) {
                // ignore
            }
        }
        if (token.hasMoreTokens()) {
            String s = token.nextToken();
            try {
                g = Integer.parseInt(s, 10) % 256;
            }
            catch (NumberFormatException e) {
                // ignore
            }
        }
        if (token.hasMoreTokens()) {
            String s = token.nextToken();
            try {
                b = Integer.parseInt(s, 10) % 256;
            }
            catch (NumberFormatException e) {
                // ignore
            }
        }
        if (isAlphaIncluded() && token.hasMoreTokens()) {
            String s = token.nextToken();
            try {
                a = Integer.parseInt(s, 10) % 256;
            }
            catch (NumberFormatException e) {
                // ignore
            }
        }

        return new Color(r, g, b, a);
    }
}
