/*
 * @(#)ResizableWindow.java 2/18/2005
 *
 * Copyright 2002 - 2005 JIDE Software Inc. All rights reserved.
 */
package com.jidesoft.swing;

import com.jidesoft.utils.SystemInfo;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;

/**
 * A resizable window.
 */
public class ResizableWindow extends JWindow implements ResizableSupport {

    private ResizablePanel _resizablePanel;
    private boolean _routingKeyStrokes;

    public ResizableWindow() {
        initComponents();
    }

    public ResizableWindow(Frame owner) {
        super(owner);
        initComponents();
    }

    public ResizableWindow(GraphicsConfiguration gc) {
        super(gc);
        initComponents();
    }

    public ResizableWindow(Window owner) {
        super(owner);
        initComponents();
    }

    public ResizableWindow(Window owner, GraphicsConfiguration gc) {
        super(owner, gc);
        initComponents();
    }

    /**
     * Initializes the resizable window.
     */
    protected void initComponents() {
        _resizablePanel = new ResizablePanel() {
            @Override
            protected Resizable createResizable() {
                return new Resizable(this) {
                    @Override
                    public void resizing(int resizeDir, int newX, int newY, int newW, int newH) {
                        Container container = ResizableWindow.this.getContentPane();
                        if (SystemInfo.isJdk15Above()) {
                            container.setPreferredSize(new Dimension(newW, newH));
                        }
                        else if (container instanceof JComponent) {
                            ((JComponent) container).setPreferredSize(new Dimension(newW, newH));
                        }
                        ResizableWindow.this.setBounds(newX, newY, newW, newH);
                        ResizableWindow.this.resizing();
                    }


                    @Override
                    public void beginResizing(int resizeCorner) {
                        super.beginResizing(resizeCorner);
                        ResizableWindow.this.beginResizing();
                    }

                    @Override
                    public void endResizing(int resizeCorner) {
                        super.endResizing(resizeCorner);
                        ResizableWindow.this.endResizing();
                    }


                    @Override
                    public boolean isTopLevel() {
                        return true;
                    }

                };
            }

            @Override
            protected void processKeyEvent(KeyEvent e) {
                super.processKeyEvent(e);
                
                if (e.isConsumed() || !isRoutingKeyStrokes())
                    return ;

                Component routingParent = getRoutingComponent();
                if (routingParent == null) {
                    return ;
                }
                routingParent.dispatchEvent(e);

                return;
            }
        };
        setContentPane(_resizablePanel);

        // make sure the content pane resized along with the window.
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                _resizablePanel.setSize(getSize());
            }
        });
    }

    protected void beginResizing() {
    }

    protected void resizing() {
    }

    protected void endResizing() {
    }

    /**
     * Sets the border of the resizable window. Do not pass in an empty border. Otherwise the window won't be
     * resizable.
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

    public Component getRoutingComponent() {
        return getOwner();
    }

    public void setRoutingKeyStrokes(boolean routingKeyStrokes) {
        _routingKeyStrokes = routingKeyStrokes;
    }

    public boolean isRoutingKeyStrokes() {
        return _routingKeyStrokes;
    }


}
