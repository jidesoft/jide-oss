/*
 * @(#)XmlUtils.java 5/8/2005
 *
 * Copyright 2002 - 2005 JIDE Software Inc. All rights reserved.
 */
package com.jidesoft.converter;

import com.jidesoft.swing.JideSwingUtilities;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 */
public class XmlUtils {

    private static final Pattern mutatorPattern =
            Pattern.compile("^set([A-Z0-9_][A-Za-z0-9_]*)$");

    private static final int MUTATOR = 2;
    private static final int ANYOTHER = 0;

    public static void readElement(Object object, Element element) {
        if (object == null) {
            return;
        }

        NamedNodeMap map = element.getAttributes();
        HashMap<String, String> properties = new HashMap<String, String>();
        for (int i = 0; i < map.getLength(); i++) {
            Node node = map.item(i);
            String name = node.getNodeName();
            properties.put(name, node.getNodeValue());
        }

        Method[] methods = object.getClass().getMethods();
        for (Method method : methods) {
            Matcher matcher;
            int methodType = ANYOTHER;
            Class<?> type = null;

            if (!Modifier.isPublic(method.getModifiers()) || Modifier.isStatic(method.getModifiers())) {
                continue;
            }

            if ((matcher = mutatorPattern.matcher(method.getName())).matches()) {
                if (method.getReturnType() == void.class && method.getParameterTypes().length == 1) {
                    methodType = MUTATOR;
                    type = method.getParameterTypes()[0];
                }
            }

            if (methodType == MUTATOR) {
                String name = matcher.group(1);
                if (name.equals("Class")) // don't use getClass()
                    continue;
                name = name.substring(0, 1).toLowerCase() + name.substring(1);
                Object value = properties.get(name);
                if (value == null) {
                    continue;
                }

                try {
                    method.invoke(object, ObjectConverterManager.fromString((String) value, type));
                }
                catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
                catch (InvocationTargetException e) {
                    JideSwingUtilities.ignoreException(e);
                }
            }
        }
    }
}
