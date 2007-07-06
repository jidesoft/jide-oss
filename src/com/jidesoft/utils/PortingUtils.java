/*
 * @(#)PortingUtils.java 4/12/2006
 *
 * Copyright 2002 - 2006 JIDE Software Inc. All rights reserved.
 */
package com.jidesoft.utils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.geom.Area;
import java.util.ArrayList;
import java.util.List;

/**
 * A class that keeps all 1.4/1.3 different stuff.
 */
public class PortingUtils {
    private static Rectangle _virtualBounds = null;

    /**
     * Gets current focused components. If 1.3, just uses event's source;
     * 1.4, used keyboard focus manager to get the correct focused component.
     *
     * @param event
     * @return current focused component
     */
    public static Component getCurrentFocusComponent(AWTEvent event) {
        return KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
    }

    /**
     * Gets frame's state. In 1.3, used getState; in 1.4, uses getExtendedState.
     *
     * @param frame
     * @return frame's state
     */
    public static int getFrameState(Frame frame) {
        return frame.getExtendedState();
    }

    /**
     * Sets frame's state. In 1.3, uses sets frame's state; in 1.4, uses gets frame's state.
     *
     * @param frame
     * @param state
     */
    public static void setFrameState(Frame frame, int state) {
        frame.setExtendedState(state);
    }

    /**
     * Gets mouse modifiers. If 1.3, uses getModifiers; 1.4, getModifiersEx.
     *
     * @param e
     * @return mouse modifiers
     */
    public static int getMouseModifiers(MouseEvent e) {
        return e.getModifiersEx();
    }

    /**
     * Makes sure the component won't receive the focus.
     *
     * @param component
     */
    public static void removeFocus(JComponent component) {
        component.setRequestFocusEnabled(false);
        component.setFocusable(false);
    }

    /**
     * Removes the button border.
     *
     * @param button
     */
    public static void removeButtonBorder(AbstractButton button) {
        button.setContentAreaFilled(false);
        button.setMargin(new Insets(0, 0, 0, 0));
        button.setBorder(BorderFactory.createEmptyBorder());
    }

    /**
     * To make sure the rectangle is within the screen bounds.
     *
     * @param invoker
     * @param rect
     * @return the rectange that is in the screen bounds.
     */
    public static Rectangle containsInScreenBounds(Component invoker, Rectangle rect) {
        Rectangle screenBounds = getScreenBounds(invoker);
        Point p = rect.getLocation();
        if (p.x + rect.width > screenBounds.x + screenBounds.width) {
            p.x = screenBounds.x + screenBounds.width - rect.width;
        }
        if (p.y + rect.height > screenBounds.y + screenBounds.height) {
            p.y = screenBounds.y + screenBounds.height - rect.height;
        }
        if (p.x < screenBounds.x) {
            p.x = screenBounds.x;
        }
        if (p.y < screenBounds.y) {
            p.y = screenBounds.y;
        }
        return new Rectangle(p, rect.getSize());
    }

    /**
     * To make sure the rectangle has overlap with the screen bounds.
     *
     * @param invoker
     * @param rect
     * @return the rectange that has overlap with the screen bounds.
     */
    public static Rectangle overlapWithScreenBounds(Component invoker, Rectangle rect) {
        Rectangle screenBounds = getScreenBounds(invoker);
        Point p = rect.getLocation();
        if (p.x > screenBounds.x + screenBounds.width) {
            p.x = screenBounds.x + screenBounds.width - rect.width;
        }
        if (p.y > screenBounds.y + screenBounds.height) {
            p.y = screenBounds.y + screenBounds.height - rect.height;
        }
        if (p.x + rect.width < screenBounds.x) {
            p.x = screenBounds.x;
        }
        if (p.y + rect.height < screenBounds.y) {
            p.y = screenBounds.y;
        }
        return new Rectangle(p, rect.getSize());
    }

    /**
     * Gets the screen size. In JDK1.4+, the returned size will exclude task bar area on Windows OS.
     *
     * @param invoker
     * @return the screen size.
     */
    public static Dimension getScreenSize(Component invoker) {
        ensureVirtualBounds();

        // to handle multi-display case
        Dimension screenSize = _virtualBounds.getSize();  // Toolkit.getDefaultToolkit().getScreenSize();

        // jdk1.4 only
        if (invoker != null && !(invoker instanceof JApplet) && invoker.getGraphicsConfiguration() != null) {
            Insets insets = Toolkit.getDefaultToolkit().getScreenInsets(invoker.getGraphicsConfiguration());
            screenSize.width -= insets.left + insets.right;
            screenSize.height -= insets.top + insets.bottom;
        }

        return screenSize;
    }

