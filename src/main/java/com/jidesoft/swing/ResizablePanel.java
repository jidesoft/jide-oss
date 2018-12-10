/*
 * @(#)ResizablePanel.java 2/14/2005
 *
 * Copyright 2002 - 2005 JIDE Software Inc. All rights reserved.
 */
package com.jidesoft.swing;

import com.jidesoft.plaf.UIDefaultsLookup;

import javax.swing.*;
import java.awt.*;

/**
 * <code>ResizablePanel</code> is a panel that can be resized. You can resize it from any of the four corners or four
 * sides.
 */
public class ResizablePanel extends JPanel implements ResizableSupport {
    private Resizable _resizable;

    /**
     * Creates a new <code>ResizablePanel</code> with a double buffer and a flow layout.
     */
    public ResizablePanel() {
        initComponents();
    }

    /**
     * Creates a new <code>ResizablePanel</code> with <code>FlowLayout</code> and the specified buffering strategy. If
     * <code>isDoubleBuffered</code> is true, the <code>JPanel</code> will use a double buffer.
     *
     * @param isDoubleBuffered a boolean, true for double-buffering, which uses additional memory space to achieve fast,
     *                         flicker-free updates
     */
    public ResizablePanel(boolean isDoubleBuffered) {
        super(isDoubleBuffered);
        initComponents();
    }

    /**
     * Create a new buffered <code>ResizablePanel</code> with the specified layout manager
     *
     * @param layout the LayoutManager to use
     */
    public ResizablePanel(LayoutManager layout) {
        super(layout);
        initComponents();
    }

    /**
     * Creates a new <code>ResizablePanel</code> with the specified layout manager and buffering strategy.
     *
     * @param layout           the LayoutManager to use
     * @param isDoubleBuffered a boolean, true for double-buffering, which uses additional memory space to achieve fast,
     *                         flicker-free updates
     */
    public ResizablePanel(LayoutManager layout, boolean isDoubleBuffered) {
        super(layout, isDoubleBuffered);
        initComponents();
    }

    /**
     * Creates the Resizable class. It also set the panel's layout to BorderLayout.
     */
    protected void initComponents() {
        _resizable = createResizable();
        setLayout(new BorderLayout());
    }

    /**
     * Creates the Resizable. Subclass class can override this method to create its own Resizable and tweak some
     * options.
     *
     * @return Resizable.
     */
    protected Resizable createResizable() {
        return new Resizable(this);
    }

    /**
     * Gets the Resizable.
     *
     * @return the Resizable.
     */
    public Resizable getResizable() {
        return _resizable;
    }

    /**
     * Overrides the updateUI method to set border to resizable border defined in UIManagerLookup.getBorder("Resizable.resizeBorder")).
     */
    @Override
    public void updateUI() {
        super.updateUI();
        setBorder(UIDefaultsLookup.getBorder("Resizable.resizeBorder"));
    }
}
