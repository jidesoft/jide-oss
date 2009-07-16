package com.jidesoft.dialog;

import javax.swing.*;
import java.awt.*;

/**
 * AbstractDialogPage is an abstract base class extends AbstractPage. In addition to AbstractPage, this class has some
 * new properties so that it can be used in dialog. <BR> For example, it can support ButtonEvent which is used by
 * ButtonPanel. In addition, it has title, icon, description and parent attribute.
 */
public abstract class AbstractDialogPage extends AbstractPage {

    protected transient ButtonEvent _buttonEvent = null;

    protected String _title;
    protected String _description;
    protected Icon _icon;
    protected boolean _pageEnabled = true;
    protected AbstractDialogPage _parentPage;

    public static final String TITLE_PROPERTY = "title";
    public static final String DESCRIPTION_PROPERTY = "description";
    public static final String ICON_PROPERTY = "icon";
    public static final String PROPERTY_PAGE_ENABLED = "enabled";

    private Component _defaultFocusComponent;

    /**
     * Creates an AbstractDialogPage.
     */
    protected AbstractDialogPage() {
    }

    /**
     * Creates an AbstractDialogPage with title.
     *
     * @param title the title of the page
     */
    public AbstractDialogPage(String title) {
        _title = title;
    }

    /**
     * Creates an AbstractDialogPage with title and icon.
     *
     * @param title       the title of the page
     * @param description the description for the page
     */
    public AbstractDialogPage(String title, String description) {
        _title = title;
        _description = description;
    }

    /**
     * Creates an AbstractDialogPage with title and icon.
     *
     * @param title the title of the page
     * @param icon  the icon of the page
     */
    public AbstractDialogPage(String title, Icon icon) {
        _title = title;
        _icon = icon;
    }

    /**
     * Creates an AbstractDialogPage with title, icon and description.
     *
     * @param title       the title of the page
     * @param icon        the icon of the page
     * @param description the description for the page
     */
    public AbstractDialogPage(String title, String description, Icon icon) {
        _title = title;
        _icon = icon;
        _description = description;
    }

    /**
     * /** Creates an AbstractDialogPage with title, icon, description and its parent.
     *
     * @param title       the title of the page
     * @param icon        the icon of the page
     * @param description the description for the page
     * @param parentPage  the parent of the page
     */
    public AbstractDialogPage(String title, String description, Icon icon, AbstractDialogPage parentPage) {
        _title = title;
        _icon = icon;
        _description = description;
        _parentPage = parentPage;
    }

    /**
     * Adds a <code>ButtonListener</code> to the page.
     *
     * @param l the <code>ButtonListener</code> to be added
     */
    public void addButtonListener(ButtonListener l) {
        listenerList.add(ButtonListener.class, l);
    }

    /**
     * Removes a <code>ButtonListener</code> from the page.
     *
     * @param l the <code>ButtonListener</code> to be removed
     */
    public void removeButtonListener(ButtonListener l) {
        listenerList.remove(ButtonListener.class, l);
    }

    /**
     * Returns an array of all the <code>ButtonListener</code>s added to this <code>Page</code> with
     * <code>ButtonListener</code>.
     *
     * @return all of the <code>ButtonListener</code>s added, or an empty array if no listeners have been added
     *
     * @since 1.4
     */
    public ButtonListener[] getButtonListeners() {
        return listenerList.getListeners(
                ButtonListener.class);
    }

    /**
     * Fire button event with id. The only event that doesn't take a button name as parameter is the {@link
     * ButtonEvent#CLEAR_DEFAULT_BUTTON} event.
     *
     * @param id
     */
    public void fireButtonEvent(int id) {
        fireButtonEvent(id, null, null);
    }

    /**
     * Fire button event with id and button name.
     *
     * @param id
     * @param buttonName
     */
    public void fireButtonEvent(int id, String buttonName) {
        fireButtonEvent(id, buttonName, null);
    }

