/*
 * @(#)WindowsIconFactoryEx.java 5/17/2011
 *
 * Copyright 2002 - 2011 JIDE Software Inc. All rights reserved.
 */

package com.jidesoft.plaf.windows;

import com.jidesoft.swing.TristateButtonModel;
import com.jidesoft.swing.TristateCheckBox;

import javax.swing.*;
import javax.swing.plaf.UIResource;
import java.awt.*;
import java.io.Serializable;

public class WindowsIconFactory {
    private static Icon checkBoxIcon;

    private static class CheckBoxIcon implements Icon, Serializable, UIResource {
        final static int csize = 13;
        static int cw = -1;
        static int ch = -1;

        public void paintIcon(Component c, Graphics g, int x, int y) {
            JCheckBox cb = (JCheckBox) c;
            ButtonModel model = cb.getModel();
            XPStyle xp = XPStyle.getXP();

            if (xp != null) {
                TMSchema.State state;

                // the following statement added for LegacyTristateCheckBox
                if (model instanceof TristateButtonModel && ((TristateButtonModel) model).isMixed()) {
                    state = TMSchema.State.MIXEDNORMAL;
                    if (!model.isEnabled()) {
                        state = TMSchema.State.MIXEDDISABLED;
                    }
                    else if (model.isPressed() && model.isArmed()) {
                        state = TMSchema.State.MIXEDPRESSED;
                    }
                    else if (model.isRollover()) {
                        state = TMSchema.State.MIXEDHOT;
                    }
                }
                else {
                    if (model.isSelected()) {
                        state = TMSchema.State.CHECKEDNORMAL;
                        if (!model.isEnabled()) {
                            state = TMSchema.State.CHECKEDDISABLED;
                        }
                        else if (model.isPressed() && model.isArmed()) {
                            state = TMSchema.State.CHECKEDPRESSED;
                        }
                        else if (model.isRollover()) {
                            state = TMSchema.State.CHECKEDHOT;
                        }
                    }
                    else {
                        state = TMSchema.State.UNCHECKEDNORMAL;
                        if (!model.isEnabled()) {
                            state = TMSchema.State.UNCHECKEDDISABLED;
                        }
                        else if (model.isPressed() && model.isArmed()) {
                            state = TMSchema.State.UNCHECKEDPRESSED;
                        }
                        else if (model.isRollover()) {
                            state = TMSchema.State.UNCHECKEDHOT;
                        }
                    }
                }
                TMSchema.Part part = TMSchema.Part.BP_CHECKBOX;
                xp.getSkin(c, part).paintSkin(g, x, y, state);
            }
            else {
                // outer bevel
                if (!cb.isBorderPaintedFlat()) {
                    // Outer top/left
                    g.setColor(UIManager.getColor("CheckBox.shadow"));
                    g.drawLine(x, y, x + 11, y);
                    g.drawLine(x, y + 1, x, y + 11);

                    // Outer bottom/right
                    g.setColor(UIManager.getColor("CheckBox.highlight"));
                    g.drawLine(x + 12, y, x + 12, y + 12);
                    g.drawLine(x, y + 12, x + 11, y + 12);

                    // Inner top.left
                    g.setColor(UIManager.getColor("CheckBox.darkShadow"));
                    g.drawLine(x + 1, y + 1, x + 10, y + 1);
                    g.drawLine(x + 1, y + 2, x + 1, y + 10);

                    // Inner bottom/right
                    g.setColor(UIManager.getColor("CheckBox.light"));
                    g.drawLine(x + 1, y + 11, x + 11, y + 11);
                    g.drawLine(x + 11, y + 1, x + 11, y + 10);

                    // inside box
                    if ((model.isPressed() && model.isArmed()) || !model.isEnabled()) {
                        g.setColor(UIManager.getColor("CheckBox.background"));
                    }
                    else {
                        g.setColor(UIManager.getColor("CheckBox.interiorBackground"));
                    }
                    g.fillRect(x + 2, y + 2, csize - 4, csize - 4);
                }
                else {
                    g.setColor(UIManager.getColor("CheckBox.shadow"));
                    g.drawRect(x + 1, y + 1, csize - 3, csize - 3);

                    if ((model.isPressed() && model.isArmed()) || !model.isEnabled()) {
                        g.setColor(UIManager.getColor("CheckBox.background"));
                    }
                    else {
                        g.setColor(UIManager.getColor("CheckBox.interiorBackground"));
                    }
                    g.fillRect(x + 2, y + 2, csize - 4, csize - 4);
                }

                if (model.isEnabled()) {
                    g.setColor(UIManager.getColor("CheckBox.foreground"));
                }
                else {
                    g.setColor(UIManager.getColor("CheckBox.shadow"));
                }

                if (c instanceof TristateCheckBox && ((TristateCheckBox) c).getModel() instanceof TristateButtonModel && ((TristateButtonModel) ((TristateCheckBox) c).getModel()).isMixed()) {
                    drawSquare(c, g, x, y);
                }
                else
                    // paint check
                    if (model.isSelected()) {
                        g.drawLine(x + 9, y + 3, x + 9, y + 3);
                        g.drawLine(x + 8, y + 4, x + 9, y + 4);
                        g.drawLine(x + 7, y + 5, x + 9, y + 5);
                        g.drawLine(x + 6, y + 6, x + 8, y + 6);
                        g.drawLine(x + 3, y + 7, x + 7, y + 7);
                        g.drawLine(x + 4, y + 8, x + 6, y + 8);
                        g.drawLine(x + 5, y + 9, x + 5, y + 9);
                        g.drawLine(x + 3, y + 5, x + 3, y + 5);
                        g.drawLine(x + 3, y + 6, x + 4, y + 6);
                    }
            }
        }

        // the following method added for LegacyTristateCheckBox
        protected void drawSquare(Component c, Graphics g, int x, int y) {
            final int w = getIconWidth();
            final int h = getIconHeight();
            int margin = w / 3;
            g.fillRect(x + margin, y + margin, w - margin * 2, h - margin * 2);
        }

        public int getIconWidth() {
            XPStyle xp = XPStyle.getXP();
            if (xp != null) {
                if (cw == -1) {
                    cw = xp.getSkin(null, TMSchema.Part.BP_CHECKBOX).getWidth();
                }
                return cw;
            }
            else {
                return csize;
            }
        }

        public int getIconHeight() {
            XPStyle xp = XPStyle.getXP();
            if (xp != null) {
                if (ch == -1) {
                    ch = xp.getSkin(null, TMSchema.Part.BP_CHECKBOX).getHeight();
                }
                return ch;
            }
            else {
                return csize;
            }
        }
    }

    public static Icon getCheckBoxIcon() {
        if (checkBoxIcon == null) {
            checkBoxIcon = new CheckBoxIcon();
        }
        return checkBoxIcon;
    }
}
