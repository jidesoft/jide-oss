/*
 * @(#)JideBorderLayout.java
 *
 * Copyright 2002 JIDE Software Inc. All rights reserved.

 * Copyright (c) 1995, 2006, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.jidesoft.swing;

import java.awt.*;

/**
 * This is a modified version of <code>BorderLayout</code>. Different from <code>BorderLayout</code>, the TOP and BOTTOM
 * component's width are same as CENTER component's width. In BorderLayout, their width are the same as the container's
 * width.
 *
 * @see BorderLayout
 */

public class JideBorderLayout implements LayoutManager2,
        java.io.Serializable {
    /**
     * Constructs a border layout with the horizontal gaps between components. The horizontal gap is specified by
     * <code>hgap</code>.
     *
     * @serial
     * @see #getHgap()
     * @see #setHgap(int)
     */
    int hgap;

    /**
     * Constructs a border layout with the vertical gaps between components. The vertical gap is specified by
     * <code>vgap</code>.
     *
     * @serial
     * @see #getVgap()
     * @see #setVgap(int)
     */
    int vgap;

    /**
     * Constant to specify components location to be the north portion of the border layout.
     *
     * @serial
     * @see #getChild(String, boolean)
     * @see #addLayoutComponent
     * @see #getLayoutAlignmentX
     * @see #getLayoutAlignmentY
     * @see #removeLayoutComponent
     */
    Component north;
    /**
     * Constant to specify components location to be the west portion of the border layout.
     *
     * @serial
     * @see #getChild(String, boolean)
     * @see #addLayoutComponent
     * @see #getLayoutAlignmentX
     * @see #getLayoutAlignmentY
     * @see #removeLayoutComponent
     */
    Component west;
    /**
     * Constant to specify components location to be the east portion of the border layout.
     *
     * @serial
     * @see #getChild(String, boolean)
     * @see #addLayoutComponent
     * @see #getLayoutAlignmentX
     * @see #getLayoutAlignmentY
     * @see #removeLayoutComponent
     */
    Component east;
    /**
     * Constant to specify components location to be the south portion of the border layout.
     *
     * @serial
     * @see #getChild(String, boolean)
     * @see #addLayoutComponent
     * @see #getLayoutAlignmentX
     * @see #getLayoutAlignmentY
     * @see #removeLayoutComponent
     */
    Component south;
    /**
     * Constant to specify components location to be the center portion of the border layout.
     *
     * @serial
     * @see #getChild(String, boolean)
     * @see #addLayoutComponent
     * @see #getLayoutAlignmentX
     * @see #getLayoutAlignmentY
     * @see #removeLayoutComponent
     */
    Component center;

    /**
     * A relative positioning constant, that can be used instead of north, south, east, west or center. mixing the two
     * types of constants can lead to unpredictable results.  If you use both types, the relative constants will take
     * precedence. For example, if you add components using both the <code>NORTH</code> and
     * <code>BEFORE_FIRST_LINE</code> constants in a container whose orientation is <code>LEFT_TO_RIGHT</code>, only the
     * <code>BEFORE_FIRST_LINE</code> will be laid out. This will be the same for lastLine, firstItem, lastItem.
     *
     * @serial
     */
    Component firstLine;
    /**
     * A relative positioning constant, that can be used instead of north, south, east, west or center. Please read
     * Description for firstLine.
     *
     * @serial
     */
    Component lastLine;
    /**
     * A relative positioning constant, that can be used instead of north, south, east, west or center. Please read
     * Description for firstLine.
     *
     * @serial
     */
    Component firstItem;
    /**
     * A relative positioning constant, that can be used instead of north, south, east, west or center. Please read
     * Description for firstLine.
     *
     * @serial
     */
    Component lastItem;

    /**
     * The north layout constraint (top of container).
     */
    public static final String NORTH = "North";

    /**
     * The south layout constraint (bottom of container).
     */
    public static final String SOUTH = "South";

    /**
     * The east layout constraint (right side of container).
     */
    public static final String EAST = "East";

    /**
     * The west layout constraint (left side of container).
     */
    public static final String WEST = "West";

    /**
     * The center layout constraint (middle of container).
     */
    public static final String CENTER = "Center";

    /**
     * Synonym for PAGE_START.  Exists for compatibility with previous versions.  PAGE_START is preferred.
     *
     * @see #PAGE_START
     * @since 1.2
     */
    public static final String BEFORE_FIRST_LINE = "First";

    /**
     * Synonym for PAGE_END.  Exists for compatibility with previous versions.  PAGE_END is preferred.
     *
     * @see #PAGE_END
     * @since 1.2
     */
    public static final String AFTER_LAST_LINE = "Last";

    /**
     * Synonym for LINE_START.  Exists for compatibility with previous versions.  LINE_START is preferred.
     *
     * @see #LINE_START
     * @since 1.2
     */
    public static final String BEFORE_LINE_BEGINS = "Before";

    /**
     * Synonym for LINE_END.  Exists for compatibility with previous versions.  LINE_END is preferred.
     *
     * @see #LINE_END
     * @since 1.2
     */
    public static final String AFTER_LINE_ENDS = "After";

    /**
     * The component comes before the first line of the layout's content. For Western, left-to-right and top-to-bottom
     * orientations, this is equivalent to NORTH.
     *
     * @see java.awt.Component#getComponentOrientation
     * @since 1.4
     */
    public static final String PAGE_START = BEFORE_FIRST_LINE;

    /**
     * The component comes after the last line of the layout's content. For Western, left-to-right and top-to-bottom
     * orientations, this is equivalent to SOUTH.
     *
     * @see java.awt.Component#getComponentOrientation
     * @since 1.4
     */
    public static final String PAGE_END = AFTER_LAST_LINE;

    /**
     * The component goes at the beginning of the line direction for the layout. For Western, left-to-right and
     * top-to-bottom orientations, this is equivalent to WEST.
     *
     * @see java.awt.Component#getComponentOrientation
     * @since 1.4
     */
    public static final String LINE_START = BEFORE_LINE_BEGINS;

    /**
     * The component goes at the end of the line direction for the layout. For Western, left-to-right and top-to-bottom
     * orientations, this is equivalent to EAST.
     *
     * @see java.awt.Component#getComponentOrientation
     * @since 1.4
     */
    public static final String LINE_END = AFTER_LINE_ENDS;

    /*
     * JDK 1.1 serialVersionUID
     */
    private static final long serialVersionUID = -8658291919501921765L;

    /**
     * Constructs a new border layout with no gaps between components.
     */
    public JideBorderLayout() {
        this(0, 0);
    }

    /**
     * Constructs a border layout with the specified gaps between components. The horizontal gap is specified by
     * <code>hgap</code> and the vertical gap is specified by <code>vgap</code>.
     *
     * @param hgap the horizontal gap.
     * @param vgap the vertical gap.
     */
    public JideBorderLayout(int hgap, int vgap) {
        this.hgap = hgap;
        this.vgap = vgap;
    }

    /**
     * Returns the horizontal gap between components.
     *
     * @since JDK1.1
     */
    public int getHgap() {
        return hgap;
    }

    /**
     * Sets the horizontal gap between components.
     *
     * @param hgap the horizontal gap between components
     * @since JDK1.1
     */
    public void setHgap(int hgap) {
        this.hgap = hgap;
    }

    /**
     * Returns the vertical gap between components.
     *
     * @since JDK1.1
     */
    public int getVgap() {
        return vgap;
    }

    /**
     * Sets the vertical gap between components.
     *
     * @param vgap the vertical gap between components
     * @since JDK1.1
     */
    public void setVgap(int vgap) {
        this.vgap = vgap;
    }

    /**
     * Adds the specified component to the layout, using the specified constraint object.  For border layouts, the
     * constraint must be one of the following constants:  <code>NORTH</code>, <code>SOUTH</code>, <code>EAST</code>,
     * <code>WEST</code>, or <code>CENTER</code>.
     * <p/>
     * Most applications do not call this method directly. This method is called when a component is added to a
     * container using the <code>Container.add</code> method with the same argument types.
     *
     * @param comp        the component to be added.
     * @param constraints an object that specifies how and where the component is added to the layout.
     * @throws IllegalArgumentException if the constraint object is not a string, or if it not one of the five specified
     *                                  constants.
     * @see java.awt.Container#add(java.awt.Component, java.lang.Object)
     * @since JDK1.1
     */
    public void addLayoutComponent(Component comp, Object constraints) {
        synchronized (comp.getTreeLock()) {
            if ((constraints == null) || (constraints instanceof String)) {
                addLayoutComponent((String) constraints, comp);
            }
            else {
                throw new IllegalArgumentException("cannot add to layout: constraint must be a string (or null)");
            }
        }
    }

    /**
     * @deprecated replaced by <code>addLayoutComponent(Component, Object)</code>.
     */
    @Deprecated
    public void addLayoutComponent(String name, Component comp) {
        synchronized (comp.getTreeLock()) {
            /* Special case:  treat null the same as "Center". */
            if (name == null) {
                name = "Center";
            }

            /* Assign the component to one of the known regions of the layout.
            */
            if ("Center".equals(name)) {
                center = comp;
            }
            else if ("North".equals(name)) {
                north = comp;
            }
            else if ("South".equals(name)) {
                south = comp;
            }
            else if ("East".equals(name)) {
                east = comp;
            }
            else if ("West".equals(name)) {
                west = comp;
            }
            else if (BEFORE_FIRST_LINE.equals(name)) {
                firstLine = comp;
            }
            else if (AFTER_LAST_LINE.equals(name)) {
                lastLine = comp;
            }
            else if (BEFORE_LINE_BEGINS.equals(name)) {
                firstItem = comp;
            }
            else if (AFTER_LINE_ENDS.equals(name)) {
                lastItem = comp;
            }
            else {
                throw new IllegalArgumentException("cannot add to layout: unknown constraint: " + name);
            }
        }
    }

    /**
     * Removes the specified component from this border layout. This method is called when a container calls its
     * <code>remove</code> or <code>removeAll</code> methods. Most applications do not call this method directly.
     *
     * @param comp the component to be removed.
     * @see java.awt.Container#remove(java.awt.Component)
     * @see java.awt.Container#removeAll()
     */
    public void removeLayoutComponent(Component comp) {
        synchronized (comp.getTreeLock()) {
            if (comp == center) {
                center = null;
            }
            else if (comp == north) {
                north = null;
            }
            else if (comp == south) {
                south = null;
            }
            else if (comp == east) {
                east = null;
            }
            else if (comp == west) {
                west = null;
            }
            if (comp == firstLine) {
                firstLine = null;
            }
            else if (comp == lastLine) {
                lastLine = null;
            }
            else if (comp == firstItem) {
                firstItem = null;
            }
            else if (comp == lastItem) {
                lastItem = null;
            }
        }
    }

    /**
     * Gets the component that was added using the given constraint
     *
     * @param constraints the desired constraint, one of <code>CENTER</code>, <code>NORTH</code>, <code>SOUTH</code>,
     *                    <code>WEST</code>, <code>EAST</code>, <code>PAGE_START</code>, <code>PAGE_END</code>,
     *                    <code>LINE_START</code>, <code>LINE_END</code>
     * @return the component at the given location, or <code>null</code> if the location is empty
     *
     * @throws IllegalArgumentException if the constraint object is not one of the nine specified constants
     * @see #addLayoutComponent(java.awt.Component, java.lang.Object)
     * @since 1.5
     */
    public Component getLayoutComponent(Object constraints) {
        if (CENTER.equals(constraints)) {
            return center;
        }
        else if (NORTH.equals(constraints)) {
            return north;
        }
        else if (SOUTH.equals(constraints)) {
            return south;
        }
        else if (WEST.equals(constraints)) {
            return west;
        }
        else if (EAST.equals(constraints)) {
            return east;
        }
        else if (PAGE_START.equals(constraints)) {
            return firstLine;
        }
        else if (PAGE_END.equals(constraints)) {
            return lastLine;
        }
        else if (LINE_START.equals(constraints)) {
            return firstItem;
        }
        else if (LINE_END.equals(constraints)) {
            return lastItem;
        }
        else {
            throw new IllegalArgumentException("cannot get component: unknown constraint: " + constraints);
        }
    }


    /**
     * Returns the component that corresponds to the given constraint location based on the target
     * <code>Container</code>'s component orientation. Components added with the relative constraints
     * <code>PAGE_START</code>, <code>PAGE_END</code>, <code>LINE_START</code>, and <code>LINE_END</code> take
     * precedence over components added with the explicit constraints <code>NORTH</code>, <code>SOUTH</code>,
     * <code>WEST</code>, and <code>EAST</code>. The <code>Container</code>'s component orientation is used to determine
     * the location of components added with <code>LINE_START</code> and <code>LINE_END</code>.
     *
     * @param constraints the desired absolute position, one of <code>CENTER</code>, <code>NORTH</code>,
     *                    <code>SOUTH</code>, <code>EAST</code>, <code>WEST</code>
     * @param target      the {@code Container} used to obtain the constraint location based on the target {@code
     *                    Container}'s component orientation.
     * @return the component at the given location, or <code>null</code> if the location is empty
     *
     * @throws IllegalArgumentException if the constraint object is not one of the five specified constants
     * @throws NullPointerException     if the target parameter is null
     * @see #addLayoutComponent(java.awt.Component, java.lang.Object)
     * @since 1.5
     */
    public Component getLayoutComponent(Container target, Object constraints) {
        boolean ltr = target.getComponentOrientation().isLeftToRight();
        Component result = null;

        if (NORTH.equals(constraints)) {
            result = (firstLine != null) ? firstLine : north;
        }
        else if (SOUTH.equals(constraints)) {
            result = (lastLine != null) ? lastLine : south;
        }
        else if (WEST.equals(constraints)) {
            result = ltr ? firstItem : lastItem;
            if (result == null) {
                result = west;
            }
        }
        else if (EAST.equals(constraints)) {
            result = ltr ? lastItem : firstItem;
            if (result == null) {
                result = east;
            }
        }
        else if (CENTER.equals(constraints)) {
            result = center;
        }
        else {
            throw new IllegalArgumentException("cannot get component: invalid constraint: " + constraints);
        }

        return result;
    }


    /**
     * Gets the constraints for the specified component
     *
     * @param comp the component to be queried
     * @return the constraint for the specified component, or null if component is null or is not present in this
     *         layout
     *
     * @see #addLayoutComponent(java.awt.Component, java.lang.Object)
     * @since 1.5
     */
    public Object getConstraints(Component comp) {
        //fix for 6242148 : API method java.awt.BorderLayout.getConstraints(null) should return null
        if (comp == null) {
            return null;
        }
        if (comp == center) {
            return CENTER;
        }
        else if (comp == north) {
            return NORTH;
        }
        else if (comp == south) {
            return SOUTH;
        }
        else if (comp == west) {
            return WEST;
        }
        else if (comp == east) {
            return EAST;
        }
        else if (comp == firstLine) {
            return PAGE_START;
        }
        else if (comp == lastLine) {
            return PAGE_END;
        }
        else if (comp == firstItem) {
            return LINE_START;
        }
        else if (comp == lastItem) {
            return LINE_END;
        }
        return null;
    }

    /**
     * Determines the minimum size of the <code>target</code> container using this layout manager.
     * <p/>
     * This method is called when a container calls its <code>getMinimumSize</code> method. Most applications do not
     * call this method directly.
     *
     * @param target the container in which to do the layout.
     * @return the minimum dimensions needed to lay out the subcomponents of the specified container.
     *
     * @see java.awt.Container
     * @see java.awt.BorderLayout#preferredLayoutSize
     * @see java.awt.Container#getMinimumSize()
     */
    public Dimension minimumLayoutSize(Container target) {
        synchronized (target.getTreeLock()) {
            Dimension dim = new Dimension(0, 0);

            boolean ltr = target.getComponentOrientation().isLeftToRight();
            Component c = null;

            if ((c = getChild(CENTER, ltr)) != null) {
                Dimension d = c.getMinimumSize();
                dim.width += d.width;
                dim.height = Math.max(d.height, dim.height);
            }
            if ((c = getChild(NORTH, ltr)) != null) {
                Dimension d = c.getMinimumSize();
                dim.width = Math.max(d.width, dim.width);
                dim.height += d.height + vgap;
            }
            if ((c = getChild(SOUTH, ltr)) != null) {
                Dimension d = c.getMinimumSize();
                dim.width = Math.max(d.width, dim.width);
                dim.height += d.height + vgap;
            }
            if ((c = getChild(EAST, ltr)) != null) {
                Dimension d = c.getMinimumSize();
                dim.width += d.width + hgap;
                dim.height = Math.max(d.height, dim.height);
            }
            if ((c = getChild(WEST, ltr)) != null) {
                Dimension d = c.getMinimumSize();
                dim.width += d.width + hgap;
                dim.height = Math.max(d.height, dim.height);
            }

            Insets insets = target.getInsets();
            dim.width += insets.left + insets.right;
            dim.height += insets.top + insets.bottom;

            return dim;
        }
    }

    /**
     * Determines the preferred size of the <code>target</code> container using this layout manager, based on the
     * components in the container.
     * <p/>
     * Most applications do not call this method directly. This method is called when a container calls its
     * <code>getPreferredSize</code> method.
     *
     * @param target the container in which to do the layout.
     * @return the preferred dimensions to lay out the subcomponents of the specified container.
     *
     * @see java.awt.Container
     * @see java.awt.BorderLayout#minimumLayoutSize
     * @see java.awt.Container#getPreferredSize()
     */
    public Dimension preferredLayoutSize(Container target) {
        synchronized (target.getTreeLock()) {
            Dimension dim = new Dimension(0, 0);

            boolean ltr = target.getComponentOrientation().isLeftToRight();
            Component c = null;

            if ((c = getChild(CENTER, ltr)) != null) {
                Dimension d = c.getPreferredSize();
                dim.width += d.width;
                dim.height = Math.max(d.height, dim.height);
            }
            if ((c = getChild(NORTH, ltr)) != null) {
                Dimension d = c.getPreferredSize();
                dim.width = Math.max(d.width, dim.width);
                dim.height += d.height + vgap;
            }
            if ((c = getChild(SOUTH, ltr)) != null) {
                Dimension d = c.getPreferredSize();
                dim.width = Math.max(d.width, dim.width);
                dim.height += d.height + vgap;
            }
            if ((c = getChild(EAST, ltr)) != null) {
                Dimension d = c.getPreferredSize();
                dim.width += d.width + hgap;
                dim.height = Math.max(d.height, dim.height);
            }
            if ((c = getChild(WEST, ltr)) != null) {
                Dimension d = c.getPreferredSize();
                dim.width += d.width + hgap;
                dim.height = Math.max(d.height, dim.height);
            }

            Insets insets = target.getInsets();
            dim.width += insets.left + insets.right;
            dim.height += insets.top + insets.bottom;

            return dim;
        }
    }

    /**
     * Returns the maximum dimensions for this layout given the components in the specified target container.
     *
     * @param target the component which needs to be laid out
     * @see Container
     * @see #minimumLayoutSize
     * @see #preferredLayoutSize
     */
    public Dimension maximumLayoutSize(Container target) {
        return new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE);
    }

    /**
     * Returns the alignment along the x axis.  This specifies how the component would like to be aligned relative to
     * other components.  The value should be a number between 0 and 1 where 0 represents alignment along the origin, 1
     * is aligned the furthest away from the origin, 0.5 is centered, etc.
     */
    public float getLayoutAlignmentX(Container parent) {
        return 0.5f;
    }

    /**
     * Returns the alignment along the y axis.  This specifies how the component would like to be aligned relative to
     * other components.  The value should be a number between 0 and 1 where 0 represents alignment along the origin, 1
     * is aligned the furthest away from the origin, 0.5 is centered, etc.
     */
    public float getLayoutAlignmentY(Container parent) {
        return 0.5f;
    }

    /**
     * Invalidates the layout, indicating that if the layout manager has cached information it should be discarded.
     */
    public void invalidateLayout(Container target) {
    }

    /**
     * Lays out the container argument using this border layout.
     * <p/>
     * This method actually reshapes the components in the specified container in order to satisfy the constraints of
     * this <code>BorderLayout</code> object. The <code>NORTH</code> and <code>SOUTH</code> components, if any, are
     * placed at the top and bottom of the container, respectively. The <code>WEST</code> and <code>EAST</code>
     * components are then placed on the left and right, respectively. Finally, the <code>CENTER</code> object is placed
     * in any remaining space in the middle.
     * <p/>
     * Most applications do not call this method directly. This method is called when a container calls its
     * <code>doLayout</code> method.
     *
     * @param target the container in which to do the layout.
     * @see java.awt.Container
     * @see java.awt.Container#doLayout()
     */
    public void layoutContainer(Container target) {
        synchronized (target.getTreeLock()) {
            Insets insets = target.getInsets();
            int top = insets.top;
            int bottom = target.getHeight() - insets.bottom;
            int left = insets.left;
            int right = target.getWidth() - insets.right;

            boolean ltr = target.getComponentOrientation().isLeftToRight();

            Component north = getChild(NORTH, ltr);
            Component south = getChild(SOUTH, ltr);
            Component east = getChild(EAST, ltr);
            Component west = getChild(WEST, ltr);
            Component center = getChild(CENTER, ltr);

            /*
             * Include the hgap here since if the east or west components
             * do not exist then there should be no gap applied
             */
            int westGap = (west != null ? west.getPreferredSize().width + hgap : 0);
            int eastGap = (east != null ? east.getPreferredSize().width + hgap : 0);

            if (north != null) {
//                north.setSize(right - left - westGap - eastGap/* - 2 * hgap*/, north.getHeight());
                Dimension d = north.getPreferredSize();
                north.setBounds(left + westGap/* + hgap*/,
                        top,
                        right - left - westGap - eastGap/* - 2 * hgap*/,
                        d.height);
                top += d.height + vgap;
            }
            if (south != null) {
//                south.setSize(right - left - westGap - eastGap/* - 2 * hgap*/, south.getHeight());
                Dimension d = south.getPreferredSize();
                south.setBounds(left + westGap/* + hgap*/,
                        bottom - d.height,
                        right - left - westGap - eastGap/* - 2 * hgap*/,
                        d.height);
                bottom -= d.height + vgap;
            }
            if (east != null) {
                east.setSize(east.getWidth(), bottom - top);
                Dimension d = east.getPreferredSize();
                east.setBounds(right - d.width, top, d.width, bottom - top);
                right -= d.width + hgap;
            }
            if (west != null) {
                west.setSize(west.getWidth(), bottom - top);
                Dimension d = west.getPreferredSize();
                west.setBounds(left, top, d.width, bottom - top);
                left += d.width + hgap;
            }
            if (center != null) {
                center.setBounds(left, top, right - left, bottom - top);
            }
        }
    }

    /**
     * Get the component that corresponds to the given constraint location
     *
     * @param key The desired absolute position, either NORTH, SOUTH, EAST, or WEST.
     * @param ltr Is the component line direction left-to-right?
     */
    private Component getChild(String key, boolean ltr) {
        Component result = null;

        if (key == NORTH) {
            result = (firstLine != null) ? firstLine : north;
        }
        else if (key == SOUTH) {
            result = (lastLine != null) ? lastLine : south;
        }
        else if (key == WEST) {
            result = ltr ? firstItem : lastItem;
            if (result == null) {
                result = west;
            }
        }
        else if (key == EAST) {
            result = ltr ? lastItem : firstItem;
            if (result == null) {
                result = east;
            }
        }
        else if (key == CENTER) {
            result = center;
        }
        if (result != null && !result.isVisible()) {
            result = null;
        }
        return result;
    }

    /**
     * Returns a string representation of the state of this border layout.
     *
     * @return a string representation of this border layout.
     */
    public String toString() {
        return getClass().getName() + "[hgap=" + hgap + ",vgap=" + vgap + "]";
    }
}
