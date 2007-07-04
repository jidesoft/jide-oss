/*
 * @(#)DialogPageListCellRenderer.java
 *
 * Copyright 2002 JIDE Software. All rights reserved.
 */
package com.jidesoft.dialog;

import javax.swing.*;
import java.awt.*;

/**
 * A list cell renderer for AbstractDialogPage.
 */
class DialogPageListCellRenderer extends DefaultListCellRenderer {
    @Override
    public Component getListCellRendererComponent(
            JList list,
            Object value,
            int index,
            boolean isSelected,
            boolean cellHasFocus) {
        if (value instanceof AbstractDialogPage) {
            AbstractDialogPage page = (AbstractDialogPage) value;
            return super.getListCellRendererComponent(list, page.getTitle(), index, isSelected, cellHasFocus);
        }
        else {
            return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        }
    }
}
