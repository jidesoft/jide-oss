/*
 * @(#)OverlayableTextField.java 8/10/2007
 *
 * Copyright 2002 - 2007 JIDE Software Inc. All rights reserved.
 */

package com.jidesoft.swing;

import javax.swing.*;
import javax.swing.text.Document;

public class OverlayTextField extends JTextField {
    public OverlayTextField() {
    }

    public OverlayTextField(String text) {
        super(text);
    }

    public OverlayTextField(int columns) {
        super(columns);
    }

    public OverlayTextField(String text, int columns) {
        super(text, columns);
    }

    public OverlayTextField(Document doc, String text, int columns) {
        super(doc, text, columns);
    }

    @Override
    public void repaint(long tm, int x, int y, int width, int height) {
        super.repaint(tm, x, y, width, height);
        OverlayableUtils.repaintOverlayable(this);
    }

}
