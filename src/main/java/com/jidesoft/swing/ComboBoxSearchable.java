/*
 * @(#)${NAME}
 *
 * Copyright 2002 - 2004 JIDE Software Inc. All rights reserved.
 */
package com.jidesoft.swing;

import com.jidesoft.swing.event.SearchableEvent;

import javax.swing.*;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * <code>ComboBoxSearchable</code> is an concrete implementation of {@link Searchable} that enables the search function
 * in non-editable JComboBox. <p>It's very simple to use it. Assuming you have a JComboBox, all you need to do is to
 * call
 * <code><pre>
 * JComboBox comboBox = ....;
 * ComboBoxSearchable searchable = new ComboBoxSearchable(comboBox);
 * </pre></code>
 * Now the JComboBox will have the search function.
 * <p/>
 * There is very little customization you need to do to ComboBoxSearchable. The only thing you might need is when the
 * element in the JComboBox needs a special conversion to convert to string. If so, you can override
 * convertElementToString() to provide you own algorithm to do the conversion.
 * <code><pre>
 * JComboBox comboBox = ....;
 * ComboBoxSearchable searchable = new ComboBoxSearchable(comboBox) {
 *      protected String convertElementToString(Object object) {
 *          ...
 *      }
 * };
 * </pre></code>
 * <p/>
 * Additional customization can be done on the base Searchable class such as background and foreground color,
 * keystrokes, case sensitivity,
 */
public class ComboBoxSearchable extends Searchable implements ListDataListener, PropertyChangeListener, PopupMenuListener {

    private boolean _refreshPopupDuringSearching = false;
    private boolean _showPopupDuringSearching = true;

    public ComboBoxSearchable(final JComboBox comboBox) {
        super(comboBox);

        // to avoid conflict with default type-match feature of JComboBox.
        comboBox.setKeySelectionManager(new JComboBox.KeySelectionManager() {
            public int selectionForKey(char aKey, ComboBoxModel aModel) {
                return -1;
            }
        });
        comboBox.getModel().addListDataListener(this);
        comboBox.addPropertyChangeListener("model", this);
        comboBox.addPopupMenuListener(this);

        if (comboBox.isEditable()) {
            Component editorComponent = comboBox.getEditor().getEditorComponent();
            final JTextField textField = (JTextField) editorComponent;
            textField.addKeyListener(new KeyAdapter() {
                @Override
                public void keyReleased(KeyEvent e) {
                    if (e.getKeyChar() != KeyEvent.CHAR_UNDEFINED && e.getKeyCode() != KeyEvent.VK_ENTER
                            && e.getKeyCode() != KeyEvent.VK_ESCAPE) {
                        String text = textField.getText();
                        ComboBoxModel model = comboBox.getModel();
                        ListDataListener removedListener = null;
                        // this is a hack. We have to remove the listener registered in BasicComboBoxUI while filtering the combobox model.
                        // the code below will break if the listener is not a class in BasicComboBoxUI.
                        if (model instanceof AbstractListModel) {
                            ListDataListener[] listeners = ((AbstractListModel) model).getListDataListeners();
                            for (ListDataListener listener : listeners) {
                                //noinspection IndexOfReplaceableByContains
                                if (listener.getClass().toString().indexOf("BasicComboBoxUI") != -1) {
                                    removedListener = listener;
                                    model.removeListDataListener(listener);
                                }
                            }
                        }
                        textChanged(text);
                        if (isShowPopupDuringSearching()) {
                            if (!comboBox.getUI().getClass().getName().contains("ExComboBoxUI")) { // only cover the JComboBox for now but not the subclass of JComboBox because don't want dependency on ExComboBox in JCL
                                comboBox.hidePopup();
                            }
                            comboBox.showPopup();
                        }
                        if (removedListener != null) {
                            model.addListDataListener(removedListener);
                        }
                    }
                }
            });
            setSearchableProvider(new SearchableProvider() {
                @Override
                public String getSearchingText() {
                    return textField.getText();
                }

                @Override
                public boolean isPassive() {
                    return true;
                }

                @Override
                public void processKeyEvent(KeyEvent e) {
                }
            });
        }
    }

    @Override
    public void uninstallListeners() {
        super.uninstallListeners();
        if (_component instanceof JComboBox) {
            ((JComboBox) _component).getModel().removeListDataListener(this);
            ((JComboBox) _component).removePopupMenuListener(this);
        }
        _component.removePropertyChangeListener("model", this);
    }

    /**
     * Checks if the popup is showing during searching.
     *
     * @return true if popup is visible during searching.
     */
    public boolean isShowPopupDuringSearching() {
        return _showPopupDuringSearching;
    }

