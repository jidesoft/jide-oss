/*
 * @(#) HexColorConverter.java
 *
 * Copyright 2002 - 2003 JIDE Software. All rights reserved.
 */
package com.jidesoft.converter;

import java.awt.*;

/**
 * If alpha value is not included, converts Color to/from #xxxxxx format. For example #000000 is Color(0, 0, 0) and
 * #FF00FF is Color(255, 0, 255). If alpha value is included, converts Color to/from #xxxxxxxx format. For example
 * #FF000000 is Color(0, 0, 0, 255) and #64FF00FF is Color(255, 0, 255, 100).
 */
public class HexColorConverter extends ColorConverter {

    private boolean _alphaIncluded = false;

    /**
     * Creates a HexColorConverter. This is the default constructor and will not include alpha value.
     */
    public HexColorConverter() {
    }

    /**
     * Creates a HexColorConverter. With this constructor, you can create a converter with alpha value included.
     *
     * @param alphaIncluded the flag if alpha value will be included in this converter
     */
    public HexColorConverter(boolean alphaIncluded) {
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
     * @see HexColorConverter
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

    protected String getHexString(int color) {
        String value = Integer.toHexString(color).toUpperCase();
        if (value.length() == 1) {
            value = "0" + value;
        }
        return value;
    }

    public String toString(Object object, ConverterContext context) {
        if (object instanceof Color) {
            Color color = (Color) object;
            StringBuffer colorText = new StringBuffer("#");
            if (isAlphaIncluded()) {
                colorText.append(getHexString(color.getAlpha()));
            }
            colorText.append(getHexString(color.getRed()));
            colorText.append(getHexString(color.getGreen()));
            colorText.append(getHexString(color.getBlue()));
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
        if (string.startsWith("#")) {
            string = string.substring(1);
        }
        if (isAlphaIncluded()) {
            if (string.length() > 8) {
                string = string.substring(string.length() - 8);
            }
        }
        else {
            if (string.length() > 6) {
                string = string.substring(string.length() - 6);
            }
        }
        long value;
        try {
            value = Long.parseLong(string, 16);
        }
        catch (NumberFormatException e) {
            return null;
        }
        return new Color((int) value, isAlphaIncluded());
    }
}
