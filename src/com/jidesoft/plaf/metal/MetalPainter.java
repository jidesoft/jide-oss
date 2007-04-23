/*
 * @(#)${NAME}
 *
 * Copyright 2002 - 2004 JIDE Software Inc. All rights reserved.
 */
package com.jidesoft.plaf.metal;

import com.jidesoft.plaf.UIDefaultsLookup;
import com.jidesoft.plaf.basic.BasicPainter;
import com.jidesoft.plaf.basic.ThemePainter;

import javax.swing.*;
import javax.swing.plaf.metal.MetalLookAndFeel;
import java.awt.*;

/**
 * Painter for Metal L&F.
 * <p/>
 * Please note, this class is an internal class which is meant to be used by other JIDE classes only.
 * Future version might break your build if you use it.
 */
public class MetalPainter extends BasicPainter {

    private static MetalPainter _instance;

    public static ThemePainter getInstance() {
        if (_instance == null) {
            _instance = new MetalPainter();
        }
        return _instance;
    }

    public MetalPainter() {
    }

    public void paintGripper(JComponent c, Graphics g, Rectangle rect, int orientation, int state) {
        if (orientation == HORIZONTAL) {
            MetalBumps bumps = new MetalBumps(rect.width, rect.height - 6,
                    state == ThemePainter.STATE_SELECTED ? MetalLookAndFeel.getPrimaryControlHighlight() : MetalLookAndFeel.getControlHighlight(),
                    state == ThemePainter.STATE_SELECTED ? MetalLookAndFeel.getPrimaryControlDarkShadow() : MetalLookAndFeel.getControlDarkShadow(),
                    state == ThemePainter.STATE_SELECTED ? UIDefaultsLookup.getColor("DockableFrame.activeTitleBackground") : UIDefaultsLookup.getColor("DockableFrame.inactiveTitleBackground"));
            bumps.paintIcon(null, g, rect.x, rect.y + 3);
        }
        else {
            MetalBumps bumps = new MetalBumps(rect.width - 6, rect.height,
                    state == ThemePainter.STATE_SELECTED ? MetalLookAndFeel.getPrimaryControlHighlight() : MetalLookAndFeel.getControlHighlight(),
                    state == ThemePainter.STATE_SELECTED ? MetalLookAndFeel.getPrimaryControlDarkShadow() : MetalLookAndFeel.getControlDarkShadow(),
                    state == ThemePainter.STATE_SELECTED ? UIDefaultsLookup.getColor("DockableFrame.activeTitleBackground") : UIDefaultsLookup.getColor("DockableFrame.inactiveTitleBackground"));
            bumps.paintIcon(null, g, rect.x + 3, rect.y);
        }
    }
}

