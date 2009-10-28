/*
 * @(#)JideBoxLayout.java
 *
 * Copyright 2002 JIDE Software Inc. All rights reserved.
 */
package com.jidesoft.swing;

import com.jidesoft.utils.SecurityUtils;
import com.jidesoft.dialog.JideOptionPane;

import java.awt.*;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * JideBoxLayout is very similar to BoxLayout in the way that all components are arranged either from left to right or
 * from top to bottom. Different \ from BoxLayout, there are three possible constraints when adding component to this
 * layout - FIX, FLEXIBLE and VARY. <ul> <li>FIX: use the preferred size of the component and size is fixed
 * <li>FLEXIBLE: respect the preferred size of the component but size can be changed. <li>VARY: ignore preferred size.
 * Its size is calculated based how much area left. </ul> This is the default layout manager for {@link
 * com.jidesoft.swing.JideSplitPane}.
 */
public class JideBoxLayout implements LayoutManager2, Serializable {

    private static final Logger LOGGER = Logger.getLogger(JideBoxLayout.class.getName());

    /**
     * True if resetToPreferredSizes has been invoked.
     */
    private boolean doReset = true;

    /**
     * Axis, 0 for horizontal, or 1 for vertical.
     */
    protected int _axis;
    protected Container _target;
    private int _gap = 0;

    protected int[] _componentSizes;

    /**
     * For FIX component, the width (or height if vertical) is and will always be the preferred width.
     */
    public static final String FIX = "fix";

    /**
     * FLEXIBLE components try to keep the preferred width. If there isn't enough space, all FLEXIBLE components will
     * shrink proportionally.
     */
    public static final String FLEXIBLE = "flexible";

    /**
     * For VARY component, the width will always be whatever width left. You can allow add multiple FIX or FLEXIBLE
     * components but only one VARY component is allowed.
     */
    public static final String VARY = "vary";

    private final HashMap<Component, Object> _constraintMap = new HashMap<Component, Object>();

    /**
     * Specifies that components should be laid out left to right.
     */
    public static final int X_AXIS = 0;

    /**
     * Specifies that components should be laid out top to bottom.
     */
    public static final int Y_AXIS = 1;

    /**
     * Specifies that components should be laid out in the direction of a line of text as determined by the target
     * container's <code>ComponentOrientation</code> property.
     */
    public static final int LINE_AXIS = 2;

    /**
     * Specifies that components should be laid out in the direction that lines flow across a page as determined by the
     * target container's <code>ComponentOrientation</code> property.
     */
    public static final int PAGE_AXIS = 3;

    private boolean _resetWhenInvalidate = true;
    private boolean _alwaysLayout = false;
    private static final long serialVersionUID = -183922972679053590L;

    /**
     * Creates a layout manager that will lay out components along the given axis.
     *
     * @param target the container that needs to be laid out
     * @throws AWTError if the value of <code>axis</code> is invalid
     */
    public JideBoxLayout(Container target) {
        this(target, X_AXIS);
    }

    /**
     * @param target the container that needs to be laid out
     * @param axis   the axis to lay out components along. Can be one of: <code>JideBoxLayout.X_AXIS</code>,
     *               <code>JideBoxLayout.Y_AXIS</code>, <code>JideBoxLayout.LINE_AXIS</code> or
     *               <code>JideBoxLayout.PAGE_AXIS</code>
     */
    public JideBoxLayout(Container target, int axis) {
        this(target, axis, 0);
    }


    /**
     * @param target the container that needs to be laid out
     * @param axis   the axis to lay out components along. Can be one of: <code>JideBoxLayout.X_AXIS</code>,
     *               <code>JideBoxLayout.Y_AXIS</code>, <code>JideBoxLayout.LINE_AXIS</code> or
     *               <code>JideBoxLayout.PAGE_AXIS</code>
     * @param gap the gap
     */
    public JideBoxLayout(Container target, int axis, int gap) {
        if (axis != X_AXIS && axis != Y_AXIS &&
                axis != LINE_AXIS && axis != PAGE_AXIS) {
            throw new AWTError("Invalid axis");
        }
        _axis = axis;
        _target = target;
        _gap = gap;
    }

