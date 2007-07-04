/*
 * @(#) RectangleConverter.java
 *
 * Copyright 2002 - 2003 JIDE Software. All rights reserved.
 */
package com.jidesoft.converter;

import java.awt.*;

/**
 * Converter which converts Rectangle to String and converts it back.
 */
public class RectangleConverter extends ArrayConverter {
    RectangleConverter() {
        super("; ", 4, Integer.class);
    }

    public String toString(Object object, ConverterContext context) {
        if (object instanceof Rectangle) {
            Rectangle rectangle = (Rectangle) object;
            return arrayToString(new Object[]{
                    rectangle.x, rectangle.y, rectangle.width, rectangle.height
            }, context);
        }
        else {
            return "";
        }
    }

    public boolean supportToString(Object object, ConverterContext context) {
        return true;
    }

    public Object fromString(String string, ConverterContext context) {
        if (string == null || string.trim().length() == 0) {
            return null;
        }
        Object[] objects = arrayFromString(string, context);
        int x = 0, y = 0, w = 0, h = 0;
        if (objects.length >= 1 && objects[0] instanceof Integer) {
            x = (Integer) objects[0];
        }
        if (objects.length >= 2 && objects[1] instanceof Integer) {
            y = (Integer) objects[1];
        }
        if (objects.length >= 3 && objects[2] instanceof Integer) {
            w = (Integer) objects[2];
        }
        if (objects.length >= 4 && objects[3] instanceof Integer) {
            h = (Integer) objects[3];
        }
        return new Rectangle(x, y, w, h);
    }

    public boolean supportFromString(String string, ConverterContext context) {
        return true;
    }
}
