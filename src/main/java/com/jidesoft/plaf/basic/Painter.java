/*
 * @(#)Painter.java
 *
 * Copyright 2002 - 2004 JIDE Software Inc. All rights reserved.
 */
package com.jidesoft.plaf.basic;

import javax.swing.*;
import javax.swing.plaf.UIResource;
import java.awt.*;

/**
 * An interface which can be used to paint any area. The main usage of this interface is to
 * allow user to customize certain UI elements by adding it to UIManager.
 */
public interface Painter extends UIResource {
    void paint(JComponent c, Graphics g, Rectangle rect, int orientation, int state);
}
