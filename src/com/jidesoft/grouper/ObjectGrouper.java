package com.jidesoft.grouper;

/**
 * An interface that can convert a object to a group so that the objects that has the same group can be grouped together.
 */
public interface ObjectGrouper {
    /**
     * Gets the group value after this value is grouped. If two objects return the same value
     * in this getGroupValue method, the two objects are considered as one group.
     * We assume all values returned from this method are of the same type which is returned in {@link #getType()}.
     *
     * @param value the value
     * @return the value after grouped.
     */
    Object getValue(Object value);

    /**
     * Gets the group value type. It should be the type of the value that is returned from the getGroupValue.
     *
     * @return the group value type.
     */
    Class getType();

    /**
     * Gets the name of this object grouper.
     *
     * @return the name of this grouper.
     */
    String getName();
}
