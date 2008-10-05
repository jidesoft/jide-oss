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
     * @param tree
     * @return A TreeSearchable
     */
    public static TreeSearchable installSearchable(JTree tree) {
        return new TreeSearchable(tree);
    }

    /**
     * Installs the searchable function onto a JTable.
     *
     * @param table
     * @return A TableSearchable
     */
    public static TableSearchable installSearchable(JTable table) {
        return new TableSearchable(table);
    }

    /**
     * Installs the searchable function onto a JList.
     *
     * @param list
     * @return A ListSearchable
     */
    public static ListSearchable installSearchable(JList list) {
        return new ListSearchable(list);
    }

    /**
     * Installs the searchable function onto a JComboBox.
     *
     * @param combobox
     * @return A ComboBoxSearchable
     */
    public static ComboBoxSearchable installSearchable(JComboBox combobox) {
        return new ComboBoxSearchable(combobox);
    }

    /**
     * Installs the searchable function onto a JTextComponent.
     *
     * @param textComponent
     * @return A TextComponentSearchable
     */
    public static TextComponentSearchable installSearchable(JTextComponent textComponent) {
        return new TextComponentSearchable(textComponent);
    }

    public static void uninstallSearchable(Searchable searchable) {
        searchable.uninstallListeners();
    }
}
