/*
 * @(#)JideTabbedPane.java	Oct 7, 2002
 *
 * Copyright 2002 JIDE Software Inc. All rights reserved.
 */
package com.jidesoft.swing;

import com.jidesoft.plaf.JideTabbedPaneUI;
import com.jidesoft.plaf.LookAndFeelFactory;
import com.jidesoft.plaf.UIDefaultsLookup;
import com.jidesoft.plaf.basic.BasicJideTabbedPaneUI;
import com.jidesoft.popup.JidePopup;
import com.jidesoft.utils.JideFocusTracker;
import com.jidesoft.utils.PortingUtils;
import com.jidesoft.utils.SystemInfo;

import javax.swing.*;
import javax.swing.plaf.TabbedPaneUI;
import javax.swing.plaf.UIResource;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <code>JideTabbedPane</code> is an enhanced version of <code>JTabbedPane</code>. Different from
 * <code>JTabbedPane</code>, it <ul> <li> has an option to hide tab area if there is only one component in the tabbed
 * pane. <li> has an option to resize tab width so that all tabs can be fitted in one row. <li> has an option to show a
 * close button along with scroll left and scroll right buttons in tab area. </ul> Except methods to set additional
 * options specified above, the usage of <code>JideTabbedPane</code> is the same as <code>JTabbedPane</code>.
 */
public class JideTabbedPane extends JTabbedPane {

    private boolean _hideOneTab = false;

    private boolean _showTabButtons = false;

    private boolean _showCloseButton = false;
    private boolean _showCloseButtonOnTab = false;
    private boolean _showCloseButtonOnMouseOver = false;
    private boolean _useDefaultShowCloseButtonOnTab = false;
    private boolean _showTabArea = true;
    private boolean _showTabContent = true;

    private boolean _showIconsOnTab = true;
    private boolean _useDefaultShowIconsOnTab = true;

    private boolean _rightClickSelect;
    private boolean _dragOverDisabled;

    private boolean _scrollSelectedTabOnWheel = false;

    private int _tabAlignment = SwingConstants.LEADING;

    /**
     * Bound property name for shrink tabs.
     */
    public static final String SHRINK_TAB_PROPERTY = "shrinkTab";

    /**
     * Bound property name for hide tab area if there is only one tab.
     */
    public static final String HIDE_IF_ONE_TAB_PROPERTY = "hideIfOneTab";

    /**
     * Bound property name for show tab button.
     */
    public static final String SHOW_TAB_BUTTONS_PROPERTY = "showTabButtons";

    /**
     * Bound property name for box style
     */
    public static final String BOX_STYLE_PROPERTY = "boxStyle";

    /**
     * Bound property name for show icons on tab
     */
    public static final String SHOW_ICONS_PROPERTY = "showIconsOnTab";

    /**
     * Bound property name for using default show icons on tab value from UIDefaults
     */
    public static final String USE_DEFAULT_SHOW_ICONS_PROPERTY = "useDefaultShowIconsOnTab";

    /**
     * Bound property name for if showing close button on tab
     */
    public static final String SHOW_CLOSE_BUTTON_ON_TAB_PROPERTY = "showCloseButtonOnTab";

    /**
     * Bound property name for if showing close button
     */
    public static final String SHOW_CLOSE_BUTTON_PROPERTY = "showCloseButton";

    /**
     * Bound property name for if the tab area is visible.
     */
    public static final String SHOW_TAB_AREA_PROPERTY = "showTabArea";

    /**
     * Bound property name for if the tab area is visible.
     */
    public static final String SHOW_TAB_CONTENT_PROPERTY = "showTabContent";

    /**
     * Bound property name for tab closable.
     */
    public static final String TAB_CLOSABLE_PROPERTY = "tabClosable";

    /**
     * Bound property name for using default show close button on tab value from UIDefaults
     */
    public static final String USE_DEFAULT_SHOW_CLOSE_BUTTON_ON_TAB_PROPERTY = "useDefaultShowCloseButtonOnTab";

    /**
     * Bound property name for if the active tab title is in bold
     */
    public static final String BOLDACTIVETAB_PROPERTY = "boldActiveTab";

    /**
     * Bound property name for gripper.
     */
    public static final String GRIPPER_PROPERTY = "gripper";

    public static final String PROPERTY_TAB_SHAPE = "tabShape";
    public static final String PROPERTY_COLOR_THEME = "colorTheme";
    public static final String PROPERTY_TAB_RESIZE_MODE = "tabResizeMode";
    public static final String PROPERTY_TAB_LEADING_COMPONENT = "tabLeadingComponent";
    public static final String PROPERTY_TAB_TRAILING_COMPONENT = "tabTrailingComponent";
    public static final String PROPERTY_TAB_COLOR_PROVIDER = "tabColorProvider";
    public static final String PROPERTY_CONTENT_BORDER_INSETS = "contentBorderInsets";
    public static final String PROPERTY_TAB_AREA_INSETS = "tabAreaInsets";
    public static final String PROPERTY_TAB_INSETS = "tabInsets";
    public static final String PROPERTY_DRAG_OVER_DISABLED = "dragOverDisabled";
    public static final String SCROLL_TAB_ON_WHEEL_PROPERTY = "scrollTabOnWheel";
    public static final String PROPERTY_SELECTED_INDEX = "selectedIndex";
    public static final String PROPERTY_SHOW_CLOSE_BUTTON_ON_MOUSE_OVER = "showCloseButtonOnMouseOver";

    public static final int BUTTON_CLOSE = 0;
    public static final int BUTTON_EAST = 1;
    public static final int BUTTON_WEST = 2;
    public static final int BUTTON_NORTH = 3;
    public static final int BUTTON_SOUTH = 4;
    public static final int BUTTON_LIST = 5;

    /**
     * @see #getUIClassID
     * @see #readObject
     */
    private static final String uiClassID = "JideTabbedPaneUI";

    /**
     * If the gripper should be shown. Gripper is something on divider to indicate it can be dragged.
     */
    private boolean _showGripper = false;

    /**
     * A converter to shorten
     */
    private StringConverter _stringConverter;

    private boolean _boldActiveTab = false;

    /**
     * The Set for the tab closable. If there is an entry in the Set, it means the tab is NOT closable.
     */
    private Set<Object> _closableSet = new HashSet<Object>();

    private Hashtable<Component, Object> _pageLastFocusTrackers = new Hashtable<Component, Object>();

    private Font _selectedTabFont;

    /**
     * A tab resize mode. The default resize mode means it will use the resize mode of {@link
     * #getDefaultTabResizeMode()} which is defined in UIDefault "JideTabbedPane.defaultResizeMode". You can change this
     * in UIDefault. It will affect the resize mode of all <code>JideTabbedPane</code>s.
     */
    public static final int RESIZE_MODE_DEFAULT = 0;

    /**
     * A tab resize mode. The none resize mode means the tab will not resize when tabbed pane width changes.
     */
    public static final int RESIZE_MODE_NONE = 1;

    /**
     * A tab resize mode. The fit resize mode means the tabs will shrink if the tabbed pane width shrinks so there is no
     * way to display the full contents of the tabs.
     */
    public static final int RESIZE_MODE_FIT = 2;

    /**
     * A tab resize mode. All tabs will be at a fixed width. The fixed width is defined as UIDefault
     * "JideTabbedPane.fixedStyleRectSize" which is an integer.
     */
    public static final int RESIZE_MODE_FIXED = 3;

    /**
     * A tab resize mode. In this mode, the select tab will have full tab width. Non-selected tab will only display the
     * icon. The actual width of non-selected tab is determined by UIDefault "JideTabbedPane.compressedStyleNoIconRectSize"
     * which is an integer.
     */
    public static final int RESIZE_MODE_COMPRESSED = 4;

    private int _tabResizeMode = RESIZE_MODE_DEFAULT;

    /**
     * color style
     */
    public static final int COLOR_THEME_DEFAULT = 0;
    public static final int COLOR_THEME_WIN2K = 1;
    public static final int COLOR_THEME_OFFICE2003 = 2;
    public static final int COLOR_THEME_VSNET = 3;
    public static final int COLOR_THEME_WINXP = 4;

    // color style
    private int _colorTheme = COLOR_THEME_DEFAULT;

    // tab shape
    public static final int SHAPE_DEFAULT = 0;
    public static final int SHAPE_WINDOWS = 1;
    public static final int SHAPE_VSNET = 2;
    public static final int SHAPE_BOX = 3;
    public static final int SHAPE_OFFICE2003 = 4;
    public static final int SHAPE_FLAT = 5;
    public static final int SHAPE_ECLIPSE = 6;
    public static final int SHAPE_ECLIPSE3X = 7;
    public static final int SHAPE_EXCEL = 8;
    public static final int SHAPE_ROUNDED_VSNET = 9;
    public static final int SHAPE_ROUNDED_FLAT = 10;
    public static final int SHAPE_WINDOWS_SELECTED = 11;

    private int _tabShape = SHAPE_DEFAULT;

    private Component _tabLeadingComponent = null;
    private Component _tabTrailingComponent = null;
    private boolean _hideTrailingWhileNoButtons = false;

    // show close button on active tab only
    private boolean _showCloseButtonOnSelectedTab = false;

    private ListCellRenderer _tabListCellRenderer;

    private Insets _contentBorderInsets;
    private Insets _tabAreaInsets;
    private Insets _tabInsets;

    private static final Logger LOGGER_EVENT = Logger.getLogger(TabEditingEvent.class.getName());

    private boolean _closeTabOnMouseMiddleButton = false;
    private boolean _layoutTrailingComponentBeforeButtons = false;
    private JidePopup _tabListPopup;

    /**
     * Creates an empty <code>TabbedPane</code> with a default tab placement of <code>JTabbedPane.TOP</code>.
     *
     * @see #addTab
     */
    public JideTabbedPane() {
        this(JideTabbedPane.TOP, JideTabbedPane.SCROLL_TAB_LAYOUT);
    }

    /**
     * Creates an empty <code>TabbedPane</code> with the specified tab placement of either:
     * <code>JTabbedPane.TOP</code>, <code>JTabbedPane.BOTTOM</code>, <code>JTabbedPane.LEFT</code>, or
     * <code>JTabbedPane.RIGHT</code>.
     *
     * @param tabPlacement the placement for the tabs relative to the content
     * @see #addTab
     */
    public JideTabbedPane(int tabPlacement) {
        this(tabPlacement, JideTabbedPane.SCROLL_TAB_LAYOUT);
    }

    /**
     * Creates an empty <code>JideTabbedPane</code> with the specified tab placement and tab layout policy.  Tab
     * placement may be either: <code>JTabbedPane.TOP</code> or <code>JTabbedPane.BOTTOM</code> Tab layout policy should
     * always be <code>JTabbedPane.SCROLL_TAB_LAYOUT</code>. <code>JTabbedPane</code> also supports
     * <code>JTabbedPane.WRAP_TAB_LAYOUT</code>. However the style of tabs in <code>JideTabbedPane</code> doesn't match
     * with <code>JTabbedPane.WRAP_TAB_LAYOUT</code> very well, so we decided not to support it.
     *
     * @param tabPlacement    the placement for the tabs relative to the content
     * @param tabLayoutPolicy the policy for laying out tabs when all tabs will not fit on one run
     * @throws IllegalArgumentException if tab placement or tab layout policy are not one of the above supported values
     * @see #addTab
     */
    public JideTabbedPane(int tabPlacement, int tabLayoutPolicy) {
        super(tabPlacement, tabLayoutPolicy);
//        if(tabLayoutPolicy == WRAP_TAB_LAYOUT)
//            tabLayoutPolicy = SCROLL_TAB_LAYOUT;

        setModel(new IgnoreableSingleSelectionModel());
    }

