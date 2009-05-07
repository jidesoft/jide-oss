package com.jidesoft.plaf.xerto;

import com.jidesoft.plaf.UIDefaultsLookup;

import javax.swing.*;
import java.awt.*;


/**
 * XertoUtils
 *
 * @author Created by Jasper Potts (17-Mar-2005)
 */
public class XertoUtils {
    // =================================================================================================================
    // Xerto Color Methods

    private static Color BASE_COLOR;
    private static Color CONTROL_COLOR;
    private static Color LIGHT_CONTROL_COLOR;
    private static Color MID_CONTROL_COLOR;
    private static Color CONTROL_VERY_LIGHT_SHADOW_COLOR;
    private static Color CONTROL_LIGHT_SHADOW_COLOR;
    private static Color CONTROL_MID_SHADOW_COLOR;
    private static Color CONTROL_DARK_SHADOW_COLOR;

    private static Color SELECTION_COLOR;
    private static Color INACTIVE_CAPTION_COLOR;
    private static Color SELECTED_TAB_BACKGROUND_COLOR;
    private static Color TAB_FORGROUND_COLOR;
    private static Color FRAME_ACTIVE_TITLE_TOP_COLOR;
    private static Color FRAME_ACTIVE_TITLE_BOTTOM_COLOR;
    private static Color FRAME_INACTIVE_TITLE_TOP_COLOR;
    private static Color FRAME_INACTIVE_TITLE_BOTTOM_COLOR;

    static {
        updateColors();
    }

    /**
     * Updates the colors based on the  current UIDefaults
     */
    public static void updateColors() {
        UIDefaults uiDefaults = UIManager.getDefaults();
        BASE_COLOR = uiDefaults.getColor("activeCaption");
        CONTROL_COLOR = uiDefaults.getColor("control");
        INACTIVE_CAPTION_COLOR = uiDefaults.getColor("inactiveCaption");
        LIGHT_CONTROL_COLOR = uiDefaults.getColor("MenuItem.background");
        // get hsb base colors
        float[] oControlHSB = Color.RGBtoHSB(CONTROL_COLOR.getRed(), CONTROL_COLOR.getGreen(),
                CONTROL_COLOR.getBlue(), null);
        float[] oBaseHSB = Color.RGBtoHSB(BASE_COLOR.getRed(), BASE_COLOR.getGreen(), BASE_COLOR.getBlue(), null);
        // calculate colors
        MID_CONTROL_COLOR = Color.getHSBColor(oControlHSB[0], Math.max(oControlHSB[1] - 0.05f, 0),
                (oControlHSB[2] < 0.95f) ? oControlHSB[2] + 0.05f : 0.98f);
        SELECTED_TAB_BACKGROUND_COLOR = Color.getHSBColor(oControlHSB[0], oControlHSB[1],
                (oControlHSB[1] == 0) ? 0.75f : 0.85f);
        TAB_FORGROUND_COLOR = Color.getHSBColor(oControlHSB[0], (oControlHSB[1] > 0.01) ? 0.45f : oControlHSB[0], 0.2f);
        CONTROL_VERY_LIGHT_SHADOW_COLOR = Color.getHSBColor(oControlHSB[0], oControlHSB[1], oControlHSB[2] - 0.02f);
        CONTROL_LIGHT_SHADOW_COLOR = Color.getHSBColor(oControlHSB[0], oControlHSB[1], oControlHSB[2] - 0.06f);
        CONTROL_MID_SHADOW_COLOR = Color.getHSBColor(oControlHSB[0], oControlHSB[1], oControlHSB[2] - 0.16f);
        CONTROL_DARK_SHADOW_COLOR = Color.getHSBColor(oControlHSB[0], oControlHSB[1], oControlHSB[2] - 0.32f);
        SELECTION_COLOR = Color.getHSBColor(oBaseHSB[0], (oBaseHSB[1] > 0.01) ? 0.45f : oBaseHSB[0], 0.80f);
        FRAME_ACTIVE_TITLE_TOP_COLOR = Color.getHSBColor(oBaseHSB[0], (oBaseHSB[1] > 0.01) ? 0.3f : oBaseHSB[0], 0.90f);
        FRAME_ACTIVE_TITLE_BOTTOM_COLOR = Color.getHSBColor(oBaseHSB[0], (oBaseHSB[1] > 0.01) ? 0.45f : oBaseHSB[0], 0.70f);
        FRAME_INACTIVE_TITLE_TOP_COLOR = Color.getHSBColor(oControlHSB[0], oControlHSB[1], 0.75f);
        FRAME_INACTIVE_TITLE_BOTTOM_COLOR = Color.getHSBColor(oControlHSB[0], oControlHSB[1], 0.5f);
    }

