/*
 * @(#)XPUtils.java
 *
 * Copyright 2002 - 2004 JIDE Software Inc. All rights reserved.
 */
package com.jidesoft.plaf;

import com.jidesoft.utils.SystemInfo;

import java.awt.*;

/**
 * Util class for XP style.
 */
public class XPUtils {
    public static final String PROPERTY_THEMEACTIVE = "win.xpstyle.themeActive";
    public static final String PROPERTY_COLORNAME = "win.xpstyle.colorName";
    public static final String PROPERTY_DLLNAME = "win.xpstyle.dllName";
    public static final String DEFAULT = "Default";
    public static final String GRAY = "Gray";
    public static final String BLUE = "NormalColor";
    public static final String HOMESTEAD = "HomeStead";
    public static final String METALLIC = "Metallic";

    /**
     * Checks if the XP style is on. Even on Windows XP OS, user can choose Classic style
     * or XP style. This method will tell you if XP style is on.
     * <p/>
     * Please note it will return the correct value only if it's
     * jdk1.4.2 and above. Otherwise it will throw UnsupportedOperationException.
     *
     * @return true if XP style in on.
     * @throws UnsupportedOperationException if jdk version is not 1.4.2 or above.
     */
    public static boolean isXPStyleOn() throws UnsupportedOperationException {
        if (SystemInfo.isJdk142Above()) {
            Toolkit toolkit = Toolkit.getDefaultToolkit();
            return Boolean.TRUE.equals(toolkit.getDesktopProperty(PROPERTY_THEMEACTIVE));
        }
        else {
            throw new UnsupportedOperationException("JDK 1.4.2 and up is required to support this method call.");
        }
    }

    /**
     * Gets the color name. On Windows XP, it could be one of the values BLUE, HOMESTEAD or METALLIC. If XP style is not on
     * or the system is not Windows XP at all, it will return null.
     * <p/>
     * Please note it will return the correct value only if it's
     * jdk1.4.2 and above. Otherwise it will throw UnsupportedOperationException.
     *
     * @return the color name of XP theme.
     * @throws UnsupportedOperationException if jdk version is not 1.4.2 or above.
     */
    public static String getColorName() throws UnsupportedOperationException {
        if (SystemInfo.isJdk142Above()) {
            Toolkit toolkit = Toolkit.getDefaultToolkit();
            return (String) toolkit.getDesktopProperty(PROPERTY_COLORNAME);
        }
        else {
            throw new UnsupportedOperationException("JDK 1.4.2 and up is required to support this method call.");
        }
    }

    private static String getXPStyleDll() {
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        return (String) toolkit.getDesktopProperty(PROPERTY_DLLNAME);
    }


    public static void main(String[] args) {
        try {
            System.out.println(XPUtils.isXPStyleOn());
        }
        catch (UnsupportedOperationException e) {
            System.out.println("Unknown XP style because " + e.getMessage());
        }
        try {
            System.out.println(XPUtils.getColorName());
        }
        catch (UnsupportedOperationException e) {
            System.out.println("Unknown XP color because " + e.getMessage());
        }
        System.out.println(XPUtils.getXPStyleDll());
    }
}
