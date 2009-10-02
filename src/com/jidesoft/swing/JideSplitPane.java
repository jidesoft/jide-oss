/*
 * @(#)JideSplitPane.java
 *
 * Copyright 2002 JIDE Software Inc. All rights reserved.
 */

package com.jidesoft.swing;

import com.jidesoft.plaf.LookAndFeelFactory;
import com.jidesoft.plaf.UIDefaultsLookup;

import javax.accessibility.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Arrays;

/**
 * <code>JideSplitPane</code> is used to divide multiple <code>Component</code>s.
 * <p/>
 * These <code>Component</code>s in a split pane can be aligned left to right using
 * <code>JideSplitPane.HORIZONTAL_SPLIT</code>, or top to bottom using <code>JideSplitPane.VERTICAL_SPLIT</code>.
 */
public class JideSplitPane extends JPanel implements ContainerListener, ComponentListener, Accessible {

    /**
     * The divider used for non-continuous layout is added to the split pane with this object.
     */
    protected static final String NON_CONTINUOUS_DIVIDER =
            "nonContinuousDivider";

    /**
     * Vertical split indicates the <code>Component</code>s are split along the y axis.  For example the two or more
     * <code>Component</code>s will be split one on top of the other.
     */
    public static final int VERTICAL_SPLIT = 0;

    /**
     * Horizontal split indicates the <code>Component</code>s are split along the x axis.  For example the two or more
     * <code>Component</code>s will be split one to the left of the other.
     */
    public static final int HORIZONTAL_SPLIT = 1;


    /**
     * Bound property name for orientation (horizontal or vertical).
     */
    public static final String ORIENTATION_PROPERTY = "orientation";

    /**
     * Bound property name for border size.
     */
    public static final String DIVIDER_SIZE_PROPERTY = "dividerSize";

    /**
     * Bound property name for border size.
     */
    public static final String PROPERTY_DIVIDER_LOCATION = "dividerLocation";

    /**
     * Bound property name for continuousLayout.
     */
    public static final String CONTINUOUS_LAYOUT_PROPERTY = "continuousLayout";

    /**
     * Bound property name for gripper.
     */
    public static final String GRIPPER_PROPERTY = "gripper";

    /**
     * Bound property name for proportional layout.
     */
    public static final String PROPORTIONAL_LAYOUT_PROPERTY = "proportionalLayout";

    /**
     * Bound property name for the proportions used in the layout.
     */
    public static final String PROPORTIONS_PROPERTY = "proportions";

    public static final String PROPERTY_HEAVYWEIGHT_COMPONENT_ENABLED = "heavyweightComponentEnabled";

    /**
     * How the views are split. The value of it can be either <code>HORIZONTAL_SPLIT</code> or
     * <code>VERTICAL_SPLIT</code>.
     */
    private int _orientation;

    /**
     * Size of the divider. All dividers have the same size. If <code>orientation</code> is
     * <code>HORIZONTAL_SPLIT</code>, the size will equal to the width of the divider If <code>orientation</code> is
     * <code>VERTICAL_SPLIT</code>, the size will equal to the height of the divider.
     */
    private int _dividerSize = UIDefaultsLookup.getInt("JideSplitPane.dividerSize");

    //    /**
    //     * Instance for the shadow of the divider when non continuous layout
    //     * is being used.
    //     */
    //    private Contour _nonContinuousLayoutDivider;
    private HeavyweightWrapper _nonContinuousLayoutDividerWrapper;

    /**
     * Continuous layout or not.
     */
    private boolean _continuousLayout = false;

    /**
     * Layered pane where _nonContinuousLayoutDivider is added to.
     */
    private Container _layeredPane;

    /**
     * If the gripper should be shown. Gripper is something on divider to indicate it can be dragged.
     */
    private boolean _showGripper = false;

    /**
     * Whether the contained panes should be laid out proportionally.
     */
    private boolean _proportionalLayout = false;

    /**
     * An array of the proportions to assign to the widths or heights of the contained panes.  Has one fewer elements
     * than there are contained panes; the last pane receives the remaining room.
     */
    private double[] _proportions;

    /**
     * For proportional layouts only, when this flag is true the initial layout uses even proportions for the contained
     * panes, unless the proportions are explicitly set.
     */
    private boolean _initiallyEven = true;

    private boolean _heavyweightComponentEnabled = false;
    public WindowAdapter _windowDeactivatedListener;
    private int _dividerStepSize = 0;
    private boolean _dragResizable = true;

    /**
     * Creates a new <code>JideSplitPane</code> configured to arrange the child components side-by-side horizontally.
     */
    public JideSplitPane() {
        this(HORIZONTAL_SPLIT);
    }


    /**
     * Creates a new <code>JideSplitPane</code> configured with the specified orientation.
     *
     * @param newOrientation <code>JideSplitPane.HORIZONTAL_SPLIT</code> or <code>JideSplitPane.VERTICAL_SPLIT</code>
     * @throws IllegalArgumentException if <code>orientation</code> is not one of HORIZONTAL_SPLIT or VERTICAL_SPLIT.
     */
    public JideSplitPane(int newOrientation) {
        super();

        _orientation = newOrientation;

        if (_orientation != HORIZONTAL_SPLIT && _orientation != VERTICAL_SPLIT)
            throw new IllegalArgumentException("cannot create JideSplitPane, " +
                    "orientation must be one of " +
                    "JideSplitPane.HORIZONTAL_SPLIT " +
                    "or JideSplitPane.VERTICAL_SPLIT");

        // setup layout
        LayoutManager layoutManager;
        if (_orientation == HORIZONTAL_SPLIT) {
            layoutManager = new JideSplitPaneLayout(this, JideSplitPaneLayout.X_AXIS);
        }
        else {
            layoutManager = new JideSplitPaneLayout(this, JideSplitPaneLayout.Y_AXIS);
        }
        super.setLayout(layoutManager);

        setOpaque(false);

        // setup listener
        installListeners();
    }

