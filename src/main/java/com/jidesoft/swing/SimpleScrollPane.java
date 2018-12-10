/*
 * @(#)FlatScrollPane.java 12/13/2006
 *
 * Copyright 2002 - 2006 JIDE Software Inc. All rights reserved.
 */

package com.jidesoft.swing;

import com.jidesoft.icons.JideIconsFactory;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.UIResource;
import java.awt.*;
import java.awt.event.*;

/**
 * <code>SimpleScrollPane</code> is a special scroll pane. There is no scroll bar. It just uses four scroll buttons to
 * do the scrolling.
 */
public class SimpleScrollPane extends JScrollPane implements ChangeListener, MouseWheelListener {

    private AbstractButton _scrollUpButton;
    private AbstractButton _scrollDownButton;

    private AbstractButton _scrollLeftButton;
    private AbstractButton _scrollRightButton;

    private int _horizontalUnitIncrement = 10;
    private boolean _horizontalUnitIncrementSet = false;

    private int _verticalUnitIncrement = 10;
    private boolean _verticalUnitIncrementSet = false;

    private int _repeatDelay = 50;
    private boolean _scrollOnRollover = true;

    public static final String SCROLL_UP_BUTTON = "SCROLL_UP_BUTTON";
    public static final String SCROLL_DOWN_BUTTON = "SCROLL_DOWN_BUTTON";
    public static final String SCROLL_LEFT_BUTTON = "SCROLL_LEFT_BUTTON";
    public static final String SCROLL_RIGHT_BUTTON = "SCROLL_RIGHT_BUTTON";

    /**
     * Creates a <code>JideScrollPane</code> that displays the view component in a viewport whose view position can be
     * controlled with a pair of scrollbars. The scrollbar policies specify when the scrollbars are displayed, For
     * example, if <code>vsbPolicy</code> is <code>VERTICAL_SCROLLBAR_AS_NEEDED</code> then the vertical scrollbar only
     * appears if the view doesn't fit vertically. The available policy settings are listed at {@link
     * #setVerticalScrollBarPolicy} and {@link #setHorizontalScrollBarPolicy}.
     *
     * @param view      the component to display in the scrollpanes viewport
     * @param vsbPolicy an integer that specifies the vertical scrollbar policy
     * @param hsbPolicy an integer that specifies the horizontal scrollbar policy
     * @see #setViewportView
     */
    public SimpleScrollPane(Component view, int vsbPolicy, int hsbPolicy) {
        setLayout(new SimpleScrollPaneLayout.UIResource());
        setVerticalScrollBarPolicy(vsbPolicy);
        setHorizontalScrollBarPolicy(hsbPolicy);
        setViewport(createViewport());
        setScrollUpButton(createScrollButton(SwingConstants.NORTH));
        setScrollDownButton(createScrollButton(SwingConstants.SOUTH));
        setScrollLeftButton(createScrollButton(SwingConstants.WEST));
        setScrollRightButton(createScrollButton(SwingConstants.EAST));
        if (null != view) {
            setViewportView(view);
        }
        updateButtonState();
        setOpaque(true);
        setFocusable(false);
        if (getHorizontalScrollBar() != null) {
            getHorizontalScrollBar().setVisible(false);
            getHorizontalScrollBar().setFocusable(false);
        }
        if (getVerticalScrollBar() != null) {
            getVerticalScrollBar().setVisible(false);
            getVerticalScrollBar().setFocusable(false);
        }
        updateUI();

        if (!getComponentOrientation().isLeftToRight()) {
            viewport.setViewPosition(new Point(Integer.MAX_VALUE, 0));
        }

        if (this.isWheelScrollingEnabled())
            this.addMouseWheelListener(this);
    }


    /**
     * Creates a <code>JideScrollPane</code> that displays the contents of the specified component, where both
     * horizontal and vertical scrollbars appear whenever the component's contents are larger than the view.
     *
     * @param view the component to display in the scrollpane's viewport
     * @see #setViewportView
     */
    public SimpleScrollPane(Component view) {
        this(view, VERTICAL_SCROLLBAR_AS_NEEDED, HORIZONTAL_SCROLLBAR_AS_NEEDED);
    }


    /**
     * Creates an empty (no viewport view) <code>JideScrollPane</code> with specified scrollbar policies. The available
     * policy settings are listed at {@link #setVerticalScrollBarPolicy} and {@link #setHorizontalScrollBarPolicy}.
     *
     * @param vsbPolicy an integer that specifies the vertical scrollbar policy
     * @param hsbPolicy an integer that specifies the horizontal scrollbar policy
     * @see #setViewportView
     */
    public SimpleScrollPane(int vsbPolicy, int hsbPolicy) {
        this(null, vsbPolicy, hsbPolicy);
    }


