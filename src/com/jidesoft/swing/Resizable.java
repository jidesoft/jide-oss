/*
 * @(#)${NAME}.java
 *
 * Copyright 2002 - 2005 JIDE Software Inc. All rights reserved.
 */
package com.jidesoft.swing;

import com.jidesoft.plaf.UIDefaultsLookup;

import javax.swing.*;
import javax.swing.event.MouseInputListener;
import java.awt.*;

/**
 * Resizable is a class that support resizable feature.
 * <p/>
 * To use it and make a component resizable, you just need to create new Resizable() and pass in that component to the
 * constructor.
 */
public class Resizable {

    public static final int NONE = 0x0;
    public static final int UPPER_LEFT = 0x1;
    public static final int UPPER = 0x2;
    public static final int UPPER_RIGHT = 0x4;
    public static final int RIGHT = 0x8;
    public static final int LOWER_RIGHT = 0x10;
    public static final int LOWER = 0x20;
    public static final int LOWER_LEFT = 0x40;
    public static final int LEFT = 0x80;
    public static final int ALL = 0xFF;

    private int _resizableCorners = 0xFF;

    private int _resizeCornerSize = 16;

    public static final String PROPERTY_RESIZABLE_CORNERS = "resizableCorner";
    public static final String PROPERTY_RESIZE_CORNER_SIZE = "resizeCornerSize";

    protected final JComponent _component;
    private Insets _resizeInsets;
    private MouseInputListener _mouseInputAdapter;


    private boolean _topLevel;

    /**
     * Creates a new <code>Resizable</code>. This call will make the component to be resizable.
     */
    public Resizable(JComponent component) {
        _component = component;
        installListeners();
    }

    /**
     * Gets the resizable corners. The value is a bitwise OR of eight constants defined in {@link Resizable}.
     *
     * @return resizable corners.
     */
    public int getResizableCorners() {
        return _resizableCorners;
    }

    /**
     * Sets resizable corners.
     *
     * @param resizableCorners new resizable corners. The value is a bitwise OR of eight constants defined in {@link
     *                         Resizable}.
     */
    public void setResizableCorners(int resizableCorners) {
        if (_resizableCorners != resizableCorners) {
            int old = _resizableCorners;
            _resizableCorners = resizableCorners;
            _component.firePropertyChange(PROPERTY_RESIZABLE_CORNERS, old, _resizableCorners);
        }
    }

    /**
     * Gets resize corner size. This size is the corner's sensitive area which will trigger the resizing from both
     * sides.
     *
     * @return the resize corner size.
     */
    public int getResizeCornerSize() {
        return _resizeCornerSize;
    }

    /**
     * Sets the resize corner size.
     *
     * @param resizeCornerSize the resize corner size.
     */
    public void setResizeCornerSize(int resizeCornerSize) {
        if (_resizeCornerSize != resizeCornerSize) {
            int old = _resizeCornerSize;
            _resizeCornerSize = resizeCornerSize;
            _component.firePropertyChange(PROPERTY_RESIZE_CORNER_SIZE, old, _resizeCornerSize);
        }
    }

    /**
     * Installs the listeners needed to perform resizing operations. You do not need to call this method directly.
     * Constructor will call this method automatically.
     */
    protected void installListeners() {
        _mouseInputAdapter = createMouseInputListener();
        _component.addMouseListener(_mouseInputAdapter);
        _component.addMouseMotionListener(_mouseInputAdapter);
    }

    /**
     * Uninstalls the listeners that created to perform resizing operations. After the uninstallation, the component
     * will not be resizable anymore.
     */
    public void uninstallListeners() {
        _component.removeMouseListener(_mouseInputAdapter);
        _component.removeMouseMotionListener(_mouseInputAdapter);
        _mouseInputAdapter = null;
    }

    /**
     * Creates the MouseInputListener for resizing. Subclass can override this method to provide its own
     * MouseInputListener to customize existing one.
     *
     * @return the MouseInputListener for resizing.
     */
    protected MouseInputListener createMouseInputListener() {
        return new ResizableMouseInputAdapter(this);
    }

    /**
     * Gets the mouse adapter for resizing.
     *
     * @return the mouse adapter for resizing.
     */
    public MouseInputListener getMouseInputAdapter() {
        return _mouseInputAdapter;
    }

    /**
     * This method is called when resizing operation started.
     *
     * @param resizeCorner the resize corner.
     */
    public void beginResizing(int resizeCorner) {
    }

