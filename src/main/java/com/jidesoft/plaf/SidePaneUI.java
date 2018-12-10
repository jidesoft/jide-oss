/*
 * @(#)JideSidePaneUI.java
 *
 * Copyright 2002 JIDE Software Inc. All rights reserved.
 */
package com.jidesoft.plaf;

import com.jidesoft.swing.SidePaneGroup;
import com.jidesoft.swing.SidePaneItem;

import javax.swing.plaf.PanelUI;
import java.awt.*;

/**
 * ComponentUI for SidePane.
 */
public abstract class SidePaneUI extends PanelUI {

    abstract public int getSelectedItemIndex(Point p);

    abstract public SidePaneGroup getGroupForIndex(int index);

    abstract public SidePaneItem getItemForIndex(int index);
}
