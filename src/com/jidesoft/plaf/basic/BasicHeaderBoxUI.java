/*
 * @(#)BasicHeaderBoxUI.java 4/27/2006
 *
 * Copyright 2002 - 2006 JIDE Software Inc. All rights reserved.
 */

package com.jidesoft.plaf.basic;

import com.jidesoft.plaf.HeaderBoxUI;
import com.jidesoft.plaf.UIDefaultsLookup;
import com.jidesoft.swing.HeaderBox;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.MouseInputAdapter;
import javax.swing.plaf.ComponentUI;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

/**
 * JideButtonUI implementation
 */
public class BasicHeaderBoxUI extends HeaderBoxUI {
    // Shared UI object
    private static HeaderBoxUI _headerBoxUI;
    private Border _border;

    public static ComponentUI createUI(JComponent c) {
        if (_headerBoxUI == null) {
            _headerBoxUI = new BasicHeaderBoxUI();
        }
        return _headerBoxUI;
    }

    @Override
    public void installUI(JComponent c) {
        HeaderBox p = (HeaderBox) c;
        super.installUI(p);
        installDefaults(p);
        installListeners(p);
    }

    @Override
    public void uninstallUI(JComponent c) {
        HeaderBox p = (HeaderBox) c;
        uninstallDefaults(p);
        uninstallListeners(p);
        super.uninstallUI(c);
    }

    protected class RolloverMouseInputAdapter extends MouseInputAdapter {
        private long lastPressedTimestamp = -1;
        private boolean shouldDiscardRelease = false;

        @Override
        public void mousePressed(MouseEvent e) {
            if (SwingUtilities.isLeftMouseButton(e)) {
                AbstractButton b = (AbstractButton) e.getSource();

                if (b.contains(e.getX(), e.getY())) {
                    long multiClickThreshhold = b.getMultiClickThreshhold();
                    long lastTime = lastPressedTimestamp;
                    long currentTime = lastPressedTimestamp = e.getWhen();
                    if (lastTime != -1 && currentTime - lastTime < multiClickThreshhold) {
                        shouldDiscardRelease = true;
                        return;
                    }

                    ButtonModel model = b.getModel();
                    if (!model.isEnabled()) {
                        // Disabled buttons ignore all input...
                        return;
                    }
                    if (!model.isArmed()) {
                        // button not armed, should be
                        model.setArmed(true);
                    }
                    model.setPressed(true);
                    if (!b.hasFocus() && b.isRequestFocusEnabled()) {
                        b.requestFocus();
                    }
                }
            }
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            if (SwingUtilities.isLeftMouseButton(e)) {
                // Support for multiClickThreshhold
                if (shouldDiscardRelease) {
                    shouldDiscardRelease = false;
                    return;
                }
                AbstractButton b = (AbstractButton) e.getSource();
                ButtonModel model = b.getModel();
                model.setPressed(false);
                model.setArmed(false);
            }
        }

        @Override
        public void mouseEntered(MouseEvent e) {
            AbstractButton b = (AbstractButton) e.getSource();
            ButtonModel model = b.getModel();
            if (b.isRolloverEnabled() && !SwingUtilities.isLeftMouseButton(e)) {
                model.setRollover(true);
            }
            if (model.isPressed())
                model.setArmed(true);
        }

        @Override
        public void mouseExited(MouseEvent e) {
            AbstractButton b = (AbstractButton) e.getSource();
            ButtonModel model = b.getModel();
            if (b.isRolloverEnabled() && !b.contains(e.getPoint())) {
                model.setRollover(false);
            }
            model.setArmed(false);
        }
    }

    protected void installListeners(JComponent c) {
        MouseInputAdapter l = createHeaderBoxMouseListener();
        c.addMouseListener(l);
    }

    /**
     * Returns the ButtonListener for the passed in Button, or null if one could not be found.
     */
    private RolloverMouseInputAdapter getMouseListener(HeaderBox b) {
        MouseMotionListener[] listeners = b.getMouseMotionListeners();

        if (listeners != null) {
            for (int counter = 0; counter < listeners.length; counter++) {
                if (listeners[counter] instanceof RolloverMouseInputAdapter) {
                    return (RolloverMouseInputAdapter) listeners[counter];
                }
            }
        }
        return null;
    }

    private RolloverMouseInputAdapter createHeaderBoxMouseListener() {
        return new RolloverMouseInputAdapter();
    }

    protected void uninstallListeners(JComponent c) {
        HeaderBox b = (HeaderBox) c;
        RolloverMouseInputAdapter listener = getMouseListener(b);
        if (listener != null) {
            b.removeMouseListener(listener);
        }
    }

    protected void installDefaults(HeaderBox p) {
        LookAndFeel.installColorsAndFont(p,
                "Panel.background",
                "Panel.foreground",
                "Panel.font");
        LookAndFeel.installBorder(p, "Panel.border");
        p.setOpaque(true);
        _border = UIDefaultsLookup.getBorder("NestedTableHeader.cellBorder");
    }

    @Override
    public void paint(Graphics g, JComponent c) {
        super.paint(g, c);
        paintBackground(g, c);
        paintBorder(g, c);
    }

    protected void paintBorder(Graphics g, JComponent c) {
        if (_border != null) {
            _border.paintBorder(c, g, 0, 0, c.getWidth(), c.getHeight());
        }
    }

    protected void paintBackground(Graphics g, JComponent c) {
        HeaderBox headerBox = (HeaderBox) c;

        boolean isCellEditor = Boolean.TRUE.equals(headerBox.getClientProperty(HeaderBox.CLIENT_PROPERTY_TABLE_CELL_EDITOR));

        if (headerBox.getModel().isPressed() || headerBox.getModel().isSelected()) {
            if (isCellEditor) {
                g.setColor(new Color(222, 223, 216));
                g.fillRect(0, 0, c.getWidth(), c.getHeight());
            }
            else {
                g.setColor(new Color(222, 223, 216));
                g.fillRoundRect(0, 0, c.getWidth(), c.getHeight(), 6, 6);

                g.setColor(Color.LIGHT_GRAY);
                g.drawRoundRect(0, 0, c.getWidth() - 1, c.getHeight() - 1, 4, 4);
            }
        }
        else {
            if (isCellEditor) {
                g.setColor(new Color(235, 234, 219));
                g.fillRect(0, 0, c.getWidth() - 1, c.getHeight() - 1);
            }
            else {
                g.setColor(new Color(235, 234, 219));
                g.fillRoundRect(0, 0, c.getWidth() - 1, c.getHeight() - 1, 2, 2);

                g.setColor(Color.LIGHT_GRAY);
                g.drawRoundRect(0, 0, c.getWidth() - 1, c.getHeight() - 1, 2, 4);
            }

            g.setColor(new Color(226, 222, 205));
            g.drawLine(1, c.getHeight() - 3, c.getWidth() - 2, c.getHeight() - 3);
            g.setColor(new Color(214, 210, 194));
            g.drawLine(1, c.getHeight() - 2, c.getWidth() - 2, c.getHeight() - 2);

            if (isCellEditor) {
                g.setColor(new Color(198, 197, 178));
                g.drawLine(c.getWidth() - 3, 4, c.getWidth() - 3, c.getHeight() - 7);
                g.setColor(Color.WHITE);
                g.drawLine(c.getWidth() - 2, 4, c.getWidth() - 2, c.getHeight() - 7);
            }
        }
    }

    protected void uninstallDefaults(HeaderBox p) {
        LookAndFeel.uninstallBorder(p);
        _border = null;
    }
}
