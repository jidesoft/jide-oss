/*
 * @(#)ResizableFrame.java 10/22/2005
 *
 * Copyright 2002 - 2005 JIDE Software Inc. All rights reserved.
 */
package com.jidesoft.swing;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

/**
 * A resizable undecorated frame.
 */
public class ResizableFrame extends JFrame implements ResizableSupport {

    private ResizablePanel _resizablePanel;

    public ResizableFrame() throws HeadlessException {
        initComponents();
    }

    public ResizableFrame(GraphicsConfiguration gc) {
        super(gc);
        initComponents();
    }

    public ResizableFrame(String title) throws HeadlessException {
        super(title);
        initComponents();
    }

    public ResizableFrame(String title, GraphicsConfiguration gc) {
        super(title, gc);
        initComponents();
    }

    /**
     * Initializes the resizable window.
     */
    protected void initComponents() {
        setUndecorated(true);

        _resizablePanel = new ResizablePanel() {
            protected Resizable createResizable() {
                return new Resizable(this) {
                    public void resizing(int resizeDir, int newX, int newY, int newW, int newH) {
                        ResizableFrame.this.setBounds(newX, newY, newW, newH);
                    }

                    public boolean isTopLevel() {
                        return true;
                    }
                };
            }
        };
        setContentPane(_resizablePanel);

        // make sure the content pane resized along with the window.
        addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent e) {
                _resizablePanel.setSize(getSize());
            }
        });
    }

    /**
     * Sets the border of the resizable window. Do not pass in an empty border. Otherwise
     * the window won't be resizable.
     *
     * @param border the border.
     */
    public void setBorder(Border border) {
        _resizablePanel.setBorder(border);
    }

    /**
     * Gets the border of the resizable window. By default, <code>UIManagerLookup.getBorder("Resizable.resizeBorder")</code>
     * will be used.
     *
     * @return the border.
     */
    public Border getBorder() {
        return _resizablePanel.getBorder();
    }

    /**
     * Gets the underlying Resizable.
     *
     * @return the Resizable.
     */
    public Resizable getResizable() {
        return _resizablePanel.getResizable();
    }
}
