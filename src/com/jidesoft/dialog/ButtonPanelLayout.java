/*
 * @(#)ButtonPaneLayout.java
 *
 * Copyright 2002 - 2003 JIDE Software. All rights reserved.
 */

package com.jidesoft.dialog;

import javax.swing.*;
import java.awt.*;
import java.io.PrintStream;
import java.io.Serializable;
import java.util.List;
import java.util.Vector;

class ButtonPanelLayout implements LayoutManager2, Serializable {

    public static boolean DEBUG = false;
    /**
     * Specifies that components should be laid out left to right.
     */
    public static final int X_AXIS = 0;

    /**
     * Specifies that components should be laid out top to bottom.
     */
    public static final int Y_AXIS = 1;

    /**
     * Specifies that components should be laid out in the direction of
     * a line of text as determined by the target container's
     * <code>ComponentOrientation</code> property.
     */
    public static final int LINE_AXIS = 2;

    /**
     * Specifies that components should be laid out in the direction that
     * lines flow across a page as determined by the target container's
     * <code>ComponentOrientation</code> property.
     */
    public static final int PAGE_AXIS = 3;

    private Container _target;

    private transient SizeRequirements[] _xChildren;
    private transient SizeRequirements[] _yChildren;
    private transient SizeRequirements _xTotal;
    private transient SizeRequirements _yTotal;

    private transient PrintStream dbg;

    private List _affirmativeButtons = new Vector(13);
    private List _cancelButtons = new Vector(13);
    private List _helpButtons = new Vector(13);
    private List _otherButtons = new Vector(13);

    private int _totalButtonCount; // calculated value by checkRequests

    private int _totalGroup; // calculated value by checkRequests

    int _minWidth; // calculated value by checkRequests

    int _maxWidth; // calculated value by checkRequests

    int _groupGap = 10; // get from L&F or user can set it

    int _buttonGap = 6; // get from L&F or user can set it

    private int _sizeConstraint = ButtonPanel.NO_LESS_THAN; // get from L&F or user can set it

    private String _buttonOrder = "ACO"; // get from L&F or user can set it

    private String _oppositeButtonOrder = "H"; // get from L&F or user can set it

    private int _minButtonWidth = 75;

    private int _axis = X_AXIS; // get from L&F or user can set it

    private int _alignment = SwingConstants.RIGHT; // get from L&F or user can set it

    /**
     * Creates a layout manager that will lay out components along the
     * given axis.
     *
     * @param target              the container that needs to be laid out
     * @param axis                the axis to lay out components along. Can be one of:
     *                            <code>BoxLayout.X_AXIS</code>,
     *                            <code>BoxLayout.Y_AXIS</code>,
     *                            <code>BoxLayout.LINE_AXIS</code> or
     *                            <code>BoxLayout.PAGE_AXIS</code>
     * @param alignment
     * @param sizeConstraint
     * @param buttonOrder
     * @param oppositeButtonOrder
     * @param buttonGap
     * @param groupGap
     * @throws java.awt.AWTError if the value of <code>axis</code> is invalid
     */
    public ButtonPanelLayout(Container target, int axis, int alignment,
                             int sizeConstraint, String buttonOrder, String oppositeButtonOrder,
                             int buttonGap, int groupGap) {
        if (axis != X_AXIS
                && axis != Y_AXIS
                && axis != LINE_AXIS
                && axis != PAGE_AXIS) {
            throw new AWTError("Invalid axis");
        }
        _axis = axis;
        _target = target;
        _alignment = alignment;
        _sizeConstraint = sizeConstraint;
        _buttonOrder = buttonOrder;
        _oppositeButtonOrder = oppositeButtonOrder;
        _buttonGap = buttonGap;
        _groupGap = groupGap;
    }

    /**
     * Indicates that a child has changed its layout related information,
     * and thus any cached calculations should be flushed.
     * <p/>
     * This method is called by AWT when the invalidate method is called
     * on the Container.  Since the invalidate method may be called
     * asynchronously to the event thread, this method may be called
     * asynchronously.
     *
     * @param target the affected container
     * @throws java.awt.AWTError if the target isn't the container specified to the
     *                           BoxLayout constructor
     */
    public synchronized void invalidateLayout(Container target) {
        checkContainer(target);
        _xChildren = null;
        _yChildren = null;
        _xTotal = null;
        _yTotal = null;
    }