    /**
     * Lays out the specified container.
     *
     * @param container the container to be laid out
     */
    public void layoutContainer(Container container) {
        synchronized (container.getTreeLock()) {
            if (LOGGER.isLoggable(Level.FINE)) {
                LOGGER.fine(this + " layoutContainer started");
            }
            Dimension containerSize = container.getSize();
            if (containerSize.height <= 0 || containerSize.width <= 0) {
                return;
            }

            Insets insets = _target.getInsets();

            if (doReset) {
                _componentSizes = new int[_target.getComponentCount()];
                int availableSize = getAvailableSize(containerSize, insets);
                availableSize -= getGapSize();
                if (availableSize <= 0) {
                    return;
                }
                boolean success = calculateComponentSizes(availableSize, 0, _target.getComponentCount());
                if (!success) {
                    return;
                }
                doReset = false;
                if (_componentSizes.length == 0) {
                    container.repaint(); // repaint when the last component is removed.
                }
            }
            else {
                int totalSize = 0;
                for (int componentSize : _componentSizes) {
                    totalSize += componentSize;
                }
                boolean containerResized = totalSize + getGapSize() != getSizeForPrimaryAxis(containerSize);
                if (containerResized) {
                    int availableSize = getAvailableSize(containerSize, insets);
                    availableSize -= getGapSize();
                    if (availableSize <= 0) {
                        return;
                    }
                    boolean success = calculateComponentSizes(availableSize, 0, _target.getComponentCount());
                    if (!success) {
                        return;
                    }
                }
            }

            ComponentOrientation o = _target.getComponentOrientation();
            boolean ltr = o.isLeftToRight();
            int location = getSizeForPrimaryAxis(insets, true);
            boolean needRedoLayout = false;
            if (!ltr && resolveAxis(_axis, o) == X_AXIS) {
                location = containerSize.width - location;
            }
            for (int i = 0; i < _target.getComponentCount(); i++) {
                Component comp = _target.getComponent(i);
                int oldSize = getPreferredSizeOfComponent(comp);
                if (!ltr && resolveAxis(_axis, o) == X_AXIS) {
                    location -= _componentSizes[i];
                    setComponentToSize(comp, _componentSizes[i], location, insets, containerSize);
                    if (LOGGER.isLoggable(Level.FINE)) {
                        LOGGER.fine("layoutContainer index: " + i + " size: " + _componentSizes[i]);
                    }
                    if (_componentSizes[i] != 0)
                        location -= _gap;
                }
                else {
                    setComponentToSize(comp, _componentSizes[i], location, insets, containerSize);
                    if (LOGGER.isLoggable(Level.FINE)) {
                        LOGGER.fine("layoutContainer index: " + i + " size: " + _componentSizes[i]);
                    }
                    location += _componentSizes[i];
                    if (_componentSizes[i] != 0)
                        location += _gap;
                }
                int newSize = getPreferredSizeOfComponent(comp);
                if (newSize != oldSize) {
                    needRedoLayout = true;
                }
            }
            if (LOGGER.isLoggable(Level.FINE)) {
                LOGGER.fine("layoutContainer ended");
            }
            if (_target instanceof JideOptionPane) {
                for (int i = 0; i < container.getComponentCount(); i++) {
                    container.getComponent(i).invalidate();
                }
                if (needRedoLayout) {
                    Component parent = container.getParent();
                    while (parent != null) {
                        if (parent instanceof Dialog) {
                            break;
                        }
                        parent = parent.getParent();
                    }
                    if (parent != null) {
                        ((Dialog) parent).pack();
                    }
                }
            }
        }
    }

