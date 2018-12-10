/*
 * @(#)HeavyweightWrapper.java 10/18/2005
 *
 * Copyright 2002 - 2005 JIDE Software Inc. All rights reserved.
 */
package com.jidesoft.swing;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

/**
 * HeavyweightWrapper is a special heavyweight Panel that can hold another component.
 * <p/>
 * It's package local right now. Whenever it is ready, we will make it public.
 */
public class HeavyweightWrapper extends Panel {
    private Component _component;
    private boolean _heavyweight;
    final private Dimension MIN_DIM = new Dimension(0, 0);

    public HeavyweightWrapper(Component component) {
        this(component, false);
    }

    @Override
    public Dimension getMinimumSize() {
        return MIN_DIM;
    }

    public HeavyweightWrapper(Component component, boolean heavyweight) {
        _component = component;
        if (_component != null) {
            ((JComponent) _component).putClientProperty("HeavyweightWrapper", this);
            _component.addComponentListener(new ComponentListener() {
                public void componentResized(ComponentEvent e) {
                }

                public void componentMoved(ComponentEvent e) {
                }

                public void componentShown(ComponentEvent e) {
                    setVisible(true);
                }

                public void componentHidden(ComponentEvent e) {
                    setVisible(false);
                }
            });
        }
        setLayout(new BorderLayout());
        setVisible(false);
        _heavyweight = heavyweight;
    }

    public boolean isHeavyweight() {
        return _heavyweight;
    }

    public void setHeavyweight(boolean heavyweight) {
        _heavyweight = heavyweight;
    }

    public void delegateAdd(Container parent, Object constraints) {
        JideSwingUtilities.removeFromParentWithFocusTransfer(_component);

        if (isHeavyweight()) {
            if (_component.getParent() != this) {
                add(_component);
//                System.out.println("added component");
            }
            if (this.getParent() != parent) {
                parent.add(this, constraints);
//                System.out.println("added parent");
            }
        }
        else {
            parent.add(_component, constraints);
        }
    }

    public void delegateRemove(Container parent) {
        JideSwingUtilities.removeFromParentWithFocusTransfer(_component);

        if (isHeavyweight()) {
            remove(_component);
            parent.remove(this);
//            System.out.println("removed");
        }
        else {
            parent.remove(_component);
        }
    }

    public void delegateSetVisible(boolean visible) {
        if (isHeavyweight()) {
            this.setVisible(visible);
            _component.setVisible(visible);
        }
        else {
            _component.setVisible(visible);
        }
    }

    public void delegateSetBounds(Rectangle bounds) {
        if (isHeavyweight()) {
            this.setBounds(bounds);
            _component.setBounds(0, 0, bounds.width, bounds.height);
        }
        else {
            _component.setBounds(bounds);
        }
    }

    public void delegateSetBounds(int x, int y, int width, int height) {
        if (isHeavyweight()) {
            this.setBounds(x, y, width, height);
            _component.setBounds(0, 0, width, height);
        }
        else {
            _component.setBounds(x, y, width, height);
        }
    }

    public void delegateSetLocation(int x, int y) {
        if (isHeavyweight()) {
            this.setLocation(x, y);
            _component.setLocation(0, 0);
        }
        else {
            _component.setLocation(x, y);
        }
    }

    public void delegateSetLocation(Point p) {
        if (isHeavyweight()) {
            this.setLocation(p);
            _component.setLocation(0, 0);
        }
        else {
            _component.setLocation(p);
        }
    }

    public void delegateSetCursor(Cursor cursor) {
        _component.setCursor(cursor);
    }

    public void delegateSetNull() {
        ((JComponent) _component).putClientProperty("HeavyweightWrapper", null);
        _component = null;
    }

    public Container delegateGetParent() {
        if (isHeavyweight()) {
            return getParent();
        }
        else {
            return _component.getParent();
        }
    }

    public boolean delegateIsVisible() {
        if (isHeavyweight()) {
            return isVisible();
        }
        else {
            return _component.isVisible();
        }
    }

    public Rectangle delegateGetBounds() {
        if (isHeavyweight()) {
            return getBounds();
        }
        else {
            return _component.getBounds();
        }
    }

    public void delegateRepaint() {
        if (isHeavyweight()) {
            repaint();
            _component.repaint();
        }
        else {
            _component.repaint();
        }
    }

    public Component getComponent() {
        return _component;
    }

    public void setComponent(Component component) {
        _component = component;
    }
}
