/*
 * @(#)CheckBoxList.java 4/21/2005
 *
 * Copyright 2002 - 2005 JIDE Software Inc. All rights reserved.
 */
package com.jidesoft.swing;

import javax.swing.*;
import javax.swing.text.Position;
import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

/**
 * <code>CheckBoxListWithSelectable</code> is a special JList which uses JCheckBox as the list element. In addition to
 * regular JList feature, it also allows you select any number of elements in the list by selecting the check boxes.
 * <p/>
 * The element is ListModel should be an instance of {@link Selectable}. If you have your own class that represents the
 * element in the list, you can implement <code>Selectable</code> and implements a few very simple methods. If your
 * elements are already in an array or Vector that you pass in to the constructor of JList, we will convert them to
 * {@link DefaultSelectable} which implements <code>Selectable</code> interface.
 * <p/>
 * To select an element, user can mouse click on the check box, or highlight the rows and press SPACE key to toggle the
 * selections.
 * <p/>
 * To listen to the check box selection change, you can call addItemListener to add an ItemListener.
 * <p/>
 * Please note, there are two implementations of CheckBoxList. CheckBoxListWithSelectable is one. There is also another
 * one call CheckBoxList. CheckBoxListWithSelectable is actually the old implementation. In 1.9.2, we introduced a new
 * implementation and renamed the old implementation to CheckBoxListWithSelectable. The main difference between the two
 * implementation is at how the selection state is kept. In new implementation, the selection state is kept at a
 * separate ListSelectionModel which you can get using {@link CheckBoxList#getCheckBoxListSelectionModel()}. The old
 * implementation kept the selection state at Selectable object in the ListModel.
 */
public class CheckBoxListWithSelectable extends JList implements ItemSelectable {

    protected CheckBoxListCellRenderer _listCellRenderer;

    public static final String PROPERTY_CHECKBOX_ENABLED = "checkBoxEnabled";

    private boolean _checkBoxEnabled = true;

    /**
     * Constructs a <code>CheckBoxList</code> with an empty model.
     */
    public CheckBoxListWithSelectable() {
        init();
    }

    /**
     * Constructs a <code>CheckBoxList</code> that displays the elements in the specified <code>Vector</code>. If the
     * Vector contains elements which is not an instance of {@link Selectable}, it will wrap it automatically into
     * {@link DefaultSelectable} and add to ListModel.
     *
     * @param listData the <code>Vector</code> to be loaded into the data model
     */
    public CheckBoxListWithSelectable(final Vector<?> listData) {
        super(wrap(listData));
        init();
    }

    /**
     * Constructs a <code>CheckBoxList</code> that displays the elements in the specified <code>Object[]</code>. If the
     * Object array contains elements which is not an instance of {@link Selectable}, it will wrap it automatically into
     * {@link DefaultSelectable} and add to ListModel.
     *
     * @param listData the array of Objects to be loaded into the data model
     */
    public CheckBoxListWithSelectable(final Object[] listData) {
        super(wrap(listData));
        init();
    }

    /**
     * Constructs a <code>CheckBoxList</code> that displays the elements in the specified, non-<code>null</code> model.
     * All <code>CheckBoxList</code> constructors delegate to this one.
     * <p/>
     * Please note, if you are using this constructor, please make sure all elements in dataModel are instance of {@link
     * Selectable}.
     *
     * @param dataModel the data model for this list
     * @throws IllegalArgumentException if <code>dataModel</code> is <code>null</code>
     */
    public CheckBoxListWithSelectable(ListModel dataModel) {
        super(wrap(dataModel));
        init();
    }

    /**
     * Initialize the CheckBoxList.
     */
    protected void init() {
        setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        _listCellRenderer = createCellRenderer();
        Handler handle = createHandler();
        addMouseListener(handle);
        addKeyListener(handle);
    }

    /**
     * Creates the cell renderer.
     *
     * @return the cell renderer.
     */
    protected CheckBoxListCellRenderer createCellRenderer() {
        return new CheckBoxListCellRenderer();
    }

