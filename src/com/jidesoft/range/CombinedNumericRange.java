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
    private final Object monitor = new Object();
    private List<Range<Double>> _ranges = new ArrayList<Range<Double>>();
    private Double _max = null;
    private Double _min = null;

    /**
     * Using this constructor relies on the user subsequently calling add() to add a range
     */
    public CombinedNumericRange() {

    }

    /**
     * Add a new range to this combined range. Notice the method returns this instance, so method calls can be chained
     * together. If you pass null to this method the CombinedNumericRange remains unchanged; an Exception is
     * NOT thrown.
     *
     * @param range the new range to add
     * @return this instance
     */
    public CombinedNumericRange add(Range<Double> range) {
        if (range == null) {
            return this;
        }
        synchronized(monitor) {
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
     * Calls to this method throw an UnsupportedOprationException. The idea is that in the case of this
     * class we don't want to be able to mess with the lower and upper bounds as they are computed from
     * the supplied range values. If the class needed to recompute the lower and upper bounds any previous
     * adjustment that had been made through this method would have been lost.
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
        synchronized(monitor) {
            if (_max != null) {
                return _max;
            }
            if (_ranges == null || _ranges.size() == 0) {
                return Double.MAX_VALUE;
            }
            _max = Double.MIN_VALUE;
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
        synchronized(monitor) {
            if (_min != null) {
                return _min;
            }
            if (_ranges == null || _ranges.size() == 0) {
                return Double.MIN_VALUE;
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
     * This range contains some point iff one or more of its subranges contain that point
     */
    public boolean contains(Double x) {
        synchronized(monitor) {
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
        synchronized(monitor) {
            return maximum() - minimum();
        }
    }

}
