/*
 * @(#)VsnetUtils.java
 *
 * Copyright 2002-2003 JIDE Software. All rights reserved.
 */
package com.jidesoft.plaf.vsnet;

import com.jidesoft.utils.ColorUtils;

import javax.swing.plaf.ColorUIResource;
import java.awt.*;

/**
 */
class VsnetUtils {

    static Color getLighterColor(Color color) {
        if (Color.BLACK.equals(color)) {
            return color;
        }
        else if (Color.WHITE.equals(color)) {
            return color;
        }
        return ColorUtils.getDerivedColor(color, 0.93f);
    }

    static final Color DARK_GREEN = new Color(0, 128, 0);
    static final Color DARK_MAGENTA = new Color(128, 0, 128);

    static Color getMenuSelectionColor(Color color) {
        if (DARK_GREEN.equals(color) || DARK_MAGENTA.equals(color)) {
            return color;
        }
        return ColorUtils.getDerivedColor(color, 0.8555f);
    }

    static Color getMenuBackgroundColor(Color color) {
        return getLighterColor(color);
    }

    static Color getToolBarBackgroundColor(Color color) {
        if (Color.BLACK.equals(color)) {
            return color;
        }
        else if (Color.WHITE.equals(color)) {
            return color;
        }
        return ColorUtils.getDerivedColor(color, 0.645f);
    }

    static Color getGripperForegroundColor(Color color) {
        int r = getGripperValue(color.getRed());
        int g = getGripperValue(color.getGreen());
        int b = getGripperValue(color.getBlue());
        if (r >= 255) r = 255;
        if (g >= 255) g = 255;
        if (b >= 255) b = 255;
        return new ColorUIResource(r, g, b);
    }

    static int getGripperValue(int x) {
        if (x == 255) {
            return 0;
        }
        else if (x >= 0 && x <= 64) {
            return x * 33 / 64 + 123;
        }
        else {
            return (x - 65) * 157 / 189 + 33;
        }
    }

    static Color getDefaultBackgroundColor(Color color) {
        Color backIDE;

        // Check for the 'Classic' control color
        if ((color.getRed() == 212) && (color.getGreen() == 208) && (color.getBlue() == 200)) {
            // Use the exact background for this color
            backIDE = new ColorUIResource(247, 243, 233);
        }
        else if ((color.getRed() == 236) && (color.getGreen() == 233) && (color.getBlue() == 216)) {
            // Check for the 'XP' control color
            // Use the exact background for this color
            backIDE = new ColorUIResource(255, 251, 233);
        }
        else {
            // Calculate the IDE background color as only half as dark as the control color
            int r = color.getRed() + 35;
            int g = color.getGreen() + 35;
            int b = color.getBlue() + 35;
            if (r >= 255) r = 255;
            if (g >= 255) g = 255;
            if (b >= 255) b = 255;
            backIDE = new ColorUIResource(r, g, b);
        }

        return backIDE;
    }

    private static double RATIO1 = 0.80;
    private static double RATIO2 = 0.92f;
    private static double RATIO3 = 0.86f;

    /**
     * Adjusts the ratio we used to derive different colors from a base color.
     *
     * @param selectedAndFocused the ratio for selected and rollover color. Default is 0.755f.
     * @param rollover           the ratio for rollover color. Default is 0.78f.
     * @param selected           the ratio for selected color. Default is 0.86f.
     */
    public static void setColorRatios(double selectedAndFocused, double rollover, double selected) {
        RATIO1 = selectedAndFocused;
        RATIO2 = rollover;
        RATIO3 = selected;
    }

    static int getLightColor(int x, double ratio) {
        return (int) ((255 - x) * ratio + x);
    }

    static Color getLighterColor(Color color, float ratio) {
        if (DARK_GREEN.equals(color) || DARK_MAGENTA.equals(color)) {
            return color;
        }
        return ColorUtils.getDerivedColor(color, ratio);
    }

    static Color getSelectedAndRolloverButtonColor(Color color) {
        return getLighterColor(color, (float) RATIO1);
    }

    static Color getRolloverButtonColor(Color color) {
        return getLighterColor(color, (float) RATIO2);
    }

    static Color getSelectedButtonColor(Color color) {
        return getLighterColor(color, (float) RATIO3);
    }

    static Color getButtonBorderColor(Color color) {
        if (DARK_GREEN.equals(color) || DARK_MAGENTA.equals(color)) {
            return new ColorUIResource(Color.WHITE);
        }
        return color;
    }
}
