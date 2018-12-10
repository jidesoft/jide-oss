/*
 * @(#)DelegateMouseListener.java 1/30/2007
 *
 * Copyright 2002 - 2007 JIDE Software Inc. All rights reserved.
 */

package com.jidesoft.swing;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 * This mouse listener can delegate the mouse events to another mouse listener.
 * It can be used when you need to do something extra steps when there is a mouse event but you still
 * want the original mouse listener to be called under certain condition.
 */
public class DelegateMouseListener implements MouseListener {
    private MouseListener _listener;

    public DelegateMouseListener(MouseListener listener) {
        _listener = listener;
    }

    public void mouseClicked(MouseEvent e) {
        if (_listener != null) {
            _listener.mouseClicked(e);
        }
    }

    public void mousePressed(MouseEvent e) {
        if (_listener != null) {
            _listener.mousePressed(e);
        }
    }

    public void mouseReleased(MouseEvent e) {
        if (_listener != null) {
            _listener.mouseReleased(e);
        }
    }

    public void mouseEntered(MouseEvent e) {
        if (_listener != null) {
            _listener.mouseEntered(e);
        }
    }

    public void mouseExited(MouseEvent e) {
        if (_listener != null) {
            _listener.mouseExited(e);
        }
    }
}
