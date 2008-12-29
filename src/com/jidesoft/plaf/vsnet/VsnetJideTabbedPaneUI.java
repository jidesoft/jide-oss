/*
 * @(#)WindowsTabbedPaneUI.java
 *
 * Copyright 2002 JIDE Software Inc. All rights reserved.
 */

package com.jidesoft.plaf.vsnet;

import com.jidesoft.plaf.UIDefaultsLookup;
import com.jidesoft.plaf.basic.BasicJideTabbedPaneUI;
import com.jidesoft.plaf.basic.ThemePainter;
import com.jidesoft.swing.JideTabbedPane;
import com.jidesoft.utils.ColorUtils;

import javax.swing.*;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.UIResource;
import java.awt.*;

/**
 * JideTabbedPane UI implementation
 */
public class VsnetJideTabbedPaneUI extends BasicJideTabbedPaneUI {

    protected Color _backgroundSelectedColorStart;

    protected Color _backgroundSelectedColorEnd;

    protected Color _backgroundUnselectedColorStart;

    protected Color _backgroundUnselectedColorEnd;

    public static ComponentUI createUI(JComponent c) {
        return new VsnetJideTabbedPaneUI();
    }

    @Override
    public void installColorTheme() {
        super.installColorTheme();

        int tabStyle = getTabShape();
        int colorTheme = getColorTheme();
        switch (tabStyle) {
            case JideTabbedPane.SHAPE_BOX:
                if (colorTheme == JideTabbedPane.COLOR_THEME_VSNET) {
                    _selectColor1 = _shadow;
                    _selectColor2 = _selectColor1;
                }
                else if (colorTheme == JideTabbedPane.COLOR_THEME_WINXP) {
                    _selectColor1 = UIDefaultsLookup.getColor("TextArea.selectionBackground");
                    _selectColor2 = _selectColor1;
                }
                _unselectColor1 = getPainter().getControlShadow();

                _unselectColor2 = _lightHighlight;
                break;

            case JideTabbedPane.SHAPE_EXCEL:
                if (_tabPane.getColorTheme() == JideTabbedPane.COLOR_THEME_VSNET) {
                    _selectColor2 = null;
                    _selectColor3 = null;
                    _unselectColor2 = _lightHighlight;
                    _unselectColor3 = _shadow;
                }
                break;
            case JideTabbedPane.SHAPE_WINDOWS:
            case JideTabbedPane.SHAPE_WINDOWS_SELECTED:
                if (_tabPane.getColorTheme() == JideTabbedPane.COLOR_THEME_VSNET) {
                    _selectColor1 = getPainter().getTabbedPaneSelectDk();
                    _selectColor2 = getPainter().getTabbedPaneSelectLt();
                    _selectColor3 = getPainter().getControlDk();
                    _unselectColor1 = getPainter().getControlDk();
                    _unselectColor2 = null;
                    _unselectColor3 = null;
                }
                if (_tabPane.getColorTheme() == JideTabbedPane.COLOR_THEME_WINXP) {
                    _selectColor1 = getPainter().getTabbedPaneSelectDk();
                    _selectColor2 = getPainter().getTabbedPaneSelectLt();
                    _selectColor3 = getPainter().getControlDk();
                    _unselectColor1 = getPainter().getControlDk();
                    _unselectColor2 = null;
                    _unselectColor3 = null;
                }
                break;
        }

        installBackgroundColor();
    }

    protected void installBackgroundColor() {
        int colorTheme = getColorTheme();

        switch (getTabShape()) {
            case JideTabbedPane.SHAPE_VSNET:
                _backgroundSelectedColorStart = _highlight;
                _backgroundSelectedColorEnd = _highlight;
                _backgroundUnselectedColorStart = null;
                _backgroundUnselectedColorEnd = null;
                break;
            case JideTabbedPane.SHAPE_ROUNDED_VSNET:
                if (colorTheme == JideTabbedPane.COLOR_THEME_WIN2K) {
                    _backgroundSelectedColorStart = _highlight;
                    _backgroundSelectedColorEnd = _highlight;
                    _backgroundUnselectedColorStart = null;
                    _backgroundUnselectedColorEnd = null;
                }
                else {
                    _backgroundSelectedColorStart = _background;
                    _backgroundSelectedColorEnd = _highlight;
                    _backgroundUnselectedColorStart = null;
                    _backgroundUnselectedColorEnd = null;
                }
                break;
            case JideTabbedPane.SHAPE_FLAT:
            case JideTabbedPane.SHAPE_ROUNDED_FLAT:
                _backgroundSelectedColorStart = _highlight;
                _backgroundSelectedColorEnd = _highlight;
                _backgroundUnselectedColorStart = _highlight;
                _backgroundUnselectedColorEnd = _highlight;
                break;
            case JideTabbedPane.SHAPE_BOX:
                if (colorTheme == JideTabbedPane.COLOR_THEME_WIN2K) {
                    _backgroundSelectedColorStart = _highlight;
                    _backgroundSelectedColorEnd = _highlight;
                    _backgroundUnselectedColorStart = null;
                    _backgroundUnselectedColorEnd = null;
                }
                else if (_tabPane.getColorTheme() == JideTabbedPane.COLOR_THEME_VSNET) {
                    _backgroundSelectedColorStart = _lightHighlight;
                    _backgroundSelectedColorEnd = _lightHighlight;
                    _backgroundUnselectedColorStart = null;
                    _backgroundUnselectedColorEnd = null;
                }
                else if (_tabPane.getColorTheme() == JideTabbedPane.COLOR_THEME_WINXP) {
                    _backgroundSelectedColorStart = _lightHighlight;
                    _backgroundSelectedColorEnd = _highlight;
                    _backgroundUnselectedColorStart = null;
                    _backgroundUnselectedColorEnd = null;
                }
                break;
            case JideTabbedPane.SHAPE_EXCEL:
                if (_tabPane.getColorTheme() == JideTabbedPane.COLOR_THEME_WIN2K) {
                    _backgroundSelectedColorStart = _highlight;
                    _backgroundSelectedColorEnd = _highlight;
                    _backgroundUnselectedColorStart = _lightHighlight;
                    _backgroundUnselectedColorEnd = _lightHighlight;
                }
                else if (_tabPane.getColorTheme() == JideTabbedPane.COLOR_THEME_VSNET) {
                    _backgroundSelectedColorStart = _lightHighlight;
                    _backgroundSelectedColorEnd = _lightHighlight;
                    _backgroundUnselectedColorStart = _highlight;
                    _backgroundUnselectedColorEnd = _highlight;
                }
                else /*if (_tabPane.getColorTheme() == JideTabbedPane.COLOR_THEME_WINXP)*/ {
                    _backgroundSelectedColorStart = _lightHighlight;
                    _backgroundSelectedColorEnd = _lightHighlight;
                    _backgroundUnselectedColorStart = _lightHighlight;
                    _backgroundUnselectedColorEnd = _highlight;
                }
                break;
            case JideTabbedPane.SHAPE_WINDOWS:
            case JideTabbedPane.SHAPE_WINDOWS_SELECTED:
                if (colorTheme == JideTabbedPane.COLOR_THEME_VSNET || colorTheme == JideTabbedPane.COLOR_THEME_WINXP) {
                    _backgroundSelectedColorStart = _lightHighlight;
                    _backgroundSelectedColorEnd = _lightHighlight;
                    _backgroundUnselectedColorStart = _lightHighlight;
                    _backgroundUnselectedColorEnd = _highlight;
                }
                else /*if (colorTheme == JideTabbedPane.COLOR_THEME_WIN2K)*/ {
                    _backgroundSelectedColorStart = _highlight;
                    _backgroundSelectedColorEnd = _highlight;
                    _backgroundUnselectedColorStart = _highlight;
                    _backgroundUnselectedColorEnd = _highlight;
                }
                break;
            case JideTabbedPane.SHAPE_OFFICE2003:
                if (colorTheme == JideTabbedPane.COLOR_THEME_WIN2K) {
                    _backgroundSelectedColorStart = _highlight;
                    _backgroundSelectedColorEnd = _highlight;
                    _backgroundUnselectedColorStart = _highlight;
                    _backgroundUnselectedColorEnd = _highlight;
                }
                else if (colorTheme == JideTabbedPane.COLOR_THEME_WINXP) {
                    _backgroundSelectedColorStart = _lightHighlight;
                    _backgroundSelectedColorEnd = _lightHighlight;
                    _backgroundUnselectedColorStart = _lightHighlight;
                    _backgroundUnselectedColorEnd = _highlight;
                }
                else {
                    _backgroundSelectedColorStart = _lightHighlight;
                    _backgroundSelectedColorEnd = _lightHighlight;
                    _backgroundUnselectedColorStart = _lightHighlight;
                    _backgroundUnselectedColorEnd = _highlight;
                }
                break;
        }
    }

