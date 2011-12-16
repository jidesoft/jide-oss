/*
 * @(#)JideScrollPane.java
 *
 * Copyright 2002 - 2005 JIDE Software Inc. All rights reserved.
 */
package com.jidesoft.swing;

import javax.swing.*;
import javax.swing.plaf.UIResource;
import java.awt.*;

/**
 * <code>JideScrollPane</code> is an enhanced version of <code>JScrollPane</code>. In <code>JScrollPane</code>, you can
 * have rowHeader and columnHeader. However you can't have rowFooter and columnFooter. However rowFooter and
 * columnFooter are very useful in table. For example they can be used to display "total" or "summary" type of
 * information.
 * <p/>
 * Several methods related to rowFooter and columnFooter are added such as {@link #setRowFooter(javax.swing.JViewport)},
 * and {@link #setColumnFooter(javax.swing.JViewport)} which will set the viewport to rowFooter and columnFooter area
 * respectively. The usage of those methods are exactly the same as {@link JScrollPane#setRowHeader(javax.swing.JViewport)}.
 * <p/>
 * To fully leverage the power of JideScrollPane, we also create a class called <code>TableScrollPane</code> which is
 * part of JIDE Grids package. It will allow you to easily create table with row header, row footer and column footer.
 * <p><code>JideScrollPane</code> also provides support for scrollbar corners. You can set them using {@link
 * #setScrollBarCorner(String, java.awt.Component)}. Available key for scroll bar corner is defined at {@link
 * JideScrollPaneConstants}  which can be access from <code>JideScrollPane</code>.
 * <p/>
 * <b>Credit:</b> This implementation of scroll bar corner is based on work from Santhosh Kumar -
 * santhosh@in.fiorano.com.
 */
public class JideScrollPane extends JScrollPane implements JideScrollPaneConstants {

    /**
     * The row footer child.  Default is <code>null</code>.
     *
     * @see #setRowFooter(javax.swing.JViewport)
     */
    protected JViewport _rowFooter;


    /**
     * The component under column header.  Default is <code>null</code>.
     *
     * @see #setSubColumnHeader(javax.swing.JViewport)
     */
    protected JViewport _subColumnHeader;
    /**
     * The component under upper left corner.  Default is <code>null</code>.
     *
     * @see #setCorner(String, java.awt.Component)
     */
    protected Component _subUpperLeft;
    /**
     * The component under upper right corner.  Default is <code>null</code>.
     *
     * @see #setCorner(String, java.awt.Component)
     */
    protected Component _subUpperRight;
    /**
     * The column footer child.  Default is <code>null</code>.
     *
     * @see #setColumnFooter(javax.swing.JViewport)
     */
    protected JViewport _columnFooter;

    /**
     * The component to the left of horizontal scroll bar.
     */
    protected Component _hLeft;
    /**
     * The component to the right of horizontal scroll bar.
     */
    protected Component _hRight;

    /**
     * The component to the top of vertical scroll bar.
     */
    protected Component _vTop;

    /**
     * The component to the bottom of vertical scroll bar.
     */
    protected Component _vBottom;

    private boolean _keepCornerVisible = false;

    private boolean _horizontalScrollBarCoversWholeWidth;
    private boolean _verticalScrollBarCoversWholeHeight;
    private boolean _flatLayout = false;

    public static final String PROPERTY_HORIZONTAL_SCROLL_BAR_COVERS_WHOLE_WIDTH = "horizontalScrollBarCoversWholeWidth";
    public static final String PROPERTY_VERTICAL_SCROLL_BAR_COVERS_WHOLE_HEIGHT = "verticalScrollBarCoversWholeHeight";
    public static final String PROPERTY_KEEP_CORNER_VISIBLE = "keepCornerVisible";
    public static final String PROPERTY_FLAT_LAYOUT = "flatLayout";

    private boolean _columnHeadersHeightUnified;
    private boolean _columnFootersHeightUnified;
    public static final String PROPERTY_COLUMN_HEADERS_HEIGHT_UNIFIED = "columnHeadersHeightUnified";
    public static final String PROPERTY_COLUMN_FOOTERS_HEIGHT_UNIFIED = "columnFootersHeightUnified";