    /**
     * Creates an empty (no viewport view) <code>JideScrollPane</code> where both horizontal and vertical scrollbars
     * appear when needed.
     */
    public SimpleScrollPane() {
        this(null, VERTICAL_SCROLLBAR_AS_NEEDED, HORIZONTAL_SCROLLBAR_AS_NEEDED);
    }

    @Override
    public void updateUI() {
        super.updateUI();
        setLayout(new SimpleScrollPaneLayout.UIResource());
        LookAndFeel.installBorder(this, "JideScrollPane.border");
        getViewport().addChangeListener(this);
    }

    public void stateChanged(ChangeEvent e) {
        if (e.getSource() == getViewport()) {
            updateButtonState();
        }
    }

    public AbstractButton getScrollUpButton() {
        return _scrollUpButton;
    }

    public void setScrollUpButton(AbstractButton scrollUpButton) {
        AbstractButton old = getScrollUpButton();
        _scrollUpButton = scrollUpButton;
        add(_scrollUpButton, SCROLL_UP_BUTTON);
        firePropertyChange("scrollUpButton", old, _scrollUpButton);

        revalidate();
        repaint();
    }

    public AbstractButton getScrollDownButton() {
        return _scrollDownButton;
    }

    public void setScrollDownButton(AbstractButton scrollDownButton) {
        AbstractButton old = getScrollDownButton();
        _scrollDownButton = scrollDownButton;
        add(_scrollDownButton, SCROLL_DOWN_BUTTON);
        firePropertyChange("scrollDownButton", old, _scrollDownButton);

        revalidate();
        repaint();
    }

    public AbstractButton getScrollLeftButton() {
        return _scrollLeftButton;
    }

    public void setScrollLeftButton(AbstractButton scrollLeftButton) {
        AbstractButton old = getScrollLeftButton();
        _scrollLeftButton = scrollLeftButton;
        add(_scrollLeftButton, SCROLL_LEFT_BUTTON);
        firePropertyChange("scrollLeftButton", old, _scrollLeftButton);

        revalidate();
        repaint();
    }

    public AbstractButton getScrollRightButton() {
        return _scrollRightButton;
    }

    public void setScrollRightButton(AbstractButton scrollRightButton) {
        AbstractButton old = getScrollRightButton();
        _scrollRightButton = scrollRightButton;
        add(_scrollRightButton, SCROLL_RIGHT_BUTTON);
        firePropertyChange("scrollRightButton", old, _scrollRightButton);

        revalidate();
        repaint();
    }

    /**
     * The scroll button for SimpleScrollPane. You can extend this class to create your own buttons.
     */
    public class ScrollButton extends JideButton implements MouseListener, ActionListener, UIResource {
        private int _type;
        private Timer _timer;

        /**
         * Creates a ScrollButton.
         *
         * @param type one of the four values - NORTH, SOUTH, WEST, EAST as defined in SwingConstants.
         */
        public ScrollButton(int type) {
            _type = type;
            switch (type) {
                case SwingConstants.NORTH:
                    setIcon(JideIconsFactory.getImageIcon(JideIconsFactory.Arrow.UP));
                    break;
                case SwingConstants.SOUTH:
                    setIcon(JideIconsFactory.getImageIcon(JideIconsFactory.Arrow.DOWN));
                    break;
                case SwingConstants.WEST:
                    setIcon(JideIconsFactory.getImageIcon(JideIconsFactory.Arrow.LEFT));
                    break;
                case SwingConstants.EAST:
                    setIcon(JideIconsFactory.getImageIcon(JideIconsFactory.Arrow.RIGHT));
                    break;
            }
            addActionListener(this);
            addMouseListener(this);
            setPreferredSize(new Dimension(10, 10));
            setMinimumSize(new Dimension(10, 10));
        }

        public void actionPerformed(ActionEvent e) {
            scroll(getViewport(), _type);
            updateButtonState();
        }

        public void mouseClicked(MouseEvent e) {
        }

        public void mousePressed(MouseEvent e) {
            if (!isScrollOnRollover()) {
                startTimer(e, 500);
            }
            else {
                updateTimer(e);
            }
        }

        public void mouseReleased(MouseEvent e) {
            if (!isScrollOnRollover()) {
                stopTimer();
            }
            else {
                updateTimer(e);
            }
        }

        public void mouseEntered(MouseEvent e) {
            if (isScrollOnRollover()) {
                startTimer(e, 500);
            }
        }

        private void updateTimer(MouseEvent e) {
            if (_timer != null) {
                _timer.setDelay(getDelay(e));
            }
        }

        private void startTimer(MouseEvent e, int initDelay) {
            stopTimer();
            _timer = new Timer(getDelay(e), this);
            _timer.setInitialDelay(initDelay);
            _timer.start();
        }

        private void stopTimer() {
            if (_timer != null) {
                _timer.stop();
                _timer = null;
            }
        }

