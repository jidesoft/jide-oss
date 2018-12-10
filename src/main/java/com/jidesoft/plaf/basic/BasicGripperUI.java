/**
 * @(#)BasicGripperUI.java
 *
 * Copyright 2002 - 2004 JIDE Software. All rights reserved.
 */
package com.jidesoft.plaf.basic;

import com.jidesoft.plaf.GripperUI;
import com.jidesoft.plaf.UIDefaultsLookup;
import com.jidesoft.swing.Gripper;
import com.jidesoft.swing.JideSwingUtilities;
import com.jidesoft.utils.SecurityUtils;

import javax.swing.*;
import javax.swing.plaf.ComponentUI;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 * A Basic L&F implementation of GripperUI.
 */
public class BasicGripperUI extends GripperUI {
    private int _size;

    protected ThemePainter _painter;

    protected Painter _gripperPainter;

    public static ComponentUI createUI(JComponent c) {
        return new BasicGripperUI();
    }

    @Override
    public void installUI(JComponent c) {
        _painter = (ThemePainter) UIDefaultsLookup.get("Theme.painter");
        _gripperPainter = (Painter) UIDefaultsLookup.get("Gripper.painter");
        installDefaults((Gripper) c);
        installListeners((Gripper) c);
    }

    @Override
    public void uninstallUI(JComponent c) {
        _painter = null;
        _gripperPainter = null;
        uninstallDefaults((Gripper) c);
        uninstallListeners((Gripper) c);
    }

    protected void installDefaults(Gripper s) {
        _size = UIDefaultsLookup.getInt("Gripper.size");
    }

    protected void uninstallDefaults(Gripper s) {
    }

    protected MouseListener createMouseListener() {
        return new GripperMouseListener();
    }

    protected void installListeners(Gripper g) {
        MouseListener listener = createMouseListener();
        if (listener != null) {
            // put the listener in the gripper's client properties so that
            // we can get at it later
            g.putClientProperty(this, listener);
            g.addMouseListener(listener);
        }
    }

    protected void uninstallListeners(Gripper g) {
        MouseListener listener = (MouseListener) g.getClientProperty(this);
        g.putClientProperty(this, null);
        if (listener != null) {
            g.removeMouseListener(listener);
        }
    }

    protected void paintBackground(Graphics g, Gripper b) {
        Rectangle rect = new Rectangle(0, 0, b.getWidth(), b.getHeight());
        if (b.isRollover()) {
            getPainter().paintButtonBackground(b, g, rect, 0, ThemePainter.STATE_ROLLOVER);
        }
        else {
            if (b.isOpaque()) {
                getPainter().paintButtonBackground(b, g, rect, 0, b.isSelected() ? ThemePainter.STATE_SELECTED : ThemePainter.STATE_DEFAULT, false);
                if ("true".equals(SecurityUtils.getProperty("shadingtheme", "false"))) {
                    JideSwingUtilities.fillGradient(g, rect, SwingConstants.HORIZONTAL);
                }
            }
        }
    }

    @Override
    public void paint(Graphics g, JComponent c) {
        Gripper gripper = (Gripper) c;
        paintBackground(g, gripper);
        int state = gripper.isSelected() ? ThemePainter.STATE_SELECTED : ThemePainter.STATE_DEFAULT;
        if (_gripperPainter == null) {
            getPainter().paintGripper(c, g, new Rectangle(0, 0, c.getWidth(), c.getHeight()), gripper.getOrientation(), state);
        }
        else {
            _gripperPainter.paint(c, g, new Rectangle(0, 0, c.getWidth(), c.getHeight()), gripper.getOrientation(), state);
        }
    }

    @Override
    public Dimension getPreferredSize(JComponent c) {
        return new Dimension(_size, _size);
    }

    @Override
    public Dimension getMinimumSize(JComponent c) {
        return getPreferredSize(c);
    }

    @Override
    public Dimension getMaximumSize(JComponent c) {
        Gripper gripper = (Gripper) c;
        if (gripper.getOrientation() == SwingConstants.HORIZONTAL)
            return new Dimension(_size, c.getParent().getHeight());
        else
            return new Dimension(c.getParent().getWidth(), _size);
    }

    public ThemePainter getPainter() {
        return _painter;
    }

    class GripperMouseListener extends MouseAdapter {
        @Override
        public void mouseEntered(MouseEvent e) {
            super.mouseEntered(e);
            if (e.getSource() instanceof Gripper) {
                if (((Gripper) e.getSource()).isRolloverEnabled()) {
                    ((Gripper) e.getSource()).setRollover(true);
                }
            }
        }

        @Override
        public void mouseExited(MouseEvent e) {
            super.mouseExited(e);
            if (e.getSource() instanceof Gripper) {
                if (((Gripper) e.getSource()).isRolloverEnabled()) {
                    ((Gripper) e.getSource()).setRollover(false);
                }
            }
        }
    }
}




