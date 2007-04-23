/*
 * @(#)DockableFrameUI.java
 *
 * Copyright 2002 JIDE Software Inc. All rights reserved.
 */
package com.jidesoft.plaf;

import javax.swing.plaf.TabbedPaneUI;
import java.awt.*;

/**
 * ComponentUI for JideTabbedPane.
 */
public abstract class JideTabbedPaneUI extends TabbedPaneUI {
    abstract public Component getTabPanel();

    abstract public boolean editTabAt(int tabIndex);

    abstract public boolean isTabEditing();

    abstract public void stopTabEditing();

    abstract public void cancelTabEditing();

    abstract public int getEditingTabIndex();

}