        private int getDelay(MouseEvent e) {
            if (isScrollOnRollover()) {
                return SwingUtilities.isLeftMouseButton(e) ? getRepeatDelay() : getRepeatDelay() * 2;
            }
            else {
                return getRepeatDelay();
            }
        }

        public void mouseExited(MouseEvent e) {
            if (isScrollOnRollover()) {
                stopTimer();
            }
        }
    }

    /**
     * Creates the scroll button. You can override this method to change the attributes on the button. For example, you
     * can do this to create a bigger scroll button.
     * <code><pre>
     * SimpleScrollPane pane = new SimpleScrollPane(){
     *     protected AbstractButton createScrollButton(int type) {
     *         AbstractButton scrollButton = super.createScrollButton(type);
     *         scrollButton.setPreferredSize(new Dimension(20, 20));
     *         return scrollButton;
     *     }
     * };
     * </pre></code>
     *
     * @param type the type of the scroll button. It could be {@link javax.swing.SwingConstants#NORTH}, {@link
     *             javax.swing.SwingConstants#SOUTH}, {@link javax.swing.SwingConstants#WEST} or {@link
     *             javax.swing.SwingConstants#EAST} .
     * @return the scroll button.
     */
    protected AbstractButton createScrollButton(int type) {
        return new ScrollButton(type);
    }

    protected void updateButtonState() {
        Point p = viewport.getViewPosition();
        _scrollUpButton.setEnabled(p.y != 0);
        _scrollDownButton.setEnabled(p.y != viewport.getViewSize().height - viewport.getViewRect().height);
        _scrollLeftButton.setEnabled(p.x != 0);
        _scrollRightButton.setEnabled(p.x != viewport.getViewSize().width - viewport.getViewRect().width);
        revalidate();
        repaint();
    }

    public void scroll(JViewport viewport, int type) {
        Point p = viewport.getViewPosition();

        JViewport vp = getViewport();
        switch (type) {
            case SwingConstants.NORTH:
                if (!_verticalUnitIncrementSet && (vp != null) &&
                        (vp.getView() instanceof Scrollable)) {
                    Scrollable view = (Scrollable) (vp.getView());
                    Rectangle vr = vp.getViewRect();
                    p.y -= view.getScrollableUnitIncrement(vr, SwingConstants.VERTICAL, -1);
                }
                else {
                    p.y -= getVerticalUnitIncrement();
                }
                if (p.y < 0) {
                    p.y = 0;
                }
                break;
            case SwingConstants.SOUTH:
                if (!_verticalUnitIncrementSet && (vp != null) &&
                        (vp.getView() instanceof Scrollable)) {
                    Scrollable view = (Scrollable) (vp.getView());
                    Rectangle vr = vp.getViewRect();
                    p.y += view.getScrollableUnitIncrement(vr, SwingConstants.VERTICAL, 1);
                }
                else {
                    p.y += getVerticalUnitIncrement();
                }
                if (p.y + viewport.getViewRect().height > viewport.getViewSize().height) {
                    p.y = viewport.getViewSize().height - viewport.getViewRect().height;
                }
                break;
            case SwingConstants.WEST:
                if (!_horizontalUnitIncrementSet && (vp != null) &&
                        (vp.getView() instanceof Scrollable)) {
                    Scrollable view = (Scrollable) (vp.getView());
                    Rectangle vr = vp.getViewRect();
                    p.x -= view.getScrollableUnitIncrement(vr, SwingConstants.HORIZONTAL, -1);
                }
                else {
                    p.x -= getHorizontalUnitIncrement();
                }
                if (p.x < 0) {
                    p.x = 0;
                }
                break;
            case SwingConstants.EAST:
                if (!_horizontalUnitIncrementSet && (vp != null) &&
                        (vp.getView() instanceof Scrollable)) {
                    Scrollable view = (Scrollable) (vp.getView());
                    Rectangle vr = vp.getViewRect();
                    p.x += view.getScrollableUnitIncrement(vr, SwingConstants.HORIZONTAL, 1);
                }
                else {
                    p.x += getHorizontalUnitIncrement();
                }
                if (p.x + viewport.getViewRect().width > viewport.getViewSize().width) {
                    p.x = viewport.getViewSize().width - viewport.getViewRect().width;
                }
                break;
        }

        viewport.setViewPosition(p);
    }

