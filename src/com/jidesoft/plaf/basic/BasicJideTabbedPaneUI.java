/* @(#)BasicJideTabbedPaneUI.java
 *
 * Copyright 2002 JIDE Software Inc. All rights reserved.
 */

package com.jidesoft.plaf.basic;

import com.jidesoft.plaf.JideTabbedPaneUI;
import com.jidesoft.plaf.UIDefaultsLookup;
import com.jidesoft.popup.JidePopup;
import com.jidesoft.swing.JideSwingUtilities;
import com.jidesoft.swing.JideTabbedPane;
import com.jidesoft.swing.PartialLineBorder;
import com.jidesoft.swing.Sticky;
import com.jidesoft.utils.PortingUtils;
import com.jidesoft.utils.SecurityUtils;
import com.jidesoft.utils.SystemInfo;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.plaf.*;
import javax.swing.plaf.basic.BasicGraphicsUtils;
import javax.swing.plaf.basic.BasicHTML;
import javax.swing.text.View;
import java.awt.*;
import java.awt.dnd.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Vector;

/**
 * A basic L&f implementation of JideTabbedPaneUI
 */
public class BasicJideTabbedPaneUI extends JideTabbedPaneUI implements SwingConstants, DocumentListener {

    // pixels
    protected int _tabRectPadding;// distance from tab rect to icon/text

    protected int _closeButtonMarginHorizon; // when tab is on top or bottom, and each tab has close button, the gap around the close button

    protected int _closeButtonMarginVertical;// when tab is on left or right, and each tab has close button, the gap around the close button

    protected int _textMarginVertical;// tab area and text area gap

    protected int _noIconMargin;// gap around text area when there is no icon

    protected int _iconMargin;// distance from icon to tab rect start x

    protected int _textPadding;// distance from text to tab rect start

    protected int _buttonSize;// scroll button size

    protected int _buttonMargin;// scroll button margin

    protected int _fitStyleBoundSize;// margin for the whole tab area

    protected int _fitStyleFirstTabMargin;// the first tab position

    protected int _fitStyleIconMinWidth;// minimum width to display icon

    protected int _fitStyleTextMinWidth;// minimum width to display text

    protected int _compressedStyleNoIconRectSize;// tab size when there is no icon and tab not selected

    protected int _compressedStyleIconMargin;// margin around icon

    protected int _compressedStyleCloseButtonMarginHorizon;// the close button margin on the left or right when the tab is on the top or bottom

    protected int _compressedStyleCloseButtonMarginVertical;// the close button margin on the top or bottom when the tab is on the left or right

    protected int _fixedStyleRectSize;// tab rect size

    protected int _closeButtonMargin;// margin around close button

    protected int _gripLeftMargin;// left margin

    protected int _closeButtonMarginSize;// margin around the close button

    protected int _closeButtonLeftMargin;// the close button gap when the tab is on the left

    protected int _closeButtonRightMargin;// the close button gap when the tab is on the right


    protected Component _tabLeadingComponent = null;

    protected Component _tabTrailingComponent = null;

    protected JideTabbedPane _tabPane;

    protected Color _tabBackground;

    protected Color _background;

    protected Color _highlight;

    protected Color _lightHighlight;

    protected Color _shadow;

    protected Color _darkShadow;

    protected Color _focus;

    protected Color _inactiveTabForeground;
    protected Color _activeTabForeground;
    protected Color _tabListBackground;

    protected Color _selectedColor;

    protected int _textIconGap;

    protected int _tabRunOverlay;

    protected boolean _showIconOnTab;

    protected boolean _showCloseButtonOnTab;

    protected int _closeButtonAlignment = SwingConstants.TRAILING;

    protected Insets _tabInsets;

    protected Insets _selectedTabPadInsets;

    protected Insets _tabAreaInsets;

    protected boolean _ignoreContentBorderInsetsIfNoTabs;

    // Transient variables (recalculated each time TabbedPane is laid out)

    protected int _tabRuns[] = new int[10];

    protected int _runCount = 0;

    protected int _selectedRun = -1;

    protected Rectangle _rects[] = new Rectangle[0];

    protected int _maxTabHeight;

    protected int _maxTabWidth;

    protected int _gripperWidth = 6;

    protected int _gripperHeight = 6;

    // Listeners

    protected ChangeListener _tabChangeListener;
    protected FocusListener _tabFocusListener;

    protected PropertyChangeListener _propertyChangeListener;

    protected MouseListener _mouseListener;

    protected MouseMotionListener _mousemotionListener;

    protected MouseWheelListener _mouseWheelListener;

    // PENDING(api): See comment for ContainerHandler
    private ContainerListener _containerListener;

    private ComponentListener _componentListener;

    // Private instance data

    private Insets _currentTabInsets = new Insets(0, 0, 0, 0);

    private Insets _currentPadInsets = new Insets(0, 0, 0, 0);

    private Insets _currentTabAreaInsets = new Insets(2, 4, 0, 4);

    private Insets _currentContentBorderInsets = new Insets(3, 0, 0, 0);

    private Component visibleComponent;

    // PENDING(api): See comment for ContainerHandler
    private Vector htmlViews;

    private Hashtable _mnemonicToIndexMap;

    /**
     * InputMap used for mnemonics. Only non-null if the JTabbedPane has mnemonics associated with it. Lazily created in
     * initMnemonics.
     */
    private InputMap _mnemonicInputMap;

    // For use when tabLayoutPolicy = SCROLL_TAB_LAYOUT
    public ScrollableTabSupport _tabScroller;

    /**
     * A rectangle used for general layout calculations in order to avoid constructing many new Rectangles on the fly.
     */
    protected transient Rectangle _calcRect = new Rectangle(0, 0, 0, 0);

    /**
     * Number of tabs. When the count differs, the mnemonics are updated.
     */
    // PENDING: This wouldn't be necessary if JTabbedPane had a better
    // way of notifying listeners when the count changed.
    protected int _tabCount;

    protected TabCloseButton[] _closeButtons;

    // UI creation

    private ThemePainter _painter;

    private Painter _gripperPainter;

    private DropTargetListener _dropListener;

    public DropTarget _dt;

    // the left margin of the first tab according to the style
    public static final int DEFAULT_LEFT_MARGIN = 0;
    public static final int OFFICE2003_LEFT_MARGIN = 18;
    public static final int EXCEL_LEFT_MARGIN = 6;


    protected int _rectSizeExtend = 0;//when the style is eclipse,
    //we should extend the size of the rects for hold the title

    protected Polygon tabRegion = null;

    protected Color _selectColor1 = null;
    protected Color _selectColor2 = null;
    protected Color _selectColor3 = null;
    protected Color _unselectColor1 = null;
    protected Color _unselectColor2 = null;
    protected Color _unselectColor3 = null;

    protected Color _officeTabBorderColor;
    protected Color _defaultTabBorderShadowColor;

    protected boolean _mouseEnter = false;

    protected int _indexMouseOver = -1;

    protected boolean _alwaysShowLineBorder = false;
    protected boolean _showFocusIndicator = false;

    public static final String BUTTON_NAME_CLOSE = "JideTabbedPane.close";
    public static final String BUTTON_NAME_TAB_LIST = "JideTabbedPane.showList";
    public static final String BUTTON_NAME_SCROLL_BACKWARD = "JideTabbedPane.scrollBackward";
    public static final String BUTTON_NAME_SCROLL_FORWARD = "JideTabbedPane.scrollForward";

    public static ComponentUI createUI(JComponent c) {
        return new BasicJideTabbedPaneUI();
    }

    // UI Installation/De-installation

    @Override
    public void installUI(JComponent c) {
        if (c == null) {
            return;
        }

        _tabPane = (JideTabbedPane) c;

        if (_tabPane.isTabShown() && _tabPane.getTabLeadingComponent() != null) {
            _tabLeadingComponent = _tabPane.getTabLeadingComponent();
        }
        if (_tabPane.isTabShown() && _tabPane.getTabTrailingComponent() != null) {
            _tabTrailingComponent = _tabPane.getTabTrailingComponent();
        }

        c.setLayout(createLayoutManager());
        installComponents();
        installDefaults();
        installColorTheme();
        installListeners();
        installKeyboardActions();
    }

    public void installColorTheme() {
        switch (getTabShape()) {
            case JideTabbedPane.SHAPE_EXCEL:
                _selectColor1 = _darkShadow;
                _selectColor2 = _lightHighlight;
                _selectColor3 = _shadow;
                _unselectColor1 = _darkShadow;
                _unselectColor2 = _lightHighlight;
                _unselectColor3 = _shadow;
                break;
            case JideTabbedPane.SHAPE_WINDOWS:
            case JideTabbedPane.SHAPE_WINDOWS_SELECTED:
                _selectColor1 = _lightHighlight;
                _selectColor2 = _shadow;
                _selectColor3 = _defaultTabBorderShadowColor;
                _unselectColor1 = _selectColor1;
                _unselectColor2 = _selectColor2;
                _unselectColor3 = _selectColor3;
                break;
            case JideTabbedPane.SHAPE_VSNET:
                _selectColor1 = _shadow;
                _selectColor2 = _shadow;
                _unselectColor1 = _selectColor1;
                break;
            case JideTabbedPane.SHAPE_ROUNDED_VSNET:
                _selectColor1 = _shadow;
                _selectColor2 = _selectColor1;
                _unselectColor1 = _selectColor1;
                break;
            case JideTabbedPane.SHAPE_FLAT:
                _selectColor1 = _shadow;
                _unselectColor1 = _selectColor1;
                break;
            case JideTabbedPane.SHAPE_ROUNDED_FLAT:
                _selectColor1 = _shadow;
                _selectColor2 = _shadow;
                _unselectColor1 = _selectColor1;
                _unselectColor2 = _selectColor2;
                break;
            case JideTabbedPane.SHAPE_BOX:
                _selectColor1 = _shadow;
                _selectColor2 = _lightHighlight;
                _unselectColor1 = getPainter().getControlShadow();
                _unselectColor2 = _lightHighlight;
                break;
            case JideTabbedPane.SHAPE_OFFICE2003:
            default:
                _selectColor1 = _shadow;
                _selectColor2 = _lightHighlight;
                _unselectColor1 = _shadow;
                _unselectColor2 = null;
                _unselectColor3 = null;
        }

    }

    @Override
    public void uninstallUI(JComponent c) {
        uninstallKeyboardActions();
        uninstallListeners();
        uninstallColorTheme();
        uninstallDefaults();
        uninstallComponents();
        c.setLayout(null);
        _tabTrailingComponent = null;
        _tabLeadingComponent = null;
        _tabPane = null;
    }

    public void uninstallColorTheme() {
        _selectColor1 = null;
        _selectColor2 = null;
        _selectColor3 = null;
        _unselectColor1 = null;
        _unselectColor2 = null;
        _unselectColor3 = null;
    }

    /**
     * Invoked by <code>installUI</code> to create a layout manager object to manage the <code>JTabbedPane</code>.
     *
     * @return a layout manager object
     *
     * @see TabbedPaneLayout
     * @see JTabbedPane#getTabLayoutPolicy
     */
    protected LayoutManager createLayoutManager() {
        if (_tabPane.getTabLayoutPolicy() == JideTabbedPane.SCROLL_TAB_LAYOUT) {
            return new TabbedPaneScrollLayout();
        }
        else { /* WRAP_TAB_LAYOUT */
            return new TabbedPaneLayout();
        }
    }

    /* In an attempt to preserve backward compatibility for programs
     * which have extended VsnetJideTabbedPaneUI to do their own layout, the
     * UI uses the installed layoutManager (and not tabLayoutPolicy) to
     * determine if scrollTabLayout is enabled.
     */
    protected boolean scrollableTabLayoutEnabled() {
        return (_tabPane.getLayout() instanceof TabbedPaneScrollLayout);
    }

    /**
     * Creates and installs any required subcomponents for the JTabbedPane. Invoked by installUI.
     */
    protected void installComponents() {
        if (scrollableTabLayoutEnabled()) {
            if (_tabScroller == null) {
                _tabScroller = new ScrollableTabSupport(_tabPane.getTabPlacement());
                _tabPane.add(_tabScroller.viewport);
                _tabPane.add(_tabScroller.scrollForwardButton);
                _tabPane.add(_tabScroller.scrollBackwardButton);
                _tabPane.add(_tabScroller.listButton);
                _tabPane.add(_tabScroller.closeButton);
                if (_tabLeadingComponent != null) {
                    _tabPane.add(_tabLeadingComponent);
                }
                if (_tabTrailingComponent != null) {
                    _tabPane.add(_tabTrailingComponent);
                }
            }
        }
    }

    /**
     * Removes any installed subcomponents from the JTabbedPane. Invoked by uninstallUI.
     */
    protected void uninstallComponents() {
        if (scrollableTabLayoutEnabled()) {
            _tabPane.remove(_tabScroller.viewport);
            _tabPane.remove(_tabScroller.scrollForwardButton);
            _tabPane.remove(_tabScroller.scrollBackwardButton);
            _tabPane.remove(_tabScroller.listButton);
            _tabPane.remove(_tabScroller.closeButton);
            if (_tabLeadingComponent != null) {
                _tabPane.remove(_tabLeadingComponent);
            }
            if (_tabTrailingComponent != null) {
                _tabPane.remove(_tabTrailingComponent);
            }
            _tabScroller = null;
        }
    }

    protected void installDefaults() {
        _painter = (ThemePainter) UIDefaultsLookup.get("Theme.painter");
        _gripperPainter = (Painter) UIDefaultsLookup.get("JideTabbedPane.gripperPainter");

        LookAndFeel.installColorsAndFont(_tabPane, "JideTabbedPane.background",
                "JideTabbedPane.foreground", "JideTabbedPane.font");
        LookAndFeel.installBorder(_tabPane, "JideTabbedPane.border");
        Font f = _tabPane.getSelectedTabFont();
        if (f == null || f instanceof UIResource) {
            _tabPane.setSelectedTabFont(UIDefaultsLookup.getFont("JideTabbedPane.selectedTabFont"));
        }

        _highlight = UIDefaultsLookup.getColor("JideTabbedPane.light");
        _lightHighlight = UIDefaultsLookup.getColor("JideTabbedPane.highlight");
        _shadow = UIDefaultsLookup.getColor("JideTabbedPane.shadow");
        _darkShadow = UIDefaultsLookup.getColor("JideTabbedPane.darkShadow");
        _focus = UIDefaultsLookup.getColor("TabbedPane.focus");

        if (getTabShape() == JideTabbedPane.SHAPE_BOX) {
            _background = UIDefaultsLookup.getColor("JideTabbedPane.selectedTabBackground");
            _tabBackground = UIDefaultsLookup.getColor("JideTabbedPane.selectedTabBackground");
            _inactiveTabForeground = UIDefaultsLookup.getColor("JideTabbedPane.foreground"); // text is black
            _activeTabForeground = UIDefaultsLookup.getColor("JideTabbedPane.foreground"); // text is black
            _selectedColor = _lightHighlight;
        }
        else {
            _background = UIDefaultsLookup.getColor("JideTabbedPane.background");
            _tabBackground = UIDefaultsLookup.getColor("JideTabbedPane.tabAreaBackground");
            _inactiveTabForeground = UIDefaultsLookup.getColor("JideTabbedPane.unselectedTabTextForeground");
            _activeTabForeground = UIDefaultsLookup.getColor("JideTabbedPane.selectedTabTextForeground");
            _selectedColor = UIDefaultsLookup.getColor("JideTabbedPane.selectedTabBackground");
        }

        _tabListBackground = UIDefaultsLookup.getColor("JideTabbedPane.tabListBackground");

        _textIconGap = UIDefaultsLookup.getInt("JideTabbedPane.textIconGap");
        _tabInsets = UIDefaultsLookup.getInsets("JideTabbedPane.tabInsets");
        _selectedTabPadInsets = UIDefaultsLookup.getInsets("TabbedPane.selectedTabPadInsets");
        if (_selectedTabPadInsets == null) _selectedTabPadInsets = new InsetsUIResource(0, 0, 0, 0);
        _tabAreaInsets = UIDefaultsLookup.getInsets("JideTabbedPane.tabAreaInsets");
        if (_tabAreaInsets == null) _tabAreaInsets = new InsetsUIResource(0, 0, 0, 0);
        Insets insets = _tabPane.getContentBorderInsets();
        if (insets == null || insets instanceof UIResource) {
            _tabPane.setContentBorderInsets(UIDefaultsLookup.getInsets("JideTabbedPane.contentBorderInsets"));
        }

        _ignoreContentBorderInsetsIfNoTabs = UIDefaultsLookup.getBoolean("JideTabbedPane.ignoreContentBorderInsetsIfNoTabs");
        _tabRunOverlay = UIDefaultsLookup.getInt("JideTabbedPane.tabRunOverlay");
        _showIconOnTab = UIDefaultsLookup.getBoolean("JideTabbedPane.showIconOnTab");
        _showCloseButtonOnTab = UIDefaultsLookup.getBoolean("JideTabbedPane.showCloseButtonOnTab");
        _closeButtonAlignment = UIDefaultsLookup.getInt("JideTabbedPane.closeButtonAlignment");
        _gripperWidth = UIDefaultsLookup.getInt("Gripper.size");

        _tabRectPadding = UIDefaultsLookup.getInt("JideTabbedPane.tabRectPadding");
        _closeButtonMarginHorizon = UIDefaultsLookup.getInt("JideTabbedPane.closeButtonMarginHorizonal");
        _closeButtonMarginVertical = UIDefaultsLookup.getInt("JideTabbedPane.closeButtonMarginVertical");
        _textMarginVertical = UIDefaultsLookup.getInt("JideTabbedPane.textMarginVertical");
        _noIconMargin = UIDefaultsLookup.getInt("JideTabbedPane.noIconMargin");
        _iconMargin = UIDefaultsLookup.getInt("JideTabbedPane.iconMargin");
        _textPadding = UIDefaultsLookup.getInt("JideTabbedPane.textPadding");
        _buttonSize = UIDefaultsLookup.getInt("JideTabbedPane.buttonSize");
        _buttonMargin = UIDefaultsLookup.getInt("JideTabbedPane.buttonMargin");
        _fitStyleBoundSize = UIDefaultsLookup.getInt("JideTabbedPane.fitStyleBoundSize");
        _fitStyleFirstTabMargin = UIDefaultsLookup.getInt("JideTabbedPane.fitStyleFirstTabMargin");
        _fitStyleIconMinWidth = UIDefaultsLookup.getInt("JideTabbedPane.fitStyleIconMinWidth");
        _fitStyleTextMinWidth = UIDefaultsLookup.getInt("JideTabbedPane.fitStyleTextMinWidth");
        _compressedStyleNoIconRectSize = UIDefaultsLookup.getInt("JideTabbedPane.compressedStyleNoIconRectSize");
        _compressedStyleIconMargin = UIDefaultsLookup.getInt("JideTabbedPane.compressedStyleIconMargin");
        _compressedStyleCloseButtonMarginHorizon = UIDefaultsLookup.getInt("JideTabbedPane.compressedStyleCloseButtonMarginHorizontal");
        _compressedStyleCloseButtonMarginVertical = UIDefaultsLookup.getInt("JideTabbedPane.compressedStyleCloseButtonMarginVertical");
        _fixedStyleRectSize = UIDefaultsLookup.getInt("JideTabbedPane.fixedStyleRectSize");
        _closeButtonMargin = UIDefaultsLookup.getInt("JideTabbedPane.closeButtonMargin");
        _gripLeftMargin = UIDefaultsLookup.getInt("JideTabbedPane.gripLeftMargin");
        _closeButtonMarginSize = UIDefaultsLookup.getInt("JideTabbedPane.closeButtonMarginSize");
        _closeButtonLeftMargin = UIDefaultsLookup.getInt("JideTabbedPane.closeButtonLeftMargin");
        _closeButtonRightMargin = UIDefaultsLookup.getInt("JideTabbedPane.closeButtonRightMargin");

        _defaultTabBorderShadowColor = UIDefaultsLookup.getColor("JideTabbedPane.defaultTabBorderShadowColor");
        _alwaysShowLineBorder = UIDefaultsLookup.getBoolean("JideTabbedPane.alwaysShowLineBorder");
        _showFocusIndicator = UIDefaultsLookup.getBoolean("JideTabbedPane.showFocusIndicator");
    }

    protected void uninstallDefaults() {
        _painter = null;
        _gripperPainter = null;

        _highlight = null;
        _lightHighlight = null;
        _shadow = null;
        _darkShadow = null;
        _focus = null;
        _inactiveTabForeground = null;
        _selectedColor = null;
        _tabInsets = null;
        _selectedTabPadInsets = null;
        _tabAreaInsets = null;

        _defaultTabBorderShadowColor = null;
    }

    protected void installListeners() {
        if (_propertyChangeListener == null) {
            _propertyChangeListener = createPropertyChangeListener();
            _tabPane.addPropertyChangeListener(_propertyChangeListener);
        }
        if (_tabChangeListener == null) {
            _tabChangeListener = createChangeListener();
            _tabPane.addChangeListener(_tabChangeListener);
        }
        if (_tabFocusListener == null) {
            _tabFocusListener = createFocusListener();
            _tabPane.addFocusListener(_tabFocusListener);
        }

        if (_mouseListener == null) {
            _mouseListener = createMouseListener();
            _tabPane.addMouseListener(_mouseListener);
        }

        if (_mousemotionListener == null) {
            _mousemotionListener = createMouseMotionListener();
            _tabPane.addMouseMotionListener(_mousemotionListener);
        }

        if (_mouseWheelListener == null) {
            _mouseWheelListener = createMouseWheelListener();
            _tabPane.addMouseWheelListener(_mouseWheelListener);
        }

        // PENDING(api) : See comment for ContainerHandler
        if (_containerListener == null) {
            _containerListener = new ContainerHandler();
            _tabPane.addContainerListener(_containerListener);
            if (_tabPane.getTabCount() > 0) {
                htmlViews = createHTMLVector();
            }
        }
        if (_componentListener == null) {
            _componentListener = new ComponentHandler();
            _tabPane.addComponentListener(_componentListener);
        }

        if (!_tabPane.isDragOverDisabled()) {
            if (_dropListener == null) {
                _dropListener = createDropListener();
                _dt = new DropTarget(getTabPanel(), _dropListener);
            }
        }
    }

    protected DropListener createDropListener() {
        return new DropListener();
    }

    protected void uninstallListeners() {
        // PENDING(api): See comment for ContainerHandler
        if (_containerListener != null) {
            _tabPane.removeContainerListener(_containerListener);
            _containerListener = null;
            if (htmlViews != null) {
                htmlViews.removeAllElements();
                htmlViews = null;
            }
        }

        if (_componentListener != null) {
            _tabPane.removeComponentListener(_componentListener);
            _componentListener = null;
        }

        if (_tabChangeListener != null) {
            _tabPane.removeChangeListener(_tabChangeListener);
            _tabChangeListener = null;
        }

        if (_tabFocusListener != null) {
            _tabPane.removeFocusListener(_tabFocusListener);
            _tabFocusListener = null;
        }

        if (_mouseListener != null) {
            _tabPane.removeMouseListener(_mouseListener);
            _mouseListener = null;
        }

        if (_mousemotionListener != null) {
            _tabPane.removeMouseMotionListener(_mousemotionListener);
            _mousemotionListener = null;
        }

        if (_mouseWheelListener != null) {
            _tabPane.removeMouseWheelListener(_mouseWheelListener);
            _mouseWheelListener = null;
        }

        if (_propertyChangeListener != null) {
            _tabPane.removePropertyChangeListener(_propertyChangeListener);
            _propertyChangeListener = null;
        }

        if (_dt != null && _dropListener != null) {
            _dt.removeDropTargetListener(_dropListener);
            _dropListener = null;
            _dt = null;
            getTabPanel().setDropTarget(null);
        }
    }

    protected ChangeListener createChangeListener() {
        return new TabSelectionHandler();
    }

    protected FocusListener createFocusListener() {
        return new TabFocusListener();
    }

    protected PropertyChangeListener createPropertyChangeListener() {
        return new PropertyChangeHandler();
    }

    protected void installKeyboardActions() {
        InputMap km = getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        SwingUtilities.replaceUIInputMap(_tabPane, JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT, km);
        km = getInputMap(JComponent.WHEN_FOCUSED);
        SwingUtilities.replaceUIInputMap(_tabPane, JComponent.WHEN_FOCUSED, km);
        ActionMap am = getActionMap();

        SwingUtilities.replaceUIActionMap(_tabPane, am);

        ensureCloseButtonCreated();

        if (scrollableTabLayoutEnabled()) {
            _tabScroller.scrollForwardButton.setAction(am.get("scrollTabsForwardAction"));
            _tabScroller.scrollBackwardButton.setAction(am.get("scrollTabsBackwardAction"));
            _tabScroller.listButton.setAction(am.get("scrollTabsListAction"));
            Action action = _tabPane.getCloseAction();
            updateButtonFromAction(_tabScroller.closeButton, action);
            _tabScroller.closeButton.setAction(am.get("closeTabAction"));
        }

    }

    InputMap getInputMap(int condition) {
        if (condition == JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT) {
            return (InputMap) UIDefaultsLookup.get("JideTabbedPane.ancestorInputMap");
        }
        else if (condition == JComponent.WHEN_FOCUSED) {
            return (InputMap) UIDefaultsLookup.get("JideTabbedPane.focusInputMap");
        }
        return null;
    }

    ActionMap getActionMap() {
        ActionMap map = (ActionMap) UIDefaultsLookup.get("JideTabbedPane.actionMap");

        if (map == null) {
            map = createActionMap();
            if (map != null) {
                UIManager.getLookAndFeelDefaults().put("JideTabbedPane.actionMap", map);
            }
        }
        return map;
    }

    ActionMap createActionMap() {
        ActionMap map = new ActionMapUIResource();
        map.put("navigateNext", new NextAction());
        map.put("navigatePrevious", new PreviousAction());
        map.put("navigateRight", new RightAction());
        map.put("navigateLeft", new LeftAction());
        map.put("navigateUp", new UpAction());
        map.put("navigateDown", new DownAction());
        map.put("navigatePageUp", new PageUpAction());
        map.put("navigatePageDown", new PageDownAction());
        map.put("requestFocus", new RequestFocusAction());
        map.put("requestFocusForVisibleComponent", new RequestFocusForVisibleAction());
        map.put("setSelectedIndex", new SetSelectedIndexAction());
        map.put("scrollTabsForwardAction", new ScrollTabsForwardAction());
        map.put("scrollTabsBackwardAction", new ScrollTabsBackwardAction());
        map.put("scrollTabsListAction", new ScrollTabsListAction());
        map.put("closeTabAction", new CloseTabAction());
        return map;
    }

    protected void uninstallKeyboardActions() {
        SwingUtilities.replaceUIActionMap(_tabPane, null);
        SwingUtilities.replaceUIInputMap(_tabPane, JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT, null);
        SwingUtilities.replaceUIInputMap(_tabPane, JComponent.WHEN_FOCUSED, null);

        if (_closeButtons != null) {
            for (int i = 0; i < _closeButtons.length; i++) {
                _closeButtons[i] = null;
            }
            _closeButtons = null;
        }
    }

    /**
     * Reloads the mnemonics. This should be invoked when a mnemonic changes, when the title of a mnemonic changes, or
     * when tabs are added/removed.
     */
    protected void updateMnemonics() {
        resetMnemonics();
        for (int counter = _tabPane.getTabCount() - 1; counter >= 0; counter--) {
            int mnemonic = _tabPane.getMnemonicAt(counter);

            if (mnemonic > 0) {
                addMnemonic(counter, mnemonic);
            }
        }
    }

    /**
     * Resets the mnemonics bindings to an empty state.
     */
    private void resetMnemonics() {
        if (_mnemonicToIndexMap != null) {
            _mnemonicToIndexMap.clear();
            _mnemonicInputMap.clear();
        }
    }

    /**
     * Adds the specified mnemonic at the specified index.
     * @param index the index
     * @param mnemonic the mnemonic for the index
     */
    private void addMnemonic(int index, int mnemonic) {
        if (_mnemonicToIndexMap == null) {
            initMnemonics();
        }
        _mnemonicInputMap.put(KeyStroke.getKeyStroke(mnemonic, Event.ALT_MASK), "setSelectedIndex");
        _mnemonicToIndexMap.put(mnemonic, index);
    }

    /**
     * Installs the state needed for mnemonics.
     */
    private void initMnemonics() {
        _mnemonicToIndexMap = new Hashtable();
        _mnemonicInputMap = new InputMapUIResource();
        _mnemonicInputMap.setParent(SwingUtilities.getUIInputMap(_tabPane, JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT));
        SwingUtilities.replaceUIInputMap(_tabPane, JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT, _mnemonicInputMap);
    }

    // Geometry

    @Override
    public Dimension getPreferredSize(JComponent c) {
        // Default to LayoutManager's preferredLayoutSize
        return null;
    }

    @Override
    public Dimension getMinimumSize(JComponent c) {
        // Default to LayoutManager's minimumLayoutSize
        return null;
    }

    @Override
    public Dimension getMaximumSize(JComponent c) {
        // Default to LayoutManager's maximumLayoutSize
        return null;
    }

    // UI Rendering

    @Override
    public void paint(Graphics g, JComponent c) {
        int tc = _tabPane.getTabCount();

        paintBackground(g, c);

        if (tc == 0) {
            return;
        }

        if (_tabCount != tc) {
            _tabCount = tc;
            updateMnemonics();
        }

        int selectedIndex = _tabPane.getSelectedIndex();
        int tabPlacement = _tabPane.getTabPlacement();

        ensureCurrentLayout();

        // Paint tab area
        // If scrollable tabs are enabled, the tab area will be
        // painted by the scrollable tab panel instead.
        //
        if (!scrollableTabLayoutEnabled()) { // WRAP_TAB_LAYOUT
            paintTabArea(g, tabPlacement, selectedIndex, c);
        }

        // Paint content border
//        if (_tabPane.isTabShown())
        paintContentBorder(g, tabPlacement, selectedIndex);
    }

    public void paintBackground(Graphics g, Component c) {
        if (_tabPane.isOpaque()) {
            int width = c.getWidth();
            int height = c.getHeight();
            g.setColor(_background);
            g.fillRect(0, 0, width, height);
        }
    }

    /**
     * Paints the tabs in the tab area. Invoked by paint(). The graphics parameter must be a valid <code>Graphics</code>
     * object.  Tab placement may be either: <code>JTabbedPane.TOP</code>, <code>JTabbedPane.BOTTOM</code>,
     * <code>JTabbedPane.LEFT</code>, or <code>JTabbedPane.RIGHT</code>. The selected index must be a valid tabbed pane
     * tab index (0 to tab count - 1, inclusive) or -1 if no tab is currently selected. The handling of invalid
     * parameters is unspecified.
     *
     * @param g             the graphics object to use for rendering
     * @param tabPlacement  the placement for the tabs within the JTabbedPane
     * @param selectedIndex the tab index of the selected component
     * @param c             the component
     */
    protected void paintTabArea(Graphics g, int tabPlacement, int selectedIndex, Component c) {

        if (!PAINT_TABAREA) {
            return;
        }

        int tabCount = _tabPane.getTabCount();

        Rectangle iconRect = new Rectangle(),
                textRect = new Rectangle();

        Rectangle rect = new Rectangle(0, 0, c.getWidth(), c.getHeight());

        paintTabAreaBackground(g, rect, tabPlacement);

        // Paint tabRuns of tabs from back to front
        for (int i = _runCount - 1; i >= 0; i--) {
            int start = _tabRuns[i];
            int next = _tabRuns[(i == _runCount - 1) ? 0 : i + 1];
            int end = (next != 0 ? next - 1 : tabCount - 1);
            for (int j = start; j <= end; j++) {
                if (_rects[j].intersects(rect) && j != selectedIndex) {
                    paintTab(g, tabPlacement, _rects, j, iconRect, textRect);
                }
            }
        }

        // Paint selected tab if its in the front run
        // since it may overlap other tabs
        if (selectedIndex >= 0 && getRunForTab(tabCount, selectedIndex) == 0) {
            if (_rects[selectedIndex].intersects(rect)) {
                paintTab(g, tabPlacement, _rects, selectedIndex, iconRect, textRect);
            }
        }
    }

    protected void paintTabAreaBackground(Graphics g, Rectangle rect, int tabPlacement) {
        getPainter().paintTabAreaBackground(_tabPane, g, rect,
                tabPlacement == JideTabbedPane.TOP || tabPlacement == JideTabbedPane.BOTTOM ? SwingConstants.HORIZONTAL : SwingConstants.VERTICAL,
                ThemePainter.STATE_DEFAULT);
    }

    protected void paintTab(Graphics g, int tabPlacement,
                            Rectangle[] rects, int tabIndex,
                            Rectangle iconRect, Rectangle textRect) {

        if (!PAINT_TAB) {
            return;
        }

        Rectangle tabRect = rects[tabIndex];

        int selectedIndex = _tabPane.getSelectedIndex();
        boolean isSelected = selectedIndex == tabIndex;
        boolean leftToRight = _tabPane.getComponentOrientation().isLeftToRight();

        paintTabBackground(g, tabPlacement, tabIndex, tabRect.x, tabRect.y,
                tabRect.width, tabRect.height, isSelected);

        Object savedHints = JideSwingUtilities.setupShapeAntialiasing(g);
        paintTabBorder(g, tabPlacement, tabIndex, tabRect.x, tabRect.y,
                tabRect.width, tabRect.height, isSelected);
        JideSwingUtilities.restoreShapeAntialiasing(g, savedHints);

        Icon icon = _tabPane.getIconForTab(tabIndex);

        Rectangle tempTabRect = new Rectangle(tabRect);

        if (_tabPane.isShowGripper()) {
            if (leftToRight) {
                tempTabRect.x += _gripperWidth;
            }
            tempTabRect.width -= _gripperWidth;
            Rectangle gripperRect = new Rectangle(tabRect);
            if (leftToRight) {
                gripperRect.x += _gripLeftMargin;
            }
            else {
                gripperRect.x = tabRect.x + tabRect.width - _gripLeftMargin - _gripperWidth;
            }
            gripperRect.width = _gripperWidth;
            if (_gripperPainter != null) {
                _gripperPainter.paint(_tabPane, g, gripperRect, SwingConstants.HORIZONTAL, isSelected ? ThemePainter.STATE_SELECTED : ThemePainter.STATE_DEFAULT);
            }
            else {
                getPainter().paintGripper(_tabPane, g, gripperRect, SwingConstants.HORIZONTAL, isSelected ? ThemePainter.STATE_SELECTED : ThemePainter.STATE_DEFAULT);
            }
        }

        if (isShowCloseButton() && isShowCloseButtonOnTab() && _tabPane.isTabClosableAt(tabIndex)
                && (!_tabPane.isShowCloseButtonOnSelectedTab() || isSelected)) {
            if (tabPlacement == TOP || tabPlacement == BOTTOM) {
                int buttonWidth = _closeButtons[tabIndex].getPreferredSize().width + _closeButtonLeftMargin + _closeButtonRightMargin;
                if (!(_closeButtonAlignment == SwingConstants.LEADING ^ leftToRight)) {
                    tempTabRect.x += buttonWidth;
                }
                tempTabRect.width -= buttonWidth;
            }
            else {
                int buttonHeight = _closeButtons[tabIndex].getPreferredSize().height + _closeButtonLeftMargin + _closeButtonRightMargin;
                if (_closeButtonAlignment == SwingConstants.LEADING) {
                    tempTabRect.y += buttonHeight;
                    tempTabRect.height -= buttonHeight;
                }
                else {
                    tempTabRect.height -= buttonHeight;
                }
            }
        }

        String title = getCurrentDisplayTitleAt(_tabPane, tabIndex);
        Font font = null;

        if (isSelected && _tabPane.getSelectedTabFont() != null) {
            font = _tabPane.getSelectedTabFont();
        }
        else {
            font = _tabPane.getFont();
        }

        if (isSelected && _tabPane.isBoldActiveTab() && font.getStyle() != Font.BOLD) {
            font = font.deriveFont(Font.BOLD);
        }

        FontMetrics metrics = g.getFontMetrics(font);

        while (title == null || title.length() < 3)
            title += " ";

        layoutLabel(tabPlacement, metrics, tabIndex, title, icon,
                tempTabRect, iconRect, textRect, isSelected);

        if (!_isEditing || (!isSelected))
            paintText(g, tabPlacement, font, metrics, tabIndex, title, textRect, isSelected);

        paintIcon(g, tabPlacement, tabIndex, icon, iconRect, isSelected);

        paintFocusIndicator(g, tabPlacement, rects, tabIndex,
                iconRect, textRect, isSelected);
    }


    /* This method will create and return a polygon shape for the given tab rectangle
     * which has been cropped at the specified crop line with a torn edge visual.
     * e.g. A "File" tab which has cropped been cropped just after the "i":
     *             -------------
     *             |  .....     |
     *             |  .          |
     *             |  ...  .    |
     *             |  .    .   |
     *             |  .    .    |
     *             |  .    .     |
     *             --------------
     *
     * The x, y arrays below define the pattern used to create a "torn" edge
     * segment which is repeated to fill the edge of the tab.
     * For tabs placed on TOP and BOTTOM, this righthand torn edge is created by
     * line segments which are defined by coordinates obtained by
     * subtracting xCropLen[i] from (tab.x + tab.width) and adding yCroplen[i]
     * to (tab.y).
     * For tabs placed on LEFT or RIGHT, the bottom torn edge is created by
     * subtracting xCropLen[i] from (tab.y + tab.height) and adding yCropLen[i]
     * to (tab.x).
     */
    private int xCropLen[] = {1, 1, 0, 0, 1, 1, 2, 2};

    private int yCropLen[] = {0, 3, 3, 6, 6, 9, 9, 12};

    private static final int CROP_SEGMENT = 12;

/*
    private Polygon createCroppedTabClip(int tabPlacement, Rectangle tabRect, int cropline) {
        int rlen = 0;
        int start = 0;
        int end = 0;
        int ostart = 0;

        switch (tabPlacement) {
            case LEFT:
            case RIGHT:
                rlen = tabRect.width;
                start = tabRect.x;
                end = tabRect.x + tabRect.width;
                ostart = tabRect.y;
                break;
            case TOP:
            case BOTTOM:
            default:
                rlen = tabRect.height;
                start = tabRect.y;
                end = tabRect.y + tabRect.height;
                ostart = tabRect.x;
        }
        int rcnt = rlen / CROP_SEGMENT;
        if (rlen % CROP_SEGMENT > 0) {
            rcnt++;
        }
        int npts = 2 + (rcnt << 3);
        int xp[] = new int[npts];
        int yp[] = new int[npts];
        int pcnt = 0;

        xp[pcnt] = ostart;
        yp[pcnt++] = end;
        xp[pcnt] = ostart;
        yp[pcnt++] = start;
        for (int i = 0; i < rcnt; i++) {
            for (int j = 0; j < xCropLen.length; j++) {
                xp[pcnt] = cropline - xCropLen[j];
                yp[pcnt] = start + (i * CROP_SEGMENT) + yCropLen[j];
                if (yp[pcnt] >= end) {
                    yp[pcnt] = end;
                    pcnt++;
                    break;
                }
                pcnt++;
            }
        }
        if (tabPlacement == JideTabbedPane.TOP || tabPlacement == JideTabbedPane.BOTTOM) {
            return new Polygon(xp, yp, pcnt);

        }
        else { // LEFT or RIGHT
            return new Polygon(yp, xp, pcnt);
        }
    }
*/

    /* If tabLayoutPolicy == SCROLL_TAB_LAYOUT, this method will paint an edge
     * indicating the tab is cropped in the viewport display
     */
    private void paintCroppedTabEdge(Graphics g, int tabPlacement, int tabIndex,
                                     boolean isSelected,
                                     int x, int y) {
        switch (tabPlacement) {
            case LEFT:
            case RIGHT:
                int xx = x;
                g.setColor(_shadow);
                while (xx <= x + _rects[tabIndex].width) {
                    for (int i = 0; i < xCropLen.length; i += 2) {
                        g.drawLine(xx + yCropLen[i], y - xCropLen[i],
                                xx + yCropLen[i + 1] - 1, y - xCropLen[i + 1]);
                    }
                    xx += CROP_SEGMENT;
                }
                break;
            case TOP:
            case BOTTOM:
            default:
                int yy = y;
                g.setColor(_shadow);
                while (yy <= y + _rects[tabIndex].height) {
                    for (int i = 0; i < xCropLen.length; i += 2) {
                        g.drawLine(x - xCropLen[i], yy + yCropLen[i],
                                x - xCropLen[i + 1], yy + yCropLen[i + 1] - 1);
                    }
                    yy += CROP_SEGMENT;
                }
        }
    }

    protected void layoutLabel(int tabPlacement,
                               FontMetrics metrics, int tabIndex,
                               String title, Icon icon,
                               Rectangle tabRect, Rectangle iconRect,
                               Rectangle textRect, boolean isSelected) {
        textRect.x = textRect.y = iconRect.x = iconRect.y = 0;

        View v = getTextViewForTab(tabIndex);
        if (v != null) {
            _tabPane.putClientProperty("html", v);
        }

        SwingUtilities.layoutCompoundLabel(_tabPane,
                metrics, title, icon,
                SwingUtilities.CENTER,
                SwingUtilities.CENTER,
                SwingUtilities.CENTER,
                SwingUtilities.TRAILING,
                tabRect,
                iconRect,
                textRect,
                _textIconGap);

        _tabPane.putClientProperty("html", null);

        if (tabPlacement == TOP || tabPlacement == BOTTOM) {
//            iconRect.x = tabRect.x + _iconMargin;
//            textRect.x = (icon != null ? iconRect.x + iconRect.width + _textIconGap : tabRect.x + _textPadding);
//            iconRect.width = Math.min(iconRect.width, tabRect.width - _tabRectPadding);
//            textRect.width = tabRect.width - _tabRectPadding - iconRect.width - (icon != null ? _textIconGap + _iconMargin : _noIconMargin);

            if (getTabResizeMode() == JideTabbedPane.RESIZE_MODE_COMPRESSED
                    && isShowCloseButton() && isShowCloseButtonOnTab()) {
                if (!_tabPane.isShowCloseButtonOnSelectedTab()) {
                    if (!isSelected) {
                        iconRect.width = iconRect.width + _closeButtons[tabIndex].getPreferredSize().width + _closeButtonMarginHorizon;
                        textRect.width = 0;
                    }
                }
            }
        }
        else {// tabplacement is left or right
            iconRect.y = tabRect.y + _iconMargin;
            textRect.y = (icon != null ? iconRect.y + iconRect.height + _textIconGap : tabRect.y + _textPadding);
            iconRect.x = tabRect.x + 2;
            textRect.x = tabRect.x + 2;
            textRect.width = tabRect.width - _textMarginVertical;
            textRect.height = tabRect.height - _tabRectPadding - iconRect.height - (icon != null ? _textIconGap + _iconMargin : _noIconMargin);

            if (getTabResizeMode() == JideTabbedPane.RESIZE_MODE_COMPRESSED
                    && isShowCloseButton() && isShowCloseButtonOnTab()) {
                if (!_tabPane.isShowCloseButtonOnSelectedTab()) {
                    if (!isSelected) {
                        iconRect.height = iconRect.height + _closeButtons[tabIndex].getPreferredSize().height + _closeButtonMarginVertical;
                        textRect.height = 0;
                    }
                }
            }
        }
    }

    protected void paintIcon(Graphics g, int tabPlacement,
                             int tabIndex, Icon icon, Rectangle iconRect,
                             boolean isSelected) {
        if (icon != null && iconRect.width >= icon.getIconWidth()) {
            if (tabPlacement == TOP || tabPlacement == BOTTOM) {
                icon.paintIcon(_tabPane, g, iconRect.x, iconRect.y);
            }
            else {
                if (iconRect.height < _rects[tabIndex].height - _gripperHeight) {
                    icon.paintIcon(_tabPane, g, iconRect.x, iconRect.y);
                }
            }
        }
    }

    protected void paintText(Graphics g, int tabPlacement,
                             Font font, FontMetrics metrics, int tabIndex,
                             String title, Rectangle textRect,
                             boolean isSelected) {
        Graphics2D g2d = (Graphics2D) g.create();
        if (isSelected && _tabPane.isBoldActiveTab()) {
            g2d.setFont(font.deriveFont(Font.BOLD));
        }
        else {
            g2d.setFont(font);
        }

        String actualText = title;

        if (tabPlacement == JideTabbedPane.TOP || tabPlacement == JideTabbedPane.BOTTOM) {
            if (textRect.width <= 0)
                return;

            while (SwingUtilities.computeStringWidth(metrics, actualText) > textRect.width) {
                actualText = actualText.substring(0, actualText.length() - 1);
            }
            if (!actualText.equals(title)) {
                if (actualText.length() >= 2)
                    actualText = actualText.substring(0, actualText.length() - 2) + "..";
                else
                    actualText = "";
            }
        }
        else {
            if (textRect.height <= 0)
                return;

            while (SwingUtilities.computeStringWidth(metrics, actualText) > textRect.height) {
                actualText = actualText.substring(0, actualText.length() - 1);
            }
            if (!actualText.equals(title)) {
                if (actualText.length() >= 2)
                    actualText = actualText.substring(0, actualText.length() - 2) + "..";
                else
                    actualText = "";
            }
        }

        View v = getTextViewForTab(tabIndex);
        if (v != null) {
            // html
            v.paint(g2d, textRect);
        }
        else {
            // plain text
            int mnemIndex = _tabPane.getDisplayedMnemonicIndexAt(tabIndex);
            JideTabbedPane.ColorProvider colorProvider = _tabPane.getTabColorProvider();
            if (_tabPane.isEnabled() && _tabPane.isEnabledAt(tabIndex)) {
                if (colorProvider != null) {
                    g2d.setColor(colorProvider.getForegroudAt(tabIndex));
                }
                else {
                    Color color = _tabPane.getForegroundAt(tabIndex);
                    if (isSelected && showFocusIndicator()) {
                        if (!(color instanceof ColorUIResource)) {
                            g2d.setColor(color);
                        }
                        else {
                            g2d.setColor(_activeTabForeground);
                        }
                    }
                    else {
                        if (!(color instanceof ColorUIResource)) {
                            g2d.setColor(color);
                        }
                        else {
                            g2d.setColor(_inactiveTabForeground);
                        }
                    }
                }

                if (tabPlacement == TOP || tabPlacement == BOTTOM) {
                    JideSwingUtilities.drawStringUnderlineCharAt(_tabPane, g2d, actualText, mnemIndex, textRect.x, textRect.y + metrics.getAscent());
                }
                else {// draw string from top to bottom
                    AffineTransform old = g2d.getTransform();
                    g2d.translate(textRect.x, textRect.y);
                    if (tabPlacement == RIGHT) {
                        g2d.rotate(Math.PI / 2);
                        g2d.translate(0, -textRect.width);
                    }
                    else {
                        g2d.rotate(-Math.PI / 2);
                        g2d.translate(-textRect.height + metrics.getHeight() / 2 + _rectSizeExtend, 0); // no idea why i need 7 here
                    }
                    JideSwingUtilities.drawStringUnderlineCharAt(_tabPane, g2d, actualText, mnemIndex, 0,
                            ((textRect.width - metrics.getHeight()) / 2) + metrics.getAscent());
                    g2d.setTransform(old);
                }
            }
            else { // tab disabled
                if (tabPlacement == TOP || tabPlacement == BOTTOM) {
                    g2d.setColor(_tabPane.getBackgroundAt(tabIndex).brighter());
                    JideSwingUtilities.drawStringUnderlineCharAt(_tabPane, g2d, actualText, mnemIndex, textRect.x, textRect.y + metrics.getAscent());
                    g2d.setColor(_tabPane.getBackgroundAt(tabIndex).darker());
                    JideSwingUtilities.drawStringUnderlineCharAt(_tabPane, g2d, actualText, mnemIndex, textRect.x - 1, textRect.y + metrics.getAscent() - 1);
                }
                else {// draw string from top to bottom
                    AffineTransform old = g2d.getTransform();
                    g2d.translate(textRect.x, textRect.y);
                    if (tabPlacement == RIGHT) {
                        g2d.rotate(Math.PI / 2);
                        g2d.translate(0, -textRect.width);
                    }
                    else {
                        g2d.rotate(-Math.PI / 2);
                        g2d.translate(-textRect.height + metrics.getHeight() / 2 + _rectSizeExtend, 0); // no idea why i need 7 here
                    }
                    g2d.setColor(_tabPane.getBackgroundAt(tabIndex).brighter());
                    JideSwingUtilities.drawStringUnderlineCharAt(_tabPane, g2d, actualText, mnemIndex,
                            0, ((textRect.width - metrics.getHeight()) / 2) + metrics.getAscent());
                    g2d.setColor(_tabPane.getBackgroundAt(tabIndex).darker());
                    JideSwingUtilities.drawStringUnderlineCharAt(_tabPane, g2d, actualText, mnemIndex,
                            tabPlacement == RIGHT ? -1 : 1, ((textRect.width - metrics.getHeight()) / 2) + metrics.getAscent() - 1);
                    g2d.setTransform(old);
                }
            }
        }
        g2d.dispose();
    }

    /**
     * this function draws the border around each tab note that this function does now draw the background of the tab.
     * that is done elsewhere
     */
    protected void paintTabBorder(Graphics g, int tabPlacement, int tabIndex, int x, int y, int w, int h, boolean isSelected) {
        if (!PAINT_TAB_BORDER) {
            return;
        }

        switch (getTabShape()) {
            case JideTabbedPane.SHAPE_BOX:
                paintBoxTabBorder(g, tabPlacement, tabIndex, x, y, w, h, isSelected);
                break;
            case JideTabbedPane.SHAPE_EXCEL:
                paintExcelTabBorder(g, tabPlacement, tabIndex, x, y, w, h, isSelected);
                break;
            case JideTabbedPane.SHAPE_WINDOWS:
                paintWindowsTabBorder(g, tabPlacement, tabIndex, x, y, w, h, isSelected);
                break;
            case JideTabbedPane.SHAPE_WINDOWS_SELECTED:
                if (isSelected) {
                    paintWindowsTabBorder(g, tabPlacement, tabIndex, x, y, w, h, isSelected);
                }
                break;
            case JideTabbedPane.SHAPE_VSNET:
                paintVsnetTabBorder(g, tabPlacement, tabIndex, x, y, w, h, isSelected);
                break;
            case JideTabbedPane.SHAPE_ROUNDED_VSNET:
                paintRoundedVsnetTabBorder(g, tabPlacement, tabIndex, x, y, w, h, isSelected);
                break;
            case JideTabbedPane.SHAPE_FLAT:
                paintFlatTabBorder(g, tabPlacement, tabIndex, x, y, w, h, isSelected);
                break;
            case JideTabbedPane.SHAPE_ROUNDED_FLAT:
                paintRoundedFlatTabBorder(g, tabPlacement, tabIndex, x, y, w, h, isSelected);
                break;
            case JideTabbedPane.SHAPE_OFFICE2003:
            default:
                paintOffice2003TabBorder(g, tabPlacement, tabIndex, x, y, w, h, isSelected);
        }

        int tabShape = getTabShape();
        if (tabShape == JideTabbedPane.SHAPE_WINDOWS) {
            if (_mouseEnter && _tabPane.getColorTheme() == JideTabbedPane.COLOR_THEME_WINXP
                    && tabIndex == _indexMouseOver && !isSelected && _tabPane.isEnabledAt(_indexMouseOver)) {
                paintTabBorderMouseOver(g, tabPlacement, tabIndex, x, y, w, h, isSelected);
            }
        }
        else if (tabShape == JideTabbedPane.SHAPE_WINDOWS_SELECTED) {
            if (_mouseEnter && tabIndex == _indexMouseOver && !isSelected && _tabPane.isEnabledAt(_indexMouseOver)) {
                paintTabBorderMouseOver(g, tabPlacement, tabIndex, x, y, w, h, isSelected);
            }
        }
    }

    protected void paintOffice2003TabBorder(Graphics g, int tabPlacement, int tabIndex,
                                            int x, int y, int w, int h, boolean isSelected) {
        boolean leftToRight = _tabPane.getComponentOrientation().isLeftToRight();

        switch (tabPlacement) {
            case LEFT:// when the tab on the left
                y += 2;
                if (isSelected) {// the tab is selected
                    g.setColor(_selectColor1);

                    g.drawLine(x, y + 3, x, y + h - 5);// left
                    g.drawLine(x + 1, y + h - 4, x + 1, y + h - 4);// bottom
                    // arc
                    g.drawLine(x + 2, y + h - 3, x + w - 1, y + h - 3);// bottom

                    g.drawLine(x + 1, y + 2, x + 1, y + 1);// top arc
                    g.drawLine(x + 2, y, x + 2, y - 1);

                    for (int i = 0; i < w - 4; i++) {// top
                        g.drawLine(x + 3 + i, y - 2 - i, x + 3 + i, y - 2 - i);
                    }

                    g.drawLine(x + w - 1, y - w + 1, x + w - 1, y - w + 2);

                    g.setColor(_selectColor2);

                    g.drawLine(x + 1, y + 3, x + 1, y + h - 5);// left
                    g.drawLine(x + 2, y + h - 4, x + w - 1, y + h - 4);// bottom

                    g.drawLine(x + 2, y + 2, x + 2, y + 1);// top arc
                    g.drawLine(x + 3, y, x + 3, y - 1);

                    for (int i = 0; i < w - 4; i++) {// top
                        g.drawLine(x + 4 + i, y - 2 - i, x + 4 + i, y - 2 - i);
                    }

                }
                else {
                    if (tabIndex == 0) {
                        g.setColor(_unselectColor1);

                        g.drawLine(x, y + 3, x, y + h - 5);// left
                        g.drawLine(x + 1, y + h - 4, x + 1, y + h - 4);// bottom
                        // arc
                        g.drawLine(x + 2, y + h - 3, x + w - 1, y + h - 3);// bottom

                        g.drawLine(x + 1, y + 2, x + 1, y + 1);// top arc
                        g.drawLine(x + 2, y, x + 2, y - 1);

                        for (int i = 0; i < w - 4; i++) {// top
                            g.drawLine(x + 3 + i, y - 2 - i, x + 3 + i, y - 2 - i);
                        }

                        g.drawLine(x + w - 1, y - w + 1, x + w - 1, y - w + 2);

                        if (_unselectColor2 != null) {
                            g.setColor(_unselectColor2);

                            g.drawLine(x + 1, y + 3, x + 1, y + h - 6);// left

                            g.drawLine(x + 2, y + 2, x + 2, y + 1);// top arc
                            g.drawLine(x + 3, y, x + 3, y - 1);

                            for (int i = 0; i < w - 4; i++) {// top
                                g.drawLine(x + 4 + i, y - 2 - i, x + 4 + i, y - 2 - i);
                            }

                            g.setColor(getPainter().getControlDk());
                        }

                        if (_unselectColor3 != null) {
                            g.setColor(_unselectColor3);

                            g.drawLine(x + 2, y + h - 4, x + w - 1, y + h - 4);// bottom
                            g.drawLine(x + 1, y + h - 5, x + 1, y + h - 5);// bottom
                            // arc
                        }
                    }
                    else {
                        g.setColor(_unselectColor1);

                        g.drawLine(x, y + 3, x, y + h - 5);// left
                        g.drawLine(x + 1, y + h - 4, x + 1, y + h - 4);// bottom
                        // arc
                        g.drawLine(x + 2, y + h - 3, x + w - 1, y + h - 3);// bottom

                        g.drawLine(x + 1, y + 2, x + 1, y + 1);// top arc
                        g.drawLine(x + 2, y, x + 2, y - 1);
                        g.drawLine(x + 3, y - 2, x + 3, y - 2);

                        if (_unselectColor2 != null) {
                            g.setColor(_unselectColor2);

                            g.drawLine(x + 1, y + 3, x + 1, y + h - 6);// left

                            g.drawLine(x + 2, y + 2, x + 2, y + 1);// top arc
                            g.drawLine(x + 3, y, x + 3, y - 1);
                            g.drawLine(x + 4, y - 2, x + 4, y - 2);

                        }

                        if (_unselectColor3 != null) {
                            g.setColor(_unselectColor3);

                            g.drawLine(x + 2, y + h - 4, x + w - 1, y + h - 4);// bottom
                            g.drawLine(x + 1, y + h - 5, x + 1, y + h - 5);
                        }
                    }
                }
                break;
            case RIGHT:
                if (isSelected) {// the tab is selected
                    g.setColor(_selectColor1);

                    g.drawLine(x + w - 1, y + 5, x + w - 1, y + h - 3);// right
                    g.drawLine(x + w - 2, y + h - 2, x + w - 2, y + h - 2);// bottom
                    // arc
                    g.drawLine(x + w - 3, y + h - 1, x, y + h - 1);// bottom

                    g.drawLine(x + w - 2, y + 4, x + w - 2, y + 3);// top arc
                    g.drawLine(x + w - 3, y + 2, x + w - 3, y + 1);// top arc

                    for (int i = 0; i < w - 4; i++) {// top
                        g.drawLine(x + w - 4 - i, y - i, x + w - 4 - i, y - i);
                    }

                    g.drawLine(x, y - w + 3, x, y - w + 4);

                    g.setColor(_selectColor2);

                    g.drawLine(x + w - 2, y + 5, x + w - 2, y + h - 3);// right
                    g.drawLine(x + w - 3, y + h - 2, x, y + h - 2);// bottom

                    g.drawLine(x + w - 3, y + 4, x + w - 3, y + 3);// top arc
                    g.drawLine(x + w - 4, y + 2, x + w - 4, y + 1);

                    for (int i = 0; i < w - 4; i++) {// top
                        g.drawLine(x + w - 5 - i, y - i, x + w - 5 - i, y - i);
                    }
                }
                else {
                    if (tabIndex == 0) {
                        g.setColor(_unselectColor1);

                        g.drawLine(x + w - 1, y + 5, x + w - 1, y + h - 3);// right
                        g.drawLine(x + w - 2, y + h - 2, x + w - 2, y + h - 2);// bottom
                        // arc
                        g.drawLine(x + w - 3, y + h - 1, x, y + h - 1);// bottom

                        g.drawLine(x + w - 2, y + 4, x + w - 2, y + 3);// top arc
                        g.drawLine(x + w - 3, y + 2, x + w - 3, y + 1);// top arc

                        for (int i = 0; i < w - 4; i++) {// top
                            g.drawLine(x + w - 4 - i, y - i, x + w - 4 - i, y - i);
                        }

                        g.drawLine(x, y - w + 3, x, y - w + 4);

                        if (_unselectColor2 != null) {
                            g.setColor(_unselectColor2);

                            g.drawLine(x + w - 2, y + 5, x + w - 2, y + h - 4);// right

                            g.drawLine(x + w - 3, y + 4, x + w - 3, y + 3);// top
                            // arc
                            g.drawLine(x + w - 4, y + 2, x + w - 4, y + 1);

                            for (int i = 0; i < w - 4; i++) {// top
                                g.drawLine(x + w - 5 - i, y - i, x + w - 5 - i, y
                                        - i);
                            }
                        }

                        if (_unselectColor3 != null) {
                            g.setColor(_unselectColor3);

                            g.drawLine(x + w - 2, y + h - 3, x + w - 2, y + h - 3);// bottom
                            // arc
                            g.drawLine(x + w - 3, y + h - 2, x, y + h - 2);// bottom

                        }
                    }
                    else {
                        g.setColor(_unselectColor1);

                        g.drawLine(x + w - 1, y + 5, x + w - 1, y + h - 3);// right
                        g.drawLine(x + w - 2, y + h - 2, x + w - 2, y + h - 2);// bottom
                        // arc
                        g.drawLine(x + w - 3, y + h - 1, x, y + h - 1);// bottom

                        g.drawLine(x + w - 2, y + 4, x + w - 2, y + 3);// top arc
                        g.drawLine(x + w - 3, y + 2, x + w - 3, y + 1);// top arc
                        g.drawLine(x + w - 4, y, x + w - 4, y);// top arc

                        if (_unselectColor2 != null) {
                            g.setColor(_unselectColor2);

                            g.drawLine(x + w - 2, y + 5, x + w - 2, y + h - 4);// right

                            g.drawLine(x + w - 3, y + 4, x + w - 3, y + 3);// top
                            // arc
                            g.drawLine(x + w - 4, y + 2, x + w - 4, y + 1);
                            g.drawLine(x + w - 5, y, x + w - 5, y);
                        }

                        if (_unselectColor3 != null) {
                            g.setColor(_unselectColor3);

                            g.drawLine(x + w - 2, y + h - 3, x + w - 2, y + h - 3);// bottom
                            // arc
                            g.drawLine(x + w - 3, y + h - 2, x, y + h - 2);// bottom
                        }
                    }
                }
                break;
            case BOTTOM:
                if (leftToRight) {
                    if (isSelected) {// the tab is selected
                        g.setColor(_selectColor1);

                        g.drawLine(x + w - 1, y + h - 3, x + w - 1, y);// right

                        g.drawLine(x + w - 2, y + h - 2, x + w - 2, y + h - 2);// right
                        // arc

                        g.drawLine(x + 5, y + h - 1, x + w - 3, y + h - 1);// bottom

                        g.drawLine(x + 3, y + h - 2, x + 4, y + h - 2);// left arc
                        g.drawLine(x + 1, y + h - 3, x + 2, y + h - 3);// left arc
                        g.drawLine(x, y + h - 4, x, y + h - 4);// left arc

                        // left
                        for (int i = 3; i < h - 2; i++) {
                            g.drawLine(x + 2 - i, y + h - 2 - i, x + 2 - i, y + h
                                    - 2 - i);
                        }

                        g.drawLine(x - h + 3, y, x - h + 4, y);

                        g.setColor(_selectColor2);

                        g.drawLine(x + 5, y + h - 2, x + w - 3, y + h - 2);// bottom

                        g.drawLine(x + w - 2, y, x + w - 2, y + h - 3);// right

                        g.drawLine(x + 3, y + h - 3, x + 4, y + h - 3);// left arc
                        g.drawLine(x + 1, y + h - 4, x + 2, y + h - 4);// left arc
                        g.drawLine(x, y + h - 5, x, y + h - 5);// left arc

                        for (int i = 3; i < h - 2; i++) {// left
                            g.drawLine(x + 2 - i, y + h - 3 - i, x + 2 - i, y + h
                                    - 3 - i);
                        }

                    }
                    else {
                        if (tabIndex == 0) {
                            g.setColor(_unselectColor1);

                            g.drawLine(x + w - 1, y + h - 3, x + w - 1, y);// right

                            g.drawLine(x + w - 2, y + h - 2, x + w - 2, y + h - 2);// right
                            // arc

                            g.drawLine(x + 5, y + h - 1, x + w - 3, y + h - 1);// bottom

                            g.drawLine(x + 3, y + h - 2, x + 4, y + h - 2);// left arc
                            g.drawLine(x + 1, y + h - 3, x + 2, y + h - 3);// left arc
                            g.drawLine(x, y + h - 4, x, y + h - 4);// left arc

                            // left
                            for (int i = 3; i < h - 2; i++) {
                                g.drawLine(x + 2 - i, y + h - 2 - i, x + 2 - i, y + h
                                        - 2 - i);
                            }

                            g.drawLine(x - h + 3, y, x - h + 4, y);

                            if (_unselectColor2 != null) {
                                g.setColor(_unselectColor2);

                                g.drawLine(x + 3, y + h - 3, x + 4, y + h - 3);// left
                                // arc
                                g.drawLine(x + 1, y + h - 4, x + 2, y + h - 4);// left
                                // arc
                                g.drawLine(x, y + h - 5, x, y + h - 5);// left arc

                                // left
                                for (int i = 3; i < h - 2; i++) {
                                    g.drawLine(x + 2 - i, y + h - 3 - i, x + 2 - i, y
                                            + h - 3 - i);
                                }

                                g.drawLine(x + 5, y + h - 2, x + w - 4, y + h - 2);// bottom

                            }

                            if (_unselectColor3 != null) {
                                g.setColor(_unselectColor3);

                                g.drawLine(x + w - 3, y + h - 2, x + w - 3, y + h - 2);
                                g.drawLine(x + w - 2, y + h - 3, x + w - 2, y);// right
                            }
                        }
                        else {
                            g.setColor(_unselectColor1);

                            g.drawLine(x + 5, y + h - 1, x + w - 3, y + h - 1);// bottom

                            g.drawLine(x + w - 1, y + h - 3, x + w - 1, y);// right

                            g.drawLine(x + w - 2, y + h - 2, x + w - 2, y + h - 2);// right
                            // arc

                            g.drawLine(x + 3, y + h - 2, x + 4, y + h - 2);// left arc
                            g.drawLine(x + 1, y + h - 3, x + 2, y + h - 3);// left arc
                            g.drawLine(x, y + h - 4, x, y + h - 4);// left arc

                            if (_unselectColor2 != null) {
                                g.setColor(_unselectColor2);

                                g.drawLine(x + 3, y + h - 3, x + 4, y + h - 3);// left
                                // arc
                                g.drawLine(x + 1, y + h - 4, x + 2, y + h - 4);// left
                                // arc
                                g.drawLine(x, y + h - 5, x, y + h - 5);// left arc

                                g.drawLine(x + 5, y + h - 2, x + w - 4, y + h - 2);// bottom
                            }

                            if (_unselectColor3 != null) {
                                g.setColor(_unselectColor3);

                                g.drawLine(x + w - 3, y + h - 2, x + w - 3, y + h - 2);
                                g.drawLine(x + w - 2, y + h - 3, x + w - 2, y);// right
                            }
                        }
                    }
                }
                else {
                    if (isSelected) {// the tab is selected
                        g.setColor(_selectColor1);

                        g.drawLine(x, y + h - 3, x, y);// left

                        g.drawLine(x + 1, y + h - 2, x + 1, y + h - 2);// left
                        // arc

                        g.drawLine(x + w - 6, y + h - 1, x + 2, y + h - 1);// bottom

                        g.drawLine(x + w - 4, y + h - 2, x + w - 5, y + h - 2);// right arc
                        g.drawLine(x + w - 2, y + h - 3, x + w - 3, y + h - 3);// right arc
                        g.drawLine(x + w - 1, y + h - 4, x + w - 1, y + h - 4);// right arc

                        // right
                        for (int i = 3; i < h - 2; i++) {
                            g.drawLine(x + w - 3 + i, y + h - 2 - i, x + w - 3 + i, y + h - 2 - i);
                        }

                        g.drawLine(x + w - 4 + h, y, x + w - 5 + h, y);

                        g.setColor(_selectColor2);

                        g.drawLine(x + w - 6, y + h - 2, x + 2, y + h - 2);// bottom

                        g.drawLine(x + 1, y, x + 1, y + h - 3);// left

                        g.drawLine(x + w - 4, y + h - 3, x + w - 5, y + h - 3);// right arc
                        g.drawLine(x + w - 2, y + h - 4, x + w - 3, y + h - 4);// right arc
                        g.drawLine(x + w - 1, y + h - 5, x + w - 1, y + h - 5);// right arc

                        for (int i = 3; i < h - 2; i++) {// right
                            g.drawLine(x + w - 3 + i, y + h - 3 - i, x + w - 3 + i, y + h - 3 - i);
                        }
                    }
                    else {
                        if (tabIndex == 0) {
                            g.setColor(_unselectColor1);

                            g.drawLine(x, y + h - 3, x, y);// left

                            g.drawLine(x + 1, y + h - 2, x + 1, y + h - 2);// left
                            // arc

                            g.drawLine(x + w - 6, y + h - 1, x + 2, y + h - 1);// bottom

                            g.drawLine(x + w - 4, y + h - 2, x + w - 5, y + h - 2);// right arc
                            g.drawLine(x + w - 2, y + h - 3, x + w - 3, y + h - 3);// right arc
                            g.drawLine(x + w - 1, y + h - 4, x + w - 1, y + h - 4);// right arc

                            // right
                            for (int i = 3; i < h - 2; i++) {
                                g.drawLine(x + w - 3 + i, y + h - 2 - i, x + w - 3 + i, y + h - 2 - i);
                            }

                            g.drawLine(x + w - 4 + h, y, x + w -5 + h, y);

                            if (_unselectColor2 != null) {
                                g.setColor(_unselectColor2);

                                g.drawLine(x + w - 4, y + h - 3, x + w - 5, y + h - 3);// right
                                // arc
                                g.drawLine(x + w - 2, y + h - 4, x + w - 3, y + h - 4);// right
                                // arc
                                g.drawLine(x + w - 1, y + h - 5, x + w - 1, y + h - 5);// right arc

                                // right
                                for (int i = 3; i < h - 2; i++) {
                                    g.drawLine(x + w - 3 + i, y + h - 3 - i, x + w - 3 + i, y + h - 3 - i);
                                }

                                g.drawLine(x + w - 6, y + h - 2, x + 3, y + h - 2);// bottom
                            }

                            if (_unselectColor3 != null) {
                                g.setColor(_unselectColor3);

                                g.drawLine(x + 2, y + h - 2, x + 2, y + h - 2);
                                g.drawLine(x + 1, y + h - 3, x + 1, y);// left
                            }
                        }
                        else {
                            g.setColor(_unselectColor1);

                            g.drawLine(x + w - 6, y + h - 1, x + 2, y + h - 1);// bottom

                            g.drawLine(x, y + h - 3, x, y);// left

                            g.drawLine(x + 1, y + h - 2, x + 1, y + h - 2);// left
                            // arc

                            g.drawLine(x + w - 4, y + h - 2, x + w - 5, y + h - 2);// right arc
                            g.drawLine(x + w - 2, y + h - 3, x + w - 3, y + h - 3);// right arc
                            g.drawLine(x + w - 1, y + h - 4, x + w - 1, y + h - 4);// right arc

                            if (_unselectColor2 != null) {
                                g.setColor(_unselectColor2);

                                g.drawLine(x + w - 4, y + h - 3, x + w - 5, y + h - 3);// right
                                // arc
                                g.drawLine(x + w - 2, y + h - 4, x + w - 3, y + h - 4);// right
                                // arc
                                g.drawLine(x + w - 1, y + h - 5, x + w -1, y + h - 5);// right arc

                                g.drawLine(x + w - 6, y + h - 2, x + 3, y + h - 2);// bottom
                            }

                            if (_unselectColor3 != null) {
                                g.setColor(_unselectColor3);

                                g.drawLine(x + 2, y + h - 2, x + 2, y + h - 2);
                                g.drawLine(x + 1, y + h - 3, x + 1, y);// left
                            }
                        }
                    }
                }
                break;
            case TOP:
            default:
                if (leftToRight) {
                    if (isSelected) {// the tab is selected
                        g.setColor(_selectColor1);

                        g.drawLine(x + 3, y + 1, x + 4, y + 1);// left arc
                        g.drawLine(x + 1, y + 2, x + 2, y + 2);// left arc
                        g.drawLine(x, y + 3, x, y + 3);

                        g.drawLine(x + 5, y, x + w - 3, y);// top

                        g.drawLine(x + w - 2, y + 1, x + w - 2, y + 1);// right arc

                        g.drawLine(x + w - 1, y + 2, x + w - 1, y + h - 1);// right

                        // left
                        for (int i = 3; i < h - 2; i++) {
                            g.drawLine(x + 2 - i, y + 1 + i, x + 2 - i, y + 1 + i);
                        }
                        g.drawLine(x - h + 3, y + h - 1, x - h + 4, y + h - 1);

                        g.setColor(_selectColor2);

                        g.drawLine(x + 3, y + 2, x + 4, y + 2);// left arc
                        g.drawLine(x + 1, y + 3, x + 2, y + 3);// left arc
                        g.drawLine(x, y + 4, x, y + 4);

                        g.drawLine(x + 5, y + 1, x + w - 3, y + 1);// top

                        g.drawLine(x + w - 2, y + 2, x + w - 2, y + h - 1);// right

                        // left
                        for (int i = 3; i < h - 2; i++) {
                            g.drawLine(x + 2 - i, y + 2 + i, x + 2 - i, y + 2 + i);
                        }
                    }
                    else {
                        if (tabIndex == 0) {
                            g.setColor(_unselectColor1);

                            g.drawLine(x + 3, y + 1, x + 4, y + 1);// left arc
                            g.drawLine(x + 1, y + 2, x + 2, y + 2);// left arc
                            g.drawLine(x, y + 3, x, y + 3);

                            g.drawLine(x + 5, y, x + w - 3, y);// top

                            g.drawLine(x + w - 2, y + 1, x + w - 2, y + 1);// right arc

                            g.drawLine(x + w - 1, y + 2, x + w - 1, y + h - 1);// right

                            // left
                            for (int i = 3; i < h - 2; i++) {
                                g.drawLine(x + 2 - i, y + 1 + i, x + 2 - i, y + 1 + i);
                            }
                            g.drawLine(x - h + 3, y + h - 1, x - h + 4, y + h - 1);

                            if (_unselectColor2 != null) {
                                g.setColor(_unselectColor2);

                                g.drawLine(x + 3, y + 2, x + 4, y + 2);// left arc
                                g.drawLine(x + 1, y + 3, x + 2, y + 3);// left arc
                                g.drawLine(x, y + 4, x, y + 4);

                                // left
                                for (int i = 3; i < h - 2; i++) {
                                    g.drawLine(x + 2 - i, y + 2 + i, x + 2 - i, y + 2
                                            + i);
                                }

                                g.drawLine(x + 5, y + 1, x + w - 4, y + 1);// top
                            }

                            if (_unselectColor3 != null) {
                                g.setColor(_unselectColor3);

                                g.drawLine(x + w - 3, y + 1, x + w - 3, y + 1);
                                g.drawLine(x + w - 2, y + 2, x + w - 2, y + h - 1);// right
                            }
                        }
                        else {
                            g.setColor(_unselectColor1);

                            g.drawLine(x + 3, y + 1, x + 4, y + 1);// left arc
                            g.drawLine(x + 1, y + 2, x + 2, y + 2);// left arc
                            g.drawLine(x, y + 3, x, y + 3);

                            g.drawLine(x + 5, y, x + w - 3, y);// top

                            g.drawLine(x + w - 2, y + 1, x + w - 2, y + 1);// right arc

                            g.drawLine(x + w - 1, y + 2, x + w - 1, y + h - 1);// right

                            if (_unselectColor2 != null) {
                                g.setColor(_unselectColor2);

                                g.drawLine(x + 3, y + 2, x + 4, y + 2);// left arc
                                g.drawLine(x + 1, y + 3, x + 2, y + 3);// left arc
                                g.drawLine(x, y + 4, x, y + 4);

                                g.drawLine(x + 5, y + 1, x + w - 4, y + 1);// top
                            }

                            if (_unselectColor3 != null) {
                                g.setColor(_unselectColor3);

                                g.drawLine(x + w - 3, y + 1, x + w - 3, y + 1);
                                g.drawLine(x + w - 2, y + 2, x + w - 2, y + h - 1);// right
                            }
                        }
                    }
                }
                else {
                    if (isSelected) {// the tab is selected
                        g.setColor(_selectColor1);

                        g.drawLine(x + w - 4, y + 1, x + w - 5, y + 1);// right arc
                        g.drawLine(x + w - 2, y + 2, x + w - 3, y + 2);// right arc
                        g.drawLine(x + w - 1, y + 3, x + w - 1, y + 3);

                        g.drawLine(x + w - 6, y, x + 2, y);// top

                        g.drawLine(x + 1, y + 1, x + 1, y + 1);// left arc

                        g.drawLine(x, y + 2, x, y + h - 1);// left

                        // right
                        for (int i = 3; i < h - 2; i++) {
                            g.drawLine(x + w - 3 + i, y + 1 + i, x + w - 3 + i, y + 1 + i);
                        }
                        g.drawLine(x + w - 4 + h, y + h - 1, x + w - 5 + h, y + h - 1);

                        g.setColor(_selectColor2);

                        g.drawLine(x + w - 4, y + 2, x + w - 5, y + 2);// right arc
                        g.drawLine(x + w - 2, y + 3, x + w - 3, y + 3);// right arc
                        g.drawLine(x + w - 1, y + 4, x + w - 1, y + 4);

                        g.drawLine(x + w - 6, y + 1, x + 2, y + 1);// top

                        g.drawLine(x + 1, y + 2, x + 1, y + h - 1);// right

                        // right
                        for (int i = 3; i < h - 2; i++) {
                            g.drawLine(x + w - 3 + i, y + 2 + i, x + w - 3 + i, y + 2 + i);
                        }
                    }
                    else {
                        if (tabIndex == 0) {
                            g.setColor(_unselectColor1);

                            g.drawLine(x + w - 4, y + 1, x + w - 5, y + 1);// right arc
                            g.drawLine(x + w - 2, y + 2, x + w - 3, y + 2);// right arc
                            g.drawLine(x + w - 1, y + 3, x + w - 1, y + 3);

                            g.drawLine(x + w - 6, y, x + 2, y);// top

                            g.drawLine(x + 1, y + 1, x + 1, y + 1);// left arc

                            g.drawLine(x, y + 2, x, y + h - 1);// left

                            // right
                            for (int i = 3; i < h - 2; i++) {
                                g.drawLine(x + w - 3 + i, y + 1 + i, x + w - 3 + i, y + 1 + i);
                            }
                            g.drawLine(x + w - 4 + h, y + h - 1, x + w - 5 + h, y + h - 1);//

                            if (_unselectColor2 != null) {
                                g.setColor(_unselectColor2);

                                g.drawLine(x + w - 4, y + 2, x + w - 5, y + 2);// right arc
                                g.drawLine(x + w - 2, y + 3, x + w - 3, y + 3);// right arc
                                g.drawLine(x + w - 1, y + 4, x + w - 1, y + 4);

                                // right
                                for (int i = 3; i < h - 2; i++) {
                                    g.drawLine(x + w - 3 + i, y + 2 + i, x + w - 3 + i, y + 2 + i);
                                }

                                g.drawLine(x + w - 6, y + 1, x + 3, y + 1);// top
                            }

                            if (_unselectColor3 != null) {
                                g.setColor(_unselectColor3);

                                g.drawLine(x + 2, y + 1, x + 2, y + 1);
                                g.drawLine(x + 1, y + 2, x + 1, y + h - 1);// left
                            }
                        }
                        else {
                            g.setColor(_unselectColor1);

                            g.drawLine(x + w - 4, y + 1, x + w - 5, y + 1);// right arc
                            g.drawLine(x + w - 2, y + 2, x + w - 3, y + 2);// right arc
                            g.drawLine(x + w - 1, y + 3, x + w - 1, y + 3);

                            g.drawLine(x + w - 6, y, x + 2, y);// top

                            g.drawLine(x + 1, y + 1, x + 1, y + 1);// left arc

                            g.drawLine(x, y + 2, x, y + h - 1);// left

                            if (_unselectColor2 != null) {
                                g.setColor(_unselectColor2);

                                g.drawLine(x + w - 4, y + 2, x + w - 5, y + 2);// right arc
                                g.drawLine(x + w - 2, y + 3, x + w - 3, y + 3);// right arc
                                g.drawLine(x + w - 1, y + 4, x + w - 1, y + 4);

                                g.drawLine(x + w - 6, y + 1, x + 3, y + 1);// top
                            }

                            if (_unselectColor3 != null) {
                                g.setColor(_unselectColor3);

                                g.drawLine(x + 2, y + 1, x + 2, y + 1);
                                g.drawLine(x + 1, y + 2, x + 1, y + h - 1);// left
                            }
                        }
                    }
                }
        }

    }


    protected void paintExcelTabBorder(Graphics g, int tabPlacement, int tabIndex,
                                       int x, int y, int w, int h, boolean isSelected) {
        boolean leftToRight = _tabPane.getComponentOrientation().isLeftToRight();
        switch (tabPlacement) {
            case LEFT:
                if (isSelected) {
                    g.setColor(_selectColor1);
                    g.drawLine(x, y + 5, x, y + h - 5);// left
                    for (int i = 0, j = 0; i < w / 2 + 1; i++, j = j + 2) {// top
                        g.drawLine(x + 1 + j, y + 4 - i, x + 2 + j, y + 4 - i);
                    }
                    for (int i = 0, j = 0; i < w / 2 + 1; i++, j = j + 2) {// bottom
                        g.drawLine(x + j, y + h - 4 + i, x + 1 + j, y + h - 4 + i);
                    }

                    if (_selectColor2 != null) {
                        g.setColor(_selectColor2);
                        g.drawLine(x + 1, y + 6, x + 1, y + h - 6);// left
                        for (int i = 0, j = 0; i < w / 2 + 1; i++, j = j + 2) {// top
                            g.drawLine(x + 1 + j, y + 5 - i, x + 2 + j, y + 5 - i);
                        }
                    }

                    if (_selectColor3 != null) {
                        g.setColor(_selectColor3);
                        g.drawLine(x + 1, y + h - 5, x + 1, y + h - 5);// a point
                        for (int i = 0, j = 0; i < w / 2 + 1; i++, j = j + 2) {// bottom
                            g.drawLine(x + 2 + j, y + h - 4 + i, x + 3 + j, y + h - 4 + i);
                        }
                    }
                }
                else {
                    if (tabIndex == 0) {
                        g.setColor(_unselectColor1);
                        g.drawLine(x, y + 5, x, y + h - 5);// left
                        for (int i = 0, j = 0; i < w / 2 + 1; i++, j = j + 2) {// top
                            g.drawLine(x + 1 + j, y + 4 - i, x + 2 + j, y + 4 - i);
                        }

                        for (int i = 0, j = 0; i < w / 2 + 1; i++, j = j + 2) {// bottom
                            g.drawLine(x + j, y + h - 4 + i, x + 1 + j, y + h - 4 + i);
                        }

                        if (_unselectColor2 != null) {
                            g.setColor(_unselectColor2);
                            g.drawLine(x + 1, y + 6, x + 1, y + h - 6);// left
                            for (int i = 0, j = 0; i < w / 2; i++, j = j + 2) {// top
                                g.drawLine(x + 1 + j, y + 5 - i, x + 2 + j, y + 5 - i);
                            }
                        }

                        if (_unselectColor3 != null) {
                            g.setColor(_unselectColor3);
                            g.drawLine(x + 1, y + h - 5, x + 1, y + h - 5);// a point
                            for (int i = 0, j = 0; i < w / 2 + 1; i++, j = j + 2) {// bottom
                                g.drawLine(x + 2 + j, y + h - 4 + i, x + 3 + j, y + h - 4 + i);
                            }
                        }
                    }
                    else if (tabIndex == _tabPane.getSelectedIndex() - 1) {
                        g.setColor(_unselectColor1);
                        g.drawLine(x, y + 5, x, y + h - 5);// left
                        for (int i = 0, j = 0; i < 4; i++, j = j + 2) {// top
                            g.drawLine(x + 1 + j, y + 4 - i, x + 2 + j, y + 4 - i);
                        }

                        for (int i = 0, j = 0; i < 5; i++, j = j + 2) {// bottom
                            g.drawLine(x + j, y + h - 4 + i, x + 1 + j, y + h - 4 + i);
                        }

                        if (_unselectColor2 != null) {
                            g.setColor(_unselectColor2);
                            g.drawLine(x + 1, y + 6, x + 1, y + h - 6);// left
                            for (int i = 0, j = 0; i < 4; i++, j = j + 2) {// top
                                g.drawLine(x + 1 + j, y + 5 - i, x + 2 + j, y + 5 - i);
                            }
                        }

                        if (_unselectColor3 != null) {
                            g.setColor(_unselectColor3);
                            g.drawLine(x + 1, y + h - 5, x + 1, y + h - 5);// a point
                            for (int i = 0, j = 0; i < 5; i++, j = j + 2) {// bottom
                                g.drawLine(x + 2 + j, y + h - 4 + i, x + 3 + j, y + h - 4 + i);
                            }
                        }
                    }
                    else if (tabIndex != _tabPane.getSelectedIndex() - 1) {
                        g.setColor(_unselectColor1);
                        g.drawLine(x, y + 5, x, y + h - 5);// left
                        for (int i = 0, j = 0; i < 4; i++, j = j + 2) {// top
                            g.drawLine(x + 1 + j, y + 4 - i, x + 2 + j, y + 4 - i);
                        }
                        for (int i = 0, j = 0; i < w / 2 + 1; i++, j = j + 2) {// bottom
                            g.drawLine(x + j, y + h - 4 + i, x + 1 + j, y + h - 4 + i);
                        }

                        if (_unselectColor2 != null) {
                            g.setColor(_unselectColor2);
                            g.drawLine(x + 1, y + 6, x + 1, y + h - 6);// left
                            for (int i = 0, j = 0; i < 4; i++, j = j + 2) {// top
                                g.drawLine(x + 1 + j, y + 5 - i, x + 2 + j, y + 5 - i);
                            }
                        }

                        if (_unselectColor3 != null) {
                            g.setColor(_unselectColor3);
                            g.drawLine(x + 1, y + h - 5, x + 1, y + h - 5);// a point
                            for (int i = 0, j = 0; i < w / 2 + 1; i++, j = j + 2) {// bottom
                                g.drawLine(x + 2 + j, y + h - 4 + i, x + 3 + j, y + h - 4 + i);
                            }
                        }
                    }
                }
                break;
            case RIGHT:
                if (isSelected) {
                    g.setColor(_selectColor1);
                    g.drawLine(x + w - 1, y + 5, x + w - 1, y + h - 5);// right
                    for (int i = 0, j = 0; i < w / 2 + 1; i++, j += 2) {// top
                        g.drawLine(x + w - 2 - j, y + 4 - i, x + w - 3 - j, y + 4 - i);
                    }
                    for (int i = 0, j = 0; i < w / 2 + 1; i++, j += 2) {// bottom
                        g.drawLine(x + w - 1 - j, y + h - 4 + i, x + w - 2 - j, y + h - 4 + i);
                    }

                    if (_selectColor2 != null) {
                        g.setColor(_selectColor2);
                        g.drawLine(x + w - 2, y + 6, x + w - 2, y + h - 6);// right
                        for (int i = 0, j = 0; i < w / 2 + 1; i++, j += 2) {// top
                            g.drawLine(x + w - 2 - j, y + 5 - i, x + w - 3 - j, y + 5 - i);
                        }
                    }

                    if (_selectColor3 != null) {
                        g.setColor(_selectColor3);
                        g.drawLine(x + w - 2, y + h - 5, x + w - 2, y + h - 5);// a point
                        for (int i = 0, j = 0; i < w / 2 + 1; i++, j += 2) {// bottom
                            g.drawLine(x + w - 3 - j, y + h - 4 + i, x + w - 4 - j, y + h - 4 + i);
                        }
                    }
                }
                else {
                    if (tabIndex == 0) {
                        g.setColor(_unselectColor1);
                        g.drawLine(x + w - 1, y + 5, x + w - 1, y + h - 5);// right
                        for (int i = 0, j = 0; i < w / 2 + 1; i++, j += 2) {// top
                            g.drawLine(x + w - 2 - j, y + 4 - i, x + w - 3 - j, y + 4 - i);
                        }

                        for (int i = 0, j = 0; i < w / 2 + 1; i++, j += 2) {// bottom
                            g.drawLine(x + w - 1 - j, y + h - 4 + i, x + w - 2 - j, y + h - 4 + i);
                        }

                        if (_unselectColor2 != null) {
                            g.setColor(_unselectColor2);
                            g.drawLine(x + w - 2, y + 6, x + w - 2, y + h - 6);// right

                            for (int i = 0, j = 0; i < w / 2 + 1; i++, j += 2) {// top
                                g.drawLine(x + w - 2 - j, y + 5 - i, x + w - 3 - j, y + 5 - i);
                            }
                        }

                        if (_unselectColor3 != null) {
                            g.setColor(_unselectColor3);

                            g.drawLine(x + w - 2, y + h - 5, x + w - 2, y + h - 5);// a
                            // point

                            for (int i = 0, j = 0; i < w / 2 + 1; i++, j += 2) {// bottom
                                g.drawLine(x + w - 3 - j, y + h - 4 + i, x + w - 4 - j, y + h - 4 + i);
                            }
                        }
                    }
                    else if (tabIndex == _tabPane.getSelectedIndex() - 1) {
                        g.setColor(_unselectColor1);
                        g.drawLine(x + w - 1, y + 5, x + w - 1, y + h - 5);// right
                        for (int i = 0, j = 0; i < 4; i++, j += 2) {// top
                            g.drawLine(x + w - 2 - j, y + 4 - i, x + w - 3 - j, y + 4 - i);
                        }

                        for (int i = 0, j = 0; i < 5; i++, j += 2) {// bottom
                            g.drawLine(x + w - 1 - j, y + h - 4 + i, x + w - 2 - j, y + h - 4 + i);
                        }

                        if (_unselectColor2 != null) {
                            g.setColor(_unselectColor2);
                            g.drawLine(x + w - 2, y + 6, x + w - 2, y + h - 6);// right
                            for (int i = 0, j = 0; i < 4; i++, j += 2) {// top
                                g.drawLine(x + w - 2 - j, y + 5 - i, x + w - 3 - j, y + 5 - i);
                            }

                        }

                        if (_unselectColor3 != null) {
                            g.setColor(_unselectColor3);

                            g.drawLine(x + w - 2, y + h - 5, x + w - 2, y + h - 5);// a point

                            for (int i = 0, j = 0; i < 5; i++, j += 2) {// bottom
                                g.drawLine(x + w - 3 - j, y + h - 4 + i, x + w - 4 - j, y + h - 4 + i);
                            }
                        }
                    }
                    else if (tabIndex != _tabPane.getSelectedIndex() - 1) {
                        g.setColor(_unselectColor1);
                        g.drawLine(x + w - 1, y + 5, x + w - 1, y + h - 5);// right
                        for (int i = 0, j = 0; i < 4; i++, j += 2) {// top
                            g.drawLine(x + w - 2 - j, y + 4 - i, x + w - 3 - j, y + 4 - i);
                        }

                        for (int i = 0, j = 0; i < w / 2 + 1; i++, j += 2) {// bottom
                            g.drawLine(x + w - 1 - j, y + h - 4 + i, x + w - 2 - j, y + h - 4 + i);
                        }

                        if (_unselectColor2 != null) {
                            g.setColor(_unselectColor2);
                            g.drawLine(x + w - 2, y + 6, x + w - 2, y + h - 6);// right
                            for (int i = 0, j = 0; i < 4; i++, j += 2) {// top
                                g.drawLine(x + w - 2 - j, y + 5 - i, x + w - 3 - j, y + 5 - i);
                            }
                        }

                        if (_unselectColor3 != null) {
                            g.setColor(_unselectColor3);
                            g.drawLine(x + w - 2, y + h - 5, x + w - 2, y + h - 5);// a point
                            for (int i = 0, j = 0; i < w / 2 + 1; i++, j += 2) {// bottom
                                g.drawLine(x + w - 3 - j, y + h - 4 + i, x + w - 4 - j, y + h - 4 + i);
                            }
                        }
                    }
                }
                break;
            case BOTTOM:
                if (isSelected) {
                    g.setColor(_selectColor1);
                    g.drawLine(x + 5, y + h - 1, x + w - 5, y + h - 1);// bottom
                    for (int i = 0, j = 0; i < h / 2 + 1; i++, j += 2) {// left
                        g.drawLine(x + 4 - i, y + h - 2 - j, x + 4 - i, y + h - 3 - j);
                    }

                    for (int i = 0, j = 0; i < h / 2 + 1; i++, j += 2) {// right
                        g.drawLine(x + w - 4 - 1 + i, y + h - 1 - j, x + w - 4 - 1 + i, y + h - 2 - j);
                    }

                    if (_selectColor2 != null) {
                        g.setColor(_selectColor2);

                        g.drawLine(x + 5, y + h - 3, x + 5, y + h - 3);// bottom

                        for (int i = 0, j = 0; i < h / 2 + 1; i++, j += 2) {// left
                            g.drawLine(x + 4 - i, y + h - 4 - j, x + 4 - i, y + h - 5 - j);
                        }

                    }

                    if (_selectColor3 != null) {
                        g.setColor(_selectColor3);
                        g.drawLine(x + 5, y + h - 2, x + w - 6, y + h - 2);// a point
                        for (int i = 0, j = 0; i < h / 2 + 1; i++, j += 2) {// right
                            g.drawLine(x + w - 5 + i, y + h - 3 - j, x + w - 5 + i, y + h - 4 - j);
                        }
                    }
                }
                else {
                    if ((leftToRight && tabIndex == 0) || (!leftToRight && tabIndex == _tabPane.getTabCount() - 1)) {
                        g.setColor(_unselectColor1);
                        g.drawLine(x + 5, y + h - 1, x + w - 5, y + h - 1);// bottom
                        for (int i = 0, j = 0; i < h / 2 + 1; i++, j += 2) {// left
                            g.drawLine(x + 4 - i, y + h - 2 - j, x + 4 - i, y + h - 3 - j);
                        }

                        for (int i = 0, j = 0; i < h / 2 + 1; i++, j += 2) {// right
                            g.drawLine(x + w - 4 - 1 + i, y + h - 1 - j, x + w - 4 - 1 + i, y + h - 2 - j);
                        }

                        if (_unselectColor2 != null) {
                            g.setColor(_unselectColor2);
                            for (int i = 0, j = 0; i < h / 2 + 1; i++, j += 2) {// left
                                g.drawLine(x + 5 - i, y + h - 2 - j, x + 5 - i, y + h - 3 - j);
                            }
                        }

                        if (_unselectColor3 != null) {
                            g.setColor(_unselectColor3);
                            g.drawLine(x + w - 6, y + h - 2, x + w - 6, y + h - 2);// a point
                            for (int i = 0, j = 0; i < h / 2 + 1; i++, j += 2) {// right
                                g.drawLine(x + w - 5 + i, y + h - 3 - j, x + w - 5 + i, y + h - 4 - j);
                            }
                        }
                    }
                    else if (tabIndex == _tabPane.getSelectedIndex() + (leftToRight ? -1 : 1)) {
                        g.setColor(_unselectColor1);
                        g.drawLine(x + 5, y + h - 1, x + w - 6, y + h - 1);// bottom
                        for (int i = 0, j = 0; i < 5; i++, j += 2) {// left
                            g.drawLine(x + 4 - i, y + h - 2 - j, x + 4 - i, y + h - 3 - j);
                        }
                        for (int i = 0, j = 0; i < 5; i++, j += 2) {// right
                            g.drawLine(x + w - 5 + i, y + h - 1 - j, x + w - 5 + i, y + h - 2 - j);
                        }

                        if (_unselectColor2 != null) {
                            g.setColor(_unselectColor2);
                            for (int i = 0, j = 0; i < 5; i++, j += 2) {// left
                                g.drawLine(x + 5 - i, y + h - 2 - j, x + 5 - i, y + h - 3 - j);
                            }
                        }

                        if (_unselectColor3 != null) {
                            g.setColor(_unselectColor3);
                            g.drawLine(x + w - 6, y + h - 2, x + w - 6, y + h - 2);// a point
                            for (int i = 0, j = 0; i < 5; i++, j += 2) {// right
                                g.drawLine(x + w - 5 + i, y + h - 3 - j, x + w - 5 + i, y + h - 4 - j);
                            }
                        }
                    }
                    else if (tabIndex != _tabPane.getSelectedIndex() + (leftToRight ? -1 : 1)) {
                        g.setColor(_unselectColor1);
                        g.drawLine(x + 5, y + h - 1, x + w - 6, y + h - 1);// bottom
                        for (int i = 0, j = 0; i < 5; i++, j += 2) {// left
                            g.drawLine(x + 4 - i, y + h - 2 - j, x + 4 - i, y + h - 3 - j);
                        }

                        for (int i = 0, j = 0; i < h / 2 + 1; i++, j += 2) {// right
                            g.drawLine(x + w - 5 + i, y + h - 1 - j, x + w - 5 + i, y + h - 2 - j);
                        }

                        if (_unselectColor2 != null) {
                            g.setColor(_unselectColor2);
                            for (int i = 0, j = 0; i < 5; i++, j += 2) {// left
                                g.drawLine(x + 5 - i, y + h - 2 - j, x + 5 - i, y + h - 3 - j);
                            }
                        }

                        if (_unselectColor3 != null) {
                            g.setColor(_unselectColor3);
                            g.drawLine(x + w - 6, y + h - 2, x + w - 6, y + h - 2);// a point
                            for (int i = 0, j = 0; i < h / 2 + 1; i++, j += 2) {// right
                                g.drawLine(x + w - 5 + i, y + h - 3 - j, x + w - 5 + i, y + h - 4 - j);
                            }
                        }
                    }
                }
                break;
            case TOP:
            default:
                if (isSelected) {
                    g.setColor(_selectColor1);
                    g.drawLine(x + 5, y, x + w - 5, y);// top
                    for (int i = 0, j = 0; i < h / 2 + 1; i++, j += 2) {// left
                        g.drawLine(x + 4 - i, y + 1 + j, x + 4 - i, y + 2 + j);
                    }
                    for (int i = 0, j = 0; i < h / 2 + 1; i++, j += 2) {// right
                        g.drawLine(x + w - 4 - 1 + i, y + j, x + w - 4 - 1 + i, y + 1 + j);
                    }

                    if (_selectColor2 != null) {
                        g.setColor(_selectColor2);
                        g.drawLine(x + 6, y + 1, x + w - 7, y + 1);// top
                        for (int i = 0, j = 0; i < h / 2 + 1; i++, j += 2) {// left
                            g.drawLine(x + 5 - i, y + 1 + j, x + 5 - i, y + 2 + j);
                        }
                    }

                    if (_selectColor3 != null) {
                        g.setColor(_selectColor3);
                        g.drawLine(x + w - 6, y + 1, x + w - 6, y + 1);// a point
                        for (int i = 0, j = 0; i < h / 2 + 1; i++, j += 2) {// right
                            g.drawLine(x + w - 5 + i, y + 2 + j, x + w - 5 + i, y + 3 + j);
                        }
                    }
                }
                else {
                    if ((leftToRight && tabIndex == 0) || (!leftToRight && tabIndex == _tabPane.getTabCount() - 1)) {
                        g.setColor(_unselectColor1);
                        g.drawLine(x + 5, y, x + w - 5, y);// top
                        for (int i = 0, j = 0; i < h / 2 + 1; i++, j += 2) {// left
                            g.drawLine(x + 4 - i, y + 1 + j, x + 4 - i, y + 2 + j);
                        }
                        for (int i = 0, j = 0; i < h / 2 + 1; i++, j += 2) {// right
                            g.drawLine(x + w - 4 - 1 + i, y + j, x + w - 4 - 1 + i, y + 1 + j);
                        }
                        if (_unselectColor2 != null) {
                            g.setColor(_unselectColor2);
                            g.drawLine(x + 6, y + 1, x + w - 7, y + 1);// top
                            for (int i = 0, j = 0; i < h / 2 + 1; i++, j += 2) {// left
                                g.drawLine(x + 5 - i, y + 1 + j, x + 5 - i, y + 2 + j);
                            }
                        }
                        if (_unselectColor3 != null) {
                            g.setColor(_unselectColor3);
                            g.drawLine(x + w - 6, y + 1, x + w - 6, y + 1);// a point
                            for (int i = 0, j = 0; i < h / 2 + 1; i++, j += 2) {// right
                                g.drawLine(x + w - 5 + i, y + 2 + j, x + w - 5 + i, y + 3 + j);
                            }
                        }
                    }
                    else if (tabIndex == _tabPane.getSelectedIndex() + (leftToRight ? -1 : 1)) {
                        g.setColor(_unselectColor1);
                        g.drawLine(x + 5, y, x + w - 5, y);// top
                        for (int i = 0, j = 0; i < 5; i++, j += 2) {// left
                            g.drawLine(x + 4 - i, y + 1 + j, x + 4 - i, y + 2 + j);
                        }
                        for (int i = 0, j = 0; i < 5; i++, j += 2) {// right
                            g.drawLine(x + w - 4 - 1 + i, y + j, x + w - 4 - 1 + i, y + 1 + j);
                        }

                        if (_unselectColor2 != null) {
                            g.setColor(_unselectColor2);
                            g.drawLine(x + 6, y + 1, x + w - 7, y + 1);// top
                            for (int i = 0, j = 0; i < 5; i++, j += 2) {// left
                                g.drawLine(x + 5 - i, y + 1 + j, x + 5 - i, y + 2 + j);
                            }
                        }
                        if (_unselectColor3 != null) {
                            g.setColor(_unselectColor3);
                            g.drawLine(x + w - 6, y + 1, x + w - 6, y + 1);// a point
                            for (int i = 0, j = 0; i < 5; i++, j += 2) {// right
                                g.drawLine(x + w - 5 + i, y + 2 + j, x + w - 5 + i, y + 3 + j);
                            }
                        }
                    }
                    else if (tabIndex != _tabPane.getSelectedIndex() + (leftToRight ? -1 : 1)) {
                        g.setColor(_unselectColor1);
                        g.drawLine(x + 5, y, x + w - 5, y);// top
                        for (int i = 0, j = 0; i < 5; i++, j += 2) {// left
                            g.drawLine(x + 4 - i, y + 1 + j, x + 4 - i, y + 2 + j);
                        }
                        for (int i = 0, j = 0; i < h / 2 + 1; i++, j += 2) {// right
                            g.drawLine(x + w - 4 - 1 + i, y + j, x + w - 4 - 1 + i, y + 1 + j);
                        }
                        if (_unselectColor2 != null) {
                            g.setColor(_unselectColor2);
                            g.drawLine(x + 6, y + 1, x + w - 7, y + 1);// top
                            for (int i = 0, j = 0; i < 5; i++, j += 2) {// left
                                g.drawLine(x + 5 - i, y + 1 + j, x + 5 - i, y + 2 + j);
                            }
                        }

                        if (_unselectColor3 != null) {
                            g.setColor(_unselectColor3);
                            g.drawLine(x + w - 6, y + 1, x + w - 6, y + 1);// a point
                            for (int i = 0, j = 0; i < h / 2 + 1; i++, j += 2) {// right
                                g.drawLine(x + w - 5 + i, y + 2 + j, x + w - 5 + i, y + 3 + j);
                            }
                        }
                    }
                }
        }
    }


    protected void paintWindowsTabBorder(Graphics g, int tabPlacement, int tabIndex,
                                         int x, int y, int w, int h, boolean isSelected) {
        int colorTheme = getColorTheme();
        switch (tabPlacement) {
            case LEFT:
                if (colorTheme == JideTabbedPane.COLOR_THEME_OFFICE2003 || colorTheme == JideTabbedPane.COLOR_THEME_WIN2K) {
                    if (isSelected) {
                        g.setColor(_selectColor1);
                        g.drawLine(x - 2, y + 1, x - 2, y + h - 1);// left
                        g.drawLine(x - 1, y, x - 1, y);// top arc
                        g.drawLine(x, y - 1, x + w - 1, y - 1);// top

                        g.setColor(_selectColor2);
                        g.drawLine(x - 1, y + h, x - 1, y + h);// bottom arc
                        g.drawLine(x, y + h + 1, x, y + h + 1);// bottom arc
                        g.drawLine(x + 1, y + h, x + w - 1, y + h);// bottom

                        g.setColor(_selectColor3);
                        g.drawLine(x, y + h, x, y + h);// bottom arc
                        g.drawLine(x + 1, y + h + 1, x + w - 1, y + h + 1);// bottom
                    }
                    else {
                        if (tabIndex > _tabPane.getSelectedIndex()) {
                            g.setColor(_unselectColor1);
                            g.drawLine(x, y + 2, x, y + h - 3);// left
                            g.drawLine(x + 1, y + 1, x + 1, y + 1);// top arc
                            g.drawLine(x + 2, y, x + w - 1, y);// top

                            g.setColor(_unselectColor2);
                            g.drawLine(x + 1, y + h - 2, x + 1, y + h - 2);// bottom arc
                            g.drawLine(x + 2, y + h - 1, x + 2, y + h - 1);// bottom arc
                            g.drawLine(x + 3, y + h - 2, x + w - 1, y + h - 2);// bottom

                            g.setColor(_unselectColor3);
                            g.drawLine(x + 2, y + h - 2, x + 2, y + h - 2);// bottom arc
                            g.drawLine(x + 3, y + h - 1, x + w - 1, y + h - 1);// bottom
                        }
                        else if (tabIndex < _tabPane.getSelectedIndex()) {
                            g.setColor(_unselectColor1);
                            g.drawLine(x, y + 3, x, y + h - 2);// left
                            g.drawLine(x + 1, y + 2, x + 1, y + 2);// top arc
                            g.drawLine(x + 2, y + 1, x + w - 1, y + 1);// top

                            g.setColor(_unselectColor2);
                            g.drawLine(x + 1, y + h - 1, x + 1, y + h - 1);// bottom arc
                            g.drawLine(x + 2, y + h, x + 2, y + h);// bottom arc
                            g.drawLine(x + 3, y + h - 1, x + w - 1, y + h - 1);// bottom

                            g.setColor(_unselectColor3);
                            g.drawLine(x + 2, y + h - 1, x + 2, y + h - 1);// bottom arc
                            g.drawLine(x + 3, y + h, x + w - 1, y + h);// bottom
                        }
                    }
                }
                else {
                    if (isSelected) {
                        g.setColor(_selectColor1);
                        g.drawLine(x - 2, y + 1, x - 2, y + h - 1);// left
                        g.drawLine(x - 1, y, x - 1, y);// top arc
                        g.drawLine(x, y - 1, x, y - 1);// top arc
                        g.drawLine(x - 1, y + h, x - 1, y + h);// bottom arc
                        g.drawLine(x, y + h + 1, x, y + h + 1);// bottom arc

                        g.setColor(_selectColor2);
                        g.drawLine(x - 1, y + 1, x - 1, y + h - 1);// left
                        g.drawLine(x, y, x, y + h);// left

                        g.setColor(_selectColor3);
                        g.drawLine(x + 1, y - 2, x + w - 1, y - 2);// top
                        g.drawLine(x + 1, y + h + 2, x + w - 1, y + h + 2);// bottom
                    }
                    else {
                        if (tabIndex > _tabPane.getSelectedIndex()) {
                            g.setColor(_unselectColor1);
                            g.drawLine(x, y + 2, x, y + h - 4);// left
                            g.drawLine(x + 1, y + 1, x + 1, y + 1);// top arc
                            g.drawLine(x + 2, y, x + w - 1, y);// top
                            g.drawLine(x + 1, y + h - 3, x + 1, y + h - 3);// bottom arc
                            g.drawLine(x + 2, y + h - 2, x + w - 1, y + h - 2);// bottom
                        }
                        else if (tabIndex < _tabPane.getSelectedIndex()) {
                            g.setColor(_unselectColor1);
                            g.drawLine(x, y + 4, x, y + h - 2);// left
                            g.drawLine(x + 1, y + 3, x + 1, y + 3);// top arc
                            g.drawLine(x + 2, y + 2, x + w - 1, y + 2);// top
                            g.drawLine(x + 1, y + h - 1, x + 1, y + h - 1);// bottom arc
                            g.drawLine(x + 2, y + h, x + w - 1, y + h);// bottom
                        }
                    }
                }
                break;
            case RIGHT:
                if (colorTheme == JideTabbedPane.COLOR_THEME_OFFICE2003 || colorTheme == JideTabbedPane.COLOR_THEME_WIN2K) {
                    if (isSelected) {
                        g.setColor(_selectColor1);
                        g.drawLine(x + w - 1, y - 1, x, y - 1);// top

                        g.setColor(_selectColor2);
                        g.drawLine(x + w, y + 1, x + w, y + h - 1);// right
                        g.drawLine(x + w - 1, y + h, x, y + h);// bottom

                        g.setColor(_selectColor3);
                        g.drawLine(x + w, y, x + w, y);// top arc
                        g.drawLine(x + w + 1, y + 1, x + w + 1, y + h - 1);// right
                        g.drawLine(x + w, y + h, x + w, y + h);// bottom arc
                        g.drawLine(x + w - 1, y + h + 1, x, y + h + 1);// bottom
                    }
                    else {
                        if (tabIndex > _tabPane.getSelectedIndex()) {
                            g.setColor(_unselectColor1);
                            g.drawLine(x + w - 3, y, x, y);// top

                            g.setColor(_unselectColor2);
                            g.drawLine(x + w - 2, y + 2, x + w - 2, y + h - 3);// right
                            g.drawLine(x + w - 3, y + h - 2, x, y + h - 2);// bottom

                            g.setColor(_unselectColor3);
                            g.drawLine(x + w - 2, y + 1, x + w - 2, y + 1);// top arc
                            g.drawLine(x + w - 1, y + 2, x + w - 1, y + h - 3);// right
                            g.drawLine(x + w - 2, y + h - 2, x + w - 2, y + h - 2);// bottom arc
                            g.drawLine(x + w - 3, y + h - 1, x, y + h - 1);// bottom
                        }
                        else if (tabIndex < _tabPane.getSelectedIndex()) {
                            g.setColor(_unselectColor1);
                            g.drawLine(x + w - 3, y + 1, x, y + 1);// top

                            g.setColor(_unselectColor2);
                            g.drawLine(x + w - 2, y + 3, x + w - 2, y + h - 2);// right
                            g.drawLine(x + w - 3, y + h - 1, x, y + h - 1);// bottom

                            g.setColor(_unselectColor3);
                            g.drawLine(x + w - 2, y + 2, x + w - 2, y + 2);// top arc
                            g.drawLine(x + w - 1, y + 3, x + w - 1, y + h - 2);// right
                            g.drawLine(x + w - 2, y + h - 1, x + w - 2, y + h - 1);// bottom arc
                            g.drawLine(x + w - 3, y + h, x, y + h);// bottom
                        }
                    }
                }
                else {
                    if (isSelected) {
                        g.setColor(_selectColor1);
                        g.drawLine(x + w + 1, y + 1, x + w + 1, y + h - 1);// right
                        g.drawLine(x + w, y, x + w, y);// top arc
                        g.drawLine(x + w - 1, y - 1, x + w - 1, y - 1);// top  arc
                        g.drawLine(x + w, y + h, x + w, y + h);// bottom arc
                        g.drawLine(x + w - 1, y + h + 1, x + w - 1, y + h + 1);// bottom arc

                        g.setColor(_selectColor2);
                        g.drawLine(x + w, y + 1, x + w, y + h - 1);// right
                        g.drawLine(x + w - 1, y, x + w - 1, y + h);// right

                        g.setColor(_selectColor3);
                        g.drawLine(x + w - 2, y - 2, x, y - 2);// top
                        g.drawLine(x + w - 2, y + h + 2, x, y + h + 2);// bottom
                    }
                    else {
                        if (tabIndex > _tabPane.getSelectedIndex()) {
                            g.setColor(_unselectColor1);
                            g.drawLine(x + w - 1, y + 2, x + w - 1, y + h - 4);// right
                            g.drawLine(x + w - 2, y + 1, x + w - 2, y + 1);// top arc
                            g.drawLine(x + w - 2, y + h - 3, x + w - 2, y + h - 3);// bottom arc
                            g.drawLine(x + w - 3, y, x, y);// top
                            g.drawLine(x + w - 3, y + h - 2, x, y + h - 2);// bottom
                        }
                        else if (tabIndex < _tabPane.getSelectedIndex()) {
                            g.setColor(_unselectColor1);
                            g.drawLine(x + w - 1, y + 4, x + w - 1, y + h - 2);// right
                            g.drawLine(x + w - 2, y + 3, x + w - 2, y + 3);// top arc
                            g.drawLine(x + w - 3, y + 2, x, y + 2);// top
                            g.drawLine(x + w - 2, y + h - 1, x + w - 2, y + h - 1);// bottom arc
                            g.drawLine(x + w - 3, y + h, x, y + h);// bottom
                        }
                    }
                }
                break;
            case BOTTOM:
                if (colorTheme == JideTabbedPane.COLOR_THEME_OFFICE2003 || colorTheme == JideTabbedPane.COLOR_THEME_WIN2K) {
                    if (isSelected) {
                        g.setColor(_selectColor1);
                        g.drawLine(x, y + h, x, y + h);// left arc
                        g.drawLine(x - 1, y + h - 1, x - 1, y);// left

                        g.setColor(_selectColor2);
                        g.drawLine(x + 1, y + h, x + w - 2, y + h);// bottom
                        g.drawLine(x + w - 1, y + h - 1, x + w - 1, y - 1);// right

                        g.setColor(_selectColor3);
                        g.drawLine(x + 1, y + h + 1, x + w - 2, y + h + 1);// bottom
                        g.drawLine(x + w - 1, y + h, x + w - 1, y + h);// right arc
                        g.drawLine(x + w, y + h - 1, x + w, y - 1);// right
                    }
                    else {
                        if (tabIndex > _tabPane.getSelectedIndex()) {
                            g.setColor(_unselectColor1);
                            g.drawLine(x, y + h - 2, x, y + h - 2);// left arc
                            g.drawLine(x - 1, y + h - 3, x - 1, y);// left

                            g.setColor(_unselectColor2);
                            g.drawLine(x + 1, y + h - 2, x + w - 4, y + h - 2);// bottom
                            g.drawLine(x + w - 3, y + h - 3, x + w - 3, y - 1);// right

                            g.setColor(_unselectColor3);
                            g.drawLine(x + 1, y + h - 1, x + w - 4, y + h - 1);// bottom
                            g.drawLine(x + w - 3, y + h - 2, x + w - 3, y + h - 2);// right arc
                            g.drawLine(x + w - 2, y + h - 3, x + w - 2, y - 1);// right
                        }
                        else if (tabIndex < _tabPane.getSelectedIndex()) {
                            g.setColor(_unselectColor1);
                            g.drawLine(x + 2, y + h - 2, x + 2, y + h - 2);// left arc
                            g.drawLine(x + 1, y + h - 3, x + 1, y);// left

                            g.setColor(_unselectColor2);
                            g.drawLine(x + 3, y + h - 2, x + w - 2, y + h - 2);// bottom
                            g.drawLine(x + w - 1, y + h - 3, x + w - 1, y);// right

                            g.setColor(_unselectColor3);
                            g.drawLine(x + 3, y + h - 1, x + w - 2, y + h - 1);// bottom
                            g.drawLine(x + w - 1, y + h - 2, x + w - 1, y + h - 2);// right arc
                            g.drawLine(x + w, y + h - 3, x + w, y);// right
                        }
                    }
                }
                else {
                    if (isSelected) {
                        g.setColor(_selectColor1);
                        g.drawLine(x + 1, y + h + 1, x + w, y + h + 1);// bottom
                        g.drawLine(x, y + h, x, y + h);// right arc
                        g.drawLine(x - 1, y + h - 1, x - 1, y + h - 1);// right arc
                        g.drawLine(x + w + 1, y + h, x + w + 1, y + h);// left arc
                        g.drawLine(x + w + 2, y + h - 1, x + w + 2, y + h - 1);// left arc

                        g.setColor(_selectColor2);
                        g.drawLine(x + 1, y + h, x + w, y + h);// bottom
                        g.drawLine(x, y + h - 1, x + w + 1, y + h - 1);// bottom

                        g.setColor(_selectColor3);
                        g.drawLine(x - 1, y + h - 2, x - 1, y);// left
                        g.drawLine(x + w + 2, y + h - 2, x + w + 2, y);// right
                    }
                    else {
                        if (tabIndex > _tabPane.getSelectedIndex()) {
                            g.setColor(_unselectColor1);
                            g.drawLine(x + 3, y + h - 1, x + w - 3, y + h - 1);// bottom
                            g.drawLine(x + w - 2, y + h - 2, x + w - 2, y + h - 2);// right arc
                            g.drawLine(x + w - 1, y + h - 3, x + w - 1, y - 1);// right
                            g.drawLine(x + 2, y + h - 2, x + 2, y + h - 2);// left arc
                            g.drawLine(x + 1, y + h - 3, x + 1, y);// left
                        }
                        else if (tabIndex < _tabPane.getSelectedIndex()) {
                            g.setColor(_unselectColor1);
                            g.drawLine(x + 3, y + h - 1, x + w - 3, y + h - 1);// bottom
                            g.drawLine(x + w - 2, y + h - 2, x + w - 2, y + h - 2);// right arc
                            g.drawLine(x + w - 1, y + h - 3, x + w - 1, y - 1);// right
                            g.drawLine(x + 2, y + h - 2, x + 2, y + h - 2);// left arc
                            g.drawLine(x + 1, y + h - 3, x + 1, y);// left
                        }
                    }
                }
                break;
            case TOP:
            default:
                if (colorTheme == JideTabbedPane.COLOR_THEME_OFFICE2003 || colorTheme == JideTabbedPane.COLOR_THEME_WIN2K) {
                    if (isSelected) {
                        g.setColor(_selectColor1);
                        g.drawLine(x, y - 1, x, y - 1); // left arc
                        g.drawLine(x - 1, y, x - 1, y + h - 1);// left
                        g.drawLine(x + 1, y - 2, x + w + 1, y - 2);// top

                        g.setColor(_selectColor2);
                        g.drawLine(x + w + 2, y - 1, x + w + 2, y + h - 1);// right

                        g.setColor(_selectColor3);
                        g.drawLine(x + w + 2, y - 1, x + w + 2, y - 1);// right arc
                        g.drawLine(x + w + 3, y, x + w + 3, y + h - 1);// right
                    }
                    else {
                        if (tabIndex > _tabPane.getSelectedIndex()) {
                            g.setColor(_unselectColor1);
                            g.drawLine(x + 2, y + 1, x + 2, y + 1); // left arc
                            g.drawLine(x + 1, y + 2, x + 1, y + h - 1); // left
                            g.drawLine(x + 3, y, x + w - 2, y); // top

                            g.setColor(_unselectColor2);
                            g.drawLine(x + w - 1, y + 1, x + w - 1, y + h - 1);// right

                            g.setColor(_unselectColor3);
                            g.drawLine(x + w - 1, y + 1, x + w - 1, y + 1);// right arc
                            g.drawLine(x + w, y + 2, x + w, y + h - 1);// right
                        }
                        else if (tabIndex < _tabPane.getSelectedIndex()) {
                            g.setColor(_unselectColor1);
                            g.drawLine(x + 2, y + 1, x + 2, y + 1); // left arc
                            g.drawLine(x + 1, y + 2, x + 1, y + h - 1); // left
                            g.drawLine(x + 3, y, x + w - 2, y); // top

                            g.setColor(_unselectColor2);
                            g.drawLine(x + w - 1, y + 1, x + w - 1, y + h - 1);// right

                            g.setColor(_unselectColor3);
                            g.drawLine(x + w - 1, y + 1, x + w - 1, y + 1);// right arc
                            g.drawLine(x + w, y + 2, x + w, y + h - 1);// right
                        }
                    }
                }
                else {
                    if (isSelected) {
                        g.setColor(_selectColor1);
                        g.drawLine(x + 1, y - 2, x + w, y - 2); // top
                        g.drawLine(x, y - 1, x, y - 1); // left arc
                        g.drawLine(x - 1, y, x - 1, y); // left arc
                        g.drawLine(x + w + 1, y - 1, x + w + 1, y - 1);// right arc
                        g.drawLine(x + w + 2, y, x + w + 2, y);// right arc

                        g.setColor(_selectColor2);
                        g.drawLine(x + 1, y - 1, x + w, y - 1);// top
                        g.drawLine(x, y, x + w + 1, y);// top

                        g.setColor(_selectColor3);
                        g.drawLine(x - 1, y + 1, x - 1, y + h - 1);// left
                        g.drawLine(x + w + 2, y + 1, x + w + 2, y + h - 1);// right
                    }
                    else {
                        if (tabIndex > _tabPane.getSelectedIndex()) {
                            g.setColor(_unselectColor1);
                            g.drawLine(x + 1, y + 2, x + 1, y + h - 1); // left
                            g.drawLine(x + 2, y + 1, x + 2, y + 1); // left arc
                            g.drawLine(x + 3, y, x + w - 3, y); // top
                            g.drawLine(x + w - 2, y + 1, x + w - 2, y + 1);// right arc
                            g.drawLine(x + w - 1, y + 2, x + w - 1, y + h - 1);// right
                        }
                        else if (tabIndex < _tabPane.getSelectedIndex()) {
                            g.setColor(_unselectColor1);
                            g.drawLine(x + 1, y + 2, x + 1, y + h - 1); // left
                            g.drawLine(x + 2, y + 1, x + 2, y + 1); // left arc
                            g.drawLine(x + 3, y, x + w - 3, y); // top
                            g.drawLine(x + w - 2, y + 1, x + w - 2, y + 1);// right arc
                            g.drawLine(x + w - 1, y + 2, x + w - 1, y + h - 1);// right
                        }
                    }
                }
        }
    }

    protected void paintTabBorderMouseOver(Graphics g, int tabPlacement, int tabIndex,
                                           int x, int y, int w, int h, boolean isSelected) {
        if (getTabShape() == JideTabbedPane.SHAPE_WINDOWS) {
            switch (tabPlacement) {
                case LEFT:
                    if (tabIndex > _tabPane.getSelectedIndex()) {
                        y = y - 2;
                    }

                    g.setColor(_selectColor1);
                    g.drawLine(x, y + 4, x, y + h - 2);
                    g.drawLine(x + 1, y + 3, x + 1, y + 3);
                    g.drawLine(x + 2, y + 2, x + 2, y + 2);
                    g.drawLine(x + 1, y + h - 1, x + 1, y + h - 1);
                    g.drawLine(x + 2, y + h, x + 2, y + h);

                    g.setColor(_selectColor2);
                    g.drawLine(x + 1, y + 4, x + 1, y + h - 2);
                    g.drawLine(x + 2, y + 3, x + 2, y + h - 1);
                    break;
                case RIGHT:
                    if (tabIndex > _tabPane.getSelectedIndex()) {
                        y = y - 2;
                    }

                    g.setColor(_selectColor1);
                    g.drawLine(x + w - 1, y + 4, x + w - 1, y + h - 2);
                    g.drawLine(x + w - 2, y + 3, x + w - 2, y + 3);
                    g.drawLine(x + w - 3, y + 2, x + w - 3, y + 2);
                    g.drawLine(x + w - 2, y + h - 1, x + w - 2, y + h - 1);
                    g.drawLine(x + w - 3, y + h, x + w - 3, y + h);

                    g.setColor(_selectColor2);
                    g.drawLine(x + w - 2, y + 4, x + w - 2, y + h - 2);
                    g.drawLine(x + w - 3, y + 3, x + w - 3, y + h - 1);
                    break;
                case BOTTOM:
                    g.setColor(_selectColor1);
                    g.drawLine(x + 3, y + h - 1, x + w - 3, y + h - 1);
                    g.drawLine(x + 2, y + h - 2, x + 2, y + h - 2);
                    g.drawLine(x + 1, y + h - 3, x + 1, y + h - 3);
                    g.drawLine(x + w - 2, y + h - 2, x + w - 2, y + h - 2);
                    g.drawLine(x + w - 1, y + h - 3, x + w - 1, y + h - 3);

                    g.setColor(_selectColor2);
                    g.drawLine(x + 3, y + h - 2, x + w - 3, y + h - 2);
                    g.drawLine(x + 2, y + h - 3, x + w - 2, y + h - 3);
                    break;
                case TOP:
                default:
                    g.setColor(_selectColor1);
                    g.drawLine(x + 3, y, x + w - 3, y);
                    g.drawLine(x + 2, y + 1, x + 2, y + 1);
                    g.drawLine(x + 1, y + 2, x + 1, y + 2);
                    g.drawLine(x + w - 2, y + 1, x + w - 2, y + 1);
                    g.drawLine(x + w - 1, y + 2, x + w - 1, y + 2);

                    g.setColor(_selectColor2);
                    g.drawLine(x + 3, y + 1, x + w - 3, y + 1);
                    g.drawLine(x + 2, y + 2, x + w - 2, y + 2);
            }
        }
        else if (getTabShape() == JideTabbedPane.SHAPE_WINDOWS_SELECTED) {
            switch (tabPlacement) {
                case LEFT:
                    if (getColorTheme() == JideTabbedPane.COLOR_THEME_WINXP) {
                        if (tabIndex > _tabPane.getSelectedIndex()) {
                            y -= 2;
                        }

                        g.setColor(_selectColor1);
                        g.drawLine(x, y + 4, x, y + h - 2);
                        g.drawLine(x + 1, y + 3, x + 1, y + 3);
                        g.drawLine(x + 2, y + 2, x + 2, y + 2);
                        g.drawLine(x + 1, y + h - 1, x + 1, y + h - 1);
                        g.drawLine(x + 2, y + h, x + 2, y + h);

                        g.setColor(_selectColor2);
                        g.drawLine(x + 1, y + 4, x + 1, y + h - 2);
                        g.drawLine(x + 2, y + 3, x + 2, y + h - 1);

                        g.setColor(_selectColor3);
                        g.drawLine(x + 3, y + 2, x + w - 1, y + 2);
                        g.drawLine(x + 3, y + h, x + w - 1, y + h);
                    }
                    else {
                        if (tabIndex > _tabPane.getSelectedIndex()) {
                            y = y - 1;
                        }
                        g.setColor(_selectColor1);
                        g.drawLine(x, y + 3, x, y + h - 2);// left
                        g.drawLine(x + 1, y + 2, x + 1, y + 2);// top arc
                        g.drawLine(x + 2, y + 1, x + w - 1, y + 1);// top

                        g.setColor(_selectColor2);
                        g.drawLine(x + 1, y + h - 1, x + 1, y + h - 1);// bottom arc
                        g.drawLine(x + 2, y + h, x + 2, y + h);// bottom arc
                        g.drawLine(x + 3, y + h - 1, x + w - 1, y + h - 1);// bottom

                        g.setColor(_selectColor3);
                        g.drawLine(x + 2, y + h - 1, x + 2, y + h - 1);// bottom arc
                        g.drawLine(x + 3, y + h, x + w - 1, y + h);// bottom
                    }
                    break;
                case RIGHT:
                    if (getColorTheme() == JideTabbedPane.COLOR_THEME_WINXP) {
                        if (tabIndex > _tabPane.getSelectedIndex()) {
                            y = y - 2;
                        }

                        g.setColor(_selectColor1);
                        g.drawLine(x + w - 1, y + 4, x + w - 1, y + h - 2);
                        g.drawLine(x + w - 2, y + 3, x + w - 2, y + 3);
                        g.drawLine(x + w - 3, y + 2, x + w - 3, y + 2);
                        g.drawLine(x + w - 2, y + h - 1, x + w - 2, y + h - 1);
                        g.drawLine(x + w - 3, y + h, x + w - 3, y + h);

                        g.setColor(_selectColor2);
                        g.drawLine(x + w - 2, y + 4, x + w - 2, y + h - 2);
                        g.drawLine(x + w - 3, y + 3, x + w - 3, y + h - 1);

                        g.setColor(_selectColor3);
                        g.drawLine(x + w - 4, y + 2, x, y + 2);
                        g.drawLine(x + w - 4, y + h, x, y + h);
                    }
                    else {
                        if (tabIndex > _tabPane.getSelectedIndex()) {
                            y = y - 1;
                        }

                        g.setColor(_selectColor3);
                        g.drawLine(x + w - 1, y + 3, x + w - 1, y + h - 2);// right
                        g.drawLine(x + w - 2, y + h - 1, x + w - 2, y + h - 1);// bottom arc
                        g.drawLine(x + w - 3, y + h, x, y + h);// bottom

                        g.setColor(_selectColor2);
                        g.drawLine(x + w - 2, y + 3, x + w - 2, y + h - 2);// right
                        g.drawLine(x + w - 3, y + h - 1, x, y + h - 1);// bottom

                        g.setColor(_selectColor1);
                        g.drawLine(x + w - 2, y + 2, x + w - 2, y + 2);// top arc
                        g.drawLine(x + w - 3, y + 1, x, y + 1);// top
                    }
                    break;
                case BOTTOM:
                    if (getColorTheme() == JideTabbedPane.COLOR_THEME_WINXP) {
                        g.setColor(_selectColor1);
                        g.drawLine(x + 3, y + h - 1, x + w - 3, y + h - 1);
                        g.drawLine(x + 2, y + h - 2, x + 2, y + h - 2);
                        g.drawLine(x + 1, y + h - 3, x + 1, y + h - 3);
                        g.drawLine(x + w - 2, y + h - 2, x + w - 2, y + h - 2);
                        g.drawLine(x + w - 1, y + h - 3, x + w - 1, y + h - 3);

                        g.setColor(_selectColor2);
                        g.drawLine(x + 3, y + h - 2, x + w - 3, y + h - 2);
                        g.drawLine(x + 2, y + h - 3, x + w - 2, y + h - 3);

                        g.setColor(_selectColor3);
                        g.drawLine(x + 1, y, x + 1, y + h - 4); // left
                        g.drawLine(x + w - 1, y, x + w - 1, y + h - 4);// right
                    }
                    else {
                        if (tabIndex > _tabPane.getSelectedIndex()) {
                            x = x - 2;
                        }

                        g.setColor(_selectColor3);
                        g.drawLine(x + 3, y + h - 1, x + w - 2, y + h - 1);// bottom
                        g.drawLine(x + w - 1, y + h - 2, x + w - 1, y + h - 2);// right arc
                        g.drawLine(x + w, y + h - 3, x + w, y);// right

                        g.setColor(_selectColor1);
                        g.drawLine(x + 2, y + h - 2, x + 2, y + h - 2);// left
                        g.drawLine(x + 1, y + h - 3, x + 1, y);// left arc

                        g.setColor(_selectColor2);
                        g.drawLine(x + 3, y + h - 2, x + w - 2, y + h - 2);// bottom
                        g.drawLine(x + w - 1, y + h - 3, x + w - 1, y);// right
                    }
                    break;
                case TOP:
                default:
                    if (getColorTheme() == JideTabbedPane.COLOR_THEME_WINXP) {
                        g.setColor(_selectColor1);
                        g.drawLine(x + 3, y, x + w - 3, y);
                        g.drawLine(x + 2, y + 1, x + 2, y + 1);
                        g.drawLine(x + 1, y + 2, x + 1, y + 2);
                        g.drawLine(x + w - 2, y + 1, x + w - 2, y + 1);
                        g.drawLine(x + w - 1, y + 2, x + w - 1, y + 2);

                        g.setColor(_selectColor2);
                        g.drawLine(x + 3, y + 1, x + w - 3, y + 1);
                        g.drawLine(x + 2, y + 2, x + w - 2, y + 2);

                        g.setColor(_selectColor3);
                        g.drawLine(x + 1, y + 3, x + 1, y + h - 1); // left
                        g.drawLine(x + w - 1, y + 3, x + w - 1, y + h - 1);// right
                    }
                    else {
                        if (tabIndex > _tabPane.getSelectedIndex()) {
                            x = x - 1;
                        }
                        g.setColor(_selectColor1);
                        g.drawLine(x + 2, y + 1, x + 2, y + 1); // left arc
                        g.drawLine(x + 1, y + 2, x + 1, y + h - 1); // left
                        g.drawLine(x + 3, y, x + w - 2, y); // top

                        g.setColor(_selectColor2);
                        g.drawLine(x + w - 1, y + 1, x + w - 1, y + h - 1);// right

                        g.setColor(_selectColor3);
                        g.drawLine(x + w - 1, y + 1, x + w - 1, y + 1);// right arc
                        g.drawLine(x + w, y + 2, x + w, y + h - 1);// right
                    }
            }

        }
    }

    protected void paintVsnetTabBorder(Graphics g, int tabPlacement, int tabIndex,
                                       int x, int y, int w, int h, boolean isSelected) {
        boolean leftToRight = _tabPane.getComponentOrientation().isLeftToRight();
        switch (tabPlacement) {
            case LEFT:
                if (isSelected) {
                    g.setColor(_selectColor1);
                    g.drawLine(x, y, x + w - 1, y);// top
                    g.drawLine(x, y, x, y + h - 2);// left

                    g.setColor(_selectColor2);
                    g.drawLine(x, y + h - 1, x + w - 1, y + h - 1);// bottom
                }
                else {
                    g.setColor(_unselectColor1);
                    if (tabIndex > _tabPane.getSelectedIndex()) {
                        g.drawLine(x + 2, y + h - 2, x + w - 2, y + h - 2);// bottom
                    }
                    else if (tabIndex < _tabPane.getSelectedIndex() && tabIndex != 0) {
                        g.drawLine(x + 2, y, x + w - 2, y);// top
                    }
                }
                break;
            case RIGHT:
                if (isSelected) {
                    g.setColor(_selectColor1);
                    g.drawLine(x, y, x + w - 1, y);// top

                    g.setColor(_selectColor2);
                    g.drawLine(x + w - 1, y, x + w - 1, y + h - 2);// left
                    g.drawLine(x, y + h - 1, x + w - 1, y + h - 1);// bottom
                }
                else {
                    g.setColor(_unselectColor1);
                    if (tabIndex > _tabPane.getSelectedIndex()) {
                        g.drawLine(x + 1, y + h - 2, x + w - 3, y + h - 2);// bottom
                    }
                    else if (tabIndex < _tabPane.getSelectedIndex() && tabIndex != 0) {
                        g.drawLine(x + 1, y, x + w - 3, y);// top
                    }
                }
                break;
            case BOTTOM:
                if (isSelected) {
                    g.setColor(_selectColor1);
                    g.drawLine(x, y, x, y + h - 1); // left

                    g.setColor(_selectColor2);
                    g.drawLine(x, y + h - 1, x + w - 1, y + h - 1); // bottom
                    g.drawLine(x + w - 1, y, x + w - 1, y + h - 2); // right
                }
                else {
                    g.setColor(_unselectColor1);
                    if (leftToRight) {
                        if (tabIndex > _tabPane.getSelectedIndex()) {
                            g.drawLine(x + w - 2, y + 2, x + w - 2, y + h - 2); // right
                        }
                        else if (tabIndex < _tabPane.getSelectedIndex() && tabIndex != 0) {
                            g.drawLine(x, y + 2, x, y + h - 2); // left
                        }
                    }
                    else {
                        if (tabIndex > _tabPane.getSelectedIndex()) {
                            g.drawLine(x, y + 2, x, y + h - 2); // left
                        }
                        else if (tabIndex < _tabPane.getSelectedIndex() && tabIndex != _tabPane.getTabCount() - 1) {
                            g.drawLine(x + w - 2, y + 2, x + w - 2, y + h - 2); // right
                        }
                    }
                }
                break;
            case TOP:
            default:
                if (isSelected) {
                    g.setColor(_selectColor1);
                    g.drawLine(x, y + 1, x, y + h - 1); // left
                    g.drawLine(x, y, x + w - 1, y); // top

                    g.setColor(_selectColor2);
                    g.drawLine(x + w - 1, y, x + w - 1, y + h - 1); // right
                }
                else {
                    g.setColor(_unselectColor1);
                    if (leftToRight) {
                        if (tabIndex > _tabPane.getSelectedIndex()) {
                            g.drawLine(x + w - 2, y + 2, x + w - 2, y + h - 2); // right
                        }
                        else if (tabIndex < _tabPane.getSelectedIndex() && tabIndex != 0) {
                            g.drawLine(x, y + 2, x, y + h - 2); // left
                        }
                    }
                    else {
                        if (tabIndex > _tabPane.getSelectedIndex()) {
                            g.drawLine(x, y + 2, x, y + h - 2); // left
                        }
                        else if (tabIndex < _tabPane.getSelectedIndex() && tabIndex != _tabPane.getTabCount() - 1) {
                            g.drawLine(x + w - 2, y + 2, x + w - 2, y + h - 2); // right
                        }
                    }
                }
        }

    }

    protected void paintRoundedVsnetTabBorder(Graphics g, int tabPlacement, int tabIndex,
                                              int x, int y, int w, int h, boolean isSelected) {
        boolean leftToRight = _tabPane.getComponentOrientation().isLeftToRight();
        switch (tabPlacement) {
            case LEFT:
                if (isSelected) {
                    g.setColor(_selectColor1);
                    g.drawLine(x + 2, y, x + w - 1, y);// top
                    g.drawLine(x + 1, y + 1, x + 1, y + 1);// top-left
                    g.drawLine(x, y + 2, x, y + h - 3);// left
                    g.drawLine(x + 1, y + h - 2, x + 1, y + h - 2);// bottom-left
                    g.drawLine(x + 2, y + h - 1, x + w - 1, y + h - 1);// bottom
                }
                else {
                    g.setColor(_unselectColor1);
                    if (tabIndex > _tabPane.getSelectedIndex()) {
                        g.drawLine(x + 2, y + h - 2, x + w - 2, y + h - 2);// bottom
                    }
                    else if (tabIndex < _tabPane.getSelectedIndex() && tabIndex != 0) {
                        g.drawLine(x + 2, y + 1, x + w - 2, y + 1);// top
                    }
                }
                break;
            case RIGHT:
                if (isSelected) {
                    g.setColor(_selectColor1);
                    g.drawLine(x, y, x + w - 3, y);// top
                    g.drawLine(x + w - 2, y + 1, x + w - 2, y + 1);// top-left
                    g.drawLine(x + w - 1, y + 2, x + w - 1, y + h - 3);// left
                    g.drawLine(x + w - 2, y + h - 2, x + w - 2, y + h - 2);// bottom-left
                    g.drawLine(x, y + h - 1, x + w - 3, y + h - 1);// bottom
                }
                else {
                    g.setColor(_unselectColor1);
                    if (tabIndex > _tabPane.getSelectedIndex()) {
                        g.drawLine(x + 1, y + h - 2, x + w - 3, y + h - 2);// bottom
                    }
                    else if (tabIndex < _tabPane.getSelectedIndex() && tabIndex != 0) {
                        g.drawLine(x + 1, y + 1, x + w - 3, y + 1);// top
                    }
                }
                break;
            case BOTTOM:
                if (isSelected) {
                    g.setColor(_selectColor1);
                    g.drawLine(x, y, x, y + h - 3); // left
                    g.drawLine(x + 1, y + h - 2, x + 1, y + h - 2); // bottom-left
                    g.drawLine(x + 2, y + h - 1, x + w - 3, y + h - 1); // bottom
                    g.drawLine(x + w - 2, y + h - 2, x + w - 2, y + h - 2); // bottom-right
                    g.drawLine(x + w - 1, y, x + w - 1, y + h - 3); // right
                }
                else {
                    g.setColor(_unselectColor1);
                    if (leftToRight) {
                        if (tabIndex > _tabPane.getSelectedIndex()) {
                            g.drawLine(x + w - 2, y + 2, x + w - 2, y + h - 2); // right
                        }
                        else if (tabIndex < _tabPane.getSelectedIndex() && tabIndex != 0) {
                            g.drawLine(x, y + 2, x, y + h - 2); // left
                        }
                    }
                    else {
                        if (tabIndex > _tabPane.getSelectedIndex()) {
                            g.drawLine(x, y + 2, x, y + h - 2); // left
                        }
                        else if (tabIndex < _tabPane.getSelectedIndex() && tabIndex != _tabPane.getTabCount() - 1) {
                            g.drawLine(x + w - 2, y + 2, x + w - 2, y + h - 2); // right
                        }
                    }
                }
                break;
            case TOP:
            default:
                if (isSelected) {
                    g.setColor(_selectColor1);
                    g.drawLine(x, y + 2, x, y + h - 1); // left
                    g.drawLine(x, y + 2, x + 2, y); // top-left
                    g.drawLine(x + 2, y, x + w - 3, y); // top
                    g.drawLine(x + w - 3, y, x + w - 1, y + 2); // top-left
                    g.drawLine(x + w - 1, y + 2, x + w - 1, y + h - 1); // right
                }
                else {
                    g.setColor(_unselectColor1);
                    if (leftToRight) {
                        if (tabIndex > _tabPane.getSelectedIndex()) {
                            g.drawLine(x + w - 2, y + 2, x + w - 2, y + h - 2); // right
                        }
                        else if (tabIndex < _tabPane.getSelectedIndex() && tabIndex != 0) {
                            g.drawLine(x, y + 2, x, y + h - 2); // left
                        }
                    }
                    else {
                        if (tabIndex > _tabPane.getSelectedIndex()) {
                            g.drawLine(x, y + 2, x, y + h - 2); // left
                        }
                        else if (tabIndex < _tabPane.getSelectedIndex() && tabIndex != _tabPane.getTabCount() - 1) {
                            g.drawLine(x + w - 2, y + 2, x + w - 2, y + h - 2); // right
                        }
                    }
                }
        }

    }

    protected void paintFlatTabBorder(Graphics g, int tabPlacement, int tabIndex,
                                      int x, int y, int w, int h, boolean isSelected) {
        switch (tabPlacement) {
            case LEFT:
                if (isSelected) {
                    g.setColor(_selectColor1);
                    g.drawRect(x, y, w, h);
                }
                else {
                    g.setColor(_unselectColor1);
                    if (tabIndex > _tabPane.getSelectedIndex()) {
                        if (tabIndex == _tabPane.getTabCount() - 1) {
                            g.drawRect(x, y, w, h - 1);
                        }
                        else {
                            g.drawRect(x, y, w, h);
                        }
                    }
                    else if (tabIndex < _tabPane.getSelectedIndex()) {
                        g.drawRect(x, y, w, h);
                    }
                }
                break;
            case RIGHT:
                if (isSelected) {
                    g.setColor(_selectColor1);
                    g.drawRect(x - 1, y, w, h);
                }
                else {
                    g.setColor(_unselectColor1);
                    if (tabIndex > _tabPane.getSelectedIndex()) {
                        if (tabIndex == _tabPane.getTabCount() - 1) {
                            g.drawRect(x - 1, y, w, h - 1);
                        }
                        else {
                            g.drawRect(x - 1, y, w, h);
                        }
                    }
                    else if (tabIndex < _tabPane.getSelectedIndex()) {
                        g.drawRect(x - 1, y, w, h);
                    }
                }
                break;
            case BOTTOM:
                if (isSelected) {
                    g.setColor(_selectColor1);
                    g.drawRect(x, y - 1, w, h);
                }
                else {
                    g.setColor(_unselectColor1);
                    g.drawRect(x, y - 1, w, h);
                }
                break;
            case TOP:
            default:
                if (isSelected) {
                    g.setColor(_selectColor1);
                    g.drawRect(x, y, w, h);
                }
                else {
                    g.setColor(_unselectColor1);
                    g.drawRect(x, y, w, h);
                }
        }
    }

    protected void paintRoundedFlatTabBorder(Graphics g, int tabPlacement, int tabIndex,
                                             int x, int y, int w, int h, boolean isSelected) {
        switch (tabPlacement) {
            case LEFT:
                if (isSelected) {
                    g.setColor(_selectColor1);
                    g.drawLine(x + 2, y, x + w - 1, y);
                    g.drawLine(x + 2, y + h, x + w - 1, y + h);
                    g.drawLine(x, y + 2, x, y + h - 2);

                    g.setColor(_selectColor2);
                    g.drawLine(x + 1, y + 1, x + 1, y + 1);
//                    g.drawLine(x, y + 1, x, y + 1);
                    g.drawLine(x + 1, y + h - 1, x + 1, y + h - 1);
//                    g.drawLine(x + 1, y + h, x + 1, y + h);
                }
                else {
                    if (tabIndex > _tabPane.getSelectedIndex()) {
                        g.setColor(_unselectColor1);
                        g.drawLine(x + 2, y, x + w - 1, y);

                        if (tabIndex == _tabPane.getTabCount() - 1) {
                            g.drawLine(x + 2, y + h - 1, x + w - 1, y + h - 1);
                            g.drawLine(x, y + 2, x, y + h - 3);
                        }
                        else {
                            g.drawLine(x + 2, y + h, x + w - 1, y + h);
                            g.drawLine(x, y + 2, x, y + h - 2);
                        }

                        g.setColor(_unselectColor2);
                        g.drawLine(x + 1, y + 1, x + 1, y + 1);
//                        g.drawLine(x, y + 1, x, y + 1);

                        if (tabIndex == _tabPane.getTabCount() - 1) {
                            g.drawLine(x, y + h - 2, x, y + h - 2);
                            g.drawLine(x + 1, y + h - 1, x + 1, y + h - 1);
                        }
                        else {
                            g.drawLine(x, y + h - 1, x, y + h - 1);
                            g.drawLine(x + 1, y + h, x + 1, y + h);
                        }
                    }
                    else if (tabIndex < _tabPane.getSelectedIndex()) {
                        g.setColor(_unselectColor1);
                        g.drawLine(x + 2, y, x + w - 1, y);
                        g.drawLine(x + 2, y + h, x + w - 1, y + h);
                        g.drawLine(x, y + 2, x, y + h - 2);

                        g.setColor(_unselectColor2);
                        g.drawLine(x + 1, y + 1, x + 1, y + 1);
//                        g.drawLine(x, y + 1, x, y + 1);
                        g.drawLine(x + 1, y + h - 1, x + 1, y + h - 1);
//                        g.drawLine(x + 1, y + h, x + 1, y + h);
                    }
                }
                break;
            case RIGHT:
                if (isSelected) {
                    g.setColor(_selectColor1);
                    g.drawLine(x, y, x + w - 3, y);
                    g.drawLine(x, y + h, x + w - 3, y + h);
                    g.drawLine(x + w - 1, y + 2, x + w - 1, y + h - 2);

                    g.setColor(_selectColor2);
                    g.drawLine(x + w - 2, y + 1, x + w - 2, y + 1);
//                    g.drawLine(x + w - 1, y + 1, x + w - 1, y + 1);
//                    g.drawLine(x + w - 1, y + h - 1, x + w - 1, y + h - 1);
                    g.drawLine(x + w - 2, y + h - 1, x + w - 2, y + h - 1);
                }
                else {
                    if (tabIndex > _tabPane.getSelectedIndex()) {
                        g.setColor(_unselectColor1);
                        g.drawLine(x, y, x + w - 3, y);

                        if (tabIndex == _tabPane.getTabCount() - 1) {
                            g.drawLine(x, y + h - 1, x + w - 3, y + h - 1);
                            g.drawLine(x + w - 1, y + 2, x + w - 1, y + h - 3);
                        }
                        else {
                            g.drawLine(x, y + h, x + w - 3, y + h);
                            g.drawLine(x + w - 1, y + 2, x + w - 1, y + h - 2);
                        }

                        g.setColor(_unselectColor2);
                        g.drawLine(x + w - 2, y + 1, x + w - 2, y + 1);
//                        g.drawLine(x + w - 1, y + 1, x + w - 1, y + 1);

                        if (tabIndex == _tabPane.getTabCount() - 1) {
                            g.drawLine(x + w - 2, y + h - 2, x + w - 2, y + h - 2);
//                            g.drawLine(x + w - 2, y + h - 1, x + w - 2, y + h - 1);
                        }
                        else {
//                            g.drawLine(x + w - 1, y + h - 1, x + w - 1, y + h - 1);
                            g.drawLine(x + w - 2, y + h - 1, x + w - 2, y + h - 1);
                        }
                    }
                    else if (tabIndex < _tabPane.getSelectedIndex()) {
                        g.setColor(_unselectColor1);
                        g.drawLine(x, y, x + w - 3, y);
                        g.drawLine(x, y + h, x + w - 3, y + h);
                        g.drawLine(x + w - 1, y + 2, x + w - 1, y + h - 2);

                        g.setColor(_unselectColor2);
                        g.drawLine(x + w - 2, y + 1, x + w - 2, y + 1);
//                        g.drawLine(x + w - 1, y + 1, x + w - 1, y + 1);
                        g.drawLine(x + w - 2, y + h - 1, x + w - 2, y + h - 1);
//                        g.drawLine(x + w - 2, y + h, x + w - 2, y + h);
                    }
                }
                break;
            case BOTTOM:
                if (isSelected) {
                    g.setColor(_selectColor1);
                    g.drawLine(x, y, x, y + h - 3);
                    g.drawLine(x + 2, y + h - 1, x + w - 2, y + h - 1);
                    g.drawLine(x + w, y, x + w, y + h - 3);

                    g.setColor(_selectColor2);
                    g.drawLine(x + 1, y + h - 2, x + 1, y + h - 2);
//                    g.drawLine(x + 1, y + h - 1, x + 1, y + h - 1);
//                    g.drawLine(x + w - 1, y + h - 1, x + w - 1, y + h - 1);
                    g.drawLine(x + w - 1, y + h - 2, x + w - 1, y + h - 2);
                }
                else {
                    if (tabIndex > _tabPane.getSelectedIndex()) {
                        g.setColor(_unselectColor1);
                        g.drawLine(x, y, x, y + h - 3);

                        if (tabIndex == _tabPane.getTabCount() - 1) {
                            g.drawLine(x + 2, y + h - 1, x + w - 3, y + h - 1);
                            g.drawLine(x + w - 1, y, x + w - 1, y + h - 3);
                        }
                        else {
                            g.drawLine(x + 2, y + h - 1, x + w - 2, y + h - 1);
                            g.drawLine(x + w, y, x + w, y + h - 3);
                        }

                        g.setColor(_unselectColor2);
                        g.drawLine(x + 1, y + h - 2, x + 1, y + h - 2);
//                        g.drawLine(x + 1, y + h - 1, x + 1, y + h - 1);

                        if (tabIndex == _tabPane.getTabCount() - 1) {
//                            g.drawLine(x + w - 2, y + h - 1, x + w - 2, y + h - 1);
                            g.drawLine(x + w - 2, y + h - 2, x + w - 2, y + h - 2);
                        }
                        else {
//                            g.drawLine(x + w - 1, y + h - 1, x + w - 1, y + h - 1);
                            g.drawLine(x + w - 1, y + h - 2, x + w - 1, y + h - 2);
                        }
                    }
                    else if (tabIndex < _tabPane.getSelectedIndex()) {
                        g.setColor(_unselectColor1);
                        g.drawLine(x, y, x, y + h - 3);
                        g.drawLine(x + 2, y + h - 1, x + w - 2, y + h - 1);
                        g.drawLine(x + w, y, x + w, y + h - 3);

                        g.setColor(_unselectColor2);
                        g.drawLine(x + 1, y + h - 2, x + 1, y + h - 2);
//                        g.drawLine(x + 1, y + h - 1, x + 1, y + h - 1);
//                        g.drawLine(x + w - 1, y + h - 1, x + w - 1, y + h - 1);
                        g.drawLine(x + w - 1, y + h - 2, x + w - 1, y + h - 2);
                    }
                }
                break;
            case TOP:
            default:
                if (isSelected) {
                    g.setColor(_selectColor1);
                    g.drawLine(x, y + h - 1, x, y + 2);
                    g.drawLine(x + 2, y, x + w - 2, y);
                    g.drawLine(x + w, y + 2, x + w, y + h - 1);

                    g.setColor(_selectColor2);
                    g.drawLine(x, y + 2, x + 2, y); // top-left
                    g.drawLine(x + w - 2, y, x + w, y + 2);
//                    g.drawLine(x + w, y + 1, x + w, y + 1);
                }
                else {
                    if (tabIndex > _tabPane.getSelectedIndex()) {
                        g.setColor(_unselectColor1);
                        g.drawLine(x, y + h - 1, x, y + 2);

                        if (tabIndex == _tabPane.getTabCount() - 1) {
                            g.drawLine(x + 2, y, x + w - 3, y);
                            g.drawLine(x + w - 1, y + 2, x + w - 1, y + h - 1);
                        }
                        else {
                            g.drawLine(x + 2, y, x + w - 2, y);
                            g.drawLine(x + w, y + 2, x + w, y + h - 1);
                        }

                        g.setColor(_unselectColor2);
                        g.drawLine(x, y + 2, x + 2, y); // top-left

                        if (tabIndex == _tabPane.getTabCount() - 1) {
                            g.drawLine(x + w - 3, y, x + w - 1, y + 2);
                        }
                        else {
                            g.drawLine(x + w - 2, y, x + w, y + 2);
                        }
                    }
                    else if (tabIndex < _tabPane.getSelectedIndex()) {
                        g.setColor(_unselectColor1);
                        g.drawLine(x, y + h - 1, x, y + 2);
                        g.drawLine(x + 2, y, x + w - 2, y);
                        g.drawLine(x + w, y + 2, x + w, y + h - 1);

                        g.setColor(_unselectColor2);
                        g.drawLine(x, y + 2, x + 2, y);
                        g.drawLine(x + w - 2, y, x + w, y + 2);
                    }
                }
        }
    }

    protected void paintBoxTabBorder(Graphics g, int tabPlacement, int tabIndex,
                                     int x, int y, int w, int h, boolean isSelected) {
        boolean leftToRight = _tabPane.getComponentOrientation().isLeftToRight();

        if (isSelected) {
            g.setColor(_selectColor1);
            g.drawLine(x, y, x + w - 2, y);// top
            g.drawLine(x, y, x, y + h - 2);// left

            g.setColor(_selectColor2);
            g.drawLine(x + w - 1, y, x + w - 1, y + h - 1);// right
            g.drawLine(x, y + h - 1, x + w - 1, y + h - 1);// bottom
        }
        else {
            if (tabIndex != _tabPane.getSelectedIndex() - 1) {
                switch (tabPlacement) {
                    case LEFT:
                    case RIGHT:
                        g.setColor(_unselectColor1);
                        g.drawLine(x + 2, y + h, x + w - 2, y + h);// bottom

                        g.setColor(_unselectColor2);
                        g.drawLine(x + 2, y + h + 1, x + w - 2, y + h + 1);// bottom
                        break;
                    case BOTTOM:
                    case TOP:
                    default:
                        if (leftToRight) {
                            g.setColor(_unselectColor1);
                            g.drawLine(x + w, y + 2, x + w, y + h - 2);// right

                            g.setColor(_unselectColor2);
                            g.drawLine(x + w + 1, y + 2, x + w + 1, y + h - 2);// right
                        }
                        else {
                            g.setColor(_unselectColor1);
                            g.drawLine(x, y + 2, x, y + h - 2);// right

                            g.setColor(_unselectColor2);
                            g.drawLine(x + 1, y + 2, x + 1, y + h - 2);// right
                        }
                }
            }
        }
    }

    protected void paintTabBackground(Graphics g, int tabPlacement, int tabIndex, int x, int y, int w, int h, boolean isSelected) {
        if (!PAINT_TAB_BACKGROUND) {
            return;
        }

        switch (getTabShape()) {
            case JideTabbedPane.SHAPE_BOX:
                paintButtonTabBackground(g, tabPlacement, tabIndex, x, y, w, h, isSelected);
                break;
            case JideTabbedPane.SHAPE_EXCEL:
                paintExcelTabBackground(g, tabPlacement, tabIndex, x, y, w, h, isSelected);
                break;
            case JideTabbedPane.SHAPE_WINDOWS:
                paintDefaultTabBackground(g, tabPlacement, tabIndex, x, y, w, h, isSelected);
                break;
            case JideTabbedPane.SHAPE_WINDOWS_SELECTED:
                if (isSelected) {
                    paintDefaultTabBackground(g, tabPlacement, tabIndex, x, y, w, h, isSelected);
                }
                break;
            case JideTabbedPane.SHAPE_VSNET:
            case JideTabbedPane.SHAPE_ROUNDED_VSNET:
                paintVsnetTabBackground(g, tabPlacement, tabIndex, x, y, w, h, isSelected);
                break;
            case JideTabbedPane.SHAPE_FLAT:
            case JideTabbedPane.SHAPE_ROUNDED_FLAT:
                paintFlatTabBackground(g, tabPlacement, tabIndex, x, y, w, h, isSelected);
                break;
            case JideTabbedPane.SHAPE_OFFICE2003:
            default:
                paintOffice2003TabBackground(g, tabPlacement, tabIndex, x, y, w, h, isSelected);
        }

    }

    protected void paintOffice2003TabBackground(Graphics g, int tabPlacement,
                                                int tabIndex, int x, int y, int w, int h, boolean isSelected) {
        boolean leftToRight = _tabPane.getComponentOrientation().isLeftToRight();
        switch (tabPlacement) {
            case LEFT:
                if (tabIndex != 0 && !isSelected) {
                    int xp[] = {x + w, x + 4, x + 2, x, x, x + 3, x + w};
                    int yp[] = {y, y, y + 2, y + 5, y + h - 5, y + h - 2, y + h - 2};
                    int np = yp.length;
                    tabRegion = new Polygon(xp, yp, np);
                }
                else {// tabIndex != 0
                    int xp[] = {x + w, x + 2, x, x, x + 3, x + w};
                    int yp[] = {y - w + 2 + 2, y + 2, y + 5, y + h - 5, y + h - 2, y + h - 2};
                    int np = yp.length;
                    tabRegion = new Polygon(xp, yp, np);
                }
                break;
            case RIGHT:
                if (tabIndex != 0 && !isSelected) {
                    int xp[] = {x, x + w - 4, x + w - 3, x + w - 1, x + w - 1, x + w - 3, x};
                    int yp[] = {y, y, y + 2, y + 5, y + h - 5, y + h - 2, y + h - 2};
                    int np = yp.length;
                    tabRegion = new Polygon(xp, yp, np);
                }
                else {
                    int xp[] = {x, x + w - 3, x + w - 1, x + w - 1, x + w - 3, x};
                    int yp[] = {y - w + 2 + 2, y + 2, y + 5, y + h - 5, y + h - 2, y + h - 2};
                    int np = yp.length;
                    tabRegion = new Polygon(xp, yp, np);
                }
                break;
            case BOTTOM:
                if (leftToRight) {
                    int xp[] = {x - (tabIndex == 0 || isSelected ? h - 5 : 0), x, x + 4, x + w - 3, x + w - 1, x + w - 1};
                    int yp[] = {y, y + h - 5, y + h - 1, y + h - 1, y + h - 5, y};
                    int np = yp.length;
                    tabRegion = new Polygon(xp, yp, np);
                }
                else {
                    int xp[] = {x, x, x + 2, x + w - 5, x + w - 1, x + w - 1 + (tabIndex == 0 || isSelected ? h - 5 : 0)};
                    int yp[] = {y, y + h - 5, y + h - 1, y + h - 1, y + h - 5, y};
                    int np = yp.length;
                    tabRegion = new Polygon(xp, yp, np);
                }
                break;
            case TOP:
            default:
                if (leftToRight) {
                    int xp[] = {x - (tabIndex == 0 || isSelected ? h - 5 : 0), x, x + 4, x + w - 3, x + w - 1, x + w - 1};
                    int yp[] = {y + h, y + 3, y + 1, y + 1, y + 3, y + h};
                    int np = yp.length;
                    tabRegion = new Polygon(xp, yp, np);
                }
                else {
                    int xp[] = {x, x, x + 2, x + w - 5, x + w - 1, x + w - 1 + (tabIndex == 0 || isSelected ? h - 5 : 0)};
                    int yp[] = {y + h, y + 3, y + 1, y + 1, y + 3, y + h};
                    int np = yp.length;
                    tabRegion = new Polygon(xp, yp, np);
                }
        }
    }

    protected void paintExcelTabBackground(Graphics g, int tabPlacement,
                                           int tabIndex, int x, int y, int w, int h, boolean isSelected) {
        boolean leftToRight = _tabPane.getComponentOrientation().isLeftToRight();
        switch (tabPlacement) {
            case LEFT:
                if (!isSelected) {
                    if ((leftToRight && tabIndex == 0) || (!leftToRight && tabIndex == _tabPane.getTabCount() - 1)) {
                        int xp[] = {x + w, x, x, x + w};
                        int yp[] = {y - 5, y + 5, y + h - 5, y + h + 6};
                        int np = yp.length;
                        tabRegion = new Polygon(xp, yp, np);
                    }
                    else {
                        int xp[] = {x + w, x + 9, x, x, x + w};
                        int yp[] = {y + 8, y + 2, y + 6, y + h - 5, y + h + 6};
                        int np = yp.length;
                        tabRegion = new Polygon(xp, yp, np);
                    }
                }
                else {
                    int xp[] = {x + w, x, x, x + w};
                    int yp[] = {y - 5, y + 5, y + h - 5, y + h + 6};
                    int np = yp.length;
                    tabRegion = new Polygon(xp, yp, np);
                }
                break;
            case RIGHT:
                if (!isSelected) {
                    if ((leftToRight && tabIndex == 0) || (!leftToRight && tabIndex == _tabPane.getTabCount() - 1)) {
                        int xp[] = {x, x + w - 1, x + w - 1, x};
                        int yp[] = {y - 5, y + 5, y + h - 5, y + h + 6};
                        int np = yp.length;
                        tabRegion = new Polygon(xp, yp, np);
                    }
                    else {
                        int xp[] = {x, x + w - 10, x + w - 1, x + w - 1, x};
                        int yp[] = {y + 8, y + 2, y + 6, y + h - 5,
                                y + h + 6};
                        int np = yp.length;
                        tabRegion = new Polygon(xp, yp, np);
                    }
                }
                else {
                    int xp[] = {x, x + w - 1, x + w - 1, x};
                    int yp[] = {y - 5, y + 5, y + h - 4, y + h + 6};
                    int np = yp.length;
                    tabRegion = new Polygon(xp, yp, np);
                }
                break;
            case BOTTOM:
                if (!isSelected) {
                    if ((leftToRight && tabIndex == 0) || (!leftToRight && tabIndex == _tabPane.getTabCount() - 1)) {
                        int xp[] = {x - 5, x + 5, x + w - 5, x + w + 5};
                        int yp[] = {y, y + h - 1, y + h - 1, y};
                        int np = yp.length;
                        tabRegion = new Polygon(xp, yp, np);
                    }
                    else {
                        int xp[] = {x + 7, x + 1, x + 5, x + w - 5, x + w + 5};
                        int yp[] = {y, y + h - 10, y + h - 1, y + h - 1, y};
                        int np = yp.length;
                        tabRegion = new Polygon(xp, yp, np);
                    }
                }
                else {
                    int xp[] = {x - 5, x + 5, x + w - 5, x + w + 5};
                    int yp[] = {y, y + h - 1, y + h - 1, y};
                    int np = yp.length;
                    tabRegion = new Polygon(xp, yp, np);
                }
                break;
            case TOP:
            default:
                if (!isSelected) {
                    if ((leftToRight && tabIndex == 0) || (!leftToRight && tabIndex == _tabPane.getTabCount() - 1)) {
                        int xp[] = {x - 6, x + 5, x + w - 5, x + w + 5};
                        int yp[] = {y + h, y, y, y + h};
                        int np = yp.length;
                        tabRegion = new Polygon(xp, yp, np);
                    }
                    else {
                        int xp[] = {x + 7, x + 1, x + 6, x + w - 5, x + w + 5};
                        int yp[] = {y + h, y + 9, y, y, y + h};
                        int np = yp.length;
                        tabRegion = new Polygon(xp, yp, np);
                    }
                }
                else {
                    int xp[] = {x - 6, x + 5, x + w - 5, x + w + 5};
                    int yp[] = {y + h, y, y, y + h};
                    int np = yp.length;
                    tabRegion = new Polygon(xp, yp, np);
                }
        }
    }

    protected void paintDefaultTabBackground(Graphics g, int tabPlacement,
                                             int tabIndex, int x, int y, int w, int h, boolean isSelected) {
        switch (tabPlacement) {
            case LEFT:
                if (isSelected) {
                    x = x + 1;
                    int xp[] = {x + w, x, x - 2, x - 2, x + w};
                    int yp[] = {y - 1, y - 1, y + 1, y + h + 2, y + h + 2};
                    int np = yp.length;
                    tabRegion = new Polygon(xp, yp, np);
                }
                else {
                    if (tabIndex < _tabPane.getSelectedIndex()) {
                        y = y + 1;
                        int xp[] = {x + w, x + 2, x, x, x + w};
                        int yp[] = {y + 1, y + 1, y + 3, y + h - 1,
                                y + h - 1};
                        int np = yp.length;
                        tabRegion = new Polygon(xp, yp, np);
                    }
                    else {
                        int xp[] = {x + w, x + 2, x, x, x + w};
                        int yp[] = {y + 1, y + 1, y + 3, y + h - 2,
                                y + h - 2};
                        int np = yp.length;
                        tabRegion = new Polygon(xp, yp, np);
                    }
                }
                break;
            case RIGHT:
                if (isSelected) {

                    int xp[] = {x, x + w - 1, x + w, x + w, x};
                    int yp[] = {y - 1, y - 1, y + 1, y + h + 2, y + h + 2};
                    int np = yp.length;
                    tabRegion = new Polygon(xp, yp, np);
                }
                else {
                    if (tabIndex < _tabPane.getSelectedIndex()) {
                        y = y + 1;
                        int xp[] = {x, x + w - 3, x + w - 1, x + w - 1, x};
                        int yp[] = {y + 1, y + 1, y + 3, y + h - 1,
                                y + h - 1};
                        int np = yp.length;
                        tabRegion = new Polygon(xp, yp, np);
                    }
                    else {
                        int xp[] = {x, x + w - 2, x + w - 1, x + w - 1, x};
                        int yp[] = {y + 1, y + 1, y + 3, y + h - 2,
                                y + h - 2};
                        int np = yp.length;
                        tabRegion = new Polygon(xp, yp, np);
                    }
                }
                break;
            case BOTTOM:
                if (isSelected) {
                    int xp[] = {x, x, x + 2, x + w + 2, x + w + 2};
                    int yp[] = {y + h, y, y - 2, y - 2, y + h};
                    int np = yp.length;
                    tabRegion = new Polygon(xp, yp, np);
                }
                else {
                    int xp[] = {x + 1, x + 1, x + 1, x + w - 1, x + w - 1};
                    int yp[] = {y + h - 1, y + 2, y, y, y + h - 1};
                    int np = yp.length;
                    tabRegion = new Polygon(xp, yp, np);
                }
                break;
            case TOP:
            default:
                if (isSelected) {
                    int xp[] = {x, x, x + 2, x + w + 2, x + w + 2};
                    int yp[] = {y + h + 1, y, y - 2, y - 2, y + h + 1};
                    int np = yp.length;
                    tabRegion = new Polygon(xp, yp, np);
                }
                else {
                    int xp[] = {x + 1, x + 1, x + 3, x + w - 1, x + w - 1};
                    int yp[] = {y + h, y + 2, y, y, y + h};
                    int np = yp.length;
                    tabRegion = new Polygon(xp, yp, np);
                }
        }
    }

    protected void paintTabBackgroundMouseOver(Graphics g, int tabPlacement, int tabIndex,
                                               int x, int y, int w, int h, boolean isSelected, Color backgroundUnselectedColorStart, Color backgroundUnselectedColorEnd) {
        Graphics2D g2d = (Graphics2D) g;

        Polygon polygon = null;

        switch (tabPlacement) {
            case LEFT:
                if (tabIndex < _tabPane.getSelectedIndex()) {

                    int xp[] = {x + w, x + 2, x, x, x + 2, x + w};
                    int yp[] = {y + 2, y + 2, y + 4, y + h - 1, y + h,
                            y + h};
                    int np = yp.length;
                    polygon = new Polygon(xp, yp, np);

                }
                else {// tabIndex > _tabPane.getSelectedIndex()

                    int xp[] = {x + w, x + 2, x, x, x + 2, x + w};
                    int yp[] = {y + 1, y + 1, y + 3, y + h - 3,
                            y + h - 2, y + h - 2};
                    int np = yp.length;
                    polygon = new Polygon(xp, yp, np);

                }
                JideSwingUtilities.fillGradient(g2d, polygon, backgroundUnselectedColorStart, backgroundUnselectedColorEnd, false);
                break;
            case RIGHT:
                if (tabIndex < _tabPane.getSelectedIndex()) {
                    int xp[] = {x, x + w - 3, x + w - 1, x + w - 1,
                            x + w - 3, x};
                    int yp[] = {y + 2, y + 2, y + 4, y + h - 1, y + h,
                            y + h};
                    int np = yp.length;
                    polygon = new Polygon(xp, yp, np);
                }
                else {
                    int xp[] = {x, x + w - 2, x + w - 1, x + w - 1,
                            x + w - 3, x};
                    int yp[] = {y + 1, y + 1, y + 3, y + h - 3,
                            y + h - 2, y + h - 2};
                    int np = yp.length;
                    polygon = new Polygon(xp, yp, np);
                }
                JideSwingUtilities.fillGradient(g2d, polygon, backgroundUnselectedColorEnd, backgroundUnselectedColorStart, false);
                break;
            case BOTTOM:
                int xp[] = {x + 1, x + 1, x + 1, x + w - 1, x + w - 1};
                int yp[] = {y + h - 2, y + 2, y, y, y + h - 2};
                int np = yp.length;
                polygon = new Polygon(xp, yp, np);
                JideSwingUtilities.fillGradient(g2d, polygon, backgroundUnselectedColorEnd, backgroundUnselectedColorStart, true);
                break;
            case TOP:
            default:
                int xp1[] = {x + 1, x + 1, x + 3, x + w - 1, x + w - 1};
                int yp1[] = {y + h, y + 2, y, y, y + h};
                int np1 = yp1.length;
                polygon = new Polygon(xp1, yp1, np1);
                JideSwingUtilities.fillGradient(g2d, polygon, backgroundUnselectedColorStart, backgroundUnselectedColorEnd, true);
        }
    }

    protected void paintVsnetTabBackground(Graphics g, int tabPlacement,
                                           int tabIndex, int x, int y, int w, int h, boolean isSelected) {
        int xp[];
        int yp[];
        switch (tabPlacement) {
            case LEFT:
                xp = new int[]{x + 1, x + 1, x + w, x + w};
                yp = new int[]{y + h - 1, y + 1, y + 1, y + h - 1};
                break;
            case RIGHT:
                xp = new int[]{x, x, x + w - 1, x + w - 1};
                yp = new int[]{y + h - 1, y + 1, y + 1, y + h - 1};
                break;
            case BOTTOM:
                xp = new int[]{x + 1, x + 1, x + w - 1, x + w - 1};
                yp = new int[]{y + h - 1, y, y, y + h - 1};
                break;
            case TOP:
            default:
                xp = new int[]{x + 1, x + 1, x + w - 1, x + w - 1};
                yp = new int[]{y + h, y + 1, y + 1, y + h};
                break;
        }
        int np = yp.length;
        tabRegion = new Polygon(xp, yp, np);
    }

    protected void paintFlatTabBackground(Graphics g, int tabPlacement, int tabIndex, int x, int y, int w, int h, boolean isSelected) {
        switch (tabPlacement) {
            case LEFT:
                int xp1[] = {x + 1, x + 1, x + w, x + w};
                int yp1[] = {y + h, y + 1, y + 1, y + h};
                int np1 = yp1.length;
                tabRegion = new Polygon(xp1, yp1, np1);
                break;
            case RIGHT:
                int xp2[] = {x, x, x + w - 1, x + w - 1};
                int yp2[] = {y + h, y + 1, y + 1, y + h};
                int np2 = yp2.length;
                tabRegion = new Polygon(xp2, yp2, np2);
                break;
            case BOTTOM:
                int xp3[] = {x + 1, x + 1, x + w, x + w};
                int yp3[] = {y + h - 1, y, y, y + h - 1};
                int np3 = yp3.length;
                tabRegion = new Polygon(xp3, yp3, np3);
                break;
            case TOP:
            default:
                int xp4[] = {x, x + 1, x + w, x + w};
                int yp4[] = {y + h, y + 1, y + 1, y + h};
                int np4 = yp4.length;
                tabRegion = new Polygon(xp4, yp4, np4);
        }
    }

    protected void paintButtonTabBackground(Graphics g, int tabPlacement,
                                            int tabIndex, int x, int y, int w, int h, boolean isSelected) {
        int xp[] = {x, x, x + w, x + w};
        int yp[] = {y + h, y, y, y + h};
        int np = yp.length;
        tabRegion = new Polygon(xp, yp, np);
    }

    protected void paintContentBorder(Graphics g, int tabPlacement, int selectedIndex) {
        int width = _tabPane.getWidth();
        int height = _tabPane.getHeight();
        Insets insets = _tabPane.getInsets();

        int x = insets.left;
        int y = insets.top;
        int w = width - insets.right - insets.left;
        int h = height - insets.top - insets.bottom;

        int temp = -1;
        Dimension lsize = new Dimension(0, 0);
        Dimension tsize = new Dimension(0, 0);

        if (isTabLeadingComponentVisible()) {
            lsize = _tabLeadingComponent.getPreferredSize();
        }
        if (isTabTrailingComponentVisible()) {
            tsize = _tabTrailingComponent.getPreferredSize();
        }

        switch (tabPlacement) {
            case LEFT:
                x += calculateTabAreaWidth(tabPlacement, _runCount, _maxTabWidth);
                if (isTabLeadingComponentVisible()) {
                    if (lsize.width > calculateTabAreaWidth(tabPlacement, _runCount, _maxTabWidth)) {
                        x = insets.left + lsize.width;
                        temp = _tabLeadingComponent.getSize().width;
                    }
                }
                if (isTabTrailingComponentVisible()) {
                    if (_maxTabWidth < tsize.width
                            && temp < tsize.width) {
                        x = insets.left + tsize.width;
                    }
                }
                w -= (x - insets.left);
                break;
            case RIGHT:
                w -= calculateTabAreaWidth(tabPlacement, _runCount, _maxTabWidth);
                break;
            case BOTTOM:
                h -= calculateTabAreaHeight(tabPlacement, _runCount, _maxTabHeight);
                break;
            case TOP:
            default:
                y += calculateTabAreaHeight(tabPlacement, _runCount, _maxTabHeight);
                if (isTabLeadingComponentVisible()) {
                    if (lsize.height > calculateTabAreaHeight(tabPlacement, _runCount, _maxTabHeight)) {
                        y = insets.top + lsize.height;
                        temp = lsize.height;
                    }
                }
                if (isTabTrailingComponentVisible()) {
                    if (_maxTabHeight < tsize.height
                            && temp < tsize.height) {
                        y = insets.top + tsize.height;
                    }
                }
                h -= (y - insets.top);
        }

        if (getTabShape() != JideTabbedPane.SHAPE_BOX) {

            // Fill region behind content area
            paintContentBorder(g, x, y, w, h);

            switch (tabPlacement) {
                case LEFT:
                    paintContentBorderLeftEdge(g, tabPlacement, selectedIndex, x, y, w, h);
                    break;
                case RIGHT:
                    paintContentBorderRightEdge(g, tabPlacement, selectedIndex, x, y, w, h);
                    break;
                case BOTTOM:
                    paintContentBorderBottomEdge(g, tabPlacement, selectedIndex, x, y, w, h);
                    break;
                case TOP:
                default:
                    paintContentBorderTopEdge(g, tabPlacement, selectedIndex, x, y, w, h);
                    break;

            }
        }

    }

    protected void paintContentBorderLeftEdge(Graphics g, int tabPlacement,
                                              int selectedIndex, int x, int y, int w, int h) {

    }

    protected void paintContentBorderRightEdge(Graphics g, int tabPlacement,
                                               int selectedIndex, int x, int y, int w, int h) {

    }

    protected void paintContentBorder(Graphics g, int x, int y, int w, int h) {
        if (!PAINT_CONTENT_BORDER) {
            return;
        }

        if (_tabPane.isOpaque()) {
            g.setColor(_tabBackground);
            g.fillRect(x, y, w, h);
        }
    }

    protected Color getBorderEdgeColor() {
        if ("true".equals(SecurityUtils.getProperty("shadingtheme", "false"))) {
            return _shadow;
        }
        else {
            return _lightHighlight;
        }
    }

    protected void paintContentBorderTopEdge(Graphics g, int tabPlacement,
                                             int selectedIndex,
                                             int x, int y, int w, int h) {

        if (!PAINT_CONTENT_BORDER_EDGE) {
            return;
        }

        if (selectedIndex < 0) {
            return;
        }

        if (!_tabPane.isTabShown()) {
            return;
        }

        Rectangle selRect = getTabBounds(selectedIndex, _calcRect);

        g.setColor(getBorderEdgeColor());

        // Draw unbroken line if tabs are not on TOP, OR
        // selected tab is not in run adjacent to content, OR
        // selected tab is not visible (SCROLL_TAB_LAYOUT)
        //
        if (tabPlacement != TOP || selectedIndex < 0 ||
                /*(selRect.y + selRect.height + 1 < y) ||*/
                (selRect.x < x || selRect.x > x + w)) {
            g.drawLine(x, y, x + w - 1, y);
        }
        else {
            // Break line to show visual connection to selected tab
            g.drawLine(x, y, selRect.x, y);
            if (!getBorderEdgeColor().equals(_lightHighlight)) {
                if (selRect.x + selRect.width < x + w - 2) {
                    g.drawLine(selRect.x + selRect.width - 1, y,
                            selRect.x + selRect.width - 1, y);
                    g.drawLine(selRect.x + selRect.width, y, x + w - 1, y);
                }
                else {
                    g.drawLine(x + w - 2, y, x + w - 1, y);
                }
            }
            else {
                if (selRect.x + selRect.width < x + w - 2) {
                    g.setColor(_darkShadow);
                    g.drawLine(selRect.x + selRect.width - 1, y,
                            selRect.x + selRect.width - 1, y);
                    g.setColor(_lightHighlight);
                    g.drawLine(selRect.x + selRect.width, y, x + w - 1, y);
                }
                else {
                    g.setColor(_selectedColor == null ?
                            _tabPane.getBackground() : _selectedColor);
                    g.drawLine(x + w - 2, y, x + w - 1, y);
                }
            }
        }
    }

    protected void paintContentBorderBottomEdge(Graphics g, int tabPlacement,
                                                int selectedIndex,
                                                int x, int y, int w, int h) {
        if (!PAINT_CONTENT_BORDER_EDGE) {
            return;
        }

        if (selectedIndex < 0) {
            return;
        }

        if (!_tabPane.isTabShown()) {
            return;
        }

        Rectangle selRect = getTabBounds(selectedIndex, _calcRect);

        // Draw unbroken line if tabs are not on BOTTOM, OR
        // selected tab is not in run adjacent to content, OR
        // selected tab is not visible (SCROLL_TAB_LAYOUT)
        //
        if (tabPlacement != BOTTOM || selectedIndex < 0 ||
                /*(selRect.y - 1 > h) ||*/
                (selRect.x < x || selRect.x > x + w)) {
            g.setColor(getBorderEdgeColor());
            g.drawLine(x, y + h - 1, x + w - 2, y + h - 1);
        }
        else {
            if (!getBorderEdgeColor().equals(_lightHighlight)) {
                g.setColor(getBorderEdgeColor());
                g.drawLine(x, y + h - 1, selRect.x - 1, y + h - 1);
                g.drawLine(selRect.x, y + h - 1, selRect.x, y + h - 1);
                if (selRect.x + selRect.width < x + w - 2) {
                    g.drawLine(selRect.x + selRect.width - 1, y + h - 1, x + w - 2, y + h - 1); // dark line to the end
                }
            }
            else {
                // Break line to show visual connection to selected tab
                g.setColor(_darkShadow); // dark line at the beginning
                g.drawLine(x, y + h - 1, selRect.x - 1, y + h - 1);
                g.setColor(_lightHighlight); // light line to meet with tab
                // border
                g.drawLine(selRect.x, y + h - 1, selRect.x, y + h - 1);
                if (selRect.x + selRect.width < x + w - 2) {
                    g.setColor(_darkShadow);
                    g.drawLine(selRect.x + selRect.width - 1, y + h - 1, x + w - 2, y + h - 1); // dark line to the end
                }
            }
        }

    }


    protected void ensureCurrentLayout() {
        /*
         * If tabPane doesn't have a peer yet, the validate() call will
         * silently fail.  We handle that by forcing a layout if tabPane
         * is still invalid.  See bug 4237677.
         */
        if (!_tabPane.isValid()) {
            TabbedPaneLayout layout = (TabbedPaneLayout) _tabPane.getLayout();
            layout.calculateLayoutInfo();
        }
    }

    private void updateCloseButtons() {
        boolean leftToRight = _tabPane.getComponentOrientation().isLeftToRight();
        if (scrollableTabLayoutEnabled() && isShowCloseButton() && isShowCloseButtonOnTab()) {
            for (int i = 0; i < _closeButtons.length; i++) {
                if (_tabPane.isShowCloseButtonOnSelectedTab()) {
                    if (i != _tabPane.getSelectedIndex()) {
                        _closeButtons[i].setBounds(0, 0, 0, 0);
                        continue;
                    }
                }
                else {
                    if (i >= _rects.length) {
                        _closeButtons[i].setBounds(0, 0, 0, 0);
                        continue;
                    }
                }

                if (!_tabPane.isTabClosableAt(i)) {
                    _closeButtons[i].setBounds(0, 0, 0, 0);
                    continue;
                }
                Dimension size = _closeButtons[i].getPreferredSize();

                Rectangle bounds;
                if (_closeButtonAlignment == SwingConstants.TRAILING) {
                    if (_tabPane.getTabPlacement() == JideTabbedPane.TOP || _tabPane.getTabPlacement() == JideTabbedPane.BOTTOM) {
                        if (leftToRight) {
                            bounds = new Rectangle(_rects[i].x + _rects[i].width - size.width - _closeButtonRightMargin,
                                    _rects[i].y + (_rects[i].height - size.height) / 2, size.width, size.height);
                            bounds.x -= getTabGap();
                        }
                        else {
                            bounds = new Rectangle(_rects[i].x + _closeButtonLeftMargin + getTabGap(), _rects[i].y + (_rects[i].height - size.height) / 2, size.width, size.height);
                        }
                    }
                    else /*if (_tabPane.getTabPlacement() == JideTabbedPane.LEFT || _tabPane.getTabPlacement() == JideTabbedPane.RIGHT)*/ {
                        bounds = new Rectangle(_rects[i].x + (_rects[i].width - size.width) / 2, _rects[i].y + _rects[i].height - size.height - _closeButtonRightMargin, size.width, size.height);
                        bounds.y -= getTabGap();
                    }
                }
                else {
                    if (_tabPane.getTabPlacement() == JideTabbedPane.TOP || _tabPane.getTabPlacement() == JideTabbedPane.BOTTOM) {
                        if (leftToRight) {
                            bounds = new Rectangle(_rects[i].x + _closeButtonLeftMargin + getTabGap(), _rects[i].y + (_rects[i].height - size.height) / 2, size.width, size.height);
                        }
                        else {
                            bounds = new Rectangle(_rects[i].x + _rects[i].width - size.width - _closeButtonRightMargin,
                                    _rects[i].y + (_rects[i].height - size.height) / 2, size.width, size.height);
                            bounds.x -= getTabGap();
                        }
                    }
                    else if (_tabPane.getTabPlacement() == JideTabbedPane.LEFT) {
                        bounds = new Rectangle(_rects[i].x + (_rects[i].width - size.width) / 2, _rects[i].y + _closeButtonLeftMargin, size.width, size.height);
                    }
                    else /*if (_tabPane.getTabPlacement() == JideTabbedPane.RIGHT)*/ {
                        bounds = new Rectangle(_rects[i].x + (_rects[i].width - size.width) / 2 - 2, _rects[i].y + _closeButtonLeftMargin, size.width, size.height);
                    }
                }
                _closeButtons[i].setIndex(i);
                if (!bounds.equals(_closeButtons[i].getBounds())) {
                    _closeButtons[i].setBounds(bounds);
                }
                if (_tabPane.getSelectedIndex() == i) {
                    _closeButtons[i].setBackground(_selectedColor == null ? _tabPane.getBackgroundAt(i) : _selectedColor);
                }
                else {
                    _closeButtons[i].setBackground(_tabPane.getBackgroundAt(i));
                }
            }
        }
    }

    // TabbedPaneUI methods

    /**
     * Returns the bounds of the specified tab index.  The bounds are with respect to the JTabbedPane's coordinate
     * space.
     */
    @Override
    public Rectangle getTabBounds(JTabbedPane pane, int i) {
        ensureCurrentLayout();
        Rectangle tabRect = new Rectangle();
        return getTabBounds(i, tabRect);
    }

    @Override
    public int getTabRunCount(JTabbedPane pane) {
        ensureCurrentLayout();
        return _runCount;
    }

    /**
     * Returns the tab index which intersects the specified point in the JTabbedPane's coordinate space.
     */
    @Override
    public int tabForCoordinate(JTabbedPane pane, int x, int y) {
        ensureCurrentLayout();
        Point p = new Point(x, y);

        if (scrollableTabLayoutEnabled()) {
            translatePointToTabPanel(x, y, p);
        }
        int tabCount = _tabPane.getTabCount();
        for (int i = 0; i < tabCount; i++) {
            if (_rects[i].contains(p.x, p.y)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Returns the bounds of the specified tab in the coordinate space of the JTabbedPane component. This is required
     * because the tab rects are by default defined in the coordinate space of the component where they are rendered,
     * which could be the JTabbedPane (for WRAP_TAB_LAYOUT) or a ScrollableTabPanel (SCROLL_TAB_LAYOUT). This method
     * should be used whenever the tab rectangle must be relative to the JTabbedPane itself and the result should be
     * placed in a designated Rectangle object (rather than instantiating and returning a new Rectangle each time). The
     * tab index parameter must be a valid tabbed pane tab index (0 to tab count - 1, inclusive).  The destination
     * rectangle parameter must be a valid <code>Rectangle</code> instance. The handling of invalid parameters is
     * unspecified.
     *
     * @param tabIndex the index of the tab
     * @param dest     the rectangle where the result should be placed
     * @return the resulting rectangle
     */
    protected Rectangle getTabBounds(int tabIndex, Rectangle dest) {
        if (_rects.length == 0) {
            return null;
        }
        // to make the index is in bound.
        if (tabIndex > _rects.length - 1) {
            tabIndex = _rects.length - 1;
        }
        if (tabIndex < 0) {
            tabIndex = 0;
        }

        dest.width = _rects[tabIndex].width;
        dest.height = _rects[tabIndex].height;

        if (scrollableTabLayoutEnabled()) { // SCROLL_TAB_LAYOUT
            // Need to translate coordinates based on viewport location &
            // view position
            Point vpp = _tabScroller.viewport.getLocation();
            Point viewp = _tabScroller.viewport.getViewPosition();
            dest.x = _rects[tabIndex].x + vpp.x - viewp.x;
            dest.y = _rects[tabIndex].y + vpp.y - viewp.y;

        }
        else { // WRAP_TAB_LAYOUT
            dest.x = _rects[tabIndex].x;
            dest.y = _rects[tabIndex].y;
        }
        return dest;
    }

    /**
     * Returns the tab index which intersects the specified point in the coordinate space of the component where the
     * tabs are actually rendered, which could be the JTabbedPane (for WRAP_TAB_LAYOUT) or a ScrollableTabPanel
     * (SCROLL_TAB_LAYOUT).
     */
    public int getTabAtLocation(int x, int y) {
        ensureCurrentLayout();

        int tabCount = _tabPane.getTabCount();
        for (int i = 0; i < tabCount; i++) {
            if (_rects[i].contains(x, y)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Returns the index of the tab closest to the passed in location, note that the returned tab may not contain the
     * location x,y.
     */
    private int getClosestTab(int x, int y) {
        int min = 0;
        int tabCount = Math.min(_rects.length, _tabPane.getTabCount());
        int max = tabCount;
        int tabPlacement = _tabPane.getTabPlacement();
        boolean useX = (tabPlacement == TOP || tabPlacement == BOTTOM);
        int want = (useX) ? x : y;
        Rectangle[] rects = new Rectangle[_rects.length];
        boolean needConvert = false;
        if (!useX || _tabPane.getComponentOrientation().isLeftToRight()) {
            System.arraycopy(_rects, 0, rects, 0, _rects.length);
        }
        else {
            needConvert = true;
            for (int i = 0; i < _rects.length; i++) {
                rects[i] = _rects[_rects.length - 1 - i];
            }
        }

        while (min != max) {
            int current = (max + min) >> 1;
            int minLoc;
            int maxLoc;

            if (useX) {
                minLoc = rects[current].x;
                maxLoc = minLoc + rects[current].width;
            }
            else {
                minLoc = rects[current].y;
                maxLoc = minLoc + rects[current].height;
            }
            if (want < minLoc) {
                max = current;
                if (min == max) {
                    int tabIndex = Math.max(0, current - 1);
                    return needConvert ? rects.length - 1 - tabIndex : tabIndex;
                }
            }
            else if (want >= maxLoc) {
                min = current;
                if (max - min <= 1) {
                    int tabIndex = Math.max(current + 1, tabCount - 1);
                    return needConvert ? rects.length - 1 - tabIndex : tabIndex;
                }
            }
            else {
                return needConvert ? rects.length - 1 - current : current;
            }
        }
        return needConvert ? rects.length - 1 - min : min;
    }

    /**
     * Returns a point which is translated from the specified point in the JTabbedPane's coordinate space to the
     * coordinate space of the ScrollableTabPanel. This is used for SCROLL_TAB_LAYOUT ONLY.
     */
    private Point translatePointToTabPanel(int srcx, int srcy, Point dest) {
        Point vpp = _tabScroller.viewport.getLocation();
        Point viewp = _tabScroller.viewport.getViewPosition();
        dest.x = srcx - vpp.x + viewp.x;
        dest.y = srcy - vpp.y + viewp.y;
        return dest;
    }

    // VsnetJideTabbedPaneUI methods

    protected Component getVisibleComponent() {
        return visibleComponent;
    }

    protected void setVisibleComponent(Component component) {
        if (visibleComponent != null && visibleComponent != component &&
                visibleComponent.getParent() == _tabPane) {
            visibleComponent.setVisible(false);
        }
        if (component != null && !component.isVisible()) {
            component.setVisible(true);
        }
        visibleComponent = component;
    }

    protected void assureRectsCreated(int tabCount) {
        int rectArrayLen = _rects.length;
        if (tabCount != rectArrayLen) {
            Rectangle[] tempRectArray = new Rectangle[tabCount];
            System.arraycopy(_rects, 0, tempRectArray, 0,
                    Math.min(rectArrayLen, tabCount));
            _rects = tempRectArray;
            for (int rectIndex = rectArrayLen; rectIndex < tabCount; rectIndex++) {
                _rects[rectIndex] = new Rectangle();
            }
        }

    }

    protected void expandTabRunsArray() {
        int rectLen = _tabRuns.length;
        int[] newArray = new int[rectLen + 10];
        System.arraycopy(_tabRuns, 0, newArray, 0, _runCount);
        _tabRuns = newArray;
    }

    protected int getRunForTab(int tabCount, int tabIndex) {
        for (int i = 0; i < _runCount; i++) {
            int first = _tabRuns[i];
            int last = lastTabInRun(tabCount, i);
            if (tabIndex >= first && tabIndex <= last) {
                return i;
            }
        }
        return 0;
    }

    protected int lastTabInRun(int tabCount, int run) {
        if (_runCount == 1) {
            return tabCount - 1;
        }
        int nextRun = (run == _runCount - 1 ? 0 : run + 1);
        if (_tabRuns[nextRun] == 0) {
            return tabCount - 1;
        }
        return _tabRuns[nextRun] - 1;
    }

    protected int getTabRunOverlay(int tabPlacement) {
        return _tabRunOverlay;
    }

    protected int getTabRunIndent(int tabPlacement, int run) {
        return 0;
    }

    protected boolean shouldPadTabRun(int tabPlacement, int run) {
        return _runCount > 1;
    }

    protected boolean shouldRotateTabRuns(int tabPlacement) {
        return true;
    }

    /**
     * Returns the text View object required to render stylized text (HTML) for the specified tab or null if no
     * specialized text rendering is needed for this tab. This is provided to support html rendering inside tabs.
     *
     * @param tabIndex the index of the tab
     * @return the text view to render the tab's text or null if no specialized rendering is required
     */
    protected View getTextViewForTab(int tabIndex) {
        if (htmlViews != null && tabIndex < htmlViews.size()) {
            return (View) htmlViews.elementAt(tabIndex);
        }
        return null;
    }

    protected int calculateTabHeight(int tabPlacement, int tabIndex, FontMetrics metrics) {
        int height = 0;
        if (tabPlacement == JideTabbedPane.TOP || tabPlacement == JideTabbedPane.BOTTOM) {
            View v = getTextViewForTab(tabIndex);
            if (v != null) {
                // html
                height += (int) v.getPreferredSpan(View.Y_AXIS);
            }
            else {
                // plain text
                height += metrics.getHeight();
            }
            Icon icon = _tabPane.getIconForTab(tabIndex);
            Insets tabInsets = getTabInsets(tabPlacement, tabIndex);

            if (icon != null) {
                height = Math.max(height, icon.getIconHeight());
            }
            height += tabInsets.top + tabInsets.bottom + 2;
        }
        else {
            Icon icon = _tabPane.getIconForTab(tabIndex);
            Insets tabInsets = getTabInsets(tabPlacement, tabIndex);
            height = tabInsets.top + tabInsets.bottom + 3;

            if (icon != null) {
                height += icon.getIconHeight() + _textIconGap;
            }
            View v = getTextViewForTab(tabIndex);
            if (v != null) {
                // html
                height += (int) v.getPreferredSpan(View.X_AXIS);
            }
            else {
                // plain text
                String title = getCurrentDisplayTitleAt(_tabPane, tabIndex);
                height += SwingUtilities.computeStringWidth(metrics, title);
            }

            // for gripper
            if (_tabPane.isShowGripper()) {
                height += _gripperHeight;
            }

            if (scrollableTabLayoutEnabled() && isShowCloseButton() && isShowCloseButtonOnTab() && _tabPane.isTabClosableAt(tabIndex)) {
                if (_tabPane.isShowCloseButtonOnSelectedTab()) {
                    if (_tabPane.getSelectedIndex() == tabIndex) {
                        height += _closeButtons[tabIndex].getPreferredSize().height + _closeButtonRightMargin + _closeButtonLeftMargin;
                    }
                }
                else {
                    height += _closeButtons[tabIndex].getPreferredSize().height + _closeButtonRightMargin + _closeButtonLeftMargin;
                }
            }

//            height += _tabRectPadding;
        }
        return height;
    }

    protected int calculateMaxTabHeight(int tabPlacement) {
        int tabCount = _tabPane.getTabCount();
        int result = 0;
        for (int i = 0; i < tabCount; i++) {
            FontMetrics metrics = getFontMetrics(i);
            result = Math.max(calculateTabHeight(tabPlacement, i, metrics), result);
        }
        return result;
    }

    protected int calculateTabWidth(int tabPlacement, int tabIndex, FontMetrics metrics) {
        int width = 0;
        if (tabPlacement == JideTabbedPane.TOP || tabPlacement == JideTabbedPane.BOTTOM) {
            Icon icon = _tabPane.getIconForTab(tabIndex);
            Insets tabInsets = getTabInsets(tabPlacement, tabIndex);
            width = tabInsets.left + tabInsets.right + 3 + getTabGap();

            if (icon != null) {
                width += icon.getIconWidth() + _textIconGap;
            }
            View v = getTextViewForTab(tabIndex);
            if (v != null) {
                // html
                width += (int) v.getPreferredSpan(View.X_AXIS);
            }
            else {
                // plain text
                String title = getCurrentDisplayTitleAt(_tabPane, tabIndex);
                while (title == null || title.length() < 3)
                    title += " ";
                width += SwingUtilities.computeStringWidth(metrics, title);
            }

            // for gripper
            if (_tabPane.isShowGripper()) {
                width += _gripperWidth;
            }

            if (scrollableTabLayoutEnabled() && isShowCloseButton() && isShowCloseButtonOnTab() && _tabPane.isTabClosableAt(tabIndex)) {
                if (_tabPane.isShowCloseButtonOnSelectedTab()) {
                    if (_tabPane.getSelectedIndex() == tabIndex) {
                        width += _closeButtons[tabIndex].getPreferredSize().width + _closeButtonRightMargin + _closeButtonLeftMargin;
                    }
                }
                else {
                    width += _closeButtons[tabIndex].getPreferredSize().width + _closeButtonRightMargin + _closeButtonLeftMargin;
                }
            }

//            width += _tabRectPadding;
        }
        else {
            View v = getTextViewForTab(tabIndex);
            if (v != null) {
                // html
                width += (int) v.getPreferredSpan(View.Y_AXIS);
            }
            else {
                // plain text
                width += metrics.getHeight();
            }
            Icon icon = _tabPane.getIconForTab(tabIndex);
            Insets tabInsets = getTabInsets(tabPlacement, tabIndex);

            if (icon != null) {
                width = Math.max(width, icon.getIconWidth());
            }
            width += tabInsets.left + tabInsets.right + 2;
        }
        return width;
    }

    protected int calculateMaxTabWidth(int tabPlacement) {
        int tabCount = _tabPane.getTabCount();
        int result = 0;
        for (int i = 0; i < tabCount; i++) {
            FontMetrics metrics = getFontMetrics(i);
            result = Math.max(calculateTabWidth(tabPlacement, i, metrics), result);
        }
        return result;
    }

    protected int calculateTabAreaHeight(int tabPlacement, int horizRunCount, int maxTabHeight) {
        if (!_tabPane.isTabShown()) {
            return 0;
        }
        Insets tabAreaInsets = getTabAreaInsets(tabPlacement);
        int tabRunOverlay = getTabRunOverlay(tabPlacement);
        return (horizRunCount > 0 ? horizRunCount * (maxTabHeight - tabRunOverlay) + tabRunOverlay + tabAreaInsets.top + tabAreaInsets.bottom : 0);
    }

    protected int calculateTabAreaWidth(int tabPlacement, int vertRunCount, int maxTabWidth) {
        if (!_tabPane.isTabShown()) {
            return 0;
        }
        Insets tabAreaInsets = getTabAreaInsets(tabPlacement);
        int tabRunOverlay = getTabRunOverlay(tabPlacement);
        return (vertRunCount > 0 ? vertRunCount * (maxTabWidth - tabRunOverlay) + tabRunOverlay + tabAreaInsets.left + tabAreaInsets.right : 0);
    }

    protected Insets getTabInsets(int tabPlacement, int tabIndex) {
        rotateInsets(_tabInsets, _currentTabInsets, tabPlacement);
        return _currentTabInsets;
    }

    protected Insets getSelectedTabPadInsets(int tabPlacement) {
        rotateInsets(_selectedTabPadInsets, _currentPadInsets, tabPlacement);
        return _currentPadInsets;
    }

    protected Insets getTabAreaInsets(int tabPlacement) {
        rotateInsets(_tabAreaInsets, _currentTabAreaInsets, tabPlacement);
        return _currentTabAreaInsets;
    }

    protected Insets getContentBorderInsets(int tabPlacement) {
        rotateInsets(_tabPane.getContentBorderInsets(), _currentContentBorderInsets, tabPlacement);
        if (_ignoreContentBorderInsetsIfNoTabs && !_tabPane.isTabShown())
            return new Insets(0, 0, 0, 0);
        else
            return _currentContentBorderInsets;

    }

    protected FontMetrics getFontMetrics(int tab) {
        Font font = null;
        int selectedIndex = _tabPane.getSelectedIndex();
        if (selectedIndex == tab && _tabPane.getSelectedTabFont() != null) {
            font = _tabPane.getSelectedTabFont();
        }
        else {
            font = _tabPane.getFont();
        }

        if (selectedIndex == tab && _tabPane.isBoldActiveTab() && font.getStyle() != Font.BOLD) {
            font = font.deriveFont(Font.BOLD);
        }
        return _tabPane.getFontMetrics(font);
    }

    // Tab Navigation methods

    protected void navigateSelectedTab(int direction) {
        int tabPlacement = _tabPane.getTabPlacement();
        int current = _tabPane.getSelectedIndex();
        int tabCount = _tabPane.getTabCount();

        boolean leftToRight = _tabPane.getComponentOrientation().isLeftToRight();

        // If we have no tabs then don't navigate.
        if (tabCount <= 0) {
            return;
        }

        int offset;
        switch (tabPlacement) {
            case NEXT:
                selectNextTab(current);
                break;
            case PREVIOUS:
                selectPreviousTab(current);
                break;
            case LEFT:
            case RIGHT:
                switch (direction) {
                    case NORTH:
                        selectPreviousTabInRun(current);
                        break;
                    case SOUTH:
                        selectNextTabInRun(current);
                        break;
                    case WEST:
                        offset = getTabRunOffset(tabPlacement, tabCount, current, false);
                        selectAdjacentRunTab(tabPlacement, current, offset);
                        break;
                    case EAST:
                        offset = getTabRunOffset(tabPlacement, tabCount, current, true);
                        selectAdjacentRunTab(tabPlacement, current, offset);
                        break;
                    default:
                }
                break;
            case BOTTOM:
            case TOP:
            default:
                switch (direction) {
                    case NORTH:
                        offset = getTabRunOffset(tabPlacement, tabCount, current, false);
                        selectAdjacentRunTab(tabPlacement, current, offset);
                        break;
                    case SOUTH:
                        offset = getTabRunOffset(tabPlacement, tabCount, current, true);
                        selectAdjacentRunTab(tabPlacement, current, offset);
                        break;
                    case EAST:
                        if (leftToRight) {
                            selectNextTabInRun(current);
                        }
                        else {
                            selectPreviousTabInRun(current);
                        }
                        break;
                    case WEST:
                        if (leftToRight) {
                            selectPreviousTabInRun(current);
                        }
                        else {
                            selectNextTabInRun(current);
                        }
                        break;
                    default:
                }
        }
    }

    protected void selectNextTabInRun(int current) {
        int tabCount = _tabPane.getTabCount();
        int tabIndex = getNextTabIndexInRun(tabCount, current);

        while (tabIndex != current && !_tabPane.isEnabledAt(tabIndex)) {
            tabIndex = getNextTabIndexInRun(tabCount, tabIndex);
        }
        _tabPane.setSelectedIndex(tabIndex);
    }

    protected void selectPreviousTabInRun(int current) {
        int tabCount = _tabPane.getTabCount();
        int tabIndex = getPreviousTabIndexInRun(tabCount, current);

        while (tabIndex != current && !_tabPane.isEnabledAt(tabIndex)) {
            tabIndex = getPreviousTabIndexInRun(tabCount, tabIndex);
        }
        _tabPane.setSelectedIndex(tabIndex);
    }

    protected void selectNextTab(int current) {
        int tabIndex = getNextTabIndex(current);

        while (tabIndex != current && !_tabPane.isEnabledAt(tabIndex)) {
            tabIndex = getNextTabIndex(tabIndex);
        }
        _tabPane.setSelectedIndex(tabIndex);
    }

    protected void selectPreviousTab(int current) {
        int tabIndex = getPreviousTabIndex(current);

        while (tabIndex != current && !_tabPane.isEnabledAt(tabIndex)) {
            tabIndex = getPreviousTabIndex(tabIndex);
        }
        _tabPane.setSelectedIndex(tabIndex);
    }

    protected void selectAdjacentRunTab(int tabPlacement,
                                        int tabIndex, int offset) {
        if (_runCount < 2) {
            return;
        }
        int newIndex;
        Rectangle r = _rects[tabIndex];
        switch (tabPlacement) {
            case LEFT:
            case RIGHT:
                newIndex = getTabAtLocation(r.x + (r.width >> 1) + offset,
                        r.y + (r.height >> 1));
                break;
            case BOTTOM:
            case TOP:
            default:
                newIndex = getTabAtLocation(r.x + (r.width >> 1),
                        r.y + (r.height >> 1) + offset);
        }
        if (newIndex != -1) {
            while (!_tabPane.isEnabledAt(newIndex) && newIndex != tabIndex) {
                newIndex = getNextTabIndex(newIndex);
            }
            _tabPane.setSelectedIndex(newIndex);
        }
    }

    protected int getTabRunOffset(int tabPlacement, int tabCount,
                                  int tabIndex, boolean forward) {
        int run = getRunForTab(tabCount, tabIndex);
        int offset;
        switch (tabPlacement) {
            case LEFT: {
                if (run == 0) {
                    offset = (forward ?
                            -(calculateTabAreaWidth(tabPlacement, _runCount, _maxTabWidth) - _maxTabWidth) :
                            -_maxTabWidth);

                }
                else if (run == _runCount - 1) {
                    offset = (forward ?
                            _maxTabWidth :
                            calculateTabAreaWidth(tabPlacement, _runCount, _maxTabWidth) - _maxTabWidth);
                }
                else {
                    offset = (forward ? _maxTabWidth : -_maxTabWidth);
                }
                break;
            }
            case RIGHT: {
                if (run == 0) {
                    offset = (forward ?
                            _maxTabWidth :
                            calculateTabAreaWidth(tabPlacement, _runCount, _maxTabWidth) - _maxTabWidth);
                }
                else if (run == _runCount - 1) {
                    offset = (forward ?
                            -(calculateTabAreaWidth(tabPlacement, _runCount, _maxTabWidth) - _maxTabWidth) :
                            -_maxTabWidth);
                }
                else {
                    offset = (forward ? _maxTabWidth : -_maxTabWidth);
                }
                break;
            }
            case BOTTOM: {
                if (run == 0) {
                    offset = (forward ?
                            _maxTabHeight :
                            calculateTabAreaHeight(tabPlacement, _runCount, _maxTabHeight) - _maxTabHeight);
                }
                else if (run == _runCount - 1) {
                    offset = (forward ?
                            -(calculateTabAreaHeight(tabPlacement, _runCount, _maxTabHeight) - _maxTabHeight) :
                            -_maxTabHeight);
                }
                else {
                    offset = (forward ? _maxTabHeight : -_maxTabHeight);
                }
                break;
            }
            case TOP:
            default: {
                if (run == 0) {
                    offset = (forward ?
                            -(calculateTabAreaHeight(tabPlacement, _runCount, _maxTabHeight) - _maxTabHeight) :
                            -_maxTabHeight);
                }
                else if (run == _runCount - 1) {
                    offset = (forward ?
                            _maxTabHeight :
                            calculateTabAreaHeight(tabPlacement, _runCount, _maxTabHeight) - _maxTabHeight);
                }
                else {
                    offset = (forward ? _maxTabHeight : -_maxTabHeight);
                }
            }
        }
        return offset;
    }

    protected int getPreviousTabIndex(int base) {
        int tabIndex = (base - 1 >= 0 ? base - 1 : _tabPane.getTabCount() - 1);
        return (tabIndex >= 0 ? tabIndex : 0);
    }

    protected int getNextTabIndex(int base) {
        return (base + 1) % _tabPane.getTabCount();
    }

    protected int getNextTabIndexInRun(int tabCount, int base) {
        if (_runCount < 2) {
            return getNextTabIndex(base);
        }
        int currentRun = getRunForTab(tabCount, base);
        int next = getNextTabIndex(base);
        if (next == _tabRuns[getNextTabRun(currentRun)]) {
            return _tabRuns[currentRun];
        }
        return next;
    }

    protected int getPreviousTabIndexInRun(int tabCount, int base) {
        if (_runCount < 2) {
            return getPreviousTabIndex(base);
        }
        int currentRun = getRunForTab(tabCount, base);
        if (base == _tabRuns[currentRun]) {
            int previous = _tabRuns[getNextTabRun(currentRun)] - 1;
            return (previous != -1 ? previous : tabCount - 1);
        }
        return getPreviousTabIndex(base);
    }

    protected int getPreviousTabRun(int baseRun) {
        int runIndex = (baseRun - 1 >= 0 ? baseRun - 1 : _runCount - 1);
        return (runIndex >= 0 ? runIndex : 0);
    }

    protected int getNextTabRun(int baseRun) {
        return (baseRun + 1) % _runCount;
    }

    public static void rotateInsets(Insets topInsets, Insets targetInsets, int targetPlacement) {
        switch (targetPlacement) {
            case LEFT:
                targetInsets.top = topInsets.left;
                targetInsets.left = topInsets.top;
                targetInsets.bottom = topInsets.right;
                targetInsets.right = topInsets.bottom;
                break;
            case BOTTOM:
                targetInsets.top = topInsets.bottom;
                targetInsets.left = topInsets.left;
                targetInsets.bottom = topInsets.top;
                targetInsets.right = topInsets.right;
                break;
            case RIGHT:
                targetInsets.top = topInsets.left;
                targetInsets.left = topInsets.bottom;
                targetInsets.bottom = topInsets.right;
                targetInsets.right = topInsets.top;
                break;
            case TOP:
            default:
                targetInsets.top = topInsets.top;
                targetInsets.left = topInsets.left;
                targetInsets.bottom = topInsets.bottom;
                targetInsets.right = topInsets.right;
        }
    }

    protected boolean requestFocusForVisibleComponent() {
        Component visibleComponent = getVisibleComponent();
        Component lastFocused = _tabPane.getLastFocusedComponent(visibleComponent);
        if (lastFocused != null && lastFocused.requestFocusInWindow()) {
            return true;
        }
        else if (visibleComponent != null && JideSwingUtilities.passesFocusabilityTest(visibleComponent)) { //  visibleComponent.isFocusTraversable()) {
            JideSwingUtilities.compositeRequestFocus(visibleComponent);
            return true;
        }
        else if (visibleComponent instanceof JComponent) {
            if (((JComponent) visibleComponent).requestDefaultFocus()) {
                return true;
            }
        }
        return false;
    }

    private static class RightAction extends AbstractAction {
        private static final long serialVersionUID = -1759791760116532857L;

        public void actionPerformed(ActionEvent e) {
            JTabbedPane pane = (JTabbedPane) e.getSource();
            BasicJideTabbedPaneUI ui = (BasicJideTabbedPaneUI) pane.getUI();
            ui.navigateSelectedTab(EAST);
        }
    }

    private static class LeftAction extends AbstractAction {
        public void actionPerformed(ActionEvent e) {
            JTabbedPane pane = (JTabbedPane) e.getSource();
            BasicJideTabbedPaneUI ui = (BasicJideTabbedPaneUI) pane.getUI();
            ui.navigateSelectedTab(WEST);
        }
    }

    private static class UpAction extends AbstractAction {
        private static final long serialVersionUID = -6961702501242792445L;

        public void actionPerformed(ActionEvent e) {
            JTabbedPane pane = (JTabbedPane) e.getSource();
            BasicJideTabbedPaneUI ui = (BasicJideTabbedPaneUI) pane.getUI();
            ui.navigateSelectedTab(NORTH);
        }
    }

    private static class DownAction extends AbstractAction {
        private static final long serialVersionUID = -453174268282628886L;

        public void actionPerformed(ActionEvent e) {
            JTabbedPane pane = (JTabbedPane) e.getSource();
            BasicJideTabbedPaneUI ui = (BasicJideTabbedPaneUI) pane.getUI();
            ui.navigateSelectedTab(SOUTH);
        }
    }

    private static class NextAction extends AbstractAction {
        public void actionPerformed(ActionEvent e) {
            JTabbedPane pane = (JTabbedPane) e.getSource();
            BasicJideTabbedPaneUI ui = (BasicJideTabbedPaneUI) pane.getUI();
            ui.navigateSelectedTab(NEXT);
        }
    }

    private static class PreviousAction extends AbstractAction {
        public void actionPerformed(ActionEvent e) {
            JTabbedPane pane = (JTabbedPane) e.getSource();
            BasicJideTabbedPaneUI ui = (BasicJideTabbedPaneUI) pane.getUI();
            ui.navigateSelectedTab(PREVIOUS);
        }
    }

    private static class PageUpAction extends AbstractAction {
        public void actionPerformed(ActionEvent e) {
            JTabbedPane pane = (JTabbedPane) e.getSource();
            BasicJideTabbedPaneUI ui = (BasicJideTabbedPaneUI) pane.getUI();
            int tabPlacement = pane.getTabPlacement();
            if (tabPlacement == TOP || tabPlacement == BOTTOM) {
                ui.navigateSelectedTab(WEST);
            }
            else {
                ui.navigateSelectedTab(NORTH);
            }
        }
    }

    private static class PageDownAction extends AbstractAction {
        public void actionPerformed(ActionEvent e) {
            JTabbedPane pane = (JTabbedPane) e.getSource();
            BasicJideTabbedPaneUI ui = (BasicJideTabbedPaneUI) pane.getUI();
            int tabPlacement = pane.getTabPlacement();
            if (tabPlacement == TOP || tabPlacement == BOTTOM) {
                ui.navigateSelectedTab(EAST);
            }
            else {
                ui.navigateSelectedTab(SOUTH);
            }
        }
    }

    private static class RequestFocusAction extends AbstractAction {
        public void actionPerformed(ActionEvent e) {
            JTabbedPane pane = (JTabbedPane) e.getSource();
            if (!pane.requestFocusInWindow()) {
                pane.requestFocus();
            }
        }
    }

    private static class RequestFocusForVisibleAction extends AbstractAction {
        public void actionPerformed(ActionEvent e) {
            JTabbedPane pane = (JTabbedPane) e.getSource();
            BasicJideTabbedPaneUI ui = (BasicJideTabbedPaneUI) pane.getUI();
            ui.requestFocusForVisibleComponent();
        }
    }

    /**
     * Selects a tab in the JTabbedPane based on the String of the action command. The tab selected is based on the
     * first tab that has a mnemonic matching the first character of the action command.
     */
    private static class SetSelectedIndexAction extends AbstractAction {
        public void actionPerformed(ActionEvent e) {
            JTabbedPane pane = (JTabbedPane) e.getSource();

            if (pane != null && (pane.getUI() instanceof BasicJideTabbedPaneUI)) {
                BasicJideTabbedPaneUI ui = (BasicJideTabbedPaneUI) pane.getUI();
                String command = e.getActionCommand();

                if (command != null && command.length() > 0) {
                    int mnemonic = (int) e.getActionCommand().charAt(0);
                    if (mnemonic >= 'a' && mnemonic <= 'z') {
                        mnemonic -= ('a' - 'A');
                    }
                    Integer index = (Integer) ui._mnemonicToIndexMap
                            .get(new Integer(mnemonic));
                    if (index != null && pane.isEnabledAt(index)) {
                        pane.setSelectedIndex(index);
                    }
                }
            }
        }
    }

    protected TabCloseButton createNoFocusButton(int type) {
        return new TabCloseButton(type);
    }

    private static class ScrollTabsForwardAction extends AbstractAction {
        public ScrollTabsForwardAction() {
            super();
            putValue(Action.SHORT_DESCRIPTION, Resource.getResourceBundle(Locale.getDefault()).getString("JideTabbedPane.scrollForward"));
        }

        public void actionPerformed(ActionEvent e) {
            JTabbedPane pane = null;
            Object src = e.getSource();
            if (src instanceof JTabbedPane) {
                pane = (JTabbedPane) src;
            }
            else if (src instanceof TabCloseButton) {
                pane = (JTabbedPane) SwingUtilities.getAncestorOfClass(JTabbedPane.class, (TabCloseButton) src);
            }

            if (pane != null) {
                BasicJideTabbedPaneUI ui = (BasicJideTabbedPaneUI) pane.getUI();

                if (ui.scrollableTabLayoutEnabled()) {
                    ui._tabScroller.scrollForward(pane.getTabPlacement());
                }
            }
        }
    }

    private static class ScrollTabsBackwardAction extends AbstractAction {
        public ScrollTabsBackwardAction() {
            super();
            putValue(Action.SHORT_DESCRIPTION, Resource.getResourceBundle(Locale.getDefault()).getString("JideTabbedPane.scrollBackward"));
        }

        public void actionPerformed(ActionEvent e) {
            JTabbedPane pane = null;
            Object src = e.getSource();
            if (src instanceof JTabbedPane) {
                pane = (JTabbedPane) src;
            }
            else if (src instanceof TabCloseButton) {
                pane = (JTabbedPane) SwingUtilities.getAncestorOfClass(JTabbedPane.class, (TabCloseButton) src);
            }

            if (pane != null) {
                BasicJideTabbedPaneUI ui = (BasicJideTabbedPaneUI) pane.getUI();

                if (ui.scrollableTabLayoutEnabled()) {
                    ui._tabScroller.scrollBackward(pane.getTabPlacement());
                }
            }
        }
    }


    private static class ScrollTabsListAction extends AbstractAction {

        public ScrollTabsListAction() {
            super();
            putValue(Action.SHORT_DESCRIPTION, Resource.getResourceBundle(Locale.getDefault()).getString("JideTabbedPane.showList"));
        }

        public void actionPerformed(ActionEvent e) {
            JTabbedPane pane = null;
            Object src = e.getSource();
            if (src instanceof JTabbedPane) {
                pane = (JTabbedPane) src;
            }
            else if (src instanceof TabCloseButton) {
                pane = (JTabbedPane) SwingUtilities.getAncestorOfClass(JTabbedPane.class, (TabCloseButton) src);
            }

            if (pane != null) {
                BasicJideTabbedPaneUI ui = (BasicJideTabbedPaneUI) pane.getUI();

                if (ui.scrollableTabLayoutEnabled()) {
                    if (ui._tabScroller._popup != null && ui._tabScroller._popup.isPopupVisible()) {
                        ui._tabScroller._popup.hidePopupImmediately();
                        ui._tabScroller._popup = null;
                    }
                    else {
                        ui._tabScroller.createPopup(pane.getTabPlacement());
                    }
                }
            }
        }
    }

    protected void stopOrCancelEditing() {
        boolean isEditValid = true;
        if (_tabPane != null && _tabPane.isTabEditing() && _tabPane.getTabEditingValidator() != null) {
            isEditValid = _tabPane.getTabEditingValidator().isValid(_editingTab, _oldPrefix + _tabEditor.getText() + _oldPostfix);
        }
        if (isEditValid)
            _tabPane.stopTabEditing();
        else
            _tabPane.cancelTabEditing();
    }

    private static class CloseTabAction extends AbstractAction {
        public CloseTabAction() {
            super();
            putValue(Action.SHORT_DESCRIPTION, Resource.getResourceBundle(Locale.getDefault()).getString("JideTabbedPane.close"));
        }

        public void actionPerformed(ActionEvent e) {
            JideTabbedPane pane = null;
            Object src = e.getSource();
            int index = -1;
            boolean closeSelected = false;
            if (src instanceof JideTabbedPane) {
                pane = (JideTabbedPane) src;
            }
            else if (src instanceof TabCloseButton && ((TabCloseButton) src).getParent() instanceof JideTabbedPane) {
                pane = (JideTabbedPane) ((TabCloseButton) src).getParent();
                closeSelected = true;
            }
            else if (src instanceof TabCloseButton && ((TabCloseButton) src).getParent() instanceof ScrollableTabPanel) {
                pane = (JideTabbedPane) SwingUtilities.getAncestorOfClass(JideTabbedPane.class, (TabCloseButton) src);
                closeSelected = false;
            }
            else {
                return; // shouldn't happen
            }

            if (pane.isTabEditing()) {
                ((BasicJideTabbedPaneUI) pane.getUI()).stopOrCancelEditing();//pane.stopTabEditing();
            }

            ActionEvent e2 = e;
            if (src instanceof TabCloseButton) {
                index = ((TabCloseButton) src).getIndex();
                Component compSrc = index != -1 ? pane.getComponentAt(index) : pane.getSelectedComponent();
                // note - We create a new action because we could be in the middle of a chain and
                // if we just use setSource we could cause problems.
                // also the AWT documentation pooh-pooh this. (for good reason)
                if (compSrc != null)
                    e2 = new ActionEvent(compSrc, e.getID(), e.getActionCommand(), e.getWhen(), e.getModifiers());
            }
            else if ("middleMouseButtonClicked".equals(e.getActionCommand())) {
                index = e.getID();
                Component compSrc = index != -1 ? pane.getComponentAt(index) : pane.getSelectedComponent();
                // note - We create a new action because we could be in the middle of a chain and
                // if we just use setSource we could cause problems.
                // also the AWT documentation pooh-pooh this. (for good reason)
                if (compSrc != null)
                    e2 = new ActionEvent(compSrc, e.getID(), e.getActionCommand(), e.getWhen(), e.getModifiers());
            }

            if (pane.getCloseAction() != null) {
                pane.getCloseAction().actionPerformed(e2);
            }
            else {
                if ("middleMouseButtonClicked".equals(e.getActionCommand())) {
                    index = e.getID();
                    if (index >= 0)
                        pane.removeTabAt(index);
                    if (pane.getTabCount() == 0) {
                        pane.updateUI();
                    }
                }
                else if (closeSelected) {
                    if (pane.getSelectedIndex() >= 0)
                        pane.removeTabAt(pane.getSelectedIndex());
                    if (pane.getTabCount() == 0) {
                        pane.updateUI();
                    }
                }
                else {
                    int i = ((TabCloseButton) src).getIndex();
                    if (i != -1) {

                        int tabIndex = pane.getSelectedIndex();

                        pane.removeTabAt(i);

                        if (i < tabIndex) {
                            pane.setSelectedIndex(tabIndex - 1);
                        }

                        if (pane.getTabCount() == 0) {
                            pane.updateUI();
                        }
                    }
                }
            }
        }
    }

    /**
     * This inner class is marked &quot;public&quot; due to a compiler bug. This class should be treated as a
     * &quot;protected&quot; inner class. Instantiate it only within subclasses of VsnetJideTabbedPaneUI.
     */
    public class TabbedPaneLayout implements LayoutManager {
        public void addLayoutComponent(String name, Component comp) {
        }

        public void removeLayoutComponent(Component comp) {
        }

        public Dimension preferredLayoutSize(Container parent) {
            Dimension dimension = calculateSize(false);
            return dimension;
        }

        public Dimension minimumLayoutSize(Container parent) {
            return calculateSize(true);
        }

        protected Dimension calculateSize(boolean minimum) {
            int tabPlacement = _tabPane.getTabPlacement();
            Insets insets = _tabPane.getInsets();
            Insets contentInsets = getContentBorderInsets(tabPlacement);
            Insets tabAreaInsets = getTabAreaInsets(tabPlacement);

            Dimension zeroSize = new Dimension(0, 0);
            int height = contentInsets.top + contentInsets.bottom;
            int width = contentInsets.left + contentInsets.right;
            int cWidth = 0;
            int cHeight = 0;

            synchronized (this) {
                ensureCloseButtonCreated();
                calculateLayoutInfo();

                // Determine minimum size required to display largest
                // child in each dimension
                //

                if (_tabPane.isShowTabContent()) {
                    for (int i = 0; i < _tabPane.getTabCount(); i++) {
                        Component component = _tabPane.getComponentAt(i);
                        if (component != null) {
                            Dimension size = zeroSize;
                            size = minimum ? component.getMinimumSize() :
                                    component.getPreferredSize();

                            if (size != null) {
                                cHeight = Math.max(size.height, cHeight);
                                cWidth = Math.max(size.width, cWidth);
                            }
                        }
                    }
                    // Add content border insets to minimum size
                    width += cWidth;
                    height += cHeight;
                }

                int tabExtent = 0;

                // Calculate how much space the tabs will need, based on the
                // minimum size required to display largest child + content border
                //
                Dimension lsize = new Dimension(0, 0);
                Dimension tsize = new Dimension(0, 0);

                if (isTabLeadingComponentVisible()) {
                    lsize = _tabLeadingComponent.getPreferredSize();
                }
                if (isTabTrailingComponentVisible()) {
                    tsize = _tabTrailingComponent.getPreferredSize();
                }

                switch (tabPlacement) {
                    case LEFT:
                    case RIGHT:
                        height = Math.max(height, (minimum ? 0 : calculateMaxTabHeight(tabPlacement)) + tabAreaInsets.top + tabAreaInsets.bottom);
                        tabExtent = calculateTabAreaHeight(tabPlacement, _runCount, _maxTabHeight);

                        if (isTabLeadingComponentVisible()) {
                            tabExtent = Math.max(lsize.width, tabExtent);
                        }
                        if (isTabTrailingComponentVisible()) {
                            tabExtent = Math.max(tsize.width, tabExtent);
                        }

                        width += tabExtent;
                        break;
                    case TOP:
                    case BOTTOM:
                    default:
                        if (_tabPane.getTabResizeMode() == JideTabbedPane.RESIZE_MODE_FIT) {
                            width = Math.max(width, (_tabPane.getTabCount() << 2) +
                                    tabAreaInsets.left + tabAreaInsets.right);
                        }
                        else {
                            width = Math.max(width, (minimum ? 0 : calculateMaxTabWidth(tabPlacement)) +
                                    tabAreaInsets.left + tabAreaInsets.right);
                        }

                        if (_tabPane.isTabShown()) {
                            tabExtent = calculateTabAreaHeight(tabPlacement, _runCount, _maxTabHeight);

                            if (isTabLeadingComponentVisible()) {
                                tabExtent = Math.max(lsize.height, tabExtent);
                            }
                            if (isTabTrailingComponentVisible()) {
                                tabExtent = Math.max(tsize.height, tabExtent);
                            }

                            height += tabExtent;
                        }
                }
            }
            return new Dimension(width + insets.left + insets.right,
                    height + insets.bottom + insets.top);

        }

        protected int preferredTabAreaHeight(int tabPlacement, int width) {
            int tabCount = _tabPane.getTabCount();
            int total = 0;
            if (tabCount > 0) {
                int rows = 1;
                int x = 0;

                int maxTabHeight = calculateMaxTabHeight(tabPlacement);

                for (int i = 0; i < tabCount; i++) {
                    FontMetrics metrics = getFontMetrics(i);
                    int tabWidth = calculateTabWidth(tabPlacement, i, metrics);

                    if (x != 0 && x + tabWidth > width) {
                        rows++;
                        x = 0;
                    }
                    x += tabWidth;
                }
                total = calculateTabAreaHeight(tabPlacement, rows, maxTabHeight);
            }
            return total;
        }

        protected int preferredTabAreaWidth(int tabPlacement, int height) {
            int tabCount = _tabPane.getTabCount();
            int total = 0;
            if (tabCount > 0) {
                int columns = 1;
                int y = 0;

                _maxTabWidth = calculateMaxTabWidth(tabPlacement);

                for (int i = 0; i < tabCount; i++) {
                    FontMetrics metrics = getFontMetrics(i);
                    int tabHeight = calculateTabHeight(tabPlacement, i, metrics);
                    if (y != 0 && y + tabHeight > height) {
                        columns++;
                        y = 0;
                    }
                    y += tabHeight;
                }
                total = calculateTabAreaWidth(tabPlacement, columns, _maxTabWidth);
            }
            return total;
        }

        public void layoutContainer(Container parent) {
            int tabPlacement = _tabPane.getTabPlacement();
            Insets insets = _tabPane.getInsets();
            int selectedIndex = _tabPane.getSelectedIndex();
            Component visibleComponent = getVisibleComponent();

            synchronized (this) {
                ensureCloseButtonCreated();
                calculateLayoutInfo();

                if (selectedIndex < 0) {
                    if (visibleComponent != null) {
                        // The last tab was removed, so remove the component
                        setVisibleComponent(null);
                    }
                }
                else {
                    int cx, cy, cw, ch;
                    int totalTabWidth = 0;
                    int totalTabHeight = 0;
                    Insets contentInsets = getContentBorderInsets(tabPlacement);

                    Component selectedComponent = _tabPane.getComponentAt(selectedIndex);
                    boolean shouldChangeFocus = false;

                    // In order to allow programs to use a single component
                    // as the display for multiple tabs, we will not change
                    // the visible compnent if the currently selected tab
                    // has a null component. This is a bit dicey, as we don't
                    // explicitly state we support this in the spec, but since
                    // programs are now depending on this, we're making it work.
                    //
                    if (selectedComponent != null) {
                        if (selectedComponent != visibleComponent && visibleComponent != null) {
                            if (JideSwingUtilities.isAncestorOfFocusOwner(visibleComponent) && _tabPane.isAutoRequestFocus()) {
                                shouldChangeFocus = true;
                            }
                        }
                        setVisibleComponent(selectedComponent);
                    }

                    Rectangle bounds = _tabPane.getBounds();
                    int numChildren = _tabPane.getComponentCount();

                    if (numChildren > 0) {
                        switch (tabPlacement) {
                            case LEFT:
                                totalTabWidth = calculateTabAreaWidth(tabPlacement, _runCount, _maxTabWidth);
                                cx = insets.left + totalTabWidth + contentInsets.left;
                                cy = insets.top + contentInsets.top;
                                break;
                            case RIGHT:
                                totalTabWidth = calculateTabAreaWidth(tabPlacement, _runCount, _maxTabWidth);
                                cx = insets.left + contentInsets.left;
                                cy = insets.top + contentInsets.top;
                                break;
                            case BOTTOM:
                                totalTabHeight = calculateTabAreaHeight(tabPlacement, _runCount, _maxTabHeight);
                                cx = insets.left + contentInsets.left;
                                cy = insets.top + contentInsets.top;
                                break;
                            case TOP:
                            default:
                                totalTabHeight = calculateTabAreaHeight(tabPlacement, _runCount, _maxTabHeight);
                                cx = insets.left + contentInsets.left;
                                cy = insets.top + totalTabHeight + contentInsets.top;
                        }

                        cw = bounds.width - totalTabWidth -
                                insets.left - insets.right -
                                contentInsets.left - contentInsets.right;
                        ch = bounds.height - totalTabHeight -
                                insets.top - insets.bottom -
                                contentInsets.top - contentInsets.bottom;

                        for (int i = 0; i < numChildren; i++) {
                            Component child = _tabPane.getComponent(i);
                            child.setBounds(cx, cy, cw, ch);
                        }
                    }

                    if (shouldChangeFocus) {
                        if (!requestFocusForVisibleComponent()) {
                            if (!_tabPane.requestFocusInWindow()) {
                                _tabPane.requestFocus();
                            }
                        }
                    }
                }
            }
        }

        public void calculateLayoutInfo() {
            int tabCount = _tabPane.getTabCount();
            assureRectsCreated(tabCount);
            calculateTabRects(_tabPane.getTabPlacement(), tabCount);
        }

        protected void calculateTabRects(int tabPlacement, int tabCount) {
            Dimension size = _tabPane.getSize();
            Insets insets = _tabPane.getInsets();
            Insets tabAreaInsets = getTabAreaInsets(tabPlacement);
            int selectedIndex = _tabPane.getSelectedIndex();
            int tabRunOverlay;
            int i, j;
            int x, y;
            int returnAt;
            boolean verticalTabRuns = (tabPlacement == LEFT || tabPlacement == RIGHT);
            boolean leftToRight = _tabPane.getComponentOrientation().isLeftToRight();

            //
            // Calculate bounds within which a tab run must fit
            //
            switch (tabPlacement) {
                case LEFT:
                    _maxTabWidth = calculateMaxTabWidth(tabPlacement);
                    x = insets.left + tabAreaInsets.left;
                    y = insets.top + tabAreaInsets.top;
                    returnAt = size.height - (insets.bottom + tabAreaInsets.bottom);
                    break;
                case RIGHT:
                    _maxTabWidth = calculateMaxTabWidth(tabPlacement);
                    x = size.width - insets.right - tabAreaInsets.right - _maxTabWidth;
                    y = insets.top + tabAreaInsets.top;
                    returnAt = size.height - (insets.bottom + tabAreaInsets.bottom);
                    break;
                case BOTTOM:
                    _maxTabHeight = calculateMaxTabHeight(tabPlacement);
                    x = insets.left + tabAreaInsets.left;
                    y = size.height - insets.bottom - tabAreaInsets.bottom - _maxTabHeight;
                    returnAt = size.width - (insets.right + tabAreaInsets.right);
                    break;
                case TOP:
                default:
                    _maxTabHeight = calculateMaxTabHeight(tabPlacement);
                    x = insets.left + tabAreaInsets.left;
                    y = insets.top + tabAreaInsets.top;
                    returnAt = size.width - (insets.right + tabAreaInsets.right);
                    break;
            }

            tabRunOverlay = getTabRunOverlay(tabPlacement);

            _runCount = 0;
            _selectedRun = -1;

            if (tabCount == 0) {
                return;
            }

            // Run through tabs and partition them into runs
            Rectangle rect;
            for (i = 0; i < tabCount; i++) {
                FontMetrics metrics = getFontMetrics(i);
                rect = _rects[i];

                if (!verticalTabRuns) {
                    // Tabs on TOP or BOTTOM....
                    if (i > 0) {
                        rect.x = _rects[i - 1].x + _rects[i - 1].width;
                    }
                    else {
                        _tabRuns[0] = 0;
                        _runCount = 1;
                        _maxTabWidth = 0;
                        rect.x = x;
                    }
                    rect.width = calculateTabWidth(tabPlacement, i, metrics);
                    _maxTabWidth = Math.max(_maxTabWidth, rect.width);

                    // Never move a TAB down a run if it is in the first column.
                    // Even if there isn't enough room, moving it to a fresh
                    // line won't help.
                    if (rect.x != 2 + insets.left && rect.x + rect.width > returnAt) {
                        if (_runCount > _tabRuns.length - 1) {
                            expandTabRunsArray();
                        }
                        _tabRuns[_runCount] = i;
                        _runCount++;
                        rect.x = x;
                    }
                    // Initialize y position in case there's just one run
                    rect.y = y;
                    rect.height = _maxTabHeight/* - 2 */;

                }
                else {
                    // Tabs on LEFT or RIGHT...
                    if (i > 0) {
                        rect.y = _rects[i - 1].y + _rects[i - 1].height;
                    }
                    else {
                        _tabRuns[0] = 0;
                        _runCount = 1;
                        _maxTabHeight = 0;
                        rect.y = y;
                    }
                    rect.height = calculateTabHeight(tabPlacement, i, metrics);
                    _maxTabHeight = Math.max(_maxTabHeight, rect.height);

                    // Never move a TAB over a run if it is in the first run.
                    // Even if there isn't enough room, moving it to a fresh
                    // column won't help.
                    if (rect.y != 2 + insets.top && rect.y + rect.height > returnAt) {
                        if (_runCount > _tabRuns.length - 1) {
                            expandTabRunsArray();
                        }
                        _tabRuns[_runCount] = i;
                        _runCount++;
                        rect.y = y;
                    }
                    // Initialize x position in case there's just one column
                    rect.x = x;
                    rect.width = _maxTabWidth/* - 2 */;

                }
                if (i == selectedIndex) {
                    _selectedRun = _runCount - 1;
                }
            }

            if (_runCount > 1) {
                // Re-distribute tabs in case last run has leftover space
                normalizeTabRuns(tabPlacement, tabCount, verticalTabRuns ? y : x, returnAt);

                _selectedRun = getRunForTab(tabCount, selectedIndex);

                // Rotate run array so that selected run is first
                if (shouldRotateTabRuns(tabPlacement)) {
                    rotateTabRuns(tabPlacement, _selectedRun);
                }
            }

            // Step through runs from back to front to calculate
            // tab y locations and to pad runs appropriately
            for (i = _runCount - 1; i >= 0; i--) {
                int start = _tabRuns[i];
                int next = _tabRuns[i == (_runCount - 1) ? 0 : i + 1];
                int end = (next != 0 ? next - 1 : tabCount - 1);
                if (!verticalTabRuns) {
                    for (j = start; j <= end; j++) {
                        rect = _rects[j];
                        rect.y = y;
                        rect.x += getTabRunIndent(tabPlacement, i);
                    }
                    if (shouldPadTabRun(tabPlacement, i)) {
                        padTabRun(tabPlacement, start, end, returnAt);
                    }
                    if (tabPlacement == BOTTOM) {
                        y -= (_maxTabHeight - tabRunOverlay);
                    }
                    else {
                        y += (_maxTabHeight - tabRunOverlay);
                    }
                }
                else {
                    for (j = start; j <= end; j++) {
                        rect = _rects[j];
                        rect.x = x;
                        rect.y += getTabRunIndent(tabPlacement, i);
                    }
                    if (shouldPadTabRun(tabPlacement, i)) {
                        padTabRun(tabPlacement, start, end, returnAt);
                    }
                    if (tabPlacement == RIGHT) {
                        x -= (_maxTabWidth - tabRunOverlay);
                    }
                    else {
                        x += (_maxTabWidth - tabRunOverlay);
                    }
                }
            }

            // Pad the selected tab so that it appears raised in front
            padSelectedTab(tabPlacement, selectedIndex);

            // if right to left and tab placement on the top or
            // the bottom, flip x positions and adjust by widths
            if (!leftToRight && !verticalTabRuns) {
                int rightMargin = size.width
                        - (insets.right + tabAreaInsets.right);
                for (i = 0; i < tabCount; i++) {
                    _rects[i].x = rightMargin - _rects[i].x - _rects[i].width;
                }
            }
        }

        /*
           * Rotates the run-index array so that the selected run is run[0]
           */
        protected void rotateTabRuns(int tabPlacement, int selectedRun) {
            for (int i = 0; i < selectedRun; i++) {
                int save = _tabRuns[0];
                for (int j = 1; j < _runCount; j++) {
                    _tabRuns[j - 1] = _tabRuns[j];
                }
                _tabRuns[_runCount - 1] = save;
            }
        }

        protected void normalizeTabRuns(int tabPlacement, int tabCount,
                                        int start, int max) {
            boolean verticalTabRuns = (tabPlacement == LEFT || tabPlacement == RIGHT);
            int run = _runCount - 1;
            boolean keepAdjusting = true;
            double weight = 1.25;

            // At this point the tab runs are packed to fit as many
            // tabs as possible, which can leave the last run with a lot
            // of extra space (resulting in very fat tabs on the last run).
            // So we'll attempt to distribute this extra space more evenly
            // across the runs in order to make the runs look more consistent.
            //
            // Starting with the last run, determine whether the last tab in
            // the previous run would fit (generously) in this run; if so,
            // move tab to current run and shift tabs accordingly. Cycle
            // through remaining runs using the same algorithm.
            //
            while (keepAdjusting) {
                int last = lastTabInRun(tabCount, run);
                int prevLast = lastTabInRun(tabCount, run - 1);
                int end;
                int prevLastLen;

                if (!verticalTabRuns) {
                    end = _rects[last].x + _rects[last].width;
                    prevLastLen = (int) (_maxTabWidth * weight);
                }
                else {
                    end = _rects[last].y + _rects[last].height;
                    prevLastLen = (int) (_maxTabHeight * weight * 2);
                }

                // Check if the run has enough extra space to fit the last tab
                // from the previous row...
                if (max - end > prevLastLen) {

                    // Insert tab from previous row and shift rest over
                    _tabRuns[run] = prevLast;
                    if (!verticalTabRuns) {
                        _rects[prevLast].x = start;
                    }
                    else {
                        _rects[prevLast].y = start;
                    }
                    for (int i = prevLast + 1; i <= last; i++) {
                        if (!verticalTabRuns) {
                            _rects[i].x = _rects[i - 1].x + _rects[i - 1].width;
                        }
                        else {
                            _rects[i].y = _rects[i - 1].y + _rects[i - 1].height;
                        }
                    }

                }
                else if (run == _runCount - 1) {
                    // no more room left in last run, so we're done!
                    keepAdjusting = false;
                }
                if (run - 1 > 0) {
                    // check previous run next...
                    run -= 1;
                }
                else {
                    // check last run again...but require a higher ratio
                    // of extraspace-to-tabsize because we don't want to
                    // end up with too many tabs on the last run!
                    run = _runCount - 1;
                    weight += .25;
                }
            }
        }

        protected void padTabRun(int tabPlacement, int start, int end, int max) {
            Rectangle lastRect = _rects[end];
            if (tabPlacement == TOP || tabPlacement == BOTTOM) {
                int runWidth = (lastRect.x + lastRect.width) - _rects[start].x;
                int deltaWidth = max - (lastRect.x + lastRect.width);
                float factor = (float) deltaWidth / (float) runWidth;

                for (int j = start; j <= end; j++) {
                    Rectangle pastRect = _rects[j];
                    if (j > start) {
                        pastRect.x = _rects[j - 1].x + _rects[j - 1].width;
                    }
                    pastRect.width += Math.round((float) pastRect.width * factor);
                }
                lastRect.width = max - lastRect.x;
            }
            else {
                int runHeight = (lastRect.y + lastRect.height) - _rects[start].y;
                int deltaHeight = max - (lastRect.y + lastRect.height);
                float factor = (float) deltaHeight / (float) runHeight;

                for (int j = start; j <= end; j++) {
                    Rectangle pastRect = _rects[j];
                    if (j > start) {
                        pastRect.y = _rects[j - 1].y + _rects[j - 1].height;
                    }
                    pastRect.height += Math.round((float) pastRect.height * factor);
                }
                lastRect.height = max - lastRect.y;
            }
        }

        protected void padSelectedTab(int tabPlacement, int selectedIndex) {

            if (selectedIndex >= 0) {
                Rectangle selRect = _rects[selectedIndex];
                Insets padInsets = getSelectedTabPadInsets(tabPlacement);
                selRect.x -= padInsets.left;
                selRect.width += (padInsets.left + padInsets.right);
                selRect.y -= padInsets.top;
                selRect.height += (padInsets.top + padInsets.bottom);
            }
        }
    }

    protected TabSpaceAllocator tryTabSpacer = new TabSpaceAllocator();

    protected class TabbedPaneScrollLayout extends TabbedPaneLayout {

        @Override
        protected int preferredTabAreaHeight(int tabPlacement, int width) {
            return calculateMaxTabHeight(tabPlacement);
        }

        @Override
        protected int preferredTabAreaWidth(int tabPlacement, int height) {
            return calculateMaxTabWidth(tabPlacement);
        }

        @Override
        public void layoutContainer(Container parent) {
            int tabPlacement = _tabPane.getTabPlacement();
            int tabCount = _tabPane.getTabCount();
            Insets insets = _tabPane.getInsets();
            int selectedIndex = _tabPane.getSelectedIndex();
            Component visibleComponent = getVisibleComponent();
            boolean leftToRight = _tabPane.getComponentOrientation().isLeftToRight();
            JViewport viewport = null;

            calculateLayoutInfo();

            if (selectedIndex < 0) {
                if (visibleComponent != null) {
                    // The last tab was removed, so remove the component
                    setVisibleComponent(null);
                }

            }
            else {
                Component selectedComponent = selectedIndex >= _tabPane.getTabCount() ? null : _tabPane.getComponentAt(selectedIndex); // check for range because of a change in JDK1.6-rc-b89
                boolean shouldChangeFocus = false;

                // In order to allow programs to use a single component
                // as the display for multiple tabs, we will not change
                // the visible compnent if the currently selected tab
                // has a null component. This is a bit dicey, as we don't
                // explicitly state we support this in the spec, but since
                // programs are now depending on this, we're making it work.
                //
                if (selectedComponent != null) {
                    if (selectedComponent != visibleComponent && visibleComponent != null) {
                        if (JideSwingUtilities.isAncestorOfFocusOwner(visibleComponent) && _tabPane.isAutoRequestFocus()) {
                            shouldChangeFocus = true;
                        }
                    }
                    setVisibleComponent(selectedComponent);
                }
                int tx, ty, tw, th; // tab area bounds
                int cx, cy, cw, ch; // content area bounds
                Insets contentInsets = getContentBorderInsets(tabPlacement);
                Rectangle bounds = _tabPane.getBounds();
                int numChildren = _tabPane.getComponentCount();

                Dimension lsize = new Dimension(0, 0);
                Dimension tsize = new Dimension(0, 0);

                if (isTabLeadingComponentVisible()) {
                    lsize = _tabLeadingComponent.getPreferredSize();
                }
                if (isTabTrailingComponentVisible()) {
                    tsize = _tabTrailingComponent.getPreferredSize();
                }

                if (numChildren > 0) {
                    switch (tabPlacement) {
                        case LEFT:
                            // calculate tab area bounds
                            tw = calculateTabAreaHeight(TOP, _runCount, _maxTabWidth);
                            th = bounds.height - insets.top - insets.bottom;
                            tx = insets.left;
                            ty = insets.top;

                            if (isTabLeadingComponentVisible()) {
                                ty += lsize.height;
                                th -= lsize.height;

                                if (lsize.width > tw) {
                                    tw = lsize.width;
                                }
                            }
                            if (isTabTrailingComponentVisible()) {
                                th -= tsize.height;

                                if (tsize.width > tw) {
                                    tw = tsize.width;
                                }
                            }

                            // calculate content area bounds
                            cx = tx + tw + contentInsets.left;
                            cy = insets.top + contentInsets.top;
                            cw = bounds.width - insets.left - insets.right - tw - contentInsets.left - contentInsets.right;
                            ch = bounds.height - insets.top - insets.bottom - contentInsets.top - contentInsets.bottom;
                            break;
                        case RIGHT:
                            // calculate tab area bounds
                            tw = calculateTabAreaHeight(TOP, _runCount,
                                    _maxTabWidth);
                            th = bounds.height - insets.top - insets.bottom;
                            tx = bounds.width - insets.right - tw;
                            ty = insets.top;

                            if (isTabLeadingComponentVisible()) {
                                ty += lsize.height;
                                th -= lsize.height;

                                if (lsize.width > tw) {
                                    tw = lsize.width;
                                    tx = bounds.width - insets.right - tw;
                                }
                            }
                            if (isTabTrailingComponentVisible()) {
                                th -= tsize.height;

                                if (tsize.width > tw) {
                                    tw = tsize.width;
                                    tx = bounds.width - insets.right - tw;
                                }
                            }

                            // calculate content area bounds
                            cx = insets.left + contentInsets.left;
                            cy = insets.top + contentInsets.top;
                            cw = bounds.width - insets.left - insets.right - tw - contentInsets.left - contentInsets.right;
                            ch = bounds.height - insets.top - insets.bottom - contentInsets.top - contentInsets.bottom;
                            break;
                        case BOTTOM:
                            // calculate tab area bounds
                            tw = bounds.width - insets.left - insets.right;
                            th = calculateTabAreaHeight(tabPlacement, _runCount,
                                    _maxTabHeight);
                            tx = insets.left;
                            ty = bounds.height - insets.bottom - th;

                            if (leftToRight) {
                                if (isTabLeadingComponentVisible()) {
                                    tx += lsize.width;
                                    tw -= lsize.width;

                                    if (lsize.height > th) {
                                        th = lsize.height;
                                        ty = bounds.height - insets.bottom - th;
                                    }
                                }
                                if (isTabTrailingComponentVisible()) {
                                    tw -= tsize.width;

                                    if (tsize.height > th) {
                                        th = tsize.height;
                                        ty = bounds.height - insets.bottom - th;
                                    }
                                }
                            }
                            else {
                                if (isTabTrailingComponentVisible()) {
                                    tx += tsize.width;
                                    tw -= tsize.width;

                                    if (tsize.height > th) {
                                        th = tsize.height;
                                        ty = bounds.height - insets.bottom - th;
                                    }
                                }
                                if (isTabLeadingComponentVisible()) {
                                    tw -= lsize.width;

                                    if (lsize.height > th) {
                                        th = lsize.height;
                                        ty = bounds.height - insets.bottom - th;
                                    }
                                }
                            }

                            // calculate content area bounds
                            cx = insets.left + contentInsets.left;
                            cy = insets.top + contentInsets.top;
                            cw = bounds.width - insets.left - insets.right
                                    - contentInsets.left - contentInsets.right;
                            ch = bounds.height - insets.top - insets.bottom - th - contentInsets.top - contentInsets.bottom;
                            break;
                        case TOP:
                        default:
                            // calculate tab area bounds
                            tw = bounds.width - insets.left - insets.right;
                            th = calculateTabAreaHeight(tabPlacement, _runCount,
                                    _maxTabHeight);
                            tx = insets.left;
                            ty = insets.top;

                            if (leftToRight) {
                                if (isTabLeadingComponentVisible()) {
                                    tx += lsize.width;
                                    tw -= lsize.width;

                                    if (lsize.height > th) {
                                        th = lsize.height;
                                    }
                                }
                                if (isTabTrailingComponentVisible()) {
                                    tw -= tsize.width;

                                    if (tsize.height > th) {
                                        th = tsize.height;
                                    }
                                }
                            }
                            else {
                                if (isTabTrailingComponentVisible()) {
                                    tx += tsize.width;
                                    tw -= tsize.width;

                                    if (tsize.height > th) {
                                        th = tsize.height;
                                    }
                                }
                                if (isTabLeadingComponentVisible()) {
                                    tw -= lsize.width;

                                    if (lsize.height > th) {
                                        th = lsize.height;
                                    }
                                }
                            }

                            // calculate content area bounds
                            cx = insets.left + contentInsets.left;
                            cy = insets.top + th + contentInsets.top;
                            cw = bounds.width - insets.left - insets.right
                                    - contentInsets.left - contentInsets.right;
                            ch = bounds.height - insets.top - insets.bottom - th - contentInsets.top - contentInsets.bottom;
                    }

//                    if (tabPlacement == JideTabbedPane.TOP || tabPlacement == JideTabbedPane.BOTTOM) {
//                        if (getTabResizeMode() != JideTabbedPane.RESIZE_MODE_FIT) {
//                            int numberOfButtons = isShrinkTabs() ? 1 : 4;
//                            if (tw < _rects[0].width + numberOfButtons * _buttonSize) {
//                                return;
//                            }
//                        }
//                    }
//                    else {
//                        if (getTabResizeMode() != JideTabbedPane.RESIZE_MODE_FIT) {
//                            int numberOfButtons = isShrinkTabs() ? 1 : 4;
//                            if (th < _rects[0].height + numberOfButtons * _buttonSize) {
//                                return;
//                            }
//                        }
//                    }

                    for (int i = 0; i < numChildren; i++) {
                        Component child = _tabPane.getComponent(i);

                        if (child instanceof ScrollableTabViewport) {
                            viewport = (JViewport) child;
//                            Rectangle viewRect = viewport.getViewRect();
                            int vw = tw;
                            int vh = th;
                            int numberOfButtons = getNumberOfTabButtons();
                            switch (tabPlacement) {
                                case LEFT:
                                case RIGHT:
                                    int totalTabHeight = _rects[tabCount - 1].y + _rects[tabCount - 1].height;
                                    if (totalTabHeight > th || isShowTabButtons()) {
                                        if (!isShowTabButtons()) numberOfButtons += 3;
                                        // Allow space for scrollbuttons
                                        vh = Math.max(th - _buttonSize * numberOfButtons, 0);
//                                        if (totalTabHeight - viewRect.y <= vh) {
//                                            // Scrolled to the end, so ensure the
//                                            // viewport size is
//                                            // such that the scroll offset aligns
//                                            // with a tab
//                                            vh = totalTabHeight - viewRect.y;
//                                        }
                                    }
                                    else {
                                        // Allow space for scrollbuttons
                                        vh = Math.max(th - _buttonSize * numberOfButtons, 0);
                                    }

                                    if (vh + getLayoutSize() < th - _buttonSize * numberOfButtons) {
                                        vh += getLayoutSize();
                                    }
                                    break;
                                case BOTTOM:
                                case TOP:
                                default:
                                    int totalTabWidth = _rects[tabCount - 1].x + _rects[tabCount - 1].width;
                                    boolean widthEnough = leftToRight ? totalTabWidth <= tw : _rects[tabCount - 1].x >= 0;
                                    if (isShowTabButtons() || !widthEnough) {
                                        if (!isShowTabButtons()) numberOfButtons += 3;
                                        // Need to allow space for scrollbuttons
                                        vw = Math.max(tw - _buttonSize * numberOfButtons, 0);
                                        if (!leftToRight) {
                                            tx = _buttonSize * numberOfButtons;
                                        }

//                                        if (totalTabWidth - viewRect.x <= vw) {
//                                            // Scrolled to the end, so ensure the
//                                            // viewport size is
//                                            // such that the scroll offset aligns
//                                            // with a tab
//                                            vw = totalTabWidth - viewRect.x;
//                                        }
                                    }
                                    else {
                                        // Allow space for scrollbuttons
                                        vw = Math.max(tw - _buttonSize * numberOfButtons, 0);
                                        if (!leftToRight) {
                                            tx = _buttonSize * numberOfButtons;
                                        }
                                    }
                                    if (vw + getLayoutSize() < tw - _buttonSize * numberOfButtons) {
                                        vw += getLayoutSize();
                                        if (!leftToRight) {
                                            tx -= getLayoutSize();
                                        }
                                    }
                                    break;
                            }
                            child.setBounds(tx, ty, vw, vh);

                        }
                        else if (child instanceof TabCloseButton) {
                            TabCloseButton scrollbutton = (TabCloseButton) child;
                            if (_tabPane.isTabShown() && (scrollbutton.getType() != TabCloseButton.CLOSE_BUTTON || !isShowCloseButtonOnTab())) {
                                Dimension bsize = scrollbutton.getPreferredSize();
                                int bx = 0;
                                int by = 0;
                                int bw = bsize.width;
                                int bh = bsize.height;
                                boolean visible = false;

                                switch (tabPlacement) {
                                    case LEFT:
                                    case RIGHT:
                                        int totalTabHeight = _rects[tabCount - 1].y + _rects[tabCount - 1].height;
                                        if (_tabPane.isTabShown() && (isShowTabButtons() || totalTabHeight > th)) {
                                            int dir = scrollbutton.getType();//NoFocusButton.EAST_BUTTON : NoFocusButton.WEST_BUTTON;
                                            scrollbutton.setType(dir);
                                            switch (dir) {
                                                case TabCloseButton.CLOSE_BUTTON:
                                                    if (isShowCloseButton()) {
                                                        visible = true;
                                                        by = bounds.height - insets.top - bsize.height - 5;
                                                    }
                                                    else {
                                                        visible = false;
                                                        by = 0;
                                                    }
                                                    break;
                                                case TabCloseButton.LIST_BUTTON:
                                                    visible = true;
                                                    by = bounds.height - insets.top - (2 - (!isShowCloseButton() || isShowCloseButtonOnTab() ? 1 : 0)) * bsize.height - 5;
                                                    break;
                                                case TabCloseButton.EAST_BUTTON:
                                                    visible = !isShrinkTabs();
                                                    by = bounds.height - insets.top - (3 - (!isShowCloseButton() || isShowCloseButtonOnTab() ? 1 : 0)) * bsize.height - 5;
                                                    break;
                                                case TabCloseButton.WEST_BUTTON:
                                                    visible = !isShrinkTabs();
                                                    by = bounds.height - insets.top - (4 - (!isShowCloseButton() || isShowCloseButtonOnTab() ? 1 : 0)) * bsize.height - 5;
                                                    break;
                                            }
                                            bx = tx + 2;

                                        }
                                        else {
                                            int dir = scrollbutton.getType();
                                            scrollbutton.setType(dir);
                                            if (dir == TabCloseButton.CLOSE_BUTTON) {
                                                if (isShowCloseButton()) {
                                                    visible = true;
                                                    by = bounds.height - insets.top - bsize.height - 5;
                                                }
                                                else {
                                                    visible = false;
                                                    by = 0;
                                                }
                                                bx = tx + 2;
                                            }
                                        }
                                        if (isTabTrailingComponentVisible()) {
                                            by = by - tsize.height;
                                        }
                                        int temp = -1;
                                        if (isTabLeadingComponentVisible()) {
                                            if (lsize.width >= _rects[0].width) {
                                                if (tabPlacement == LEFT) {
                                                    bx += lsize.width - _rects[0].width;
                                                    temp = lsize.width;
                                                }
                                            }
                                        }
                                        if (isTabTrailingComponentVisible()) {
                                            if (tsize.width >= _rects[0].width
                                                    && temp < tsize.width) {
                                                if (tabPlacement == LEFT) {
                                                    bx += tsize.width - _rects[0].width;
                                                }
                                            }
                                        }
                                        break;
                                    case TOP:
                                    case BOTTOM:
                                    default:
                                        int totalTabWidth = _rects[tabCount - 1].x + _rects[tabCount - 1].width;
                                        boolean widthEnough = leftToRight ? totalTabWidth <= tw : _rects[tabCount - 1].x >= 0;
                                        if (_tabPane.isTabShown() && (isShowTabButtons() || !widthEnough)) {
                                            int dir = scrollbutton.getType();// NoFocusButton.EAST_BUTTON
                                            // NoFocusButton.WEST_BUTTON;
                                            scrollbutton.setType(dir);
                                            switch (dir) {
                                                case TabCloseButton.CLOSE_BUTTON:
                                                    if (isShowCloseButton()) {
                                                        visible = true;
                                                        if (leftToRight) {
                                                            bx = bounds.width - insets.left - bsize.width - 5;
                                                        }
                                                        else {
                                                            bx = insets.left - 5;
                                                        }
                                                    }
                                                    else {
                                                        visible = false;
                                                        bx = 0;
                                                    }
                                                    break;
                                                case TabCloseButton.LIST_BUTTON:
                                                    visible = true;
                                                    if (leftToRight) {
                                                        bx = bounds.width - insets.left - (2 - (!isShowCloseButton() || isShowCloseButtonOnTab() ? 1 : 0)) * bsize.width - 5;
                                                    }
                                                    else {
                                                        bx = insets.left + (1 - (!isShowCloseButton() || isShowCloseButtonOnTab() ? 1 : 0)) * bsize.width + 5;
                                                    }
                                                    break;
                                                case TabCloseButton.EAST_BUTTON:
                                                    visible = !isShrinkTabs();
                                                    if (leftToRight) {
                                                        bx = bounds.width - insets.left - (3 - (!isShowCloseButton() || isShowCloseButtonOnTab() ? 1 : 0)) * bsize.width - 5;
                                                    }
                                                    else {
                                                        bx = insets.left + (2 - (!isShowCloseButton() || isShowCloseButtonOnTab() ? 1 : 0)) * bsize.width + 5;
                                                    }
                                                    break;
                                                case TabCloseButton.WEST_BUTTON:
                                                    visible = !isShrinkTabs();
                                                    if (leftToRight) {
                                                        bx = bounds.width - insets.left - (4 - (!isShowCloseButton() || isShowCloseButtonOnTab() ? 1 : 0)) * bsize.width - 5;
                                                    }
                                                    else {
                                                        bx = insets.left + (3 - (!isShowCloseButton() || isShowCloseButtonOnTab() ? 1 : 0)) * bsize.width + 5; 
                                                    }
                                                    break;
                                            }
                                            by = ((th - bsize.height) >> 1) + ty;

                                        }
                                        else {
                                            int dir = scrollbutton.getType();
                                            scrollbutton.setType(dir);
                                            if (dir == TabCloseButton.CLOSE_BUTTON) {
                                                if (isShowCloseButton()) {
                                                    visible = true;
                                                    bx = bounds.width - insets.left - bsize.width - 5;
                                                }
                                                else {
                                                    visible = false;
                                                    bx = 0;
                                                }
                                                by = ((th - bsize.height) >> 1) + ty;
                                            }
                                        }
                                        if (isTabTrailingComponentVisible()) {
                                            bx -= tsize.width;
                                        }
                                        temp = -1;
                                        if (isTabLeadingComponentVisible()) {
                                            if (lsize.height >= _rects[0].height) {
                                                if (tabPlacement == TOP) {
                                                    by = ty + 2 + lsize.height - _rects[0].height;
                                                    temp = lsize.height;
                                                }
                                                else {
                                                    by = ty + 2;
                                                }
                                            }
                                        }
                                        if (isTabTrailingComponentVisible()) {
                                            if (tsize.height >= _rects[0].height
                                                    && temp < tsize.height) {
                                                if (tabPlacement == TOP) {
                                                    by = ty + 2 + tsize.height - _rects[0].height;
                                                }
                                                else {
                                                    by = ty + 2;
                                                }
                                            }
                                        }

                                }

                                child.setVisible(visible);
                                if (visible) {
                                    child.setBounds(bx, by, bw, bh);
                                }
                            }
                            else {
                                scrollbutton.setBounds(0, 0, 0, 0);
                            }
                        }
                        else if (child != _tabPane.getTabLeadingComponent() && child != _tabPane.getTabTrailingComponent()) {
                            if (_tabPane.isShowTabContent()) {
                                // All content children...
                                child.setBounds(cx, cy, cw, ch);
                            }
                            else {
                                child.setBounds(0, 0, 0, 0);
                            }
                        }
                    }

                    if (leftToRight) {
                        if (isTabLeadingComponentVisible()) {
                            switch (_tabPane.getTabPlacement()) {
                                case LEFT:
                                    _tabLeadingComponent.setBounds(tx + tw - lsize.width, ty - lsize.height, lsize.width, lsize.height);
                                    break;
                                case RIGHT:
                                    _tabLeadingComponent.setBounds(tx, ty - lsize.height, lsize.width, lsize.height);
                                    break;
                                case BOTTOM:
                                    _tabLeadingComponent.setBounds(tx - lsize.width, ty, lsize.width, lsize.height);
                                    break;
                                case TOP:
                                default:
                                    _tabLeadingComponent.setBounds(tx - lsize.width, ty + th - lsize.height, lsize.width, lsize.height);
                                    break;
                            }

                        }

                        if (isTabTrailingComponentVisible()) {
                            switch (_tabPane.getTabPlacement()) {
                                case LEFT:
                                    _tabTrailingComponent.setBounds(tx + tw - tsize.width, ty + th, tsize.width, tsize.height);
                                    break;
                                case RIGHT:
                                    _tabTrailingComponent.setBounds(tx, ty + th, tsize.width, tsize.height);
                                    break;
                                case BOTTOM:
                                    _tabTrailingComponent.setBounds(tx + tw, ty, tsize.width, tsize.height);
                                    break;
                                case TOP:
                                default:
                                    _tabTrailingComponent.setBounds(tx + tw, ty + th - tsize.height, tsize.width, tsize.height);
                                    break;
                            }
                        }
                    }
                    else {
                        if (isTabTrailingComponentVisible()) {
                            switch (_tabPane.getTabPlacement()) {
                                case LEFT:
                                    _tabTrailingComponent.setBounds(tx + tw - tsize.width, ty - tsize.height, tsize.width, tsize.height);
                                    break;
                                case RIGHT:
                                    _tabTrailingComponent.setBounds(tx, ty - tsize.height, tsize.width, tsize.height);
                                    break;
                                case BOTTOM:
                                    _tabTrailingComponent.setBounds(tx - tsize.width, ty, tsize.width, tsize.height);
                                    break;
                                case TOP:
                                default:
                                    _tabTrailingComponent.setBounds(tx - tsize.width, ty + th - tsize.height, tsize.width, tsize.height);
                                    break;
                            }

                        }

                        if (isTabLeadingComponentVisible()) {
                            switch (_tabPane.getTabPlacement()) {
                                case LEFT:
                                    _tabLeadingComponent.setBounds(tx + tw - lsize.width, ty + th, lsize.width, lsize.height);
                                    break;
                                case RIGHT:
                                    _tabLeadingComponent.setBounds(tx, ty + th, lsize.width, lsize.height);
                                    break;
                                case BOTTOM:
                                    _tabLeadingComponent.setBounds(tx + tw, ty, lsize.width, lsize.height);
                                    break;
                                case TOP:
                                default:
                                    _tabLeadingComponent.setBounds(tx + tw, ty + th - lsize.height, lsize.width, lsize.height);
                                    break;
                            }
                        }
                    }

                    boolean verticalTabRuns = (tabPlacement == LEFT || tabPlacement == RIGHT);
                    if (!leftToRight && !verticalTabRuns && viewport != null && !viewport.getSize().equals(_tabPane.getSize())) {
                        int offset = _tabPane.getWidth() - viewport.getWidth();
                        for (Rectangle rect : _rects) {
                            rect.x -= offset;
                        }
                    }
                    updateCloseButtons();

                    if (shouldChangeFocus) {
                        if (!requestFocusForVisibleComponent()) {
                            if (!_tabPane.requestFocusInWindow()) {
                                _tabPane.requestFocus();
                            }
                        }
                    }
                }
            }
        }

        @Override
        protected void calculateTabRects(int tabPlacement, int tabCount) {
            Dimension size = _tabPane.getSize();
            Insets insets = _tabPane.getInsets();
            Insets tabAreaInsets = getTabAreaInsets(tabPlacement);
            boolean verticalTabRuns = (tabPlacement == LEFT || tabPlacement == RIGHT);
            boolean leftToRight = _tabPane.getComponentOrientation().isLeftToRight();
            int x = tabAreaInsets.left;
            int y = tabAreaInsets.top;

            //
            // Calculate bounds within which a tab run must fit
            //
            Dimension lsize = new Dimension(0, 0);
            Dimension tsize = new Dimension(0, 0);

            if (isTabLeadingComponentVisible()) {
                lsize = _tabLeadingComponent.getPreferredSize();
            }
            if (isTabTrailingComponentVisible()) {
                tsize = _tabTrailingComponent.getPreferredSize();
            }

            switch (tabPlacement) {
                case LEFT:
                case RIGHT:
                    _maxTabWidth = calculateMaxTabWidth(tabPlacement);
                    if (isTabLeadingComponentVisible()) {
                        if (tabPlacement == RIGHT) {
                            if (_maxTabWidth < lsize.width) {
                                _maxTabWidth = lsize.width;
                            }
                        }
                    }
                    if (isTabTrailingComponentVisible()) {
                        if (tabPlacement == RIGHT) {
                            if (_maxTabWidth < tsize.width) {
                                _maxTabWidth = tsize.width;
                            }
                        }
                    }
                    break;
                case BOTTOM:
                case TOP:
                default:
                    _maxTabHeight = calculateMaxTabHeight(tabPlacement);
                    if (isTabLeadingComponentVisible()) {
                        if (tabPlacement == BOTTOM) {
                            if (_maxTabHeight < lsize.height) {
                                _maxTabHeight = lsize.height;
                            }
                        }
                    }
                    if (isTabTrailingComponentVisible()) {
                        if (tabPlacement == BOTTOM) {
                            if (_maxTabHeight < tsize.height) {
                                _maxTabHeight = tsize.height;
                            }
                        }
                    }
            }

            _runCount = 0;
            _selectedRun = -1;

            if (tabCount == 0) {
                return;
            }

            _selectedRun = 0;
            _runCount = 1;

            // Run through tabs and lay them out in a single run
            Rectangle rect;
            for (int i = 0; i < tabCount; i++) {
                FontMetrics metrics = getFontMetrics(i);
                rect = _rects[i];

                if (!verticalTabRuns) {
                    // Tabs on TOP or BOTTOM....
                    if (i > 0) {
                        rect.x = _rects[i - 1].x + _rects[i - 1].width;
                    }
                    else {
                        _tabRuns[0] = 0;
                        _maxTabWidth = 0;
                        if (getTabShape() != JideTabbedPane.SHAPE_BOX) {
                            rect.x = x + getLeftMargin();// give the first tab arrow angle extra space
                        }
                        else {
                            rect.x = x;
                        }
                    }
                    rect.width = calculateTabWidth(tabPlacement, i, metrics) + _rectSizeExtend;
                    _maxTabWidth = Math.max(_maxTabWidth, rect.width);

                    rect.y = y;

                    int temp = -1;
                    if (isTabLeadingComponentVisible()) {
                        if (tabPlacement == TOP) {
                            if (_maxTabHeight < lsize.height) {
                                rect.y = y + lsize.height - _maxTabHeight - 2;
                                temp = lsize.height;

                                if (_rectSizeExtend > 0) {
                                    rect.y = rect.y + 2;
                                }
                            }

                        }
                    }
                    if (isTabTrailingComponentVisible()) {
                        if (tabPlacement == TOP) {
                            if (_maxTabHeight < tsize.height
                                    && temp < tsize.height) {
                                rect.y = y + tsize.height - _maxTabHeight - 2;

                                if (_rectSizeExtend > 0) {
                                    rect.y = rect.y + 2;
                                }
                            }

                        }
                    }
                    rect.height = calculateMaxTabHeight(tabPlacement);///* - 2 */;
                }
                else {
                    // Tabs on LEFT or RIGHT...
                    if (i > 0) {
                        rect.y = _rects[i - 1].y + _rects[i - 1].height;
                    }
                    else {
                        _tabRuns[0] = 0;
                        _maxTabHeight = 0;
                        if (getTabShape() != JideTabbedPane.SHAPE_BOX) {
                            rect.y = y + getLeftMargin();// give the first tab arrow angle extra space
                        }
                        else {
                            rect.y = y;
                        }
                    }
                    rect.height = calculateTabHeight(tabPlacement, i, metrics) + _rectSizeExtend;
                    _maxTabHeight = Math.max(_maxTabHeight, rect.height);

                    rect.x = x;
                    int temp = -1;
                    if (isTabLeadingComponentVisible()) {
                        if (tabPlacement == LEFT) {
                            if (_maxTabWidth < lsize.width) {
                                rect.x = x + lsize.width - _maxTabWidth - 2;
                                temp = lsize.width;

                                if (_rectSizeExtend > 0) {
                                    rect.x = rect.x + 2;
                                }
                            }

                        }
                    }
                    if (isTabTrailingComponentVisible()) {
                        if (tabPlacement == LEFT) {
                            if (_maxTabWidth < tsize.width
                                    && temp < tsize.width) {
                                rect.x = x + tsize.width - _maxTabWidth - 2;

                                if (_rectSizeExtend > 0) {
                                    rect.x = rect.x + 2;
                                }
                            }

                        }
                    }
                    rect.width = calculateMaxTabWidth(tabPlacement)/* - 2 */;

                }
            }

            // if right to left and tab placement on the top or
            // the bottom, flip x positions and adjust by widths
            if (!leftToRight && !verticalTabRuns) {
                int rightMargin = size.width
                        - (insets.right + tabAreaInsets.right);
                if (isTabLeadingComponentVisible()) {
                    rightMargin -= lsize.width;
                }
                int offset = 0;
                if (isTabTrailingComponentVisible()) {
                    offset += tsize.width;
                }
                for (int i = 0; i < tabCount; i++) {
                    _rects[i].x = rightMargin - _rects[i].x - _rects[i].width - offset + tabAreaInsets.left;
//                    if(i == tabCount - 1) {
//                        _rects[i].width += getLeftMargin();
//                        _rects[i].x -= getLeftMargin();
//                    }
                }
            }

            ensureCurrentRects(getLeftMargin(), tabCount);

        }
    }


    protected void ensureCurrentRects(int leftMargin, int tabCount) {
        Dimension size = _tabPane.getSize();
        Insets insets = _tabPane.getInsets();
        int totalWidth = 0;
        int totalHeight = 0;
        boolean verticalTabRuns = (_tabPane.getTabPlacement() == LEFT || _tabPane.getTabPlacement() == RIGHT);
        boolean ltr = _tabPane.getComponentOrientation().isLeftToRight();

        if (tabCount == 0) {
            return;
        }

        Rectangle r = _rects[tabCount - 1];

        Dimension lsize = new Dimension(0, 0);
        Dimension tsize = new Dimension(0, 0);

        if (isTabLeadingComponentVisible()) {
            lsize = _tabLeadingComponent.getPreferredSize();
        }
        if (isTabTrailingComponentVisible()) {
            tsize = _tabTrailingComponent.getPreferredSize();
        }


        if (verticalTabRuns) {
            totalHeight = r.y + r.height;

            if (_tabLeadingComponent != null) {
                totalHeight -= lsize.height;
            }

        }
        else {
//            totalWidth = r.x + r.width;
            for (Rectangle rect : _rects) {
                totalWidth += rect.width;
            }
            if (ltr) {
                totalWidth += _rects[0].x;
            }
            else {
                totalWidth += size.width - _rects[0].x - _rects[0].width;
            }

            if (_tabLeadingComponent != null) {
                totalWidth -= lsize.width;
            }

        }


        if (getTabResizeMode() == JideTabbedPane.RESIZE_MODE_FIT) {// LayOut Style is Size to Fix
            if (verticalTabRuns) {
                int availHeight;
                if (getTabShape() != JideTabbedPane.SHAPE_BOX) {
                    availHeight = (int) size.getHeight() - _fitStyleBoundSize
                            - insets.top - insets.bottom - leftMargin - getTabRightPadding();// give the first tab extra space
                }
                else {
                    availHeight = (int) size.getHeight() - _fitStyleBoundSize
                            - insets.top - insets.bottom;
                }

                if (_tabPane.isShowCloseButton()) {
                    availHeight -= _buttonSize;
                }

                if (isTabLeadingComponentVisible()) {
                    availHeight = availHeight - lsize.height;
                }
                if (isTabTrailingComponentVisible()) {
                    availHeight = availHeight - tsize.height;
                }

                int numberOfButtons = getNumberOfTabButtons();
                availHeight -= _buttonSize * numberOfButtons;
                if (totalHeight > availHeight) { // shrink is necessary
                    // calculate each tab width
                    int tabHeight = availHeight / tabCount;

                    totalHeight = _fitStyleFirstTabMargin; // start

                    for (int k = 0; k < tabCount; k++) {
                        _rects[k].height = tabHeight;
                        Rectangle tabRect = _rects[k];
                        if (getTabShape() != JideTabbedPane.SHAPE_BOX) {
                            tabRect.y = totalHeight + leftMargin;// give the first tab extra space
                        }
                        else {
                            tabRect.y = totalHeight;
                        }
                        totalHeight += tabRect.height;
                    }
                }

            }
            else {
                int availWidth;
                if (getTabShape() != JideTabbedPane.SHAPE_BOX) {
                    availWidth = (int) size.getWidth() - _fitStyleBoundSize
                            - insets.left - insets.right - leftMargin - getTabRightPadding();
                }
                else {
                    availWidth = (int) size.getWidth() - _fitStyleBoundSize
                            - insets.left - insets.right;
                }

                if (_tabPane.isShowCloseButton()) {
                    availWidth -= _buttonSize;
                }

                if (isTabLeadingComponentVisible()) {
                    availWidth -= lsize.width;
                }
                if (isTabTrailingComponentVisible()) {
                    availWidth -= tsize.width;
                }

                int numberOfButtons = getNumberOfTabButtons();
                availWidth -= _buttonSize * numberOfButtons;
                if (totalWidth > availWidth) { // shrink is necessary
                    // calculate each tab width
                    int tabWidth = availWidth / tabCount;
                    int gripperWidth = _tabPane.isShowGripper() ? _gripperWidth
                            : 0;
                    if (tabWidth < _textIconGap + _fitStyleTextMinWidth
                            + _fitStyleIconMinWidth + gripperWidth
                            && tabWidth > _fitStyleIconMinWidth + gripperWidth) // cannot
                        // hold any text but can hold an icon
                        tabWidth = _fitStyleIconMinWidth + gripperWidth;

                    if (tabWidth < _fitStyleIconMinWidth + gripperWidth
                            && tabWidth > _fitStyleFirstTabMargin + gripperWidth) // cannot
                        // hold any icon but gripper
                        tabWidth = _fitStyleFirstTabMargin + gripperWidth;

                    tryTabSpacer.reArrange(_rects, insets, availWidth);
                }
                totalWidth = _fitStyleFirstTabMargin; // start

                for (int k = 0; k < tabCount; k++) {
                    Rectangle tabRect = _rects[k];
                    if (getTabShape() != JideTabbedPane.SHAPE_BOX) {
                        if (ltr) {
                            tabRect.x = totalWidth + leftMargin;// give the first tab extra space when the style is not box style
                        }
                        else {
                            tabRect.x = availWidth - totalWidth - tabRect.width + leftMargin;// give the first tab extra space when the style is not box style
                        }
                    }
                    else {
                        if (ltr) {
                            tabRect.x = totalWidth;
                        }
                        else {
                            tabRect.x = availWidth - totalWidth - tabRect.width;
                        }
                    }
                    totalWidth += tabRect.width;
                }
            }
        }

        if (getTabResizeMode() == JideTabbedPane.RESIZE_MODE_FIXED) {// LayOut Style is Fix
            if (verticalTabRuns) {
                for (int k = 0; k < tabCount; k++) {
                    _rects[k].height = _fixedStyleRectSize;// + _rectSizeExtend * 2;

                    if (isShowCloseButton() && _tabPane.isShowCloseButtonOnTab()) {
                        _rects[k].height += _closeButtons[k].getPreferredSize().height;
                    }

                    if (k != 0) {
                        _rects[k].y = _rects[k - 1].y + _rects[k - 1].height;
                    }

                    totalHeight = _rects[k].y + _rects[k].height;
                }

            }
            else {
                for (int k = 0; k < tabCount; k++) {
                    int oldWidth = _rects[k].width;
                    _rects[k].width = _fixedStyleRectSize;

                    if (isShowCloseButton() && _tabPane.isShowCloseButtonOnTab()) {
                        _rects[k].width += _closeButtons[k].getPreferredSize().width;
                    }

                    if (k == 0 && !ltr) {
                        _rects[k].x += oldWidth - _rects[k].width;
                    }

                    if (k != 0) {
                        if (ltr) {
                            _rects[k].x = _rects[k - 1].x + _rects[k - 1].width;
                        }
                        else {
                            _rects[k].x = _rects[k - 1].x - _rects[k - 1].width;
                        }
                    }

                    totalWidth = _rects[k].x + _rects[k].width;
                }
            }
        }

        if (getTabResizeMode() == JideTabbedPane.RESIZE_MODE_COMPRESSED) {// LayOut Style is Compressed
            if (verticalTabRuns) {
                for (int k = 0; k < tabCount; k++) {
                    if (k != _tabPane.getSelectedIndex()) {
                        if (!_tabPane.isShowIconsOnTab() && !_tabPane.isUseDefaultShowIconsOnTab()) {
                            _rects[k].height = _compressedStyleNoIconRectSize;
                        }
                        else {
                            Icon icon = _tabPane.getIconForTab(k);
                            _rects[k].height = icon.getIconHeight() + _compressedStyleIconMargin;
                        }

                        if (isShowCloseButton() && isShowCloseButtonOnTab() && !_tabPane.isShowCloseButtonOnSelectedTab()) {
                            _rects[k].height = _rects[k].height + _closeButtons[k].getPreferredSize().height + _compressedStyleCloseButtonMarginVertical;
                        }
                    }

                    if (k != 0) {
                        _rects[k].y = _rects[k - 1].y + _rects[k - 1].height;
                    }

                    totalHeight = _rects[k].y + _rects[k].height;

                }

            }
            else {
                for (int k = 0; k < tabCount; k++) {
                    int oldWidth = _rects[k].width;
                    if (k != _tabPane.getSelectedIndex()) {
                        if (!_tabPane.isShowIconsOnTab()
                                && !_tabPane.isUseDefaultShowIconsOnTab()) {
                            _rects[k].width = _compressedStyleNoIconRectSize;
                        }
                        else {
                            Icon icon = _tabPane.getIconForTab(k);
                            _rects[k].width = icon.getIconWidth() + _compressedStyleIconMargin;
                        }

                        if (isShowCloseButton() && isShowCloseButtonOnTab() && !_tabPane.isShowCloseButtonOnSelectedTab()) {
                            _rects[k].width = _rects[k].width + _closeButtons[k].getPreferredSize().width + _compressedStyleCloseButtonMarginHorizon;
                        }

                    }

                    if (k == 0 && !ltr) {
                        _rects[k].x += oldWidth - _rects[k].width;
                    }

                    if (k != 0) {
                        if (ltr) {
                            _rects[k].x = _rects[k - 1].x + _rects[k - 1].width;
                        }
                        else {
                            _rects[k].x = _rects[k - 1].x - _rects[k - 1].width;
                        }
                    }

                    totalWidth = _rects[k].x + _rects[k].width;

                }

            }
        }

        if (_tabPane.getTabPlacement() == TOP || _tabPane.getTabPlacement() == BOTTOM) {
            totalWidth += getLayoutSize();

            if (isTabLeadingComponentVisible()) {
                totalWidth += lsize.width;
            }
        }
        else {
            totalHeight += getLayoutSize();

            if (isTabLeadingComponentVisible()) {
                totalHeight += tsize.height;
            }
        }

        _tabScroller.tabPanel.setPreferredSize(new Dimension(totalWidth, totalHeight));

    }

    protected class ActivateTabAction extends AbstractAction {
        int _tabIndex;

        public ActivateTabAction(String name, Icon icon, int tabIndex) {
            super(name, icon);
            _tabIndex = tabIndex;
        }

        public void actionPerformed(ActionEvent e) {
            _tabPane.setSelectedIndex(_tabIndex);
        }
    }

    protected ListCellRenderer getTabListCellRenderer() {
        return _tabPane.getTabListCellRenderer();
    }

    public class ScrollableTabSupport implements ChangeListener {
        public ScrollableTabViewport viewport;
        public ScrollableTabPanel tabPanel;
        public TabCloseButton scrollForwardButton;
        public TabCloseButton scrollBackwardButton;
        public TabCloseButton listButton;
        public TabCloseButton closeButton;
        public int leadingTabIndex;

        private Point tabViewPosition = new Point(0, 0);
        public JidePopup _popup;

        ScrollableTabSupport(int tabPlacement) {
            viewport = new ScrollableTabViewport();
            tabPanel = new ScrollableTabPanel();
            viewport.setView(tabPanel);
            viewport.addChangeListener(this);

            scrollForwardButton = createNoFocusButton(TabCloseButton.EAST_BUTTON);
            scrollForwardButton.setName(BUTTON_NAME_SCROLL_FORWARD);
            scrollBackwardButton = createNoFocusButton(TabCloseButton.WEST_BUTTON);
            scrollBackwardButton.setName(BUTTON_NAME_SCROLL_BACKWARD);

            scrollForwardButton.setBackground(viewport.getBackground());
            scrollBackwardButton.setBackground(viewport.getBackground());

            listButton = createNoFocusButton(TabCloseButton.LIST_BUTTON);
            listButton.setName(BUTTON_NAME_TAB_LIST);
            listButton.setBackground(viewport.getBackground());

            closeButton = createNoFocusButton(TabCloseButton.CLOSE_BUTTON);
            closeButton.setName(BUTTON_NAME_CLOSE);
            closeButton.setBackground(viewport.getBackground());
        }

        public void createPopupMenu(int tabPlacement) {
            JPopupMenu popup = new JPopupMenu();
            int totalCount = _tabPane.getTabCount();

            // drop down menu items
            int selectedIndex = _tabPane.getSelectedIndex();
            for (int i = 0; i < totalCount; i++) {
                if (_tabPane.isEnabledAt(i)) {
                    JMenuItem item;
                    popup.add(item = new JCheckBoxMenuItem(new ActivateTabAction(_tabPane.getTitleAt(i), _tabPane.getIconForTab(i), i)));
                    item.setToolTipText(_tabPane.getToolTipTextAt(i));
                    item.setSelected(selectedIndex == i);
                    item.setHorizontalTextPosition(JMenuItem.RIGHT);
                }
            }

            Dimension preferredSize = popup.getPreferredSize();
            Rectangle bounds = listButton.getBounds();
            switch (tabPlacement) {
                case TOP:
                    popup.show(_tabPane, bounds.x + bounds.width - preferredSize.width, bounds.y + bounds.height);
                    break;
                case BOTTOM:
                    popup.show(_tabPane, bounds.x + bounds.width - preferredSize.width, bounds.y - preferredSize.height);
                    break;
                case LEFT:
                    popup.show(_tabPane, bounds.x + bounds.width, bounds.y + bounds.height - preferredSize.height);
                    break;
                case RIGHT:
                    popup.show(_tabPane, bounds.x - preferredSize.width, bounds.y + bounds.height - preferredSize.height);
                    break;
            }
        }

        public void createPopup(int tabPlacement) {
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
            new Sticky(list);
            list.setBackground(_tabListBackground);
            JScrollPane scroller = new JScrollPane(list);
            scroller.setBorder(BorderFactory.createEmptyBorder());
            scroller.getViewport().setOpaque(false);
            scroller.setOpaque(false);

            JPanel panel = new JPanel(new BorderLayout());
            panel.setBackground(_tabListBackground);
            panel.setOpaque(true);
            panel.add(scroller);
            panel.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));

            if (_popup != null) {
                if (_popup.isPopupVisible()) {
                    _popup.hidePopupImmediately();
                }
                _popup = null;
            }
            _popup = com.jidesoft.popup.JidePopupFactory.getSharedInstance().createPopup();
            _popup.setPopupBorder(BorderFactory.createLineBorder(_darkShadow));
            _popup.add(panel);
            _popup.addExcludedComponent(listButton);
            _popup.setDefaultFocusComponent(list);

            DefaultListModel listModel = new DefaultListModel();

            // drop down menu items
            int selectedIndex = _tabPane.getSelectedIndex();
            int totalCount = _tabPane.getTabCount();
            for (int i = 0; i < totalCount; i++) {
                listModel.addElement(_tabPane);
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
            Insets insets = panel.getInsets();
            int max = (PortingUtils.getLocalScreenSize(_tabPane).height - insets.top - insets.bottom) / list.getCellBounds(0, 0).height;
            if (listModel.getSize() > max) {
                list.setVisibleRowCount(max);
            }
            else {
                list.setVisibleRowCount(listModel.getSize());
            }

            _popup.setOwner(_tabPane);
            _popup.removeExcludedComponent(_tabPane);

            Dimension size = _popup.getPreferredSize();
            Rectangle bounds = listButton.getBounds();
            Point p = listButton.getLocationOnScreen();
            bounds.x = p.x;
            bounds.y = p.y;
            int x;
            int y;
            switch (tabPlacement) {
                case TOP:
                default:
                    if (_tabPane.getComponentOrientation().isLeftToRight()) {
                        x = bounds.x + bounds.width - size.width;
                    }
                    else {
                        x = bounds.x;
                    }
                    y = bounds.y + bounds.height + 2;
                    break;
                case BOTTOM:
                    if (_tabPane.getComponentOrientation().isLeftToRight()) {
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

            Rectangle screenBounds = PortingUtils.getScreenBounds(_tabPane);
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

            _popup.showPopup(x, y);
        }

        private void componentSelected(JList list) {
            int tabIndex = list.getSelectedIndex();
            if (tabIndex != -1 && _tabPane.isEnabledAt(tabIndex)) {
                if (tabIndex == _tabPane.getSelectedIndex() && JideSwingUtilities.isAncestorOfFocusOwner(_tabPane)) {
                    if (_tabPane.isAutoFocusOnTabHideClose() && _tabPane.isRequestFocusEnabled()) {
                        Runnable runnable = new Runnable() {
                            public void run() {
                                _tabPane.requestFocus();
                            }
                        };
                        SwingUtilities.invokeLater(runnable);
                    }
                }
                else {
                    _tabPane.setSelectedIndex(tabIndex);
                    final Component comp = _tabPane.getComponentAt(tabIndex);
                    if (_tabPane.isAutoFocusOnTabHideClose() && !comp.isVisible() && SystemInfo.isJdk15Above() && !SystemInfo.isJdk6Above()) {
                        comp.addComponentListener(new ComponentAdapter() {
                            @Override
                            public void componentShown(ComponentEvent e) {
                                // remove the listener
                                comp.removeComponentListener(this);

                                final Component lastFocused = _tabPane.getLastFocusedComponent(comp);
                                Runnable runnable = new Runnable() {
                                    public void run() {
                                        if (lastFocused != null) {
                                            lastFocused.requestFocus();
                                        }
                                        else if (_tabPane.isRequestFocusEnabled()) {
                                            _tabPane.requestFocus();
                                        }
                                    }
                                };
                                SwingUtilities.invokeLater(runnable);
                            }
                        });
                    }
                    else {
                        final Component lastFocused = _tabPane.getLastFocusedComponent(comp);
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
                ensureActiveTabIsVisible(false);
                _popup.hidePopupImmediately();
                _popup = null;
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

        public void scrollForward(int tabPlacement) {
            Dimension viewSize = viewport.getViewSize();
            Rectangle viewRect = viewport.getViewRect();

            if (tabPlacement == TOP || tabPlacement == BOTTOM) {
                if (viewRect.width >= viewSize.width - viewRect.x) {
                    return; // no room left to scroll
                }
            }
            else { // tabPlacement == LEFT || tabPlacement == RIGHT
                if (viewRect.height >= viewSize.height - viewRect.y) {
                    return;
                }
            }
            setLeadingTabIndex(tabPlacement, leadingTabIndex + 1);
        }

        public void scrollBackward(int tabPlacement) {
            setLeadingTabIndex(tabPlacement, leadingTabIndex > 0 ? leadingTabIndex - 1 : 0);
        }

        public void setLeadingTabIndex(int tabPlacement, int index) {
            // make sure the index is in range
            if (index < 0 || index >= _tabPane.getTabCount()) {
                return;
            }
            leadingTabIndex = index;
            Dimension viewSize = viewport.getViewSize();
            Rectangle viewRect = viewport.getViewRect();

            switch (tabPlacement) {
                case TOP:
                case BOTTOM:
                    tabViewPosition.y = 0;
                    if (_tabPane.getComponentOrientation().isLeftToRight()) {
                        tabViewPosition.x = leadingTabIndex == 0 ? 0 : _rects[leadingTabIndex].x;

                        if ((viewSize.width - tabViewPosition.x) < viewRect.width) {
                            tabViewPosition.x = viewSize.width - viewRect.width;
                            //                        // We've scrolled to the end, so adjust the viewport size
                            //                        // to ensure the view position remains aligned on a tab boundary
                            //                        Dimension extentSize = new Dimension(viewSize.width - tabViewPosition.x,
                            //                                viewRect.height);
                            //                        System.out.println("setExtendedSize: " + extentSize);
                            //                        viewport.setExtentSize(extentSize);
                        }
                    }
                    else {
                        tabViewPosition.x -= 10;
                    }
                    break;
                case LEFT:
                case RIGHT:
                    tabViewPosition.x = 0;
                    tabViewPosition.y = leadingTabIndex == 0 ? 0 : _rects[leadingTabIndex].y;

                    if ((viewSize.height - tabViewPosition.y) < viewRect.height) {
                        tabViewPosition.y = viewSize.height - viewRect.height;
//                        // We've scrolled to the end, so adjust the viewport size
//                        // to ensure the view position remains aligned on a tab boundary
//                        Dimension extentSize = new Dimension(viewRect.width,
//                                viewSize.height - tabViewPosition.y);
//                        viewport.setExtentSize(extentSize);
                    }
                    break;
            }
            viewport.setViewPosition(tabViewPosition);
        }

        public void stateChanged(ChangeEvent e) {
            if (_tabPane == null) return;

            ensureCurrentLayout();

            JViewport viewport = (JViewport) e.getSource();
            int tabPlacement = _tabPane.getTabPlacement();
            int tabCount = _tabPane.getTabCount();
            Rectangle vpRect = viewport.getBounds();
            Dimension viewSize = viewport.getViewSize();
            Rectangle viewRect = viewport.getViewRect();

            if ((tabPlacement == TOP || tabPlacement == BOTTOM) && !_tabPane.getComponentOrientation().isLeftToRight()) {
                leadingTabIndex = getClosestTab(viewRect.x + viewRect.width, viewRect.y + viewRect.height);
                if (leadingTabIndex < 0) {
                    leadingTabIndex = 0;
                }
            }
            else {
                leadingTabIndex = getClosestTab(viewRect.x, viewRect.y);
            }

            // If the tab isn't right aligned, adjust it.
            if (leadingTabIndex < _rects.length && leadingTabIndex >= _rects.length) {
                switch (tabPlacement) {
                    case TOP:
                    case BOTTOM:
                        if (_rects[leadingTabIndex].x < viewRect.x) {
                            leadingTabIndex++;
                        }
                        break;
                    case LEFT:
                    case RIGHT:
                        if (_rects[leadingTabIndex].y < viewRect.y) {
                            leadingTabIndex++;
                        }
                        break;
                }
            }


            Insets contentInsets = getContentBorderInsets(tabPlacement);
            switch (tabPlacement) {
                case LEFT:
                    _tabPane.repaint(vpRect.x + vpRect.width, vpRect.y, contentInsets.left, vpRect.height);
                    scrollBackwardButton.setEnabled(viewRect.y > 0 || leadingTabIndex > 0);
                    scrollForwardButton.setEnabled(leadingTabIndex < tabCount - 1 && viewSize.height - viewRect.y > viewRect.height);
                    break;
                case RIGHT:
                    _tabPane.repaint(vpRect.x - contentInsets.right, vpRect.y, contentInsets.right, vpRect.height);
                    scrollBackwardButton.setEnabled(viewRect.y > 0 || leadingTabIndex > 0);
                    scrollForwardButton.setEnabled(leadingTabIndex < tabCount - 1 && viewSize.height - viewRect.y > viewRect.height);
                    break;
                case BOTTOM:
                    _tabPane.repaint(vpRect.x, vpRect.y - contentInsets.bottom, vpRect.width, contentInsets.bottom);
                    scrollBackwardButton.setEnabled(viewRect.x > 0 || leadingTabIndex > 0);
                    scrollForwardButton.setEnabled(leadingTabIndex < tabCount - 1 && viewSize.width - viewRect.x > viewRect.width);
                    break;
                case TOP:
                default:
                    _tabPane.repaint(vpRect.x, vpRect.y + vpRect.height, vpRect.width, contentInsets.top);
                    scrollBackwardButton.setEnabled(viewRect.x > 0 || leadingTabIndex > 0);
                    scrollForwardButton.setEnabled(leadingTabIndex < tabCount - 1 && viewSize.width - viewRect.x > viewRect.width);
            }

            if (SystemInfo.isJdk15Above()) {
                _tabPane.setComponentZOrder(_tabScroller.scrollForwardButton, 0);
                _tabPane.setComponentZOrder(_tabScroller.scrollBackwardButton, 0);
            }
            _tabScroller.scrollForwardButton.repaint();
            _tabScroller.scrollBackwardButton.repaint();


            int selectedIndex = _tabPane.getSelectedIndex();
            if (selectedIndex >= 0 && selectedIndex < _tabPane.getTabCount()) {
                closeButton.setEnabled(_tabPane.isTabClosableAt(selectedIndex));
            }
        }

        @Override
        public String toString() {
            return "viewport.viewSize=" + viewport.getViewSize() + "\n" +
                    "viewport.viewRectangle=" + viewport.getViewRect() + "\n" +
                    "leadingTabIndex=" + leadingTabIndex + "\n" +
                    "tabViewPosition=" + tabViewPosition;
        }

    }

    public class ScrollableTabViewport extends JViewport implements UIResource {
        public ScrollableTabViewport() {
            super();
            setScrollMode(JViewport.SIMPLE_SCROLL_MODE);
            setOpaque(false);
        }

        /**
         * Gets the background color of this component.
         *
         * @return this component's background color; if this component does not have a background color, the background
         *         color of its parent is returned
         */
        @Override
        public Color getBackground() {
            return UIDefaultsLookup.getColor("JideTabbedPane.background");
        }
    }

    public class ScrollableTabPanel extends JPanel implements UIResource {
        public ScrollableTabPanel() {
            setLayout(null);
        }

        @Override
        public boolean isOpaque() {
            return false;
        }

        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);

            if (_tabPane.isOpaque()) {
                if (getTabShape() == JideTabbedPane.SHAPE_BOX) {
                    g.setColor(UIDefaultsLookup.getColor("JideTabbedPane.selectedTabBackground"));
                }
                else {
                    g.setColor(UIDefaultsLookup.getColor("JideTabbedPane.tabAreaBackground"));
                }
                g.fillRect(0, 0, getWidth(), getHeight());
            }

            paintTabArea(g, _tabPane.getTabPlacement(), _tabPane.getSelectedIndex(), this);
        }

        // workaround for swing bug
        // http://developer.java.sun.com/developer/bugParade/bugs/4668865.html
        @Override
        public void setToolTipText(String text) {
            _tabPane.setToolTipText(text);
        }

        @Override
        public String getToolTipText() {
            return _tabPane.getToolTipText();
        }

        @Override
        public String getToolTipText(MouseEvent event) {
            return _tabPane.getToolTipText(SwingUtilities.convertMouseEvent(this, event, _tabPane));
        }

        @Override
        public Point getToolTipLocation(MouseEvent event) {
            return _tabPane.getToolTipLocation(SwingUtilities.convertMouseEvent(this, event, _tabPane));
        }

        @Override
        public JToolTip createToolTip() {
            return _tabPane.createToolTip();
        }
    }

    protected Color _closeButtonSelectedColor = new Color(255, 162, 165);
    protected Color _closeButtonColor = Color.BLACK;
    protected Color _popupColor = Color.BLACK;

    /**
     * Close button on the tab.
     */
    public class TabCloseButton extends JButton implements MouseMotionListener, MouseListener, UIResource {
        public static final int CLOSE_BUTTON = 0;
        public static final int EAST_BUTTON = 1;
        public static final int WEST_BUTTON = 2;
        public static final int NORTH_BUTTON = 3;
        public static final int SOUTH_BUTTON = 4;
        public static final int LIST_BUTTON = 5;
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

        public TabCloseButton() {
            this(CLOSE_BUTTON);
        }

        public TabCloseButton(int type) {
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
            if (!isEnabled()) {
                setMouseOver(false);
                setMousePressed(false);
            }
            if (isMouseOver() && isMousePressed()) {
                g.setColor(UIDefaultsLookup.getColor("controlDkShadow"));
                g.drawLine(0, 0, getWidth() - 1, 0);
                g.drawLine(0, getHeight() - 2, 0, 1);
                g.setColor(UIDefaultsLookup.getColor("control"));
                g.drawLine(getWidth() - 1, 1, getWidth() - 1, getHeight() - 2);
                g.drawLine(getWidth() - 1, getHeight() - 1, 0, getHeight() - 1);
            }
            else if (isMouseOver()) {
                g.setColor(UIDefaultsLookup.getColor("control"));
                g.drawLine(0, 0, getWidth() - 1, 0);
                g.drawLine(0, getHeight() - 2, 0, 1);
                g.setColor(UIDefaultsLookup.getColor("controlDkShadow"));
                g.drawLine(getWidth() - 1, 1, getWidth() - 1, getHeight() - 2);
                g.drawLine(getWidth() - 1, getHeight() - 1, 0, getHeight() - 1);
            }
            g.setColor(UIDefaultsLookup.getColor("controlShadow").darker());
            int centerX = getWidth() >> 1;
            int centerY = getHeight() >> 1;
            switch (getType()) {
                case CLOSE_BUTTON:
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
                case EAST_BUTTON:
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
                    if (_tabPane.getTabPlacement() == TOP || _tabPane.getTabPlacement() == BOTTOM) {
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
                case WEST_BUTTON: {
                    //
                    // |
                    // ||
                    // |||
                    // ||||
                    // *||||
                    // ||||
                    // |||
                    // ||
                    // |
                    //
                    {
                        if (_tabPane.getTabPlacement() == TOP || _tabPane.getTabPlacement() == BOTTOM) {
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
                case LIST_BUTTON: {
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

        public boolean scrollsForward() {
            return getType() == EAST_BUTTON || getType() == SOUTH_BUTTON;
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
            _tabScroller.tabPanel.repaint();
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

// Controller: event listeners

    /**
     * This inner class is marked &quot;public&quot; due to a compiler bug. This class should be treated as a
     * &quot;protected&quot; inner class. Instantiate it only within subclasses of VsnetJideTabbedPaneUI.
     */
    public class PropertyChangeHandler implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent e) {
            JTabbedPane pane = (JTabbedPane) e.getSource();
            String name = e.getPropertyName();
            if ("mnemonicAt".equals(name)) {
                updateMnemonics();
                pane.repaint();
            }
            else if ("displayedMnemonicIndexAt".equals(name)) {
                pane.repaint();
            }
            else if (name.equals("indexForTitle")) {
                int index = (Integer) e.getNewValue();
                String title = getCurrentDisplayTitleAt(_tabPane, index);
                if (BasicHTML.isHTMLString(title)) {
                    if (htmlViews == null) { // Initialize vector
                        htmlViews = createHTMLVector();
                    }
                    else { // Vector already exists
                        View v = BasicHTML.createHTMLView(_tabPane, title);
                        htmlViews.setElementAt(v, index);
                    }
                }
                else {
                    if (htmlViews != null && htmlViews.elementAt(index) != null) {
                        htmlViews.setElementAt(null, index);
                    }
                }
                updateMnemonics();

                if (scrollableTabLayoutEnabled()) {
                    _tabScroller.viewport.setViewSize(new Dimension(
                            _tabPane.getWidth(), _tabScroller.viewport.getViewSize().height));
                    ensureActiveTabIsVisible(false);
                }
            }
            else if (name.equals("tabLayoutPolicy")) {
                _tabPane.updateUI();
            }
            else if (name.equals("closeTabAction")) {
                updateCloseAction();
            }
            else if (name.equals(JideTabbedPane.PROPERTY_DRAG_OVER_DISABLED)) {
                _tabPane.updateUI();
            }
            else if (name.equals(JideTabbedPane.PROPERTY_TAB_COLOR_PROVIDER)) {
                _tabPane.repaint();
            }
            else if (name.equals("locale")) {
                _tabPane.updateUI();
            }
            else if (name.equals(JideTabbedPane.BOLDACTIVETAB_PROPERTY)) {
                getTabPanel().invalidate();
                _tabPane.invalidate();
                if (scrollableTabLayoutEnabled()) {
                    _tabScroller.viewport.setViewSize(new Dimension(_tabPane.getWidth(), _tabScroller.viewport.getViewSize().height));
                    ensureActiveTabIsVisible(true);
                }
            }
            else if (name.equals(JideTabbedPane.PROPERTY_TAB_LEADING_COMPONENT)) {
                ensureCurrentLayout();
                if (_tabLeadingComponent != null) {
                    _tabLeadingComponent.setVisible(false);
                    _tabPane.remove(_tabLeadingComponent);
                }
                _tabLeadingComponent = (Component) e.getNewValue();
                if (_tabLeadingComponent != null) {
                    _tabLeadingComponent.setVisible(true);
                    _tabPane.add(_tabLeadingComponent);
                }
                _tabScroller.tabPanel.updateUI();
            }
            else if (name.equals(JideTabbedPane.PROPERTY_TAB_TRAILING_COMPONENT)) {
                ensureCurrentLayout();
                if (_tabTrailingComponent != null) {
                    _tabTrailingComponent.setVisible(false);
                    _tabPane.remove(_tabTrailingComponent);
                }
                _tabTrailingComponent = (Component) e.getNewValue();
                if (_tabTrailingComponent != null) {
                    _tabPane.add(_tabTrailingComponent);
                    _tabTrailingComponent.setVisible(true);
                }
                _tabScroller.tabPanel.updateUI();
            }
            else if (name.equals(JideTabbedPane.SHRINK_TAB_PROPERTY) ||
                    name.equals(JideTabbedPane.HIDE_IF_ONE_TAB_PROPERTY) ||
                    name.equals(JideTabbedPane.SHOW_TAB_AREA_PROPERTY) ||
                    name.equals(JideTabbedPane.SHOW_TAB_CONTENT_PROPERTY) ||
                    name.equals(JideTabbedPane.BOX_STYLE_PROPERTY) ||
                    name.equals(JideTabbedPane.SHOW_ICONS_PROPERTY) ||
                    name.equals(JideTabbedPane.SHOW_CLOSE_BUTTON_PROPERTY) ||
                    name.equals(JideTabbedPane.USE_DEFAULT_SHOW_ICONS_PROPERTY) ||
                    name.equals(JideTabbedPane.SHOW_CLOSE_BUTTON_ON_TAB_PROPERTY) ||
                    name.equals(JideTabbedPane.USE_DEFAULT_SHOW_CLOSE_BUTTON_ON_TAB_PROPERTY) ||
                    name.equals(JideTabbedPane.TAB_CLOSABLE_PROPERTY) ||
                    name.equals(JideTabbedPane.PROPERTY_TAB_SHAPE) ||
                    name.equals(JideTabbedPane.PROPERTY_COLOR_THEME) ||
                    name.equals(JideTabbedPane.PROPERTY_TAB_RESIZE_MODE) ||
                    name.equals(JideTabbedPane.SHOW_TAB_BUTTONS_PROPERTY)) {
                if ((name.equals(JideTabbedPane.USE_DEFAULT_SHOW_CLOSE_BUTTON_ON_TAB_PROPERTY) || name.equals(JideTabbedPane.SHOW_CLOSE_BUTTON_ON_TAB_PROPERTY))
                        && isShowCloseButton() && isShowCloseButtonOnTab()) {
                    ensureCloseButtonCreated();
                }
                _tabPane.updateUI();
            }
        }
    }

    protected void updateCloseAction() {
        ensureCloseButtonCreated();
    }

    /**
     * This inner class is marked &quot;public&quot; due to a compiler bug. This class should be treated as a
     * &quot;protected&quot; inner class. Instantiate it only within subclasses of VsnetJideTabbedPaneUI.
     */
    public class TabSelectionHandler implements ChangeListener {
        public void stateChanged(ChangeEvent e) {
            ((BasicJideTabbedPaneUI) _tabPane.getUI()).stopOrCancelEditing();//pane.stopTabEditing();
            ensureCloseButtonCreated();
            ensureActiveTabIsVisible(false);
        }
    }

    public class TabFocusListener implements FocusListener {
        public void focusGained(FocusEvent e) {
            repaintSelectedTab();
        }

        public void focusLost(FocusEvent e) {
            repaintSelectedTab();
        }

        private void repaintSelectedTab() {
            if (_tabPane.getTabCount() > 0) {
                Rectangle rect = getTabBounds(_tabPane, _tabPane.getSelectedIndex());
                if (rect != null) {
                    _tabPane.repaint(rect);
                }
            }
        }
    }

    public class MouseMotionHandler extends MouseMotionAdapter {

    }

    /**
     * This inner class is marked &quot;public&quot; due to a compiler bug. This class should be treated as a
     * &quot;protected&quot; inner class. Instantiate it only within subclasses of VsnetJideTabbedPaneUI.
     */
    public class MouseHandler extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent e) {
            if (_tabPane == null || !_tabPane.isEnabled()) {
                return;
            }

            if (SwingUtilities.isMiddleMouseButton(e)) {
                int tabIndex = tabForCoordinate(_tabPane, e.getX(), e.getY());
                Action action = getActionMap().get("closeTabAction");
                if (action != null && tabIndex >= 0 && _tabPane.isEnabledAt(tabIndex) && _tabPane.isCloseTabOnMouseMiddleButton() && _tabPane.isTabClosableAt(tabIndex)) {
                    ActionEvent event = new ActionEvent(_tabPane, tabIndex, "middleMouseButtonClicked");
                    action.actionPerformed(event);
                }
            }
        }

        @Override
        public void mousePressed(MouseEvent e) {
            if (_tabPane == null || !_tabPane.isEnabled()) {
                return;
            }

            if (SwingUtilities.isLeftMouseButton(e) || _tabPane.isRightClickSelect()) {
                int tabIndex = tabForCoordinate(_tabPane, e.getX(), e.getY());
                if (tabIndex >= 0 && _tabPane.isEnabledAt(tabIndex)) {
                    if (tabIndex == _tabPane.getSelectedIndex() && JideSwingUtilities.isAncestorOfFocusOwner(_tabPane)) {
                        if (_tabPane.isAutoFocusOnTabHideClose() && _tabPane.isRequestFocusEnabled()) {
//                            if (!_tabPane.requestFocusInWindow()) {
                            _tabPane.requestFocus();
//                            }
                        }
                    }
                    else {
                        _tabPane.setSelectedIndex(tabIndex);
                        _tabPane.processMouseSelection(tabIndex, e);
                        final Component comp = _tabPane.getComponentAt(tabIndex);
                        if (_tabPane.isAutoFocusOnTabHideClose() && !comp.isVisible() && SystemInfo.isJdk15Above() && !SystemInfo.isJdk6Above()) {
                            comp.addComponentListener(new ComponentAdapter() {
                                @Override
                                public void componentShown(ComponentEvent e) {
                                    // remove the listener
                                    comp.removeComponentListener(this);

                                    Component lastFocused = _tabPane.getLastFocusedComponent(comp);
                                    if (lastFocused != null) {
                                        // this code works in JDK6 but on JDK5
//                                        if (!lastFocused.requestFocusInWindow()) {
                                        lastFocused.requestFocus();
//                                        }
                                    }
                                    else if (_tabPane.isRequestFocusEnabled()) {
//                                        if (!_tabPane.requestFocusInWindow()) {
                                        _tabPane.requestFocus();
//                                        }
                                    }
                                }
                            });
                        }
                        else {
                            Component lastFocused = _tabPane.getLastFocusedComponent(comp);
                            if (lastFocused != null) {
                                // this code works in JDK6 but on JDK5
//                                if (!lastFocused.requestFocusInWindow()) {
                                lastFocused.requestFocus();
//                                }
                            }
                            else {
                                // first try to find a default component.
                                boolean foundInTab = JideSwingUtilities.compositeRequestFocus(comp);
                                if (!foundInTab) { // && !_tabPane.requestFocusInWindow()) {
                                    _tabPane.requestFocus();
                                }
                            }
                        }
                    }
                }
            }
            if (!isTabEditing())
                startEditing(e); // start editing tab
        }

    }

    public class MouseWheelHandler implements MouseWheelListener {
        public void mouseWheelMoved(MouseWheelEvent e) {
            if (_tabPane.isScrollSelectedTabOnWheel()) {
                // set selected tab to the currently selected tab plus the wheel rotation but between
                // 0 and tabCount-1
                _tabPane.setSelectedIndex(
                        Math.min(_tabPane.getTabCount() - 1, Math.max(0, _tabPane.getSelectedIndex() + e.getWheelRotation())));
            }
            else if (scrollableTabLayoutEnabled() && e.getWheelRotation() != 0) {
                if (e.getWheelRotation() > 0) {
                    for (int i = 0; i < e.getScrollAmount(); i++) {
                        _tabScroller.scrollForward(_tabPane.getTabPlacement());
                    }
                }
                else if (e.getWheelRotation() < 0) {
                    for (int i = 0; i < e.getScrollAmount(); i++) {
                        _tabScroller.scrollBackward(_tabPane.getTabPlacement());
                    }
                }
            }
        }
    }

    private class ComponentHandler implements ComponentListener {
        public void componentResized(ComponentEvent e) {
            if (scrollableTabLayoutEnabled()) {
                _tabScroller.viewport.setViewSize(new Dimension(_tabPane.getWidth(), _tabScroller.viewport.getViewSize().height));
                ensureActiveTabIsVisible(true);
            }
        }

        public void componentMoved(ComponentEvent e) {
        }

        public void componentShown(ComponentEvent e) {
        }

        public void componentHidden(ComponentEvent e) {
        }
    }

    /* GES 2/3/99:
       The container listener code was added to support HTML
       rendering of tab titles.

       Ideally, we would be able to listen for property changes
       when a tab is added or its text modified.  At the moment
       there are no such events because the Beans spec doesn't
       allow 'indexed' property changes (i.e. tab 2's text changed
       from A to B).

       In order to get around this, we listen for tabs to be added
       or removed by listening for the container events.  we then
       queue up a runnable (so the component has a chance to complete
       the add) which checks the tab title of the new component to see
       if it requires HTML rendering.

       The Views (one per tab title requiring HTML rendering) are
       stored in the htmlViews Vector, which is only allocated after
       the first time we run into an HTML tab.  Note that this vector
       is kept in step with the number of pages, and nulls are added
       for those pages whose tab title do not require HTML rendering.

       This makes it easy for the paint and layout code to tell
       whether to invoke the HTML engine without having to check
       the string during time-sensitive operations.

       When we have added a way to listen for tab additions and
       changes to tab text, this code should be removed and
       replaced by something which uses that.  */

    private class ContainerHandler implements ContainerListener {
        public void componentAdded(ContainerEvent e) {
            JideTabbedPane tp = (JideTabbedPane) e.getContainer();
//            updateTabPanel();
            Component child = e.getChild();
            if (child instanceof UIResource || child == tp.getTabLeadingComponent() || child == tp.getTabTrailingComponent()) {
                return;
            }

            int index = tp.indexOfComponent(child);
            String title = getCurrentDisplayTitleAt(tp, index);
            boolean isHTML = BasicHTML.isHTMLString(title);
            if (isHTML) {
                if (htmlViews == null) { // Initialize vector
                    htmlViews = createHTMLVector();
                }
                else { // Vector already exists
                    View v = BasicHTML.createHTMLView(tp, title);
                    htmlViews.insertElementAt(v, index);
                }
            }
            else { // Not HTML
                if (htmlViews != null) { // Add placeholder
                    htmlViews.insertElementAt(null, index);
                } // else nada!
            }

            if (_tabPane.isTabEditing()) {
                ((BasicJideTabbedPaneUI) _tabPane.getUI()).stopOrCancelEditing();//_tabPane.stopTabEditing();
            }

            ensureCloseButtonCreated();
        }

        public void componentRemoved(ContainerEvent e) {
            JideTabbedPane tp = (JideTabbedPane) e.getContainer();
//            updateTabPanel();
            Component child = e.getChild();
            if (child instanceof UIResource || child == tp.getTabLeadingComponent() || child == tp.getTabTrailingComponent()) {
                return;
            }

            // NOTE 4/15/2002 (joutwate):
            // This fix is implemented using client properties since there is
            // currently no IndexPropertyChangeEvent. Once
            // IndexPropertyChangeEvents have been added this code should be
            // modified to use it.
            Integer index =
                    (Integer) tp.getClientProperty("__index_to_remove__");
            if (index != null) {
                if (htmlViews != null && htmlViews.size() >= index) {
                    htmlViews.removeElementAt(index);
                }
                tp.putClientProperty("__index_to_remove__", null);
            }

            if (_tabPane.isTabEditing()) {
                ((BasicJideTabbedPaneUI) _tabPane.getUI()).stopOrCancelEditing();//_tabPane.stopTabEditing();
            }

            ensureCloseButtonCreated();
//            ensureActiveTabIsVisible(true);
        }
    }

    private Vector createHTMLVector() {
        Vector htmlViews = new Vector();
        int count = _tabPane.getTabCount();
        if (count > 0) {
            for (int i = 0; i < count; i++) {
                String title = getCurrentDisplayTitleAt(_tabPane, i);
                if (BasicHTML.isHTMLString(title)) {
                    htmlViews.addElement(BasicHTML.createHTMLView(_tabPane, title));
                }
                else {
                    htmlViews.addElement(null);
                }
            }
        }
        return htmlViews;
    }

    @Override
    public Component getTabPanel() {
        if (scrollableTabLayoutEnabled())
            return _tabScroller.tabPanel;
        else
            return _tabPane;
    }

    static class AbstractTab {
        int width;

        int id;

        public void copy(AbstractTab tab) {
            this.width = tab.width;
            this.id = tab.id;
        }
    }

    public static class TabSpaceAllocator {
        static final int startOffset = 4;

        private Insets insets = null;

        static final int tabWidth = 24;

        static final int textIconGap = 8;

        private AbstractTab tabs[];

        private void setInsets(Insets insets) {
            this.insets = (Insets) insets.clone();
        }

        private void init(Rectangle rects[], Insets insets) {
            setInsets(insets);
            tabs = new AbstractTab[rects.length];
            // fill up internal datastructure
            for (int i = 0; i < rects.length; i++) {
                tabs[i] = new AbstractTab();
                tabs[i].id = i;
                tabs[i].width = rects[i].width;
            }
            tabSort();
        }

        private void bestfit(AbstractTab tabs[], int freeWidth, int startTab) {
            int tabCount = tabs.length;
            int worstWidth;
            int currentTabWidth;

            int initialPos;
            currentTabWidth = tabs[startTab].width;
            initialPos = startTab;

            if (startTab == tabCount - 1) {
                // directly fill as worst case
                tabs[startTab].width = freeWidth;
                return;
            }
            worstWidth = freeWidth / (tabCount - startTab);

            while (currentTabWidth < worstWidth) {
                freeWidth -= currentTabWidth;
                if (++startTab < tabCount - 1) {
                    currentTabWidth = tabs[startTab].width;
                }
                else {
                    tabs[startTab].width = worstWidth;
                    return;
                }
            }

            if (startTab == initialPos) {
                // didn't find anything smaller
                for (int i = startTab; i < tabCount; i++) {
                    tabs[i].width = worstWidth;
                }
            }
            else if (startTab < tabCount - 1) {
                bestfit(tabs, freeWidth, startTab);
            }
        }

        // bubble sort for now
        private void tabSort() {
            int tabCount = tabs.length;
            AbstractTab tempTab = new AbstractTab();
            for (int i = 0; i < tabCount - 1; i++) {
                for (int j = i + 1; j < tabCount; j++) {
                    if (tabs[i].width > tabs[j].width) {
                        tempTab.copy(tabs[j]);
                        tabs[j].copy(tabs[i]);
                        tabs[i].copy(tempTab);
                    }
                }
            }
        }

        // directly modify the rects
        private void outpush(Rectangle rects[]) {
            for (AbstractTab tab : tabs) {
                rects[tab.id].width = tab.width;
            }
            rects[0].x = startOffset;
            for (int i = 1; i < rects.length; i++) {
                rects[i].x = rects[i - 1].x + rects[i - 1].width;
            }
        }

        public void reArrange(Rectangle rects[], Insets insets, int totalAvailableSpace) {
            init(rects, insets);
            bestfit(tabs, totalAvailableSpace, 0);
            outpush(rects);
            clearup();
        }

        private void clearup() {
            for (int i = 0; i < tabs.length; i++) {
                tabs[i] = null;
            }
            tabs = null;
        }
    }

    @Override
    public void ensureActiveTabIsVisible(boolean scrollLeft) {
        if (_tabPane == null || _tabPane.getWidth() == 0) {
            return;
        }

        if (scrollableTabLayoutEnabled()) {
            ensureCurrentLayout();
            if (scrollLeft && _rects.length > 0) {
                if (_tabPane.getTabPlacement() == LEFT || _tabPane.getTabPlacement() == RIGHT || _tabPane.getComponentOrientation().isLeftToRight()) {
                    _tabScroller.viewport.setViewPosition(new Point(0, 0));
                    _tabScroller.tabPanel.scrollRectToVisible(_rects[0]);
                }
                else {
                    _tabScroller.viewport.setViewPosition(new Point(0, 0));
                }
            }
            int index = _tabPane.getSelectedIndex();
            if ((!scrollLeft || index != 0) && index < _rects.length && index != -1) {
                if (index == 0) {
                    _tabScroller.viewport.setViewPosition(new Point(0, 0));
                }
                else {
                    if (index == _rects.length - 1) { // last one, scroll to the end
                        Rectangle lastRect = _rects[index];
                        lastRect.width = _tabScroller.tabPanel.getWidth() - lastRect.x;
                        _tabScroller.tabPanel.scrollRectToVisible(lastRect);
                    }
                    else if (index == 0) { // first one, scroll to the front
                        Rectangle firstRect = _rects[index];
                        firstRect.x = 0;
                        _tabScroller.tabPanel.scrollRectToVisible(firstRect);
                    }
                    else {
                        _tabScroller.tabPanel.scrollRectToVisible(_rects[index]);
                    }
                }
                _tabScroller.tabPanel.getParent().doLayout();
            }
            _tabPane.revalidate();
            _tabPane.repaintTabAreaAndContentBorder();
        }
    }

    protected boolean isShowCloseButtonOnTab() {
        if (_tabPane.isUseDefaultShowCloseButtonOnTab()) {
            return _showCloseButtonOnTab;
        }
        else return _tabPane.isShowCloseButtonOnTab();
    }

    protected boolean isShowCloseButton() {
        return _tabPane.isShowCloseButton();
    }

    public void ensureCloseButtonCreated() {
        if (isShowCloseButton() && isShowCloseButtonOnTab() && scrollableTabLayoutEnabled()) {
            if (_closeButtons == null) {
                _closeButtons = new TabCloseButton[_tabPane.getTabCount()];
            }
            else if (_closeButtons.length > _tabPane.getTabCount()) {
                TabCloseButton[] temp = new TabCloseButton[_tabPane
                        .getTabCount()];
                System.arraycopy(_closeButtons, 0, temp, 0, temp.length);
                for (int i = temp.length; i < _closeButtons.length; i++) {
                    TabCloseButton tabCloseButton = _closeButtons[i];
                    _tabScroller.tabPanel.remove(tabCloseButton);
                }
                _closeButtons = temp;
            }
            else if (_closeButtons.length < _tabPane.getTabCount()) {
                TabCloseButton[] temp = new TabCloseButton[_tabPane
                        .getTabCount()];
                System.arraycopy(_closeButtons, 0, temp, 0,
                        _closeButtons.length);
                _closeButtons = temp;
            }
            ActionMap am = getActionMap();
            for (int i = 0; i < _closeButtons.length; i++) {
                TabCloseButton closeButton = _closeButtons[i];
                if (closeButton == null) {
                    closeButton = createNoFocusButton(TabCloseButton.CLOSE_BUTTON);
                    closeButton.setName("JideTabbedPane.close");
                    _closeButtons[i] = closeButton;
                    closeButton.setBounds(0, 0, 0, 0);
                    Action action = _tabPane.getCloseAction();
                    closeButton.setAction(am.get("closeTabAction"));
                    updateButtonFromAction(closeButton, action);
                    _tabScroller.tabPanel.add(closeButton);
                }
                closeButton.setIndex(i);
            }
        }
    }

    private void updateButtonFromAction(TabCloseButton closeButton, Action action) {
        if (action == null) {
            return;
        }
        closeButton.setEnabled(action.isEnabled());
        Object desc = action.getValue(Action.SHORT_DESCRIPTION);
        if (desc instanceof String) {
            closeButton.setToolTipText((String) desc);
        }
        Object icon = action.getValue(Action.SMALL_ICON);
        if (icon instanceof Icon) {
            closeButton.setIcon((Icon) icon);
        }
    }

    protected boolean isShowTabButtons() {
        return _tabPane.getTabCount() != 0 && _tabPane.isShowTabArea() && _tabPane.isShowTabButtons();
    }

    protected boolean isShrinkTabs() {
        return _tabPane.getTabCount() != 0 && _tabPane.getTabResizeMode() == JideTabbedPane.RESIZE_MODE_FIT;
    }

    protected TabEditor _tabEditor;

    protected boolean _isEditing;

    protected int _editingTab = -1;

    protected String _oldValue;

    protected String _oldPrefix;

    protected String _oldPostfix;
    // mtf - changed
    protected Component _originalFocusComponent;

    @Override
    public boolean isTabEditing() {
        return _isEditing;
    }

    protected TabEditor createDefaultTabEditor() {
        final TabEditor editor = new TabEditor();
        editor.getDocument().addDocumentListener(this);
        editor.setInputVerifier(new InputVerifier() {
            @Override
            public boolean verify(JComponent input) {
                return true;
            }

            public boolean shouldYieldFocus(JComponent input) {
                boolean shouldStopEditing = true;
                if (_tabPane != null && _tabPane.isTabEditing() && _tabPane.getTabEditingValidator() != null) {
                    shouldStopEditing = _tabPane.getTabEditingValidator().alertIfInvalid(_editingTab, _oldPrefix + _tabEditor.getText() + _oldPostfix);
                }

                if (shouldStopEditing && _tabPane != null && _tabPane.isTabEditing()) {
                    _tabPane.stopTabEditing();
                }

                return shouldStopEditing;
            }
        });
        editor.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                _originalFocusComponent = e.getOppositeComponent();
            }

            @Override
            public void focusLost(FocusEvent e) {
            }
        });
        editor.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                editor.transferFocus();
            }
        });
        editor.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (_isEditing && (e.getKeyCode() == KeyEvent.VK_ESCAPE)) {
                    if (_editingTab >= 0 && _editingTab < _tabPane.getTabCount()) {
                        _tabPane.setTitleAt(_editingTab, _oldValue);
                    }
                    _tabPane.cancelTabEditing();
                }
            }
        });
        editor.setFont(_tabPane.getFont());

        return editor;
    }

    @Override
    public void stopTabEditing() {
        if (_editingTab >= 0 && _editingTab < _tabPane.getTabCount()) {
            _tabPane.setTitleAt(_editingTab, _oldPrefix + _tabEditor.getText() + _oldPostfix);
        }
        cancelTabEditing();
    }

    @Override
    public void cancelTabEditing() {
        if (_tabEditor != null) {
            _isEditing = false;

            ((Container) getTabPanel()).remove(_tabEditor);

            if (_editingTab >= 0 && _editingTab < _tabPane.getTabCount()) {
                Rectangle tabRect = _tabPane.getBoundsAt(_editingTab);
                getTabPanel().repaint(tabRect.x, tabRect.y,
                        tabRect.width, tabRect.height);
            }
            else {
                getTabPanel().repaint();
            }

            if (_originalFocusComponent != null)
                _originalFocusComponent.requestFocus(); // InWindow();
            else
                _tabPane.requestFocusForVisibleComponent();

            _editingTab = -1;
            _oldValue = null;
            _tabPane.doLayout();
        }
    }

    @Override
    public boolean editTabAt(int tabIndex) {
        if (_isEditing) {
            return false;
        }

//        _tabPane.popupSelectedIndex(tabIndex);
        if (_tabEditor == null)
            _tabEditor = createDefaultTabEditor();

        if (_tabEditor != null) {
            prepareEditor(_tabEditor, tabIndex);

            ((Container) getTabPanel()).add(_tabEditor);
            resizeEditor(tabIndex);

            _editingTab = tabIndex;
            _isEditing = true;

            _tabEditor.requestFocusInWindow();
            _tabEditor.selectAll();
            return true;
        }
        return false;
    }

    @Override
    public int getEditingTabIndex() {
        return _editingTab;
    }

    protected void prepareEditor(TabEditor e, int tabIndex) {
        Font font = null;
        if (_tabPane.getSelectedTabFont() != null) {
            font = _tabPane.getSelectedTabFont();
        }
        else {
            font = _tabPane.getFont();
        }
        if (_tabPane.isBoldActiveTab() && font.getStyle() != Font.BOLD) {
            font = font.deriveFont(Font.BOLD);
        }
        e.setFont(font);

        _oldValue = _tabPane.getTitleAt(tabIndex);
        if (_oldValue.startsWith("<HTML>") && _oldValue.endsWith("/HTML>")) {
            _oldPrefix = "<HTML>";
            _oldPostfix = "</HTML>";
            String title = _oldValue.substring("<HTML>".length(), _oldValue.length() - "</HTML>".length());
            if (title.startsWith("<B>") && title.endsWith("/B>")) {
                title = title.substring("<B>".length(), title.length() - "</B>".length());
                _oldPrefix += "<B>";
                _oldPostfix = "</B>" + _oldPostfix;
            }
            e.setText(title);
        }
        else {
            _oldPrefix = "";
            _oldPostfix = "";
            e.setText(_oldValue);
        }
        e.selectAll();
        e.setForeground(_tabPane.getForegroundAt(tabIndex));
    }

    protected Rectangle getTabsTextBoundsAt(int tabIndex) {
        Rectangle tabRect = _tabPane.getBoundsAt(tabIndex);
        Rectangle iconRect = new Rectangle(),
                textRect = new Rectangle();

        if (tabRect.width < 200) // random max size;
            tabRect.width = 200;

        String title = getCurrentDisplayTitleAt(_tabPane, tabIndex);
        while (title == null || title.length() < 3)
            title += " ";

        Icon icon = _tabPane.getIconForTab(tabIndex);

        Font font = _tabPane.getFont();
        if (tabIndex == _tabPane.getSelectedIndex() && _tabPane.isBoldActiveTab()) {
            font = font.deriveFont(Font.BOLD);
        }
        SwingUtilities.layoutCompoundLabel(_tabPane, _tabPane.getGraphics().getFontMetrics(font), title, icon,
                SwingUtilities.CENTER, SwingUtilities.CENTER,
                SwingUtilities.CENTER, SwingUtilities.TRAILING, tabRect,
                iconRect, textRect, icon == null ? 0 : _textIconGap);

        if (_tabPane.getTabPlacement() == TOP || _tabPane.getTabPlacement() == BOTTOM) {
            iconRect.x = tabRect.x + _iconMargin;
            textRect.x = (icon != null ? iconRect.x + iconRect.width + _textIconGap : tabRect.x + _textPadding);
            textRect.width += 2;
        }
        else {
            iconRect.y = tabRect.y + _iconMargin;
            textRect.y = (icon != null ? iconRect.y + iconRect.height + _textIconGap : tabRect.y + _textPadding);
            iconRect.x = tabRect.x + 2;
            textRect.x = tabRect.x + 2;
            textRect.height += 2;
        }

        return textRect;
    }

    private void updateTab() {
        if (_isEditing) {
            resizeEditor(getEditingTabIndex());
        }
    }

    public void insertUpdate(DocumentEvent e) {
        updateTab();
    }

    public void removeUpdate(DocumentEvent e) {
        updateTab();
    }

    public void changedUpdate(DocumentEvent e) {
        updateTab();
    }

    protected void resizeEditor(int tabIndex) {
        // note - this should use the logic of label paint text so that the text overlays exactly.
        Rectangle tabsTextBoundsAt = getTabsTextBoundsAt(tabIndex);
        if (tabsTextBoundsAt.isEmpty()) {
            tabsTextBoundsAt = new Rectangle(14, 3); // note - 14 should be the font height but...
        }

        tabsTextBoundsAt.x = tabsTextBoundsAt.x - _tabEditor.getBorder().getBorderInsets(_tabEditor).left;
        tabsTextBoundsAt.width = +tabsTextBoundsAt.width +
                _tabEditor.getBorder().getBorderInsets(_tabEditor).left +
                _tabEditor.getBorder().getBorderInsets(_tabEditor).right;
        tabsTextBoundsAt.y = tabsTextBoundsAt.y - _tabEditor.getBorder().getBorderInsets(_tabEditor).top;
        tabsTextBoundsAt.height = tabsTextBoundsAt.height +
                _tabEditor.getBorder().getBorderInsets(_tabEditor).top +
                _tabEditor.getBorder().getBorderInsets(_tabEditor).bottom;
        _tabEditor.setBounds(SwingUtilities.convertRectangle(_tabPane, tabsTextBoundsAt, getTabPanel()));
        _tabEditor.invalidate();
        _tabEditor.validate();

//        getTabPanel().invalidate();
//        getTabPanel().validate();
//        getTabPanel().repaint();
//        getTabPanel().doLayout();

        _tabPane.doLayout();

        // mtf - note - this is an exteme repaint but we need to paint any content borders
        getTabPanel().getParent().getParent().repaint();
    }

    protected String getCurrentDisplayTitleAt(JideTabbedPane tp, int index) {
        String returnTitle = tp.getDisplayTitleAt(index);
        if ((_isEditing) && (index == _editingTab))
            returnTitle = _tabEditor.getText();

        return returnTitle;
    }

    protected class TabEditor extends JTextField implements UIResource {
        TabEditor() {
            setOpaque(false);
//            setBorder(BorderFactory.createEmptyBorder());
            setBorder(BorderFactory
                    .createCompoundBorder(new PartialLineBorder(Color.BLACK, 1, true),
                    BorderFactory.createEmptyBorder(0, 2, 0, 2)));
        }

        public boolean stopEditing() {
            return true;
        }

        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            Composite orgComposite = g2.getComposite();
            Color orgColor = g2.getColor();

            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.70f));
            Object o = JideSwingUtilities.setupShapeAntialiasing(g);

            g2.setColor(getBackground());
            g.fillRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 1, 1);

            JideSwingUtilities.restoreShapeAntialiasing(g, o);
            g2.setColor(orgColor);
            g2.setComposite(orgComposite);
            super.paintComponent(g);
        }
    }

    public void startEditing(MouseEvent e) {
        int tabIndex = tabForCoordinate(_tabPane, e.getX(), e.getY());

        if (!e.isPopupTrigger() && tabIndex >= 0
                && _tabPane.isEnabledAt(tabIndex)
                && _tabPane.isTabEditingAllowed() && (e.getClickCount() == 2)) {
            boolean shouldEdit = true;
            if (_tabPane.getTabEditingValidator() != null)
                shouldEdit = _tabPane.getTabEditingValidator().shouldStartEdit(tabIndex, e);

            if (shouldEdit) {
                e.consume();
                _tabPane.editTabAt(tabIndex);
            }
        }
        if (e.getClickCount() == 1) {
            if (_tabPane.isTabEditing()) {
                boolean shouldStopEdit = true;
                if (_tabPane.getTabEditingValidator() != null)
                    shouldStopEdit = _tabPane.getTabEditingValidator().alertIfInvalid(tabIndex, _oldPrefix + _tabEditor.getText() + _oldPostfix);

                if (shouldStopEdit)
                    _tabPane.stopTabEditing();
            }
        }
    }

    public ThemePainter getPainter() {
        return _painter;
    }

    private class DragOverTimer extends Timer implements ActionListener {
        private int _index;

        public DragOverTimer(int index) {
            super(500, null);
            _index = index;
            addActionListener(this);
            setRepeats(false);
        }

        public void actionPerformed(ActionEvent e) {
            if (_tabPane.getTabCount() == 0) {
                return;
            }
            if (_index == _tabPane.getSelectedIndex()) {
                if (_tabPane.isRequestFocusEnabled()) {
                    _tabPane.requestFocusInWindow();
                    _tabPane.repaint(getTabBounds(_tabPane, _index));
                }
            }
            else {
                if (_tabPane.isRequestFocusEnabled()) {
                    _tabPane.requestFocusInWindow();
                }
                _tabPane.setSelectedIndex(_index);
            }
            stop();
        }
    }

    private class DropListener implements DropTargetListener {
        private DragOverTimer _timer;

        int _index = -1;

        public DropListener() {
        }

        public void dragEnter(DropTargetDragEvent dtde) {
        }

        public void dragOver(DropTargetDragEvent dtde) {
            if (!_tabPane.isEnabled()) {
                return;
            }

            int tabIndex = getTabAtLocation(dtde.getLocation().x, dtde.getLocation().y);
            if (tabIndex >= 0 && _tabPane.isEnabledAt(tabIndex)) {
                if (tabIndex == _tabPane.getSelectedIndex()) {
                    // selected already, do nothing
                }
                else if (tabIndex == _index) {
                    // same tab, timer has started
                }
                else {
                    stopTimer();
                    startTimer(tabIndex);
                    _index = tabIndex; // save the index
                }
            }
            else {
                stopTimer();
            }
            dtde.rejectDrag();
        }

        private void startTimer(int tabIndex) {
            _timer = new DragOverTimer(tabIndex);
            _timer.start();
        }

        private void stopTimer() {
            if (_timer != null) {
                _timer.stop();
                _timer = null;
                _index = -1;
            }
        }

        public void dropActionChanged(DropTargetDragEvent dtde) {
        }

        public void dragExit(DropTargetEvent dte) {
            stopTimer();
        }

        public void drop(DropTargetDropEvent dtde) {
            stopTimer();
        }
    }

    protected void paintFocusIndicator(Graphics g, int tabPlacement,
                                       Rectangle[] rects, int tabIndex,
                                       Rectangle iconRect, Rectangle textRect,
                                       boolean isSelected) {
        Rectangle tabRect = rects[tabIndex];
        if (_tabPane.hasFocus() && isSelected) {
            int x, y, w, h;
            g.setColor(_focus);
            switch (tabPlacement) {
                case LEFT:
                    x = tabRect.x + 3;
                    y = tabRect.y + 3;
                    w = tabRect.width - 5;
                    h = tabRect.height - 6 - getTabGap();
                    break;
                case RIGHT:
                    x = tabRect.x + 2;
                    y = tabRect.y + 3;
                    w = tabRect.width - 5;
                    h = tabRect.height - 6 - getTabGap();
                    break;
                case BOTTOM:
                    x = tabRect.x + 3;
                    y = tabRect.y + 2;
                    w = tabRect.width - 6 - getTabGap();
                    h = tabRect.height - 5;
                    break;
                case TOP:
                default:
                    x = tabRect.x + 3;
                    y = tabRect.y + 3;
                    w = tabRect.width - 6 - getTabGap();
                    h = tabRect.height - 5;
            }
            BasicGraphicsUtils.drawDashedRect(g, x, y, w, h);
        }
    }

    protected boolean isRoundedCorner() {
        return "true".equals(SecurityUtils.getProperty("shadingtheme", "false"));
    }

    protected int getTabShape() {
        return _tabPane.getTabShape();
    }

    protected int getTabResizeMode() {
        return _tabPane.getTabResizeMode();
    }

    protected int getColorTheme() {
        return _tabPane.getColorTheme();
    }

    // for debug purpose
    final protected boolean PAINT_TAB = true;

    final protected boolean PAINT_TAB_BORDER = true;

    final protected boolean PAINT_TAB_BACKGROUND = true;

    final protected boolean PAINT_TABAREA = true;

    final protected boolean PAINT_CONTENT_BORDER = true;

    final protected boolean PAINT_CONTENT_BORDER_EDGE = true;

    protected int getLeftMargin() {
        if (getTabShape() == JideTabbedPane.SHAPE_OFFICE2003) {
            return OFFICE2003_LEFT_MARGIN;
        }
        else if (getTabShape() == JideTabbedPane.SHAPE_EXCEL) {
            return EXCEL_LEFT_MARGIN;
        }
        else {
            return DEFAULT_LEFT_MARGIN;
        }
    }

    protected int getTabGap() {
        if (getTabShape() == JideTabbedPane.SHAPE_OFFICE2003) {
            return 4;
        }
        else {
            return 0;
        }
    }

    protected int getLayoutSize() {
        int tabShape = getTabShape();
        if (tabShape == JideTabbedPane.SHAPE_EXCEL) {
            return EXCEL_LEFT_MARGIN;
        }
        else if (tabShape == JideTabbedPane.SHAPE_ECLIPSE3X) {
            return 15;
        }
        else if (_tabPane.getTabShape() == JideTabbedPane.SHAPE_FLAT || _tabPane.getTabShape() == JideTabbedPane.SHAPE_ROUNDED_FLAT) {
            return 2;
        }
        else if (tabShape == JideTabbedPane.SHAPE_WINDOWS
                || tabShape == JideTabbedPane.SHAPE_WINDOWS_SELECTED) {
            return 6;
        }
        else {
            return 0;
        }
    }

    protected int getTabRightPadding() {
        if (getTabShape() == JideTabbedPane.SHAPE_EXCEL) {
            return 4;
        }
        else {
            return 0;
        }
    }

    protected MouseListener createMouseListener() {
        if (getTabShape() == JideTabbedPane.SHAPE_WINDOWS || getTabShape() == JideTabbedPane.SHAPE_WINDOWS_SELECTED) {
            return new RolloverMouseHandler();
        }
        else {
            return new MouseHandler();
        }
    }

    protected MouseWheelListener createMouseWheelListener() {
        return new MouseWheelHandler();
    }

    protected MouseMotionListener createMouseMotionListener() {
        if (getTabShape() == JideTabbedPane.SHAPE_WINDOWS || getTabShape() == JideTabbedPane.SHAPE_WINDOWS_SELECTED) {
            return new RolloverMouseMotionHandler();
        }
        else {
            return new MouseMotionHandler();
        }
    }

    public class DefaultMouseMotionHandler extends MouseMotionAdapter {
        @Override
        public void mouseMoved(MouseEvent e) {
            super.mouseMoved(e);
            int tabIndex = getTabAtLocation(e.getX(), e.getY());
            if (tabIndex != _indexMouseOver) {
                _indexMouseOver = tabIndex;
                _tabPane.repaint();
            }

        }

    }

    public class DefaultMouseHandler extends BasicJideTabbedPaneUI.MouseHandler {
        @Override
        public void mousePressed(MouseEvent e) {
            super.mousePressed(e);
        }

        @Override
        public void mouseEntered(MouseEvent e) {
            super.mouseEntered(e);
            int tabIndex = getTabAtLocation(e.getX(), e.getY());
            _mouseEnter = true;
            _indexMouseOver = tabIndex;
            _tabPane.repaint();
        }

        @Override
        public void mouseExited(MouseEvent e) {
            super.mouseExited(e);
            _indexMouseOver = -1;
            _mouseEnter = false;
            _tabPane.repaint();
        }
    }

    public class RolloverMouseMotionHandler extends MouseMotionAdapter {
        @Override
        public void mouseMoved(MouseEvent e) {
            super.mouseMoved(e);
            int tabIndex = tabForCoordinate(_tabPane, e.getX(), e.getY());
            if (tabIndex != _indexMouseOver) {
                _indexMouseOver = tabIndex;
                _tabPane.repaint();
            }

        }

    }

    public class RolloverMouseHandler extends BasicJideTabbedPaneUI.MouseHandler {
        @Override
        public void mouseEntered(MouseEvent e) {
            super.mouseEntered(e);
            int tabIndex = tabForCoordinate(_tabPane, e.getX(), e.getY());
            _mouseEnter = true;
            _indexMouseOver = tabIndex;
            _tabPane.repaint();
        }

        @Override
        public void mouseExited(MouseEvent e) {
            super.mouseExited(e);
            _indexMouseOver = -1;
            _mouseEnter = false;
            _tabPane.repaint();
        }
    }

    protected boolean isTabLeadingComponentVisible() {
        return _tabPane.isTabShown() && _tabLeadingComponent != null && _tabLeadingComponent.isVisible();
    }

    protected boolean isTabTrailingComponentVisible() {
        return _tabPane.isTabShown() && _tabTrailingComponent != null && _tabTrailingComponent.isVisible();
    }

    protected boolean isTabTopVisible(int tabPlacement) {
        switch (tabPlacement) {
            case LEFT:
            case RIGHT:
                return (isTabLeadingComponentVisible() && _tabLeadingComponent.getPreferredSize().width > calculateMaxTabWidth(tabPlacement)) ||
                        (isTabTrailingComponentVisible() && _tabTrailingComponent.getPreferredSize().width > calculateMaxTabWidth(tabPlacement));
            case TOP:
            case BOTTOM:
            default:
                return (isTabLeadingComponentVisible() && _tabLeadingComponent.getPreferredSize().height > calculateMaxTabHeight(tabPlacement)) ||
                        (isTabTrailingComponentVisible() && _tabTrailingComponent.getPreferredSize().height > calculateMaxTabHeight(tabPlacement));
        }
    }

    protected boolean showFocusIndicator() {
        return _tabPane.hasFocusComponent() && _showFocusIndicator;
    }

    private int getNumberOfTabButtons() {
        int numberOfButtons = (!isShowTabButtons() || isShrinkTabs()) ? 1 : 4;
        if (!isShowCloseButton() || isShowCloseButtonOnTab()) {
            numberOfButtons--;
        }
        return numberOfButtons;
    }

    /**
     * Gets the resource string used in DocumentPane. Subclass can override it to provide their own strings.
     *
     * @param key the resource key
     * @return the localized string.
     */
    protected String getResourceString(String key) {
        return Resource.getResourceBundle(_tabPane != null ? _tabPane.getLocale() : Locale.getDefault()).getString(key);
    }
}