    /**
     * This method is called during the resizing of ResizablePanel. In default implementation, it call
     * <code><pre>
     * setPreferredSize(new Dimension(newW, newH));
     * getParent().doLayout();
     * </pre></code>
     * in fact, depending on where you added this ResizablePanel, you may need to override this method to do something
     * else. For example, {@link ResizableWindow} uses <code>ResizablePanel</code> to implement resizable feature in
     * JWindow. It overrides this method to call setBounds on JWindow itself.
     *
     * @param resizeCorner the resize corner.
     * @param newX         the new x position.
     * @param newY         the new y position.
     * @param newW         the new width.
     * @param newH         the new height.
     */
    public void resizing(int resizeCorner, int newX, int newY, int newW, int newH) {
        Dimension minimumSize = _component.getMinimumSize();
        Dimension maximumSize = _component.getMaximumSize();
        if (newW < minimumSize.width) {
            newW = minimumSize.width;
        }
        if (newH < minimumSize.height) {
            newW = minimumSize.height;
        }
        if (newW > maximumSize.width) {
            newW = maximumSize.width;
        }
        if (newH > maximumSize.height) {
            newH = maximumSize.height;
        }
        _component.setPreferredSize(new Dimension(newW, newH));
        _component.getParent().doLayout();
    }

    /**
     * The method is called when resizing ends.
     *
     * @param resizeCorner the resize corner.
     */
    public void endResizing(int resizeCorner) {
    }

    /**
     * Checks if the Resizable is added to a top level component. <p> If it's top level component, it will use screen
     * coordinates to do all calculations during resizing. If resizing the resizable panel won't affect any top level
     * container's position, you can return false here. Otherwise, return true. The default implementation always return
     * false. Subclasses can override to return different value. In the case of ResizableWindow or ResizableDialog, this
     * method is overridden and returns true.
     *
     * @return false.
     */
    public boolean isTopLevel() {
        return _topLevel;
    }

    /**
     * To indicates this <code>Resizable</code> is installed on a top level component such as JWindow, JDialog and
     * JFrame v.s. a JPanel which is not a top level component because a JPanel must be added to another top level
     * component in order to be displayed.
     *
     * @param topLevel true or false.
     */
    public void setTopLevel(boolean topLevel) {
        _topLevel = topLevel;
    }

    /**
     * Gets the component which has this Resizable object.
     *
     * @return the component which has this Resizable object.
     */
    public JComponent getComponent() {
        return _component;
    }

    /**
     * Returns the insets that should be used to calculate the resize area. Unless you have used setResizeInsets or
     * overridden this method, it'll return the insets of the component.
     *
     * @return the insets that should be used to calculate the resize area.
     */
    public Insets getResizeInsets() {
        if (_resizeInsets != null) {
            return _resizeInsets;
        }
        return getComponent().getInsets();
    }

    /**
     * Sets the insets the be used to calculate the resize area.
     *
     * @param resizeInsets
     */
    public void setResizeInsets(Insets resizeInsets) {
        _resizeInsets = resizeInsets;
    }

    public static class ResizeCorner extends JComponent {
        static final int SIZE = 16;
        private int _corner = LOWER_RIGHT;

        public ResizeCorner() {
        }

        public ResizeCorner(int corner) {
            _corner = corner;
        }

        public int getCorner() {
            return _corner;
        }

        public void setCorner(int corner) {
            _corner = corner;
        }

        @Override
        public Dimension getPreferredSize() {
            return new Dimension(SIZE, SIZE);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            int size = Math.min(getWidth(), getHeight());
            int count = Math.min(size / 4, 4);
            Color old = g.getColor();
            int corner = getCorner();
            boolean ltr = getComponentOrientation().isLeftToRight();
            switch (corner) {
                case LOWER_RIGHT: {
                    g.setColor(UIDefaultsLookup.getColor("controlLtHighlight"));
                    int delta = 0;
                    for (int i = 0; i < count; i++) {
                        delta += 4;
                        if (ltr) {
                            g.drawLine(size, size - delta, size - delta, size);
                        }
                        else {
                            g.drawLine(0, delta, size - delta, size);
                        }
                    }
                    g.setColor(UIDefaultsLookup.getColor("controlShadow"));
                    delta = 0;
                    for (int i = 0; i < count; i++) {
                        delta += 4;
                        if (ltr) {
                            g.drawLine(size, size - delta + 1, size - delta + 1, size);
                            g.drawLine(size, size - delta + 2, size - delta + 2, size);
                        }
                        else {
                            g.drawLine(0, delta + 1, size - delta - 1, size);
                            g.drawLine(0, delta + 2, size - delta - 2, size);
                        }
                    }
                }
                break;
                case UPPER_RIGHT: {
                    g.setColor(UIDefaultsLookup.getColor("controlLtHighlight"));
                    int delta = 0;
                    for (int i = 0; i < count; i++) {
                        delta += 4;
                        if (ltr) {
                            g.drawLine(size - delta, 0, size, delta);
                        }
                        else {
                            g.drawLine(delta, 0, size, size - delta);
                        }
                    }
                    g.setColor(UIDefaultsLookup.getColor("controlShadow"));
                    delta = 0;
                    for (int i = 0; i < count; i++) {
                        delta += 4;
                        if (ltr) {
                            g.drawLine(size - delta + 1, 0, size, delta - 1);
                            g.drawLine(size - delta + 2, 0, size, delta - 2);
                        }
                        else {
                            g.drawLine(delta + 1, 0, size, size - delta - 1);
                            g.drawLine(delta + 2, 0, size, size - delta - 2);
                        }
                    }
                }
                break;

            }
            g.setColor(old);
        }
    }
}