    @Override
    public void uninstallColorTheme() {
        super.uninstallColorTheme();

        _backgroundSelectedColorStart = null;
        _backgroundSelectedColorEnd = null;
        _backgroundUnselectedColorStart = null;
        _backgroundUnselectedColorEnd = null;
    }

    @Override
    protected void paintTabBackground(Graphics g, int tabPlacement,
                                      int tabIndex, int x, int y, int w, int h, boolean isSelected) {
        super.paintTabBackground(g, tabPlacement, tabIndex, x, y, w, h, isSelected);

        if (tabRegion != null) {
            Color[] colors = getGradientColors(tabIndex, isSelected);
            if (colors != null) {
                getPainter().paintTabBackground(_tabPane, g, tabRegion, colors, SwingConstants.HORIZONTAL, ThemePainter.STATE_DEFAULT);
            }
        }

        if (getTabShape() == JideTabbedPane.SHAPE_WINDOWS_SELECTED) {
            if (_mouseEnter && tabIndex == _indexMouseOver && !isSelected && _tabPane.isEnabledAt(_indexMouseOver)) {
                paintTabBackgroundMouseOver(g, tabPlacement, tabIndex, x, y, w, h, isSelected, _backgroundUnselectedColorStart, _backgroundUnselectedColorEnd);
            }
        }
    }

