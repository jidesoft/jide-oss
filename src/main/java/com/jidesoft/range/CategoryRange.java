/*
 * @(#)CategoryRange.java
 *
 * 2002 - 2015 JIDE Software Incorporated. All rights reserved.
 * Copyright (c) 2005 - 2015 Catalysoft Limited. All rights reserved.
 */

package com.jidesoft.range;

import java.util.*;


/**
 * Note that this class is iterable so you can use it in an advanced for.. loop
 *
 * @author Simon White (swhite@catalysoft.com)
 */
public class CategoryRange<T> extends AbstractRange<T> implements Iterable<Category<T>> {
    public static final String PROPERTY_VALUES = "values";
    public static final String PROPERTY_COMPARATOR = "comparator";
    public static final String PROPERTY_SORTED = "sorted";
    private List<T> _possibleValues = null;
    private List<Category<T>> _categoryValues = null;
    private Double minimum;
    private Double maximum;
    private Comparator<T> comparator = null;
    private boolean sorted = false;
    // Private member variable to flag whether the possible values have been sorted or are in need of a sort
    private boolean alreadySorted = false;
    private Map<T, Integer> positionIndices = new HashMap<T, Integer>();

    public CategoryRange() {
        _possibleValues = new ArrayList<T>();
        _categoryValues = new ArrayList<Category<T>>();
        alreadySorted = false;
    }

    /**
     * Create a CategoryRange from the supplied values
     *
     * @param values the values.
     */
    public CategoryRange(T... values) {
        _possibleValues = new ArrayList<T>();
        _possibleValues.addAll(Arrays.asList(values));
        alreadySorted = false;
    }

    /**
     * Create a CategoryRange from a set of values. Note that internally, a list is created out of the set so that the
     * class can reliably determine an <code>upper()</code> and a <code>lower()</code> value.
     *
     * @param values - the set of possible values
     */
    public CategoryRange(Set<T> values) {
        _possibleValues = new ArrayList<T>(values);
        alreadySorted = false;
    }

    /**
     * Create a new CategoryRange by copying an existing one. This would allow you subsequently to tweak the values in
     * the copy without affecting the original.
     *
     * @param categoryRange the category range instance to copy
     */
    public CategoryRange(CategoryRange<T> categoryRange) {
        _categoryValues = new ArrayList<Category<T>>(categoryRange.getCategoryValues());
        _possibleValues = new ArrayList<T>(categoryRange.getPossibleValues());
        comparator = categoryRange.getComparator();
        setMinimum(categoryRange.minimum());
        setMaximum(categoryRange.maximum());
        alreadySorted = false;
    }

    // When you call getCategoryValues() it will call this method and retrieve a list of possible values
    // which is sorted if necessary
    public List<T> getPossibleValues() {
        if (sorted && !alreadySorted) {
            if (comparator == null) {
                Comparator<T> defaultComparator = new Comparator<T>() {
                    public int compare(T o1, T o2) {
                        if (o1 == null && o2 == null) {
                            return 0;
                        } else if (o1 == null) {
                            return -1;
                        } else if (o2 == null) {
                            return 1;
                        } else {
                            // Use natural sort order if available
                            if (o1 instanceof Comparable) {
                                Comparable t1 = (Comparable) o1;
                                return t1.compareTo(o2);
                            } else {
                                // otherwise use the toString method to derive a string comparator
                                String s1 = o1.toString();
                                String s2 = o2.toString();
                                return s1.compareTo(s2);
                            }
                        }
                    }
                };
                Collections.sort(_possibleValues, defaultComparator);
                positionIndices.clear();
            } else {
                Collections.sort(_possibleValues, comparator);
                positionIndices.clear();
            }
        }
        return _possibleValues;
    }

    /**
     * Returns the category with the supplied position value. (Note that the first position is 1, not 0.)
     *
     * @param position the position of a category along an axis
     * @return the category with the supplied position value.
     */
    public Category<T> getCategory(int position) {
        if (position < 1) {
            throw new IllegalArgumentException("Supplied category index was " + position + " but it should be >= 1");
        }
        return getCategoryValues().get(position - 1);
    }

    /**
     * <p>Adds a category to the range. Note that after adding categories, you will need to call reset() if you want the
     * minimum and maximum numeric values of the range to be recomputed.</p> <p>This method fires a property change
     * event, but to avoid cloning a list for efficiency, the old value is always null</p>
     *
     * @param c the category to add
     * @return this range
     */
    public CategoryRange<T> add(Category<T> c) {
        if (!contains(c)) {
            if (comparator == null) {
                _possibleValues.add(c.getValue());
                _categoryValues.add(c);
            } else {
                _possibleValues.add(c.getValue());
                alreadySorted = false;
                // Force the category values to be recomputed
                _categoryValues = null;
            }
            c.setRange(this);
            firePropertyChange(PROPERTY_VALUES, null, _possibleValues);
        }
        return this;
    }

