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
    private Double minimum;
    private Double maximum;

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
    
    /**
     * Create a new CategoryRange by copying an existing one. This would allow you subsequently to tweak
     * the values in the copy without affecting the original.
     * @param categoryRange the category range instance to copy
     */
    public CategoryRange(CategoryRange<T> categoryRange) {
        _categoryValues = new ArrayList<Category<T>>(categoryRange.getCategoryValues());
        _possibleValues = new ArrayList<T>(categoryRange.getPossibleValues());
        CategoryRange<T> range = new CategoryRange<T>();
        setMinimum(categoryRange.minimum());
        setMaximum(categoryRange.maximum());
    }

    public List<T> getPossibleValues() {
        return _possibleValues;
    }

    /**
     * <p>Adds a category to the range. Note that after adding categories, you will need to call reset() if you want the
     * minimum and maximum numeric values of the range to be recomputed.</p>
     * <p>This method fires a property change event, but to avoid cloning a list for efficiency, the old value is always
     * null</p>
     *
     * @param c the category to add
     * @return this range
     */
    public CategoryRange<T> add(Category<T> c) {
        if (!contains(c)) {
            _possibleValues.add(c.getValue());
            _categoryValues.add(c);
            c.setRange(this);
            firePropertyChange(PROPERTY_VALUES, null, _possibleValues);
        }
        return this;
    }
    

    @Override
    public Range<T> copy() {
        return new CategoryRange<T>(this);
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

    /**
     * Not supported for Category Ranges
     */
    public void adjust(T lower, T upper) {
        throw new UnsupportedOperationException();
    }

    /**
     * @return the maximum value for the axis in the range
     *
     * @see com.jidesoft.range.Range#maximum()
     */
    public double maximum() {
        if (maximum == null) {
            T upper = upper();
            if (upper == null) {
                return 1.0;
            } else {
                maximum = position(upper) + 1.0;
            }
        }
        return maximum;
    }

    /**
     * @return the minimum value for the axis in the range
     *
     * @see com.jidesoft.range.Range#minimum()
     */
    public double minimum() {
        if (minimum == null) {
            T lower = lower();
            if (lower == null) {
                return 0.0;
            } else {
                minimum = position(lower) - 1.0;
            }
        }
        return minimum;
    }
    
    /**
     * Reset the maximum and minimum. They will be recomputed on the next call to minimum() or maximum() respectively
     */
    public void reset() {
        maximum = null;
        minimum = null;
    }
    
    public void setMinimum(double value) {
        Double oldValue = this.minimum;
        this.minimum = value;
        firePropertyChange(PROPERTY_MIN, oldValue, value);
    }
    
    public void setMaximum(double value) {
        Double oldValue = this.maximum;
        this.maximum = value;
        firePropertyChange(PROPERTY_MAX, oldValue, value);
    }

    /**
     * Returns the size of the range, as given by the maximum minus the minimum. To compute the size of
     * the range in terms of the number of members in the category, use getPossibleValue().size()
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
        // The previous definition of size() prevented us from being able to zoom
        // in on a categorical bar chart
        return maximum() - minimum();
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
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((_categoryValues == null) ? 0 : _categoryValues.hashCode());
        result = prime * result + ((_possibleValues == null) ? 0 : _possibleValues.hashCode());
        result = prime * result + ((maximum == null) ? 0 : maximum.hashCode());
        result = prime * result + ((minimum == null) ? 0 : minimum.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        CategoryRange other = (CategoryRange) obj;
        if (_categoryValues == null) {
            if (other._categoryValues != null)
                return false;
        } else if (!_categoryValues.equals(other._categoryValues))
            return false;
        if (_possibleValues == null) {
            if (other._possibleValues != null)
                return false;
        } else if (!_possibleValues.equals(other._possibleValues))
            return false;
        if (maximum == null) {
            if (other.maximum != null)
                return false;
        } else if (!maximum.equals(other.maximum))
            return false;
        if (minimum == null) {
            if (other.minimum != null)
                return false;
        } else if (!minimum.equals(other.minimum))
            return false;
        return true;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("#<CategoryRange ");
        builder.append("minimum=");
        builder.append(minimum);
        builder.append(" maximum=");
        builder.append(maximum);
        builder.append(">");
        return builder.toString();
    }
}