    /**
     * Returns the UI object which implements the L&F for this component.
     *
     * @return a <code>TabbedPaneUI</code> object
     * @see #setUI
     */
    @Override
    public TabbedPaneUI getUI() {
        return (TabbedPaneUI) ui;
    }

    /**
     * Sets the UI object which implements the L&F for this component.
     *
     * @param ui the new UI object
     * @see UIDefaults#getUI
     */
    @Override
    public void setUI(TabbedPaneUI ui) {
        super.setUI(ui);
    }

    /**
     * Resets the UI property to a value from the current look and feel.
     *
     * @see JComponent#updateUI
     */
    @Override
    public void updateUI() {
        if (UIDefaultsLookup.get(uiClassID) == null) {
            LookAndFeelFactory.installJideExtension();
        }
        setUI((TabbedPaneUI) UIManager.getUI(JideTabbedPane.this));
    }


    /**
     * Returns the name of the UI class that implements the L&F for this component.
     *
     * @return the string "TabbedPaneUI"
     * @see JComponent#getUIClassID
     * @see UIDefaults#getUI
     */
    @Override
    public String getUIClassID() {
        return uiClassID;
    }

    /**
     * Checks if tab area will be hidden if there is only one tab. <br> If the showTabButtons option is true,
     * isHideOneTab will always return false so that there is a place to place those tab buttons.
     *
     * @return true if tab areas will be hidden if there is only one tab; false otherwise.
     */
    public boolean isHideOneTab() {
        return !isShowTabButtons() && _hideOneTab;
    }

    /**
     * Sets the value if tab area will be hidden if there is only one tab. PropertyChangeEvent of
     * HIDE_IF_ONE_TAB_PROPERTY will be fired. <br> If the showTabButtons option is true, no matter what option you pass
     * to setHideOneTab, isHideOneTab will always return false.
     *
     * @param hideOne true to hide tab areas if there is only one tab; false otherwise.
     */
    public void setHideOneTab(boolean hideOne) {
        boolean oldValue = _hideOneTab;

        if (oldValue != hideOne) {
            _hideOneTab = hideOne;
            firePropertyChange(HIDE_IF_ONE_TAB_PROPERTY, oldValue, _hideOneTab);
        }
    }

    /**
     * Checks if tab area is shown.
     *
     * @return true if tab area is visible; false otherwise.
     */
    public boolean isTabShown() {
        return isShowTabArea() && !(isHideOneTab() && getTabCount() <= 1);
    }

    /**
     * Checks if tab buttons are always visible. Tab buttons are scroll left button, scroll right button and close
     * button which appear to the right of tabs in tab area. <br> If the showTabButtons is set to true, isHideOneTab
     * will always return false so that there is a place to place those tab buttons.
     *
     * @return true if tab buttons are always visible; false otherwise.
     */
    public boolean isShowTabButtons() {
        return _showTabButtons;
    }

    /**
     * Sets the value if tab buttons are always visible. PropertyChangeEvent of SHOW_TAB_BUTTONS_PROPERTY will be
     * fired.
     *
     * @param showButtons true to always show tab buttons; false otherwise.
     */
    public void setShowTabButtons(boolean showButtons) {
        boolean oldValue = _showTabButtons;

        if (oldValue != showButtons) {
            _showTabButtons = showButtons;
            firePropertyChange(SHOW_TAB_BUTTONS_PROPERTY, oldValue, _showTabButtons);
        }
    }

    private Action _closeAction;

    /**
     * Sets default close action for close button.
     *
     * @param action the close action.
     */
    public void setCloseAction(Action action) {
        Action old = _closeAction;
        if (old != action) {
            _closeAction = action;
            firePropertyChange("closeTabAction", old, _closeAction);
        }
    }

    /**
     * Gets close action.
     *
     * @return close action
     */
    public Action getCloseAction() {
        return _closeAction;
    }

    public void setAutoFocusOnTabHideClose(boolean autoFocusonTabHideClose) {
        _autoFocusonTabHideClose = autoFocusonTabHideClose;
    }

    public boolean isAutoFocusOnTabHideClose() {
        return _autoFocusonTabHideClose;
    }

    boolean _autoFocusonTabHideClose = true;

    /**
     * Resets close action to default. Default action is to remove currently selected tab.
     */
    public void resetDefaultCloseAction() {
        setCloseAction(null);
    }

    private boolean _suppressStateChangedEvents = false;

    public void setSuppressStateChangedEvents(boolean suppress) {
        _suppressStateChangedEvents = suppress;
    }

    public boolean isSuppressStateChangedEvents() {
        return _suppressStateChangedEvents;
    }

    @Override
    protected void fireStateChanged() {
        if (_suppressStateChangedEvents)
            return;

        if (!isAutoFocusOnTabHideClose())
            clearVisComp();
        super.fireStateChanged();

    }


    // setSelectedIndex will be called during moving tab. So we use this flag to suppress it.
    private boolean _suppressSetSelectedIndex = false;

    public boolean isSuppressSetSelectedIndex() {
        return _suppressSetSelectedIndex;
    }

    public void setSuppressSetSelectedIndex(boolean suppressSetSelectedIndex) {
        _suppressSetSelectedIndex = suppressSetSelectedIndex;
    }

    @Override
    public void setSelectedIndex(int index) {
        if (_suppressSetSelectedIndex)
            return;

        boolean old = isFocusCycleRoot();
        setFocusCycleRoot(true);
        try {
            int oldIndex = getSelectedIndex();
            if (oldIndex != index) {
                super.setSelectedIndex(index);
                firePropertyChange(PROPERTY_SELECTED_INDEX, oldIndex, index);
            }
        }
        finally {
            setFocusCycleRoot(old);
        }
    }

    /*
    * This is called by the popup menu in the scrollrect area
    */

    public void popupSelectedIndex(int index) {
        setSelectedIndex(index);
    }

    public void setComponentAt(int index, Component c) {
        Component oldComponent = getComponentAt(index);
        if (oldComponent != null) {
            // JTabbedPane allows a null c, but doesn't really support it.
            PageLastFocusTracker tracker = (PageLastFocusTracker) _pageLastFocusTrackers.get(oldComponent);
            _pageLastFocusTrackers.remove(oldComponent);
            if (tracker != null) {
                tracker.setHighestComponent(null); // Clear its listeners
            }
        }

        boolean contains = false;
        if (_closableSet.contains(oldComponent)) {
            contains = true;
        }

        super.setComponentAt(index, c);

        if (contains) {
            _closableSet.add(c);
        }

        if (!isAutoFocusOnTabHideClose())
            clearVisComp();
    }

    private boolean _autoRequestFocus = true;

    /**
     * Checks if the UI should automatically request focus on selected component when doing the layout. This method is
     * only used internally when the tab is being moved.
     *
     * @return true or false. Default is true.
     */
    public boolean isAutoRequestFocus() {
        return _autoRequestFocus;
    }

    public void setAutoRequestFocus(boolean autoRequestFocus) {
        _autoRequestFocus = autoRequestFocus;
    }

    /**
     * Moves selected tab from current position to the position specified in tabIndex.
     *
     * @param tabIndex new index
     */
    public void moveSelectedTabTo(int tabIndex) {
        int selectedIndex = getSelectedIndex();
        if (selectedIndex == tabIndex) { // do nothing
            return;
        }
        if (tabIndex == -1 || selectedIndex == -1)
            return;

        if (isTabEditing())
            stopTabEditing();

        Component selectedComponent = getComponentAt(selectedIndex);

        boolean old = isAutoRequestFocus();

        boolean shouldChangeFocus = false;
        // we will not let UI to auto request focus so we will have to do it here.
        // if the selected component has focus, we will request it after the tab is moved.
        if (selectedComponent != null) {
            if (JideSwingUtilities.isAncestorOfFocusOwner(selectedComponent) && isAutoFocusOnTabHideClose()) {
                shouldChangeFocus = true;
            }
        }

        try {
            _suppressStateChangedEvents = true;
            setAutoRequestFocus(false);

            if (selectedIndex - tabIndex == 1 || tabIndex - selectedIndex == 1) {
                Component c = getComponentAt(tabIndex);
                String title = getTitleAt(tabIndex);
                String tooltip = getToolTipTextAt(tabIndex);
                Icon icon = getIconAt(tabIndex);
                _suppressSetSelectedIndex = true;
                boolean closable = true;
                if (_closableSet != null) {
                    closable = isTabClosableAt(tabIndex);
                }
                try {
                    if (tabIndex > selectedIndex)
                        insertTab(title, icon, c, tooltip, selectedIndex);
                    else {
                        insertTab(title, icon, c, tooltip, selectedIndex + 1);
                    }
                    if (!closable) {
                        _closableSet.add(c);
                    }
                }
                finally {
                    _suppressSetSelectedIndex = false;
                }
            }
            else {
                Component c = getComponentAt(selectedIndex);
                String title = getTitleAt(selectedIndex);
                String tooltip = getToolTipTextAt(selectedIndex);
                Icon icon = getIconAt(selectedIndex);
                _suppressSetSelectedIndex = true;
                boolean closable = true;
                if (_closableSet != null) {
                    closable = isTabClosableAt(tabIndex);
                }
                try {
                    if (tabIndex > selectedIndex)
                        insertTab(title, icon, c, tooltip, tabIndex + 1);
                    else {
                        insertTab(title, icon, c, tooltip, tabIndex);
                    }
                    if (!closable) {
                        _closableSet.add(c);
                    }
                }
                finally {
                    _suppressSetSelectedIndex = false;
                }
            }

            if (!SystemInfo.isJdk15Above()) {
                // a workaround for Swing bug
                if (tabIndex == getTabCount() - 2) {
                    setSelectedIndex(getTabCount() - 1);
                }
            }

            setAutoRequestFocus(old);
            setSelectedIndex(tabIndex);
        }
        finally {
            _suppressStateChangedEvents = false;

            if (shouldChangeFocus) {
                if (!requestFocusForVisibleComponent()) {
//                    System.out.println("---tabpane.requestfocus41");
                    requestFocusInWindow();
                }
            }
        }
    }

    // mtf - review if this is still needed

    public boolean requestFocusForVisibleComponent() {
        return false;
/*
        if (true)
            return false;
//        System.out.println("---JideTabbedPane.requestFocusForVisibleComponent()");
        Component visibleComponent = getSelectedComponent();
        Component lastFocused = getLastFocusedComponent(visibleComponent);
        if (lastFocused != null && lastFocused.requestFocusInWindow()) {
            return true;
        }
        else {
            // Focus the next component in the focus cycle after the tab.
            Container nearestRoot = (isFocusCycleRoot()) ?
                    this : getFocusCycleRootAncestor();
            if (nearestRoot == null) {
                return false;
            }
            Component comp = nearestRoot.getFocusTraversalPolicy().getComponentAfter(nearestRoot, this);
            return comp != null && comp.requestFocusInWindow() || JideSwingUtilities.compositeRequestFocus(visibleComponent);
        }
*/
    }