    /**
     * Get the step size while dragging the divider.
     * <p/>
     * The default value of the step size is 0, which means no constraints on the dragging position
     *
     * @return the step size.
     */
    public int getDividerStepSize() {
        return _dividerStepSize;
    }

    /**
     * Set the step size while dragging the divider.
     * <p/>
     * The step size cannot be negative.
     *
     * @param dividerStepSize the step size
     */
    public void setDividerStepSize(int dividerStepSize) {
        if (dividerStepSize < 0) {
            return;
        }
        _dividerStepSize = dividerStepSize;
    }

    @Override
    public void updateUI() {
        if (UIDefaultsLookup.get("JideSplitPane.dividerSize") == null) {
            LookAndFeelFactory.installJideExtension();
        }
        super.updateUI();
    }

    /**
     * Install listeners
     */
    private void installListeners() {
        addContainerListener(this);
    }

    /**
     * Sets the size of the divider.
     *
     * @param newSize an integer giving the size of the divider in pixels
     */
    public void setDividerSize(int newSize) {
        int oldSize = _dividerSize;

        if (oldSize != newSize) {
            _dividerSize = newSize;
            firePropertyChange(DIVIDER_SIZE_PROPERTY, oldSize, newSize);
            invalidate();
        }
    }


    /**
     * Returns the size of the divider.
     *
     * @return an integer giving the size of the divider in pixels
     */
    public int getDividerSize() {
        return _dividerSize;
    }

    /**
     * Inserts the specified pane to this container at the given position. Note: Divider is not counted.
     *
     * @param pane  the pane to be added
     * @param index the position at which to insert the component.
     * @return the component <code>pane</code>
     */
    public Component insertPane(Component pane, int index) {
        return insertPane(pane, null, index);
    }

    /**
     * Inserts the specified pane to this container at the given position. Note: Divider is not counted.
     *
     * @param pane       the pane to be added
     * @param constraint an object expressing layout constraints for this component
     * @param index      the position at which to insert the component.
     * @return the component <code>pane</code>
     */
    public Component insertPane(Component pane, Object constraint, int index) {
        if (index <= 0) {
            addImpl(pane, constraint, 0);
        }
        else if (index >= getPaneCount()) {
            addImpl(pane, constraint, -1);
        }
        else {
            addImpl(pane, constraint, (index << 1) - 1);
        }

        return pane;
    }

    /**
     * Adds the specified pane to this container at the end.
     *
     * @param pane the pane to be added
     * @return the pane <code>pane</code>
     */
    public Component addPane(Component pane) {
        if (pane == null) {
            return null;
        }
        return super.add(pane);
    }

    /**
     * Removes the pane, specified by <code>index</code>, from this container.
     *
     * @param pane the pane to be removed.
     */
    public void removePane(Component pane) {
        removePane(indexOfPane(pane));
    }

    /**
     * Replaces the pane at the position specified by index.
     *
     * @param pane  new pane
     * @param index position
     */
    public void setPaneAt(Component pane, int index) {
        setPaneAt(pane, null, index);
    }

    /**
     * Replaces the pane at the position specified by index.
     *
     * @param pane       new pane
     * @param constraint an object expressing layout constraints for this component
     * @param index      position
     */
    public void setPaneAt(Component pane, Object constraint, int index) {
        double[] proportions = _proportions;
        _proportions = null; // Just turn them off temporarily
        removePane(index);
        insertPane(pane, constraint, index);
        _proportions = proportions;
    }

    /**
     * Removes the pane, specified by <code>index</code>, from this container.
     *
     * @param index the index of the component to be removed.
     */
    public void removePane(int index) {
        if (index == 0) { // if first one
            super.remove(0); // the component
        }
        else { // not first one. then remove itself and the divider before it
            super.remove(index << 1); // component
        }
    }

    /**
     * Sets the orientation, or how the splitter is divided. The options are:<ul> <li>JideSplitPane.VERTICAL_SPLIT
     * (above/below orientation of components) <li>JideSplitPane.HORIZONTAL_SPLIT  (left/right orientation of
     * components) </ul>
     *
     * @param orientation an integer specifying the orientation
     * @throws IllegalArgumentException if orientation is not one of: HORIZONTAL_SPLIT or VERTICAL_SPLIT.
     */
    public void setOrientation(int orientation) {
        if ((orientation != VERTICAL_SPLIT) &&
                (orientation != HORIZONTAL_SPLIT)) {
            throw new IllegalArgumentException("JideSplitPane: orientation must " +
                    "be one of " +
                    "JideSplitPane.VERTICAL_SPLIT or " +
                    "JideSplitPane.HORIZONTAL_SPLIT");
        }

        if (_orientation == orientation)
            return;

        int oldOrientation = _orientation;
        _orientation = orientation;

//        if (_orientation == JideSplitPane.HORIZONTAL_SPLIT)
//            setBorder(BorderFactory.createLineBorder(Color.RED));
//        else
//            setBorder(BorderFactory.createLineBorder(Color.CYAN));
        LayoutManager layoutManager;
        if (_orientation == HORIZONTAL_SPLIT) {
            layoutManager = new JideSplitPaneLayout(this, JideSplitPaneLayout.X_AXIS);
        }
        else {
            layoutManager = new JideSplitPaneLayout(this, JideSplitPaneLayout.Y_AXIS);
        }
        super.setLayout(layoutManager);
        doLayout();

        firePropertyChange(ORIENTATION_PROPERTY, oldOrientation, orientation);
    }

