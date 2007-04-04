/*
 * @(#) CacheMap.java
 *
 * Copyright 2002 - 2003 JIDE Software. All rights reserved.
 */
package com.jidesoft.converter;

import javax.swing.event.EventListenerList;
import java.util.*;

/**
 * CacheMap is a two level HashMap.
 */
public class CacheMap {
    private HashMap _cache = new HashMap();

    private Object _defaultContext; // used for fallback lookup. (Problem: there are two Default_Contexts (editor, converter)

    /**
     * Constructs a CacheMap.
     *
     * @param defaultContext
     */
    public CacheMap(Object defaultContext) {
        _defaultContext = defaultContext;
    }

    static class Cache extends HashMap {
        public Object getObject(Object context) {
            return get(context);
        }

        public void setObject(Object context, Object object) {
            if (object == null) {
                remove(context);
            }
            else {
                put(context, object);
            }
        }
    }

    protected Cache getCache(Class clazz) {
        if (clazz == null) {
            new IllegalArgumentException("Clazz cannot be null");
        }
        return (Cache) _cache.get(clazz);
    }

    protected Cache initCache(Class clazz) {
        Object editors = getCache(clazz);
        if (editors != null) {
            return (Cache) editors;
        }
        else {
            editors = new Cache();
            _cache.put(clazz, editors);
            return (Cache) editors;
        }
    }

    /**
     * Regsiters an object with the specified clazz and object.
     *
     * @param clazz
     * @param object
     * @param context
     */
    public void register(Class clazz, Object object, Object context) {
        if (clazz == null) {
            throw new IllegalArgumentException("Parameter clazz cannot be null");
        }

        Cache cache = initCache(clazz);
        cache.setObject(context, object);
        fireRegistrationChanged(new RegistrationEvent(this, RegistrationEvent.REGISTRATION_ADDED, object, clazz, context));
    }

    /**
     * Unregistered the object associated with the specified class and context.
     *
     * @param clazz
     * @param context
     */
    public void unregister(Class clazz, Object context) {
        Cache cache = getCache(clazz);
        if (cache != null) {
            Object object = cache.getObject(context);
            cache.setObject(context, null);
            fireRegistrationChanged(new RegistrationEvent(this, RegistrationEvent.REGISTRATION_REMOVED, object, clazz, context));
        }
    }

    // do a lookup through the interface graph (breath first recursion)
    private Cache lookupInterfaces(Class clazz) {
        Cache editors = null;

        if (clazz != null) {
            Class interfaces[] = clazz.getInterfaces();
            for (int i = 0; editors == null && interfaces != null && i < interfaces.length; i++) {
                editors = getCache(interfaces[i]);
            }
            if (editors == null) {
                for (int i = 0; editors == null && interfaces != null && i < interfaces.length; i++) {
                    // check inherited interfaces
                    editors = lookupInterfaces(interfaces[i]);
                }
            }
        }
        return editors;
    }

    /**
     * Gets registered object from CacheMap. The algorithm used to look up is <BR>
     * 1. First check for exact match with clazz.<BR>
     * 2. If didn't find, look for interfaces that clazz implements.<BR>
     * 3. If still didn't find, look for super class of clazz<BR>
     * 4. If still didn't find, return null.<BR>
     * If found a match in step 1, 2, and 3, it will return the registered object immediately.
     *
     * @param clazz
     * @param context
     * @return registered object
     */
    public Object getRegisteredObject(Class clazz, Object context) {
        if (clazz == null) {
            return null;
        }
        Class originalClazz = clazz; //RM for lookup using default context if first lookup fails
        Cache editors = getCache(clazz);

        if (editors == null || !editors.containsKey(context)) {
            List classesToSearch = new ArrayList();

            // Direct superinterfaces, recursively
            Class[] interfaces = clazz.getInterfaces();
            for (int i = 0; i < interfaces.length; i++) {
                Class c = interfaces[i];
                classesToSearch.add(c);
            }

            // Direct superclass, recursively
            while (clazz != null && !clazz.isInterface()) {
                clazz = clazz.getSuperclass();
                if (clazz != null) {
                    classesToSearch.add(clazz);
                    interfaces = clazz.getInterfaces();
                    for (int i = 0; i < interfaces.length; i++) {
                        Class c = interfaces[i];
                        classesToSearch.add(c);
                    }
                }
                else {
                    break;
                }
            }

            if (classesToSearch.isEmpty()) {
                classesToSearch.add(Object.class);  // use Object as default fallback.
            }

            for (int i = 0; i < classesToSearch.size(); i++) {
                Class c = (Class) classesToSearch.get(i);
                editors = getCache(c);

                if (editors != null) {
                    Object object = editors.getObject(context);
                    if (object == null && !_defaultContext.equals(context)) {
                        return getRegisteredObject(c, _defaultContext);
                    }
                    if (object != null) {
                        return object;
                    }
                }
            }
        }
        else {
            Object object = editors.getObject(context);
            if (object == null && !_defaultContext.equals(context)) {
                return getRegisteredObject(clazz, _defaultContext);
            }
            if (object != null) {
                return object;
            }
        }

        return null;
    }

    public List getValues() {
        ArrayList list = new ArrayList();
        Collection col = _cache.values();
        for (Iterator iterator = col.iterator(); iterator.hasNext();) {
            Object o = iterator.next();
            if (o instanceof CacheMap.Cache) {
                Collection col2 = ((CacheMap.Cache) o).values();
                for (Iterator iterator2 = col2.iterator(); iterator2.hasNext();) {
                    Object o2 = iterator2.next();
                    if (!list.contains(o2)) {
                        list.add(o2);
                    }
                }
            }
        }
        return list;
    }

    public void clear() {
        _cache.clear();
        fireRegistrationChanged(new RegistrationEvent(this, RegistrationEvent.REGISTRATION_CLEARED));
    }

    /**
     * List of listeners
     */
    protected EventListenerList listenerList = new EventListenerList();

    /**
     * Adds a listener to the list that's notified each time a change
     * to the registration occurs.
     *
     * @param l the RegistrationListener
     */
    public void addRegistrationListener(RegistrationListener l) {
        listenerList.add(RegistrationListener.class, l);
    }

    /**
     * Removes a listener from the list that's notified each time a
     * change to the registration occurs.
     *
     * @param l the RegistrationListener
     */
    public void removeRegistrationListener(RegistrationListener l) {
        listenerList.remove(RegistrationListener.class, l);
    }

    /**
     * Returns an array of all the registration listeners
     * registered on this registration.
     *
     * @return all of this registration's <code>RegistrationListener</code>s
     *         or an empty array if no registration listeners are currently registered
     * @see #addRegistrationListener
     * @see #removeRegistrationListener
     */
    public RegistrationListener[] getRegistrationListeners() {
        return (RegistrationListener[]) listenerList.getListeners(
                RegistrationListener.class);
    }

    /**
     * Forwards the given notification event to all
     * <code>RegistrationListeners</code> that registered
     * themselves as listeners for this table model.
     *
     * @param e the event to be forwarded
     * @see #addRegistrationListener
     * @see RegistrationEvent
     * @see EventListenerList
     */
    public void fireRegistrationChanged(RegistrationEvent e) {
        // Guaranteed to return a non-null array
        Object[] listeners = listenerList.getListenerList();
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == RegistrationListener.class) {
                ((RegistrationListener) listeners[i + 1]).registrationChanged(e);
            }
        }
    }
}
