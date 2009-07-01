/*
 * @(#)BasicRangeSliderUI.java 11/22/2005
 *
 * Copyright 2002 - 2005 JIDE Software Inc. All rights reserved.
 */

package com.jidesoft.plaf.basic;

import com.jidesoft.plaf.UIDefaultsLookup;
import com.jidesoft.swing.RangeSlider;

import javax.swing.*;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicSliderUI;
import java.awt.*;
import java.awt.event.MouseEvent;

/**
 * JideButtonUI implementation
 */
public class BasicRangeSliderUI extends BasicSliderUI {
    protected Icon _lowerIcon;
    protected Icon _upperIcon;
    protected Icon _middleIcon;
    protected Icon _lowerIconV;
    protected Icon _upperIconV;
    protected Icon _middleIconV;

    public BasicRangeSliderUI(JSlider slider) {
        super(slider);
    }

    // ********************************
    //          Create PLAF
    // ********************************
    public static ComponentUI createUI(JComponent slider) {
        return new BasicRangeSliderUI((JSlider) slider);
    }

    @Override
    protected void installDefaults(JSlider slider) {
        super.installDefaults(slider);
        resetAllIcons();
    }

    protected void resetAllIcons() {
        _lowerIcon = UIDefaultsLookup.getIcon("RangeSlider.lowerIcon");
        _upperIcon = UIDefaultsLookup.getIcon("RangeSlider.upperIcon");
        _middleIcon = UIDefaultsLookup.getIcon("RangeSlider.middleIcon");
        _lowerIconV = UIDefaultsLookup.getIcon("RangeSlider.lowerVIcon");
        _upperIconV = UIDefaultsLookup.getIcon("RangeSlider.upperVIcon");
        _middleIconV = UIDefaultsLookup.getIcon("RangeSlider.middleVIcon");
    }

    @Override
    public void uninstallUI(JComponent c) {
        super.uninstallUI(c);
        _lowerIcon = null;
        _upperIcon = null;
        _middleIcon = null;
        _lowerIconV = null;
        _upperIconV = null;
        _middleIconV = null;
    }

    @Override
    protected void calculateTrackRect() {
        super.calculateTrackRect();
        if (slider.getOrientation() == JSlider.VERTICAL) {
            trackRect.x += 4;
        }
        else {
            trackRect.y += 4;
        }
    }

    @Override
    protected TrackListener createTrackListener(JSlider slider) {
        return new RangeTrackListener();
    }

    protected class RangeTrackListener extends TrackListener {
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

