/*
 * @(#)JidePopup.java 2/24/2005
 *
 * Copyright 2002 - 2005 JIDE Software Inc. All rights reserved.
 */

package com.jidesoft.popup;

import com.jidesoft.plaf.LookAndFeelFactory;
import com.jidesoft.plaf.PopupUI;
import com.jidesoft.plaf.UIDefaultsLookup;
import com.jidesoft.swing.*;
import com.jidesoft.utils.PortingUtils;
import com.jidesoft.utils.SecurityUtils;
import com.jidesoft.utils.SystemInfo;
import sun.awt.EmbeddedFrame;

import javax.accessibility.Accessible;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleRole;
import javax.accessibility.AccessibleValue;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import java.applet.Applet;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

/**
 * <code>JidePopup</code> is a popup window which can be resized, dragged and autohide if time out.
 * <p/>
 * JidePopup uses JWindow as the container in order to show itself. By default, JidePopup is not focusable which means
 * no component in the JidePopup will get focus. For example, if you put a JTextField in JidePopup and the JTextField
 * becomes not editable, this is a result of non-focusable JWindow. So if you want components in JidePopup to be able to
 * receive focus, you can either call setFocusable(true) or you can call {@link #setDefaultFocusComponent(java.awt.Component)}
 * to set a child component as the default focus component.
 */
public class JidePopup extends JComponent implements Accessible, WindowConstants {

    /**
     * @see #getUIClassID
     * @see #readObject
     */
    private static final String uiClassID = "JidePopupUI";

    /**
     * The <code>JRootPane</code> instance that manages the content pane and optional menu bar for this Popup, as well
     * as the glass pane.
     *
     * @see javax.swing.JRootPane
     * @see javax.swing.RootPaneContainer
     */
    private JRootPane rootPane;

    /**
     * If <code>true</code> then calls to <code>add</code> and <code>setLayout</code> cause an exception to be thrown.
     */
    private boolean rootPaneCheckingEnabled = false;

    /**
     * Bound property name.
     */
    public static final String CONTENT_PANE_PROPERTY = "contentPane";

    /**
     * Bound property name.
     */
    public static final String MENU_BAR_PROPERTY = "JMenuBar";

    /**
     * Bound property name.
     */
    public static final String LAYERED_PANE_PROPERTY = "layeredPane";

    /**
     * Bound property name.
     */
    public static final String ROOT_PANE_PROPERTY = "rootPane";

    /**
     * Bound property name.
     */
    public static final String GLASS_PANE_PROPERTY = "glassPane";

    /**
     * Bound property name.
     */
    public static final String VISIBLE_PROPERTY = "visible";

    public static final String TRANSIENT_PROPERTY = "transient";

    /**
     * Constrained property name indicated that this frame has
     * selected status.
     */

    /**
     * Constrained property name indicating that the popup is attachable.
     */
    public static final String ATTACHABLE_PROPERTY = "attachable";

    private boolean _attachable = true;

    /**
     * Bound property name for gripper.
     */
    public static final String MOVABLE_PROPERTY = "movable";

    /**
     * If the gripper should be shown. Gripper is something on divider to indicate it can be dragged.
     */
    private boolean _movable = false;

    /**
     * Bound property name for if the popup is detached.
     */
    public static final String DETACHED_PROPERTY = "detached";

    protected boolean _detached;

    protected ResizableWindow _window;
    protected ResizablePanel _panel;
    protected ResizableSupport _resizableSupport;

    private ComponentAdapter _componentListener;
    private WindowAdapter _windowListener;
    private ComponentAdapter _ownerComponentListener;
    private HierarchyListener _hierarchyListener;
    private Point _displayStartLocation;

    /**
     * If the popup shows a dialog and you don't want the popup to be hidden when the dialog is shown, you can use this
     * special client property to do it. Here is the code, assuming the dialog is shown from your popup.
     * <code><pre>
     * JComponent c = JideSwingUtilities.getFirstJComponent(dialog);
     *   if(c != null) {
     *       c.putClientProperty(JidePopup.CLIENT_PROPERTY_POPUP_ACTUAL_OWNER, component);
     *   }
     * </pre></code>
     */
    public static final String CLIENT_PROPERTY_POPUP_ACTUAL_OWNER = "JidePopup.actualOwner";

    /**
     * Bound property name for resizable.
     */
    public static final String RESIZABLE_PROPERTY = "resizable";

    private boolean _resizable = true;

    private boolean _keepPreviousSize = true;

//    /**
//     * Bound property name for movable.
//     */
//    public static final String MOVABLE_PROPERTY = "movable";
//
//    private boolean _movable;

    /**
     * Bound property name for owner.
     */
    public static final String OWNER_PROPERTY = "owner";

    private Component _owner;

    private Border _popupBorder;

    private boolean _transient = true;

    private int _timeout = 0;
    private Timer _timer;

    private Component _defaultFocusComponent;

    /**
     * Hides the popup when the owner is moved.
     */
    public static final int DO_NOTHING_ON_MOVED = -1;

    /**
     * Hides the popup when the owner is moved.
     */
    public static final int HIDE_ON_MOVED = 0;

    /**
     * Moves the popup along with owner when the owner is moved.
     */
    public static final int MOVE_ON_MOVED = 1;

    private int _defaultMoveOperation = HIDE_ON_MOVED;
    /**
     * The distance between alert and screen border.
     */
    public int DISTANCE_TO_SCREEN_BORDER = 10;

    private List<Component> _excludedComponents;

    private int _gripperLocation = SwingConstants.NORTH;

    public static final String PROPERTY_GRIPPER_LOCATION = "gripperLocation";
    private ComponentAdapter _popupResizeListener;

    protected Dimension _previousSize;
    protected Component _actualOwner;
    protected Point _actualOwnerLocation;

    /**
     * Key used to indicate a light weight popup should be used.
     */
    public static final int LIGHT_WEIGHT_POPUP = 0;

    /*
     * Key used to indicate a heavy weight Popup should be used.
     */
    public static final int HEAVY_WEIGHT_POPUP = 2;

    private int _popupType = HEAVY_WEIGHT_POPUP;
    private ActionListener _escapeActionListener;

    /**
     * Creates a Popup.
     */
    public JidePopup() {
        _excludedComponents = new ArrayList<Component>();
        setRootPane(createRootPane());
        setLayout(new BorderLayout());
        setRootPaneCheckingEnabled(true);
        setFocusable(false);
        updateUI();
    }

    /**
     * Called by the constructor to set up the <code>JRootPane</code>.
     *
     * @return a new <code>JRootPane</code>
     *
     * @see javax.swing.JRootPane
     */
    protected JRootPane createRootPane() {
        JRootPane pane = new JRootPane();
        // by default, the subclass BorderLayout cause memory leak if isPopupVolatile in AbstractComboBox.
        pane.getContentPane().setLayout(new BorderLayout());
        pane.setOpaque(false);  // on Nimbus L&F, JRootPane is opaque by default. So we have to set it to false explicitly.
        return pane;
    }

    /**
     * Returns the look-and-feel object that renders this component.
     *
     * @return the <code>PopupUI</code> object that renders this component
     */
    public PopupUI getUI() {
        return (PopupUI) ui;
    }

    /**
     * Sets the UI delegate for this <code>Popup</code>.
     *
     * @param ui the UI delegate
     */
    public void setUI(PopupUI ui) {
        boolean checkingEnabled = isRootPaneCheckingEnabled();
        try {
            setRootPaneCheckingEnabled(false);
            super.setUI(ui);
        }
        finally {
            setRootPaneCheckingEnabled(checkingEnabled);
        }
    }

    /**
     * Notification from the <code>UIManager</code> that the look and feel has changed. Replaces the current UI object
     * with the latest version from the <code>UIManager</code>.
     *
     * @see javax.swing.JComponent#updateUI
     */
    @Override
    public void updateUI() {
        if (UIDefaultsLookup.get(uiClassID) == null) {
            LookAndFeelFactory.installJideExtension();
        }
        setUI((PopupUI) UIManager.getUI(this));
        invalidate();
    }

    /**
     * Returns the name of the look-and-feel class that renders this component.
     *
     * @return the string "PopupUI"
     *
     * @see javax.swing.JComponent#getUIClassID
     * @see javax.swing.UIDefaults#getUI
     */
    @Override
    public String getUIClassID() {
        return uiClassID;
    }

    /**
     * Returns whether calls to <code>add</code> and <code>setLayout</code> cause an exception to be thrown.
     *
     * @return <code>true</code> if <code>add</code> and <code>setLayout</code> are checked
     *
     * @see #addImpl
     * @see #setLayout
     * @see #setRootPaneCheckingEnabled
     */
    protected boolean isRootPaneCheckingEnabled() {
        return rootPaneCheckingEnabled;
    }

    /**
     * Determines whether calls to <code>add</code> and <code>setLayout</code> cause an exception to be thrown.
     *
     * @param enabled a boolean value, <code>true</code> if checking is to be enabled, which cause the exceptions to be
     *                thrown
     * @see #addImpl
     * @see #setLayout
     * @see #isRootPaneCheckingEnabled
     */
    protected void setRootPaneCheckingEnabled(boolean enabled) {
        rootPaneCheckingEnabled = enabled;
    }

    /**
     * Ensures that, by default, children cannot be added directly to this component. Instead, children must be added to
     * its content pane. For example:
     * <pre>
     * thisComponent.getContentPane().add(child)
     * </pre>
     * An attempt to add to directly to this component will cause a runtime exception to be thrown. Subclasses can
     * disable this behavior.
     *
     * @param comp        the <code>Component</code> to be added
     * @param constraints the object containing the constraints, if any
     * @param index       the index
     * @throws Error if called with <code>isRootPaneChecking</code> <code>true</code>
     * @see #setRootPaneCheckingEnabled
     */
    @Override
    protected void addImpl(Component comp, Object constraints, int index) {
        if (isRootPaneCheckingEnabled()) {
            getContentPane().add(comp, constraints, index);
        }
        else {
            super.addImpl(comp, constraints, index);
        }
    }

    /**
     * Removes the specified component from this container.
     *
     * @param comp the component to be removed
     * @see #add
     */
    @Override
    public void remove(Component comp) {
        int oldCount = getComponentCount();
        super.remove(comp);
        if (oldCount == getComponentCount()) {
            // Client mistake, but we need to handle it to avoid a
            // common object leak in client applications.
            getContentPane().remove(comp);
        }
    }