    /**
     * Not used by this class.
     *
     * @param name the name of the component
     * @param comp the component
     */
    public void addLayoutComponent(String name, Component comp) {
        addLayoutComponent(comp, name);
    }

    /**
     * Not used by this class.
     *
     * @param comp the component
     */
    public void removeLayoutComponent(Component comp) {
        if (_affirmativeButtons.contains(comp)) {
            _affirmativeButtons.remove(comp);
        }
        if (_cancelButtons.contains(comp)) {
            _cancelButtons.remove(comp);
        }
        if (_helpButtons.contains(comp)) {
            _helpButtons.remove(comp);
        }
        if (_otherButtons.contains(comp)) {
            _otherButtons.remove(comp);
        }
    }

    /**
     * Not used by this class.
     *
     * @param comp        the component
     * @param constraints constraints
     */
    public void addLayoutComponent(Component comp, Object constraints) {
        if (ButtonPanel.AFFIRMATIVE_BUTTON.equals(constraints)) {
            if (!_affirmativeButtons.contains(comp)) {
                _affirmativeButtons.add(comp);
            }
        }

        if (ButtonPanel.CANCEL_BUTTON.equals(constraints)) {
            if (!_cancelButtons.contains(comp)) {
                _cancelButtons.add(comp);
            }
        }

        if (ButtonPanel.HELP_BUTTON.equals(constraints)) {
            if (!_helpButtons.contains(comp)) {
                _helpButtons.add(comp);
            }
        }

        if (ButtonPanel.OTHER_BUTTON.equals(constraints)) {
            if (!_otherButtons.contains(comp)) {
                _otherButtons.add(comp);
            }
        }
    }

    /**
     * Returns the preferred dimensions for this layout, given the components
     * in the specified target container.
     *
     * @param target the container that needs to be laid out
     * @return the dimensions >= 0 && <= Integer.MAX_VALUE
     * @throws java.awt.AWTError if the target isn't the container specified to the
     *                           BoxLayout constructor
     * @see java.awt.Container
     * @see #minimumLayoutSize
     * @see #maximumLayoutSize
     */
    public Dimension preferredLayoutSize(Container target) {
        Dimension size;
        synchronized (this) {
            checkContainer(target);
            checkRequests();
            size = new Dimension(_xTotal.preferred, _yTotal.preferred);
        }

        Insets insets = target.getInsets();
        size.width =
                (int) Math.min(
                        (long) size.width + (long) insets.left + (long) insets.right,
                        Integer.MAX_VALUE);
        size.height =
                (int) Math.min(
                        (long) size.height + (long) insets.top + (long) insets.bottom,
                        Integer.MAX_VALUE);
        return size;
    }

    /**
     * Returns the minimum dimensions needed to lay out the components
     * contained in the specified target container.
     *
     * @param target the container that needs to be laid out
     * @return the dimensions >= 0 && <= Integer.MAX_VALUE
     * @throws java.awt.AWTError if the target isn't the container specified to the
     *                           BoxLayout constructor
     * @see #preferredLayoutSize
     * @see #maximumLayoutSize
     */
    public Dimension minimumLayoutSize(Container target) {
        Dimension size;
        synchronized (this) {
            checkContainer(target);
            checkRequests();
            size = new Dimension(_xTotal.minimum, _yTotal.minimum);
        }

        Insets insets = target.getInsets();
        size.width =
                (int) Math.min(
                        (long) size.width + (long) insets.left + (long) insets.right,
                        Integer.MAX_VALUE);
        size.height =
                (int) Math.min(
                        (long) size.height + (long) insets.top + (long) insets.bottom,
                        Integer.MAX_VALUE);
        return size;
    }

