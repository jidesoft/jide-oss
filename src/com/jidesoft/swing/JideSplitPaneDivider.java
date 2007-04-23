/*
 * @(#)JideSplitPaneDivider.java
 *
 * Copyright 2002 JIDE Software Inc. All rights reserved.
 */
/*
 * @(#)JideSplitPaneDivider.java
 *
 * Copyright 2002 JIDE Software Inc. All rights reserved.
 */


package com.jidesoft.swing;


import com.jidesoft.plaf.UIDefaultsLookup;
import com.jidesoft.plaf.basic.Painter;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.MouseInputAdapter;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;


/**
 * Divider used by JideSplitPane.
 */
public class JideSplitPaneDivider extends JPanel
        implements PropertyChangeListener {
    /**
     * Handles mouse dragging message to do the actual dragging.
     */
    protected DragController _dragger;

    /**
     * Size of the divider.
     */
    protected int _dividerSize = UIDefaultsLookup.getInt("JideSplitPane.dividerSize"); // default - SET TO 0???

    /**
     * JideSplitPane the receiver is contained in.
     */
    protected JideSplitPane _jideSplitPane;

    /**
     * Handles mouse events from both this class, and the split pane.
     * Mouse events are handled for the splitpane since you want to be able
     * to drag when clicking on the border of the divider, which is not
     * drawn by the divider.
     */
    protected MouseHandler _mouseHandler;

    /**
     * Orientation of the JideSplitPane.
     */
    protected int _orientation;

    /**
     * Cursor used for HORIZONTAL_SPLIT splitpanes.
     */
    static final Cursor HORIZONTAL_CURSOR =
            JideCursors.getPredefinedCursor(JideCursors.HSPLIT_CURSOR);

    /**
     * Cursor used for VERTICAL_SPLIT splitpanes.
     */
    static final Cursor VERTICAL_CURSOR =
            JideCursors.getPredefinedCursor(JideCursors.VSPLIT_CURSOR);

    /**
     * Default cursor.
     */
    static final Cursor DEFAULT_CURSOR = Cursor.getDefaultCursor();

    private Painter _gripperPainter;

    /**
     * Creates an instance of BasicJideSplitPaneDivider. Registers this
     * instance for mouse events and mouse dragged events.
     */
    public JideSplitPaneDivider(JideSplitPane splitPane) {
        setJideSplitPane(splitPane);
        _orientation = _jideSplitPane.getOrientation();

        // get divider size from JideSplitPane
        setDividerSize(splitPane.getDividerSize());
        setDefaultResizeCursor();

        setBackground(UIDefaultsLookup.getColor("JideSplitPaneDivider.background"));
        setBorder(UIDefaultsLookup.getBorder("JideSplitPaneDivider.border"));
        _gripperPainter = (Painter) UIDefaultsLookup.get("JideSplitPaneDivider.gripperPainter");
        setOpaque(false);
    }

    public void setDefaultResizeCursor() {
        setCursor((_orientation == JideSplitPane.HORIZONTAL_SPLIT) ?
                HORIZONTAL_CURSOR : VERTICAL_CURSOR);
    }

    /**
     * Gets the <code>JideSplitPane</code>.
     *
     * @return the <code>JideSplitPane</code>
     */
    public JideSplitPane getJideSplitPane() {
        return _jideSplitPane;
    }

    /**
     * Sets the JideSplitPane that is using this divider.
     */
    public void setJideSplitPane(JideSplitPane newPane) {
        uninstallListeners();
        _jideSplitPane = newPane;
        installListeners();
    }

    private void installListeners() {
        if (_jideSplitPane != null) {
            if (_mouseHandler == null) {
                _mouseHandler = createMouseHandler();
            }
            _jideSplitPane.addMouseListener(_mouseHandler);
            _jideSplitPane.addMouseMotionListener(_mouseHandler);
            addMouseListener(_mouseHandler);
            addMouseMotionListener(_mouseHandler);
            _jideSplitPane.addPropertyChangeListener(this);
        }
    }

    private void uninstallListeners() {
        if (_jideSplitPane != null) {
            _jideSplitPane.removePropertyChangeListener(this);
            if (_mouseHandler != null) {
                _jideSplitPane.removeMouseListener(_mouseHandler);
                _jideSplitPane.removeMouseMotionListener(_mouseHandler);
                removeMouseListener(_mouseHandler);
                removeMouseMotionListener(_mouseHandler);
                _mouseHandler = null;
            }
        }
    }

    protected MouseHandler createMouseHandler() {
        return new MouseHandler();
    }

    /**
     * Sets the size of the divider to <code>newSize</code>. That is
     * the width if the splitpane is <code>HORIZONTAL_SPLIT</code>, or
     * the height of <code>VERTICAL_SPLIT</code>.
     */
    public void setDividerSize(int newSize) {
        _dividerSize = newSize;
    }


    /**
     * Returns the size of the divider, that is the width if the splitpane
     * is HORIZONTAL_SPLIT, or the height of VERTICAL_SPLIT.
     */
    public int getDividerSize() {
        return _dividerSize;
    }

    /**
     * Returns dividerSize x dividerSize
     */
    public Dimension getPreferredSize() {
        return new Dimension(getDividerSize(), getDividerSize());
    }

    /**
     * Returns dividerSize x dividerSize
     */
    public Dimension getMinimumSize() {
        return getPreferredSize();
    }


    /**
     * Property change event, presumably from the JideSplitPane, will message
     * updateOrientation if necessary.
     */
    public void propertyChange(PropertyChangeEvent e) {
        if (e.getSource() == _jideSplitPane) {
            if (e.getPropertyName().equals(JideSplitPane.ORIENTATION_PROPERTY)) {
                _orientation = _jideSplitPane.getOrientation();
                setCursor((_orientation == JideSplitPane.HORIZONTAL_SPLIT) ?
                        HORIZONTAL_CURSOR : VERTICAL_CURSOR);
                invalidate();
                validate();
            }
        }
    }


    /**
     * Resets the UI property to a value from the current look and feel.
     * <code>JComponent</code> subclasses must override this method
     * like this:
     * <pre>
     *   public void updateUI() {
     *      setUI((SliderUI)UIManager.getUI(this);
     *   }
     *  </pre>
     *
     * @see #setUI
     * @see UIManager#getLookAndFeel
     * @see UIManager#getUI
     */
    public void updateUI() {
        super.updateUI();
        setBackground(UIDefaultsLookup.getColor("JideSplitPaneDivider.background"));
        setBorder(UIDefaultsLookup.getBorder("JideSplitPaneDivider.border"));
        _gripperPainter = (Painter) UIDefaultsLookup.get("JideSplitPaneDivider.gripperPainter");
    }

    /**
     * Paints the divider.
     */
    public void paint(Graphics g) {
        super.paint(g);
        // Paint the border.
        Border border = getBorder();

        Dimension size = getSize();

        if (isOpaque()) {
            g.setColor(getBackground());
            g.fillRect(0, 0, size.width, size.height);
        }

        if (border != null) {
            border.paintBorder(this, g, 0, 0, size.width, size.height);
        }

        if (_jideSplitPane.isShowGripper()) {
            Rectangle rect = new Rectangle(size);
            if (_gripperPainter != null) {
                if (rect.width > rect.height) {
                    rect.x = rect.x + rect.width / 2 - 10;
                    rect.width = 22;
                    _gripperPainter.paint(this, g, rect, SwingConstants.VERTICAL, 0);
                }
                else {
                    rect.y = rect.y + rect.height / 2 - 10;
                    rect.height = 22;
                    _gripperPainter.paint(this, g, rect, SwingConstants.HORIZONTAL, 0);
                }
            }
            else {
                rect.x++;
                rect.y++;
                JideSwingUtilities.drawGrip(g, rect, 9, UIDefaultsLookup.getInt("JideSplitPane.dividerSize") / 3);
            }
        }
    }

    /**
     * Message to prepare for dragging. This messages the BasicJideSplitPaneUI
     * with startDragging.
     */
    protected void prepareForDragging() {
        _jideSplitPane.startDragging(this);
    }

    protected void dragDividerTo(int location) {
        _jideSplitPane.dragDividerTo(this, location);
    }

    protected void finishDraggingTo(int location) {
        _jideSplitPane.finishDraggingTo(this, location);
    }

    protected int getPreviousDividerLocation(boolean ignoreVisibility) {
        return _jideSplitPane.getPreviousDividerLocation(this, ignoreVisibility);
    }

    protected int getNextDividerLocation(boolean ignoreVisibility) {
        return _jideSplitPane.getNextDividerLocation(this, ignoreVisibility);
    }

    /**
     * Gets the first component. This divider is installed between two
     * components. The first component is usually the one on the left or on the top.
     *
     * @return the first component
     */
    public Component getFirstComponent(boolean ignoreVisibility) {
        int index = _jideSplitPane.indexOf(this);
        if (index - 1 >= 0) {
            for (int i = (index - 1); i >= 0; i--) {
                if (ignoreVisibility || _jideSplitPane.getComponent(i).isVisible()) {
                    return _jideSplitPane.getComponent(i);
                }
            }
            // return an invisible component in lieu of null
            return _jideSplitPane.getComponent(index - 1);
        }
        else {
            throw new IndexOutOfBoundsException("There is no component before divider " + index);
        }
    }

    /**
     * Gets the second component. This divider is installed between two
     * components. The second component is usually the one on the right or on the bottom.
     *
     * @return the first component
     */
    public Component getSecondComponent(boolean ignoreVisibility) {
        int index = _jideSplitPane.indexOf(this);

        if (index + 1 < _jideSplitPane.getComponentCount()) {
            for (int i = (index + 1); i >= 0; i++) {
                if (ignoreVisibility || _jideSplitPane.getComponent(i).isVisible()) {
                    return _jideSplitPane.getComponent(i);
                }
            }
            // return an invisible component in lieu of null
            return _jideSplitPane.getComponent(index + 1);
        }
        else {
            throw new IndexOutOfBoundsException("There is no component before divider " + index);
        }
    }

    /**
     * MouseHandler is responsible for converting mouse events
     * (released, dragged...) into the appropriate DragController
     * methods.
     * <p/>
     */
    protected class MouseHandler extends MouseInputAdapter {
        /**
         * Starts the dragging session by creating the appropriate instance
         * of DragController.
         */
        public void mousePressed(MouseEvent e) {
            if ((e.getSource() == JideSplitPaneDivider.this/*||
                    e.getSource() == _jideSplitPane*/) && _dragger == null && _jideSplitPane.isEnabled()) {
                if (getFirstComponent(true) != null &&
                        getSecondComponent(true) != null) {
                    if (_orientation == JideSplitPane.HORIZONTAL_SPLIT) {
                        _dragger = new DragController(e);
                    }
                    else {
                        _dragger = new VerticalDragController(e);
                    }
                    if (!_dragger.isValid()) {
                        _dragger = null;
                    }
                    else {
                        prepareForDragging();
                        _dragger.continueDrag(e);
                    }
                }
                e.consume();
            }
        }


        /**
         * If dragger is not null it is messaged with completeDrag.
         */
        public void mouseReleased(MouseEvent e) {
            if (_dragger != null) {
                if (e.getSource() == _jideSplitPane) {
                    _dragger.completeDrag(e.getX(), e.getY());
                }
                else if (e.getSource() == JideSplitPaneDivider.this) {
                    Point ourLoc = getLocation();
                    _dragger.completeDrag(e.getX() + ourLoc.x,
                            e.getY() + ourLoc.y);
                }
                _dragger = null;
                e.consume();
            }
        }

        //
        // MouseMotionListener
        //

        /**
         * If dragger is not null it is messaged with continueDrag.
         */
        public void mouseDragged(MouseEvent e) {
            if (_dragger != null) {
                if (e.getSource() == _jideSplitPane) {
                    _dragger.continueDrag(e.getX(), e.getY());
                }
                else if (e.getSource() == JideSplitPaneDivider.this) {
                    Point ourLoc = getLocation();
                    _dragger.continueDrag(e.getX() + ourLoc.x,
                            e.getY() + ourLoc.y);
                }
                e.consume();
            }
        }
    }


    /**
     * Handles the events during a dragging session for a
     * HORIZONTAL_SPLIT oriented split pane. This continually
     * messages <code>dragDividerTo</code> and then when done messages
     * <code>finishDraggingTo</code>. When an instance is created it should be
     * messaged with <code>isValid</code> to insure that dragging can happen
     * (dragging won't be allowed if the two views can not be resized).
     */
    protected class DragController {
        /**
         * Initial location of the divider.
         */
        int initialX;

        /**
         * Maximum and minimum positions to drag to.
         */
        int maxX, minX;

        /**
         * Initial location the mouse down happened at.
         */
        int offset;


        protected DragController(MouseEvent e) {
            Component leftC = getFirstComponent(false);
            Component rightC = getSecondComponent(false);

            initialX = getLocation().x;
            if (e.getSource() == JideSplitPaneDivider.this) {
                offset = e.getX();
            }
            else { // splitPane
                offset = e.getX() - initialX;
            }
            if (leftC == null || rightC == null || offset < -1 ||
                    offset >= _jideSplitPane.getSize().width) {
                // Don't allow dragging.
                maxX = -1;
            }
            else {
                Insets insets = _jideSplitPane.getInsets();

                if (leftC.isVisible()) {
                    minX = getPreviousDividerLocation(false) + leftC.getMinimumSize().width;
                    if (insets != null) {
                        minX += insets.left;
                    }
                }
                else {
                    minX = getPreviousDividerLocation(true);
                    if (insets != null) {
                        minX += insets.left;
                    }
                }
                if (rightC.isVisible()) {
                    int right = (insets != null) ? insets.right : 0;
                    maxX = Math.max(0, getNextDividerLocation(false) - (getSize().width + right) - rightC.getMinimumSize().width);
                }
                else {
                    int right = (insets != null) ? insets.right : 0;
                    maxX = Math.max(0, getNextDividerLocation(true) - (getSize().width + right));
                }
                if (maxX < minX) minX = maxX = 0;
            }
        }


        /**
         * Returns true if the dragging session is valid.
         */
        protected boolean isValid() {
            return (maxX > 0);
        }


        /**
         * Returns the new position to put the divider at based on
         * the passed in MouseEvent.
         */
        protected int positionForMouseEvent(MouseEvent e) {
            int newX = (e.getSource() == JideSplitPaneDivider.this) ?
                    (e.getX() + getLocation().x) : e.getX();

            newX = Math.min(maxX, Math.max(minX, newX - offset));
            return newX;
        }


        /**
         * Returns the x argument, since this is used for horizontal
         * splits.
         */
        protected int getNeededLocation(int x, int y) {
            int newX;

            newX = Math.min(maxX, Math.max(minX, x - offset));
            return newX;
        }


        protected void continueDrag(int newX, int newY) {
            dragDividerTo(getNeededLocation(newX, newY));
        }


        /**
         * Messages dragDividerTo with the new location for the mouse
         * event.
         */
        protected void continueDrag(MouseEvent e) {
            dragDividerTo(positionForMouseEvent(e));
        }


        protected void completeDrag(int x, int y) {
            finishDraggingTo(getNeededLocation(x, y));
        }


        /**
         * Messages finishDraggingTo with the new location for the mouse
         * event.
         */
        protected void completeDrag(MouseEvent e) {
            finishDraggingTo(positionForMouseEvent(e));
        }
    } // End of BasicJideSplitPaneDivider.DragController


    /**
     * Handles the events during a dragging session for a
     * VERTICAL_SPLIT oriented split pane. This continually
     * messages <code>dragDividerTo</code> and then when done messages
     * <code>finishDraggingTo</code>. When an instance is created it should be
     * messaged with <code>isValid</code> to insure that dragging can happen
     * (dragging won't be allowed if the two views can not be resized).
     */
    protected class VerticalDragController extends DragController {
        /* DragControllers ivars are now in terms of y, not x. */
        protected VerticalDragController(MouseEvent e) {
            super(e);
            Component leftC = getFirstComponent(false);
            Component rightC = getSecondComponent(false);

            initialX = getLocation().y;
            if (e.getSource() == JideSplitPaneDivider.this) {
                offset = e.getY();
            }
            else { // splitPane
                offset = e.getY() - initialX;
            }
            if (leftC == null || rightC == null || offset < -1 ||
                    offset >= _jideSplitPane.getSize().height) {
                // Don't allow dragging.
                maxX = -1;
            }
            else {
                Insets insets = _jideSplitPane.getInsets();

                if (leftC.isVisible()) {
                    minX = getPreviousDividerLocation(false) + leftC.getMinimumSize().height;
                    if (insets != null) {
                        minX += insets.top;
                    }
                }
                else {
                    minX = 0;
                }
                if (rightC.isVisible()) {
                    int right = (insets != null) ? insets.top : 0;
                    maxX = Math.max(0, getNextDividerLocation(false) - (getSize().height + right) - rightC.getMinimumSize().height);
                }
                else {
                    int right = (insets != null) ? insets.top : 0;
                    maxX = Math.max(0, getNextDividerLocation(true) - (getSize().height + right));
                }
                if (maxX < minX) minX = maxX = 0;
            }
        }


        /**
         * Returns the y argument, since this is used for vertical
         * splits.
         */
        protected int getNeededLocation(int x, int y) {
            int newY;

            newY = Math.min(maxX, Math.max(minX, y - offset));
            return newY;
        }


        /**
         * Returns the new position to put the divider at based on
         * the passed in MouseEvent.
         */
        protected int positionForMouseEvent(MouseEvent e) {
            int newY = (e.getSource() == JideSplitPaneDivider.this) ?
                    (e.getY() + getLocation().y) : e.getY();

            newY = Math.min(maxX, Math.max(minX, newY - offset));
            return newY;
        }
    } // End of BasicSplitPaneDividier.VerticalDragController

}
