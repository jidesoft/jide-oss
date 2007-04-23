package com.jidesoft.grouper;

/**
 * Default implenmentation of <code>ObjectGrouper</code>.
 * Its getGroupValue simply returns the value directly.
 */
public class DefaultObjectGrouper implements ObjectGrouper {
    public Object getValue(Object value) {
        return value;
    }

    public Class getType() {
        return Object.class;
    }

    public String getName() {
        return "";
    }
}
