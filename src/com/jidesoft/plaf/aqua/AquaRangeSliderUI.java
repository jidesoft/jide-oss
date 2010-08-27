/*
 * @(#)AquaRangeSliderUI.java 7/14/2010
 *
 * Copyright 2002 - 2010 JIDE Software Inc. All rights reserved.
 */

package com.jidesoft.plaf.aqua;

import com.apple.laf.AquaSliderUI;
import com.jidesoft.swing.RangeSlider;

import javax.swing.*;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicSliderUI;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.lang.reflect.Field;

public class AquaRangeSliderUI extends AquaSliderUI {
    public AquaRangeSliderUI(JSlider jSlider) {
        super(jSlider);
    }

    public static ComponentUI createUI(JComponent c) {
        return new AquaRangeSliderUI((JSlider) c);
    }

    @Override
    public void paint(Graphics g, JComponent c) {
        second = false;
        super.paint(g, c);

        Rectangle clip = g.getClipBounds();

        second = true;
        Point p = adjustThumbForHighValue();

        if (clip.intersects(thumbRect)) {
            apple.laf.CoreUIConstants.Orientation orientation = slider.getOrientation() != 0 ? apple.laf.CoreUIConstants.Orientation.VERTICAL : apple.laf.CoreUIConstants.Orientation.HORIZONTAL;
            apple.laf.CoreUIConstants.State state = getState();
            paintThumb(g, c, orientation, state);
        }

        restoreThumbForLowValue(p);
        second = false;
    }

    protected static boolean isActive(JComponent jcomponent) {
        if (jcomponent == null)
            return true;
        Object obj = jcomponent.getClientProperty("Frame.active");
        return !Boolean.FALSE.equals(obj);
    }


    apple.laf.CoreUIConstants.State getState() {
        if (!slider.isEnabled())
            return apple.laf.CoreUIConstants.State.DISABLED;
        if (fIsDragging)
            return apple.laf.CoreUIConstants.State.PRESSED;
        if (!isActive(slider))
            return apple.laf.CoreUIConstants.State.INACTIVE;
        else
            return apple.laf.CoreUIConstants.State.ACTIVE;
    }

    protected void restoreThumbForLowValue(Point p) {
        thumbRect.x = p.x;
        thumbRect.y = p.y;
    }

    protected Point adjustThumbForHighValue() {
        Point p = thumbRect.getLocation();
        if (slider.getOrientation() == JSlider.HORIZONTAL) {
            int valuePosition = xPositionForValue(((RangeSlider) slider).getHighValue());
            thumbRect.x = valuePosition - (thumbRect.width / 2);
        }
        else {
            int valuePosition = yPositionForValue(((RangeSlider) slider).getHighValue());
            thumbRect.y = valuePosition - (thumbRect.height / 2);
        }
        return p;
    }

    protected void adjustSnapHighValue() {
        int sliderValue = ((RangeSlider) slider).getHighValue();
        int snappedValue = sliderValue;
        int majorTickSpacing = slider.getMajorTickSpacing();
        int minorTickSpacing = slider.getMinorTickSpacing();
        int tickSpacing = 0;

        if (minorTickSpacing > 0) {
            tickSpacing = minorTickSpacing;
        }
        else if (majorTickSpacing > 0) {
            tickSpacing = majorTickSpacing;
        }

        if (tickSpacing != 0) {
            // If it's not on a tick, change the value
            if ((sliderValue - slider.getMinimum()) % tickSpacing != 0) {
                float temp = (float) (sliderValue - slider.getMinimum())
                        / (float) tickSpacing;
                int whichTick = Math.round(temp);
                snappedValue =
                        slider.getMinimum() + (whichTick * tickSpacing);
            }

            if (snappedValue != sliderValue) {
                ((RangeSlider) slider).setHighValue(snappedValue);
            }
        }
    }

    @Override
    protected void calculateThumbLocation() {
        if (slider.getSnapToTicks()) {
            adjustSnapHighValue();
        }
        super.calculateThumbLocation();
    }

    @Override
    protected BasicSliderUI.TrackListener createTrackListener(JSlider slider) {
        return new RangeTrackListener();
    }

    protected class RangeTrackListener extends BasicSliderUI.TrackListener {
        int handle;
        int handleOffset;
        int mouseStartLocation;

        public RangeTrackListener() {

        }