    @Override
    protected void paintContentBorderTopEdge(Graphics g, int tabPlacement,
                                             int selectedIndex, int x, int y, int w, int h) {

        if (!PAINT_CONTENT_BORDER_EDGE) {
            return;
        }

        if (selectedIndex < 0) {
            return;
        }

        if (!_tabPane.isTabShown()) {
            return;
        }

        Rectangle selRect = getTabBounds(selectedIndex, _calcRect);

        Rectangle viewRect = _tabScroller.viewport.getViewRect();
        Rectangle r = _rects[selectedIndex];

        int tabShape = getTabShape();

        Insets contentInsets = getContentBorderInsets(tabPlacement);

        if (tabShape == JideTabbedPane.SHAPE_OFFICE2003) {
            if (_tabPane.getColorTheme() == JideTabbedPane.COLOR_THEME_WIN2K) {// the color set is default
                g.setColor(_shadow);
            }
            else {// the color set is office2003
                g.setColor(_selectColor1);
            }

            if (contentInsets.left > 0) {
                g.drawLine(x, y, x, y + h - 1);// left
            }
            if (contentInsets.right > 0) {
                g.drawLine(x + w - 1, y, x + w - 1, y + h - 1);// right
            }
            if (contentInsets.bottom > 0) {
                g.drawLine(x, y + h - 1, x + w - 1, y + h - 1);// bottom
            }

            if (!_tabPane.isTabShown() || r.x >= viewRect.x + viewRect.width) {
                g.drawLine(x, y, x + w - 1, y);// top
            }
            else {
                g.drawLine(x, y, selRect.x - selRect.height + 2, y);// top left
                g.drawLine(selRect.x + selRect.width, y, x + w - 1, y);// top right
            }
        }
        else if (tabShape == JideTabbedPane.SHAPE_EXCEL) {
            if (_tabPane.getColorTheme() == JideTabbedPane.COLOR_THEME_WIN2K) {
                g.setColor(Color.BLACK);
            }
            else {// the color set is vsnet
                g.setColor(getPainter().getControlShadow());
            }

            g.drawLine(x, y, selRect.x - selRect.height / 2 + 4, y);// top left

            if (contentInsets.left > 0) {
                g.drawLine(x, y, x, y + h - 1);// left
            }
            if (contentInsets.right > 0) {
                g.drawLine(x + w - 1, y, x + w - 1, y + h - 1);// right
            }
            if (contentInsets.bottom > 0) {
                g.drawLine(x, y + h - 1, x + w - 1, y + h - 1);// bottom
            }


            if (!_tabPane.isTabShown() || r.x >= viewRect.x + viewRect.width) {
                g.drawLine(x, y, x + w - 1, y);// top
            }
            else {
                if (!_tabPane.isShowIconsOnTab()
                        && !_tabPane.isUseDefaultShowIconsOnTab()) {
                    g.drawLine(selRect.x + selRect.width + selRect.height / 2 - 4,
                            y, x + w - 1, y);// top right
                }
                else {
                    g.drawLine(selRect.x + selRect.width + selRect.height / 2 - 6,
                            y, x + w - 1, y);// top right
                }
            }
        }
        else if (tabShape == JideTabbedPane.SHAPE_WINDOWS
                || tabShape == JideTabbedPane.SHAPE_WINDOWS_SELECTED) {
            if (_tabPane.getColorTheme() == JideTabbedPane.COLOR_THEME_VSNET
                    || _tabPane.getColorTheme() == JideTabbedPane.COLOR_THEME_WINXP) {// the color set is winxp
                g.setColor(getPainter().getControlDk());
            }
            else {// the color set is default or office2003
                g.setColor(getBorderEdgeColor());
            }

            if (_tabPane.getColorTheme() == JideTabbedPane.COLOR_THEME_VSNET
                    || _tabPane.getColorTheme() == JideTabbedPane.COLOR_THEME_WINXP) {// the color set is winxp
                g.drawLine(x, y, selRect.x - 1, y);// top left
            }
            else {// the color set is default or office2003
                g.drawLine(x, y, selRect.x - 1, y);// top left
            }

            if (contentInsets.left > 0) {
                g.drawLine(x, y, x, y + h - 1);// left
            }

            if (!_tabPane.isTabShown() || r.x >= viewRect.x + viewRect.width) {
                if (_tabPane.getColorTheme() == JideTabbedPane.COLOR_THEME_VSNET
                        || _tabPane.getColorTheme() == JideTabbedPane.COLOR_THEME_WINXP) {// the color set is winxp
                    g.setColor(getPainter().getControlDk());
                }
                else {// the color set is default or office2003
                    g.setColor(_lightHighlight);
                }

                g.drawLine(x, y, x + w - 1, y);// top
            }
            else {
                if (_tabPane.getColorTheme() == JideTabbedPane.COLOR_THEME_VSNET
                        || _tabPane.getColorTheme() == JideTabbedPane.COLOR_THEME_WINXP) {// the color set is winxp
                    g.setColor(getPainter().getControlDk());
                    g.drawLine(selRect.x + selRect.width + 2, y, x + w - 1, y);// top right
                }
                else {// the color set is default or office2003
                    g.setColor(_lightHighlight);
                    g.drawLine(selRect.x + selRect.width + 2, y, x + w - 1, y);// top right
                }
            }

            if (_tabPane.getColorTheme() == JideTabbedPane.COLOR_THEME_WIN2K) {
                g.setColor(new Color(115, 109, 99));
                g.drawLine(x + w - 1, y, x + w - 1, y + h - 1);// right
                g.drawLine(x, y + h - 1, x + w - 1, y + h - 1);// bottom
            }
            else {// the color set is winxp
                g.setColor(getPainter().getControlDk());

                if (contentInsets.left > 0) {
                    g.drawLine(x, y, x, y + h - 1);// left
                }
                if (contentInsets.right > 0) {
                    g.drawLine(x + w - 1, y, x + w - 1, y + h - 1);// right
                }
                if (contentInsets.bottom > 0) {
                    g.drawLine(x, y + h - 1, x + w - 1, y + h - 1);// bottom
                }
            }
        }
        else if (tabShape == JideTabbedPane.SHAPE_VSNET) {
            g.setColor(_selectColor1);

            // Break line to show visual connection to selected tab
            g.drawLine(x, y, selRect.x, y);// top left

            if (contentInsets.left > 0) {
                g.drawLine(x, y, x, y + h - 1);// left
            }

            if (!_tabPane.isTabShown() || r.x >= viewRect.x + viewRect.width) {
                g.setColor(_selectColor1);
                g.drawLine(x, y, x + w - 1, y);// top
            }
            else {
                g.setColor(_selectColor2);
                g.drawLine(selRect.x + selRect.width - 1, y, selRect.x + selRect.width - 1, y);// a point

                g.setColor(_selectColor1);
                g.drawLine(selRect.x, y, selRect.x, y);// a point

                g.setColor(_selectColor1);
                g.drawLine(selRect.x + selRect.width, y, x + w - 1, y);// top right
            }

            g.setColor(_selectColor2);
            if (contentInsets.right > 0) {
                g.drawLine(x + w - 1, y, x + w - 1, y + h - 1);// right
            }
            if (contentInsets.bottom > 0) {
                g.drawLine(x, y + h - 1, x + w - 1, y + h - 1);// bottom
            }

        }
        else if (tabShape == JideTabbedPane.SHAPE_ROUNDED_VSNET) {
            g.setColor(_selectColor1);

            // Break line to show visual connection to selected tab
            g.drawLine(x, y, selRect.x, y);// top left

            if (contentInsets.left > 0) {
                g.drawLine(x, y, x, y + h - 1);// left
            }

            if (!_tabPane.isTabShown() || r.x >= viewRect.x + viewRect.width) {
                g.drawLine(x, y, x + w - 1, y);// top
            }
            else {
                g.drawLine(selRect.x + selRect.width - 1, y, selRect.x + selRect.width - 1, y);// a point

                g.drawLine(selRect.x, y, selRect.x, y);// a point

                g.drawLine(selRect.x + selRect.width, y, x + w - 1, y);// top right
            }

            g.drawLine(x + w - 1, y + 1, x + w - 1, y + h - 1);// right
            g.drawLine(x, y + h - 1, x + w - 1, y + h - 1);// bottom
        }
        else if (tabShape == JideTabbedPane.SHAPE_FLAT || tabShape == JideTabbedPane.SHAPE_ROUNDED_FLAT) {
            g.setColor(_shadow);

            if (contentInsets.left > 0) {
                g.drawLine(x, y, x, y + h - 1);// left
            }
            if (contentInsets.right > 0) {
                g.drawLine(x + w - 1, y, x + w - 1, y + h - 1);// right
            }
            if (contentInsets.bottom > 0) {
                g.drawLine(x, y + h - 1, x + w - 1, y + h - 1);// bottom
            }

            g.drawLine(x, y, selRect.x, y);// top left

            if (!_tabPane.isTabShown() || r.x >= viewRect.x + viewRect.width) {
                g.drawLine(x, y, x + w - 1, y);// top
            }
            else {
                g.drawLine(selRect.x + selRect.width, y, x + w - 1, y);// top right
            }
        }
        else {
            super.paintContentBorderTopEdge(g, tabPlacement, selectedIndex, x, y, w, h);
        }

    }

