/*
 * @(#)JiToolTipFactory.java 4/25/2008
 *
 * Copyright 2002 - 2005 JIDE Software Inc. All rights reserved.
 */
package com.jidesoft.swing;

import javax.swing.*;
import java.awt.*;

/**
 * This class creates instances of JTooltip components. It provides a consistent means for creating custom tooltips
 * without the need for overriding JIDE components that create tooltips
 */
public class JToolTipFactory {

    private static JToolTipFactory _tooltipFactory;

    /**
     * Creates a new tooltip.
     *
     * @param c the component the tooltip describes
     * @return the new tooltip object
     */
    public JToolTip createToolTip(JComponent c) {
        return createToolTip(c, false);
    }

    /**
     * Creates a new tooltip. If overlapping is true then the tooltip will take on the foreground/background color and
     * font of the specified component (if the component isspecifiedd)
     *
     * @param c           the component the tooltip describes
     * @param overlapping whether the tooltip is for a normal or overlapping tooltip
     * @return the new tooltip object
     */
    public JToolTip createToolTip(JComponent c, boolean overlapping) {
        JToolTip tt = new JToolTip();
        if (c != null) {
            tt.setComponent(c);
            if (overlapping) {
                if (c.getBackground() != null) {
                    Color bg = c.getBackground();
                    if (bg.getAlpha() != 255) {
                        bg = new Color(bg.getRed(), bg.getGreen(), bg.getBlue());
                    }
                    tt.setBackground(bg);
                }
                if (c.getForeground() != null) {
                    tt.setForeground(c.getForeground());
                }
                if (c.getFont() != null) {
                    tt.setFont(c.getFont());
                }
            }
        }
        return tt;
    }

    /**
     * Sets the <code>JToolTipFactory</code> that will be used to obtain <code>JToolTip</code>s. This will throw an
     * <code>IllegalArgumentException</code> if <code>factory</code> is null.
     *
     * @param factory the shared factory
     * @throws IllegalArgumentException if <code>factory</code> is null
     */
    public static void setSharedInstance(JToolTipFactory factory) {
        if (factory == null) {
            throw new IllegalArgumentException("JToolTipFactory can not be null");
        }

        _tooltipFactory = factory;
    }

    /**
     * Returns the shared <code>JToolTipFactory</code> which can be used to obtain <code>JToolTip</code>s.
     *
     * @return the shared factory
     */
    public static JToolTipFactory getSharedInstance() {
        if (_tooltipFactory == null) {
            _tooltipFactory = new JToolTipFactory();
        }

        return _tooltipFactory;
    }
}
