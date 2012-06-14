/*
 * @(#)TransparentComponentWrapper.java
 *
 * 2002 - 2012 JIDE Software Incorporated. All rights reserved.
 * Copyright (c) 2005 - 2012 Catalysoft Limited. All rights reserved.
 */
package com.jidesoft.swing;

import javax.swing.*;
import java.awt.*;

/**
 * This is a wrapper component for containing another component that uses a transparent (or translucent background).
 * <p/>
 * This class is a solution to a problem that may be encountered in Swing when using transparency in the background of a
 * component. The problem is that when you want to use a background colour with transparency, you need to set the opaque
 * property of the component to be false to force the opaque parent components to be painted first. The catch is that if
 * the opaque property is set to false, the component's background may not be painted at all - so you lose the
 * transparency effect you wanted to achieve.
 * <p/>
 * A component that uses a transparent background can be wrapped by this class, which has opaque set to false (to ensure
 * that the opaque parents are painted first) and also takes on the responsibility of painting the background of the
 * wrapped component.
 */
public class TransparentComponentWrapper extends JComponent {

    private JComponent delegate;

    public TransparentComponentWrapper(JComponent delegate) {
        super();
        this.delegate = delegate;
        setOpaque(false);
        delegate.setOpaque(false);
        setLayout(new BorderLayout());
        add(delegate, BorderLayout.CENTER);
    }

    /**
     * Paints the background of this component using the background colour of the delegate
     */
    @Override
    public void paintComponent(Graphics g) {
        Dimension size = getSize();
        g.setColor(delegate.getBackground());
        g.fillRect(0, 0, size.width, size.height);
    }

}
