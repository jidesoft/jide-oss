/*
 * @(#)MenuCheckIcon.java 4/29/2009
 *
 * Copyright 2002 - 2009 JIDE Software Inc. All rights reserved.
 *
 */

package com.jidesoft.icons;


import javax.swing.*;
import javax.swing.plaf.UIResource;
import java.io.Serializable;
import java.awt.*;

/**
 * Icon wrapper class for check icons. The only thing this class does is to check the selection before it really paints
 * its wrapped icon. The reason we provide this class is that similar mechanism resides in Swing and we cannot override it.
 * So if we just update the UI with normal icons, the icon could be displayed unexpectedly. With this icon wrapper class,
 * you can get exactly the same behavior with Swing default icons.
 */
public class MenuCheckIcon implements Icon, UIResource, Serializable {

    private ImageIcon _icon;
    private static final long serialVersionUID = -6303936713472505092L;

    public MenuCheckIcon(ImageIcon icon) {
        if (icon == null) {
            throw new IllegalArgumentException("The icon should not be null.");
        }
        _icon = icon;
    }

    public int getIconHeight() {
        return getIcon().getIconHeight();
    }

    public int getIconWidth() {
        return getIcon().getIconWidth();
    }

    public void paintIcon(Component c, Graphics g, int x, int y) {
        Icon icon = getIcon();
        if (c instanceof AbstractButton) {
            AbstractButton b = (AbstractButton) c;
            if (b.isSelected()) {
                icon.paintIcon(c, g, x, y);
            }
        }
    }

    private Icon getIcon() {
        return _icon;
    }
}