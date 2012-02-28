/*
 * @(#)VisibilityFocusTraversalPolicy.java 2/28/2012
 *
 * Copyright 2002 - 2012 JIDE Software Inc. All rights reserved.
 */

package com.jidesoft.swing;

import javax.swing.*;
import java.awt.*;

/**
 * The focus traversal policy to screen out those components that are not actually painted in the target container.
 *
 * @since 3.3.6
 */
public class VisibilityFocusTraversalPolicy extends FocusTraversalPolicy {
    private FocusTraversalPolicy _defaultPolicy;
    private Container _container;

    /**
     * The constructor.
     *
     * @param defaultPolicy the default FocusTraversalPolicy
     * @param container     the container to check the visibility of its child components
     */
    public VisibilityFocusTraversalPolicy(FocusTraversalPolicy defaultPolicy, Container container) {
        super();
        _defaultPolicy = defaultPolicy;
        _container = container;
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
        Rectangle collapsiblePaneBounds = _container.getBounds();
        Container parent = _container.getParent();
        while (component != null && _container.isAncestorOf(component) && aComponent != component) {
            Rectangle bounds = SwingUtilities.convertRectangle(component.getParent(), component.getBounds(), parent);
            if (bounds.x < collapsiblePaneBounds.x + collapsiblePaneBounds.width && bounds.x + bounds.width > collapsiblePaneBounds.x && bounds.y < collapsiblePaneBounds.y + collapsiblePaneBounds.height && bounds.y + bounds.height > collapsiblePaneBounds.y) {
                break;
            }
            component = _defaultPolicy.getComponentAfter(aContainer, component);
        }
        return component;
    }

    @Override
    public Component getComponentBefore(Container aContainer, Component aComponent) {
        if (_defaultPolicy == null) {
            return null;
        }
        Component component = _defaultPolicy.getComponentBefore(aContainer, aComponent);
        Rectangle collapsiblePaneBounds = _container.getBounds();
        Container parent = _container.getParent();
        while (component != null && _container.isAncestorOf(component) && aComponent != component) {
            Rectangle bounds = SwingUtilities.convertRectangle(component.getParent(), component.getBounds(), parent);
            if (bounds.x < collapsiblePaneBounds.x + collapsiblePaneBounds.width && bounds.x + bounds.width > collapsiblePaneBounds.x && bounds.y < collapsiblePaneBounds.y + collapsiblePaneBounds.height && bounds.y + bounds.height > collapsiblePaneBounds.y) {
                break;
            }
            component = _defaultPolicy.getComponentBefore(aContainer, component);
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
        Rectangle collapsiblePaneBounds = _container.getBounds();
        Container parent = _container.getParent();
        while (component != null && _container.isAncestorOf(component)) {
            Rectangle bounds = SwingUtilities.convertRectangle(component.getParent(), component.getBounds(), parent);
            if (bounds.x < collapsiblePaneBounds.x + collapsiblePaneBounds.width && bounds.x + bounds.width > collapsiblePaneBounds.x && bounds.y < collapsiblePaneBounds.y + collapsiblePaneBounds.height && bounds.y + bounds.height > collapsiblePaneBounds.y) {
                break;
            }
            component = _defaultPolicy.getComponentAfter(aContainer, component);
            if (aComponent == component) {
                break;
            }
        }
        return component;
    }

    @Override
    public Component getLastComponent(Container aContainer) {
        if (_defaultPolicy == null) {
            return null;
        }
        Component component = _defaultPolicy.getLastComponent(aContainer);
        Component aComponent = component;
        Rectangle collapsiblePaneBounds = _container.getBounds();
        Container parent = _container.getParent();
        while (component != null && _container.isAncestorOf(component)) {
            Rectangle bounds = SwingUtilities.convertRectangle(component.getParent(), component.getBounds(), parent);
            if (bounds.x < collapsiblePaneBounds.x + collapsiblePaneBounds.width && bounds.x + bounds.width > collapsiblePaneBounds.x && bounds.y < collapsiblePaneBounds.y + collapsiblePaneBounds.height && bounds.y + bounds.height > collapsiblePaneBounds.y) {
                break;
            }
            component = _defaultPolicy.getComponentBefore(aContainer, component);
            if (aComponent == component) {
                break;
            }
        }
        return component;
    }

    @Override
    public Component getDefaultComponent(Container aContainer) {
        if (_defaultPolicy == null) {
            return null;
        }
        Component component = _defaultPolicy.getDefaultComponent(aContainer);
        Component aComponent = component;
        Rectangle collapsiblePaneBounds = _container.getBounds();
        Container parent = _container.getParent();
        while (component != null && _container.isAncestorOf(component)) {
            Rectangle bounds = SwingUtilities.convertRectangle(component.getParent(), component.getBounds(), parent);
            if (bounds.x < collapsiblePaneBounds.x + collapsiblePaneBounds.width && bounds.x + bounds.width > collapsiblePaneBounds.x && bounds.y < collapsiblePaneBounds.y + collapsiblePaneBounds.height && bounds.y + bounds.height > collapsiblePaneBounds.y) {
                break;
            }
            component = _defaultPolicy.getComponentAfter(aContainer, component);
            if (aComponent == component) {
                break;
            }
        }
        return component;
    }

    @Override
    public Component getInitialComponent(Window window) {
        if (_defaultPolicy == null) {
            return null;
        }
        return _defaultPolicy.getInitialComponent(window);
    }
}