    /**
     * Returns the orientation.
     *
     * @return an integer giving the orientation
     *
     * @see #setOrientation
     */
    public int getOrientation() {
        return _orientation;
    }

    /**
     * Lays out the <code>JideSplitPane</code> layout based on the preferred size children components, or based on the
     * proportions if proportional layout is on. This will likely result in changing the divider location.
     */
    public void resetToPreferredSizes() {
        doLayout();
    }

    /**
     * Sets this split pane to lay its constituents out proportionally if the given flag is true, or by preferred sizes
     * otherwise.
     *
     * @param proportionalLayout true or false.
     */
    public void setProportionalLayout(boolean proportionalLayout) {
        if (proportionalLayout == _proportionalLayout)
            return;
        _proportionalLayout = proportionalLayout;
        revalidate();
        firePropertyChange(PROPORTIONAL_LAYOUT_PROPERTY, !proportionalLayout, proportionalLayout);
        if (!proportionalLayout)
            setProportions(null);
    }

    /**
     * Returns the proportional layout flag.
     *
     * @return true or false.
     */
    public boolean isProportionalLayout() {
        return _proportionalLayout;
    }

    void internalSetProportions(double[] proportions) {
        _proportions = proportions;
    }

    /**
     * Sets the proportions to use in laying out this split pane's children.  Only applicable when {@link
     * #isProportionalLayout} is true; calling it when false will throw an exception.  The given array must either be
     * null, or have one fewer slots than there are {@linkplain #getPaneCount() contained panes}.  Each item in the
     * array (if not null) must be a number between 0 and 1, and the sum of all of them must be no more than 1.
     *
     * @param proportions the proportions of all the panes.
     */
    public void setProportions(double[] proportions) {
//        if ( ! _proportionalLayout )
        if (!_proportionalLayout && proportions != null)
            throw new IllegalStateException("Can't set proportions on a non-proportional split pane");
        if (Arrays.equals(proportions, _proportions))
            return;
        if (proportions != null && proportions.length != getPaneCount() - 1)
            throw new IllegalArgumentException(
                    "Must provide one fewer proportions than there are panes: got " + proportions.length
                            + ", expected " + (getPaneCount() - 1));
        if (proportions != null) {
            double sum = 0.0;
            for (int i = 0; i < proportions.length; ++i) {
                if (proportions[i] < 0.0)
                    proportions[i] = 0.0;
                if (proportions[i] > 1.0)
                    proportions[i] = 1.0;
                sum += proportions[i];
            }
            if (sum > 1.0)
                throw new IllegalArgumentException("Sum of proportions must be no more than 1, got " + sum);
        }
        double[] oldProportions = _proportions;
        _proportions = (proportions == null) ? null : proportions.clone();

        LayoutManager layoutManager = getLayout();
        boolean reset = false;
        if (layoutManager instanceof JideBoxLayout) {
            reset = ((JideBoxLayout) layoutManager).isResetWhenInvalidate();
            ((JideBoxLayout) layoutManager).setResetWhenInvalidate(true);
        }
        revalidate();
        if (reset) {
            ((JideBoxLayout) layoutManager).setResetWhenInvalidate(reset);
        }
        firePropertyChange(PROPORTIONS_PROPERTY, oldProportions, proportions);
    }

    /**
     * Returns the current array of proportions used for proportional layout, or null if none has been established via
     * {@link #setProportions} or via user action.
     *
     * @return the proportions.
     */
    public double[] getProportions() {
        double[] answer = _proportions;
        if (answer != null)
            answer = answer.clone();
        return answer;
    }

    /**
     * Sets the flag telling whether to do even proportions for the initial proportional layout, in the absence of
     * explicit proportions.
     *
     * @param initiallyEven true or false.
     */
    public void setInitiallyEven(boolean initiallyEven) {
        _initiallyEven = initiallyEven;
    }

    /**
     * Returns the flag that tells whether to do even proportions for the initial proportional layout, in the absence of
     * explicit proportions.
     *
     * @return true or false.
     */
    public boolean isInitiallyEven() {
        return _initiallyEven;
    }

    /**
     * Returns true, so that calls to <code>revalidate</code> on any descendant of this <code>JideSplitPane</code> will
     * cause a request to be queued that will validate the <code>JideSplitPane</code> and all its descendants.
     *
     * @return true
     *
     * @see JComponent#revalidate
     */
    @Override
    public boolean isValidateRoot() {
        return true;
    }

    /**
     * Prepares dragging if it's not continuous layout. If it's continuous layout, do nothing.
     *
     * @param divider the divider
     */
    protected void startDragging(JideSplitPaneDivider divider) {
        if (!isContinuousLayout()) {
            Component topLevelAncestor = getTopLevelAncestor();
            if (_windowDeactivatedListener == null) {
                // this a listener to remove the dragging outline when window is deactivated
                _windowDeactivatedListener = new WindowAdapter() {
                    @Override
                    public void windowDeactivated(WindowEvent e) {
                        stopDragging();
                        if (e.getWindow() != null) {
                            e.getWindow().removeWindowListener(_windowDeactivatedListener);
                        }
                    }
                };
            }
            if (topLevelAncestor instanceof Window)
                ((Window) topLevelAncestor).addWindowListener(_windowDeactivatedListener);
            if (topLevelAncestor instanceof RootPaneContainer) {
                _layeredPane = ((RootPaneContainer) topLevelAncestor).getLayeredPane();

                // left over, remove them
                if (_nonContinuousLayoutDividerWrapper == null) {
                    Contour nonContinuousLayoutDivider = new JideSplitPaneContour();
                    _nonContinuousLayoutDividerWrapper = new JideSplitPaneHeavyweightWrapper(nonContinuousLayoutDivider);
                    _nonContinuousLayoutDividerWrapper.setHeavyweight(isHeavyweightComponentEnabled());
                }

                _nonContinuousLayoutDividerWrapper.delegateSetCursor((_orientation == HORIZONTAL_SPLIT) ?
                        JideSplitPaneDivider.HORIZONTAL_CURSOR : JideSplitPaneDivider.VERTICAL_CURSOR);
                _nonContinuousLayoutDividerWrapper.delegateSetVisible(false);
                _nonContinuousLayoutDividerWrapper.delegateAdd(_layeredPane, JLayeredPane.DRAG_LAYER);

                Rectangle bounds = getVisibleRect();
                Rectangle layeredPaneBounds = SwingUtilities.convertRectangle(this, bounds, _layeredPane);
                int dividerThickness = Math.min(4, getDividerSize());
                if (getOrientation() == HORIZONTAL_SPLIT) {
                    _nonContinuousLayoutDividerWrapper.delegateSetBounds(layeredPaneBounds.x, layeredPaneBounds.y,
                            dividerThickness, layeredPaneBounds.height);
                }
                else {
                    _nonContinuousLayoutDividerWrapper.delegateSetBounds(layeredPaneBounds.x, layeredPaneBounds.y,
                            layeredPaneBounds.width, dividerThickness);
                }
            }
        }
    }