    @Override
    public Rectangle getViewportBorderBounds() {
        Rectangle borderR = new Rectangle(getSize());

        Insets insets = getInsets();
        borderR.x = insets.left;
        borderR.y = insets.top;
        borderR.width -= insets.left + insets.right;
        borderR.height -= insets.top + insets.bottom;

        if (_scrollUpButton != null && _scrollUpButton.isVisible()) {
            borderR.y += _scrollUpButton.getHeight();
            borderR.height -= _scrollUpButton.getHeight();
        }

        if (_scrollLeftButton != null && _scrollLeftButton.isVisible()) {
            borderR.x += _scrollLeftButton.getWidth();
            borderR.width -= _scrollLeftButton.getWidth();
        }

        if (_scrollDownButton != null && _scrollDownButton.isVisible()) {
            borderR.height -= _scrollDownButton.getHeight();
        }

        if (_scrollRightButton != null && _scrollRightButton.isVisible()) {
            borderR.width -= _scrollRightButton.getWidth();
        }

        return borderR;
    }

    public int getHorizontalUnitIncrement() {
        return _horizontalUnitIncrement;
    }

    public void setHorizontalUnitIncrement(int horizontalUnitIncrement) {
        _horizontalUnitIncrementSet = true;
        if (horizontalUnitIncrement != _horizontalUnitIncrement) {
            int old = _horizontalUnitIncrement;
            _horizontalUnitIncrement = horizontalUnitIncrement;
            firePropertyChange("horizontalUnitIncrement", old, _horizontalUnitIncrement);
        }
    }

    public int getVerticalUnitIncrement() {
        return _verticalUnitIncrement;
    }

    public void setVerticalUnitIncrement(int verticalUnitIncrement) {
        _verticalUnitIncrementSet = true;
        if (verticalUnitIncrement != _verticalUnitIncrement) {
            int old = _verticalUnitIncrement;
            _verticalUnitIncrement = verticalUnitIncrement;
            firePropertyChange("verticalUnitIncrement", old, _verticalUnitIncrement);
        }
    }

    /**
     * Checks if the scroll button scrolls on rollover.
     *
     * @return true if it scrolls on rollover.
     */
    public boolean isScrollOnRollover() {
        return _scrollOnRollover;
    }

    /**
     * Sets scroll on rollover. If true, the scrolling will start when mouse is placed above the scroll button. If
     * false, the scrolling will start only when you click or press and hold the mouse button.
     *
     * @param scrollOnRollover true or false.
     */
    public void setScrollOnRollover(boolean scrollOnRollover) {
        if (_scrollOnRollover != scrollOnRollover) {
            boolean old = _scrollOnRollover;
            _scrollOnRollover = scrollOnRollover;
            firePropertyChange("scrollOnRollover", old, _scrollOnRollover);
        }
    }

    /**
     * Gets the delay in ms between each unit scrolling.
     *
     * @return the delay.
     */
    public int getRepeatDelay() {
        return _repeatDelay;
    }

    /**
     * Sets the delay in ms between each unit scrolling. By default, it's 50. The big thenumberr, the slow the
     * scrolling.
     *
     * @param repeatDelay the new repeat delay.
     */
    public void setRepeatDelay(int repeatDelay) {
        if (repeatDelay != _repeatDelay) {
            int old = _repeatDelay;
            _repeatDelay = repeatDelay;
            firePropertyChange("repeatDelay", old, _repeatDelay);
        }
    }


    public void mouseWheelMoved(MouseWheelEvent e) {
        if (this.isWheelScrollingEnabled() && e.getScrollAmount() != 0) {
            boolean scrollingUp = (e.getWheelRotation() >= 0);
            int direction = SwingConstants.CENTER;

            if (!this.isButtonVisible(scrollingUp))
                return;

            direction = this.getScrollDirection(scrollingUp);
            if (direction != SwingConstants.CENTER)
                this.scroll(this.getViewport(), direction);
        }
    }

    private boolean isButtonVisible(boolean scrollingUp) {
        if (scrollingUp)
            return (((_scrollUpButton != null) && _scrollUpButton.isVisible()) ||
                    ((_scrollLeftButton != null) && _scrollLeftButton.isVisible()));
        else
            return (((_scrollDownButton != null) && _scrollDownButton.isVisible()) ||
                    ((_scrollRightButton != null) && _scrollRightButton.isVisible()));
    }

    private int getScrollDirection(boolean scrollingUp) {
        if (scrollingUp) {
            if ((_scrollUpButton != null) && _scrollUpButton.isVisible()) return SwingConstants.SOUTH;
            if ((_scrollLeftButton != null) && _scrollLeftButton.isVisible()) return SwingConstants.EAST;
        }
        else {
            if ((_scrollDownButton != null) && _scrollDownButton.isVisible()) return SwingConstants.NORTH;
            if ((_scrollRightButton != null) && _scrollRightButton.isVisible()) return SwingConstants.WEST;
        }

        return SwingConstants.CENTER;
    }

    @Override
    public void setWheelScrollingEnabled(boolean handleWheel) {
        if (handleWheel && !isWheelScrollingEnabled())
            this.addMouseWheelListener(this);
        if (!handleWheel && isWheelScrollingEnabled())
            this.removeMouseWheelListener(this);
        super.setWheelScrollingEnabled(handleWheel);
    }
}

