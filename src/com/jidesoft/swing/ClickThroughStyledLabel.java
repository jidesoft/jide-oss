package com.jidesoft.swing;

import javax.swing.*;
import javax.swing.event.MouseInputListener;
import java.awt.*;
import java.awt.event.MouseEvent;

/**
 * <tt>ClickThroughStyledLabel</tt> is a special ClickThroughStyledLabel that will retarget all mouse events to specified target component.
 * <p/>
 * For example, you need to paint some text on a JComponent. Usually you can call Java2D paint text method and paint
 * the text. However the other way to do it is to add JLabel to JComponent and JLabel will not only paint
 * the text but also an optional icon which is better. However if you had mouse listener added to JComponent, the mouse
 * listener will not receive any mouse events when mouse clicks on the JLabel. By using this <tt>ClickThroughLabel</tt>,
 * mouse event will be passed to underlying JComponent.
 * <p/>
 * Please note, we didn't pass all mouse events. In most cases, MOUSE_EXITED and MOUSE_ENTERED doesn't make sense to pass through.
 * However there are cases, for example when the JLabel is at the border of JComponent, you may expect MOUSE_ENTERED event
 * on JComponent but it will not happen. So please be aware of those cases so that you don't depend on it for important
 * decision in your code.
 */
public class ClickThroughStyledLabel extends StyledLabel implements MouseInputListener {

    private Component _target;

    public ClickThroughStyledLabel() {
        installListeners();
    }

    public ClickThroughStyledLabel(Icon image) {
        super(image);
        installListeners();
    }

    public ClickThroughStyledLabel(Icon image, int horizontalAlignment) {
        super(image, horizontalAlignment);
        installListeners();
    }

    public ClickThroughStyledLabel(String text) {
        super(text);
        installListeners();
    }

    public ClickThroughStyledLabel(String text, int horizontalAlignment) {
        super(text, horizontalAlignment);
        installListeners();
    }

    public ClickThroughStyledLabel(String text, Icon icon, int horizontalAlignment) {
        super(text, icon, horizontalAlignment);
        installListeners();
    }

    public Component getTarget() {
        return _target;
    }

    public void setTarget(Component target) {
        _target = target;
    }

    protected void installListeners() {
        addMouseListener(this);
        addMouseMotionListener(this);
    }

    protected void uninstallListeners() {
        removeMouseListener(this);
        removeMouseMotionListener(this);
    }

    /*
    * Dispatch an event clone, retargeted for the specified target.
    */
    private void retargetMouseEvent(int id, MouseEvent e,
                                    Component target) {
        if (target == null) {
            return;
        }
        // fix for bug #4202966 -- hania
        // When retargetting a mouse event, we need to translate
        // the event's coordinates relative to the target.

        Point p = SwingUtilities.convertPoint(this,
                e.getX(), e.getY(),
                target);
        MouseEvent retargeted = new MouseEvent(target,
                id,
                e.getWhen(),
                e.getModifiers(),
                p.x,
                p.y,
                e.getClickCount(),
                e.isPopupTrigger());
        target.dispatchEvent(retargeted);
    }

    public void mouseClicked(MouseEvent e) {
        retargetMouseEvent(e.getID(), e, getTarget());
    }

    public void mousePressed(MouseEvent e) {
        retargetMouseEvent(e.getID(), e, getTarget());
    }

    public void mouseReleased(MouseEvent e) {
        retargetMouseEvent(e.getID(), e, getTarget());
    }

    public void mouseEntered(MouseEvent e) {
        retargetMouseEvent(e.getID(), e, getTarget());
    }

    public void mouseExited(MouseEvent e) {
        retargetMouseEvent(e.getID(), e, getTarget());
    }

    public void mouseDragged(MouseEvent e) {
        retargetMouseEvent(e.getID(), e, getTarget());
    }

    public void mouseMoved(MouseEvent e) {
        retargetMouseEvent(e.getID(), e, getTarget());
    }
}