    private void stopDragging() {
        if (!isContinuousLayout() && _layeredPane != null && _nonContinuousLayoutDividerWrapper != null) {
            _nonContinuousLayoutDividerWrapper.delegateSetVisible(false);
            _nonContinuousLayoutDividerWrapper.delegateRemove(_layeredPane);
            _nonContinuousLayoutDividerWrapper.delegateSetNull();
            _nonContinuousLayoutDividerWrapper = null;
            // add a protection in case there is another wrapper inside the layered pane
            Component[] childComponents = _layeredPane.getComponents();
            for (Component component : childComponents) {
                if (component instanceof JideSplitPaneContour || component instanceof JideSplitPaneHeavyweightWrapper) {
                    _layeredPane.remove(component);
                }
            }
        }
    }

    /**
     * Drags divider to right location. If it's continuous layout, really drag the divider; if not, only drag the
     * shadow.
     *
     * @param divider  the divider
     * @param location new location
     */
    protected void dragDividerTo(JideSplitPaneDivider divider, int location) {
        if (_layeredPane == null || isContinuousLayout()) {
            setDividerLocation(divider, location);
        }
        else {
            if (_nonContinuousLayoutDividerWrapper != null) {
                Point p;
                Dimension size = new Dimension();
                Rectangle rect = getVisibleRect();
                int dividerThickness = Math.min(4, getDividerSize());
                Rectangle convertedRect = SwingUtilities.convertRectangle(this, rect, _layeredPane);
                if (getOrientation() == HORIZONTAL_SPLIT) {
                    p = SwingUtilities.convertPoint(this, location, rect.y, _layeredPane);
                    p.x += ((getDividerSize() - dividerThickness) >> 1);
                    size.width = dividerThickness;
                    size.height = convertedRect.height;
                }
                else {
                    p = SwingUtilities.convertPoint(this, rect.x, location, _layeredPane);
                    p.y += ((getDividerSize() - dividerThickness) >> 1);
                    size.width = convertedRect.width;
                    size.height = dividerThickness;
                }
                _nonContinuousLayoutDividerWrapper.delegateSetBounds(new Rectangle(p, size));
                _nonContinuousLayoutDividerWrapper.delegateSetVisible(true);
            }
        }
    }

    /**
     * Finishes dragging. If it's not continuous layout, clear up the shadow component.
     *
     * @param divider  the divider
     * @param location new location
     */
    protected void finishDraggingTo(JideSplitPaneDivider divider, int location) {
        if (isContinuousLayout() || _nonContinuousLayoutDividerWrapper != null) {
            stopDragging();
            setDividerLocation(divider, location);
        }
    }

    /**
     * Returns the index of the divider. For example, the index of the first divider is 0, the index of the second is 1.
     * Notes: Pane is not counted
     *
     * @param divider divider to get index
     * @return index of the divider. -1 if comp doesn't exist in this container
     */
    public int indexOfDivider(JideSplitPaneDivider divider) {
        int index = indexOf(divider);
        if (index == -1)
            return index;
        else {
            if (index % 2 == 0)
                //noinspection UseOfSystemOutOrSystemErr
                System.err.println("Warning: divider's index is even. (index = " + index + ")");
            return (index - 1) / 2;
        }
    }

    /**
     * Returns the index of the pane. For example, the index of the first pane is 0, the index of the second is 1.
     * Notes: divider is not counted
     *
     * @param pane pane to get index
     * @return index of the pane. -1 if comp doesn't exist in this container
     */
    public int indexOfPane(Component pane) {
        int index = indexOf(pane);
        if (index == -1)
            return -1;
        else {
            if (index % 2 != 0)
                //noinspection UseOfSystemOutOrSystemErr
                System.err.println("Warning: pane's index is odd. (index = " + index + ")");
            return index >> 1;
        }
    }

    /**
     * Returns the index of the component.
     *
     * @param comp component to get index
     * @return index of the comp. -1 if comp doesn't exist in this container
     */
    public int indexOf(Component comp) {
        for (int i = 0; i < getComponentCount(); i++) {
            if (getComponent(i).equals(comp))
                return i;
        }
        return -1;
    }

    /**
     * Returns the divider at index.
     *
     * @param index index
     * @return the divider at the index
     */
    public JideSplitPaneDivider getDividerAt(int index) {
        if (index < 0 || index * 2 + 1 >= getComponentCount())
            return null;
        return (JideSplitPaneDivider) getComponent(index * 2 + 1);
    }

