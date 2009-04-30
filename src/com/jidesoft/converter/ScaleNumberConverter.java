/*
 * @(#)ScaleNumberConverter.java 4/30/2009
 *
 * Copyright 2002 - 2009 JIDE Software Inc. All rights reserved.
 *
 */

package com.jidesoft.converter;

import java.text.NumberFormat;

/**
 * Converter which converts number with defined scale to String and converts it back.
 */
public class ScaleNumberConverter extends DoubleConverter {
    /**
     * Default ConverterContext for ScaleNumberConverter.
     */
    public static ConverterContext CONTEXT = new ConverterContext("Scale Number");
    private int _scale;

    /**
     * Default constructor of ScaleNumberConverter. Default scale is 2.
     */
    public ScaleNumberConverter() {
        this(2);
    }

    /**
     * Constructor of ScaleNumberConverter with scale as its parameter.
     * @param scale scale
     */
    public ScaleNumberConverter(int scale) {
        super();
        setScale(scale);
    }

    /**
     * Get the scale of this converter.
     * @return scale.
     */
    public int getScale() {
        return _scale;
    }

    /**
     * Set the scale of this converter.
     * @param scale scale
     */
    public void setScale(int scale) {
        _scale = scale;

        NumberFormat format = getNumberFormat();
        if (format == null) {
            format = NumberFormat.getInstance();
        }
        format.setMinimumFractionDigits(scale);
        format.setMaximumFractionDigits(scale);
    }
}