    public static final String CLIENT_PROPERTY_SLAVE_VIEWPORT = "synchronizeViewSlaveViewport";
    public static final String CLIENT_PROPERTY_MASTER_VIEWPORT = "synchronizeViewMasterViewport";

    /**
     * Creates a <code>JideScrollPane</code> that displays the view component in a viewport whose view position can be
     * controlled with a pair of scrollbars. The scrollbar policies specify when the scrollbars are displayed, For
     * example, if <code>vsbPolicy</code> is <code>VERTICAL_SCROLLBAR_AS_NEEDED</code> then the vertical scrollbar only
     * appears if the view doesn't fit vertically. The available policy settings are listed at {@link
     * #setVerticalScrollBarPolicy(int)} and {@link #setHorizontalScrollBarPolicy(int)}.
     *
     * @param view      the component to display in the scrollpanes viewport
     * @param vsbPolicy an integer that specifies the vertical scrollbar policy
     * @param hsbPolicy an integer that specifies the horizontal scrollbar policy
     * @see #setViewportView(java.awt.Component)
     */
    public JideScrollPane(Component view, int vsbPolicy, int hsbPolicy) {
        setLayout(new JideScrollPaneLayout.UIResource());
        setVerticalScrollBarPolicy(vsbPolicy);
        setHorizontalScrollBarPolicy(hsbPolicy);
        setViewport(createViewport());
        setVerticalScrollBar(createVerticalScrollBar());
        setHorizontalScrollBar(createHorizontalScrollBar());
        if (null != view) {
            setViewportView(view);
        }
        setOpaque(true);
        updateUI();

        if (!getComponentOrientation().isLeftToRight()) {
            viewport.setViewPosition(new Point(Integer.MAX_VALUE, 0));
        }
    }


    /**
     * Creates a <code>JideScrollPane</code> that displays the contents of the specified component, where both
     * horizontal and vertical scrollbars appear whenever the component's contents are larger than the view.
     *
     * @param view the component to display in the scrollpane's viewport
     * @see #setViewportView(java.awt.Component)
     */
    public JideScrollPane(Component view) {
        this(view, VERTICAL_SCROLLBAR_AS_NEEDED, HORIZONTAL_SCROLLBAR_AS_NEEDED);
    }


    /**
     * Creates an empty (no viewport view) <code>JideScrollPane</code> with specified scrollbar policies. The available
     * policy settings are listed at {@link #setVerticalScrollBarPolicy(int)} and {@link
     * #setHorizontalScrollBarPolicy(int)}.
     *
     * @param vsbPolicy an integer that specifies the vertical scrollbar policy
     * @param hsbPolicy an integer that specifies the horizontal scrollbar policy
     * @see #setViewportView(java.awt.Component)
     */
    public JideScrollPane(int vsbPolicy, int hsbPolicy) {
        this(null, vsbPolicy, hsbPolicy);
    }


    /**
     * Creates an empty (no viewport view) <code>JideScrollPane</code> where both horizontal and vertical scrollbars
     * appear when needed.
     */
    public JideScrollPane() {
        this(null, VERTICAL_SCROLLBAR_AS_NEEDED, HORIZONTAL_SCROLLBAR_AS_NEEDED);
    }