    @Override
    protected void paintContentBorderBottomEdge(Graphics g, int tabPlacement,
                                                int selectedIndex, int x, int y, int w, int h) {
        if (!PAINT_CONTENT_BORDER_EDGE) {
            return;
        }

        if (selectedIndex < 0) {
            return;
        }

        if (!_tabPane.isTabShown()) {
            return;
        }

        Rectangle selRect = getTabBounds(selectedIndex, _calcRect);

        Rectangle viewRect = _tabScroller.viewport.getViewRect();
        Rectangle r = _rects[selectedIndex];

        int tabShape = getTabShape();

        Insets contentInsets = getContentBorderInsets(tabPlacement);

        if (tabShape == JideTabbedPane.SHAPE_OFFICE2003) {
            if (_tabPane.getColorTheme() == JideTabbedPane.COLOR_THEME_WIN2K) {// the color set is default
                g.setColor(_shadow);
            }
            else {// the color set is office2003
                g.setColor(_selectColor1);
            }
            if (contentInsets.left > 0) {
                g.drawLine(x, y, x, y + h - 1);// left
            }
            if (contentInsets.right > 0) {
                g.drawLine(x + w - 1, y, x + w - 1, y + h - 1);// right
            }
            if (contentInsets.top > 0) {
                g.drawLine(x, y, x + w - 1, y);// top
            }

            if (!_tabPane.isTabShown() || r.x >= viewRect.x + viewRect.width) {
                g.drawLine(x, y + h - 1, x + w - 1, y + h - 1);// bottom
            }
            else {
                if (!_tabPane.isTabShown()) {
                    g.drawLine(x, y + h - 1, x + w - 1, y + h - 1);// bottom
                }
                else {
                    g.drawLine(x, y + h - 1, selRect.x - selRect.height + 2, y + h - 1);// bottom left
                    g.drawLine(selRect.x + selRect.width, y + h - 1, x + w - 1, y + h - 1);// bottom right
                }
            }
        }
        else if (tabShape == JideTabbedPane.SHAPE_EXCEL) {
            if (_tabPane.getColorTheme() == JideTabbedPane.COLOR_THEME_WIN2K) {
                g.setColor(Color.BLACK);
            }
            else {// the color set is vsnet
                g.setColor(getPainter().getControlShadow());
            }

            g.drawLine(x, y + h - 1, selRect.x - selRect.height / 2 + 4, y + h - 1);// bottom left

            if (contentInsets.left > 0) {
                g.drawLine(x, y, x, y + h - 1);// left
            }
            if (contentInsets.right > 0) {
                g.drawLine(x + w - 1, y, x + w - 1, y + h - 1);// right
            }
            if (contentInsets.top > 0) {
                g.drawLine(x, y, x + w - 1, y);// top
            }

            if (!_tabPane.isTabShown() || r.x >= viewRect.x + viewRect.width) {
                g.drawLine(x, y + h - 1, x + w - 1, y + h - 1);// bottom
            }
            else {
                if (!_tabPane.isShowIconsOnTab() && !_tabPane.isUseDefaultShowIconsOnTab()) {
                    g.drawLine(selRect.x + selRect.width + selRect.height / 2 - 4, y + h - 1, x + w - 1, y + h - 1);// bottom right
                }
                else {
                    g.drawLine(selRect.x + selRect.width + selRect.height / 2 - 6, y + h - 1, x + w - 1, y + h - 1);// bottom right
                }
            }
        }
        else if (tabShape == JideTabbedPane.SHAPE_WINDOWS
                || tabShape == JideTabbedPane.SHAPE_WINDOWS_SELECTED) {
            if (_tabPane.getColorTheme() == JideTabbedPane.COLOR_THEME_WIN2K) {
                g.setColor(new Color(113, 111, 100));
                g.drawLine(x, y + h - 1, selRect.x - 2, y + h - 1);
                if (contentInsets.right > 0) {
                    g.drawLine(x + w - 1, y + h - 1, x + w - 1, y);
                }

                if (!_tabPane.isTabShown() || r.x >= viewRect.x + viewRect.width) {
                    g.drawLine(x, y + h - 1, x + w - 1, y + h - 1);
                }
                else {
                    g.drawLine(selRect.x + selRect.width, y + h - 1, x + w - 1, y + h - 1);
                    g.setColor(UIDefaultsLookup.getColor("control"));
                    g.drawLine(selRect.x, y + h - 1, selRect.x + selRect.width - 2, y + h - 1);
                    g.drawLine(selRect.x, y + h - 2, selRect.x + selRect.width, y + h - 2);
                }

                g.setColor(new Color(255, 255, 255));

                if (contentInsets.left > 0) {
                    g.drawLine(x, y, x, y + h - 2);
                }
                if (contentInsets.top > 0) {
                    g.drawLine(x, y, x + w - 2, y);
                }

            }
            else {// the color set is winxp
                g.setColor(getPainter().getControlDk());

                g.drawLine(x, y + h - 1, selRect.x - 1, y + h - 1);

                if (!_tabPane.isTabShown() || r.x >= viewRect.x + viewRect.width) {
                    g.drawLine(x, y + h - 1, x + w - 1, y + h - 1);
                }
                else {
                    g.drawLine(selRect.x + selRect.width + 2, y + h - 1, x + w - 1, y + h - 1);
                }

                g.drawLine(selRect.x - 1, y + h - 1, selRect.x - 1, y + h - 1);
                g.drawLine(selRect.x + selRect.width + 2, y + h - 1, selRect.x + selRect.width + 2, y + h - 1);

                if (contentInsets.left > 0) {
                    g.drawLine(x, y, x, y + h - 2);
                }
                if (contentInsets.top > 0) {
                    g.drawLine(x, y, x + w - 2, y);
                }
                if (contentInsets.left > 0) {
                    g.drawLine(x + w - 1, y + h - 1, x + w - 1, y);
                }
            }
        }
        else if (tabShape == JideTabbedPane.SHAPE_VSNET) {
//			 Break line to show visual connection to selected tab
            g.setColor(_selectColor2);
            g.drawLine(x, y + h - 1, selRect.x - 1, y + h - 1);// bottom left

            g.setColor(_selectColor1);
            g.drawLine(selRect.x, y + h - 1, selRect.x, y + h - 1);

            if (!_tabPane.isTabShown() || r.x >= viewRect.x + viewRect.width) {
                g.setColor(_selectColor2);
                g.drawLine(x, y + h - 1, x + w - 1, y + h - 1);// bottom
            }
            else {
                g.setColor(_selectColor2);
                g.drawLine(selRect.x + selRect.width - 1, y + h - 1, x + w - 2, y
                        + h - 1); // bottom right
            }

            g.setColor(_selectColor2);
            if (contentInsets.right > 0) {
                g.drawLine(x + w - 1, y, x + w - 1, y + h - 1);// right
            }

            g.setColor(_selectColor1);
            if (contentInsets.left > 0) {
                g.drawLine(x, y, x, y + h - 2);// left
            }
            if (contentInsets.top > 0) {
                g.drawLine(x, y, x + w - 2, y);// top
            }
        }
        else if (tabShape == JideTabbedPane.SHAPE_ROUNDED_VSNET) {
//			 Break line to show visual connection to selected tab
            g.setColor(_selectColor1);
            g.drawLine(x, y + h - 1, selRect.x - 1, y + h - 1);// bottom left

            g.drawLine(selRect.x, y + h - 1, selRect.x, y + h - 1);

            if (!_tabPane.isTabShown() || r.x >= viewRect.x + viewRect.width) {
                g.drawLine(x, y + h - 1, x + w - 1, y + h - 1);// bottom
            }
            else {
                g.drawLine(selRect.x + selRect.width - 1, y + h - 1, x + w - 2, y
                        + h - 1); // bottom right
            }

            if (contentInsets.left > 0) {
                g.drawLine(x, y, x, y + h - 2);// left
            }
            if (contentInsets.right > 0) {
                g.drawLine(x + w - 1, y, x + w - 1, y + h - 1);// right
            }
            if (contentInsets.top > 0) {
                g.drawLine(x, y, x + w - 2, y);// top
            }
        }
        else if (tabShape == JideTabbedPane.SHAPE_FLAT
                || tabShape == JideTabbedPane.SHAPE_ROUNDED_FLAT) {
            g.setColor(_shadow);
            g.drawLine(x, y + h - 1, selRect.x - 1, y + h - 1);// bottom left
            if (contentInsets.left > 0) {
                g.drawLine(x, y, x, y + h - 1);// left
            }
            if (contentInsets.right > 0) {
                g.drawLine(x + w - 1, y, x + w - 1, y + h - 1);// right
            }
            if (contentInsets.top > 0) {
                g.drawLine(x, y, x + w - 1, y);// top
            }
            g.drawLine(selRect.x, y + h - 1, selRect.x, y + h - 1);// a point

            if (!_tabPane.isTabShown() || r.x >= viewRect.x + viewRect.width) {
                g.drawLine(x, y + h - 1, x + w - 2, y + h - 1);// bottom
            }
            else {
                g.drawLine(selRect.x + selRect.width, y + h - 1, x + w - 2, y + h - 1);// bottom right
            }
        }
        else {
            super.paintContentBorderBottomEdge(g, tabPlacement, selectedIndex, x, y, w, h);
        }
    }

