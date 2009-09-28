/*
 * @(#)${NAME}.java
 *
 * Copyright 2002 - 2005 JIDE Software Inc. All rights reserved.
 */
package com.jidesoft.swing;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

/**
 * The layout manager used by <code>JideScrollPane</code>. <code>JideScrollPaneLayout</code> is responsible for eleven
 * components: a viewport, two scrollbars, a row header, a column header, a row footer, a column footer, and four
 * "corner" components.
 */
public class JideScrollPaneLayout extends ScrollPaneLayout implements JideScrollPaneConstants {
    /**
     * The row footer child.  Default is <code>null</code>.
     *
     * @see JideScrollPane#setRowFooter
     */
    protected JViewport _rowFoot;


    /**
     * The row sub column header componeng.  Default is <code>null</code>.
     *
     * @see JideScrollPane#setSubColumnHeader
     */
    protected JViewport _subColHead;
    /**
     * The column footer child.  Default is <code>null</code>.
     *
     * @see JideScrollPane#setColumnFooter
     */
    protected JViewport _colFoot;

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
    private static final long serialVersionUID = 7897026041296359186L;

    @Override
    public void syncWithScrollPane(JScrollPane sp) {
        super.syncWithScrollPane(sp);
        if (sp instanceof JideScrollPane) {
            _rowFoot = ((JideScrollPane) sp).getRowFooter();
            _colFoot = ((JideScrollPane) sp).getColumnFooter();
            _subColHead = ((JideScrollPane) sp).getSubColumnHeader();
            _hLeft = ((JideScrollPane) sp).getScrollBarCorner(HORIZONTAL_LEFT);
            _hRight = ((JideScrollPane) sp).getScrollBarCorner(HORIZONTAL_RIGHT);
            _vTop = ((JideScrollPane) sp).getScrollBarCorner(VERTICAL_TOP);
            _vBottom = ((JideScrollPane) sp).getScrollBarCorner(VERTICAL_BOTTOM);
        }
    }

    protected boolean isHsbCoversWholeWidth(JScrollPane sp) {
        return sp instanceof JideScrollPane && ((JideScrollPane) sp).isHorizontalScrollBarCoversWholeWidth();
    }

    protected boolean isVsbCoversWholeHeight(JScrollPane sp) {
        return sp instanceof JideScrollPane && ((JideScrollPane) sp).isVerticalScrollBarCoversWholeHeight();
    }

    protected boolean isColumnHeadersHeightUnified(JScrollPane sp) {
        return sp instanceof JideScrollPane && ((JideScrollPane) sp).isColumnHeadersHeightUnified();
    }

    protected boolean isColumnFootersHeightUnified(JScrollPane sp) {
        return sp instanceof JideScrollPane && ((JideScrollPane) sp).isColumnFootersHeightUnified();
    }

    @Override
    public void addLayoutComponent(String s, Component c) {
        if (s.equals(ROW_FOOTER)) {
            _rowFoot = (JViewport) addSingletonComponent(_rowFoot, c);
        }
        else if (s.equals(SUB_COLUMN_HEADER)) {
            _subColHead = (JViewport) addSingletonComponent(_subColHead, c);
        }
        else if (s.equals(COLUMN_FOOTER)) {
            _colFoot = (JViewport) addSingletonComponent(_colFoot, c);
        }
        else if (s.equals(HORIZONTAL_LEFT)) {
            _hLeft = addSingletonComponent(_hLeft, c);
        }
        else if (s.equals(HORIZONTAL_RIGHT)) {
            _hRight = addSingletonComponent(_hRight, c);
        }
        else if (s.equals(VERTICAL_TOP)) {
            _vTop = addSingletonComponent(_vTop, c);
        }
        else if (s.equals(VERTICAL_BOTTOM)) {
            _vBottom = addSingletonComponent(_vBottom, c);
        }
        else {
            super.addLayoutComponent(s, c);
        }
    }

    @Override
    public void removeLayoutComponent(Component c) {
        if (c == _rowFoot) {
            _rowFoot = null;
        }
        else if (c == _subColHead) {
            _subColHead = null;
        }
        else if (c == _colFoot) {
            _colFoot = null;
        }
        else if (c == _hLeft) {
            _hLeft = null;
        }
        else if (c == _hRight) {
            _hRight = null;
        }
        else if (c == _vTop) {
            _vTop = null;
        }
        else if (c == _vBottom) {
            _vBottom = null;
        }
        else {
            super.removeLayoutComponent(c);
        }
    }

    /**
     * Returns the <code>JViewport</code> object that is the row footer.
     *
     * @return the <code>JViewport</code> object that is the row footer
     *
     * @see JideScrollPane#getRowFooter
     */
    public JViewport getRowFooter() {
        return _rowFoot;
    }

    /**
     * Returns the <code>JViewport</code> object that is the row sub column header.
     *
     * @return the <code>JViewport</code> object that is the row sub column header.
     *
     * @see com.jidesoft.swing.JideScrollPane#getSubColumnHeader()
     */
    public JViewport getRowSubColumnHeader() {
        return _subColHead;
    }

    /**
     * Returns the <code>JViewport</code> object that is the column footer.
     *
     * @return the <code>JViewport</code> object that is the column footer
     *
     * @see JideScrollPane#getColumnFooter
     */
    public JViewport getColumnFooter() {
        return _colFoot;
    }

