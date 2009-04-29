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