    @Override
    public void setViewport(JViewport viewport) {
        JViewport old = getViewport();
        super.setViewport(viewport);
        if (old != null) {
            if (rowHeader != null) {
                JideSwingUtilities.unsynchronizeView(rowHeader, old);
            }
            if (_rowFooter != null) {
                JideSwingUtilities.unsynchronizeView(_rowFooter, old);
                JideSwingUtilities.unsynchronizeView(old, _rowFooter);
            }
            if (_columnFooter != null) {
                JideSwingUtilities.unsynchronizeView(_columnFooter, old);
                JideSwingUtilities.unsynchronizeView(old, _columnFooter);
            }
            if (columnHeader != null) {
                JideSwingUtilities.unsynchronizeView(columnHeader, old);
            }
            if (_subColumnHeader != null) {
                JideSwingUtilities.unsynchronizeView(_subColumnHeader, old);
                JideSwingUtilities.unsynchronizeView(old, _subColumnHeader);
            }
        }
        if (viewport != null) {
            if (rowHeader != null) {
                JideSwingUtilities.synchronizeView(rowHeader, getViewport(), SwingConstants.VERTICAL);
            }
            if (_rowFooter != null) {
                JideSwingUtilities.synchronizeView(_rowFooter, getViewport(), SwingConstants.VERTICAL);
                JideSwingUtilities.synchronizeView(getViewport(), _rowFooter, SwingConstants.VERTICAL);
            }
            if (_columnFooter != null) {
                JideSwingUtilities.synchronizeView(_columnFooter, getViewport(), SwingConstants.HORIZONTAL);
                JideSwingUtilities.synchronizeView(getViewport(), _columnFooter, SwingConstants.HORIZONTAL);
            }
            if (columnHeader != null) {
                JideSwingUtilities.synchronizeView(columnHeader, getViewport(), SwingConstants.HORIZONTAL);
            }
            if (_subColumnHeader != null) {
                JideSwingUtilities.synchronizeView(_subColumnHeader, getViewport(), SwingConstants.HORIZONTAL);
                JideSwingUtilities.synchronizeView(getViewport(), _subColumnHeader, SwingConstants.HORIZONTAL);
            }
        }
    }

    /**
     * Returns the row footer.
     *
     * @return the <code>rowFooter</code> property
     *
     * @see #setRowFooter(javax.swing.JViewport)
     */
    public JViewport getRowFooter() {
        return _rowFooter;
    }


    /**
     * Removes the old rowFooter, if it exists.  If the new rowFooter isn't <code>null</code>, syncs the y coordinate of
     * its viewPosition with the viewport (if there is one) and then adds it to the scrollpane.
     *
     * @param rowFooter the new row footer to be used; if <code>null</code> the old row footer is still removed and the
     *                  new rowFooter is set to <code>null</code>
     * @see #getRowFooter
     * @see #setRowFooterView(java.awt.Component)
     */
    public void setRowFooter(JViewport rowFooter) {
        JViewport old = getRowFooter();
        _rowFooter = rowFooter;
        if (null != rowFooter) {
            add(rowFooter, ROW_FOOTER);
        }
        else if (null != old) {
            remove(old);
        }
        firePropertyChange("rowFooter", old, rowFooter);
        revalidate();
        repaint();
        if (old != null) {
            JideSwingUtilities.unsynchronizeView(old, getViewport());
            JideSwingUtilities.unsynchronizeView(getViewport(), old);
        }
        if (rowFooter != null) {
            JideSwingUtilities.synchronizeView(rowFooter, getViewport(), SwingConstants.VERTICAL);
            JideSwingUtilities.synchronizeView(getViewport(), rowFooter, SwingConstants.VERTICAL);
        }
    }

    /**
     * Override setRowHeader method in JScrollPane and synchronize the view with the main viewport. Swing tried to
     * implement this feature but it will break if the view position changes starts from rowHeader.
     *
     * @param rowHeader the new row header
     */
    @Override
    public void setRowHeader(JViewport rowHeader) {
        JViewport old = getRowHeader();
        super.setRowHeader(rowHeader);
        if (old != null) {
            JideSwingUtilities.unsynchronizeView(old, getViewport());
            JideSwingUtilities.unsynchronizeView(getViewport(), old);
        }
        if (getRowHeader() != null) {
            JideSwingUtilities.synchronizeView(getRowHeader(), getViewport(), SwingConstants.VERTICAL);
            JideSwingUtilities.synchronizeView(getViewport(), getRowHeader(), SwingConstants.VERTICAL);
        }
    }

    /**
     * Creates a row-footer viewport if necessary, sets its view and then adds the row-footer viewport to the
     * scrollpane.  For example:
     * <pre>
     * JScrollPane scrollpane = new JideScrollPane();
     * scrollpane.setViewportView(myBigComponentToScroll);
     * scrollpane.setRowFooterView(myBigComponentsRowFooter);
     * </pre>
     *
     * @param view the component to display as the row footer
     * @see #setRowFooter(javax.swing.JViewport)
     * @see JViewport#setView(java.awt.Component)
     */
    public void setRowFooterView(Component view) {
        if (null == getRowFooter()) {
            setRowFooter(createViewport());
        }
        getRowFooter().setView(view);
    }


