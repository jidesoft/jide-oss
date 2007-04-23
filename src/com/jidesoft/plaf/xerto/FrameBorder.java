package com.jidesoft.plaf.xerto;

import com.jidesoft.plaf.UIDefaultsLookup;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.plaf.UIResource;
import java.awt.*;

/**
 * FrameBorder - Simple single line border with small drop shadow
 *
 * @author Created by Jasper Potts (21-Jun-2004)
 * @version 1.0
 */
public class FrameBorder implements Border, UIResource {

    private static final Insets INSETS = new Insets(1, 1, 3, 3);

    /**
     * Returns the insets of the border.
     *
     * @param c the component for which this border insets value applies
     */
    public Insets getBorderInsets(Component c) {
        return INSETS;
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
        g.setColor(XertoUtils.getFrameBorderColor());
        g.drawLine(x, y, x + width - 3, y);
        g.drawLine(x, y, x, y + height - 3);
        g.drawLine(x + width - 3, y, x + width - 3, y + height - 3);
        g.drawLine(x, y + height - 3, x + width - 3, y + height - 3);
        g.setColor(XertoUtils.getControlColor());
        g.fillRect(x + width - 2, y, 2, 2);
        g.fillRect(x, y + height - 2, 2, 2);
        g.setColor(XertoUtils.getControlMidShadowColor());
        g.drawLine(x + width - 2, y + 1, x + width - 2, y + height - 2);
        g.drawLine(x + 1, y + height - 2, x + width - 2, y + height - 2);
        g.setColor(XertoUtils.getControlLightShadowColor());
        g.drawLine(x + width - 1, y + 2, x + width - 1, y + height - 1);


        if ("DockableFrameUI".equals(((JComponent) c).getUIClassID()) && c.getParent().getComponentCount() > 1) {
            g.setColor(UIDefaultsLookup.getColor("JideTabbedPane.selectedTabBackground"));
            g.drawLine(x + 2, y + height - 1, x + width - 2, y + height - 1);
        }
        else {
            g.setColor(XertoUtils.getControlLightShadowColor());
            g.drawLine(x + 2, y + height - 1, x + width - 1, y + height - 1);
        }

    }
}
