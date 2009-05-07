/*
 * @(#)${NAME}
 *
 * Copyright 2002 - 2004 JIDE Software Inc. All rights reserved.
 */
package com.jidesoft.plaf.aqua;

import apple.laf.AquaImageFactory;
import com.jidesoft.icons.IconsFactory;
import com.jidesoft.plaf.UIDefaultsLookup;
import com.jidesoft.plaf.basic.BasicPainter;
import com.jidesoft.plaf.basic.ThemePainter;
import com.jidesoft.swing.JideSwingUtilities;
import sun.java2d.SunGraphics2D;

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
            super.paintButtonBackground(c, g, rect, orientation, state);
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
                AquaImageFactory.drawFrameTitleBackground((SunGraphics2D) g, rect.x, rect.y, rect.width, rect.height, true, false, false);
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
                AquaImageFactory.drawFrameTitleBackground((SunGraphics2D) g, rect.x, rect.y, rect.width, rect.height, false, false, false);
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
                AquaImageFactory.drawFrameTitleBackground((SunGraphics2D) g, rect.x, rect.y, rect.width, rect.height, state == STATE_SELECTED, false, false);
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
                AquaImageFactory.drawFrameTitleBackground((SunGraphics2D) g, rect.x, rect.y, rect.width, rect.height, true, false, false);
                return;
            }
            catch (Exception e) {
                _errorOccurred = true;
                LOGGER.warning(e.getLocalizedMessage());
            }
        }
        super.paintCommandBarTitlePane(c, g, rect, orientation, state);
    }
}

