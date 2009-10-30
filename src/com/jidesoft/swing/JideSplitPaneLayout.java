/*
 * @(#)JideSplitPaneLayout.java 7/7/2009
 *
 * Copyright 2002 - 2009 JIDE Software Inc. All rights reserved.
 *
 */

package com.jidesoft.swing;

import com.jidesoft.utils.SystemInfo;

import javax.swing.*;
import java.awt.*;
import java.util.*;

/**
     * Layout manager used by JideSplitPane.
     */
public class JideSplitPaneLayout extends JideBoxLayout {
    private static final long serialVersionUID = -1826651835409198865L;

    public JideSplitPaneLayout(Container target) {
        super(target);
        setResetWhenInvalidate(false);
    }

    public JideSplitPaneLayout(Container target, int axis) {
        super(target, axis);
        setResetWhenInvalidate(false);
    }

    public JideSplitPaneLayout(Container target, int axis, int gap) {
        super(target, axis, gap);
        setResetWhenInvalidate(false);
    }

    int getDividerLocation(int index) {
        if (_componentSizes == null) {
            return -1;
        }
        if (index < 0 || (index + 1) << 1 >= _componentSizes.length)
            return -1;

        boolean ltr = _target.getComponentOrientation().isLeftToRight();
        boolean reversed = !ltr && ((JideSplitPane) _target).getOrientation() == JideSplitPane.HORIZONTAL_SPLIT;

        int location = 0;
        if (reversed) {
            for (int i = _componentSizes.length - 1; i >= (index * 2) + 1; i--)
                location += _componentSizes[i];
        }
        else {
            for (int i = 0; i < (index * 2) + 1; i++)
                location += _componentSizes[i];
        }

        Insets insets = _target.getInsets();
        if (insets != null) {
            if (((JideSplitPane) _target).getOrientation() == JideSplitPane.HORIZONTAL_SPLIT) {
                location += reversed ? insets.right : insets.left;
            }
            else {
                location += insets.top;
            }
        }
        return location;
    }