    /**
     * Returns the column footer.
     *
     * @return the <code>columnFooter</code> property
     *
     * @see #setColumnFooter(javax.swing.JViewport)
     */
    public JViewport getColumnFooter() {
        return _columnFooter;
    }


    /**
     * Removes the old columnFooter, if it exists.  If the new columnFooter isn't <code>null</code>, sync the x
     * coordinate of the its viewPosition with the viewport (if there is one) and then add it to the scrollpane.
     *
     * @param columnFooter the new column footer to be used; if <code>null</code> the old column footer is still removed
     *                     and the new columnFooter is set to <code>null</code>
     * @see #getColumnFooter
     * @see #setColumnFooterView(java.awt.Component)
     */
    public void setColumnFooter(JViewport columnFooter) {
        JViewport old = getColumnFooter();
        _columnFooter = columnFooter;
        if (null != columnFooter) {
            add(columnFooter, COLUMN_FOOTER);
        }
        else if (null != old) {
            remove(old);
        }
        firePropertyChange("columnFooter", old, columnFooter);

        revalidate();
        repaint();

        if (old != null) {
            JideSwingUtilities.unsynchronizeView(old, getViewport());
            JideSwingUtilities.unsynchronizeView(getViewport(), old);
        }
        if (_columnFooter != null) {
            JideSwingUtilities.synchronizeView(_columnFooter, getViewport(), SwingConstants.HORIZONTAL);
            JideSwingUtilities.synchronizeView(getViewport(), _columnFooter, SwingConstants.HORIZONTAL);
        }
    }

    /**
     * Overrides to make column header viewport synchronizing with the main viewport.
     *
     * @param columnHeader the column header
     */
    @Override
    public void setColumnHeader(JViewport columnHeader) {
        JViewport old = getColumnHeader();
        super.setColumnHeader(columnHeader);
        if (old != null) {
            JideSwingUtilities.unsynchronizeView(old, getViewport());
        }
        if (getColumnHeader() != null) {
            JideSwingUtilities.synchronizeView(getColumnHeader(), getViewport(), SwingConstants.HORIZONTAL);
        }
    }

    /**
     * Returns the sub column header.
     *
     * @return the <code>rowSubColumnHeader</code> property
     *
     * @see #setSubColumnHeader(javax.swing.JViewport)
     */
    public JViewport getSubColumnHeader() {
        return _subColumnHeader;
    }

    /**
     * Removes the old sub column header, if it exists.  If the new sub column header isn't <code>null</code>, sync the
     * x coordinate of the its viewPosition with the viewport (if there is one) and then add it to the scroll pane.
     *
     * @param subColumnHeader the new sub column header to be used; if <code>null</code> the old sub column header is
     *                        still removed and the new sub column header is set to <code>null</code>
     * @see #getSubColumnHeader()
     */
    public void setSubColumnHeader(JViewport subColumnHeader) {
        JViewport old = getSubColumnHeader();
        _subColumnHeader = subColumnHeader;
        if (null != subColumnHeader) {
            add(subColumnHeader, SUB_COLUMN_HEADER);
        }
        else if (null != old) {
            remove(old);
        }
        firePropertyChange("subColumnHeader", old, subColumnHeader);

        revalidate();
        repaint();

        if (old != null) {
            JideSwingUtilities.unsynchronizeView(old, getViewport());
            JideSwingUtilities.unsynchronizeView(getViewport(), old);
        }
        if (_subColumnHeader != null) {
            JideSwingUtilities.synchronizeView(_subColumnHeader, getViewport(), SwingConstants.HORIZONTAL);
            JideSwingUtilities.synchronizeView(getViewport(), _subColumnHeader, SwingConstants.HORIZONTAL);
        }
    }

