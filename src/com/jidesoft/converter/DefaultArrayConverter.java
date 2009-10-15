package com.jidesoft.converter;

import java.awt.*;
import java.lang.reflect.Array;

/**
 * Converts an array to a string and converts a string to an array.
 */
public class DefaultArrayConverter extends ArrayConverter {
    public DefaultArrayConverter(String separator, Class<?> elementClass) {
        super(separator, -1, elementClass);
    }

    public String toString(Object object, ConverterContext context) {
        if (object == null) {
            return "";
        }
        else {
            if (object.getClass().isArray()) {
                Object[] objects;
                if (getElementClass() == Object.class) {
                    objects = (Object[]) object;
                }
                else {
                    objects = new Object[Array.getLength(object)];
                }
                for (int i = 0; i < objects.length; i++) {
                    objects[i] = Array.get(object, i);
                }
                return arrayToString(objects, context);
            }
            else {
                return ObjectConverterManager.toString(object, getElementClass(), context);
            }
        }
    }

    public boolean supportToString(Object object, ConverterContext context) {
        return true;
    }

    public Object fromString(String string, ConverterContext context) {
        if (string == null || "".equals(string)) {
            return new Object[0];
        }
        else {
            Object[] objects = arrayFromString(string, context);
            if (objects == null) {
                return new Object[0];
            }
            Class<?> elementClass = getElementClass();
            if (elementClass == Object.class) {
                return objects;
            }
            for (Object object : objects) {
                if (!elementClass.isAssignableFrom(object.getClass())) {
                    return new Object[0];
                }
            }
            Object array = Array.newInstance(elementClass, objects.length);
            for (int i = 0; i < objects.length; i++) {
                Object object = objects[i];
                Array.set(array, i, object);
            }
            return array;
        }
    }

    public boolean supportFromString(String string, ConverterContext context) {
        return true;
    }

    public static void main(String[] args) {
        System.out.println(new DefaultArrayConverter(";", int.class).toString(new int[]{2, 3, 2, 4}, null));
        System.out.println(new DefaultArrayConverter(";", int.class).fromString("2;3;2;4", null));

        System.out.println(new DefaultArrayConverter(";", Color.class).toString(new Color[]{Color.RED, Color.YELLOW, Color.GREEN}, HexColorConverter.CONTEXT_HEX));
        System.out.println(new DefaultArrayConverter(";", Color.class).fromString("#FF0000;#FFFF00;#00FF00", HexColorConverter.CONTEXT_HEX));
        System.out.println(new DefaultArrayConverter(";", Object.class).fromString("#FF0000;#FFFF00;#00FF00", null));
    }
}
