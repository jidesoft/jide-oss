package com.jidesoft.utils;

/**
 * Utils methods for data type.
 */
public class TypeUtils {
    // indexes referring to columns in the PRIMITIVE_ARRAY_TYPES table.
    private static final int WRAPPER_TYPE_INDEX = 0;
    private static final int PRIMITIVE_TYPE_INDEX = 1;
    private static final int PRIMITIVE_TYPE_KEY_INDEX = 2;

    private static final Object[][] PRIMITIVE_ARRAY_TYPES = {
            {Boolean.class, boolean.class, "Z"},
            {Character.class, char.class, "C"},
            {Byte.class, byte.class, "B"},
            {Short.class, short.class, "S"},
            {Integer.class, int.class, "I"},
            {Long.class, long.class, "J"},
            {Float.class, float.class, "F"},
            {Double.class, double.class, "D"}
    };

    public static boolean isPrimitive(Class<?> primitive) {
        for (Object[] primitiveArrayType : PRIMITIVE_ARRAY_TYPES) {
            if (primitiveArrayType[PRIMITIVE_TYPE_INDEX] == primitive) {
                return true;
            }
        }
        return false;
    }

    public static boolean isPrimitiveWrapper(Class<?> wrapperType) {
        for (Object[] primitiveArrayType : PRIMITIVE_ARRAY_TYPES) {
            if (primitiveArrayType[WRAPPER_TYPE_INDEX] == wrapperType) {
                return true;
            }
        }
        return false;
    }

    public static Class<?> convertPrimitiveToWrapperType(Class<?> primitive) {
        for (Object[] primitiveArrayType : PRIMITIVE_ARRAY_TYPES) {
            if (primitiveArrayType[PRIMITIVE_TYPE_INDEX] == primitive) {
                return (Class<?>) primitiveArrayType[WRAPPER_TYPE_INDEX];
            }
        }
        return primitive;
    }

    public static Class<?> convertWrapperToPrimitiveType(Class<?> primitive) {
        for (Object[] primitiveArrayType : PRIMITIVE_ARRAY_TYPES) {
            if (primitiveArrayType[WRAPPER_TYPE_INDEX] == primitive) {
                return (Class<?>) primitiveArrayType[PRIMITIVE_TYPE_INDEX];
            }
        }
        return primitive;
    }
}
