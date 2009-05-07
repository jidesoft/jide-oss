package com.jidesoft.plaf;

import javax.swing.*;
import javax.swing.plaf.ComponentUI;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * When UIManager.getUI(JComponent target) is called to retrieve a ComponentUI object for a target, we want to make sure
 * that ComponentUI is from the same classloader as the target classloader.
 * <p/>
 * The CacheCleanerLookAndFeel will install itself as a hook to intercept UIManager().getUI(). It will clean up the
 * UIManager cache if needed and also update the UIManager L&F defaults table if needed. This is very useful if you have
 * to use multiple class loader and each class loader has its own version of JIDE jars.
 * <p/>
 * <code><pre>
 * CachedLookAndFeel.install();
 * CachedLookAndFeel.installJideExtension(LookAndFeelFactory.class.getClassLoader(), true);
 * </pre></code>
 */
class CachedLookAndFeel extends LookAndFeel {
    static ClassLoader currentLoader; // active classloader

    UIDefaults customDefaults = new CustomUIDefaults();

    /**
     * Install itself as UIManager.getLAFState().multiLookAndFeel to that calls to getUI() can be trapped
     */
    public static void install() {
        try {
            // lafState = UIManager.getLAFState() method
            Method method = UIManager.class.getDeclaredMethod("getLAFState", new Class[]{null});
            method.setAccessible(true); // turn off checking for private method
            Object lafState = method.invoke(null, new Object[]{null});

            // lafState.multiLookAndFeel = new CacheCleanerLookAndFeel()
            Field field = lafState.getClass().getDeclaredField("multiLookAndFeel");
            field.setAccessible(true); // turn off security checking
            CachedLookAndFeel laf = new CachedLookAndFeel();
            field.set(lafState, laf);

        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public CachedLookAndFeel() {
        customDefaults = new CustomUIDefaults();
    }

    @Override
    public String getName() {
        return "CachedLookAndFeel";
    }

    @Override
    public String getID() {
        return "CachedLookAndFeel";
    }

    @Override
    public String getDescription() {
        return "Provide customized behaviour for getUI() method";
    }

    @Override
    public boolean isNativeLookAndFeel() {
        return false;
    }

    @Override
    public boolean isSupportedLookAndFeel() {
        return true;
    }

    @Override
    public UIDefaults getDefaults() {
        return customDefaults;
    }

    /**
     * Call the LookAndFeelFactory.installJideExtension using a specific class loader. This is used for GUI context
     * switch when going between two class loaders.
     *
     * @param newLoader ClassLoader
     */
    public static void installJideExtension(ClassLoader newLoader) {
        installJideExtension(newLoader, false);
    }

    /**
     * Call the LookAndFeelFactory.installJideExtension using a specific class loader. This is used for GUI context
     * switch when going between two class loaders.
     *
     * @param newLoader ClassLoader
     * @param force     if true, we will install the extension even when the current loader and new class loader are the
     *                  same.
     */
    public static void installJideExtension(ClassLoader newLoader, boolean force) {
        if (currentLoader == newLoader && !force) {
            return;
        }

        try {
            Class lafFactory = newLoader.loadClass("com.jidesoft.plaf.LookAndFeelFactory");
            Method installJideExtension = lafFactory.getDeclaredMethod("installJideExtension", new Class[]{int.class});
            int style = LookAndFeelFactory.getDefaultStyle();
            UIManager.put(LookAndFeelFactory.JIDE_EXTENSION_INSTALLLED, null); // force population
            installJideExtension.invoke(null, style);

        }
        catch (Exception e) {
            // ignore
        }

        currentLoader = newLoader;
        UIManager.put("ClassLoader", newLoader);
    }

    public static void reinstallJideExtension() {
        installJideExtension(currentLoader, true);
    }

    private static void removeCachedClass(UIDefaults defaults, Class componentUIClass) {
        if (componentUIClass != null) {
            // remove className <--> class definition
            defaults.remove(componentUIClass.getName());
            // remove class definition <--> method definition
            defaults.remove(componentUIClass);
        }
    }

    static class CustomUIDefaults extends UIDefaults {
        private static final long serialVersionUID = -6034471887061473005L;

        @Override
        public ComponentUI getUI(JComponent target) {
            UIDefaults defaults = UIManager.getDefaults();

            // to increase the performance, UIManager is caching the
            // className <--> class definition object
            // It does not differentiate between classNames from different class loaders
            // This means that the cached class definition may  not match the current
            // classloader and will cause ClassCastException later on.
            // http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4675772
            String className = (String) defaults.get(target.getUIClassID());
            Class componentUIClass = className != null ? (Class) defaults.get(className) : null;
            ClassLoader componentUIClassLoader = componentUIClass != null ? componentUIClass.getClassLoader() : null;

            ClassLoader targetClassLoader = target.getClass().getClassLoader();
            ClassLoader uiClassLoader = (ClassLoader) UIManager.get("ClassLoader");

            if (targetClassLoader == null) { // for JMenuItem and JPopupMenu.Separator
                if (componentUIClassLoader != null && componentUIClassLoader != uiClassLoader) {
                    removeCachedClass(defaults, componentUIClass);
                }
                return null;
            }

            if (targetClassLoader != componentUIClassLoader ||
                    targetClassLoader != uiClassLoader) {
                if (componentUIClassLoader != null) {
                    removeCachedClass(defaults, componentUIClass);
                }
                installJideExtension(targetClassLoader);
            }
            return null;
        }
    }
}
