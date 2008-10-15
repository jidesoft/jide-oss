package com.jidesoft.plaf.basic;

import com.jidesoft.plaf.UIDefaultsLookup;
import com.jidesoft.swing.JideButton;

import javax.swing.*;
import javax.swing.plaf.ComponentInputMapUIResource;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicButtonListener;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;

public class BasicJideButtonListener extends BasicButtonListener {
    private boolean _mouseOver = false;

    public BasicJideButtonListener(AbstractButton b) {
        super(b);
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        AbstractButton b = (AbstractButton) e.getSource();
        ButtonModel model = b.getModel();
        if (b.isRolloverEnabled()) {
            model.setRollover(true);
        }

        _mouseOver = true;

        if (model.isPressed())
            model.setArmed(true);
    }

    @Override
    public void propertyChange(PropertyChangeEvent e) {
        super.propertyChange(e);
        String prop = e.getPropertyName();
        if (JideButton.BUTTON_STYLE_PROPERTY.equals(prop)
                || "opaque".equals(prop)
                || AbstractButton.CONTENT_AREA_FILLED_CHANGED_PROPERTY.equals(prop)
                ) {
            AbstractButton b = (AbstractButton) e.getSource();
            b.repaint();
        }
        else if (JideButton.PROPERTY_ORIENTATION.equals(prop)
                || "hideActionText".equals(prop)) {
            AbstractButton b = (AbstractButton) e.getSource();
            b.invalidate();
            b.repaint();
        }
        else if ("verticalTextPosition".equals(prop)
                || "horizontalTextPosition".equals(prop)) {
            AbstractButton b = (AbstractButton) e.getSource();
            b.updateUI();
        }
    }

    // when on JideButton on popup, mouseReleased is not triggered for some reason.
    // So listen to clicked event instead.
    // bug at http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4991772
    @Override
    public void mouseClicked(MouseEvent e) {
        super.mouseClicked(e);
        cancelMenuIfNecessary(e);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        AbstractButton b = (AbstractButton) e.getSource();
        ButtonModel model = b.getModel();
        if (b.contains(e.getPoint())) {
            if (b.isRolloverEnabled()) {
                model.setRollover(true);
            }
        }
        if (!_mouseOver) {
            // these two lines order matters. In this order, it would not trigger actionPerformed.
            model.setArmed(false);
            model.setPressed(false);
        }
        super.mouseReleased(e);
        cancelMenuIfNecessary(e);
    }

    /**
     * Cancel the menu if this button is on JPopupMenu.
     *
     * @param e the mouse event.
     */
    private void cancelMenuIfNecessary(MouseEvent e) {
        AbstractButton b = (AbstractButton) e.getSource();
        MenuSelectionManager manager = MenuSelectionManager.defaultManager();
        MenuElement[] menuElements = manager.getSelectedPath();
        for (int i = menuElements.length - 1; i >= 0; i--) {
            MenuElement menuElement = menuElements[i];
            if (menuElement instanceof JPopupMenu && ((JPopupMenu) menuElement).isAncestorOf(b)) {
                b.getModel().setPressed(false);
                b.getModel().setArmed(false);
                b.getModel().setRollover(false);
                if (!Boolean.FALSE.equals(b.getClientProperty(JideButton.CLIENT_PROPERTY_HIDE_POPUPMENU))) {
                    manager.clearSelectedPath();
                }
                break;
            }
        }
    }

    @Override
    public void mouseExited(MouseEvent e) {
        AbstractButton b = (AbstractButton) e.getSource();
        ButtonModel model = b.getModel();
        if (b.isRolloverEnabled()) {
            model.setRollover(false);
        }
        model.setArmed(false);
        _mouseOver = false;
    }

