/*
 * @(#)Flashable.java 3/16/2007
 *
 * Copyright 2002 - 2007 JIDE Software Inc. All rights reserved.
 */

package com.jidesoft.swing;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * <code>Flashable</code> is a basic interface to enable flashing in any component. Internally it uses {@link Animator}
 * to create the flashing effect.
 * <p/>
 * Whenever a Flashable is installed to a JComponent, you can always use {@link #isFlashableInstalled(javax.swing.JComponent)}
 * to check if it is installed.
 */
public abstract class Flashable {
    public static final String CLIENT_PROPERTY_FLASHABLE = "jide.flashable";
    private int _interval = 500;

    protected JComponent _component;

    protected Animator _animator;
    protected Timer _timer = null;

    private static boolean _synchronizedFlashFlag;
    private static Timer _synchronizedFlashTimer;

    public Flashable(JComponent component) {
        _component = component;
        install(_component);
    }

    static class FlashTimer extends Timer implements ActionListener {
        public FlashTimer(int delay, ActionListener listener) {
            super(delay, listener);
            addActionListener(this);
        }

        public void actionPerformed(ActionEvent e) {
            _synchronizedFlashFlag = !_synchronizedFlashFlag;
        }
    }

    /**
     * Gets the flash flag. We have an internal timer which sets this flag value. All Flashables will use the same flash
     * flag so that they are all in sync when flashing.
     *
     * @return true or false. True means the flash is on and false means flash is off.
     */
    public static boolean getSynchronizedFlashFlag() {
        return _synchronizedFlashFlag;
    }

    private void install(JComponent component) {
        _animator = new Animator(component, 0, getInterval(), -1) {
            @Override
            protected Timer createTimer(int delay, ActionListener listener) {
                if (_synchronizedFlashTimer == null) {
                    _synchronizedFlashTimer = new FlashTimer(delay, listener);
                }
                _synchronizedFlashTimer.addActionListener(listener);
                return _synchronizedFlashTimer;
            }
        };
        _animator.addAnimatorListener(new AnimatorListener() {
            public void animationStarts(Component component) {

            }

            public void animationFrame(Component component, int totalStep, int step) {
                flash();
            }

            public void animationEnds(Component component) {

            }
        });
        component.putClientProperty(CLIENT_PROPERTY_FLASHABLE, this);
    }

    /**
     * Gets the table that will flash.
     *
     * @return the table.
     */
    public JComponent getComponent() {
        return _component;
    }

    /**
     * Sets the table that will flash.
     *
     * @param component the new table.
     */
    public void setComponent(JComponent component) {
        JComponent old = _component;
        if (old != component) {
            _component.putClientProperty(CLIENT_PROPERTY_FLASHABLE, null);
            _component = component;
            _component.putClientProperty(CLIENT_PROPERTY_FLASHABLE, this);
        }
    }

    /**
     * Gets the interval, in ms.
     *
     * @return the interval.
     */
    public int getInterval() {
        return _interval;
    }

    /**
     * Sets the interval, in ms. If the flashing is running, the new interval will take effect immediately. By default,
     * it is 300 ms.
     *
     * @param interval the new interval.
     */
    public void setInterval(int interval) {
        int old = _interval;
        if (old != interval) {
            _interval = interval;
            getAnimator().setDelay(interval);
        }
    }


    /**
     * This method actually does the flashing. This method is called in the actionPerformed of the timer.
     */
    abstract public void flash();

    /**
     * Clears any flashing effect. This method will be called in {@link #startFlashing()} and {@link #stopFlashing()}.
     */
    abstract public void clearFlashing();

    protected Animator getAnimator() {
        return _animator;
    }

    /**
     * Starts flashing.
     */
    public void startFlashing() {
        clearFlashing();
        getAnimator().start();
    }

    /**
     * Stops flashing.
     */
    public void stopFlashing() {
        clearFlashing();
        getAnimator().stop();
    }

    /**
     * Uninstalls the <code>Flashable</code> from the component. Once uninstalled, you have to create a new Flashable in
     * order to use thflashingng feature again. If you just want to stop flashing, you should use {@link
     * #stopFlashing()}.
     */
    public void uninstall() {
        stopFlashing();
        _animator.dispose();
        _animator = null;
        getComponent().putClientProperty(CLIENT_PROPERTY_FLASHABLE, null);
    }

    /**
     * Checks if it is flashing.
     *
     * @return true if flashing.
     */
    public boolean isFlashing() {
        return getAnimator().isRunning();
    }

    /**
     * Checks if there is a Flashable installed on the component.
     *
     * @param component the component.
     * @return true if installed.
     */
    public static boolean isFlashableInstalled(JComponent component) {
        Object flashable = component.getClientProperty(CLIENT_PROPERTY_FLASHABLE);
        return flashable instanceof Flashable;
    }

    /**
     * Gets the TableFlashable installed on the table, if any.
     *
     * @param component the component.
     * @return whether a Flashable is installed.
     */
    public static Flashable getFlashable(JComponent component) {
        Object flashable = component.getClientProperty(CLIENT_PROPERTY_FLASHABLE);
        if (flashable instanceof Flashable) {
            return ((Flashable) flashable);
        }
        else {
            return null;
        }
    }
}
