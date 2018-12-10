/*
 * @(#)DelegateMouseInputListener.java 10/20/2006
 *
 * Copyright 2002 - 2006 JIDE Software Inc. All rights reserved.
 */

package com.jidesoft.swing;

import javax.swing.event.MouseInputListener;
import java.awt.event.MouseEvent;

/**
 * This mouse input listener can delegate the mouse events to another mouse input listener.
 * It can be used when you need to do something extra steps when there is a mouse event but you still
 * want the original mouse input listener to be called under certain condition.
 */
public class DelegateMouseInputListener implements MouseInputListener {
    private MouseInputListener _listener;

    public DelegateMouseInputListener(MouseInputListener listener) {
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

    public void mouseDragged(MouseEvent e) {
        if (_listener != null) {
            _listener.mouseDragged(e);
        }
    }

    public void mouseMoved(MouseEvent e) {
        if (_listener != null) {
            _listener.mouseMoved(e);
        }
    }
}