    public static Color getBaseColor() {
        return BASE_COLOR;
    }

    public static Color getInActiveCaptionColor() {
        return INACTIVE_CAPTION_COLOR;
    }

    public static Color getControlColor() {
        return CONTROL_COLOR;
    }

    public static Color getMidControlColor() {
        return MID_CONTROL_COLOR;
    }

    public static Color getLightControlColor() {
        return LIGHT_CONTROL_COLOR;
    }

    public static Color getSelectedTabBackgroundColor() {
        return SELECTED_TAB_BACKGROUND_COLOR;
    }

    public static Color getTabForgroundColor() {
        return TAB_FORGROUND_COLOR;
    }

    public static Color getControlVeryLightShadowColor() {
        return CONTROL_VERY_LIGHT_SHADOW_COLOR;
    }

    public static Color getControlLightShadowColor() {
        return CONTROL_LIGHT_SHADOW_COLOR;
    }

    public static Color getControlMidShadowColor() {
        return CONTROL_MID_SHADOW_COLOR;
    }

    public static Color getControlDarkShadowColor() {
        return CONTROL_DARK_SHADOW_COLOR;
    }

    public static Color getSelectionColor() {
        return SELECTION_COLOR;
    }

    public static Color getApplicationFrameBackgroundColor() {
        return getControlColor();
    }

    public static Color getFrameBorderColor() {
        return UIDefaultsLookup.getColor("controlShadow");
    }

    public static Color getFrameActiveTitleTopColor() {
        return FRAME_ACTIVE_TITLE_TOP_COLOR;
    }

    public static Color getFrameActiveTitleBottomColor() {
        return FRAME_ACTIVE_TITLE_BOTTOM_COLOR;
    }

    public static Color getFrameInactiveTitleTopColor() {
        return FRAME_INACTIVE_TITLE_TOP_COLOR;
    }

    public static Color getFrameInactiveTitleBottomColor() {
        return FRAME_INACTIVE_TITLE_BOTTOM_COLOR;
    }

    // =================================================================================================================
    // Jide Color Methods

    public static Color getLighterColor(Color backColor) {
        int r = getLighterColor(backColor.getRed());
        int g = getLighterColor(backColor.getGreen());
        int b = getLighterColor(backColor.getBlue());
        if (r >= 255) r = 255;
        if (g >= 255) g = 255;
        if (b >= 255) b = 255;
        return new Color(r, g, b);
    }

    public static int getLighterColor(int x) {
        return (int) (x * 36.0 / 255.0 + 219.5);
    }


    public static Color getMenuSelectionColor(Color backColor) {
        int r = getMenuSelectionValue(backColor.getRed());
        int g = getMenuSelectionValue(backColor.getGreen());
        int b = getMenuSelectionValue(backColor.getBlue());
        if (r >= 255) r = 255;
        if (g >= 255) g = 255;
        if (b >= 255) b = 255;
        return new Color(r, g, b);
    }

    public static int getMenuSelectionValue(int x) {
        return (int) (x * 76.0 / 255.0 + 179.5);
    }

    public static Color getMenuBackgroundColor(Color color) {
        if (Color.BLACK.equals(color)) {
            return color;
        }
        else if (Color.WHITE.equals(color)) {
            return color;
        }
        int r = getMenuValue(color.getRed());
        int g = getMenuValue(color.getGreen());
        int b = getMenuValue(color.getBlue());
        if (r >= 255) r = 255;
        if (g >= 255) g = 255;
        if (b >= 255) b = 255;
        return new Color(r, g, b);
    }