    /**
     * Ensures that, by default, the layout of this component cannot be set. Instead, the layout of its content pane
     * should be set. For example:
     * <pre>
     * thisComponent.getContentPane().setLayout(new GridLayout(1,2))
     * </pre>
     * An attempt to set the layout of this component will cause an runtime exception to be thrown. Subclasses can
     * disable this behavior.
     *
     * @param manager the <code>LayoutManager</code>
     * @throws Error if called with <code>isRootPaneChecking</code> <code>true</code>
     * @see #setRootPaneCheckingEnabled
     */
    @Override
    public void setLayout(LayoutManager manager) {
        if (isRootPaneCheckingEnabled()) {
            getContentPane().setLayout(manager);
        }
        else {
            super.setLayout(manager);
        }
    }

//////////////////////////////////////////////////////////////////////////
/// Property Methods
//////////////////////////////////////////////////////////////////////////

    /**
     * Returns the current <code>JMenuBar</code> for this <code>Popup</code>, or <code>null</code> if no menu bar has
     * been set.
     *
     * @return the <code>JMenuBar</code> used by this Popup.
     *
     * @see #setJMenuBar
     */
    public JMenuBar getJMenuBar() {
        return getRootPane().getJMenuBar();
    }

    /**
     * Sets the <code>menuBar</code> property for this <code>Popup</code>.
     *
     * @param m the <code>JMenuBar</code> to use in this Popup.
     * @see #getJMenuBar
     */
    public void setJMenuBar(JMenuBar m) {
        JMenuBar oldValue = getJMenuBar();
        getRootPane().setJMenuBar(m);
        firePropertyChange(MENU_BAR_PROPERTY, oldValue, m);
    }

    // implements javax.swing.RootPaneContainer

    /**
     * Returns the content pane for this Popup.
     *
     * @return the content pane
     */
    public Container getContentPane() {
        return getRootPane().getContentPane();
    }


    /**
     * Sets this <code>Popup</code>'s <code>contentPane</code> property.
     *
     * @param c the content pane for this popup.
     * @throws java.awt.IllegalComponentStateException
     *          (a runtime exception) if the content pane parameter is <code>null</code>
     * @see javax.swing.RootPaneContainer#getContentPane
     */
    public void setContentPane(Container c) {
        Container oldValue = getContentPane();
        getRootPane().setContentPane(c);
        firePropertyChange(CONTENT_PANE_PROPERTY, oldValue, c);
    }

    /**
     * Returns the layered pane for this popup.
     *
     * @return a <code>JLayeredPane</code> object
     *
     * @see javax.swing.RootPaneContainer#setLayeredPane
     * @see javax.swing.RootPaneContainer#getLayeredPane
     */
    public JLayeredPane getLayeredPane() {
        return getRootPane().getLayeredPane();
    }

    /**
     * Sets this <code>Popup</code>'s <code>layeredPane</code> property.
     *
     * @param layered the <code>JLayeredPane</code> for this popup
     * @throws java.awt.IllegalComponentStateException
     *          (a runtime exception) if the layered pane parameter is <code>null</code>
     * @see javax.swing.RootPaneContainer#setLayeredPane
     */
    public void setLayeredPane(JLayeredPane layered) {
        JLayeredPane oldValue = getLayeredPane();
        getRootPane().setLayeredPane(layered);
        firePropertyChange(LAYERED_PANE_PROPERTY, oldValue, layered);
    }

    /**
     * Returns the glass pane for this popup.
     *
     * @return the glass pane
     *
     * @see javax.swing.RootPaneContainer#setGlassPane
     */
    public Component getGlassPane() {
        return getRootPane().getGlassPane();
    }

    /**
     * Sets this <code>Popup</code>'s <code>glassPane</code> property.
     *
     * @param glass the glass pane for this popup
     * @see javax.swing.RootPaneContainer#getGlassPane
     */
    public void setGlassPane(Component glass) {
        Component oldValue = getGlassPane();
        getRootPane().setGlassPane(glass);
        firePropertyChange(GLASS_PANE_PROPERTY, oldValue, glass);
    }

    /**
     * Returns the <code>rootPane</code> object for this popup.
     *
     * @return the <code>rootPane</code> property
     *
     * @see javax.swing.RootPaneContainer#getRootPane
     */
    @Override
    public JRootPane getRootPane() {
        return rootPane;
    }


    /**
     * Sets the <code>rootPane</code> property for this <code>Popup</code>. This method is called by the constructor.
     *
     * @param root the new <code>JRootPane</code> object
     */
    protected void setRootPane(JRootPane root) {
        if (rootPane != null) {
            rootPane.removeAll();
            remove(rootPane);
        }
        JRootPane oldValue = getRootPane();
        rootPane = root;
        if (rootPane != null) {
            boolean checkingEnabled = isRootPaneCheckingEnabled();
            try {
                setRootPaneCheckingEnabled(false);
                add(rootPane, BorderLayout.CENTER);
            }
            finally {
                setRootPaneCheckingEnabled(checkingEnabled);
            }
        }
        firePropertyChange(ROOT_PANE_PROPERTY, oldValue, root);
    }

    /**
     * Makes the component visible or invisible. Overrides <code>Component.setVisible</code>.
     *
     * @param visible true to make the component visible; false to make it invisible
     */
    @Override
    public void setVisible(boolean visible) {
        boolean old = isVisible();
        if (visible != old) {
            super.setVisible(visible);
            firePropertyChange(VISIBLE_PROPERTY, old, visible);
        }
    }

    /**
     * Gets the <code>AccessibleContext</code> associated with this <code>Popup</code>. For popups, the
     * <code>AccessibleContext</code> takes the form of an <code>AccessiblePopup</code> object. A new
     * <code>AccessiblePopup</code> instance is created if necessary.
     *
     * @return an <code>AccessiblePopup</code> that serves as the <code>AccessibleContext</code> of this
     *         <code>Popup</code>
     *
     * @see com.jidesoft.popup.JidePopup.AccessiblePopup
     */
    @Override
    public AccessibleContext getAccessibleContext() {
        if (accessibleContext == null) {
            accessibleContext = new AccessiblePopup();
        }
        return accessibleContext;
    }

    /**
     * Get the flag indicating if JidePopup should keep the size last time it was popped up.
     * <p/>
     * The default value of this flag is true. If you want the popup to resize based on the changing contents like in IntelliHints,
     * you need set this flag to false.
     *
     * @return the flag.
     */
    public boolean isKeepPreviousSize() {
        return _keepPreviousSize;
    }

    /**
     * Set the flag indicating if JidePopup should keep the size last time it was popped up.
     *
     * @param keepPreviousSize the flag.
     */
    public void setKeepPreviousSize(boolean keepPreviousSize) {
        _keepPreviousSize = keepPreviousSize;
    }

    /**
     * Get the insets so that when the JidePopup is dragged back to this area, the JidePopup will jump to its original
     * position automatically.
     * <p/>
     * By default, the value is {10, 10, 10, 10}. You can disable the jump functionality by setting the insets to {0, 0, 0, 0}.
     *
     * @return the insets.
     */
    public Insets getBackToOriginalInsets() {
        return _backToOriginalInsets;
    }

    /**
     * Set the insets so that when the JidePopup is dragged back to this area, the JidePopup will jump to its original
     * position automatically.
     *
     * @param backToOriginalInsets the insets
     */
    public void setBackToOriginalInsets(Insets backToOriginalInsets) {
        _backToOriginalInsets = backToOriginalInsets;
    }

    /**
     * This class implements accessibility support for the <code>Popup</code> class.  It provides an implementation of
     * the Java Accessibility API appropriate to popup user-interface elements.
     */
    protected class AccessiblePopup extends AccessibleJComponent
            implements AccessibleValue {
        private static final long serialVersionUID = -1095213042773793649L;

        /**
         * Get the accessible name of this object.
         *
         * @return the localized name of the object -- can be <code>null</code> if this object does not have a name
         *
         * @see #setAccessibleName
         */
        @Override
        public String getAccessibleName() {
            if (accessibleName != null) {
                return accessibleName;
            }
            else {
                return getName();
            }
        }

        /**
         * Get the role of this object.
         *
         * @return an instance of AccessibleRole describing the role of the object
         *
         * @see javax.accessibility.AccessibleRole
         */
        @Override
        public AccessibleRole getAccessibleRole() {
            return AccessibleRole.SWING_COMPONENT; // use a generic one since there is no specific one to choose
        }

        /**
         * Gets the AccessibleValue associated with this object.  In the implementation of the Java Accessibility API
         * for this class, returns this object, which is responsible for implementing the <code>AccessibleValue</code>
         * interface on behalf of itself.
         *
         * @return this object
         */
        @Override
        public AccessibleValue getAccessibleValue() {
            return this;
        }

        //
        // AccessibleValue methods
        //

        /**
         * Get the value of this object as a Number.
         *
         * @return value of the object -- can be <code>null</code> if this object does not have a value
         */
        public Number getCurrentAccessibleValue() {
            if (isVisible()) {
                return 1;
            }
            else {
                return 0;
            }
        }

        /**
         * Set the value of this object as a Number.
         *
         * @return <code>true</code> if the value was set
         */
        public boolean setCurrentAccessibleValue(Number n) {
            if (n instanceof Integer) {
                if (n.intValue() == 0)
                    setVisible(true);
                else
                    setVisible(false);
                return true;
            }
            else {
                return false;
            }
        }

        /**
         * Get the minimum value of this object as a Number.
         *
         * @return Minimum value of the object; <code>null</code> if this object does not have a minimum value
         */
        public Number getMinimumAccessibleValue() {
            return Integer.MIN_VALUE;
        }

        /**
         * Get the maximum value of this object as a Number.
         *
         * @return Maximum value of the object; <code>null</code> if this object does not have a maximum value
         */
        public Number getMaximumAccessibleValue() {
            return Integer.MAX_VALUE;
        }
    }

    /**
     * Shows the popup. By default, it will show right below the owner.
     */
    public void showPopup() {
//David: To account for a popup within a popup, let the caller specify an owner
//  different from the RootPaneContainer(Applet) or ContentContainer.
//          showPopup(new Insets(0, 0, 0, 0));
        showPopup(new Insets(0, 0, 0, 0), null);
    }

    /**
     * Shows the popup. By default, it will show right below the owner after considering the insets.
     *
     * @param owner the popup window's owner; if unspecified, it will default to the RootPaneContainer(Applet) or
     *              ContentContainer
     */
    public void showPopup(Component owner) {
        showPopup(new Insets(0, 0, 0, 0), owner);
    }

    /**
     * Shows the popup. By default, it will show right below the owner after considering the insets.
     *
     * @param insets the popup's insets RootPaneContainer(Applet) or ContentContainer
     */
    public void showPopup(Insets insets) {
        showPopup(insets, null);
    }

    protected Insets _insets = null;