    /**
     * Returns the maximum dimensions the target container can use
     * to lay out the components it contains.
     *
     * @param target the container that needs to be laid out
     * @return the dimenions >= 0 && <= Integer.MAX_VALUE
     * @throws java.awt.AWTError if the target isn't the container specified to the
     *                           BoxLayout constructor
     * @see #preferredLayoutSize
     * @see #minimumLayoutSize
     */
    public Dimension maximumLayoutSize(Container target) {
        Dimension size;
        synchronized (this) {
            checkContainer(target);
            checkRequests();
            size = new Dimension(_xTotal.maximum, _yTotal.maximum);
        }

        Insets insets = target.getInsets();
        size.width =
                (int) Math.min(
                        (long) size.width + (long) insets.left + (long) insets.right,
                        Integer.MAX_VALUE);
        size.height =
                (int) Math.min(
                        (long) size.height + (long) insets.top + (long) insets.bottom,
                        Integer.MAX_VALUE);
        return size;
    }

    /**
     * Returns the alignment along the X axis for the container.
     * If the box is horizontal, the default
     * alignment will be returned. Otherwise, the alignment needed
     * to place the children along the X axis will be returned.
     *
     * @param target the container
     * @return the alignment >= 0.0f && <= 1.0f
     * @throws java.awt.AWTError if the target isn't the container specified to the
     *                           BoxLayout constructor
     */
    public synchronized float getLayoutAlignmentX(Container target) {
        checkContainer(target);
        checkRequests();
        return _xTotal.alignment;
    }

    /**
     * Returns the alignment along the Y axis for the container.
     * If the box is vertical, the default
     * alignment will be returned. Otherwise, the alignment needed
     * to place the children along the Y axis will be returned.
     *
     * @param target the container
     * @return the alignment >= 0.0f && <= 1.0f
     * @throws java.awt.AWTError if the target isn't the container specified to the
     *                           BoxLayout constructor
     */
    public synchronized float getLayoutAlignmentY(Container target) {
        checkContainer(target);
        checkRequests();
        return _yTotal.alignment;
    }