    /**
     * Creates a column-footer viewport if necessary, sets its view, and then adds the column-footer viewport to the
     * scrollpane.  For example:
     * <pre>
     * JScrollPane scrollpane = new JideScrollPane();
     * scrollpane.setViewportView(myBigComponentToScroll);
     * scrollpane.setColumnFooterView(myBigComponentsColumnFooter);
     * </pre>
     *
     * @param view the component to display as the column footer
     * @see #setColumnFooter(javax.swing.JViewport)
     * @see JViewport#setView(java.awt.Component)
     */
    public void setColumnFooterView(Component view) {
        if (null == getColumnFooter()) {
            setColumnFooter(createViewport());
        }
        getColumnFooter().setView(view);
    }

    /**
     * Creates a sub-column-header viewport if necessary, sets its view, and then adds the sub-column-header viewport to
     * the scrollpane.
     *
     * @param view the component to display as the sub column header
     * @see #setSubColumnHeader(javax.swing.JViewport)
     * @see JViewport#setView(java.awt.Component)
     */
    public void setSubColumnHeaderView(Component view) {
        if (null == getSubColumnHeader()) {
            setSubColumnHeader(createViewport());
        }
        getSubColumnHeader().setView(view);
    }

    @Override
    public Component getCorner(String key) {
        if (key == null) {
            return null;
        }
        if (key.equals(SUB_UPPER_LEFT)) {
            return _subUpperLeft;
        }
        else if (key.equals(SUB_UPPER_RIGHT)) {
            return _subUpperRight;
        }
        return super.getCorner(key);
    }

    @Override
    public void setCorner(String key, Component corner) {
        if (key == null) {
            return;
        }
        if (key.equals(SUB_UPPER_LEFT) || key.equals(SUB_UPPER_RIGHT)) {
            Component old;
            if (key.equals(SUB_UPPER_LEFT)) {
                old = _subUpperLeft;
                _subUpperLeft = corner;
            }
            else {
                old = _subUpperRight;
                _subUpperRight = corner;
            }
            if (old != null) {
                remove(old);
            }
            if (corner != null) {
                add(corner, key);
            }
            firePropertyChange(key, old, corner);
            revalidate();
            repaint();
            return;
        }
        super.setCorner(key, corner);
    }

    /**
     * Returns the component at the specified scroll bar corner. The <code>key</code> value specifying the corner is one
     * of: <ul> <li>{@link JideScrollPane#HORIZONTAL_LEFT} <li>{@link JideScrollPane#HORIZONTAL_RIGHT} <li>{@link
     * JideScrollPane#VERTICAL_TOP} <li>{@link JideScrollPane#VERTICAL_BOTTOM} <li>{@link
     * JideScrollPane#HORIZONTAL_LEADING} <li>{@link JideScrollPane#HORIZONTAL_TRAILING} </ul>
     *
     * @param key one of the values as shown above
     * @return one of the components listed below or <code>null</code> if <code>key</code> is invalid: <ul>
     *         <li>lowerLeft <li>lowerRight <li>upperLeft <li>upperRight </ul>
     *
     * @see #setCorner(String, java.awt.Component)
     */
    public Component getScrollBarCorner(String key) {
        boolean isLeftToRight = getComponentOrientation().isLeftToRight();
        if (key.equals(HORIZONTAL_LEADING)) {
            key = isLeftToRight ? HORIZONTAL_LEFT : HORIZONTAL_RIGHT;
        }
        else if (key.equals(HORIZONTAL_TRAILING)) {
            key = isLeftToRight ? HORIZONTAL_RIGHT : HORIZONTAL_LEFT;
        }

        if (key.equals(HORIZONTAL_LEFT)) {
            return _hLeft;
        }
        else if (key.equals(HORIZONTAL_RIGHT)) {
            return _hRight;
        }
        else if (key.equals(VERTICAL_BOTTOM)) {
            return _vBottom;
        }
        else if (key.equals(VERTICAL_TOP)) {
            return _vTop;
        }
        else {
            return null;
        }
    }