    public static int getMenuValue(int x) {
        return (int) (x * 36.0 / 255.0 + 219.5);
    }

    public static Color getToolBarBackgroundColor(Color color) {
        if (Color.BLACK.equals(color)) {
            return color;
        }
        else if (Color.WHITE.equals(color)) {
            return color;
        }
        int r = getToolBarValue(color.getRed());
        int g = getToolBarValue(color.getGreen());
        int b = getToolBarValue(color.getBlue());
        if (r >= 255) r = 255;
        if (g >= 255) g = 255;
        if (b >= 255) b = 255;
        return new Color(r, g, b);
    }

    public static int getToolBarValue(int x) {
        return x * 215 / 255 + 40;
    }

    public static Color getGripperForegroundColor(Color backColor) {
        int r = getGripperValue(backColor.getRed());
        int g = getGripperValue(backColor.getGreen());
        int b = getGripperValue(backColor.getBlue());
        if (r >= 255) r = 255;
        if (g >= 255) g = 255;
        if (b >= 255) b = 255;
        return new Color(r, g, b);
    }

    public static int getGripperValue(int x) {
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

    public static Color getDefaultBackgroundColor(Color backColor) {
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

    final public static double RATIO1 = 0.67;
    final public static double RATIO2 = 0.78;
    final public static double RATIO3 = 0.86;

    static final Color DARK_GREEN = new Color(0, 128, 0);
    static final Color DARK_MAGENTA = new Color(128, 0, 128);

    public static int getLightColor(int x, double ratio) {
        return (int) ((255 - x) * ratio + x);
    }

    public static Color getLighterColor(Color color, double ratio) {
        if (DARK_GREEN.equals(color) || DARK_MAGENTA.equals(color)) {
            return color;
        }
        int r = getLightColor(color.getRed(), ratio);
        int g = getLightColor(color.getGreen(), ratio) + 1;
        int b = getLightColor(color.getBlue(), ratio);
        if (r >= 255) r = 255;
        if (g >= 255) g = 255;
        if (b >= 255) b = 255;
        return new Color(r, g, b);
    }

    public static Color getHighlightColor(Color bottomColor) {
        float[] oHSB = new float[3];
        Color.RGBtoHSB(bottomColor.getRed(), bottomColor.getGreen(), bottomColor.getBlue(), oHSB);
        oHSB[1] -= 0.07f;
        if (oHSB[1] < 0) oHSB[1] = 0f;
        oHSB[2] += 0.04f;
        if (oHSB[2] > 1) oHSB[2] = 1f;
        return Color.getHSBColor(oHSB[0], oHSB[1], oHSB[2]);
    }

    public static Color getEmBaseColor(Color bottomColor) {
        float[] oHSB = new float[3];
        Color.RGBtoHSB(bottomColor.getRed(), bottomColor.getGreen(), bottomColor.getBlue(), oHSB);
        oHSB[1] += 0.1f;
        if (oHSB[1] > 1) oHSB[1] = 1f;
        oHSB[2] -= 0.1f;
        if (oHSB[2] < 0) oHSB[2] = 0f;
        return Color.getHSBColor(oHSB[0], oHSB[1], oHSB[2]);
    }

    public static Color getTextColor(Color backgroundColor) {
        float[] oHSB = new float[3];
        Color.RGBtoHSB(backgroundColor.getRed(), backgroundColor.getGreen(), backgroundColor.getBlue(), oHSB);
        return (oHSB[2] > 0.53) ? UIDefaultsLookup.getColor("controlText") : Color.WHITE;
    }

    public static Color getSelectedAndFocusedButtonColor(Color backColor) {
        return getLighterColor(backColor, RATIO1);
    }

    public static Color getFocusedButtonColor(Color backColor) {
        return getLighterColor(backColor, RATIO2);
    }

    public static Color getSelectedButtonColor(Color backColor) {
        return getLighterColor(backColor, RATIO3);
    }
}