    /**
     * Get the flag that if the trailing component should be hidden while no buttons are visible.
     * <p/>
     * Be default, the flag is false. If you want to connect visibility of those two components, please set it to true.
     *
     * @return true if the trailing component would be hidden while no buttons are visible. Otherwise false.
     * @see #isShowTabArea()
     * @see #isShowTabButtons()
     * @see #setHideTrailingWhileNoButtons(boolean)
     */
    public boolean isHideTrailingWhileNoButtons() {
        return _hideTrailingWhileNoButtons;
    }

    /**
     * Set the flag that if the trailing component should be hidden while no buttons are visible.
     *
     * @param hideTrailingWhileNoButtons the flag
     * @see #isHideTrailingWhileNoButtons()
     */
    public void setHideTrailingWhileNoButtons(boolean hideTrailingWhileNoButtons) {
        _hideTrailingWhileNoButtons = hideTrailingWhileNoButtons;
    }

    /**
     * Gets the flag indicating if the trailing component should be layout before the default buttons.
     *
     * @return true if the trailing component should be layout to the left/up. Otherwise false.
     * @see #setLayoutTrailingComponentBeforeButtons(boolean)
     */
    public boolean isLayoutTrailingComponentBeforeButtons() {
        return _layoutTrailingComponentBeforeButtons;
    }

    /**
     * Sets the flag indicating if the trailing component should be layout before the default buttons.
     * <p/>
     * The default value is false. If you want your trailing component preceding to the default buttons, please set this
     * flag to true.
     *
     * @param layoutTrailingComponentBeforeButtons
     *         the flag
     */
    public void setLayoutTrailingComponentBeforeButtons(boolean layoutTrailingComponentBeforeButtons) {
        _layoutTrailingComponentBeforeButtons = layoutTrailingComponentBeforeButtons;
    }

    public boolean isTabEditingAllowed(int tabIndex) {
        return true;
    }

    /*
      * Used to allow the tabswitching to be delayed until after drag/reorder opperations are done.
      */

    protected class IgnoreableSingleSelectionModel extends DefaultSingleSelectionModel {
        private static final long serialVersionUID = -4321082126384337792L;

        @Override
        protected void fireStateChanged() {
            if (!_suppressStateChangedEvents) {
                super.fireStateChanged();
            }
        }
    }

    @SuppressWarnings({"UnusedDeclaration"})
    public void processMouseSelection(int tabIndex, MouseEvent e) {

    }

    /**
     * Gets tab height.
     *
     * @return height of tab
     */
    public int getTabHeight() {
        if (getTabPlacement() == TOP || getTabPlacement() == BOTTOM) {
            return ((JideTabbedPaneUI) getUI()).getTabPanel().getHeight();
        }
        else {
            return ((JideTabbedPaneUI) getUI()).getTabPanel().getWidth();
        }
    }

    /**
     * Returns true if you want right click on unselected tab will select that tab.
     *
     * @return true if right click on unselected tab will select that tab
     */
    public boolean isRightClickSelect() {
        return _rightClickSelect;
    }

    /**
     * Sets if you want right click on unselected tab will select that tab.
     *
     * @param rightClickSelect true if right click on unselected tab will select that tab
     */
    public void setRightClickSelect(boolean rightClickSelect) {
        _rightClickSelect = rightClickSelect;
    }

    public int getTabAtLocation(int x, int y) {
        int tabCount = getTabCount();
        int i = getUI().tabForCoordinate(this, x, y);
        return i < 0 || i >= tabCount ? -1 : i;
    }


    /**
     * If the grip is visible.
     *
     * @return true if grip is visible
     */
    public boolean isShowGripper() {
        return _showGripper;
    }

    /**
     * Sets the visibility of grip.
     *
     * @param showGripper true to show grip
     */
    public void setShowGripper(boolean showGripper) {
        boolean oldShowGripper = _showGripper;
        if (oldShowGripper != showGripper) {
            _showGripper = showGripper;
            firePropertyChange(GRIPPER_PROPERTY, oldShowGripper, _showGripper);
        }
    }

    /**
     * Checks if the icon will be shown on tab.
     *
     * @return true if the icon will be shown on tab.
     */
    public boolean isShowIconsOnTab() {
        return _showIconsOnTab;
    }

    /**
     * Sets to true if the icon will be shown on tab. The value set to this method will be used only when
     * isUseDefaultShowIconsOnTab() returns false.
     *
     * @param showIconsOnTab true or false.
     */
    public void setShowIconsOnTab(boolean showIconsOnTab) {
        boolean oldShowIconsOnTab = _showIconsOnTab;
        if (oldShowIconsOnTab != showIconsOnTab) {
            _showIconsOnTab = showIconsOnTab;
            firePropertyChange(SHOW_ICONS_PROPERTY, oldShowIconsOnTab, _showIconsOnTab);
        }
    }

    /**
     * If the return is true, the value set to setShowIconsOnTab() will be ignored.
     *
     * @return if use default value from UIDefaults in L&F.
     */
    public boolean isUseDefaultShowIconsOnTab() {
        return _useDefaultShowIconsOnTab;
    }

    /**
     * Set if use the default value from UIDefaults.
     *
     * @param useDefaultShowIconsOnTab true or false.
     */
    public void setUseDefaultShowIconsOnTab(boolean useDefaultShowIconsOnTab) {
        boolean oldUseDefaultShowIconsOnTab = _useDefaultShowIconsOnTab;
        if (oldUseDefaultShowIconsOnTab != useDefaultShowIconsOnTab) {
            _useDefaultShowIconsOnTab = useDefaultShowIconsOnTab;
            firePropertyChange(USE_DEFAULT_SHOW_ICONS_PROPERTY, oldUseDefaultShowIconsOnTab, _useDefaultShowIconsOnTab);
        }
    }

    /**
     * Checks if the close button will be shown on tab.
     *
     * @return true if close button will be shown on tab.
     */
    public boolean isShowCloseButtonOnTab() {
        return _showCloseButtonOnTab;
    }

    /**
     * Sets to true if the close button will be shown on tab. If you ever call this method, we will automatically call
     * setUseDefaultShowCloseButtonOnTab(false). It will also automatically call setShowCloseButton(true) if the
     * showCloseButtonOnTab parameter is true.
     *
     * @param showCloseButtonOnTab true or false.
     */
    public void setShowCloseButtonOnTab(boolean showCloseButtonOnTab) {
        boolean oldShowCloseButtonOnTab = _showCloseButtonOnTab;
        if (oldShowCloseButtonOnTab != showCloseButtonOnTab) {
            _showCloseButtonOnTab = showCloseButtonOnTab;
            firePropertyChange(SHOW_CLOSE_BUTTON_ON_TAB_PROPERTY, oldShowCloseButtonOnTab, _showCloseButtonOnTab);
            if (_showCloseButtonOnTab) {
                setShowCloseButton(true);
            }
        }
        setUseDefaultShowCloseButtonOnTab(false);
    }

    /**
     * If the return is true, the value set to setShowCloseButtonOnTab() will be ignored.
     *
     * @return if use default value from UIDefaults in L&F.
     */
    public boolean isUseDefaultShowCloseButtonOnTab() {
        return _useDefaultShowCloseButtonOnTab;
    }

    /**
     * Set if use the default value from UIDefaults.
     *
     * @param useDefaultShowCloseButtonOnTab true or false.
     */
    public void setUseDefaultShowCloseButtonOnTab(boolean useDefaultShowCloseButtonOnTab) {
        boolean oldUseDefaultShowCloseButtonOnTab = _useDefaultShowCloseButtonOnTab;
        if (oldUseDefaultShowCloseButtonOnTab != useDefaultShowCloseButtonOnTab) {
            _useDefaultShowCloseButtonOnTab = useDefaultShowCloseButtonOnTab;
            firePropertyChange(USE_DEFAULT_SHOW_CLOSE_BUTTON_ON_TAB_PROPERTY, oldUseDefaultShowCloseButtonOnTab, _useDefaultShowCloseButtonOnTab);
        }
    }

    // below is the code to allow editing the tab title directly
    transient protected boolean _tabEditingAllowed = false;

    /**
     * Sets the value if the tab editing is allowed. Tab editing allows user to edit the tab title directly by double
     * clicking on the tab.
     *
     * @param allowed true or false.
     */
    public void setTabEditingAllowed(boolean allowed) {
        _tabEditingAllowed = allowed;
    }


    /**
     * Checks if the tab editing is allowed.
     *
     * @return true if tab editing is allowed. Otherwise false.
     */
    public boolean isTabEditingAllowed() {
        return _tabEditingAllowed && getTabLayoutPolicy() == SCROLL_TAB_LAYOUT;
    }

    transient protected TabEditingValidator _tabEditValidator;

    public void setTabEditingValidator(TabEditingValidator tabEditValidator) {
        _tabEditValidator = tabEditValidator;
    }

    public TabEditingValidator getTabEditingValidator() {
        return _tabEditValidator;
    }

    /**
     * If close button is visible.
     *
     * @return true if the close button is visible.
     */
    public boolean isShowCloseButton() {
        return _showCloseButton;
    }

    /**
     * Sets if the close button is visible. Close button can be either side by side with scroll buttons, or on each tab.
     * If you call setShowCloseButton(false), it will hide close buttons for both cases.
     *
     * @param showCloseButton true or false.
     */
    public void setShowCloseButton(boolean showCloseButton) {
        boolean oldShowCloseButton = _showCloseButton;
        if (oldShowCloseButton != showCloseButton) {
            _showCloseButton = showCloseButton;
            firePropertyChange(SHOW_CLOSE_BUTTON_PROPERTY, oldShowCloseButton, _showCloseButton);
        }
    }

    /**
     * If the tab area is visible.
     *
     * @return true if the tab area is visible.
     */
    public boolean isShowTabArea() {
        return _showTabArea;
    }

    /**
     * Sets if the tab area is visible. If not visible, you can programatically call setSelectedIndex to change ta. User
     * will not be able to do it by clicking on tabs since they are not visible.
     *
     * @param showTabArea true or false.
     */
    public void setShowTabArea(boolean showTabArea) {
        boolean oldShowTabArea = _showTabArea;
        if (oldShowTabArea != showTabArea) {
            _showTabArea = showTabArea;
            firePropertyChange(SHOW_TAB_AREA_PROPERTY, oldShowTabArea, _showTabArea);
        }
    }

    /**
     * If the tab content is visible.
     *
     * @return true if the tab content is visible.
     */
    public boolean isShowTabContent() {
        return _showTabContent;
    }

    /**
     * Sets if the tab content is visible.
     *
     * @param showTabContent true or false.
     */
    public void setShowTabContent(boolean showTabContent) {
        boolean oldShowTabContent = _showTabContent;
        if (oldShowTabContent != showTabContent) {
            _showTabContent = showTabContent;
            firePropertyChange(SHOW_TAB_CONTENT_PROPERTY, oldShowTabContent, _showTabContent);
        }
    }

