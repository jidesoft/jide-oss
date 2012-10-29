/*
 * @(#)VisibilityFocusTraversalPolicy.java 2/28/2012
 *
 * Copyright 2002 - 2012 JIDE Software Inc. All rights reserved.
 */

package com.jidesoft.swing;

import com.jidesoft.utils.ReflectionUtils;

import javax.swing.*;
import java.util.HashSet;
import java.awt.*;
import java.util.Set;

/**
 * The focus traversal policy to screen out those components that are not actually painted in the target container.
 *
 * @since 3.3.6
 */
public class VisibilityFocusTraversalPolicy extends FocusTraversalPolicy {
    private FocusTraversalPolicy _defaultPolicy;
    private Set<Container> _containers;

    /**
     * The constructor.
     *
     * @param defaultPolicy the default FocusTraversalPolicy
     * @since 3.4.0
     */
    public VisibilityFocusTraversalPolicy(FocusTraversalPolicy defaultPolicy) {
        this(defaultPolicy, null);
    }

    /**
     * The constructor.
     *
     * @param defaultPolicy the default FocusTraversalPolicy
     * @param container     the container to check the visibility of its child components
     */
    public VisibilityFocusTraversalPolicy(FocusTraversalPolicy defaultPolicy, Container container) {
        super();
        if (defaultPolicy != null && ReflectionUtils.isSubClassOf(defaultPolicy, "LegacyGlueFocusTraversalPolicy")) {
            throw new IllegalArgumentException("VisibilityFocusTraversalPolicy can't work well with LegacyGlueFocusTraversalPolicy.");
        }
        _defaultPolicy = defaultPolicy;
        if (container != null) {
            _containers = new HashSet<Container>();
            _containers.add(container);
        }
    }

    /**
     * Adds the container that needs to check the visibility of its child component.
     *
     * @param container the container
     * @since 3.4.0
     */
    public void addContainer(Container container) {
        if (_containers == null) {
            _containers = new HashSet<Container>();
        }
        _containers.add(container);
    }

    /**
     * Removes the container that needs to check the visibility of its child component.
     *
     * @param container the container
     * @since 3.4.0
     */
    public void removeContainer(Container container) {
        if (_containers != null) {
            _containers.remove(container);
        }
    }

    /**
     * Gets all the containers that need to check the visibility of its child component.
     *
     * @return the container array.
     * @since 3.4.0
     */
    public Container[] getContainers() {
        if (_containers == null) {
            return new Container[0];
        }
        else {
            return _containers.toArray(new Container[_containers.size()]);
        }
    }

    /**
     * Gets the default FocusTraversalPolicy
     *
     * @return the default FocusTraversalPolicy.
     */
    public FocusTraversalPolicy getDefaultPolicy() {
        return _defaultPolicy;
    }

    @Override
    public Component getComponentAfter(Container aContainer, Component aComponent) {
        if (_defaultPolicy == null) {
            return null;
        }
        Component component = _defaultPolicy.getComponentAfter(aContainer, aComponent);
        boolean componentChanged = true;
        while (component != null && aComponent != component && componentChanged) {
            componentChanged = false;
            for (Container container : _containers) {
                if (container.isAncestorOf(component)) {
                    Rectangle collapsiblePaneBounds = container.getBounds();
                    Container parent = container.getParent();
                    Rectangle bounds = SwingUtilities.convertRectangle(component.getParent(), component.getBounds(), parent);
                    if (bounds.x < collapsiblePaneBounds.x + collapsiblePaneBounds.width && bounds.x + bounds.width > collapsiblePaneBounds.x && bounds.y < collapsiblePaneBounds.y + collapsiblePaneBounds.height && bounds.y + bounds.height > collapsiblePaneBounds.y) {
                        return component;
                    }
                    component = _defaultPolicy.getComponentAfter(aContainer, component);
                    componentChanged = true;
                    break;
                }
            }
        }
        return component;
    }

    @Override
    public Component getComponentBefore(Container aContainer, Component aComponent) {
        if (_defaultPolicy == null) {
            return null;
        }
        Component component = _defaultPolicy.getComponentBefore(aContainer, aComponent);
        boolean componentChanged = true;
        while (component != null && aComponent != component && componentChanged) {
            componentChanged = false;
            for (Container container : _containers) {
                if (container.isAncestorOf(component)) {
                    Rectangle collapsiblePaneBounds = container.getBounds();
                    Container parent = container.getParent();
                    Rectangle bounds = SwingUtilities.convertRectangle(component.getParent(), component.getBounds(), parent);
                    if (bounds.x < collapsiblePaneBounds.x + collapsiblePaneBounds.width && bounds.x + bounds.width > collapsiblePaneBounds.x && bounds.y < collapsiblePaneBounds.y + collapsiblePaneBounds.height && bounds.y + bounds.height > collapsiblePaneBounds.y) {
                        return component;
                    }
                    component = _defaultPolicy.getComponentBefore(aContainer, component);
                    componentChanged = true;
                    break;
                }
            }
        }
        return component;
    }

