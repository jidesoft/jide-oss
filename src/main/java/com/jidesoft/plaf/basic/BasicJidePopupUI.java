/*
 * @(#)BasicJidePopupUI.java 2/25/2005
 *
 * Copyright 2002 - 2005 JIDE Software Inc. All rights reserved.
 */

package com.jidesoft.plaf.basic;

import com.jidesoft.plaf.PopupUI;
import com.jidesoft.plaf.UIDefaultsLookup;
import com.jidesoft.popup.JidePopup;
import com.jidesoft.swing.Gripper;

import javax.swing.*;
import javax.swing.plaf.ActionMapUIResource;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.UIResource;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;


/**
 * A basic L&F implementation of Popup.
 */
public class BasicJidePopupUI extends PopupUI {

    protected JidePopup _popup;

    //    protected MouseInputAdapter _borderListener;
    protected PropertyChangeListener _propertyChangeListener;
    protected LayoutManager _dockableFrameLayout;

    protected JComponent _northPane;
    protected JComponent _southPane;
    protected JComponent _westPane;
    protected JComponent _eastPane;

    protected Gripper _titlePane; // access needs this

    private boolean keyBindingRegistered = false;
    private boolean keyBindingActive = false;

/////////////////////////////////////////////////////////////////////////////
// ComponentUI Interface Implementation methods
/////////////////////////////////////////////////////////////////////////////

    public static ComponentUI createUI(JComponent b) {
        return new BasicJidePopupUI((JidePopup) b);
    }

    public BasicJidePopupUI() {
    }

    public BasicJidePopupUI(JidePopup f) {
        _popup = f;
    }

    @Override
    public void installUI(JComponent c) {

        _popup = (JidePopup) c;

        installDefaults();
        installListeners();
        installComponents();
        installKeyboardActions();
        _popup.setOpaque(true);
    }

    @Override
    public void uninstallUI(JComponent c) {
        if (c != _popup)
            throw new IllegalComponentStateException(this + " was asked to deinstall() "
                    + c + " when it only knows about "
                    + _popup + ".");

        uninstallKeyboardActions();
        uninstallComponents();
        uninstallListeners();
        uninstallDefaults();
        _popup = null;
    }

    protected void installDefaults() {
        JComponent contentPane = (JComponent) _popup.getContentPane();
        if (contentPane != null) {
            Color bg = contentPane.getBackground();
            if (bg instanceof UIResource)
                contentPane.setBackground(null);
        }
        _popup.setLayout(_dockableFrameLayout = createLayoutManager());
        _popup.setBackground(UIDefaultsLookup.getColor("JideButton.background"));

        LookAndFeel.installBorder(_popup, "Popup.border");

    }

    protected void installKeyboardActions() {
        ActionMap actionMap = getActionMap();
        SwingUtilities.replaceUIActionMap(_popup, actionMap);
    }

    ActionMap getActionMap() {
        ActionMap map = (ActionMap) UIDefaultsLookup.get("Popup.actionMap");
        if (map == null) {
            map = createActionMap();
            if (map != null) {
                UIManager.getLookAndFeelDefaults().put("Popup.actionMap",
                        map);
            }
        }
        return map;
    }

    ActionMap createActionMap() {
        ActionMap map = new ActionMapUIResource();
        // we don't use it right now. Leave it since we might use it later.
        return map;
    }

    protected void installComponents() {
        setNorthPane(createNorthPane(_popup));
        setSouthPane(createSouthPane(_popup));
        setEastPane(createEastPane(_popup));
        setWestPane(createWestPane(_popup));
    }

    /*
     * @since 1.3
     */
    protected void installListeners() {
        _propertyChangeListener = createPropertyChangeListener();
        _popup.addPropertyChangeListener(_propertyChangeListener);
    }

    InputMap getInputMap(int condition) {
        if (condition == JComponent.WHEN_IN_FOCUSED_WINDOW) {
            return createInputMap(condition);
        }
        return null;
    }

    InputMap createInputMap(int condition) {
        if (condition == JComponent.WHEN_IN_FOCUSED_WINDOW) {
            Object[] bindings = (Object[]) UIDefaultsLookup.get("Popup.windowBindings");

            if (bindings != null) {
                return LookAndFeel.makeComponentInputMap(_popup, bindings);
            }
        }
        return null;
    }

    protected void uninstallDefaults() {
        _dockableFrameLayout = null;
        _popup.setLayout(null);
        LookAndFeel.uninstallBorder(_popup);
    }

    protected void uninstallComponents() {
        setNorthPane(null);
        setSouthPane(null);
        setEastPane(null);
        setWestPane(null);
        _titlePane = null;
    }

    /*
     * @since 1.3
     */
    protected void uninstallListeners() {
        _popup.removePropertyChangeListener(_propertyChangeListener);
        _propertyChangeListener = null;
    }