    @Override
    protected void paintContentBorderLeftEdge(Graphics g, int tabPlacement,
                                              int selectedIndex, int x, int y, int w, int h) {

        if (!PAINT_CONTENT_BORDER_EDGE) {
            return;
        }

        if (selectedIndex < 0) {
            return;
        }

        if (!_tabPane.isTabShown()) {
            return;
        }

        Rectangle selRect = getTabBounds(selectedIndex, _calcRect);

        Rectangle viewRect = _tabScroller.viewport.getViewRect();
        Rectangle r = _rects[selectedIndex];

        int tabShape = getTabShape();

        Insets contentInsets = getContentBorderInsets(tabPlacement);

        if (tabShape == JideTabbedPane.SHAPE_OFFICE2003) {
            if (_tabPane.getColorTheme() == JideTabbedPane.COLOR_THEME_WIN2K) {// the color set is default
                g.setColor(_shadow);
            }
            else {// the color set is office2003
                g.setColor(_selectColor1);
            }

            if (contentInsets.top > 0) {
                g.drawLine(x, y, x + w - 1, y);// top
            }
            if (contentInsets.right > 0) {
                g.drawLine(x + w - 1, y, x + w - 1, y + h - 1);// right
            }
            if (contentInsets.bottom > 0) {
                g.drawLine(x, y + h - 1, x + w - 1, y + h - 1);// bottom
            }

            if (!_tabPane.isTabShown() || r.y >= viewRect.y + viewRect.height) {
                g.drawLine(x, y, x, y + h - 1);// left
            }
            else {
                g.drawLine(x, y, x, selRect.y - selRect.width + 2);// left top
                g.drawLine(x, selRect.y + selRect.height, x, y + h - 1);// left bottom
            }
        }
        else if (tabShape == JideTabbedPane.SHAPE_EXCEL) {
            if (_tabPane.getColorTheme() == JideTabbedPane.COLOR_THEME_WIN2K) {
                g.setColor(Color.BLACK);
            }
            else {// the color set is vsnet
                g.setColor(getPainter().getControlShadow());
            }

            g.drawLine(x, y, x, selRect.y - selRect.width / 2 + 4);// left top

            if (contentInsets.top > 0) {
                g.drawLine(x, y, x + w - 1, y);// top
            }
            if (contentInsets.right > 0) {
                g.drawLine(x + w - 1, y, x + w - 1, y + h - 1);// right
            }
            if (contentInsets.bottom > 0) {
                g.drawLine(x, y + h - 1, x + w - 1, y + h - 1);// bottom
            }

            if (!_tabPane.isTabShown() || r.y >= viewRect.y + viewRect.height) {
                g.drawLine(x, y, x, y + h - 1);// left
            }
            else {
                g.drawLine(x, selRect.y + selRect.height + selRect.width / 2 - 4, x, y + h - 1);// left bottom
            }
        }
        else if (tabShape == JideTabbedPane.SHAPE_WINDOWS
                || tabShape == JideTabbedPane.SHAPE_WINDOWS_SELECTED) {
            if (_tabPane.getColorTheme() == JideTabbedPane.COLOR_THEME_WIN2K) {
                g.setColor(new Color(115, 109, 99));
                if (contentInsets.right > 0) {
                    g.drawLine(x + w - 1, y, x + w - 1, y + h - 1);// right
                }
                if (contentInsets.bottom > 0) {
                    g.drawLine(x, y + h - 1, x + w - 1, y + h - 1);// bottom
                }

                g.setColor(new Color(255, 255, 255));
                if (contentInsets.top > 0) {
                    g.drawLine(x, y, x + w - 2, y);
                }
                g.drawLine(x, y, x, selRect.y - 1);

                if (!_tabPane.isTabShown() || r.y >= viewRect.y + viewRect.height) {
                    g.drawLine(x, y, x, y + h - 2);
                }
                else {
                    g.drawLine(x, selRect.y + selRect.height + 1, x, y + h - 2);
                }

            }
            else {// the color set is winxp
                g.setColor(getPainter().getControlDk());

                if (contentInsets.top > 0) {
                    g.drawLine(x, y, x + w - 1, y);// top
                }
                if (contentInsets.right > 0) {
                    g.drawLine(x + w - 1, y, x + w - 1, y + h - 1);// right
                }
                if (contentInsets.bottom > 0) {
                    g.drawLine(x, y + h - 1, x + w - 1, y + h - 1);// bottom
                }
                g.drawLine(x, y, x, selRect.y - 2);

                if (!_tabPane.isTabShown() || r.y >= viewRect.y + viewRect.height) {
                    g.drawLine(x, y, x, y + h - 2);
                }
                else {
                    g.drawLine(x, selRect.y + selRect.height + 2, x, y + h - 2);
                }
            }
        }
        else if (tabShape == JideTabbedPane.SHAPE_VSNET) {
            g.setColor(_selectColor1);

            // Break line to show visual connection to selected tab
            g.drawLine(x, y, x, selRect.y);// left top

            if (contentInsets.top > 0) {
                g.drawLine(x, y, x + w - 1, y);// top
            }

            if (!_tabPane.isTabShown() || r.x >= viewRect.x + viewRect.width) {
                g.setColor(_selectColor1);
                g.drawLine(x, y, x, y + h - 1);// left
            }
            else {
                g.setColor(_selectColor1);
                g.drawLine(x, selRect.y, x, selRect.y);// a point

                g.setColor(_selectColor2);
                g.drawLine(x, selRect.y + selRect.height - 1, x, selRect.y + selRect.height - 1);// a point

                g.setColor(_selectColor1);
                g.drawLine(x, selRect.y + selRect.height, x, y + h - 1);// left
                // bottom
            }

            g.setColor(_selectColor2);
            if (contentInsets.right > 0) {
                g.drawLine(x + w - 1, y, x + w - 1, y + h - 1);// right
            }
            if (contentInsets.bottom > 0) {
                g.drawLine(x, y + h - 1, x + w - 1, y + h - 1);// bottom
            }
        }
        else if (tabShape == JideTabbedPane.SHAPE_ROUNDED_VSNET) {
            g.setColor(_selectColor1);

            // Break line to show visual connection to selected tab
            g.drawLine(x, y, x, selRect.y);// left top

            if (!_tabPane.isTabShown() || r.x >= viewRect.x + viewRect.width) {
                g.drawLine(x, y, x, y + h - 1);// left
            }
            else {
                g.drawLine(x, selRect.y, x, selRect.y);// a point
                g.drawLine(x, selRect.y + selRect.height - 1, x, selRect.y + selRect.height - 1);// a point
                g.drawLine(x, selRect.y + selRect.height, x, y + h - 1);// left
                // bottom
            }

            if (contentInsets.top > 0) {
                g.drawLine(x, y, x + w - 1, y);// top
            }
            if (contentInsets.right > 0) {
                g.drawLine(x + w - 1, y, x + w - 1, y + h - 1);// right
            }
            if (contentInsets.bottom > 0) {
                g.drawLine(x, y + h - 1, x + w - 1, y + h - 1);// bottom
            }
        }
        else if (tabShape == JideTabbedPane.SHAPE_FLAT || tabShape == JideTabbedPane.SHAPE_ROUNDED_FLAT) {
            g.setColor(_shadow);
            if (contentInsets.top > 0) {
                g.drawLine(x, y, x + w - 1, y);// top
            }
            if (contentInsets.right > 0) {
                g.drawLine(x + w - 1, y, x + w - 1, y + h - 1);// right
            }
            if (contentInsets.bottom > 0) {
                g.drawLine(x, y + h - 1, x + w - 1, y + h - 1);// bottom
            }
            g.drawLine(x, y, x, selRect.y);// left top

            if (!_tabPane.isTabShown() || r.y >= viewRect.y + viewRect.height) {
                g.drawLine(x, y, x, y + h - 1);// left
            }
            else {
                g.drawLine(x, selRect.y + selRect.height, x, y + h - 1);// left bottom
            }
        }
        else {
            super.paintContentBorderLeftEdge(g, tabPlacement, selectedIndex, x, y, w, h);
        }
    }

