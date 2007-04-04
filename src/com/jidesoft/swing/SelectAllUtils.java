/*
 * @(#)SelectAllFocusListener.java 8/30/2006
 *
 * Copyright 2002 - 2006 JIDE Software Inc. All rights reserved.
 */

package com.jidesoft.swing;

import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

/**
 * <code>SelectAllUtils</code> is a utility class to select all the text
 * in a text component when the component first time receives focus. It's very easy to use it.
 * <pre><code>
 * JTextField field = new JTextField();
 * SelectAllUtils.install(field);
 * </code></pre>
 * The component you pass in can be a JTextComponent or any container that contains
 * one or more JTextComponents. All JTextComponents will be installed such a
 * focus listener to select all when it gets focus for the first time. For example,
 * you can install it to an editable JComboBox.
 * <pre><code>
 * JComboBox comboBox = new JComboBox();
 * comboBox.setEditable(true);
 * SelectAllUtils.install(comboBox);
 * </code></pre>
 * Although JComboBox is not JTextComponent but it contains a JTextField so it
 * will still work. However please make sure call it after the call to
 * comboBox.setEditable(true). Otherwise it will not work because JTextField is not created
 * until setEditable(true) is called.
 */
public class SelectAllUtils {

    private static FocusListener SELECT_ALL = new FocusAdapter() {
        public void focusGained(FocusEvent e) {
            Object object = e.getSource();
            if (object instanceof JTextComponent) {
                ((JTextComponent) object).selectAll();
                ((JTextComponent) object).removeFocusListener(SELECT_ALL);
            }
            else if (object instanceof Component) {
                JideSwingUtilities.setRecursively((Component) object, new JideSwingUtilities.Handler() {
                    public boolean condition(Component c) {
                        return c instanceof JTextComponent;
                    }

                    public void action(Component c) {
                        ((JTextComponent) c).selectAll();
                        ((JTextComponent) c).removeFocusListener(SELECT_ALL);
                    }

                    public void postAction(Component c) {
                    }
                });
            }
        }
    };

    /**
     * Installs focus listener to all text components inside the component. This focus listener
     * will select all the text when it gets focus.
     *
     * @param component
     */
    public static void install(Component component) {
        if (component instanceof JTextComponent) {
            component.addFocusListener(SELECT_ALL);
        }
        else if (component instanceof Component) {
            JideSwingUtilities.setRecursively(component, new JideSwingUtilities.Handler() {
                public boolean condition(Component c) {
                    return c instanceof JTextComponent;
                }

                public void action(Component c) {
                    c.addFocusListener(SELECT_ALL);
                }

                public void postAction(Component c) {
                }
            });
        }
    }

    /**
     * Uninstalls focus listener to all text components inside the component.
     *
     * @param component
     */
    public static void uninstall(Component component) {
        if (component instanceof JTextComponent) {
            component.removeFocusListener(SELECT_ALL);
        }
        else if (component instanceof Component) {
            JideSwingUtilities.setRecursively((Component) component, new JideSwingUtilities.Handler() {
                public boolean condition(Component c) {
                    return c instanceof JTextComponent;
                }

                public void action(Component c) {
                    c.removeFocusListener(SELECT_ALL);
                }

                public void postAction(Component c) {
                }
            });
        }
    }
}
