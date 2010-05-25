/*
 * @(#)$fileName
 *
 * Copyright 2002 - 2004 JIDE Software Inc. All rights reserved.
 */
package com.jidesoft.plaf.eclipse;

import com.jidesoft.plaf.UIDefaultsLookup;
import com.jidesoft.plaf.basic.BasicPainter;
import com.jidesoft.plaf.basic.ThemePainter;
import com.jidesoft.swing.JideSwingUtilities;

import javax.swing.*;
import java.awt.*;

/**
 * Painter for Eclipse L&F.
 * <p/>
 * Please note, this class is an internal class which is meant to be used by other JIDE classes only. Future version might break your build if you use it.
 */
public class EclipsePainter extends BasicPainter {

    private static EclipsePainter _instance;

    public static ThemePainter getInstance() {
        if (_instance == null) {
            _instance = new EclipsePainter();
        }
        return _instance;
    }

    protected Color _shadowColor;
    protected Color _darkShadowColor;
    protected Color _highlight;
    protected Color _lightHighlightColor;

    protected EclipsePainter() {
        _shadowColor = UIDefaultsLookup.getColor("controlShadow");
        _darkShadowColor = UIDefaultsLookup.getColor("controlDkShadow");
        _highlight = UIDefaultsLookup.getColor("controlHighlight");
        _lightHighlightColor = UIDefaultsLookup.getColor("controlLtHighlight");
    }

    @Override
    public void paintButtonBackground(JComponent c, Graphics g, Rectangle rect, int orientation, int state) {
        Color oldColor = g.getColor();
        if (state == STATE_DEFAULT) {
        }
        else if (state == STATE_ROLLOVER) {
            if (orientation == HORIZONTAL) {
                g.setColor(_lightHighlightColor);    // inner 3D border
                g.drawLine(rect.x, rect.y, rect.x + rect.width - 1, rect.y);
                g.drawLine(rect.x, rect.y, rect.x, rect.y + rect.height - 1);

                g.setColor(_shadowColor);     // black drop shadow  __|
                g.drawLine(rect.x, rect.y + rect.height - 1, rect.x + rect.width - 1, rect.y + rect.height - 1);
                g.drawLine(rect.x + rect.width - 1, rect.y, rect.x + rect.width - 1, rect.x + rect.height - 1);
            }
            else {
                g.setColor(_lightHighlightColor);     // black drop shadow  __|
                g.drawLine(rect.x, rect.y, rect.x, rect.y + rect.height - 1);
                g.drawLine(rect.x, rect.y + rect.height - 1, rect.x + rect.width - 1, rect.y + rect.height - 1);

                g.setColor(_shadowColor);    // inner 3D border
                g.drawLine(rect.x, rect.y, rect.x + rect.width - 1, rect.y);
                g.drawLine(rect.x + rect.width - 1, rect.y, rect.x + rect.width - 1, rect.x + rect.height - 1);
            }
        }
        else if (state == STATE_SELECTED) {
            EclipseUtils.fillRectWithHatch((Graphics2D) g,
                    new Rectangle(2, 2, rect.width - 4, rect.height - 4), UIDefaultsLookup.getColor("JideButton.background"));

            if (orientation == HORIZONTAL) {
                g.setColor(_shadowColor);    // inner 3D border
                g.drawLine(rect.x, rect.y, rect.x + rect.width - 1, rect.y);
                g.drawLine(rect.x, rect.y, rect.x, rect.y + rect.height - 1);

                g.setColor(_lightHighlightColor);     // black drop shadow  __|
                g.drawLine(rect.x, rect.y + rect.height - 1, rect.x + rect.width - 1, rect.y + rect.height - 1);
                g.drawLine(rect.x + rect.width - 1, rect.y, rect.x + rect.width - 1, rect.y + rect.height - 1);
            }
            else {
                g.setColor(_shadowColor);    // inner 3D border
                g.drawLine(rect.x, rect.y, rect.x, rect.y + rect.height - 1);
                g.drawLine(rect.x, rect.y + rect.height - 1, rect.x + rect.width - 1, rect.y + rect.height - 1);

                g.setColor(_lightHighlightColor);     // black drop shadow  __|
                g.drawLine(rect.x, rect.y, rect.x + rect.width - 1, rect.y);
                g.drawLine(rect.x + rect.width - 1, rect.y, rect.x + rect.width - 1, rect.y + rect.height - 1);

            }
        }
        else if (state == STATE_PRESSED) {
            g.setColor(_shadowColor);    // inner 3D border
            g.drawLine(rect.x, rect.y, rect.x + rect.width - 1, rect.y);
            g.drawLine(rect.x, rect.y, rect.x, rect.y + rect.height - 1);

            g.setColor(_lightHighlightColor);     // black drop shadow  __|
            g.drawLine(rect.x, rect.y + rect.height - 1, rect.x + rect.width - 1, rect.y + rect.height - 1);
            g.drawLine(rect.x + rect.width - 1, rect.y, rect.x + rect.width - 1, rect.y + rect.height - 1);
        }
        g.setColor(oldColor);
    }