    @Override
    protected void paintContentBorderRightEdge(Graphics g, int tabPlacement,
                                               int selectedIndex, int x, int y, int w, int h) {

        if (!PAINT_CONTENT_BORDER_EDGE) {
            return;
        }

        if (selectedIndex < 0) {
            return;
        }

        if (!_tabPane.isTabShown()) {
            return;
        }

        Rectangle selRect = getTabBounds(selectedIndex, _calcRect);

        Rectangle viewRect = _tabScroller.viewport.getViewRect();
        Rectangle r = _rects[selectedIndex];

        int tabShape = getTabShape();

        Insets contentInsets = getContentBorderInsets(tabPlacement);

        if (tabShape == JideTabbedPane.SHAPE_OFFICE2003) {
            if (_tabPane.getColorTheme() == JideTabbedPane.COLOR_THEME_WIN2K) {// the color set is default
                g.setColor(_shadow);
            }
            else {// the color set is office2003
                g.setColor(_selectColor1);
            }

            if (contentInsets.top > 0) {
                g.drawLine(x, y, x + w - 1, y);// top
            }
            if (contentInsets.left > 0) {
                g.drawLine(x, y, x, y + h - 1);// left
            }
            if (contentInsets.bottom > 0) {
                g.drawLine(x, y + h - 1, x + w - 1, y + h - 1);// bottom
            }

            if (!_tabPane.isTabShown() || r.y >= viewRect.y + viewRect.height) {
                g.drawLine(x + w - 1, y, x + w - 1, y + h - 1);// right
            }
            else {
                g.drawLine(x + w - 1, y, x + w - 1, selRect.y - selRect.width + 2);// right top
                g.drawLine(x + w - 1, selRect.y + selRect.height, x + w - 1, y + h - 1);// right bottom
            }
        }
        else if (tabShape == JideTabbedPane.SHAPE_EXCEL) {
            if (_tabPane.getColorTheme() == JideTabbedPane.COLOR_THEME_WIN2K) {
                g.setColor(Color.BLACK);
            }
            else {// the color set is vsnet
                g.setColor(getPainter().getControlShadow());
            }

            g.drawLine(x + w - 1, y, x + w - 1, selRect.y - selRect.width / 2 + 4);// right top

            if (contentInsets.top > 0) {
                g.drawLine(x, y, x + w - 1, y);// top
            }
            if (contentInsets.left > 0) {
                g.drawLine(x, y, x, y + h - 1);// left
            }
            if (contentInsets.bottom > 0) {
                g.drawLine(x, y + h - 1, x + w - 1, y + h - 1);// bottom
            }

            if (!_tabPane.isTabShown() || r.y >= viewRect.y + viewRect.height) {
                g.drawLine(x + w - 1, y, x + w - 1, y + h - 1);// right
            }
            else {
                g.drawLine(x + w - 1, selRect.y + selRect.height + selRect.width / 2 - 4, x + w - 1, y + h - 1);// right bottom
            }
        }
        else if (tabShape == JideTabbedPane.SHAPE_WINDOWS
                || tabShape == JideTabbedPane.SHAPE_WINDOWS_SELECTED) {
            if (_tabPane.getColorTheme() == JideTabbedPane.COLOR_THEME_WIN2K) {

                g.setColor(new Color(115, 109, 99));
                g.drawLine(x + w - 1, y, x + w - 1, selRect.y - 2);
                if (contentInsets.bottom > 0) {
                    g.drawLine(x, y + h - 1, x + w - 1, y + h - 1);// bottom
                }

                if (!_tabPane.isTabShown() || r.y >= viewRect.y + viewRect.height) {
                    g.drawLine(x + w - 1, y, x + w - 1, y + h - 1);
                    g.setColor(new Color(173, 170, 156));
                    g.drawLine(x + w - 2, y + 1, x + w - 2, y + h - 2);
                }
                else {
                    g.drawLine(x + w - 1, selRect.y + selRect.height + 1, x + w - 1, y + h - 1);
                    g.setColor(UIDefaultsLookup.getColor("control"));
                    g.drawLine(x + w - 1, selRect.y, x + w - 1, selRect.y + selRect.height - 1);
                    g.drawLine(x + w - 2, selRect.y, x + w - 2, selRect.y + selRect.height + 1);
                }

                g.setColor(new Color(255, 255, 255));
                if (contentInsets.top > 0) {
                    g.drawLine(x, y, x + w - 2, y);// top
                }
                if (contentInsets.left > 0) {
                    g.drawLine(x, y, x, y + h - 2);// left
                }
            }
            else {// the color set is winxp
                g.setColor(getPainter().getControlDk());
                if (contentInsets.top > 0) {
                    g.drawLine(x, y, x + w - 1, y);// top
                }
                if (contentInsets.left > 0) {
                    g.drawLine(x, y, x, y + h - 1);// left
                }
                if (contentInsets.bottom > 0) {
                    g.drawLine(x, y + h - 1, x + w - 1, y + h - 1);// bottom
                }
                g.drawLine(x + w - 1, y, x + w - 1, selRect.y - 2);

                if (!_tabPane.isTabShown() || r.y >= viewRect.y + viewRect.height) {
                    g.drawLine(x + w - 1, y, x + w - 1, y + h - 2);
                }
                else {
                    g.drawLine(x + w - 1, selRect.y + selRect.height + 2, x + w - 1, y + h - 2);
                }

            }
        }
        else if (tabShape == JideTabbedPane.SHAPE_VSNET) {
            g.setColor(getBorderEdgeColor());

            // Break line to show visual connection to selected tab
            g.setColor(_selectColor2);
            g.drawLine(x + w - 1, y, x + w - 1, selRect.y);// right top
            if (contentInsets.bottom > 0) {
                g.drawLine(x, y + h - 1, x + w - 1, y + h - 1);// bottom
            }

            if (!_tabPane.isTabShown() || r.x >= viewRect.x + viewRect.width) {
                g.drawLine(x + w - 1, y, x + w - 1, y + h - 1);// right
            }
            else {
                g.setColor(_selectColor2);
                g.drawLine(x + w - 1, selRect.y + selRect.height - 1, x + w - 1, selRect.y + selRect.height - 1);// a point
                g.drawLine(x + w - 1, selRect.y + selRect.height, x + w - 1, y + h - 1);// right bottom
            }

            g.setColor(_selectColor1);
            if (contentInsets.top > 0) {
                g.drawLine(x, y, x + w - 2, y);// top
            }
            if (contentInsets.left > 0) {
                g.drawLine(x, y, x, y + h - 2);// left
            }
        }
        else if (tabShape == JideTabbedPane.SHAPE_ROUNDED_VSNET) {
            g.setColor(_selectColor1);

            // Break line to show visual connection to selected tab
            g.drawLine(x + w - 1, y, x + w - 1, selRect.y - 1);// right top

            if (!_tabPane.isTabShown() || r.x >= viewRect.x + viewRect.width) {
                g.drawLine(x + w - 1, y, x + w - 1, y + h - 1);// right
            }
            else {
                g.drawLine(x + w - 1, selRect.y, x + w - 1, selRect.y);// a point
                g.drawLine(x + w - 1, selRect.y + selRect.height - 1, x + w - 1, selRect.y + selRect.height - 1);// a point
                g.drawLine(x + w - 1, selRect.y + selRect.height, x + w - 1, y + h - 1);// right bottom
            }

            if (contentInsets.top > 0) {
                g.drawLine(x, y, x + w - 1, y);// top
            }
            if (contentInsets.left > 0) {
                g.drawLine(x, y, x, y + h - 2);// left
            }
            if (contentInsets.bottom > 0) {
                g.drawLine(x, y + h - 1, x + w - 1, y + h - 1);// bottom
            }
        }
        else if (tabShape == JideTabbedPane.SHAPE_FLAT
                || tabShape == JideTabbedPane.SHAPE_ROUNDED_FLAT) {
            g.setColor(_shadow);

            if (contentInsets.top > 0) {
                g.drawLine(x, y, x + w - 1, y);// top
            }
            if (contentInsets.left > 0) {
                g.drawLine(x, y, x, y + h - 1);// left
            }
            if (contentInsets.bottom > 0) {
                g.drawLine(x, y + h - 1, x + w - 1, y + h - 1);// bottom
            }
            g.drawLine(x + w - 1, y, x + w - 1, selRect.y);// right top

            if (!_tabPane.isTabShown() || r.y >= viewRect.y + viewRect.height) {
                g.drawLine(x + w - 1, y, x + w - 1, y + h - 1);// right
            }
            else {
                g.drawLine(x + w - 1, selRect.y + selRect.height, x + w - 1, y + h - 1);// right bottom
            }
        }
        else {
            super.paintContentBorderRightEdge(g, tabPlacement, selectedIndex, x, y, w, h);
        }
    }

