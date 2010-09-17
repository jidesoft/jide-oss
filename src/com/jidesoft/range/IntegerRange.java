/*
 * @(#)IntegerRange.java 9/5/2009
 *
 * Copyright 2005 - 2009 Catalysoft Ltd. All rights reserved.
 */

package com.jidesoft.range;

/**
 * Specifies upper and lower bounds for a range of values
 *
 * @author Simon
 */
public class IntegerRange extends AbstractNumericRange<Integer> {
    protected int _min;
    protected int _max;

    /**
     * Creates a numeric range with minimum 0.0 and maximum of 1.0
     */
    public IntegerRange() {
        this(0, 1);
    }

    /**
     * Create a numeric range by supplying minimum and maximum values
     *
     * @param min the minimum
     * @param max the maximum
     */
    public IntegerRange(int min, int max) {
        super();
        _min = Math.min(min, max);
        _max = Math.max(min, max);
    }

    /**
     * @return the minimum value
     */
    public double minimum() {
        return _min;
    }

    /**
     * @return the maximum value
     */
    public double maximum() {
        return _max;
    }

    /**
     * @return the minimum value
     */
    public int getMin() {
        return _min;
    }

    /**
     * Sets the minimum value
     *
     * @param min the new minimum value.
     */
    public void setMin(int min) {
        int old = _min;
        if (old == min) {
            return;
        }
        assert min <= _max : "minimum " + min + " not <= " + _max;
        _min = min;
        firePropertyChange(PROPERTY_MIN, old, min);
    }

    /**
     * @return the maximum value
     */
    public int getMax() {
        return _max;
    }

    /**
     * Sets the maximum value
     *
     * @param max the new maximum value.
     */
    public void setMax(int max) {
        int old = _max;
        if (old == max) {
            return;
        }
        assert max >= _min : "maximum " + max + " not >= " + _min;
        _max = max;
        firePropertyChange(PROPERTY_MAX, old, max);
    }

    public void adjust(Integer lower, Integer upper) {
        setMin(lower);
        setMax(upper);
    }

    /**
     * @return the size of the range
     */
    public double size() {
        return _max - _min;
    }

    /**
     * @return the minimum() value for the range
     */
    public Integer lower() {
        return (int) minimum();
    }

    /**
     * @return the maximum() value for the range
     */
    public Integer upper() {
        return (int) maximum();
    }

    /**
     * Determines whether the range contains the supplied value
     */
    public boolean contains(Integer x) {
        return x >= _min && x <= _max;
    }

    /**
     * Test for equality based on the values of min and max
     */
    @Override
    public boolean equals(Object other) {
        if (other instanceof IntegerRange) {
            IntegerRange otherRange = (IntegerRange) other;
            return _min == otherRange._min && _max == otherRange._max;
        }
        else {
            return false;
        }
    }

    @Override
    public String toString() {
        return String.format("#<IntegerRange min=%d max=%d>", _min, _max);
    }
}