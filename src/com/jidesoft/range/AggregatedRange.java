/*
 * @(#)AggregatedRange.java 6/9/2011
 *
 * Copyright 2002 - 2011 JIDE Software Inc. All rights reserved.
 */

package com.jidesoft.range;

import com.jidesoft.swing.JideSwingUtilities;

import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * A range class formed from a collection of Positionable instances. The class can be used to derive the minimum and
 * maximum values for the collection of Positionables, as well as providing other useful information such as the sum of
 * all the positive values and the sum of all the negative values. (These are used in the preparation of a stacked bar
 * chart.)
 */
public class AggregatedRange implements Range<Double> {
    private Double positiveSum;
    private int positiveCount;
    private Double negativeSum;
    private int negativeCount;
    private List<Positionable> positions;

    /**
     * Create an empty range
     */
    public AggregatedRange() {
        this(null);
    }

    /**
     * Create a range from the supplied Positionable instances
     *
     * @param positions the instances of the Positionable interface
     */
    public AggregatedRange(Collection<Positionable> positions) {
        this.positions = positions == null ? new ArrayList<Positionable>() : new ArrayList<Positionable>(positions);
        Collections.sort(this.positions);
    }

    /**
     * The lower value of the range; for this class it is the same as minimum()
     *
     * @return the lower value of the range
     */
    public Double lower() {
        return minimum();
    }

    /**
     * The upper value of the range; for this class it is the same as maximum()
     *
     * @return the upper value of the range
     */
    public Double upper() {
        return maximum();
    }

    /**
     * The number of points being combined in this range
     *
     * @return the number of points contributing to the range
     */
    public int getCount() {
        return positions.size();
    }

    /**
     * Computes the sum of all the positive Positionables
     *
     * @return the sum of all the positive Positionables
     */
    public double getPositiveSum() {
        if (positiveSum == null) {
            updatePositives();
        }
        return positiveSum;
    }

    public int getPositiveCount() {
        if (positiveSum == null) {
            updatePositives();
        }
        return positiveCount;
    }

    private void updatePositives() {
        // Use a double to avoid autoboxing
        double sum = 0.0;
        positiveCount = 0;
        if (positions != null) {
            for (Positionable pos : positions) {
                if (pos.position() >= 0) {
                    sum += pos.position();
                    positiveCount++;
                }
            }
        }
        positiveSum = sum;
    }

    /**
     * Computes the sum of all the negative Positionables
     *
     * @return the sum of all the negative Positionables
     */
    public double getNegativeSum() {
        if (negativeSum == null) {
            updateNegatives();
        }
        return negativeSum;
    }

    public int getNegativeCount() {
        if (negativeSum == null) {
            updateNegatives();
        }
        return negativeCount;
    }


    private void updateNegatives() {
        // Use a double to avoid autoboxing
        double sum = 0.0;
        negativeCount = 0;
        if (positions != null) {
            for (Positionable pos : positions) {
                if (pos.position() < 0) {
                    sum += pos.position();
                    negativeCount++;
                }
            }
        }
        negativeSum = sum;
    }

    /**
     * Returns the minimum (numeric) value in the range
     *
     * @return the minimum value in the range
     */
    public double minimum() {
        if (positions == null || positions.size() == 0) {
            return Double.NEGATIVE_INFINITY;
        }
        Positionable first = positions.get(0);
        return first.position();
    }

    /**
     * Returns the maximum (numeric) value in the range
     *
     * @return the maximum value in the range
     */
    public double maximum() {
        if (positions == null || positions.size() == 0) {
            return Double.POSITIVE_INFINITY;
        }
        Positionable last = positions.get(positions.size() - 1);
        return last.position();
    }

    /**
     * The size of the range, computed as the difference between the maximum and the minimum
     *
     * @return the size of the range (i.e., max - min)
     */
    public double size() {
        return maximum() - minimum();
    }

    /**
     * Returns a boolean to indicate whether the supplied Double lies within this range
     */
    public boolean contains(Double item) {
        return item != null && item >= minimum() && item <= maximum();
    }

    /**
     * Not supported in this class
     */
    public void adjust(Double lower, Double upper) {
        throw new UnsupportedOperationException("An aggregated range is immutable");
    }

    /**
     * Not supported in this class
     */
    public void addPropertyChangeListener(PropertyChangeListener propertyChangeListener) {
        throw new UnsupportedOperationException("An aggregated range is immutable");
    }

    /**
     * Not supported in this class
     */
    public void removePropertyChangeListener(PropertyChangeListener propertyChangeListener) {
        throw new UnsupportedOperationException("An aggregated range is immutable");
    }

    @Override
    public PropertyChangeListener[] getPropertyChangeListeners() {
        return new PropertyChangeListener[0];
    }

    @Override
    public PropertyChangeListener[] getPropertyChangeListeners(String propertyName) {
        return new PropertyChangeListener[0];
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AggregatedRange)) return false;

        AggregatedRange that = (AggregatedRange) o;

        if (negativeCount != that.negativeCount) return false;
        if (positiveCount != that.positiveCount) return false;
        if (negativeSum != null ? !negativeSum.equals(that.negativeSum) : that.negativeSum != null) return false;
        if (!JideSwingUtilities.equals(positions, that.positions, true)) return false;
        if (positiveSum != null ? !positiveSum.equals(that.positiveSum) : that.positiveSum != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = positiveSum != null ? positiveSum.hashCode() : 0;
        result = 31 * result + positiveCount;
        result = 31 * result + (negativeSum != null ? negativeSum.hashCode() : 0);
        result = 31 * result + negativeCount;
        result = 31 * result + (positions != null ? positions.hashCode() : 0);
        return result;
    }
}
