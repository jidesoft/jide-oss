/*
 * @(#)BooleanUtils.java 5/27/2014
 *
 * Copyright 2002 - 2014 JIDE Software Inc. All rights reserved.
 */

package com.jidesoft.utils;

import com.jidesoft.range.BooleanRange;
import com.jidesoft.range.Range;

import java.util.List;

public class BooleanUtils {
    /**
     * Returns the min boolean in the booleans list.
     *
     * @param booleans the booleans to calculate the min.
     * @return the min boolean in the booleans list.
     */
    public static boolean min(List<Boolean> booleans) {
        for (boolean value : booleans) {
            if (!value) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns the max boolean in the booleans list.
     *
     * @param booleans the booleans to calculate the max.
     * @return the max boolean in the booleans list.
     */
    public static boolean max(List<Boolean> booleans) {
        for (boolean value : booleans) {
            if (value) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns the range of booleans.
     *
     * @param booleans the booleans to calculate the range.
     * @return the range of the booleans.
     */
    public static Range<Boolean> range(List<Boolean> booleans) {
        boolean min = true;
        boolean max = false;
        for (boolean value : booleans) {
            if (value) {
                max = true;
            }
            else {
                min = false;
            }
            if (max && !min) { // found
                break;
            }
        }
        return new BooleanRange(min, max);
    }
}
