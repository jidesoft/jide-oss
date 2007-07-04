/*
 * @(#)InsetsConverter.java 4/12/2005
 *
 * Copyright 2002 - 2005 JIDE Software Inc. All rights reserved.
 */
package com.jidesoft.converter;

import java.awt.*;

/**
 * Converter which converts Insets to String and converts it back.
 */
public class InsetsConverter extends ArrayConverter {
    InsetsConverter() {
        super("; ", 4, Integer.class);
    }

    public String toString(Object object, ConverterContext context) {
        if (object instanceof Insets) {
            Insets Insets = (Insets) object;
            return arrayToString(new Object[]{
                    Insets.top, Insets.left, Insets.bottom, Insets.right
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
        int top = 0, left = 0, bottom = 0, right = 0;
        if (objects.length >= 1 && objects[0] instanceof Integer) {
            top = (Integer) objects[0];
        }
        if (objects.length >= 2 && objects[1] instanceof Integer) {
            left = (Integer) objects[1];
        }
        if (objects.length >= 3 && objects[2] instanceof Integer) {
            bottom = (Integer) objects[2];
        }
        if (objects.length >= 4 && objects[3] instanceof Integer) {
            right = (Integer) objects[3];
        }
        return new Insets(top, left, bottom, right);
    }

    public boolean supportFromString(String string, ConverterContext context) {
        return true;
    }
}