    /**
     * Gets the string converter that converts the tab title to the display title.
     *
     * @return the converter that converts the tab title to the display title.
     */
    public StringConverter getStringConverter() {
        return _stringConverter;
    }

    /**
     * Sets the string converter.
     *
     * @param stringConverter the StringConverter.
     * @see #getStringConverter()
     */
    public void setStringConverter(StringConverter stringConverter) {
        _stringConverter = stringConverter;
    }

    /**
     * Gets the display title. Display title is result of using string converter that converts from the title to a
     * display title. There is no setter for display title. You control the value by using a different string
     * converter.
     *
     * @param index the index to display
     * @return the display title.
     */
    public String getDisplayTitleAt(int index) {
        if (_stringConverter != null) {
            return _stringConverter.convert(super.getTitleAt(index));
        }
        else {
            return super.getTitleAt(index);
        }
    }

    /**
     * If the active tab is in bold.
     *
     * @return if the active tab is in bold.
     */
    public boolean isBoldActiveTab() {
        return _boldActiveTab;
    }

    /**
     * Sets if the active tab is in bold.
     *
     * @param boldActiveTab the flag
     */
    public void setBoldActiveTab(boolean boldActiveTab) {
        boolean old = _boldActiveTab;
        if (old != boldActiveTab) {
            _boldActiveTab = boldActiveTab;
            firePropertyChange(BOLDACTIVETAB_PROPERTY, old, _boldActiveTab);
        }
    }

    @Override
    public void removeTabAt(int index) {
        int tabCount = getTabCount();
        int selected = getSelectedIndex();
        boolean enforce = false;
        if (selected == index && selected < tabCount - 1) {
            // since JDK5 fixed this, we only need to enforce the event when it is not JDK5 and above.
            // http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6368047
            enforce = !SystemInfo.isJdk15Above();
        }

        Component c = getComponentAt(index);
        boolean contains = false;
        if (_closableSet.contains(c)) {
            contains = true;
        }

        if (!isAutoFocusOnTabHideClose())
            clearVisComp();

        if (contains) {
            _closableSet.remove(c);
        }
        if (c != null) {
            // JTabbedPane allows a null c, but doesn't really support it.
            PageLastFocusTracker tracker = (PageLastFocusTracker) _pageLastFocusTrackers.get(c);
            _pageLastFocusTrackers.remove(c);
            if (tracker != null) {
                tracker.setHighestComponent(null); // Clear its listeners
            }
        }

        if (getUI() instanceof BasicJideTabbedPaneUI) {
            ((BasicJideTabbedPaneUI) getUI()).ensureActiveTabIsVisible(true);
            if (isAutoFocusOnTabHideClose() && hasFocusComponent()) {
                ((BasicJideTabbedPaneUI) getUI()).requestFocusForVisibleComponent();
            }
        }

        super.removeTabAt(index);

        // We need to fire events
        if (enforce) {
            try {
                fireStateChanged();
            }
            catch (Throwable th) {
                th.printStackTrace();
            }
        }
    }

    /**
     * Checks if the tab at tabIndex should show the close button. This is only a valid if showCloseButtonOnTab
     * attribute is true.
     * <p/>
     * By default, this method always return true. Subclass can override this method to return a different value.
     *
     * @param tabIndex the tab index
     * @return the flag.
     * @throws IndexOutOfBoundsException if index is out of range (index < 0 || index >= tab count)
     */
    public boolean isTabClosableAt(int tabIndex) {
        return !_closableSet.contains(getComponentAt(tabIndex));
    }

    /**
     * Checks if the tab at tabIndex should show the close button. This is only a valid if showCloseButtonOnTab
     * attribute is true.
     * <p/>
     * By default, this method always return true. Subclass can override this method to return a different value.
     * <p/>
     * Please note, this attribute has effect only when {@link #isShowCloseButtonOnTab()} return true.
     *
     * @param tabIndex the tab index
     * @param closable the flag indicating if the tab is closable
     * @throws IndexOutOfBoundsException if index is out of range (index < 0 || index >= tab count)
     */
    public void setTabClosableAt(int tabIndex, boolean closable) {
        if (closable) {
            _closableSet.remove(getComponentAt(tabIndex));
        }
        else {
            _closableSet.add(getComponentAt(tabIndex));
        }
        firePropertyChange(TAB_CLOSABLE_PROPERTY, !closable, closable);
    }

    protected Hashtable getPageLastFocusTrackers() {
        return _pageLastFocusTrackers;
    }

    /**
     * Gets the last focused component of a particular page.
     *
     * @param pageComponent the page component
     * @return the last focused component of a particular page.
     */

    public Component getLastFocusedComponent(Component pageComponent) {
        if (pageComponent == null) {
            return null;
        }
//        System.out.println("---JideTabbedPane.getLastFocusedComponent()" + pageComponent);

        PageLastFocusTracker tracker = (PageLastFocusTracker) (
                getPageLastFocusTrackers().get(pageComponent));

//        System.out.println("---JideTabbedPane.getLastFocusedComponent()" + componentReturn);
//        if (false) {
//            Component compTest = new JPanel() {
//                public void requestFocus() {
////                    System.out.println("---.requestFocus()22");
//                    componentReturn.requestFocus();
//                }
//
//                public boolean isRequestFocusEnabled() {
////                    System.out.println("---.isRequestFocusEnabled()");
//                    return true;
//                }
//
//                public Container getParent() {
////                    System.out.println("---.getParent()");
//                    return (Container) componentReturn;
//                }
//            };
//            if (componentReturn != null)
//                ((Container) componentReturn).add(compTest);
//            return compTest;
//        }
        return ((tracker != null) ? tracker.getLastFocusedComponent() : null);
    }

    protected void clearVisComp() {
        // this is done so that the super removetab and fireselection do not attempt to manage focus
        // A very dirty hack to access a private variable is jtabpane. Note - this only works on 1.6
        if(SystemInfo.isJdk6Above()) {
            return;
        }
        try {
            java.lang.reflect.Field field = JTabbedPane.class.getDeclaredField("visComp");
            // set accessible true
            field.setAccessible(true);
            field.set(this, null);
//			superVisComp = (Component) field.get(this);
        }
        catch (Exception e) {
            // null
        }
    }

    /**
     * Overridden to add a <code>PageLastFocusTracker</code> to each page, used to update the page's last focused
     * component.
     */
    @Override
    public void insertTab(String title, Icon icon, Component component, String tip, int index) {
        // set the component to visible false initially because the layout manager will set it to visible when
        // appropriate. This also limits the flicker from mixing lightweight/heavyweight components.
        if(component != null) {
            if (component == getTabLeadingComponent() || component == getTabTrailingComponent()) {
                return;
            }
        }
        if (component != null && !component.isVisible())
            component.setVisible(false);

        super.insertTab(title, icon, component, tip, index);

        if (component != null) {
            // JTabbedPane allows a null component, but doesn't really support it.
            _pageLastFocusTrackers.put(component, new PageLastFocusTracker(component));
        }

//        fireStateChanged();
    }

    protected class PageLastFocusTracker extends JideFocusTracker {
        // keep track of last focused component
        private Component _lastFocusedComponent;

        private FocusListener _lastFocusedListener;

        protected PageLastFocusTracker(Component pageComp) {
            this.setHighestComponent(pageComp);
        }

        protected Component getLastFocusedComponent() {
            return _lastFocusedComponent;
        }

        @Override
        public void setHighestComponent(Component compHighest) {
            if (compHighest == null) {
                if (_lastFocusedListener != null) {
                    this.removeFocusListener(_lastFocusedListener);
                    _lastFocusedListener = null;
                }
            }
            else {
                if (_lastFocusedListener == null) {
                    _lastFocusedListener = new FocusAdapter() {
                        @Override
                        public void focusGained(FocusEvent e) {
                            _lastFocusedComponent = e.getComponent();
                        }
                    };
                    this.addFocusListener(_lastFocusedListener);
                }
            }
            super.setHighestComponent(compHighest);
        }
    }

    /**
     * Gets the font for selected tab.
     *
     * @return the font for selected tab.
     */
    public Font getSelectedTabFont() {
        return _selectedTabFont;
    }

    /**
     * Sets the font for selected tab.
     *
     * @param selectedTabFont new font for selected tab.
     */
    public void setSelectedTabFont(Font selectedTabFont) {
        _selectedTabFont = selectedTabFont;
    }

    public int getColorTheme() {
        if (_colorTheme == COLOR_THEME_DEFAULT) {
            return getDefaultColorTheme();
        }
        else {
            return _colorTheme;
        }
    }

    public int getDefaultColorTheme() {
        return UIDefaultsLookup.getInt("JideTabbedPane.defaultTabColorTheme");
    }

    public void setColorTheme(int colorTheme) {
        int old = _colorTheme;
        if (old != colorTheme) {
            _colorTheme = colorTheme;
            firePropertyChange(PROPERTY_COLOR_THEME, old, colorTheme);
        }
    }

    public int getTabResizeMode() {
        if (_tabResizeMode == RESIZE_MODE_DEFAULT) {
            return getDefaultTabResizeMode();
        }
        else {
            return _tabResizeMode;
        }
    }

    /**
     * Sets the tab resize mode. There are five resize modes. - {@link #RESIZE_MODE_DEFAULT}, {@link #RESIZE_MODE_NONE},
     * {@link #RESIZE_MODE_FIT}, {@link #RESIZE_MODE_FIXED} and {@link #RESIZE_MODE_COMPRESSED}.
     *
     * @param resizeMode the new resize mode.
     */
    public void setTabResizeMode(int resizeMode) {
        int old = _tabResizeMode;
        if (old != resizeMode) {
            _tabResizeMode = resizeMode;
            firePropertyChange(PROPERTY_TAB_RESIZE_MODE, old, resizeMode);
        }
    }

    public int getDefaultTabResizeMode() {
        return UIDefaultsLookup.getInt("JideTabbedPane.defaultResizeMode");
    }


    public int getTabShape() {
        if (_tabShape == SHAPE_DEFAULT) {
            return getDefaultTabStyle();
        }
        else {
            return _tabShape;
        }
    }

    public int getDefaultTabStyle() {
        return UIDefaultsLookup.getInt("JideTabbedPane.defaultTabShape");
    }

    public void setTabShape(int tabShape) {
        int old = _tabShape;
        if (old != tabShape) {
            _tabShape = tabShape;
            firePropertyChange(PROPERTY_TAB_SHAPE, old, _tabShape);
        }
    }

    /**
     * Sets the tab leading component. The tab leading component will appear before the tabs in the tab area. Please
     * note, you must implement UIResource for the component you want to use as tab leading component.
     *
     * @param component the tab leading component
     * @throws IllegalArgumentException if the component doesn't implement UIResource.
     */
    public void setTabLeadingComponent(Component component) {
//        if (component != null && !(component instanceof UIResource)) {
//            throw new IllegalArgumentException("TabLeadingComponent must implement javax.swing.plaf.UIResource interface.");
//        }
        Component old = _tabLeadingComponent;
        _tabLeadingComponent = component;
        firePropertyChange(PROPERTY_TAB_LEADING_COMPONENT, old, component);
    }

    public Component getTabLeadingComponent() {
        return _tabLeadingComponent;
    }