    /**
     * Resets the binding for the mnemonic in the WHEN_IN_FOCUSED_WINDOW UI InputMap.
     *
     * @param b the button.
     */
    void updateMnemonicBinding(AbstractButton b) {
        int m = b.getMnemonic();
        if (m != 0) {
            InputMap map = SwingUtilities.getUIInputMap(
                    b, JComponent.WHEN_IN_FOCUSED_WINDOW);

            if (map == null) {
                map = new ComponentInputMapUIResource(b);
                SwingUtilities.replaceUIInputMap(b,
                        JComponent.WHEN_IN_FOCUSED_WINDOW, map);
            }
            map.clear();
            map.put(KeyStroke.getKeyStroke(m, InputEvent.ALT_MASK, false),
                    "pressed");
            map.put(KeyStroke.getKeyStroke(m, InputEvent.ALT_MASK, true),
                    "released");
            map.put(KeyStroke.getKeyStroke(m, 0, true), "released");
        }
        else {
            InputMap map = SwingUtilities.getUIInputMap(b, JComponent.
                    WHEN_IN_FOCUSED_WINDOW);
            if (map != null) {
                map.clear();
            }
        }
    }

    /**
     * Returns the ui that is of type <code>clazz</code>, or null if one can not be found.
     *
     * @param ui    the ComponentUI
     * @param clazz the class
     * @return the UI of the ComponentUI if it is an instance of the type.
     */
    static Object getUIOfType(ComponentUI ui, Class clazz) {
        if (clazz.isInstance(ui)) {
            return ui;
        }
        return null;
    }

    /**
     * Returns the InputMap for condition <code>condition</code>. Called as part of
     * <code>installKeyboardActions</code>.
     *
     * @param condition the condition.
     * @param c         the component
     * @return the InputMap on the component for the condition.
     */
    public InputMap getInputMap(int condition, JComponent c) {
        if (condition == JComponent.WHEN_FOCUSED) {
            BasicJideButtonUI ui = (BasicJideButtonUI) getUIOfType(
                    ((AbstractButton) c).getUI(), BasicJideButtonUI.class);
            if (ui != null) {
                return (InputMap) UIDefaultsLookup.get(ui.getPropertyPrefix() + "focusInputMap");
            }
        }
        return null;
    }

    /**
     * Actions for Buttons. Two type of action are supported: pressed: Moves the button to a pressed state released:
     * Disarms the button.
     */
    private static class Actions extends UIAction {
        private static final String PRESS = "pressed";
        private static final String RELEASE = "released";

        Actions(String name) {
            super(name);
        }

        public void actionPerformed(ActionEvent e) {
            AbstractButton b = (AbstractButton) e.getSource();
            String key = getName();
            if (PRESS.equals(key)) {
                ButtonModel model = b.getModel();
                model.setArmed(true);
                model.setPressed(true);
                if (!b.hasFocus() && b.isRequestFocusEnabled()) {
                    b.requestFocus();
                }
            }
            else if (RELEASE.equals(key)) {
                ButtonModel model = b.getModel();
                model.setPressed(false);
                model.setArmed(false);
            }
        }

        @Override
        public boolean isEnabled(Object sender) {
            return !(sender != null && (sender instanceof AbstractButton) &&
                    !((AbstractButton) sender).getModel().isEnabled());
        }
    }

    /**
     * Populates Buttons actions.
     *
     * @param map the action map.
     */
    public static void loadActionMap(LazyActionMap map) {
        map.put(new Actions(Actions.PRESS));
        map.put(new Actions(Actions.RELEASE));
    }


    @Override
    public void installKeyboardActions(JComponent c) {
        AbstractButton b = (AbstractButton) c;
        // Update the mnemonic binding.
        updateMnemonicBinding(b);

        LazyActionMap.installLazyActionMap(c, BasicJideButtonListener.class,
                "JideButton.actionMap");

        InputMap km = getInputMap(JComponent.WHEN_FOCUSED, c);

        SwingUtilities.replaceUIInputMap(c, JComponent.WHEN_FOCUSED, km);
    }
}

