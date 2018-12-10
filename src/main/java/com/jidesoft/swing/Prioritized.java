package com.jidesoft.swing;

/**
 * An interface to indicate something that has priority. The priority is just an int value. Although it is up to
 * developer to decide which priority value is higher than the other, we suggest the higher value, the higher priority
 * with 0 means default priority when priority is not specified.
 */
public interface Prioritized {
    /**
     * Gets the priority. The value could be from Integer.MIN_VALUE to Integer.MAX_VALUE with 0 as the default
     * priority.
     *
     * @return the priority
     */
    int getPriority();
}