    /**
     * Creates the mouse listener and key listener used by CheckBoxList.
     *
     * @return the Handler.
     */
    protected Handler createHandler() {
        return new Handler(this);
    }

    /**
     * Sets the selected elements.
     *
     * @param elements the elements to be selected
     */
    public void setSelectedObjects(Object[] elements) {
        Map<Object, String> selected = new HashMap<Object, String>();
        for (Object element : elements) {
            selected.put(element, "");
        }
        setSelectedObjects(selected);
    }

    /**
     * Sets the selected objects.
     *
     * @param objects the elements to be selected in a Vector.
     */
    public void setSelectedObjects(Vector<?> objects) {
        Map<Object, String> selected = new HashMap<Object, String>();
        for (Object element : objects) {
            selected.put(element, "");
        }
        setSelectedObjects(selected);
    }

    @Override
    public ListCellRenderer getCellRenderer() {
        if (_listCellRenderer != null) {
            _listCellRenderer.setActualListRenderer(super.getCellRenderer());
            return _listCellRenderer;
        }
        else {
            return super.getCellRenderer();
        }
    }

    public ListCellRenderer getActualCellRenderer() {
        if (_listCellRenderer != null) {
            return _listCellRenderer.getActualListRenderer();
        }
        else {
            return super.getCellRenderer();
        }
    }

    private void setSelectedObjects(Map<Object, String> selected) {
        for (int i = 0; i < getModel().getSize(); i++) {
            Object elementAt = getModel().getElementAt(i);
            if (elementAt instanceof Selectable) {
                Selectable selectable = (Selectable) elementAt;
                if (selectable instanceof DefaultSelectable) {
                    elementAt = ((DefaultSelectable) selectable).getObject();
                }
                if (selected.get(elementAt) != null) {
                    selectable.setSelected(true);
                    fireItemStateChanged(new ItemEvent(this, ItemEvent.ITEM_STATE_CHANGED, selectable, ItemEvent.SELECTED));
                    selected.remove(elementAt);
                    if (selected.size() == 0) {
                        break;
                    }
                }
                else {
                    if (selectable.isSelected()) {
                        selectable.setSelected(false);
                        fireItemStateChanged(new ItemEvent(this, ItemEvent.ITEM_STATE_CHANGED, selectable, ItemEvent.DESELECTED));
                    }
                }
            }
        }
        repaint();
    }

    private static ListModel wrap(ListModel dataModel) {
        for (int i = 0; i < dataModel.getSize(); i++) {
            if (!(dataModel.getElementAt(i) instanceof Selectable)) {
                throw new IllegalArgumentException("The ListModel contains an element which is not an instance of Selectable at index " + i + ".");
            }
        }
        return dataModel;
    }

    private static Selectable[] wrap(Object[] objects) {
        if (objects instanceof Selectable[]) {
            return (Selectable[]) objects;
        }
        else {
            Selectable[] elements = new Selectable[objects.length];
            for (int i = 0; i < elements.length; i++) {
                elements[i] = new DefaultSelectable(objects[i]);
            }
            return elements;
        }
    }

    private static Vector<?> wrap(Vector<?> objects) {
        Vector<Selectable> elements = new Vector<Selectable>();
        for (Object o : objects) {
            if (o instanceof Selectable) {
                elements.add((Selectable) o);
            }
            else {
                elements.add(new DefaultSelectable(o));
            }
        }
        return elements;
    }

    protected static class Handler implements MouseListener, KeyListener {
        protected CheckBoxListWithSelectable _list;
        int _hotspot = new JCheckBox().getPreferredSize().width;

        public Handler(CheckBoxListWithSelectable list) {
            _list = list;
        }

        protected boolean clicksInCheckBox(MouseEvent e) {
            int index = _list.locationToIndex(e.getPoint());
            Rectangle bounds = _list.getCellBounds(index, index);
            if (bounds != null) {
                if (_list.getComponentOrientation().isLeftToRight()) {
                    return e.getX() < bounds.x + _hotspot;
                }
                else {
                    return e.getX() > bounds.x + bounds.width - _hotspot;
                }
            }
            else {
                return false;
            }

        }

