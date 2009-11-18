/*
 * @(#)CategoryRange.java
 * 
 * 2002 - 2009 JIDE Software Incorporated. All rights reserved.
 * Copyright (c) 2005 - 2009 Catalysoft Limited. All rights reserved.
 */

package com.jidesoft.range;

import java.util.*;


/**
 * Note that this class is iterable so you can use it in an advanced for.. loop
 *
 * @author Simon White (swhite@catalysoft.com)
 */
public class CategoryRange<T> extends AbstractRange<T> implements Iterable<Category<T>> {
    private static final String PROPERTY_VALUES = "values";
    private List<T> _possibleValues = null;
    private List<Category<T>> _categoryValues = null;

    public CategoryRange() {
        _possibleValues = new ArrayList<T>();
        _categoryValues = new ArrayList<Category<T>>();
    }

    /**
     * Create a CategoryRange from the supplied values
     *
     * @param values the values.
     */
    public CategoryRange(T... values) {
        _possibleValues = new ArrayList<T>();
        _possibleValues.addAll(Arrays.asList(values));
    }

    /**
     * Create a CategoryRange from a set of values. Note that internally, a list is created out of the set so that the
     * class can reliably determine an <code>upper()</code> and a <code>lower()</code> value.
     *
     * @param values - the set of possible values
     */
    public CategoryRange(Set<T> values) {
        _possibleValues = new ArrayList<T>(values);
    }

    public List<T> getPossibleValues() {
        return _possibleValues;
    }

    /**
     * This method fires a propety change event, but to avoid cloning a list for efficiency, the old value is always
     * null
     *
     * @param c the category to add
     * @return this range
     */
    public CategoryRange<T> add(Category<T> c) {
        _possibleValues.add(c.getValue());
        _categoryValues.add(c);
        c.setRange(this);
        firePropertyChange(PROPERTY_VALUES, null, _possibleValues);
        return this;
    }

    // TODO: This assumes the possible values are sorted
    public T lower() {
        if (_possibleValues == null || _possibleValues.size() == 0) {
            return null;
        }
        return _possibleValues.get(0);
    }

    // TODO: This assumes the possible values are sorted
    public T upper() {
        if (_possibleValues == null || _possibleValues.size() == 0) {
            return null;
        }
        int numElements = _possibleValues.size();
        return _possibleValues.get(numElements - 1); // get the last element
    }

    public void adjust(T lower, T upper) {
    }

    /**
     * @return the maximum value for the axis in the range
     *
     * @see com.jidesoft.range.Range#maximum()
     */
    public double maximum() {
        return position(upper()) + 1;
    }

    /**
     * @return the minimum value for the axis in the range
     *
     * @see com.jidesoft.range.Range#minimum()
     */
    public double minimum() {
        return position(lower()) - 1;
    }

    /**
     * @return the size of the range
     *
     * @see com.jidesoft.range.Range#size()
     */
    public double size() {
        if (_possibleValues == null) {
            return 0;
        }

        int numElements = _possibleValues.size();
        if (numElements == 0) {
            return 0;
        }
        return numElements + 1;
    }


    public int position(T value) {
        int index = _possibleValues.indexOf(value);
        if (index < 0) {
            throw new IllegalArgumentException("Value " + value + " not known");
        }
        return 1 + index;
    }

    /**
     * Determines whether the category range contains the supplied possible value
     */
    public boolean contains(T x) {
        if (x == null) {
            return false;
        }
        else {
            for (T category : _possibleValues) {
                if (x.equals(category)) {
                    return true;
                }
            }
            return false;
        }
    }

    /**
     * Determines whether the category range contains the specified category value
     *
     * @param value the category value.
     * @return true if the range contains the specified value. Otherwise false.
     */
    public boolean contains(Category<T> value) {
        if (value == null) {
            return false;
        }
        else {
            for (Category<T> category : getCategoryValues()) {
                if (value.equals(category)) {
                    return true;
                }
            }
            return false;
        }
    }

    public Iterator<Category<T>> iterator() {
        return getCategoryValues().iterator();
    }

    public List<Category<T>> getCategoryValues() {
        if (_categoryValues == null) {
            _categoryValues = new ArrayList<Category<T>>();
            for (T value : _possibleValues) {
                _categoryValues.add(new Category<T>(value, this));
            }
        }
        return _categoryValues;
    }
}
