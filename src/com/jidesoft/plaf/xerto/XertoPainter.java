/*
 * @(#)XertoPainter.java 3/22/2005
 *
 * Copyright 2002 - 2005 JIDE Software Inc. All rights reserved.
 */
package com.jidesoft.plaf.xerto;

import com.jidesoft.icons.IconsFactory;
import com.jidesoft.plaf.basic.BasicPainter;
import com.jidesoft.plaf.basic.ThemePainter;
import com.jidesoft.swing.JideSwingUtilities;

import javax.swing.*;
import java.awt.*;

/**
 * Painter for Xerto L&F.
 * <p/>
 * Please note, this class is an internal class which is meant to be used by other JIDE classes only.
 * Future version might break your build if you use it.
 */
public class XertoPainter extends BasicPainter {

    private static XertoPainter _instance;
    private final static ImageIcon SELECTED = IconsFactory.getImageIcon(XertoPainter.class, "icons/selected.gif");
    private final static ImageIcon SELECTED_C = IconsFactory.getImageIcon(XertoPainter.class, "icons/selected_c.gif");
    private final static ImageIcon ROLLOVER = IconsFactory.getImageIcon(XertoPainter.class, "icons/rollover.gif");
    private final static ImageIcon ROLLOVER_C = IconsFactory.getImageIcon(XertoPainter.class, "icons/rollover_c.gif");
    private final static ImageIcon PRESSED = IconsFactory.getImageIcon(XertoPainter.class, "icons/pressed.gif");
    private final static ImageIcon PRESSED_C = IconsFactory.getImageIcon(XertoPainter.class, "icons/pressed_c.gif");

    public static ThemePainter getInstance() {
        if (_instance == null) {
            _instance = new XertoPainter();
        }
        return _instance;
    }

    protected XertoPainter() {
    }

//    public void paintContentBackground(JComponent c, Graphics g, Rectangle rect, int orientation, int state) {
//        JideSwingUtilities.fillGradient((Graphics2D) g, rect,
//                XertoUtils.getMidControlColor(), XertoUtils.getControlColor(), false);
//    }
//

    public void paintCollapsiblePaneTitlePaneBackground(JComponent c, Graphics g, Rectangle rect, int orientation, int state) {
        Graphics2D g2d = (Graphics2D) g;
        Color gradientBot = c.getBackground();
        Color gradientTop = XertoUtils.getHeighlightColor(gradientBot);
        JideSwingUtilities.fillGradient(g2d, rect, gradientTop, gradientBot, true);
    }

    public void paintCollapsiblePaneTitlePaneBackgroundEmphasized(JComponent c, Graphics g, Rectangle rect, int orientation, int state) {
        Graphics2D g2d = (Graphics2D) g;
        Color gradientBot = XertoUtils.getEmBaseColor(c.getBackground());
        Color gradientTop = c.getBackground();
        JideSwingUtilities.fillGradient(g2d, rect, gradientTop, gradientBot, true);
    }

    public void paintMenuItemBackground(JComponent c, Graphics g, Rectangle rect, int orientation, int state, boolean showBorder) {
        super.paintMenuItemBackground(c, g, rect, orientation, state, showBorder);
    }

    public void paintButtonBackground(JComponent c, Graphics g, Rectangle rect, int orientation, int state) {
        if (state == STATE_DEFAULT) {
            super.paintButtonBackground(c, g, rect, orientation, state);
        }
        else if (state == STATE_ROLLOVER) {
            paintImageBorder(g, rect, ROLLOVER, ROLLOVER_C, null);
        }
        else if (state == STATE_SELECTED) {
            paintImageBorder(g, rect, SELECTED, SELECTED_C, Color.WHITE);
        }
        else if (state == STATE_PRESSED) {
            paintImageBorder(g, rect, PRESSED, PRESSED_C, null);
        }
    }

    private void paintImageBorder(Graphics g, Rectangle rect, ImageIcon icon, ImageIcon center, Color background) {
        JideSwingUtilities.drawImageBorder(g, icon, rect, new Insets(4, 4, 4, 4), false);

        if (center == null) {
            Color oldColor = g.getColor();
            g.setColor(background);
            g.fillRect(rect.x + 4, rect.y + 4, rect.width - 8, rect.height - 8);
            g.setColor(oldColor);
        }
        else {
            g.drawImage(center.getImage(), rect.x + 4, rect.y + 4, rect.x + rect.width - 4, rect.y + rect.height - 4,
                    0, 0, center.getIconWidth(), center.getIconHeight(), background, null);
        }
    }

    public void paintGripper(JComponent c, Graphics g, Rectangle rect, int orientation, int state) {
        if (rect.width > 30) {
            orientation = SwingConstants.VERTICAL;
        }

        int h = (orientation == SwingConstants.HORIZONTAL) ? rect.height : rect.width;
        int count = Math.min(9, (h - 6) / 4);
        int y = rect.y;
        int x = rect.x;

        if (orientation == SwingConstants.HORIZONTAL) {
            y += rect.height / 2 - count * 2;
            x += rect.width / 2 - 1;
        }
        else {
            x += rect.width / 2 - count * 2;
            y += rect.height / 2 - 1;
        }

        for (int i = 0; i < count; i++) {
            g.setColor(getGripperForegroundLt());
            g.fillRect(x + 1, y + 1, 2, 2);
            g.setColor(XertoUtils.getControlMidShadowColor());
            g.fillRect(x, y, 2, 2);
            g.setColor(XertoUtils.getControlLightShadowColor());
            g.fillRect(x, y, 1, 1);
            g.setColor(XertoUtils.getControlDarkShadowColor());
            g.fillRect(x + 1, y + 1, 1, 1);

            if (orientation == SwingConstants.HORIZONTAL) {
                y += 4;
            }
            else {
                x += 4;
            }
        }
    }


    public void paintDockableFrameTitlePane(JComponent c, Graphics g, Rectangle rect, int orientation, int state) {
        Graphics2D g2d = (Graphics2D) g;
        if (ThemePainter.STATE_SELECTED == state) {
            JideSwingUtilities.fillGradient(g2d, new Rectangle(rect.x, rect.y, rect.width, rect.height),
                    XertoUtils.getFrameActiveTitleTopColor(), XertoUtils.getFrameActiveTitleBottomColor(), orientation == SwingConstants.HORIZONTAL);
        }
        else {
            JideSwingUtilities.fillGradient(g2d, new Rectangle(rect.x, rect.y, rect.width, rect.height),
                    XertoUtils.getFrameInactiveTitleTopColor(), XertoUtils.getFrameInactiveTitleBottomColor(), orientation == SwingConstants.HORIZONTAL);
        }
    }

    public Color getGripperForeground() {
        return XertoUtils.getControlLightShadowColor();
    }

    public Color getGripperForegroundLt() {
        return Color.WHITE;
    }

    public Color getSelectionSelectedDk() {
        return XertoUtils.getControlMidShadowColor();
    }

    public Color getSelectionSelectedLt() {
        return XertoUtils.getControlLightShadowColor();
    }

}