        public void mouseClicked(MouseEvent e) {
        }

        public void mousePressed(MouseEvent e) {
            if (!_list.isCheckBoxEnabled() || !_list.isEnabled()) {
                return;
            }

            if (clicksInCheckBox(e)) {
                int index = _list.locationToIndex(e.getPoint());
                toggleSelection(index);
            }
        }

        public void mouseReleased(MouseEvent e) {
        }

        public void mouseEntered(MouseEvent e) {
        }

        public void mouseExited(MouseEvent e) {
        }

        public void keyPressed(KeyEvent e) {
            if (!_list.isCheckBoxEnabled() || !_list.isEnabled()) {
                return;
            }

            if (e.getModifiers() == 0 && e.getKeyChar() == KeyEvent.VK_SPACE)
                toggleSelections();
        }

        public void keyTyped(KeyEvent e) {
        }

        public void keyReleased(KeyEvent e) {
        }

        protected void toggleSelections() {
            int[] indices = _list.getSelectedIndices();
            ListModel model = _list.getModel();
            for (int index : indices) {
                Object element = model.getElementAt(index);
                if (element instanceof Selectable && ((Selectable) element).isEnabled()) {
                    ((Selectable) element).invertSelected();
                    boolean selected = ((Selectable) element).isSelected();
                    _list.fireItemStateChanged(new ItemEvent(_list, ItemEvent.ITEM_STATE_CHANGED, element, selected ? ItemEvent.SELECTED : ItemEvent.DESELECTED));
                }
            }
            _list.repaint();
        }

        protected void toggleSelection(int index) {
            ListModel model = _list.getModel();
            if (index >= 0) {
                Object element = model.getElementAt(index);
                if (element instanceof Selectable && ((Selectable) element).isEnabled()) {
                    ((Selectable) element).invertSelected();
                    boolean selected = ((Selectable) element).isSelected();
                    _list.fireItemStateChanged(new ItemEvent(_list, ItemEvent.ITEM_STATE_CHANGED, element, selected ? ItemEvent.SELECTED : ItemEvent.DESELECTED));
                }
                _list.repaint();
            }
        }

        protected void toggleSelection() {
            int index = _list.getSelectedIndex();
            toggleSelection(index);
        }
    }

    /**
     * Adds a listener to the list that's notified each time a change to the item selection occurs.  Listeners added
     * directly to the <code>CheckBoxList</code> will have their <code>ItemEvent.getSource() == this
     * CheckBoxList</code>.
     *
     * @param listener the <code>ItemListener</code> to add
     */
    public void addItemListener(ItemListener listener) {
        listenerList.add(ItemListener.class, listener);
    }


    /**
     * Removes a listener from the list that's notified each time a change to the item selection occurs.
     *
     * @param listener the <code>ItemListener</code> to remove
     */
    public void removeItemListener(ItemListener listener) {
        listenerList.remove(ItemListener.class, listener);
    }


    /**
     * Returns an array of all the <code>ItemListener</code>s added to this JList with addItemListener().
     *
     * @return all of the <code>ItemListener</code>s added or an empty array if no listeners have been added
     *
     * @see #addItemListener
     */
    public ItemListener[] getItemListeners() {
        return listenerList.getListeners(ItemListener.class);
    }

