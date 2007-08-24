/*
 * @(#)OverlayableRadioButton.java 8/10/2007
 *
 * Copyright 2002 - 2007 JIDE Software Inc. All rights reserved.
 */

package com.jidesoft.swing;

import javax.swing.*;

public class OverlayRadioButton extends JRadioButton {

    public OverlayRadioButton() {
    }

    public OverlayRadioButton(Icon icon) {
        super(icon);
    }

    public OverlayRadioButton(Action a) {
        super(a);
    }

    public OverlayRadioButton(Icon icon, boolean selected) {
        super(icon, selected);
    }

    public OverlayRadioButton(String text) {
        super(text);
    }

    public OverlayRadioButton(String text, boolean selected) {
        super(text, selected);
    }

    public OverlayRadioButton(String text, Icon icon) {
        super(text, icon);
    }

    public OverlayRadioButton(String text, Icon icon, boolean selected) {
        super(text, icon, selected);
    }

    @Override
    public void repaint(long tm, int x, int y, int width, int height) {
        super.repaint(tm, x, y, width, height);
        OverlayableUtils.repaintOverlayable(this);
    }
}