    @SuppressWarnings({"RedundantCast"})
    int setDividerLocation(int index, int location, boolean isOriginator) {
        int oldLocation = getDividerLocation(index);
        if (oldLocation == -1 || oldLocation == location)
            return -1;
        boolean ltr = _target.getComponentOrientation().isLeftToRight();
        boolean reversed = !ltr && ((JideSplitPane) _target).getOrientation() == JideSplitPane.HORIZONTAL_SPLIT;
        int prevIndex = reversed ? 2 * index + 2 : 2 * index;
        int nextIndex = reversed ? 2 * index : 2 * index + 2;
        int nextDividerIndex = reversed ? index - 1 : index + 1;
        int prevDividerIndex = reversed ? index + 1 : index - 1;
        int flexibleNextIndex;
        int flexiblePrevIndex;
        java.util.List<Integer> componentIndexChanged = new ArrayList<Integer>();

        if (reversed) {
            while (nextIndex >= 0 && !isPaneVisible(nextIndex)) {
                nextIndex -= 2;
                nextDividerIndex --;
            }

            while (prevIndex < _componentSizes.length && !isPaneVisible(prevIndex)) {
                prevIndex += 2;
                prevDividerIndex ++;
            }

            flexibleNextIndex = nextIndex;
            while (flexibleNextIndex >= 0 &&
                    (getConstraintMap().get(_target.getComponent(flexibleNextIndex)) == JideBoxLayout.FIX || !isPaneVisible(flexibleNextIndex))) {
                flexibleNextIndex -= 2;
            }
            if (flexibleNextIndex < 0) {
                return -1;
            }

            flexiblePrevIndex = prevIndex;
            while (flexiblePrevIndex < _componentSizes.length &&
                    (getConstraintMap().get(_target.getComponent(flexiblePrevIndex)) == JideBoxLayout.FIX || !isPaneVisible(flexiblePrevIndex))) {
                flexiblePrevIndex += 2;
            }
            if (flexiblePrevIndex >= _componentSizes.length) {
                return -1;
            }
        }
        else {
            while (nextIndex < _componentSizes.length && !isPaneVisible(nextIndex)) {
                nextIndex += 2;
                nextDividerIndex ++;
            }

            while (prevIndex >= 0 && !isPaneVisible(prevIndex)) {
                prevIndex -= 2;
                prevDividerIndex --;
            }

            flexibleNextIndex = nextIndex;
            while (flexibleNextIndex < _componentSizes.length &&
                    (getConstraintMap().get(_target.getComponent(flexibleNextIndex)) == JideBoxLayout.FIX || !isPaneVisible(flexibleNextIndex))) {
                flexibleNextIndex += 2;
            }
            if (flexibleNextIndex >= _componentSizes.length) {
                return -1;
            }

            flexiblePrevIndex = prevIndex;
            while (flexiblePrevIndex >= 0 &&
                    (getConstraintMap().get(_target.getComponent(flexiblePrevIndex)) == JideBoxLayout.FIX || !isPaneVisible(flexiblePrevIndex))) {
                flexiblePrevIndex -= 2;
            }
            if (flexiblePrevIndex < 0) {
                return -1;
            }
        }

        if (isOriginator
                && getConstraintMap().get(_target.getComponent(nextIndex)) == JideBoxLayout.FIX
                && getConstraintMap().get(_target.getComponent(prevIndex)) == JideBoxLayout.FIX) {
            return -1;
        }

        if (location > oldLocation) {
            int size = _componentSizes[2 * index + 1];
            if (getConstraintMap().get(_target.getComponent(nextIndex)) == JideBoxLayout.FIX) {
                size += _componentSizes[nextIndex];
            }
            else {
                if (((JideSplitPane) _target).getOrientation() == JideSplitPane.HORIZONTAL_SPLIT) {
                    size += _target.getComponent(nextIndex).getMinimumSize().getWidth();
                }
                else {
                    size += _target.getComponent(nextIndex).getMinimumSize().getHeight();
                }
            }

            int nextDividerLocation = getDividerLocation(nextDividerIndex);
            if (nextDividerLocation < 0) {
                if (((JideSplitPane) _target).getOrientation() == JideSplitPane.HORIZONTAL_SPLIT) {
                    location = Math.min(location, _target.getWidth() - size);
                }
                else {
                    location = Math.min(location, _target.getHeight() - size);
                }
            }
            else if (location + size > nextDividerLocation) {
                int actualLocation = setDividerLocation(nextDividerIndex, location + size, false);
                if (actualLocation == -1) {
                    return -1;
                }
                location = actualLocation - size;
            }
            if (getConstraintMap().get(_target.getComponent(nextIndex)) != JideBoxLayout.FIX) {
                _componentSizes[nextIndex] -= location - oldLocation;
                componentIndexChanged.add(nextIndex);
            }
            if (isOriginator) {
                _componentSizes[flexiblePrevIndex] += location - oldLocation;
                componentIndexChanged.add(flexiblePrevIndex);
            }
            else if (getConstraintMap().get(_target.getComponent(prevIndex)) != JideBoxLayout.FIX) {
                _componentSizes[prevIndex] += location - oldLocation;
                componentIndexChanged.add(prevIndex);
            }
        }
        else if (location < oldLocation) {
            int size = 0;
            if (prevDividerIndex >= 0) {
                size = _componentSizes[prevIndex - 1];
            }
            if (getConstraintMap().get(_target.getComponent(prevIndex)) == JideBoxLayout.FIX) {
                size += _componentSizes[prevIndex];
            }
            else {
                if (((JideSplitPane) _target).getOrientation() == JideSplitPane.HORIZONTAL_SPLIT) {
                    size += _target.getComponent(prevIndex).getMinimumSize().getWidth();
                }
                else {
                    size += _target.getComponent(prevIndex).getMinimumSize().getHeight();
                }
            }

            int prevDividerLocation = getDividerLocation(prevDividerIndex);
            if (prevDividerLocation < 0) {
                location = Math.max(location, size);
            }
            else if (location - size < prevDividerLocation) {
                int actualLocation = setDividerLocation(prevDividerIndex, location - size, false);
                if (actualLocation == -1) {
                    return -1;
                }
                location = actualLocation + size;
            }
            if (getConstraintMap().get(_target.getComponent(prevIndex)) != JideBoxLayout.FIX) {
                _componentSizes[prevIndex] -= oldLocation - location;
                componentIndexChanged.add(prevIndex);
            }
            if (isOriginator) {
                _componentSizes[flexibleNextIndex] += oldLocation - location;
                componentIndexChanged.add(flexibleNextIndex);
            }
            else if (getConstraintMap().get(_target.getComponent(nextIndex)) != JideBoxLayout.FIX) {
                _componentSizes[nextIndex] += oldLocation - location;
                componentIndexChanged.add(nextIndex);
            }
        }

        if (SystemInfo.isJdk15Above()) {
            if (_target instanceof JideSplitPane) {
                ((JideSplitPane) _target).firePropertyChange(JideSplitPane.PROPERTY_DIVIDER_LOCATION, oldLocation, location);
            }
            else {
                _target.firePropertyChange(JideSplitPane.PROPERTY_DIVIDER_LOCATION, oldLocation, location);
            }
        }
        ((JideSplitPane) _target).revalidate();

        if (((JideSplitPane) _target).isProportionalLayout()) {
            replaceProportions();
            return location;
        }

        if (((JideSplitPane) _target).getOrientation() == JideSplitPane.HORIZONTAL_SPLIT) {
            for (int changedIndex : componentIndexChanged) {
                Component component = _target.getComponent(changedIndex);
                if (component instanceof JComponent) {
                    ((JComponent) component).setPreferredSize(new Dimension(_componentSizes[changedIndex], component.getPreferredSize().height));
                }
            }
        }
        else {
            for (int changedIndex : componentIndexChanged) {
                Component component = _target.getComponent(changedIndex);
                if (component instanceof JComponent) {
                    ((JComponent) component).setPreferredSize(new Dimension(component.getPreferredSize().width, _componentSizes[changedIndex]));
                }
            }
        }
        return location;
    }