    /**
     * Sets the tab trailing component. The tab trailing component will appear after the tabs in the tab area. Please
     * note, you must implement UIResource for the component you want to use as tab trailing component.
     *
     * @param component the tab trailing component
     * @throws IllegalArgumentException if the component doesn't implement UIResource.
     */
    public void setTabTrailingComponent(Component component) {
//        if (component != null && !(component instanceof UIResource)) {
//            throw new IllegalArgumentException("TabLeadingComponent must implement javax.swing.plaf.UIResource interface.");
//        }
        Component old = _tabTrailingComponent;
        _tabTrailingComponent = component;
        firePropertyChange(PROPERTY_TAB_TRAILING_COMPONENT, old, component);
    }

    @Override
    public Component add(Component component) {
        if (!(component instanceof UIResource) && component != getTabTrailingComponent() && component != getTabLeadingComponent()) {
            addTab(component.getName(), component);
        }
        else {
            addImpl(component, null, -1);
        }
        return component;
    }

    public Component getTabTrailingComponent() {
        return _tabTrailingComponent;
    }

    public boolean isShowCloseButtonOnSelectedTab() {
        return _showCloseButtonOnSelectedTab;
    }

    /**
     * Shows the close button on the selected tab only. You also need to setShowCloseButtonOnTab(true) and
     * setShowCloseButton(true) if you want to setShowCloseButtonOnSelectedTab(true).
     *
     * @param i the flag indicating if close button should be shown in the selected tab
     */
    public void setShowCloseButtonOnSelectedTab(boolean i) {
        _showCloseButtonOnSelectedTab = i;
    }

    /**
     * Gets the flag indicating if the close button should only be displayed when the mouse is over the tab.
     *
     * @return true if the close button should only be displayed when the mouse is over the tab. Otherwise false.
     * @see #setShowCloseButtonOnMouseOver(boolean)
     * @since 3.3.3
     */
    public boolean isShowCloseButtonOnMouseOver() {
        return _showCloseButtonOnMouseOver;
    }

    /**
     * Sets the flag indicating if the close button should only be displayed when the mouse is over the tab.
     * <p/>
     * The default value of the flag is false to keep default behavior not changed.
     *
     * @param showCloseButtonOnMouseOverOnly the flag
     * @since 3.3.3
     */
    public void setShowCloseButtonOnMouseOver(boolean showCloseButtonOnMouseOverOnly) {
        if (_showCloseButtonOnMouseOver != showCloseButtonOnMouseOverOnly) {
            boolean old = _showCloseButtonOnMouseOver;
            _showCloseButtonOnMouseOver = showCloseButtonOnMouseOverOnly;
            firePropertyChange(PROPERTY_SHOW_CLOSE_BUTTON_ON_MOUSE_OVER, old, _showCloseButtonOnMouseOver);
        }
    }


    private ColorProvider _tabColorProvider;

    /**
     * An interface to provide colors for tab background and foreground.
     */
    public static interface ColorProvider {
        /**
         * Gets the tab background for the tab at the specified index.
         *
         * @param tabIndex the index of the tab
         * @return the tab background for the tab at the specified index.
         */
        Color getBackgroundAt(int tabIndex);

        /**
         * Gets the tab foreground for the tab at the specified index.
         *
         * @param tabIndex the index of the tab
         * @return the tab foreground for the tab at the specified index.
         */
        Color getForegroundAt(int tabIndex);

        /**
         * Gets the gradient ratio. We will use this ratio to provide another color in order to paint gradient.
         *
         * @param tabIndex the index of the tab
         * @return the gradient ratio. The value should be between 0 and 1. 0 will produce the darkest and color and 1
         *         will produce the lightest color. 0.5 will provide the same color.
         */
        float getGradientRatio(int tabIndex);
    }

    /**
     * A ColorProvider that can supports gradient tab background. The ColorProvider can also do gradient but the other
     * color has to be be a lighter or darker version of the color of getBackgroundAt. GradientColorProvider allows you
     * to specify an independent color as the start color.
     */
    public static interface GradientColorProvider extends ColorProvider {
        /**
         * Gets the tab background at the top (or other direction depending on the tab placement) of the tab. The
         * JideTabbedPaneUI will paint a gradient using this color and the color of getBackgroundAt.
         *
         * @param tabIndex the index of the tab
         * @return the top background color.
         */
        Color getTopBackgroundAt(int tabIndex);
    }

    private static Color[] ONENOTE_COLORS = {
            new Color(138, 168, 228), // blue
            new Color(238, 149, 151), // pink
            new Color(180, 158, 222), // purple
            new Color(145, 186, 174), // cyan
            new Color(246, 176, 120), // gold
            new Color(255, 216, 105), // yellow
            new Color(183, 201, 151)  // green
    };

    public static ColorProvider ONENOTE_COLOR_PROVIDER = new OneNoteColorProvider();

    private static class OneNoteColorProvider implements ColorProvider {
        public Color getBackgroundAt(int index) {
            return ONENOTE_COLORS[index % ONENOTE_COLORS.length];
        }

        public Color getForegroundAt(int index) {
            return Color.BLACK;
        }

        public float getGradientRatio(int tabIndex) {
            return 0.86f;
        }

    }

    /**
     * Gets the tab color provider.
     *
     * @return tab color provider.
     */
    public ColorProvider getTabColorProvider() {
        return _tabColorProvider;
    }

    /**
     * Sets the tab color provider.It allows you to set the background color of each tab. The reason to use this way
     * instead of {@link #setBackgroundAt(int, java.awt.Color)} method is because this way queries the color. So it can
     * support unlimited number of tabs. When you don't know exactly how many tabs it will be, this way can still handle
     * it very well. There is {@link #ONENOTE_COLOR_PROVIDER} which provides the tab color as you see in Microsoft
     * OneNote 2003. You can also define your own ColorProvider to fit your application color theme.
     *
     * @param tabColorProvider the tab color provider
     */
    public void setTabColorProvider(ColorProvider tabColorProvider) {
        ColorProvider old = _tabColorProvider;
        if (old != tabColorProvider) {
            _tabColorProvider = tabColorProvider;
            firePropertyChange(PROPERTY_TAB_COLOR_PROVIDER, old, tabColorProvider);
        }
    }

    /**
     * Starts tab editing. This works only when {@link #setTabEditingAllowed(boolean)} is set to true.
     *
     * @param tabIndex the index of the tab
     */
    public void editTabAt(int tabIndex) {
        boolean started = ((JideTabbedPaneUI) getUI()).editTabAt(tabIndex);
        if (started) {
            fireTabEditing(TabEditingEvent.TAB_EDITING_STARTED, tabIndex, getTitleAt(tabIndex), null);
        }
    }

    /**
     * Checks if tab is in editing mode.
     *
     * @return true if editing.
     */
    public boolean isTabEditing() {
        return ((JideTabbedPaneUI) getUI()).isTabEditing();
    }

    public void stopTabEditing() {
        int tabIndex = getEditingTabIndex();
        if (tabIndex != -1 && tabIndex < getTabCount()) {
            String oldTitle = getTitleAt(tabIndex);
            ((JideTabbedPaneUI) getUI()).stopTabEditing();
            String newTitle = getTitleAt(tabIndex);
            fireTabEditing(TabEditingEvent.TAB_EDITING_STOPPED, tabIndex, oldTitle, newTitle);
        }
    }

    public void cancelTabEditing() {
        int tabIndex = getEditingTabIndex();
        if (tabIndex != -1) {
            ((JideTabbedPaneUI) getUI()).cancelTabEditing();
            fireTabEditing(TabEditingEvent.TAB_EDITING_CANCELLED, tabIndex, getTitleAt(tabIndex), getTitleAt(tabIndex));
        }
    }

    public int getEditingTabIndex() {
        return ((JideTabbedPaneUI) getUI()).getEditingTabIndex();
    }

    protected PropertyChangeListener _focusChangeListener;