    /**
     * Returns the component at index.
     *
     * @param index index
     * @return the component at the index
     */
    public Component getPaneAt(int index) {
        if (index < 0 || index << 1 >= getComponentCount())
            return null;
        return getComponent(index << 1);
    }

    /**
     * Gets the count of panes, regardless of dividers.
     *
     * @return the count of panes
     */
    public int getPaneCount() {
        return (getComponentCount() + 1) >> 1;
    }

    /**
     * Set the divider location.
     *
     * @param divider  the divider
     * @param location new location
     */
    public void setDividerLocation(JideSplitPaneDivider divider, int location) {
        setDividerLocation(indexOfDivider(divider), location);
    }

    /**
     * Set the divider location. You can only call this method to set the divider location when the component is
     * rendered on the screen. If the component has never been displayed before, this method call has no effect.
     *
     * @param dividerIndex the divider index, starting from 0 for the first divider.
     * @param location     new location
     */
    public void setDividerLocation(int dividerIndex, int location) {
        ((JideSplitPaneLayout) getLayout()).setDividerLocation(dividerIndex, location, true);
    }

    /**
     * Get the divider location. You can only get a valid divider location when the component is displayed on the
     * screen. If the component has never been displayed on screen, -1 will be returned.
     *
     * @param dividerIndex the divider index
     * @return the location of the divider.
     */
    public int getDividerLocation(int dividerIndex) {
        return ((JideSplitPaneLayout) getLayout()).getDividerLocation(dividerIndex);
    }

    /**
     * Invoked when a component has been added to the container. Basically if you add anything which is not divider, a
     * divider will automatically added before or after the component.
     *
     * @param e ContainerEvent
     */
    public void componentAdded(ContainerEvent e) {
        e.getChild().addComponentListener(this);
        if (!(e.getChild() instanceof JideSplitPaneDivider)) {
            addExtraDividers();
        }
        setDividersVisible();
        resetToPreferredSizes();
    }

    /**
     * Invoked when a component has been removed from the container. Basically if you remove anything which is not
     * divider, a divider will automatically deleted before or after the component.
     *
     * @param e ContainerEvent
     */
    public void componentRemoved(ContainerEvent e) {
        e.getChild().removeComponentListener(this);
        if (!(e.getChild() instanceof JideSplitPaneDivider)) {
            removeExtraDividers();
        }
        setDividersVisible();
        resetToPreferredSizes();

/*
        if (getComponentCount() == 1 && getComponent(0) instanceof JideSplitPane) {
            JideSplitPane childPane = (JideSplitPane)getComponent(0);
            if(getOrientation() != childPane.getOrientation()) {
                setOrientation(childPane.getOrientation());
            }
            // copy all children of its splitpane child to this
            boolean savedAutoRemove = childPane.isAutomaticallyRemove();
            childPane.setAutomaticallyRemove(false);
            for(int i = 0; i < childPane.getComponentCount(); i ++) {
                if(childPane.getComponent(i) instanceof JideSplitPaneDivider)
                    continue;
                System.out.println("Adding " + childPane.getComponent(i));
                add(childPane.getComponent(i));
                i --;
            }
            childPane.setAutomaticallyRemove(savedAutoRemove);
            System.out.println("Removing " + childPane);
            remove(childPane);
        }

        if (isAutomaticallyRemove() && getComponentCount() == 0 && getParent() != null) {
            System.out.println("Automatically Removing this " + this);
            getParent().remove(this);
            return;
        }
*/
    }

    public void componentResized(ComponentEvent e) {
    }

    public void componentMoved(ComponentEvent e) {
    }

    public void componentShown(ComponentEvent e) {
        if (e.getComponent() instanceof JideSplitPaneDivider) {
            return;
        }
        setDividersVisible();
        resetToPreferredSizes();
    }

    public void componentHidden(ComponentEvent e) {
        if (e.getComponent() instanceof JideSplitPaneDivider) {
            return;
        }
        setDividersVisible();
        resetToPreferredSizes();
    }

    /**
     * Remove extra divider. One is considered as extra dividers where two dividers are adjacent.
     *
     * @return true if dividers are removed.
     */
    protected boolean removeExtraDividers() {
        int extra = 0;

        if (getComponentCount() == 0) {
            if (_proportions != null)
                setProportions(null);
            return false;
        }

        boolean changed = false;
        // remove first divider if it's one
        if (getComponent(0) instanceof JideSplitPaneDivider) {
            remove(0);
            removeProportion(0);
            changed = true;
        }

        for (int i = 0; i < getComponentCount(); i++) {
            Component comp = getComponent(i);
            if (comp instanceof JideSplitPaneDivider) {
                extra++;
                if (extra == 2) {
                    remove(comp);
                    if (_proportions != null && getPaneCount() == _proportions.length)
                        removeProportion(i / 2);
                    changed = true;
                    extra--;
                    i--;
                }
            }
            else
                extra = 0;
        }

        if (extra == 1) { // remove last one if it's a divider
            remove(getComponentCount() - 1);
            removeProportion((getComponentCount() + 1) / 2);
            changed = true;
        }

        return changed;
    }

    /**
     * Removes the proportion at the given pane index, spreading its value proportionally across the other proportions.
     * If it's the last proportion being removed, sets the proportions to null.
     *
     * @param paneIndex the pane index.
     */
    protected void removeProportion(int paneIndex) {
        double[] oldProportions = _proportions;
        if (oldProportions == null)
            return;
        if (oldProportions.length <= 1) {
            setProportions(null);
            return;
        }
        double[] newProportions = new double[oldProportions.length - 1];
        double p;
        if (paneIndex < oldProportions.length)
            p = oldProportions[paneIndex];
        else {
            p = 1.0;
            for (double proportion : oldProportions) p -= proportion;
        }
        double total = 1.0 - p;
        for (int i = 0; i < newProportions.length; ++i) {
            int j = (i < paneIndex) ? i : i + 1;
            newProportions[i] = oldProportions[j] / total;
        }
//       to make sure the proportion adds up to 1 for this special case.
        if (newProportions.length == 1) {
            newProportions[0] = 1.0;
        }

        setProportions(newProportions);
    }

