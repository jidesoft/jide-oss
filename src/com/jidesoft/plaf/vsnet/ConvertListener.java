/*
 * @(#)ConvertListener.java
 *
 * Copyright 2002 JIDE Software Inc. All rights reserved.
 */
package com.jidesoft.plaf.vsnet;


/**
 * Convert multiple objects into one object. Used by ExtDesktopProperty.
 *
 * @see com.jidesoft.plaf.ExtWindowsDesktopProperty
 */
public interface ConvertListener {
    Object convert(Object[] obj);
}
