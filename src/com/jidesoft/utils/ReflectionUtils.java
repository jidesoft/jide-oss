/*
 * @(#)ReflectionUtils.java 3/23/2011
 *
 * Copyright 2002 - 2011 JIDE Software Inc. All rights reserved.
 */

package com.jidesoft.utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/**
 * Utility class to use reflection to call methods.
 */
public class ReflectionUtils {
    /**
     * Helper method to call a set boolean method.
     *
     * @param thisObject the instance
     * @param methodName the method name
     * @param value      true or false
     *
     * @throws Exception if the method is not found or invocation to the method fails.
     */
    public static void callSetBoolean(Object thisObject, String methodName, boolean value) throws Exception {
        Class<?>[] argTypes = new Class<?>[]{boolean.class};
        Object[] args = new Object[]{value ? Boolean.TRUE : Boolean.FALSE};
        Method method = thisObject.getClass().getMethod(methodName, argTypes);
        method.invoke(thisObject, args);
    }

    /**
     * Helper method to call a set boolean method.
     *
     * @param thisObject the instance
     * @param methodName the method name
     * @param value      the value
     *
     * @throws Exception if the method is not found or invocation to the method fails.
     */
    public static void callSetInt(Object thisObject, String methodName, int value) throws Exception {
        Class<?>[] argTypes = new Class<?>[]{int.class};
        Object[] args = new Object[]{value};
        Method method = thisObject.getClass().getMethod(methodName, argTypes);
        method.invoke(thisObject, args);
    }

    /**
     * Helper method to call a set boolean method.
     *
     * @param thisObject the instance
     * @param methodName the method name
     * @param value      the value
     *
     * @throws Exception if the method is not found or invocation to the method fails.
     */
    public static void callSet(Object thisObject, String methodName, Object value)
            throws Exception {
        Class<?>[] argTypes = new Class<?>[]{value.getClass()};
        Object[] args = new Object[]{value};
        Method method = thisObject.getClass().getMethod(methodName, argTypes);
        method.invoke(thisObject, args);
    }

    /**
     * Helper method to call a get method with no argument.
     *
     * @param thisObject the instance
     * @param methodName the method name
     *
     * @return the value the method returns.
     *
     * @throws Exception if the method is not found or invocation to the method fails.
     */
    public static Object callGet(Object thisObject, String methodName) throws Exception {
        Method method = thisObject.getClass().getMethod(methodName, (Class[]) null);
        return method.invoke(thisObject, (Object[]) null);
    }

    /**
     * Helper method to call a no argument, no return method.
     *
     * @param thisObject the instance
     * @param methodName the method name
     *
     * @throws Exception if the method is not found or invocation to the method fails.
     */
    public static void call(Object thisObject, String methodName) throws Exception {
        Method method = thisObject.getClass().getMethod(methodName, (Class[]) null);
        method.invoke(thisObject, (Object[]) null);
    }

    /**
     * Helper method to call a constructor.
     *
     * @param clazz    the class
     * @param argTypes argument Classes for constructor lookup. Must not be null.
     * @param args     the argument array
     *
     * @return the value the method returns.
     *
     * @throws Exception if the method is not found or invocation to the method fails.
     */
    public static Object callConstructor(Class<?> clazz, Class<?>[] argTypes, Object[] args) throws Exception {
        Constructor constructor = clazz.getConstructor(argTypes);
        return constructor.newInstance(args);
    }

    /**
     * Helper method to call a multi-argument method having a return. The class types will be derived from the input
     * values. This call is usually successful with primitive types or Strings as arguments, but care should be used
     * with other kinds of values. The constructor lookup is not polymorphic.
     * <p/>
     * Calls <code>callAny(Object, methodName, argTypes, args)</code>.
     *
     * @param thisObject the instance
     * @param methodName the method name
     * @param args       the argument array, must not contain null.
     *
     * @return the value the method returns.
     *
     * @throws Exception if the method is not found or invocation to the method fails.
     */
    public static Object callAny(Object thisObject, String methodName, Object[] args) throws Exception {
        Class<?>[] argTypes = null;
        if (args != null) {
            argTypes = new Class[args.length];
            for (int i = 0; i < args.length; i++) {
                argTypes[i] = args[i].getClass();
            }
        }
        return callAny(thisObject, methodName, argTypes, args);
    }

