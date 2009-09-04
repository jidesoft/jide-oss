/*
 * @(#)ResizableFrame.java 10/22/2005
 *
 * Copyright 2002 - 2005 JIDE Software Inc. All rights reserved.
 */
package com.jidesoft.swing;

import com.jidesoft.utils.PortingUtils;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;

/**
 * A resizable undecorated frame.
 */
public class ResizableFrame extends JFrame implements ResizableSupport {

    private ResizablePanel _resizablePanel;
    private boolean _routingKeyStrokes;

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
            @Override
            protected Resizable createResizable() {
                return new Resizable(this) {
                    @Override
                    public void resizing(int resizeDir, int newX, int newY, int newW, int newH) {
                        Container container = ResizableFrame.this.getContentPane();
                        PortingUtils.setPreferredSize(container, new Dimension(newW, newH));
                        if (!JFrame.isDefaultLookAndFeelDecorated()) {
                            ResizableFrame.this.setBounds(newX, newY, newW, newH);
                        }
                        ResizableFrame.this.resizing();
                    }


                    @Override
                    public void beginResizing(int resizeCorner) {
                        super.beginResizing(resizeCorner);
                        ResizableFrame.this.beginResizing();
                    }

                    @Override
                    public void endResizing(int resizeCorner) {
                        super.endResizing(resizeCorner);
                        ResizableFrame.this.endResizing();
                    }

                    @Override
                    public boolean isTopLevel() {
                        return true;
                    }
                };
            }

            @Override
            protected boolean processKeyBinding(KeyStroke ks, KeyEvent e, int condition, boolean pressed) {
                boolean processed = super.processKeyBinding(ks, e, condition, pressed);
                if (processed || e.isConsumed() || !isRoutingKeyStrokes())
                    return processed;

                // check if the root pane of the source component has any registered action
                if (e.getSource() instanceof JComponent) {
                    JRootPane rootPane = ((JComponent) e.getSource()).getRootPane();
                    Class componentClass = rootPane.getClass();
                    while (componentClass != JComponent.class && componentClass != null) {
                        componentClass = componentClass.getSuperclass();
                    }
                    try {
                        if (componentClass != null) {
                            Method m = componentClass.getDeclaredMethod("processKeyBinding", new Class[]{KeyStroke.class, KeyEvent.class, int.class, boolean.class});
                            m.setAccessible(true);
                            processed = (Boolean) m.invoke(rootPane, ks, e, JComponent.WHEN_IN_FOCUSED_WINDOW, pressed);
                        }
                    }
                    catch (NoSuchMethodException e1) {
                        e1.printStackTrace();
                    }
                    catch (InvocationTargetException e1) {
                        e1.printStackTrace();
                    }
                    catch (IllegalAccessException e1) {
                        e1.printStackTrace();
                    }
                }
                if (processed || e.isConsumed()) {
                    return processed;
                }

                Component routingParent = getRoutingComponent();
                if (routingParent == null) {
                    return false;
                }
                KeyboardFocusManager.getCurrentKeyboardFocusManager().redispatchEvent(
                        routingParent, e);
                return (e.isConsumed());
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
