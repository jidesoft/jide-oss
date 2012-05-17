/*
 * @(#)CacheMap.java 3/20/2012
 *
 * Copyright 2002 - 2012 JIDE Software Inc. All rights reserved.
 */

package com.jidesoft.converter;

/**
 * This class was moved to com.jidesoft.utils since 3.3.7 release.
 *
 * @deprecated please use the one at com.jidesoft.utils.CacheMap.
 */
@Deprecated
public class CacheMap<T, K> extends com.jidesoft.utils.CacheMap<T, K> {
    public CacheMap(K defaultContext) {
        super(defaultContext);
    }
}