    /**
     * Add divider if there are two panes side by side without a divider in between.
     */
    protected void addExtraDividers() {
        int extra = 0;
        for (int i = 0; i < getComponentCount(); i++) {
            Component comp = getComponent(i);
            if (!(comp instanceof JideSplitPaneDivider)) {
                extra++;
                if (extra == 2) {
                    add(createSplitPaneDivider(), JideSplitPaneLayout.FIX, i);
                    if (_proportions != null && getPaneCount() == _proportions.length + 2)
                        addProportion((i + 1) / 2);
                    extra = 0;
                }
            }
            else
                extra = 0;
        }
    }

    /**
     * Adds a proportion at the given pane index, taking a proportional amount from each of the existing proportions.
     *
     * @param paneIndex the pane index.
     */
    protected void addProportion(int paneIndex) {
        double[] oldProportions = _proportions;
        if (oldProportions == null)
            return;
        double[] newProportions = new double[oldProportions.length + 1];
        double p = 1.0 / (newProportions.length + 1);
        double total = 1.0 - p;
        for (int i = 0; i < newProportions.length; ++i) {
            if (i == paneIndex)
                newProportions[i] = p;
            else {
                int j = (i < paneIndex) ? i : i - 1;
                if (j < oldProportions.length)
                    newProportions[i] = oldProportions[j] * total;
                else
                    newProportions[i] = p;
            }
        }
        setProportions(newProportions);
    }

    /**
     * Before this method is call, the panes must be separated by dividers.
     */
    protected void setDividersVisible() {
        if (getComponentCount() == 1) {
            setVisible(getComponent(0).isVisible());
        }
        else if (getComponentCount() > 1) {
            boolean anyVisible = false;
            boolean anyPrevVisible = false;
            for (int i = 0; i < getComponentCount(); i++) {
                Component comp = getComponent(i);
                if (!(comp instanceof JideSplitPaneDivider)) {
                    if (comp.isVisible() && !anyVisible) {
                        anyVisible = true;
                    }
                    continue;
                }
                boolean visiblePrev = i - 1 >= 0 && getComponent(i - 1).isVisible();
                boolean visibleNext = i + 1 < getComponentCount() && getComponent(i + 1).isVisible();
                if (visiblePrev && visibleNext) {
                    comp.setVisible(true);
                }
                else if (!visiblePrev && !visibleNext) {
                    comp.setVisible(false);
                }
                else if (visiblePrev && !visibleNext) {
                    comp.setVisible(false);
                    anyPrevVisible = true;
                }
                else /*if (visibleNext && !visiblePrev)*/ {
                    if (anyPrevVisible) {
                        comp.setVisible(true);
                        anyPrevVisible = false;
                    }
                    else {
                        comp.setVisible(false);
                    }
                }
            }

            setVisible(anyVisible);
        }
    }

    protected JideSplitPaneDivider createSplitPaneDivider() {
        return new JideSplitPaneDivider(this);
    }

    /**
     * Get previous divider's, if any, location from current divider. If there is no previous divider, return 0.
     *
     * @param divider          the divider
     * @param ignoreVisibility true to not check if the pane is visible.
     * @param reversed         from left to right or reversed.
     * @return the location of previous divider if any
     */
    protected int getPreviousDividerLocation(JideSplitPaneDivider divider, boolean ignoreVisibility, boolean reversed) {
        int index = indexOfDivider(divider);
        int location = -1;
        if (reversed) {
            if (((index + 1) * 2) + 1 <= getComponentCount()) {
                for (int i = index + 1; (i * 2) + 1 < getComponentCount(); i++) {
                    if (ignoreVisibility || getDividerAt(i).isVisible()) {
                        if (_orientation == HORIZONTAL_SPLIT) {
                            location = getDividerAt(i).getBounds().x;
                        }
                        else {
                            location = getDividerAt(i).getBounds().y;
                        }
                        break;
                    }
                }
            }
        }
        else {
            if (index > 0) {
                for (int i = index - 1; i >= 0; i--) {
                    if (ignoreVisibility || getDividerAt(i).isVisible()) {
                        if (_orientation == HORIZONTAL_SPLIT) {
                            location = getDividerAt(i).getBounds().x;
                        }
                        else {
                            location = getDividerAt(i).getBounds().y;
                        }
                        break;
                    }
                }
            }
        }

        if (location != -1) {
            return location + getDividerSize();
        }

        return 0;
    }

    /**
     * Get previous divider's, if any, location from current divider. If there is no previous divider, return 0.
     *
     * @param divider          the divider
     * @param ignoreVisibility true to not check if the pane is visible.
     * @param reversed         from left to right or reversed.
     * @return the location of next divider if any
     */
    public int getNextDividerLocation(JideSplitPaneDivider divider, boolean ignoreVisibility, boolean reversed) {
        int index = indexOfDivider(divider);
        int location = -1;
        if (!reversed) {
            if (((index + 1) * 2) + 1 <= getComponentCount()) {
                for (int i = index + 1; (i * 2) + 1 < getComponentCount(); i++) {
                    if (ignoreVisibility || getDividerAt(i).isVisible()) {
                        if (_orientation == HORIZONTAL_SPLIT) {
                            location = getDividerAt(i).getBounds().x;
                        }
                        else {
                            location = getDividerAt(i).getBounds().y;
                        }
                        break;
                    }
                }
            }
        }
        else {
            if (index > 0) {
                for (int i = index - 1; i >= 0; i--) {
                    if (ignoreVisibility || getDividerAt(i).isVisible()) {
                        if (_orientation == HORIZONTAL_SPLIT) {
                            location = getDividerAt(i).getBounds().x;
                        }
                        else {
                            location = getDividerAt(i).getBounds().y;
                        }
                        break;
                    }
                }
            }
        }

        if (location != -1) {
            return location - getDividerSize();
        }

        return getOrientation() == HORIZONTAL_SPLIT ? getWidth() - getDividerSize() : getHeight() - getDividerSize();
    }

