/*
 * @(#)JDK9Utils.java 10/18/2016
 *
 * Copyright 2002 - 2016 JIDE Software Inc. All rights reserved.
 */

package com.jidesoft.jdk;

import sun.swing.plaf.synth.SynthIcon;

import javax.swing.*;
import javax.swing.plaf.synth.Region;
import javax.swing.plaf.synth.SynthContext;
import javax.swing.plaf.synth.SynthLookAndFeel;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class JdkSpecificClass {
    public static void paintTableHeaderIcon(JComponent c, Graphics g, Icon icon, int x, int y) {
        SynthContext context = new SynthContext(c, Region.TABLE_HEADER, SynthLookAndFeel.getStyle(c, Region.TABLE_HEADER), 0);
        ((SynthIcon) icon).paintIcon(context, g, x, y, ((SynthIcon) icon).getIconWidth(context), ((SynthIcon) icon).getIconHeight(context));
    }

    private static Map<String, Boolean> _synthIconMap;

    /**
     * Check if the class name is a SynthIcon class name.
     * <p/>
     * It's an interface reserved in case Sun changes the name or package of the class SynthIcon.
     *
     * @param name the class name to check
     * @return true if it's a SynthIcon class name. Otherwise false.
     */
    public static boolean isSynthIconClassName(String name) {
        return name != null && name.contains("sun.swing.plaf.synth.SynthIcon");
    }

    public static boolean isSynthIcon(Icon icon) {
        if (_synthIconMap == null) {
            _synthIconMap = new HashMap<String, Boolean>();
        }
        Class<?> aClass = icon.getClass();
        java.util.List<String> classNamesToPut = new ArrayList<String>();
        boolean isSynthIcon = false;
        while (aClass != null) {
            String name = aClass.getCanonicalName();
            if (name != null) {
                Boolean value = _synthIconMap.get(name);
                if (value != null) {
                    return value;
                }
                classNamesToPut.add(name);
                if (isSynthIconClassName(name)) {
                    isSynthIcon = true;
                    break;
                }
            }
            aClass = aClass.getSuperclass();
        }
        for (String name : classNamesToPut) {
            _synthIconMap.put(name, isSynthIcon);
        }
        return isSynthIcon;
    }

    public static void paintCheckBoxIcon(JComponent c, Icon icon, Graphics g, int state, int x, int y) {
        SynthContext context = new SynthContext(c, Region.CHECK_BOX, SynthLookAndFeel.getStyle(c, Region.CHECK_BOX), state);
        final int w = ((SynthIcon) icon).getIconWidth(context);
        final int h = ((SynthIcon) icon).getIconHeight(context);
        ((SynthIcon) icon).paintIcon(context, g, x, y, w, h);
    }

    public static void paintTableCellIcon(JTable table, Icon icon, Graphics g, int iconX, int iconY) {
        SynthContext context = new SynthContext(table, Region.TREE_CELL, SynthLookAndFeel.getStyle(table, Region.TREE_CELL), 0);
        ((SynthIcon) icon).paintIcon(context, g, iconX, iconY, ((SynthIcon) icon).getIconWidth(context), ((SynthIcon) icon).getIconHeight(context));
    }

    public static int[] getVersions() {
        return null;
    }
}