    /**
     * Shows the popup. By default, it will show right below the owner after considering the insets. Please note, if the
     * owner is not displayed (isShowing returns false), the popup will not be displayed either.
     *
     * @param insets the popup's insets
     * @param owner  the popup window's owner; if unspecified, it will default to the RootPaneContainer(Applet) or
     *               ContentContainer
     */
    public void showPopup(Insets insets, Component owner) {
        _insets = insets;
        Component actualOwner = (owner != null) ? owner : getOwner();
        if (actualOwner != null && actualOwner.isShowing()) {
            Point point = actualOwner.getLocationOnScreen();
            internalShowPopup(point.x, point.y, actualOwner);
        }
        else {
            showPopup(SwingConstants.CENTER);
        }
    }

    /**
     * Calculates the popup location.
     *
     * @param point owner is top-left coordinate relative to screen.
     * @param size  the size of the popup window.
     * @param owner the owner
     * @return new popup location. By default, it will return the coordinate of the bottom-left corner of owner.
     */
    protected Point getPopupLocation(Point point, Dimension size, Component owner) {
        Component actualOwner = (owner != null) ? owner : getOwner();
        Dimension ownerSize = actualOwner != null ? actualOwner.getSize() : new Dimension(0, 0);
        Dimension screenSize = PortingUtils.getScreenSize(owner);

        if (size.width == 0) {
            size = this.getPreferredSize();
        }

        Point p = new Point(point.x + _insets.left, point.y + ownerSize.height - _insets.bottom);
        int left = p.x + size.width;
        int bottom = p.y + size.height;

        if (left > screenSize.width) {
            p.x -= left - screenSize.width; // move left so that the whole popup can fit in
        }

        if (bottom > screenSize.height) {
            p.y = point.y + _insets.top - size.height; // flip to upward
            if (isResizable()) {
                setupResizeCorner(Resizable.UPPER_RIGHT);
            }
        }
        else {
            if (isResizable()) {
                setupResizeCorner(Resizable.LOWER_RIGHT);
            }
        }
        return p;
    }

    /**
     * Setup Resizable's ResizeCorner.
     *
     * @param corner the corner.
     */
    public void setupResizeCorner(int corner) {
        switch (corner) {
            case Resizable.UPPER_RIGHT:
                if (_resizableSupport != null) {
                    _resizableSupport.getResizable().setResizableCorners(Resizable.UPPER_RIGHT);
                    JideSwingUtilities.setRecursively(this, new JideSwingUtilities.Handler() {
                        public boolean condition(Component c) {
                            return c instanceof JideScrollPane;
                        }

                        public void action(Component c) {
                            Resizable.ResizeCorner corner = new Resizable.ResizeCorner(Resizable.UPPER_RIGHT);
                            corner.addMouseListener(_resizableSupport.getResizable().getMouseInputAdapter());
                            corner.addMouseMotionListener(_resizableSupport.getResizable().getMouseInputAdapter());
                            ((JideScrollPane) c).setScrollBarCorner(JideScrollPane.VERTICAL_TOP, corner);
                            ((JideScrollPane) c).setScrollBarCorner(JideScrollPane.VERTICAL_BOTTOM, null);
                        }

                        public void postAction(Component c) {

                        }
                    });
                }
                break;
            case Resizable.LOWER_RIGHT:
                if (_resizableSupport != null) {
                    _resizableSupport.getResizable().setResizableCorners(Resizable.LOWER_RIGHT);
                    JideSwingUtilities.setRecursively(this, new JideSwingUtilities.Handler() {
                        public boolean condition(Component c) {
                            return c instanceof JideScrollPane;
                        }

                        public void action(Component c) {
                            Resizable.ResizeCorner corner = new Resizable.ResizeCorner(Resizable.LOWER_RIGHT);
                            corner.addMouseListener(_resizableSupport.getResizable().getMouseInputAdapter());
                            corner.addMouseMotionListener(_resizableSupport.getResizable().getMouseInputAdapter());
                            ((JideScrollPane) c).setScrollBarCorner(JideScrollPane.VERTICAL_BOTTOM, corner);
                            ((JideScrollPane) c).setScrollBarCorner(JideScrollPane.VERTICAL_TOP, null);
                        }

                        public void postAction(Component c) {
                        }
                    });
                }
                break;
            default:
                if (_resizableSupport != null) {
                    _resizableSupport.getResizable().setResizableCorners(corner);
                }
                break;
        }
    }

    public static Component getTopLevelAncestor(Component component) {
        if (component == null) {
            return null;
        }

        for (Component p = component; p != null; p = p.getParent()) {
            if (p instanceof Window || p instanceof Applet) {
                return p;
            }
        }
        return null;
    }

    /**
     * Shows the popup at the specified location relative to the screen. The valid locations are: <ul> <li>{@link
     * SwingConstants#CENTER} <li>{@link SwingConstants#SOUTH} <li>{@link SwingConstants#NORTH} <li>{@link
     * SwingConstants#WEST} <li>{@link SwingConstants#EAST} <li>{@link SwingConstants#NORTH_EAST} <li>{@link
     * SwingConstants#NORTH_WEST} <li>{@link SwingConstants#SOUTH_EAST} <li>{@link SwingConstants#SOUTH_WEST} </ul> The
     * actual location will be based on the main screen bounds. Say if the location is SwingConstants.SOUTH_EAST, the
     * popup will appear at the south west corner of main screen with 10 pixels to the border. The 10 pixel is the
     * default value. You can change it by setting {@link #DISTANCE_TO_SCREEN_BORDER}.
     *
     * @param location the new location.
     */
    public void showPopup(int location) {
        showPopup(location, null);
    }

    /**
     * Shows the popup at the specified location relative to the owner. The valid locations are: <ul> <li>{@link
     * SwingConstants#CENTER} <li>{@link SwingConstants#SOUTH} <li>{@link SwingConstants#NORTH} <li>{@link
     * SwingConstants#WEST} <li>{@link SwingConstants#EAST} <li>{@link SwingConstants#NORTH_EAST} <li>{@link
     * SwingConstants#NORTH_WEST} <li>{@link SwingConstants#SOUTH_EAST} <li>{@link SwingConstants#SOUTH_WEST} </ul> The
     * actual location will be based on the owner's bounds. Say if the location is SwingConstants.SOUTH_EAST, the popup
     * will appear at the south west corner of owner with 10 pixels to the border. The 10 pixel is the default value.
     * You can change it by setting {@link #DISTANCE_TO_SCREEN_BORDER}.
     *
     * @param location the new location
     * @param owner    the popup window's owner; if unspecified, it will default to the RootPaneContainer(Applet) or
     *                 ContentContainer
     */
    public void showPopup(int location, Component owner) {
        setDetached(true);
        Rectangle screenDim = getDisplayScreenBounds(owner);
        // Get the bounds of the splash window
        Dimension actualSize = getSize();
        Dimension size = actualSize.width == 0 ? getPreferredSize() : actualSize;
        Point displayLocation = getDisplayStartLocation(screenDim, size, location);
        internalShowPopup(displayLocation.x, displayLocation.y, owner);
    }

    /**
     * Set the display start location of the popup.
     *
     * @see #getDisplayStartLocation(java.awt.Rectangle, java.awt.Dimension, int)
     * @param startLocation the display start location.
     */
    public void setDisplayStartLocation(Point startLocation) {
        _displayStartLocation = startLocation;
    }

    /**
     * Get the display start location of the popup. It will automatically calculate a point if the customer
     * didn't invoke {@link #setDisplayStartLocation(java.awt.Point)} explicitly. It will just return the location
     * if the customer already set it.
     *
     * @param screenDim the dimension of the screen
     * @param size      the size of the popup
     * @param location  the direction to show the popup
     * @return the display start location.
     */
    protected Point getDisplayStartLocation(Rectangle screenDim, Dimension size, int location) {
        if (_displayStartLocation != null) {
            return _displayStartLocation;
        }
        switch (location) {
            case SwingConstants.CENTER:
                return new Point(screenDim.x + (screenDim.width - size.width) / 2,
                        screenDim.y + (screenDim.height - size.height) / 2);
            case SwingConstants.SOUTH:
                return new Point(screenDim.x + (screenDim.width - size.width) / 2,
                        screenDim.y + screenDim.height - size.height - DISTANCE_TO_SCREEN_BORDER);
            case SwingConstants.NORTH:
                return new Point(screenDim.x + (screenDim.width - size.width) / 2,
                        screenDim.y + DISTANCE_TO_SCREEN_BORDER);
            case SwingConstants.EAST:
                return new Point(screenDim.x + screenDim.width - size.width - DISTANCE_TO_SCREEN_BORDER,
                        screenDim.y + (screenDim.height - size.height) / 2);
            case SwingConstants.WEST:
                return new Point(screenDim.x + DISTANCE_TO_SCREEN_BORDER,
                        screenDim.y + (screenDim.height - size.height) / 2);
            case SwingConstants.SOUTH_WEST:
                return new Point(screenDim.x + DISTANCE_TO_SCREEN_BORDER,
                        screenDim.y + screenDim.height - size.height - DISTANCE_TO_SCREEN_BORDER);
            case SwingConstants.NORTH_EAST:
                return new Point(screenDim.x + screenDim.width - size.width - DISTANCE_TO_SCREEN_BORDER,
                        screenDim.y + DISTANCE_TO_SCREEN_BORDER);
            case SwingConstants.NORTH_WEST:
                return new Point(screenDim.x + DISTANCE_TO_SCREEN_BORDER,
                        screenDim.y + DISTANCE_TO_SCREEN_BORDER);
            case SwingConstants.SOUTH_EAST:
            default:
                return new Point(screenDim.x + screenDim.width - size.width - DISTANCE_TO_SCREEN_BORDER,
                        screenDim.y + screenDim.height - size.height - DISTANCE_TO_SCREEN_BORDER);
        }
    }

    protected Rectangle getDisplayScreenBounds(Component owner) {
        Rectangle screenDim;
        if (owner != null && owner.isShowing()) {
            screenDim = owner.getBounds();
            Point p = owner.getLocationOnScreen();
            screenDim.x = p.x;
            screenDim.y = p.y;
        }
        else {
            screenDim = PortingUtils.getLocalScreenBounds();
        }
        return screenDim;
    }

    public void packPopup() {
        if (_popupType == LIGHT_WEIGHT_POPUP) {
            if (_panel == null) {
                return;
            }
            _panel.setSize(_panel.getPreferredSize());
        }
        else if (_popupType == HEAVY_WEIGHT_POPUP) {
            if (_window == null) {
                return;
            }
            _window.pack();
        }
    }

//David: To account for a popup within a popup, let the caller specify an owner
//  different from the RootPaneContainer(Applet) or ContentContainer.

