/*
 * @(#) CacheMap.java
 *
 * Copyright 2002 - 2003 JIDE Software. All rights reserved.
 */
package com.jidesoft.utils;

import java.util.*;

/**
 * <code>CacheMap</code> is a two-level <code>HashMap</code>. It uses Class as the key and you can map the key to an
 * object and a context as a pair. We use context because we want to register multiple objects with the same Class.
 * {@link #register(Class, Object, Object)} is the method to register a new entry. {@link #getRegisteredObject(Class,
 * Object)} will allow you to look up the object by specifying the Class and the context.
 */
public class CacheMap<T, K> {

    private HashMap<Class<?>, Cache<K, T>> _cache = new HashMap<Class<?>, Cache<K, T>>();

    private K _defaultContext; // used for fallback lookup.

    /**
     * Constructs a <code>CacheMap</code>.
     *
     * @param defaultContext the default context.
     */
    public CacheMap(K defaultContext) {
        _defaultContext = defaultContext;
    }

    static class Cache<K, T> extends HashMap<K, T> {
        private static final long serialVersionUID = 7764545350468551102L;

        public T getObject(K context) {
            return get(context);
        }

        public void setObject(K context, T object) {
            if (object == null) {
                remove(context);
            }
            else {
                put(context, object);
            }
        }
    }

    protected Cache<K, T> getCache(Class<?> clazz) {
        if (clazz == null) {
            throw new IllegalArgumentException("Clazz cannot be null");
        }
        return _cache.get(clazz);
    }

    /**
     * Gets the secondary keys that are registered with the class in CacheMap.
     *
     * @param clazz the class
     * @param a     the array to receive the keys.
     *
     * @return the secondary keys.
     */
    public K[] getKeys(Class<?> clazz, K[] a) {
        Cache<K, T> cache = getCache(clazz);
        if (cache != null) {
            Set<K> set = cache.keySet();
            return set.toArray(a);
        }
        else {
            return a;
        }
    }

    protected Cache<K, T> initCache(Class<?> clazz) {
        Cache<K, T> cache = getCache(clazz);
        if (cache != null) {
            return cache;
        }
        else {
            cache = new Cache<K, T>();
            _cache.put(clazz, cache);
            return cache;
        }
    }

    /**
     * Registers an object with the specified clazz and object.
     *
     * @param clazz   the class which is used as the key.
     * @param object  the object, or the value of the mapping
     * @param context the secondary key. It is used to register multiple objects to the same primary key (the clazz
     *                parameter in this case).
     */
    public void register(Class<?> clazz, T object, K context) {
        if (clazz == null) {
            throw new IllegalArgumentException("Parameter clazz cannot be null");
        }

        Cache<K, T> cache = initCache(clazz);
        cache.setObject(context, object);
        fireRegistrationChanged(new RegistrationEvent(this, RegistrationEvent.REGISTRATION_ADDED, object, clazz, context));
    }

    /**
     * Unregisters the object associated with the specified class and context.
     *
     * @param clazz   the class
     * @param context the context
     */
    public void unregister(Class<?> clazz, K context) {
        Cache<K, T> cache = getCache(clazz);
        if (cache != null) {
            Object object = cache.getObject(context);
            cache.setObject(context, null);
            fireRegistrationChanged(new RegistrationEvent(this, RegistrationEvent.REGISTRATION_REMOVED, object, clazz, context));
            if (cache.size() == 0) {
                _cache.remove(clazz);
            }
        }
    }

    /**
     * Gets registered object from CacheMap. The algorithm used to look up is <BR> 1. First check for exact match with
     * clazz and context.<BR> 2. If didn't find, look for interfaces that clazz implements using the exact context.<BR>
     * 3. If still didn't find, look for super class of clazz using the exact context. <BR> 4. If still didn't find,
     * using the exact clazz with default context.<BR> 5. If still didn't find, return null.<BR> If found a match in
     * step 1, 2, 3 or 4, it will return the registered object immediately.
     *
     * @param clazz   the class which is used as the primary key.
     * @param context the context which is used as the secondary key. This parameter could be null in which case the
     *                default context is used.
     *
     * @return registered object the object associated with the class and the context.
     */
    public T getRegisteredObject(Class<?> clazz, K context) {
        if (clazz == null) {
            return null;
        }

        Cache<K, T> cache = getCache(clazz);

        if (cache == null || !cache.containsKey(context)) {
            List<Class<?>> classesToSearch = new ArrayList<Class<?>>();

            classesToSearch.add(clazz);
            if (TypeUtils.isPrimitive(clazz)) {
                classesToSearch.add(TypeUtils.convertPrimitiveToWrapperType(clazz));
            }
            else if (TypeUtils.isPrimitiveWrapper(clazz)) {
                classesToSearch.add(TypeUtils.convertWrapperToPrimitiveType(clazz));
            }

            // Direct super interfaces, recursively
            Class<?>[] interfaces = clazz.getInterfaces();
            classesToSearch.addAll(Arrays.asList(interfaces));

            Class<?> superClass = clazz;
            // Direct super class, recursively
            while (!superClass.isInterface()) {
                superClass = superClass.getSuperclass();
                if (superClass != null) {
                    classesToSearch.add(superClass);
                    interfaces = superClass.getInterfaces();
                    classesToSearch.addAll(Arrays.asList(interfaces));
                }
                else {
                    break;
                }
            }

            List<Class<?>> interfacesToSearch = new ArrayList<Class<?>>();
            for (Class<?> aClass : classesToSearch) {
                if (aClass.isInterface()) {
                    addInterface(aClass, interfacesToSearch, classesToSearch);
                }
            }
            classesToSearch.addAll(interfacesToSearch);

            classesToSearch.remove(Object.class);
            classesToSearch.add(Object.class);  // use Object as the last default fallback.

            // search to match context first
            for (Class<?> c : classesToSearch) {
                Cache<K, T> cacheForClass = getCache(c);
                if (cacheForClass != null) {
                    T object = cacheForClass.getObject(context);
                    if (object != null) {
                        return object;
                    }
                }
            }

            // fall back to default context
            if (!_defaultContext.equals(context)) {
                for (Class<?> c : classesToSearch) {
                    Cache<K, T> cacheForClass = getCache(c);
                    if (cacheForClass != null) {
                        T object = cacheForClass.getObject(_defaultContext);
                        if (object != null) {
                            return object;
                        }
                    }
                }
            }
        }

        if (cache != null) {
            T object = cache.getObject(context);
            if (object == null && !_defaultContext.equals(context)) {
                return getRegisteredObject(clazz, _defaultContext);
            }
            if (object != null) {
                return object;
            }
        }

        return null;
    }

