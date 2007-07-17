/*
 * @(#)DelegateAction.java 10/2/2006
 *
 * Copyright 2002 - 2006 JIDE Software Inc. All rights reserved.
 */

package com.jidesoft.swing;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * DelegateAction is a special AbstractAction which can do something then delegate to another action depending on
 * the return value of {@link #delegateActionPerformed(java.awt.event.ActionEvent)}.
 * There are two usages of it. First, you can use {@link #replaceAction(javax.swing.JComponent,int,javax.swing.KeyStroke,DelegateAction)}
 * to replace the action associated with the specified keystroke with the DelegateAction. The DelegateAction will be
 * triggered when the keystroke is pressed. After DelegateAction is done, it can return true or false. If false, the original action
 * associated with the keystroke will be triggered as well. This solves the problem that {@link JComponent#registerKeyboardAction(java.awt.event.ActionListener,String,javax.swing.KeyStroke,int)}
 * will replace the original action so that the original actino will never be triggered.
 * <p/>
 * The second way to use DelegateAction is to delegate the action from one component to another component using {@link #replaceAction(javax.swing.JComponent,int,javax.swing.JComponent,int,javax.swing.KeyStroke,DelegateAction)}.
 * In this case, the keystroke on the first component parameter will be triggered the DelegateAction. If DelegateAction returns false, the registered action on the second component parameter will be triggered.
 * If you pass in {@link PassthroughDelegateAction}, the registered action on the second component
 * will always be triggered.
 * <p/>
 * Please notes, if you call replaceAction several times on the same component with the same keystroke,
 * it will form a chain of DelegateActions. In this case, the first call will be the first DelegateAction.
 * In the other words, the first one will have the highest priority and will be triggered first.
 * Ideally, we should assign a priroty to each DelegateAction. But for the sake of simplicity,
 * we decided not doing it for now. So because of this, this class is not ready to be used as public API. We have
 * to make it public because different packages in JIDE need to use it. If you want to use, please use it with caution.
 * We don't gurantee that we will not change the public methods on this classes.
 * <p/>
 */
abstract public class DelegateAction extends AbstractAction {
    private Action _action;
    private JComponent _target;

    public DelegateAction() {
    }

    public DelegateAction(Action action) {
        _action = action;
    }

    public DelegateAction(Action action, JComponent target) {
        _action = action;
        _target = target;
    }

    final public void actionPerformed(ActionEvent e) {
        if (!delegateActionPerformed(e)) {
            if (_action != null) {
                if (_target == null) {
                    _action.actionPerformed(e);
                }
                else {
                    _action.actionPerformed(new ActionEvent(getTarget(), e.getID(), e.getActionCommand(), e.getWhen(), e.getModifiers()));
                }
            }
        }
    }

    protected Action getAction() {
        return _action;
    }

    protected void setAction(Action action) {
        _action = action;
    }

    protected JComponent getTarget() {
        return _target;
    }

    protected void setTarget(JComponent target) {
        _target = target;
    }

    /**
     * Performs an action. Returns true if no further action should be taken for this keystroke. Otherwise, returns false.
     *
     * @param e the action event.
     * @return true if no further action should be taken for this keystroke. Otherwise, returns false.
     */
    abstract public boolean delegateActionPerformed(ActionEvent e);

    public static class PassthroughDelegateAction extends DelegateAction {
        @Override
        public boolean delegateActionPerformed(ActionEvent e) {
            return false;
        }
    }

    public static void replaceAction(JComponent component, int condition, KeyStroke keyStroke, DelegateAction delegateAction) {
        replaceAction(component, condition, component, condition, keyStroke, delegateAction);
    }

    public static void replaceAction(JComponent component, int condition, KeyStroke keyStroke, DelegateAction delegateAction, boolean first) {
        replaceAction(component, condition, component, condition, keyStroke, delegateAction, first);
    }

    public static void replaceAction(JComponent component, int condition, JComponent target, int targetCondition, KeyStroke keyStroke) {
        replaceAction(component, condition, target, targetCondition, keyStroke, new DelegateAction.PassthroughDelegateAction(), false);
    }

    public static void replaceAction(JComponent component, int condition, JComponent target, int targetCondition, KeyStroke keyStroke, DelegateAction delegateAction) {
        replaceAction(component, condition, target, targetCondition, keyStroke, delegateAction, false);
    }

    public static void replaceAction(JComponent component, int condition, JComponent target, int targetCondition, KeyStroke keyStroke, DelegateAction delegateAction, boolean first) {
        Object actionCommand = target.getInputMap(targetCondition).get(keyStroke);
        if (actionCommand != null) {
            Action action = target.getActionMap().get(actionCommand);
            if (action != delegateAction) {
                if (!first && action instanceof DelegateAction) {
                    delegateAction.setAction(((DelegateAction) action).getAction());
                    ((DelegateAction) action).setAction(delegateAction);
                    delegateAction = (DelegateAction) action;
                }
                else {
                    delegateAction.setAction(action);
                }
                if (target != component) {
                    delegateAction.setTarget(target);
                    replaceAction(component, condition, keyStroke, delegateAction);
                }
                else {
                    component.getActionMap().put(actionCommand, delegateAction);
                }
            }
        }
        else {
            if (target != component) {
                delegateAction.setTarget(target);
                replaceAction(component, condition, keyStroke, delegateAction);
            }
            else {
                component.registerKeyboardAction(delegateAction, keyStroke, condition);
            }
        }
    }

    public static void restoreAction(JComponent component, int condition, KeyStroke keyStroke) {
        ActionListener action = component.getActionForKeyStroke(keyStroke);
        if (action instanceof DelegateAction) {
            component.registerKeyboardAction(((DelegateAction) action).getAction(), keyStroke, condition);
        }
    }

    public static void restoreAction(JComponent component, int condition, KeyStroke keyStroke, Class actionClass) {
        ActionListener action = component.getActionForKeyStroke(keyStroke);
        ActionListener parent = action;
        ActionListener top = action;
        while (action instanceof DelegateAction) {
            if (actionClass.isAssignableFrom(action.getClass())) {
                if (top == action) {
                    component.registerKeyboardAction(((DelegateAction) action).getAction(), keyStroke, condition);
                }
                else {
                    ((DelegateAction) parent).setAction(((DelegateAction) action).getAction());
                }
                break;
            }
            parent = action;
            action = ((DelegateAction) action).getAction();
        }
    }

    public static void restoreAction(JComponent component, int condition, KeyStroke keyStroke, Action actionToBeRemoved) {
        ActionListener action = component.getActionForKeyStroke(keyStroke);
        ActionListener parent = action;
        ActionListener top = action;
        while (action instanceof DelegateAction) {
            if (actionToBeRemoved == action) {
                if (top == action) {
                    component.registerKeyboardAction(((DelegateAction) action).getAction(), keyStroke, condition);
                }
                else {
                    ((DelegateAction) parent).setAction(((DelegateAction) action).getAction());
                }
                break;
            }
            parent = action;
            action = ((DelegateAction) action).getAction();
        }
    }
}
