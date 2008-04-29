/*
 * @(#)HollowButton.java 7/12/2005
 *
 * Copyright 2002 - 2005 JIDE Software Inc. All rights reserved.
 */
package com.jidesoft.swing;

import com.jidesoft.plaf.XPUtils;

import javax.swing.*;

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
 * We creates a few null-components. It doesn't cover all components. You can always create your own. All you need to do
 * is this
 * <pre><code>
 * public class NullXxxComponent extends XxxComponent {
 *     // all the constructors
 * <p/>
 *     public void updateUI() {
 *         super.updateUI();
 *         clearAttribute();
 *     }
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
 * @see NullPanel
 * @see NullCheckBox
 * @see NullJideButton
 * @see NullLabel
 * @see NullRadioButton
 */
public class NullButton extends JButton {
    public NullButton() {
    }

    public NullButton(Icon icon) {
        super(icon);
    }

    public NullButton(String text) {
        super(text);
    }

    public NullButton(Action a) {
        super(a);
    }

    public NullButton(String text, Icon icon) {
        super(text, icon);
    }

    @Override
    public void updateUI() {
        super.updateUI();
        clearAttribute();
    }

    private void clearAttribute() {
        setFont(null);
        if (XPUtils.isXPStyleOn()) {
            setBackground(null);
        }
    }
}
