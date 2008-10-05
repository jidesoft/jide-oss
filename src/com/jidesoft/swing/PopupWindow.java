/*
 * @(#)PopupWindow.java
 *
 * Copyright 2002 - 2003 JIDE Software. All rights reserved.
 */
package com.jidesoft.swing;

import com.jidesoft.utils.PortingUtils;

import javax.swing.*;
import javax.swing.event.EventListenerList;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;
import java.util.Vector;

/**
 * PopupWindow class
 * <p/>
 * You can add another JPopupMenu or JComboxBox in this popup.
 * <p/>
 * This class is copied from http://forum.java.sun.com/thread.jsp?forum=57&thread=230866 with some minor modifications.
 */
public class PopupWindow {

    /**
     * A list of event listeners for this component.
     */
    protected EventListenerList listenerList = new EventListenerList();

    private JWindow _delegate;
    private Container _container;
    private List _grabbed = new Vector();
    private List _excluded = new Vector();
    private WindowListener _windowListener;
    private ComponentListener _componentListener;
    private ContainerListener _containerListener;
    private MouseListener _mouseListener;
    private Component _component;

    // JDK 1.3 Porting Hint.
    // Use AWTEventListener instead
    //    private AWTEventListener _keyEventDispatcher;
    private KeyEventDispatcher _keyEventDispatcher;

    private Component _parent;

    public PopupWindow(Container container) {
        _container = container;
        createDelegate();
        createListeners();
    }

    private void createDelegate() {
        Window window = getWindow();
        if (window != null) {
            _delegate = new JWindow(window);
        }
    }

