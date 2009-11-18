/*
 * @(#)CombinedNumericRange.java
 * 
 * 2002 - 2009 JIDE Software Incorporated. All rights reserved.
 * Copyright (c) 2005 - 2009 Catalysoft Limited. All rights reserved.
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
    private List<Range<Double>> _ranges = new ArrayList<Range<Double>>();
    private Double _max = null;
    private Double _min = null;

    /**
     * Using this constructor relies on the user subsequently calling add() to add a range
     */
    public CombinedNumericRange() {

    }

    /**
     * Add a new range to this combined range. Notice this method returns this instance, so method calls can be chained
     * together.
     *
     * @param range the new range to add
     * @return this instance
     */
    public CombinedNumericRange add(Range<Double> range) {
        _ranges.add(range);
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

    public void adjust(Double lower, Double upper) {
    }

    /**
     * Lazily calculates the maximum value in the range
     */
    public double maximum() {
        if (_max != null) {
            return _max;
        }
        _max = Double.MIN_VALUE;
        for (Range<Double> range : _ranges) {
            if (range.maximum() > _max) {
                _max = range.maximum();
            }
        }
        return _max;
    }

    /**
     * Lazily calculates the minimum value in the range
     */
    public double minimum() {
        if (_min != null) {
            return _min;
        }
        _min = Double.MAX_VALUE;
        for (Range<Double> range : _ranges) {
            if (range.minimum() < _min) {
                _min = range.minimum();
            }
        }
        return _min;
    }


    /**
     * This range contains some point iff one or more of its subranges contain that point
     */
    public boolean contains(Double x) {
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

    public double size() {
        return maximum() - minimum();
    }

}
