/*
 * @(#)Positionable.java
 * 
 * 2002 - 2012 JIDE Software Incorporated. All rights reserved.
 * Copyright (c) 2005 - 2012 Catalysoft Limited. All rights reserved.
 */

package com.jidesoft.range;

/**
 * The idea of being positionable is to place a point somewhere along a one-dimensional axis
 * This is very natural for numerical data, but can be applied to categorical data too.
 * @author Simon White (swhite@catalysoft.com)
 */
public interface Positionable extends Comparable<Positionable> {
    /**
     * Map the <code>Positionable</code> object to a numeric value (a double) so that it can be positioned on an axis.
     * @return the position of the object
     */
	double position();
}
