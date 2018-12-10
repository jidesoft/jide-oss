/*
 * @(#)VsnetUtils.java
 *
 * Copyright 2002-2003 JIDE Software. All rights reserved.
 */
package com.jidesoft.plaf.eclipse;

import java.awt.*;

/**
 */
public class EclipseUtils {

    static Color getLighterColor(Color backColor) {
        int r = getLighterColor(backColor.getRed());
        int g = getLighterColor(backColor.getGreen());
        int b = getLighterColor(backColor.getBlue());
        if (r >= 255) r = 255;
        if (g >= 255) g = 255;
        if (b >= 255) b = 255;
        return new Color(r, g, b);
    }

    static int getLighterColor(int x) {
        return (int) (x * 36.0 / 255.0 + 219.5);
    }


    static Color getMenuSelectionColor(Color backColor) {
        int r = getMenuSelectionValue(backColor.getRed());
        int g = getMenuSelectionValue(backColor.getGreen());
        int b = getMenuSelectionValue(backColor.getBlue());
        if (r >= 255) r = 255;
        if (g >= 255) g = 255;
        if (b >= 255) b = 255;
        return new Color(r, g, b);
    }

    static int getMenuSelectionValue(int x) {
        return (int) (x * 76.0 / 255.0 + 179.5);
    }

    static Color getMenuBackgroundColor(Color backColor) {
        int r = getMenuValue(backColor.getRed());
        int g = getMenuValue(backColor.getGreen());
        int b = getMenuValue(backColor.getBlue());
        if (r >= 255) r = 255;
        if (g >= 255) g = 255;
        if (b >= 255) b = 255;
        return new Color(r, g, b);
    }

    static int getMenuValue(int x) {
        return (int) (x * 36.0 / 255.0 + 219.5);
    }

    static Color getDefaultBackgroundColor(Color backColor) {
        Color backIDE;

        // Check for the 'Classic' control color
        if ((backColor.getRed() == 212) && (backColor.getGreen() == 208) && (backColor.getBlue() == 200)) {
            // Use the exact background for this color
            backIDE = new Color(247, 243, 233);
        }
        else if ((backColor.getRed() == 236) && (backColor.getGreen() == 233) && (backColor.getBlue() == 216)) {
            // Check for the 'XP' control color
            // Use the exact background for this color
            backIDE = new Color(255, 251, 233);
        }
        else {
            // Calculate the IDE background color as only half as dark as the control color
            int r = backColor.getRed() + 35;
            int g = backColor.getGreen() + 35;
            int b = backColor.getBlue() + 35;
            if (r >= 255) r = 255;
            if (g >= 255) g = 255;
            if (b >= 255) b = 255;
            backIDE = new Color(r, g, b);
        }

        return backIDE;
    }

    private static final BasicStroke DOTTED_STROKE = new BasicStroke(1.0f, BasicStroke.CAP_SQUARE,
            BasicStroke.JOIN_ROUND, 1.0f, new float[]{0, 2, 0, 2}, 0);

    public static void fillRectWithHatch(Graphics2D g, Rectangle rect, Color color) {
        Stroke oldStroke = g.getStroke();

        g.setColor(Color.white);
        g.fillRect(rect.x, rect.y, rect.width, rect.height);

        g.setColor(color);
        g.setStroke(DOTTED_STROKE);

        for (int i = 0; i < rect.width; i++) {
            if (i % 2 == 0) {
                g.drawLine(rect.x + i, rect.y, rect.x + i, rect.y + rect.height - 1);
            }
            else {
                g.drawLine(rect.x + i, rect.y + 1, rect.x + i, rect.y + rect.height - 1);
            }
        }
        g.setStroke(oldStroke);
    }

    static final double RATIO1 = 0.67;
    static final double RATIO2 = 0.78;
    static final double RATIO3 = 0.86;

    static int getLightColor(int x, double ratio) {
        return (int) ((255 - x) * ratio + x);
    }

    static Color getLighterColor(Color backColor, double ratio) {
        int r = getLightColor(backColor.getRed(), ratio);
        int g = getLightColor(backColor.getGreen(), ratio) + 1;
        int b = getLightColor(backColor.getBlue(), ratio);
        if (r >= 255) r = 255;
        if (g >= 255) g = 255;
        if (b >= 255) b = 255;
        return new Color(r, g, b);
    }

    static Color getSelectedAndFocusedButtonColor(Color backColor) {
        return getLighterColor(backColor, RATIO1);
    }

    static Color getFocusedButtonColor(Color backColor) {
        return getLighterColor(backColor, RATIO2);
    }

    static Color getSelectedButtonColor(Color backColor) {
        return getLighterColor(backColor, RATIO3);
    }
}