    protected void internalShowPopup(int x, int y) {
        internalShowPopup(x, y, null);
    }

    protected void internalShowPopup(int x, int y, Component owner) {
        _actualOwner = owner != null ? owner : getOwner();
        if (_actualOwner != null) {
            try {
                _actualOwnerLocation = _actualOwner.getLocationOnScreen();
            }
            catch (IllegalComponentStateException e) {
                return;
            }
        }
        createWindow(_actualOwner, x, y);
        showPopupImmediately();
    }

    protected void createWindow(Component owner, int x, int y) {
        if (_popupType == LIGHT_WEIGHT_POPUP) {
            if (_panel == null) {
                _panel = createLightweightPopupContainer(owner);
                _resizableSupport = _panel;
                installListeners();
                installBorder();
            }

            if (_previousSize != null && isKeepPreviousSize()) {
                setPreferredSize(_previousSize);
            }
            _previousSize = null;
            packPopup();

            if (_insets != null) {
                Point p = getPopupLocation(new Point(x, y), _panel.getSize(), owner);
                x = p.x;
                y = p.y;
            }

            JRootPane rootPane = JideSwingUtilities.getOutermostRootPane(owner);
            JLayeredPane layeredPane;
            if (rootPane != null)
                layeredPane = rootPane.getLayeredPane();
            else {
                return; // has to have layer pane
            }

            Point p = new Point(x, y);
            SwingUtilities.convertPointFromScreen(p, layeredPane);
            layeredPane.add(_panel, JLayeredPane.PALETTE_LAYER);
            if (SystemInfo.isJdk15Above()) {
                layeredPane.setComponentZOrder(_panel, 0);
            }

            _panel.setLocation(p.x, p.y);
        }
        else if (_popupType == HEAVY_WEIGHT_POPUP) {
            if (_window == null) {
                _window = createHeavyweightPopupContainer(owner);
                _resizableSupport = _window;
                installListeners();
                installBorder();
            }

            if (_previousSize != null && isKeepPreviousSize()) {
                setPreferredSize(_previousSize);
            }
            _previousSize = null;
            packPopup();

            if (_insets != null) {
                Point p = getPopupLocation(new Point(x, y), _window.getSize(), owner);
                x = p.x;
                y = p.y;
            }

            _window.setLocation(x, y);
        }
    }

    /**
     * Shows the popup at the specified x and y coordinates.
     *
     * @param x the x position. It is screen position.
     * @param y the y position. It is screen position.
     */
    public void showPopup(int x, int y) {
        showPopup(x, y, null);
    }

    /**
     * Shows the popup at the specified x and y coordinates.
     *
     * @param x     the x position. It is screen position.
     * @param y     the y position. It is screen position.
     * @param owner the popup window's owner; if unspecified, it will default to the RootPaneContainer(Applet) or
     *              ContentContainer
     */
    public void showPopup(int x, int y, Component owner) {
        internalShowPopup(x, y, owner);
    }

    protected static Frame getFrame(Component c) {
        Component w = c;

        while (!(w instanceof Frame) && (w != null)) {
            w = w.getParent();
        }
        return (Frame) w;
    }

    /**
     * @param owner the owner for this popup container. It will be used to find the top level ancestor and use it as the
     *              parent for this popup window.
     * @return a ResizableWindow.
     */
    protected ResizableWindow createHeavyweightPopupContainer(Component owner) {
        ResizableWindow container;
        Component topLevelAncestor = getTopLevelAncestor(owner);
        if (topLevelAncestor instanceof Frame) {
            container = new ResizableWindow((Frame) topLevelAncestor);
        }
        else if (topLevelAncestor instanceof Window) {
            container = new ResizableWindow((Window) topLevelAncestor);
        }
        else {
            Frame frame = getFrame(owner);
            container = new ResizableWindow(frame);
        }
        container.setName("JidePopup");
        container.getContentPane().add(this);
        return container;
    }

    /**
     * Creates lightweight container for the popup.
     *
     * @param owner the owner for this popup container. This parameter is not used in this method. It was there mainly
     *              because the corresponding {@link #createHeavyweightPopupContainer(java.awt.Component)} has this
     *              parameter.
     * @return a ResizablePanel
     */
    @SuppressWarnings({"UnusedDeclaration"})
    protected ResizablePanel createLightweightPopupContainer(Component owner) {
        ResizablePanel panel = new ResizablePanel() {
            @Override
            protected Resizable createResizable() {
                return new Resizable(this) {
                    @Override
                    public void resizing(int resizeCorner, int newX, int newY, int newW, int newH) {
                        setBounds(newX, newY, newW, newH);
                    }
                };
            }
        };
        panel.setVisible(false);
        panel.setOpaque(false);
        panel.setLayout(new BorderLayout());
        panel.add(this);
        return panel;
    }