    /**
     * Checks if the gripper is visible.
     *
     * @return true if gripper is visible
     */
    public boolean isShowGripper() {
        return _showGripper;
    }

    /**
     * Sets the visibility of gripper.
     *
     * @param showGripper true to show gripper
     */
    public void setShowGripper(boolean showGripper) {
        boolean oldShowGripper = _showGripper;
        if (oldShowGripper != showGripper) {
            _showGripper = showGripper;
            firePropertyChange(GRIPPER_PROPERTY, oldShowGripper, _showGripper);
        }
    }

    /**
     * Causes this container to lay out its components.  Most programs should not call this method directly, but should
     * invoke the <code>validate</code> method instead.
     *
     * @see LayoutManager#layoutContainer
     * @see #setLayout
     * @see #validate
     * @since JDK1.1
     */
    @Override
    public void doLayout() {
        if (removeExtraDividers()) {
            ((JideSplitPaneLayout) getLayout()).invalidateLayout(this);
        }
        super.doLayout();
    }

    /**
     * Determines whether the JSplitPane is set to use a continuous layout.
     *
     * @return true or false.
     */
    public boolean isContinuousLayout() {
        return _continuousLayout;
    }

    /**
     * Turn continuous layout on/off.
     *
     * @param continuousLayout true or false.
     */
    public void setContinuousLayout(boolean continuousLayout) {
        boolean oldCD = _continuousLayout;

        _continuousLayout = continuousLayout;
        firePropertyChange(CONTINUOUS_LAYOUT_PROPERTY, oldCD, continuousLayout);
    }

    /**
     * Gets the AccessibleContext associated with this JideSplitPane. For split panes, the AccessibleContext takes the
     * form of an AccessibleJideSplitPane. A new AccessibleJideSplitPane instance is created if necessary.
     *
     * @return an AccessibleJideSplitPane that serves as the AccessibleContext of this JideSplitPane
     */
    @Override
    public AccessibleContext getAccessibleContext() {
        if (accessibleContext == null) {
            accessibleContext = new AccessibleJideSplitPane();
        }
        return accessibleContext;
    }

    /**
     * Get the flag indicating if dragging the divider could resize the panes.
     * <p/>
     * By default, the value is true for JideSplitPane. You could set it to false if you don't like it.
     *
     * @return the flag.
     */
    public boolean isDragResizable() {
        return _dragResizable;
    }

    /**
     * Set the flag indicating if dragging the divider could resize the panes.
     *
     * @param dragResizable the flag
     */
    public void setDragResizable(boolean dragResizable) {
        _dragResizable = dragResizable;
    }

    /**
     * This class implements accessibility support for the <code>JideSplitPane</code> class.  It provides an
     * implementation of the Java Accessibility API appropriate to split pane user-interface elements.
     */
    protected class AccessibleJideSplitPane extends AccessibleJComponent {
        private static final long serialVersionUID = -6167624875135108683L;

        /**
         * Gets the state set of this object.
         *
         * @return an instance of AccessibleState containing the current state of the object
         *
         * @see javax.accessibility.AccessibleState
         */
        @Override
        public AccessibleStateSet getAccessibleStateSet() {
            AccessibleStateSet states = super.getAccessibleStateSet();
            if (getOrientation() == VERTICAL_SPLIT) {
                states.add(AccessibleState.VERTICAL);
            }
            else {
                states.add(AccessibleState.HORIZONTAL);
            }
            return states;
        }

        /**
         * Gets the role of this object.
         *
         * @return an instance of AccessibleRole describing the role of the object
         *
         * @see AccessibleRole
         */
        @Override
        public AccessibleRole getAccessibleRole() {
            return AccessibleRole.SPLIT_PANE;
        }
    } // inner class AccessibleJideSplitPane

    /**
     * @return true if the heavyweight component is enabled.
     */
    public boolean isHeavyweightComponentEnabled() {
        return _heavyweightComponentEnabled;
    }

    /**
     * Enables heavyweight components. The difference is the divider. If true, the divider will be heavyweight divider.
     * Otherwise it will use lightweight divider.
     *
     * @param heavyweightComponentEnabled true to enable the usage of heavyweight components.
     */
    public void setHeavyweightComponentEnabled(boolean heavyweightComponentEnabled) {
        boolean old = _heavyweightComponentEnabled;
        if (_heavyweightComponentEnabled != heavyweightComponentEnabled) {
            _heavyweightComponentEnabled = heavyweightComponentEnabled;
            firePropertyChange(PROPERTY_HEAVYWEIGHT_COMPONENT_ENABLED, old, _heavyweightComponentEnabled);
        }
    }

    /*
     * Added on 05/14/2008 in response to http://www.jidesoft.com/forum/viewtopic.php?p=26074#26074,
     * http://www.jidesoft.com/forum/viewtopic.php?p=5148#5148 and
     * http://www.jidesoft.com/forum/viewtopic.php?p=23403#23403.
     *
     * The addition below provides the option of adding a one-touch button which is capable of expanding/collapsing the
     * split pane (in one click of the mouse).
     *
     * @see #setOneTouchExpandable(boolean)
     */