    /**
     * Called by the AWT <!-- XXX CHECK! --> when the specified container
     * needs to be laid out.
     *
     * @param target the container to lay out
     * @throws java.awt.AWTError if the target isn't the container specified to the
     *                           BoxLayout constructor
     */
    public void layoutContainer(Container target) {
        checkContainer(target);
        int nChildren = target.getComponentCount();
//        int[] xOffsets = new int[nChildren];
//        int[] xSpans = new int[nChildren];
//        int[] yOffsets = new int[nChildren];
//        int[] ySpans = new int[nChildren];

        Dimension alloc = target.getSize();
        Insets in = target.getInsets();
        alloc.width -= in.left + in.right;
        alloc.height -= in.top + in.bottom;

        if (DEBUG) {
            System.out.println("Width:" + alloc.width);
            System.out.println("Height:" + alloc.height);
        }

        // Resolve axis to an absolute value (either X_AXIS or Y_AXIS)
        ComponentOrientation o = target.getComponentOrientation();
        int absoluteAxis = resolveAxis(_axis, o);

        // determine the child placements
        synchronized (this) {
            checkRequests();
            resetBounds();
            if (absoluteAxis == X_AXIS) {
                int y = in.top;
                if (_alignment == SwingConstants.CENTER) {
                    Dimension size = preferredLayoutSize(target);
                    // layout left aligned button first
                    int x = in.left + (alloc.width + size.width) / 2;
                    for (int i = 0; i < getButtonOrder().length(); i++) {
                        char c = getButtonOrder().charAt(getButtonOrder().length() - i - 1);
                        if (c == 'A' || c == 'a') {
                            x = layoutButtonsRightAlign(_affirmativeButtons, x, y, alloc);
                        }
                        else if (c == 'C' || c == 'c') {
                            x = layoutButtonsRightAlign(_cancelButtons, x, y, alloc);
                        }
                        else if (c == 'H' || c == 'h') {
                            x = layoutButtonsRightAlign(_helpButtons, x, y, alloc);
                        }
                        else if (c == 'O' || c == 'o') {
                            x = layoutButtonsRightAlign(_otherButtons, x, y, alloc);
                        }
                    }

                    // layout right aligned button
                    // layout left aligned button first
                    x = in.left + (alloc.width - size.width) / 2;
                    for (int i = 0; i < getOppositeButtonOrder().length(); i++) {
                        char c = getOppositeButtonOrder().charAt(i);
                        if (c == 'A' || c == 'a') {
                            x = layoutButtonsLeftAlign(_affirmativeButtons, x, y, alloc);
                        }
                        else if (c == 'C' || c == 'c') {
                            x = layoutButtonsLeftAlign(_cancelButtons, x, y, alloc);
                        }
                        else if (c == 'H' || c == 'h') {
                            x = layoutButtonsLeftAlign(_helpButtons, x, y, alloc);
                        }
                        else if (c == 'O' || c == 'o') {
                            x = layoutButtonsLeftAlign(_otherButtons, x, y, alloc);
                        }
                    }
                }
                else if (_alignment == SwingConstants.RIGHT) {
                    // layout left aligned button first
                    int x = in.left + alloc.width;
                    for (int i = 0; i < getButtonOrder().length(); i++) {
                        char c = getButtonOrder().charAt(getButtonOrder().length() - i - 1);
                        if (c == 'A' || c == 'a') {
                            x = layoutButtonsRightAlign(_affirmativeButtons, x, y, alloc);
                        }
                        else if (c == 'C' || c == 'c') {
                            x = layoutButtonsRightAlign(_cancelButtons, x, y, alloc);
                        }
                        else if (c == 'H' || c == 'h') {
                            x = layoutButtonsRightAlign(_helpButtons, x, y, alloc);
                        }
                        else if (c == 'O' || c == 'o') {
                            x = layoutButtonsRightAlign(_otherButtons, x, y, alloc);
                        }
                    }

                    // layout right aligned button
                    // layout left aligned button first
                    x = in.left;
                    for (int i = 0; i < getOppositeButtonOrder().length(); i++) {
                        char c = getOppositeButtonOrder().charAt(i);
                        if (c == 'A' || c == 'a') {
                            x = layoutButtonsLeftAlign(_affirmativeButtons, x, y, alloc);
                        }
                        else if (c == 'C' || c == 'c') {
                            x = layoutButtonsLeftAlign(_cancelButtons, x, y, alloc);
                        }
                        else if (c == 'H' || c == 'h') {
                            x = layoutButtonsLeftAlign(_helpButtons, x, y, alloc);
                        }
                        else if (c == 'O' || c == 'o') {
                            x = layoutButtonsLeftAlign(_otherButtons, x, y, alloc);
                        }
                    }
                }
                else if (_alignment == SwingConstants.LEFT) {
                    // layout left aligned button first
                    int x = in.left;
                    for (int i = 0; i < getButtonOrder().length(); i++) {
                        char c = getButtonOrder().charAt(i);
                        if (c == 'A' || c == 'a') {
                            x = layoutButtonsLeftAlign(_affirmativeButtons, x, y, alloc);
                        }
                        else if (c == 'C' || c == 'c') {
                            x = layoutButtonsLeftAlign(_cancelButtons, x, y, alloc);
                        }
                        else if (c == 'H' || c == 'h') {
                            x = layoutButtonsLeftAlign(_helpButtons, x, y, alloc);
                        }
                        else if (c == 'O' || c == 'o') {
                            x = layoutButtonsLeftAlign(_otherButtons, x, y, alloc);
                        }
                    }

                    // layout right aligned button
                    x = in.left + alloc.width;
                    for (int i = 0; i < getOppositeButtonOrder().length(); i++) {
                        char c = getOppositeButtonOrder().charAt(getOppositeButtonOrder().length() - i - 1);
                        if (c == 'A' || c == 'a') {
                            x = layoutButtonsRightAlign(_affirmativeButtons, x, y, alloc);
                        }
                        else if (c == 'C' || c == 'c') {
                            x = layoutButtonsRightAlign(_cancelButtons, x, y, alloc);
                        }
                        else if (c == 'H' || c == 'h') {
                            x = layoutButtonsRightAlign(_helpButtons, x, y, alloc);
                        }
                        else if (c == 'O' || c == 'o') {
                            x = layoutButtonsRightAlign(_otherButtons, x, y, alloc);
                        }
                    }
                }
            }
            else {
                int x = in.left;
                if (_alignment == SwingConstants.TOP) {
                    // layout top aligned button first
                    int y = in.top;
                    for (int i = 0; i < getButtonOrder().length(); i++) {
                        char c = getButtonOrder().charAt(i);
                        if (c == 'A' || c == 'a') {
                            y = layoutButtonsTopAlign(_affirmativeButtons, x, y, alloc);
                        }
                        else if (c == 'C' || c == 'c') {
                            y = layoutButtonsTopAlign(_cancelButtons, x, y, alloc);
                        }
                        else if (c == 'H' || c == 'h') {
                            y = layoutButtonsTopAlign(_helpButtons, x, y, alloc);
                        }
                        else if (c == 'O' || c == 'o') {
                            y = layoutButtonsTopAlign(_otherButtons, x, y, alloc);
                        }
                    }

                    // layout bottom aligned button
                    y = in.top + alloc.height;
                    for (int i = 0; i < getOppositeButtonOrder().length(); i++) {
                        char c = getOppositeButtonOrder().charAt(getOppositeButtonOrder().length() - i - 1);
                        if (c == 'A' || c == 'a') {
                            y = layoutButtonsBottomAlign(_affirmativeButtons, x, y, alloc);
                        }
                        else if (c == 'C' || c == 'c') {
                            y = layoutButtonsBottomAlign(_cancelButtons, x, y, alloc);
                        }
                        else if (c == 'H' || c == 'h') {
                            y = layoutButtonsBottomAlign(_helpButtons, x, y, alloc);
                        }
                        else if (c == 'O' || c == 'o') {
                            y = layoutButtonsBottomAlign(_otherButtons, x, y, alloc);
                        }
                    }
                }
                else if (_alignment == SwingConstants.BOTTOM) {
                    // layout top aligned button first
                    int y = in.top + alloc.height;
                    for (int i = 0; i < getButtonOrder().length(); i++) {
                        char c = getButtonOrder().charAt(getButtonOrder().length() - i - 1);
                        if (c == 'A' || c == 'a') {
                            y = layoutButtonsBottomAlign(_affirmativeButtons, x, y, alloc);
                        }
                        else if (c == 'C' || c == 'c') {
                            y = layoutButtonsBottomAlign(_cancelButtons, x, y, alloc);
                        }
                        else if (c == 'H' || c == 'h') {
                            y = layoutButtonsBottomAlign(_helpButtons, x, y, alloc);
                        }
                        else if (c == 'O' || c == 'o') {
                            y = layoutButtonsBottomAlign(_otherButtons, x, y, alloc);
                        }
                    }

                    // layout bottom aligned button
                    y = in.top;
                    for (int i = 0; i < getOppositeButtonOrder().length(); i++) {
                        char c = getOppositeButtonOrder().charAt(i);
                        if (c == 'A' || c == 'a') {
                            y = layoutButtonsTopAlign(_affirmativeButtons, x, y, alloc);
                        }
                        else if (c == 'C' || c == 'c') {
                            y = layoutButtonsTopAlign(_cancelButtons, x, y, alloc);
                        }
                        else if (c == 'H' || c == 'h') {
                            y = layoutButtonsTopAlign(_helpButtons, x, y, alloc);
                        }
                        else if (c == 'O' || c == 'o') {
                            y = layoutButtonsTopAlign(_otherButtons, x, y, alloc);
                        }
                    }
                }
            }
        }

        if (dbg != null) {
            for (int i = 0; i < nChildren; i++) {
                Component c = target.getComponent(i);
                dbg.println(c.toString());
                dbg.println("X: " + _xChildren[i]);
                dbg.println("Y: " + _yChildren[i]);
            }
        }

    }

