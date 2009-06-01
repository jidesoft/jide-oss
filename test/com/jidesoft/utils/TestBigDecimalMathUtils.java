package com.jidesoft.utils;

import junit.framework.TestCase;

import java.math.BigDecimal;

public class TestBigDecimalMathUtils extends TestCase {
    public void testPerformRoot() {
        BigDecimal dec = new BigDecimal("25029.33333");
        assertEquals(new BigDecimal("158.20662"), round(BigDecimalMathUtils.sqrt(dec), 5));

        dec = new BigDecimal("36");
        assertEquals(6, BigDecimalMathUtils.sqrt(dec).longValue());

        dec = new BigDecimal("770884");
        assertEquals(878, BigDecimalMathUtils.sqrt(dec).longValue());
    }

    public static BigDecimal round(BigDecimal decimal, int decimalDigits) {
        BigDecimal scale = new BigDecimal(Math.pow(10, decimalDigits));
        return new BigDecimal(Math.round(decimal.multiply(scale).doubleValue())).divide(scale);
    }
}