    public void add(Component component) {
        _component = component;
        _component.addPropertyChangeListener("preferredSize", new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                if (_delegate != null) {
                    _delegate.pack();
                }
            }
        });
        if (_delegate != null) {
            _delegate.getContentPane().add(component);
            _delegate.pack();
            // workaround for a problem. JWindow somehow offset the height by 1
            // See http://developer.java.sun.com/developer/bugParade/bugs/4511106.html
            // looks like call pack again solve the problem.
            _delegate.pack();
//                mDelegate.setSize(mDelegate.getSize().width, mDelegate.getSize().height + 1);
        }
    }

    public void show(Component relative, int x, int y) {
        _parent = relative;
        if (_delegate == null) {
            createDelegate();
            if (_delegate == null) return;
            add(_component);
        }

        Point p = new Point(x, y);

        SwingUtilities.convertPointToScreen(p, relative);

        Rectangle screenSize = PortingUtils.getScreenBounds(relative);

        Dimension size = _component.getPreferredSize();

        int left = p.x + size.width;
        int bottom = p.y + size.height;

        if (p.x < screenSize.x) {
            p.x = screenSize.x;
        }
        if (left > screenSize.width) {
            p.x = screenSize.width - size.width;
        }

        if (p.y < screenSize.y) {
            p.y = screenSize.y;
        }
        if (bottom > screenSize.height) {
            p.y = screenSize.height - size.height;
        }

//        Point location = relative.getLocationOnScreen();
        _delegate.setLocation(p.x, p.y);
        _delegate.setSize(_component.getPreferredSize());
        firePopupMenuWillBecomeVisible();
        _delegate.setVisible(true);
        grabContainers();

        // set popup window focus and register esc key
//        _delegate.toFront();
//        _delegate.requestFocus();

        // JDK 1.3 Porting Hint
        // Replace by AWTEventListener
// Following Block is for JDK 1.3
//        _keyEventDispatcher = new AWTEventListener() {
//            public void eventDispatched(AWTEvent e) {
//                if (e instanceof KeyEvent) {
//                    if (((KeyEvent) e).getKeyCode() == KeyEvent.VK_ESCAPE) {
//                        hide();
//                    }
//                }
//            }
//        };
//        Toolkit.getDefaultToolkit().addAWTEventListener(_keyEventDispatcher, AWTEvent.KEY_EVENT_MASK);

// Following Block is for JDK 1.4
        _keyEventDispatcher = new KeyEventDispatcher() {
            public boolean dispatchKeyEvent(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    hide();
                    return true;
                }
                return false;
            }
        };
        DefaultFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(_keyEventDispatcher);
    }

    public void hide() {
        if (_parent != null) {
            _parent.requestFocus();
        }
        firePopupMenuWillBecomeInvisible();

        if (_delegate != null) {
            _delegate.setVisible(false);
        }

        if (_keyEventDispatcher != null) {
            // JDK 1.3 Porting Hint
            // Replace by AWTEventListener
            // Toolkit.getDefaultToolkit().removeAWTEventListener(_keyEventDispatcher);
            DefaultFocusManager.getCurrentKeyboardFocusManager().removeKeyEventDispatcher(_keyEventDispatcher);
            _keyEventDispatcher = null;
        }
        releaseContainers();
        disposeDelegate();
    }

    private void createListeners() {
        _windowListener = new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                hide();
            }

            @Override
            public void windowClosed(WindowEvent e) {
                hide();
            }

            @Override
            public void windowIconified(WindowEvent e) {
                hide();
            }
        };
        _componentListener = new ComponentListener() {
            public void componentResized(ComponentEvent e) {
                hide();
            }

            public void componentMoved(ComponentEvent e) {
                hide();
            }

            public void componentShown(ComponentEvent e) {
                hide();
            }

            public void componentHidden(ComponentEvent e) {
                hide();
            }
        };
        _containerListener = new ContainerListener() {
            public void componentAdded(ContainerEvent e) {
                hide();
            }

            public void componentRemoved(ContainerEvent e) {
                hide();
            }
        };
        _mouseListener = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                hide();
            }
        };
    }

    private void disposeDelegate() {
        if (_delegate != null) {
            _delegate.dispose();
            _delegate = null;
        }
    }

    private Window getWindow() {
        Container c = _container;
        if (c == null) {
            return null;
        }
        while (!(c instanceof Window) && c.getParent() != null) c = c.getParent();
        if (c instanceof Window) return (Window) c;
        return null;
    }

    private void grabContainers() {
        Container c = _container;
        while (!(c instanceof Window) && c.getParent() != null)
            c = c.getParent();
        grabContainer(c);
    }

    private void grabContainer(Container c) {
        if (c instanceof Window) {
            ((Window) c).addWindowListener(_windowListener);
            c.addComponentListener(_componentListener);
            _grabbed.add(c);
        }

        synchronized (c.getTreeLock()) {
            int ncomponents = c.getComponentCount();
            Component[] component = c.getComponents();
            for (int i = 0; i < ncomponents; i++) {
                Component comp = component[i];
                if (!comp.isVisible()) continue;
                if (isExcludedComponent(comp)) {
                    continue;
                }
//                // TODO: this is not the right way to do things. Leave it for future enhancement to popup panel
//                // don't hide popup when button of abstract combobox is pressed so that that button can toggle visibility of popup panel
//                if(comp instanceof AbstractButton && comp.getParent() instanceof AbstractComboBox) {
//                    if(_delegate.isAncestorOf(((AbstractComboBox) comp.getParent()).getPopupPanel())) {
//                        continue;
//                    }
//                }
                comp.addMouseListener(_mouseListener);
                _grabbed.add(comp);
                if (comp instanceof Container) {
                    Container cont = (Container) comp;
                    if (cont instanceof JLayeredPane) {
                        cont.addContainerListener(_containerListener);
                    }
                    grabContainer(cont);
                }
            }
        }
    }

    void releaseContainers() {
        for (Object o : _grabbed) {
            Component c = (Component) o;
            if (c instanceof Window) {
                ((Window) c).removeWindowListener(_windowListener);
                c.removeComponentListener(_componentListener);
            }
            else {
                c.removeMouseListener(_mouseListener);
            }

            if (c instanceof Container) {
                if (c instanceof JLayeredPane) {
                    ((Container) c).removeContainerListener(_containerListener);
                }
            }
        }
        _grabbed.clear();
    }

    /**
     * Gets the visibility of this popup.
     *
     * @return true if popup is visible
     */
    public boolean isVisible() {
        return _delegate != null ? _delegate.isVisible() : false;
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
     * @since 1.4
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
     *
     * @since 1.4
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
     * @since 1.4
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
     * @since 1.4
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
     * @since 1.4
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
     * PopupWindow will add necessary listeners to some components so that mouse click etc can hide the popup window.
     * However in certain case, you might not want this.
     *
     * @param comp component which will not hide popup when it is clicked.
     */
    public void addAsExcludedComponents(Component comp) {
        if (_excluded.contains(comp)) {
            return;
        }
        _excluded.add(comp);
    }

    public void removeFromExcludedComponents(Component comp) {
        if (!_excluded.contains(comp)) {
            return;
        }
        _excluded.remove(comp);
    }

    public boolean isExcludedComponent(Component comp) {
        return _excluded.contains(comp);
    }
}
