package com.jidesoft.swing;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * This is a util class to support the four arrow keys navigation in any container. To use it, you can call
 * <code><pre>
 * new ArrowKeyNavigationSupport().install(container);
 * </pre></code>
 * The container could be any container. A typical use case is the button panel. By default we used it in {@link
 * com.jidesoft.dialog.ButtonPanel} class to enable left/right/up/down key.
 * <p/>
 * By default, all components will be navigable in the container but you can further define what components are
 * navigable by using the constructor
 * <code><pre>
 * new ArrowKeyNavigationSupport(Class[] componentTypes)
 * </pre></code>
 * where componentTypes is the list of the classes of the components that you would like to be navigable. For example,
 * <code><pre>
 * new ArrowKeyNavigationSupport(new Class[]{ AbstractButton.class }).install(container);
 * </pre></code>
 * to only allow any buttons (JButton, JideButton, JCheckBox, JRadioButton) etc.
 * <p/>
 * You can also allow certain keys to be used. For example.
 * <code><pre>
 * new ArrowKeyNavigationSupport(new int[]{ KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT}).install(container);
 * </pre></code>
 * if only left and right keys are making sense to navigate in your container.
 */
public class ArrowKeyNavigationSupport {
    private int[] _keyCode = new int[]{KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT, KeyEvent.VK_UP, KeyEvent.VK_DOWN};
    private Class[] _componentTypes;
    public static final String CLIENT_PROPERTY_ARROWKEY_NAVIGATION_SUPPORT = "ArrowKeyNavigationSupport.previousAction";

    public ArrowKeyNavigationSupport() {
    }

    public ArrowKeyNavigationSupport(Class[] componentTypes) {
        _componentTypes = componentTypes;
    }

    public ArrowKeyNavigationSupport(int[] keyCodes) {
        _keyCode = keyCodes;
    }

    public ArrowKeyNavigationSupport(Class[] componentTypes, int[] keyCode) {
        _keyCode = keyCode;
        _componentTypes = componentTypes;
    }

