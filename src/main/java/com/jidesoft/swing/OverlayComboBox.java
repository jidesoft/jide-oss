/*
 * @(#)OverlayableComboBox.java 8/10/2007
 *
 * Copyright 2002 - 2007 JIDE Software Inc. All rights reserved.
 */

package com.jidesoft.swing;

import javax.swing.*;
import java.util.Vector;

public class OverlayComboBox extends JComboBox {
    public OverlayComboBox() {
    }

    public OverlayComboBox(Vector<?> items) {
        super(items);
    }

    public OverlayComboBox(final Object items[]) {
        super(items);
    }

    public OverlayComboBox(ComboBoxModel aModel) {
        super(aModel);
    }

    @Override
    public void repaint(long tm, int x, int y, int width, int height) {
        super.repaint(tm, x, y, width, height);
        OverlayableUtils.repaintOverlayable(this);
    }

}