    /**
     * Sets the property which determines if the popup should be shown during searching.
     *
     * @param showPopupDuringSearching the flag indicating if we should show popup during searching
     */
    public void setShowPopupDuringSearching(boolean showPopupDuringSearching) {
        _showPopupDuringSearching = showPopupDuringSearching;
    }

    /**
     * Checks if the popup should be refreshed during searching.
     * <p/>
     * By default, the value is false. ComboBoxShrinkSearchSupport will set this flag to true.
     *
     * @return true if popup is refreshed during searching.
     */
    public boolean isRefreshPopupDuringSearching() {
        return _refreshPopupDuringSearching;
    }

    /**
     * Sets the property which determines if the popup should be refreshed during searching.
     *
     * @param refreshPopupDuringSearching the flag indicating if we should refresh popup during searching
     */
    public void setRefreshPopupDuringSearching(boolean refreshPopupDuringSearching) {
        _refreshPopupDuringSearching = refreshPopupDuringSearching;
    }

    @Override
    protected void setSelectedIndex(int index, boolean incremental) {
        if (((JComboBox) _component).getSelectedIndex() != index) {
            ((JComboBox) _component).setSelectedIndex(index);
        }
        if (isShowPopupDuringSearching() || isRefreshPopupDuringSearching()) {
            if (_component.getClientProperty("ShrinkSearchableSupport") != null && ((JComboBox) _component).isPopupVisible()) {
                boolean old = isHideSearchPopupOnEvent();
                setHideSearchPopupOnEvent(false);
                ((JComboBox) _component).hidePopup();
                setHideSearchPopupOnEvent(old);
            }
            try {
                if (!((JComboBox) _component).isPopupVisible() &&
                        KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner() != null &&
                        SwingUtilities.isDescendingFrom(KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner(), _component)) {
                    ((JComboBox) _component).showPopup();
                }
            }
            catch (IllegalComponentStateException e) {
                //null
            }
        }
    }

    @Override
    protected int getSelectedIndex() {
        return ((JComboBox) _component).getSelectedIndex();
    }

    @Override
    protected Object getElementAt(int index) {
        ComboBoxModel comboBoxModel = ((JComboBox) _component).getModel();
        return comboBoxModel.getElementAt(index);
    }

    @Override
    protected int getElementCount() {
        ComboBoxModel comboBoxModel = ((JComboBox) _component).getModel();
        return comboBoxModel.getSize();
    }

    /**
     * Converts the element in JCombobox to string. The returned value will be the <code>toString()</code> of whatever
     * element that returned from <code>list.getModel().getElementAt(i)</code>.
     *
     * @param object the object to be converted
     *
     * @return the string representing the element in the JComboBox.
     */
    @Override
    protected String convertElementToString(Object object) {
        if (object != null) {
            return object.toString();
        }
        else {
            return "";
        }
    }

    public void contentsChanged(ListDataEvent e) {
        if (!isProcessModelChangeEvent()) {
            return;
        }
        if (e.getIndex0() == -1 && e.getIndex1() == -1) {
        }
        else {
            hidePopup();
            fireSearchableEvent(new SearchableEvent(this, SearchableEvent.SEARCHABLE_MODEL_CHANGE));
        }
    }

    public void intervalAdded(ListDataEvent e) {
        if (!isProcessModelChangeEvent()) {
            return;
        }
        hidePopup();
        fireSearchableEvent(new SearchableEvent(this, SearchableEvent.SEARCHABLE_MODEL_CHANGE));
    }

    public void intervalRemoved(ListDataEvent e) {
        if (!isProcessModelChangeEvent()) {
            return;
        }
        hidePopup();
        fireSearchableEvent(new SearchableEvent(this, SearchableEvent.SEARCHABLE_MODEL_CHANGE));
    }

    public void propertyChange(PropertyChangeEvent evt) {
        if ("model".equals(evt.getPropertyName())) {
            hidePopup();

            if (evt.getOldValue() instanceof ComboBoxModel) {
                ((ComboBoxModel) evt.getOldValue()).removeListDataListener(this);
            }

            if (evt.getNewValue() instanceof ComboBoxModel) {
                ((ComboBoxModel) evt.getNewValue()).addListDataListener(this);
            }
            fireSearchableEvent(new SearchableEvent(this, SearchableEvent.SEARCHABLE_MODEL_CHANGE));
        }
    }

    public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
    }

    public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
        if (isHideSearchPopupOnEvent()) {
            hidePopup();
        }
    }

    public void popupMenuCanceled(PopupMenuEvent e) {
    }
}