    protected void installListeners() {
//David: Adding the MouseEventHandler synchronously causes a strange
//  initialization sequence if the popup owner is a tree/table editor:
//
//  - The editor gets moved to its initial location based on the cell location,
//    generating a COMPONENT_MOVED ComponentEvent.
//  - You add the MouseEventHandler, which includes a ComponentEvent listener on
//    the owner's hierarchy, including the editor.
//  - ComponentEvent is asynchronous. The ComponentEvent generated from moving
//    the editor to its initial position was placed on the event queue. So the
//    ComponentEvent gets handled by the MouseEventHandler's ComponentEvent
//    listener, even though it happened before the listener was added.
//
//  I fixed it by calling addMouseEventHandler asynchronously, guaranteeing that
//  any previously-generated event it may listen for has been removed from the
//  event queue before the listener is added.
//
//  This causes a couple of minor problems:
//
//  - It is possible that hidePopupImmediately can be called synchronously before
//    the asynchronous call to addMouseEventHandler is executed. This can cause
//    NPEs because the listener handler methods dereference _window, which is set
//    to null in hidePopupImmediately. So I added null checks on _window in the
//    listener handler methods.
//
//  - The removeMouseEventHandler method is called from hidePopupImmediately.
//    That means it could be called before addMouseEventHandler is executed
//    asynchronously; that would result in the listener not getting removed. So
//    I changed the removeMouseEventHandler to an asynchronous call, as well.
//
//  This issue appeared in the 1.8.3 release because you changed the
//  COMPONENT_MOVED handler to hide the popup instead of moving it. Since you
//  made this an option in the 1.8.4 release, all of this asynchronous code is
//  needed.
//
//        addMouseEventHandler()
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                addMouseEventHandler();
            }
        });
        _componentListener = new ComponentAdapter() {
            @Override
            public void componentHidden(ComponentEvent e) {
                hidePopup();
            }
        };
        _escapeActionListener = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Component owner = getActualOwner();
                hidePopupImmediately(true);
                if (owner != null) {
                    owner.requestFocus();
                }
            }
        };
        registerKeyboardAction(_escapeActionListener, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        if (_popupType == HEAVY_WEIGHT_POPUP) {
            _window.addComponentListener(_componentListener);
            _windowListener = new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    hidePopup();
                }
            };
            _window.addWindowListener(_windowListener);
        }

        Component owner = getActualOwner();
        if (owner != null) {
            _ownerComponentListener = new ComponentAdapter() {
                @Override
                public void componentHidden(ComponentEvent e) {
                    ancestorHidden();
                }

                @Override
                public void componentMoved(ComponentEvent e) {
                    try {
                        if (_actualOwnerLocation == null || _actualOwner == null || !_actualOwner.getLocationOnScreen().equals(_actualOwnerLocation)) {
                            ancestorMoved();
                        }
                    }
                    catch (Exception ex) {
                        // ignore in case IllegalComponentStateException happens in getLocationOnScreen
                    }
                }
            };
            owner.addComponentListener(_ownerComponentListener);
            _hierarchyListener = new HierarchyListener() {
                public void hierarchyChanged(HierarchyEvent e) {
                    ancestorHidden();
                }
            };
            owner.addHierarchyListener(_hierarchyListener);
        }

        _popupResizeListener = new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                removeComponentListener(_popupResizeListener);
                contentResized();
                addComponentListener(_popupResizeListener);
            }
        };
        addComponentListener(_popupResizeListener);
    }

    protected void contentResized() {
        // pack is good enough to replace all code above
        packPopup();
    }

    protected void installBorder() {
        if (getPopupBorder() != null) {
            if (isResizable()) {
                _resizableSupport.getResizable().setResizableCorners(Resizable.ALL);
            }
            else {
                _resizableSupport.getResizable().setResizableCorners(Resizable.NONE);
            }
            _resizableSupport.setBorder(getPopupBorder());
        }
        else {
            if (isDetached()) {
                if (isResizable()) {
                    _resizableSupport.getResizable().setResizableCorners(Resizable.ALL);
                }
                else {
                    _resizableSupport.getResizable().setResizableCorners(Resizable.NONE);
                }
                _resizableSupport.setBorder(UIDefaultsLookup.getBorder("Resizable.resizeBorder"));
            }
            else {
                if (isResizable()) {
                    _resizableSupport.getResizable().setResizableCorners(Resizable.RIGHT | Resizable.LOWER | Resizable.LOWER_RIGHT);
                }
                else {
                    _resizableSupport.getResizable().setResizableCorners(Resizable.NONE);
                }
                _resizableSupport.setBorder(UIDefaultsLookup.getBorder("PopupMenu.border"));
            }
        }
    }

    protected void showPopupImmediately() {
        if (_popupType == LIGHT_WEIGHT_POPUP) {
            if (_panel == null) {
                return;
            }

            _panel.applyComponentOrientation(getComponentOrientation());

            firePopupMenuWillBecomeVisible();

            if (!_panel.isVisible()) {
                packPopup();
                _panel.setVisible(true);
            }

            firePropertyChange("visible", Boolean.FALSE, Boolean.TRUE);

            if (isFocusable() || getDefaultFocusComponent() != null) {
                // only allow window to have focus when there is a default focus component.
                if (getDefaultFocusComponent() != null) {
                    Runnable runnable = new Runnable() {
                        public void run() {
                            getDefaultFocusComponent().requestFocus();
                        }
                    };
                    SwingUtilities.invokeLater(runnable);
                }
            }
        }
        else if (_popupType == HEAVY_WEIGHT_POPUP) {
            if (_window == null) {
                return;
            }
            _window.applyComponentOrientation(getComponentOrientation());

            firePopupMenuWillBecomeVisible();

            // only when the focus cycle root is true, the component in JidePopup won't request focus automatically.
            if (!isFocusable() && getDefaultFocusComponent() == null) {
                _window.setFocusableWindowState(false);
            }
            else {
                setFocusCycleRoot(true);
            }

            if (!_window.isVisible()) {
                _window.pack();
                _window.setVisible(true);
            }

            firePropertyChange("visible", Boolean.FALSE, Boolean.TRUE);

            if (isFocusable() || getDefaultFocusComponent() != null) {
                // only allow window to have focus when there is a default focus component.
                _window.setFocusable(true);
                if (getDefaultFocusComponent() != null) {
                    Runnable runnable = new Runnable() {
                        public void run() {
                            getDefaultFocusComponent().requestFocus();
                        }
                    };
                    SwingUtilities.invokeLater(runnable);
                }
            }
        }

        if (getTimeout() != 0) {
            startTimeoutTimer();
        }
    }

    protected void movePopup() {
        if (isPopupVisible()) {
            if (!isDetached() && _actualOwner != null) {
                if (_insets != null) {
                    showPopup(_insets, _actualOwner);
                }
                else if (_actualOwnerLocation != null) {
                    Point newLocation = _actualOwner.getLocationOnScreen();
                    Point p = null;
                    if (_popupType == LIGHT_WEIGHT_POPUP) {
                        p = _panel.getLocationOnScreen();
                    }
                    else if (_popupType == HEAVY_WEIGHT_POPUP) {
                        p = _window.getLocationOnScreen();
                    }
                    if (p != null) {
                        p.x += newLocation.x - _actualOwnerLocation.x;
                        p.y += newLocation.y - _actualOwnerLocation.y;
                        showPopup(p.x, p.y, _actualOwner);
                    }
                }
            }
        }
    }

    private boolean _isDragging = false;

    /**
     * Mouse location related the frame it drags.
     */
    private double _relativeX,
            _relativeY;

    private Point _startPoint;
    private Window _currentWindow;
    private JPanel _currentPanel;

    protected void endDragging() {
        _isDragging = false;
        if (_popupType == LIGHT_WEIGHT_POPUP) {
            _currentPanel = null;
        }
        else if (_popupType == HEAVY_WEIGHT_POPUP) {
            if (_currentWindow instanceof JWindow && ((JWindow) _currentWindow).getGlassPane() != null) {
                ((JWindow) _currentWindow).getGlassPane().setVisible(false);
                ((JWindow) _currentWindow).getGlassPane().setCursor(Cursor.getDefaultCursor());
            }
            else if (_currentWindow instanceof JDialog && ((JDialog) _currentWindow).getGlassPane() != null) {
                ((JDialog) _currentWindow).getGlassPane().setVisible(false);
                ((JDialog) _currentWindow).getGlassPane().setCursor(Cursor.getDefaultCursor());
            }
            _currentWindow = null;
        }
        _relativeX = 0;
        _relativeY = 0;
    }

    @SuppressWarnings({"UnusedDeclaration"})
    protected void beginDragging(JComponent f, int mouseX, int mouseY, double relativeX, double relativeY) {
        _relativeX = relativeX;
        _relativeY = relativeY;

        Component owner = getActualOwner();
        if (_popupType == LIGHT_WEIGHT_POPUP) {
            _currentPanel = _panel;
            _isDragging = true;
            if (isDetached() && owner != null) {
                _startPoint = owner.getLocationOnScreen();
                _startPoint.y += owner.getHeight();
            }
            else {
                _startPoint = _currentPanel.getLocationOnScreen();
            }
        }
        else if (_popupType == HEAVY_WEIGHT_POPUP) {
            if (f.getTopLevelAncestor() instanceof JWindow)
                _currentWindow = (JWindow) f.getTopLevelAncestor();
            if (f.getTopLevelAncestor() instanceof JDialog)
                _currentWindow = (JDialog) f.getTopLevelAncestor();

            if (_currentWindow instanceof JWindow && ((JWindow) _currentWindow).getGlassPane() != null) {
                ((JWindow) _currentWindow).getGlassPane().setVisible(true);
                ((JWindow) _currentWindow).getGlassPane().setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
            }
            else if (_currentWindow instanceof JDialog && ((JDialog) _currentWindow).getGlassPane() != null) {
                ((JDialog) _currentWindow).getGlassPane().setVisible(true);
                ((JDialog) _currentWindow).getGlassPane().setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
            }

            _isDragging = true;
            if (isDetached() && owner != null) {
                _startPoint = owner.getLocationOnScreen();
                _startPoint.y += owner.getHeight();
            }
            else {
                _startPoint = _currentWindow.getLocationOnScreen();
            }
        }
    }

    protected boolean isDragging() {
        return _isDragging;
    }

    static void convertPointToScreen(Point p, Component c, boolean startInFloat) {
        int x, y;

        do {
            if (c instanceof JComponent) {
                x = c.getX();
                y = c.getY();
            }
            else if (c instanceof java.applet.Applet || (startInFloat ? c instanceof Window : c instanceof JFrame)) {
                try {
                    Point pp = c.getLocationOnScreen();
                    x = pp.x;
                    y = pp.y;
                }
                catch (IllegalComponentStateException icse) {
                    x = c.getX();
                    y = c.getY();
                }
            }
            else {
                x = c.getX();
                y = c.getY();
            }

            p.x += x;
            p.y += y;

            if ((startInFloat ? c instanceof Window : c instanceof JFrame) || c instanceof java.applet.Applet)
                break;
            c = c.getParent();
        }
        while (c != null);
    }

    @SuppressWarnings({"UnusedDeclaration"})
    protected void drag(JComponent f, int newX, int newY, int mouseModifiers) {
        if (_popupType == LIGHT_WEIGHT_POPUP) {
            int x = newX - (int) (_currentPanel.getWidth() * _relativeX);
            int y = newY - (int) (_currentPanel.getHeight() * _relativeY);
            Rectangle bounds = new Rectangle(x, y, _currentPanel.getWidth(), _currentPanel.getHeight());

            Rectangle screenBounds = PortingUtils.getScreenBounds(_currentPanel);
            if (bounds.y + bounds.height > screenBounds.y + screenBounds.height) {
                bounds.y = screenBounds.y + screenBounds.height - bounds.height;
            }
            if (bounds.y < screenBounds.y) {
                bounds.y = screenBounds.y;
            }

            if (isAttachable() && isWithinAroundArea(new Point(x, y), _startPoint)) {
                Point p = new Point(_startPoint);
                SwingUtilities.convertPointFromScreen(p, _currentPanel.getParent());
                _currentPanel.setLocation(p);
                setDetached(false);
            }
            else {
                Point p = new Point(x, y);
                SwingUtilities.convertPointFromScreen(p, _currentPanel.getParent());
                _currentPanel.setLocation(p);
                setDetached(true);
            }
        }
        else if (_popupType == HEAVY_WEIGHT_POPUP) {
            int x = newX - (int) (_currentWindow.getWidth() * _relativeX);
            int y = newY - (int) (_currentWindow.getHeight() * _relativeY);
            Rectangle bounds = new Rectangle(x, y, _currentWindow.getWidth(), _currentWindow.getHeight());

            Rectangle screenBounds = PortingUtils.getScreenBounds(_currentWindow);
            if (bounds.y + bounds.height > screenBounds.y + screenBounds.height) {
                bounds.y = screenBounds.y + screenBounds.height - bounds.height;
            }
            if (bounds.y < screenBounds.y) {
                bounds.y = screenBounds.y;
            }

            if (isAttachable() && isWithinAroundArea(new Point(x, y), _startPoint)) {
                _currentWindow.setLocation(_startPoint);
                setDetached(false);
            }
            else {
                _currentWindow.setLocation(x, y);
                setDetached(true);
            }
        }
    }

    private Insets _backToOriginalInsets = new Insets(10, 10, 10, 10);

    boolean isWithinAroundArea(Point p, Point newPoint) {
        if (getBackToOriginalInsets().left == 0 && getBackToOriginalInsets().top == 0 && getBackToOriginalInsets().right == 0 && getBackToOriginalInsets().bottom == 0) {
            return false;
        }
        Rectangle rect = new Rectangle(p.x - getBackToOriginalInsets().left, p.y - getBackToOriginalInsets().top, p.x + getBackToOriginalInsets().right, p.y + getBackToOriginalInsets().bottom);
        return rect.contains(newPoint);
    }

