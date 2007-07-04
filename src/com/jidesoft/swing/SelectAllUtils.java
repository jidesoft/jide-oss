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
    /**
     * A client property. If set to Boolean.TRUE, we will only select all the text just for the first time when the component gets focus.
     */
    public static final String CLIENT_PROPERTY_ONLYONCE = "SelectAll.onlyOnce";

    private static FocusListener SELECT_ALL = new FocusAdapter() {
        @Override
        public void focusGained(FocusEvent e) {
            Object object = e.getSource();
            if (object instanceof JTextComponent) {
                ((JTextComponent) object).selectAll();
                Object clientProperty = ((JTextComponent) object).getClientProperty(CLIENT_PROPERTY_ONLYONCE);
                if (Boolean.TRUE.equals(clientProperty)) {
                    ((JTextComponent) object).removeFocusListener(SELECT_ALL);
                }
            }
            else if (object instanceof Component) {
                JideSwingUtilities.setRecursively((Component) object, new JideSwingUtilities.Handler() {
                    public boolean condition(Component c) {
                        return c instanceof JTextComponent;
                    }

                    public void action(Component c) {
                        ((JTextComponent) c).selectAll();
                        Object clientProperty = ((JTextComponent) c).getClientProperty(CLIENT_PROPERTY_ONLYONCE);
                        if (Boolean.TRUE.equals(clientProperty)) {
                            c.removeFocusListener(SELECT_ALL);
                        }
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
     * @param component the component to make it select all when having focus. The component could be a JTextComponent or could be
     *                  a container that contains one or more JTextComponents. This install method will make all JTextComponents
     *                  to have this select all feature.
     */
    public static void install(final Component component) {
        install(component, true);
    }

    /**
     * Installs focus listener to all text components inside the component. This focus listener
     * will select all the text when it gets focus.
     *
     * @param component the component to make it select all when having focus. The component could be a JTextComponent or could be
     *                  a container that contains one or more JTextComponents. This install method will make all JTextComponents
     *                  to have this select all feature.
     * @param onlyOnce  if true, we will only select all the text when the component has focus for the first time. Otherwise, it will
     *                  always select all the text whenever the component receives focus.
     */
    public static void install(final Component component, final boolean onlyOnce) {
        if (component instanceof JTextComponent) {
            if (onlyOnce) {
                ((JTextComponent) component).putClientProperty(CLIENT_PROPERTY_ONLYONCE, Boolean.TRUE);
            }
            component.addFocusListener(SELECT_ALL);
        }
        else {
            JideSwingUtilities.setRecursively(component, new JideSwingUtilities.Handler() {
                public boolean condition(Component c) {
                    return c instanceof JTextComponent;
                }

                public void action(Component c) {
                    if (onlyOnce) {
                        ((JTextComponent) c).putClientProperty(CLIENT_PROPERTY_ONLYONCE, Boolean.TRUE);
                    }
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
     * @param component the component which {@link #install(java.awt.Component)} is called.
     */
    public static void uninstall(Component component) {
        if (component instanceof JTextComponent) {
            component.removeFocusListener(SELECT_ALL);
        }
        else {
            JideSwingUtilities.setRecursively(component, new JideSwingUtilities.Handler() {
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
