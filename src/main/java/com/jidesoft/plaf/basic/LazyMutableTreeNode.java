/*
 * @(#)LazyMutableTreeNode.java 10/9/2005
 *
 * Copyright 2002 - 2005 JIDE Software Inc. All rights reserved.
 */
package com.jidesoft.plaf.basic;

import javax.swing.tree.DefaultMutableTreeNode;

/**
 * LazyMutableTreeNode. <br>
 */
public abstract class LazyMutableTreeNode extends DefaultMutableTreeNode {

    protected boolean _loaded = false;

    public LazyMutableTreeNode() {
        super();
    }

    public LazyMutableTreeNode(Object userObject) {
        super(userObject);
    }

    public LazyMutableTreeNode(Object userObject, boolean allowsChildren) {
        super(userObject, allowsChildren);
    }

    @Override
    public int getChildCount() {
        synchronized (this) {
            if (!_loaded) {
                _loaded = true;
                initChildren();
            }
        }
        return super.getChildCount();
    }

    public void clear() {
        removeAllChildren();
        _loaded = false;
    }

    public boolean isLoaded() {
        return _loaded;
    }

    protected abstract void initChildren();

}