    private void addInterface(Class<?> anInterface, List<Class<?>> interfacesToSearch, List<Class<?>> classesToSearch) {
        if (anInterface != null) {
            Class<?>[] interfaces = anInterface.getInterfaces();
            for (Class<?> superInterface : interfaces) {
                if (!classesToSearch.contains(superInterface)) {
                    interfacesToSearch.add(superInterface);
                    addInterface(superInterface, interfacesToSearch, classesToSearch);
                }
            }
        }
    }

    /**
     * Gets the exact match registered object. Different from {@link #getRegisteredObject(Class, Object)} which will try
     * different context and super classes and interfaces to find match. This method will do an exact match.
     *
     * @param clazz   the class which is used as the primary key.
     * @param context the context which is used as the secondary key. This parameter could be null in which case the
     *                default context is used.
     *
     * @return registered object the object associated with the class and the context.
     */
    public T getMatchRegisteredObject(Class<?> clazz, K context) {
        if (clazz == null) {
            return null;
        }

        if (context == null) {
            context = _defaultContext;
        }

        Cache<K, T> cache = getCache(clazz);
        if (cache != null) {
            T object = cache.getObject(context);
            if (object != null) {
                return object;
            }
        }
        return null;
    }

    public List<T> getValues() {
        List<T> list = new ArrayList<T>();
        Collection<Cache<K, T>> col = _cache.values();
        for (Cache<K, T> o : col) {
            Collection<T> col2 = o.values();
            for (T o2 : col2) {
                if (!list.contains(o2)) {
                    list.add(o2);
                }
            }
        }
        return list;
    }

    /**
     * Remove all registrations for the designated class.
     *
     * @param clazz the class
     */
    @SuppressWarnings("unchecked")
    public void remove(Class<?> clazz) {
        Cache<K, T> cache = getCache(clazz);
        if (cache != null) {
            Object[] keys = cache.keySet().toArray();
            for (Object context : keys) {
                Object object = cache.getObject((K) context);
                cache.setObject((K) context, null);
                fireRegistrationChanged(new RegistrationEvent(this, RegistrationEvent.REGISTRATION_REMOVED, object, clazz, context));
            }
        }
        _cache.remove(clazz);
    }

    public void clear() {
        _cache.clear();
        fireRegistrationChanged(new RegistrationEvent(this, RegistrationEvent.REGISTRATION_CLEARED));
    }

    /**
     * List of listeners
     */
    protected List<RegistrationListener> listenerList = new ArrayList<RegistrationListener>();

    /**
     * Adds a listener to the list that's notified each time a change to the registration occurs.
     *
     * @param l the RegistrationListener
     */
    public void addRegistrationListener(RegistrationListener l) {
        listenerList.add(l);
    }

    /**
     * Removes a listener from the list that's notified each time a change to the registration occurs.
     *
     * @param l the RegistrationListener
     */
    public void removeRegistrationListener(RegistrationListener l) {
        listenerList.remove(l);
    }

    /**
     * Returns an array of all the registration listeners registered on this registration.
     *
     * @return all of this registration's <code>RegistrationListener</code>s or an empty array if no registration
     *         listeners are currently registered
     *
     * @see #addRegistrationListener
     * @see #removeRegistrationListener
     */
    public RegistrationListener[] getRegistrationListeners() {
        return listenerList.toArray(new RegistrationListener[listenerList.size()]);
    }

    /**
     * Forwards the given notification event to all <code>RegistrationListeners</code> that registered themselves as
     * listeners for this table model.
     *
     * @param e the event to be forwarded
     *
     * @see #addRegistrationListener
     * @see RegistrationEvent
     */
    public void fireRegistrationChanged(RegistrationEvent e) {
        // Guaranteed to return a non-null array
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listenerList.size() - 2; i >= 0; i -= 2) {
            listenerList.get(i + 1).registrationChanged(e);
        }
    }
}