    private boolean isPaneVisible(int index) {
        // Considering collapse/expand feature, we need consider the visibility of divider as well
        return index < _componentSizes.length && (_componentSizes[index] != 0 || index - 1 < 0 || _componentSizes[index - 1] != 0);
    }

    /**
     * Uses the component sizes to generate a new array of proportions, and replaces the existing one.
     */
    private void replaceProportions() {
        ((JideSplitPane) _target).setProportions(deduceProportions());
    }

    /**
     * Uses the component sizes to generate a new array of proportions.
     *
     * @return a new array of proportions
     */
    private double[] deduceProportions() {
        double total = 0.0; // Total height or width of contained panes (not including dividers)
        for (int i = 0; i < _componentSizes.length; i += 2) // Note we're skipping dividers
            total += _componentSizes[i];
        double[] newProportions;
        if (total == 0.0)
            newProportions = null;
        else {
            newProportions = new double[(_componentSizes.length - 1) / 2];
            for (int i = 0; i < newProportions.length; ++i)
                newProportions[i] = _componentSizes[i * 2] / total;
        }
        return newProportions;
    }

    // For proportional layouts, override the key box layout method:
    @Override
    protected boolean calculateComponentSizes(int availableSize, int startIndex, int endIndex) {
        // Just go to super for non-proportional layout, or if there are no more
        // than one component.
        if (!((JideSplitPane) _target).isProportionalLayout()
                || _target.getComponentCount() <= 1)
            return super.calculateComponentSizes(availableSize, startIndex, endIndex);

        // If we have no set proportions, either call super to get initial
        // proportions or set them up as even.
        if (((JideSplitPane) _target).getProportions() == null) {
            if (!((JideSplitPane) _target).isInitiallyEven()) {
                if (!super.calculateComponentSizes(availableSize, startIndex, endIndex))
                    return false;
//<syd_0033> Luke's change
//   David: We now wait to set proportions until the user sets them directly.  Otherwise recreated
//   docked frames end up with tiny sizes instead of using preferred size, since they are
//   initially added to their ContainerContainers without their contents.
//                    _proportions = deduceProportions(); // Note no call to setProportions: no event
//</syd_0033>
                return true;
            }
            // What remains is the "initially even" logic:
            int c = ((JideSplitPane) _target).getPaneCount();
            double[] p = new double[c - 1];
            for (int i = 0; i < p.length; ++i)
                p[i] = 1.0 / c;
            ((JideSplitPane) _target).internalSetProportions(p); // Note no call to setProportions: no event may be sent here
        }

        // Spin through the dividers, setting up their sizes and subtracting from available.
        for (int i = 1; i < _target.getComponentCount(); i += 2) {
            if (_target.getComponent(i).isVisible())
                _componentSizes[i] = ((JideSplitPane) _target).getDividerSize();
            availableSize -= _componentSizes[i];
        }

        if (availableSize < 0)
            return false;

        // Then spin through the panes and set their sizes according to the
        // proportions.
        double[] proportions = ((JideSplitPane) _target).getProportions();
//            double last = 1.0;
//            for (double p : proportions) last -= p;
//            double total = 0.0;
//            for (int i = 0; i < getComponentCount(); i += 2) {
//                int j = i / 2;
//                if (getComponent(i).isVisible())
//                    total += (j < proportions.length) ? proportions[j] : last;
//            }
        int size = availableSize;
        for (int i = 0; i < proportions.length; ++i) {
            int j = i * 2;
            if (_target.getComponent(j).isVisible()) {
                double d = proportions[i]/* / total*/;
                if (d <= 1.0)
                    _componentSizes[j] = (int) (0.5 + size * d);
            }
            availableSize -= _componentSizes[j];
        }
        // Now set the last one to whatever is left over.
        if (availableSize < 0)
            return false;

        _componentSizes[_componentSizes.length - 1] = availableSize;
        return true;
    }
}
