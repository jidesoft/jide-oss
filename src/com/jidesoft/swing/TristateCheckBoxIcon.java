/*
 * @(#)TristateCheckBoxIcon.java 5/20/2011
 *
 * Copyright 2002 - 2011 JIDE Software Inc. All rights reserved.
 */

package com.jidesoft.swing;

import com.jidesoft.jdk.JdkSpecificClass;

import javax.swing.*;
import javax.swing.plaf.synth.SynthConstants;
import java.awt.*;

public class TristateCheckBoxIcon implements Icon, SynthConstants {
    private UIDefaults.LazyValue _originalIcon;

    public TristateCheckBoxIcon(UIDefaults.LazyValue originalIcon) {
        _originalIcon = originalIcon;
    }

    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {
        Icon icon = (Icon) _originalIcon.createValue(UIManager.getDefaults());
        if (JdkSpecificClass.isSynthIcon(icon)) {
            int state = getComponentState((JComponent) c);
            if (c instanceof TristateCheckBox && ((TristateCheckBox) c).getModel() instanceof TristateButtonModel && ((TristateButtonModel) ((TristateCheckBox) c).getModel()).isMixed())
                state &= ~SynthConstants.SELECTED;
            JdkSpecificClass.paintCheckBoxIcon((JComponent) c, icon, g, state, x, y);
        }
        else {
            if (c instanceof TristateCheckBox && ((TristateCheckBox) c).getModel() instanceof TristateButtonModel && ((TristateButtonModel) ((TristateCheckBox) c).getModel()).isMixed()) {
                ((TristateButtonModel) ((TristateCheckBox) c).getModel()).internalSetSelected(false);
            }
            icon.paintIcon(c, g, x, y);
            if (c instanceof TristateCheckBox && ((TristateCheckBox) c).getModel() instanceof TristateButtonModel && ((TristateButtonModel) ((TristateCheckBox) c).getModel()).isMixed()) {
                ((TristateButtonModel) ((TristateCheckBox) c).getModel()).internalSetSelected(true);
            }
        }

        g.setColor(UIManager.getColor("CheckBox.foreground"));
        if (c instanceof TristateCheckBox && ((TristateCheckBox) c).getModel() instanceof TristateButtonModel && ((TristateButtonModel) ((TristateCheckBox) c).getModel()).isMixed())
            drawSquare(c, g, x, y);
    }

    /**
     * Returns the current state of the passed in <code>AbstractButton</code>.
     */
    private int getComponentState(JComponent c) {
        int state = ENABLED;

        if (!c.isEnabled()) {
            state = DISABLED;
        }

        AbstractButton button = (AbstractButton) c;
        ButtonModel model = button.getModel();

        if (model.isPressed()) {
            if (model.isArmed()) {
                state = PRESSED;
            }
            else {
                state = MOUSE_OVER;
            }
        }
        if (model.isRollover()) {
            state |= MOUSE_OVER;
        }
        if (model.isSelected()) {
            state |= SELECTED;
        }
        if (c.isFocusOwner() && button.isFocusPainted()) {
            state |= FOCUSED;
        }
        if ((c instanceof JButton) && ((JButton) c).isDefaultButton()) {
            state |= DEFAULT;
        }
        return state;
    }

    @Override
    public int getIconWidth() {
        Icon icon = (Icon) _originalIcon.createValue(UIManager.getDefaults());
        return icon.getIconWidth();
    }

    @Override
    public int getIconHeight() {
        Icon icon = (Icon) _originalIcon.createValue(UIManager.getDefaults());
        return icon.getIconHeight();
    }

    // the following method added for LegacyTristateCheckBox
    protected void drawSquare(Component c, Graphics g, int x, int y) {
        final int w = Math.min(getIconWidth(), getIconHeight());
        final int h = Math.min(getIconWidth(), getIconHeight());
        int xMargin = w / 3;
        int yMargin = h / 3;
        g.fillRect(x + xMargin, y + yMargin, w - xMargin * 2, h - yMargin * 2);
    }
}
