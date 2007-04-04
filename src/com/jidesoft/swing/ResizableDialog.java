/*
 * @(#)ResizableDialog.java
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
 * A resizable undecorated dialog.
 */
public class ResizableDialog extends JDialog implements ResizableSupport {
    private ResizablePanel _resizablePanel;

    public ResizableDialog() throws HeadlessException {
        initComponents();
    }

    public ResizableDialog(Frame owner) throws HeadlessException {
        super(owner);
        initComponents();
    }

    public ResizableDialog(Frame owner, boolean modal) throws HeadlessException {
        super(owner, modal);
        initComponents();
    }

    public ResizableDialog(Frame owner, String title) throws HeadlessException {
        super(owner, title);
        initComponents();
    }

    public ResizableDialog(Frame owner, String title, boolean modal) throws HeadlessException {
        super(owner, title, modal);
        initComponents();
    }

    public ResizableDialog(Frame owner, String title, boolean modal, GraphicsConfiguration gc) {
        super(owner, title, modal, gc);
        initComponents();
    }

    public ResizableDialog(Dialog owner) throws HeadlessException {
        super(owner);
        initComponents();
    }

    public ResizableDialog(Dialog owner, boolean modal) throws HeadlessException {
        super(owner, modal);
        initComponents();
    }

    public ResizableDialog(Dialog owner, String title) throws HeadlessException {
        super(owner, title);
        initComponents();
    }

    public ResizableDialog(Dialog owner, String title, boolean modal) throws HeadlessException {
        super(owner, title, modal);
        initComponents();
    }

    public ResizableDialog(Dialog owner, String title, boolean modal, GraphicsConfiguration gc) throws HeadlessException {
        super(owner, title, modal, gc);
        initComponents();
    }

    /**
     * Initializes the resizable window.
     */
    protected void initComponents() {
        setModal(false);
        setUndecorated(true);

        _resizablePanel = new ResizablePanel() {
            protected Resizable createResizable() {
                return new Resizable(this) {
                    public void resizing(int resizeDir, int newX, int newY, int newW, int newH) {
                        ResizableDialog.this.setBounds(newX, newY, newW, newH);
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
     * @param border
     */
    public void setBorder(Border border) {
        _resizablePanel.setBorder(border);
    }

    /**
     * Gets the border of the resizable window. By default, <code>UIManager.getBorder("Resizable.resizeBorder")</code>
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
