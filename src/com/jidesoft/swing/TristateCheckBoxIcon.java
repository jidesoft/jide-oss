/*
 * @(#)TristateCheckBoxIcon.java 5/20/2011
 *
 * Copyright 2002 - 2011 JIDE Software Inc. All rights reserved.
 */

package com.jidesoft.swing;

import sun.swing.plaf.synth.SynthIcon;

import javax.swing.*;
import javax.swing.plaf.synth.Region;
import javax.swing.plaf.synth.SynthConstants;
import javax.swing.plaf.synth.SynthContext;
import javax.swing.plaf.synth.SynthLookAndFeel;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TristateCheckBoxIcon implements Icon, SynthConstants {
    private UIDefaults.LazyValue _originalIcon;

    public TristateCheckBoxIcon(UIDefaults.LazyValue originalIcon) {
        _originalIcon = originalIcon;
    }

    private static Map<String, Boolean> _synthIconMap;

    /**
     * Check if the class name is a SynthIcon class name.
     * <p/>
     * It's an interface reserved in case Sun changes the name or package of the class SynthIcon.
     *
     * @param name the class name to check
     * @return true if it's a SynthIcon class name. Otherwise false.
     */
    protected boolean isSynthIconClassName(String name) {
        return name != null && name.contains("sun.swing.plaf.synth.SynthIcon");
    }

    private boolean isSynthIcon(Icon icon) {
        if (_synthIconMap == null) {
            _synthIconMap = new HashMap<String, Boolean>();
        }
        Class<?> aClass = icon.getClass();
        java.util.List<String> classNamesToPut = new ArrayList<String>();
        boolean isSynthIcon = false;
        while (aClass != null) {
            String name = aClass.getCanonicalName();
            if (name != null) {
                Boolean value = _synthIconMap.get(name);
                if (value != null) {
                    return value;
                }
                classNamesToPut.add(name);
                if (isSynthIconClassName(name)) {
                    isSynthIcon = true;
                    break;
                }
            }
            aClass = aClass.getSuperclass();
        }
        for (String name : classNamesToPut) {
            _synthIconMap.put(name, isSynthIcon);
        }
        return isSynthIcon;
    }

    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {
        Icon icon = (Icon) _originalIcon.createValue(UIManager.getDefaults());
        if (isSynthIcon(icon)) {
            int state = getComponentState((JComponent) c);
            if (c instanceof TristateCheckBox && ((TristateCheckBox) c).getModel() instanceof TristateButtonModel && ((TristateButtonModel) ((TristateCheckBox) c).getModel()).isMixed())
                state &= ~SynthConstants.SELECTED;
            SynthContext context = new SynthContext((JComponent) c, Region.CHECK_BOX, SynthLookAndFeel.getStyle((JComponent) c, Region.CHECK_BOX), state);
            final int w = ((SynthIcon) icon).getIconWidth(context);
            final int h = ((SynthIcon) icon).getIconHeight(context);
            ((SynthIcon) icon).paintIcon(context, g, x, y, w, h);
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
        final int w = getIconWidth();
        final int h = getIconHeight();
        int margin = w / 3;
        g.fillRect(x + margin, y + margin, w - margin * 2, h - margin * 2);
    }
}