    /**
     * Adds a child that will appear in one of the scroll bars corners. Scroll bar will make room to show the corner
     * component. Legal values for the <b>key</b> are: <ul> <li>{@link JideScrollPane#HORIZONTAL_LEFT} <li>{@link
     * JideScrollPane#HORIZONTAL_RIGHT} <li>{@link JideScrollPane#VERTICAL_TOP} <li>{@link
     * JideScrollPane#VERTICAL_BOTTOM} <li>{@link JideScrollPane#HORIZONTAL_LEADING} <li>{@link
     * JideScrollPane#HORIZONTAL_TRAILING} </ul>
     * <p/>
     * Although "corner" doesn't match any beans property signature, <code>PropertyChange</code> events are generated
     * with the property name set to the corner key.
     *
     * @param key    identifies which corner the component will appear in
     * @param corner one of the following components: <ul> <li>lowerLeft <li>lowerRight <li>upperLeft <li>upperRight
     *               </ul>
     * @throws IllegalArgumentException if corner key is invalid
     */
    public void setScrollBarCorner(String key, Component corner) {
        Component old;
        boolean isLeftToRight = getComponentOrientation().isLeftToRight();
        if (key.equals(HORIZONTAL_LEADING)) {
            key = isLeftToRight ? HORIZONTAL_LEFT : HORIZONTAL_RIGHT;
        }
        else if (key.equals(HORIZONTAL_TRAILING)) {
            key = isLeftToRight ? HORIZONTAL_RIGHT : HORIZONTAL_LEFT;
        }

        if (key.equals(HORIZONTAL_LEFT)) {
            old = _hLeft;
            _hLeft = corner;
        }
        else if (key.equals(HORIZONTAL_RIGHT)) {
            old = _hRight;
            _hRight = corner;
        }
        else if (key.equals(VERTICAL_TOP)) {
            old = _vTop;
            _vTop = corner;
        }
        else if (key.equals(VERTICAL_BOTTOM)) {
            old = _vBottom;
            _vBottom = corner;
        }
        else {
            throw new IllegalArgumentException("invalid scroll bar corner key");
        }

        if (null != old) {
            remove(old);
        }
        if (null != corner) {
            add(corner, key);
        }
        if (corner != null) corner.setComponentOrientation(getComponentOrientation());
        firePropertyChange(key, old, corner);
        revalidate();
        repaint();
    }

    @Override
    public void updateUI() {
        super.updateUI();
        setLayout(new JideScrollPaneLayout.UIResource());
        if (getBorder() instanceof UIResource) {
            LookAndFeel.installBorder(this, "JideScrollPane.border");
        }
    }

    @Override
    public void setLayout(LayoutManager layout) {
        if (!(layout instanceof JideScrollPaneLayout)) {
            super.setLayout(new JideScrollPaneLayout.UIResource());
        }
        else {
            super.setLayout(layout);
        }
    }

    public boolean isVerticalScrollBarCoversWholeHeight() {
        return _verticalScrollBarCoversWholeHeight;
    }

    public void setHorizontalScrollBarCoversWholeWidth(boolean horizontalScrollBarCoversWholeWidth) {
        boolean old = _horizontalScrollBarCoversWholeWidth;
        if (old != horizontalScrollBarCoversWholeWidth) {
            _horizontalScrollBarCoversWholeWidth = horizontalScrollBarCoversWholeWidth;
            firePropertyChange(PROPERTY_HORIZONTAL_SCROLL_BAR_COVERS_WHOLE_WIDTH, old, _horizontalScrollBarCoversWholeWidth);
            invalidate();
            doLayout();
            if (getHorizontalScrollBar() != null) {
                getHorizontalScrollBar().doLayout();
            }
        }
    }

    public boolean isHorizontalScrollBarCoversWholeWidth() {
        return _horizontalScrollBarCoversWholeWidth;
    }

    public void setVerticalScrollBarCoversWholeHeight(boolean verticalScrollBarCoversWholeHeight) {
        boolean old = _verticalScrollBarCoversWholeHeight;
        if (old != verticalScrollBarCoversWholeHeight) {
            _verticalScrollBarCoversWholeHeight = verticalScrollBarCoversWholeHeight;
            firePropertyChange(PROPERTY_VERTICAL_SCROLL_BAR_COVERS_WHOLE_HEIGHT, old, _verticalScrollBarCoversWholeHeight);
            invalidate();
            doLayout();
            if (getVerticalScrollBar() != null) {
                getVerticalScrollBar().doLayout();
            }
        }
    }