    /**
     * Gets the screen size. In JDK1.4+, the returned size will exclude task bar area on Windows OS.
     *
     * @param invoker
     * @return the screen size.
     */
    public static Dimension getLocalScreenSize(Component invoker) {
        ensureVirtualBounds();

        // jdk1.4 only
        if (invoker != null && !(invoker instanceof JApplet) && invoker.getGraphicsConfiguration() != null) {
            // to handle multi-display case
            GraphicsConfiguration gc = invoker.getGraphicsConfiguration();
            Rectangle bounds = gc.getBounds();
            Insets insets = Toolkit.getDefaultToolkit().getScreenInsets(gc);
            bounds.width -= insets.left + insets.right;
            bounds.height -= insets.top + insets.bottom;
            return bounds.getSize();
        }
        else {
            return getScreenSize(invoker);
        }
    }

    /**
     * Gets the screen bounds. In JDK1.4+, the returned bounds will exclude task bar area on Windows OS.
     *
     * @param invoker
     * @return the screen bounds.
     */
    public static Rectangle getScreenBounds(Component invoker) {
        ensureVirtualBounds();

        // to handle multi-display case
        Rectangle bounds = (Rectangle) _virtualBounds.clone();

        // TODO
        // jdk1.4 only
        if (invoker != null && !(invoker instanceof JApplet) && invoker.getGraphicsConfiguration() != null) {
            Insets insets = Toolkit.getDefaultToolkit().getScreenInsets(invoker.getGraphicsConfiguration());
            bounds.x += insets.left;
            bounds.y += insets.top;
            bounds.width -= insets.left + insets.right;
            bounds.height -= insets.top + insets.bottom;
        }

        return bounds;
    }

    /**
     * Gets the local monitor's screen bounds.
     *
     * @return the screen bounds.
     */
    public static Rectangle getLocalScreenBounds() {
        GraphicsEnvironment e = GraphicsEnvironment.getLocalGraphicsEnvironment();
        return e.getMaximumWindowBounds();
    }

    private static void ensureVirtualBounds() {
        if (_virtualBounds == null) {
            _virtualBounds = new Rectangle();
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            GraphicsDevice[] gs = ge.getScreenDevices();
            for (GraphicsDevice gd : gs) {
                GraphicsConfiguration gc = gd.getDefaultConfiguration();
                _virtualBounds = _virtualBounds.union(gc.getBounds());
            }
        }
    }

    /**
     * Makes the point parameter is within the screen bounds. If not, it will be modified to make sure it is in.
     *
     * @param invoker
     * @param point
     * @deprecated Please use {@link #ensureOnScreen(java.awt.Rectangle)} instead.
     */
    public static void withinScreen(Component invoker, Point point) {
        if (invoker != null && !(invoker instanceof JApplet)) {
            GraphicsConfiguration gc = invoker.getGraphicsConfiguration();
            Rectangle bounds = gc.getBounds();
            Insets insets = Toolkit.getDefaultToolkit().getScreenInsets(invoker.getGraphicsConfiguration());
            if (point.x < bounds.x + insets.left) {
                point.x = bounds.x + insets.left;
            }
            if (point.x > bounds.x + bounds.width - insets.right) {
                point.x = bounds.x + bounds.width - insets.right;
            }
            if (point.y < bounds.y + insets.top) {
                point.y = bounds.y + insets.top;
            }
            if (point.y > bounds.y + bounds.height - insets.bottom) {
                point.y = bounds.y + bounds.height - insets.bottom;
            }
        }
    }

    private static Area SCREEN_AREA;
    private static Rectangle[] SCREENS;
    private static Insets[] INSETS;

    private static Thread _initalizationThread = null;

    /**
     * If you use methods such as {@link #ensureOnScreen(java.awt.Rectangle)}, {@link #getContainingScreenBounds(java.awt.Rectangle,boolean)}
     * or {@link #getScreenArea()} for the first time, it will take up to a few seconds to run because it needs to get device information.
     * To avoid any slowness, you can call {@link #initializeScreenArea()} method in the class where you will use those three methods.
     * This method will spawn a thread to retrieve device information thus it will return immediately.
     * Hopefully, when you use the three methods, the thread is done so user will not notice any slowness.
     */
    synchronized public static void initializeScreenArea() {
        initializeScreenArea(Thread.NORM_PRIORITY);
    }