    @Override
    public Component getFirstComponent(Container aContainer) {
        if (_defaultPolicy == null) {
            return null;
        }
        Component component = _defaultPolicy.getFirstComponent(aContainer);
        Component aComponent = component;
        boolean componentChanged;
        do {
            componentChanged = false;
            for (Container container : _containers) {
                if (container.isAncestorOf(component)) {
                    Rectangle collapsiblePaneBounds = container.getBounds();
                    Container parent = container.getParent();
                    Rectangle bounds = SwingUtilities.convertRectangle(component.getParent(), component.getBounds(), parent);
                    if (bounds.x < collapsiblePaneBounds.x + collapsiblePaneBounds.width && bounds.x + bounds.width > collapsiblePaneBounds.x && bounds.y < collapsiblePaneBounds.y + collapsiblePaneBounds.height && bounds.y + bounds.height > collapsiblePaneBounds.y) {
                        return component;
                    }
                    component = _defaultPolicy.getComponentAfter(aContainer, component);
                    componentChanged = true;
                    break;
                }
            }
        }
        while (component != null && aComponent != component && componentChanged);
        return component;
    }

    @Override
    public Component getLastComponent(Container aContainer) {
        if (_defaultPolicy == null) {
            return null;
        }
        Component component = _defaultPolicy.getLastComponent(aContainer);
        Component aComponent = component;
        boolean componentChanged;
        do {
            componentChanged = false;
            for (Container container : _containers) {
                if (container.isAncestorOf(component)) {
                    Rectangle collapsiblePaneBounds = container.getBounds();
                    Container parent = container.getParent();
                    Rectangle bounds = SwingUtilities.convertRectangle(component.getParent(), component.getBounds(), parent);
                    if (bounds.x < collapsiblePaneBounds.x + collapsiblePaneBounds.width && bounds.x + bounds.width > collapsiblePaneBounds.x && bounds.y < collapsiblePaneBounds.y + collapsiblePaneBounds.height && bounds.y + bounds.height > collapsiblePaneBounds.y) {
                        return component;
                    }
                    component = _defaultPolicy.getComponentBefore(aContainer, component);
                    componentChanged = true;
                    break;
                }
            }
        }
        while (component != null && aComponent != component && componentChanged);
        return component;
    }

    @Override
    public Component getDefaultComponent(Container aContainer) {
        if (_defaultPolicy == null) {
            return null;
        }
        Component component = _defaultPolicy.getDefaultComponent(aContainer);
        Component aComponent = component;
        boolean old = _defaultPolicy instanceof SortingFocusTraversalPolicy && ((SortingFocusTraversalPolicy) _defaultPolicy).getImplicitDownCycleTraversal();
        if (_defaultPolicy instanceof SortingFocusTraversalPolicy) {
            ((SortingFocusTraversalPolicy) _defaultPolicy).setImplicitDownCycleTraversal(false);
        }
        try {
            boolean componentChanged;
            do {
                componentChanged = false;
                for (Container container : _containers) {
                    if (container.isAncestorOf(component)) {
                        Rectangle collapsiblePaneBounds = container.getBounds();
                        Container parent = container.getParent();
                        Rectangle bounds = SwingUtilities.convertRectangle(component.getParent(), component.getBounds(), parent);
                        if (bounds.x < collapsiblePaneBounds.x + collapsiblePaneBounds.width && bounds.x + bounds.width > collapsiblePaneBounds.x && bounds.y < collapsiblePaneBounds.y + collapsiblePaneBounds.height && bounds.y + bounds.height > collapsiblePaneBounds.y) {
                            return component;
                        }
                        component = _defaultPolicy.getComponentAfter(aContainer, component);
                        componentChanged = true;
                        break;
                    }
                }
            }
            while (component != null && aComponent != component && componentChanged);
            return component;
        }
        finally {
            if (_defaultPolicy instanceof SortingFocusTraversalPolicy) {
                ((SortingFocusTraversalPolicy) _defaultPolicy).setImplicitDownCycleTraversal(old);
            }
        }
    }

    @Override
    public Component getInitialComponent(Window window) {
        if (_defaultPolicy == null) {
            return null;
        }
        return _defaultPolicy.getInitialComponent(window);
    }
}
