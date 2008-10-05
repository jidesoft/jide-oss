package com.jidesoft.grouper;

import com.jidesoft.comparator.ComparatorContext;
import com.jidesoft.converter.ConverterContext;

/**
 * The abstract implementation of <code>ObjectGrouper</code>. It just implements the {@link #getConverterContext()} and
 * {@link #getComparatorContext()} methods.
 */
abstract public class AbstractObjectGrouper implements ObjectGrouper {
    public ConverterContext getConverterContext() {
        return null;
    }

    public ComparatorContext getComparatorContext() {
        return null;
    }
}
