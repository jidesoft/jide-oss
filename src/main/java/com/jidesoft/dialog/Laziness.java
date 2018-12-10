/*
 * @(#)Laziness.java
 *
 * Copyright 2002 JIDE Software Inc. All rights reserved.
 */
package com.jidesoft.dialog;

/**
 * An interface to indicate something can be initialized lazily.
 */
public interface Laziness {

    /**
     * This method must be implemented by any child class. Instead of putting
     * initialization code in constructor, user should put code in this method
     * in order to taking advantage of lazy loading.
     */
    abstract void lazyInitialize();
}
