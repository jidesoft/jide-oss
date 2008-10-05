/*
 * @(#)AutoCompletionComboBox.java 7/24/2005
 *
 * Copyright 2002 - 2005 JIDE Software Inc. All rights reserved.
 */
package com.jidesoft.swing;

import javax.swing.*;
import java.util.Vector;

/**
 * An auto completion combobox. It used {@link AutoCompletion} to make the combobox auto-completing. You can use {@link
 * AutoCompletion} directly to make any combobox auto-completing. This class is just a convenient class if all you need
 * is an auto complete combobox.
 * <p/>
 * Since auto-complete has to listen to the key user types, it has to be editable. If you want to limit user to the list
 * available in the combobox model, you can call {@link #setStrict(boolean)} and set it to true.
 */
public class AutoCompletionComboBox extends JComboBox {
    protected AutoCompletion _autoCompletion;

    public AutoCompletionComboBox() {
        initComponents();
    }

    public AutoCompletionComboBox(Vector<?> items) {
        super(items);
        initComponents();
    }

    public AutoCompletionComboBox(final Object items[]) {
        super(items);
        initComponents();
    }

    public AutoCompletionComboBox(ComboBoxModel aModel) {
        super(aModel);
        initComponents();
    }

    protected void initComponents() {
        setEditable(true);
        _autoCompletion = createAutoCompletion();
    }

    /**
     * Creates the <code>AutoCompletion</code>.
     *
     * @return the <code>AutoCompletion</code>.
     */
    protected AutoCompletion createAutoCompletion() {
        return new AutoCompletion(this);
    }

    /**
     * Gets the strict property.
     *
     * @return the value of strict property.
     */
    public boolean isStrict() {
        return getAutoCompletion().isStrict();
    }

    /**
     * Sets the strict property. If true, it will not allow user to type in anything that is not in the known item list.
     * If false, user can type in whatever he/she wants. If the text can match with a item in the known item list, it
     * will still auto-complete.
     *
     * @param strict true or false.
     */
    public void setStrict(boolean strict) {
        getAutoCompletion().setStrict(strict);
    }

    /**
     * Gets the strict completion property.
     *
     * @return the value of strict completion property.
     *
     * @see #setStrictCompletion(boolean)
     */
    public boolean isStrictCompletion() {
        return getAutoCompletion().isStrictCompletion();
    }

    /**
     * Sets the strict completion property. If true, in case insensitive searching, it will always use the exact item in
     * the Searchable to replace whatever user types. For example, when Searchable has an item "Arial" and user types in
     * "AR", if this flag is true, it will auto-completed as "Arial". If false, it will be auto-completed as "ARial". Of
     * course, this flag will only make a difference if Searchable is case insensitive.
     *
     * @param strictCompletion
     */
    public void setStrictCompletion(boolean strictCompletion) {
        getAutoCompletion().setStrictCompletion(strictCompletion);
    }

    /**
     * Gets the underlying AutoCompletion class.
     *
     * @return the underlying AutoCompletion.
     */
    public AutoCompletion getAutoCompletion() {
        return _autoCompletion;
    }

}
