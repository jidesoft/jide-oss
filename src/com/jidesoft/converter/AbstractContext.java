/*
 * @(#) AbstractContext.java
 *
 * Copyright 2002 - 2003 JIDE Software. All rights reserved.
 */
package com.jidesoft.converter;

import java.io.Serializable;

/**
 * AbstractContext is a generic context class. It has two fields: name and userObject.
 * Name is just the name of the context. You can use some meanful string to name it.
 * userObject is customized portion of Context. You can set whatever you want as userObject.
 * It's just a convention between whoever set it and whoever use it.
 */
abstract public class AbstractContext implements Serializable {
    private String _name;

    private Object _userObject;

    /**
     * Creates an abstract context with a name.
     *
     * @param name
     */
    public AbstractContext(String name) {
        _name = name;
    }

    /**
     * Creates an abstract contex with a name and an object.
     *
     * @param name
     * @param object the user object. It can be used any object to pass informaton along.
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
     * @param userObject
     */
    public void setUserObject(Object userObject) {
        _userObject = userObject;
    }

    /**
     * Override equals. Two abstract context equals as long as the name is the same.
     *
     * @param o
     * @return if two objects euqnals.
     */
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AbstractContext)) return false;

        final AbstractContext abstractContext = (AbstractContext) o;

        return !(_name != null ? !_name.equals(abstractContext._name) : abstractContext._name != null);
    }

    public int hashCode() {
        return (_name != null ? _name.hashCode() : 0);
    }

    public String toString() {
        return getName();
    }
}
