/*
 * @(#)BigDecimalRange.java 5/22/2014
 *
 * Copyright 2002 - 2014 JIDE Software Inc. All rights reserved.
 */

package com.jidesoft.range;

import java.math.BigDecimal;

/**
 * Specifies upper and lower bounds for a range of values
 */
public class BigDecimalRange extends AbstractRange<BigDecimal> {
    protected BigDecimal _min;
    protected BigDecimal _max;
    private static final BigDecimal TWO = new BigDecimal(2);

    /**
     * Creates a numeric range with minimum 0.0 and maximum of 1.0
     */
    public BigDecimalRange() {
        this(BigDecimal.ZERO, BigDecimal.ONE);
    }

    /**
     * Create a numeric range by supplying minimum and maximum values
     *
     * @param min the minimum
     * @param max the maximum
     */
    public BigDecimalRange(BigDecimal min, BigDecimal max) {
        super();
        boolean inOrder = min.compareTo(max) < 0;
        _min = inOrder ? min : max;
        _max = inOrder ? max : min;
    }

    /**
     * Creates a copy of the supplied BigDecimalRange
     *
     * @param BigDecimalRange the BigDecimalRange instance to copy
     */
    public BigDecimalRange(BigDecimalRange BigDecimalRange) {
        this(BigDecimalRange.getMin(), BigDecimalRange.getMax());
    }

    @Override
    public Range<BigDecimal> copy() {
        return new BigDecimalRange(this);
    }

    /**
     * @return the minimum value
     */
    public double minimum() {
        return _min.doubleValue();
    }

    /**
     * @return the maximum value
     */
    public double maximum() {
        return _max.doubleValue();
    }

    /**
     * @return the minimum value
     */
    public BigDecimal getMin() {
        return _min;
    }

    /**
     * Sets the minimum value
     *
     * @param min the new minimum value.
     */
    public void setMin(BigDecimal min) {
        BigDecimal old = _min;
        if (old != null && old.equals(min)) {
            return;
        }
        assert min.compareTo(_max) <= 0;
        _min = min;
        firePropertyChange(PROPERTY_MIN, old, min);
    }

    /**
     * @return the maximum value
     */
    public BigDecimal getMax() {
        return _max;
    }

    /**
     * Sets the maximum value
     *
     * @param max the new maximum value.
     */
    public void setMax(BigDecimal max) {
        BigDecimal old = _max;
        if (old != null && old.equals(max)) {
            return;
        }
        assert max.compareTo(_min) >= 0;
        _max = max;
        firePropertyChange(PROPERTY_MAX, old, max);
    }

    /**
     * @return the size of the range
     */
    public double size() {
        return _max.subtract(_min).doubleValue();
    }

    /**
     * @return the minimum() value for the range
     */
    public BigDecimal lower() {
        return getMin();
    }

    @Override
    public void adjust(BigDecimal lower, BigDecimal upper) {
        double size = size(); // save it
        if (lower != null) {
            setMin(lower);
        }
        else if (upper != null) {
            setMin(upper.add(new BigDecimal(size)));
        }
        if (upper != null) {
            setMax(upper);
        }
        else if (lower != null) {
            setMin(lower.add(new BigDecimal(size)));
        }
    }

    /**
     * @return the maximum() value for the range
     */
    public BigDecimal upper() {
        return getMax();
    }

    @Override
    public boolean contains(BigDecimal x) {
        return x != null && x.compareTo(_min) >= 0 && x.compareTo(_max) <= 0;
    }

    /**
     * Creates a new BigDecimalRange by enlarging this numeric range about its mid-point. For example to make it 10%
     * bigger, use a stretch factor of 1.1. Note that this method can also be used to shrink a BigDecimalRange.
     *
     * @param stretchFactor the multiplication factor for the enlargement
     * @return a new BigDecimalRange
     */
    public BigDecimalRange stretch(double stretchFactor) {
        return stretch(stretchFactor, stretchFactor);
    }

    /**
     * Creates a new BigDecimalRange by enlarging this numeric range about its mid-point. For example to make it 10%
     * bigger, use a stretch factor of 1.1. Note that this method can also be used to shrink a BigDecimalRange.
     *
     * @param stretchFactorForLower the multiplication factor for the enlargement for the lower range
     * @param stretchFactorForUpper the multiplication factor for the enlargement for the upper range
     * @return a new BigDecimalRange
     */
    public BigDecimalRange stretch(double stretchFactorForLower, double stretchFactorForUpper) {
        BigDecimal mid = _max.add(_min).divide(TWO);
        BigDecimal halfSize = _max.subtract(_min).divide(TWO);
        return new BigDecimalRange(mid.subtract(halfSize.multiply(new BigDecimal(stretchFactorForLower))), mid.add(halfSize.multiply(new BigDecimal(stretchFactorForUpper))));
    }


    @Override
    public Range<BigDecimal> createIntermediate(Range<BigDecimal> target, double position) {
        throw new UnsupportedOperationException("createIntermediate method is not currently support in BigDecimalRange");
    }

    /**
     * Test for equality based on the values of min and max
     */
    @Override
    public boolean equals(Object other) {
        if (other instanceof BigDecimalRange) {
            BigDecimalRange otherRange = (BigDecimalRange) other;
            return _min.equals(otherRange._min) && _max.equals(otherRange._max);
        }
        else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return _max.multiply(new BigDecimal(29)).add(_min).intValue();
    }

    @Override
    public String toString() {
        return String.format("#<BigDecimalRange min=%f max=%f>", _min, _max);
    }
}
