/*
 * @(#)WindowsTabbedPaneUI.java
 *
 * Copyright 2002 JIDE Software Inc. All rights reserved.
 */

package com.jidesoft.plaf.eclipse;

import com.jidesoft.plaf.UIDefaultsLookup;
import com.jidesoft.plaf.vsnet.VsnetJideTabbedPaneUI;
import com.jidesoft.swing.JideSwingUtilities;
import com.jidesoft.swing.JideTabbedPane;

import javax.swing.*;
import javax.swing.plaf.ComponentUI;
import java.awt.*;


/**
 * JideTabbedPane UI implementation
 */
public class EclipseJideTabbedPaneUI extends VsnetJideTabbedPaneUI {

    public static ComponentUI createUI(JComponent c) {
        return new EclipseJideTabbedPaneUI();
    }

    /**
     * this function draws the border around each tab
     * note that this function does now draw the background of the tab.
     * that is done elsewhere
     */
    @Override
    protected void paintTabBorder(Graphics g, int tabPlacement, int tabIndex, int x, int y, int w, int h, boolean isSelected) {
        if (getTabShape() == JideTabbedPane.SHAPE_ECLIPSE) {
            Color old = g.getColor();

            boolean leftToRight = _tabPane.getComponentOrientation().isLeftToRight();

            switch (tabPlacement) {
                case LEFT:
                    if (isSelected) {
                        g.setColor(_lightHighlight);
                        g.drawRect(x + 1, y + 1, w - 1, h - 3);

                        g.setColor(_shadow);
                        g.drawLine(x + 1, y, x + w - 1, y);
                        g.drawLine(x + 1, y + h - 1, x + w - 1, y + h - 1);
                        g.drawLine(x, y + 1, x, y + 1);
                        g.drawLine(x, y + h - 2, x, y + h - 2);
                    }
                    else {
                        g.setColor(_shadow);
                        if (tabIndex > _tabPane.getSelectedIndex()/* && tabIndex != _tabPane.getTabCount() - 1*/) {
                            if (tabIndex == _tabPane.getTabCount() - 1) {
                                g.drawLine(x, y + h - 1, x + w, y + h - 1); // bottom shadow
                            }
                            else {
                                g.drawLine(x, y + h - 1, x + w / 2, y + h - 1); // bottom shadow
                            }
                        }
                        else if (tabIndex < _tabPane.getSelectedIndex()) {
                            if (tabIndex != 0) {
                                g.drawLine(x, y, x + w / 2, y); // top shadow
                            }
                            else {
                                if (isTabLeadingComponentVisible()) {
                                    g.drawLine(x, y, x + w, y); // top shadow
                                }
                            }
                        }
                    }

                    if (isTabTopVisible(tabPlacement)) {
                        g.setColor(_shadow);
                        g.drawLine(x, y, x, y + h - 1); // tab top
                    }
                    break;
                case RIGHT:
                    if (isSelected) {
                        g.setColor(_lightHighlight);
                        g.drawRect(x, y + 1, w - 1, h - 3);

                        g.setColor(_shadow);
                        g.drawLine(x, y, x + w - 1, y);
                        g.drawLine(x, y + h - 1, x + w - 1, y + h - 1);
                        g.drawLine(x + w, y + 1, x + w, y + 1);
                        g.drawLine(x + w, y + h - 2, x + w, y + h - 2);
                    }
                    else {  // not selected
                        g.setColor(_shadow);
                        if (tabIndex > _tabPane.getSelectedIndex()) {
                            if (tabIndex == _tabPane.getTabCount() - 1) {
                                g.drawLine(x, y + h - 1, x + w, y + h - 1); // bottom shadow
                            }
                            else {
                                g.drawLine(x + w / 2, y + h - 1, x + w, y + h - 1); // bottom shadow
                            }
                        }
                        else if (tabIndex < _tabPane.getSelectedIndex()) {
                            if (tabIndex != 0) {
                                g.drawLine(x + w / 2, y, x + w, y); // top shadow
                            }
                            else {
                                if (isTabLeadingComponentVisible()) {
                                    g.drawLine(x, y, x + w, y); // top shadow
                                }
                            }
                        }
                    }

                    if (isTabTopVisible(tabPlacement)) {
                        g.setColor(_shadow);
                        g.drawLine(x + w, y, x + w, y + h - 1); // tab top
                    }
                    break;
                case BOTTOM:
                    if (isSelected) {
                        g.setColor(_lightHighlight);
                        g.drawRect(x + 1, y, w - 3, h - 1);

                        g.setColor(_shadow);
                        g.drawLine(x, y, x, y + h - 1);
                        g.drawLine(x + w - 1, y, x + w - 1, y + h - 1);
                        g.drawLine(x + 1, y + h, x + 1, y + h);
                        g.drawLine(x + w - 2, y + h, x + w - 2, y + h);
                    }
                    else {  // not selected
                        g.setColor(_shadow);
                        if (leftToRight) {
                            if (tabIndex > _tabPane.getSelectedIndex()) {
                                if (tabIndex == _tabPane.getTabCount() - 1) {
                                    g.drawLine(x + w - 1, y, x + w - 1, y + h); // right shadow
                                }
                                else {
                                    g.drawLine(x + w - 1, y + h / 2, x + w - 1, y + h); // right shadow
                                }
                            }
                            else if (tabIndex < _tabPane.getSelectedIndex()) {
                                if (tabIndex != 0) {
                                    g.drawLine(x, y + h / 2, x, y + h); // right shadow
                                }
                                else {
                                    if (isTabLeadingComponentVisible()) {
                                        g.drawLine(x, y, x, y + h); // right shadow
                                    }
                                }
                            }
                        }
                        else {
                            if (tabIndex > _tabPane.getSelectedIndex()) {
                                if (tabIndex == _tabPane.getTabCount() - 1) {
                                    g.drawLine(x, y, x, y + h); // right shadow
                                }
                                else {
                                    g.drawLine(x, y + h / 2, x, y + h); // right shadow
                                }
                            }
                            else if (tabIndex < _tabPane.getSelectedIndex()) {
                                if (tabIndex != 0) {
                                    g.drawLine(x + w - 1, y + h / 2, x + w - 1, y + h); // right shadow
                                }
                                else {
                                    if (isTabLeadingComponentVisible()) {
                                        g.drawLine(x + w - 1, y, x + w - 1, y + h); // right shadow
                                    }
                                }
                            }

                        }
                    }

                    if (isTabTopVisible(tabPlacement)) {
                        g.setColor(_shadow);
                        g.drawLine(x, y + h, x + w - 1, y + h); // tab top
                    }
                    break;
                case TOP:
                default:
                    if (isSelected) {
                        g.setColor(_lightHighlight);
                        g.drawRect(x + 1, y + 1, w - 3, h);

                        g.setColor(_shadow);
                        g.drawLine(x, y + 1, x, y + h - 1);
                        g.drawLine(x + w - 1, y + 1, x + w - 1, y + h - 1);
                        g.drawLine(x + 1, y, x + 1, y);
                        g.drawLine(x + w - 2, y, x + w - 2, y);
                    }
                    else {
                        g.setColor(_shadow);
                        if (leftToRight) {
                            if (tabIndex > _tabPane.getSelectedIndex()) {
                                if (tabIndex == _tabPane.getTabCount() - 1) {
                                    g.drawLine(x + w - 1, y, x + w - 1, y + h); // right shadow
                                }
                                else {
                                    g.drawLine(x + w - 1, y, x + w - 1, y + h / 2); // right shadow
                                }
                            }
                            else if (tabIndex < _tabPane.getSelectedIndex()) {
                                if (tabIndex != 0) {
                                    g.drawLine(x, y, x, y + h / 2); // right shadow
                                }
                                else {
                                    if (isTabLeadingComponentVisible()) {
                                        g.drawLine(x, y, x, y + h); // right shadow
                                    }
                                }
                            }
                        }
                        else {
                            if (tabIndex > _tabPane.getSelectedIndex()) {
                                if (tabIndex == _tabPane.getTabCount() - 1) {
                                    g.drawLine(x, y, x, y + h); // right shadow
                                }
                                else {
                                    g.drawLine(x, y, x, y + h / 2); // right shadow
                                }
                            }
                            else if (tabIndex < _tabPane.getSelectedIndex()) {
                                if (tabIndex != 0) {
                                    g.drawLine(x + w - 1, y, x + w - 1, y + h / 2); // right shadow
                                }
                                else {
                                    if (isTabLeadingComponentVisible()) {
                                        g.drawLine(x + w - 1, y, x + w - 1, y + h); // right shadow
                                    }
                                }
                            }
                        }
                    }

                    if (isTabTopVisible(tabPlacement)) {
                        g.setColor(_shadow);
                        g.drawLine(x, y, x + w - 1, y); // tab top
                    }
                    break;
            }
            g.setColor(old);

        }
        else {
            super.paintTabBorder(g, tabPlacement, tabIndex, x, y, w, h, isSelected);
        }
    }

