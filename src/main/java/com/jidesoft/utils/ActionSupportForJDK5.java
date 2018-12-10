/*
 * @(#)ActionSupportForJDK5.java 7/14/2010
 *
 * Copyright 2002 - 2010 JIDE Software Inc. All rights reserved.
 */

package com.jidesoft.utils;

import javax.swing.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * In JDK6, Swing adds three new properties on Action class: SELECTED_KEY, DISPLAYED_MNEMONIC_INDEX_KEY and LARGE_ICON. You can find more information at
 * http://weblogs.java.net/blog/zixle/archive/2005/11/changes_to_acti.html
 * <p/>
 * However, for users who are still using JDK5, you are out of luck. In this class, we provide a simple way to use those new properties
 * on JDK5.
 * <p/>
 * First of all, you need to call this method.
 * <p/>
 * <code><pre>
 * Action action = new AbstractAction("Text") {...};
 * JButton button = new JButton(action);
 * ActionSupportForJDK5.install(button);
 * </pre></code>
 * <p/>
 * When you about to change the selected state of action, you call ActionSupportForJDK5.setActionSelected(action, selected). The selected value could
 * be true or false. This call will automatically make the button selected or not selected.
 * <p/>
 * There are also setDisplayedMnemonicIndex and setLargeIcon methods on ActionSupportForJDK5 to the other two new properties.
 * <p/>
 * Last but not least, if you don't use the button anymore, it is a good practice to call ActionSupportForJDK5.uninstall to remove the installed listeners.
 */
public class ActionSupportForJDK5 {
    public static final String SELECTED_KEY = "SwingSelectedKey";
    public static final String DISPLAYED_MNEMONIC_INDEX_KEY = "SwingDisplayedMnemonicIndexKey";
    public static final String LARGE_ICON_KEY = "SwingLargeIconKey";

    protected static final String CLIENT_PROPERTY_PROPERTY_CHANGE_LISTENER = "ActionSupportForJDK5.propertyChangeListener";
    protected static final String CLIENT_PROPERTY_ITEM_LISTENER = "ActionSupportForJDK5.itemListener";

    public static void setActionSelected(Action action, boolean selected) {
        action.putValue(SELECTED_KEY, selected);
    }

    public static boolean isActionSelected(Action action) {
        return Boolean.TRUE.equals(action.getValue(SELECTED_KEY));
    }

    public static void setDisplayedMnemonicIndex(Action action, int newIndex) {
        action.putValue(DISPLAYED_MNEMONIC_INDEX_KEY, newIndex);
    }

    public static boolean hasDisplayedMnemonicIndex(Action action) {
        return action.getValue(DISPLAYED_MNEMONIC_INDEX_KEY) != null;
    }

    public static int getDisplayedMnemonicIndex(Action action) {
        if (hasDisplayedMnemonicIndex(action)) {
            return (Integer) action.getValue(DISPLAYED_MNEMONIC_INDEX_KEY);
        }
        else {
            return -1;
        }
    }

    public static void setLargeIcon(Action action, Icon icon) {
        action.putValue(LARGE_ICON_KEY, icon);
    }

    public static Icon getLargeIcon(Action action) {
        Object o = action.getValue(LARGE_ICON_KEY);
        return o instanceof Icon ? (Icon) o : null;
    }

    public static void install(final AbstractButton button, final Action action) {
        if (isActionSelected(action)) {
            setSelectedFromAction(button, action);
        }
        if (getDisplayedMnemonicIndex(action) != -1) {
            setDisplayedMnemonicIndexFromAction(button, action, true);
        }

        PropertyChangeListener listener = new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent e) {
                ActionSupportForJDK5.actionPropertyChanged(button, action, e.getPropertyName());
            }
        };
        action.addPropertyChangeListener(listener);
        ItemListener itemListener = new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                setActionSelected(action, e.getStateChange() == ItemEvent.SELECTED);
            }
        };
        button.addItemListener(itemListener);
        button.putClientProperty(CLIENT_PROPERTY_PROPERTY_CHANGE_LISTENER, listener);
        button.putClientProperty(CLIENT_PROPERTY_ITEM_LISTENER, itemListener);

    }

    public static void install(final AbstractButton button) {
        Action action = button.getAction();
        install(button, action);
    }

    public static void uninstall(final AbstractButton button, final Action action) {
        Object o = button.getClientProperty(CLIENT_PROPERTY_PROPERTY_CHANGE_LISTENER);
        if (o instanceof PropertyChangeListener) {
            action.removePropertyChangeListener((PropertyChangeListener) o);
        }
        o = button.getClientProperty(CLIENT_PROPERTY_ITEM_LISTENER);
        if (o instanceof ItemListener) {
            button.removeItemListener((ItemListener) o);
        }
    }

    public static void uninstall(final AbstractButton button) {
        Action action = button.getAction();
        uninstall(button, action);
    }

    public static void actionPropertyChanged(AbstractButton button, Action action, String propertyName) {
        if (SELECTED_KEY.equals(propertyName) && hasSelectedKey(action))
            setSelectedFromAction(button, action);
        else if (DISPLAYED_MNEMONIC_INDEX_KEY.equals(propertyName))
            setDisplayedMnemonicIndexFromAction(button, action, true);
        else if (LARGE_ICON_KEY.equals(propertyName))
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
                if (buttongroup != null) {
                    buttongroup.remove(button);
                    button.setSelected(false);
                    buttongroup.add(button);
                }
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

//    public static void main(String[] argv) {
//        JFrame frame = new JFrame();
//        final AbstractAction abstractAction = new AbstractAction("Action") {
//            public void actionPerformed(ActionEvent actionEvent) {
//                System.out.println("print");
//            }
//        };
//        ActionSupportForJDK5.setActionSelected(abstractAction, true);
//        ActionSupportForJDK5.setDisplayedMnemonicIndex(abstractAction, 2);
//        frame.setLayout(new FlowLayout());
//        final JToggleButton button = new JToggleButton(abstractAction);
//        ButtonGroup group = new ButtonGroup();
//        group.add(button);
//        JToggleButton button2 = new JToggleButton("ABC");
//        group.add(button2);
//        ActionSupportForJDK5.install(button);
//        frame.add(button);
//        frame.add(button2);
//        frame.add(new JButton(new AbstractAction("Select") {
//            public void actionPerformed(ActionEvent actionEvent) {
//                ActionSupportForJDK5.setActionSelected(abstractAction, !ActionSupportForJDK5.isActionSelected(abstractAction));
//            }
//        }));
//        frame.pack();
//        frame.setVisible(true);
//    }

}