    private int layoutButtonsRightAlign(List buttons, int x, int y, Dimension alloc) {
        boolean containsVisibleButton = false;
        for (int i = _target.getComponentCount() - 1; i >= 0; i--) {
            Component component = _target.getComponent(i);
            if (!component.isVisible() || !buttons.contains(component)) {
                continue;
            }
            containsVisibleButton = true;
            int prefWidth = component.getPreferredSize().width;
            int width = prefWidth > _minWidth || shouldKeepPreferredWidth(component) ? prefWidth : _minWidth;
            component.setBounds(x - width, y, width, alloc.height);
            if (DEBUG) {
                System.out.println("layout at x: " + (x - width) + " width: " + width);
            }
            x -= width;
            if (i != 0) {
                x -= _buttonGap;
            }
        }
        if (buttons.size() != 0 && containsVisibleButton) {
            x -= _groupGap;
        }
        if (DEBUG) {
            System.out.println("position: " + x);
        }
        return x;
    }

    private int layoutButtonsLeftAlign(List buttons, int x, int y, Dimension alloc) {
        boolean containsVisibleButton = false;
        for (int i = 0; i < _target.getComponentCount(); i++) {
            Component component = _target.getComponent(i);
            if (!component.isVisible() || !buttons.contains(component)) {
                continue;
            }
            containsVisibleButton = true;
            int prefWidth = component.getPreferredSize().width;
            int width = prefWidth > _minWidth || shouldKeepPreferredWidth(component) ? prefWidth : _minWidth;
            component.setBounds(x, y, width, alloc.height);
            if (DEBUG) {
                System.out.println("layout at x: " + x + " width: " + width);
            }
            x += width;
            if (i != buttons.size() - 1 && containsVisibleButton) {
                x += _buttonGap;
            }
        }
        if (buttons.size() != 0) {
            x += _groupGap;
        }
        if (DEBUG) {
            System.out.println("position: " + x);
        }
        return x;
    }

