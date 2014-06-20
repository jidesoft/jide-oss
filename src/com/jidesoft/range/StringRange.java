/*
 * @(#)StringRange.java 5/27/2014
 *
 * Copyright 2002 - 2014 JIDE Software Inc. All rights reserved.
 */

package com.jidesoft.range;

public class StringRange extends AbstractRange<String> {
    private String _lower;
    private String _upper;

    public StringRange(String lower, String upper) {
        adjust(lower, upper);
    }

    @Override
    public Range<String> createIntermediate(Range<String> targetRange, double position) {
        return null;
    }

    @Override
    public String lower() {
        return _lower;
    }

    @Override
    public String upper() {
        return _lower;
    }

    @Override
    public void adjust(String lower, String upper) {
        setMin(lower);
        setMax(upper);
    }

    protected void setMin(String lower) {
        String old = _lower;
        _lower = lower;
        firePropertyChange(PROPERTY_MIN, old, _lower);
    }

    protected void setMax(String upper) {
        String old = _upper;
        _upper = upper;
        firePropertyChange(PROPERTY_MAX, old, _upper);
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
        return Math.abs(_lower.compareTo(_upper));
    }

    @Override
    public boolean contains(String x) {
        if (x == null) {
            return false;
        }
        return x.compareTo(lower()) >= 0 && x.compareTo(upper()) <= 0;
    }

    @Override
    public String toString() {
        return "StringRange{" +
                "lower='" + _lower + '\'' +
                ", upper='" + _upper + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof StringRange)) return false;

        StringRange that = (StringRange) o;

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

    @Override
    public int compareTo(Range o) {
        if (o instanceof StringRange) {
            int lowerGap = _lower.compareTo(((StringRange) o)._lower);
            if (size() == 0 && o.size() == 0) return lowerGap;
            if (size() == 0 && size() < o.size()) return -1;
            if (o.size() == 0 && size() > o.size()) return 1;
            return lowerGap == 0 ? _upper.compareTo(((StringRange) o)._upper) : lowerGap;
        }
        return 0;
    }
}
