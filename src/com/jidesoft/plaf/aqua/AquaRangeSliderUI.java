/*
 * @(#)AquaRangeSliderUI.java 7/14/2010
 *
 * Copyright 2002 - 2010 JIDE Software Inc. All rights reserved.
 */

package com.jidesoft.plaf.aqua;

import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.lang.reflect.Field;

import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JSlider;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicSliderUI;

import com.apple.laf.AquaSliderUI;
import com.jidesoft.swing.RangeSlider;

import sun.swing.UIAction;

public class AquaRangeSliderUI extends AquaSliderUI {
    public AquaRangeSliderUI(JSlider jSlider) {
        super(jSlider);
    }

    public static ComponentUI createUI(JComponent c) {
        return new AquaRangeSliderUI((JSlider) c);
    }
    protected void installKeyboardActions( JSlider slider ) {
        super.installKeyboardActions(slider);
        installAdditionalKeyboardActions(slider);
    }

    protected void installAdditionalKeyboardActions(JSlider slider) {
        final InputMap km = SwingUtilities.getUIInputMap(slider, JComponent.WHEN_FOCUSED);
        final ActionMap am = SwingUtilities.getUIActionMap(slider);

        final Actions actionPositiveUnitIncrementUpper = new Actions(Actions.POSITIVE_UNIT_INCREMENT_UPPER);
        final Actions actionPositiveBlockIncrementUpper = new Actions(Actions.POSITIVE_BLOCK_INCREMENT_UPPER);
        final Actions actionNegativeUnitIncrementUpper = new Actions(Actions.NEGATIVE_UNIT_INCREMENT_UPPER);
        final Actions actionNegativeBlockIncrementUpper = new Actions(Actions.NEGATIVE_BLOCK_INCREMENT_UPPER);
        final Actions actionMinScrollIncrementUpper = new Actions(Actions.MIN_SCROLL_INCREMENT_UPPER);
        final Actions actionMaxScrollIncrementUpper = new Actions(Actions.MAX_SCROLL_INCREMENT_UPPER);

        registerAction(actionPositiveUnitIncrementUpper, KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, InputEvent.SHIFT_DOWN_MASK), km, am);
        registerAction(actionPositiveUnitIncrementUpper, KeyStroke.getKeyStroke(KeyEvent.VK_KP_RIGHT, InputEvent.SHIFT_DOWN_MASK), km, am);
        registerAction(actionPositiveUnitIncrementUpper, KeyStroke.getKeyStroke(KeyEvent.VK_UP, InputEvent.SHIFT_DOWN_MASK), km, am);
        registerAction(actionPositiveUnitIncrementUpper, KeyStroke.getKeyStroke(KeyEvent.VK_KP_UP, InputEvent.SHIFT_DOWN_MASK), km, am);
        registerAction(actionPositiveBlockIncrementUpper, KeyStroke.getKeyStroke(KeyEvent.VK_PAGE_UP, InputEvent.SHIFT_DOWN_MASK), km, am);

