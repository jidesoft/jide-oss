/*
 * @(#)OverlayTextArea.java 8/14/2007
 *
 * Copyright 2002 - 2007 JIDE Software Inc. All rights reserved.
 */

package com.jidesoft.swing;

import javax.swing.*;
import javax.swing.text.Document;

public class OverlayTextArea extends JTextArea {
    public OverlayTextArea() {
    }

    public OverlayTextArea(String text) {
        super(text);
    }

    public OverlayTextArea(int rows, int columns) {
        super(rows, columns);
    }

    public OverlayTextArea(String text, int rows, int columns) {
        super(text, rows, columns);
    }

    public OverlayTextArea(Document doc) {
        super(doc);
    }

    public OverlayTextArea(Document doc, String text, int rows, int columns) {
        super(doc, text, rows, columns);
    }

    @Override
    public void repaint(long tm, int x, int y, int width, int height) {
        super.repaint(tm, x, y, width, height);
        OverlayableUtils.repaintOverlayable(this);
    }
}
