/*
 * @(#)SecurityUtils.java
 *
 * Copyright 2002 - 2004 JIDE Software Inc. All rights reserved.
 */
package com.jidesoft.utils;

import javax.swing.plaf.FontUIResource;
import java.awt.*;
import java.io.IOException;
import java.security.AccessControlException;
import java.util.Hashtable;
import java.util.Locale;
import java.util.MissingResourceException;

/**
 * A class that keeps all the security stuff so that an application can safely run in applet or webstart environment.
 * Please refer to JIDE_Developer_Guide_for_Webstart_Applet.pdf in doc folder for more information.
 */
public class SecurityUtils {
    public static FontUIResource createFontUIResource(String name, int style, int size) {
        Font font = createFont(name, style, size);
        if (font != null) {
            return new FontUIResource(font);
        }
        else {
            return null;
        }
    }

    static class FontStruct {
        String font;
        int style;
    }

    public static final String BOLD = "Bold";
    public static final String ITALIC = "Italic";
    public static final String BOLD_ITALIC = "Bold Italic";

    private static String createFontStrings(String font, int style) {
        String fontString;
        switch (style) {
            case Font.BOLD:
                fontString = font + " " + BOLD;
                break;
            case Font.ITALIC:
                fontString = font + " " + ITALIC;
                break;
            case Font.BOLD | Font.ITALIC:
                fontString = font + " " + BOLD_ITALIC;
                break;
            case Font.PLAIN:
            default:
                fontString = font;
                break;
        }
        return fontString.replace(' ', '_');
    }

    private static FontStruct getFontStruct(String text) {
        if (text == null || text.length() == 0) {
            return null;
        }
        text = text.replace('_', ' ');
        FontStruct fontStruct = new FontStruct();
        if (text.endsWith(BOLD)) {
            fontStruct.style = Font.BOLD;
            fontStruct.font = text.substring(0, text.length() - BOLD.length());
        }
        else if (text.endsWith(ITALIC)) {
            fontStruct.style = Font.ITALIC;
            fontStruct.font = text.substring(0, text.length() - ITALIC.length());
        }
        else if (text.endsWith(BOLD)) {
            fontStruct.style = Font.BOLD | Font.ITALIC;
            fontStruct.font = text.substring(0, text.length() - BOLD_ITALIC.length());
        }
        else {
            fontStruct.style = Font.PLAIN;
            fontStruct.font = text;
        }
        return fontStruct;

    }

    /**
     * Creates font. If there is no permission to access font file, it will try to create the font
     * directly from font file that is bundled as part of jar.
     *
     * @param name
     * @param style
     * @param size
     * @return the font.
     */
    public static Font createFont(String name, int style, int size) {
        try {
//            System.out.println("new Font");
            return new Font(name, style, size);
        }
        catch (AccessControlException e) {
//            System.out.println("new Font failed " + createFontStrings(name, style));
            ClassLoader cl = SecurityUtils.class.getClassLoader();
            try {
                String value = null;
                try {
                    value = FontFilesResource.getResourceBundle(Locale.getDefault()).getString(createFontStrings(name, style));
                }
                catch (MissingResourceException me1) {
                    try {
                        value = FontFilesResource.getResourceBundle(Locale.getDefault()).getString(name);
                    }
                    catch (MissingResourceException me2) {
                    }
                }
                if (value == null) {
                    return null;
                }
                else {
//                    System.out.print("createFont " + value);
                    Font font = Font.createFont(Font.TRUETYPE_FONT, cl.getResourceAsStream(value));
//                    System.out.println("successful " + font);
                    if (font != null) {
                        return font.deriveFont(style, size);
                    }
                }
            }
            catch (FontFormatException e1) {
                e1.printStackTrace();
                throw e;
            }
            catch (IOException e1) {
                e1.printStackTrace();
                throw e;
            }
        }
        return null;
    }

    private static Hashtable _safeProperties = null;

    private static Hashtable getSafeProperties() {
        if (_safeProperties == null) {
            _safeProperties = new Hashtable(13);
            _safeProperties.put("java.version", "");
            _safeProperties.put("java.vendor", "");
            _safeProperties.put("java.vendor.url", "");
            _safeProperties.put("java.class.version", "");
            _safeProperties.put("os.name", "");
            _safeProperties.put("os.version", "");
            _safeProperties.put("os.arch", "");
            _safeProperties.put("file.separator", "");
            _safeProperties.put("path.separator", "");
            _safeProperties.put("line.separator", "");
            _safeProperties.put("java.specification.version", "");
            _safeProperties.put("java.specification.vendor", "");
            _safeProperties.put("java.specification.name", "");
            _safeProperties.put("java.vm.specification.vendor", "");
            _safeProperties.put("java.vm.specification.name", "");
            _safeProperties.put("java.vm.version", "");
            _safeProperties.put("java.vm.vendor", "");
            _safeProperties.put("java.vm.name", "");
        }
        return _safeProperties;
    }

    ;

    /**
     * Gets the system property.
     *
     * @param key
     * @param defaultValue
     * @return the system property.
     */
    public static String getProperty(String key, String defaultValue) {
        try {
            return System.getProperty(key, defaultValue);
        }
        catch (AccessControlException e) {
            return defaultValue;
        }
    }

    private static boolean _AWTEventListenerDisabled = false;

    /**
     * Checks if AWTEventListener is disabled. This flag can be set by user. If false, JIDE code will read the value and
     * not use AWTEventListener. The reason we need this flag is because AWTEventListener needs a special security permission.
     * If applet, it will throw security if the user policy doesn't have the correct permission.
     *
     * @return true if AWTEventListener is disabled.
     */
    public static boolean isAWTEventListenerDisabled() {
        return _AWTEventListenerDisabled;
    }

    /**
     * Enables or disables the usage of AWTEventListener. If you wantto change it, you should change the value at
     * the beginning of your main method.
     *
     * @param AWTEventListenerDisabled
     */
    public static void setAWTEventListenerDisabled(boolean AWTEventListenerDisabled) {
        _AWTEventListenerDisabled = AWTEventListenerDisabled;
    }
}
