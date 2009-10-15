/*
 * @(#)SpinnerWheelSupport.java 7/28/2006
 *
 * Copyright 2002 - 2006 JIDE Software Inc. All rights reserved.
 */

package com.jidesoft.spinner;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

/**
 * A helper class to add mouse wheel support to JSpinner. You can call {@link #installMouseWheelSupport(javax.swing.JSpinner)}
 * to add the support and {@link #uninstallMouseWheelSupport(javax.swing.JSpinner)} to remove the support.
 */
public class SpinnerWheelSupport {

    public static final String CLIENT_PROPERTY_MOUSE_WHEEL_LISTENER = "mouseWheelListener";
    protected static final String ACTION_NAME_INCREMENT = "increment";
    protected static final String ACTION_NAME_DECREMENT = "decrement";

    public static void installMouseWheelSupport(final JSpinner spinner) {
        MouseWheelListener l = new MouseWheelListener() {
            public void mouseWheelMoved(MouseWheelEvent e) {
                int rotation = e.getWheelRotation();
                if (rotation < 0) {
                    Action action = spinner.getActionMap().get(ACTION_NAME_INCREMENT);
                    if (action != null) {
                        action.actionPerformed(new ActionEvent(e.getSource(), 0, ACTION_NAME_INCREMENT));
                    }
                }
                else if (rotation > 0) {
                    Action action = spinner.getActionMap().get(ACTION_NAME_DECREMENT);
                    if (action != null) {
                        action.actionPerformed(new ActionEvent(e.getSource(), 0, ACTION_NAME_DECREMENT));
                    }
                }
            }
        };
        spinner.addMouseWheelListener(l);
        spinner.putClientProperty(CLIENT_PROPERTY_MOUSE_WHEEL_LISTENER, l);
    }

    public static void uninstallMouseWheelSupport(final JSpinner spinner) {
        MouseWheelListener l = (MouseWheelListener) spinner.getClientProperty(CLIENT_PROPERTY_MOUSE_WHEEL_LISTENER);
        if (l != null) {
            spinner.removeMouseWheelListener(l);
        }
    }
}