    /**
     * If you use methods such as {@link #ensureOnScreen(java.awt.Rectangle)}, {@link #getContainingScreenBounds(java.awt.Rectangle,boolean)}
     * or {@link #getScreenArea()} for the first time, it will take up to a few seconds to run because it needs to get device information.
     * To avoid any slowness, you can call {@link #initializeScreenArea()} method in the class where you will use those three methods.
     * This method will spawn a thread to retrieve device information thus it will return immediately.
     * Hopefully, when you use the three methods, the thread is done so user will not notice any slowness.
     *
     * @param priority as we will use a thread to calculate the screen area, you can use this parameter to control the priority of the thread. If you
     *                 are waiting for the result before the next step, you should use normal priority (which is 5). If you just want to calcualte when app starts,
     *                 you can use a lower priority (such as 3). For example, AbstractComboBox needs screen size so that the popup doesn't go beyond the screen.
     *                 So when AbstractComboBox is used, we will kick off the thread at priority 3. If user clicks on the drop down after the thread finished,
     *                 there will be no time delay.
     */
    synchronized public static void initializeScreenArea(int priority) {
        if (_initalizationThread == null) {
            _initalizationThread = new Thread() {
                @Override
                public void run() {
                    SCREEN_AREA = new Area();
                    GraphicsEnvironment environment = GraphicsEnvironment.getLocalGraphicsEnvironment();
                    List screensList = new ArrayList();
                    List insetsList = new ArrayList();
                    GraphicsDevice[] screenDevices = environment.getScreenDevices();
                    for (int i = 0; i < screenDevices.length; i++) {
                        GraphicsDevice device = screenDevices[i];
                        GraphicsConfiguration[] configurations = device.getConfigurations();
                        for (int j = 0; j < configurations.length; j++) {
                            GraphicsConfiguration graphicsConfiguration = configurations[j];
                            Rectangle screenBounds = graphicsConfiguration.getBounds();
                            Insets insets = Toolkit.getDefaultToolkit().getScreenInsets(graphicsConfiguration);
                            screensList.add(screenBounds);
                            insetsList.add(insets);
                            SCREEN_AREA.add(new Area(screenBounds));
                        }
                    }
                    SCREENS = (Rectangle[]) screensList.toArray(new Rectangle[screensList.size()]);
                    INSETS = (Insets[]) insetsList.toArray(new Insets[screensList.size()]);
                }
            };
            _initalizationThread.setPriority(priority);
            _initalizationThread.start();
        }
    }

    public static boolean isInitalizationThreadAlive() {
        return _initalizationThread != null && _initalizationThread.isAlive();
    }

    public static boolean isInitalizationThreadStarted() {
        return _initalizationThread != null;
    }

    private static void waitForInitialization() {
        initializeScreenArea();

        while (_initalizationThread.isAlive()) {
            try {
                Thread.sleep(100);
            }
            catch (InterruptedException e) {
            }
        }
    }

    /**
     * Ensures the rectangle is visible on the screen.
     *
     * @param invoker the invoking component
     * @param bounds  the input bounds
     * @return the modified bounds.
     */
    public static Rectangle ensureVisible(Component invoker, Rectangle bounds) {
        Rectangle mainScreenBounds = PortingUtils.getLocalScreenBounds(); // this is fast. Only if it is outside this bounds, we try the more expensive one.
        if (!mainScreenBounds.contains(bounds.getLocation())) {
            Rectangle screenBounds = PortingUtils.getScreenBounds(invoker);
            if (bounds.x > screenBounds.x + screenBounds.width || bounds.x < screenBounds.x) {
                bounds.x = mainScreenBounds.x;
            }
            if (bounds.y > screenBounds.y + screenBounds.height || bounds.y < screenBounds.y) {
                bounds.y = mainScreenBounds.y;
            }
        }
        return bounds;
    }