        registerAction(actionNegativeUnitIncrementUpper, KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, InputEvent.SHIFT_DOWN_MASK), km, am);
        registerAction(actionNegativeUnitIncrementUpper, KeyStroke.getKeyStroke(KeyEvent.VK_KP_LEFT, InputEvent.SHIFT_DOWN_MASK), km, am);
        registerAction(actionNegativeUnitIncrementUpper, KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, InputEvent.SHIFT_DOWN_MASK), km, am);
        registerAction(actionNegativeUnitIncrementUpper, KeyStroke.getKeyStroke(KeyEvent.VK_KP_DOWN, InputEvent.SHIFT_DOWN_MASK), km, am);
        registerAction(actionNegativeBlockIncrementUpper, KeyStroke.getKeyStroke(KeyEvent.VK_PAGE_DOWN, InputEvent.SHIFT_DOWN_MASK), km, am);

        registerAction(actionMinScrollIncrementUpper, KeyStroke.getKeyStroke(KeyEvent.VK_HOME, InputEvent.SHIFT_DOWN_MASK), km, am);
        registerAction(actionMaxScrollIncrementUpper, KeyStroke.getKeyStroke(KeyEvent.VK_END, InputEvent.SHIFT_DOWN_MASK), km, am);
    }

    private void registerAction(final Actions action, final KeyStroke keyStroke, final InputMap km, final ActionMap am) {
        km.put(keyStroke, action.getValue(Action.NAME));
        am.put(action.getValue(Action.NAME), action);
    }

    private static class Actions extends UIAction {
        public static final String POSITIVE_UNIT_INCREMENT_UPPER =
                "positiveUnitIncrementUpper";
        public static final String POSITIVE_BLOCK_INCREMENT_UPPER =
                "positiveBlockIncrementUpper";
        public static final String NEGATIVE_UNIT_INCREMENT_UPPER =
                "negativeUnitIncrementUpper";
        public static final String NEGATIVE_BLOCK_INCREMENT_UPPER =
                "negativeBlockIncrementUpper";
        public static final String MIN_SCROLL_INCREMENT_UPPER = "minScrollUpper";
        public static final String MAX_SCROLL_INCREMENT_UPPER = "maxScrollUpper";

        Actions() {
            super(null);
        }

        public Actions(String name) {
            super(name);
        }

        public void actionPerformed(ActionEvent evt) {
            RangeSlider rangeSlider = (RangeSlider)evt.getSource();
            BasicSliderUI ui = (BasicSliderUI) getUIOfType(rangeSlider.getUI(), BasicSliderUI.class);
            String name = getName();

            if (ui == null) {
                return;
            }

            try {
                if (POSITIVE_UNIT_INCREMENT_UPPER == name) {
                    scroll(rangeSlider, ui, POSITIVE_SCROLL, false);
                } else if (NEGATIVE_UNIT_INCREMENT_UPPER == name) {
                    scroll(rangeSlider, ui, NEGATIVE_SCROLL, false);
                } else if (POSITIVE_BLOCK_INCREMENT_UPPER == name) {
                    scroll(rangeSlider, ui, POSITIVE_SCROLL, true);
                } else if (NEGATIVE_BLOCK_INCREMENT_UPPER == name) {
                    scroll(rangeSlider, ui, NEGATIVE_SCROLL, true);
                } else if (MIN_SCROLL_INCREMENT_UPPER == name) {
                    scroll(rangeSlider, ui, MIN_SCROLL, false);
                } else if (MAX_SCROLL_INCREMENT_UPPER == name) {
                    scroll(rangeSlider, ui, MAX_SCROLL, false);
                }
            }
            finally {
                rangeSlider.putClientProperty(RangeSlider.CLIENT_PROPERTY_MOUSE_POSITION, null);
            }
        }

        private void scroll(RangeSlider rangeSlider, BasicSliderUI ui, int direction,
                            boolean isBlock) {
            boolean invert = rangeSlider.getInverted();

            rangeSlider.putClientProperty(RangeSlider.CLIENT_PROPERTY_MOUSE_POSITION, false);

            if (direction == NEGATIVE_SCROLL || direction == POSITIVE_SCROLL) {
                if (invert) {
                    direction = (direction == POSITIVE_SCROLL) ?
                            NEGATIVE_SCROLL : POSITIVE_SCROLL;
                }

                if (isBlock) {
                    ui.scrollByBlock(direction);
                } else {
                    ui.scrollByUnit(direction);
                }
            } else {  // MIN or MAX
                if (invert) {
                    direction = (direction == MIN_SCROLL) ?
                            MAX_SCROLL : MIN_SCROLL;
                }

                rangeSlider.setHighValue((direction == MIN_SCROLL) ?
                        rangeSlider.getMinimum() : rangeSlider.getMaximum());
            }
        }

        static Object getUIOfType(ComponentUI ui, Class<?> klass) {
            if (klass.isInstance(ui)) {
                return ui;
            }
            return null;
        }
    }

    @Override
    public void paint(Graphics g, JComponent c) {
        second = false;
        super.paint(g, c);

        Rectangle clip = g.getClipBounds();

        second = true;
        Point p = adjustThumbForHighValue();

        if (clip.intersects(thumbRect)) {
            apple.laf.JRSUIConstants.Orientation orientation = slider.getOrientation() != 0 ? apple.laf.JRSUIConstants.Orientation.VERTICAL : apple.laf.JRSUIConstants.Orientation.HORIZONTAL;
            apple.laf.JRSUIConstants.State state = getState();
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


    apple.laf.JRSUIConstants.State getState() {
        if (!slider.isEnabled())
            return apple.laf.JRSUIConstants.State.DISABLED;
        if (fIsDragging)
            return apple.laf.JRSUIConstants.State.PRESSED;
        if (!isActive(slider))
            return apple.laf.JRSUIConstants.State.INACTIVE;
        else
            return apple.laf.JRSUIConstants.State.ACTIVE;
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
        return new RangeTrackListener(super.createTrackListener(slider));
    }

    protected class RangeTrackListener extends BasicSliderUI.TrackListener {
        int handle;
        int handleOffset;
        int mouseStartLocation;
        BasicSliderUI.TrackListener _listener;

        public RangeTrackListener(BasicSliderUI.TrackListener listener) {
            _listener = listener;
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

            if (handle == MOUSE_HANDLE_MAX || handle == MOUSE_HANDLE_MIN || handle == MOUSE_HANDLE_MIDDLE) {
                handleOffset = (slider.getOrientation() == JSlider.VERTICAL) ?
                        e.getY() - yPositionForValue(((RangeSlider) slider).getLowValue()) :
                        e.getX() - xPositionForValue(((RangeSlider) slider).getLowValue());

                mouseStartLocation = (slider.getOrientation() == JSlider.VERTICAL) ? e.getY() : e.getX();

                slider.getModel().setValueIsAdjusting(true);
            }
            else if (handle == MOUSE_HANDLE_LOWER || handle == MOUSE_HANDLE_UPPER) {
                _listener.mousePressed(e);
                slider.putClientProperty(RangeSlider.CLIENT_PROPERTY_MOUSE_POSITION, null);
            }
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

            if (handle == MOUSE_HANDLE_BOTH) {
                if ((newLocation - mouseStartLocation) >= 1) {
                    handle = MOUSE_HANDLE_MAX;
                }
                else if ((newLocation - mouseStartLocation) <= -1) {
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
                            rangeSlider.setLowValue(rangeSlider.getLowValue() + delta);
                            rangeSlider.setHighValue(rangeSlider.getHighValue() + delta);
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
            _listener.mouseReleased(e);
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
                case MOUSE_HANDLE_MAX:
                case MOUSE_HANDLE_BOTH:
                    setCursor(Cursor.DEFAULT_CURSOR);
                    break;
                case MOUSE_HANDLE_MIDDLE:
                    if (slider instanceof RangeSlider && ((RangeSlider) slider).isRangeDraggable()) {
                        setCursor(Cursor.MOVE_CURSOR);
                    }
                    else {
                        setCursor(Cursor.DEFAULT_CURSOR);
                    }
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
            hover = true;
            slider.repaint();
        }

        /**
         * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
         */
        @Override
        public void mouseExited(MouseEvent e) {
            hover = false;
            slider.repaint();
            setCursor(Cursor.DEFAULT_CURSOR);
        }
    }

    protected static final int MOUSE_HANDLE_NONE = 0;

    protected static final int MOUSE_HANDLE_MIN = 1;

    protected static final int MOUSE_HANDLE_MAX = 2;

    protected static final int MOUSE_HANDLE_MIDDLE = 4;

    protected static final int MOUSE_HANDLE_LOWER = 5;

    protected static final int MOUSE_HANDLE_UPPER = 6;

    protected static final int MOUSE_HANDLE_BOTH = 7;

    protected int getMouseHandle(int x, int y) {
        Rectangle rect = trackRect;

        slider.putClientProperty(RangeSlider.CLIENT_PROPERTY_MOUSE_POSITION, null);

        boolean inMin = false;
        boolean inMax = false;
        if (thumbRect.contains(x, y)) {
            inMin = true;
        }
        Point p = adjustThumbForHighValue();
        if (thumbRect.contains(x, y)) {
            inMax = true;
        }
        restoreThumbForLowValue(p);
        if (inMin && inMax) {
            return MOUSE_HANDLE_BOTH;
        }
        else if (inMin) {
            return MOUSE_HANDLE_MIN;
        }
        else if (inMax) {
            return MOUSE_HANDLE_MAX;
        }

        if (slider.getOrientation() == JSlider.VERTICAL) {
            int minY = yPositionForValue(((RangeSlider) slider).getLowValue());
            int maxY = yPositionForValue(((RangeSlider) slider).getHighValue());
            Rectangle midRect = new Rectangle(rect.x, Math.min(minY, maxY) + thumbRect.height / 2, rect.width, Math.abs(maxY - minY) - thumbRect.height);
            if (midRect.contains(x, y)) {
                return MOUSE_HANDLE_MIDDLE;
            }
            int sy = rect.y + Math.max(minY, maxY) + thumbRect.height / 2;
            Rectangle lowerRect = new Rectangle(rect.x, sy, rect.width, rect.y + rect.height - sy);
            if (lowerRect.contains(x, y)) {
                slider.putClientProperty(RangeSlider.CLIENT_PROPERTY_MOUSE_POSITION, true);
                return MOUSE_HANDLE_LOWER;
            }
            Rectangle upperRect = new Rectangle(rect.x, rect.y, rect.width, Math.min(maxY, minY) - thumbRect.height / 2);
            if (upperRect.contains(x, y)) {
                slider.putClientProperty(RangeSlider.CLIENT_PROPERTY_MOUSE_POSITION, false);
                return MOUSE_HANDLE_UPPER;
            }

            return MOUSE_HANDLE_NONE;
        }
        else {
            int minX = xPositionForValue(((RangeSlider) slider).getLowValue());
            int maxX = xPositionForValue(((RangeSlider) slider).getHighValue());

            Rectangle midRect = new Rectangle(Math.min(minX, maxX) + thumbRect.width / 2, rect.y, Math.abs(maxX - minX) - thumbRect.width, rect.height);
            if (midRect.contains(x, y)) {
                return MOUSE_HANDLE_MIDDLE;
            }
            Rectangle lowerRect = new Rectangle(rect.x, rect.y, Math.min(minX, maxX) - thumbRect.width / 2 - rect.x, rect.height);
            if (lowerRect.contains(x, y)) {
                slider.putClientProperty(RangeSlider.CLIENT_PROPERTY_MOUSE_POSITION, true);
                return MOUSE_HANDLE_LOWER;
            }
            int sx = rect.x + Math.abs(maxX - minX) + thumbRect.width / 2;
            Rectangle upperRect = new Rectangle(sx, rect.y, rect.width - sx, rect.height);
            if (upperRect.contains(x, y)) {
                slider.putClientProperty(RangeSlider.CLIENT_PROPERTY_MOUSE_POSITION, false);
                return MOUSE_HANDLE_UPPER;
            }
            return MOUSE_HANDLE_NONE;
        }
    }

    protected boolean hover;
    protected boolean second;
    protected boolean rollover1;
    protected boolean pressed1;
    protected boolean rollover2;
    protected boolean pressed2;

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
//            e.printStackTrace();
        }
        catch (IllegalAccessException e) {
//            e.printStackTrace();
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
            case MOUSE_HANDLE_MIDDLE:
            case MOUSE_HANDLE_BOTH: {
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
            case MOUSE_HANDLE_MIDDLE:
            case MOUSE_HANDLE_BOTH: {
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

    @Override
    public void scrollByBlock(int direction) {
        synchronized (slider) {

            int oldValue;
            Object clientProperty = slider.getClientProperty(RangeSlider.CLIENT_PROPERTY_MOUSE_POSITION);
            if (clientProperty == null) {
                oldValue = slider.getValue();
            }
            else if (Boolean.TRUE.equals(clientProperty)) {
                oldValue = ((RangeSlider) slider).getLowValue();
            }
            else {
                oldValue = ((RangeSlider) slider).getHighValue();
            }
            int blockIncrement =
                    (slider.getMaximum() - slider.getMinimum()) / 10;
            if (blockIncrement <= 0 &&
                    slider.getMaximum() > slider.getMinimum()) {

                blockIncrement = 1;
            }

            int delta = blockIncrement * ((direction > 0) ? POSITIVE_SCROLL : NEGATIVE_SCROLL);
            slider.putClientProperty(RangeSlider.CLIENT_PROPERTY_ADJUST_ACTION, "scrollByBlock");
            if (clientProperty == null) {
                slider.setValue(Math.max(Math.min(oldValue + delta, slider.getMaximum()), slider.getMinimum()));
            }
            else if (Boolean.TRUE.equals(clientProperty)) {
                ((RangeSlider) slider).setLowValue(Math.max(Math.min(oldValue + delta, slider.getMaximum()), slider.getMinimum()));
            }
            else {
                ((RangeSlider) slider).setHighValue(Math.max(Math.min(oldValue + delta, slider.getMaximum()), slider.getMinimum()));
            }
            slider.putClientProperty(RangeSlider.CLIENT_PROPERTY_ADJUST_ACTION, null);
        }
    }

    @Override
    public void scrollByUnit(int direction) {
        synchronized (slider) {

            int oldValue;
            Object clientProperty = slider.getClientProperty(RangeSlider.CLIENT_PROPERTY_MOUSE_POSITION);
            if (clientProperty == null) {
                oldValue = slider.getValue();
            }
            else if (Boolean.TRUE.equals(clientProperty)) {
                oldValue = ((RangeSlider) slider).getLowValue();
            }
            else {
                oldValue = ((RangeSlider) slider).getHighValue();
            }
            int delta = 1 * ((direction > 0) ? POSITIVE_SCROLL : NEGATIVE_SCROLL);

            slider.putClientProperty(RangeSlider.CLIENT_PROPERTY_ADJUST_ACTION, "scrollByUnit");
            if (clientProperty == null) {
                slider.setValue(Math.max(Math.min(oldValue + delta, slider.getMaximum()), slider.getMinimum()));
            }
            else if (Boolean.TRUE.equals(clientProperty)) {
                ((RangeSlider) slider).setLowValue(Math.max(Math.min(oldValue + delta, slider.getMaximum()), slider.getMinimum()));
            }
            else {
                ((RangeSlider) slider).setHighValue(Math.max(Math.min(oldValue + delta, slider.getMaximum()), slider.getMinimum()));
            }
            slider.putClientProperty(RangeSlider.CLIENT_PROPERTY_ADJUST_ACTION, null);
        }
    }
}
