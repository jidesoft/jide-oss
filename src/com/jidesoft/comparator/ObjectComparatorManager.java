/*
 * @(#) ObjectComparatorManager.java
 *
 * Copyright 2002 - 2003 JIDE Software. All rights reserved.
 */
package com.jidesoft.comparator;

import com.jidesoft.converter.CacheMap;
import com.jidesoft.converter.RegistrationListener;

import java.text.Collator;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;

/**
 * A global object that can register comparator with a type and a ComparatorContext.
 */
public class ObjectComparatorManager {

    private static final CacheMap<Comparator<?>, ComparatorContext> _cache = new CacheMap<Comparator<?>, ComparatorContext>(new ComparatorContext(""));

    private static final Comparator<Object> _defaultComparator = new DefaultComparator();

    public static void registerComparator(Class<?> clazz, Comparator comparator) {
        registerComparator(clazz, comparator, ComparatorContext.DEFAULT_CONTEXT);
    }

    /**
     * Registers a comparator with the type specified as class and a comparator context specified as context.
     *
     * @param clazz      type.
     * @param comparator the comparator to be registered.
     * @param context    the comparator context.
     */
    public static void registerComparator(Class<?> clazz, Comparator comparator, ComparatorContext context) {
        if (clazz == null) {
            throw new IllegalArgumentException("Parameter clazz cannot be null");
        }
        if (context == null) {
            context = ComparatorContext.DEFAULT_CONTEXT;
        }

        if (isAutoInit() && !_initing) {
            initDefaultComparator();
        }

        _cache.register(clazz, comparator, context);
    }

    /**
     * Unregisters comparator associated with clazz and context.
     *
     * @param clazz the data type.
     */
    public static void unregisterComparator(Class<?> clazz) {
        _cache.unregister(clazz, ComparatorContext.DEFAULT_CONTEXT);
    }

    /**
     * Unregisters comparator associated with clazz and context.
     *
     * @param clazz   the data type.
     * @param context the comparator context.
     */
    public static void unregisterComparator(Class<?> clazz, ComparatorContext context) {
        if (context == null) {
            context = ComparatorContext.DEFAULT_CONTEXT;
        }
        _cache.unregister(clazz, context);
    }

    /**
     * Unregisters all the comparators which registered before.
     */
    public static void unregisterAllComparators() {
        _cache.clear();
    }

    /**
     * Gets the registered comparator associated with class and default context.
     *
     * @param clazz the data type.
     * @return the registered comparator.
     */
    public static Comparator getComparator(Class<?> clazz) {
        return getComparator(clazz, ComparatorContext.DEFAULT_CONTEXT);
    }

    /**
     * Gets the comparator.
     *
     * @param clazz   the data type.
     * @param context the comparator context.
     * @return the comparator.
     */
    public static Comparator getComparator(Class<?> clazz, ComparatorContext context) {
        if (isAutoInit()) {
            initDefaultComparator();
        }

        if (context == null) {
            context = ComparatorContext.DEFAULT_CONTEXT;
        }
        Comparator object = _cache.getRegisteredObject(clazz, context);
        if (object != null) {
            return object;
        }
        else {
            return _defaultComparator;
        }
    }

    /**
     * Compares the two objects. It will look up in <code>ObjectComparatorManager</code> to find the comparator and
     * compare.
     *
     * @param o1 the first object to be compared.
     * @param o2 the second object to be compared.
     * @return the compare result as defined in {@link Comparator#compare(Object,Object)}
     */

    public static int compare(Object o1, Object o2) {
        return compare(o1, o2, ComparatorContext.DEFAULT_CONTEXT);
    }

    /**
     * Compares the two objects. It will look up in <code>ObjectComparatorManager</code> to find the comparator and
     * compare.
     *
     * @param o1      the first object to be compared.
     * @param o2      the second object to be compared.
     * @param context the comparator context
     * @return the compare result as defined in {@link Comparator#compare(Object,Object)}
     */

    public static int compare(Object o1, Object o2, ComparatorContext context) {
        if (o1 == null && o2 == null) {
            return 0;
        }
        else if (o1 == null) {
            return -1;
        }
        else if (o2 == null) {
            return 1;
        }

        // both not null

        Class<?> clazz;
        Class<?> clazz1 = o1.getClass();
        Class<?> clazz2 = o2.getClass();
        if (clazz1 == clazz2) {
            clazz = clazz1;
        }
        else if (clazz1.isAssignableFrom(clazz2)) {
            clazz = clazz1;
        }
        else if (clazz2.isAssignableFrom(clazz1)) {
            clazz = clazz2;
        }
        else if (clazz1.isAssignableFrom(Comparable.class) && clazz2.isAssignableFrom(Comparable.class)) {
            clazz = Comparable.class;
        }
        else {
            clazz = Object.class;
        }

        return compare(o1, o2, clazz, context);
    }

    /**
     * Compares the two objects. It will look up in <code>ObjectComparatorManager</code> to find the comparator and
     * compare. This method needs a third parameter which is the data type. This is useful when you have two objects
     * that have different data types but both extend the same super class. In this case, you may want the super class
     * as the key to look up in <code>ObjectComparatorManager</code>.
     *
     * @param o1    the first object to be compared.
     * @param o2    the second object to be compared.
     * @param clazz the data type of the two objects. If your two objects have the same type, you may just use {@link
     *              #compare(Object,Object)} methods.
     * @return the compare result as defined in {@link Comparator#compare(Object,Object)}
     */
    public static int compare(Object o1, Object o2, Class<?> clazz) {
        return compare(o1, o2, clazz, ComparatorContext.DEFAULT_CONTEXT);
    }

