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
import javax.swing.event.MouseInputAdapter;
import javax.swing.plaf.ComponentUI;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

/**
 * JideButtonUI implementation
 */
public class BasicHeaderBoxUI extends HeaderBoxUI {
    private static HeaderBoxUI _headerBoxUI;
    protected ThemePainter _painter;

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
                    long multiClickThreshold = b.getMultiClickThreshhold();
                    long lastTime = lastPressedTimestamp;
                    long currentTime = lastPressedTimestamp = e.getWhen();
                    if (lastTime != -1 && currentTime - lastTime < multiClickThreshold) {
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
            if (b.isRolloverEnabled()) {
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
            for (MouseMotionListener listener : listeners) {
                if (listener instanceof RolloverMouseInputAdapter) {
                    return (RolloverMouseInputAdapter) listener;
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
        _painter = (ThemePainter) UIDefaultsLookup.get("Theme.painter");
        LookAndFeel.installColorsAndFont(p,
                "Panel.background",
                "Panel.foreground",
                "Panel.font");
        LookAndFeel.installBorder(p, "Panel.border");
    }

    @Override
    public void paint(Graphics g, JComponent c) {
        super.paint(g, c);
        paintBackground(g, c);
        paintBorder(g, c);
    }

    protected void paintBorder(Graphics g, JComponent c) {
    }

    protected void paintBackground(Graphics g, JComponent c) {
        HeaderBox headerBox = (HeaderBox) c;

        Rectangle rect = new Rectangle(0, 0, c.getWidth(), c.getHeight());
        if (headerBox.getModel().isPressed()) {
            _painter.paintHeaderBoxBackground(c, g, rect, SwingConstants.HORIZONTAL, ThemePainter.STATE_PRESSED);
        }
        else if (headerBox.getModel().isSelected()) {
            _painter.paintHeaderBoxBackground(c, g, rect, SwingConstants.HORIZONTAL, ThemePainter.STATE_SELECTED);
        }
        else if (headerBox.getModel().isRollover()) {
            _painter.paintHeaderBoxBackground(c, g, rect, SwingConstants.HORIZONTAL, ThemePainter.STATE_ROLLOVER);
        }
        else {
            _painter.paintHeaderBoxBackground(c, g, rect, SwingConstants.HORIZONTAL, ThemePainter.STATE_DEFAULT);
        }
    }

    protected void uninstallDefaults(HeaderBox p) {
        LookAndFeel.uninstallBorder(p);
        _painter = null;
    }
}
