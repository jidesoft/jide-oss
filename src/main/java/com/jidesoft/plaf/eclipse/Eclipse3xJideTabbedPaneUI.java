/*
 * @(#)Eclipse3xJideTabbedPaneUI.java 8/28/2006
 *
 * Copyright 2002 - 2006 JIDE Software Inc. All rights reserved.
 */

package com.jidesoft.plaf.eclipse;

import com.jidesoft.plaf.UIDefaultsLookup;
import com.jidesoft.plaf.vsnet.VsnetJideTabbedPaneUI;
import com.jidesoft.swing.JideSwingUtilities;
import com.jidesoft.swing.JideTabbedPane;

import javax.swing.*;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicGraphicsUtils;
import javax.swing.text.View;
import java.awt.*;

/**
 * A eclipse L&f implementation of JideTabbedPaneUI
 */
public class Eclipse3xJideTabbedPaneUI extends VsnetJideTabbedPaneUI {
    // pixels
    protected int _closeButtonMargin;// margin around the close button

    protected int _closeButtonMarginSize;// margin of the close button when every tab has a close button

    protected int _iconMarginHorizon;// distance from icon to tab rect start when the tab is on the top or bottom

    protected int _iconMarginVertical;// distance from icon to tab rect start when the tab is on the left or right

    @SuppressWarnings({"UnusedDeclaration"})
    public static ComponentUI createUI(JComponent c) {
        return new Eclipse3xJideTabbedPaneUI();
    }

    @Override
    public void installUI(JComponent c) {
        super.installUI(c);
        _rectSizeExtend = 12;
    }

    @Override
    protected void installDefaults() {
        super.installDefaults();
        // set the border of the TabbedPane
        LookAndFeel.installBorder(_tabPane, "JideTabbedPane.border");
        _closeButtonMargin = UIDefaultsLookup.getInt("JideTabbedPane.closeButtonMargin");
        _closeButtonMarginSize = UIDefaultsLookup.getInt("JideTabbedPane.closeButtonMarginSize");
        _iconMarginHorizon = UIDefaultsLookup.getInt("JideTabbedPane.iconMarginHorizon");
        _iconMarginVertical = UIDefaultsLookup.getInt("JideTabbedPane.iconMarginVertical");
    }

    @Override
    public void paintBackground(Graphics g, Component c) {
        if (getTabShape() == JideTabbedPane.SHAPE_ECLIPSE3X) {
            if (_tabPane.isOpaque()) {
                int width = c.getWidth();
                int height = c.getHeight();

                int temp1 = -1;
                int temp2 = -1;
                if (isTabLeadingComponentVisible()) {
                    if (height < _tabLeadingComponent.getSize().height) {
                        height = _tabLeadingComponent.getSize().height;
                        temp1 = _tabLeadingComponent.getSize().height;
                    }
                    if (width < _tabLeadingComponent.getSize().width) {
                        width = _tabLeadingComponent.getSize().width;
                        temp2 = _tabLeadingComponent.getSize().width;
                    }
                }

                if (isTabTrailingComponentVisible()) {
                    if (height < _tabTrailingComponent.getSize().height && temp1 < _tabTrailingComponent.getSize().height) {
                        height = _tabTrailingComponent.getSize().height;
                    }
                    if (width < _tabTrailingComponent.getSize().width && temp2 < _tabTrailingComponent.getSize().width) {
                        width = _tabTrailingComponent.getSize().width;
                    }
                }

                g.setColor(_background);
                g.fillRect(0, 0, width, height);
            }
        }
        else {
            super.paintBackground(g, c);
        }
    }

    @Override
    protected void ensureCurrentLayout() {
        /*
           * If tabPane doesn't have a peer yet, the validate() call will silently
           * fail. We handle that by forcing a layout if tabPane is still invalid.
           * See bug 4237677.
           */
        if (!_tabPane.isValid()) {
            TabbedPaneLayout layout = (TabbedPaneLayout) _tabPane.getLayout();
            layout.calculateLayoutInfo();
        }

        // ensure the bounds of the close buttons when they are showed on the
        // tab
        if (scrollableTabLayoutEnabled() && isShowCloseButton()
                && isShowCloseButtonOnTab()) {
            for (int i = 0; i < _closeButtons.length; i++) {
                if (_tabPane.isShowCloseButtonOnSelectedTab()) {
                    if (i != _tabPane.getSelectedIndex()) {
                        _closeButtons[i].setBounds(0, 0, 0, 0);
                        continue;
                    }
                }
                else {
                    if (i >= _rects.length) {
                        _closeButtons[i].setBounds(0, 0, 0, 0);
                        continue;
                    }
                }

                if (!_tabPane.isTabClosableAt(i)) {
                    _closeButtons[i].setBounds(0, 0, 0, 0);
                    continue;
                }
                Dimension size = _closeButtons[i].getPreferredSize();

                Rectangle bounds;
                if (_closeButtonAlignment == SwingConstants.TRAILING) {
                    if (_tabPane.getTabPlacement() == JideTabbedPane.TOP || _tabPane.getTabPlacement() == JideTabbedPane.BOTTOM) {
                        if (_tabPane.getComponentOrientation().isLeftToRight()) {
                            bounds = new Rectangle(_rects[i].x + _rects[i].width - size.width - 16, _rects[i].y + ((_rects[i].height - size.height) >> 1), size.width, size.height);
                        }
                        else {
                            bounds = new Rectangle(_rects[i].x + 4, ((_rects[i].height - size.height) >> 1), size.width, size.height);
                        }
                    }
                    else if (_tabPane.getTabPlacement() == JideTabbedPane.LEFT) {
                        bounds = new Rectangle(_rects[i].x + ((_rects[i].width - size.width) >> 1), _rects[i].y + _rects[i].height - size.height - 16, size.width, size.height);
                    }
                    else /*if (_tabPane.getTabPlacement() == JideTabbedPane.RIGHT)*/ {
                        bounds = new Rectangle(_rects[i].x + ((_rects[i].width - size.width) >> 1), _rects[i].y + _rects[i].height - size.height - 16, size.width, size.height);
                    }
                }
                else {
                    bounds = new Rectangle(_rects[i].x + 4, ((_rects[i].height - size.height) >> 1), size.width, size.height);
                    if (!_tabPane.getComponentOrientation().isLeftToRight() && (_tabPane.getTabPlacement() == JideTabbedPane.TOP || _tabPane.getTabPlacement() == JideTabbedPane.BOTTOM)) {
                        bounds = new Rectangle(_rects[i].x + _rects[i].width - size.width - 16, _rects[i].y + ((_rects[i].height - size.height) >> 1), size.width, size.height);
                    }
                }
                if (_closeButtons[i] instanceof JideTabbedPane.NoFocusButton) {
                    ((JideTabbedPane.NoFocusButton) _closeButtons[i]).setIndex(i);
                }
                if (!bounds.equals(_closeButtons[i].getBounds())) {
                    _closeButtons[i].setBounds(bounds);
                }
                if (_tabPane.getSelectedIndex() == i) {
                    _closeButtons[i].setBackground(_selectedColor == null ? _tabPane.getBackgroundAt(i) : _selectedColor);
                }
                else {
                    _closeButtons[i].setBackground(_tabPane.getBackgroundAt(i));
                }
            }
        }

    }

