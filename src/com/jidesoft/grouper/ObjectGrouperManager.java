/*
 * @(#) ObjectGrouperManager.java
 *
 * Copyright 2002 - 2003 JIDE Software. All rights reserved.
 */
package com.jidesoft.grouper;

import com.jidesoft.converter.CacheMap;
import com.jidesoft.converter.RegistrationListener;
import com.jidesoft.grouper.date.DateMonthGrouper;
import com.jidesoft.grouper.date.DateYearGrouper;

import java.util.Calendar;
import java.util.Date;

/**
 * A global object that can register Object Grouper with a type and a GrouperContext.
 */
public class ObjectGrouperManager {

    private static CacheMap<ObjectGrouper, GrouperContext> _cache = new CacheMap<ObjectGrouper, GrouperContext>(GrouperContext.DEFAULT_CONTEXT);

    private static ObjectGrouper _defaultGrouper = null;

    /**
     * Registers a grouper with the type specified as class and a grouper context specified as context.
     *
     * @param clazz   type
     * @param grouper group to be registered
     * @param context the grouper context.
     */
    public static void registerGrouper(Class<?> clazz, ObjectGrouper grouper, GrouperContext context) {
        if (clazz == null) {
            throw new IllegalArgumentException("Parameter class cannot be null");
        }

        if (context == null) {
            context = GrouperContext.DEFAULT_CONTEXT;
        }

        if (isAutoInit() && !_initing) {
            initDefaultGrouper();
        }

        _cache.register(clazz, grouper, context);
    }

    /**
     * Registers a grouper with type specified as clazz.
     *
     * @param clazz   the data type.
     * @param grouper the grouper to be registered
     */
    public static void registerGrouper(Class<?> clazz, ObjectGrouper grouper) {
        registerGrouper(clazz, grouper, GrouperContext.DEFAULT_CONTEXT);
    }

    /**
     * Unregisters grouper associated with clazz and context.
     *
     * @param clazz   the data type.
     * @param context the grouper context.
     */
    public static void unregisterGrouper(Class<?> clazz, GrouperContext context) {
        if (context == null) {
            context = GrouperContext.DEFAULT_CONTEXT;
        }
        _cache.unregister(clazz, context);
    }

    /**
     * Unregisters grouper associated with clazz.
     *
     * @param clazz the data type.
     */
    public static void unregisterGrouper(Class<?> clazz) {
        unregisterGrouper(clazz, GrouperContext.DEFAULT_CONTEXT);
    }

    /**
     * Unregisters all the groupers which registered before.
     */
    public static void unregisterAllGroupers() {
        _cache.clear();
    }

    /**
     * Gets the registered grouper associated with class and context.
     *
     * @param clazz   the data type.
     * @param context the grouper context.
     * @return the registered grouper. It could return null if there is no grouper for the type and the context.
     */
    public static ObjectGrouper getGrouper(Class<?> clazz, GrouperContext context) {
        if (isAutoInit()) {
            initDefaultGrouper();
        }

        if (context == null) {
            context = GrouperContext.DEFAULT_CONTEXT;
        }
        ObjectGrouper object = _cache.getRegisteredObject(clazz, context);
        if (object != null) {
            return object;
        }
        else {
            return _defaultGrouper;
        }
    }

    /**
     * Gets the grouper associated with the type.
     *
     * @param clazz the data type.
     * @return the grouper. It could return null if there is no grouper for the type.
     */
    public static ObjectGrouper getGrouper(Class<?> clazz) {
        return getGrouper(clazz, GrouperContext.DEFAULT_CONTEXT);
    }

    /**
     * Converts an object to string using default grouper context.
     *
     * @param object object to be converted.
     * @return the string
     */
    public static Object getGroupValue(Object object) {
        if (object != null)
            return getGroupValue(object, object.getClass(), GrouperContext.DEFAULT_CONTEXT);
        else
            return null;
    }

    /**
     * Converts an object to string using default grouper context.
     *
     * @param object object to be converted.
     * @param clazz  type of the object
     * @return the string
     */
    public static Object getGroupValue(Object object, Class<?> clazz) {
        return getGroupValue(object, clazz, GrouperContext.DEFAULT_CONTEXT);
    }

    /**
     * Converts an object to string using grouper context specified.
     *
     * @param object  object to be converted.
     * @param clazz   type of the object
     * @param context group context
     * @return the string converted from object
     */
    public static Object getGroupValue(Object object, Class<?> clazz, GrouperContext context) {
        ObjectGrouper grouper = getGrouper(clazz, context);
        if (grouper != null) {
            return grouper.getValue(object);
        }
        return null;
    }

