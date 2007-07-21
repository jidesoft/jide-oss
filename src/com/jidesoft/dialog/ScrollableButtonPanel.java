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

    /**
     * Override this method to make sure the button panel expand all the way if the view port is small.
     *
     * @return true if parent is null or parent width is greater than preferred width
     */
    public boolean getScrollableTracksViewportWidth() {
        if (getParent() == null) {
            return true;
        }

        return getParent().getSize().width > getPreferredSize().width;
    }

    public boolean getScrollableTracksViewportHeight() {
        return false;
    }
}