    /**
     * Paints the tabs in the tab area. Invoked by paint(). The graphics parameter must be a valid <code>Graphics</code>
     * object. Tab placement may be either: <code>JTabbedPane.TOP</code>, <code>JTabbedPane.BOTTOM</code>,
     * <code>JTabbedPane.LEFT</code>, or <code>JTabbedPane.RIGHT</code>. The selected index must be a valid tabbed pane
     * tab index (0 to tab count - 1, inclusive) or -1 if no tab is currently selected. The handling of invalid
     * parameters is unspecified.
     *
     * @param g             the graphics object to use for rendering
     * @param tabPlacement  the placement for the tabs within the JTabbedPane
     * @param selectedIndex the tab index of the selected component
     */
    @Override
    protected void paintTabArea(Graphics g, int tabPlacement, int selectedIndex, Component c) {

        if (!PAINT_TABAREA) {
            return;
        }

        if (getTabShape() == JideTabbedPane.SHAPE_ECLIPSE3X) {
            int tabCount = _tabPane.getTabCount();

            Rectangle iconRect = new Rectangle(), textRect = new Rectangle();
            Rectangle clipRect = g.getClipBounds();
            Rectangle viewRect = _tabScroller.viewport.getViewRect();

            if (_tabPane.isOpaque()) {
                g.setColor(_tabBackground);
                g.fillRect(clipRect.x, clipRect.y, clipRect.width, clipRect.height);
            }

            // Paint tabRuns of tabs from back to front
            for (int i = _runCount - 1; i >= 0; i--) {
                int start = _tabRuns[i];
                int next = _tabRuns[(i == _runCount - 1) ? 0 : i + 1];
                int end = (next != 0 ? next - 1 : tabCount - 1);
                for (int j = start; j <= end; j++) {
                    if (_rects[j].intersects(clipRect)) {
                        paintTab(g, tabPlacement, _rects, j, iconRect, textRect);
                    }
                }
            }

            // Paint selected tab if its in the front run
            // since it may overlap other tabs
            if (selectedIndex >= 0 && getRunForTab(tabCount, selectedIndex) == 0) {
                if (_rects[selectedIndex].intersects(clipRect)) {
                    paintTab(g, tabPlacement, _rects, selectedIndex, iconRect,
                            textRect);
                }
            }

            if (_tabPane.isOpaque()) {
                g.setColor(_shadow);
                switch (tabPlacement) {
                    case LEFT:
                        if (!isTabLeadingComponentVisible()) {
                            g.fillRect(viewRect.x, viewRect.y + 3, 1, 2);
                            g.fillRect(viewRect.x + 1, viewRect.y + 2, 1, 1);
                            g.fillRect(viewRect.x + 2, viewRect.y + 1, 1, 1);
                            g.fillRect(viewRect.x + 3, viewRect.y, 2, 1);
                        }

                        if (isNoneTabTrailingComponentVisible()) {
                            g.fillRect(viewRect.x + 3, viewRect.y + viewRect.height - 1, 2, 1);
                            g.fillRect(viewRect.x + 2, viewRect.y + viewRect.height - 2, 1, 1);
                            g.fillRect(viewRect.x + 1, viewRect.y + viewRect.height - 3, 1, 1);
                            g.fillRect(viewRect.x, viewRect.y + viewRect.height - 5, 1, 2);
                        }

                        g.setColor(_tabBackground);
                        g.fillRect(viewRect.x, viewRect.y, 3, 1);
                        g.fillRect(viewRect.x, viewRect.y + 1, 2, 1);
                        g.fillRect(viewRect.x, viewRect.y + 2, 1, 1);
                        break;
                    case RIGHT:
                        if (!isTabLeadingComponentVisible()) {
                            g.fillRect(viewRect.x + viewRect.width - 5, viewRect.y, 2, 1);
                            g.fillRect(viewRect.x + viewRect.width - 3, viewRect.y + 1, 1, 1);
                            g.fillRect(viewRect.x + viewRect.width - 2, viewRect.y + 2, 1, 1);
                            g.fillRect(viewRect.x + viewRect.width - 1, viewRect.y + 3, 1, 2);
                        }

                        if (isNoneTabTrailingComponentVisible()) {
                            g.fillRect(viewRect.x + viewRect.width - 5, viewRect.y + viewRect.height - 1, 2, 1);
                            g.fillRect(viewRect.x + viewRect.width - 3, viewRect.y + viewRect.height - 2, 1, 1);
                            g.fillRect(viewRect.x + viewRect.width - 2, viewRect.y + viewRect.height - 3, 1, 1);
                            g.fillRect(viewRect.x + viewRect.width - 1, viewRect.y + viewRect.height - 5, 1, 2);
                        }

                        g.setColor(_tabBackground);
                        g.fillRect(viewRect.x + viewRect.width - 3, viewRect.y, 3, 1);
                        g.fillRect(viewRect.x + viewRect.width - 2, viewRect.y + 1, 2, 1);
                        g.fillRect(viewRect.x + viewRect.width - 1, viewRect.y + 2, 1, 1);
                        break;
                    case BOTTOM:
                        if (!isTabLeadingComponentVisible()) {
                            g.fillRect(viewRect.x + 3, viewRect.y + viewRect.height - 1, 2, 1);
                            g.fillRect(viewRect.x + 2, viewRect.y + viewRect.height - 2, 1, 1);
                            g.fillRect(viewRect.x + 1, viewRect.y + viewRect.height - 3, 1, 1);
                            g.fillRect(viewRect.x, viewRect.y + viewRect.height - 5, 1, 2);
                        }

                        if (isNoneTabTrailingComponentVisible()) {
                            g.fillRect(viewRect.x + viewRect.width - 5, viewRect.y + viewRect.height - 1, 2, 1);
                            g.fillRect(viewRect.x + viewRect.width - 3, viewRect.y + viewRect.height - 2, 1, 1);
                            g.fillRect(viewRect.x + viewRect.width - 2, viewRect.y + viewRect.height - 3, 1, 1);
                            g.fillRect(viewRect.x + viewRect.width - 1, viewRect.y + viewRect.height - 5, 1, 2);
                        }

                        g.setColor(_tabBackground);
                        g.fillRect(viewRect.x, viewRect.y + viewRect.height - 1, 3, 1);
                        g.fillRect(viewRect.x, viewRect.y + viewRect.height - 2, 2, 1);
                        g.fillRect(viewRect.x, viewRect.y + viewRect.height - 3, 1, 1);
                        break;
                    case TOP:
                    default:
                        if (!isTabLeadingComponentVisible()) {
                            g.fillRect(viewRect.x + 3, viewRect.y, 2, 1);
                            g.fillRect(viewRect.x + 2, viewRect.y + 1, 1, 1);
                            g.fillRect(viewRect.x + 1, viewRect.y + 2, 1, 1);
                            g.fillRect(viewRect.x, viewRect.y + 3, 1, 2);
                        }

                        if (isNoneTabTrailingComponentVisible()) {
                            g.fillRect(viewRect.x + viewRect.width - 5, viewRect.y, 2, 1);
                            g.fillRect(viewRect.x + viewRect.width - 3, viewRect.y + 1, 1, 1);
                            g.fillRect(viewRect.x + viewRect.width - 2, viewRect.y + 2, 1, 1);
                            g.fillRect(viewRect.x + viewRect.width - 1, viewRect.y + 3, 1, 2);
                        }

                        g.setColor(_tabBackground);
                        g.fillRect(viewRect.x, viewRect.y, 3, 1);
                        g.fillRect(viewRect.x, viewRect.y + 1, 2, 1);
                        g.fillRect(viewRect.x, viewRect.y + 2, 1, 1);
                }
            }
        }
        else {
            super.paintTabArea(g, tabPlacement, selectedIndex, c);
        }
    }