    /**
     * Specify whether the categories of the range should be sorted.
     * If you call this method with <code>true</code> but do not explicitly
     * set a comparator for the sort ordering, then the natural ordering of the
     * objects (using java.util.Comparable) will be used. If the objects do not
     * implement Comparable, then a string comparator is constructed based on the
     * toString() method of the object.
     * @param sorted whether the categories of the range should be sorted
     */
    public void setSorted(boolean sorted) {
        boolean oldValue = this.sorted;
        this.sorted = sorted;
        // Force the category values to be recomputed
        if (sorted) {
            _categoryValues = null;
        }
        positionIndices.clear();
        firePropertyChange(PROPERTY_SORTED, oldValue, sorted);
    }

    /**
     * Returns a value to indicate whether the categories of the range are sorted
     * @return a value to indicate whether the categories of the range are sorted
     */
    public boolean isSorted() {
        return this.sorted;
    }

    /**
     * Returns the comparator that, if set, will be used to sort the values in the range
     * @return the comparator that, if set, will be used to sort the values in the range
     */
    public Comparator<T> getComparator() {
        return comparator;
    }

    /**
     * Specify the comparator that will be used to sort the values in the range.
     * Calling this method implicitly calls setSorted(): the sorted property will be set
     * to true if the comparator is non-null and will be set to false if the comparator is null
     * @param comparator the comparator to be used to sort the values in the range
     */
    public void setComparator(Comparator<T> comparator) {
        Comparator<T> oldValue = this.comparator;
        this.comparator = comparator;
        // This call will also force the category values to be recomputed
        setSorted(comparator != null);
        firePropertyChange(PROPERTY_COMPARATOR, oldValue, comparator);
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
            }
            else {
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
            }
            else {
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
        positionIndices.clear();
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
     * Returns the size of the range, as given by the maximum minus the minimum. To compute the size of the range in
     * terms of the number of members in the category, use getPossibleValue().size()
     *
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
        Integer index = positionIndices.get(value);
        if (index == null) {
            List<T> possibleValues = getPossibleValues();
            index = possibleValues.indexOf(value);
            if (index < 0) {
                throw new IllegalArgumentException("Value " + value + " not known");
            }
            positionIndices.put(value, index);
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

    /**
     * Returns an iterator for the category values
     * @return an iterator for the category values
     */
    public Iterator<Category<T>> iterator() {
        return getCategoryValues().iterator();
    }

    /**
     * Returns a list of the category values in this range
     * @return a list of category values
     */
    public List<Category<T>> getCategoryValues() {
        if (_categoryValues == null) {
            _categoryValues = new ArrayList<Category<T>>();
            for (T value : getPossibleValues()) {
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
        }
        else if (!_categoryValues.equals(other._categoryValues))
            return false;
        if (_possibleValues == null) {
            if (other._possibleValues != null)
                return false;
        }
        else if (!_possibleValues.equals(other._possibleValues))
            return false;
        if (maximum == null) {
            if (other.maximum != null)
                return false;
        }
        else if (!maximum.equals(other.maximum))
            return false;
        if (minimum == null) {
            if (other.minimum != null)
                return false;
        }
        else if (!minimum.equals(other.minimum))
            return false;
        return true;
    }

    /**
     * Creates an intermediate range between this range and a target range. Used for range morphing.
     * @param target the target range of the morph
     * @param position a value between 0 and 1 indicating the position of the morph
     * @return a CategoryRange
     */
    public Range<T> createIntermediate(Range<T> target, double position) {
        double sourceMin = this.minimum();
        double sourceMax = this.maximum();
        double targetMin = target.minimum();
        double targetMax = target.maximum();
        double min = sourceMin + position * (targetMin - sourceMin);
        double max= sourceMax + position * (targetMax - sourceMax);
        CategoryRange r;
        if (position < 0.5) {
            r = new CategoryRange(this);
        } else {
            if (target instanceof CategoryRange) {
                r = new CategoryRange((CategoryRange) target);
            } else {
                throw new IllegalArgumentException("Cannot create intermediate range from "+target.getClass());
            }
        }
        r.setMinimum(min);
        r.setMaximum(max);
        return r;
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
