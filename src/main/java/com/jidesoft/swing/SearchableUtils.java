/*
 * @(#)SearchableUtils.java
 *
 * Copyright 2002 - 2004 JIDE Software Inc. All rights reserved.
 */
package com.jidesoft.swing;

import javax.swing.*;
import javax.swing.text.JTextComponent;

/**
 * Utility class to make component searchable. It's very easy to use this class. In order to make a component, all you
 * need to do is to call
 * <code><pre>
 * SearchableUtils.installSearchable(component);
 * </pre></code>
 * The component could be a JList, JTree or JTable. If you need to further customize some attributes of Searchable, you
 * can assign a variable that returns from installSearchable().
 * <code><pre>
 * Searchable searchable = SearchableUtils.installSearchable(component);
 * // further configure it
 * searchable.setCaseSensitive(true);
 * // ...
 * </pre></code>
 * Usually you don't need to uninstall the searchable from the component. But if for some reason, you need to disable
 * the searchable feature of the component, you can call uninstallSearchable().
 * <code><pre>
 * Searchable searchable = SearchableUtils.installSearchable(component);
 * // ...
 * // Now disable it
 * SearchableUtils.uninstallSearchable(searchable);
 * </pre></code>
 * <p/>
 * There is a small trick that you should know. JTree and JList implemented partially the quick search feature so that
 * when you type in the first character, it will jump to the first occurrence. This feature sometimes conflicts with the
 * Searchable we provided. So it'd better if you disable the JTree or JList default feature by creating JTree and JList
 * with getNextMatch method overridden. See below
 * <code><pre>
 * JTree tree = new JTree(...) {
 *     public TreePath getNextMatch(String prefix, int startingRow, Position.Bias bias) {
 *         return null;
 *     }
 * };
 * <p/>
 * JList list = new JList(...){
 *     public int getNextMatch(String prefix, int startIndex, Position.Bias bias) {
 *         return -1;
 *     }
 * };
 * </pre></code>
 */
public class SearchableUtils {
    /**
     * Installs the searchable function onto a JTree.
     *
     * @param tree the JTree to install searchable
     * @return A TreeSearchable
     */
    public static TreeSearchable installSearchable(JTree tree) {
        return new TreeSearchable(tree);
    }

    /**
     * Installs the searchable function onto a JTable.
     *
     * @param table the JTable to install searchable
     * @return A TableSearchable
     */
    public static TableSearchable installSearchable(JTable table) {
        return new TableSearchable(table);
    }

    /**
     * Installs the searchable function onto a JList.
     *
     * @param list the JList to install searchable
     * @return A ListSearchable
     */
    public static ListSearchable installSearchable(JList list) {
        return new ListSearchable(list);
    }

    /**
     * Installs the searchable function onto a JComboBox.
     *
     * @param combobox the combo box to install searchable
     * @return A ComboBoxSearchable
     */
    public static ComboBoxSearchable installSearchable(JComboBox combobox) {
        return new ComboBoxSearchable(combobox);
    }

    /**
     * Installs the searchable function onto a JTextComponent.
     *
     * @param textComponent the text component to install searchable
     * @return A TextComponentSearchable
     */
    public static TextComponentSearchable installSearchable(JTextComponent textComponent) {
        return new TextComponentSearchable(textComponent);
    }

    /**
     * Uninstall the searchable that was installed to a component
     *
     * @param searchable the searchable.
     */
    public static void uninstallSearchable(Searchable searchable) {
        if (searchable != null) {
            searchable.hidePopup();
            searchable.uninstallListeners();
            if (searchable.getComponent() instanceof JComponent) {
                Object clientProperty = ((JComponent) searchable.getComponent()).getClientProperty(Searchable.CLIENT_PROPERTY_SEARCHABLE);
                if (clientProperty == searchable) {
                    ((JComponent) searchable.getComponent()).putClientProperty(Searchable.CLIENT_PROPERTY_SEARCHABLE, null);
                }
            }
        }
    }

    /**
     * Uninstall the searchable that was installed to a component
     *
     * @param component the component that has a searchable installed.
     */
    public static void uninstallSearchable(JComponent component) {
        if (component != null) {
            Object clientProperty = component.getClientProperty(Searchable.CLIENT_PROPERTY_SEARCHABLE);
            if (clientProperty instanceof Searchable) {
                Searchable searchable = ((Searchable) clientProperty);
                searchable.hidePopup();
                searchable.uninstallListeners();
                component.putClientProperty(Searchable.CLIENT_PROPERTY_SEARCHABLE, null);
            }
        }
    }
}