        /**
         * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
         */
        @Override
        public void mousePressed(MouseEvent e) {
            if (!slider.isEnabled()) {
                return;
            }

            if (slider.isRequestFocusEnabled()) {
                slider.requestFocus();
            }

            handle = getMouseHandle(e.getX(), e.getY());
            setMousePressed(handle);

            handleOffset = (slider.getOrientation() == JSlider.VERTICAL) ?
                    e.getY() - yPositionForValue(((RangeSlider) slider).getLowValue()) :
                    e.getX() - xPositionForValue(((RangeSlider) slider).getLowValue());

            mouseStartLocation = (slider.getOrientation() == JSlider.VERTICAL) ? e.getY() : e.getX();

            slider.getModel().setValueIsAdjusting(true);
        }

        /**
         * @see java.awt.event.MouseMotionListener#mouseDragged(java.awt.event.MouseEvent)
         */
        @Override
        public void mouseDragged(MouseEvent e) {
            if (!slider.isEnabled()) {
                return;
            }

            int newLocation = (slider.getOrientation() == JSlider.VERTICAL) ? e.getY() : e.getX();

            int newValue = (slider.getOrientation() == JSlider.VERTICAL) ? valueForYPosition(newLocation) : valueForXPosition(newLocation);

            if (newValue < slider.getModel().getMinimum()) {
                newValue = slider.getModel().getMinimum();
            }

            if (newValue > slider.getModel().getMaximum()) {
                newValue = slider.getModel().getMaximum();
            }

            if (handle == (MOUSE_HANDLE_MIN | MOUSE_HANDLE_MAX)) {
                if ((newLocation - mouseStartLocation) > 2) {
                    handle = MOUSE_HANDLE_MAX;
                }
                else if ((newLocation - mouseStartLocation) < -2) {
                    handle = MOUSE_HANDLE_MIN;
                }
                else {
                    return;
                }
            }

            RangeSlider rangeSlider = (RangeSlider) slider;
            switch (handle) {
                case MOUSE_HANDLE_MIN:
                    rangeSlider.setLowValue(Math.min(newValue, rangeSlider.getHighValue()));
                    break;
                case MOUSE_HANDLE_MAX:
                    rangeSlider.setHighValue(Math.max(rangeSlider.getLowValue(), newValue));
                    break;
                case MOUSE_HANDLE_MIDDLE:
                    if (((RangeSlider) slider).isRangeDraggable()) {
                        int delta = (slider.getOrientation() == JSlider.VERTICAL) ?
                                valueForYPosition(newLocation - handleOffset) - rangeSlider.getLowValue() :
                                valueForXPosition(newLocation - handleOffset) - rangeSlider.getLowValue();
                        if ((delta < 0) && ((rangeSlider.getLowValue() + delta) < rangeSlider.getMinimum())) {
                            delta = rangeSlider.getMinimum() - rangeSlider.getLowValue();
                        }

                        if ((delta > 0) && ((rangeSlider.getHighValue() + delta) > rangeSlider.getMaximum())) {
                            delta = rangeSlider.getMaximum() - rangeSlider.getHighValue();
                        }

                        if (delta != 0) {
                            offset(delta);
                        }
                    }
                    break;
            }
        }

        /**
         * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
         */
        @Override
        public void mouseReleased(MouseEvent e) {
            slider.getModel().setValueIsAdjusting(false);
            setMouseReleased(handle);
        }

        private void setCursor(int c) {
            Cursor cursor = Cursor.getPredefinedCursor(c);

            if (slider.getCursor() != cursor) {
                slider.setCursor(cursor);
            }
        }

        /**
         * @see java.awt.event.MouseMotionListener#mouseMoved(java.awt.event.MouseEvent)
         */
        @Override
        public void mouseMoved(MouseEvent e) {
            if (!slider.isEnabled()) {
                return;
            }

            int handle = getMouseHandle(e.getX(), e.getY());
            setMouseRollover(handle);
            switch (handle) {
                case MOUSE_HANDLE_MIN:
                    setCursor(Cursor.DEFAULT_CURSOR);
                    break;
                case MOUSE_HANDLE_MAX:
                    setCursor(Cursor.DEFAULT_CURSOR);
                    break;
                case MOUSE_HANDLE_MIDDLE:
                    setCursor(Cursor.MOVE_CURSOR);
                    break;
                case MOUSE_HANDLE_NONE:
                default:
                    setCursor(Cursor.DEFAULT_CURSOR);
                    break;
            }
        }

        /**
         * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
         */
        @Override
        public void mouseClicked(MouseEvent e) {
            if (e.getClickCount() == 2) {
                slider.getModel().setValue(slider.getModel().getMinimum());
                slider.getModel().setExtent(slider.getModel().getMaximum() - slider.getModel().getMinimum());
                slider.repaint();
            }
        }