    /**
     * Returns the <code>Component</code> at the specified corner.
     *
     * @param key the <code>String</code> specifying the corner
     * @return the <code>Component</code> at the specified corner, as defined in {@link ScrollPaneConstants}; if
     *         <code>key</code> is not one of the four corners, <code>null</code> is returned
     *
     * @see JScrollPane#getCorner
     */
    public Component getScrollBarCorner(String key) {
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
            return super.getCorner(key);
        }
    }

    /**
     * The preferred size of a <code>ScrollPane</code> is the size of the insets, plus the preferred size of the
     * viewport, plus the preferred size of the visible headers, plus the preferred size of the scrollbars that will
     * appear given the current view and the current scrollbar displayPolicies. <p>Note that the rowHeader is calculated
     * as part of the preferred width and the colHeader is calculated as part of the preferred size.
     *
     * @param parent the <code>Container</code> that will be laid out
     * @return a <code>Dimension</code> object specifying the preferred size of the viewport and any scrollbars
     *
     * @see ViewportLayout
     * @see LayoutManager
     */
    @Override
    public Dimension preferredLayoutSize(Container parent) {
        /* Sync the (now obsolete) policy fields with the
         * JScrollPane.
         */
        JScrollPane scrollPane = (JScrollPane) parent;
        vsbPolicy = scrollPane.getVerticalScrollBarPolicy();
        hsbPolicy = scrollPane.getHorizontalScrollBarPolicy();

        Insets insets = parent.getInsets();
        int prefWidth = insets.left + insets.right;
        int prefHeight = insets.top + insets.bottom;

        /* Note that viewport.getViewSize() is equivalent to
         * viewport.getView().getPreferredSize() modulo a null
         * view or a view whose size was explicitly set.
         */

        Dimension extentSize = null;
        Dimension viewSize = null;
        Component view = null;

        if (viewport != null) {
            extentSize = viewport.getPreferredSize();
            viewSize = viewport.getViewSize();
            view = viewport.getView();
        }

        /* If there's a viewport add its preferredSize.
         */

        if (extentSize != null) {
            prefWidth += extentSize.width;
            prefHeight += extentSize.height;
        }

        /* If there's a JScrollPane.viewportBorder, add its insets.
         */

        Border viewportBorder = scrollPane.getViewportBorder();
        if (viewportBorder != null) {
            Insets vpbInsets = viewportBorder.getBorderInsets(parent);
            prefWidth += vpbInsets.left + vpbInsets.right;
            prefHeight += vpbInsets.top + vpbInsets.bottom;
        }

        /* If a header exists and it's visible, factor its
         * preferred size in.
         */

        int rowHeaderWidth = 0;
        if (rowHead != null && rowHead.isVisible()) {
            rowHeaderWidth = rowHead.getPreferredSize().width;
        }
        if (upperLeft != null && upperLeft.isVisible()) {
            rowHeaderWidth = Math.max(rowHeaderWidth, upperLeft.getPreferredSize().width);
        }
        if (lowerLeft != null && lowerLeft.isVisible()) {
            rowHeaderWidth = Math.max(rowHeaderWidth, lowerLeft.getPreferredSize().width);
        }
        prefWidth += rowHeaderWidth;

        int upperHeight = getUpperHeight();

        prefHeight += upperHeight;

        if ((_rowFoot != null) && _rowFoot.isVisible()) {
            prefWidth += _rowFoot.getPreferredSize().width;
        }

        int lowerHeight = getLowerHeight();
        prefHeight += lowerHeight;

        /* If a scrollbar is going to appear, factor its preferred size in.
         * If the scrollbars policy is AS_NEEDED, this can be a little
         * tricky:
         *
         * - If the view is a Scrollable then scrollableTracksViewportWidth
         * and scrollableTracksViewportHeight can be used to effectively
         * disable scrolling (if they're true) in their respective dimensions.
         *
         * - Assuming that a scrollbar hasn't been disabled by the
         * previous constraint, we need to decide if the scrollbar is going
         * to appear to correctly compute the JScrollPanes preferred size.
         * To do this we compare the preferredSize of the viewport (the
         * extentSize) to the preferredSize of the view.  Although we're
         * not responsible for laying out the view we'll assume that the
         * JViewport will always give it its preferredSize.
         */

        if ((vsb != null) && (vsbPolicy != VERTICAL_SCROLLBAR_NEVER)) {
            if (vsbPolicy == VERTICAL_SCROLLBAR_ALWAYS) {
                prefWidth += vsb.getPreferredSize().width;
            }
            else if ((viewSize != null) && (extentSize != null)) {
                boolean canScroll = true;
                if (view instanceof Scrollable) {
                    canScroll = !((Scrollable) view).getScrollableTracksViewportHeight();
                }
                if (canScroll && (viewSize.height > extentSize.height)) {
                    prefWidth += vsb.getPreferredSize().width;
                }
            }
        }

        if ((hsb != null) && (hsbPolicy != HORIZONTAL_SCROLLBAR_NEVER)) {
            if (hsbPolicy == HORIZONTAL_SCROLLBAR_ALWAYS) {
                prefHeight += hsb.getPreferredSize().height;
            }
            else if ((viewSize != null) && (extentSize != null)) {
                boolean canScroll = true;
                if (view instanceof Scrollable) {
                    canScroll = !((Scrollable) view).getScrollableTracksViewportWidth();
                }
                if (canScroll && (viewSize.width > extentSize.width)) {
                    prefHeight += hsb.getPreferredSize().height;
                }
            }
        }

        return new Dimension(prefWidth, prefHeight);
    }

    private int getUpperHeight() {
        int upperHeight = 0;

        if ((upperLeft != null) && upperLeft.isVisible()) {
            upperHeight = upperLeft.getPreferredSize().height;
        }
        if ((upperRight != null) && upperRight.isVisible()) {
            upperHeight = Math.max(upperRight.getPreferredSize().height, upperHeight);
        }

        if ((colHead != null) && colHead.isVisible()) {
            upperHeight = Math.max(colHead.getPreferredSize().height, upperHeight);
        }
        return upperHeight;
    }

    private int getLowerHeight() {
        int lowerHeight = 0;

        if ((lowerLeft != null) && lowerLeft.isVisible()) {
            lowerHeight = lowerLeft.getPreferredSize().height;
        }
        if ((lowerRight != null) && lowerRight.isVisible()) {
            lowerHeight = Math.max(lowerRight.getPreferredSize().height, lowerHeight);
        }
        if ((_colFoot != null) && _colFoot.isVisible()) {
            lowerHeight = Math.max(_colFoot.getPreferredSize().height, lowerHeight);
        }
        return lowerHeight;
    }

    /**
     * The minimum size of a <code>ScrollPane</code> is the size of the insets plus minimum size of the viewport, plus
     * the scrollpane's viewportBorder insets, plus the minimum size of the visible headers, plus the minimum size of
     * the scrollbars whose displayPolicy isn't NEVER.
     *
     * @param parent the <code>Container</code> that will be laid out
     * @return a <code>Dimension</code> object specifying the minimum size
     */
    @Override
    public Dimension minimumLayoutSize(Container parent) {
        /* Sync the (now obsolete) policy fields with the
         * JScrollPane.
         */
        JScrollPane scrollPane = (JScrollPane) parent;
        vsbPolicy = scrollPane.getVerticalScrollBarPolicy();
        hsbPolicy = scrollPane.getHorizontalScrollBarPolicy();

        Insets insets = parent.getInsets();
        int minWidth = insets.left + insets.right;
        int minHeight = insets.top + insets.bottom;

        /* If there's a viewport add its minimumSize.
         */

        if (viewport != null) {
            Dimension size = viewport.getMinimumSize();
            minWidth += size.width;
            minHeight += size.height;
        }

        /* If there's a JScrollPane.viewportBorder, add its insets.
         */

        Border viewportBorder = scrollPane.getViewportBorder();
        if (viewportBorder != null) {
            Insets vpbInsets = viewportBorder.getBorderInsets(parent);
            minWidth += vpbInsets.left + vpbInsets.right;
            minHeight += vpbInsets.top + vpbInsets.bottom;
        }

        /* If a header exists and it's visible, factor its
         * minimum size in.
         */

        int rowHeaderWidth = 0;
        if (rowHead != null && rowHead.isVisible()) {
            Dimension size = rowHead.getMinimumSize();
            rowHeaderWidth = size.width;
            minHeight = Math.max(minHeight, size.height);
        }
        if (upperLeft != null && upperLeft.isVisible()) {
            rowHeaderWidth = Math.max(rowHeaderWidth, upperLeft.getMinimumSize().width);
        }
        if (lowerLeft != null && lowerLeft.isVisible()) {
            rowHeaderWidth = Math.max(rowHeaderWidth, lowerLeft.getMinimumSize().width);
        }
        minWidth += rowHeaderWidth;

        int upperHeight = 0;

        if ((upperLeft != null) && upperLeft.isVisible()) {
            upperHeight = upperLeft.getMinimumSize().height;
        }
        if ((upperRight != null) && upperRight.isVisible()) {
            upperHeight = Math.max(upperRight.getMinimumSize().height, upperHeight);
        }

        if ((colHead != null) && colHead.isVisible()) {
            Dimension size = colHead.getMinimumSize();
            minWidth = Math.max(minWidth, size.width);
            upperHeight = Math.max(size.height, upperHeight);
        }

        minHeight += upperHeight;
        if (_subColHead != null && _subColHead.isVisible()) {
            Dimension size = _subColHead.getMinimumSize();
            minWidth = Math.max(minWidth, size.width);
            minHeight += size.height;
        }

        // JIDE: added for JideScrollPaneLayout
        int lowerHeight = 0;

        if ((lowerLeft != null) && lowerLeft.isVisible()) {
            lowerHeight = lowerLeft.getMinimumSize().height;
        }
        if ((lowerRight != null) && lowerRight.isVisible()) {
            lowerHeight = Math.max(lowerRight.getMinimumSize().height, lowerHeight);
        }

        if ((_colFoot != null) && _colFoot.isVisible()) {
            Dimension size = _colFoot.getMinimumSize();
            minWidth = Math.max(minWidth, size.width);
            lowerHeight = Math.max(size.height, lowerHeight);
        }

        minHeight += lowerHeight;

        if ((_rowFoot != null) && _rowFoot.isVisible()) {
            Dimension size = _rowFoot.getMinimumSize();
            minWidth = Math.max(minWidth, size.width);
            minHeight += size.height;
        }
        // JIDE: End of added for JideScrollPaneLayout

        /* If a scrollbar might appear, factor its minimum
         * size in.
         */

        if ((vsb != null) && (vsbPolicy != VERTICAL_SCROLLBAR_NEVER)) {
            Dimension size = vsb.getMinimumSize();
            minWidth += size.width;
            minHeight = Math.max(minHeight, size.height);
        }

        if ((hsb != null) && (hsbPolicy != HORIZONTAL_SCROLLBAR_NEVER)) {
            Dimension size = hsb.getMinimumSize();
            minWidth = Math.max(minWidth, size.width);
            minHeight += size.height;
        }

        return new Dimension(minWidth, minHeight);
    }


    /**
     * Lays out the scrollpane. The positioning of components depends on the following constraints: <ul> <li> The row
     * header, if present and visible, gets its preferred width and the viewport's height.
     * <p/>
     * <li> The column header, if present and visible, gets its preferred height and the viewport's width.
     * <p/>
     * <li> If a vertical scrollbar is needed, i.e. if the viewport's extent height is smaller than its view height or
     * if the <code>displayPolicy</code> is ALWAYS, it's treated like the row header with respect to its dimensions and
     * is made visible.
     * <p/>
     * <li> If a horizontal scrollbar is needed, it is treated like the column header (see the paragraph above regarding
     * the vertical scrollbar).
     * <p/>
     * <li> If the scrollpane has a non-<code>null</code> <code>viewportBorder</code>, then space is allocated for
     * that.
     * <p/>
     * <li> The viewport gets the space available after accounting for the previous constraints.
     * <p/>
     * <li> The corner components, if provided, are aligned with the ends of the scrollbars and headers. If there is a
     * vertical scrollbar, the right corners appear; if there is a horizontal scrollbar, the lower corners appear; a row
     * header gets left corners, and a column header gets upper corners. </ul>
     *
     * @param parent the <code>Container</code> to lay out
     */
    @Override
    public void layoutContainer(Container parent) {
        /* Sync the (now obsolete) policy fields with the
         * JScrollPane.
         */
        JScrollPane scrollPane = (JScrollPane) parent;
        vsbPolicy = scrollPane.getVerticalScrollBarPolicy();
        hsbPolicy = scrollPane.getHorizontalScrollBarPolicy();

        Rectangle availR = scrollPane.getBounds();
        availR.x = availR.y = 0;

        Insets insets = parent.getInsets();
        availR.x = insets.left;
        availR.y = insets.top;
        availR.width -= insets.left + insets.right;
        availR.height -= insets.top + insets.bottom;

        /* If there's a visible column header remove the space it
        * needs from the top of availR.  The column header is treated
        * as if it were fixed height, arbitrary width.
        */

        Rectangle colHeadR = new Rectangle(0, availR.y, 0, 0);

        int upperHeight = getUpperHeight();

        if ((colHead != null) && (colHead.isVisible())) {
            int colHeadHeight = Math.min(availR.height, upperHeight);
            colHeadR.height = colHeadHeight;
            availR.y += colHeadHeight;
            availR.height -= colHeadHeight;
        }

        Rectangle subColHeadR = new Rectangle(0, availR.y, 0, 0);
        if (_subColHead != null && _subColHead.isVisible()) {
            int subColHeadHeight = Math.min(availR.height, _subColHead.getPreferredSize().height);
            subColHeadR.height = subColHeadHeight;
            availR.y += subColHeadHeight;
            availR.height -= subColHeadHeight;
        }

        /* If there's a visible row header remove the space it needs
         * from the left or right of availR.  The row header is treated
         * as if it were fixed width, arbitrary height.
         */

        Rectangle rowHeadR = new Rectangle(0, 0, 0, 0);

        if ((rowHead != null) && (rowHead.isVisible())) {
            int rowHeadWidth = rowHead.getPreferredSize().width;
            if (upperLeft != null && upperLeft.isVisible()) {
                rowHeadWidth = Math.max(rowHeadWidth, upperLeft.getPreferredSize().width);
            }
            if (lowerLeft != null && lowerLeft.isVisible()) {
                rowHeadWidth = Math.max(rowHeadWidth, lowerLeft.getPreferredSize().width);
            }

            rowHeadR.width = rowHeadWidth;
            availR.width -= rowHeadWidth;
            rowHeadR.x = availR.x;
            availR.x += rowHeadWidth;
        }

        /* If there's a JScrollPane.viewportBorder, remove the
         * space it occupies for availR.
         */

        Border viewportBorder = scrollPane.getViewportBorder();
        Insets vpbInsets;
        if (viewportBorder != null) {
            vpbInsets = viewportBorder.getBorderInsets(parent);
            availR.x += vpbInsets.left;
            availR.y += vpbInsets.top;
            availR.width -= vpbInsets.left + vpbInsets.right;
            availR.height -= vpbInsets.top + vpbInsets.bottom;
        }
        else {
            vpbInsets = new Insets(0, 0, 0, 0);
        }

        /* If there's a visible row footer remove the space it needs
         * from the left or right of availR.  The row footer is treated
         * as if it were fixed width, arbitrary height.
         */

        Rectangle rowFootR = new Rectangle(0, 0, 0, 0);

        if ((_rowFoot != null) && (_rowFoot.isVisible())) {
            int rowFootWidth = _rowFoot.getPreferredSize().width;
            if (upperRight != null && upperRight.isVisible()) {
                rowFootWidth = Math.max(rowFootWidth, upperRight.getPreferredSize().width);
            }
            if (lowerRight != null && lowerRight.isVisible()) {
                rowFootWidth = Math.max(rowFootWidth, lowerRight.getPreferredSize().width);
            }
            rowFootR.width = rowFootWidth;
            availR.width -= rowFootWidth;
            rowFootR.x = availR.x + availR.width;
        }

        /* If there's a visible column footer remove the space it
         * needs from the top of availR.  The column footer is treated
         * as if it were fixed height, arbitrary width.
         */

        Rectangle colFootR = new Rectangle(0, availR.y, 0, 0);

        int lowerHeight = getLowerHeight();

        if ((_colFoot != null) && (_colFoot.isVisible())) {
            int colFootHeight = Math.min(availR.height, lowerHeight);
            colFootR.height = colFootHeight;
            availR.height -= colFootHeight;
            colFootR.y = availR.y + availR.height;
        }

        /* At this point availR is the space available for the viewport
         * and scrollbars. rowHeadR is correct except for its height and y
* and colHeadR is correct except for its width and x.  Once we're
         * through computing the dimensions  of these three parts we can
         * go back and set the dimensions of rowHeadR.height, rowHeadR.y,
* colHeadR.width, colHeadR.x and the bounds for the corners.
         *
* We'll decide about putting up scrollbars by comparing the
* viewport views preferred size with the viewports extent
         * size (generally just its size).  Using the preferredSize is
         * reasonable because layout proceeds top down - so we expect
         * the viewport to be laid out next.  And we assume that the
         * viewports layout manager will give the view it's preferred
         * size.  One exception to this is when the view implements
         * Scrollable and Scrollable.getViewTracksViewport{Width,Height}
         * methods return true.  If the view is tracking the viewports
         * width we don't bother with a horizontal scrollbar, similarly
         * if view.getViewTracksViewport(Height) is true we don't bother
         * with a vertical scrollbar.
         */

        Component view = (viewport != null) ? viewport.getView() : null;
        Dimension viewPrefSize = (view != null) ? view.getPreferredSize() : new Dimension(0, 0);

        Dimension extentSize =
                (viewport != null) ? viewport.toViewCoordinates(availR.getSize())
                        : new Dimension(0, 0);

        boolean viewTracksViewportWidth = false;
        boolean viewTracksViewportHeight = false;
        boolean isEmpty = (availR.width < 0 || availR.height < 0);
        Scrollable sv;
        // Don't bother checking the Scrollable methods if there is no room
        // for the viewport, we aren't going to show any scrollbars in this
        // case anyway.
        if (!isEmpty && view instanceof Scrollable) {
            sv = (Scrollable) view;
            viewTracksViewportWidth = sv.getScrollableTracksViewportWidth();
            viewTracksViewportHeight = sv.getScrollableTracksViewportHeight();
        }
        else {
            sv = null;
        }

        /* If there's a vertical scrollbar and we need one, allocate
         * space for it (we'll make it visible later). A vertical
         * scrollbar is considered to be fixed width, arbitrary height.
         */

        Rectangle vsbR = new Rectangle(0, isVsbCoversWholeHeight(scrollPane) ? insets.top : availR.y - vpbInsets.top, 0, 0);

        boolean vsbNeeded;
        if (vsbPolicy == VERTICAL_SCROLLBAR_ALWAYS) {
            vsbNeeded = true;
        }
        else if (vsbPolicy == VERTICAL_SCROLLBAR_NEVER) {
            vsbNeeded = false;
        }
        else if (isEmpty) {
            vsbNeeded = false;
        }
        else {  // vsbPolicy == VERTICAL_SCROLLBAR_AS_NEEDED
            vsbNeeded = !viewTracksViewportHeight && (viewPrefSize.height > extentSize.height || (rowHead != null && rowHead.getView() != null && rowHead.getView().getPreferredSize().height > extentSize.height));
            if (!vsbNeeded && scrollPane instanceof JideScrollPane && ((JideScrollPane) scrollPane).isKeepCornerVisible() && (_vBottom != null || _vTop != null)) {
                vsbNeeded = true;
            }
        }


        if ((vsb != null) && vsbNeeded) {
            adjustForVSB(true, availR, vsbR, vpbInsets, true);
            extentSize = viewport.toViewCoordinates(availR.getSize());
        }

        /* If there's a horizontal scrollbar and we need one, allocate
         * space for it (we'll make it visible later). A horizontal
         * scrollbar is considered to be fixed height, arbitrary width.
         */

        Rectangle hsbR = new Rectangle(isHsbCoversWholeWidth(scrollPane) ? insets.left : availR.x - vpbInsets.left, 0, 0, 0);
        boolean hsbNeeded;
        if (hsbPolicy == HORIZONTAL_SCROLLBAR_ALWAYS) {
            hsbNeeded = true;
        }
        else if (hsbPolicy == HORIZONTAL_SCROLLBAR_NEVER) {
            hsbNeeded = false;
        }
        else if (isEmpty) {
            hsbNeeded = false;
        }
        else {  // hsbPolicy == HORIZONTAL_SCROLLBAR_AS_NEEDED
            hsbNeeded = !viewTracksViewportWidth && (viewPrefSize.width > extentSize.width || (colHead != null && colHead.getView() != null && colHead.getView().getPreferredSize().width > extentSize.width));
            if (!hsbNeeded && scrollPane instanceof JideScrollPane && ((JideScrollPane) scrollPane).isKeepCornerVisible() && (_hLeft != null || _hRight != null)) {
                hsbNeeded = true;
            }
        }

        if ((hsb != null) && hsbNeeded) {
            adjustForHSB(true, availR, hsbR, vpbInsets);

            /* If we added the horizontal scrollbar then we've implicitly
             * reduced  the vertical space available to the viewport.
             * As a consequence we may have to add the vertical scrollbar,
             * if that hasn't been done so already.  Of course we
             * don't bother with any of this if the vsbPolicy is NEVER.
             */
            if ((vsb != null) && !vsbNeeded &&
                    (vsbPolicy != VERTICAL_SCROLLBAR_NEVER)) {

                extentSize = viewport.toViewCoordinates(availR.getSize());
                vsbNeeded = viewPrefSize.height > extentSize.height;
                if (!vsbNeeded && scrollPane instanceof JideScrollPane && ((JideScrollPane) scrollPane).isKeepCornerVisible() && (_vBottom != null || _vTop != null)) {
                    vsbNeeded = true;
                }

                if (vsbNeeded) {
                    adjustForVSB(true, availR, vsbR, vpbInsets, true);
                }
            }
        }

        /* Set the size of the viewport first, and then recheck the Scrollable
         * methods. Some components base their return values for the Scrollable
         * methods on the size of the Viewport, so that if we don't
         * ask after resetting the bounds we may have gotten the wrong
         * answer.
         */

        // Get the scrollPane's orientation.
        boolean ltr = scrollPane.getComponentOrientation().isLeftToRight();


        if (viewport != null) {
            viewport.setBounds(adjustBounds(parent, availR, ltr));
//            viewport.setViewSize(availR.getSize());  // to fix the strange scroll bar problem reported on http://www.jidesoft.com/forum/viewtopic.php?p=20526#20526

            if (sv != null) {
                extentSize = viewport.toViewCoordinates(availR.getSize());

                boolean oldHSBNeeded = hsbNeeded;
                boolean oldVSBNeeded = vsbNeeded;
                viewTracksViewportWidth = sv.getScrollableTracksViewportWidth();
                viewTracksViewportHeight = sv.getScrollableTracksViewportHeight();
                if (vsb != null && vsbPolicy == VERTICAL_SCROLLBAR_AS_NEEDED) {
                    boolean newVSBNeeded = !viewTracksViewportHeight && (viewPrefSize.height > extentSize.height || (rowHead != null && rowHead.getView() != null && rowHead.getView().getPreferredSize().height > extentSize.height));
                    if (!newVSBNeeded && scrollPane instanceof JideScrollPane && ((JideScrollPane) scrollPane).isKeepCornerVisible() && (_vBottom != null || _vTop != null)) {
                        newVSBNeeded = true;
                    }
                    if (newVSBNeeded != vsbNeeded) {
                        vsbNeeded = newVSBNeeded;
                        adjustForVSB(vsbNeeded, availR, vsbR, vpbInsets, true);
                        extentSize = viewport.toViewCoordinates
                                (availR.getSize());
                    }
                }
                if (hsb != null && hsbPolicy == HORIZONTAL_SCROLLBAR_AS_NEEDED) {
                    boolean newHSBbNeeded = !viewTracksViewportWidth && (viewPrefSize.width > extentSize.width || (colHead != null && colHead.getView() != null && colHead.getView().getPreferredSize().width > extentSize.width));
                    if (!newHSBbNeeded && scrollPane instanceof JideScrollPane && ((JideScrollPane) scrollPane).isKeepCornerVisible() && (_hLeft != null || _hRight != null)) {
                        newHSBbNeeded = true;
                    }
                    if (newHSBbNeeded != hsbNeeded) {
                        hsbNeeded = newHSBbNeeded;
                        adjustForHSB(hsbNeeded, availR, hsbR, vpbInsets);
                        if ((vsb != null) && !vsbNeeded &&
                                (vsbPolicy != VERTICAL_SCROLLBAR_NEVER)) {

                            extentSize = viewport.toViewCoordinates
                                    (availR.getSize());
                            vsbNeeded = viewPrefSize.height >
                                    extentSize.height;
                            if (!vsbNeeded && scrollPane instanceof JideScrollPane && ((JideScrollPane) scrollPane).isKeepCornerVisible() && (_vBottom != null || _vTop != null)) {
                                vsbNeeded = true;
                            }

                            if (vsbNeeded) {
                                adjustForVSB(true, availR, vsbR, vpbInsets, true);
                            }
                        }
                        if (_rowFoot != null && _rowFoot.isVisible()) {
                            vsbR.x += rowFootR.width;
                        }
                    }
                }
                if (oldHSBNeeded != hsbNeeded ||
                        oldVSBNeeded != vsbNeeded) {
                    viewport.setBounds(adjustBounds(parent, availR, ltr));
                    // You could argue that we should recheck the
                    // Scrollable methods again until they stop changing,
                    // but they might never stop changing, so we stop here
                    // and don't do any additional checks.
                }
            }
        }

        /*
         * We now have the final size of the viewport: availR.
         * Now fixup the header and scrollbar widths/heights.
         */
        vsbR.height = isVsbCoversWholeHeight(scrollPane) ? scrollPane.getHeight() - insets.bottom - insets.top : availR.height + vpbInsets.top + vpbInsets.bottom;
        hsbR.width = isHsbCoversWholeWidth(scrollPane) ? scrollPane.getWidth() - vsbR.width - insets.left - insets.right : availR.width + vpbInsets.left + vpbInsets.right;
        rowHeadR.height = availR.height + vpbInsets.top + vpbInsets.bottom;
        rowHeadR.y = availR.y - vpbInsets.top;
        colHeadR.width = availR.width + vpbInsets.left + vpbInsets.right;
        colHeadR.x = availR.x - vpbInsets.left;
        subColHeadR.width = colHeadR.width;
        subColHeadR.x = colHeadR.x;

        colFootR.x = availR.x;
        colFootR.y = rowHeadR.y + rowHeadR.height;
        colFootR.width = availR.width;
        rowFootR.x = availR.x + availR.width;
        rowFootR.y = availR.y;
        rowFootR.height = availR.height;

        vsbR.x += rowFootR.width;
        hsbR.y += colFootR.height;

        /* Set the bounds of the remaining components.  The scrollbars
         * are made invisible if they're not needed.
         */

        if (rowHead != null) {
            rowHead.setBounds(adjustBounds(parent, rowHeadR, ltr));
        }

        if (_rowFoot != null) {
            _rowFoot.setBounds(adjustBounds(parent, rowFootR, ltr));
        }

        int columnHeaderHeight = isColumnHeadersHeightUnified(scrollPane) ? Math.max(colHeadR.height,
                Math.max(upperLeft == null ? 0 : upperLeft.getPreferredSize().height, upperRight == null ? 0 : upperRight.getPreferredSize().height)) : 0;
        int columnFooterHeight = isColumnFootersHeightUnified(scrollPane) ? Math.max(colFootR.height,
                Math.max(lowerLeft == null ? 0 : lowerLeft.getPreferredSize().height, lowerRight == null ? 0 : lowerRight.getPreferredSize().height)) : 0;

        if (colHead != null) {
            int height = isColumnHeadersHeightUnified(scrollPane) ? columnHeaderHeight : Math.min(colHeadR.height, colHead.getPreferredSize().height);
            colHead.setBounds(adjustBounds(parent, new Rectangle(colHeadR.x, colHeadR.y + colHeadR.height - height, colHeadR.width, height), ltr));
        }

        if (_subColHead != null) {
            _subColHead.setBounds(adjustBounds(parent, subColHeadR, ltr));
        }

        if (_colFoot != null) {
            int height = isColumnFootersHeightUnified(scrollPane) ? columnFooterHeight : Math.min(colFootR.height, _colFoot.getPreferredSize().height);
            _colFoot.setBounds(adjustBounds(parent, new Rectangle(colFootR.x, colFootR.y, colFootR.width, height), ltr));
        }
        else {
            if (isColumnFootersHeightUnified(scrollPane)) {
                columnFooterHeight = hsbR.height;
            }
        }

        if (vsb != null) {
            if (vsbNeeded) {
                vsb.setVisible(true);
                if (vsbPolicy == VERTICAL_SCROLLBAR_AS_NEEDED && !isEmpty && !(!viewTracksViewportHeight && (viewPrefSize.height > extentSize.height || (rowHead != null && rowHead.getView() != null && rowHead.getView().getPreferredSize().height > extentSize.height)))) {
                    vsb.setVisible(false);
                }
                if (_vTop == null && _vBottom == null)
                    vsb.setBounds(adjustBounds(parent, vsbR, ltr));
                else {
                    Rectangle rect = new Rectangle(vsbR);
                    if (_vTop != null) {
                        Dimension dim = _vTop.getPreferredSize();
                        rect.y += dim.height;
                        rect.height -= dim.height;
                        _vTop.setVisible(true);
                        _vTop.setBounds(adjustBounds(parent, new Rectangle(vsbR.x, vsbR.y, vsbR.width, dim.height), ltr));
                    }
                    if (_vBottom != null) {
                        Dimension dim = _vBottom.getPreferredSize();
                        rect.height -= dim.height;
                        _vBottom.setVisible(true);
                        _vBottom.setBounds(adjustBounds(parent, new Rectangle(vsbR.x, vsbR.y + vsbR.height - dim.height, vsbR.width, dim.height), ltr));
                    }
                    vsb.setBounds(adjustBounds(parent, rect, ltr));
                }
            }
            else {
                if (viewPrefSize.height > extentSize.height) {
                    vsb.setVisible(true);
                    vsb.setBounds(adjustBounds(parent, new Rectangle(vsbR.x, vsbR.y, 0, vsbR.height), ltr));
                }
                else {
                    vsb.setVisible(false);
                }
                if (_vTop != null)
                    _vTop.setVisible(false);
                if (_vBottom != null)
                    _vBottom.setVisible(false);
            }
        }

        if (hsb != null) {
            if (hsbNeeded) {
                hsb.setVisible(true);
                if (hsbPolicy == HORIZONTAL_SCROLLBAR_AS_NEEDED && !isEmpty && !(!viewTracksViewportWidth && (viewPrefSize.width > extentSize.width || (colHead != null && colHead.getView() != null && colHead.getView().getPreferredSize().width > extentSize.width)))) {
                    hsb.setVisible(false);
                }
                if (_hLeft == null && _hRight == null)
                    hsb.setBounds(adjustBounds(parent, hsbR, ltr));
                else {
                    Rectangle rect = new Rectangle(hsbR);
                    if (_hLeft != null) {
                        Dimension dim = _hLeft.getPreferredSize();
                        rect.x += dim.width;
                        rect.width -= dim.width;
                        _hLeft.setVisible(true);
                        _hLeft.setBounds(adjustBounds(parent, new Rectangle(hsbR.x, hsbR.y, dim.width, hsbR.height), ltr));
                        _hLeft.doLayout();
                    }
                    if (_hRight != null) {
                        Dimension dim = _hRight.getPreferredSize();
                        rect.width -= dim.width;
                        _hRight.setVisible(true);
                        _hRight.setBounds(adjustBounds(parent, new Rectangle(hsbR.x + hsbR.width - dim.width, hsbR.y, dim.width, hsbR.height), ltr));
                    }
                    hsb.setBounds(adjustBounds(parent, rect, ltr));
                }
            }
            else {
                if (viewPrefSize.width > extentSize.width) {
                    hsb.setVisible(true);
                    hsb.setBounds(adjustBounds(parent, new Rectangle(hsbR.x, hsbR.y, hsbR.width, 0), ltr));
                }
                else {
                    hsb.setVisible(false);
                }
                if (_hLeft != null)
                    _hLeft.setVisible(false);
                if (_hRight != null)
                    _hRight.setVisible(false);
            }
        }

        if (lowerLeft != null && lowerLeft.isVisible()) {
            int height = isColumnFootersHeightUnified(scrollPane) ? columnFooterHeight : Math.min(lowerLeft.getPreferredSize().height, colFootR.height);
            lowerLeft.setBounds(adjustBounds(parent, new Rectangle(rowHeadR.x, colFootR.y != 0 ? colFootR.y : hsbR.y, rowHeadR.width, height), ltr));
        }

        if (lowerRight != null && lowerRight.isVisible()) {
            int height = isColumnFootersHeightUnified(scrollPane) ? columnFooterHeight : Math.min(lowerRight.getPreferredSize().height, colFootR.height);
            lowerRight.setBounds(adjustBounds(parent, new Rectangle(rowFootR.x, colFootR.y != 0 ? colFootR.y : hsbR.y, rowFootR.width + (isVsbCoversWholeHeight(scrollPane) ? 0 : vsbR.width), height), ltr));
        }

        if (upperLeft != null && upperLeft.isVisible()) {
            int height = isColumnHeadersHeightUnified(scrollPane) ? columnHeaderHeight : Math.min(upperLeft.getPreferredSize().height, colHeadR.height);
            upperLeft.setBounds(adjustBounds(parent, new Rectangle(rowHeadR.x, colHeadR.y + colHeadR.height - height, rowHeadR.width, height), ltr));
        }

        if (upperRight != null && upperRight.isVisible()) {
            int height = isColumnHeadersHeightUnified(scrollPane) ? columnHeaderHeight : Math.min(upperRight.getPreferredSize().height, colHeadR.height);
            upperRight.setBounds(adjustBounds(parent, new Rectangle(rowFootR.x, colHeadR.y + colHeadR.height - height, rowFootR.width + (isVsbCoversWholeHeight(scrollPane) ? 0 : vsbR.width), height), ltr));
        }
    }

    private Rectangle adjustBounds(Container container, Rectangle rect, boolean ltr) {
        if (ltr) {
            return rect;
        }
        else {
            Rectangle r = new Rectangle(rect);
            int w = container.getWidth();
            r.x = w - (rect.x + rect.width);
            return r;
        }
    }

    //
    // Adjusts the <code>Rectangle</code> <code>available</code> based on if the vertical scrollbar is needed
    // (<code>wantsVSB</code>). The location of the vsb is updated in <code>vsbR</code>, and the viewport border insets
    // (<code>vpbInsets</code>) are used to offset the vsb. This is only called when <code>wantsVSB</code> has changed,
    // eg you shouldn't invoke adjustForVSB(true) twice.
    //
    private void adjustForVSB(boolean wantsVSB, Rectangle available,
                              Rectangle vsbR, Insets vpbInsets,
                              boolean leftToRight) {
        int oldWidth = vsbR.width;
        if (wantsVSB) {
            int vsbWidth = Math.max(0, vsb.getPreferredSize().width);

            available.width -= vsbWidth;
            vsbR.width = vsbWidth;

            if (leftToRight) {
                vsbR.x = available.x + available.width + vpbInsets.right;
            }
            else {
                vsbR.x = available.x - vpbInsets.left;
                available.x += vsbWidth;
            }
        }
        else {
            available.width += oldWidth;
        }
    }

    //
    // Adjusts the <code>Rectangle</code> <code>available</code> based on if the horizontal scrollbar is needed
    // (<code>wantsHSB</code>). The location of the hsb is updated in <code>hsbR</code>, and the viewport border insets
    // (<code>vpbInsets</code>) are used to offset the hsb.  This is only called when <code>wantsHSB</code> has changed,
    // eg you shouldn't invoked adjustForHSB(true) twice.
    //
    private void adjustForHSB(boolean wantsHSB, Rectangle available,
                              Rectangle hsbR, Insets vpbInsets) {
        int oldHeight = hsbR.height;
        if (wantsHSB) {
            int hsbHeight = Math.max(0, hsb.getPreferredSize().height);

            available.height -= hsbHeight;
            hsbR.y = available.y + available.height + vpbInsets.bottom;
            hsbR.height = hsbHeight;
        }
        else {
            available.height += oldHeight;
        }
    }

    /**
     * The UI resource version of <code>ScrollPaneLayout</code>.
     */
    static class UIResource extends JideScrollPaneLayout implements javax.swing.plaf.UIResource {
        private static final long serialVersionUID = 1057343395078846689L;
    }
}