    private boolean isNoneTabTrailingComponentVisible() {
        return !_tabScroller.scrollForwardButton.isVisible() && !_tabScroller.scrollBackwardButton.isVisible() && !_tabScroller.closeButton.isVisible() && !isTabTrailingComponentVisible();
    }


    @Override
    protected void layoutLabel(int tabPlacement, FontMetrics metrics,
                               int tabIndex, String title, Icon icon, Rectangle tabRect,
                               Rectangle iconRect, Rectangle textRect, boolean isSelected) {
        textRect.x = textRect.y = iconRect.x = iconRect.y = 0;

        View v = getTextViewForTab(tabIndex);
        if (v != null) {
            _tabPane.putClientProperty("html", v);
        }

        SwingUtilities.layoutCompoundLabel(_tabPane, metrics, title, icon,
                SwingUtilities.CENTER, SwingUtilities.CENTER,
                SwingUtilities.CENTER, SwingUtilities.TRAILING, tabRect,
                iconRect, textRect, _textIconGap);

        _tabPane.putClientProperty("html", null);

        if (tabPlacement == JideTabbedPane.TOP || tabPlacement == JideTabbedPane.BOTTOM) {
            iconRect.x = tabRect.x + _iconMarginHorizon;
            textRect.x = (icon != null ? iconRect.x + iconRect.width + _textIconGap : tabRect.x + _textPadding);
            iconRect.width = Math.min(iconRect.width, tabRect.width - _tabRectPadding);
            textRect.width = tabRect.width - _tabRectPadding - iconRect.width - (icon != null ? _textIconGap : _noIconMargin);

            if ((getTabResizeMode() == JideTabbedPane.RESIZE_MODE_FIT || _tabPane.getTabResizeMode() == JideTabbedPane.RESIZE_MODE_FIXED)) {
                textRect.width -= 10;
                if (isShowCloseButton() && isShowCloseButtonOnTab()) {
                    if (_tabPane.isShowCloseButtonOnSelectedTab()) {
                        if (isSelected) {
                            textRect.width -= _closeButtons[tabIndex].getPreferredSize().width;
                        }
                    }
                    else {
                        textRect.width -= _closeButtons[tabIndex].getPreferredSize().width;
                    }
                }
            }
            else if (getTabResizeMode() == JideTabbedPane.RESIZE_MODE_COMPRESSED && isShowCloseButton() && isShowCloseButtonOnTab()) {
                if (!_tabPane.isShowCloseButtonOnSelectedTab()) {
                    if (!isSelected) {
                        iconRect.width = iconRect.width
                                + _closeButtons[tabIndex].getPreferredSize().width
                                + _closeButtonMarginSize;
                        textRect.width = 0;
                    }
                }
            }
        }
        else {
            iconRect.y = tabRect.y + _iconMarginVertical;
            textRect.y = (icon != null ? iconRect.y + iconRect.height
                    + _textIconGap : tabRect.y + _textPadding);
            iconRect.x = tabRect.x + 3;
            textRect.x = tabRect.x + 3;
            textRect.width = tabRect.width - _textMarginVertical;
            textRect.height = tabRect.height - _tabRectPadding - iconRect.height - (icon != null ? _textIconGap : _noIconMargin);

            if ((getTabResizeMode() == JideTabbedPane.RESIZE_MODE_FIT || _tabPane
                    .getTabResizeMode() == JideTabbedPane.RESIZE_MODE_FIXED)) {
                textRect.height -= 10;
                if (isShowCloseButton() && isShowCloseButtonOnTab()) {
                    if (_tabPane.isShowCloseButtonOnSelectedTab()) {
                        if (isSelected) {
                            textRect.height -= _closeButtons[tabIndex].getPreferredSize().height;
                        }
                    }
                    else {
                        textRect.height -= _closeButtons[tabIndex].getPreferredSize().height;
                    }
                }
            }
            else if (getTabResizeMode() == JideTabbedPane.RESIZE_MODE_COMPRESSED
                    && isShowCloseButton() && isShowCloseButtonOnTab()) {
                if (!_tabPane.isShowCloseButtonOnSelectedTab()) {
                    if (!isSelected) {
                        iconRect.height = iconRect.height + _closeButtons[tabIndex].getPreferredSize().height + _closeButtonMarginSize;
                        textRect.height = 0;
                    }
                }

            }
        }

    }


