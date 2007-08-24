/*
 * @(#)OverlayableCheckBox.java 8/10/2007
 *
 * Copyright 2002 - 2007 JIDE Software Inc. All rights reserved.
 */

package com.jidesoft.swing;

import javax.swing.*;

public class OverlayCheckBox extends JCheckBox {
    public OverlayCheckBox() {
    }

    public OverlayCheckBox(Icon icon) {
        super(icon);
    }

    public OverlayCheckBox(Icon icon, boolean selected) {
        super(icon, selected);
    }

    public OverlayCheckBox(String text) {
        super(text);
    }

    public OverlayCheckBox(Action a) {
        super(a);
    }

    public OverlayCheckBox(String text, boolean selected) {
        super(text, selected);
    }

    public OverlayCheckBox(String text, Icon icon) {
        super(text, icon);
    }

    public OverlayCheckBox(String text, Icon icon, boolean selected) {
        super(text, icon, selected);
    }

    @Override
    public void repaint(long tm, int x, int y, int width, int height) {
        super.repaint(tm, x, y, width, height);
        OverlayableUtils.repaintOverlayable(this);
    }
}
