/*
 * @(#)FlatScrollPaneLayout.java 12/13/2006
 *
 * Copyright 2002 - 2006 JIDE Software Inc. All rights reserved.
 */

package com.jidesoft.swing;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

/**
 * The layout manager used by <code>SimpleScrollPaneLayout</code>.
 */
class SimpleScrollPaneLayout extends ScrollPaneLayout {
    protected AbstractButton _scrollUp;
    protected AbstractButton _scrollDown;
    protected AbstractButton _scrollLeft;
    protected AbstractButton _scrollRight;

    @Override
    public void syncWithScrollPane(JScrollPane sp) {
        super.syncWithScrollPane(sp);
        if (sp instanceof SimpleScrollPane) {
            _scrollUp = ((SimpleScrollPane) sp).getScrollUpButton();
            _scrollDown = ((SimpleScrollPane) sp).getScrollDownButton();
            _scrollLeft = ((SimpleScrollPane) sp).getScrollLeftButton();
            _scrollRight = ((SimpleScrollPane) sp).getScrollRightButton();
        }
    }

    @Override
    public void addLayoutComponent(String s, Component c) {
        if (SimpleScrollPane.SCROLL_UP_BUTTON.equals(s)) {
            _scrollUp = (AbstractButton) addSingletonComponent(_scrollUp, c);
        }
        else if (SimpleScrollPane.SCROLL_DOWN_BUTTON.equals(s)) {
            _scrollDown = (AbstractButton) addSingletonComponent(_scrollDown, c);
        }
        else if (SimpleScrollPane.SCROLL_LEFT_BUTTON.equals(s)) {
            _scrollLeft = (AbstractButton) addSingletonComponent(_scrollLeft, c);
        }
        else if (SimpleScrollPane.SCROLL_RIGHT_BUTTON.equals(s)) {
            _scrollRight = (AbstractButton) addSingletonComponent(_scrollRight, c);
        }
        else {
            super.addLayoutComponent(s, c);
        }
    }

    @Override
    public void removeLayoutComponent(Component c) {
        if (c == _scrollUp) {
            _scrollUp = null;
        }
        else if (c == _scrollDown) {
            _scrollDown = null;
        }
        else if (c == _scrollLeft) {
            _scrollLeft = null;
        }
        else if (c == _scrollRight) {
            _scrollRight = null;
        }
        else {
            super.removeLayoutComponent(c);
        }
    }

    /**
     * The preferred size of a <code>ScrollPane</code> is the size of the insets,
     * plus the preferred size of the viewport, plus the preferred size of
     * the visible headers, plus the preferred size of the scrollbars
     * that will appear given the current view and the current
     * scrollbar displayPolicies.
     * <p>Note that the rowHeader is calculated as part of the preferred width
     * and the colHeader is calculated as part of the preferred size.
     *
     * @param parent the <code>Container</code> that will be laid out
     * @return a <code>Dimension</code> object specifying the preferred size of the
     *         viewport and any scrollbars
     * @see javax.swing.ViewportLayout
     * @see java.awt.LayoutManager
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

        if (_scrollUp != null && _scrollDown != null && vsbPolicy != VERTICAL_SCROLLBAR_NEVER) {
            if (vsbPolicy == VERTICAL_SCROLLBAR_ALWAYS) {
                prefHeight += _scrollUp.isVisible() ? _scrollUp.getPreferredSize().height : 0;
                prefHeight += _scrollDown.isVisible() ? _scrollDown.getPreferredSize().height : 0;
            }
            else if ((viewSize != null) && (extentSize != null)) {
                boolean canScroll = true;
                if (view instanceof Scrollable) {
                    canScroll = !((Scrollable) view).getScrollableTracksViewportHeight();
                }
                if (canScroll && (viewSize.height > extentSize.height)) {
                    prefHeight += _scrollUp.isVisible() ? _scrollUp.getPreferredSize().height : 0;
                    prefHeight += _scrollDown.isVisible() ? _scrollDown.getPreferredSize().height : 0;
                }
            }
        }

        if (_scrollLeft != null && _scrollRight != null && hsbPolicy != HORIZONTAL_SCROLLBAR_NEVER) {
            if (hsbPolicy == HORIZONTAL_SCROLLBAR_ALWAYS) {
                prefWidth += _scrollLeft.isVisible() ? _scrollLeft.getPreferredSize().width : 0;
                prefWidth += _scrollRight.isVisible() ? _scrollRight.getPreferredSize().width : 0;
            }
            else if ((viewSize != null) && (extentSize != null)) {
                boolean canScroll = true;
                if (view instanceof Scrollable) {
                    canScroll = !((Scrollable) view).getScrollableTracksViewportWidth();
                }
                if (canScroll && (viewSize.width > extentSize.width)) {
                    prefWidth += _scrollLeft.isVisible() ? _scrollLeft.getPreferredSize().width : 0;
                    prefWidth += _scrollRight.isVisible() ? _scrollRight.getPreferredSize().width : 0;
                }
            }
        }

        return new Dimension(prefWidth, prefHeight);
    }

    /**
     * The minimum size of a <code>ScrollPane</code> is the size of the insets
     * plus minimum size of the viewport, plus the scrollpane's
     * viewportBorder insets, plus the minimum size
     * of the visible headers, plus the minimum size of the
     * scrollbars whose displayPolicy isn't NEVER.
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

        /* If a scrollbar might appear, factor its minimum
         * size in.
         */

