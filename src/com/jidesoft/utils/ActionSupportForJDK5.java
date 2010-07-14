/*
 * @(#)ActionSupportForJDK5.java 7/14/2010
 *
 * Copyright 2002 - 2010 JIDE Software Inc. All rights reserved.
 */

package com.jidesoft.utils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * In JDK6, Swing adds three new properties on Action class: SELECTED_KEY, DISPLAYED_MNEMONIC_INDEX_KEY and LARGE_ICON.
 * http://weblogs.java.net/blog/zixle/archive/2005/11/changes_to_acti.html
 * <p/>
 * However, for users who are still using JDK5, you are out of luck. In this class, we provide a simple way to use those new properties
 * on JDK5. You can find more information at
 * <p/>
 * First of all, you need to subclass the button so that you can override actionPropertyChanged method. In the overridden method, you have
 * <p/>
 * <code><pre>
 * protected void actionPropertyChanged(Action action, String propertyName) {
 *     super.actionPropertyChanged(action, propertyName);
 *     ActionSupportForJDK5.actionPropertyChanged(this, action, propertyName);  // this is the added line
 * }
 * </pre></code>
 * <p/>
 * When you about to change the selected state of action, you call ActionSupportForJDK5.setActionSelected(action, selected). The selected value could
 * be true or false.
 * <p/>
 * There are also setDisplayedMnemonicIndex and setLargeIcon methods on ActionSupportForJDK5 to the other two new properties.
 */
public class ActionSupportForJDK5 {
    public static final String SELECTED_KEY = "SwingSelectedKey";
    public static final String DISPLAYED_MNEMONIC_INDEX_KEY = "SwingDisplayedMnemonicIndexKey";
    public static final String LARGE_ICON_KEY = "SwingLargeIconKey";

    protected static final String CLIENT_PROPERTY_EXTRA_PROPERTY_CHANGE_LISTENER = "ActionSupportForJDK5.propertyChangeListener";

    public static void setActionSelected(Action action, boolean selected) {
        action.putValue(SELECTED_KEY, selected);
    }

    public static boolean isActionSelected(Action action) {
        return Boolean.TRUE.equals(action.getValue(SELECTED_KEY));
    }

    public static void setDisplayedMnemonicIndex(Action action, int newIndex) {
        action.putValue(DISPLAYED_MNEMONIC_INDEX_KEY, newIndex);
    }

    public static int getDisplayedMnemonicIndex(Action action) {
        return (Integer) action.getValue(DISPLAYED_MNEMONIC_INDEX_KEY);
    }

    public static void setLargeIcon(Action action, Icon icon) {
        action.putValue(LARGE_ICON_KEY, icon);
    }

    public static Icon getLargeIcon(Action action) {
        Object o = action.getValue(LARGE_ICON_KEY);
        return o instanceof Icon ? (Icon) o : null;
    }
    
    public static void bind(final AbstractButton button, final Action action) {
        PropertyChangeListener listener = new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent e) {
                ActionSupportForJDK5.actionPropertyChanged(button, action, e.getPropertyName());
            }
        };
        action.addPropertyChangeListener(listener);
        button.putClientProperty(CLIENT_PROPERTY_EXTRA_PROPERTY_CHANGE_LISTENER, listener);
    }

    public static void unbind(final AbstractButton button, final Action action) {
        Object o = button.getClientProperty(CLIENT_PROPERTY_EXTRA_PROPERTY_CHANGE_LISTENER);
        if(o instanceof PropertyChangeListener) {
            action.removePropertyChangeListener((PropertyChangeListener) o);
        }
    }

    public static void bind(final AbstractButton button) {
        Action action = button.getAction();
        bind(button, action);
    }

    public static void unbind(final AbstractButton button) {
        Action action = button.getAction();
        unbind(button, action);
    }

    public static void actionPropertyChanged(AbstractButton button, Action action, String propertyName) {
        if (SELECTED_KEY.equals(propertyName) && hasSelectedKey(action))
            setSelectedFromAction(button, action);
        else if (DISPLAYED_MNEMONIC_INDEX_KEY.equals(propertyName))
            setDisplayedMnemonicIndexFromAction(button, action, true);
        else if (SELECTED_KEY.equals(propertyName))
            largeIconChanged(button, action);
    }

    static boolean hasSelectedKey(Action action) {
        return action != null && action.getValue(SELECTED_KEY) != null;
    }

    private static void setSelectedFromAction(AbstractButton button, Action action) {
        boolean flag = false;
        if (action != null)
            flag = isActionSelected(action);
        if (flag != button.isSelected()) {
            button.setSelected(flag);
            if (!flag && button.isSelected() && (button.getModel() instanceof DefaultButtonModel)) {
                ButtonGroup buttongroup = ((DefaultButtonModel) button.getModel()).getGroup();
                if (buttongroup != null)
                    buttongroup.setSelected(null, false);
            }
        }
    }

    private static void setDisplayedMnemonicIndexFromAction(AbstractButton button, Action action, boolean flag) {
        Integer integer = action != null ? (Integer) action.getValue(DISPLAYED_MNEMONIC_INDEX_KEY) : null;
        if (flag || integer != null) {
            int i;
            if (integer == null) {
                i = -1;
            }
            else {
                i = integer;
                String s = button.getText();
                if (s == null || i >= s.length())
                    i = -1;
            }
            button.setDisplayedMnemonicIndex(i);
        }
    }

    static void largeIconChanged(AbstractButton button, Action action) {
        setIconFromAction(button, action);
    }

    static void setIconFromAction(AbstractButton button, Action action) {
        Icon icon = null;
        if (action != null) {
            icon = (Icon) action.getValue(LARGE_ICON_KEY);
            if (icon == null)
                icon = (Icon) action.getValue(Action.SMALL_ICON);
        }
        button.setIcon(icon);
    }

    public static void main(String[] argv){
        JFrame frame = new JFrame();
        final AbstractAction abstractAction = new AbstractAction("Action") {
            public void actionPerformed(ActionEvent actionEvent) {
                System.out.println("print");
            }
        };
        frame.setLayout(new FlowLayout());
        final JButton button = new JButton(abstractAction);
        ButtonGroup group = new ButtonGroup();
        group.add(button);
        JToggleButton button2 = new JToggleButton("ABC");
        group.add(button2);
        ActionSupportForJDK5.bind(button);
        frame.add(button);
        frame.add(button2);
        frame.add(new JButton(new AbstractAction("Select"){
            public void actionPerformed(ActionEvent actionEvent) {
                ActionSupportForJDK5.setActionSelected(abstractAction, !ActionSupportForJDK5.isActionSelected(abstractAction));
            }
        }));
        frame.pack();
        frame.setVisible(true);
    }

}