    @Override
    protected void paintContentBorder(Graphics g, int x, int y, int w, int h) {
        if (!PAINT_CONTENT_BORDER) {
            return;
        }

        Insets insets = getContentBorderInsets(_tabPane.getTabPlacement());

        JideTabbedPane.ColorProvider colorProvider = _tabPane.getTabColorProvider();
        boolean useDefault = true;
        if (colorProvider != null) {
            Color backgroundAt = _tabPane.getBackground();
            if (_tabPane.getSelectedIndex() != -1)
                backgroundAt = colorProvider.getBackgroundAt(_tabPane.getSelectedIndex());
            if (backgroundAt != null) {
                g.setColor(backgroundAt);
                g.fillRect(x, y, w, h);
                useDefault = false;
            }
        }

        if (useDefault) {
            Color[] colors = getGradientColors(_tabPane.getSelectedIndex(), true);
            if (colors != null) {
                g.setColor(colors[1]);
                g.fillRect(x, y, w, insets.top); // top
                g.fillRect(x, y, insets.left, h); // left
                g.fillRect(x, y + h - insets.bottom, w, insets.bottom); // bottom
                g.fillRect(x + w - insets.right, y, insets.right, h); // right
            }
        }
    }

    protected Color[] getGradientColors(int tabIndex, boolean isSelected) {
        Color backgroundEnd = null;
        Color backgroundStart = null;

        JideTabbedPane.ColorProvider colorProvider = _tabPane.getTabColorProvider();
        if (colorProvider != null) {
            backgroundEnd = colorProvider.getBackgroundAt(tabIndex);
            if (colorProvider instanceof JideTabbedPane.GradientColorProvider) {
                backgroundStart = ((JideTabbedPane.GradientColorProvider) colorProvider).getTopBackgroundAt(tabIndex);
            }
            else {
                backgroundStart = backgroundEnd != null ? ColorUtils.getDerivedColor(backgroundEnd, colorProvider.getGradientRatio(tabIndex)) : null;
            }
        }
        else {
            Color color = _tabPane.getBackground();
            if (tabIndex != -1)
                color = _tabPane.getBackgroundAt(tabIndex);
            if (!(color instanceof UIResource) && color != _tabPane.getBackground()) {
                backgroundEnd = color;
                if (getColorTheme() == JideTabbedPane.COLOR_THEME_OFFICE2003) {
                    backgroundStart = ColorUtils.getDerivedColor(color, 0.8f);
                }
                else {
                    backgroundStart = color;
                }
            }
        }

        if (isSelected) {
            if (showFocusIndicator()) {
                if (backgroundEnd == null) {
                    backgroundEnd = _backgroundSelectedColorEnd;
                }
                if (backgroundStart == null) {
                    backgroundStart = _backgroundSelectedColorStart;
                }
            }
            else {
                if (getColorTheme() == JideTabbedPane.COLOR_THEME_VSNET) {
                    if (backgroundEnd == null) {
                        backgroundEnd = _backgroundSelectedColorEnd;
                    }
                    if (backgroundStart == null) {
                        backgroundStart = _backgroundSelectedColorStart;
                    }
                }
                else {
                    if (backgroundEnd == null) {
                        backgroundEnd = ColorUtils.getDerivedColor(_backgroundUnselectedColorEnd, 0.7f);
                    }
                    if (backgroundStart == null) {
                        backgroundStart = ColorUtils.getDerivedColor(_backgroundUnselectedColorStart, 0.8f);
                    }
                }
            }
        }
        else {
            if (getTabShape() != JideTabbedPane.SHAPE_WINDOWS_SELECTED) {
                if (backgroundEnd == null) {
                    backgroundEnd = _backgroundUnselectedColorEnd;
                }
                if (backgroundStart == null) {
                    backgroundStart = _backgroundUnselectedColorStart;
                }
            }
        }
        return new Color[]{backgroundStart, backgroundEnd};
    }
}
