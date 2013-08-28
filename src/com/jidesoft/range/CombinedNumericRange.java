/*
 * @(#)CombinedNumericRange.java
 * 
 * 2002 - 2012 JIDE Software Incorporated. All rights reserved.
 * Copyright (c) 2005 - 2012 Catalysoft Limited. All rights reserved.
 */

package com.jidesoft.range;

import java.util.ArrayList;
import java.util.List;

/**
 * A little convenience class to compute the maximum and minimum values of multiple ranges.
 *
 * @author swhite@catalysoft.com
 */
public class CombinedNumericRange extends AbstractNumericRange<Double> {
    private final Object monitor = new Object();
    private List<Range<Double>> _ranges = new ArrayList<Range<Double>>();
    private Double _max = null;
    private Double _min = null;

    /**
     * Using this constructor relies on the user subsequently calling add() to add a range
     */
    public CombinedNumericRange() {
        super();
    }

    /**
     * Add a new range to this combined range. Notice the method returns this instance, so method calls can be chained
     * together. If you pass null to this method the CombinedNumericRange remains unchanged; an Exception is NOT
     * thrown.
     *
     * @param range the new range to add
     * @return this instance
     */
    public CombinedNumericRange add(Range<Double> range) {
        if (range == null) {
            return this;
        }
        synchronized (monitor) {
            _ranges.add(range);
            _min = null;
            _max = null;
        }
        return this;
    }

    /**
     * The lower value in the range; here, the same as <code>minimum</code>
     */
    public Double lower() {
        return minimum();
    }

    /**
     * The upper value in the range; here, the same as <code>maximum()</code>
     */
    public Double upper() {
        return maximum();
    }

    /**
     * Returns the number of sub-ranges contained by this range
     *
     * @return the number of sub-ranges in this range
     */
    public int rangeCount() {
        return _ranges == null ? 0 : _ranges.size();
    }

    /**
     * Calls to this method throw an UnsupportedOprationException. The idea is that in the case of this class we don't
     * want to be able to mess with the lower and upper bounds as they are computed from the supplied range values. If
     * the class needed to recompute the lower and upper bounds any previous adjustment that had been made through this
     * method would have been lost.
     *
     * @throws UnsupportedOperationException
     */
    public void adjust(Double lower, Double upper) {
        throw new UnsupportedOperationException();
    }

    /**
     * Lazily calculates the maximum value in the range
     */
    public double maximum() {
        synchronized (monitor) {
            if (_max != null) {
                return _max;
            }
            if (_ranges == null || _ranges.size() == 0) {
                return Double.MAX_VALUE;
            }
            _max = -Double.MAX_VALUE;
            for (Range<Double> range : _ranges) {
                if (range != null && range.maximum() > _max) {
                    _max = range.maximum();
                }
            }
            return _max;
        }
    }

    /**
     * Lazily calculates the minimum value in the range
     */
    public double minimum() {
        synchronized (monitor) {
            if (_min != null) {
                return _min;
            }
            if (_ranges == null || _ranges.size() == 0) {
                return -Double.MAX_VALUE;
            }
            _min = Double.MAX_VALUE;
            for (Range<Double> range : _ranges) {
                if (range != null && range.minimum() < _min) {
                    _min = range.minimum();
                }
            }
            return _min;
        }
    }


    /**
     * This range contains some point iff one or more of its sub-ranges contain that point
     */
    public boolean contains(Double x) {
        synchronized (monitor) {
            if (x == null || _ranges.size() == 0) {
                return false;
            }
            else {
                for (Range<Double> range : _ranges) {
                    if (range.contains(x)) {
                        return true;
                    }
                }
                return false;
            }
        }
    }

    /**
     * The size of the range is computed as the maximum minus the minimum value.
     */
    public double size() {
        synchronized (monitor) {
            return maximum() - minimum();
        }
    }

    /**
     * Returns a new numeric range that is based on this range, but with a margin introduced at each end. The margin
     * proportion is a value between 0 and 1. For example to add a 20% margin to each end use parameters of 0.2 for both
     * the leading and trailing margin proportion.
     *
     * @param leadingMarginProportion  how much margin to add at the low end of the range
     * @param trailingMarginProportion how much margin to add at the top end of the range
     * @return a new NumericRange object with margins added
     */
    public NumericRange getRange(double leadingMarginProportion, double trailingMarginProportion) {
        double maximum = maximum();
        double minimum = minimum();
        double difference = Math.abs(maximum - minimum);
        double leadingMargin = leadingMarginProportion * difference;
        double trailingMargin = trailingMarginProportion * difference;
        return new NumericRange(minimum - leadingMargin, maximum + trailingMargin);
    }

    @Override
    public Range<Double> createIntermediate(Range<Double> targetRange, double position) {
        double sourceMin = this.minimum();
        double sourceMax = this.maximum();
        double targetMin = targetRange.minimum();
        double targetMax = targetRange.maximum();
        double min = sourceMin + position * (targetMin - sourceMin);
        double max= sourceMax + position * (targetMax - sourceMax);
        return new NumericRange(min, max);
    }

    public String toString() {
        return String.format("#<CombinedNumericRange min=%s max=%s>", minimum(), maximum());
    }

}