//    private AWTEventListener _awtEventListener = new AWTEventListener() {
//        public void eventDispatched(AWTEvent event) {
//            if (event instanceof MouseEvent) {
//                if (event.getID() == MouseEvent.MOUSE_PRESSED) {
//                    MouseEvent e = (MouseEvent) event;
//                    Object source = SwingUtilities.getDeepestComponentAt(e.getComponent(), e.getX(), e.getY());
//                    if (!isAncestorOf((Container) source, JidePopup.this.getTopLevelAncestor())) { // todo: add a flag to not hidePopup in some cases
//                        hidePopup();
//                    }
//                    else {
//                        Point point = SwingUtilities.convertPoint((Component) e.getSource(), e.getPoint(), JidePopup.this);
//
//                        Rectangle startingBounds = JidePopup.this.getTopLevelAncestor().getBounds();
//                        _relativeX = (double) point.x / startingBounds.width;
//                        _relativeY = (double) point.y / startingBounds.height;
//
//                        Point screenPoint = new Point(e.getX(), e.getY());
//                        JidePopup.convertPointToScreen(screenPoint, (Component) e.getSource(), true);
//
//                        // drag on gripper
//                        if (source == JidePopup.this.getUI().getGripper()) {
//                            beginDragging(JidePopup.this, screenPoint.x, screenPoint.y, _relativeX, _relativeY);
//                            e.consume();
//                        }
//                    }
//                }
//                else if (event.getID() == MouseEvent.MOUSE_DRAGGED) {
//                    if (isDragging()) {
//                        MouseEvent e = (MouseEvent) event;
//                        Point screenPoint = e.getPoint();
//                        convertPointToScreen(screenPoint, ((Component) e.getSource()), true);
//                        drag(null, screenPoint.x, screenPoint.y, e.getModifiersEx());
//                        e.consume();
//                    }
//                }
//                else if (event.getID() == MouseEvent.MOUSE_RELEASED) {
//                    if (isDragging()) {
//                        MouseEvent e = (MouseEvent) event;
//                        endDragging();
//                        e.consume();
//                    }
//                }
//                else if (event.getID() == MouseEvent.MOUSE_ENTERED) {
//                    if (_window.isAncestorOf(((Component) event.getSource())) && getTimeout() != 0) {
//                        stopTimeoutTimer();
//                    }
//                }
//                else if (event.getID() == MouseEvent.MOUSE_EXITED) {
//                    if (_window.isAncestorOf(((Component) event.getSource())) && getTimeout() != 0) {
//                        startTimeoutTimer();
//                    }
//                }
//            }
//            else if (event instanceof WindowEvent) {
//                WindowEvent e = (WindowEvent) event;
//                if (e.getSource() != JidePopup.this.getTopLevelAncestor() && isAncestorOf(getOwner(), e.getWindow())) {
//                    if (e.getID() == WindowEvent.WINDOW_CLOSING || e.getID() == WindowEvent.WINDOW_ICONIFIED) {
//                        hidePopup();
//                    }
//                }
//            }
//            else if (event instanceof ComponentEvent) {
//                ComponentEvent e = (ComponentEvent) event;
//                if (e.getID() == ComponentEvent.COMPONENT_HIDDEN && isAncestorOf(getOwner(), e.getSource())) {
//                    hidePopup();
//                }
//                else if (e.getID() == ComponentEvent.COMPONENT_MOVED && isAncestorOf(getOwner(), e.getSource())) {
//                    movePopup();
//                }
//            }
//        }
//    };

    private AWTEventListener _awtEventListener;

    protected void handleMousePressed(MouseEvent e) {
        Component c = e.getComponent();
        if (c == null) {
            return;
        }
        Component component = SwingUtilities.getDeepestComponentAt(c, e.getX(), e.getY());
        if (!isClickOnPopup(e)) {
            if (isExcludedComponent(component)) {
                return;
            }
            ancestorHidden();
        }
        else if (isPopupVisible()) {
            Point point = SwingUtilities.convertPoint(component, e.getPoint(), this);

            Rectangle startingBounds = null;

            if (_popupType == LIGHT_WEIGHT_POPUP) {
                startingBounds = _panel.getBounds();
                Container parent = _panel.getParent();
                if (SystemInfo.isJdk15Above()) {
                    if (isClickOnPopup(e) && parent.getComponentZOrder(_panel) != 0) {
                        parent.setComponentZOrder(_panel, 0);
                        parent.repaint();
                    }
                }
            }
            else if (_popupType == HEAVY_WEIGHT_POPUP) {
                final Window sourceWindow = SwingUtilities.getWindowAncestor(component);
                if (sourceWindow == _window) {
                    startingBounds = _window.getBounds();
                    if (KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusedWindow() != _window) {
                        _window.toFront();
                    }
                }
            }

            if (startingBounds != null) {
                _relativeX = (double) point.x / startingBounds.width;
                _relativeY = (double) point.y / startingBounds.height;

                Point screenPoint = new Point(e.getX(), e.getY());
                convertPointToScreen(screenPoint, component, true);

                // drag on gripper
                if (JideSwingUtilities.isAncestorOf(component, getUI().getGripper())) {
                    beginDragging(this, screenPoint.x, screenPoint.y, _relativeX, _relativeY);
                    e.consume();
                }
            }
        }
    }

    protected void handleMouseReleased(MouseEvent e) {
        if (isDragging()) {
            endDragging();
            e.consume();
        }
    }

    protected void handleMouseDragged(MouseEvent e) {
        if (isDragging()) {
            Point screenPoint = e.getPoint();
            if (e.getSource() instanceof Component) {
                convertPointToScreen(screenPoint, ((Component) e.getSource()), true);
                drag(null, screenPoint.x, screenPoint.y, e.getModifiersEx());
                e.consume();
            }
        }
    }

    protected void handleMouseEntered(MouseEvent e) {
        if (_popupType == LIGHT_WEIGHT_POPUP) {
            if ((_panel != null) && e.getSource() instanceof Component &&
                    _panel.isAncestorOf(((Component) e.getSource())) && getTimeout() != 0) {
                stopTimeoutTimer();
            }
        }
        else if (_popupType == HEAVY_WEIGHT_POPUP) {
            if ((_window != null) && e.getSource() instanceof Component &&
                    _window.isAncestorOf(((Component) e.getSource())) && getTimeout() != 0) {
                stopTimeoutTimer();
            }
        }
    }

    protected void handleMouseExited(MouseEvent e) {
        if (_popupType == LIGHT_WEIGHT_POPUP) {
            if ((_panel != null) && e.getSource() instanceof Component &&
                    _panel.isAncestorOf(((Component) e.getSource())) && getTimeout() != 0) {
                startTimeoutTimer();
            }
        }
        else if (_popupType == HEAVY_WEIGHT_POPUP) {
            if ((_window != null) && e.getSource() instanceof Component &&
                    _window.isAncestorOf(((Component) e.getSource())) && getTimeout() != 0) {
                startTimeoutTimer();
            }
        }
    }

    private static boolean checkedUnpostPopup;
    private static boolean unpostPopup;

    private static boolean doUnpostPopupOnDeactivation() {
        if (!checkedUnpostPopup) {
            unpostPopup = (Boolean) java.security.AccessController.doPrivileged(
                    new java.security.PrivilegedAction<Object>() {
                        public Object run() {
                            String pKey = "sun.swing.unpostPopupsOnWindowDeactivation";
                            String value = System.getProperty(pKey, "true");
                            return Boolean.valueOf(value);
                        }
                    }
            );
            checkedUnpostPopup = true;
        }
        return unpostPopup;
    }

    protected void handleWindowEvent(WindowEvent e) {
        Component owner = getActualOwner();
        if (e.getSource() != getTopLevelAncestor() && JideSwingUtilities.isAncestorOf(owner, e.getWindow())) { // check if it's embedded in browser
            if (e.getID() == WindowEvent.WINDOW_CLOSING || e.getID() == WindowEvent.WINDOW_ICONIFIED) {
                hidePopup(true);
            }

// The cases for window deactivated are too complex. Let's not consider it for now.
// One case the code below didn't consider is an dialog is shown while in another thread, alert is showing and the owner is the frame.
// At the end, frame received deactivated event and cause alert to hide immediately.
//
// 1/2/07: we have to put this code back because combobox's popup not hiding when the window is deactivated.
// But I also copied the code from MenuSelectionManager to check doUnpostPopupOnDeactivation. Hopefully that addresses the issue above.
            else if (isTransient() && e.getID() == WindowEvent.WINDOW_DEACTIVATED
                    && !(e.getWindow() instanceof EmbeddedFrame)) {
                // TODO: don't why DEACTIVATED event is fired when popup is showing only if the applet is in browser mode.
                // so the best solution is to find out why. For now just skip the case if the frame is a EmbeddedFrame.
                if (doUnpostPopupOnDeactivation()) {
                    Window oppositeWindow = e.getOppositeWindow();
                    if (oppositeWindow == getTopLevelAncestor()) {
                        return;
                    }
                    if (oppositeWindow instanceof RootPaneContainer) {
                        JComponent realParent = getRealParent((RootPaneContainer) oppositeWindow);
                        if (realParent != null && realParent.getTopLevelAncestor() == getTopLevelAncestor()) {
                            return;
                        }
                    }
                    hidePopup(true);
                }
            }
        }
    }

    protected JComponent getRealParent(RootPaneContainer rootPaneContainer) {
        JComponent c = JideSwingUtilities.getFirstJComponent(rootPaneContainer);
        if (c != null) {
            Object clientProperty = c.getClientProperty(CLIENT_PROPERTY_POPUP_ACTUAL_OWNER);
            if (clientProperty instanceof JComponent) {
                return (JComponent) clientProperty;
            }
        }
        return null;
    }

    /**
     * This method will process component event. By default, if popup's ancestor is hidden, we will hide the popup as
     * well if the popup is transient (isTransient returns true). If popup's ancestor is moved, we will either move or
     * hide the popup depending on {@link #getDefaultMoveOperation()} value.
     *
     * @param e the ComponentEvent.
     */
    protected void handleComponentEvent(ComponentEvent e) {
        Component owner = getActualOwner();
        if (e.getID() == ComponentEvent.COMPONENT_HIDDEN && JideSwingUtilities.isAncestorOf(owner, e.getSource())) {
            ancestorHidden();
        }
        else if (e.getID() == ComponentEvent.COMPONENT_MOVED && JideSwingUtilities.isAncestorOf(owner, e.getSource())) {
            // this line is for Linux because the JFrame moves when combobox is shown inside JidePopup
//            System.out.println("_actualOwnerLocation " + _actualOwnerLocation + " _actualOwner " + _actualOwner + " _actualOwner.getLocationOnScreen() " + (_actualOwner != null ? _actualOwner.getLocationOnScreen() : null));
            try {
                if (_actualOwnerLocation == null || _actualOwner == null || !_actualOwner.getLocationOnScreen().equals(_actualOwnerLocation)) {
                    ancestorMoved();
                }
            }
            catch (Exception ex) {
                // ignore in case IllegalComponentStateException happens in getLocationOnScreen
            }
        }
    }

    /**
     * This method will process component hidden event for the popup's ancestor. By default we will hide the popup
     * immediately. You can override this to customize the behavior.
     */
    protected void ancestorHidden() {
        if (isTransient()) {
            hidePopupImmediately(true);
        }
    }

    /**
     * This method will process component moved event for the popup's ancestor. By default we will move the popup if
     * getDefaultMoveOperation() is MOVE_ON_MOVED, or hide the popup if getDefaultMoveOperation() is HIDE_ON_MOVED. You
     * can override this to customize the behavior.
     */
    protected void ancestorMoved() {
        if (getDefaultMoveOperation() == MOVE_ON_MOVED) {
            movePopup();
        }
        else if (getDefaultMoveOperation() == HIDE_ON_MOVED) {
            if (isTransient()) {
                hidePopupImmediately(true);
            }
        }
    }

    public void hidePopup() {
        hidePopup(false);
    }

    public void hidePopup(boolean cancelled) {
        if (!isPopupVisible()) {
            return;
        }
        hidePopupImmediately(cancelled);
    }

    public boolean isPopupVisible() {
        if (_popupType == LIGHT_WEIGHT_POPUP) {
            return _panel != null && _panel.isVisible();
        }
        else if (_popupType == HEAVY_WEIGHT_POPUP) {
            return _window != null && _window.isShowing();
        }
        return false;
    }

    public Rectangle getPopupBounds() {
        if (_popupType == LIGHT_WEIGHT_POPUP) {
            return isPopupVisible() ? _panel.getBounds() : null;
        }
        else if (_popupType == HEAVY_WEIGHT_POPUP) {
            return isPopupVisible() ? _window.getBounds() : null;
        }
        return null;
    }

    public void hidePopupImmediately(boolean cancelled) {
        Component owner = getActualOwner();
        if (owner != null) {
            owner.removeHierarchyListener(_hierarchyListener);
            _hierarchyListener = null;
            owner.removeComponentListener(_ownerComponentListener);
            _ownerComponentListener = null;
        }
        if (_escapeActionListener != null) {
            unregisterKeyboardAction(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0));
            _escapeActionListener = null;
        }
        if (_window != null) {
            _window.removeWindowListener(_windowListener);
            _windowListener = null;
            _window.removeComponentListener(_componentListener);
            _componentListener = null;
            _window.getContentPane().remove(this);
            if (cancelled) {
                firePopupMenuCanceled(); // will cause hidePopupImmediately called again.
            }
            firePopupMenuWillBecomeInvisible();
        }
        if (_panel != null) {
            _panel.remove(this);
            if (cancelled) {
                firePopupMenuCanceled(); // will cause hidePopupImmediately called again.
            }
            firePopupMenuWillBecomeInvisible();
        }
        if (_popupResizeListener != null) {
            removeComponentListener(_popupResizeListener);
            _popupResizeListener = null;
        }

        if (_window != null) {
            _previousSize = _window.getSize();
            _window.setVisible(false);
            _window.removeAll();
            _window.dispose();
            _window = null;
            firePropertyChange("visible", Boolean.TRUE, Boolean.FALSE);
        }

        if (_panel != null) {
            _previousSize = _panel.getSize();
            _panel.setVisible(false);
            Container parent = _panel.getParent();
            if (parent != null) {
                parent.remove(_panel);
            }
            _panel = null;
            firePropertyChange("visible", Boolean.TRUE, Boolean.FALSE);
        }

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                removeMouseEventHandler();
            }
        });

        _resizableSupport = null;
        _owner = null;
        _actualOwner = null;
        _actualOwnerLocation = null;
    }

    /**
     * Hides the popup immediately (compare to {@link #hidePopup()} could use animation to hide the popup).
     */
    public void hidePopupImmediately() {
        hidePopupImmediately(false);
    }

    /**
     * Add an entry to global event queue.
     */
    private void addMouseEventHandler() {
        if (shouldAWTEventListenerBeUsed()) {
            return;
        }

        if (_awtEventListener == null) {
            _awtEventListener = new AWTEventListener() {
                public void eventDispatched(AWTEvent event) {
                    if ("sun.awt.UngrabEvent".equals(event.getClass().getName())) {
                        // this is really a hack so that it can detect event when closing the windows in Eclipse RCP env.
                        // Popup should be canceled in case of ungrab event
                        hidePopupImmediately(true);
                        return;
                    }

                    if (event instanceof MouseEvent) {
                        if (event.getID() == MouseEvent.MOUSE_PRESSED) {
                            handleMousePressed((MouseEvent) event);
                        }
                        else if (event.getID() == MouseEvent.MOUSE_DRAGGED) {
                            handleMouseDragged((MouseEvent) event);
                        }
                        else if (event.getID() == MouseEvent.MOUSE_RELEASED) {
                            handleMouseReleased((MouseEvent) event);
                        }
                        else if (event.getID() == MouseEvent.MOUSE_ENTERED) {
                            handleMouseEntered((MouseEvent) event);
                        }
                        else if (event.getID() == MouseEvent.MOUSE_EXITED) {
                            handleMouseExited((MouseEvent) event);
                        }
                    }
                    else if (event instanceof WindowEvent) {
                        handleWindowEvent((WindowEvent) event);
                    }
                    else if (event instanceof ComponentEvent) {
                        handleComponentEvent((ComponentEvent) event);
                    }
                }
            };
        }
        try {
            java.security.AccessController.doPrivileged(
                    new java.security.PrivilegedAction<Object>() {
                        public Object run() {
                            Toolkit.getDefaultToolkit().addAWTEventListener(_awtEventListener, AWTEvent.MOUSE_EVENT_MASK
                                    | AWTEvent.MOUSE_MOTION_EVENT_MASK | AWTEvent.WINDOW_EVENT_MASK | AWTEvent.COMPONENT_EVENT_MASK);
                            return null;
                        }
                    }
            );
        }
        catch (SecurityException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Returns whether the AWTEventEventListener should be used to handle event processing
     *
     * @return true to use the AWT event listener; false otherwise
     */
    protected boolean shouldAWTEventListenerBeUsed() {
        return SecurityUtils.isAWTEventListenerDisabled() || "true".equals(SecurityUtils.getProperty("jide.disableAWTEventListener", "false"));
    }

    /**
     * Add an entry to global event queue.
     */
    private void removeMouseEventHandler() {
        if (shouldAWTEventListenerBeUsed()) {
            return;
        }

        try {
            java.security.AccessController.doPrivileged(
                    new java.security.PrivilegedAction<Object>() {
                        public Object run() {
                            Toolkit.getDefaultToolkit().removeAWTEventListener(_awtEventListener);
                            _awtEventListener = null;
                            return null;
                        }
                    }
            );
        }
        catch (SecurityException e) {
            throw new RuntimeException(e);
        }
    }

    public Component getOwner() {
        return _owner;
    }

    public void setOwner(Component owner) {
        if (_owner != owner) {
            Component old = _owner;
            _owner = owner;
            firePropertyChange(OWNER_PROPERTY, old, _owner);
            removeExcludedComponent(old);
            addExcludedComponent(_owner);
        }
    }

    /**
     * Checks if the popup is movable. If yes, it will show the gripper so that user can grab it and move the popup. If
     * the popup is attached to its owner, moving it will detach from the owner.
     *
     * @return true if gripper is visible
     */
    public boolean isMovable() {
        return _movable;
    }

    /**
     * Sets the movable attribute.
     *
     * @param movable true or false.
     */
    public void setMovable(boolean movable) {
        boolean old = _movable;
        if (old != movable) {
            _movable = movable;
            firePropertyChange(MOVABLE_PROPERTY, old, _movable);
        }
    }

    /**
     * Checks if the popup is resizable. By default, resizable option is true.
     * <p/>
     * Depending on the detached/attached mode, the resizing behavior may be different. If a popup is detached to a
     * component, it only allows you to resize from bottom, bottom right and right It obviously doesn't make sense to
     * resize from top and top side is aligned with the attached component.
     * <p/>
     * (Notes: in the future we will allow resize from different corner if the popup is shown above owner due to not
     * enough space on the screen).
     *
     * @return if the popup is resizable.
     */
    public boolean isResizable() {
        return _resizable;
    }

    /**
     * Sets the resizable option.
     *
     * @param resizable true or false.
     */
    public void setResizable(boolean resizable) {
        if (_resizable != resizable) {
            boolean old = _resizable;
            _resizable = resizable;
            firePropertyChange(RESIZABLE_PROPERTY, old, _resizable);
        }
    }

    /**
     * Checks if the popup is attachable. By default, attachable option is true.
     *
     * @return if the popup is attachable.
     */
    public boolean isAttachable() {
        return _attachable;
    }

    /**
     * Sets the attachable option.
     *
     * @param attachable true or false.
     */
    public void setAttachable(boolean attachable) {
        if (_attachable != attachable) {
            boolean old = _attachable;
            _attachable = attachable;
            firePropertyChange(ATTACHABLE_PROPERTY, old, _attachable);
        }
    }

    /**
     * Checks if the popup is detached.
     * <p/>
     * A popup has detached and attached mode. When a popup is in attached, it will act like it's part of the owner
     * (which can be set using {@link #setOwner(java.awt.Component)}. When owner is moved, the popup will be moved. If
     * the owner is hidden, the popup will hidden. In the other word, it is attached with the owner. In detached mode,
     * popup becomes an independent floating window. It will stay at the same location regardless if owner is moved. It
     * could still be visible when owner is hidden.
     * <p/>
     *
     * @return true if it's ddetached Otherwise false.
     */
    public boolean isDetached() {
        return _detached;
    }

    /**
     * Changes the popup's detached mode.
     *
     * @param detached true or false.
     */
    public void setDetached(boolean detached) {
        if (_detached != detached) {
            boolean old = _detached;
            _detached = detached;
            firePropertyChange("detacted", old, _detached);
            if (_resizableSupport != null) { // todo: check property change
                if (_detached) {
                    if (getPopupBorder() == null) {
                        _resizableSupport.setBorder(UIDefaultsLookup.getBorder("Resizable.resizeBorder"));
                    }
                    else {
                        _resizableSupport.setBorder(getPopupBorder());
                    }
                    if (isResizable()) {
                        _resizableSupport.getResizable().setResizableCorners(Resizable.ALL);
                    }
                    else {
                        _resizableSupport.getResizable().setResizableCorners(Resizable.NONE);
                    }
                }
                else {
                    if (getPopupBorder() == null) {
                        _resizableSupport.setBorder(UIDefaultsLookup.getBorder("PopupMenu.border"));
                    }
                    else {
                        _resizableSupport.setBorder(getPopupBorder());
                    }
                    if (isResizable()) {
                        _resizableSupport.getResizable().setResizableCorners(Resizable.RIGHT | Resizable.LOWER | Resizable.LOWER_RIGHT);
                    }
                    else {
                        _resizableSupport.getResizable().setResizableCorners(Resizable.NONE);
                    }
                }
            }
        }
    }

    /**
     * Gets the popup border set by {@link #setPopupBorder(javax.swing.border.Border)}.
     *
     * @return the border for this popup.
     */
    public Border getPopupBorder() {
        return _popupBorder;
    }

    /**
     * Sets the border for this popup. Please note a non-empty border is needed if you want the popup to be resizable.
     *
     * @param popupBorder the border for the popup.
     */
    public void setPopupBorder(Border popupBorder) {
        _popupBorder = popupBorder;
    }

    /**
     * Checks if the popup is transient.
     *
     * @return true if transient.
     *
     * @see #setTransient(boolean)
     */
    public boolean isTransient() {
        return _transient;
    }

    /**
     * Sets the transient attribute. If a popup is transient, it will hide automatically when mouse is clicked outside
     * the popup. Otherwise, it will stay visible until timeout or hidePopup() is called.
     *
     * @param isTransient true or false.
     */
    public void setTransient(boolean isTransient) {
        boolean old = _transient;
        if (old != isTransient) {
            _transient = isTransient;
            firePropertyChange(TRANSIENT_PROPERTY, old, isTransient);
        }
    }

    /**
     * Gets the time out value, in milliseconds.
     *
     * @return the time out value, in milliseconds.
     */
    public int getTimeout() {
        return _timeout;
    }

    /**
     * Sets the time out value, in milliseconds. If you don't want the popup hide after the time out, set the value to
     * 0. By default it's 0 meaning it will never time out.
     * <p/>
     * Typically, you call setTimeOut before the popup is visible. But if you do call setTimeOut when popup is already
     * visible (which means the timer is running), we will restart the timer using the new time out value you just set,
     * even the new time out value is the same as the old one. In the other word, this setTimeOut call will always
     * restart the timer if the timer is running.
     *
     * @param timeout new time out value, in milliseconds. 0 if you don't want popup automatically hides.
     */
    public void setTimeout(int timeout) {
        _timeout = timeout;
        if (_timer != null && _timer.isRunning()) {
            startTimeoutTimer(); // this call will restart the timer.
        }
    }

    private void stopTimeoutTimer() {
        if (_timer != null) {
            _timer.stop();
            _timer = null;
//            System.out.println("stop");
        }
    }

    private void startTimeoutTimer() {
        stopTimeoutTimer();
        _timer = new Timer(getTimeout(), new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                hidePopup();
            }
        });
        _timer.setRepeats(false);
        _timer.start();
//        System.out.println("start");
    }

    /**
     * Gets the default focus component.
     *
     * @return the default focus component.
     */
    public Component getDefaultFocusComponent() {
        return _defaultFocusComponent;
    }

    /**
     * Sets the default focus component. Default focus component should be a child component on this popup. It will get
     * focus when popup is shown. By setting a non-null component as default focus component, the JWindow that contains
     * the JidePopup will be set focusable. Otherwise the JWindow will be non-focusable.
     *
     * @param defaultFocusComponent the default focus component.
     */
    public void setDefaultFocusComponent(Component defaultFocusComponent) {
        _defaultFocusComponent = defaultFocusComponent;
    }

    /**
     * Adds a <code>PopupMenu</code> listener which will listen to notification messages from the popup portion of the
     * combo box.
     * <p/>
     * For all standard look and feels shipped with Java 2, the popup list portion of combo box is implemented as a
     * <code>JPopupMenu</code>. A custom look and feel may not implement it this way and will therefore not receive the
     * notification.
     *
     * @param l the <code>PopupMenuListener</code> to add
     */
    public void addPopupMenuListener(PopupMenuListener l) {
        listenerList.add(PopupMenuListener.class, l);
    }

    /**
     * Removes a <code>PopupMenuListener</code>.
     *
     * @param l the <code>PopupMenuListener</code> to remove
     * @see #addPopupMenuListener
     * @since 1.4
     */
    public void removePopupMenuListener(PopupMenuListener l) {
        listenerList.remove(PopupMenuListener.class, l);
    }

    /**
     * Returns an array of all the <code>PopupMenuListener</code>s added to this JComboBox with addPopupMenuListener().
     *
     * @return all of the <code>PopupMenuListener</code>s added or an empty array if no listeners have been added
     */
    public PopupMenuListener[] getPopupMenuListeners() {
        return listenerList.getListeners(PopupMenuListener.class);
    }

    /**
     * Notifies <code>PopupMenuListener</code>s that the popup portion of the combo box will become visible.
     * <p/>
     * This method is public but should not be called by anything other than the UI delegate.
     *
     * @see #addPopupMenuListener
     */
    public void firePopupMenuWillBecomeVisible() {
        Object[] listeners = listenerList.getListenerList();
        PopupMenuEvent e = null;
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == PopupMenuListener.class) {
                if (e == null)
                    e = new PopupMenuEvent(this);
                ((PopupMenuListener) listeners[i + 1]).popupMenuWillBecomeVisible(e);
            }
        }
    }

    /**
     * Notifies <code>PopupMenuListener</code>s that the popup portion of the combo box has become invisible.
     * <p/>
     * This method is public but should not be called by anything other than the UI delegate.
     *
     * @see #addPopupMenuListener
     */
    public void firePopupMenuWillBecomeInvisible() {
        Object[] listeners = listenerList.getListenerList();
        PopupMenuEvent e = null;
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == PopupMenuListener.class) {
                if (e == null)
                    e = new PopupMenuEvent(this);
                ((PopupMenuListener) listeners[i + 1]).popupMenuWillBecomeInvisible(e);
            }
        }
    }

    /**
     * Notifies <code>PopupMenuListener</code>s that the popup portion of the combo box has been canceled.
     * <p/>
     * This method is public but should not be called by anything other than the UI delegate.
     *
     * @see #addPopupMenuListener
     */
    public void firePopupMenuCanceled() {
        Object[] listeners = listenerList.getListenerList();
        PopupMenuEvent e = null;
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == PopupMenuListener.class) {
                if (e == null)
                    e = new PopupMenuEvent(this);
                ((PopupMenuListener) listeners[i + 1]).popupMenuCanceled(e);
            }
        }
    }

    /**
     * Gets the default operation when the owner is moved. The valid values are either {@link #HIDE_ON_MOVED}, {@link
     * #MOVE_ON_MOVED} or {@link #DO_NOTHING_ON_MOVED}.
     *
     * @return the default operation when the owner is moved.
     */
    public int getDefaultMoveOperation() {
        return _defaultMoveOperation;
    }

    /**
     * Sets the default operation when the owner is moved. The valid could be either {@link #HIDE_ON_MOVED}, {@link
     * #MOVE_ON_MOVED} or {@link #DO_NOTHING_ON_MOVED}.
     *
     * @param defaultMoveOperation the default operation when the owner is moved.
     */
    public void setDefaultMoveOperation(int defaultMoveOperation) {
        _defaultMoveOperation = defaultMoveOperation;
    }

    /**
     * Adds a component as excluded component. If a component is an excluded component or descendant of an excluded
     * component, clicking on it will not hide the popup.
     * <p/>
     * For example, AbstractComboBox uses JidePopup to display the popup. If you want to show a JDialog from the popup,
     * you will have to add the dialog as excluded component. See below for an example.
     * <pre><code>
     * JDialog dialog =new JDialog((Frame) JideSwingUtilities.getWindowForComponent(this), true);
     * dialog.add(new JTable(10, 4));
     * dialog.pack();
     * Container ancestorOfClass = SwingUtilities.getAncestorOfClass(JidePopup.class, this); // try
     * to find the JidePopup
     * if(ancestorOfClass instanceof  JidePopup) {
     *     ((JidePopup) ancestorOfClass).addExcludedComponent(dialog);
     * }
     * dialog.setVisible(true);
     * if(ancestorOfClass instanceof  JidePopup) {
     *     ((JidePopup) ancestorOfClass).removeExcludedComponent(dialog);
     * }
     * </code></pre>
     *
     * @param component the component should be excluded.
     */
    public void addExcludedComponent(Component component) {
        if (component != null && !_excludedComponents.contains(component)) {
            _excludedComponents.add(component);
        }
    }

    /**
     * Removes a component from the excluded component list. If a component is an excluded component, clicking on it
     * will not hide the popup.
     *
     * @param component the component was excluded before.
     */
    public void removeExcludedComponent(Component component) {
        _excludedComponents.remove(component);
    }

    /**
     * Removes all excluded components that were added before.
     */
    public void removeAllExcludedComponents() {
        _excludedComponents.clear();
    }

    /**
     * Checks if a component is an excluded component. If a component is an excluded component, clicking on it will not
     * hide the popup. By default, owner is always the excluded component.
     *
     * @param component a component.
     * @return true if the component is an excluded component.
     */
    public boolean isExcludedComponent(Component component) {
        boolean contain = _excludedComponents.contains(component);
        if (!contain) {
            for (Component c : _excludedComponents) {
                if (c instanceof Container) {
                    if (((Container) c).isAncestorOf(component)) {
                        return true;
                    }
                }
            }

            if (component instanceof JComponent) {
                Container c = ((JComponent) component).getTopLevelAncestor();
                if (c instanceof RootPaneContainer) {
                    JComponent realParent = getRealParent((RootPaneContainer) c);
                    if (realParent != null && realParent.getTopLevelAncestor() == getTopLevelAncestor()) {
                        return true;
                    }
                }
            }
        }
        return contain;
    }

    public int getGripperLocation() {
        return _gripperLocation;
    }

    /**
     * Sets the gripper location. The valid values are {@link SwingConstants#NORTH}, {@link SwingConstants#SOUTH},
     * {@link SwingConstants#EAST}, and {@link SwingConstants#WEST}.
     *
     * @param gripperLocation the new gripper location.
     */
    public void setGripperLocation(int gripperLocation) {
        int old = _gripperLocation;
        if (old != gripperLocation) {
            _gripperLocation = gripperLocation;
            firePropertyChange(PROPERTY_GRIPPER_LOCATION, old, gripperLocation);
        }
    }

    public int getPopupType() {
        return _popupType;
    }

    public void setPopupType(int popupType) {
        if (popupType != LIGHT_WEIGHT_POPUP && popupType != HEAVY_WEIGHT_POPUP) {
            throw new IllegalArgumentException("invalid popup type. It must be JidePopup.HEAVY_WEIGHT_POPUP or JidePopup.LIGHT_WEIGHT_POPUP.");
        }
        _popupType = popupType;
    }

    /**
     * Checks if the mouse event is on the popup. By default, we will check if popup is an ancestor of the clicked
     * component. If it returns true, the popup will not be hidden. If false, the popup will be hidden as we consider
     * the mouse click is outside the popup.
     *
     * @param e the mouse event
     * @return true or false.
     */
    public boolean isClickOnPopup(MouseEvent e) {
        Component c = e.getComponent();
        if (c == null) {
            return false;
        }
        Component component = SwingUtilities.getDeepestComponentAt(c, e.getX(), e.getY());
        return getPopupType() == HEAVY_WEIGHT_POPUP ? JideSwingUtilities.isAncestorOf(component, _window) : JideSwingUtilities.isAncestorOf(component, _panel);
    }

    /**
     * Gets the actual owner. User can set owner using {@link #setOwner(java.awt.Component)} method. But when one of the
     * showPopup methods with owner parameter is called, the actual owner will be changed to this component.
     *
     * @return the actual owner.
     */
    protected Component getActualOwner() {
        if (_actualOwner != null) {
            return _actualOwner;
        }
        else {
            return getOwner();
        }
    }

    /**
     * Sets the preferred popup size. This method can be used when you want to keep the popup size to be the same as
     * when it was closed.
     *
     * @param size the size of the popup when it was shown last time.
     */
    public void setPreferredPopupSize(Dimension size) {
        _previousSize = size;
    }

    public static boolean isPopupAncestorOf(JidePopup popup, Component c) {
        Container p;
        if (c == null || ((p = c.getParent()) == null)) {
            return false;
        }
        while (p != null) {
            if (p == popup) {
                return true;
            }
            if (p instanceof JidePopup) { // found another popup
                return false;
            }
            p = p.getParent();
        }
        return false;
    }

}
