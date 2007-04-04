/*
 * @(#)DelegateMouseMotionListener.java 1/30/2007
 *
 * Copyright 2002 - 2007 JIDE Software Inc. All rights reserved.
 */

package com.jidesoft.swing;

import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

/**
 * This mouse motion listener can delegate the mouse events to another mouse motion listener.
 * It can be used when you need to do something extra steps when there is a mouse motion event but you still
 * want the original mouse motion listener to be called under certain condition.
 */
public class DelegateMouseMotionListener implements MouseMotionListener {
    private MouseMotionListener _listener;

    public DelegateMouseMotionListener(MouseMotionListener listener) {
        _listener = listener;
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
