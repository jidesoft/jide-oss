/*
 * @(#)AlphaColorConverter.java 4/19/2009
 *
 * Copyright 2002 - 2009 JIDE Software Inc. All rights reserved.
 *
 */

package com.jidesoft.converter;

import java.awt.*;
import java.util.StringTokenizer;

/**
 * Converts Color to/from "XXX, XXX, XXX (XXX)" format. For example "0, 0, 0 (255)" is Color(0, 0, 0, 255) and "255, 0, 255" is
 * Color(255, 0, 255).
 */
public class AlphaColorConverter extends ColorConverter {

    /**
     * Creates a AlphaColorConverter.
     */
    public AlphaColorConverter() {
    }

    public String toString(Object object, ConverterContext context) {
        if (object instanceof Color) {
            Color color = (Color) object;
            StringBuffer colorText = new StringBuffer();
            colorText.append(color.getRed()).append(", ");
            colorText.append(color.getGreen()).append(", ");
            colorText.append(color.getBlue()).append(", ");
            colorText.append(color.getAlpha());
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
        if (token.hasMoreTokens()) {
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