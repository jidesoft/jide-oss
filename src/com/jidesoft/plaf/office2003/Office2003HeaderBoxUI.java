/*
 * @(#)Office2003HeaderBoxUI.java 5/6/2006
 *
 * Copyright 2002 - 2006 JIDE Software Inc. All rights reserved.
 */

package com.jidesoft.plaf.office2003;

import com.jidesoft.plaf.HeaderBoxUI;
import com.jidesoft.plaf.basic.BasicHeaderBoxUI;
import com.jidesoft.swing.HeaderBox;

import javax.swing.*;
import javax.swing.plaf.ComponentUI;
import java.awt.*;

public class Office2003HeaderBoxUI extends BasicHeaderBoxUI {
    // Shared UI object
    private static HeaderBoxUI _headerBoxUI;

    public static ComponentUI createUI(JComponent c) {
        if (_headerBoxUI == null) {
            _headerBoxUI = new Office2003HeaderBoxUI();
        }
        return _headerBoxUI;
    }

    protected void paintBorder(Graphics g, JComponent c) {
    }

    public void paintBackground(Graphics g, JComponent c) {
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
        else if (headerBox.getModel().isRollover()) {
            if (isCellEditor) {
                g.setColor(new Color(250, 248, 243));
                g.fillRect(0, 0, c.getWidth() - 1, c.getHeight() - 1);
            }
            else {
                g.setColor(new Color(250, 248, 243));
                g.fillRoundRect(0, 0, c.getWidth() - 1, c.getHeight() - 1, 4, 4);

                g.setColor(Color.LIGHT_GRAY);
                g.drawRoundRect(0, 0, c.getWidth() - 1, c.getHeight() - 1, 4, 4);
            }

            g.setColor(new Color(248, 169, 0));
            g.drawLine(1, c.getHeight() - 3, c.getWidth() - 2, c.getHeight() - 3);
            g.setColor(new Color(246, 196, 86));
            g.drawLine(1, c.getHeight() - 2, c.getWidth() - 2, c.getHeight() - 2);
            g.setColor(new Color(249, 177, 25));
            g.drawLine(3, c.getHeight() - 1, c.getWidth() - 3, c.getHeight() - 1);
        }
        else {
            if (isCellEditor) {
                g.setColor(new Color(235, 234, 219));
                g.fillRect(0, 0, c.getWidth() - 1, c.getHeight() - 1);
            }
            else {
                g.setColor(new Color(235, 234, 219));
                g.fillRoundRect(0, 0, c.getWidth() - 1, c.getHeight() - 1, 4, 4);

                g.setColor(Color.LIGHT_GRAY);
                g.drawRoundRect(0, 0, c.getWidth() - 1, c.getHeight() - 1, 4, 4);
            }

            g.setColor(new Color(226, 222, 205));
            g.drawLine(1, c.getHeight() - 3, c.getWidth() - 2, c.getHeight() - 3);
            g.setColor(new Color(214, 210, 194));
            g.drawLine(1, c.getHeight() - 2, c.getWidth() - 3, c.getHeight() - 2);

            if (isCellEditor) {
                g.setColor(new Color(198, 197, 178));
                g.drawLine(c.getWidth() - 3, 4, c.getWidth() - 3, c.getHeight() - 7);
                g.setColor(Color.WHITE);
                g.drawLine(c.getWidth() - 2, 4, c.getWidth() - 2, c.getHeight() - 7);
            }
        }
    }
}
