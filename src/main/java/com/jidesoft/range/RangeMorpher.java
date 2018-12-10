/**
 * Copyright (c) Catalysoft Ltd, 2005-2013 All Rights Reserved
 * Created: 23/06/2013 at 19:11
 */
package com.jidesoft.range;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class RangeMorpher {
    public static final String PROPERTY_MORPH_STARTED = "Morph Started";
    public static final String PROPERTY_MORPH_ENDED = "Morph Ended";
    public static final String PROPERTY_MORPH_RANGES = "Morph Ranges";
    private double position = 1.0;
    private int numSteps = 10;
    private int delay = 40;
    private int stepsLeft = numSteps;
    private Timer timer;
    private Range<?>[] ranges = null;
    private PropertyChangeSupport support = new PropertyChangeSupport(this);

    /**
     * This constructor is provided mainly for Java Bean compatibility. If you use it, make sure you call
     * <code>setChart</code> to inform the object of the chart on which it operates.
     */
    public RangeMorpher() {

    }

    /**
     * Create a RangeMorpher instance on the supplied <code>Chart</code>.
     *
     * @param numSteps the number of steps in a transition
     * @param delay    the delay between the steps, in milliseconds
     */
    public RangeMorpher(int numSteps, int delay) {
        this.numSteps = numSteps;
        stepsLeft = numSteps;
        this.delay = delay;
    }

    /**
     * Add a property change listener to this object. Property events are fired at the beginning and at the end of
     * a transition.
     *
     * @param listener the property change listener
     */
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        support.addPropertyChangeListener(listener);
    }

    /**
     * Remove a property change listener from this object.
     *
     * @param listener the property change listener to remove
     */
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        support.removePropertyChangeListener(listener);
    }

    /**
     * Returns true if a morph is currently in progress
     *
     * @return a boolean to indicate whether a morph is in progress.
     */
    public boolean isMorphing() {
        return position < 1.0;
    }

    public void morph(final Range<?>[] sourceRanges, final Range<?>[] destinationRanges) {
        // Make sure the timer is not running
        stopAnimation();
        ActionListener listener = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                stepsLeft--;
                position = ((double) (numSteps - stepsLeft)) / numSteps;
                if (stepsLeft == 0) {
                    stopAnimation();
                    support.firePropertyChange(PROPERTY_MORPH_ENDED, ranges, destinationRanges);
                } else {
                    Range<?>[] oldRanges = ranges;
                    ranges = createIntermediate(sourceRanges, destinationRanges, position);
                    support.firePropertyChange(PROPERTY_MORPH_RANGES, oldRanges, ranges);
                }
            }
        };
        timer = new Timer(delay, listener);
        timer.start();
        support.firePropertyChange(PROPERTY_MORPH_STARTED, null, sourceRanges);
    }

    /**
     * Creates an intermediate array of ranges to be used during the transition
     *
     * @param sources   the source ranges
     * @param targets   the parallel array of corresponding target ranges
     * @param position the position on a scale from 0 (source) to 1 (target)
     * @return a Chartable point representing the given position during the transition
     */
    Range<?>[] createIntermediate(Range<?>[] sources, Range<?>[] targets, double position) {
        Range<?>[] ranges = new Range<?>[sources.length];
        for (int i=0; i<ranges.length; i++) {
            if (sources[i] instanceof AbstractRange) {
                AbstractRange<?> ar = (AbstractRange) sources[i];
                ranges[i] = ar.createIntermediate((AbstractRange) targets[i], position);
            } else {
                throw new UnsupportedOperationException("Cannot morph "+sources[i].getClass().getName());
            }
        }
        return ranges;
    }


    Range<?> createIntermediate(NumericRange source, NumericRange target, double position) {
        double sourceMin = source.minimum();
        double sourceMax = source.maximum();
        double targetMin = target.minimum();
        double targetMax = target.maximum();
        double min = sourceMin + position * (targetMin - sourceMin);
        double max= sourceMax + position * (targetMax - sourceMax);
        return new NumericRange(min, max);
    }

    Range<?> createIntermediate(CategoryRange<?> source, CategoryRange<?> target, double position) {
        double sourceMin = source.minimum();
        double sourceMax = source.maximum();
        double targetMin = target.minimum();
        double targetMax = target.maximum();
        double min = sourceMin + position * (targetMin - sourceMin);
        double max= sourceMax + position * (targetMax - sourceMax);
        CategoryRange r = position < 0.5 ? new CategoryRange(source) : new CategoryRange(target);
        r.setMinimum(min);
        r.setMaximum(max);
        return r;
    }

    Range<?> createIntermediate(TimeRange source, TimeRange target, double position) {
        double sourceMin = source.minimum();
        double sourceMax = source.maximum();
        double targetMin = target.minimum();
        double targetMax = target.maximum();
        double min = sourceMin + position * (targetMin - sourceMin);
        double max= sourceMax + position * (targetMax - sourceMax);
        return new TimeRange((long) min, (long) max);
    }

    /**
     * Stops the morphing.
     */
    private void stopAnimation() {
        if (timer != null && timer.isRunning()) {
            timer.stop();
            stepsLeft = numSteps;
        }
    }

}

