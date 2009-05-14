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
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.awt.*;

/**
 * <code>ListSearchable</code> is an concrete implementation of {@link Searchable} that enables the search function in
 * JList. <p>It's very simple to use it. Assuming you have a JList, all you need to do is to call
 * <code><pre>
 * JList list = ....;
 * ListSearchable searchable = new ListSearchable(list);
 * </pre></code>
 * Now the JList will have the search function.
 * <p/>
 * There is very little customization you need to do to ListSearchable. The only thing you might need is when the
 * element in the JList needs a special conversion to convert to string. If so, you can overide convertElementToString()
 * to provide you own algorithm to do the conversion.
 * <code><pre>
 * JList list = ....;
 * ListSearchable searchable = new ListSearchable(list) {
 *      protected String convertElementToString(Object object) {
 *          ...
 *      }
 * };
 * </pre></code>
 * <p/>
 * Additional customization can be done on the base Searchable class such as background and foreground color,
 * keystrokes, case sensitivity.
 * <p/>
 * JList actually has a simple searchable feature but has flaws. It will affect our searchable feature. To workaround
 * it, you can override getNextMatch method and always return -1 when you create your JList. <code>
 * <pre>
 * JList list = new JList(...) {
 *     public int getNextMatch(String prefix, int startIndex, Position.Bias bias) {
 *         return -1;
 *     }
 * };
 * </pre>
 * </code>
 */
public class ListSearchable extends Searchable implements ListDataListener, PropertyChangeListener {
    private boolean _useRendererAsConverter = false;

    public ListSearchable(JList list) {
        super(list);
        list.getModel().addListDataListener(this);
        list.addPropertyChangeListener("model", this);
    }

    @Override
    public void uninstallListeners() {
        super.uninstallListeners();
        if (_component instanceof JList) {
            ((JList) _component).getModel().removeListDataListener(this);
        }
        _component.removePropertyChangeListener("model", this);
    }


    @Override
    public void setSelectedIndex(int index, boolean incremental) {
        if (incremental) {
            ((JList) _component).addSelectionInterval(index, index);
        }
        else {
            if (((JList) _component).getSelectedIndex() != index) {
                ((JList) _component).setSelectedIndex(index);
            }
        }
        ((JList) _component).ensureIndexIsVisible(index);
    }

    @Override
    protected int getSelectedIndex() {
        return ((JList) _component).getSelectedIndex();
    }

    @Override
    protected Object getElementAt(int index) {
        ListModel listModel = ((JList) _component).getModel();
        return listModel.getElementAt(index);
    }

    @Override
    protected int getElementCount() {
        ListModel listModel = ((JList) _component).getModel();
        return listModel.getSize();
    }

    /**
     * Converts the element in Jlist to string. The returned value will be the <code>toString()</code> of whatever
     * element that returned from <code>list.getModel().getElementAt(i)</code>.
     *
     * @param object the object to be converted to string
     * @return the string representing the element in the JList.
     */
    @Override
    protected String convertElementToString(Object object) {
        if (isUseRendererAsConverter()) {
            ListCellRenderer renderer = ((JList) _component).getCellRenderer();
            // try to get the string displayed on the list first so we can search exactly on what the customers are looking at
            // if cannot get it, still go object.toString().
            if (renderer != null) {
                Component component = renderer.getListCellRendererComponent((JList) _component, object, 0, false, false);
                if (component != null) {
                    if (component instanceof JLabel) {
                        return ((JLabel) component).getText();
                    }
                    else if (component instanceof CheckBoxListCellRenderer) {
                        ListCellRenderer actualRenderer = ((CheckBoxListCellRenderer) component).getActualListRenderer();
                        if (actualRenderer != null && actualRenderer instanceof JLabel) {
                            return ((JLabel) actualRenderer).getText();
                        }
                    }
                }
            }
        }
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
            return;
        }
        hidePopup();
        fireSearchableEvent(new SearchableEvent(this, SearchableEvent.SEARCHABLE_MODEL_CHANGE));
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

            ListModel oldModel = (ListModel) evt.getOldValue();
            if (oldModel != null) {
                oldModel.removeListDataListener(this);
            }

            ListModel newModel = (ListModel) evt.getNewValue();
            if (newModel != null) {
                newModel.addListDataListener(this);
            }
            fireSearchableEvent(new SearchableEvent(this, SearchableEvent.SEARCHABLE_MODEL_CHANGE));
        }
    }

    /**
     * Get the flag if the ListSearchable should use the renderer in the list as its converter.
     * <p/>
     * The default value for this field is false so we can get higher performance. For AutoFilterBox, we will set it
     * to false automatically.
     *
     * @return true if you want to use the renderer as its converter. Otherwise false.
     */
    public boolean isUseRendererAsConverter() {
        return _useRendererAsConverter;
    }

    /**
     * Set the flag if the ListSearchable should use the renderer in the list as its converter.
     * <p/>
     * @see #isUseRendererAsConverter()
     *
     * @param useRendererAsConverter the flag
     */
    public void setUseRendererAsConverter(boolean useRendererAsConverter) {
        _useRendererAsConverter = useRendererAsConverter;
    }
}
