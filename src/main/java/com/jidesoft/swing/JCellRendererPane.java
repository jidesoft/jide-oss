/*
 * @(#)JCellRendererPane.java 10/25/2005
 *
 * Copyright 2002 - 2005 JIDE Software Inc. All rights reserved.
 */
package com.jidesoft.swing;

import javax.accessibility.Accessible;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleRole;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.ObjectOutputStream;

/**
 * Copied from CellRendererPane and make it extending JComponent so that tooltips of renderer works.
 *
 * @deprecated no longer used in other JIDE classes.
 */
@Deprecated
public class JCellRendererPane extends JComponent implements Accessible {
    /**
     * Construct a CellRendererPane object.
     */
    public JCellRendererPane() {
        super();
        setLayout(null);
        setVisible(false);
    }

    /**
     * Overridden to avoid propagating a invalidate up the tree when the cell renderer child is configured.
     */
    @Override
    public void invalidate() {
    }


    /**
     * Shouldn't be called.
     */
    @Override
    public void paint(Graphics g) {
    }


    /**
     * Shouldn't be called.
     */
    @Override
    public void update(Graphics g) {
    }


    /**
     * If the specified component is already a child of this then we don't bother doing anything - stacking order doesn't matter for cell renderer components (CellRendererPane doesn't paint anyway).<
     */
    @Override
    protected void addImpl(Component x, Object constraints, int index) {
        if (x.getParent() == this) {
            return;
        }
        else {
            super.addImpl(x, constraints, index);
        }
    }


    /**
     * Paint a cell renderer component c on graphics object g.  Before the component is drawn it's reparented to this (if that's necessary), it's bounds are set to w,h and the graphics object is
     * (effectively) translated to x,y. If it's a JComponent, double buffering is temporarily turned off. After the component is painted it's bounds are reset to -w, -h, 0, 0 so that, if it's the last
     * renderer component painted, it will not start consuming input. The Container p is the component we're actually drawing on, typically it's equal to this.getParent(). If shouldValidate is true
     * the component c will be validated before painted.
     */
    public void paintComponent(Graphics g, Component c, Container p, int x, int y, int w, int h, boolean shouldValidate) {
        if (c == null) {
            if (p != null) {
                Color oldColor = g.getColor();
                g.setColor(p.getBackground());
                g.fillRect(x, y, w, h);
                g.setColor(oldColor);
            }
            return;
        }

        if (c.getParent() != this) {
            this.add(c);
        }

        c.setBounds(x, y, w, h);

        if (shouldValidate) {
            c.validate();
        }

        boolean wasDoubleBuffered = false;
        if ((c instanceof JComponent) && c.isDoubleBuffered()) {
            wasDoubleBuffered = true;
            ((JComponent) c).setDoubleBuffered(false);
        }

        Graphics cg = g.create(x, y, w, h);
        try {
            c.paint(cg);
        }
        finally {
            cg.dispose();
        }

        if (wasDoubleBuffered && (c instanceof JComponent)) {
            ((JComponent) c).setDoubleBuffered(true);
        }

        c.setBounds(-w, -h, 0, 0);
    }


    /**
     * Calls this.paintComponent(g, c, p, x, y, w, h, false).
     */
    public void paintComponent(Graphics g, Component c, Container p, int x, int y, int w, int h) {
        paintComponent(g, c, p, x, y, w, h, false);
    }


    /**
     * Calls this.paintComponent() with the rectangles x,y,width,height fields.
     */
    public void paintComponent(Graphics g, Component c, Container p, Rectangle r) {
        paintComponent(g, c, p, r.x, r.y, r.width, r.height);
    }


    private void writeObject(ObjectOutputStream s) throws IOException {
        removeAll();
        s.defaultWriteObject();
    }

/////////////////
// Accessibility support
////////////////

    protected AccessibleContext accessibleContext = null;

    /**
     * Gets the AccessibleContext associated with this CellRendererPane. For CellRendererPanes, the AccessibleContext takes the form of an AccessibleCellRendererPane. A new AccessibleCellRendererPane
     * instance is created if necessary.
     *
     * @return an AccessibleCellRendererPane that serves as the AccessibleContext of this CellRendererPane
     */
    @Override
    public AccessibleContext getAccessibleContext() {
        if (accessibleContext == null) {
            accessibleContext = new AccessibleCellRendererPane();
        }
        return accessibleContext;
    }

    /**
     * This class implements accessibility support for the <code>CellRendererPane</code> class.
     */
    protected class AccessibleCellRendererPane extends AccessibleAWTContainer {
        // AccessibleContext methods
        //

        /**
         * Get the role of this object.
         *
         * @return an instance of AccessibleRole describing the role of the object
         *
         * @see javax.accessibility.AccessibleRole
         */
        @Override
        public AccessibleRole getAccessibleRole() {
            return AccessibleRole.PANEL;
        }
    } // inner class AccessibleCellRendererPane
}


