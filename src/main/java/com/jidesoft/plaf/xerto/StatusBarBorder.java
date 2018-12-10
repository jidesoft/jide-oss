package com.jidesoft.plaf.xerto;

import javax.swing.border.Border;
import javax.swing.plaf.UIResource;
import java.awt.*;

/**
 * StatusBarBorder
 *
 * @author Created by Jasper Potts (01-Apr-2004)
 * @version 1.0
 */
public class StatusBarBorder implements Border, UIResource {

    /**
     * Returns the insets of the border.
     *
     * @param c the component for which this border insets value applies
     */
    public Insets getBorderInsets(Component c) {
        return new Insets(6, 0, 2, 0);
    }

    /**
     * Returns whether or not the border is opaque.  If the border is opaque, it is responsible for filling in it's own
     * background when painting.
     */
    public boolean isBorderOpaque() {
        return false;
    }

    /**
     * Paints the border for the specified component with the specified position and size.
     *
     * @param c      the component for which this border is being painted
     * @param g      the paint graphics
     * @param x      the x position of the painted border
     * @param y      the y position of the painted border
     * @param width  the width of the painted border
     * @param height the height of the painted border
     */
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        g.setColor(XertoUtils.getControlDarkShadowColor());
        g.drawLine(x, y + 2, x + width, y + 2);
        g.setColor(XertoUtils.getControlMidShadowColor());
        g.drawLine(x, y + 3, x + width, y + 3);
        g.drawLine(x, y + height - 1, x + width, y + height - 1);
        g.setColor(XertoUtils.getControlLightShadowColor());
        g.drawLine(x, y + 4, x + width, y + 4);
        g.drawLine(x, y + height - 2, x + width, y + height - 2);
        g.setColor(XertoUtils.getControlVeryLightShadowColor());
        g.drawLine(x, y + 5, x + width, y + 5);
        g.drawLine(x, y + height - 3, x + width, y + height - 3);
    }
}
