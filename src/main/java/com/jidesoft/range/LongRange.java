/*
 * @(#)LongRange.java 5/27/2014
 *
 * Copyright 2002 - 2014 JIDE Software Inc. All rights reserved.
 */

package com.jidesoft.range;

/**
 * Specifies upper and lower bounds for a range of values
 *
 * @author Simon
 */
public class LongRange extends AbstractNumericRange<Long> {
    protected long _min;
    protected long _max;

    /**
     * Creates a numeric range with minimum 0.0 and maximum of 1.0
     */
    public LongRange() {
        this(0, 1);
    }

    /**
     * Create a numeric range by supplying minimum and maximum values
     *
     * @param min the minimum
     * @param max the maximum
     */
    public LongRange(long min, long max) {
        super();
        _min = Math.min(min, max);
        _max = Math.max(min, max);
    }

    /**
     * Constructs a copy of the supplied IntegerRange object
     *
     * @param integerRange the long range object to copy
     */
    public LongRange(LongRange integerRange) {
        this((long) integerRange.minimum(), (long) integerRange.maximum());
    }

    @Override
    public Range<Long> copy() {
        return new LongRange(this);
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
    public long getMin() {
        return _min;
    }

    /**
     * Sets the minimum value
     *
     * @param min the new minimum value.
     */
    public void setMin(long min) {
        long old = _min;
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
    public long getMax() {
        return _max;
    }

    /**
     * Sets the maximum value
     *
     * @param max the new maximum value.
     */
    public void setMax(long max) {
        long old = _max;
        if (old == max) {
            return;
        }
        assert max >= _min : "maximum " + max + " not >= " + _min;
        _max = max;
        firePropertyChange(PROPERTY_MAX, old, max);
    }

    public void adjust(Long lower, Long upper) {
        double size = size(); // save it
        if (lower != null) {
            setMin(lower);
        }
        else if (upper != null) {
            setMin(upper - (long) size);
        }
        if (upper != null) {
            setMax(upper);
        }
        else if (lower != null) {
            setMin(lower + (long) size);
        }
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
    public Long lower() {
        return (long) minimum();
    }

    /**
     * @return the maximum() value for the range
     */
    public Long upper() {
        return (long) maximum();
    }

    /**
     * Determines whether the range contains the supplied value
     */
    public boolean contains(Long x) {
        return x >= _min && x <= _max;
    }

    @Override
    public Range<Long> createIntermediate(Range<Long> targetRange, double position) {
        double sourceMin = this.minimum();
        double sourceMax = this.maximum();
        double targetMin = targetRange.minimum();
        double targetMax = targetRange.maximum();
        double min = sourceMin + position * (targetMin - sourceMin);
        double max = sourceMax + position * (targetMax - sourceMax);
        return new LongRange((int) Math.round(min), (int) Math.round(max));
    }

    /**
     * Test for equality based on the values of min and max
     */
    @Override
    public boolean equals(Object other) {
        if (other instanceof LongRange) {
            LongRange otherRange = (LongRange) other;
            return _min == otherRange._min && _max == otherRange._max;
        }
        else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return (int) (_max * 29 + _min);
    }

    @Override
    public String toString() {
        return String.format("#<LongRange min=%d max=%d>", _min, _max);
    }
}