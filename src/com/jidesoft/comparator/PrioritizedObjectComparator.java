package com.jidesoft.comparator;

import com.jidesoft.swing.Prioritized;

import java.util.Comparator;

/**
 * <code>Comparator</code> for objects that implements {@link com.jidesoft.swing.Prioritized}
 * interface. It is a singleton pattern. You use {@link #getInstance()} to get an instance.
 */
@SuppressWarnings({"RawUseOfParameterizedType"})
public class PrioritizedObjectComparator implements Comparator {
    private static PrioritizedObjectComparator singleton = null;

    protected PrioritizedObjectComparator() {
    }

    /**
     * Gets an instance of <code>PrioritizedObjectComparator</code>.
     *
     * @return an instance Cof <code>PrioritizedObjectComparator</code>.
     */
    public static PrioritizedObjectComparator getInstance() {
        if (singleton == null) {
            singleton = new PrioritizedObjectComparator();
        }
        return singleton;
    }

    public int compare(Object o1, Object o2) {
        int p1 = 0;
        if (o1 instanceof Prioritized) {
            p1 = ((Prioritized) o1).getPriority();
        }
        int p2 = 0;
        if (o2 instanceof Prioritized) {
            p2 = ((Prioritized) o2).getPriority();
        }
        return p1 - p2;
    }
}