    /**
     * Fire button event with id, button name and user object if needed.
     *
     * @param id
     * @param buttonName
     * @param userObject
     */
    public void fireButtonEvent(int id, String buttonName, String userObject) {
        Object[] listeners = listenerList.getListenerList();
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == ButtonListener.class) {
                if (_buttonEvent == null) {
                    _buttonEvent = new ButtonEvent(this, id, buttonName, userObject);
                }
                else {
                    _buttonEvent.setID(id);
                    _buttonEvent.setButtonName(buttonName);
                    _buttonEvent.setUserObject(userObject);
                }
                ((ButtonListener) listeners[i + 1]).buttonEventFired(_buttonEvent);
            }
        }
    }

    /**
     * Gets the title of the page.
     *
     * @return the title
     */
    public String getTitle() {
        return _title;
    }

    /**
     * Sets the title of the page.
     *
     * @param title the new title
     */
    public void setTitle(String title) {
        String old = _title;
        _title = title;
        firePropertyChange(TITLE_PROPERTY, old, _title);
    }

    /**
     * Gets the icon of the page.
     *
     * @return the icon of the page.
     */
    public Icon getIcon() {
        return _icon;
    }

    /**
     * Sets the icon of the page.
     *
     * @param icon the new icon
     */
    public void setIcon(Icon icon) {
        Icon old = _icon;
        _icon = icon;
        firePropertyChange(ICON_PROPERTY, old, _icon);
    }


    /**
     * Checks if the page is enabled.
     *
     * @return true if the page is enabled. Otherwise false.
     */
    public boolean isPageEnabled() {
        return _pageEnabled;
    }

    /**
     * Sets page enabled or disabled. The only place this flag is used right now is in MultiplePageDialog ICON_STYLE and
     * TAB_STYLE. Disabled page will have a disabled icon or tab as indicator.
     *
     * @param pageEnabled
     */
    public void setPageEnabled(boolean pageEnabled) {
        if (_pageEnabled != pageEnabled) {
            Boolean oldValue = _pageEnabled ? Boolean.TRUE : Boolean.FALSE;
            Boolean newValue = pageEnabled ? Boolean.TRUE : Boolean.FALSE;
            _pageEnabled = pageEnabled;
            firePropertyChange(PROPERTY_PAGE_ENABLED, oldValue, newValue);
        }
    }

    /**
     * Gets the description of the page.
     *
     * @return the description
     */
    public String getDescription() {
        return _description;
    }

    /**
     * Sets the description of the page.
     *
     * @param description the new description
     */
    public void setDescription(String description) {
        String old = _description;
        _description = description;
        firePropertyChange(DESCRIPTION_PROPERTY, old, _description);
    }

    /**
     * Gets the parent page.
     *
     * @return the parent page
     */
    public AbstractDialogPage getParentPage() {
        return _parentPage;
    }

    /**
     * Sets the parent page.
     *
     * @param parentPage the parent page
     */
    public void setParentPage(AbstractDialogPage parentPage) {
        _parentPage = parentPage;
    }

    /**
     * Gets the full title. It is basically a concat of the titles of all its parent with "." in between.
     *
     * @return the full qualified title
     */
    public String getFullTitle() {
        StringBuffer buffer = new StringBuffer(getTitle());
        AbstractDialogPage page = this;
        while (page.getParentPage() != null) {
            AbstractDialogPage parent = page.getParentPage();
            buffer.insert(0, ".");
            buffer.insert(0, parent.getTitle());
            page = parent;
        }
        return new String(buffer);
    }

    /**
     * Gets the default focus component. The default focus component will gain focus when page is shown.
     *
     * @return the default focus component.
     */
    public Component getDefaultFocusComponent() {
        return _defaultFocusComponent;
    }

    /**
     * Sets the default focus component. The default focus component will gain focus when page is shown.
     *
     * @param defaultFocusComponent a component inside the page.
     */
    public void setDefaultFocusComponent(Component defaultFocusComponent) {
        _defaultFocusComponent = defaultFocusComponent;
    }

    /**
     * Focus the default focus component if not null.
     */
    public void focusDefaultFocusComponent() {
        final Component focusComponent = getDefaultFocusComponent();
        if (focusComponent != null) {
            Runnable runnable = new Runnable() {
                public void run() {
                    if (focusComponent != null) {
                        focusComponent.requestFocusInWindow();
                    }
                }
            };
            SwingUtilities.invokeLater(runnable);
        }
    }
}