    /**
     * this function draws the border around each tab note that this function does now draw the background of the tab.
     * that is done elsewhere
     */
    @Override
    protected void paintTabBorder(Graphics g, int tabPlacement, int tabIndex,
                                  int x, int y, int w, int h, boolean isSelected) {
        if (!PAINT_TAB_BORDER) {
            return;
        }

        if (getTabShape() == JideTabbedPane.SHAPE_ECLIPSE3X) {
            g.setColor(_lightHighlight);
            boolean leftToRight = _tabPane.getComponentOrientation().isLeftToRight();
            switch (tabPlacement) {
                case LEFT:
                    if (!isTabLeadingComponentVisible())
                        y--;

                    if (isSelected) {
                        g.setColor(_shadow);
                        g.drawLine(x + 5, y, x + w - 1, y);// top

                        // top left arc
                        g.drawLine(x + 4, y + 1, x + 3, y + 1);
                        g.drawLine(x + 2, y + 2, x + 2, y + 2);
                        g.drawLine(x + 1, y + 3, x + 1, y + 3);
                        g.drawLine(x, y + 4, x, y + 5);

                        if (isTabTopVisible(tabPlacement)) {
                            g.drawLine(x, y + 5, x, y + h - 21);
                        }

                        // bottom left arc
                        g.drawLine(x, y + h - 21, x, y + h - 19);
                        g.drawLine(x + 1, y + h - 18, x + 1, y + h - 16);
                        g.drawLine(x + 2, y + h - 15, x + 2, y + h - 14);
                        g.drawLine(x + 3, y + h - 13, x + 3, y + h - 13);
                        g.drawLine(x + 4, y + h - 12, x + 4, y + h - 11);

                        // bottom
                        for (int i = 0; i < w - 10; i++) {
                            g.drawLine(x + 5 + i, y + h - 10 + i, x + 5 + i, y + h - 10 + i);
                        }

                        // bottom right arc
                        g.drawLine(x + w - 5, y + h + w - 20, x + w - 5, y + h + w - 19);
                        g.drawLine(x + w - 4, y + h + w - 18, x + w - 4, y + h + w - 18);
                        g.drawLine(x + w - 3, y + h + w - 17, x + w - 3, y + h + w - 16);
                        g.drawLine(x + w - 2, y + h + w - 15, x + w - 2, y + h + w - 13);
                        g.drawLine(x + w - 1, y + h + w - 12, x + w - 1, y + h + w - 10);

                        if (!isTabLeadingComponentVisible())
                            y++;
                        break;
                    }

                    // not selected
                    if (tabIndex > _tabPane.getSelectedIndex()) {
                        g.setColor(_shadow);
                        g.drawLine(x, y + h - 1, (x + w) - 1, y + h - 1);// bottom

                        break;
                    }

                    if (tabIndex >= _tabPane.getSelectedIndex() || tabIndex == 0)
                        break;

                    g.setColor(_shadow);
                    g.drawLine(x, y - 1, x + w - 1, y - 1);// top

                    break;
                case RIGHT:
                    if (!isTabLeadingComponentVisible())
                        y--;

                    if (isSelected) {
                        g.setColor(_shadow);

                        g.drawLine(x, y, x + w - 6, y);// top

                        // top right arc
                        g.drawLine(x + w - 5, y + 1, x + w - 4, y + 1);
                        g.drawLine(x + w - 3, y + 2, x + w - 3, y + 2);
                        g.drawLine(x + w - 2, y + 3, x + w - 2, y + 3);
                        g.drawLine(x + w - 1, y + 4, x + w - 1, y + 5);

                        if (isTabTopVisible(tabPlacement)) {
                            g.drawLine(x + w - 1, y + 5, x + w - 1, y + h - 21);
                        }

                        // bottom right arc
                        g.drawLine(x + w - 1, y + h - 21, x + w - 1, y + h - 19);
                        g.drawLine(x + w - 2, y + h - 18, x + w - 2, y + h - 16);
                        g.drawLine(x + w - 3, y + h - 15, x + w - 3, y + h - 14);
                        g.drawLine(x + w - 4, y + h - 13, x + w - 4, y + h - 13);
                        g.drawLine(x + w - 5, y + h - 12, x + w - 5, y + h - 11);

                        // bottom
                        for (int i = 0; i < w - 10; i++) {
                            g.drawLine(x + w - 6 - i, y + h - 10 + i, x + w - 6 - i, y + h - 10 + i);
                        }

                        // bottom left arc
                        g.drawLine(x + 4, y + h + w - 20, x + 4, y + h + w - 19);
                        g.drawLine(x + 3, y + h + w - 18, x + 3, y + h + w - 18);
                        g.drawLine(x + 2, y + h + w - 17, x + 2, y + h + w - 16);
                        g.drawLine(x + 1, y + h + w - 15, x + 1, y + h + w - 13);
                        g.drawLine(x, y + h + w - 12, x, y + h + w - 10);
                        break;

                    }

                    // not selected
                    if (tabIndex > _tabPane.getSelectedIndex()) {
                        g.setColor(_shadow);
                        g.drawLine(x, y + h - 1, (x + w) - 1, y + h - 1);// bottom

                        break;
                    }

                    if (tabIndex >= _tabPane.getSelectedIndex() || tabIndex == 0)
                        break;

                    g.setColor(_shadow);
                    g.drawLine(x, y - 1, x + w - 1, y - 1);// top

                    break;
                case BOTTOM:
                    if (!isTabLeadingComponentVisible()) {
                        x--;
                    }
                    if (isSelected) {

                        g.setColor(_shadow);

                        g.drawLine(x, y + h - 6, x, y);// left

                        // left bottom arc
                        g.drawLine(x + 1, y + h - 5, x + 1, y + h - 4);
                        g.drawLine(x + 2, y + h - 3, x + 2, y + h - 3);
                        g.drawLine(x + 3, y + h - 2, x + 3, y + h - 2);
                        g.drawLine(x + 4, y + h - 1, x + 5, y + h - 1);

                        if (isTabTopVisible(tabPlacement)) {
                            g.drawLine(x + 5, y + h - 1, x + w - 20, y + h - 1);
                        }

                        // right bottom arc
                        g.drawLine(x + w - 20, y + h - 1, x + w - 18, y + h - 1);
                        g.drawLine(x + w - 17, y + h - 2, x + w - 15, y + h - 2);
                        g.drawLine(x + w - 14, y + h - 3, x + w - 13, y + h - 3);
                        g.drawLine(x + w - 12, y + h - 4, x + w - 12, y + h - 4);
                        g.drawLine(x + w - 11, y + h - 5, x + w - 10, y + h - 5);

                        // right
                        for (int i = 0; i < h - 10; i++) {
                            g.drawLine(x + w - 9 + i, y + h - 6 - i, x + w - 9 + i, y + h - 6 - i);
                        }

                        // right top arc
                        g.drawLine(x + w + h - 19, y + 4, x + w + h - 18, y + 4);
                        g.drawLine(x + w + h - 17, y + 3, x + w + h - 17, y + 3);
                        g.drawLine(x + w + h - 16, y + 2, x + w + h - 15, y + 2);
                        g.drawLine(x + w + h - 14, y + 1, x + w + h - 12, y + 1);
                        g.drawLine(x + w + h - 11, y, x + w + h - 9, y);
                        break;
                    }

                    // not selected
                    if (tabIndex > _tabPane.getSelectedIndex()) {
                        g.setColor(_shadow);
                        if (leftToRight) {
                            g.drawLine(x + w - 2, y - 1, x + w - 2, y + h);// right
                        }
                        else {
                            g.drawLine(x, y - 1, x, y + h);// right
                        }
                        break;
                    }

                    if (tabIndex >= _tabPane.getSelectedIndex() || tabIndex == 0)
                        break;

                    g.setColor(_shadow);
                    if (leftToRight) {
                        g.drawLine(x, y - 1, x, y + h);// left
                    }
                    else {
                        g.drawLine(x + w - 2, y - 1, x + w - 2, y + h);// left
                    }
                    break;
                case TOP:
                default:
                    if (!isTabLeadingComponentVisible())
                        x--;

                    if (isSelected) {
                        g.setColor(_shadow);

                        g.drawLine(x, y + 5, x, y + h);// left

                        // left top arc
                        g.drawLine(x + 4, y, x + 5, y);
                        g.drawLine(x + 3, y + 1, x + 3, y + 1);
                        g.drawLine(x + 2, y + 2, x + 2, y + 2);
                        g.drawLine(x + 1, y + 3, x + 1, y + 4);

                        if (isTabTopVisible(tabPlacement)) {
                            g.drawLine(x + 5, y, x + w - 20, y);
                        }

                        // right top arc
                        g.drawLine(x + w - 20, y, x + w - 18, y);
                        g.drawLine(x + w - 17, y + 1, x + w - 15, y + 1);
                        g.drawLine(x + w - 14, y + 2, x + w - 13, y + 2);
                        g.drawLine(x + w - 12, y + 3, x + w - 12, y + 3);
                        g.drawLine(x + w - 11, y + 4, x + w - 10, y + 4);

                        // right
                        for (int i = 0; i < h - 10; i++) {
                            g.drawLine(x + w - 9 + i, y + 5 + i, x + w - 9 + i, y + 5 + i);
                        }

                        // right bottom arc
                        g.drawLine(x + w + h - 19, y + h - 5, x + w + h - 18, y + h - 5);
                        g.drawLine(x + w + h - 17, y + h - 4, x + w + h - 17, y + h - 4);
                        g.drawLine(x + w + h - 16, y + h - 3, x + w + h - 15, y + h - 3);
                        g.drawLine(x + w + h - 14, y + h - 2, x + w + h - 12, y + h - 2);
                        g.drawLine(x + w + h - 11, y + h - 1, x + w + h - 9, y + h - 1);
                        break;
                    }

                    if (tabIndex > _tabPane.getSelectedIndex()) {
                        g.setColor(_shadow);
                        if (leftToRight) {
                            g.drawLine(x + w - 2, y, x + w - 2, y + (h - 1));// right
                        }
                        else {
                            g.drawLine(x, y, x, y + (h - 1));// left
                        }
                        break;
                    }

                    if (tabIndex >= _tabPane.getSelectedIndex() || tabIndex == 0)
                        break;
                    g.setColor(_shadow);

                    if (leftToRight) {
                        g.drawLine(x, y, x, y + (h - 1));// left
                    }
                    else {
                        g.drawLine(x + w - 2, y, x + w - 2, y + (h - 1));// left
                    }
                    break;
            }
        }
        else {
            super.paintTabBorder(g, tabPlacement, tabIndex, x, y, w, h, isSelected);
        }
    }

