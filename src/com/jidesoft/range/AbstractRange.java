/*
 * @(#)AbstractRange.java 8/24/2009
 *
 * Copyright 2005 - 2012 Catalysoft Ltd. All rights reserved.
 */

package com.jidesoft.range;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 * An abstract implementation of <code>Range</code>
 */
public abstract class AbstractRange<T> implements Range<T>, Comparable<Range> {
    private PropertyChangeSupport changeSupport;

    public AbstractRange() {
    }

    public Range<T> copy() {
        throw new UnsupportedOperationException("Copy method not implemented");
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        if (listener == null) {
            return;
        }
        if (changeSupport == null) {
            changeSupport = new java.beans.PropertyChangeSupport(this);
        }
        changeSupport.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        if (listener == null || changeSupport == null) {
            return;
        }
        changeSupport.removePropertyChangeListener(listener);
    }

    public PropertyChangeListener[] getPropertyChangeListeners() {
        if (changeSupport == null) {
            return new PropertyChangeListener[0];
        }
        return changeSupport.getPropertyChangeListeners();
    }

    public PropertyChangeListener[] getPropertyChangeListeners(String propertyName) {
        if (changeSupport == null) {
            return new PropertyChangeListener[0];
        }
        return changeSupport.getPropertyChangeListeners(propertyName);
    }

    protected void firePropertyChange(PropertyChangeEvent evt) {
        if (changeSupport == null) {
            return;
        }
        changeSupport.firePropertyChange(evt);
    }

    protected void firePropertyChange(String propertyName, boolean oldValue, boolean newValue) {
        if (changeSupport == null) {
            return;
        }
        changeSupport.firePropertyChange(propertyName, oldValue, newValue);
    }

    protected void firePropertyChange(String propertyName, int oldValue, int newValue) {
        if (changeSupport == null) {
            return;
        }
        changeSupport.firePropertyChange(propertyName, oldValue, newValue);
    }

    protected void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
        if (changeSupport == null) {
            return;
        }
        changeSupport.firePropertyChange(propertyName, oldValue, newValue);
    }

    public abstract Range<T> createIntermediate(Range<T> targetRange, double position);

    @Override
    public int compareTo(Range o) {
        int lowerGap = (int) (minimum() - o.minimum());
        if (size() == 0 && o.size() == 0) return lowerGap;
        if (size() == 0 && size() < o.size()) return -1;
        if (o.size() == 0 && size() > o.size()) return 1;
        return lowerGap == 0 ? (int) (maximum() - o.maximum()) : lowerGap;
    }
}