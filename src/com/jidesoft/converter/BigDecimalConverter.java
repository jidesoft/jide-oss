package com.jidesoft.converter;

import java.math.BigDecimal;
import java.text.DecimalFormat;

/**
 * Converter for BigDecimal.
 */
public class BigDecimalConverter extends NumberFormatConverter {

    public BigDecimalConverter() {
        super(new DecimalFormat("#,##0.00"));
    }

    @Override
    public String toString(Object obj, ConverterContext convertercontext) {
        if (obj instanceof BigDecimal) {
            BigDecimal decimal = (BigDecimal) obj;
            if (decimal.doubleValue() == Double.NaN)
                return "";
            return super.toString(decimal, convertercontext);
        }
        return ""; // null or not an instance of BigDecimal
    }
}