    private int layoutButtonsBottomAlign(List buttons, int x, int y, Dimension alloc) {
        boolean containsVisibleButton = false;
        for (int i = _target.getComponentCount() - 1; i >= 0; i--) {
            Component component = _target.getComponent(i);
            if (!component.isVisible() || !buttons.contains(component)) {
                continue;
            }
            containsVisibleButton = true;
            Dimension preferredSize = component.getPreferredSize();
            int height = preferredSize.height;
            int prefWidth = preferredSize.width;
            component.setBounds(shouldKeepPreferredWidth(component) ? alloc.width - prefWidth + x : x, y - height, shouldKeepPreferredWidth(component) ? prefWidth : alloc.width, height);
            if (DEBUG) {
                System.out.println("layout at y: " + (y - height) + " height: " + height);
            }
            y -= height;
            if (i != 0) {
                y -= _buttonGap;
            }
        }
        if (buttons.size() != 0 && containsVisibleButton) {
            y -= _groupGap;
        }
        if (DEBUG) {
            System.out.println("position: " + y);
        }
        return y;
    }

    private int layoutButtonsTopAlign(List buttons, int x, int y, Dimension alloc) {
        boolean containsVisibleButton = false;
        for (int i = 0; i < _target.getComponentCount(); i++) {
            Component component = _target.getComponent(i);
            if (!component.isVisible() || !buttons.contains(component)) {
                continue;
            }
            containsVisibleButton = true;
            Dimension preferredSize = component.getPreferredSize();
            int height = preferredSize.height;
            int prefWidth = preferredSize.width;
            component.setBounds(shouldKeepPreferredWidth(component) ? alloc.width - prefWidth + x : x, y, shouldKeepPreferredWidth(component) ? prefWidth : alloc.width, height);
            if (DEBUG) {
                System.out.println("layout at y: " + y + " height: " + height);
            }
            y += height;
            if (i != buttons.size() - 1) {
                y += _buttonGap;
            }
        }
        if (buttons.size() != 0 && containsVisibleButton) {
            y += _groupGap;
        }
        if (DEBUG) {
            System.out.println("position: " + y);
        }
        return y;
    }

    private boolean shouldKeepPreferredWidth(Component component) {
        if (component instanceof JComponent) {
            return Boolean.TRUE.equals(((JComponent) component).getClientProperty(ButtonPanel.KEEP_PREFERRED_WIDTH));
        }
        else {
            return false;
        }
    }

