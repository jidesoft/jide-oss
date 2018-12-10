/*
 * @(#)LazyActionMap.java 8/19/2006
 *
 * Copyright 2002 - 2006 JIDE Software Inc. All rights reserved.
 */

package com.jidesoft.plaf.basic;

import com.jidesoft.plaf.UIDefaultsLookup;

import javax.swing.*;
import javax.swing.plaf.ActionMapUIResource;
import java.lang.reflect.Method;

/**
 * An ActionMap that populates its contents as necessary. The
 * contents are populated by invoking the <code>loadActionMap</code>
 * method on the passed in Object.
 *
 * @author Scott Violet
 * @version 1.5, 12/19/03
 */
public class LazyActionMap extends ActionMapUIResource {
    /**
     * Object to invoke <code>loadActionMap</code> on. This may be
     * a Class object.
     */
    private transient Object _loader;

    /**
     * Installs an ActionMap that will be populated by invoking the
     * <code>loadActionMap</code> method on the specified Class
     * when necessary.
     * <p/>
     * This should be used if the ActionMap can be shared.
     *
     * @param c           JComponent to install the ActionMap on.
     * @param loaderClass Class object that gets loadActionMap invoked
     *                    on.
     * @param defaultsKey Key to use to defaults table to check for
     *                    existing map and what resulting Map will be registered on.
     */
    public static void installLazyActionMap(JComponent c, Class loaderClass,
                                            String defaultsKey) {
        ActionMap map = (ActionMap) UIDefaultsLookup.get(defaultsKey);
        if (map == null) {
            map = new LazyActionMap(loaderClass);
            UIManager.getLookAndFeelDefaults().put(defaultsKey, map);
        }
        SwingUtilities.replaceUIActionMap(c, map);
    }

    /**
     * Returns an ActionMap that will be populated by invoking the
     * <code>loadActionMap</code> method on the specified Class
     * when necessary.
     * <p/>
     * This should be used if the ActionMap can be shared.
     *
     * @param loaderClass Class object that gets loadActionMap invoked
     *                    on.
     * @param defaultsKey Key to use to defaults table to check for
     *                    existing map and what resulting Map will be registered on.
     */
    static ActionMap getActionMap(Class loaderClass,
                                  String defaultsKey) {
        ActionMap map = (ActionMap) UIDefaultsLookup.get(defaultsKey);
        if (map == null) {
            map = new LazyActionMap(loaderClass);
            UIManager.getLookAndFeelDefaults().put(defaultsKey, map);
        }
        return map;
    }


    private LazyActionMap(Class loader) {
        _loader = loader;
    }

    public void put(Action action) {
        put(action.getValue(Action.NAME), action);
    }

    @Override
    public void put(Object key, Action action) {
        loadIfNecessary();
        super.put(key, action);
    }

    @Override
    public Action get(Object key) {
        loadIfNecessary();
        return super.get(key);
    }

    @Override
    public void remove(Object key) {
        loadIfNecessary();
        super.remove(key);
    }

    @Override
    public void clear() {
        loadIfNecessary();
        super.clear();
    }

    @Override
    public Object[] keys() {
        loadIfNecessary();
        return super.keys();
    }

    @Override
    public int size() {
        loadIfNecessary();
        return super.size();
    }

    @Override
    public Object[] allKeys() {
        loadIfNecessary();
        return super.allKeys();
    }

    @Override
    public void setParent(ActionMap map) {
        loadIfNecessary();
        super.setParent(map);
    }

    private void loadIfNecessary() {
        if (_loader != null) {
            Object loader = _loader;

            _loader = null;
            Class klass = (Class) loader;
            try {
                Method method = klass.getDeclaredMethod("loadActionMap",
                        new Class[]{LazyActionMap.class});
                method.invoke(klass, this);
            }
            catch (Exception nsme) {
                System.out.println("LazyActionMap unable to load actions " + klass);
            }
        }
    }
}