    protected boolean calculateComponentSizes(int availableSize, int startIndex, int endIndex) {
        int availableSizeExcludeFixed = availableSize;
        int varMinSize = 0;
        int flexMinSize = 0;
        int varIndex = -1;
        int totalFlexSize = 0;
        int totalFlexSizeMinusMin = 0;
        int lastFlexIndex = -1;
        int lastNoneZeroFlexIndex = -1;
        for (int i = startIndex; i < endIndex; i++) {
            Component comp = _target.getComponent(i);
            if (!comp.isVisible()) {
                continue;
            }
            Object constraint = _constraintMap.get(comp);
            int minimumSize = getSizeForPrimaryAxis(comp.getMinimumSize());
            int preferredSize = getSizeForPrimaryAxis(getPreferredSizeOf(comp, i));
            if (FIX.equals(constraint)) {
                availableSizeExcludeFixed -= Math.max(preferredSize, minimumSize);
            }
            else if (VARY.equals(constraint)) {
                varIndex = i;
                getPreferredSizeOf(comp, i); // there is a bug in jdk1.5 which minimum size returns a large number if preferred size is not call.
                varMinSize = minimumSize;
            }
            else /* if (FLEXIBLE.equals(constraint)) */ {
                if (preferredSize > minimumSize) {
                    totalFlexSizeMinusMin += preferredSize - minimumSize;
                }
                totalFlexSize += preferredSize;
                flexMinSize += minimumSize;
                lastFlexIndex = i;
                // prevent the zero width column has a column width like 1 or 2 eventually
                if (preferredSize != 0) {
                    lastNoneZeroFlexIndex = i;
                }
            }
        }

        if (!isAlwaysLayout() && "false".equals(SecurityUtils.getProperty("JideBoxLayout.alwaysLayout", "false")) && availableSizeExcludeFixed - varMinSize < 0) {
            return false;
        }

        boolean hasVary = varIndex != -1;
        boolean expand = availableSizeExcludeFixed - varMinSize >= totalFlexSize;

        if (!hasVary || (hasVary && !expand)) {
            double resizeRatio;
            if (expand) {
                resizeRatio = totalFlexSize == 0 ? 0 : (double) (availableSizeExcludeFixed - varMinSize) / (double) totalFlexSize;
            }
            else {
                resizeRatio = totalFlexSizeMinusMin == 0 ? 0 : (double) (availableSizeExcludeFixed - varMinSize - flexMinSize) / (double) totalFlexSizeMinusMin;
            }

            for (int i = startIndex; i < endIndex; i++) {
                Component comp = _target.getComponent(i);
                if (!comp.isVisible()) {
                    setComponentSize(i, 0);
                }
                else {
                    Object constraint = _constraintMap.get(comp);
                    int minimumSize = getSizeForPrimaryAxis(comp.getMinimumSize());
                    int preferredSize = getSizeForPrimaryAxis(getPreferredSizeOf(comp, i));
                    if (FIX.equals(constraint)) {
                        setComponentSize(i, Math.max(preferredSize, minimumSize));
                    }
                    else if (VARY.equals(constraint)) {
                        setComponentSize(i, varMinSize);
                    }
                    else /* if (FLEXIBLE.equals(constraint)) */ {
                        if (expand) {
                            setComponentSize(i, (int) (preferredSize * resizeRatio));
                        }
                        else {
                            setComponentSize(i, minimumSize + (int) ((preferredSize - minimumSize) * resizeRatio));
                        }
                    }
                }
            }
        }
        else { // if (expand && hasVary) { // VARY component get all extra spaces.
            for (int i = startIndex; i < endIndex; i++) {
                Component comp = _target.getComponent(i);
                if (!comp.isVisible()) {
                    setComponentSize(i, 0);
                }
                else {
                    Object constraint = _constraintMap.get(comp);
                    int minimumSize = getSizeForPrimaryAxis(comp.getMinimumSize());
                    int preferredSize = getSizeForPrimaryAxis(getPreferredSizeOf(comp, i));
                    if (FIX.equals(constraint)) {
                        setComponentSize(i, Math.max(preferredSize, minimumSize));
                    }
                    else if (VARY.equals(constraint)) {
                        setComponentSize(i, availableSizeExcludeFixed - totalFlexSize);
                    }
                    else /* if (FLEXIBLE.equals(constraint)) */ {
                        setComponentSize(i, Math.max(preferredSize, minimumSize));
                    }
                }
            }
        }

        int totalActualSize = 0;
        for (int i = startIndex; i < endIndex; i++) {
            totalActualSize += _componentSizes[i];
        }

        if (totalActualSize != availableSize) {
            if (varIndex != -1) {
                setComponentSize(varIndex, _componentSizes[varIndex] + (availableSize - totalActualSize));
            }
            else if (lastNoneZeroFlexIndex != -1) {
                setComponentSize(lastNoneZeroFlexIndex, _componentSizes[lastNoneZeroFlexIndex] + (availableSize - totalActualSize));
            }
            else if (lastFlexIndex != -1) {
                setComponentSize(lastFlexIndex, _componentSizes[lastFlexIndex] + (availableSize - totalActualSize));
            }
        }

        return true;
    }

