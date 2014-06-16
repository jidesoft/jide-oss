/*
 * @(#)Range.java
 * 
 * 2002 - 2012 JIDE Software Incorporated. All rights reserved.
 * Copyright (c) 2005 - 2012 Catalysoft Limited. All rights reserved.
 */

package com.jidesoft.range;

import java.beans.PropertyChangeListener;

/**
 * @author Simon White (swhite@catalysoft.com)
 */
public interface Range<T> {
    /**
     * The name of the minimum property. Used when firing property change events
     */
    public static final String PROPERTY_MIN = "min";

    /**
     * The name of the maximum property. Used when firing property change events
     */
    public static final String PROPERTY_MAX = "max";

    /**
     * @return the smallest value of the range
     */
    public T lower();

    /**
     * @return the largest value of the range
     */
    public T upper();

    /**
     * Adjusts the range.
     *
     * @param lower the new smallest value of the range
     * @param upper the new largest value of the range
     */
    void adjust(T lower, T upper);

    /**
     * This may be the numeric representation of lower() or it may be rounded down.
     *
     * @return the numeric value of the smallest value to include in the range.
     */
    public double minimum();

    /**
     * This may be the numeric representation of upper() or it may be rounded up.
     *
     * @return The numeric value of the largest value to include in the range.
     */
    public double maximum();

    /**
     * Compute the size of the range
     */
    public double size();

    /**
     * Determines whether the supplied point lies within this range. For continuous ranges this is interpreted as a pair
     * of inequalities on the supplied value (i.e., min <= x <= max), but for discrete ranges the
     * <code>contains()</code> method is more like a set membership test.
     *
     * @param x
     * @return a boolean to indicate whether the supplied point lies within the range
     */
    public boolean contains(T x);

    /**
     * You can add a property change listener if you are interested to know when the range changes
     *
     * @param listener the new property change listener
     */
    public void addPropertyChangeListener(PropertyChangeListener listener);

    /**
     * Remove a property change listener
     *
     * @param listener the <code>PropertyChangeListener</code> to remove
     */
    public void removePropertyChangeListener(PropertyChangeListener listener);

    /**
     * Gets the registered property change listeners.
     *
     * @return the registered property change listeners.
     */
    public PropertyChangeListener[] getPropertyChangeListeners();

    /**
     * Gets the registered property change listeners for a property.
     *
     * @return the registered property change listeners for a property.
     */
    public PropertyChangeListener[] getPropertyChangeListeners(String propertyName);

}
