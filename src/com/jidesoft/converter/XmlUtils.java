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

    private final static int MUTATOR = 2;
    private final static int ANYOTHER = 0;

    public static void readElement(Object object, Element element) {
        if (object == null) {
            return;
        }

        NamedNodeMap map = element.getAttributes();
        HashMap properties = new HashMap();
        for (int i = 0; i < map.getLength(); i++) {
            Node node = map.item(i);
            String name = node.getNodeName();
            properties.put(name, node.getNodeValue());
        }

        Method[] methods = object.getClass().getMethods();
        for (int i = 0; i < methods.length; i++) {
            Matcher matcher;
            int methodType = ANYOTHER;
            Class type = null;

            if (!Modifier.isPublic(methods[i].getModifiers()) || Modifier.isStatic(methods[i].getModifiers())) {
                continue;
            }

            if ((matcher = mutatorPattern.matcher(methods[i].getName())).matches()) {
                if (methods[i].getReturnType() == void.class && methods[i].getParameterTypes().length == 1) {
                    methodType = MUTATOR;
                    type = methods[i].getParameterTypes()[0];
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
                    methods[i].invoke(object, new Object[]{ObjectConverterManager.fromString((String) value, type)});
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