    private void setComponentSize(int index, int size) {
        if (LOGGER.isLoggable(Level.FINE)) {
            LOGGER.fine("setComponentSize index: " + index + " size: " + size);
        }
        _componentSizes[index] = size;
    }

    /**
     * If the layout manager uses a per-component string, adds the component <code>comp</code> to the layout,
     * associating it with the string specified by <code>name</code>.
     *
     * @param name      the string to be associated with the component
     * @param component the component to be added
     */
    public void addLayoutComponent(String name, Component component) {
        layoutReset();
    }

    /**
     * Returns the minimum size needed to contain the children. The width is the sum of all the children min widths and
     * the height is the largest of the children minimum heights.
     */
    public Dimension minimumLayoutSize(Container container) {
        int minPrimary = 0;
        int minSecondary = 0;
        Insets insets = _target.getInsets();

        synchronized (container.getTreeLock()) {
            for (int i = 0; i < _target.getComponentCount(); i++) {
                Component comp = _target.getComponent(i);
                if (!comp.isVisible()) {
                    continue;
                }
                Object constraint = _constraintMap.get(comp);
                Dimension minimumSize = comp.getMinimumSize();
                if (FIX.equals(constraint)) {
                    minPrimary += getPreferredSizeOfComponent(comp);
                }
                else {
                    minPrimary += getSizeForPrimaryAxis(minimumSize);
                }
                int secSize = getSizeForSecondaryAxis(minimumSize);
                if (secSize > minSecondary)
                    minSecondary = secSize;
            }

            if (insets != null) {
                minPrimary += getSizeForPrimaryAxis(insets, true) +
                        getSizeForPrimaryAxis(insets, false);
                minSecondary += getSizeForSecondaryAxis(insets, true) +
                        getSizeForSecondaryAxis(insets, false);
            }
        }

        ComponentOrientation o = _target.getComponentOrientation();
        if (resolveAxis(_axis, o) == X_AXIS) {
            return new Dimension(minPrimary + getGapSize(), minSecondary);
        }
        else {
            return new Dimension(minSecondary, minPrimary + getGapSize());
        }
    }