    void checkContainer(Container target) {
        if (this._target != target) {
            throw new AWTError("BorderPaneLayout can't be shared");
        }
        if (!(target instanceof ButtonPanel)) {
            throw new AWTError("Target is not a ButtonPanel");
        }
    }

    int getButtonCountof(List buttons) {
        int count = 0;
        for (int i = 0; i < buttons.size(); i++) {
            JComponent component = (JComponent) buttons.get(i);
            if (component.isVisible()) {
                count++;
            }
        }
        return count;
    }

    void checkRequests() {
        _totalGroup =
                (getButtonCountof(_affirmativeButtons) == 0 ? 0 : 1)
                        + (getButtonCountof(_otherButtons) == 0 ? 0 : 1)
                        + (getButtonCountof(_cancelButtons) == 0 ? 0 : 1)
                        + (getButtonCountof(_helpButtons) == 0 ? 0 : 1);

        _totalButtonCount =
                getButtonCountof(_affirmativeButtons)
                        + getButtonCountof(_otherButtons)
                        + getButtonCountof(_cancelButtons)
                        + getButtonCountof(_helpButtons);


        if (_xChildren == null || _yChildren == null) {
            // The requests have been invalidated... recalculate
            // the request information.
            int componentCount = _target.getComponentCount();
            int visibleComponentCount = componentCount;
            for (int i = 0; i < componentCount; i++) {
                if (!_target.getComponent(i).isVisible()) {
                    visibleComponentCount--;
                }
            }

            _xChildren = new SizeRequirements[visibleComponentCount];
            _yChildren = new SizeRequirements[visibleComponentCount];
            int index = 0;
            for (int i = 0; i < componentCount; i++) {
                Component c = _target.getComponent(i);
                if (!c.isVisible()) {
                    continue;
                }
                Dimension min = c.getMinimumSize();
                Dimension typ = c.getPreferredSize();
                Dimension max = c.getMaximumSize();
                _xChildren[index] =
                        new SizeRequirements(
                                min.width,
                                typ.width,
                                max.width,
                                c.getAlignmentX());
                _yChildren[index] =
                        new SizeRequirements(
                                min.height,
                                typ.height,
                                max.height,
                                c.getAlignmentY());
                if (shouldKeepPreferredWidth(_target.getComponent(i))) {
                    _xChildren[index].maximum = 0;
                }
                index++;
            }

            // Resolve axis to an absolute value (either X_AXIS or Y_AXIS)
            int absoluteAxis =
                    resolveAxis(_axis, _target.getComponentOrientation());

            if (absoluteAxis == X_AXIS) {
                _xTotal = SizeRequirements.getTiledSizeRequirements(_xChildren);
                _yTotal = SizeRequirements.getAlignedSizeRequirements(_yChildren);

                _maxWidth = SizeRequirements.getAlignedSizeRequirements(_xChildren).maximum;

                if (_sizeConstraint == ButtonPanel.SAME_SIZE) {
                    int width = getMinButtonWidth();
                    if (_maxWidth < width) {
                        _maxWidth = width;
                    }
                    _minWidth = _maxWidth;
                }
                else {
                    int width = getMinButtonWidth();
                    if (width == 0) {
                        _minWidth = 75;
                    }
                    else {
                        _minWidth = width;
                    }
                }

                for (int i = 0; i < _xChildren.length; i++) {
                    SizeRequirements sizeRequirements = _xChildren[i];
                    if (sizeRequirements.preferred < _minWidth) {
                        sizeRequirements.preferred = _minWidth;
                    }
                }
                _xTotal = SizeRequirements.getTiledSizeRequirements(_xChildren);

                // add gap
                _xTotal.preferred += (_totalGroup - 1) * _groupGap
                        + (_totalButtonCount - _totalGroup) * _buttonGap;
                _xTotal.minimum += (_totalGroup - 1) * _groupGap
                        + (_totalButtonCount - _totalGroup) * _buttonGap;

            }
            else {
                _xTotal = SizeRequirements.getAlignedSizeRequirements(_xChildren);
                _yTotal = SizeRequirements.getTiledSizeRequirements(_yChildren);

                int width = getMinButtonWidth();
                if (width == 0) {
                    _maxWidth = 75;
                }
                else {
                    _maxWidth = width;
                }
                _minWidth = _maxWidth;
                _xTotal.preferred = (_maxWidth > _xTotal.maximum) ? _maxWidth : _xTotal.preferred;

                // add gap
                _yTotal.preferred += (_totalGroup - 1) * _groupGap
                        + (_totalButtonCount - _totalGroup) * _buttonGap;
                _yTotal.minimum += (_totalGroup - 1) * _groupGap
                        + (_totalButtonCount - _totalGroup) * _buttonGap;
            }
        }
    }