    @Override
    public void paintSelectedMenu(JComponent c, Graphics g, Rectangle rect, int orientation, int state) {
    }

    @Override
    public void paintGripper(JComponent c, Graphics g, Rectangle rect, int orientation, int state) {
        int h = (orientation == SwingConstants.HORIZONTAL) ? rect.height : rect.width;
        h -= 4;

        int y = rect.y + 2;
        int x = rect.x + 2;

        Color oldColor = g.getColor();
        if (orientation == SwingConstants.HORIZONTAL) {
            g.setColor(_lightHighlightColor);
            g.drawLine(x, y, x, y + h);
            g.drawLine(x, y, x + 2, y);
            g.setColor(_shadowColor);
            g.drawLine(x + 2, y, x + 2, y + h);
            g.drawLine(x, y + h, x + 2, y + h);
            g.setColor(UIDefaultsLookup.getColor("JideButton.background")); // was "control"
            g.drawLine(x + 1, y + 1, x + 1, y + h - 1);
        }
        else {
            g.setColor(_lightHighlightColor);
            g.drawLine(x, y, x + h, y);
            g.drawLine(x, y, x, y + 2);
            g.setColor(_shadowColor);
            g.drawLine(x, y + 2, x + h, y + 2);
            g.drawLine(x + h, y, x + h, y + 2);
            g.setColor(UIDefaultsLookup.getColor("JideButton.background")); // was "control"
            g.drawLine(x + 1, y + 1, x + h - 1, y + 1);
        }
        g.setColor(oldColor);
    }

    @Override
    public void paintDockableFrameTitlePane(JComponent c, Graphics g, Rectangle rect, int orientation, int state) {
        int x = rect.x;
        int y = rect.y;
        int w = c.getWidth();
        int h = rect.height;
        if (c.getBorder() != null) {
            Insets insets = c.getBorder().getBorderInsets(c);
            x += insets.left;
            y += insets.top;
            w -= insets.right + insets.left;
            h -= insets.top + insets.bottom;
        }

        g.setColor(Color.white);
        g.drawLine(x, y, x + w, y);
        g.drawLine(x, y, x, y + h - 1);

        g.setColor(Color.gray);
        g.drawLine(x, y + h - 1, x + w, y + h - 1);

        if (state == STATE_SELECTED) {
            int width = rect.width;
            Graphics2D g2d = (Graphics2D) g;
            JideSwingUtilities.fillGradient(g2d, new Rectangle(x + 1, y + 1, width / 2, h - 2), UIDefaultsLookup.getColor("DockableFrame.activeTitleBackground"), UIDefaultsLookup.getColor("DockableFrame.activeTitleBackground2"), false);
            JideSwingUtilities.fillGradient(g2d, new Rectangle(x + 1 + width / 2, y + 1, width / 2, h - 2), UIDefaultsLookup.getColor("DockableFrame.activeTitleBackground2"), UIDefaultsLookup.getColor("DockableFrame.background"), false);
        }
//        else if (state == STATE_DEFAULT) {
//            int width = rect.width;
//            Graphics2D g2d = (Graphics2D) g;
//            JideSwingUtilities.fillGradient(g2d, new Rectangle(x + 1, y + 1, width, h - 2), UIManagerLookup.getColor("DockableFrame.inactiveTitleBackground"), UIManagerLookup.getColor("DockableFrame.background"), false);
//        }
    }

    @Override
    public void paintToolBarSeparator(JComponent c, Graphics g, Rectangle rect, int orientation, int state) {
        int h = (orientation == SwingConstants.HORIZONTAL) ? c.getHeight() : c.getWidth();
        h -= 5;
        int y;
        int x;

        if (JideSwingUtilities.getOrientationOf(c) == SwingConstants.HORIZONTAL) {
            y = rect.y + 3;
            x = rect.x + 1;
            g.setColor(_shadowColor);
            g.drawLine(x, y, x, y + h);
            g.setColor(_lightHighlightColor);
            g.drawLine(x + 1, y + 1, x + 1, y + h + 1);
        }
        else {
            y = rect.y + 1;
            x = rect.x + 3;
            g.setColor(_shadowColor);
            g.drawLine(x, y, x + h, y);
            g.setColor(_lightHighlightColor);
            g.drawLine(x + 1, y + 1, x + 1 + h, y + 1);
        }
    }
}