    /**
     * Notifies all listeners that have registered interest for notification on this event type.  The event instance is
     * lazily created using the <code>event</code> parameter.
     *
     * @param event the <code>ItemEvent</code> object
     * @see javax.swing.event.EventListenerList
     */
    protected void fireItemStateChanged(ItemEvent event) {
        // Guaranteed to return a non-null array
        Object[] listeners = listenerList.getListenerList();
        ItemEvent e = null;
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == ItemListener.class) {
                // Lazily create the event:
                if (e == null) {
                    e = new ItemEvent(CheckBoxListWithSelectable.this,
                            ItemEvent.ITEM_STATE_CHANGED,
                            event.getItem(),
                            event.getStateChange());
                }
                ((ItemListener) listeners[i + 1]).itemStateChanged(e);
            }
        }
    }

    /**
     * Gets the selected objects. This is different from {@link #getSelectedValues()} which is a JList's feature. The
     * List returned from this method contains the objects that is checked in the CheckBoxList.
     *
     * @return the selected objects.
     */
    public Object[] getSelectedObjects() {
        Vector<Object> elements = new Vector<Object>();
        for (int i = 0; i < getModel().getSize(); i++) {
            Object elementAt = getModel().getElementAt(i);
            if (elementAt instanceof Selectable) {
                Selectable selectable = (Selectable) elementAt;
                if (selectable.isSelected()) {
                    if (selectable instanceof DefaultSelectable) {
                        elements.add(((DefaultSelectable) selectable).getObject());
                    }
                    else {
                        elements.add(selectable);
                    }
                }
            }
        }
        return elements.toArray();
    }

    /**
     * Selects all objects in this list except those are disabled.
     */
    public void selectAll() {
        for (int i = 0; i < getModel().getSize(); i++) {
            Object elementAt = getModel().getElementAt(i);
            if (elementAt instanceof Selectable) {
                Selectable selectable = (Selectable) elementAt;
                if (selectable.isEnabled() && !selectable.isSelected()) {
                    selectable.setSelected(true);
                    fireItemStateChanged(new ItemEvent(this, ItemEvent.ITEM_STATE_CHANGED, selectable, ItemEvent.SELECTED));
                }
            }
        }
        repaint();
    }

    /**
     * Deselects all objects in this list except those are disabled.
     */
    public void selectNone() {
        for (int i = 0; i < getModel().getSize(); i++) {
            Object elementAt = getModel().getElementAt(i);
            if (elementAt instanceof Selectable) {
                Selectable selectable = (Selectable) elementAt;
                if (selectable.isEnabled() && selectable.isSelected()) {
                    selectable.setSelected(false);
                    fireItemStateChanged(new ItemEvent(this, ItemEvent.ITEM_STATE_CHANGED, selectable, ItemEvent.DESELECTED));
                }
            }
        }
        repaint();
    }

    @Override
    public void setListData(Vector<?> listData) {
        super.setListData(wrap(listData));
    }

    @Override
    public void setListData(Object[] listData) {
        super.setListData(wrap(listData));
    }

    @Override
    public int getNextMatch(String prefix, int startIndex, Position.Bias bias) {
        return -1;
    }

    /**
     * Gets the value of property checkBoxEnabled. If true, user can click on check boxes on each tree node to select
     * and deselect. If false, user can't click but you as developer can programmatically call API to select/deselect
     * it.
     *
     * @return the value of property checkBoxEnabled.
     */
    public boolean isCheckBoxEnabled() {
        return _checkBoxEnabled;
    }

    /**
     * Checks if check box is visible. There is no setter for it. The only way is to override this method to return true
     * or false.
     *
     * @param index the row index.
     * @return true or false. If false, there is not check box on the particular row index.
     */
    public boolean isCheckBoxVisible(int index) {
        return true;
    }

    /**
     * Sets the value of property checkBoxEnabled.
     *
     * @param checkBoxEnabled true to enable all the check boxes. False to disable all of them.
     */
    public void setCheckBoxEnabled(boolean checkBoxEnabled) {
        if (checkBoxEnabled != _checkBoxEnabled) {
            Boolean oldValue = _checkBoxEnabled ? Boolean.TRUE : Boolean.FALSE;
            Boolean newValue = checkBoxEnabled ? Boolean.TRUE : Boolean.FALSE;
            _checkBoxEnabled = checkBoxEnabled;
            firePropertyChange(PROPERTY_CHECKBOX_ENABLED, oldValue, newValue);
            repaint();
        }
    }
}
