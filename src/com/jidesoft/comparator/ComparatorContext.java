/*
 * @(#)ComparatorContext.java 5/13/2005
 *
 * Copyright 2002 - 2005 JIDE Software Inc. All rights reserved.
 */
package com.jidesoft.comparator;

import com.jidesoft.converter.AbstractContext;

/**
 * The context object used by <code>ObjectComparatorManager</code>. For the same type, we may need different way to
 * compare them. This context is used so that user can register different comparators for the same type.
 */
public class ComparatorContext extends AbstractContext {
    /**
     * Default comparator context with empty name and no user object.
     */
    public static final ComparatorContext DEFAULT_CONTEXT = new ComparatorContext("");

    /**
     * Creates a comparator context with a name.
     *
     * @param name the name of the comparator context.
     */
    public ComparatorContext(String name) {
        super(name);
    }

    /**
     * Creates a comparator context with a name and a user object.
     *
     * @param name   the name of the comparator context.
     * @param object the user object. It can be used as any object to pass informaton along.
     */
    public ComparatorContext(String name, Object object) {
        super(name, object);
    }
}