    @Override
    protected void paintTabBackground(Graphics g, int tabPlacement,
                                      int tabIndex, int x, int y, int w, int h, boolean isSelected) {
        if (!PAINT_TAB_BACKGROUND) {
            return;
        }

        if (!isSelected) {
            return;
        }

        if (getTabShape() == JideTabbedPane.SHAPE_ECLIPSE3X) {
            Graphics2D g2d = (Graphics2D) g;
            Color background1;
            Color background2;
            if (showFocusIndicator()) {
                background1 = _activeBackground;
                background2 = UIDefaultsLookup.getColor("DockableFrame.activeTitleBackground2");
            }
            else {
                background1 = _activeBackground;
                background2 = _background;
            }

            switch (tabPlacement) {
                case LEFT: {
                    if (!isTabLeadingComponentVisible())
                        y--;
                    int[] xp = {x + w, x + 5, x, x, x + 6, x + w - 6, x + w};
                    int[] yp = {y, y, y + 4, y + h - 19, y + h - 8,
                            y + h + w - 21, y + h + w - 10};
                    int np = yp.length;
                    Polygon p = new Polygon(xp, yp, np);
                    JideSwingUtilities.fillGradient(g2d, p, background1, background2, false);
                }
                break;
                case RIGHT: {
                    if (!isTabLeadingComponentVisible())
                        y--;
                    int[] xp = {x, x + w - 6, x + w, x + w, x + w - 5, x + 5, x};
                    int[] yp = {y, y, y + 4, y + h - 21, y + h - 10,
                            y + h + w - 21, y + h + w - 10};
                    int np = yp.length;
                    Polygon p = new Polygon(xp, yp, np);
                    JideSwingUtilities.fillGradient(g2d, p, background2, background1, false);
                }
                break;
                case BOTTOM: {
                    if (!isTabLeadingComponentVisible())
                        x--;
                    // not box style
                    int[] xp = {x, x, x + 6, x + w - 20, x + w - 16, x + w - 14,
                            x + w - 12, x + w - 9, x + w + h - 19, x + w + h - 10,
                            x + w + h - 12};
                    int[] yp = {y, y + h - 6, y + h, y + h, y + h - 2, y + h - 3,
                            y + h - 4, y + h - 6, y + 4, y + 1, y};
                    int np = yp.length;
                    Polygon p = new Polygon(xp, yp, np);
                    JideSwingUtilities.fillGradient(g2d, p, background2, background1, true);
                }
                break;
                case TOP:
                default: {
                    if (!isTabLeadingComponentVisible())
                        x--;
                    int[] xp = {x, x, x + 2, x + 3, x + 6, x + w - 20, x + w - 14,
                            x + w - 12, x + w - 9, x + w + h - 20, x + w + h - 9};
                    int[] yp = {y + h, y + 5, y + 2, y + 1, y, y, y + 2, y + 3,
                            y + 5, y + h - 6, y + h};
                    int np = xp.length;
                    Polygon p = new Polygon(xp, yp, np);
                    JideSwingUtilities.fillGradient(g2d, p, background1, background2, true);
                }
                break;
            }
        }
        else {
            super.paintTabBackground(g, tabPlacement, tabIndex, x, y, w, h, isSelected);
        }
    }