    /**
     * If true, the top-right, top-left corners the column header will have the same height. If false, three of them
     * will keep their own preferred height.
     *
     * @return true or false.
     */
    public boolean isColumnHeadersHeightUnified() {
        return _columnHeadersHeightUnified;
    }

    /**
     * Sets the flag if the top-right, top-left corner and the column header will have the same height or different
     * heights.
     *
     * @param columnHeadersHeightUnified true or false.
     */
    public void setColumnHeadersHeightUnified(boolean columnHeadersHeightUnified) {
        boolean old = _columnHeadersHeightUnified;
        if (old != columnHeadersHeightUnified) {
            _columnHeadersHeightUnified = columnHeadersHeightUnified;
            firePropertyChange(PROPERTY_COLUMN_HEADERS_HEIGHT_UNIFIED, old, _horizontalScrollBarCoversWholeWidth);
            invalidate();
            doLayout();
        }
    }

    /**
     * If true, the bottom-right, bottom-left corners the column footer will have the same height. If false, three of
     * them will keep their own preferred height.
     *
     * @return true or false.
     */
    public boolean isColumnFootersHeightUnified() {
        return _columnFootersHeightUnified;
    }

    /**
     * Sets the flag if the bottom-right, bottom-left corner and the column footer will have the same height or
     * different heights.
     *
     * @param columnFootersHeightUnified true or false.
     */
    public void setColumnFootersHeightUnified(boolean columnFootersHeightUnified) {
        boolean old = _columnFootersHeightUnified;
        if (old != columnFootersHeightUnified) {
            _columnFootersHeightUnified = columnFootersHeightUnified;
            firePropertyChange(PROPERTY_COLUMN_FOOTERS_HEIGHT_UNIFIED, old, _horizontalScrollBarCoversWholeWidth);
            invalidate();
            doLayout();
        }
    }

    /**
     * Get the flag indicating if JideScrollPane should keep the corner visible when it has corner components defined
     * even when the scroll bar is not visible.
     * <p/>
     * This flag will take effect only when the scroll bar policy is <code>HORIZONTAL_SCROLLBAR_AS_NEEDED</code> or
     * <code>VERTICAL_SCROLLBAR_AS_NEEDED</code>
     * <p/>
     * The default value of this flag is false.
     *
     * @return the flag.
     */
    public boolean isKeepCornerVisible() {
        return _keepCornerVisible;
    }

    /**
     * Set the flag indicating if JideScrollPane should keep the corner visible when it has corner components defined
     * even when the scroll bar is not visible.
     *
     * @param keepCornerVisible the flag
     */
    public void setKeepCornerVisible(boolean keepCornerVisible) {
        if (_keepCornerVisible != keepCornerVisible) {
            boolean old = _keepCornerVisible;
            _keepCornerVisible = keepCornerVisible;
            firePropertyChange(PROPERTY_KEEP_CORNER_VISIBLE, old, _keepCornerVisible);
            invalidate();
            doLayout();
        }
    }

    /**
     * Gets the flag indicating if the JideScrollPane will layout its view flat without scroll bars.
     *
     * @return true if flat layout. Otherwise false.
     * @see #setFlatLayout(boolean)
     * @since 3.3.3
     */
    public boolean isFlatLayout() {
        return _flatLayout;
    }

    /**
     * Sets the flag indicating if the JideScrollPane will layout its view flat without scroll bars.
     * <p/>
     * By default, the value is false to keep normal behavior.
     *
     * @param flatLayout the flag
     * @since 3.3.3
     */
    public void setFlatLayout(boolean flatLayout) {
        if (_flatLayout != flatLayout) {
            boolean old = _flatLayout;
            _flatLayout = flatLayout;
            firePropertyChange(PROPERTY_FLAT_LAYOUT, old, _flatLayout);
            invalidate();
            doLayout();
        }
    }
}
