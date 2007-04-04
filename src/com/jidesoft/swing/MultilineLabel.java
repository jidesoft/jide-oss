/*
 * @(#)MultilineLabel.java
 *
 * Copyright 2002 JIDE Software. All rights reserved.
 */
package com.jidesoft.swing;

import javax.swing.*;
import java.awt.*;

/**
 * Normal JLabel cannot have multiple lines. If you want to multiple
 * label, you can use this class.
 */
public class MultilineLabel extends JTextArea {
    public MultilineLabel() {
        initComponents();
    }

    public MultilineLabel(String s) {
        super(s);
        initComponents();
    }

    private void initComponents() {
        setLineWrap(true);
        setWrapStyleWord(true);
        setOpaque(false);
    }

    /**
     * Reloads the pluggable UI.  The key used to fetch the
     * new interface is <code>getUIClassID()</code>.  The type of
     * the UI is <code>TextUI</code>.  <code>invalidate</code>
     * is called after setting the UI.
     */
    public void updateUI() {
        super.updateUI();

        setLineWrap(true);
        setWrapStyleWord(true);
        setEditable(false);
        setRequestFocusEnabled(false);
        setFocusable(false);
        setOpaque(false);

        LookAndFeel.installBorder(this, "Label.border");
        LookAndFeel.installColorsAndFont(this, "Label.background", "Label.foreground", "Label.font");
    }

    /**
     * Overrides <code>getMinimumSize</code> to return <code>getPreferredSize()</code> instead.
     * We did this because of a bug at http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4824261.
     *
     * @return the preferred size as minimum size.
     */
    public Dimension getMinimumSize() {
        return getPreferredSize();
    }
}