    @Override
    protected void paintContentBorder(Graphics g, int tabPlacement, int selectedIndex) {
        if (selectedIndex < 0) {
            return;
        }

        if (getTabShape() == JideTabbedPane.SHAPE_ECLIPSE3X) {
            int width = _tabPane.getWidth();
            int height = _tabPane.getHeight();
            Insets insets = _tabPane.getInsets();

            int x = insets.left;
            int y = insets.top;
            int w = width - insets.right - insets.left;
            int h = height - insets.top - insets.bottom;

            int temp = -1;
            switch (tabPlacement) {
                case LEFT:
                    x += calculateTabAreaWidth(tabPlacement, _runCount, _maxTabWidth);
                    if (isTabLeadingComponentVisible()) {
                        if (_tabLeadingComponent.getSize().width > calculateTabAreaWidth(tabPlacement, _runCount, _maxTabWidth)) {
                            x = insets.left + _tabLeadingComponent.getSize().width;
                            temp = _tabLeadingComponent.getSize().width;
                        }
                    }
                    if (isTabTrailingComponentVisible()) {
                        if (_maxTabWidth < _tabTrailingComponent.getSize().width
                                && temp < _tabTrailingComponent.getSize().width) {
                            x = insets.left + _tabTrailingComponent.getSize().width;
                        }
                    }
                    w -= (x - insets.left);
                    break;
                case RIGHT:
                    w -= calculateTabAreaWidth(tabPlacement, _runCount, _maxTabWidth);
                    break;
                case BOTTOM:
                    h -= calculateTabAreaHeight(tabPlacement, _runCount, _maxTabHeight);
                    break;
                case TOP:
                default:
                    y += calculateTabAreaHeight(tabPlacement, _runCount, _maxTabHeight);
                    if (isTabLeadingComponentVisible()) {
                        if (_tabLeadingComponent.getSize().height > calculateTabAreaHeight(tabPlacement, _runCount, _maxTabHeight)) {
                            y = insets.top + _tabLeadingComponent.getSize().height;
                            temp = _tabLeadingComponent.getSize().height;
                        }
                    }
                    if (isTabTrailingComponentVisible()) {
                        if (_maxTabHeight < _tabTrailingComponent.getSize().height
                                && temp < _tabTrailingComponent.getSize().height) {
                            y = insets.top + _tabTrailingComponent.getSize().height;
                        }
                    }
                    h -= (y - insets.top);
            }

            // Fill region behind content area
            paintContentBorder(g, x, y, w, h);

            Rectangle viewRect = _tabScroller.viewport.getViewRect();
            Rectangle r = _rects[selectedIndex];
            Rectangle button = _tabScroller.scrollForwardButton.getBounds();
            Rectangle panel = _tabScroller.tabPanel.getBounds();
            int lsize = 0;

            if (_tabPane.getTabPlacement() == TOP || _tabPane.getTabPlacement() == BOTTOM) {
                if (isTabLeadingComponentVisible()) {
                    lsize = _tabLeadingComponent.getSize().width;
                }
            }
            else {
                if (isTabLeadingComponentVisible()) {
                    lsize = _tabLeadingComponent.getSize().height;
                }
            }


            switch (tabPlacement) {
                case LEFT:
                    if (r.y < viewRect.y + viewRect.height
                            && r.y + r.height
                            + _tabPane.getBoundsAt(selectedIndex).width - 9 > viewRect.y
                            + viewRect.height) {

                        if (selectedIndex != _tabPane.getTabCount() - 1) {
                            viewRect.y += (r.y + r.height + _tabPane.getBoundsAt(selectedIndex).width - 9 - (viewRect.y + viewRect.height));
                            _tabScroller.viewport.setViewPosition(new Point(
                                    viewRect.x, viewRect.y));
                        }
                        else {
                            if (panel.y + panel.height + lsize > button.y) {
                                viewRect.y += (r.y + r.height + _tabPane.getBoundsAt(selectedIndex).width - 9 - (viewRect.y + viewRect.height));
                                _tabScroller.viewport.setViewPosition(new Point(
                                        viewRect.x, viewRect.y));
                            }
                            else {
                                _tabScroller.viewport.setSize(viewRect.width, viewRect.height + getLayoutSize());
                            }

                        }

                    }
                    paintContentBorderLeftEdge(g, tabPlacement, selectedIndex, x, y, w, h);
                    break;

                case RIGHT:
                    if (r.y < viewRect.y + viewRect.height
                            && r.y + r.height
                            + _tabPane.getBoundsAt(selectedIndex).width - 9 > viewRect.y
                            + viewRect.height) {

                        if (selectedIndex != _tabPane.getTabCount() - 1) {
                            viewRect.y += (r.y + r.height + _tabPane.getBoundsAt(selectedIndex).width - 9 - (viewRect.y + viewRect.height));
                            _tabScroller.viewport.setViewPosition(new Point(viewRect.x, viewRect.y));
                        }
                        else {
                            if (panel.y + panel.height + lsize > button.y) {
                                viewRect.y += (r.y + r.height + _tabPane.getBoundsAt(selectedIndex).width - 9 - (viewRect.y + viewRect.height));
                                _tabScroller.viewport.setViewPosition(new Point(viewRect.x, viewRect.y));
                            }
                            else {
                                _tabScroller.viewport.setSize(viewRect.width, viewRect.height + getLayoutSize());
                            }

                        }

                    }
                    paintContentBorderRightEdge(g, tabPlacement, selectedIndex, x, y, w, h);
                    break;

                case BOTTOM:
                    if (r.x < viewRect.x + viewRect.width
                            && r.x + r.width
                            + _tabPane.getBoundsAt(selectedIndex).height
                            - 9 > viewRect.x + viewRect.width) {
                        if (selectedIndex != _tabPane.getTabCount() - 1) {
                            viewRect.x += (r.x + r.width + _tabPane.getBoundsAt(selectedIndex).height - 9 - (viewRect.x + viewRect.width));
                            _tabScroller.viewport.setViewPosition(new Point(
                                    viewRect.x, viewRect.y));
                        }
                        else {
                            if (panel.x + panel.width + lsize > button.x) {
                                viewRect.x += (r.x + r.width + _tabPane.getBoundsAt(selectedIndex).height - 9 - (viewRect.x + viewRect.width));
                                _tabScroller.viewport.setViewPosition(new Point(viewRect.x, viewRect.y));
                            }
                            else {
                                _tabScroller.viewport.setSize(viewRect.width + getLayoutSize(), viewRect.height);
                            }

                        }

                    }
                    paintContentBorderBottomEdge(g, tabPlacement, selectedIndex, x, y, w, h);
                    break;

                case TOP:
                default:

                    if (r.x < viewRect.x + viewRect.width && r.x + r.width + _tabPane.getBoundsAt(selectedIndex).height - 9 > viewRect.x + viewRect.width) {
                        if (selectedIndex != _tabPane.getTabCount() - 1) {
                            viewRect.x += (r.x + r.width + _tabPane.getBoundsAt(selectedIndex).height - 9 - (viewRect.x + viewRect.width));
                            _tabScroller.viewport.setViewPosition(new Point(viewRect.x, viewRect.y));
                        }
                        else {
                            if (panel.x + panel.width + lsize > button.x) {
                                viewRect.x += (r.x + r.width + _tabPane.getBoundsAt(selectedIndex).height - 9 - (viewRect.x + viewRect.width));
                                _tabScroller.viewport.setViewPosition(new Point(viewRect.x, viewRect.y));

                            }
                            else {
                                _tabScroller.viewport.setSize(viewRect.width + getLayoutSize(), viewRect.height);
                            }
                        }
                    }

                    paintContentBorderTopEdge(g, tabPlacement, selectedIndex, x, y, w, h);
                    break;

            }

            g.setColor(_shadow);
            if (_tabPane.isTabShown()) {
                switch (tabPlacement) {
                    case LEFT:
                        g.drawLine(width - 1, 0, width - 1, height - 1);
                        g.drawLine(6, 0, width - 1, 0);
                        g.drawLine(6, height - 1, width - 1, height - 1);
                        g.drawLine(0, 6, 0, height - 7);

                        g.drawLine(1, height - 6, 1, height - 5);
                        g.drawLine(2, height - 4, 2, height - 4);
                        g.drawLine(3, height - 3, 3, height - 3);
                        g.drawLine(4, height - 2, 5, height - 2);

                        g.drawLine(4, 1, 5, 1);
                        g.drawLine(3, 2, 3, 2);
                        g.drawLine(2, 3, 2, 3);
                        g.drawLine(1, 4, 1, 5);
                        break;
                    case RIGHT:
                        g.drawLine(0, 0, 0, height - 1);
                        g.drawLine(0, 0, width - 7, 0);
                        g.drawLine(0, height - 1, width - 7, height - 1);
                        g.drawLine(width - 1, 6, width - 1, height - 7);

                        g.drawLine(width - 2, height - 6, width - 2, height - 5);
                        g.drawLine(width - 3, height - 4, width - 3, height - 4);
                        g.drawLine(width - 4, height - 3, width - 4, height - 3);
                        g.drawLine(width - 5, height - 2, width - 6, height - 2);

                        g.drawLine(width - 6, 1, width - 5, 1);
                        g.drawLine(width - 4, 2, width - 4, 2);
                        g.drawLine(width - 3, 3, width - 3, 3);
                        g.drawLine(width - 2, 4, width - 2, 5);
                        break;
                    case BOTTOM:
                        g.drawLine(0, 0, width - 1, 0);
                        g.drawLine(0, 0, 0, height - 7);
                        g.drawLine(width - 1, 0, width - 1, height - 7);
                        g.drawLine(6, height - 1, width - 7, height - 1);

                        g.drawLine(width - 6, height - 2, width - 5, height - 2);
                        g.drawLine(width - 4, height - 3, width - 4, height - 3);
                        g.drawLine(width - 3, height - 4, width - 3, height - 4);
                        g.drawLine(width - 2, height - 5, width - 2, height - 6);

                        g.drawLine(1, height - 6, 1, height - 5);
                        g.drawLine(2, height - 4, 2, height - 4);
                        g.drawLine(3, height - 3, 3, height - 3);
                        g.drawLine(4, height - 2, 5, height - 2);
                        break;
                    case TOP:
                    default:
                        g.drawLine(6, 0, width - 7, 0);
                        g.drawLine(0, height - 1, width - 1, height - 1);
                        g.drawLine(width - 1, 6, width - 1, height - 1);
                        g.drawLine(0, 6, 0, height - 1);

                        g.drawLine(width - 6, 1, width - 5, 1);
                        g.drawLine(width - 4, 2, width - 4, 2);
                        g.drawLine(width - 3, 3, width - 3, 3);
                        g.drawLine(width - 2, 4, width - 2, 5);

                        g.drawLine(4, 1, 5, 1);
                        g.drawLine(3, 2, 3, 2);
                        g.drawLine(2, 3, 2, 3);
                        g.drawLine(1, 4, 1, 5);
                        break;
                }
            }
            else {
                g.drawRect(0, 0, width - 1, height - 1);
            }
        }
        else {
            super.paintContentBorder(g, tabPlacement, selectedIndex);
        }

    }

