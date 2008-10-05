package com.jidesoft.grouper;

/**
 * Default implementation of <code>ObjectGrouper</code>. Its getGroupValue simply returns the value directly.
 */
public class DefaultObjectGrouper extends AbstractObjectGrouper {
    private String _name;

    public DefaultObjectGrouper() {
        this("");
    }

    public DefaultObjectGrouper(String name) {
        _name = name;
    }

    public Object getValue(Object value) {
        return value;
    }

    public Class<?> getType() {
        return Object.class;
    }

    public String getName() {
        return _name;
    }

    public void setName(String name) {
        _name = name;
    }
}
