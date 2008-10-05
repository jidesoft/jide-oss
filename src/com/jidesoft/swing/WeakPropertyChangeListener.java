package com.jidesoft.swing;

import javax.swing.event.ChangeEvent;
import java.awt.event.ItemEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.ref.WeakReference;
import java.lang.reflect.Method;

/**
 * How to use this:
 * <code><pre>
 * KeyboardFocusManager focusManager =
 * KeyboardFocusManager.getCurrentKeyboardFocusManager();
 * <p/>
 * // instead of registering directly use weak listener
 * // focusManager.addPropertyChangeListener(focusOwnerListener);
 * <p/>
 * focusManager.addPropertyChangeListener(
 * new WeakPropertyChangeListener(focusOwnerListener, focusManager));
 * </pre></code>
 * <p/>
 * How does this work:
 * <p/>
 * Instead of registering propertyChangeListener directly to keyboardFocusManager, we wrap it inside
 * WeakPropertyChangeListener and register this weak listener to keyboardFocusManager. This weak listener acts a
 * delegate. It receives the propertyChangeEvents from keyboardFocusManager and delegates it the wrapped listener.
 * <p/>
 * The interesting part of this weak listener, it hold a weakReference to the original propertyChangeListener. so this
 * delegate is eligible for garbage collection which it is no longer reachable via references. When it gets garbage
 * collection, the weakReference will be pointing to null. On next propertyChangeEvent notification from
 * keyboardFocusManager, it find that the weakReference is pointing to null, and unregisters itself from
 * keyboardFocusManager. Thus the weak listener will also become eligible for garbage collection in next gc cycle.
 * <p/>
 * This concept is not something new. If you have a habit of looking into swing sources, you will find that
 * AbstractButton actually adds a weak listener to its action. The weak listener class used for this is :
 * javax.swing.AbstractActionPropertyChangeListener; This class is package-private, so you don't find it in javadoc.
 * <p/>
 * The full-fledged, generic implementation of weak listeners is available in Netbeans OpenAPI: WeakListeners.java . It
 * is worth to have a look at it.
 *
 * @author Santhosh Kumar T - santhosh@in.fiorano.com
 */
public class WeakPropertyChangeListener implements PropertyChangeListener {
    private WeakReference<PropertyChangeListener> _listenerRef;
    private Object _src;

    public WeakPropertyChangeListener(PropertyChangeListener listener, Object src) {
        _listenerRef = new WeakReference(listener);
        _src = src;
    }

    public void propertyChange(PropertyChangeEvent evt) {
        PropertyChangeListener listener = _listenerRef.get();
        if (listener == null) {
            removeListener();
        }
        else
            listener.propertyChange(evt);
    }

    public void itemStateChanged(ItemEvent e) {

    }

    public void stateChanged(ChangeEvent e) {

    }

    private void removeListener() {
        try {
            Method method = _src.getClass().getMethod("removePropertyChangeListener"
                    , new Class[]{PropertyChangeListener.class});
            method.invoke(_src, new Object[]{this});
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}