    @Override
    protected void paintTabArea(Graphics g, int tabPlacement, int selectedIndex, Component c) {
        if (getTabShape() == JideTabbedPane.SHAPE_ECLIPSE) {
            int tabCount = _tabPane.getTabCount();

            Rectangle iconRect = new Rectangle(),
                    textRect = new Rectangle();
            Rectangle clipRect = g.getClipBounds();

            g.setColor(_tabBackground);
            g.fillRect(clipRect.x, clipRect.y, clipRect.width, clipRect.height);

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
                    paintTab(g, tabPlacement, _rects, selectedIndex, iconRect, textRect);
                }
            }
        }
        else {
            super.paintTabArea(g, tabPlacement, selectedIndex, c);
        }
    }

    @Override
    protected void paintContentBorderTopEdge(Graphics g, int tabPlacement, int selectedIndex, int x, int y, int w, int h) {
        if (getTabShape() == JideTabbedPane.SHAPE_ECLIPSE) {
            if (_tabPane.isTabShown()) {
                g.setColor(_shadow);
                g.drawLine(x, y, x + w - 1, y);
            }
        }
        else {
            super.paintContentBorderTopEdge(g, tabPlacement, selectedIndex, x, y, w, h);
        }
    }

    @Override
    protected void paintContentBorderBottomEdge(Graphics g, int tabPlacement, int selectedIndex, int x, int y, int w, int h) {
        if (getTabShape() == JideTabbedPane.SHAPE_ECLIPSE) {
            if (_tabPane.isTabShown()) {
                g.setColor(_shadow);
                g.drawLine(x, y + h - 1, x + w - 1, y + h - 1);
            }
        }
        else {
            super.paintContentBorderBottomEdge(g, tabPlacement, selectedIndex, x, y, w, h);
        }
    }

    @Override
    protected void paintContentBorderLeftEdge(Graphics g, int tabPlacement, int selectedIndex, int x, int y, int w, int h) {
        if (getTabShape() == JideTabbedPane.SHAPE_ECLIPSE) {
            if (_tabPane.isTabShown()) {
                g.setColor(_shadow);
                g.drawLine(x, y, x, y + h - 1);
            }
        }
        else {
            super.paintContentBorderLeftEdge(g, tabPlacement, selectedIndex, x, y, w, h);
        }
    }

    @Override
    protected void paintContentBorderRightEdge(Graphics g, int tabPlacement, int selectedIndex, int x, int y, int w, int h) {
        if (getTabShape() == JideTabbedPane.SHAPE_ECLIPSE) {
            if (_tabPane.isTabShown()) {
                g.setColor(_shadow);
                g.drawLine(x + w - 1, y, x + w - 1, y + h - 1);
            }
        }
        else {
            super.paintContentBorderRightEdge(g, tabPlacement, selectedIndex, x, y, w, h);
        }
    }

    @Override
    protected void paintTabBackground(Graphics g, int tabPlacement, int tabIndex, int x, int y, int w, int h, boolean isSelected) {
        if (getTabShape() == JideTabbedPane.SHAPE_ECLIPSE) {
            _tabPane.setBackgroundAt(tabIndex, _tabBackground);

            if (isSelected) {
                Color background1;
                Color background2;
                if (showFocusIndicator()) {
                    background1 = _activeBackground;
                    background2 = UIDefaultsLookup.getColor("DockableFrame.activeTitleBackground2");
                }
                else {
                    background1 = Color.WHITE;
                    background2 = _background;
                }

                Graphics2D g2d = (Graphics2D) g;
                int buttonSize = 16;
                int restWidth = w - (isShowCloseButtonOnTab() ? buttonSize : 0) - 3;
                int restHeight = h - (isShowCloseButtonOnTab() ? buttonSize : 0) - 3;
                switch (tabPlacement) {
                    case LEFT:
                        JideSwingUtilities.fillGradient(g2d, new Rectangle(x, y + 1, w, restHeight >> 1), background1, background2, true);
                        break;
                    case RIGHT:
                        JideSwingUtilities.fillGradient(g2d, new Rectangle(x, y + 1, w - 1, restHeight >> 1), background1, background2, true);
                        break;
                    case BOTTOM:
                        JideSwingUtilities.fillGradient(g2d, new Rectangle(x + 1, y, restWidth >> 1, h - 1), background1, background2, false);
                        break;
                    case TOP:
                    default:
                        JideSwingUtilities.fillGradient(g2d, new Rectangle(x + 1, y + 1, restWidth >> 1, h), background1, background2, false);
                        break;
                }
                switch (tabPlacement) {
                    case LEFT:
                        JideSwingUtilities.fillGradient(g2d, new Rectangle(x, y + 1 + (restHeight >> 1), w, restHeight >> 1), background2, _tabBackground, true);
                        break;
                    case RIGHT:
                        JideSwingUtilities.fillGradient(g2d, new Rectangle(x, y + 1 + (restHeight >> 1), w - 1, restHeight >> 1), background2, _tabBackground, true);
                        break;
                    case BOTTOM:
                        JideSwingUtilities.fillGradient(g2d, new Rectangle(x + 1 + (restWidth >> 1), y, restWidth >> 1, h - 1), background2, _tabBackground, false);
                        break;
                    case TOP:
                    default:
                        JideSwingUtilities.fillGradient(g2d, new Rectangle(x + 1 + (restWidth >> 1), y + 1, restWidth >> 1, h), background2, _tabBackground, false);
                        break;
                }
            }
            else {
                super.paintTabBackground(g, tabPlacement, tabIndex, x, y, w, h, isSelected);
            }
        }
        else {
            super.paintTabBackground(g, tabPlacement, tabIndex, x, y, w, h, isSelected);
        }
    }
}