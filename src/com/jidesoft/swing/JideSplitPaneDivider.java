/*
 * @(#)JideSplitPaneDivider.java
 *
 * Copyright 2002 JIDE Software Inc. All rights reserved.
 */

package com.jidesoft.swing;


import com.jidesoft.plaf.UIDefaultsLookup;
import com.jidesoft.plaf.basic.Painter;
import com.jidesoft.utils.PortingUtils;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.MouseInputAdapter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
     * Handles mouse events from both this class, and the split pane. Mouse events are handled for the JideSplitPane
     * since you want to be able to drag when clicking on the border of the divider, which is not drawn by the divider.
     */
    protected MouseHandler _mouseHandler;

    /**
     * Orientation of the JideSplitPane.
     */
    protected int _orientation;

    /**
     * Cursor used for HORIZONTAL_SPLIT split panes.
     */
    static final Cursor HORIZONTAL_CURSOR =
            JideCursors.getPredefinedCursor(JideCursors.HSPLIT_CURSOR);

    /**
     * Cursor used for VERTICAL_SPLIT split panes.
     */
    static final Cursor VERTICAL_CURSOR =
            JideCursors.getPredefinedCursor(JideCursors.VSPLIT_CURSOR);

    /**
     * Default cursor.
     */
    static final Cursor DEFAULT_CURSOR = Cursor.getDefaultCursor();

    private Painter _gripperPainter;

    /**
     * Creates an instance of BasicJideSplitPaneDivider. Registers this instance for mouse events and mouse dragged
     * events.
     *
     * @param splitPane the JideSplitPane.
     */
    public JideSplitPaneDivider(JideSplitPane splitPane) {
        setJideSplitPane(splitPane);
        _orientation = _jideSplitPane.getOrientation();

        // get divider size from JideSplitPane
        setDividerSize(splitPane.getDividerSize());
        setDefaultResizeCursor();

        setBackground(UIDefaultsLookup.getColor("JideSplitPaneDivider.background"));
        setBorder(UIDefaultsLookup.getBorder("JideSplitPaneDivider.border"));
        oneTouchExpandableChanged();
        _gripperPainter = (Painter) UIDefaultsLookup.get("JideSplitPaneDivider.gripperPainter");
        setOpaque(false);
        setLayout(null);
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
     *
     * @param splitPane the JideSplitPane.
     */
    public void setJideSplitPane(JideSplitPane splitPane) {
        uninstallListeners();
        _jideSplitPane = splitPane;
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
     * Sets the size of the divider to <code>newSize</code>. That is the width if the split pane is
     * <code>HORIZONTAL_SPLIT</code>, or the height of <code>VERTICAL_SPLIT</code>.
     *
     * @param newSize the new divider size.
     */
    public void setDividerSize(int newSize) {
        _dividerSize = newSize;
    }


    /**
     * Returns the size of the divider, that is the width if the split pane is HORIZONTAL_SPLIT, or the height of
     * VERTICAL_SPLIT.
     *
     * @return the divider size.
     */
    public int getDividerSize() {
        return _dividerSize;
    }

    /**
     * Returns dividerSize x dividerSize
     */
    @Override
    public Dimension getPreferredSize() {
        return new Dimension(getDividerSize(), getDividerSize());
    }

    /**
     * Returns dividerSize x dividerSize
     */
    @Override
    public Dimension getMinimumSize() {
        return getPreferredSize();
    }


    /**
     * Property change event, presumably from the JideSplitPane, will message updateOrientation if necessary.
     */
    public void propertyChange(PropertyChangeEvent e) {
        if (e.getSource() == _jideSplitPane) {
            if (JideSplitPane.ORIENTATION_PROPERTY.equals(e.getPropertyName())) {
                _orientation = _jideSplitPane.getOrientation();
                setCursor((_orientation == JideSplitPane.HORIZONTAL_SPLIT) ?
                        HORIZONTAL_CURSOR : VERTICAL_CURSOR);
                invalidate();
                validate();
            }
            else if (JideSplitPane.ONE_TOUCH_EXPANDABLE_PROPERTY.equals(e.getPropertyName())) {
                setDividerSize(_jideSplitPane.getDividerSize());
                oneTouchExpandableChanged();
            }
            else if (JideSplitPane.GRIPPER_PROPERTY.equals(e.getPropertyName())) {
                repaint();
            }
        }
    }


    /**
     * Resets the UI property to a value from the current look and feel. <code>JComponent</code> subclasses must
     * override this method like this:
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
    @Override
    public void updateUI() {
        super.updateUI();
        setBackground(UIDefaultsLookup.getColor("JideSplitPaneDivider.background"));
        setBorder(UIDefaultsLookup.getBorder("JideSplitPaneDivider.border"));
        _gripperPainter = (Painter) UIDefaultsLookup.get("JideSplitPaneDivider.gripperPainter");
    }

    /**
     * Paints the divider.
     */
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
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
//                    rect.x = rect.x + rect.width / 2 - 10;
//                    rect.width = 22;
                    _gripperPainter.paint(this, g, rect, SwingConstants.VERTICAL, 0);
                }
                else {
//                    rect.y = rect.y + rect.height / 2 - 10;
//                    rect.height = 22;
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
     * Message to prepare for dragging. This messages the BasicJideSplitPaneUI with startDragging.
     */
    protected void prepareForDragging() {
        _jideSplitPane.startDragging(this);
    }

    protected void dragDividerTo(int location) {
        _jideSplitPane.dragDividerTo(this, location);

        /*
         * Update the variables used by the one-touch expand/collapse buttons.
         */
        _currentState = DEFAULT_STATE;
        int indexOfDivider = _jideSplitPane.indexOfDivider(JideSplitPaneDivider.this);
        _lastPosition = _jideSplitPane.getDividerLocation(indexOfDivider);
    }

    protected void finishDraggingTo(int location) {
        _jideSplitPane.finishDraggingTo(this, location);
    }

    protected int getPreviousDividerLocation(boolean ignoreVisibility, boolean reversed) {
        return _jideSplitPane.getPreviousDividerLocation(this, ignoreVisibility, reversed);
    }

    protected int getNextDividerLocation(boolean ignoreVisibility, boolean reversed) {
        return _jideSplitPane.getNextDividerLocation(this, ignoreVisibility, reversed);
    }

    /**
     * Gets the first component. This divider is installed between two components. The first component is usually the
     * one on the left or on the top.
     *
     * @param ignoreVisibility true to not check if the component is visible.
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
     * Gets the second component. This divider is installed between two components. The second component is usually the
     * one on the right or on the bottom.
     *
     * @param ignoreVisibility true to not check if the component is visible.
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
     * MouseHandler is responsible for converting mouse events (released, dragged...) into the appropriate
     * DragController methods.
     * <p/>
     */
    protected class MouseHandler extends MouseInputAdapter {
        /**
         * Starts the dragging session by creating the appropriate instance of DragController.
         */
        @Override
        public void mousePressed(MouseEvent e) {
            if ((e.getSource() == JideSplitPaneDivider.this/*||
                    e.getSource() == _jideSplitPane*/) && _dragger == null && _jideSplitPane.isEnabled() && _jideSplitPane.isDragResizable()) {
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
        @Override
        public void mouseReleased(MouseEvent e) {
            if (_dragger != null) {
                if (e.getSource() == _jideSplitPane) {
                    _dragger.completeDrag(e.getX(), e.getY());
                }
                else if (e.getSource() == JideSplitPaneDivider.this) {
                    Point ourLoc = getLocation();
                    _dragger.completeDrag(e.getX() + ourLoc.x, e.getY() + ourLoc.y);
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
        @Override
        public void mouseDragged(MouseEvent e) {
            if (_dragger != null) {
                if (e.getSource() == _jideSplitPane) {
                    _dragger.continueDrag(e.getX(), e.getY());
                }
                else if (e.getSource() == JideSplitPaneDivider.this) {
                    Point ourLoc = getLocation();
                    _dragger.continueDrag(e.getX() + ourLoc.x, e.getY() + ourLoc.y);
                }
                e.consume();
            }
        }
    }


    /**
     * Handles the events during a dragging session for a HORIZONTAL_SPLIT oriented split pane. This continually
     * messages <code>dragDividerTo</code> and then when done messages <code>finishDraggingTo</code>. When an instance
     * is created it should be messaged with <code>isValid</code> to insure that dragging can happen (dragging won't be
     * allowed if the two views can not be resized).
     */
    protected class DragController {
        /**
         * Initial location of the divider.
         */
        int initialLocation;

        /**
         * Maximum and minimum positions to drag to.
         */
        int maxLocation, minLocation;

        /**
         * Initial location the mouse down happened at.
         */
        int offset;


        protected DragController(MouseEvent e) {
            ComponentOrientation o = getComponentOrientation();
            boolean ltr = o.isLeftToRight();
            boolean reversed = !ltr && _jideSplitPane.getOrientation() == JideSplitPane.HORIZONTAL_SPLIT;
            Component leftC = reversed ? getSecondComponent(false) : getFirstComponent(false);
            Component rightC = reversed ? getFirstComponent(false) : getSecondComponent(false);

            initialLocation = getLocation().x;
            if (e.getSource() == JideSplitPaneDivider.this) {
                offset = e.getX();
            }
            else { // splitPane
                offset = e.getX() - initialLocation;
            }
            if (leftC == null || rightC == null || offset < -1 ||
                    offset >= _jideSplitPane.getSize().width) {
                // Don't allow dragging.
                maxLocation = -1;
            }
            else {
                int index = _jideSplitPane.indexOf(JideSplitPaneDivider.this);
                int modelLeftWidth = 0;
                int modelRightWidth = 0;
                for (int i = 0; i < index; i++) {
                    Component component = _jideSplitPane.getComponent(i);
                    if (component instanceof JideSplitPaneDivider) {
                        modelLeftWidth += component.getSize().getWidth();
                    }
                    else if (component.isVisible()) {
                        if (((JideBoxLayout) _jideSplitPane.getLayout()).getConstraintMap().get(component) == JideBoxLayout.FIX) {
                            modelLeftWidth += component.getWidth();
                        }
                        else {
                            modelLeftWidth += component.getMinimumSize().getWidth();
                        }
                    }
                }

                for (int i = index + 1; i < _jideSplitPane.getComponentCount(); i++) {
                    Component component = _jideSplitPane.getComponent(i);
                    if (component instanceof JideSplitPaneDivider) {
                        modelRightWidth += component.getSize().getWidth();
                    }
                    else if (component.isVisible()) {
                        if (((JideBoxLayout) _jideSplitPane.getLayout()).getConstraintMap().get(component) == JideBoxLayout.FIX) {
                            modelRightWidth += component.getWidth();
                        }
                        else {
                            modelRightWidth += component.getMinimumSize().getWidth();
                        }
                    }
                }

                minLocation = reversed ? modelRightWidth : modelLeftWidth;
                maxLocation = _jideSplitPane.getWidth() - ((reversed ? modelLeftWidth : modelRightWidth) + (int) getSize().getWidth());
                if (maxLocation < minLocation) minLocation = maxLocation = 0;
            }
        }


        /**
         * Returns true if the dragging session is valid.
         *
         * @return true or false.
         */
        protected boolean isValid() {
            return (maxLocation > 0);
        }


        /**
         * Returns the new position to put the divider at based on the passed in MouseEvent.
         *
         * @param e the mouse event.
         * @return the position of the mouse event after considering the max and min size it is allowed.
         */
        protected int positionForMouseEvent(MouseEvent e) {
            int newX = (e.getSource() == JideSplitPaneDivider.this) ? (e.getX() + getLocation().x) : e.getX();
            newX = Math.min(maxLocation, Math.max(minLocation, newX - offset));
            if (_jideSplitPane.getDividerStepSize() != 0) {
                int distanceFromCurrent = newX - getX();
                newX -= (distanceFromCurrent % _jideSplitPane.getDividerStepSize());
            }

            return newX;
        }


        /**
         * Returns the x argument, since this is used for horizontal splits.
         *
         * @param x x position
         * @param y y position
         * @return the actual position after considering the max and min size it is allowed.
         */
        protected int getNeededLocation(int x, int y) {
            int newX;
            newX = Math.min(maxLocation, Math.max(minLocation, x - offset));
            if (_jideSplitPane.getDividerStepSize() != 0) {
                int distanceFromCurrent = newX - getX();
                newX -= (distanceFromCurrent % _jideSplitPane.getDividerStepSize());
            }
            return newX;
        }


        protected void continueDrag(int newX, int newY) {
            dragDividerTo(getNeededLocation(newX, newY));
        }


        /**
         * Messages dragDividerTo with the new location for the mouse event.
         *
         * @param e the mouse event.
         */
        protected void continueDrag(MouseEvent e) {
            dragDividerTo(positionForMouseEvent(e));
        }


        protected void completeDrag(int x, int y) {
            finishDraggingTo(getNeededLocation(x, y));
        }


        /**
         * Messages finishDraggingTo with the new location for the mouse event.
         *
         * @param e the mouse event.
         */
        protected void completeDrag(MouseEvent e) {
            finishDraggingTo(positionForMouseEvent(e));
        }
    } // End of BasicJideSplitPaneDivider.DragController


    /**
     * Handles the events during a dragging session for a VERTICAL_SPLIT oriented split pane. This continually messages
     * <code>dragDividerTo</code> and then when done messages <code>finishDraggingTo</code>. When an instance is created
     * it should be messaged with <code>isValid</code> to insure that dragging can happen (dragging won't be allowed if
     * the two views can not be resized).
     */
    protected class VerticalDragController extends DragController {
        /* Vertical DragControllers  are now in terms of y, not x. */
        protected VerticalDragController(MouseEvent e) {
            super(e);
            Component leftC = getFirstComponent(false);
            Component rightC = getSecondComponent(false);

            initialLocation = getLocation().y;
            if (e.getSource() == JideSplitPaneDivider.this) {
                offset = e.getY();
            }
            else { // splitPane
                offset = e.getY() - initialLocation;
            }
            if (leftC == null || rightC == null || offset < -1 ||
                    offset >= _jideSplitPane.getSize().height) {
                // Don't allow dragging.
                maxLocation = -1;
            }
            else {
                int index = _jideSplitPane.indexOf(JideSplitPaneDivider.this);
                int modelUpHeight = 0;
                int modelDownHeight = 0;
                for (int i = 0; i < index; i++) {
                    Component component = _jideSplitPane.getComponent(i);
                    if (component instanceof JideSplitPaneDivider) {
                        modelUpHeight += component.getSize().getHeight();
                    }
                    else if (component.isVisible()) {
                        if (((JideBoxLayout) _jideSplitPane.getLayout()).getConstraintMap().get(component) == JideBoxLayout.FIX) {
                            modelUpHeight += component.getHeight();
                        }
                        else {
                            modelUpHeight += component.getMinimumSize().getHeight();
                        }
                    }
                }

                for (int i = index + 1; i < _jideSplitPane.getComponentCount(); i++) {
                    Component component = _jideSplitPane.getComponent(i);
                    if (component instanceof JideSplitPaneDivider) {
                        modelDownHeight += component.getSize().getHeight();
                    }
                    else if (component.isVisible()) {
                        if (((JideBoxLayout) _jideSplitPane.getLayout()).getConstraintMap().get(component) == JideBoxLayout.FIX) {
                            modelDownHeight += component.getHeight();
                        }
                        else {
                            modelDownHeight += component.getMinimumSize().getHeight();
                        }
                    }
                }

                minLocation = modelUpHeight;
                maxLocation = _jideSplitPane.getHeight() - modelDownHeight - (int) getSize().getHeight();
                if (maxLocation < minLocation) minLocation = maxLocation = 0;
            }
        }


        /**
         * Returns the y argument, since this is used for vertical splits.
         */
        @Override
        protected int getNeededLocation(int x, int y) {
            int newY;
            newY = Math.min(maxLocation, Math.max(minLocation, y - offset));
            if (_jideSplitPane.getDividerStepSize() != 0) {
                int distanceFromCurrent = newY - getY();
                newY -= (distanceFromCurrent % _jideSplitPane.getDividerStepSize());
            }
            return newY;
        }


        /**
         * Returns the new position to put the divider at based on the passed in MouseEvent.
         */
        @Override
        protected int positionForMouseEvent(MouseEvent e) {
            int newY = (e.getSource() == JideSplitPaneDivider.this) ? (e.getY() + getLocation().y) : e.getY();
            newY = Math.min(maxLocation, Math.max(minLocation, newY - offset));
            if (_jideSplitPane.getDividerStepSize() != 0) {
                int distanceFromCurrent = newY - getY();
                newY -= (distanceFromCurrent % _jideSplitPane.getDividerStepSize());
            }
            return newY;
        }
    } // End of BasicSplitPaneDividier.VerticalDragController

    /*
     * Added on 05/14/2008 in response to http://www.jidesoft.com/forum/viewtopic.php?p=26074#26074,
     * http://www.jidesoft.com/forum/viewtopic.php?p=5148#5148 and
     * http://www.jidesoft.com/forum/viewtopic.php?p=23403#23403.
     *
     * The addition below provides the option of adding a one-touch button which is capable of expanding/collapsing the
     * split pane (in one click of the mouse).
     *
     * @see #setOneTouchExpandable(boolean)
     */

    /**
     * Indicates that the pane of the left of this component has been collapse by the one-touch button.
     */
    public static final int COLLAPSED_STATE = 0;

    /**
     * Indicates that this divider has not been expanded or collapsed.
     */
    public static final int DEFAULT_STATE = 1;

    /**
     * Indicates that the pane of the right of this component has been collapse by the one-touch button. Hence, the pane
     * on the left has been "expanded".
     */
    public static final int EXPANDED_STATE = 2;

    /**
     * Indicates the current state of this divider. Either expanded, collapsed or in its default state.
     */
    private int _currentState = DEFAULT_STATE;

    /**
     * Button for quickly toggling the left component.
     */
    protected JButton _leftButton = null;

    /**
     * Button for quickly toggling the right component.
     */
    protected JButton _rightButton = null;

    /**
     * Used to paint the triangle on the one-touch buttons.
     */
    private int _triangleSize = 5;

    /**
     * Used as the one-touch button's width.
     */
    private int _buttonWidth = 5;

    /**
     * Used as the one-touch button's height.
     */
    private int _buttonHeight = 10;

    /**
     * The last non-expanded/collapsed position of the divider. We want to keep track of the dividers last position, so
     * if a user collapses the pane on the right of this divider for example, pressing the expand button will revert the
     * divider back to its original location - the last non-expanded/collapsed position.
     *
     * @see JideSplitPaneDivider.OneTouchActionHandler#actionPerformed
     */
    private int _lastPosition;

    /**
     * Invoked when the oneTouchExpandable value of the JideSplitPane changes.<p> </p> Responsible for creating the
     * one-touch buttons and revalidating the UI.
     * <p/>
     * #see JideSplitePane#setOneTouchExpandable(boolean)
     */
    protected void oneTouchExpandableChanged() {

        if (_jideSplitPane.isOneTouchExpandable() && _leftButton == null) {
            _leftButton = createLeftOneTouchButton();
            if (_leftButton != null) {
                _leftButton.addActionListener(new OneTouchActionHandler(true));
                if (_orientation == JideSplitPane.HORIZONTAL_SPLIT) {
                    _leftButton.setBounds(1, 10, _buttonWidth, _buttonHeight);
                }
                else if (_orientation == JideSplitPane.VERTICAL_SPLIT) {
                    //noinspection SuspiciousNameCombination
                    _leftButton.setBounds(10, 1, _buttonHeight, _buttonWidth);
                }
                add(_leftButton);
            }
        }

        if (_jideSplitPane.isOneTouchExpandable() && _rightButton == null) {
            _rightButton = createRightOneTouchButton();
            if (_rightButton != null) {
                _rightButton.addActionListener(new OneTouchActionHandler(false));
                if (_orientation == JideSplitPane.HORIZONTAL_SPLIT) {
                    _rightButton.setBounds(1, 25, _buttonWidth, _buttonHeight);
                }
                else if (_orientation == JideSplitPane.VERTICAL_SPLIT) {
                    //noinspection SuspiciousNameCombination
                    _rightButton.setBounds(25, 1, _buttonHeight, _buttonWidth);
                }
                add(_rightButton);
            }
        }

        if (!(_jideSplitPane.isOneTouchExpandable()) && _leftButton != null) {
            remove(_leftButton);
            _leftButton = null;
        }
        if (!(_jideSplitPane.isOneTouchExpandable()) && _rightButton != null) {
            remove(_rightButton);
            _rightButton = null;
        }

        /*
         * The one-touch buttons should completely collapse the pane in question; as such, all panes should have a
         * their minimum sizes overridden whilst one-touch expandable is on.
         *
         * We fill the minimunSizes array with the current minimum size of each pane for restoration later.
         */
        int paneCount = _jideSplitPane.getPaneCount();
        if (_jideSplitPane.isOneTouchExpandable()) {
            for (int i = 0; i < paneCount; i++) {
                Component component = _jideSplitPane.getPaneAt(i);
                PortingUtils.setMinimumSize(component, new Dimension(0, 0));
            }
        }
        else {
            for (int i = 0; i < paneCount; i++) {
                Component component = _jideSplitPane.getPaneAt(i);
                PortingUtils.setMinimumSize(component, null);
            }
        }
    }

    /**
     * Builds the Button that can be used to collapse the component to the left/above this divider.
     *
     * @return a JButton instance used to collapse the component to the left/above this divider.
     */
    protected JButton createLeftOneTouchButton() {
        JButton b = new JButton() {
            @Override
            public void setBorder(Border b) {
            }

            @Override
            public void paint(Graphics g) {
                if (_jideSplitPane != null) {
                    g.setColor(this.getBackground());
                    if (isOpaque()) {
                        g.fillRect(0, 0, this.getWidth(), this.getHeight());
                    }

                    if (_jideSplitPane.getLeftOneTouchButtonImageIcon() != null) {
                        _jideSplitPane.getLeftOneTouchButtonImageIcon().paintIcon(this, g, 0, 0);
                    }
                    else if (_orientation == JideSplitPane.HORIZONTAL_SPLIT) {

                        /*
                         * If the split pane is horizontally split, paint the 'left' button.
                         */
                        g.setColor(getDarkShadowColor());
                        int size = _triangleSize;
                        for (int i = 0; i < size; i++) {
                            g.drawLine(i, size - i, i, size + i);
                        }
                    }
                    else if (_orientation == JideSplitPane.VERTICAL_SPLIT) {

                        /*
                         * If the split pane is vertically split, paint an 'up' button.
                         */
                        g.setColor(getDarkShadowColor());
                        int size = _triangleSize;
                        for (int i = 0; i < size; i++) {
                            g.drawLine(size - i, i, size + i, i);
                        }
                    }
                }
            }

            @SuppressWarnings({"deprecation"})
            @Override
            public boolean isFocusTraversable() {
                return false;
            }
        };
        b.setMinimumSize(new Dimension(_buttonWidth, _buttonHeight));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setRequestFocusEnabled(false);
        return b;
    }

    /**
     * Builds the rightButton that can be used to expand/collapse a split panes divider to the right.
     *
     * @return a JButton instance used to expand/collapse a split panes divider to the right.
     */
    protected JButton createRightOneTouchButton() {
        JButton b = new JButton() {
            @Override
            public void setBorder(Border b) {
            }

            @Override
            public void paint(Graphics g) {
                if (_jideSplitPane != null) {
                    g.setColor(this.getBackground());
                    if (isOpaque()) {
                        g.fillRect(0, 0, this.getWidth(), this.getHeight());
                    }

                    if (_jideSplitPane.getRightOneTouchButtonImageIcon() != null) {
                        _jideSplitPane.getRightOneTouchButtonImageIcon().paintIcon(this, g, 0, 0);
                    }
                    else if (_orientation == JideSplitPane.HORIZONTAL_SPLIT) {

                        /*
                         * If the split pane is horizontally split, paint the 'right' button.
                         */
                        g.setColor(getDarkShadowColor());
                        int size = _triangleSize;
                        int j = 0;
                        for (int i = size - 1; i >= 0; i--) {
                            g.drawLine(j, size - i, j, size + i);
                            j++;
                        }
                    }
                    else if (_orientation == JideSplitPane.VERTICAL_SPLIT) {

                        /*
                         * If the split pane is vertically split, paint an 'down' button.
                         */
                        g.setColor(getDarkShadowColor());
                        int size = _triangleSize;
                        int j = 0;
                        for (int i = size - 1; i >= 0; i--) {
                            g.drawLine(size - i, j, size + i, j);
                            j++;
                        }
                    }
                }
            }

            @SuppressWarnings({"deprecation"})
            @Override
            public boolean isFocusTraversable() {
                return false;
            }
        };
        b.setMinimumSize(new Dimension(_buttonWidth, _buttonHeight));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setRequestFocusEnabled(false);
        return b;
    }

    /**
     * Returns a dark shadow color. This color is used to paint the left and right buttons graphics. It is based on the
     * current Look and Feel. (And thus fits all look and Feels.)
     *
     * @return UIManager.getColor("controlDkShadow")
     */
    protected Color getDarkShadowColor() {
        return UIManager.getColor("controlDkShadow");
    }

    /**
     * The actionListener that will listen for button presses on either the leftButton or the rightButton. This class is
     * responsible for one-touch expanding/collapsing of the divider.
     */
    protected class OneTouchActionHandler implements ActionListener {

        /**
         * If <code>collapse</code> is true, move the divider to the left/top; otherwise, move the divider to the
         * right/bottom.
         */
        private boolean _collapse;

        /**
         * Constructs the handler responsible for expanding/collapsing the panes on either side of this divider.
         *
         * @param collapse true or false.
         */
        public OneTouchActionHandler(boolean collapse) {
            _collapse = collapse;
        }

        /**
         * Called to collapse or expand this divider.
         */
        public void actionPerformed(ActionEvent e) {

            /*
             * If _collapse is true, move the divider to the left/top; otherwise, move the divider to the right/bottom.
             */

            /*
             * If the left button is pressed. (If _collapse is true.)
             */
            if (_collapse) {
                if (_currentState == COLLAPSED_STATE) {
                    int indexOfDivider = _jideSplitPane.indexOfDivider(JideSplitPaneDivider.this);
                    int dividerPosition = _jideSplitPane.getDividerLocation(indexOfDivider);
                    int previousDividerPosition = getPreviousDividerLocation(true, false);
                    if (dividerPosition != previousDividerPosition) {
                        _currentState = DEFAULT_STATE;
                    }
                }

                if (_currentState == EXPANDED_STATE) {
                    _jideSplitPane.setDividerLocation(JideSplitPaneDivider.this, _lastPosition);
                    _currentState = DEFAULT_STATE;
                }
                else if (_currentState == DEFAULT_STATE) {
                    int indexOfDivider = _jideSplitPane.indexOfDivider(JideSplitPaneDivider.this);
                    _lastPosition = _jideSplitPane.getDividerLocation(indexOfDivider);
                    int loc = getPreviousDividerLocation(true, false);
                    _jideSplitPane.setDividerLocation(JideSplitPaneDivider.this, loc);
                    _currentState = COLLAPSED_STATE;
                }
            }
            /*
             * If the right button is pressed. (If _collapse is false.)
             */
            else {
                if (_currentState == EXPANDED_STATE) {
                    int indexOfDivider = _jideSplitPane.indexOfDivider(JideSplitPaneDivider.this);
                    int dividerPosition = _jideSplitPane.getDividerLocation(indexOfDivider);
                    int nextDividerPosition = getNextDividerLocation(true, false);
                    if (dividerPosition != nextDividerPosition) {
                        _currentState = DEFAULT_STATE;
                    }
                }

                if (_currentState == COLLAPSED_STATE) {
                    _jideSplitPane.setDividerLocation(JideSplitPaneDivider.this, _lastPosition);
                    _currentState = DEFAULT_STATE;
                }
                else if (_currentState == DEFAULT_STATE) {
                    int indexOfDivider = _jideSplitPane.indexOfDivider(JideSplitPaneDivider.this);
                    _lastPosition = _jideSplitPane.getDividerLocation(indexOfDivider);
                    int loc = getNextDividerLocation(true, false);
                    _jideSplitPane.setDividerLocation(JideSplitPaneDivider.this, loc);
                    _currentState = EXPANDED_STATE;
                }
            }
        }
    }

    /**
     * Collapses the divider to the left side (or to the top if vertically).
     */
    public void collapse() {
        if (_leftButton != null) {
            _leftButton.doClick();
        }
        else {
            new OneTouchActionHandler(true).actionPerformed(null);
        }
    }

    /**
     * Expands the divider to the right side (or to the bottom if vertically).
     */
    public void expand() {
        if (_rightButton != null) {
            _rightButton.doClick();
        }
        else {
            new OneTouchActionHandler(false).actionPerformed(null);
        }
    }

    /*
     * End of one-touch expand/collapse addition.
     *
     * Added on 05/14/2008.
     */
}
