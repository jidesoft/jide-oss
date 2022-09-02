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
 * DelegateAction is a special AbstractAction which can do something then delegate to another action depending on the
 * return value of {@link #delegateActionPerformed(java.awt.event.ActionEvent)}. There are two usages of it. First, you
 * can use {@link #replaceAction(javax.swing.JComponent, int, javax.swing.KeyStroke, DelegateAction)} to replace the action
 * associated with the specified keystroke with the DelegateAction. The DelegateAction will be triggered when the
 * keystroke is pressed. After DelegateAction is done, it can return true or false. If false, the original action
 * associated with the keystroke will be triggered as well. This solves the problem that {@link
 * JComponent#registerKeyboardAction(java.awt.event.ActionListener, String, javax.swing.KeyStroke, int)} will replace the
 * original action so that the original action will never be triggered.
 * <p/>
 * The second way to use DelegateAction is to delegate the action from one component to another component using {@link
 * #replaceAction(javax.swing.JComponent, int, javax.swing.JComponent, int, javax.swing.KeyStroke, DelegateAction)}. In this
 * case, the keystroke on the first component parameter will be triggered the DelegateAction. If DelegateAction returns
 * false, the registered action on the second component parameter will be triggered. If you pass in {@link
 * PassthroughDelegateAction}, the registered action on the second component will always be triggered.
 * <p/>
 * Please notes, if you call replaceAction several times on the same component with the same keystroke, it will form a
 * chain of DelegateActions. In this case, the first call will be the first DelegateAction. In the other words, the
 * first one will have the highest priority and will be triggered first. Ideally, we should assign a priority to each
 * DelegateAction. But for the sake of simplicity, we decided not doing it for now. So because of this, this class is
 * not ready to be used as public API. We have to make it public because different packages in JIDE need to use it. If
 * you want to use, please use it with caution. We don't guarantee that we will not change the public methods on this
 * classes.
 * <p/>
 */
abstract public class DelegateAction extends AbstractAction {
    private static final long serialVersionUID = -3867985431184738600L;
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

    /**
     * Returns true if either delegateIsEnabled or the action is enabled.
     * <p/>
     * {@inheritDoc}
     */
    // Should be final like actionPerformed but not done for backward compatibility.
    @Override
    public boolean isEnabled() {
        return isDelegateEnabled() || (_action != null && _action.isEnabled());
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
     * Checks if an action can be performed. Returns true if delegateActionPerformed would perform an action. Otherwise
     * returns false.
     *
     * @return <code>true</code> if the action can be performed.
     */
    // Should be abstract like delegateActionPerformed but not done for backward compatibility.
    public boolean isDelegateEnabled() {
        return super.isEnabled();
    }

    /**
     * Performs an action. Returns true if no further action should be taken for this keystroke. Otherwise, returns
     * false.
     *
     * @param e the action event.
     * @return true if no further action should be taken for this keystroke. Otherwise, returns false.
     */
    abstract public boolean delegateActionPerformed(ActionEvent e);

    public static class PassthroughDelegateAction extends DelegateAction {
        private static final long serialVersionUID = -1555177105658867899L;

        @Override
        public boolean delegateActionPerformed(ActionEvent e) {
            return false;
        }

        @Override
        public boolean isDelegateEnabled() {
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
        ActionListener action = component.getActionForKeyStroke(keyStroke);
        if (action != delegateAction && action instanceof Action) {
            if (!first && action instanceof DelegateAction) {
                Action childAction = ((DelegateAction) action).getAction();
                while (childAction != null) {
                    if (childAction == delegateAction) {
                        return;
                    }
                    if (childAction instanceof DelegateAction) {
                        childAction = ((DelegateAction) childAction).getAction();
                    }
                    else {
                        childAction = null;
                    }
                }
                delegateAction.setAction(((DelegateAction) action).getAction());
                ((DelegateAction) action).setAction(delegateAction);
                delegateAction = (DelegateAction) action;
            }
            else {
                delegateAction.setAction((Action) action);
            }
        }

        if (target != component) {
            delegateAction.setTarget(target);
            replaceAction(component, condition, keyStroke, delegateAction);
        }
        else {
            Object actionCommand = target.getInputMap(targetCondition).get(keyStroke);
            if (actionCommand == null) {
                component.registerKeyboardAction(delegateAction, keyStroke, condition);
            }
            else {
                component.getActionMap().put(actionCommand, delegateAction);
            }
        }
    }

    public static void restoreAction(JComponent component, int condition, KeyStroke keyStroke) {
        if (component == null) {
            return;
        }
        ActionListener action = component.getActionForKeyStroke(keyStroke);
        if (action instanceof DelegateAction) {
            Action actualAction = ((DelegateAction) action).getAction();
            if (actualAction != null) {
                component.registerKeyboardAction(actualAction, keyStroke, condition);
            }
            else {
                component.unregisterKeyboardAction(keyStroke);
            }
        }
    }

    public static void restoreAction(JComponent component, int condition, KeyStroke keyStroke, Class<?> actionClass) {
        ActionListener action = component.getActionForKeyStroke(keyStroke);
        ActionListener parent = action;
        ActionListener top = action;
        while (action instanceof DelegateAction) {
            if (actionClass.isAssignableFrom(action.getClass())) {
                if (top == action) {
                    Action a = ((DelegateAction) action).getAction();
                    if (a == null) {
                        component.unregisterKeyboardAction(keyStroke);
                    }
                    else {
                        component.registerKeyboardAction(a, keyStroke, condition);
                    }
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
                    Action oldAction = ((DelegateAction) action).getAction();
                    if(oldAction != null) {
                        Object name = component.getInputMap().get(keyStroke);
                        if(name != null) {
                            component.getActionMap().remove(name);
                            component.getActionMap().remove(action);
                        }
                        component.registerKeyboardAction(oldAction, keyStroke, condition);
                    }
                    else {
                        component.unregisterKeyboardAction(keyStroke);
                    }
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