    /**
     * Modifies the position of rect so that it is completly on screen if that is possible.
     *
     * @param rect The rectange to move onto a single screen
     * @return rect after its position has been modified
     */
    public static Rectangle ensureOnScreen(Rectangle rect) {
        // optimize it so that it is faster for most cases
        Rectangle localScreenBounds = getLocalScreenBounds();
        if (localScreenBounds.contains(rect)) {
            return rect;
        }

        waitForInitialization();

        // check if rect is totaly on screen
        if (SCREEN_AREA.contains(rect)) return rect;
        // see if the top left is on any of the screens
        Rectangle containgScreen = null;
        Point rectPos = rect.getLocation();
        for (int i = 0; i < SCREENS.length; i++) {
            Rectangle screenBounds = SCREENS[i];
            if (screenBounds.contains(rectPos)) {
                containgScreen = screenBounds;
                break;
            }
        }
        // if not see if rect partialy on any screen
        for (int i = 0; i < SCREENS.length; i++) {
            Rectangle screenBounds = SCREENS[i];
            if (screenBounds.intersects(rect)) {
                containgScreen = screenBounds;
                break;
            }
        }
        // check if it was on any screen
        if (containgScreen == null) {
            // it was not on any of the screens so center it on the first screen
            rect.x = (SCREENS[0].width - rect.width) / 2;
            rect.y = (SCREENS[0].width - rect.width) / 2;
            return rect;
        }
        else {
            // move rect so it is completly on a single screen
            // check X
            int rectRight = rect.x + rect.width;
            int screenRight = containgScreen.x + containgScreen.width;
            if (rectRight > screenRight) {
                rect.x = screenRight - rect.width;
            }
            if (rect.x < containgScreen.x) rect.x = containgScreen.x;
            // check Y
            int rectBottom = rect.y + rect.height;
            int screenBottom = containgScreen.y + containgScreen.height;
            if (rectBottom > screenBottom) {
                rect.y = screenBottom - rect.height;
            }
            if (rect.y < containgScreen.y) rect.y = containgScreen.y;
            // return corrected rect
            return rect;
        }
    }

    /**
     * Gets the screen bounds that contains the rect. The screen bounds consider the screen insets if any.
     *
     * @param rect
     * @param considerInsets if consider the insets. The insets is for thing like Windows Task Bar.
     * @return the screen bounds that contains the rect.
     */
    public static Rectangle getContainingScreenBounds(Rectangle rect, boolean considerInsets) {
        waitForInitialization();
        // check if rect is totaly on screen
//        if (SCREEN_AREA.contains(rect)) return SCREEN_AREA;

        // see if the top left is on any of the screens
        Rectangle containgScreen = null;
        Insets insets = null;
        Point rectPos = rect.getLocation();
        for (int i = 0; i < SCREENS.length; i++) {
            Rectangle screenBounds = SCREENS[i];
            if (screenBounds.contains(rectPos)) {
                containgScreen = screenBounds;
                insets = INSETS[i];
                break;
            }
        }
        // if not see if rect partialy on any screen
        for (int i = 0; i < SCREENS.length; i++) {
            Rectangle screenBounds = SCREENS[i];
            if (screenBounds.intersects(rect)) {
                containgScreen = screenBounds;
                insets = INSETS[i];
                break;
            }
        }

        // fall back to the first screen
        if (containgScreen == null) {
            containgScreen = SCREENS[0];
            insets = INSETS[0];
        }

        Rectangle bounds = new Rectangle(containgScreen);
        if (considerInsets) {
            bounds.x += insets.left;
            bounds.y += insets.top;
            bounds.width -= insets.left + insets.right;
            bounds.height -= insets.top + insets.bottom;
        }
        return bounds;
    }


    /**
     * Get screen area of all monitors.
     *
     * @return Union of all screens
     */
    public static Area getScreenArea() {
        waitForInitialization();
        return SCREEN_AREA;
    }

    /**
     * Notifies user something is wrong. We use Toolkit beep method by default.
     */
    public static void notifyUser() {
        Toolkit.getDefaultToolkit().beep();
    }

    /**
     * Checks the prerequisite needed by JIDE demos. If the prerequisite doesn't meet, it will prompt a message box and exit.
     */
    public static void prerequisiteChecking() {
        if (!SystemInfo.isJdk14Above()) {
            PortingUtils.notifyUser();
            JOptionPane.showMessageDialog(null, "J2SE 1.4 or above is required for this demo.", "JIDE Software, Inc.", JOptionPane.WARNING_MESSAGE);
            java.lang.System.exit(0);
        }

        if (!SystemInfo.isJdk142Above()) {
            PortingUtils.notifyUser();
            JOptionPane.showMessageDialog(null, "J2SE 1.4.2 or above is recommended for this demo for the best experience of seamless integration with Windows XP.", "JIDE Software, Inc.", JOptionPane.WARNING_MESSAGE);
        }

        if (SystemInfo.isMacOSX()) { // set special properties for Mac OS X
            java.lang.System.setProperty("apple.laf.useScreenMenuBar", "true");
            System.setProperty("apple.awt.brushMetalLook", "true");
        }
    }
}