    /**
     * Compares the two objects. It will look up in <code>ObjectComparatorManager</code> to find the comparator and
     * compare. If it is not found, we will convert the object to string and compare the two strings.
     *
     * @param o1      the first object to be compared.
     * @param o2      the second object to be compared.
     * @param clazz   the data type of the two objects. If your two objects have the same type, you may just use {@link
     *                #compare(Object,Object)} methods.
     * @param context the comparator context
     * @return the compare result as defined in {@link Comparator#compare(Object,Object)}
     */
    public static int compare(Object o1, Object o2, Class<?> clazz, ComparatorContext context) {
        Comparator comparator = getComparator(clazz, context);
        if (comparator != null) {
            try {
                return comparator.compare(o1, o2);
            }
            catch (Exception e) {
                // ignore and let the code below handles it.
            }
        }
        if (o1 == o2) {
            return 0;
        }
        else {
            if (o1 == null) {
                return -1;
            }
            else if (o2 == null) {
                return 1;
            }
            else { // otherwise, compare as string
                return o1.toString().compareTo(o2.toString());
            }
        }
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
     * Sets autoInit to true or false. If autoInit is true, whenever someone tries to call methods like as toString or
     * fromString, {@link #initDefaultComparator()} will be called if it has never be called. By default, autoInit is
     * true.
     * <p/>
     * This might affect the behavior if users provide their own comparators and want to overwrite default comparators.
     * In this case, instead of depending on autoInit to initialize default comparators, you should call {@link
     * #initDefaultComparator()} first, then call registerComparator to add your own comparators.
     *
     * @param autoInit false if you want to disable autoInit which means you either don't want those default comparators
     *                 registered or you will call {@link #initDefaultComparator()} yourself.
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
     * Gets the available ComparatorContexts registered with the class.
     *
     * @param clazz the class.
     * @return the available ComparatorContext.
     */
    public static ComparatorContext[] getComparatorContexts(Class<?> clazz) {
        return _cache.getKeys(clazz, new ComparatorContext[0]);
    }

    /**
     * Initialize default comparator. Please make sure you call this method before you use any comparator related
     * classes such as SortableTableModel.
     */
    public static void initDefaultComparator() {
        if (_inited) {
            return;
        }

        _initing = true;

        try {
            registerComparator(Object.class, new DefaultComparator());
            registerComparator(Boolean.class, new BooleanComparator());
            registerComparator(Calendar.class, new CalendarComparator());
            registerComparator(Date.class, new DateComparator());

            NumberComparator numberComparator = new NumberComparator();
            registerComparator(Number.class, numberComparator);
            registerComparator(double.class, numberComparator);
            registerComparator(float.class, numberComparator);
            registerComparator(long.class, numberComparator);
            registerComparator(int.class, numberComparator);
            registerComparator(short.class, numberComparator);
            registerComparator(byte.class, numberComparator);

            NumberComparator absoluteNumberComparator = new NumberComparator();
            absoluteNumberComparator.setAbsolute(true);
            registerComparator(Number.class, absoluteNumberComparator, NumberComparator.CONTEXT_ABSOLUTE);
            registerComparator(double.class, absoluteNumberComparator, NumberComparator.CONTEXT_ABSOLUTE);
            registerComparator(float.class, absoluteNumberComparator, NumberComparator.CONTEXT_ABSOLUTE);
            registerComparator(long.class, absoluteNumberComparator, NumberComparator.CONTEXT_ABSOLUTE);
            registerComparator(int.class, absoluteNumberComparator, NumberComparator.CONTEXT_ABSOLUTE);
            registerComparator(short.class, absoluteNumberComparator, NumberComparator.CONTEXT_ABSOLUTE);
            registerComparator(byte.class, absoluteNumberComparator, NumberComparator.CONTEXT_ABSOLUTE);

            registerComparator(Comparable.class, new FastComparableComparator());
            registerComparator(String.class, Collator.getInstance());
            Collator caseInsensitiveCollator = Collator.getInstance();
            caseInsensitiveCollator.setStrength(Collator.PRIMARY);
            registerComparator(String.class, caseInsensitiveCollator, new ComparatorContext("Ignorecase"));
            registerComparator(CharSequence.class, new CharSequenceComparator(), CharSequenceComparator.CONTEXT);
            registerComparator(CharSequence.class, new CharSequenceComparator(false), CharSequenceComparator.CONTEXT_IGNORE_CASE);
            registerComparator(CharSequence.class, new AlphanumComparator(), AlphanumComparator.CONTEXT);
            registerComparator(CharSequence.class, new AlphanumComparator(false), AlphanumComparator.CONTEXT_IGNORE_CASE);
        }
        finally {
            _initing = false;
            _inited = true;
        }

    }

    /**
     * If {@link #initDefaultComparator()} is called once, calling it again will have no effect because an internal flag
     * is set. This method will reset the internal flag so that you can call  {@link #initDefaultComparator()} in case
     * you unregister all comparators using {@link #unregisterAllComparators()}.
     */
    public static void resetInit() {
        _inited = false;
    }

    public static void clear() {
        resetInit();
        _cache.clear();
    }
}
