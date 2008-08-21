package com.jidesoft.grouper;

import com.jidesoft.comparator.ComparatorContext;
import com.jidesoft.converter.ConverterContext;

/**
 * An interface that can convert a object to a group so that the objects that has the same group can be grouped
 * together. We suggest you extends {@link com.jidesoft.grouper.AbstractObjectGrouper} if you want to create your own
 * ObjectGrouper in case we add new methods to this interface due to requirement changes.
 */
public interface ObjectGrouper {
    /**
     * Gets the group value after this value is grouped. If two objects return the same value in this getGroupValue
     * method, the two objects are considered as one group. We assume all values returned from this method are of the
     * same type which is returned in {@link #getType()}.
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
    Class<?> getType();

    /**
     * Gets the name of this object grouper.
     *
     * @return the name of this grouper.
     */
    String getName();

    /**
     * Gets the converter context for the value returned from this object grouper. This converter context will be used
     * to find the ObjectConverter that will convert the value returned from {@link #getValue(Object)} method to String
     * so that it can be displayed somewhere.
     *
     * @return the converter context.
     */
    ConverterContext getConverterContext();

    /**
     * Gets the comparator context for the value returned from this object grouper. This comparator context will be used
     * to find the ObjectComparator that will sort the values return from {@link #getValue(Object)} method whenever
     * sorting is needed.
     *
     * @return the converter context.
     */
    ComparatorContext getComparatorContext();
}