    private static boolean _inited = false;
    private static boolean _initing = false;
    private static boolean _autoInit = true;

    /**
     * Checks the value of autoInit.
     *
     * @return true or false.
     *
     * @see #setAutoInit(boolean)
     */
    public static boolean isAutoInit() {
        return _autoInit;
    }

    /**
     * Sets autoInit to true or false. If autoInit is true, whenever someone tries to call methods getValue, {@link
     * #initDefaultGrouper()} will be called if it has never be called. By default, autoInit is true.
     * <p/>
     * This might affect the behavior if users provide their own groupers and want to overwrite default groupers. In
     * this case, instead of depending on autoInit to initialize default groupers, you should call {@link
     * #initDefaultGrouper()} first, then call registerGrouper to add your own groupers.
     *
     * @param autoInit false if you want to disable autoInit which means you either don't want those default comparators
     *                 registered or you will call {@link #initDefaultGrouper()} yourself.
     */
    public static void setAutoInit(boolean autoInit) {
        _autoInit = autoInit;
    }

    /**
     * Adds a listener to the list that's notified each time a change to the manager occurs.
     *
     * @param l the RegistrationListener
     */
    public static void addRegistrationListener(RegistrationListener l) {
        _cache.addRegistrationListener(l);
    }

    /**
     * Removes a listener from the list that's notified each time a change to the manager occurs.
     *
     * @param l the RegistrationListener
     */
    public static void removeRegistrationListener(RegistrationListener l) {
        _cache.removeRegistrationListener(l);
    }

    /**
     * Returns an array of all the registration listeners registered on this manager.
     *
     * @return all of this registration's <code>RegistrationListener</code>s or an empty array if no registration
     *         listeners are currently registered
     *
     * @see #addRegistrationListener
     * @see #removeRegistrationListener
     */
    public static RegistrationListener[] getRegistrationListeners() {
        return _cache.getRegistrationListeners();
    }

    /**
     * Gets the available GrouperContexts registered with the class.
     *
     * @param clazz the class.
     * @return the available GrouperContexts.
     */
    public static GrouperContext[] getGrouperContexts(Class<?> clazz) {
        return _cache.getKeys(clazz, new GrouperContext[0]);
    }

    /**
     * Initialize default groupers. Please make sure you call this method before you use any group related classes. By
     * default we register following groupers.
     * <code><pre>
     *   DateYearGrouper dateYearGrouper = new DateYearGrouper();
     *   registerGrouper(Date.class, dateYearGrouper, DateYearGrouper.CONTEXT);
     *   registerGrouper(Calendar.class, dateYearGrouper, DateYearGrouper.CONTEXT);
     *   registerGrouper(Long.class, dateYearGrouper, DateYearGrouper.CONTEXT);
     *   DateMonthGrouper dateMonthGrouper = new DateMonthGrouper();
     *   registerGrouper(Date.class, dateMonthGrouper, DateMonthGrouper.CONTEXT);
     *   registerGrouper(Calendar.class, dateMonthGrouper, DateMonthGrouper.CONTEXT);
     *   registerGrouper(Long.class, dateMonthGrouper, DateMonthGrouper.CONTEXT);
     * </pre></code>
     */
    public static void initDefaultGrouper() {
        if (_inited) {
            return;
        }

        _initing = true;

        try {
            DateYearGrouper dateYearGrouper = new DateYearGrouper();
            registerGrouper(Date.class, dateYearGrouper, DateYearGrouper.CONTEXT);
            registerGrouper(Calendar.class, dateYearGrouper, DateYearGrouper.CONTEXT);
            registerGrouper(Long.class, dateYearGrouper, DateYearGrouper.CONTEXT);

            DateMonthGrouper dateMonthGrouper = new DateMonthGrouper();
            registerGrouper(Date.class, dateMonthGrouper, DateMonthGrouper.CONTEXT);
            registerGrouper(Calendar.class, dateMonthGrouper, DateMonthGrouper.CONTEXT);
            registerGrouper(Long.class, dateMonthGrouper, DateMonthGrouper.CONTEXT);
        }
        finally {
            _initing = false;
            _inited = true;
        }

    }

    /**
     * If {@link #initDefaultGrouper()} is called once, calling it again will have no effect because an internal flag is
     * set. This method will reset the internal flag so that you can call {@link #initDefaultGrouper()} in case you
     * unregister all groupers using {@link #unregisterAllGroupers()}.
     */
    public static void resetInit() {
        _inited = false;
    }

    public static void clear() {
        resetInit();
        _cache.clear();
    }
}
