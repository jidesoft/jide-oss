/*
 * @(#)BooleanRange.java 5/27/2014
 *
 * Copyright 2002 - 2014 JIDE Software Inc. All rights reserved.
 */

package com.jidesoft.range;

public class BooleanRange extends AbstractRange<Boolean> {
    private Boolean _lower = false;
    private Boolean _upper = true;

    public BooleanRange(boolean lower, boolean upper) {
        adjust(lower, upper);
    }

    @Override
    public Range<Boolean> createIntermediate(Range<Boolean> targetRange, double position) {
        return null;
    }

    @Override
    public Boolean lower() {
        return _lower;
    }

    @Override
    public Boolean upper() {
        return _upper;
    }

    @Override
    public void adjust(Boolean lower, Boolean upper) {
        setMin(lower);
        setMax(upper);
    }

    protected void setMin(boolean lower) {
        boolean old = _lower;
        _lower = lower;
        firePropertyChange(PROPERTY_MIN, old, _lower.booleanValue());
    }

    protected void setMax(boolean upper) {
        boolean old = _upper;
        _upper = upper;
        firePropertyChange(PROPERTY_MAX, old, _upper.booleanValue());
    }

    @Override
    public double minimum() {
        throw new UnsupportedOperationException("minimum() is not implemented in StringRange");
    }

    @Override
    public double maximum() {
        throw new UnsupportedOperationException("maximum() is not implemented in StringRange");
    }

    @Override
    public double size() {
        return _lower.compareTo(_upper);
    }

    @Override
    public boolean contains(Boolean x) {
        if (x == null) {
            return false;
        }
        return x.compareTo(lower()) >= 0 && x.compareTo(upper()) <= 0;
    }

    @Override
    public String toString() {
        return "BooleanRange{" +
                "lower='" + _lower + '\'' +
                ", upper='" + _upper + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BooleanRange)) return false;

        BooleanRange that = (BooleanRange) o;

        if (_lower != null ? !_lower.equals(that._lower) : that._lower != null) return false;
        if (_upper != null ? !_upper.equals(that._upper) : that._upper != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = _lower != null ? _lower.hashCode() : 0;
        result = 31 * result + (_upper != null ? _upper.hashCode() : 0);
        return result;
    }
}
