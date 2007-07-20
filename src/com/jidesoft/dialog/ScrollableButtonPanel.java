package com.jidesoft.dialog;

import javax.swing.*;
import java.awt.*;

/**
 * A <code>ButtonPanel</code> that implements the <code>Scrollable</code> so that it can
 * be added to a JScrollPane.
 */
public class ScrollableButtonPanel extends ButtonPanel implements Scrollable {

    public ScrollableButtonPanel() {
    }

    public ScrollableButtonPanel(int alignment) {
        super(alignment);
    }

    public ScrollableButtonPanel(int alignment, int sizeContraint) {
        super(alignment, sizeContraint);
    }

    public Dimension getPreferredScrollableViewportSize() {
        return getPreferredSize();
    }

    public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
        if (getComponentCount() > 0) {
            Component c = getComponent(0);
            if (orientation == SwingConstants.HORIZONTAL)
                return c.getWidth();
            else
                return c.getHeight();
        }
        return 50;
    }

    public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
        if (orientation == SwingConstants.HORIZONTAL)
            return visibleRect.width;
        else
            return visibleRect.width;
    }

    public boolean getScrollableTracksViewportWidth() {
        return true;
    }

    public boolean getScrollableTracksViewportHeight() {
        return false;
    }
}