    /**
     * Helper method to call a multi-argument method having a return.
     *
     * @param thisObject the instance
     * @param methodName the method name
     * @param argTypes   argument Classes for constructor lookup. Must not be null.
     * @param args       the argument array
     *
     * @return the value the method returns.
     *
     * @throws Exception if the method is not found or invocation to the method fails.
     */
    public static Object callAny(Object thisObject, String methodName, Class<?>[] argTypes, Object[] args) throws Exception {
        Method method = thisObject.getClass().getMethod(methodName, argTypes);
        return method.invoke(thisObject, args);
    }

    /**
     * Helper method to call a multi-argument method having a return without throwing an exception.
     *
     * @param thisObject  the instance
     * @param objectClass the class which could find the method name
     * @param methodName  the method name
     * @param argTypes    argument Classes for constructor lookup. Must not be null.
     * @param args        the argument array
     *
     * @return the value  the method returns.
     */
    public static Object callAnyWithoutException(Object thisObject, Class<?> objectClass, String methodName, Class<?>[] argTypes, Object[] args) {
        Method m;
        try {
            m = objectClass.getDeclaredMethod(methodName, argTypes);
        }
        catch (Exception e) {
            m = null;
        }
        if (m != null) {
            try {
                m.setAccessible(true);
                return m.invoke(thisObject, args);
            }
            catch (Exception ignored) {
                return null;
            }
            finally {
                m.setAccessible(false);
            }
        }
        return null;
    }

    /**
     * Helper method to call a multi-argument static class method having a return.
     *
     * @param thisClass  the class
     * @param methodName the method name
     * @param argTypes   argument Classes for constructor lookup. Must not be null.
     * @param args       the argument array
     *
     * @return the value the method returns.
     *
     * @throws Exception if the method is not found or invocation to the method fails.
     */
    public static Object callStatic(Class<?> thisClass, String methodName, Class<?>[] argTypes, Object[] args) throws Exception {
        Method method = thisClass.getMethod(methodName, argTypes);
        Object result = null;
        if (method != null) {
            try {
                method.setAccessible(true);
                result = method.invoke(null, args);
            }
            finally {
                method.setAccessible(false);
            }
        }
        return result;
    }

    /**
     * Instantiate an object based on the class name.
     *
     * @param className the class name
     * @param types     the class types for the constructor
     * @param args      the constructor values
     *
     * @return the object instance. null if any exception occurs.
     */
    public static Object createInstance(String className, Class<?>[] types, Object[] args) {
        Object instantiation = null;
        // try default class
        try {
            Class<?> cls = Class.forName(className);
            if (types != null && types.length != 0) {
                Constructor<?> constructor = cls.getConstructor(types);
                instantiation = constructor.newInstance(args);
            }
            else {
                instantiation = cls.newInstance();
            }
        }
        catch (Exception e) {
            // null
        }

        return instantiation;
    }

    /**
     * Checks if the instance o is an instance of a subclass of the designated class name.
     *
     * @param o         the instance to check
     * @param className the class name to check
     *
     * @return true if the instance o is an instance of a subclass of the class name. Otherwise false.
     *
     * @since 3.4.9
     */
    public static boolean isSubClassOf(Object o, String className) {
        if (o == null) {
            return false;
        }
        Class<?> aClass = o.getClass();
        while (aClass != null) {
            if (aClass.getName().contains(className)) {
                return true;
            }
            aClass = aClass.getSuperclass();
        }
        return false;
    }
}
