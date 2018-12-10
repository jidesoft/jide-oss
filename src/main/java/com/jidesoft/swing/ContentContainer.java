/*
 * @(#)ContentContainer.java 5/5/2005
 *
 * Copyright 2002 - 2005 JIDE Software Inc. All rights reserved.
 */
package com.jidesoft.swing;

import com.jidesoft.plaf.LookAndFeelFactory;
import com.jidesoft.plaf.UIDefaultsLookup;
import com.jidesoft.plaf.basic.ThemePainter;

import javax.swing.*;
import java.awt.*;

/**
 * In JIDE Action Framework, <code>ContentContainer</code> is the area that contains all command bars. It is also the
 * largest area in the content pane of top level windows.
 * <p/>
 * <code>ContentContainer</code> uses BasicPainter to paint the background. For example, under Office 2003 L&F, it will
 * use gradient to paint the background.
 */
public class ContentContainer extends JPanel {
    private ThemePainter _painter;

    /**
     * Creates a new <code>JPanel</code> with a double buffer and a flow layout.
     */
    public ContentContainer() {
        setBorder(BorderFactory.createEmptyBorder());
        setOpaque(true);
        updateUI();
        setFocusCycleRoot(false);
        setFocusable(false);
    }

    @Override
    public void updateUI() {
        super.updateUI();
        if (UIDefaultsLookup.get("Theme.painter") == null) {
            LookAndFeelFactory.installJideExtension();
        }
        LookAndFeel.installColors(this, "ContentContainer.background", "ContentContainer.foreground");
        _painter = (ThemePainter) UIDefaultsLookup.get("Theme.painter");
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (_painter != null && isOpaque()) {
            _painter.paintContentBackground(this, g, new Rectangle(0, 0, getWidth(), getHeight()), SwingConstants.HORIZONTAL, ThemePainter.STATE_DEFAULT);
        }
    }
}
