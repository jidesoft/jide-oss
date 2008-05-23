/*
 * @(#)DesktopProperty.java
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.jidesoft.plaf;

import javax.swing.*;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.FontUIResource;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.ref.WeakReference;

/**
 * Wrapper for a value from the desktop. The value is lazily looked up, and
 * can be accessed using the <code>UIManager.ActiveValue</code> method
 * <code>createValue</code>. If the underlying desktop property changes this
 * will force the UIs to update all known Frames. You can invoke
 * <code>invalidate</code> to force the value to be fetched again.
 * <p/>
 * Note: This class is the same as DesktopProperty under com.sun.java.swing.plaf.windows.
 * We keep a copy of it here is mainly for compatibility reason in case Sun
 * decides to change the location of this class.
 */

public class WindowsDesktopProperty implements UIDefaults.ActiveValue {
    /**
     * Indicates if an updateUI call is pending.
     */
    private static boolean updatePending;

    /**
     * PropertyChangeListener attached to the Toolkit.
     */
    private WeakPCL pcl;
    /**
     * Key used to lookup value from desktop.
     */
    private String key;
    /**
     * Value to return.
     */
    private Object value;
    /**
     * Fallback value in case we get null from desktop.
     */
    private Object fallback;

    /**
     * Toolkit.
     */
    private Toolkit toolkit;

    private float fontSize = -1f;

    private int fontStyle = -1;


    /**
     * Sets whether or not an updateUI call is pending.
     */
    private static synchronized void setUpdatePending(boolean update) {
        updatePending = update;
    }

    /**
     * Returns true if a UI update is pending.
     */
    private static synchronized boolean isUpdatePending() {
        return updatePending;
    }

    /**
     * Updates the UIs of all the known Frames.
     */
    private static void updateAllUIs() {
        Frame appFrames[] = Frame.getFrames();
        for (Frame frame : appFrames) {
            updateWindowUI(frame);
        }
    }

    /**
     * Updates the UI of the passed in window and all its children.
     */
    private static void updateWindowUI(Window window) {
        SwingUtilities.updateComponentTreeUI(window);
        Window ownedWins[] = window.getOwnedWindows();
        for (Window win : ownedWins) {
            updateWindowUI(win);
        }
    }


    /**
     * Creates a DesktopProperty.
     *
     * @param key      Key used in looking up desktop value.
     * @param fallback Value used if desktop property is null.
     * @param toolkit  Toolkit used to fetch property from, can be null
     *                 in which default will be used.
     */
    public WindowsDesktopProperty(String key, Object fallback, Toolkit toolkit) {
        this.key = key;
        this.fallback = fallback;
        this.toolkit = toolkit;
    }

    public WindowsDesktopProperty(String key, Object fallback, Toolkit toolkit, float fontSize) {
        this.key = key;
        this.fallback = fallback;
        this.toolkit = toolkit;
        this.fontSize = fontSize;
    }

    public WindowsDesktopProperty(String key, Object fallback, Toolkit toolkit, float fontSize, int fontStyle) {
        this.key = key;
        this.fallback = fallback;
        this.toolkit = toolkit;
        this.fontSize = fontSize;
        this.fontStyle = fontStyle;
    }

    /**
     * UIManager.LazyValue method, returns the value from the desktop
     * or the fallback value if the desktop value is null.
     */
    public Object createValue(UIDefaults table) {
        if (value == null) {
            value = configureValue(getValueFromDesktop());
            if (value == null) {
                value = configureValue(getDefaultValue());
            }
        }
        return value;
    }

    /**
     * Returns the value from the desktop.
     */
    protected Object getValueFromDesktop() {
        if (this.toolkit == null) {
            this.toolkit = Toolkit.getDefaultToolkit();
        }
        Object value = toolkit.getDesktopProperty(getKey());
        pcl = new WeakPCL(this, toolkit);
        toolkit.addPropertyChangeListener(getKey(), pcl);
        return value;
    }

    /**
     * Returns the value to use if the desktop property is null.
     */
    protected Object getDefaultValue() {
        if (fallback instanceof String) {
            return UIDefaultsLookup.get(fallback);
        }
        else {
            return fallback;
        }
    }

    /**
     * Invalides the current value so that the next invocation of
     * <code>createValue</code> will ask for the property again.
     */
    public void invalidate() {
        if (pcl != null) {
            toolkit.removePropertyChangeListener(getKey(), pcl);
            toolkit = null;
            pcl = null;
            value = null;
        }
    }

    /**
     * Requests that all components in the GUI hierarchy be updated
     * to reflect dynamic changes in this look&feel.  This update occurs
     * by uninstalling and re-installing the UI objects. Requests are
     * batched and collapsed into a single update pass because often
     * many desktop properties will change at once.
     */
    protected void updateUI() {
        if (!isUpdatePending()) {
            setUpdatePending(true);
            Runnable uiUpdater = new Runnable() {
                public void run() {
                    updateAllUIs();
                    setUpdatePending(false);
                }
            };
            SwingUtilities.invokeLater(uiUpdater);
        }
    }

    /**
     * Configures the value as appropriate for a defaults property in
     * the UIDefaults table.
     */
    protected Object configureValue(Object value) {
        if (value != null) {
            if (value instanceof Color) {
                return new ColorUIResource((Color) value);
            }
            else if (value instanceof Font) {
                if (fontSize != -1 && fontStyle != -1f) {
                    return new FontUIResource(((Font) value).deriveFont(fontStyle, fontSize));
                }
                else if (fontSize != -1) {
                    return new FontUIResource(((Font) value).deriveFont(fontSize));
                }
                else if (fontStyle != -1f) {
                    return new FontUIResource(((Font) value).deriveFont(fontStyle));
                }
                else {
                    return new FontUIResource((Font) value);
                }
            }
            else if (value instanceof UIDefaults.ProxyLazyValue) {
                value = ((UIDefaults.ProxyLazyValue) value).createValue(null);
            }
            else if (value instanceof UIDefaults.ActiveValue) {
                value = ((UIDefaults.ActiveValue) value).createValue(null);
            }
        }
        return value;
    }

    /**
     * Returns the key used to lookup the desktop properties value.
     */
    protected String getKey() {
        return key;
    }


    /**
     * As there is typically only one Toolkit, the PropertyChangeListener
     * is handled via a WeakReference so as not to pin down the
     * DesktopProperty.
     */
    private static class WeakPCL extends WeakReference
            implements PropertyChangeListener {
        private Toolkit kit;

        WeakPCL(Object target, Toolkit kit) {
            super(target);
            this.kit = kit;
        }

        public void propertyChange(PropertyChangeEvent pce) {
            WindowsDesktopProperty property = (WindowsDesktopProperty) get();

            if (property == null) {
                // The property was GC'ed, we're no longer interested in
                // PropertyChanges, remove the listener.
                kit.removePropertyChangeListener(pce.getPropertyName(), this);
            }
            else {
                property.invalidate();
                property.updateUI();
            }
        }
    }
}