    /**
     * Given one of the 4 axis values, resolve it to an absolute axis.
     * The relative axis values, PAGE_AXIS and LINE_AXIS are converted
     * to their absolute couterpart given the target's ComponentOrientation
     * value.  The absolute axes, X_AXIS and Y_AXIS are returned unmodified.
     *
     * @param axis the axis to resolve
     * @param o    the ComponentOrientation to resolve against
     * @return the resolved axis
     */
    private int resolveAxis(int axis, ComponentOrientation o) {
        int absoluteAxis;
        if (axis == LINE_AXIS) {
            absoluteAxis = o.isHorizontal() ? X_AXIS : Y_AXIS;
        }
        else if (axis == PAGE_AXIS) {
            absoluteAxis = o.isHorizontal() ? Y_AXIS : X_AXIS;
        }
        else {
            absoluteAxis = axis;
        }
        return absoluteAxis;
    }

    public int getGroupGap() {
        return _groupGap;
    }

    public void setGroupGap(int groupGap) {
        _groupGap = groupGap;
        invalidateLayout(_target);
    }

    public int getButtonGap() {
        return _buttonGap;
    }

    public void setButtonGap(int buttonGap) {
        _buttonGap = buttonGap;
        invalidateLayout(_target);
    }

    public int getSizeConstraint() {
        return _sizeConstraint;
    }

    public void setSizeConstraint(int sizeConstraint) {
        _sizeConstraint = sizeConstraint;
        invalidateLayout(_target);
    }

    public int getMinButtonWidth() {
        return _minButtonWidth;
    }

    public void setMinButtonWidth(int minButtonWidth) {
        _minButtonWidth = minButtonWidth;
        invalidateLayout(_target);
    }

    public String getButtonOrder() {
        if (_buttonOrder == null) {
            return "";
        }
        else {
            return _buttonOrder;
        }
    }

    public void setButtonOrder(String buttonOrder) {
        _buttonOrder = buttonOrder;
        invalidateLayout(_target);
    }

    public String getOppositeButtonOrder() {
        if (_oppositeButtonOrder == null) {
            return "";
        }
        else {
            return _oppositeButtonOrder;
        }
    }

    public void setOppositeButtonOrder(String oppositeButtonOrder) {
        _oppositeButtonOrder = oppositeButtonOrder;
        invalidateLayout(_target);
    }

    public int getAxis() {
        return _axis;
    }

    public void setAxis(int axis) {
        _axis = axis;
        invalidateLayout(_target);
    }

    public int getAlignment() {
        return _alignment;
    }

    public void setAlignment(int alignment) {
        _alignment = alignment;
        invalidateLayout(_target);
    }

    void resetBounds() {
        for (int i = 0; i < _affirmativeButtons.size(); i++) {
            Component component = (Component) _affirmativeButtons.get(i);
            component.setBounds(0, 0, 0, 0);
        }
        for (int i = 0; i < _cancelButtons.size(); i++) {
            Component component = (Component) _cancelButtons.get(i);
            component.setBounds(0, 0, 0, 0);
        }
        for (int i = 0; i < _otherButtons.size(); i++) {
            Component component = (Component) _otherButtons.get(i);
            component.setBounds(0, 0, 0, 0);
        }
        for (int i = 0; i < _helpButtons.size(); i++) {
            Component component = (Component) _helpButtons.get(i);
            component.setBounds(0, 0, 0, 0);
        }
    }
}
