/*
 * @(#)MarqueePane.java 6/9/2009
 *
 * Copyright 2002 - 2009 JIDE Software Inc. All rights reserved.
 *
 */

package com.jidesoft.swing;

import javax.swing.*;
import java.awt.*;

/**
 * <code>MarqueePane</code> is a subclass of <code>JScrollPane</code> with automation of scrolling. In <code>MarqueePane</code>, you can
 * define the direction you want the component inside the <code>MarqueePane</code> to scroll to and the scrolling speed.
 */
public class MarqueePane extends JScrollPane {
    private int _scrollFrequency = 100;
    private int _freezingTimeReachingEnd = 500;
    private int _stepSize = 2;
    private boolean _startOver = false;
    private int _scrollDirection = SCROLL_LEFT;

    public static final int SCROLL_LEFT = 0;
    public static final int SCROLL_RIGHT = 1;
    public static final int SCROLL_UP = 2;
    public static final int SCROLL_DOWN = 3;

    public MarqueePane(Component view, int vsbPolicy, int hsbPolicy) {
        super(view, vsbPolicy, hsbPolicy);
    }

    public MarqueePane(Component view) {
        super(view);
    }

    public MarqueePane(int vsbPolicy, int hsbPolicy) {
        super(vsbPolicy, hsbPolicy);
    }

    public MarqueePane() {
        super();
    }

    @Override
    public void updateUI() {
        super.updateUI();
        startTimer();
        setViewportBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        setVerticalScrollBarPolicy(VERTICAL_SCROLLBAR_NEVER);
        setHorizontalScrollBarPolicy(HORIZONTAL_SCROLLBAR_NEVER);
        setWheelScrollingEnabled(false);
    }

    /**
     * Get the scroll frequency which indicating how frequent the Marquee will get repaint.
     * <p/>
     * The default value is 100ms.
     *
     * @return the scroll frequency.
     */
    public int getScrollFrequency() {
        return _scrollFrequency;
    }

    /**
     * Set the scroll frequency which indicating how frequent the Marquee will get repaint.
     *
     * @param scrollFrequency the scroll frequency
     */
    public void setScrollFrequency(int scrollFrequency) {
        _scrollFrequency = scrollFrequency;
    }

    /**
     * Get the step size each repaint will scroll.
     * <p/>
     * The default value is 2.
     *
     * @return the step size.
     */
    public int getStepSize() {
        return _stepSize;
    }

    /**
     * Set the step size each repaint will scroll.
     *
     * @param stepSize the step size
     */
    public void setStepSize(int stepSize) {
        _stepSize = stepSize;
    }

    /**
     * Get the scroll direction.
     * <p/>
     * The value could be <code>SCROLL_LEFT</code>, <code>SCROLL_RIGHT</code>, <code>SCROLL_UP</code>, <code>SCROLL_DOWN</code>
     * <p/>
     * The default value is <code>SCROLL_LEFT</code>.
     *
     * @return the scroll direction.
     */
    public int getScrollDirection() {
        return _scrollDirection;
    }

    /**
     * set the scroll direction.
     *
     * @param scrollDirection the scroll direction
     */
    public void setScrollDirection(int scrollDirection) {
        _scrollDirection = scrollDirection;
    }

    /**
     * Get freezing time while scrolling reaches the end.
     * <p/>
     * The default value is 500ms.
     *
     * @return the freezing time.
     */
    public int getFreezingTimeReachingEnd() {
        return _freezingTimeReachingEnd;
    }

    /**
     * Set freezing time while scrolling reaches the end.
     *
     * @param freezingTimeReachingEnd the freezing time
     */
    public void setFreezingTimeReachingEnd(int freezingTimeReachingEnd) {
        _freezingTimeReachingEnd = freezingTimeReachingEnd;
    }

    private void startTimer() {
        Thread updateThread = new Thread(new Runnable() {
            public void run() {
                while (true) {
                    javax.swing.SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            BoundedRangeModel rangeModel;
                            if (getScrollDirection() == SCROLL_LEFT || getScrollDirection() == SCROLL_RIGHT) {
                                rangeModel = getHorizontalScrollBar().getModel();
                            }
                            else {
                                rangeModel = getVerticalScrollBar().getModel();
                            }
                            int value = rangeModel.getValue();
                            if (getScrollDirection() == SCROLL_LEFT || getScrollDirection() == SCROLL_UP) {
                                if (value + _stepSize + rangeModel.getExtent() >= rangeModel.getMaximum()) {
                                    rangeModel.setValue(0);
                                }
                                else {
                                    rangeModel.setValue(value + _stepSize);
                                }
                                _startOver = rangeModel.getValue() + 2 * _stepSize + rangeModel.getExtent() >= rangeModel.getMaximum();
                            }
                            else {
                                if (value - _stepSize <= rangeModel.getMinimum()) {
                                    rangeModel.setValue(rangeModel.getMaximum() - rangeModel.getExtent());
                                }
                                else {
                                    rangeModel.setValue(value - _stepSize);
                                }
                                _startOver = rangeModel.getValue() - 2 * _stepSize <= rangeModel.getMinimum();
                            }
                        }
                    });
                    try {
                        Thread.sleep(_startOver ? getFreezingTimeReachingEnd() : getScrollFrequency());
                    }
                    catch (Exception exc) {
                        // empty
                    }
                }
            }
        }, "updateThread");
        updateThread.start();
    }
}