    protected PropertyChangeListener createFocusChangeListener() {
        return new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                final boolean hadFocus = JideTabbedPane.this.isAncestorOf((Component) evt.getOldValue()) || JideTabbedPane.this == evt.getOldValue();
                boolean hasFocus = JideTabbedPane.this == evt.getNewValue() || JideTabbedPane.this.hasFocusComponent();
                if (hasFocus != hadFocus) {
                    repaintTabAreaAndContentBorder();
                }
            }
        };
    }

    /**
     * Repaints the tab area and the content border if any. This is mainly for the focus border in JideTabbedPane
     * Office2003 and Eclipse3x style.
     */
    public void repaintTabAreaAndContentBorder() {
        int delay = 200;
        ((JideTabbedPaneUI) getUI()).getTabPanel().repaint(delay);

        Insets contentinsets = getContentBorderInsets();
        if (contentinsets == null) {
            LookAndFeelFactory.installJideExtension();
            contentinsets = getContentBorderInsets();
        }

        if (contentinsets != null && (contentinsets.top != 0 || contentinsets.bottom != 0 || contentinsets.left != 0 || contentinsets.right != 0)) {
            Insets insets = new Insets(0, 0, 0, 0);
            BasicJideTabbedPaneUI.rotateInsets(contentinsets, insets, tabPlacement);
            switch (getTabPlacement()) {
                case TOP:
                    insets.top += getTabHeight();
                    break;
                case BOTTOM:
                    insets.bottom += getTabHeight();
                    break;
                case LEFT:
                    insets.left += getTabHeight();
                    break;
                case RIGHT:
                    insets.right += getTabHeight();
                    break;
            }
            if (insets.top != 0) {
                repaintContentBorder(0, 0, getWidth(), insets.top);
            }
            if (insets.left != 0) {
                repaintContentBorder(0, 0, insets.left, getHeight());
            }
            if (insets.right != 0) {
                repaintContentBorder(getWidth() - insets.right, 0, insets.right, getHeight());
            }
            if (insets.bottom != 0) {
                repaintContentBorder(0, getHeight() - insets.bottom, getWidth(), insets.bottom);
            }
        }
    }

    /**
     * Calls repaint on the specified rectangular area.
     *
     * @param x      the <i>x</i> coordinate
     * @param y      the <i>y</i> coordinate
     * @param width  the width
     * @param height the height
     */
    protected void repaintContentBorder(int x, int y, int width, int height) {
        repaint(x, y, width, height);
    }

    @Override
    public void addNotify() {
        super.addNotify();
        if (_focusChangeListener == null) {
            _focusChangeListener = createFocusChangeListener();
            KeyboardFocusManager.getCurrentKeyboardFocusManager().addPropertyChangeListener("focusOwner", _focusChangeListener);
        }

    }

    @Override
    public void removeNotify() {
        super.removeNotify();
        if (_focusChangeListener != null) {
            KeyboardFocusManager.getCurrentKeyboardFocusManager().removePropertyChangeListener("focusOwner", _focusChangeListener);
            _focusChangeListener = null;
        }
    }

    /**
     * Gets the tab list cell renderer. This renderer is used to render the list in the popup when tab list button is
     * pressed.
     *
     * @return the tab list cell renderer.
     * @see #setTabListCellRenderer(javax.swing.ListCellRenderer)
     */
    public ListCellRenderer getTabListCellRenderer() {
        if (_tabListCellRenderer != null) {
            return _tabListCellRenderer;
        }
        else {
            return new TabListCellRenderer();
        }
    }

    /**
     * The default tab list cell renderer used to renderer the list in the popup when tab list button is pressed.
     */
    public static class TabListCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            if (value instanceof JideTabbedPane) {
                JideTabbedPane tabbedPane = (JideTabbedPane) value;
                if(tabbedPane.getTabCount() == 0 || tabbedPane.getTabCount() <= index) {
                    return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                }
                String title = tabbedPane.getTitleAt(index);
                String tooltip = tabbedPane.getToolTipTextAt(index);
                Icon icon = tabbedPane.getIconForTab(index);
                JLabel label = (JLabel) super.getListCellRendererComponent(list, title, index, isSelected, cellHasFocus);
                label.setToolTipText(tooltip);
                Font fnt;
                if (tabbedPane.getSelectedIndex() == index && tabbedPane.getSelectedTabFont() != null) {
                    fnt = tabbedPane.getSelectedTabFont();
                }
                else {
                    fnt = tabbedPane.getFont();
                }

                if (tabbedPane.getSelectedIndex() == index && tabbedPane.isBoldActiveTab() && fnt.getStyle() != Font.BOLD) {
                    fnt = fnt.deriveFont(Font.BOLD);
                }
                label.setFont(fnt);
                label.setIcon(icon);
                label.setEnabled(tabbedPane.isEnabledAt(index));
                return label;
            }
            else {
                return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            }
        }
    }

    /**
     * Sets the tab list cell renderer. This renderer is used to render the list in the popup when tab list button is
     * pressed. In this list cell renderer, the value will always be the JideTabbedPane. The index will tell you which
     * tab it is. See below for the default cell renderer we used.
     * <code><pre>
     * public static class TabListCellRenderer extends DefaultListCellRenderer {
     *     public Component getListCellRendererComponent(JList list, Object value, int index,
     * boolean isSelected, boolean cellHasFocus) {
     *         if (value instanceof JideTabbedPane) { // will always be true
     *             JideTabbedPane tabbedPane = (JideTabbedPane) value;
     *             String title = tabbedPane.getTitleAt(index);
     *             Icon icon = tabbedPane.getIconAt(index);
     *             JLabel label = (JLabel) super.getListCellRendererComponent(list, title, index,
     * isSelected, cellHasFocus);
     *             label.setIcon(icon);
     *             return label;
     *         }
     *         else {
     *             return super.getListCellRendererComponent(list, value, index, isSelected,
     * cellHasFocus);
     *         }
     *     }
     * }
     * </code></pre>
     * You can create your own cell renderer either extending {@link TabListCellRenderer} or starting from scratch.
     *
     * @param tabListCellRenderer the cell renderer
     */
    public void setTabListCellRenderer(ListCellRenderer tabListCellRenderer) {
        _tabListCellRenderer = tabListCellRenderer;
    }

    /**
     * Checks if the JideTabbedPane has the focus component. If true, in some styles such as Office2003 style, we will
     * paint a background on the insets to indicate the tabbed pane has focus.
     *
     * @return true if the JideTabbedPane has the focus component. Otherwise false.
     */
    public boolean hasFocusComponent() {
        return JideSwingUtilities.isAncestorOfFocusOwner(this);
    }

    public Insets getContentBorderInsets() {
        return _contentBorderInsets;
    }

    /**
     * Sets the content border insets. It's the inserts around the JideTabbedPane's content. The direction of the insets
     * is when the tabs are on top. We will rotate it automatically when the tabs are on other directions.
     *
     * @param contentBorderInsets the content border insets
     */
    public void setContentBorderInsets(Insets contentBorderInsets) {
        Insets old = _contentBorderInsets;
        _contentBorderInsets = contentBorderInsets;
        firePropertyChange(PROPERTY_CONTENT_BORDER_INSETS, old, _contentBorderInsets);
    }

    public Insets getTabAreaInsets() {
        return _tabAreaInsets;
    }

    /**
     * Sets the tab area insets. It's the inserts around the tabs. The direction of the insets is when the tabs are on
     * top. We will rotate it automatically when the tabs are on other directions.
     *
     * @param tabAreaInsets the content border insets
     */
    public void setTabAreaInsets(Insets tabAreaInsets) {
        Insets old = _tabAreaInsets;
        _tabAreaInsets = tabAreaInsets;
        firePropertyChange(PROPERTY_TAB_AREA_INSETS, old, _tabAreaInsets);
    }

    public Insets getTabInsets() {
        return _tabInsets;
    }

    /**
     * Sets the tab insets. It's the inserts around the JideTabbedPane's tab. The direction of the insets is when the
     * tabs are on top. We will rotate it automatically when the tabs are on other directions.
     *
     * @param tabInsets the content border insets
     */
    public void setTabInsets(Insets tabInsets) {
        Insets old = _tabInsets;
        _tabInsets = tabInsets;
        firePropertyChange(PROPERTY_TAB_INSETS, old, _tabInsets);
    }

    /**
     * Checks the dragOverDisabled property. By default it is false.
     *
     * @return true or false.
     * @see #setDragOverDisabled(boolean)
     */
    public boolean isDragOverDisabled() {
        return _dragOverDisabled;
    }

    /**
     * Sets the dragOverDisabled property. Default is false. It means when you drag something over an unselected tab,
     * the tab will be selected automatically. You may want to set it to true if you want to add your own drop listener
     * to the tabs.
     *
     * @param dragOverDisabled the flag indicating if drag over is disabled
     */
    public void setDragOverDisabled(boolean dragOverDisabled) {
        boolean old = _dragOverDisabled;
        if (old != dragOverDisabled) {
            _dragOverDisabled = dragOverDisabled;
            firePropertyChange(PROPERTY_DRAG_OVER_DISABLED, old, dragOverDisabled);
        }
    }

    /**
     * Scroll the selected tab visible in case the tab is outside of the viewport.
     *
     * @param scrollLeft true to scroll the first tab visible first then scroll left to make the selected tab visible.
     *                   This will get a more consistent result. If false, it will simple scroll the selected tab
     *                   visible. Sometimes the tab will appear as the first visible tab or the last visible tab
     *                   depending on the previous viewport position.
     */
    public void scrollSelectedTabToVisible(boolean scrollLeft) {
        ((JideTabbedPaneUI) getUI()).ensureActiveTabIsVisible(scrollLeft);
    }

    /**
     * Adds a <code>TabEditingListener</code> to this tabbedpane.
     *
     * @param l the <code>TabEditingListener</code> to add
     * @see #fireTabEditing
     * @see #removeTabEditingListener(TabEditingListener)
     * @see #getTabEditingListeners()
     */
    public void addTabEditingListener(TabEditingListener l) {
        listenerList.add(TabEditingListener.class, l);
    }

    /**
     * Removes a <code>TabEditingListener</code> from this tabbedpane.
     *
     * @param l the <code>TabEditingListener</code> to remove
     * @see #fireTabEditing
     * @see #addTabEditingListener
     */
    public void removeTabEditingListener(TabEditingListener l) {
        listenerList.remove(TabEditingListener.class, l);
    }

    /**
     * Returns an array of all the <code>TabEditingListener</code>s added to this <code>JTabbedPane</code> with
     * <code>addTabEditingListener</code>.
     *
     * @return all of the <code>TabEditingListener</code>s added or an empty array if no listeners have been added
     */
    public TabEditingListener[] getTabEditingListeners() {
        return listenerList.getListeners(TabEditingListener.class);
    }

    protected void fireTabEditing(int id, int index, String oldTitle, String newTitle) {
        if (LOGGER_EVENT.isLoggable(Level.FINE)) {
            switch (id) {
                case TabEditingEvent.TAB_EDITING_STARTED:
                    LOGGER_EVENT.fine("TabEditing Started at tab \"" + index + "\"; the current title is " + oldTitle);
                    break;
                case TabEditingEvent.TAB_EDITING_STOPPED:
                    LOGGER_EVENT.fine("TabEditing Stopped at tab \"" + index + "\"; the old title is " + oldTitle + "; the new title is " + newTitle);
                    break;
                case TabEditingEvent.TAB_EDITING_CANCELLED:
                    LOGGER_EVENT.fine("TabEditing Cancelled at tab \"" + index + "\"; the current title remains " + oldTitle);
                    break;
            }
        }
        Object[] listeners = listenerList.getListenerList();
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == TabEditingListener.class) {
                TabEditingEvent tabEditingEvent = new TabEditingEvent(this, id, index, oldTitle, newTitle);
                if (id == TabEditingEvent.TAB_EDITING_STARTED) {
                    ((TabEditingListener) listeners[i + 1]).editingStarted(tabEditingEvent);
                }
                else if (id == TabEditingEvent.TAB_EDITING_CANCELLED) {
                    ((TabEditingListener) listeners[i + 1]).editingCanceled(tabEditingEvent);
                }
                else if (id == TabEditingEvent.TAB_EDITING_STOPPED) {
                    ((TabEditingListener) listeners[i + 1]).editingStopped(tabEditingEvent);
                }
            }
        }
    }

    /**
     * Gets the icon for the tab after looking at the UIDefault "JideTabbedPane.showIconOnTab" and {@link
     * #isShowIconsOnTab()}. Note that getIconAt method will always return the tab even though the icon is not displayed
     * because the two flags above.
     *
     * @param tabIndex the tab index.
     * @return the icon for the tab at the specified index.
     */
    public Icon getIconForTab(int tabIndex) {
        boolean _showIconOnTab = UIDefaultsLookup.getBoolean("JideTabbedPane.showIconOnTab");
        if (isUseDefaultShowIconsOnTab()) {
            if (_showIconOnTab) {
                return (!isEnabled() || !isEnabledAt(tabIndex)) ? getDisabledIconAt(tabIndex) : getIconAt(tabIndex);
            }
            else {
                return null;
            }
        }
        else if (isShowIconsOnTab()) {
            return (!isEnabled() || !isEnabledAt(tabIndex)) ? getDisabledIconAt(tabIndex) : getIconAt(tabIndex);
        }
        else {
            return null;
        }
    }

    /**
     * Checks if the selected tab will be changed on mouse wheel event.
     *
     * @return true or false.
     */
    public boolean isScrollSelectedTabOnWheel() {
        return _scrollSelectedTabOnWheel;
    }

    /**
     * If true, the selected tab will be changed on mouse wheel. It is false by default.
     *
     * @param scrollSelectedTabOnWheel the flag
     */
    public void setScrollSelectedTabOnWheel(boolean scrollSelectedTabOnWheel) {
        boolean oldValue = isScrollSelectedTabOnWheel();
        if (oldValue != scrollSelectedTabOnWheel) {
            _scrollSelectedTabOnWheel = scrollSelectedTabOnWheel;
            firePropertyChange(SCROLL_TAB_ON_WHEEL_PROPERTY, oldValue, _scrollSelectedTabOnWheel);
        }
    }

    /**
     * Get the flag if clicking middle mouse button can close the tab. It is false by default.
     *
     * @return the flag.
     */
    public boolean isCloseTabOnMouseMiddleButton() {
        return _closeTabOnMouseMiddleButton;
    }

    /**
     * Set the flag if clicking middle mouse button can close the tab. It is false by default.
     *
     * @param closeTabOnMouseMiddleButton the flag
     */
    public void setCloseTabOnMouseMiddleButton(boolean closeTabOnMouseMiddleButton) {
        this._closeTabOnMouseMiddleButton = closeTabOnMouseMiddleButton;
    }

    /**
     * Returns the alignment of the tabs for this tabbed pane.
     *
     * @return the alignment of the tabs for this tabbed pane.
     * @see #setTabAlignment(int)
     */
    public int getTabAlignment() {
        return _tabAlignment;
    }

    /**
     * Sets the tab alignment for the tabs of a tabbed pane. Currently it only supports top and bottom tab placement.
     * Possible values are:<ul> <li><code>JideTabbedPane.LEADING</code> <li><code>JideTabbedPane.CENTER</code> </ul> The
     * default value, if not set, is <code>JideTabbedPane.LEADING</code>.
     *
     * @param tabAlignment the alignment for the tabs relative to the content
     * @throws IllegalArgumentException if tab alignment value isn't one of the above valid values
     */
    public void setTabAlignment(int tabAlignment) {
        if (tabAlignment != LEADING && tabAlignment != CENTER) {
            throw new IllegalArgumentException("illegal tab alignment: must be LEADING or CENTER");
        }
        if (_tabAlignment != tabAlignment) {
            int oldValue = _tabAlignment;
            _tabAlignment = tabAlignment;
            firePropertyChange("tabAlignment", oldValue, tabAlignment);
            revalidate();
            repaint();
        }
    }

    /**
     * Gets the resource string used in JideTabbedPane. Subclass can override it to provide their own strings.
     *
     * @param key the resource key
     * @return the localized string.
     */
    public String getResourceString(String key) {
        return com.jidesoft.plaf.basic.Resource.getResourceBundle(getLocale()).getString(key);
    }

    /**
     * Creates tab list popup.
     *
     * @return the tab list popup instance.
     * @since 3.2.2
     */
    protected JidePopup createTabListPopup() {
        return new JidePopup();
    }

    /**
     * Checks if the tab list popup is visible.
     *
     * @return true if the tab list popup is visible. Otherwise false.
     * @since 3.2.2
     */
    public boolean isTabListPopupVisible() {
        return _tabListPopup != null && _tabListPopup.isPopupVisible();
    }

    /**
     * Hides the tab list popup if it's visible.
     *
     * @since 3.2.2
     */
    public void hideTabListPopup() {
        if (_tabListPopup != null) {
            if (_tabListPopup.isPopupVisible()) {
                _tabListPopup.hidePopupImmediately();
            }
            _tabListPopup = null;
        }
    }

    /**
     * Shows the tab list popup by clicking on the list button.
     *
     * @param listButton the list button being clicked.
     * @since 3.2.2
     */
    public void showTabListPopup(JButton listButton) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(UIDefaultsLookup.getColor("JideTabbedPane.tabListBackground"));
        panel.setOpaque(true);
        panel.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));

        JList list = createTabList(panel.getInsets());
        JScrollPane scroller = new JScrollPane(list);
        scroller.setBorder(BorderFactory.createEmptyBorder());
        scroller.getViewport().setOpaque(false);
        scroller.setOpaque(false);
        panel.add(scroller);

        hideTabListPopup();
        _tabListPopup = createTabListPopup();
        _tabListPopup.setComponentOrientation(getComponentOrientation());
        _tabListPopup.setPopupBorder(BorderFactory.createLineBorder(UIDefaultsLookup.getColor("JideTabbedPane.darkShadow")));
        _tabListPopup.add(panel);
        _tabListPopup.addExcludedComponent(listButton);
        _tabListPopup.setDefaultFocusComponent(list);


        _tabListPopup.setOwner(this);
        _tabListPopup.removeExcludedComponent(this);

        Point point = calculateTabListPopupPosition(listButton);
        _tabListPopup.showPopup(point.x, point.y);
    }

    /**
     * Calculates the position where the tab list popup is to be displayed based on the list button being clicked.
     *
     * @param listButton the list button being clicked.
     * @return the point.
     * @since 3.2.2
     */
    protected Point calculateTabListPopupPosition(JButton listButton) {
        Dimension size = _tabListPopup.getPreferredSize();
        Rectangle bounds = listButton.getBounds();
        Point p = listButton.getLocationOnScreen();
        bounds.x = p.x;
        bounds.y = p.y;
        int x;
        int y;
        switch (getTabPlacement()) {
            case TOP:
            default:
                if (getComponentOrientation().isLeftToRight()) {
                    x = bounds.x + bounds.width - size.width;
                }
                else {
                    x = bounds.x;
                }
                y = bounds.y + bounds.height + 2;
                break;
            case BOTTOM:
                if (getComponentOrientation().isLeftToRight()) {
                    x = bounds.x + bounds.width - size.width;
                }
                else {
                    x = bounds.x;
                }
                y = bounds.y - size.height - 2;
                break;
            case LEFT:
                x = bounds.x + bounds.width + 2;
                y = bounds.y + bounds.height - size.height;
                break;
            case RIGHT:
                x = bounds.x - size.width - 2;
                y = bounds.y + bounds.height - size.height;
                break;
        }

        Rectangle screenBounds = PortingUtils.getScreenBounds(this, true);
        int right = x + size.width + 3;
        int bottom = y + size.height + 3;

        if (right > screenBounds.x + screenBounds.width) {
            x -= right - screenBounds.x - screenBounds.width; // move left so that the whole popup can fit in
        }

        if (x < screenBounds.x) {
            x = screenBounds.x; // move right so that the whole popup can fit in
        }

        if (bottom > screenBounds.height) {
            y -= bottom - screenBounds.height;
        }

        if (y < screenBounds.y) {
            y = screenBounds.y;
        }
        return new Point(x, y);
    }

    /**
     * Creates the tab list.
     *
     * @param insets the insets of its parent container which helps determine the visible row count of the list.
     * @return the created list instance.
     * @since 3.2.2
     */
    protected JList createTabList(Insets insets) {
        final JList list = new JList() {
            // override this method to disallow deselect by ctrl-click
            @Override
            public void removeSelectionInterval(int index0, int index1) {
                super.removeSelectionInterval(index0, index1);
                if (getSelectedIndex() == -1) {
                    setSelectedIndex(index0);
                }
            }

            @Override
            public Dimension getPreferredScrollableViewportSize() {
                Dimension preferredScrollableViewportSize = super.getPreferredScrollableViewportSize();
                if (preferredScrollableViewportSize.width < 150) {
                    preferredScrollableViewportSize.width = 150;
                }
                int screenWidth = PortingUtils.getScreenSize(this).width;
                if (preferredScrollableViewportSize.width >= screenWidth) {
                    preferredScrollableViewportSize.width = screenWidth;
                }
                return preferredScrollableViewportSize;
            }

            @Override
            public Dimension getPreferredSize() {
                Dimension preferredSize = super.getPreferredSize();
                int screenWidth = PortingUtils.getScreenSize(this).width;
                if (preferredSize.width >= screenWidth) {
                    preferredSize.width = screenWidth;
                }
                return preferredSize;
            }
        };
        DefaultListModel listModel = new DefaultListModel();

        // drop down menu items
        int selectedIndex = getSelectedIndex();
        int totalCount = getTabCount();
        for (int i = 0; i < totalCount; i++) {
            listModel.addElement(this);
        }
        list.setCellRenderer(getTabListCellRenderer());
        list.setModel(listModel);
        list.setSelectedIndex(selectedIndex);
        list.addKeyListener(new KeyListener() {
            public void keyTyped(KeyEvent e) {
            }

            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    componentSelected(list);
                }
            }

            public void keyReleased(KeyEvent e) {
            }
        });
        list.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                componentSelected(list);
            }
        });

        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        int max = (PortingUtils.getLocalScreenSize(this).height - insets.top - insets.bottom) / list.getCellBounds(0, 0).height;
        if (listModel.getSize() > max) {
            list.setVisibleRowCount(max);
        }
        else {
            list.setVisibleRowCount(listModel.getSize());
        }
        new Sticky(list);
        list.setBackground(UIDefaultsLookup.getColor("JideTabbedPane.tabListBackground"));
        return list;
    }

    private void componentSelected(JList list) {
        int tabIndex = list.getSelectedIndex();
        if (tabIndex != -1 && isEnabledAt(tabIndex)) {
            if (tabIndex == getSelectedIndex() && JideSwingUtilities.isAncestorOfFocusOwner(this)) {
                if (isAutoFocusOnTabHideClose() && isRequestFocusEnabled()) {
                    Runnable runnable = new Runnable() {
                        public void run() {
                            requestFocus();
                        }
                    };
                    SwingUtilities.invokeLater(runnable);
                }
            }
            else {
                setSelectedIndex(tabIndex);
                final Component comp = getComponentAt(tabIndex);
                if (isAutoFocusOnTabHideClose() && !comp.isVisible() && SystemInfo.isJdk15Above() && !SystemInfo.isJdk6Above()) {
                    comp.addComponentListener(new ComponentAdapter() {
                        @Override
                        public void componentShown(ComponentEvent e) {
                            // remove the listener
                            comp.removeComponentListener(this);

                            final Component lastFocused = getLastFocusedComponent(comp);
                            Runnable runnable = new Runnable() {
                                public void run() {
                                    if (lastFocused != null) {
                                        lastFocused.requestFocus();
                                    }
                                    else if (isRequestFocusEnabled()) {
                                        requestFocus();
                                    }
                                }
                            };
                            SwingUtilities.invokeLater(runnable);
                        }
                    });
                }
                else {
                    final Component lastFocused = getLastFocusedComponent(comp);
                    if (lastFocused != null) {
                        Runnable runnable = new Runnable() {
                            public void run() {
                                lastFocused.requestFocus();
                            }
                        };
                        SwingUtilities.invokeLater(runnable);
                    }
                    else {
                        Container container;
                        if (comp instanceof Container) {
                            container = (Container) comp;
                        }
                        else {
                            container = comp.getFocusCycleRootAncestor();
                        }
                        FocusTraversalPolicy traversalPolicy = container.getFocusTraversalPolicy();
                        Component focusComponent;
                        if (traversalPolicy != null) {
                            focusComponent = traversalPolicy.getDefaultComponent(container);
                            if (focusComponent == null) {
                                focusComponent = traversalPolicy.getFirstComponent(container);
                            }
                        }
                        else if (comp instanceof Container) {
                            // not sure if it is correct
                            focusComponent = findFocusableComponent((Container) comp);
                        }
                        else {
                            focusComponent = comp;
                        }
                        if (focusComponent != null) {
                            final Component theComponent = focusComponent;
                            Runnable runnable = new Runnable() {
                                public void run() {
                                    theComponent.requestFocus();
                                }
                            };
                            SwingUtilities.invokeLater(runnable);
                        }
                    }
                }
            }
            if (getUI() instanceof BasicJideTabbedPaneUI) {
                ((BasicJideTabbedPaneUI) getUI()).ensureActiveTabIsVisible(false);
            }
            hideTabListPopup();
        }
    }

    private Component findFocusableComponent(Container parent) {
        FocusTraversalPolicy traversalPolicy = parent.getFocusTraversalPolicy();
        Component focusComponent = null;
        if (traversalPolicy != null) {
            focusComponent = traversalPolicy.getDefaultComponent(parent);
            if (focusComponent == null) {
                focusComponent = traversalPolicy.getFirstComponent(parent);
            }
        }
        if (focusComponent != null) {
            return focusComponent;
        }
        int i = 0;
        while (i < parent.getComponentCount()) {
            Component comp = parent.getComponent(i);
            if (comp instanceof Container) {
                focusComponent = findFocusableComponent((Container) comp);
                if (focusComponent != null) {
                    return focusComponent;
                }
            }
            else if (comp.isFocusable()) {
                return comp;
            }
            i++;
        }
        if (parent.isFocusable()) {
            return parent;
        }
        return null;
    }

    /**
     * Creates no focus buttons for JideTabbedPane.
     *
     * @param type the button type, it could be {@link #BUTTON_LIST}, {@link #BUTTON_CLOSE}, {@link #BUTTON_EAST},
     *             {@link #BUTTON_WEST}, {@link #BUTTON_NORTH} or {@link #BUTTON_SOUTH}
     * @return the button instance.
     */
    public NoFocusButton createNoFocusButton(int type) {
        return new NoFocusButton(type);
    }

    public class NoFocusButton extends JButton implements MouseMotionListener, MouseListener, UIResource {
        private int _type;
        private int _index = -1;
        private boolean _mouseOver = false;
        private boolean _mousePressed = false;

        /**
         * Resets the UI property to a value from the current look and feel.
         *
         * @see JComponent#updateUI
         */
        @Override
        public void updateUI() {
            super.updateUI();
            setMargin(new Insets(0, 0, 0, 0));
            setBorder(BorderFactory.createEmptyBorder());
            setFocusPainted(false);
            setFocusable(false);
            setRequestFocusEnabled(false);
            String name = getName();
            if (name != null) setToolTipText(getResourceString(name));
        }

        public NoFocusButton() {
            this(BUTTON_CLOSE);
        }

        public NoFocusButton(int type) {
            addMouseMotionListener(this);
            addMouseListener(this);
            setFocusPainted(false);
            setFocusable(false);
            setType(type);
        }

        @Override
        public Dimension getPreferredSize() {
            return new Dimension(16, 16);
        }

        @Override
        public Dimension getMinimumSize() {
            return new Dimension(5, 5);
        }

        public int getIndex() {
            return _index;
        }

        public void setIndex(int index) {
            _index = index;
        }

        @Override
        public Dimension getMaximumSize() {
            return new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE);
        }

        @Override
        protected void paintComponent(Graphics g) {
            if (getIcon() != null) {
                super.paintComponent(g);
                return;
            }
            if (!isEnabled()) {
                setMouseOver(false);
                setMousePressed(false);
            }
            if (isMouseOver() && isMousePressed()) {
                g.setColor(getPressedShadowColor());
                g.drawLine(0, 0, getWidth() - 1, 0);
                g.drawLine(0, getHeight() - 2, 0, 1);
                g.setColor(getShadowColor());
                g.drawLine(getWidth() - 1, 1, getWidth() - 1, getHeight() - 2);
                g.drawLine(getWidth() - 1, getHeight() - 1, 0, getHeight() - 1);
            }
            else if (isMouseOver()) {
                g.setColor(getShadowColor());
                g.drawLine(0, 0, getWidth() - 1, 0);
                g.drawLine(0, getHeight() - 2, 0, 1);
                g.setColor(getPressedShadowColor());
                g.drawLine(getWidth() - 1, 1, getWidth() - 1, getHeight() - 2);
                g.drawLine(getWidth() - 1, getHeight() - 1, 0, getHeight() - 1);
            }
            g.setColor(getForegroundColor());
            int centerX = getWidth() >> 1;
            int centerY = getHeight() >> 1;
            int type = getType();
            if ((getTabPlacement() == TOP || getTabPlacement() == BOTTOM) && !JideTabbedPane.this.getComponentOrientation().isLeftToRight()) {
                if (type == BUTTON_EAST) {
                    type = BUTTON_WEST;
                }
                else if (type == BUTTON_WEST) {
                    type = BUTTON_EAST;
                }
            }
            switch (type) {
                case BUTTON_CLOSE:
                    if (isShowCloseButtonOnMouseOver() && !isMouseOver()) {
                        Object property = JideTabbedPane.this.getClientProperty("JideTabbedPane.mouseOverTabIndex");
                        if (property instanceof Integer && getIndex() >= 0 && (Integer) property != getIndex()) {
                            return;
                        }
                    }
                    if (isEnabled()) {
                        g.drawLine(centerX - 3, centerY - 3, centerX + 3, centerY + 3);
                        g.drawLine(centerX - 4, centerY - 3, centerX + 2, centerY + 3);
                        g.drawLine(centerX + 3, centerY - 3, centerX - 3, centerY + 3);
                        g.drawLine(centerX + 2, centerY - 3, centerX - 4, centerY + 3);
                    }
                    else {
                        g.drawLine(centerX - 3, centerY - 3, centerX + 3, centerY + 3);
                        g.drawLine(centerX + 3, centerY - 3, centerX - 3, centerY + 3);
                    }
                    break;
                case BUTTON_EAST:
                    //
                    // |
                    // ||
                    // |||
                    // ||||
                    // ||||*
                    // ||||
                    // |||
                    // ||
                    // |
                    //
                {
                    if (getTabPlacement() == TOP || getTabPlacement() == BOTTOM) {
                        int x = centerX + 2, y = centerY; // start point. mark as * above
                        if (isEnabled()) {
                            g.drawLine(x - 4, y - 4, x - 4, y + 4);
                            g.drawLine(x - 3, y - 3, x - 3, y + 3);
                            g.drawLine(x - 2, y - 2, x - 2, y + 2);
                            g.drawLine(x - 1, y - 1, x - 1, y + 1);
                            g.drawLine(x, y, x, y);
                        }
                        else {
                            g.drawLine(x - 4, y - 4, x, y);
                            g.drawLine(x - 4, y - 4, x - 4, y + 4);
                            g.drawLine(x - 4, y + 4, x, y);
                        }

                    }
                    else {
                        int x = centerX + 3, y = centerY - 2; // start point. mark as * above
                        if (isEnabled()) {
                            g.drawLine(x - 8, y, x, y);
                            g.drawLine(x - 7, y + 1, x - 1, y + 1);
                            g.drawLine(x - 6, y + 2, x - 2, y + 2);
                            g.drawLine(x - 5, y + 3, x - 3, y + 3);
                            g.drawLine(x - 4, y + 4, x - 4, y + 4);
                        }
                        else {
                            g.drawLine(x - 8, y, x, y);
                            g.drawLine(x - 8, y, x - 4, y + 4);
                            g.drawLine(x - 4, y + 4, x, y);
                        }
                    }
                }
                break;
                case BUTTON_WEST: {
                    //
                    //     |
                    //    ||
                    //   |||
                    //  ||||
                    // *||||
                    //  ||||
                    //   |||
                    //    ||
                    //     |
                    //
                    {
                        if (getTabPlacement() == TOP || getTabPlacement() == BOTTOM) {
                            int x = centerX - 3, y = centerY; // start point. mark as * above
                            if (isEnabled()) {
                                g.drawLine(x, y, x, y);
                                g.drawLine(x + 1, y - 1, x + 1, y + 1);
                                g.drawLine(x + 2, y - 2, x + 2, y + 2);
                                g.drawLine(x + 3, y - 3, x + 3, y + 3);
                                g.drawLine(x + 4, y - 4, x + 4, y + 4);
                            }
                            else {
                                g.drawLine(x, y, x + 4, y - 4);
                                g.drawLine(x, y, x + 4, y + 4);
                                g.drawLine(x + 4, y - 4, x + 4, y + 4);
                            }
                        }
                        else {

                            int x = centerX - 5, y = centerY + 3; // start point. mark as * above
                            if (isEnabled()) {
                                g.drawLine(x, y, x + 8, y);
                                g.drawLine(x + 1, y - 1, x + 7, y - 1);
                                g.drawLine(x + 2, y - 2, x + 6, y - 2);
                                g.drawLine(x + 3, y - 3, x + 5, y - 3);
                                g.drawLine(x + 4, y - 4, x + 4, y - 4);
                            }
                            else {
                                g.drawLine(x, y, x + 8, y);
                                g.drawLine(x, y, x + 4, y - 4);
                                g.drawLine(x + 8, y, x + 4, y - 4);
                            }
                        }
                    }
                    break;
                }
                case BUTTON_LIST: {
                    int x = centerX + 2, y = centerY; // start point. mark as
                    // * above
                    g.drawLine(x - 6, y - 4, x - 6, y + 4);
                    g.drawLine(x + 1, y - 4, x + 1, y + 4);
                    g.drawLine(x - 6, y - 4, x + 1, y - 4);
                    g.drawLine(x - 4, y - 2, x - 1, y - 2);
                    g.drawLine(x - 4, y, x - 1, y);
                    g.drawLine(x - 4, y + 2, x - 1, y + 2);
                    g.drawLine(x - 6, y + 4, x + 1, y + 4);
                    break;
                }
            }
        }

        protected Color getForegroundColor() {
            return UIDefaultsLookup.getColor("JideTabbedPane.foreground");
        }

        protected Color getShadowColor() {
            return UIDefaultsLookup.getColor("control");
        }

        protected Color getPressedShadowColor() {
            return UIDefaultsLookup.getColor("controlDkShadow");
        }

        @Override
        public boolean isFocusable() {
            return false;
        }

        @Override
        public void requestFocus() {
        }

        @Override
        public boolean isOpaque() {
            return false;
        }

        public void mouseDragged(MouseEvent e) {
        }

        public void mouseMoved(MouseEvent e) {
            if (!isEnabled()) return;
            setMouseOver(true);
            repaint();
        }

        public void mouseClicked(MouseEvent e) {
            if (!isEnabled()) return;
            setMouseOver(true);
            setMousePressed(false);
        }

        public void mousePressed(MouseEvent e) {
            if (!isEnabled()) return;
            setMousePressed(true);
            repaint();
        }

        public void mouseReleased(MouseEvent e) {
            if (!isEnabled()) return;
            setMousePressed(false);
            setMouseOver(false);
        }

        public void mouseEntered(MouseEvent e) {
            if (!isEnabled()) return;
            setMouseOver(true);
            repaint();
        }

        public void mouseExited(MouseEvent e) {
            if (!isEnabled()) return;
            setMouseOver(false);
            setMousePressed(false);
            repaint();
            JideTabbedPane.this.repaint();
        }

        public int getType() {
            return _type;
        }

        public void setType(int type) {
            _type = type;
        }

        public boolean isMouseOver() {
            return _mouseOver;
        }

        public void setMouseOver(boolean mouseOver) {
            _mouseOver = mouseOver;
        }

        public boolean isMousePressed() {
            return _mousePressed;
        }

        public void setMousePressed(boolean mousePressed) {
            _mousePressed = mousePressed;
        }
    }

    @Override
    public void removeAll() {
        super.removeAll();
        updateUI();
    }
}