        /**
         * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
         */
        @Override
        public void mouseEntered(MouseEvent e) {
        }

        /**
         * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
         */
        @Override
        public void mouseExited(MouseEvent e) {
            setCursor(Cursor.DEFAULT_CURSOR);
        }
    }

    private void offset(int delta) {
        slider.getModel().setValue(((RangeSlider) slider).getLowValue() + delta);
    }

    protected static final int MOUSE_HANDLE_NONE = 0;

    protected static final int MOUSE_HANDLE_MIN = 1;

    protected static final int MOUSE_HANDLE_MAX = 2;

    protected static final int MOUSE_HANDLE_MIDDLE = 4;

    protected int getMouseHandle(int x, int y) {
        Rectangle rect = trackRect;

        if (thumbRect.contains(x, y)) {
            return MOUSE_HANDLE_MIN;
        }
        Point p = adjustThumbForHighValue();
        if (thumbRect.contains(x, y)) {
            restoreThumbForLowValue(p);
            return MOUSE_HANDLE_MAX;
        }
        restoreThumbForLowValue(p);

        if (slider.getOrientation() == JSlider.VERTICAL) {
            int minY = yPositionForValue(((RangeSlider) slider).getLowValue());
            int maxY = yPositionForValue(((RangeSlider) slider).getHighValue());
            Rectangle midRect = new Rectangle(rect.x, Math.min(minY, maxY) + thumbRect.height / 2, rect.width, Math.abs(maxY - minY) - thumbRect.height);
            if (((RangeSlider) slider).isRangeDraggable() && midRect.contains(x, y)) {
                return MOUSE_HANDLE_MIDDLE;
            }

            return MOUSE_HANDLE_NONE;
        }
        else {
            int minX = xPositionForValue(((RangeSlider) slider).getLowValue());
            int maxX = xPositionForValue(((RangeSlider) slider).getHighValue());

            Rectangle midRect = new Rectangle(Math.min(minX, maxX) + thumbRect.width / 2, rect.y, Math.abs(maxX - minX) - thumbRect.height, rect.height);
            if (((RangeSlider) slider).isRangeDraggable() && midRect.contains(x, y)) {
                return MOUSE_HANDLE_MIDDLE;
            }
            return MOUSE_HANDLE_NONE;
        }
    }

    private boolean second;
    private boolean rollover1;
    private boolean pressed1;
    private boolean rollover2;
    private boolean pressed2;

    @Override
    public void paintThumb(Graphics g) {
        try {
            Field field = getClass().getSuperclass().getDeclaredField("rollover");
            field.setAccessible(true);
            field.set(this, second ? rollover2 : rollover1);

            field = getClass().getSuperclass().getDeclaredField("pressed");
            field.setAccessible(true);
            field.set(this, second ? pressed2 : pressed1);
        }
        catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        super.paintThumb(g);
    }

    protected void setMouseRollover(int handle) {
        switch (handle) {
            case MOUSE_HANDLE_MIN: {
                rollover1 = true;
                rollover2 = false;
            }
            break;
            case MOUSE_HANDLE_MAX: {
                rollover2 = true;
                rollover1 = false;
            }
            break;
            case MOUSE_HANDLE_MIDDLE: {
                rollover1 = true;
                rollover2 = true;
            }
            break;
            case MOUSE_HANDLE_NONE:
                rollover1 = false;
                rollover2 = false;
                break;
        }
        slider.repaint(thumbRect);
        Point p = adjustThumbForHighValue();
        slider.repaint(thumbRect);
        restoreThumbForLowValue(p);
    }

    protected void setMousePressed(int handle) {
        switch (handle) {
            case MOUSE_HANDLE_MIN: {
                pressed1 = true;
                pressed2 = false;
            }
            break;
            case MOUSE_HANDLE_MAX: {
                pressed2 = true;
                pressed1 = false;
            }
            break;
            case MOUSE_HANDLE_MIDDLE: {
                pressed1 = true;
                pressed2 = true;
            }
            break;
            case MOUSE_HANDLE_NONE:
                pressed1 = false;
                pressed2 = false;
                break;
        }
        slider.repaint(thumbRect);
        Point p = adjustThumbForHighValue();
        slider.repaint(thumbRect);
        restoreThumbForLowValue(p);
    }

    @SuppressWarnings({"UnusedDeclaration"})
    protected void setMouseReleased(int handle) {
        pressed1 = false;
        pressed2 = false;
        slider.repaint(thumbRect);
        Point p = adjustThumbForHighValue();
        slider.repaint(thumbRect);
        restoreThumbForLowValue(p);
    }
}