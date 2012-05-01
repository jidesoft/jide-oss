/*
 * @(#)${NAME}
 *
 * Copyright 2002 - 2004 JIDE Software Inc. All rights reserved.
 */
package com.jidesoft.plaf.aqua;

import com.jidesoft.icons.IconsFactory;
import com.jidesoft.plaf.UIDefaultsLookup;
import com.jidesoft.plaf.basic.BasicPainter;
import com.jidesoft.plaf.basic.ThemePainter;
import com.jidesoft.swing.JideSwingUtilities;

import javax.swing.*;
import java.awt.*;
import java.util.logging.Logger;

/**
 * Painter for Aqua style L&F.
 * <p/>
 * Please note, this class is an internal class which is meant to be used by other JIDE classes only. Future version
 * might break your build if you use it.
 */
public class AquaPainter extends BasicPainter {
    private static final Logger LOGGER = Logger.getLogger(AquaPainter.class.getName());

    private static AquaPainter _instance;
    private static final ImageIcon SELECTED = IconsFactory.getImageIcon(AquaPainter.class, "icons/selected.gif");
    private static final ImageIcon ROLLOVER = IconsFactory.getImageIcon(AquaPainter.class, "icons/rollover.gif");
    private static final ImageIcon PRESSED = IconsFactory.getImageIcon(AquaPainter.class, "icons/pressed.gif");
    private static final Color ROLLOVER_BACKGROUND = new Color(238, 238, 238);
    private static final Color SELECTED_BACKGROUND = new Color(153, 153, 153);
    private static final Color PRESSED_BACKGROUND = new Color(195, 195, 195);
    private static boolean _errorOccurred;

    public static ThemePainter getInstance() {
        if (_instance == null) {
            _instance = new AquaPainter();
        }
        return _instance;
    }

    public AquaPainter() {
    }

    @Override
    public Color getCommandBarTitleBarBackground() {
        return UIDefaultsLookup.getColor("JideButton.background");
    }

    @Override
    public void paintButtonBackground(JComponent c, Graphics g, Rectangle rect, int orientation, int state) {
        if (state == STATE_DEFAULT) {
            // use rollover effect to paint the button if the content filled is true. mainly for FloorTabbedPane and OutlookTabbedPane
            if (c.isOpaque() && (c instanceof AbstractButton) && ((AbstractButton) c).isContentAreaFilled()) {
                paintImageBorder(g, rect, ROLLOVER, ROLLOVER_BACKGROUND);
            }
            else {
                super.paintButtonBackground(c, g, rect, orientation, state);
            }
        }
        else if (state == STATE_ROLLOVER) {
            paintImageBorder(g, rect, ROLLOVER, ROLLOVER_BACKGROUND);
        }
        else if (state == STATE_SELECTED) {
            paintImageBorder(g, rect, SELECTED, SELECTED_BACKGROUND);
        }
        else if (state == STATE_PRESSED) {
            paintImageBorder(g, rect, PRESSED, PRESSED_BACKGROUND);
        }
    }

    private void paintImageBorder(Graphics g, Rectangle rect, ImageIcon icon, Color background) {
        JideSwingUtilities.drawImageBorder(g, icon, rect, new Insets(3, 3, 3, 3), false);

        if (background != null) {
            Color oldColor = g.getColor();
            g.setColor(background);
            g.fillRect(rect.x + 3, rect.y + 3, rect.width - 6, rect.height - 6);
            g.setColor(oldColor);
        }
    }

    @Override
    public void paintCollapsiblePaneTitlePaneBackgroundEmphasized(JComponent c, Graphics g, Rectangle rect, int orientation, int state) {
        if (!_errorOccurred) {
            try {
                drawFrameTitleBackground((Graphics2D) g, rect.x, rect.y, rect.width, rect.height, true, false, false);
                return;
            }
            catch (Exception e) {
                _errorOccurred = true;
                LOGGER.warning(e.getLocalizedMessage());
            }
        }
        super.paintCollapsiblePaneTitlePaneBackgroundEmphasized(c, g, rect, orientation, state);
    }

    public void paintCollapsiblePaneTitlePaneBackground(JComponent c, Graphics g, Rectangle rect, int orientation, int state) {
        if (!_errorOccurred) {
            try {
                drawFrameTitleBackground((Graphics2D) g, rect.x, rect.y, rect.width, rect.height, false, false, false);
                return;
            }
            catch (Exception e) {
                _errorOccurred = true;
                LOGGER.warning(e.getLocalizedMessage());
            }
        }
        super.paintCollapsiblePaneTitlePaneBackground(c, g, rect, orientation, state);
    }

    public void paintDockableFrameTitlePane(JComponent c, Graphics g, Rectangle rect, int orientation, int state) {
        if (!_errorOccurred) {
            try {
                drawFrameTitleBackground((Graphics2D) g, rect.x, rect.y, rect.width, rect.height, state == STATE_SELECTED, false, false);
                return;
            }
            catch (Exception e) {
                _errorOccurred = true;
                LOGGER.warning(e.getLocalizedMessage());
            }
        }
        super.paintDockableFrameTitlePane(c, g, rect, orientation, state);
    }

    @Override
    public void paintCommandBarTitlePane(JComponent c, Graphics g, Rectangle rect, int orientation, int state) {
        if (!_errorOccurred) {
            try {
                drawFrameTitleBackground((Graphics2D) g, rect.x, rect.y, rect.width, rect.height, true, false, false);
                return;
            }
            catch (Exception e) {
                _errorOccurred = true;
                LOGGER.warning(e.getLocalizedMessage());
            }
        }
        super.paintCommandBarTitlePane(c, g, rect, orientation, state);
    }


    private static Color ACTIVE_TOP_GRADIENT_COLOR = new Color(0xbcbcbc);
    private static Color ACTIVE_BOTTOM_GRADIENT_COLOR = new Color(0x9a9a9a);
    private static Color INACTIVE_TOP_GRADIENT_COLOR = new Color(0xe4e4e4);
    private static Color INACTIVE_BOTTOM_GRADIENT_COLOR = new Color(0xd1d1d1);


    private void drawFrameTitleBackground(Graphics2D g, int x, int y, int w, int h, boolean active, boolean c, boolean d) {
        Color topColor = active ? ACTIVE_TOP_GRADIENT_COLOR : INACTIVE_TOP_GRADIENT_COLOR;
        Color bottomColor = active ? ACTIVE_BOTTOM_GRADIENT_COLOR : INACTIVE_BOTTOM_GRADIENT_COLOR;
        JideSwingUtilities.fillGradient(g, new Rectangle(x, y, w, h), topColor, bottomColor, true);
    }
}