    // paint the component border of every tab

    @Override
    protected void paintContentBorder(Graphics g, int x, int y, int w, int h) {
        if (!PAINT_CONTENT_BORDER) {
            return;
        }

        if (getTabShape() == JideTabbedPane.SHAPE_ECLIPSE3X) {
            if (showFocusIndicator()) {
                Insets insets = getContentBorderInsets(_tabPane.getTabPlacement());
                Color selectedTitleColor2 = UIDefaultsLookup.getColor("DockableFrame.activeTitleBackground2");
                g.setColor(selectedTitleColor2);
                g.fillRect(x, y, w, insets.top); // top
                g.fillRect(x, y, insets.left, h); // left
                g.fillRect(x, y + h - insets.bottom, w, insets.bottom); // bottom
                g.fillRect(x + w - insets.right, y, insets.right, h); // right
            }
        }
        else {
            super.paintContentBorder(g, x, y, w, h);
        }
    }

    // paint the top line of the content when the tab is on the top

    @Override
    protected void paintContentBorderTopEdge(Graphics g, int tabPlacement,
                                             int selectedIndex, int x, int y, int w, int h) {
        if (!PAINT_CONTENT_BORDER_EDGE) {
            return;
        }

        if (selectedIndex < 0) {
            return;
        }

        if (getTabShape() == JideTabbedPane.SHAPE_ECLIPSE3X) {
            Rectangle selRect = getTabBounds(selectedIndex, _calcRect);

            Rectangle viewRect = _tabScroller.viewport.getViewRect();
            Rectangle r = _rects[selectedIndex];

            g.setColor(getPainter().getControlShadow());

            // Break line to show visual connection to selected tab
            if (isTabLeadingComponentVisible() && selRect.x > 0) {
                g.drawLine(x, y, selRect.x, y);
            }

            if (r.x > viewRect.x) {
                g.drawLine(x, y, selRect.x - 1, y);
            }

            if (_tabPane.isTabShown()) {
                if (r.x >= viewRect.x + viewRect.width) {
                    g.drawLine(x, y, x + w - 1, y);
                }
                else {
                    g.drawLine(selRect.x + selRect.width + _tabPane.getBoundsAt(selectedIndex).height - 9, y, x + w - 1, y);
                }
            }
        }
        else {
            super.paintContentBorderTopEdge(g, tabPlacement, selectedIndex, x, y, w, h);
        }
    }

    // paint the bottom line of the content when the tab is on the bottom

    @Override
    protected void paintContentBorderBottomEdge(Graphics g, int tabPlacement,
                                                int selectedIndex, int x, int y, int w, int h) {
        if (!PAINT_CONTENT_BORDER_EDGE) {
            return;
        }

        if (selectedIndex < 0) {
            return;
        }

        if (getTabShape() == JideTabbedPane.SHAPE_ECLIPSE3X) {
            Rectangle selRect = getTabBounds(selectedIndex, _calcRect);

            g.setColor(getPainter().getControlShadow());

            Rectangle viewRect = _tabScroller.viewport.getViewRect();
            Rectangle r = _rects[selectedIndex];

            if (isTabLeadingComponentVisible() && selRect.x > 0) {
                g.drawLine(x, y + h - 1, selRect.x, y + h - 1);
            }

            // Break line to show visual connection to selected tab
            if (r.x > viewRect.x) {
                g.drawLine(x, y + h - 1, selRect.x - 1, y + h - 1);
            }

            if (_tabPane.isTabShown()) {
                if (r.x >= viewRect.x + viewRect.width) {
                    g.drawLine(x, y + h - 1, x + w - 1, y + h - 1);
                }
                else {
                    g.drawLine(selRect.x + selRect.width + _tabPane.getBoundsAt(selectedIndex).height - 9, y + h - 1, x + w - 1, y + h - 1);
                }
            }
        }
        else {
            super.paintContentBorderBottomEdge(g, tabPlacement, selectedIndex, x, y, w, h);
        }

    }

//  paint the left line of the content when the tab is on the left

    @Override
    protected void paintContentBorderLeftEdge(Graphics g, int tabPlacement,
                                              int selectedIndex, int x, int y, int w, int h) {
        if (!PAINT_CONTENT_BORDER_EDGE) {
            return;
        }

        if (selectedIndex < 0) {
            return;
        }

        if (getTabShape() == JideTabbedPane.SHAPE_ECLIPSE3X) {
            Rectangle selRect = getTabBounds(selectedIndex, _calcRect);

            Rectangle viewRect = _tabScroller.viewport.getViewRect();
            Rectangle r = _rects[selectedIndex];

            g.setColor(getPainter().getControlShadow());

            if (isTabLeadingComponentVisible() && selRect.y > 0) {
                g.drawLine(x, y, x, selRect.y);
            }

            // Break line to show visual connection to selected tab
            if (r.y - 2 > viewRect.y) {
                g.drawLine(x, y, x, selRect.y - 3);
            }

            if (_tabPane.isTabShown()) {
                if (r.y >= viewRect.y + viewRect.height) {
                    g.drawLine(x, y, x, y + h - 1);
                }
                else {
                    g.drawLine(x, selRect.y + selRect.height + _tabPane.getBoundsAt(selectedIndex).width - 9, x, y + h - 1);
                }
            }
        }
        else {
            super.paintContentBorderLeftEdge(g, tabPlacement, selectedIndex, x, y, w, h);
        }
    }

//  paint the right line of the content when the tab is on the right

