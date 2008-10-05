/*
 * @(#) AbstractContext.java
 *
 * Copyright 2002 - 2003 JIDE Software. All rights reserved.
 */
package com.jidesoft.converter;

import java.io.Serializable;

/**
 * <code>AbstractContext</code> is a generic context class. It has two fields: name and userObject. The name is just the
 * name of the context. You can use a meaningful string to name it. The userObject is customizable portion of Context.
 * You can set whatever you want as userObject. It's just a convention between whoever set it and whoever use it. For
 * example, in <code>ConverterContext</code>, we sometimes used it to pass in a <code>Format</code>.
 */
abstract public class AbstractContext implements Serializable {

    private String _name;

    private Object _userObject;

    /**
     * Creates a named <code>AbstractContext</code>.
     *
     * @param name the name of the <code>AbstractContext</code>.
     */
    public AbstractContext(String name) {
        _name = name;
    }

    /**
     * Creates an abstract context with a name and an object.
     *
     * @param name   the name of the <code>AbstractContext</code>.
     * @param object the user object. It can be used any object to pass information along.
     */
    public AbstractContext(String name, Object object) {
        _name = name;
        _userObject = object;
    }

    /**
     * Gets the name of the abstract context.
     *
     * @return the name of the abstract context
     */
    public String getName() {
        return _name;
    }

    /**
     * Sets the name of the abstract context.
     *
     * @param name the name of the abstract context
     */
    public void setName(String name) {
        _name = name;
    }

    /**
     * Gets the user object.
     *
     * @return the user object
     */
    public Object getUserObject() {
        return _userObject;
    }

    /**
     * Sets the user object.
     *
     * @param userObject the user object.
     */
    public void setUserObject(Object userObject) {
        _userObject = userObject;
    }

    /**
     * Override equals. Two abstract context equals as long as the name is the same.
     *
     * @param o object to compare.
     * @return if two objects equal.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AbstractContext)) return false;

        final AbstractContext abstractContext = (AbstractContext) o;

        return !(_name != null ? !_name.equals(abstractContext._name) : abstractContext._name != null);
    }

    @Override
    public int hashCode() {
        return (_name != null ? _name.hashCode() : 0);
    }

    @Override
    public String toString() {
        return getName();
    }
}