    /**
     * Bound property for <code>oneTouchExpandable</code>.
     *
     * @see #setOneTouchExpandable
     */
    public static final String ONE_TOUCH_EXPANDABLE_PROPERTY = "oneTouchExpandable";

    /**
     * Flag indicating whether the SplitPane's divider should be one-touch expandable/collapsible. The default value of
     * this property is <code>false</code>
     *
     * @see #setOneTouchExpandable
     * @see #isOneTouchExpandable
     */
    private boolean _oneTouchExpandable = false;

    /**
     * The default width/height of the divider (when horizontally/vertically split respectively).
     */
    private int oneTouchExpandableDividerSize = 8;

    /**
     * The image displayed on the left one-touch button. If no image is supplied, a default triangle will be painted
     * onto the button.
     *
     * @see #setLeftOneTouchButtonImageIcon
     */
    private ImageIcon _leftOneTouchButtonImageIcon = null;

    /**
     * The image displayed on the right one-touch button. If no image is supplied, a default triangle will be painted
     * onto the button.
     *
     * @see #setRightOneTouchButtonImageIcon
     */
    private ImageIcon _rightOneTouchButtonImageIcon = null;

    /**
     * Sets the value of the <code>oneTouchExpandable</code> property. If <code>true</code>, the <code>JSplitPane</code>
     * will display a UI widget on the divider to quickly expand/collapse the divider.<p> </p> The default value of this
     * property is <code>false</code>.<p> </p> Please note: Some look and feels might not support one-touch expanding;
     * they will ignore this property.
     *
     * @param oneTouchExpandable <code>true</code> to specify that the split pane should provide a collapse/expand
     *                           widget
     * @see #isOneTouchExpandable
     */
    public void setOneTouchExpandable(boolean oneTouchExpandable) {
        boolean oldValue = _oneTouchExpandable;
        if (oldValue != oneTouchExpandable) {
            _oneTouchExpandable = oneTouchExpandable;

            /*
            * We need to widen/shrink the dividers width so that we can display/remove the one-touch buttons.
            */
            LayoutManager layoutManager = getLayout();
            if (layoutManager instanceof JideBoxLayout) {
                ((JideBoxLayout) layoutManager).setResetWhenInvalidate(true);
            }
            if (oneTouchExpandable) {
                setDividerSize(oneTouchExpandableDividerSize);
            }
            else {
                setDividerSize(UIDefaultsLookup.getInt("JideSplitPane.dividerSize"));
            }

            /*
            * We now fire a bound property so each divider listening can set up its own one-touch buttons.
            */
            firePropertyChange(ONE_TOUCH_EXPANDABLE_PROPERTY, oldValue, _oneTouchExpandable);
            revalidate();
            repaint();
            if (layoutManager instanceof JideBoxLayout) {
                ((JideBoxLayout) layoutManager).setResetWhenInvalidate(false);
            }
        }
    }

    /**
     * Returns whether one-touch expand/collapse is on.
     *
     * @return the value of the <code>oneTouchExpandable</code> property
     *
     * @see #setOneTouchExpandable
     */
    public boolean isOneTouchExpandable() {
        return _oneTouchExpandable;
    }

    /**
     * Sets the left button's image icon. By default, the button has a width of 5 pixels and a height of 10 pixel in
     * HORIZONTAL_SPLIT mode (and a width of 10 pixels and a height of 5 pixel in VERTICAL_SPLIT mode) -- this should be
     * considered when assigning its imageIcon.
     *
     * @param leftButtonImageIcon the image to be displayed on the left one-touch button
     */
    public void setLeftOneTouchButtonImageIcon(ImageIcon leftButtonImageIcon) {
        _leftOneTouchButtonImageIcon = leftButtonImageIcon;
    }

    /**
     * Gets the left button's image icon.
     *
     * @return the imageIcon used displayed on the left one-touch button
     */
    public ImageIcon getLeftOneTouchButtonImageIcon() {
        return _leftOneTouchButtonImageIcon;
    }

    /**
     * Sets the right button's image icon. By default, the button has a width of 5 pixels and a height of 10 pixel in
     * HORIZONTAL_SPLIT mode (and a width of 10 pixels and a height of 5 pixel in VERTICAL_SPLIT mode) -- this should be
     * considered when assigning its imageIcon.
     *
     * @param rightButtonImageIcon the image to be displayed on the right one-touch button
     */
    public void setRightOneTouchButtonImageIcon(ImageIcon rightButtonImageIcon) {
        _rightOneTouchButtonImageIcon = rightButtonImageIcon;
    }

    /**
     * Gets the right button's image icon.
     *
     * @return the imageIcon used displayed on the left one-touch button
     */
    public ImageIcon getRightOneTouchButtonImageIcon() {
        return _rightOneTouchButtonImageIcon;
    }

    /**
     * Sets the divider locations.
     *
     * @param locations the new divider locations.
     */
    public void setDividerLocations(int[] locations) {
        for (int i = 0; i < locations.length; i++) {
            int location = locations[i];
            setDividerLocation(i, location);
        }
    }

    /**
     * Gets the divider locations.
     *
     * @return the divider locations.
     */
    public int[] getDividerLocations() {
        int count = getPaneCount();
        if (getPaneCount() == 0) {
            return new int[0];
        }
        int[] locations = new int[count - 1];
        for (int i = 0; i < count - 1; i++) {
            locations[i] = getDividerLocation(i);
        }
        return locations;
    }

    private class JideSplitPaneContour extends Contour {
        public JideSplitPaneContour() {
            super();
        }

        public JideSplitPaneContour(int tabHeight) {
            super(tabHeight);
        }
    }

    private class JideSplitPaneHeavyweightWrapper extends HeavyweightWrapper {
        public JideSplitPaneHeavyweightWrapper(Component component) {
            super(component);
        }
    }
}