    /**
     * Installs the actions for arrow keys to allow user to navigate components using arrow keys.
     *
     * @param container the container such as ButtonPanel, JPanel etc.
     */
    public void install(JComponent container) {
        for (int keyCode : _keyCode) {
            InputMap inputMap = container.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
            KeyStroke keyStroke = KeyStroke.getKeyStroke(keyCode, 0);
            Object actionName = inputMap.get(keyStroke);
            if (actionName != null) {
                container.putClientProperty(CLIENT_PROPERTY_ARROWKEY_NAVIGATION_SUPPORT, actionName);
            }
            container.registerKeyboardAction(new NavigationAction(container, keyCode), "ArrowKeyNavigation " + keyCode, keyStroke, JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        }
    }

    /**
     * Uninstalls the actions for arrow keys.
     *
     * @param container the container such as ButtonPanel, JPanel etc.
     */
    public void uninstall(JComponent container) {
        for (int keyCode : _keyCode) {
            Object actionName = container.getClientProperty(CLIENT_PROPERTY_ARROWKEY_NAVIGATION_SUPPORT);
            if (actionName != null) {
                InputMap inputMap = container.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
                KeyStroke keyStroke = KeyStroke.getKeyStroke(keyCode, 0);
                inputMap.put(keyStroke, actionName);
            }
            else {
                container.unregisterKeyboardAction(KeyStroke.getKeyStroke(keyCode, 0));
            }
        }
    }

    private class NavigationAction implements ActionListener {
        private JComponent _parent;
        private int _keyCode;

        public NavigationAction(JComponent c, int key) {
            _parent = c;
            _keyCode = key;
        }

        public void actionPerformed(ActionEvent e) {
            final List<Rectangle> rects = new ArrayList();
            final List<Component> components = new ArrayList();
            JideSwingUtilities.setRecursively(_parent, new JideSwingUtilities.Handler() {
                public void postAction(Component c) {
                }

                public void action(Component c) {
                    if (_componentTypes != null) {
                        boolean allowed = false;
                        for (Class allowedType : _componentTypes) {
                            if (allowedType.isAssignableFrom(c.getClass())) {
                                allowed = true;
                                break;
                            }
                        }
                        if (!allowed) return;
                    }
                    Rectangle bounds = c.getBounds();
                    rects.add(SwingUtilities.convertRectangle(c, bounds, _parent));
                    components.add(c);
                }

                public boolean condition(Component c) {
                    return (c.isVisible() && c.isDisplayable() && c.isFocusable() && c.isEnabled());
                }
            });
            Component owner = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
            Component c = null;
            switch (_keyCode) {
                case KeyEvent.VK_RIGHT:
                    c = findComponentToRight(owner, rects, components);
                    break;
                case KeyEvent.VK_LEFT:
                    c = findComponentToLeft(owner, rects, components);
                    break;
                case KeyEvent.VK_UP:
                    c = findComponentToAbove(owner, rects, components);
                    break;
                case KeyEvent.VK_DOWN:
                    c = findComponentToBelow(owner, rects, components);
                    break;
            }
            if (c != null) c.requestFocusInWindow();
        }

        private Component findComponentToRight(Component c, List<Rectangle> rects, List<Component> components) {
            int max = Integer.MAX_VALUE;
            Component found = null;
            Rectangle src = SwingUtilities.convertRectangle(c, c.getBounds(), _parent);
            for (int i = 0; i < rects.size(); i++) {
                Rectangle dst = rects.get(i);
                if (dst.x <= src.x + src.width) { // not on the left
                    continue;
                }
                else if (dst.y + dst.height < src.y) { // on top
                    continue;
                }
                else if (dst.y > src.y + src.height) { // on bottom
                    continue;
                }

                int dist = dst.x - src.x - src.width;
                if (dist < max) {
                    max = dist;
                    found = components.get(i);
                }
            }

            return found;
        }

        private Component findComponentToBelow(Component c, List<Rectangle> rects, List<Component> components) {
            int max = Integer.MAX_VALUE;
            Component found = null;
            Rectangle src = SwingUtilities.convertRectangle(c, c.getBounds(), _parent);
            for (int i = 0; i < rects.size(); i++) {
                Rectangle dst = rects.get(i);
                if (dst.y <= src.y + src.height) { // not on the left
                    continue;
                }
                else if (dst.x + dst.width < src.x) { // on top
                    continue;
                }
                else if (dst.x > src.x + src.width) { // on bottom
                    continue;
                }

                int dist = dst.y - src.y - src.height;
                if (dist < max) {
                    max = dist;
                    found = components.get(i);
                }
            }

            return found;
        }

        private Component findComponentToLeft(Component c, List<Rectangle> rects, List<Component> components) {
            int max = Integer.MAX_VALUE;
            Component found = null;
            Rectangle src = SwingUtilities.convertRectangle(c, c.getBounds(), _parent);
            for (int i = 0; i < rects.size(); i++) {
                Rectangle dst = rects.get(i);
                if (dst.x + dst.width >= src.x) { // not on the right
                    continue;
                }
                else if (dst.y + dst.height < src.y) { // on top
                    continue;
                }
                else if (dst.y > src.y + src.height) { // on bottom
                    continue;
                }

                int dist = src.x - dst.x - dst.width;
                if (dist < max) {
                    max = dist;
                    found = components.get(i);
                }
            }

            return found;
        }

        private Component findComponentToAbove(Component c, List<Rectangle> rects, List<Component> components) {
            int max = Integer.MAX_VALUE;
            Component found = null;
            Rectangle src = SwingUtilities.convertRectangle(c, c.getBounds(), _parent);
            for (int i = 0; i < rects.size(); i++) {
                Rectangle dst = rects.get(i);
                if (dst.y + dst.height >= src.y) { // not on the above
                    continue;
                }
                else if (dst.x + dst.width < src.x) { // on left
                    continue;
                }
                else if (dst.x > src.x + src.width) { // on right
                    continue;
                }

                int dist = src.y - dst.y - dst.height;
                if (dist < max) {
                    max = dist;
                    found = components.get(i);
                }
            }

            return found;
        }

    }
}