            handle = getMouseHandle(e.getX(), e.getY());

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
                    break;
            }
        }

        /**
         * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
         */
        @Override
        public void mouseReleased(MouseEvent e) {
            slider.getModel().setValueIsAdjusting(false);
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

            switch (getMouseHandle(e.getX(), e.getY())) {
                case MOUSE_HANDLE_MIN:
                    setMouseRollover(MOUSE_HANDLE_MIN);
                    setCursor((slider.getOrientation() == JSlider.VERTICAL) ? Cursor.N_RESIZE_CURSOR : Cursor.W_RESIZE_CURSOR);
                    break;
                case MOUSE_HANDLE_MAX:
                    setMouseRollover(MOUSE_HANDLE_MAX);
                    setCursor((slider.getOrientation() == JSlider.VERTICAL) ? Cursor.S_RESIZE_CURSOR : Cursor.E_RESIZE_CURSOR);
                    break;
                case MOUSE_HANDLE_MIDDLE:
                    setMouseRollover(MOUSE_HANDLE_MIDDLE);
                    setCursor(Cursor.MOVE_CURSOR);
                    break;
                case MOUSE_HANDLE_NONE:
                    setMouseRollover(MOUSE_HANDLE_NONE);
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

        if (slider.getOrientation() == JSlider.VERTICAL) {
            int minY = yPositionForValue(((RangeSlider) slider).getLowValue());
            int maxY = yPositionForValue(((RangeSlider) slider).getHighValue());

            Rectangle minRect = new Rectangle(rect.x + rect.width / 2, minY - _lowerIconV.getIconHeight() / 2, _lowerIcon.getIconWidth(), _lowerIconV.getIconHeight());
            if (minRect.contains(x, y)) {
                return MOUSE_HANDLE_MIN;
            }
            Rectangle maxRect = new Rectangle(rect.x, maxY - _upperIconV.getIconHeight() / 2, _upperIconV.getIconWidth(), _upperIconV.getIconHeight());
            if (maxRect.contains(x, y)) {
                return MOUSE_HANDLE_MAX;
            }

            Rectangle midRect = new Rectangle(rect.x - _middleIcon.getIconWidth(), maxY - _middleIconV.getIconHeight() / 2, _middleIconV.getIconWidth(), _middleIconV.getIconHeight());
            if (midRect.contains(x, y)) {
                return MOUSE_HANDLE_MIDDLE;
            }
            return MOUSE_HANDLE_NONE;
        }
        else {
            int minX = xPositionForValue(((RangeSlider) slider).getLowValue());
            int maxX = xPositionForValue(((RangeSlider) slider).getHighValue());

            Rectangle minRect = new Rectangle(minX - _lowerIcon.getIconWidth() / 2, rect.y + rect.height / 2, _lowerIcon.getIconWidth(), _lowerIcon.getIconHeight());
            if (minRect.contains(x, y)) {
                return MOUSE_HANDLE_MIN;
            }
            Rectangle maxRect = new Rectangle(maxX - _upperIcon.getIconWidth() / 2, rect.y, _upperIcon.getIconWidth(), _upperIcon.getIconHeight());
            if (maxRect.contains(x, y)) {
                return MOUSE_HANDLE_MAX;
            }

            Rectangle midRect = new Rectangle(maxX - _middleIcon.getIconWidth() / 2, rect.y - _middleIcon.getIconHeight(), _middleIcon.getIconWidth(), _middleIcon.getIconHeight());
            if (midRect.contains(x, y)) {
                return MOUSE_HANDLE_MIDDLE;
            }
            return MOUSE_HANDLE_NONE;
        }
    }

    @Override
    public void paintThumb(Graphics g) {
        Rectangle rect = trackRect;

        RangeSlider rangeSlider = (RangeSlider) slider;

        if (slider.getOrientation() == JSlider.VERTICAL) {
            int minY = yPositionForValue(rangeSlider.getLowValue());
            int maxY = yPositionForValue(rangeSlider.getHighValue());

            _lowerIconV.paintIcon(slider, g, rect.x + rect.width / 2, minY - _lowerIconV.getIconHeight() / 2);
            _upperIconV.paintIcon(slider, g, rect.x, maxY - _upperIconV.getIconHeight() / 2);
            _middleIconV.paintIcon(slider, g, rect.x - _middleIconV.getIconWidth(), maxY - _middleIconV.getIconHeight() / 2);
        }
        else {
            int minX = xPositionForValue(rangeSlider.getLowValue());
            int maxX = xPositionForValue(rangeSlider.getHighValue());

            _lowerIcon.paintIcon(slider, g, minX - _lowerIcon.getIconWidth() / 2, rect.y + rect.height / 2);
            _upperIcon.paintIcon(slider, g, maxX - _upperIcon.getIconWidth() / 2, rect.y);
            _middleIcon.paintIcon(slider, g, maxX - _middleIcon.getIconWidth() / 2, rect.y - _middleIcon.getIconHeight());
        }
    }

    protected void setMouseRollover(int handle) {
        resetAllIcons();
        switch (handle) {
            case MOUSE_HANDLE_MIN: {
                Icon icon = UIDefaultsLookup.getIcon("RangeSlider.lowerRIcon");
                if (icon != null) _lowerIcon = icon;
                icon = UIDefaultsLookup.getIcon("RangeSlider.lowerVRIcon");
                if (icon != null) _lowerIconV = icon;
            }
            break;
            case MOUSE_HANDLE_MAX: {
                Icon icon = UIDefaultsLookup.getIcon("RangeSlider.upperRIcon");
                if (icon != null) _upperIcon = icon;
                icon = UIDefaultsLookup.getIcon("RangeSlider.upperVRIcon");
                if (icon != null) _upperIconV = icon;
            }
            break;
            case MOUSE_HANDLE_MIDDLE: {
                Icon icon = UIDefaultsLookup.getIcon("RangeSlider.middleRIcon");
                if (icon != null) _middleIcon = icon;
                icon = UIDefaultsLookup.getIcon("RangeSlider.middleVRIcon");
                if (icon != null) _middleIconV = icon;
            }
            break;
            case MOUSE_HANDLE_NONE:
                break;
        }
        slider.repaint();
    }
}
