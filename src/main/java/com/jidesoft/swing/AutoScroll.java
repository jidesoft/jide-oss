/*
 * @(#)AutoScroll.java 12/7/2006
 *
 * Copyright 2002 - 2006 JIDE Software Inc. All rights reserved.
 */

package com.jidesoft.swing;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;

/**
 * Please note: we are still polishing this class and may change the public interface in the future. Please do not use
 * it for now until we remove this notice. You may choose to use it as long as you don't complain when you find your
 * code won't compile after upgrading to a new JIDE release.
 */
abstract public class AutoScroll {
    public static final String CLIENT_PROPERTY_AUTO_SCROLL = "AutoScroll";
    protected Timer _timer;
    protected boolean _autoScrolling = false;
    protected int _scrollDirection = SCROLL_UP;
    protected boolean _hasEntered;

    public static final int SCROLL_UP = 0;
    public static final int SCROLL_DOWN = 1;
    public static final int SCROLL_LEFT = 2;
    public static final int SCROLL_RIGHT = 4;

    protected Component _component;
    protected boolean _vertical = true;

    protected int _autoScrollInterval = 100;
    private boolean _componentSelfScrollable = true;

    private boolean _enabled = true;

    protected AutoScroll(Component component) {
        _component = component;
        updateClientProperty(component, this);
    }

    protected AutoScroll(Component component, boolean vertical) {
        _component = component;
        _vertical = vertical;
        updateClientProperty(component, this);
    }

    public int getAutoScrollInterval() {
        return _autoScrollInterval;
    }

    public void setAutoScrollInterval(int autoScrollInterval) {
        _autoScrollInterval = autoScrollInterval;
    }

    /**
     * Checks if the AutoScroll is enabled.
     *
     * @return true if enabled. Otherwise false.
     */
    public boolean isEnabled() {
        return _enabled;
    }

    /**
     * Enables or disables the AutoScroll feature.
     *
     * @param enabled true or false.
     */
    public void setEnabled(boolean enabled) {
        _enabled = enabled;
    }

    private class AutoScrollActionHandler implements ActionListener {
        private int _direction;

        AutoScrollActionHandler(int direction) {
            _direction = direction;
        }

        public void actionPerformed(ActionEvent e) {
            autoScrolling(_direction);
        }
    }

    public void startAutoScrolling(int direction) {
        if (_autoScrolling) {
            _timer.stop();
        }

        _autoScrolling = true;
        _scrollDirection = direction;
        autoScrollingStarted(_scrollDirection);
        _timer = new Timer(_autoScrollInterval, new AutoScrollActionHandler(_scrollDirection));
        _timer.start();
    }

    /**
     * This protected method is implementation specific and should be private. do not call or override.
     */
    public void stopAutoScrolling() {
        _autoScrolling = false;

        if (_timer != null) {
            _timer.stop();
            _timer = null;
        }

        autoScrollingEnded(_scrollDirection);
    }

    public boolean isAutoScrolling() {
        return _autoScrolling;
    }

    public int getScrollDirection() {
        return _scrollDirection;
    }

    public boolean isComponentSelfScrollable() {
        return _componentSelfScrollable;
    }

    public void setComponentSelfScrollable(boolean scrollable) {
        _componentSelfScrollable = scrollable;
    }

    protected MouseEvent convertMouseEvent(MouseEvent e) {
        if (e.getSource() == _component) {
            return e;
        }

        Point convertedPoint = SwingUtilities.convertPoint((Component) e.getSource(),
                e.getPoint(), _component);
        return new MouseEvent((Component) e.getSource(),
                e.getID(),
                e.getWhen(),
                e.getModifiers(),
                convertedPoint.x,
                convertedPoint.y,
                e.getClickCount(),
                e.isPopupTrigger());
    }


    public void mouseReleased(MouseEvent e) {
        if (!isEnabled()) {
            return;
        }
        _hasEntered = false;
        stopAutoScrolling();
    }

    public void mousePressed(MouseEvent e) {
        if (!isEnabled()) {
            return;
        }
        stopAutoScrolling();
    }

    public void mouseDragged(MouseEvent e) {
        if (!isEnabled()) {
            return;
        }
        if (_componentSelfScrollable && e.getSource() == _component) {
            return;
        }
        if (_component.isVisible()) {
            MouseEvent newEvent = convertMouseEvent(e);
            Rectangle r = new Rectangle();
            if (_component instanceof JComponent) {
                ((JComponent) _component).computeVisibleRect(r);
            }
            else {
                r = _component.getBounds();
            }
            if (newEvent.getPoint().y >= r.y && newEvent.getPoint().y <= r.y + r.height - 1 &&
                    newEvent.getPoint().x >= r.x && newEvent.getPoint().x <= r.x + r.width - 1) {
                _hasEntered = true;
                if (_autoScrolling) {
                    stopAutoScrolling();
                }
                Point location = newEvent.getPoint();
                if (r.contains(location)) {
                    updateSelectionForEvent(newEvent, false);
                }
            }
            else {
                if (_hasEntered) {
                    int directionToScroll;
                    if (newEvent.getPoint().y < r.y) {
                        directionToScroll = SCROLL_UP;
                    }
                    else if (newEvent.getPoint().x < r.x) {
                        directionToScroll = SCROLL_LEFT;
                    }
                    else if (newEvent.getPoint().y > r.y + r.height) {
                        directionToScroll = SCROLL_DOWN;
                    }
                    else /*if(e.getPoint().x > r.x + r.width)*/ {
                        directionToScroll = SCROLL_RIGHT;
                    }

                    if (_autoScrolling && _scrollDirection != directionToScroll) {
                        stopAutoScrolling();
                        startAutoScrolling(directionToScroll);
                    }
                    else if (!_autoScrolling) {
                        startAutoScrolling(directionToScroll);
                    }
                }
            }
        }
    }

    public void mouseMoved(MouseEvent e) {
        if (!isEnabled()) {
            return;
        }
        if (e.getSource() == _component) {
            Point location = e.getPoint();
            Rectangle r = new Rectangle();
            if (_component instanceof JComponent) {
                ((JComponent) _component).computeVisibleRect(r);
                if (r.contains(location)) {
                    updateSelectionForEvent(e, false);
                }
            }
            else {
                updateSelectionForEvent(e, false);
            }
        }
    }

    public void autoScrollingStarted(int direction) {
    }

    public void autoScrollingEnded(int direction) {
    }

    abstract public void autoScrolling(int direction);

    abstract public void updateSelectionForEvent(MouseEvent e, boolean shouldScroll);

    /**
     * Gets the AutoScroll installed on the component. Null is no AutoScroll was installed.
     *
     * @param component the component
     * @return the AutoScroll installed. Null is no AutoScroll was installed.
     */
    public static AutoScroll getAutoScroll(Component component) {
        Object clientProperty = component instanceof JComponent ? ((JComponent) component).getClientProperty(CLIENT_PROPERTY_AUTO_SCROLL) : null;
        if (clientProperty instanceof AutoScroll) {
            return ((AutoScroll) clientProperty);
        }
        else {
            return null;
        }
    }

    private void updateClientProperty(Component component, AutoScroll autoScroll) {
        if (component != null) {
            Object clientProperty = ((JComponent) _component).getClientProperty(CLIENT_PROPERTY_AUTO_SCROLL);
            if (clientProperty instanceof AutoScroll) {
                ((AutoScroll) clientProperty).stopAutoScrolling();
            }
            if (component instanceof JComponent) {
                ((JComponent) component).putClientProperty(CLIENT_PROPERTY_AUTO_SCROLL, autoScroll);
            }
        }
    }
}
