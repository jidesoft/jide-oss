/*
 * @(#) ConverterContext.java
 *
 * Copyright 2002 - 2003 JIDE Software. All rights reserved.
 */
package com.jidesoft.grouper;

import com.jidesoft.converter.AbstractContext;

/**
 * The context object used by ObjectGrouper. For the same type, we may need different way to group them. This context is
 * used so that user can register different groupers for the same type.
 */
public class GrouperContext extends AbstractContext {
    /**
     * Default converter context with empty name and no user object.
     */
    public static GrouperContext DEFAULT_CONTEXT = new GrouperContext("");

    /**
     * Creates a converter context with a name.
     *
     * @param name the name of the grouper context.
     */
    public GrouperContext(String name) {
        super(name);
    }

    /**
     * Creates a converter context with a name and an object.
     *
     * @param name   the name of the grouper context.
     * @param object the user object. It can be used as any object to pass information along.
     */
    public GrouperContext(String name, Object object) {
        super(name, object);
    }
}
