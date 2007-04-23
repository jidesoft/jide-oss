/*
 * @(#) HexColorConverter.java
 *
 * Copyright 2002 - 2003 JIDE Software. All rights reserved.
 */
package com.jidesoft.converter;

import java.awt.*;

/**
 * Converts Color to/from #xxxxxx format. For example #000000 is Color(0, 0, 0)
 * and #FF00FF is Color(255, 0, 255).
 */
public class HexColorConverter extends ColorConverter {
    /**
     * Creates a HexColorConverter.
     */
    public HexColorConverter() {
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
        if (string.length() > 6) {
            string = string.substring(string.length() - 6);
        }
        int value = 0;
        try {
            value = Integer.parseInt(string, 16);
        }
        catch (NumberFormatException e) {
            return null;
        }
        return new Color(value);
    }
}
