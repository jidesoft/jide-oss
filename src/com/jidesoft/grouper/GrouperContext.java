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

    private boolean _useOnAutoFilter = true;
    private boolean _useOnCustomFilter = true;

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


    /**
     * Checks if the grouper will be used on the auto-filter drop down list.
     *
     * @return true or false.
     * @since 3.5.14
     */
    public boolean isUseOnAutoFilter() {
        return _useOnAutoFilter;
    }

    /**
     * Sets the flag to tell the auto-filter drop down list to use the grouper or not.
     *
     * @param useOnAutoFilter true or false. Default is true.
     * @since 3.5.14
     */
    public void setUseOnAutoFilter(boolean useOnAutoFilter) {
        _useOnAutoFilter = useOnAutoFilter;
    }

    /**
     * Checks if the grouper will be used on the custom filter editor.
     *
     * @return true or false.
     * @since 3.5.14
     */
    public boolean isUseOnCustomFilter() {
        return _useOnCustomFilter;
    }

    /**
     * Sets the flag to tell the custom filter editor to use the grouper or not.
     *
     * @param useOnCustomFilter true or false. Default is true.
     * @since 3.5.14
     */
    public void setUseOnCustomFilter(boolean useOnCustomFilter) {
        _useOnCustomFilter = useOnCustomFilter;
    }
}
