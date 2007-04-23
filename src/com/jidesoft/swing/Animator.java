/*
 * @(#)Animator.java
 *
 * Copyright 2002 - 2003 JIDE Software Inc. All rights reserved.
 */
package com.jidesoft.swing;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * An <code>ActionListener</code> with a timer. It is used to
 * simplify the animation of all kind of sliding windows.
 */

public class Animator implements ActionListener {

    private final Component _source;

    private Timer _timer;

    private final int _totalSteps;
    private int _currentStep;

    private AnimatorListener _listener;

    /**
     * Creates an animator for source with initDelay 50 ms, each step delays 10 ms and total 10 steps.
     *
     * @param source the source for this animator.
     */
    public Animator(Component source) {
        this(source, 50, 10, 10);
    }

    /**
     * Creates an animator for source.
     *
     * @param source     the source for this animator.
     * @param initDelay  the initial delay before timer starts.
     * @param delay      the delay of the timer
     * @param totalSteps the number of steps. If -1, it means this animator will never stop until {@link #stop()} is called.
     */
    public Animator(Component source, int initDelay, int delay, int totalSteps) {
        _source = source;
        _totalSteps = totalSteps;

        _timer = createTimer(delay, this);
        _timer.setInitialDelay(initDelay);
    }

    /**
     * Creates the timer.
     *
     * @param delay    the delay between each step, in ms.
     * @param listener the action listener associated with the timer.
     * @return the timer
     */
    protected Timer createTimer(int delay, ActionListener listener) {
        return new Timer(delay, listener);
    }

    /**
     * Gets the AnimatorListener so that you can custom the behavior of the animator.
     *
     * @return the listener
     */
    public AnimatorListener getAnimatorListener() {
        return _listener;
    }

    /**
     * Sets the AnimatorListener so that you can custom the behavior of the animator.
     *
     * @param listener the <code>AnimatorListener</code>.
     */
    public void setAnimatorListener(AnimatorListener listener) {
        _listener = listener;
    }

    public void actionPerformed(ActionEvent e) {
        if (_source != null) {
            if (_listener != null) {
                _listener.animationFrame(_source, _totalSteps, _currentStep);
            }
            _currentStep++;
            if (_totalSteps != -1 && _currentStep > _totalSteps) {
                stop();
                if (_listener != null) {
                    _listener.animationEnds(_source);
                }
            }
        }
    }

    /**
     * Starts the animator.
     */
    public void start() {
        if (_listener != null) {
            _listener.animationStarts(_source);
        }
        if (_timer != null)
            _timer.start();
        _currentStep = 0;
    }

    /**
     * Stop the animator and reset the counter.
     */
    public void stop() {
        if (_timer != null) {
            _timer.stop();
        }
        _currentStep = 0;
    }

    /**
     * Interrupts the animator. The counter is not reset in this case.
     */
    public void interrupt() {
        if (_timer != null) {
            _timer.stop();
        }
    }

    /**
     * If the animator is running, returns true. Otherwise, returns false.
     *
     * @return true if animator is running. Otherwise, returns false.
     */
    public boolean isRunning() {
        return _timer != null && _timer.isRunning();
    }

    public void setDelay(int delay) {
        _timer.setDelay(delay);
    }

    public void dispose() {
        stop();
        _timer.removeActionListener(this);
        _timer = null;
    }
}
