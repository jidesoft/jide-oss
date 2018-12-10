/*
 * @(#)TestNumberComparator.java 9/2/2008
 *
 * Copyright 2002 - 2008 JIDE Software Inc. All rights reserved.
 */

package com.jidesoft.comparator;

import junit.framework.TestCase;

import java.util.Arrays;

public class TestNumberComparator extends TestCase {
    public static Double[] DOUBLE_VALUES = {1.3, 13.4, 5.3, 0.2, -3.3, -17.5, 9.3};

    public void testDouble() {
        Double[] values = DOUBLE_VALUES.clone();
        Arrays.sort(values, new NumberComparator());
        assertEquals(-17.5, values[0]);
        assertEquals(13.4, values[values.length - 1]);

        NumberComparator numberComparator = new NumberComparator();
        numberComparator.setAbsolute(true);
        values = DOUBLE_VALUES.clone();
        Arrays.sort(values, numberComparator);
        assertEquals(0.2, values[0]);
        assertEquals(-17.5, values[values.length - 1]);
    }

    public static Byte[] BYTE_VALUES = {3, 4, 6, 0, -3, -7, 4};

    public void testByte() {
        Byte[] values = BYTE_VALUES.clone();
        Arrays.sort(values, new NumberComparator());
        assertEquals(new Byte((byte) -7), values[0]);
        assertEquals(new Byte((byte) 6), values[values.length - 1]);

        NumberComparator numberComparator = new NumberComparator();
        numberComparator.setAbsolute(true);
        values = BYTE_VALUES.clone();
        Arrays.sort(values, numberComparator);
        assertEquals(new Byte((byte) 0), values[0]);
        assertEquals(new Byte((byte) -7), values[values.length - 1]);
    }
}