    protected void uninstallKeyboardActions() {
        SwingUtilities.replaceUIInputMap(_popup, JComponent.
                WHEN_IN_FOCUSED_WINDOW, null);
        SwingUtilities.replaceUIActionMap(_popup, null);

    }

    @Override
    public Component getGripper() {
        return _titlePane;
    }

    protected LayoutManager createLayoutManager() {
        return new PopupLayout();
    }

    protected PropertyChangeListener createPropertyChangeListener() {
        return new PopupPropertyChangeListener();
    }


    @Override
    public Dimension getPreferredSize(JComponent x) {
        if (_popup == x && _popup.getLayout() != null)
            return _popup.getLayout().preferredLayoutSize(x);
        return new Dimension(100, 100);
    }

    @Override
    public Dimension getMinimumSize(JComponent x) {
        if (_popup == x) {
            return _popup.getLayout().minimumLayoutSize(x);
        }
        return new Dimension(0, 0);
    }

    @Override
    public Dimension getMaximumSize(JComponent x) {
        return new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE);
    }


    /**
     * Installs necessary mouse handlers on <code>newPane</code> and adds it to the frame. Reverse process for the
     * <code>currentPane</code>.
     */
    protected void replacePane(JComponent currentPane, JComponent newPane) {
        if (currentPane != null) {
            deinstallMouseHandlers(currentPane);
            _popup.remove(currentPane);
        }
        if (newPane != null) {
            _popup.add(newPane);
            installMouseHandlers(newPane);
        }
    }

    protected void deinstallMouseHandlers(JComponent c) {
    }

    protected void installMouseHandlers(JComponent c) {
    }

    protected JComponent createNorthPane(JidePopup w) {
        if (w.getGripperLocation() == SwingConstants.NORTH && w.isMovable()) {
            _titlePane = new Gripper();
            _titlePane.setOrientation(SwingConstants.VERTICAL);
            _titlePane.setRolloverEnabled(true);
            _titlePane.setOpaque(true);
            return _titlePane;
        }
        else {
            return null;
        }
    }


    protected JComponent createSouthPane(JidePopup w) {
        if (w.getGripperLocation() == SwingConstants.SOUTH && w.isMovable()) {
            _titlePane = new Gripper();
            _titlePane.setOrientation(SwingConstants.VERTICAL);
            _titlePane.setRolloverEnabled(true);
            _titlePane.setOpaque(true);
            return _titlePane;
        }
        else {
            return null;
        }
    }

    protected JComponent createWestPane(JidePopup w) {
        if (w.getGripperLocation() == SwingConstants.WEST && w.isMovable()) {
            _titlePane = new Gripper();
            _titlePane.setOrientation(SwingConstants.HORIZONTAL);
            _titlePane.setRolloverEnabled(true);
            _titlePane.setOpaque(true);
            return _titlePane;
        }
        else {
            return null;
        }
    }

    protected JComponent createEastPane(JidePopup w) {
        if (w.getGripperLocation() == SwingConstants.EAST && w.isMovable()) {
            _titlePane = new Gripper();
            _titlePane.setOrientation(SwingConstants.HORIZONTAL);
            _titlePane.setRolloverEnabled(true);
            _titlePane.setOpaque(true);
            return _titlePane;
        }
        else {
            return null;
        }
    }


    protected final boolean isKeyBindingRegistered() {
        return keyBindingRegistered;
    }

    protected final void setKeyBindingRegistered(boolean b) {
        keyBindingRegistered = b;
    }

    public final boolean isKeyBindingActive() {
        return keyBindingActive;
    }

    protected final void setKeyBindingActive(boolean b) {
        keyBindingActive = b;
    }


    protected void setupMenuOpenKey() {
        // PENDING(hania): Why are these WHEN_IN_FOCUSED_WINDOWs? Shouldn't
        // they be WHEN_ANCESTOR_OF_FOCUSED_COMPONENT?
        // Also, no longer registering on the DesktopIcon, the previous
        // action did nothing.
        InputMap map = getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        SwingUtilities.replaceUIInputMap(_popup,
                JComponent.WHEN_IN_FOCUSED_WINDOW, map);
        //ActionMap actionMap = getActionMap();
        //SwingUtilities.replaceUIActionMap(frame, actionMap);
    }

    protected void setupMenuCloseKey() {
    }

    public JComponent getNorthPane() {
        return _northPane;
    }

    protected void setNorthPane(JComponent c) {
        replacePane(_northPane, c);
        _northPane = c;
    }

    public JComponent getSouthPane() {
        return _southPane;
    }

    protected void setSouthPane(JComponent c) {
        replacePane(_southPane, c);
        _southPane = c;
    }

    public JComponent getWestPane() {
        return _westPane;
    }

    protected void setWestPane(JComponent c) {
        replacePane(_westPane, c);
        _westPane = c;
    }

    public JComponent getEastPane() {
        return _eastPane;
    }

    protected void setEastPane(JComponent c) {
        replacePane(_eastPane, c);
        _eastPane = c;
    }

    public class PopupPropertyChangeListener implements PropertyChangeListener {
        /**
         * Detects changes in state from the Popup and handles actions.
         */
        public void propertyChange(PropertyChangeEvent evt) {
            String prop = evt.getPropertyName();
            JidePopup f = (JidePopup) evt.getSource();
            Object newValue = evt.getNewValue();
            Object oldValue = evt.getOldValue();
            if (JidePopup.MOVABLE_PROPERTY.equals(prop)) {
                f.updateUI();
            }
            if (JidePopup.PROPERTY_GRIPPER_LOCATION.equals(prop)) {
                f.updateUI();
            }
        }
    }

    public class PopupLayout implements LayoutManager {
        public void addLayoutComponent(String name, Component c) {
        }

        public void removeLayoutComponent(Component c) {
        }

        public Dimension preferredLayoutSize(Container c) {
            Dimension result;
            Insets i = _popup.getInsets();

            result = new Dimension(_popup.getRootPane().getPreferredSize());
            result.width += i.left + i.right;
            result.height += i.top + i.bottom;

            if (getNorthPane() != null) {
                Dimension d = getNorthPane().getPreferredSize();
                result.width = Math.max(d.width, result.width);
                result.height += d.height;
            }

            if (getSouthPane() != null) {
                Dimension d = getSouthPane().getPreferredSize();
                result.width = Math.max(d.width, result.width);
                result.height += d.height;
            }

            if (getEastPane() != null) {
                Dimension d = getEastPane().getPreferredSize();
                result.width += d.width;
                result.height = Math.max(d.height, result.height);
            }

            if (getWestPane() != null) {
                Dimension d = getWestPane().getPreferredSize();
                result.width += d.width;
                result.height = Math.max(d.height, result.height);
            }

            return result;
        }

        public Dimension minimumLayoutSize(Container c) {

            // The minimum size of the dockable frame only takes into account the
            // _title pane since you are allowed to resize the frames to the point
            // where just the _title pane is visible.
            Dimension result = new Dimension();
            if (getNorthPane() != null) {
                result = new Dimension(getNorthPane().getMinimumSize());
            }
            if (getSouthPane() != null) {
                Dimension minimumSize = getSouthPane().getMinimumSize();
                result.width = Math.max(result.width, minimumSize.width);
                result.height += minimumSize.height;
            }
            if (getEastPane() != null) {
                Dimension minimumSize = getEastPane().getMinimumSize();
                result.width += minimumSize.width;
                result.height = Math.max(result.height, minimumSize.height);
            }
            if (getWestPane() != null) {
                Dimension minimumSize = getWestPane().getMinimumSize();
                result.width = Math.max(result.width, minimumSize.width);
                result.height += minimumSize.height;
            }
            Dimension alter = _popup.getContentPane().getMinimumSize();

            if (alter.width > result.width) {
                result.width = alter.width;
            }
            result.height += alter.height;

            Insets i = _popup.getInsets();
            result.width += i.left + i.right;
            result.height += i.top + i.bottom;

            return result;
        }

        public void layoutContainer(Container c) {
            Insets i = _popup.getInsets();
            int cx, cy, cw, ch;

            cx = i.left;
            cy = i.top;
            cw = _popup.getWidth() - i.left - i.right;
            ch = _popup.getHeight() - i.top - i.bottom;

            if (getNorthPane() != null) {
                getNorthPane().setVisible(true);
                Dimension size = getNorthPane().getPreferredSize();
                getNorthPane().setBounds(cx, cy, cw, size.height);
                cy += size.height;
                ch -= size.height;
            }
            if (getSouthPane() != null) {
                Dimension size = getSouthPane().getPreferredSize();
                getSouthPane().setBounds(cx, _popup.getHeight()
                        - i.bottom - size.height,
                        cw, size.height);
                ch -= size.height;
            }
            if (getWestPane() != null) {
                Dimension size = getWestPane().getPreferredSize();
                getWestPane().setBounds(cx, cy, size.width, ch);
                cw -= size.width;
                cx += size.width;
            }
            if (getEastPane() != null) {
                Dimension size = getEastPane().getPreferredSize();
                getEastPane().setBounds(cw - size.width, cy, size.width, ch);
                cw -= size.width;
            }

            if (_popup.getRootPane() != null) {
                _popup.getRootPane().setBounds(cx, cy, cw, ch);
            }
        }
    }
}