    /**
     * Returns the preferred size needed to contain the children. The width is the sum of all the children preferred
     * widths and the height is the largest of the children preferred heights.
     */
    public Dimension preferredLayoutSize(Container container) {
        int prePrimary = 0;
        int preSecondary = 0;
        Insets insets = _target.getInsets();

        synchronized (container.getTreeLock()) {
            for (int i = 0; i < _target.getComponentCount(); i++) {
                Component comp = _target.getComponent(i);
                if (!comp.isVisible()) {
                    continue;
                }
                Dimension preferredSize = getPreferredSizeOf(comp, i);
                prePrimary += getSizeForPrimaryAxis(preferredSize);
                int secSize = getSizeForSecondaryAxis(preferredSize);
                if (secSize > preSecondary)
                    preSecondary = secSize;
            }

            if (insets != null) {
                prePrimary += getSizeForPrimaryAxis(insets, true) +
                        getSizeForPrimaryAxis(insets, false);
                preSecondary += getSizeForSecondaryAxis(insets, true) +
                        getSizeForSecondaryAxis(insets, false);
            }
        }
        if (_axis == 0) {
            return new Dimension(prePrimary + getGapSize(), preSecondary);
        }
        else {
            return new Dimension(preSecondary, prePrimary + getGapSize());
        }
    }

    private int getGapSize() {
        if (_gap == 0) {
            return 0;
        }
        else {
            int count = 0;
            for (int i = 0; i < _target.getComponentCount(); i++) {
                if (_target.getComponent(i).isVisible()) {
                    count++;
                }
            }
            return Math.max(0, (count - 1)) * _gap;
        }
    }


    /**
     * Removes the specified component from the layout.
     *
     * @param comp the component to be removed
     */
    public void removeLayoutComponent(Component comp) {
        _constraintMap.remove(comp);

        if (comp instanceof JideSplitPaneDivider)
            layoutReset();
    }

    //
    // LayoutManager2
    //


    /**
     * Adds the specified component to the layout, using the specified constraint object.
     *
     * @param comp        the component to be added
     * @param constraints where/how the component is added to the layout.
     */
    public void addLayoutComponent(Component comp, Object constraints) {
        if (constraints == null)
            _constraintMap.put(comp, FLEXIBLE);
        else
            _constraintMap.put(comp, constraints);
        layoutReset();
    }

    private void layoutReset() {
        doReset = true;
        if (LOGGER.isLoggable(Level.FINE)) {
            LOGGER.fine(this + " layoutReset");
        }
    }

    /**
     * Returns the alignment along the x axis.  This specifies how the component would like to be aligned relative to
     * other components.  The value should be a number between 0 and 1 where 0 represents alignment along the origin, 1
     * is aligned the furthest away from the origin, 0.5 is centered, etc.
     */
    public synchronized float getLayoutAlignmentX(Container target) {
        return 0.0f;
    }


    /**
     * Returns the alignment along the y axis.  This specifies how the component would like to be aligned relative to
     * other components.  The value should be a number between 0 and 1 where 0 represents alignment along the origin, 1
     * is aligned the furthest away from the origin, 0.5 is centered, etc.
     */
    public synchronized float getLayoutAlignmentY(Container target) {
        return 0.0f;
    }


    /**
     * Invalidates the layout, indicating that if the layout manager has cached information it should be discarded.
     */
    public synchronized void invalidateLayout(Container c) {
        if (isResetWhenInvalidate() || componentCountChanged(c)) {
            layoutReset();
        }
    }

    protected boolean componentCountChanged(Container c) {
        if (_componentSizes == null) {
            return true;
        }
        int oldLength = 0;
        for (int _componentSize : _componentSizes) {
            if (_componentSize > 0) {
                oldLength++;
            }
        }
        int newLength = 0;
        for (int i = 0; i < c.getComponentCount(); i++) {
            if (c.getComponent(i).isVisible()) {
                newLength++;
            }
        }
        return newLength != oldLength;
    }