    @Override
    protected void paintContentBorderRightEdge(Graphics g, int tabPlacement,
                                               int selectedIndex, int x, int y, int w, int h) {
        if (!PAINT_CONTENT_BORDER_EDGE) {
            return;
        }

        if (selectedIndex < 0) {
            return;
        }

        if (getTabShape() == JideTabbedPane.SHAPE_ECLIPSE3X) {
            Rectangle selRect = getTabBounds(selectedIndex, _calcRect);

            Rectangle viewRect = _tabScroller.viewport.getViewRect();
            Rectangle r = _rects[selectedIndex];

            g.setColor(getPainter().getControlShadow());

            if (isTabLeadingComponentVisible() && selRect.y > 0) {
                g.drawLine(x + w - 1, y, x + w - 1, selRect.y);
            }

            // Break line to show visual connection to selected tab
            if (r.y - 2 > viewRect.y) {
                g.drawLine(x + w - 1, y, x + w - 1, selRect.y - 3);
            }

            if (_tabPane.isTabShown()) {
                if (r.y >= viewRect.y + viewRect.height) {
                    g.drawLine(x + w - 1, y, x + w - 1, y + h - 1);
                }
                else {
                    g.drawLine(x + w - 1, selRect.y + selRect.height + _tabPane.getBoundsAt(selectedIndex).width - 9, x + w - 1, y + h - 1);
                }
            }
        }
        else {
            super.paintContentBorderRightEdge(g, tabPlacement, selectedIndex, x, y, w, h);
        }
    }


    @Override
    protected Rectangle getTabsTextBoundsAt(int tabIndex) {
        Rectangle tabRect = _tabPane.getBoundsAt(tabIndex);
        Rectangle iconRect = new Rectangle(), textRect = new Rectangle();

        String title = _tabPane.getDisplayTitleAt(tabIndex);
        Icon icon = _tabPane.getIconForTab(tabIndex);

        SwingUtilities.layoutCompoundLabel(_tabPane, _tabPane.getGraphics()
                .getFontMetrics(_tabPane.getFont()), title, icon,
                SwingUtilities.CENTER, SwingUtilities.CENTER,
                SwingUtilities.CENTER, SwingUtilities.TRAILING, tabRect,
                iconRect, textRect, icon == null ? 0 : _textIconGap);

        if (_tabPane.getTabPlacement() == JideTabbedPane.TOP || _tabPane.getTabPlacement() == JideTabbedPane.BOTTOM) {
            iconRect.x = tabRect.x + _iconMarginHorizon;
            textRect.x = (icon != null ? iconRect.x + iconRect.width + _textIconGap : tabRect.x + _textPadding);
        }
        else {
            iconRect.y = tabRect.y + _iconMarginVertical;
            textRect.y = (icon != null ? iconRect.y + iconRect.height + _textIconGap : tabRect.y + _textPadding);
            iconRect.x = tabRect.x + 2;
            textRect.x = tabRect.x + 2;
        }

        return textRect;
    }


    @Override
    protected void paintFocusIndicator(Graphics g, int tabPlacement,
                                       Rectangle[] rects, int tabIndex, Rectangle iconRect,
                                       Rectangle textRect, boolean isSelected) {
        Rectangle tabRect = rects[tabIndex];
        if (_tabPane.hasFocus() && isSelected) {
            int x, y, w, h;
            g.setColor(_focus);
            switch (tabPlacement) {
                case LEFT:
                    x = tabRect.x + 2;
                    y = tabRect.y + 3;
                    w = tabRect.width - 4;
                    h = tabRect.height - 19;
                    break;
                case RIGHT:
                    x = tabRect.x + 2;
                    y = tabRect.y + 3;
                    w = tabRect.width - 4;
                    h = tabRect.height - 19;
                    break;
                case BOTTOM:
                    x = tabRect.x + 3;
                    y = tabRect.y + 2;
                    w = tabRect.width - 19;
                    h = tabRect.height - 3;
                    break;
                case TOP:
                default:
                    x = tabRect.x + 3;
                    y = tabRect.y + 2;
                    w = tabRect.width - 19;
                    h = tabRect.height - 3;
            }
            BasicGraphicsUtils.drawDashedRect(g, x, y, w, h);
        }
    }

    @Override
    protected TabCloseButton createNoFocusButton(int type) {
        return new Eclipse3xTabCloseButton(type);
    }

    public class Eclipse3xTabCloseButton extends TabCloseButton {
        public Eclipse3xTabCloseButton(int type) {
            super(type);
        }

        @Override
        public Dimension getPreferredSize() {
            return new Dimension(15, 15);
        }

        @Override
        protected void paintComponent(Graphics g) {
            if (!isEnabled()) {
                setMouseOver(false);
                setMousePressed(false);
            }
            g.setColor(UIDefaultsLookup.getColor("controlShadow").darker());
            int centerX = getWidth() >> 1;
            int centerY = getHeight() >> 1;
            switch (getType()) {
                case CLOSE_BUTTON:
                    g.drawLine(centerX - 4, centerY - 4, centerX - 2, centerY - 4); // top-left top
                    g.drawLine(centerX - 4, centerY - 4, centerX - 4, centerY - 2); // top-left left

                    g.drawLine(centerX - 1, centerY - 3, centerX - 0, centerY - 2); // top-left top-diag
                    g.drawLine(centerX - 3, centerY - 1, centerX - 2, centerY - 0); // top-left left-diag

                    g.drawLine(centerX + 3, centerY - 4, centerX + 5, centerY - 4); // top-right top
                    g.drawLine(centerX + 5, centerY - 4, centerX + 5, centerY - 2); // top-right right

                    g.drawLine(centerX + 2, centerY - 3, centerX + 1, centerY - 2); // top-right top-diag
                    g.drawLine(centerX + 4, centerY - 1, centerX + 3, centerY - 0); // top-right right-diag

                    g.drawLine(centerX - 4, centerY + 5, centerX - 2, centerY + 5); // bottom-left bottom
                    g.drawLine(centerX - 4, centerY + 5, centerX - 4, centerY + 3); // bottom-left left

                    g.drawLine(centerX - 1, centerY + 4, centerX - 0, centerY + 3); // bottom-left bottom-diag
                    g.drawLine(centerX - 3, centerY + 2, centerX - 2, centerY + 1); // bottom-left left-diag

                    g.drawLine(centerX + 3, centerY + 5, centerX + 5, centerY + 5); // bottom-right bottom
                    g.drawLine(centerX + 5, centerY + 5, centerX + 5, centerY + 3); // bottom-right right

                    g.drawLine(centerX + 2, centerY + 4, centerX + 1, centerY + 3); // bottom-right bottom-diag
                    g.drawLine(centerX + 4, centerY + 2, centerX + 3, centerY + 1); // bottom-right right-diag

                    if (isMouseOver()) {
                        g.setColor(new Color(252, 160, 160));
                    }
                    else {
                        g.setColor(Color.WHITE);
                    }
                    g.drawLine(centerX - 2, centerY - 3, centerX + 4, centerY + 3);
                    g.drawLine(centerX - 3, centerY - 3, centerX + 4, centerY + 4);
                    g.drawLine(centerX - 3, centerY - 2, centerX + 3, centerY + 4);

                    g.drawLine(centerX - 3, centerY + 3, centerX + 3, centerY - 3);
                    g.drawLine(centerX - 3, centerY + 4, centerX + 4, centerY - 3);
                    g.drawLine(centerX - 2, centerY + 4, centerX + 4, centerY - 2);
                    break;
                default:
                    super.paintComponent(g);
            }
        }
    }
}
