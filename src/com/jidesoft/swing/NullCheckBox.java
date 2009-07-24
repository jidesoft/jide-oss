/*
 * @(#)NullCheckBox.java 7/25/2005
 *
 * Copyright 2002 - 2005 JIDE Software Inc. All rights reserved.
 */
package com.jidesoft.swing;

import javax.swing.*;
import javax.swing.plaf.FontUIResource;
import javax.swing.plaf.ColorUIResource;
import java.awt.*;

/**
 * This is part of the null-components. A null component doesn't have foreground, background or font value set. In the
 * other words, the foreground, background and font value of null-component are null. But this doesn't mean
 * getBackground(), getForeground() or getFont() will return null. According to {@link
 * java.awt.Component#getBackground()}, {@link java.awt.Component#getForeground()} and {@link
 * java.awt.Component#getFont()}, if the value is null, it will get the value from its parent. In the other words, if
 * you add a null-component to JPanel, you can use JPanel to control the background, foreground and font of this
 * null-component. The feature is very helpful if you want to make sure all components in a JPanel has the same
 * background, foreground or font.
 * <p/>
 * Even in null-components, you can still change the foreground, background or font value if you do want. However, you'll
 * have to use a font which is not an instance of FontUIResource or a color which is not an instance of ColorUIResource.
 * <p/>
 * We creates a few null-components. It doesn't cover all components. You can always create your own. All you need to do
 * is this
 * <pre><code>
 * public class NullXxxComponent extends XxxComponent {
 *     // invoke clearAttribute() in all the constructors
 * <p/>
 * public void setFont(Font font) {
 *     if (font instanceof FontUIResource) {
 *         return;
 *     }
 *     super.setFont(font);
 * }
 * <p/>
 * public void setBackground(Color bg) {
 *     if (bg instanceof ColorUIResource) {
 *         return;
 *     }
 *     super.setBackground(bg);
 * }
 * <p/>
 * public void setForeground(Color fg) {
 *     if (fg instanceof ColorUIResource) {
 *         return;
 *     }
 *     super.setForeground(fg);
 * }
 * <p/>
 *     private void clearAttribute() {
 *         setFont(null);
 *         setBackground(null);
 *         // do not do this for JButton since JButton always paints button
 *         // content background. So it'd better to leave the foreground alone
 *         setForeground(null);
 *     }
 * }
 * </code></pre>
 *
 * @see com.jidesoft.swing.NullButton
 * @see com.jidesoft.swing.NullPanel
 * @see com.jidesoft.swing.NullJideButton
 * @see com.jidesoft.swing.NullLabel
 * @see com.jidesoft.swing.NullRadioButton
 * @see com.jidesoft.swing.NullTristateCheckBox
 */
public class NullCheckBox extends JCheckBox {
    public NullCheckBox() {
        clearAttribute();
    }

    public NullCheckBox(Icon icon) {
        super(icon);
        clearAttribute();
    }

    public NullCheckBox(Icon icon, boolean selected) {
        super(icon, selected);
        clearAttribute();
    }

    public NullCheckBox(String text) {
        super(text);
        clearAttribute();
    }

    public NullCheckBox(Action a) {
        super(a);
        clearAttribute();
    }

    public NullCheckBox(String text, boolean selected) {
        super(text, selected);
        clearAttribute();
    }

    public NullCheckBox(String text, Icon icon) {
        super(text, icon);
        clearAttribute();
    }

    public NullCheckBox(String text, Icon icon, boolean selected) {
        super(text, icon, selected);
        clearAttribute();
    }

    private void clearAttribute() {
        super.setFont(null);
        super.setBackground(null);
        super.setForeground(null);
    }

    @Override
    public void setFont(Font font) {
        if (font instanceof FontUIResource) {
            return;
        }
        super.setFont(font);
    }

    @Override
    public void setBackground(Color bg) {
        if (bg instanceof ColorUIResource) {
            return;
        }
        super.setBackground(bg);
    }

    @Override
    public void setForeground(Color fg) {
        if (fg instanceof ColorUIResource) {
            return;
        }
        super.setForeground(fg);
    }
}