    /**
     * Returns the maximum layout size, which is Integer.MAX_VALUE in both directions.
     */
    public Dimension maximumLayoutSize(Container target) {
        return new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE);
    }

    /**
     * Returns the width of the passed in Components preferred size.

     * @param c the component
     * @return the preferred size of the component.
     */
    protected int getPreferredSizeOfComponent(Component c) {
        return getSizeForPrimaryAxis(c.getPreferredSize());
    }


    /**
     * Returns the width of the passed in Components minimum size.

     * @param c the component
     * @return the minimum size of the component.
     */
    int getMinimumSizeOfComponent(Component c) {
        return getSizeForPrimaryAxis(c.getMinimumSize());
    }


    /**
     * Returns the width of the passed in component.

     * @param c the component
     * @return the size of the component.
     */
    protected int getSizeOfComponent(Component c) {
        return getSizeForPrimaryAxis(c.getSize());
    }

    /**
     * Returns the available width based on the container size and Insets.

     * @param containerSize the size of the container
     * @param insets the insets
     * @return the available size.
     */
    protected int getAvailableSize(Dimension containerSize,
                                   Insets insets) {
        if (insets == null)
            return getSizeForPrimaryAxis(containerSize);
        return (getSizeForPrimaryAxis(containerSize) -
                (getSizeForPrimaryAxis(insets, true) +
                        getSizeForPrimaryAxis(insets, false)));
    }


    /**
     * Returns the left inset, unless the Insets are null in which case 0 is returned.
     *
     * @param insets the insets
     * @return the initial location.
     */
    protected int getInitialLocation(Insets insets) {
        if (insets != null)
            return getSizeForPrimaryAxis(insets, true);
        return 0;
    }

    /**
     * Sets the width of the component c to be size, placing its x location at location, y to the insets.top and height
     * to the containersize.height less the top and bottom insets.
     *
     * @param c the component
     * @param size the size of the component
     * @param location the location of the component
     * @param insets the insets of the component
     * @param containerSize the size of the container
     */
    protected void setComponentToSize(Component c, int size,
                                      int location, Insets insets,
                                      Dimension containerSize) {
        if (insets != null) {
            ComponentOrientation o = _target.getComponentOrientation();
            if (resolveAxis(_axis, o) == X_AXIS) {
                c.setBounds(Math.max(location, 0),
                        Math.max(insets.top, 0),
                        Math.max(size, 0),
                        Math.max(containerSize.height - (insets.top + insets.bottom), 0));
            }
            else {
                c.setBounds(Math.max(insets.left, 0),
                        Math.max(location, 0),
                        Math.max(containerSize.width - (insets.left + insets.right), 0),
                        Math.max(size, 0));
            }
        }
        else {
            ComponentOrientation o = _target.getComponentOrientation();
            if (resolveAxis(_axis, o) == X_AXIS) {
                c.setBounds(Math.max(location, 0),
                        0,
                        Math.max(size, 0),
                        Math.max(containerSize.height, 0));
            }
            else {
                c.setBounds(0,
                        Math.max(location, 0),
                        Math.max(containerSize.width, 0),
                        Math.max(size, 0));
            }
        }
    }

    /*
     * If the axis == 0, the width is returned, otherwise the height.
     */
    int getSizeForPrimaryAxis(Dimension size) {
        ComponentOrientation o = _target.getComponentOrientation();
        if (resolveAxis(_axis, o) == X_AXIS) {
            return size.width;
        }
        else {
            return size.height;
        }
    }

    /*
     * If the axis == X_AXIS, the width is returned, otherwise the height.
     */
    int getSizeForSecondaryAxis(Dimension size) {
        ComponentOrientation o = _target.getComponentOrientation();
        if (resolveAxis(_axis, o) == X_AXIS) {
            return size.height;
        }
        else {
            return size.width;
        }
    }

    /*
     * Returns a particular value of the inset identified by the axis and <code>isTop</code><p>. axis isTop 0    true -
     * left 0    false   - right 1    true    - top 1    false   - bottom
     */
    int getSizeForPrimaryAxis(Insets insets, boolean isTop) {
        ComponentOrientation o = _target.getComponentOrientation();
        if (resolveAxis(_axis, o) == X_AXIS) {
            if (isTop) {
                return insets.left;
            }
            else {
                return insets.right;
            }
        }
        else {
            if (isTop) {
                return insets.top;
            }
            else {
                return insets.bottom;
            }
        }
    }

    /*
     * Returns a particular value of the inset identified by the axis and <code>isTop</code><p>. axis isTop 0    true -
     * left 0    false   - right 1    true    - top 1    false   - bottom
     */
    int getSizeForSecondaryAxis(Insets insets, boolean isTop) {
        ComponentOrientation o = _target.getComponentOrientation();
        if (resolveAxis(_axis, o) == X_AXIS) {
            if (isTop) {
                return insets.top;
            }
            else {
                return insets.bottom;
            }
        }
        else {
            if (isTop) {
                return insets.left;
            }
            else {
                return insets.right;
            }
        }
    }

    /**
     * Gets the map of constraints.
     *
     * @return the map of constraints
     */
    public Map<Component, Object> getConstraintMap() {
        return _constraintMap;
    }

    /**
     * Given one of the 4 axis values, resolve it to an absolute axis. The relative axis values, PAGE_AXIS and LINE_AXIS
     * are converted to their absolute counterpart given the target's ComponentOrientation value.  The absolute axes,
     * X_AXIS and Y_AXIS are returned unmodified.
     *
     * @param axis the axis to resolve
     * @param o    the ComponentOrientation to resolve against
     * @return the resolved axis
     */
    protected static int resolveAxis(int axis, ComponentOrientation o) {
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

    /**
     * Gets the gap between each component.
     *
     * @return the gap between each component.
     */
    public int getGap() {
        return _gap;
    }

    /**
     * Sets the gap between each component. Make sure you cal doLayout() after you change the gap.
     *
     * @param gap the gap
     */
    public void setGap(int gap) {
        _gap = gap;
    }

    protected Dimension getPreferredSizeOf(Component comp, int atIndex) {
        Dimension preferredSize = comp.getPreferredSize();
        Dimension minimumSize = comp.getMinimumSize();
        if (preferredSize.height < minimumSize.height) {
            preferredSize.height = minimumSize.height;
        }
        if (preferredSize.width < minimumSize.width) {
            preferredSize.width = minimumSize.width;
        }
        Dimension maximumSize = comp.getMaximumSize();
        if (preferredSize.height > maximumSize.height) {
            preferredSize.height = maximumSize.height;
        }
        if (preferredSize.width > maximumSize.width) {
            preferredSize.width = maximumSize.width;
        }
        return preferredSize;
    }

    /**
     * Checks of the layout should be reset when {@link #invalidateLayout(java.awt.Container)} is called.
     *
     * @return true or false.
     */
    public boolean isResetWhenInvalidate() {
        return _resetWhenInvalidate;
    }

    /**
     * Sets the flag if the layout should be reset when {@link #invalidateLayout(java.awt.Container)} is called.
     *
     * @param resetWhenInvalidate the flag
     */
    public void setResetWhenInvalidate(boolean resetWhenInvalidate) {
        _resetWhenInvalidate = resetWhenInvalidate;
    }

    /**
     * Gets the axis.
     *
     * @return the axis.
     */
    public int getAxis() {
        return _axis;
    }

    /**
     * Sets the axis. After changing the axis, you need to call doLayout method on the container which has this layout.
     *
     * @param axis the new axis.
     */
    public void setAxis(int axis) {
        _axis = axis;
    }

    /**
     * Checks if the alwaysLayout flag is true. If true, the layout manager will layout the components even there is no
     * way to satisfy the minimum size requirements from all FIXED components. By default, it is false.
     *
     * @return true or false.
     */
    public boolean isAlwaysLayout() {
        return _alwaysLayout;
    }

    /**
     * Sets the alwaysLayout flag.
     *
     * @param alwaysLayout true to always layout components even there is no way to satisfy the minimum size
     *                     requirements from all FIXED components.
     */
    public void setAlwaysLayout(boolean alwaysLayout) {
        _alwaysLayout = alwaysLayout;
    }
}