        if (_scrollUp != null && _scrollDown != null && vsbPolicy != VERTICAL_SCROLLBAR_NEVER) {
            Dimension size = new Dimension(Math.max(_scrollUp.getMinimumSize().width, _scrollDown.getMinimumSize().width), 0);
            size.height += _scrollUp.isVisible() ? _scrollUp.getMinimumSize().height : 0;
            size.height += _scrollDown.isVisible() ? _scrollDown.getMinimumSize().height : 0;
            minHeight += size.height;
            minWidth = Math.max(minWidth, size.width);
        }

        if (_scrollLeft != null && _scrollLeft != null && hsbPolicy != HORIZONTAL_SCROLLBAR_NEVER) {
            Dimension size = new Dimension(0, Math.max(_scrollLeft.getMinimumSize().height, _scrollRight.getMinimumSize().height));
            size.width += _scrollLeft.isVisible() ? _scrollLeft.getMinimumSize().width : 0;
            size.width += _scrollRight.isVisible() ? _scrollRight.getMinimumSize().width : 0;
            minWidth += size.width;
            minHeight = Math.max(minHeight, size.height);
        }

        return new Dimension(minWidth, minHeight);
    }


    /**
     * Lays out the scrollpane. The positioning of components depends on
     * the following constraints:
     * <ul>
     * <li> The row header, if present and visible, gets its preferred
     * width and the viewport's height.
     * <p/>
     * <li> The column header, if present and visible, gets its preferred
     * height and the viewport's width.
     * <p/>
     * <li> If a vertical scrollbar is needed, i.e. if the viewport's extent
     * height is smaller than its view height or if the <code>displayPolicy</code>
     * is ALWAYS, it's treated like the row header with respect to its
     * dimensions and is made visible.
     * <p/>
     * <li> If a horizontal scrollbar is needed, it is treated like the
     * column header (see the paragraph above regarding the vertical scrollbar).
     * <p/>
     * <li> If the scrollpane has a non-<code>null</code>
     * <code>viewportBorder</code>, then space is allocated for that.
     * <p/>
     * <li> The viewport gets the space available after accounting for
     * the previous constraints.
     * <p/>
     * <li> The corner components, if provided, are aligned with the
     * ends of the scrollbars and headers. If there is a vertical
     * scrollbar, the right corners appear; if there is a horizontal
     * scrollbar, the lower corners appear; a row header gets left
     * corners, and a column header gets upper corners.
     * </ul>
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
        Dimension viewPrefSize =
                (view != null) ? view.getPreferredSize()
                        : new Dimension(0, 0);

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

        Rectangle scrollUpR = new Rectangle(0, 0, 0, _scrollUp.getPreferredSize().height);
        Rectangle scrollDownR = new Rectangle(0, 0, 0, _scrollDown.getPreferredSize().height);

        boolean vsbNeeded;
        if (isEmpty) {
            vsbNeeded = false;
        }
        else if (vsbPolicy == VERTICAL_SCROLLBAR_ALWAYS) {
            vsbNeeded = true;
        }
        else if (vsbPolicy == VERTICAL_SCROLLBAR_NEVER) {
            vsbNeeded = false;
        }
        else {  // vsbPolicy == VERTICAL_SCROLLBAR_AS_NEEDED
            if (!_scrollUp.isEnabled()) {
                scrollUpR.height = 0;
            }
            if (!_scrollDown.isEnabled()) {
                scrollDownR.height = 0;
            }
            vsbNeeded = !viewTracksViewportHeight && (viewPrefSize.height > extentSize.height);
        }


        if (_scrollUp != null && _scrollDown != null && vsbNeeded) {
            adjustForScrollUpAndDown(true, availR, scrollUpR, scrollDownR, vpbInsets);
            extentSize = viewport.toViewCoordinates(availR.getSize());
        }

        /* If there's a horizontal scrollbar and we need one, allocate
         * space for it (we'll make it visible later). A horizontal
         * scrollbar is considered to be fixed height, arbitrary width.
         */

        Rectangle scrollLeftR = new Rectangle(0, 0, _scrollLeft.getPreferredSize().width, 0);
        Rectangle scrollRightR = new Rectangle(0, 0, _scrollRight.getPreferredSize().width, 0);
        boolean hsbNeeded;
        if (isEmpty) {
            hsbNeeded = false;
        }
        else if (hsbPolicy == HORIZONTAL_SCROLLBAR_ALWAYS) {
            hsbNeeded = true;
        }
        else if (hsbPolicy == HORIZONTAL_SCROLLBAR_NEVER) {
            hsbNeeded = false;
        }
        else {  // hsbPolicy == HORIZONTAL_SCROLLBAR_AS_NEEDED
            if (!_scrollLeft.isEnabled()) {
                scrollLeftR.width = 0;
            }
            if (!_scrollRight.isEnabled()) {
                scrollRightR.width = 0;
            }
            hsbNeeded = !viewTracksViewportWidth && (viewPrefSize.width > extentSize.width);
        }

        if ((hsb != null) && hsbNeeded) {
            adjustForScrollLeftAndRight(true, availR, scrollLeftR, scrollRightR, vpbInsets);

            /* If we added the horizontal scrollbar then we've implicitly
             * reduced  the vertical space available to the viewport.
             * As a consequence we may have to add the vertical scrollbar,
             * if that hasn't been done so already.  Of course we
             * don't bother with any of this if the vsbPolicy is NEVER.
             */
            if (_scrollUp != null && _scrollDown != null && !vsbNeeded &&
                    (vsbPolicy != VERTICAL_SCROLLBAR_NEVER)) {

                extentSize = viewport.toViewCoordinates(availR.getSize());
                vsbNeeded = viewPrefSize.height > extentSize.height;

                if (vsbNeeded) {
                    adjustForScrollUpAndDown(true, availR, scrollUpR, scrollDownR, vpbInsets);
                }
            }
        }

        /* Set the size of the viewport first, and then recheck the Scrollable
         * methods. Some components base their return values for the Scrollable
         * methods on the size of the Viewport, so that if we don't
         * ask after resetting the bounds we may have gotten the wrong
         * answer.
         */

        if (viewport != null) {
            viewport.setBounds(availR);

            if (sv != null) {
                extentSize = viewport.toViewCoordinates(availR.getSize());

                boolean oldHSBNeeded = hsbNeeded;
                boolean oldVSBNeeded = vsbNeeded;
                viewTracksViewportWidth = sv.
                        getScrollableTracksViewportWidth();
                viewTracksViewportHeight = sv.
                        getScrollableTracksViewportHeight();
                if (vsb != null && vsbPolicy == VERTICAL_SCROLLBAR_AS_NEEDED) {
                    boolean newVSBNeeded = !viewTracksViewportHeight &&
                            (viewPrefSize.height > extentSize.height);
                    if (newVSBNeeded != vsbNeeded) {
                        vsbNeeded = newVSBNeeded;
                        adjustForScrollUpAndDown(vsbNeeded, availR, scrollUpR, scrollDownR, vpbInsets);
                        extentSize = viewport.toViewCoordinates(availR.getSize());
                    }
                }
                if (hsb != null && hsbPolicy == HORIZONTAL_SCROLLBAR_AS_NEEDED) {
                    boolean newHSBbNeeded = !viewTracksViewportWidth &&
                            (viewPrefSize.width > extentSize.width);
                    if (newHSBbNeeded != hsbNeeded) {
                        hsbNeeded = newHSBbNeeded;
                        adjustForScrollLeftAndRight(hsbNeeded, availR, scrollLeftR, scrollRightR, vpbInsets);
                        if ((vsb != null) && !vsbNeeded &&
                                (vsbPolicy != VERTICAL_SCROLLBAR_NEVER)) {

                            extentSize = viewport.toViewCoordinates
                                    (availR.getSize());
                            vsbNeeded = viewPrefSize.height >
                                    extentSize.height;

                            if (vsbNeeded) {
                                adjustForScrollUpAndDown(true, availR, scrollUpR, scrollDownR, vpbInsets);
                            }
                        }
                    }
                }
                if (oldHSBNeeded != hsbNeeded ||
                        oldVSBNeeded != vsbNeeded) {
                    viewport.setBounds(availR);
                    // You could argue that we should recheck the
                    // Scrollable methods again until they stop changing,
                    // but they might never stop changing, so we stop here
                    // and don't do any additional checks.
                }
            }
        }

        /* We now have the final size of the viewport: availR.
         * Now fixup the header and scrollbar widths/heights.
         */
        // TODO

        if (_scrollUp != null && _scrollDown != null) {
            if (vsbNeeded) {
                _scrollUp.setVisible(true);
                _scrollDown.setVisible(true);
                _scrollUp.setBounds(scrollUpR);
                _scrollDown.setBounds(scrollDownR);
            }
            else {
                _scrollUp.setVisible(false);
                _scrollDown.setVisible(false);
                _scrollUp.setBounds(scrollUpR.x, scrollUpR.y, 0, 0);
                _scrollDown.setBounds(scrollDownR.x, scrollDownR.y, 0, 0);
            }
        }

        if (_scrollLeft != null && _scrollRight != null) {
            if (hsbNeeded) {
                _scrollLeft.setVisible(true);
                _scrollRight.setVisible(true);
                _scrollLeft.setBounds(scrollLeftR);
                _scrollRight.setBounds(scrollRightR);
            }
            else {
                _scrollLeft.setVisible(false);
                _scrollRight.setVisible(false);
                _scrollLeft.setBounds(scrollLeftR.x, scrollLeftR.y, 0, 0);
                _scrollRight.setBounds(scrollRightR.x, scrollRightR.y, 0, 0);
            }
        }
    }

    /**
     * Adjusts the <code>Rectangle</code> <code>available</code> based on if
     * the vertical scrollbar is needed (<code>wantsVSB</code>).
     * The location of the vsb is updated in <code>vsbR</code>, and
     * the viewport border insets (<code>vpbInsets</code>) are used to offset
     * the vsb. This is only called when <code>wantsVSB</code> has
     * changed, eg you shouldn't invoke adjustForVSB(true) twice.
     */
    private void adjustForScrollUpAndDown(boolean wantsVSB, Rectangle available,
                                          Rectangle upR, Rectangle downR, Insets vpbInsets) {
        if (wantsVSB) {
            int buttonWidth = Math.max(0, Math.max(available.width + vpbInsets.left + vpbInsets.right, Math.max(_scrollUp.getPreferredSize().width, _scrollDown.getPreferredSize().width)));

            available.height -= upR.height;
            available.height -= downR.height;

            upR.width = buttonWidth;
            downR.width = buttonWidth;

            upR.x = available.x - vpbInsets.left;
            downR.x = available.x - vpbInsets.left;

            upR.y = available.y - vpbInsets.top;
            available.y += upR.height;
            downR.y = available.y + available.height + vpbInsets.bottom;

        }
    }

    /**
     * Adjusts the <code>Rectangle</code> <code>available</code> based on if
     * the horizontal scrollbar is needed (<code>wantsHSB</code>).
     * The location of the hsb is updated in <code>hsbR</code>, and
     * the viewport border insets (<code>vpbInsets</code>) are used to offset
     * the hsb.  This is only called when <code>wantsHSB</code> has
     * changed, eg you shouldn't invoked adjustForHSB(true) twice.
     */
    private void adjustForScrollLeftAndRight(boolean wantsHSB, Rectangle available,
                                             Rectangle leftR, Rectangle rightR, Insets vpbInsets) {
        if (wantsHSB) {
            int buttonHeight = Math.max(0, Math.max(available.height + vpbInsets.top + vpbInsets.bottom, Math.max(_scrollLeft.getPreferredSize().height, _scrollRight.getPreferredSize().height)));

            available.width -= leftR.width;
            available.width -= rightR.width;

            leftR.height = buttonHeight;
            rightR.height = buttonHeight;

            leftR.y = available.y - vpbInsets.top;
            rightR.y = available.y - vpbInsets.top;

            leftR.x = available.x - vpbInsets.left;
            available.x += leftR.width;
            rightR.x = available.x + available.width + vpbInsets.right;

        }
    }

    /**
     * The UI resource version of <code>ScrollPaneLayout</code>.
     */
    static class UIResource extends SimpleScrollPaneLayout implements javax.swing.plaf.UIResource {
    }
}
